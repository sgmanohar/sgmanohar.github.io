package phic.doctor;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.*;
import phic.Person;
import phic.gui.GraphPaper;

/**
 * Observations chart
 */
public class ObservationsChart extends IFrame implements Runnable{
	Person person;

	private JScrollPane jScrollPane1=new JScrollPane();

	private JPanel chart=new JPanel();

	private Box box1;

	private Box box2;

	private FlowLayout flowLayout1=new FlowLayout();

	/** Specify whether the drawing of the charts are to be antialiased */
	public boolean antialiasing=true;

	private GraphPaper tempchart=new GraphPaper(){
		public void paint(Graphics g){
			super.paint(g);
			Point op=null;
			if(antialiasing){
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}
			g.setColor(Color.black);
			g.drawString("T",20,10);
			for(int i=0;i<observations.size();i++){
				Observations o=(Observations)observations.get(i);
				Point p=this.toScreen(new Point2D.Double(o.time,o.temp));
				if(i>0){
					g.drawLine(op.x,op.y,p.x,p.y);
				}
				op=p;
			}
		}
	};

	private GraphPaper rrchart=new GraphPaper(){
		public void paint(Graphics g){
			super.paint(g);
			if(antialiasing){
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}
			g.setColor(Color.black);
			g.drawString("RR",20,10);
			Point op=null;
			for(int i=0;i<observations.size();i++){
				Observations o=(Observations)observations.get(i);
				Point p=this.toScreen(new Point2D.Double(o.time,o.resp));
				if(i>0){
					g.drawLine(op.x,op.y,p.x,p.y);
				}
				op=p;
			}
			//draw time x-labels
			int n=1+(int)((maxx-minx)/majorx);
			for(int i=0;i<n;i++){
				int hour=(int)(((long)(minx+i*majorx))%24);
				int x=xS(minx+i*majorx);
				g.drawString(String.valueOf(hour),x,getHeight()-10);
			}
		}
	};

	/** size of hat (in pixels) for systolic and diastolic mark */
	private int h=4;

	private GraphPaper bppchart=new GraphPaper(){
		public void paint(Graphics g){
			super.paint(g);
			if(antialiasing){
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}
			g.setColor(Color.blue);
			g.drawString("BP",20,10);
			g.setColor(Color.black);
			g.drawString("HR",20,20);
			Point op=null;
			for(int i=0;i<observations.size();i++){
				Observations o=(Observations)observations.get(i);
				Point p=this.toScreen(new Point2D.Double(o.time,o.pulse)),
						s=this.toScreen(new Point2D.Double(o.time,o.systolic)),
						d=this.toScreen(new Point2D.Double(o.time,o.diastolic));
				if(i>0){
					//line for pulse
					g.setColor(Color.black);
					g.drawLine(op.x,op.y,p.x,p.y);
				}
				g.setColor(Color.blue);
				//systolic hat
				g.drawLine(s.x-h,s.y-h,s.x,s.y);
				g.drawLine(s.x+h,s.y-h,s.x,s.y);
				//diastolic hat
				g.drawLine(d.x-h,d.y+h,d.x,d.y);
				g.drawLine(d.x+h,d.y+h,d.x,d.y);
				op=p;
			}
		}
	};

	public ObservationsChart(Person p){
		if(p==null){
			throw new NullPointerException(
					"Null person supplied to observations chart");
		}
		person=p;
		init();
		observationThread.start();
	}

	/** The time at which the chart was started */
	private double startTime=0;

	private double initialX;

	public ObservationsChart(){
		init();
	}

	/**
	 * The list of observations
	 */
	public Vector observations=new Vector();

	/**
	 * The data for one set of observations, at a specific moment in time
	 */
	public class Observations{
		int pulse,resp,systolic,diastolic,consc;

		double time,temp;

		/** Create an observation from data in the given person */
		Observations(Person p){
			temp=p.body.Temp.get();
			pulse=(int)p.body.CVS.heart.rate.get();
			resp=(int)p.body.lungs.RespR.get();
			systolic=(int)(1000*p.body.CVS.SysBP.get());
			diastolic=(int)(1000*p.body.CVS.DiaBP.get());
			consc=p.body.brain.getFeeling();
			//time in hours, to nearest 15 minutes.
			time=person.body.getClock().getTime()/(1000*60);
			time=((int)(time/15))/4.;
		}
	}


	/** Get the nth observation. This is a convenience method. */
	public final Observations observations(int n){
		return(Observations)observations.get(n);
	}

	/**
	 * The horizontal (time) scale of the chart.
	 */
	final int pixelsPerHour=80;

	/**
	 * Perform one observation, updating the charts.
	 */
	public void observe(){
		Observations now=new Observations(person);
		observations.add(now);
		Observations first=observations(0);
		//truncate history
		while(now.time-first.time>historyLength){
			observations.remove(0);
			first=observations(0);
		}
		double min=first.time,max=now.time;
		if(min==max){
			min=max-1; // 1 hour before
		}
		tempchart.setXRange(min,max);
		bppchart.setXRange(min,max);
		rrchart.setXRange(min,max);
		//80 pixels per hour
		int width=(int)((max-min)*80);
		tempchart.setPreferredSize(new Dimension(width,
				tempchart.getPreferredSize().height));
		bppchart.setPreferredSize(new Dimension(width,
				bppchart.getPreferredSize().height));
		rrchart.setPreferredSize(new Dimension(width,
				rrchart.getPreferredSize().height));
		invalidate();
		repaint();
	}

	/**
	 * History length of 1 days. After this length of time, points are deleted
	 * from the left hand edge of the chart.
	 * Measured in hours
	 */
	long historyLength=24*1;

	private void init(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		//Normal ranges
		tempchart.setYRange(35,42);
		bppchart.setYRange(50,160);
		rrchart.setYRange(8,40);
		setSize(400,400);
	}

	private void jbInit() throws Exception{
		box1=Box.createVerticalBox();
		box2=Box.createHorizontalBox();
		this.setTitle("Observations");
		chart.setLayout(flowLayout1);
		tempchart.setBackground(SystemColor.window);
		tempchart.setForeground(Color.red);
		tempchart.setDrawYLabels(true);
		bppchart.setBackground(SystemColor.window);
		bppchart.setForeground(Color.red);
		bppchart.setDrawYLabels(true);
		rrchart.setBackground(SystemColor.window);
		rrchart.setForeground(Color.red);
		rrchart.setDrawYLabels(true);
		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.
				VERTICAL_SCROLLBAR_NEVER);
		this.getContentPane().add(jScrollPane1,BorderLayout.CENTER);
		jScrollPane1.getViewport().add(box2,null);
		//chart.add(box2, null);
		box2.add(box1,null);
		box1.add(tempchart,null);
		box1.add(bppchart,null);
		box1.add(rrchart,null);
	}

	//@todo link to window destroy to stop the timer.
	protected boolean destroy=false;

	/**
	 * This is the observation frequency, in minutes. Currently, observations
	 * are made every 15 minutes.
	 */
	public final int minutesBetweenObservations=15;

	/**
	 * The thread that performs the observations. The thread requests a
	 * notification from Body.clock every 15 minutes, and calls observe().
	 */
	protected Thread observationThread=new Thread(this,"Observations");

	public void run(){
		try{
			while(person==null){
				Thread.sleep(1000);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		Object waiter=new Object();
		while(!destroy){
			try{
				synchronized(waiter){
					person.body.getClock().requestNotifyAfter(minutesBetweenObservations
							*60,waiter);
					waiter.wait();
				}
				observe();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

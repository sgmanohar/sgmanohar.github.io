package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.RadioPanel;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.ThickPanel;

public class BasilarMembrane extends NeurolabExhibit implements ActionListener{
	public String getExhibitName(){return "Basilar Membrane";}

	private Timer timer;
	private double[] env;
	private int npoints=240;
	double invspeed=2.0;//was called speed - ods20
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		env=new double[npoints];
		createComponents();
		timer=new Timer((int)(100/invspeed),this);
	}

	JPanel lpanel,rpanel,blpanel,trpanel;
	JButton start,stop;
	RadioPanel freqpanel;
	TitledBorder tborder1;
	ThickPanel tlpanel;
	GraphPanel graph;

	String[] freqnames={"100 Hz","300 Hz","1 kHz","2 kHz","5 kHz","10 kHz"};
	int[] freqpeak={160,135,100,85,70,55};
	int[] freqvalues={100,300,1000,2000,5000,10000};
	int [] freqspeeds={6, 5, 4, 3, 2, 1}; //ods20
  JCheckBox envelopeCheck;

	public void createComponents(){
		getMainContainer().add(lpanel=new JPanel(),BorderLayout.WEST);
		getMainContainer().add(rpanel=new JPanel(),BorderLayout.EAST);
		lpanel.setLayout(new BorderLayout());
		lpanel.add(blpanel=new JPanel(),BorderLayout.SOUTH);
		lpanel.add(tlpanel=new ThickPanel(19),BorderLayout.NORTH);
		setBG(lpanel);setBG(rpanel);setBG(blpanel);
		tlpanel.setFont(new Font("Dialog",Font.BOLD,14));
								tlpanel.setText("Stapes                                                 Helicotrema");

		tlpanel.inner.add(graph=new GraphPanel());

		blpanel.setLayout(new GridLayout(1,2));	// start and sto
		blpanel.add(start=new JButton("Start")/*,BorderLayout.WEST*/);
		blpanel.add(stop=new JButton("Stop")/*,BorderLayout.EAST*/);
		start.addActionListener(this);
		stop.addActionListener(this);
		setBG(start);setBG(stop);

		rpanel.setLayout(new BorderLayout());
		rpanel.add(new ReturnButton(),BorderLayout.SOUTH);
		rpanel.add(trpanel=new JPanel(),BorderLayout.NORTH);
		setBG(trpanel);

		trpanel.setBorder(tborder1=BorderFactory.createTitledBorder(etched,"Frequency"));
		trpanel.add(freqpanel=new RadioPanel(freqnames,radioListener),BorderLayout.CENTER);
		rpanel.add(envelopeCheck=new JCheckBox("Show envelope"), BorderLayout.CENTER);
		setBG(envelopeCheck);
		setBG(freqpanel);

		freqpanel.setSelected(2);
//		RepaintManager.currentManager(graph).setDoubleBufferingEnabled(true);
//		graph.setDoubleBuffered(true);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Start"){
			graph.repaint();
			initCochlea();
			timer.start();
		}else if(e.getActionCommand()=="Stop"){
			timer.stop();
		}else{	// timer
			tick();
		}
	}
	public ActionListener radioListener = new ActionListener(){
		public void actionPerformed(ActionEvent e){// called when the frequency is changed
		  selectedFreq=freqpanel.getSelected();
			initCochlea();
			graph.repaint();
		}
	};
	int selectedFreq=0;
	int time;
	public void initCochlea(){
		double X,pk;
		pk=freqpeak[freqpanel.getSelected()];
		for(int i=0;i<npoints;i++){
			X=240*(i-pk)/npoints;
			env[i]=100*sfun(1.5*X/pk)*sfun(-10*X/pk);
		}
		time=0;
	}
	double sfun(double x){return 0.5*(1+x/Math.sqrt(1+x*x));}
	void paintmemb(Graphics g){
		int base=(graph.getHeight()-4)/2;
		int wid=graph.getWidth();
		int freq=freqvalues[selectedFreq];
		Point prev,curr=new Point(2, base);

/*		g.setColor(new Color(0,64,0));		//undraw partially!
		curr=new Point2D.Double(2, base);
		for(int i=0;i<npoints;i++){
			prev=curr;
			curr=new Point2D.Double(2+i*(wid-4)/npoints,base+env[i]*Math.sin(2*(i*240/npoints)*Math.PI*(freq/80000.)-time/(double)freqspeeds[freqpanel.getSelected()]));//ods20
			g.draw(new Line2D.Double(prev,curr));
		}

*/		time++;
		g.setColor(Color.green);		//redraw new
		setStrokeThickness(g,1);
		curr=new Point(2, base);
		for(int i=0;i<npoints;i++){
			prev=curr;
			curr=new Point(2+(int)(i*(wid-4)/npoints),
				(int)(base + env[i] * Math.sin(2*(i*240/npoints)*Math.PI*(freq/80000.)
					- time/(double)freqspeeds[selectedFreq])));//ods20
			g.drawLine(prev.x, prev.y, curr.x,curr.y);
		}
	}
	boolean overlay=false;
	public void tick(){
	  overlay=(envelopeCheck!=null && envelopeCheck.isSelected());
		if(!overlay)  graph.repaint();
		else          paintmemb(graph.getGraphics());
	}

	class GraphPanel extends JPanel{
		public int base;
		public void paint(Graphics g){
			super.paint(g);
			antiAlias(g);

			g.setColor(Color.black);
			g.fillRect(2,2,getWidth()-4,getHeight()-4);
			g.setColor(Color.gray);
			base=(getHeight()-4)/2;
			g.drawLine(2,base,getWidth()-4,base);
			g.setColor(Color.gray);
			Point prev,curr;
/*			for(int j=-1;j<2;j+=2){
				curr=new Point2D.Double(2,base);
				for(int i=0;i<npoints;i++){
					prev=curr;
					curr=new Point2D.Double(2+i*(getWidth()-4)/npoints,base+j*env[i]);
					g.draw(new Line2D.Double(prev,curr));
				}
			}
*/			paintmemb(g);
		}
		public Dimension getPreferredSize(){
			return new Dimension(400,230);
		}
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}
}

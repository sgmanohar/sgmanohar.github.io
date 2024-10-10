// Oscilloscope.class by Sanjay Manohar
package com.neurolab.common;

/*example of Oscilloscope:

	int bases={50,100};
	Color cols={Color.red,Color.green};
	add(Oscilloscope o=new Oscilloscope(2,this));
	o.setBaseY(bases);
	o.setColors(cols);
	o.timer.setDelay(100);	// milliseconds
	...
	int[] high={800,500},low={300,100};
	javax.swing.Timer t=new Timer(1000,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			o.setPosY(high);
			o.tick();
			o.setPosY(low);
			o.tick();
		}
	} );
	t.start();
*/


import java.lang.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Oscilloscope extends JPanel implements ActionListener{
	public JButton sweep,clear;
	public javax.swing.Timer timer;
	protected int posX,posY[],baseY[],nTraces;
	public OScreen graph;
	public int xSpeed=1;		// controls how much to advance x every tick
	private ActionListener listener;
	public class OScreen extends Panel{
		private Point pOld[],pNew[];
		private Color color[];	// use getColors() or setColors(Color[])
		private Border bevel;
		private int gutter=10;
		public int getNTraces(){
			return color.length;
		}
		public void init(Color[] c){
			color=c;
			pOld=new Point[getNTraces()];
			pNew=new Point[getNTraces()];
			for(int i=0;i<getNTraces();i++){
				pOld[i]=new Point(0,0);
				pNew[i]=new Point(0,0);
			};
			NeurolabExhibit.setBG(this);

			setBorder(bevel=BorderFactory.createLoweredBevelBorder());
		}
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(Color.black);
				//was RoundRectangle2D
			Rectangle rect = new Rectangle(
				gutter,gutter,getWidth()-2*gutter,getHeight()-2*gutter/*,20,20*/);
			g.fillRect(rect.x,rect.y, rect.width, rect.height);
			drawScreenElements(g);
		}
		public float calcY(float y,int base){
			float i=gutter+y*(getHeight()-2*gutter)/1024;	//scale =[0,1024]
			i+=base;
			if(i<gutter)i=gutter; if(i>(getHeight()-gutter))i=getHeight()-gutter;
			return i;
		}
		public int getDataWidth(){return getWidth()-2*gutter;}
		private void paintTraces(){
			Graphics g=getGraphics();
			if(g==null)return;
//is antialiased
			NeurolabExhibit.antiAlias(g);

			for(int trace=0;trace<pOld.length;trace++){
				g.setColor(color[trace]);
				g.drawLine((int)(gutter+pOld[trace].x), (int)calcY(pOld[trace].y,baseY[trace]),
							(int)(gutter+pNew[trace].x),(int)calcY(pNew[trace].y,baseY[trace]) );
			}
		}
		public void finalize() throws Throwable{
			timer.stop();
			super.finalize();
		}
	}
	public Panel buttons;
	public Oscilloscope(){this(1,null);}
	public Oscilloscope(int n,ActionListener list){
		listener=list;	// pass a listener to receive "Sweep" ActionEvents
		nTraces=n;
		posY=new int[nTraces];
		baseY=new int[nTraces];
			// the south controls
		setLayout(new BorderLayout());
		buttons=new Panel();
		buttons.setLayout(new GridLayout());
		buttons.add(sweep=new JButton("Sweep"));
		sweep.addActionListener(this);
		buttons.add(clear=new JButton("Clear"));
		add(buttons,BorderLayout.SOUTH);
		clear.addActionListener(this);
		NeurolabExhibit.setBG(sweep);
		NeurolabExhibit.setBG(clear);
			// the timer
		timer=new javax.swing.Timer(100,new ActionListener(){	// default timestep = 100
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
			// the screen
		add(graph=new OScreen(),BorderLayout.CENTER);
		Color cols[]=new Color[nTraces];
		Color seq[]={Color.green,Color.red,Color.yellow,Color.white};
		for(int i=0;i<nTraces;i++) cols[i] = seq[i % seq.length];
		graph.init(cols);	// create nTraces lines with default colours
		graph.repaint();	// posts inital repaint message
//Sweeps on start
		sweep.doClick();
	}

	// use  timer.start(), timer.stop(), timer.restart(), timer.setDelay() to control the timebase manually
	// call tick() manually to hasten a redraw -- e.g. for AP spikes?
	public void tick(){
		if((posX+=xSpeed)>graph.getDataWidth())timer.stop();else{ // alter this line if you want a cycling display!
			graph.pOld=graph.pNew;
			graph.pNew=new Point[graph.getNTraces()];
			setNewPoint();	//advance all cathode rays
			graph.paintTraces();
		};
	}
	public void drawScreenElements(Graphics g){
		//override if you want to, say, add text to display
	}
	private void setNewPoint(){
		for(int i=0;i<graph.getNTraces();i++)
			graph.pNew[i]=new Point(posX,posY[i]);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			if(listener!=null)
			listener.actionPerformed(e);	// inform creator to resart
			posX=0;setNewPoint();		// move to left edge
			timer.start();			// if not still going?
		}else if(e.getActionCommand()=="Clear"){
			graph.repaint();		//clear screen
			posX=0;setNewPoint();
		}
	}
	public void setPosY(int ny[]){posY=ny;}	// for moving the guns
	public int[] getPosY(){return posY;}
	public void setPosX(int nx){posX=nx;}
	public int getPosX(){return posX;}

	public int[] getBaseY(){return baseY;}
	public void setBaseY(int[]nb){baseY=nb;}
	public void setColors(Color[]c){
		graph.color=c;
	}
	public Color[] getColors(){return graph.color;}
	public int getGutter(){return graph.gutter;}
	public void setGutter(int g){graph.gutter=g;}
	public void finalize() throws Throwable{
		timer.stop();
		super.finalize();
	}
}


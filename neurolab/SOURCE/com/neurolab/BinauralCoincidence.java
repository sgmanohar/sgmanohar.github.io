// Binaural Coincidence.class by Sanjay Manohar
package com.neurolab;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.geom.*;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.*;
public class BinauralCoincidence extends NeurolabExhibit{
	public String getExhibitName(){return "Binaural Coincidence";}
	public void init(){
		super.init();
		createComponents();
	}
	JPanel mainpanel;
	WorldPanel world;
	EarPanel ear;
	public void createComponents(){
		getMainContainer().setLayout(new BorderLayout());
		getMainContainer().add(new ReturnButton(),BorderLayout.SOUTH);
		getMainContainer().add(mainpanel=new JPanel(),BorderLayout.NORTH);
		mainpanel.setBorder(loweredbevel);
		setBG(mainpanel);

		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(world=new WorldPanel(),BorderLayout.NORTH);
		world.setPreferredSize(new Dimension(520,200));
		mainpanel.add(ear=new EarPanel(),BorderLayout.SOUTH);
		ear.setPreferredSize(new Dimension(520,90));
		ear.setBorder(raisedbevel);
	}
	public class WorldPanel extends JPanel implements MouseListener, ActionListener{
		String clicktext="Click in the black area";
		final int gutter=5,textheight=15;	//finals

		javax.swing.Timer timer;
		Vector radii;		// vector of Integer
		Vector centres;		// vector of Point2D.Double
		Point2D earhole[];
		int[] earred;
		boolean initing;
		Vector collisions;	// vector of Boolean[2]
		int ringstroke;
		static final int PROPAGATION_RATE=25;
		public WorldPanel(){
			super();
			setBG(this);
			timer=new javax.swing.Timer(100,this);
			centres=new Vector();
			radii=new Vector();
			collisions=new Vector();
			addMouseListener(this);
			earhole=new Point2D[2];
			earred=new int[2];
			initing=true;
			ringstroke=3;
		}
		public void paint(Graphics g){
			super.paint(g);
			antiAlias(g);
			g.setFont(new Font("Arial",Font.BOLD,16));
			paintText3D(g,clicktext,(getWidth()-getTextWidth(g,clicktext))/2,16);
			g.setColor(Color.black);
			g.fillRect(gutter,textheight+gutter,getWidth()-2*gutter,getHeight()-textheight-30-gutter);
			for(int i=0;i<2;i++)
				drawEarHole(g,i);
		}
		public void drawEarHole(Graphics g,int i){
			if(initing){
				earhole[0]=new Point2D.Double(gutter+15,getHeight()-30);
				earhole[1]=new Point2D.Double(getWidth()-gutter-15,getHeight()-30);
				initing=false;
			}
			if(earred[i]>0)g.setColor(Color.red);
			else g.setColor(Color.black);
			setStrokeThickness(g,4);
			g.fillOval((int)earhole[i].getX()-10, (int)earhole[i].getY()-10, 20, 20);
			g.drawLine((int)earhole[i].getX(), (int)earhole[i].getY(),
				(int)earhole[i].getX(), getHeight());
		}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent w){}
		public void mouseClicked(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mousePressed(MouseEvent e){
			centres.add(e.getPoint());
			radii.add(new Integer(0));
			Boolean[] coll;
			collisions.add(coll=new Boolean[2]);
			coll[0]=new Boolean(false);coll[1]=new Boolean(false);
			ear.clearneurones();
			timer.start();
		}
		public void actionPerformed(ActionEvent e){
//			if(radii.size()==0){timer.stop();}
			Graphics g=getGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			for(int i=0;i<2;i++)if(earred[i]>0){
				earred[i]--;
				drawEarHole(g,i);
			}		// redraw earholes?
			g.clipRect(gutter, gutter+textheight, getWidth()-2*gutter, getHeight()-textheight-gutter-30);
			g.setColor(Color.black);
			setStrokeThickness(g,ringstroke);	//prepare for rings
			Point2D[] c=new Point2D[radii.size()];
			int[] r=new int[radii.size()];
			for(int i=0;i<radii.size();i++){
				c[i]=(Point2D)centres.get(i);
				r[i]=((Integer)radii.get(i)).intValue();
				g.drawOval((int)c[i].getX()-r[i]/2,(int)c[i].getY()-r[i]/2,r[i],r[i]);
			}	// erase
			g.setColor(Color.red);
			for(int i=0;i<radii.size();i++){
				r[i]+=PROPAGATION_RATE;	//increment copy of value
				g.drawOval((int)c[i].getX()-r[i]/2,(int)c[i].getY()-r[i]/2,r[i],r[i]);
				radii.set(i,new Integer(r[i]));
				for(int j=0;j<2;j++)
				if( (r[i]/2>c[i].distance(earhole[j]))&&
						!(((Boolean[])(collisions.get(i)))[j].booleanValue()) ){
						earred[j]=3;
//						drawEarHole(g,j);	//earhole goes red
//						g.setStroke(ringstroke);
						ear.fireEar(j);
						((Boolean[])collisions.get(i))[j]=new Boolean(true);
				}
			}
			for(int i=0;i<radii.size();i++){
				if(r[i]>2*getWidth()){
					radii.remove(i);
					centres.remove(i);
					collisions.remove(i);
					g.setColor(Color.black);
					g.drawOval((int)c[i].getX()-r[i]/2,(int)c[i].getY()-r[i]/2,r[i],r[i]);
				}
			}	// redraw
		}
		public void finalize() throws Throwable{
			timer.stop();
			super.finalize();
		}
	}
	public class EarPanel extends JPanel0 implements ActionListener{
		public static final int CELL_TIMER_MS = 250;
		Point2D p0[]=new Point2D[2],p1[]=new Point2D[2];
		final int gutter=5;
		final int NUMCELLS=18;
		boolean initing=true;
		boolean[] celllit=new boolean[NUMCELLS]; // is the cell lit up?
		int[] earlit=new int[2];	// boolean, effectively, is the ear lit up?
		Vector[] ap=new Vector[2];	// list of action potentials in each direction
		Timer timer=new Timer(CELL_TIMER_MS,this);
		int thick=5;
		public EarPanel(){
			ap[0]=new Vector();
			ap[1]=new Vector();
		}
		public void paint(Graphics g){
			if(initing){
				p0[0]=new Point2D.Float(gutter+15,20);
				p1[0]=new Point2D.Float(getWidth()-gutter-30,20);
				p1[1]=new Point2D.Float(gutter+30,40);
				p0[1]=new Point2D.Float(getWidth()-gutter-15,40);
				initing=false;
			}
			super.paint(g);
			antiAlias(g);
			setStrokeThickness(g,thick);
			for(int i=0;i<2;i++){
				paintear(g,i);
				g.setColor(Color.black);
				g.drawLine((int)p0[i].getX(), (int)p0[i].getY(),
									 (int)p1[i].getX(), (int)p1[i].getY() );
			}
			Point2D p;
			for(int i=0;i<NUMCELLS;i++){
				paintcell(g,i);
			}
		}
		public void paintear(Graphics g,int i){
			g.setColor(((--earlit[i]>3) ||(earlit[i]<1))?Color.black:Color.red);
			g.drawLine((int)p0[i].getX(), (int)p0[i].getY(), (int)p0[i].getX(), 0);
			g.fillOval((int)p0[i].getX()-10, (int)p0[i].getY()-10, 20, 20);
		}
		public void paintcell(Graphics g,int i){
			g.setColor(celllit[i]?Color.red:Color.black);
			double cx=p1[1].getX()+(p1[0].getX()-p1[1].getX())*(i+1)/(NUMCELLS+1);
			g.fillOval((int)cx-10,60,20,20);
			g.drawLine((int)cx,60,(int)cx,10);
		}
		public void fireEar(int e){
			earlit[e]=5;
			paintear(getGraphics(),e);
			ap[e].add(new Integer(0));
			timer.start();
		}
		public void clearneurones(){
			Graphics g=getGraphics();
			setStrokeThickness(g,thick);
			for(int i=0;i<NUMCELLS;i++){
				celllit[i]=false;
				paintcell(g,i);
			}
		}
		public void actionPerformed(ActionEvent ev){
			int p,numaps=0;
			Integer o;
			Graphics g=getGraphics();
			setStrokeThickness(g,thick);
			for(int i=0;i<2;i++){
				if(earlit[i]>0)paintear(g,i);
				for(Enumeration e=ap[i].elements();e.hasMoreElements();){
					p=(o=(Integer)e.nextElement()).intValue();	//get position from vector
					g.setColor(Color.black);
					if(p>0)drawAP(g,i,p);		//undraw if not starting
					p++;
					if(p<NUMCELLS+2){
						g.setColor(Color.red);
						drawAP(g,i,p);			//redraw
						ap[i].set(ap[i].indexOf(o),new Integer(p));
						numaps++;			//there is more to do..
					}else ap[i].remove(o);
				}
			}
			if(numaps==0) {
			  //timer.stop();	// if no more
			}
			else{				//collision check:
				int q;
				for(Enumeration e=ap[0].elements();e.hasMoreElements();){
					p=((Integer)e.nextElement()).intValue();
					for(Enumeration f=ap[1].elements();f.hasMoreElements();){
						q=((Integer)f.nextElement()).intValue();
						if((p==NUMCELLS-q)||(p==NUMCELLS-q+1)){
							if((p>0)&&(p<NUMCELLS+1)){
								celllit[p-1]=true;
								paintcell(g,p-1);
							}
						}
					}
				}
			}
		}
		public void drawAP(Graphics g, int i,int pos){
			double dx=(1-2*i)*(p1[0].getX()-p1[1].getX())/(NUMCELLS+1);
			double cx=p1[1-i].getX()+dx*(pos-1);
			if(dx<0){cx+=dx;dx=Math.abs(dx);}	//switch left&right points if reversed
			g.drawLine((int)cx, (i==0)?20:40, (int)(cx+dx), (i==0)?20:40);
		}
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		world.timer.stop();
		ear.timer.stop();
	}
}

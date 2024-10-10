
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import com.cudos.common.*;
import javax.swing.JPanel;
import java.awt.*;
import java.util.*;

public class Circuitboard extends JPanel {

	public int sx=32,sy=32;
	public int cw=10,ch=10;
	public double deltat=0.0001;
	Rectangle nodesrect=new Rectangle(cw,ch);
	Image cbimage=null;
	Vector components=new Vector();
	CircuitComponent selection;

	public void addCircuitComponent(CircuitComponent cc){
		components.add(cc);
		repaint();
	}
	public void removeCircuitComponent(CircuitComponent cc){
		components.remove(cc);
		repaint();
	}
        public void removeAllCircuitComponents(){
          components.removeAllElements();
          repaint();
        }
	public Circuitboard() {
	}
	public void init(){
		cbimage=CudosExhibit.getApplet(this).getImage("resources/icons/Circuitboard.jpg");
		for(int i=0;i<cw;i++)rail[i]=new CircuitRail(this,i);
	}
	public void paint(Graphics g){
		super.paint(g);
		if(cbimage!=null)
		for(int i=0;i<cw;i++){
			for(int j=0;j<ch;j++){
				g.drawImage(cbimage,sx*i,sy*j,this);
			}
		}
		g.translate(sx/2+3,sy/2+3);
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent c=(CircuitComponent)e.nextElement();
			c.paintCircuit(g);
		}
	}
	public void repaintCircuitComponent(CircuitComponent cc){
		Graphics g=getGraphics();
		if(cbimage!=null){
			int s1,s2;
			if(cc.c2>cc.c1){s1=cc.c1;s2=cc.c2;}else{s1=cc.c2;s2=cc.c1;}
			for(int i=s1;i<=s2;i++){
				g.drawImage(cbimage,sx*i,sy*cc.ch,this);
			}
		}
		g.translate(sx/2+3,sy/2+3);
		cc.paintCircuit(g);
	}
	public Point getNearestNode(Point p){
		Point np=new Point(p.x/sx,p.y/sy);
		if(nodesrect.contains(np))return np; else return null;
	}
	public Point getExactNode(Point p){
		Point np=getNearestNode(p);
		if(np!=null){
			if(Math.abs(np.x*sx+sx/2-p.x)<7 && Math.abs(np.y*sy+sy/2-p.y)<7)return np;
		};
		return null;
	}
	public CircuitComponent getComponent(Point p){
		Point np=getNearestNode(p);
		if(np==null)return null;
		CircuitComponent nc=null;
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent c=(CircuitComponent)e.nextElement();
			if((c.ch==np.y)&&
			   ((c.c1>=np.x && c.c2<=np.x) || (c.c2>=np.x && c.c1<=np.x)) ){
				nc=c;
			}
		}
		return nc;
	}
	public CircuitComponent getComponentAtNode(Point np){
		CircuitComponent nc=null;
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent c=(CircuitComponent)e.nextElement();
			if((c.ch==np.y)&&
			   (c.c1==np.x || c.c2==np.x) ){	//is left or right
				nc=c;
			}
		}
		return nc;
	}
	public boolean isOpen(Point cp){
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent c=(CircuitComponent)e.nextElement();
			if(c.ch==cp.y && 			// same horizontal level
			   ( (c.c1>=cp.x && c.c2<=cp.x)		// cp between c1 & c2
			   ||(c.c1<=cp.x && c.c2>=cp.x) )  ) return false;
		}
		return true;
	}
	public boolean isLineClear(int l,int r,int h,CircuitComponent exclude){
		if(l>r){int t=l;l=r;r=t;}	//swap if l>r
		if(!(nodesrect.contains(l,h) && nodesrect.contains(r,h)))return false;
		for(int cx=l;cx<=r;cx++){
			for(Enumeration e=components.elements();e.hasMoreElements();){
				CircuitComponent c=(CircuitComponent)e.nextElement();
				if(c!=exclude)
				if(c.ch==h && 			// same horizontal level
				   ( (c.c1>=cx && c.c2<=cx)		// cp between c1 & c2
				   ||(c.c1<=cx && c.c2>=cx) )  ) return false;
			}
		}
		return true;
	}
	public CircuitRail[] rail=new CircuitRail[cw];


	//get Component functions
		//extract components that have behaviour b
	public Vector getByBehaviour(Vector v,int b){
		Vector r=new Vector();
		Object o;
		for(Enumeration e=v.elements();e.hasMoreElements();)
			if( ((CircuitComponent)(o=e.nextElement())).getBehaviour()==b ) r.add(o);
		return r;
	}

	//calculator
	boolean traversed[];			// whether each component has been traced
	Vector pathlist=new Vector();		// all paths in circuit
	public void startCalculation(){
			//Clear everything
		traversed=new boolean[components.size()];
		for(int i=0;i<traversed.length;i++){
			traversed[i]=false;
			((CircuitComponent)components.get(i)).current=0;
		}
		for(int i=0;i<rail.length;i++)rail[i].voltage=Double.NaN;
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent cc=(CircuitComponent)e.nextElement();
			cc.paths.removeAllElements();
			cc.directions.removeAllElements();
		}
		pathlist.removeAllElements();

		Vector emfc=getByBehaviour(components,CircuitComponent.B_EMF);
		for(Enumeration e=emfc.elements();e.hasMoreElements();){
			// for each EMF component:
			CircuitComponent cc=(CircuitComponent)e.nextElement();
			if(!traversed[components.indexOf(cc)]){	// if it has not been traversed,
				rail[cc.c1].voltage=0;
				TraceStatus stat=new TraceStatus();
				int sz=pathlist.size();
				stat=traceCircuit(cc,cc.c1,stat);
				if(pathlist.size()==sz){
					//no paths found for this emf component
					if(Double.isNaN(rail[cc.c2].voltage))rail[cc.c2].voltage=cc.getEMF();
				}
			}
		}
		doEliminateOpenPaths();
		doPathCurrentCalculation();
		doVoltageCalculation();
		for(Enumeration e=components.elements();e.hasMoreElements();){
			CircuitComponent cc=(CircuitComponent)e.nextElement();
			cc.process();		// final processing of components
		}
	}


	class TraceStatus implements Cloneable{
//		double resistance=0;
		double emf=0;	//cumulative traces
		boolean[] rcrossed=new boolean[cw];	//traced rails
		Vector ccrossed=new Vector();		//traced components
		Vector directions=new Vector();		//directions components were traced
		TraceStatus(){
			for(int i=0;i<rcrossed.length;i++)rcrossed[i]=false;
		}
		public Object clone(){
			try {
				TraceStatus n=(TraceStatus)super.clone();
				n.ccrossed=(Vector)ccrossed.clone();
				n.directions=(Vector)directions.clone();
				n.rcrossed=(boolean[])rcrossed.clone();
//				n.resistance=resistance;n.emf=emf;
				return n;
			} catch(Exception x){x.printStackTrace();return null;}
		}
	}
	class CircuitPath {
		CircuitComponent cstart;
		Vector components=new Vector();
		Vector directions=new Vector();
		double current=Double.NaN;
		double emf=0;
	}
	public TraceStatus traceCircuit(CircuitComponent c,int r, TraceStatus s){
		// c gives start and end rails
		// r = current rail
		if(r==c.c2){
			//completed one loop!
			CircuitPath cp=new CircuitPath();
			cp.cstart=c;
			cp.emf=s.emf+c.getEMF();	//total emf without resistances
			pathlist.add(cp);

			for(Enumeration e=s.ccrossed.elements();e.hasMoreElements();){
				CircuitComponent cc=(CircuitComponent)e.nextElement();
				cp.components.add(cc);			// add component to path
				cc.paths.add(cp);			// add path to component
				Boolean d;
				cp.directions.add( d=(Boolean)(s.directions.get(s.ccrossed.indexOf(cc))) );
				cc.directions.add(d);
				traversed[components.indexOf(cc)]=true;	// don't bother starting a new set of circuits from here
			}
			return null;
		}
		s.rcrossed[r]=true;	// don't set rcrossed for c.c2!
		Vector nextc=rail[r].getComponentsOnRail();
		for(Enumeration e=nextc.elements();e.hasMoreElements();){
			CircuitComponent cc=(CircuitComponent)e.nextElement();
			if(cc!=c && cc.passesCurrent()){	//ignore passing back through c!
				int dr=getDestRail(cc,r);
				if(!s.rcrossed[dr]){
					TraceStatus n=(TraceStatus)s.clone();		// send next loop branch
//					n.resistance+=cc.getResistance();
					n.emf-=((r==cc.c1)?1:-1)*cc.getEMF();
					n.ccrossed.add(cc);
					n.directions.add(new Boolean(r==cc.c1));
					n=traceCircuit(c,dr,n);			// process all components to complete circuit
				}
					//else no connection here; skip to next branch
			}
		}
		return s;
	}
	public void doEliminateOpenPaths(){
		Vector removal=new Vector();
		for(Enumeration e=pathlist.elements();e.hasMoreElements();){
			CircuitPath cp=(CircuitPath)e.nextElement();
			boolean valid=true;
			for(Enumeration f=cp.components.elements();f.hasMoreElements();){
				CircuitComponent cc=(CircuitComponent)f.nextElement();
				boolean dir=((Boolean)(cp.directions.get(cp.components.indexOf(cc)))).booleanValue();
				double res=cc.getResistanceFromEMF(cp.emf*(dir?1:-1));
				if(Double.isInfinite(res))valid=false;
			}
			if(!valid){	//delete this path
				for(Enumeration f=cp.components.elements();f.hasMoreElements();){
					CircuitComponent cc=(CircuitComponent)f.nextElement();
					int ix=cc.paths.indexOf(cp);
					cc.paths.remove(ix);
					cc.directions.remove(ix);
					traversed[components.indexOf(cc)]=false;
				}
				removal.add(cp);
			}
		}
		pathlist.removeAll(removal);
	}

	public int getDestRail(CircuitComponent c,int r){
		if(c.c1==r)return c.c2;
		else if(c.c2==r)return c.c1;
		else System.out.println("Bad destination rail from "+r+" on "+c);
		return 0;
	}


	public void doPathCurrentCalculation(){
		int n=pathlist.size();
		if(n==0)return;
		double[][] resistance=new double[n][n];	//resistance matrix
		for(Enumeration e=pathlist.elements();e.hasMoreElements();){
				//for each path
			CircuitPath cp=(CircuitPath)e.nextElement();
			int icp=pathlist.indexOf(cp);
			for(Enumeration f=cp.components.elements();f.hasMoreElements();){
					//for each component in the path
				CircuitComponent cc=(CircuitComponent)f.nextElement();
				boolean dir=((Boolean)(cp.directions.get(cp.components.indexOf(cc)))).booleanValue();
				double res=cc.getResistanceFromEMF(cp.emf*(dir?1:-1));
				for(Enumeration g=cc.paths.elements();g.hasMoreElements();){
						//add its resistance to matrix at every path it crosses
					CircuitPath rp=(CircuitPath)g.nextElement();
					int irp=pathlist.indexOf(rp);
					resistance[icp][irp]+=res;
				}
			}
		}
		Matrix m=new Matrix(resistance).inverse();
		if(m!=null){
			double[][] emf=new double[n][1];
			for(int i=0;i<n;i++){
				emf[i][0]=((CircuitPath)pathlist.get(i)).emf;
			}
			Matrix current=m.multiply(new Matrix(emf));
			if(current==null)System.out.println("Error@current calculation");
			else{
				for(int i=0;i<n;i++){
					CircuitPath cp=(CircuitPath)(pathlist.get(i));
					cp.current=current.a[i][0];
				}
			}
		}
	}
	public void doVoltageCalculation(){
		for(Enumeration e=pathlist.elements();e.hasMoreElements();){
			//for each path
			CircuitPath cp=(CircuitPath)e.nextElement();
			int r=cp.cstart.c1;
			double potential=rail[r].voltage;
			if(Double.isNaN(potential))potential=rail[r].voltage=0;
			for(Enumeration f=cp.components.elements();f.hasMoreElements();){
				//for each component in the path
				CircuitComponent cc=(CircuitComponent)f.nextElement();
				//calculate current through component
				int icp=cc.paths.indexOf(cp);
				int dir=((Boolean)cc.directions.get(icp)).booleanValue()?1:-1;
				cc.current+=dir*cp.current;
				//calculate pd across component
				potential+=dir*cc.current*cc.getResistanceFromEMF(dir*cp.emf)+dir*cc.getEMF();
				//set next rail
				r=getDestRail(cc,r);
				rail[r].voltage=potential;
			}
			// add path current to current through emf-providing component
			cp.cstart.current-=cp.current;
		}
	}
}


/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.kinetic;

import java.awt.*;

public class SodiumPump extends MembraneTransporter {
	int numNBound=0;
	int numKBound=0;
	Molecule[] na=new Molecule[3],k=new Molecule[2];	//bound ions?

	int frame=0,frames=7;
	boolean transporting=false;
	Color molbg,pumpColor=new Color(128,192,128);
	public boolean initTransport(Molecule m){
		if((!transporting)&&(m.getType()==Molecule.SODIUM)&&(transportDirectionY>0)&&(numNBound<3)){
			na[numNBound]=m;
			m.y=y-s;
			m.x=x-s+(numNBound+1)*s/2-m.s/2;
			molbg=m.bg;m.bg=pumpColor;
			if((++numNBound==3) && (numKBound==2)){
				transporting=true;
				frame=frames;
			}
			return true;
		}else if((!transporting)&&(m.getType()==Molecule.POTASSIUM)&&(transportDirectionY<0)&&(numKBound<2)){
			k[numKBound]=m;
			m.y=y+s-m.s;
			m.x=x-s+(numKBound+1)*2*s/3-m.s/2;
			molbg=m.bg;m.bg=pumpColor;
			if((++numKBound==2) &&(numNBound==3)){
				transporting=true;
				frame=frames;
			}
			return true;
		}else return false;
	}
	public void postInitTransport(){
		if(!transporting){
			bound=false;ligand=null;
			//but leave me as the molecule's listener.
		}
	}
	public boolean continueTransport(Molecule m){
		if(transporting){
			if(m.getType()==Molecule.SODIUM){
				m.y+=(3*s/frames);
				if(m.y>=y+s)m.bg=molbg;
			}else if(m.getType()==Molecule.POTASSIUM){
				m.y-=(3*s/frames);
				if(m.y<=y-s-m.s)m.bg=molbg;
			}
			if(m==na[0])frame--;
			if(frame<=0){
				int d=(m.getType()==Molecule.SODIUM)?1:-1;
				if(d==1)numNBound--;else numKBound--;
				if((numNBound<=0) &&(numKBound<=0)){numNBound=0;numKBound=0;transporting=false;}
				m.bg=molbg;
				m.y+=d*m.s+2;
				m.vy=d*Math.abs(m.vy);
				return false;
			}
		}else{
/*			if(rand.nextInt(10)==1){
				m.unbind();								//want to diffuse out of active site?
				m.bg=molbg;
				if(m.getType()==Molecule.SODIUM){m.y-=m.s+5;m.vy=-Math.abs(vy);}	//release Na upwards
				else{m.y+=m.s+5;vy=Math.abs(vy);}					//release K downwards
			}
*/		}
		return true;
	}
	public void paint(Graphics2D g,boolean u){
		g.setColor(pumpColor);
		g.fillRoundRect(x-s,y-s,2*s,2*s,4,4);
	}
  public SodiumPump(Membrane m,int a,int w) {
	super(m,a,w);
  }
}
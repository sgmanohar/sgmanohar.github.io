
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

public class Antiporter extends MembraneTransporter {
	boolean transporting=false;
	boolean nBound=false,sBound=false;
	Color molbg,pumpColor=Color.pink;
	Molecule na,su;
	int frame=0,frames=8;
	public boolean initTransport(Molecule m){
		if((!transporting)&&(m.getType()==Molecule.SODIUM)&&(transportDirectionY<0)&&(!nBound)){
			na=m;
			m.y=y+s-m.s;
			m.x=x-s+4*s/3-m.s/2;
			molbg=m.bg;m.bg=pumpColor;
			nBound=true;
			if(sBound){
				transporting=true;
				frame=frames;
			}
			return true;
		}else if((!transporting)&&(m.getType()==Molecule.SUGAR)&&(transportDirectionY>0)&&(!sBound)){
			su=m;
			m.y=y-s;
			m.x=x-s+2*s/3-m.s/2;
			molbg=m.bg;m.bg=pumpColor;
			sBound=true;
			if(nBound){
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
				m.y-=(3*s/frames);
				if(m.y<=y-s-m.s)m.bg=molbg;
			}else if(m.getType()==Molecule.SUGAR){
				m.y+=(3*s/frames);
				if(m.y>=y+s)m.bg=molbg;
			}
			if(m==na)frame--;
			if(frame<=0){
				int d=(m.getType()==Molecule.SUGAR)?1:-1;
				if(d==1)nBound=false;else sBound=false;
				if((!nBound) && (!sBound))transporting=false;
				m.bg=molbg;
				m.y+=d*m.s+2;
				m.vy=d*Math.abs(m.vy);
				return false;
			}
		}
		return true;
	}
	public void paint(Graphics2D g,boolean u){
		g.setColor(bg);
		g.fillRect(x-s+2*s/3,y-s,2*s/3,2*s);
		g.setColor(pumpColor);
		g.fillRoundRect(x-s,y-s,2*s/3,2*s,4,4);
		g.fillRoundRect(x-s+4*s/3,y-s,2*s/3,2*s,4,4);
	}
  public Antiporter(Membrane m,int a,int w) {
	super(m,a,w);
  }
}

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
import java.util.*;

import com.cudos.common.*;
/*
v1	implements Ligand
v2	extends Molecule
*/
public abstract class MembraneTransporter extends Molecule implements PaintableComponent,MoleculeListener/*,Ligand*/{
/*	Ligand ligand=null;					//interface ligand methods
	public Ligand getLigand(){return ligand;}
	public void setLigand(Ligand l){ligand=l;}
	boolean bound=false;
	public boolean isBound(){return bound;}
	public void setBound(boolean b){
		if(bound && b)throw new RuntimeException("Ligand already bound!");
		bound=b;
	}
*/
	Vector walls=new Vector();
	Membrane membrane;
	protected int activeType=Molecule.SUGAR; // the molecule which binds to the transporter

  	public MembraneTransporter(Membrane mem,int along,int nw) {
		membrane=mem;
		if(membrane.horz){
			x=along+membrane.pos.p1;			//set coordinates relative to
			y=membrane.pos.ordinate+membrane.thickness/2;	//membrane.
		}else{
			y=along+membrane.pos.p1;
			x=membrane.pos.ordinate-membrane.thickness/2;
		}
		setType(MEMBRANE_PROTEIN);
		s=nw;
	}
	public void paint(Graphics2D g,boolean u){
		if(u){
			g.setColor(cols[getType()]);
			g.fillOval(x-s,y-s,s*2,s*2);
		}
	}

	protected int transportDirectionX,transportDirectionY;
	public boolean moleculeEvent(Molecule m){
		Molecule lig=null;
		if((!bound)&&(				// ensure I am not busy,
		    (!m.isBound()) || (m.isBound()&&( (lig=m.getLigand()).getType()==Molecule.WATER) )) ){	//and molecule is not busy.
			if(m.isBound())m.unbind();	//if ligand present (must be water!), then unbind it.
//			if(m.getType()==activeType){
				transportDirectionX=transportDirectionY=0;		//set direction of transport
				if(membrane.horz)transportDirectionY=((m.y>y)?-1:1);
				else transportDirectionX=((m.x>x)?-1:1);

				if(initTransport(m)){					//attempt to initate transport
					m.setLigand(this);m.setBound(true);
					ligand=m;bound=true;	//set my params
					m.setMoleculeListener(this);	//call the transporter next timer the molecule has to move...

					postInitTransport();

					return true;
				}else return false;
//			}else return false;
		}else if(m.getLigand()==this){
				//molecule transporting
			if(!continueTransport(m)){
				m.setBound(false);
				m.setLigand(null);
				m.setMoleculeListener(null);
				bound=false;ligand=null;	//my params;
				m.x+=transportDirectionX*(4+m.s);//clear from transporter?
				m.y+=transportDirectionY*(4+m.s);
			}
			return true;
		} else return false;//transporter busy-is active with another molecule!
	}
	public abstract boolean initTransport(Molecule m);	// should return true to start transport, false to reject
	public abstract boolean continueTransport(Molecule m);	// should return true for continue, false to terminate
	public void postInitTransport(){}			// override to reset 'bound' if not full (multiporters only)
}
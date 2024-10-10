
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

import com.cudos.common.*;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

public class KineticPane extends JPanel implements ActionListener{

	public Vector molecules;
	public Vector paintables;
	public Vector hwalls;
	public Vector vwalls;
	public Vector swalls;

	public Timer timer;
	Random rand=new Random();
	public int nCollisions=0;
	public KineticPane() {
		molecules=new Vector();
		paintables=new Vector();
		hwalls=new Vector();
		vwalls=new Vector();
		swalls=new Vector();
		timer=new Timer(100,this);
		timer.start();
	}
	public void actionPerformed(ActionEvent e){
		Molecule m;int nx,ny;
		Wall w;
		Graphics2D g=(Graphics2D)getGraphics();
		for(Enumeration n=molecules.elements();n.hasMoreElements();){
			boolean hitx=false,hity=false;
			m=(Molecule)n.nextElement();
			m.paint(g,false);		//undraw molecule


			if(m.isBound() &&
				((m.getLigand().getType()==Molecule.MEMBRANE_PROTEIN)	//if bound to transporter
				 ||(m.getType()==Molecule.WATER)			//or is hydration ligand
				 ||(m instanceof Antibody) ) ){
				m.fireMoleculeEvent();					//let it be moved by the listener!
			} else if(m instanceof MembraneTransporter){
				//do not move it...
			}else {
				if(m instanceof MassiveMolecule){
					MassiveMolecule mol=(MassiveMolecule) m;
					mol.move();
					nx=(int)mol.x;			//get new x
					ny=(int)mol.y;			//get new y
				}else{
					nx=m.x+m.vx;
					ny=m.y+m.vy;
				}

				//Special collision test
				for(Enumeration sw=swalls.elements();sw.hasMoreElements();){
					SpecialWall wl=(SpecialWall)sw.nextElement();
					boolean result;
					if(m instanceof MassiveMolecule){
						MassiveMolecule mol=(MassiveMolecule)m;
						if(wl.collisionTest(mol,(double)nx,(double)ny)){
							mol.move();nx=(int)mol.x;ny=(int)mol.y;
							MassiveMolecule m2=((CircularWall)wl).M;
							m2.paint((Graphics2D)getGraphics(),false);
							m2.move();
							m2.paint((Graphics2D)getGraphics(),true);
//System.out.println("->"+mol.vx+","+mol.vy+"<>"+((CircularWall)wl).M.vx+","+((CircularWall)wl).M.vy);
						}
					}else{
						if(wl.collisionTest(m,nx,ny)){
							nx=m.x+m.vx;ny=m.y+m.vy;nCollisions++;
						}
					}
				}

					//Horizontal collision test
				if(nx<0 || nx>getWidth()-m.s)hitx=true;
				for(Enumeration vw=vwalls.elements();vw.hasMoreElements();){
					w=(Wall)vw.nextElement();
					if( ( ((m.y<=w.p2) &&(m.y+m.s>=w.p1)) // y between ends
							||((ny<=w.p2) &&(ny+m.s>=w.p1)) ) // or ny between ends
						&&(	  ((nx+m.s>=w.ordinate)&&(m.x<w.ordinate))	//x crosses ordinate
						||((nx<=w.ordinate)&&(m.x+m.s>w.ordinate)) ) ) {
						if((w.action==Wall.EVENT)&&			//has hit a special?
							w.fireMoleculeEvent(m)){
							nx=m.x;ny=m.y;		//restore uncollided pos
/*						}else if((m.getType()==Molecule.WATER)&&(rand.nextDouble()<Membrane.semipermeable)){	//water permeable
							nx+=(m.vx/Math.abs(m.vx))*(Membrane.thickness+10);
*/						}else hitx=true;				//has hit a bounce
						break;
					}
				}
				if(hitx){
					if(m instanceof MassiveMolecule){
						MassiveMolecule mol=(MassiveMolecule) m;
						mol.x-=/* 2* */mol.vx;			//
						mol.vx=-mol.vx;
						nx=(int)mol.x;
					}else{
						m.vx=-m.vx;		//bounce routine
			//			m.x+=m.vx;
						nx=m.x;	//this is so as not to confuse the subsequent y calculation
					}
				}else m.x=nx;			//no bounce: set new x

				//Vertical collision test
				if(ny<0 || ny>getHeight()-m.s)hity=true;
				for(Enumeration hw=hwalls.elements();hw.hasMoreElements();){
					w=(Wall)hw.nextElement();
					if( ( ((m.x<=w.p2) &&(m.x+m.s>=w.p1)) // (x between ends
							||((nx<=w.p2) &&(nx+m.s>=w.p1))  )// or nx between ends)
						&&(	  ((ny+m.s>=w.ordinate)&&(m.y<w.ordinate))	// and (y crosses ordinate)
						||((ny<=w.ordinate)&&(m.y+m.s>w.ordinate)) ) ) {
						if((w.action==Wall.EVENT)
							&& w.fireMoleculeEvent(m)){	//special?
							nx=m.x;ny=m.y;
/*						}else if((m.getType()==Molecule.WATER)&&(rand.nextDouble()<Membrane.semipermeable)){	//water permeable
							ny+=(m.vy/Math.abs(m.vy))*(Membrane.thickness+10);
*/						}else hity=true;	// bounces
					}
				}
				if(hity){
					if(m instanceof MassiveMolecule){
						MassiveMolecule mol=(MassiveMolecule) m;
						mol.y-=/* 2* */mol.vy;
						mol.vy=-mol.vy;
						ny=(int)mol.y;
					}else{
						m.vy=-m.vy;		//bounce routine
			//			m.y+=m.vy;
						nx=m.y;	//this is so as not to confuse the subsequent y calculation
					}
				}else m.y=ny;


				if((m.getType()==Molecule.SUGAR)&&!m.isBound()){	//is unbound sugar?
					Molecule tm;
					int range=20;
					for(Enumeration m0=molecules.elements();m0.hasMoreElements();){
						if(((tm=(Molecule)m0.nextElement()).getType()==Molecule.WATER)&&	//is a water molecule
							 (!tm.isBound())&&					//free
							 ((tm.x>m.x-range)&&(tm.x<m.y+range)			//nearby?
							&&(tm.y>m.y-range)&&(tm.y<m.y+range)) &&
								(rand.nextInt(2)==0) ){				//then perhaps:
							tm.setMoleculeListener(m);			//sugar listens to water
							m.setLigand(tm);m.setBound(true);
							tm.setLigand(m);tm.setBound(true);
							break;
						}
					}
				}
			}		//end if!bound
			m.paint(g,true);		//redraw molecule in new position
		}
	}
	public Membrane createMembraneAcross(int o, int p,int q){
		Membrane m=new Membrane(true,o,p,q);
		Wall w1,w2;
		hwalls.add(w1=new Wall(o,p,q));
		hwalls.add(w2=new Wall(o+m.thickness,p,q));

		w1.setMoleculeListener(m);
		w2.setMoleculeListener(m);	//top&bottom walls listen for water molecules

		vwalls.add(new Wall(p,o,o+m.thickness));
		vwalls.add(new Wall(q,o,o+m.thickness));
		paintables.add(m);
		return m;
	}
	public MembraneTransporter createTransporter(Class transClass,Membrane mem,int along,int w){
		MembraneTransporter t;
		Wall w1,w2,w3,w4;
			//construct transporter object...
		try{
			Class iclass=Class.forName("java.lang.Integer");
			Class[] pTypes={mem.getClass(),iclass,iclass};
			Object[] params=new Object[]{mem,new Integer(along),new Integer(w)};
			molecules.add(t=(MembraneTransporter)transClass.getConstructors()[0].newInstance(params));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		hwalls.add(w1=new Wall(t.y-t.s-1,t.x-t.s,t.x+t.s));			//add walls to pane
		hwalls.add(w2=new Wall(t.y+t.s+1,t.x-t.s,t.x+t.s));
		vwalls.add(w3=new Wall(t.x-t.s,t.y-t.s,t.y+t.s));
		vwalls.add(w4=new Wall(t.x+t.s,t.y-t.s,t.y+t.s));
		w1.setMoleculeListener(t);				//add listeners to walls
		w2.setMoleculeListener(t);
		w3.setMoleculeListener(t);
		w4.setMoleculeListener(t);
		t.walls.add(w1);					//add walls to transporter
		t.walls.add(w2);
		t.walls.add(w3);
		t.walls.add(w4);
		return t;
	};

	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			((PaintableComponent)e.nextElement()).paint(g,true);
		}
		for(Enumeration e=paintables.elements();e.hasMoreElements();){
			((PaintableComponent)e.nextElement()).paint(g,true);
		}
	}
	public int getMoleculesAbove(int lev,int type){
		int count=0;
		Molecule m;
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			if(((m=(Molecule)e.nextElement()).getType()==type)
				&& m.y<lev)count++;
		}
		return count;
	}
	public Molecule getAMolecule(Random r,int type){
		int tries=0;
		Molecule m;
		do {
			m=(Molecule)molecules.get(r.nextInt(molecules.size()) );
		} while ((m.getType()!=type)&&(++tries<10));
		if(tries<10)return m;else return null;
	}
	public void setMoleculeSizes(int type,int size){
		Molecule m;
		Graphics2D g=(Graphics2D)getGraphics();
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			if((m=(Molecule)e.nextElement()).getType()==type){
				m.paint(g,false);
				m.s=size;	// don't redraw until next ActionPerformed
				timer.restart();
				if(!m.isBound()) eliminateIntersections(m);//else System.out.println("bound molecule not resized");
			}
		}
	}
	private void eliminateIntersections(Molecule m){
		Wall w;
		if(m.ligand!=null)System.out.println("Oops, moving bound mol");
		for(Enumeration vw=vwalls.elements();vw.hasMoreElements();){
			w=(Wall)vw.nextElement();
			if(   ((m.y<=w.p2) &&(m.y+m.s>=w.p1)) // y between ends
					&&((m.x<=w.ordinate)&&(m.x+m.s>=w.ordinate)) ) m.x-=m.s;
		}
		for(Enumeration hw=hwalls.elements();hw.hasMoreElements();){
			w=(Wall)hw.nextElement();
			if(   ((m.x<=w.p2) &&(m.x+m.s>=w.p1)) // y between ends
					&&((m.y<=w.ordinate)&&(m.y+m.s>=w.ordinate)) ) m.y-=m.s;
		}

	}
	public void removeMolecules(int type){
		Molecule m;
		Vector toRemove=new Vector();
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			if((m=(Molecule)e.nextElement()).getType()==type){		//found one!
				if(m.isBound())m.unbind();		//remove from all listeners & ligands
				toRemove.add(m);
				if(m instanceof MembraneTransporter){	//remove transporter special walls
					hwalls.removeAll(((MembraneTransporter)m).walls);
					vwalls.removeAll(((MembraneTransporter)m).walls);
				}
			}
		}
		molecules.removeAll(toRemove);
	}

	public void finalize() throws Throwable{
		timer.stop();
		super.finalize();
	}
}
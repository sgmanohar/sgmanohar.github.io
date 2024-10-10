
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
import java.util.Random;
import java.util.*;

public class Cell extends MassiveMolecule {

	public Image im;
	KineticPane parent;
	public SpecialWall swall;
	public Cell(int nx,int ny,KineticPane k){
		super(nx,ny);
		parent=k;
		setType(CELL);
		s=70;
		k.swalls.add(swall=new CircularBindableWall(this,70));
		Mass=40;
	}
  public Cell(Random rand,Rectangle rect,KineticPane k) {
	super(rand,rect);
	parent=k;
	setType(CELL);
	s=70;
	k.swalls.add(swall=new CircularBindableWall(this,70));
	Mass=400;
  }
	public void paint(Graphics2D g,boolean u){
		if(u){
			if(im!=null){
				g.drawImage(im,(int)x,(int)y,parent);
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(4));
		//		g.drawLine((int)(x+35+75*Math.cos(theta)/2),(int)(y+35+75*Math.sin(theta)/2),
		//			(int)(x+35+(75+h)*Math.cos(theta)/2) ,(int)(y+35+(75+h)*Math.sin(theta)/2) );
				//theta+=Math.PI/10;
			}
		}else{
			g.setColor(bg);
			g.fillOval((int)x-2,(int)y-2,55,55);
			g.setStroke(new BasicStroke(4));
		//		g.drawLine((int)(x+35+75*Math.cos(theta)/2),(int)(y+35+75*Math.sin(theta)/2),
		//			(int)(x+35+(75+h)*Math.cos(theta)/2) ,(int)(y+35+(75+h)*Math.sin(theta)/2) );

		}
	}
	Vector theta=new Vector();
	Vector abs=new Vector();
	public boolean moleculeEvent(Molecule m){
		if(m instanceof MassiveMolecule && this instanceof MassiveMolecule){
			MassiveMolecule mol=(MassiveMolecule)m;
			mol.x=x-mol.s;
			mol.y=y;
			if(mol instanceof Antibody){
				double a;
				if(abs.indexOf(mol)<0){		//new addition?
					abs.add(mol);
					a=Math.PI*2*rand.nextDouble();
					theta.add(new Double(a));
				}else{				//already added
					a=((Double)theta.get(abs.indexOf(mol))).doubleValue();
System.out.println(a);
				}
				((Antibody)mol).theta=a;
				mol.x=x+s/2*Math.cos(a);
				mol.y=y+s/2*Math.sin(a);
			}
		}else{
			m.x=(int)(x-m.s);
			m.y=(int)(y);
		}
		if(rand.nextInt(200)==0){	//wants to detatch
			m.setMoleculeListener(null);	//I wont to water listen anymore
			m.setBound(false);m.setLigand(null);
			bound=false;ligand=null;
			m.x=(int)x;m.y=(int)y;	//ensure it is not stranded somewhere
			theta.remove(abs.indexOf(m));
			abs.remove(m);
		}
		return true;	// allow me to move the molecule
	}
}
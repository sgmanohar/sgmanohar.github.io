
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class MoleculePane extends JPanel {
	public MoleculePane() {
	}

	protected Vector molecules=new Vector();
	protected Vector walls=new Vector();
	protected Random rand=new Random();

//Add Molecule
	public void addMolecule(String cls){
		addMolecule(cls,new Rectangle(0,0,getWidth(),getHeight()));
	}
	public void addMolecule(String cls,Rectangle rect){		// general proceedure to add an item;
		AbstractMolecule m;	//is an AbstractMolecule

				//find an empty space
		int tries=0;
		boolean collide=false;
		AbstractWall test;
		Point2D pos;
		Point size=new Point(10,10);	// where can I get this from ???
		do{
			pos=new Point2D.Double(rect.x+rect.width*rand.nextDouble() , rect.y+rect.height*rand.nextDouble());
			for(Enumeration e=walls.elements();e.hasMoreElements();){
				test=(AbstractWall)e.nextElement();
				if(test.intersectTest(pos,size))collide=true;
			}
		}while(collide && tries++<20);

				// set velocity
		double angle=rand.nextDouble()*Math.PI*2,speed=rand.nextDouble()*5;
		Point2D vel=new Point2D.Double(speed*Math.cos(angle),speed*Math.sin(angle));

		try{		//invoke constructor
			Class p2d=Class.forName("java.awt.geom.Point2D");
			Class[] paramcls=new Class[]{p2d,p2d,this.getClass()};
			m=(AbstractMolecule)Class.forName("com.cudos.common.molecules."+cls).getConstructor(paramcls).newInstance(new Object[]
				{pos,vel,this});
			molecules.add(m);
		}catch(Exception e){e.printStackTrace();}
	}

	public void nextFrame(){
		AbstractMolecule m;		//local temporary
		AbstractWall w;
		Graphics2D g=(Graphics2D)getGraphics();
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			m=(AbstractMolecule)e.nextElement();
			Point2D np;			//this is the new position

			if(m.attachedTo!=null){		//if attached to something else, get someone else to move it.
				np=m.attachedTo.requestLigandMove(m);
			}else{
				np=m.requestMove();
				for(Enumeration f=walls.elements();f.hasMoreElements();){
					w=(AbstractWall)f.nextElement();
					np=w.collisionFunction(np,m);		// test collision against each wall
				}
			}
				//redraw molecule in new location
			m.paint(g,false);
			m.p=np;
			m.paint(g,true);
		}
	}
}
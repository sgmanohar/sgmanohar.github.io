
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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class MoleculeMover extends AbstractMover {
	public MoleculeMover() {
	}


	Collision getCollision(AbstractMoveable mov, Point2D np){		//check new pos for collisions
		AbstractMolecule m=(AbstractMolecule)mov;
			//check with container walls
		if(!getBounds().contains(np)){
			double x=np.getX();
			if(x<getBounds().getX())return new Collision(m,new Point2D.Double(1,0));
			if(x>getBounds().getX()+getBounds().getWidth())return new Collision(m,new Point2D.Double(-1,0));
			double y=np.getY();
			if(y<getBounds().getY())return new Collision(m,new Point2D.Double(0,1));
			if(y>getBounds().getY()+getBounds().getHeight())return new Collision(m,new Point2D.Double(0,-1));
		}
			//check with other molecules
		Point2D op=m.getPos();m.setPos(np);	//try new pos
		for(Enumeration e=molecules.elements();e.hasMoreElements();){
			AbstractMolecule mm=(AbstractMolecule)e.nextElement();
			if(m!=mm && m.intersects(mm)){
				m.setPos(op);
				return new Collision(m,mm);
			}
		}
		return null;
	}
  void paintBackgroundElements(Graphics g){
	}
/*
	public boolean intersects(AbstractMolecule a, AbstractMolecule b){	//dispatch collision tests
		if(a instanceof CircularMolecule && b instanceof CircularMolecule)
			return a.intersectsCircle((CircularMolecule)b);
		else if(a instanceof CircularMolecule && b instanceof RectangularMolecule)
			return a.intersectsRectangle((RectangularMolecule)b);
		else if(a instanceof RectangularMolecule && b instanceof CircularMolecule)
			return b.intersects((RectangularMolecule)a);
		else if(a instanceof RectangularMolecule && b instanceof RectangularMolecule)
			return a.intersects((RectangularMolecule)b);
	}
*/
//Add Molecule
/*	public void addMolecule(String cls){
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
*/
}
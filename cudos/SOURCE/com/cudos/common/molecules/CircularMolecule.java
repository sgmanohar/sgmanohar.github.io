
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import java.awt.geom.*;
import java.awt.*;

public abstract class CircularMolecule extends AbstractMolecule{
		//this known how to collide with circles and polygons

		//data
			//circle centred at pos, radius here:
	protected double radius;

		//private methods
	double getRadius(){return radius;}

	Shape getShape(){return new Ellipse2D.Double(		//hopefully redundant!
		-radius,-radius,2*radius,2*radius
	);}
	Shape getShapeAbsolute(){
		return new Ellipse2D.Double(
			getPos().getX()-radius,getPos().getY()-radius,2*radius,2*radius
	);}
	boolean inCircle(double x,double y){return getPos().distance(x,y)<getRadius();}


		//public methods

	public boolean intersects(AbstractMolecule a){
		if(a instanceof RectangularMolecule){
			Rectangle2D r=((RectangularMolecule)a).getRectangleAbsolute();
			if( inCircle(r.getX(),r.getY()) )return true;		//check each corner is in circle
			if( inCircle(r.getX(),r.getY()+r.getHeight()) )return true;
			if( inCircle(r.getX()+r.getWidth(),r.getY()) )return true;
			if( inCircle(r.getX()+r.getWidth(),r.getY()+r.getHeight()) )return true;
			return false;		//no collision
		}else if(a instanceof CircularMolecule){
			return ( getPos().distance(a.getPos())<(getRadius()+((CircularMolecule)a).getRadius()) );
		}else return a.intersects(this);	//throw it to the other party!
	}
        public void paint(Graphics2D g, boolean b){
          g.fillOval((int)(getPos().getX()-radius), (int)(getPos().getY()-radius), (int)(radius*2), (int)(radius*2) );
        }

}

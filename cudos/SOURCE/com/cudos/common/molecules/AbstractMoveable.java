
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

		////Maintains an affine transform to match a position and orientation
		// base class for active sites and molecules
public abstract class AbstractMoveable {
	protected Point2D rPos=new Point2D.Double(0,0);
	protected double orientation=0;
	protected AffineTransform transform;

	void updateTransform(){			//when position or orientation changes
		transform=new AffineTransform(
			Math.cos(getOrientation()),
			-Math.sin(getOrientation()),
			Math.sin(getOrientation()),
			Math.cos(getOrientation()),	//rotation [[cos,sin][-sin,cos]]
			getPos().getX(),getPos().getY()		//xlation
		);
	}
	public AffineTransform getTransform(){return transform;}

		//getters and setters
	public Point2D getPos(){return rPos;}
	public synchronized void setPos(Point2D p){rPos=p;updateTransform();}

	public double getOrientation(){return orientation;}
	public synchronized void setOrientation(double o){orientation=o;updateTransform();}

	public AbstractMoveable() {
		updateTransform();
	}
	abstract public void paint(Graphics g);
	abstract public void move(AbstractMover m);
}
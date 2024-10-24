
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

public interface AbstractWall {

	public boolean intersectTest(Point2D p,Point s);				//is p 'within' wall? (pos,size)
	public boolean collisionTest(Point2D op,Point2D np,AbstractMolecule m2);	//has p crossed wall?
	public Point2D collisionFunction(Point2D np,AbstractMolecule m2);
}
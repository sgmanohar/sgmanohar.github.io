
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

public interface SpecialWall {
	public boolean collisionTest(Molecule m,int newx,int newy);
	public boolean collisionTest(MassiveMolecule m,double newx,double newy);

}
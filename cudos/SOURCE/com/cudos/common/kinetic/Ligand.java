
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

public interface Ligand {
	public Molecule getLigand();
	public void setLigand(Molecule l);
//	public void bindTo(Ligand l);
	public boolean isBound();
	public void setBound(boolean b);
//	public Point getActiveSite();
}
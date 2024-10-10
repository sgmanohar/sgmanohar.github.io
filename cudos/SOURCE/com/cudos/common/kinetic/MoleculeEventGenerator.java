
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

public interface MoleculeEventGenerator {
	public void setMoleculeListener(MoleculeListener l);
	public MoleculeListener getMoleculeListener();
//	public void fireMoleculeEvent(Molecule m);
}
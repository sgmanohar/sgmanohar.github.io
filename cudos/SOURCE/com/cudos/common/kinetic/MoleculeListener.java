
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

public interface MoleculeListener {
 public boolean moleculeEvent(Molecule m);	//on entry, the molecule is undrawn and ready to be moved...
						//must return true to accept a transport request.
}
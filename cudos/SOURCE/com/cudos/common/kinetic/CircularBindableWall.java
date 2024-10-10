
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

public class CircularBindableWall extends CircularWall {
	public boolean collisionTest(Molecule m,int nx,int ny){
		if(super.collisionTest(m,nx,ny)){
			if(m instanceof Antibody && M instanceof Cell && !M.bound && !m.bound){
				m.bound=true;m.ligand=M;
				m.setMoleculeListener(M);
				M.bound=true;
				return true;
			}
		}else return false;
		return true;
	}
	public boolean collisionTest(MassiveMolecule m,double nx,double ny){
		if(super.collisionTest(m,nx,ny)){
			if(m instanceof Antibody && M instanceof Cell && !M.bound && !m.bound){
				m.bound=true;m.ligand=M;
				m.setMoleculeListener(M);
				M.bound=true;M.ligand=m;
				return false;
			}
		}else return false;
		return true;
	}
	double r;
  public CircularBindableWall(MassiveMolecule mol,double radius) {
	super(mol,radius);
  }
}
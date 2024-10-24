
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

import java.util.Random;
import java.awt.*;

public class MassiveMolecule extends Molecule {
	public double x,y,vx,vy;
	public double Mass=2;
	public void move(){
		x+=vx;y+=vy;
		super.x=(int)x;super.y=(int)y;
	}
  public MassiveMolecule(int nx,int ny) {
	super(nx,ny);
	x=nx;y=ny;
  }
	public MassiveMolecule(Random r,Rectangle b){
		x=r.nextInt((int)b.getWidth())+(int)b.getX();
		y=r.nextInt((int)b.getHeight())+(int)b.getY();
		vx=r.nextInt(30/s)-(15/s);
		vy=r.nextInt(30/s)-(15/s);
		if(vx==0)vx++;if(vy==0)vy++;	// molecules going straight up/down look silly
	}

}
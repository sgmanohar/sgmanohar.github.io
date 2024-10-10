
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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Antibody extends MassiveMolecule {

  public Antibody(int nx,int ny) {
	super(nx,ny);
	init();
  }
	public Antibody(Random r,Rectangle t){
		super(r,t);
		init();
	}
	GeneralPath path=new GeneralPath();
	public void init(){
		setType(Molecule.PROTEIN);
		s=6;
		int ix=0,iy=0;
		path.moveTo(ix,iy);		path.lineTo(ix+s/2,iy+s/2);
		path.moveTo(ix+s,iy);		path.lineTo(ix+s/2,iy+s/2);
		path.moveTo(ix+s/2,iy+s);	path.lineTo(ix+s/2,iy+s/2);
		Mass=5;
	}
	AffineTransform myrotate;
	public double theta=0;
	public void paint(Graphics2D g,boolean u){
		if(u){
			g.setColor(Color.blue);
			myrotate=AffineTransform.getTranslateInstance(x,y);
			if(!bound)theta+=Math.PI/12;
//			else(theta=-Math.PI/4);
			myrotate.concatenate(AffineTransform.getRotateInstance(theta/*,x+s/2,y+s/2*/));
			g.draw(myrotate.createTransformedShape(path));
		}else{
			g.setStroke(new BasicStroke(3));
			g.setColor(bg);
			myrotate=AffineTransform.getTranslateInstance(x,y);
			myrotate.concatenate(AffineTransform.getRotateInstance(theta/*,x+s/2,y+s/2*/));
			g.draw(myrotate.createTransformedShape(path));
		}
	}
}
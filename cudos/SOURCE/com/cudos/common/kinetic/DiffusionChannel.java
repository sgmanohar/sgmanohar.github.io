
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

public class DiffusionChannel extends MembraneTransporter{

  public DiffusionChannel(Membrane mem,int along,int w) {
	super(mem,along,w);
	activeType=Molecule.SUGAR;
  }
	public void paint(Graphics2D g,boolean u){
		g.setColor(bg);
		g.fillRect(x+s/3,y-s,s/3,2*s);
		g.setColor(Color.blue);
		g.fillOval(x,y-s,s/3,2*s);
		g.fillOval(x+2*s/3,y-s,s/3,2*s);
	}
	public boolean initTransport(Molecule m){
		if(m.getType()==activeType){
			m.x=x+s/2-m.s/2;
			m.y=y-transportDirectionY*s/2;
			frame=numframes;
			return true;
		}else return false;
	}
	int frame=0,numframes=5;
	public boolean continueTransport(Molecule m){
		m.y+=transportDirectionY*membrane.thickness/numframes;
		if(frame-->0){
			m.vy=transportDirectionY*Math.abs(m.vy);
			m.y+=transportDirectionY*5;
			return true;
		}
		else return false;
	}

}
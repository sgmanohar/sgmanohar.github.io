
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

public class SimplePump extends MembraneTransporter {
	public SimplePump(Membrane mem,int along,int w){
		super(mem,along,w);
		activeType=Molecule.SUGAR;
	}
	int animframe=0,frames=10;

	Color molprevbg;
	public boolean initTransport(Molecule m){
		if((m.getType()==activeType)&&(transportDirectionY>0)){	//only downwards!
			animframe=frames;
			m.x=x;m.y=y-s;
			molprevbg=m.bg;m.bg=cols[getType()];
			return true;
		}else return false;
	}
	public boolean continueTransport(Molecule m){
		int dx=(int)((s-m.s/2)*Math.sin(animframe*Math.PI/frames));
		int dy=(int)((s-m.s/2)*Math.cos(animframe*Math.PI/frames));
		m.x=x-m.s/2+dx;m.y=y-m.s/2+dy;
		if (animframe--<=0){
			m.bg=molprevbg;
			return false;
		}
		else return true;
	}

	public void paint(Graphics2D g,boolean u){
		super.paint(g,u);
		if(animframe>0){
			int dx=(int)(s*Math.sin(animframe*Math.PI/frames));
			int dy=(int)(s*Math.cos(animframe*Math.PI/frames));
			g.setColor(Color.black);
			g.drawLine(x,y,x+dx,y+dy);
		}
	}
}

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
import com.cudos.common.PaintableComponent;
import java.util.Random;

public class Membrane implements PaintableComponent,MoleculeListener{
	public Wall pos;
	public boolean horz=true;
	public static int thickness=20;
	public final static int linew=3;
	public double[] permeability=new double[]{0.9,0,0.1,0.1,0,0};
					//  water, sugar, na, k, mprot, prot
	public Color bg=new Color(192,224,224);
	public void paint(Graphics2D g,boolean u){
		Color yel=u?Color.yellow:bg;
		Color bla=u?Color.black:bg;
		if(horz){
			g.setColor(yel);
			g.fillRect(pos.p1,pos.ordinate,pos.p2-pos.p1,thickness);
			g.setColor(bla);
			g.setStroke(new BasicStroke(linew*2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
			g.drawLine(pos.p1,pos.ordinate+thickness-linew,pos.p2,pos.ordinate+thickness-linew);
			g.drawLine(pos.p1,pos.ordinate+linew,pos.p2,pos.ordinate+3);
		}else{
			g.setColor(bla);
			g.drawLine(pos.ordinate,pos.p1,pos.ordinate,pos.p2);
		}
	}
  public Membrane() {
	this(true,10,0,10);
  }
	public Membrane(boolean a,int o,int p,int q){
		pos=new Wall(o,p,q);
		horz=a;
	}

		// molecule listener interface elements: membrane listens for collisions, decides whether
		// water can pass etc.
	Random rand=new Random();
	public boolean moleculeEvent(Molecule m){
		if(rand.nextDouble()<permeability[m.getType()]){
			if(horz)m.y+=(m.vy/Math.abs(m.vy))*(thickness+14);	//move it across
			else m.x+=(m.vx/Math.abs(m.vx))*(thickness+4);
			return true;
		}else return false;
	}
}
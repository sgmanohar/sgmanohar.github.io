
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

import com.cudos.common.PaintableComponent;
import java.awt.*;

public class Wall implements MoleculeEventGenerator,PaintableComponent{
	public static final int BOUNCE=0;
	public static final int EVENT=1;
	public static final boolean HORIZONTAL=true;
	public static final boolean VERTICAL=false;

	public int p1,p2,ordinate;
	public boolean horz;
	public int action=BOUNCE;
	public MoleculeListener listener=null;
  public Wall() {
  }
	public void setMoleculeListener(MoleculeListener l){
		action=EVENT;
		listener=l;
	}
	public MoleculeListener getMoleculeListener(){return listener;}
	public boolean fireMoleculeEvent(Molecule m){
		return listener.moleculeEvent(m);
	}
  public Wall(int o,int p,int q){
	p1=p;p2=q;ordinate=o;
  }
	public void setOrientation(boolean h){horz=h;}
	public boolean getOrientation(){return horz;}
	public void paint(Graphics2D g,boolean u){
		g.setColor(Color.black);
		if(horz)g.drawLine(p1,ordinate,p2,ordinate);
		else g.drawLine(ordinate,p1,ordinate,p2);
	}
}
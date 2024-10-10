
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import com.cudos.common.molecules.instance.Phospholipid;

public class FieldMover extends AbstractMover {
	public void paintBackgroundElements(Graphics g){}
	Phospholipid dragging=null;
	public FieldMover() {
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				for(int i=0;i<mols().size();i++){
					Phospholipid m=(Phospholipid)mols().get(i);
					if(m.getPos().distance( e.getPoint() )<15){
						synchronized(m){
							dragging=m;
							m.heldstill=true;
							m.setPos(e.getPoint());
						}
						break;
					}
				}
			}
			public void mouseReleased(MouseEvent e){
				if(dragging!=null){
					synchronized(dragging){
						dragging.heldstill=false;
						dragging.vx=0;dragging.vy=0;
						dragging=null;
					}
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(dragging!=null)dragging.setPos(e.getPoint());
			}
		});
		timer.start();
	}
	Collision getCollision(AbstractMoveable parm1, Point2D parm2) {return null;}
}
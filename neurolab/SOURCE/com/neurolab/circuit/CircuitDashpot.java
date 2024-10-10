
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab.circuit;

import java.awt.*;

public class CircuitDashpot extends CircuitComponent {

	public CircuitDashpot(CircuitPanel p,int q,int r) {super(p,q,r);}
	double width=20,height=60;
	public void paint(Graphics g_){
		paintName(g_);
			//cylinder

		try{
			Graphics g=(Graphics)g_;
			Point bl=new Point(p1.x-(int)(width*dirx),p1.y-(int)(width*diry));
			Point br=new Point(p1.x+(int)(width*dirx),p1.y+(int)(width*diry));
			g.drawLine(bl.x,bl.y,br.x,br.y);
			g.drawLine(bl.x,bl.y, bl.x+(int)(height*diry),bl.y-(int)(height*dirx)) ;
			g.drawLine(br.x,br.y, br.x+(int)(height*diry),br.y-(int)(height*dirx) );
			//plunger
			g.drawLine(p2.x-(int)(width*dirx), p2.y-(int)(width*diry),
								 p2.x+(int)(width*dirx), p2.y-(int)(width*diry) );
			Point p3=new Point(p2.x-(int)(height*diry),p2.y+(int)(height*dirx) );
			g.drawLine(p2.x,p2.y,p3.x,p3.y);
			g.fillRect(p3.x-(int)((width-3)*dirx),p3.y+(int)((width-3)*diry),(int)(width-3)*2,10);
			//,Math.PI+getLineAngle()));
		}catch(Exception e){
			//? Alternative drawing routine?
		}
	}
}

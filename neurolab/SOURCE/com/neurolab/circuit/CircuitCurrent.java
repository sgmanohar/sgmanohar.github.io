package com.neurolab.circuit;

import java.awt.*;

public class CircuitCurrent extends CircuitComponent{
	public CircuitCurrent(CircuitPanel p,int n1,int n2){super(p,n1,n2);}
	double ratio=0.3;
	public void paint(Graphics g_){
		paintName(g_);
		Graphics g = (Graphics)g_;

		Point c1=pointBetween(p1,p2,ratio);
		Point c2=pointBetween(p1,p2,ratio);
                double rad = magn * ratio;
                Rectangle r1 = new Rectangle( (int) (p1.x - rad * diry -rad),
                                             (int) (p1.y - rad * dirx -rad),
                                             (int) (2 * rad), (int) (2 * rad));
                //,getLineAngle()).getBounds2D();
                Rectangle r2 = new Rectangle( (int) (p2.x + rad * diry -rad),
                                             (int) (p2.y + rad * dirx -rad),
                                             (int) (2 * rad), (int) (2 * rad));
		//,getLineAngle()+Math.PI).getBounds2D();
		g.drawOval(r1.x,r1.y,r1.width,r1.height );
		g.drawOval(r2.x,r2.y,r2.width,r2.height );
	}
}

package com.neurolab.circuit;

import java.awt.*;

public class CircuitCapacitor extends CircuitComponent{
	public CircuitCapacitor(CircuitPanel p,int n1,int n2){super(p,n1,n2);}
	double gap=0.25;
	double wide=22;
	double platethickness=0.6;
	public void paint(Graphics g_){
		paintName(g_);
		Graphics g = (Graphics)g_;

		Point tmp1=pointBetween(p1,p2,0.5-gap);
		Point tmp2=pointBetween(p1,p2,0.5+gap);
		g.drawLine(p1.x,p1.y, tmp1.x, tmp1.y);
		g.drawLine(tmp2.x, tmp2.y, p2.x,p2.y);

		int blockw=(int)(2*wide);
		int blockh=(int)(platethickness*magn*gap);
		g.fillRect(tmp1.x+(int)(wide*dirx),tmp1.y-(int)(wide*diry),
								blockw,blockh);
								//,getLineAngle() ));
		g.fillRect(tmp2.x+(int)(wide*dirx-diry*blockh) ,tmp2.y+(int)(wide*diry+dirx*blockh),
								blockw,blockh);
								//,getLineAngle() ));
	}
}

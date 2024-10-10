package com.neurolab.circuit;

import java.awt.*;

public class CircuitCell extends CircuitComponent{
	public CircuitCell(CircuitPanel p,int n1,int n2){super(p,n1,n2);}
	double gap=0.12;
	int nwide=20;	// negative terminal
	int pwide=25;	// positive terminal
	double platethickness=0.82;
	public void paint(Graphics g_){
		Graphics g = (Graphics)g_;

		Point tmp1=pointBetween(p1,p2,0.5-gap);
		Point tmp2=pointBetween(p1,p2,0.5+gap);
		g.drawLine(p1.x,p1.y, tmp1.x,tmp1.y);
		g.drawLine(tmp2.x, tmp2.y, p2.x, p2.y);
		g.fillRect(tmp1.x-(int)(nwide*dirx),tmp1.y-(int)(nwide*diry),
						2*nwide,(int)(platethickness*magn*gap));
						//,getLineAngle() ));
		g.drawLine(tmp2.x-(int)(pwide*dirx),tmp2.y-(int)(pwide*diry),
					 tmp2.x+(int)(pwide*dirx),tmp2.y+(int)(pwide*diry) );
		paintName(g);
	}
}

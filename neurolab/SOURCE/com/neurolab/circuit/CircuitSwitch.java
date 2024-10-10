package com.neurolab.circuit;

import java.awt.*;

public class CircuitSwitch extends CircuitComponent{
	public boolean closed;
	public double jump=10;
	public CircuitSwitch(CircuitPanel p, int n1, int n2){super(p,n1,n2);}
	public void paint(Graphics g_){
		paintName(g_);
		Graphics g = (Graphics)g_;
		g.fillOval(p1.x-4,p1.y-4,8,8);
		g.fillOval(p2.x-4,p2.y-4,8,8);
		Point switchlineP1, switchlineP2;
		if(closed){
			switchlineP1=new Point(p1.x,p1.y);
			switchlineP2=new Point(p2.x,p2.y);
		}else{
			switchlineP1=new Point(p1.x-(int)(jump*dirx), p1.y-(int)(jump*diry));
			switchlineP2=new Point(p2.x-(int)(jump*dirx), p2.y-(int)(jump*diry));
		}
		g.drawLine(switchlineP1.x,switchlineP1.y,switchlineP2.x,switchlineP2.y);
		Point rectl = pointBetween(switchlineP1,switchlineP2,1d/3d);
		Point rectr = pointBetween(switchlineP1,switchlineP2,2d/3d);
		g.fillRect(rectl.x-(int)dirx,rectl.y-(int)diry, (int)(magn/3),7);
		//,getLineAngle()-Math.PI/2 ));
	}
	public void toggle(){closed=closed==false;getParent().repaint();}
}


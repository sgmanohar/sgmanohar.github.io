package com.neurolab.circuit;

import java.awt.*;

public class CircuitResistor extends CircuitComponent{
	public CircuitResistor(CircuitPanel p,int n1,int n2){super(p,n1,n2);}
	int turns=4,width=14;
	public void paint(Graphics g_){
		paintName(g_);
		Graphics g = (Graphics)g_;
		Point prev=p1,curr,tmp;
		for(int i=0;i<turns;i++){
			tmp=pointBetween(p1,p2,(i*2d+1d)/(2*turns+1));
			curr=new Point((int)(tmp.x+width*dirx),(int)(tmp.y+width*diry));
			g.drawLine(prev.x,prev.y,curr.x, curr.y);
			prev=curr;
			tmp=pointBetween(p1,p2,(i*2d+2)/(2*turns+1));
			curr=new Point((int)(tmp.x-width*dirx),(int)(tmp.y-width*diry));
			g.drawLine(prev.x,prev.y,curr.x,curr.y);
			prev=curr;
		}
		g.drawLine (prev.x,prev.y,p2.x,p2.y );
	}
}

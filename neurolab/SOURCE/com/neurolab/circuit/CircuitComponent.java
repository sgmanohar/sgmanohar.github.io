package com.neurolab.circuit;

import java.awt.*;
import com.neurolab.common.LinearComponent;

public abstract class CircuitComponent extends LinearComponent{
	private int node1,node2;
	private CircuitPanel parent;
	public String name="";
	public CircuitPanel getParent(){return parent;}
	public CircuitComponent(CircuitPanel p,int n1,int n2){
		super(p.getNode(n1),p.getNode(n2));
		parent=p;
		node1=n1;node2=n2;
	}

	public void paint(Graphics g){
		g.drawLine((int)p1.x, (int)p1.y, (int)p2.x ,(int)p2.y );
	}
	public void paintName(Graphics g){
		g.setFont(getParent().font);
		g.setColor(Color.black);
		double dist=name.length()*14.;
		g.drawString(name,(int)(p1.x-dist*Math.abs(dirx)),(int)(p1.y-dist*Math.abs(diry)) );
	}
}



package com.neurolab.circuit;

import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import com.neurolab.common.*;

public class CircuitPanel extends JPanel{
	public Vector nodes;
	public Vector componentlist;
	public Point getNode(int i){return (Point)nodes.elementAt(i);}
	int stroke;
	Font font;
	public CircuitPanel(){
//		setBG(this);
		nodes=new Vector();
		componentlist=new Vector();
		stroke=4;
		font=new Font("Arial",Font.BOLD,16);
	}
	public void moveNode(int i,Point	p){
		Point old=(Point)nodes.elementAt(i);
		nodes.setElementAt(p,i);
		CircuitComponent c;	//check components and move them too
		for(Enumeration e=componentlist.elements();e.hasMoreElements();){
			c=(CircuitComponent)e.nextElement();
			if(c.getP1()==old)c.setPos(p,c.getP2());
			else if(c.getP2()==old)c.setPos(c.getP1(),p);
		}
	}
	public void paint(Graphics g){
		super.paint(g);
		NeurolabExhibit.antiAlias(g);
		NeurolabExhibit.setStrokeThickness(g,stroke);
		for(Enumeration e=componentlist.elements();e.hasMoreElements();){
			((PaintableComponent)e.nextElement()).paint(g);
		}
	}
}


/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab.common;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

public class AngleControl extends JPanel {

	int value=0;		//properties
	boolean directional;
	double f=0.3;
	String prefix="Angle=";
				//getters and setters
	public int getValue(){return value;}
	public void setValue(int v){
		if(maximum-minimum<359){
			if(v>maximum)v=maximum;
			else if(v<minimum)v=minimum;
		}else{
			while(v<minimum)v+=360;
			while(v>maximum)v-=360;
		}
		value=v;
		//redrawArrow(getGraphics());
		repaint();
	}
		//getters, setters
	public void setDirectional(boolean d){directional=d;}
	public boolean getDirectional(){return directional;}
	public void setArrowSize(double a){f=a;};
	public double getArrowSize(){return f;};
	public void setPrefix(String s){prefix=s;}
	public String getPrefix(){return prefix;}
	protected int zero=0;
	public int getZero(){return zero;}
	public void setZero(int z){zero=z;}
	protected int maximum=359;
	public int getMaximum(){return maximum;}
	public void setMaximum(int m){maximum=m;}
	protected int minimum=0;
	public int getMinimum(){return minimum;}
	public void setMinimum(int m){minimum=m;}

	int oldx=0;
	public AngleControl() {
          this.setBackground(NeurolabExhibit.systemGray);
		MouseInputAdapter m=new MouseInputAdapter(){
			public void mousePressed(MouseEvent e){
				oldx=e.getX();
			}
			public void mouseDragged(MouseEvent e){
				if(oldx!=e.getX()){
					setValue(value+e.getX()-oldx);
					oldx=e.getX();
				}
			}
		};
		addMouseMotionListener(m);
		addMouseListener(m);
	}
	public void paint(Graphics g){
		super.paint(g);
		NeurolabExhibit.antiAlias(g);
		int w=getWidth()-4;
		g.setColor(SystemColor.controlLtHighlight);	//outer ring
		g.drawOval(0,0,w,w);
		g.setColor(SystemColor.controlDkShadow);
		g.drawOval(2,2,w,w);
		g.setColor(Color.lightGray);//SystemColor.control); ods20
		g.fillOval(1,1,w,w);
		w-=10;						//inner ring
		g.setColor(SystemColor.controlLtHighlight);
		g.fillOval(4,4,w,w);
		g.setColor(SystemColor.controlShadow);
		g.fillOval(8,8,w,w);
		redrawArrow(g);
	}
	public void redrawArrow(Graphics g){
		NeurolabExhibit.antiAlias(g);
		int w=getWidth()-14;
		g.setColor(Color.lightGray);//.control); ods20
		g.fillOval(6,6,w,w);
		g.setColor(SystemColor.controlShadow);
		g.drawOval(6,6,w,w);
		int in=7;w-=1;	// arrow inside circle
		int dx=(int)(w/2*Math.sin((zero+value)*Math.PI/180));
		int dy=(int)(-w/2*Math.cos((zero+value)*Math.PI/180));
		Polygon arrow=new Polygon();
		arrow.addPoint(in+w/2+dx,in+w/2+dy);
		arrow.addPoint(in+w/2+dx-(int)(f*(dx+dy)),in+w/2+dy-(int)(f*(dy-dx)) );
		arrow.addPoint(in+w/2+dx-(int)(f*(dx-dy)),in+w/2+dy-(int)(f*(dy+dx)) );
		NeurolabExhibit.setStrokeThickness(g,3);
		g.setColor(SystemColor.controlLtHighlight);
		g.drawLine(in+w/2+dx,in+w/2+dy,in+w/2-dx,in+w/2-dy);
		if(directional)g.fillPolygon(arrow);
		g.setColor(SystemColor.controlShadow);
		g.drawLine(in+w/2+dx-1,in+w/2+dy-1,in+w/2-dx-1,in+w/2-dy-1);
		arrow.translate(-1,-1);
		if(directional)g.fillPolygon(arrow);
		g.setColor(getBackground());
		g.fillRect(0,getWidth()+1,getWidth(),getHeight()-getWidth());
		g.setFont(getFont());			// text
		g.setColor(getForeground());
		String text=prefix+String.valueOf(value);
		g.drawString(text,0,getHeight()-10);
	}
}

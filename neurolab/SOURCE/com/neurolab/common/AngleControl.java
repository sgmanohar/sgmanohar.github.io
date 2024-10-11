
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

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

	int oldx=0, oldy=0;
	public boolean mouseHorizontalMode = false; // use rotational mode 
	public AngleControl() {
          this.setBackground(NeurolabExhibit.systemGray);
         
		MouseInputAdapter m=new MouseInputAdapter(){
		  
			public void mousePressed(MouseEvent e){
				oldx=e.getX(); oldy=e.getY();
			}
			public void mouseDragged(MouseEvent e){
				if(oldx!=e.getX() || oldy!=e.getY()){
				  if(mouseHorizontalMode) {
				    setValue(value+e.getX()-oldx);
				  }else { // calc rotation
				    int cx=getWidth()/2, cy=getHeight()/2,
				        x1=oldx-cx, y1=oldy-cy, x2=e.getX()-cx, y2=e.getY()-cy; // coords rel to centre
				    // cos theta = ( r1 dot r2 ) / ( |r1| |r2| )
				    double dt = Math.acos( (x1*x2+y1*y2)/Math.sqrt(x1*x1+y1*y1)/Math.sqrt(x2*x2+y2*y2) ) ;
				    int sign = (x2-x1)*y1-(y2-y1)*x1; 
				    if(sign!=0) sign=-sign/Math.abs(sign);
				    setValue(value+sign*(int)(180*dt/Math.PI));
				  }
					oldx=e.getX();oldy=e.getY();
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

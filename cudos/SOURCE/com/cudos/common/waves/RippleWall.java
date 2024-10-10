
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.waves;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
	//general wall class...

import com.cudos.common.*;

public class RippleWall extends DraggableComponent{
	static int sid=0;
	int id;
	Rectangle rect;
  public RippleWall() {
	this(0,0,10,20);
  }
	RippleWall me=this;
	public RippleWall(int x1,int y1,int w,int h){
		rect=new Rectangle(x1,y1,w,h);
		setLocation(x1,y1);
		setSize(w,h);
		type=TYPE_RECTANGLE;
		id=sid++;
		name="Wall"+sid;
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				((RippleTank)getParent()).setSelected(me);
			}
		});
	}
	public boolean wallcontains(int x,int y){
		if(type==TYPE_RECTANGLE || type==TYPE_RECTSTEP){
			return contains(x-getX(),y-getY());
		}else if(type==TYPE_PARABOLA){
			int h=getHeight();
			int sx=((RippleTank)getParent()).sx;
			return (x-getX()<(sx+4*getWidth()*(y-h/2)*(y-h/2)/(h*h)))&&(x>getX());
		}else if(type==TYPE_LINE){
			int sx=((RippleTank)getParent()).sx,sy=((RippleTank)getParent()).sy;
			return contains(x-getX(),y-getY())
				&&(((x-getX())/sx==(y-getY())*getWidth()/getHeight()/sy) ||
				   ((y-getY())/sy==(x-getX())*getHeight()/getWidth()/sx) );

		}
		else return false;
	}
	public void paint(Graphics g_){
		Graphics2D g=(Graphics2D)g_;
		g.setColor(new Color(0,0,128));
		if(type==TYPE_RECTANGLE){
			g.fillRect(0,0,getWidth(),getHeight());
		}else if(type==TYPE_PARABOLA){
			int h=getHeight();
			int yst=2;
			int sx=((RippleTank)getParent()).sx;
			for(int i=0;i<h;i+=yst){
				g.fillRect(0,i,sx+4*getWidth()*(i-h/2)*(i-h/2)/(h*h),yst);
			}
		}else if(type==TYPE_LINE){
			g.setStroke(new BasicStroke(3));
			g.drawLine(0,0,getWidth(),getHeight());
		}else if(type==TYPE_RECTSTEP){
			g.setPaint(new Color(0,0,128,32));
			g.fillRect(0,0,getWidth(),getHeight());
		}
		if(selected){
			g.setColor(Color.yellow);
			g.drawRect(0,0,getWidth()-1,getHeight()-1);
		}
	}

	public void setLocation(int x,int y){
		if(type==TYPE_PARABOLA){super.setLocation(x,0);}
		else super.setLocation(x,y);
		if(getParent()!=null)((RippleTank)getParent()).setupWalls();
	}
	public void setSize(int w,int h){
		if(type==TYPE_PARABOLA){
			RippleTank p=(RippleTank)getParent();
			super.setSize(w,p.dheight*p.sy);
		}else super.setSize(w,h);
		if(getParent()!=null)((RippleTank)getParent()).setupWalls();
	}

	public static final int TYPE_RECTANGLE=0,TYPE_PARABOLA=1,TYPE_LINE=2,TYPE_RECTSTEP=3;
	int type;
	public void setType(int t){
		type=t;
	}
	public int getType(){return type;}
}
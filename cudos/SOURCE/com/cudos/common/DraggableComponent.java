
/**
 * Title:        CUDOS Project<p>
 * Description:  Project led by
 * Roger Carpenter,
 * Department of Physiology,
 * University of Cambridge<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
//import java.awt.dnd.*;

public class DraggableComponent extends JComponent {
	protected boolean selected;
	public void setSelected(boolean s){
		selected=s;
		repaint();
	}
	public boolean isSelected(){return selected;}
	protected String name;
	public String getName(){return name;}
	public void setName(String name){this.name=name;}


	int sx,sy;
	public DraggableComponent(){
   		addMouseListener(new MouseAdapter(){
       			public void mousePressed(MouseEvent e){
            			sx=e.getX();
				sy=e.getY();
            		}
       		});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				dragTo(new Point(getX()+e.getX()-sx,getY()+e.getY()-sy));
			}
		});
   	}

	public void dragTo(Point p){
		if(getParent().contains(p) && getParent().contains(p.x+getSize().width,p.y+getSize().height))
			setLocation(p);
		else{	//constrain!
			Container c=getParent();
			if(p.x<0)p.x=0;
			if(p.x>c.getWidth())p.x=c.getWidth();
			if(p.y<0)p.y=0;
			if(p.y>c.getHeight())p.y=c.getHeight();
			setLocation(p);
		}
	}
}
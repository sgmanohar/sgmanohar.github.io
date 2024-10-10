
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer;

public abstract class AbstractMover extends JPanel {
	Vector molecules=new Vector();
	Random random=new Random();
	public Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){tick();}});
	public void paint(Graphics g_){
			//simply paint parent and then each molecule
		super.paint(g_);
		paintBackgroundElements(g_);
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		synchronized(molecules){
			for(Enumeration e=molecules.elements();e.hasMoreElements();){
				AbstractMoveable m=(AbstractMoveable)e.nextElement();
				m.paint(g);
			}
		}
	}
	public void tick(){
			//simply tick each molecule then repaint
		synchronized(molecules){
			for(Enumeration e=molecules.elements();e.hasMoreElements();){
				AbstractMoveable m=(AbstractMoveable)e.nextElement();
				m.move(this);
			}
		}
		repaint();
	}

	public Vector mols(){synchronized(molecules){return molecules;}}

	abstract Collision getCollision(AbstractMoveable mol, Point2D newpos);		//check new pos for collisions
	abstract void paintBackgroundElements(Graphics g);
}
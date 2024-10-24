
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
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class PercentageBar extends JComponent {
	public static final boolean HORIZONTAL=true,VERTICAL=false;

	public double p;
	public boolean horz=true;
	public Color color;
	public PercentageBar(){
		setBorder(BorderFactory.createLoweredBevelBorder());
		color=Color.blue;
		setPreferredSize(new Dimension(100,30));
//		setBG(this);
		p=0;
	}
	public void setOrientation(boolean b){horz=b;}
	public boolean getOrientation(){return horz;}
	public void setValue(int a){p=a;repaint();}
	public void setValue(double a){p=a;repaint();}
	public int getValue(){return (int)p;}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(color);
		int mw=(getWidth()-4);
		if(p>0)
			if(horz)g.fillRect(2,2,Math.min((int)(p*mw/100),mw),getHeight()-4);
			else	g.fillRect(2,getHeight()-2-(int)(p*(getHeight()-4)/100),getWidth()-4,(int)(p*(getHeight()-4)/100));
	}
}
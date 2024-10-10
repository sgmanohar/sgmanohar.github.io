package com.neurolab.common;

import javax.swing.JPanel;
import java.awt.*;
//Spacer.class by Robin Marlow
public class Spacer extends JPanel {
	int height, width;
	public Spacer(int w,int h){
		width = w;
		height = h;
	}
        public Spacer(){this(10,10);}
	public Dimension getPreferredSize(){
		return new Dimension(width,height);
	}
	public void paintComponent( Graphics g ){
		g.setColor(getParent().getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
	}
}

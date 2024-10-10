package com.neurolab.fluid;

import java.awt.*;
import java.util.*;
import javax.swing.JPanel;
import com.neurolab.common.PaintableComponent;

public class FluidPanel extends JPanel{
	public Vector componentlist;
	public FluidPanel(){
//		setBG(this);
		componentlist=new Vector();
	}
	public Dimension getPreferredSize(){
		return new Dimension(180,150);
	}
	public void paint(Graphics g_){
		super.paint(g_);
		for(Enumeration e=componentlist.elements();e.hasMoreElements();)
			((PaintableComponent)e.nextElement()).paint(g_);
	}
}

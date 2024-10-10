package com.neurolab.fluid;

import com.neurolab.common.LinearComponent;
import java.awt.*;

public abstract class FluidComponent extends LinearComponent{
	final int inset=2;
	FluidPanel parent;
	int stroke = 2*inset;
	Color watercolor=Color.blue,linecolor=Color.black;
	public FluidComponent(FluidPanel p,Point a1,Point a2){
		super(a1,a2);
		parent=p;
	}
	public FluidPanel getParent(){return parent;}
	public abstract void paint(Graphics g);
}

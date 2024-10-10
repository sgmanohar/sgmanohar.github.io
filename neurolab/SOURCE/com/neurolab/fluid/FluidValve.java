package com.neurolab.fluid;

import java.awt.*;
import com.neurolab.common.*;

public class FluidValve extends FluidPipe{
	public boolean closed;
	public int xsize=8;
	public FluidValve(FluidPanel p,Point p1,Point p2){super(p,p1,p2);}
	public void paint(Graphics g){
		super.paint(g);
		Point centre=pointBetween(p1,p2,0.5);
		g.setColor(Color.red);
		NeurolabExhibit.setStrokeThickness(g,stroke);
		if(closed)
			for(int i=-1;i<2;i+=2){
				g.drawLine((int)(centre.x+i*xsize),(int)(centre.y-xsize),
							 (int)(centre.x-i*xsize),(int)(centre.y+xsize));
				g.drawLine((int)(centre.x-i*xsize),(int)(centre.y-xsize),
							 (int)(centre.x+i*xsize),(int)(centre.y+xsize));
			}
	}
	public void toggle(){closed=(closed==false);getParent().repaint();}
}

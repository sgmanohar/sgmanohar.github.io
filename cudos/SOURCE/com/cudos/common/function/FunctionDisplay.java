package com.cudos.common.function;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class FunctionDisplay extends JPanel{
	Function function;
	int strokeThickness=3;

		//display area
	public double xmin,xmax,ymin,ymax;


	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getForeground());
		g.setStroke(new BasicStroke(strokeThickness));
		int ox=0,oy=fgY(gfX(0));
		for(int i=0;i<getWidth();i++){
			g.drawLine( ox,oy, i,oy=fgY( function.getY(gfX(i)) ) );
		}
	}


		//conversions between graph and function coordinate frames

	int fgX(double x) {return (int)((x-xmin)*getWidth()/(xmax-xmin));}
	int fgY(double y) {return (int)((y-ymin)*getHeight()/(ymax-ymin));}
	double gfX(int x) {return x*(xmax-xmin)/getWidth()+xmin;}
	double gfY(int y) {return y*(ymax-ymin)/getHeight()+ymin;}



		//public getters and setters

	public void setFunction(Function f){function=f;}
	public Function getFunction(){return function;}

	public void setStrokeThickness(int t){strokeThickness=t;}
	public int getStrokeThickness(){return strokeThickness;}

	public void setDisplayArea(Rectangle2D r){
		xmin=r.getX();xmax=r.getX()+r.getWidth();
		ymin=r.getY();ymax=r.getY()+r.getHeight();
	}
	public Rectangle2D getDisplayArea(){
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}


	public FunctionDisplay(){
		setBackground(Color.black);
		setForeground(Color.green);
	}
}

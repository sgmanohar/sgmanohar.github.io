
/**
 * Draws graph paper with a grid and internal graph-coordinate system
 */
package com.cudos.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GraphPaper extends JPanel {
	public GraphPaper(){
		setFont(new Font("Dialog",Font.PLAIN,10));
		setForeground(Color.green);
		setBackground(Color.black);
		minx=miny=0;
		maxx=maxy=100;
		majorx=majory=50;
		minorx=minory=10;
	}
	protected double minx,maxx,majorx,minorx;
	protected double miny,maxy,majory,minory;
	public void paint(Graphics g){
		super.paint(g);
		double dx=maxx-minx,dy=maxy-miny;
		//minor ticks
		g.setColor(getMidColour());

		drawg(g,((int)(minx/minorx))*minorx ,1+(int)(dx/minorx),minorx,false,false);
		drawg(g,((int)(miny/minory))*minory ,1+(int)(dy/minory),minory,true,false);
		//major ticks
		g.setColor(getForeground());
		drawg(g,((int)(minx/majorx))*majorx ,2+(int)(dx/majorx),majorx,false,drawLabels);
		drawg(g,((int)(miny/majory))*majory ,2+(int)(dy/majory),majory,true,drawLabels);
	}
	public Color getMidColour(){
		Color f=getForeground(), b=getBackground();
		return new Color(	(f.getRed()+b.getRed())/2,
					(f.getGreen()+b.getGreen())/2,
					(f.getBlue()+b.getBlue())/2  );
	}

		//setup the graphic range

	public void setXRange(double minimum, double maximum){
		minx=minimum;maxx=maximum;
		double b=tenths(maxx-minx);
		int nx=(int)((maxx-minx)/b);
/*		if(getWidth()/nx<50){
			majorx=b;
			minorx=b/2;
		}else{
			majorx=b;
			minorx=b/5;
		}
*/  majorx=b;minorx=b/5;
		//make minx an exact multiple of majorx (rounding down always)
		minx=majorx*(int)(minx/majorx);
System.out.println("x["+minx+","+maxx+"]d"+majorx);
	}
	public void setYRange(double minimum,double maximum){
		miny=minimum;maxy=maximum;
		double b=tenths(maxy-miny);
		int ny=(int)((maxx-minx)/b);
/*		if(getHeight()/ny<50){
			majory=b;
			minory=b/2;
		}else{
			majory=b/2;
			minory=b/5;
		}
*/  majory=b;minory=b/5;
		//miny must be a multiple of majory
		miny=majory*(int)(miny/majory);
System.out.println("y["+miny+","+maxy+"]d"+majory);
	}

	/** Works out majorx, majory, minorx, minory based on the current min and max */
	public void recalculateTicks(){
		double b=tenths(maxy-miny);
		int ny=(int)((maxx-minx)/b);
		if(ny==0)ny=1;
		/*
		if(getHeight()/ny<50){
			majory=b;
			minory=b/2;
		}else{
			majory=b/2;
			minory=b/10;
		}
		*/
		majory=b;minory=b/5;
		//miny must be a multiple of majory

		b=tenths(maxx-minx);
		int nx=(int)((maxx-minx)/b);
		if(nx==0)nx=1;
		/*
		if(getWidth()/nx<50){
			majorx=b;
			minorx=b/2;
		}else{
			majorx=b/2;
			minorx=b/10;
		}
		*/
		majorx=b;minorx=b/5;
		//make minx an exact multiple of majorx (rounding down always)
	}


		//delegate graphical functions
	public void drawLine(Graphics g,double x1, double y1, double x2, double y2){
		g.drawLine(xS(x1),yS(y1), xS(x2),yS(y2));
	}
	public Point toScreen(Point2D p){
		return new Point(xS(p.getX()),yS(p.getY()));
	}
	public Point2D toGraph(Point p){
		return new Point2D.Double(xG(p.x),yG(p.y));
	}


	//property drawLabels
	boolean drawLabels=false;
	public void setDrawLabels(boolean d){drawLabels=d;repaint();}
	public boolean getDrawLabels(){return drawLabels;}
public double getMajorX(){return majorx;}
public void setMajorX(double d){majorx=d;repaint();}
public double getMajorY(){return majory;}
public void setMajorY(double d){majory=d;repaint();}

	protected Color labelColour = null;



		//internal functions

		/**
		 * return largest power of ten that is below a.
		 * was round(log a)-1, now floor(log a) (27/1/03)
		 *
		 * e.g. tenths(0.75) = 0.1
		 */
	double tenths(double a){
		int r=(int)Math.floor(Math.log(a)/Math.log(10));
		return Math.pow(10,r);
	}
	void drawg(Graphics g,double low, int n, double dist, boolean horz,boolean labels){
		if(horz)
		for(int i=0;i<n;i++){
			double yg=low+dist*i;
			int y=yS(yg);
			g.drawLine(0,y,getWidth(),y);
			if(labels)g.drawString(tostring(yg,majory), 0,y);
		}
		else
		for(int i=0;i<n;i++){
			double xg=low+dist*i;
			int x=xS(xg);
			g.drawLine(x,0,x,getHeight());
			if(labels)g.drawString(tostring(xg,majorx), x,getHeight()-10);
		}
	}

	//convert double to string for labelling axes
	String tostring(double a,double major){
		a=((int)(100*a))/100.;
		if(major>10)return String.valueOf((int)a);
		else return String.valueOf(a);
	}


	//coordinate transform from graph to screen
	public int xS(double xG){return (int)( getWidth()*(xG-minx)/(maxx-minx) );}
	public int yS(double yG){return (int)( getHeight()*(1-(yG-miny)/(maxy-miny)) );}
	//coordinate transform from screen to graph
	public double xG(double xS){return xS/getWidth()*(maxx-minx)+minx;}
	public double yG(double yS){return (1-yS/getHeight())*(maxy-miny)+miny;}
	public Color getLabelColour() {
		return labelColour;
	}
	public void setLabelColour(Color labelColour) {
		this.labelColour = labelColour;
	}
}
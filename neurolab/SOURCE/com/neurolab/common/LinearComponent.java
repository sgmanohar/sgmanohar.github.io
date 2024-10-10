package com.neurolab.common;

import java.awt.*;
import com.neurolab.common.PaintableComponent;

public abstract class LinearComponent extends Object implements PaintableComponent{
	public Point p1,p2;
	public double dirx,diry,magn;
	public LinearComponent(Point a1,Point a2){
		super();
		p1=a1;p2=a2;
		calculatePos();
	}
	public void calculatePos(){
		dirx=p1.y-p2.y;
		diry=p2.x-p1.x;	// perpendicular
		magn=Math.sqrt(dirx*dirx+diry*diry);
		dirx/=magn;diry/=magn;
	}
	public void setPos(Point a,Point b){p1=a;p2=b;calculatePos();}
	public Point getP1(){return p1;}
	public Point getP2(){return p2;}

	public Point perpendicular(Point p,double dist){
		return new Point((int)(p.x+dist*dirx),(int)(p.y+dist*diry));
	}
        public Point perpendicularLess(Point p, double dist){
          return new Point((int)(p.x-Math.abs(dist*dirx)),(int)(p.y-Math.abs(dist*diry)));
        }
	public double getLineAngle(){
		double p,q,t;
		p=p2.y-p1.y;
		q=p2.x-p1.x;
		t=Math.atan( p/q );
		if(q<0)t+=Math.PI;
		return t;
	}
	/*
	public GeneralPath rect(double left,double top,double height,double width,double angle){
		GeneralPath r=new GeneralPath(new Rectangle(left,top,Math.abs(width),Math.abs(height) ));
		r.transform(AffineTransform.getRotateInstance(angle,left,top));
		return r;
	}
	*/
	public Point pointBetween(Point a,Point b,double frac){
		return new Point((int)(a.x+(b.x-a.x)*frac),
						(int)(a.y+(b.y-a.y)*frac) );
	}
}



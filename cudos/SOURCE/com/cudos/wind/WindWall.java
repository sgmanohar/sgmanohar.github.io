
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.wind;

import com.cudos.common.DraggableComponent;
import java.awt.*;
import java.awt.geom.*;

public class WindWall extends DraggableComponent {
	Shape shape;
	public Shape drawnshape;
	public WindWall() {
		this(new Ellipse2D.Double(10,10,10,10));
	}
	boolean canDragWholeComponent=true;
	public WindWall(Shape s){
		setShape(s);
/*		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if(handleRectangle(rotateHandle).contains(e.getPoint())){
					rotating=true;
					lastMouse=e.getPoint();
					canDragWholeComponent=false;
				}
				for(PathIterator i=drawnshape.getPathIterator(null); !i.isDone(); i.next()){
					double[] c=new double[2];
					i.currentSegment(c);
					if(handleRectangle(c).contains(e.getPoint())){
						System.out.println("Handle clicked!");
					}
				}
			}
			public void mouseReleased(MouseEvent e){
				rotating=false;
				shapeDragging=false;
				canDragWholeComponent=true;
			}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(rotating){
					int dx=e.getX()-lastMouse.x;
					lastMouse=e.getPoint();
					if(dx!=0){
						Point mid=getMidpoint(shape);
						shape=AffineTransform.getRotateInstance(dx/30., mid.x, mid.y)
							.createTransformedShape(shape);
						Point oo=getLocation();
						setShape(shape);
						//reset last mouse coords relative to new origin
						lastMouse.x-=getLocation().x-oo.x;
						lastMouse.y-=getLocation().y-oo.y;

						((WindTunnel)getParent()).setupWalls();
						getParent().repaint();
					}
				}
			}
		});
*/	}
	Point lastMouse;
	double[] rotateHandle=new double[]{0,0};
	boolean rotating=false, shapeDragging=false;

	Rectangle handleRectangle(double[] p){
		return new Rectangle((int)p[0]-rhandle, (int)p[1]-rhandle, rhandle*2, rhandle*2);
	}
	int rhandle=3;
	AffineTransform ident=AffineTransform.getScaleInstance(1,1);


	Point getMidpoint(Shape s){
		return new Point(s.getBounds().x+s.getBounds().width/2,
			s.getBounds().y+s.getBounds().height/2);
	}

	public void setShape(Shape s){
		shape=s;
		setLocation(shape.getBounds().getLocation());
		Dimension sz=shape.getBounds().getSize();
		setSize(sz.width+1,sz.height+1);
		AffineTransform at=AffineTransform.getTranslateInstance(-getLocation().x,-getLocation().y);
		drawnshape=at.createTransformedShape(shape);
	}
	public Shape getShape(){return shape;}

	public void paint(Graphics g_){
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);
		g.fill(drawnshape);
		g.setColor(Color.black);
		g.draw(drawnshape);
/*		for(PathIterator i=drawnshape.getPathIterator(ident); !i.isDone(); i.next()){
			double coord[]=new double[2];
			i.currentSegment(coord);
			g.fill(handleRectangle(coord));
		}
*/	}
	Point pold = null;
	public void setRotating(boolean b){ rotating = b; pold = null; }
	public boolean isRotating(){return rotating;}
	public void dragTo(Point p){
		try{
		if(rotating){
			if(pold == null) pold = p;
			int dx = pold.x - p.x; if(dx>5)dx=5;if(dx<-5)dx=-5;
			Point c = getMidpoint(shape);
			AffineTransform tra = AffineTransform.getTranslateInstance(c.x,c.y),
											rot = AffineTransform.getRotateInstance(dx*0.05),
											ttr = (AffineTransform)tra.clone();
			ttr.concatenate(rot); ttr.concatenate(tra.createInverse());
			setShape( ttr.createTransformedShape(shape) );
			((WindTunnel)getParent()).setupWalls();
			pold=p;
			return;
		}
		if(canDragWholeComponent){
			super.dragTo(p);
			shape=AffineTransform.getTranslateInstance(p.x-shape.getBounds().x,p.y-shape.getBounds().y).createTransformedShape(shape);
			((WindTunnel)getParent()).setupWalls();
		}
		}catch(NoninvertibleTransformException e){e.printStackTrace();}
	}
}
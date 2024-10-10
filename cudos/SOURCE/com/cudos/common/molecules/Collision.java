
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import java.awt.geom.*;

public class Collision {	//// data structure for collisions
	AbstractMolecule m1,m2;
	boolean binding;

	public Collision(AbstractMolecule a1,AbstractMolecule a2) {
		m1=a1;m2=a2;
		binding=false;
		for(int i=0;i<m1.activeSite.length;i++){
			for(int j=0;j<m2.activeSite.length;j++){
				if(m1.activeSite[i].intersects(m2.activeSite[j])){// do active sites intersect?
					tryBinding(m1.activeSite[i],m2.activeSite[j]);
					if(binding)break;
				}
			}
			if(binding)break;
		}
		if(binding){
			//do conservation of momentum thing
			if(m1.boundTo==m2)	bindingMomentum(m2,m1);
			else			bindingMomentum(m1,m2);
		}else{
			//delegate bounce collision stuff
			bounce(a1,a2);
		}
	}
	void tryBinding(ActiveSite a1,ActiveSite a2){
		if(a1.canBind(m2) && a2.canBind(m1)){
			a1.bind(a2);
			a2.bind(a1);
			binding=true;
		}
	}

		//direct wall collisions
	public Collision(AbstractMolecule a, Point2D normal){
		m1=a;m2=null;
		binding=false;
		bounceUnmoveable(a,normal);
	}



		//utilities
	Point2D direction(Point2D p1, Point2D p2){return new Point2D.Double(
		p2.getX()-p1.getX(),p2.getY()-p1.getY());
	}
	Point2D getNormalToRectangle(Rectangle2D r,Point2D p,double d){
		if(p.getX()+d>r.getX() && p.getX()-d<r.getX()+r.getWidth()){	//x within rect?
			if(p.getY()>r.getY()+r.getWidth())return new Point2D.Double(0,1);	//down
			if(p.getY()<r.getY())return new Point2D.Double(0,-1);	//up
		}else if(p.getY()+d>r.getY() && p.getY()-d<r.getY()+r.getHeight()){	//y within rect?
			if(p.getX()>r.getX()+r.getWidth())return new Point2D.Double(1,0);	//right
			if(p.getX()<r.getX())return new Point2D.Double(-1,0);	//left
		} //otherwise
		return direction(getRectangleCentre(r),p);
	}
	Point2D getRectangleCentre(Rectangle2D r){return new Point2D.Double(
		r.getX()+r.getWidth()/2, r.getY()+r.getHeight()/2
	);}





	//collision bounces

		// distributor
	void bounce(AbstractMolecule a1,AbstractMolecule a2){
		if(a1.isMoveable() && a2.isMoveable()){
			if(a1 instanceof CircularMolecule){
				CircularMolecule c1=(CircularMolecule)a1;
				if(a2 instanceof CircularMolecule)
					bounceCircleCircle(c1,(CircularMolecule)a2);
				else if(a2 instanceof RectangularMolecule)
					bounceCircleRectangle(c1,(RectangularMolecule)a2);
			}else if(a1 instanceof RectangularMolecule){
				RectangularMolecule r1=(RectangularMolecule)a1;
				if(a2 instanceof CircularMolecule)
					bounceCircleRectangle((CircularMolecule)a2,r1);
				else if(a2 instanceof RectangularMolecule)
					bounceRectangleRectangle( r1,(RectangularMolecule)a2 );
			}
		}else{
			if(!a1.isMoveable()){AbstractMolecule t=a1;a1=a2;a2=t;}	//make a1 moveable
			if(a1 instanceof CircularMolecule){
				CircularMolecule c1=(CircularMolecule)a1;
				if(a2 instanceof CircularMolecule){
					bounceCircle(c1,direction(a1.getPos(),a2.getPos()) );
					//radius=normal to impact
				}else if(a2 instanceof RectangularMolecule){
					bounceCircle(c1,getNormalToRectangle(((RectangularMolecule)a1).getRectangleAbsolute(),
						c1.getPos(),c1.getRadius()) );
				}
			}//only circles can bounce of fixed shapes so far
		}
	}




		//routines



	void bounceCircle(CircularMolecule m, Point2D normal){
			// bouncing off an immobile surface
		Point2D v1=m.getVel();
		double	v=v1.distance(0,0),
			thn=Math.atan2(normal.getY(),normal.getX()),	//angle of normal
			th1=Math.atan2(v1.getY(),v1.getX()),
				//angle of incidence
			th2=2*thn-th1;	//angle of reflection
		m.setVel(new Point2D.Double(v*Math.cos(th2),v*Math.sin(th2)));
	}

	void bounceCircleCircle(CircularMolecule m1, CircularMolecule m2){
			//bouncing off another molecule

		// naming convention:
		// 1 or 2 => before or after
		// m or M => first or second molecule

			// centre to centre vector
		double	dx=m2.getPos().getX()-m1.getPos().getX(),
			dy=m2.getPos().getY()-m1.getPos().getY();
			//move into position for collision
		Point2D p=sub(m2.getPos(),m1.getPos());
		Point2D v=unit(m1.getVel());
		double rr=m1.getRadius()+m2.getRadius();
		double j=dot(p,perp(v));
		double lambda=-dot(p,v)+Math.sqrt(rr*rr-j*j);
		Point2D dvector=mult( v,lambda );
		m1.setPos(sub(m1.getPos(),dvector));
			// velocities
		double	vx=m1.getVel().getX(), vy=m1.getVel().getY(),
			Vx=m2.getVel().getX(), Vy=m2.getVel().getY();

		double al,th1,th2,ph1,ph2, mass=m1.getMass(),Mass=m2.getMass();
		double v1,v2,V1,V2;

			//polar conversions
		al=Math.atan2(dy,dx);			// angle of line of impact
		th1=Math.atan2(vy,vx)-al;		// angle of v1
		ph1=Math.atan2(Vy,Vx)-al;		// angle of V1
		v1=m1.vel.distance(0,0);		// magnitude v1;
		V1=m2.vel.distance(0,0);		// magnitude V1


			// solution of conservation of momentum for elastic impact
		th2=Math.atan2( (v1*Math.sin(th1)*(mass+Mass)),
				((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1)) );
		ph2=Math.atan2( (V1*Math.sin(ph1)*(Mass+mass)),
				(2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1)) );
		V2=Math.sqrt(Math.pow((2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1))/(mass+Mass),2)
				+V1*V1*Math.sin(ph1)*Math.sin(ph1) );
		v2=Math.sqrt(Math.pow(((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1))/(mass+Mass),2)
				+v1*v1*Math.sin(th1)*Math.sin(th1) );


			//set answers
		m1.setVel(new Point2D.Double( v2*Math.cos(th2+al) , v2*Math.sin(th2+al) ));
		m2.setVel(new Point2D.Double( V2*Math.cos(ph2+al) , V2*Math.sin(ph2+al) ));
	}

	void bounceCircleRectangle(CircularMolecule m1, RectangularMolecule m2){
		//not implemented yet
	}
	void bounceRectangleRectangle(RectangularMolecule m1,RectangularMolecule m2){
		//not implemented yet
	}



		//when two molecules combine into one particle

	void bindingMomentum(AbstractMolecule m1, AbstractMolecule m2){
		//m1 receives all the momentum from m2!
		// uses m3*v3 = m1*v1 + m2*v2
		double mass2=m2.getTotalMass(),mass3=m1.getTotalMass(),mass1=mass3-mass2;
			// m1's total mass since it has already bound to m2
		m1.setVel(new Point2D.Double(
			(mass1*m1.getVel().getX()+mass2*m2.getVel().getX())/mass3,
			(mass1*m1.getVel().getY()+mass2*m2.getVel().getY())/mass3  ));
		// don't change m2's velocity?
	}


		//when one particle hits an unmoveable

	void bounceUnmoveable(AbstractMolecule a, Point2D normal){	//unit normal
			//dot product
		double perpm=dot(a.getVel(),normal);
			//components of velocity
		Point2D perp=mult(normal,perpm);
		Point2D para=sub(a.getVel(),perp);

			//we can only bounce out of the wall
		if(perpm>0)perp=mult(perp,-1);

		a.setVel(sub(para,perp));
	}
	public static double dot(Point2D a,Point2D b){return a.getX()*b.getX()+a.getY()*b.getY();}
	public static double modsq(Point2D a){return a.getX()*a.getX()+a.getY()*a.getY();}
	public static Point2D perp(Point2D a){return new Point2D.Double(a.getY(),-a.getX());}
	public static Point2D add(Point2D a,Point2D b){return new Point2D.Double(a.getX()+b.getX(),a.getY()+b.getY());}
	public static Point2D sub(Point2D a,Point2D b){return new Point2D.Double(a.getX()-b.getX(),a.getY()-b.getY());}
	public static Point2D mult(Point2D p, double c){return new Point2D.Double(p.getX()*c,p.getY()*c);}
	public static Point2D unit(Point2D p){
		double m=modsq(p);
		return new Point2D.Double(p.getX()/m,p.getY()/Math.sqrt(m));
	}
	public static double cross(Point2D a, Point2D b){return a.getX()*b.getY()-a.getY()*b.getX();}
}

/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import java.awt.*;
import java.awt.geom.*;

import com.cudos.common.*;

public abstract class AbstractMolecule extends AbstractMoveable {
						////relative to origin

		//variables
	public ActiveSite[] activeSite;
	ActiveSite boundBy=null;	//only if we are following
	AbstractMolecule boundTo=null;
	Point2D vel;
	double angvel=0.1;

	abstract protected boolean isMoveable();
	abstract protected double getMass();
	abstract boolean intersects(AbstractMolecule m);
	abstract protected String getImageName();
		//to be overridden to set image name
		//unless paint() is overridden instead


	public AbstractMolecule(){
			//defaults
		activeSite=new ActiveSite[0];		//no active sites
			//random pos & vel
		vel=new Point2D.Double(10*Math.random(),10*Math.random());
		setPos(new Point2D.Double(Math.random(),Math.random()));
			//appearance
		image=applet.getImage(getImageName());
		iw=image.getWidth(null);
		ih=image.getHeight(null);
	}


		///public methods
	public void move(AbstractMover mover){
		if(!isMoveable())return;	// non moveable
		if(boundTo!=null){		// following another molecule
			Point2D p1=boundBy.boundTo.absolutePosition(),p2=null;
			setOrientation(Math.PI/2+boundBy.boundTo.absoluteOrientation()-boundBy.getOrientation());
			try{
				p2=boundBy.getTransform().inverseTransform(new Point(0,0),null);
				p2=getTransform().createInverse().deltaTransform(p2,null);
			}catch (Exception e){e.printStackTrace();}
			setPos( new Point2D.Double(p1.getX()+p2.getX(),p1.getY()+p2.getY()) );
		}else{				// moving independently
			Point2D nPos=new Point2D.Double(getPos().getX()+vel.getX(),getPos().getY()+vel.getY());
				//calculate possible new coordinates
			double nOrientation=getOrientation()+angvel;
				//calculate possible new orientation
			Collision c;
			if((c=mover.getCollision(this,nPos))!=null){	//collided
					//recalculate and store
				nPos=new Point2D.Double(getPos().getX()+vel.getX(),getPos().getY()+vel.getY());
				nOrientation=getOrientation()+angvel;
				setPos(nPos);
				setOrientation(nOrientation);
			}else{	//go ahead with the movement
					//just store
				setPos(nPos);
				setOrientation(nOrientation);
			}
		}
	}



		//recursive mass variable
	double getBoundMass(){		//recurse downwards through binding tree
		double tot=getMass();
		for(int i=0;i<activeSite.length;i++){
			if(activeSite[i].boundTo!=null){
				AbstractMolecule m=activeSite[i].boundTo.owner;
				if(m.boundTo!=null && m.boundTo==this)tot+=m.getBoundMass();
			}
		}
		return tot;
	}
	public double getTotalMass(){	//calculate mass of whole tree
		if(boundTo==null){
			return getBoundMass();
		}else return boundTo.getTotalMass();
	}

		//recursive velocity variable
	public void setVel(Point2D v){
		if(boundTo==null)vel.setLocation(v);
		else boundTo.setVel(v);
	}
	public Point2D getVel(){
		if(boundTo==null)return vel;
		else return boundTo.getVel();
	}


	public static CudosApplet applet;
	private Image image;
	private int iw,ih;
  public AbstractMolecule attachedTo;
  public Point2D p;
  private Point getDrawingPosition(){	//translate into correct frame
		Point2D p=getPos();
		getTransform().translate(p.getX(),p.getY());
		return new Point((int)p.getX(),(int)p.getY());
	}
	AffineTransform getImageTransform(){	//just the rotation operation
		AffineTransform a=(AffineTransform)getTransform().clone();
		a.translate(-iw/2,-ih/2);
		return a;
	}
	public void paint(Graphics g_){		//render image centred on pos
		Graphics2D g=(Graphics2D)g_;
//		Point p=getDrawingPosition();
		g.drawImage(image,getImageTransform(),null);
	}

  /**
   * requestLigandMove
   *
   * @param m AbstractMolecule
   * @return Point2D
   */
  public Point2D requestLigandMove(AbstractMolecule m) {
    return null;
  }

  /**
   * requestMove
   *
   * @return Point2D
   */
  public Point2D requestMove() {
    return null;
  }

  /**
   * paint
   *
   * @param g Graphics2D
   * @param b boolean
   */
  public abstract void paint(Graphics2D g, boolean b);
  /*	public boolean updateImage(Image i,int f,int x,int y,int w,int h){
		if((f&ImageObserver.ALLBITS)>0)return false; else{
			return true;
		}
	}
*/
}

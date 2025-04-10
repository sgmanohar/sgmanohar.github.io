
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.molecules;

import java.awt.*;
import java.awt.geom.*;

public class ActiveSite extends AbstractMoveable{
				/////relative to owner!!

		//Variables
	AbstractMolecule owner;
	ActiveSite boundTo;
	Class canBind[];
	double radius;	//radius of activation

	public ActiveSite() {
	}


		//initialisation

	public void setup(AbstractMolecule owner,double distance,double angle,double radius,String[] bindable){
		this.owner=owner;
		this.radius=radius;
		setPos(new Point2D.Double(distance*Math.sin(angle),distance*Math.cos(angle)));
		setOrientation(angle);
		canBind=new Class[bindable.length];
		for(int i=0;i<canBind.length;i++)try{
			canBind[i]=Class.forName("com.cudos.common.molecules.instance."+bindable[i]);
		}catch(Exception e){e.printStackTrace();}
	}

		//binding
	public boolean canBind(AbstractMolecule m){
		if(boundTo!=null)return false;
		for(int i=0;i<canBind.length;i++)if(m.getClass()==canBind[i])return true;
		return false;
	}
	public boolean intersects(ActiveSite a){
		return absolutePosition().distance(a.absolutePosition())<radius+a.radius;
	}
	public void bind(ActiveSite a){
		boundTo=a;
		if(owner.getMass()<a.owner.getMass()){	//we are following...
			owner.boundBy=this;
			owner.boundTo=a.owner;
		}
	}


		//position
	public Point2D absolutePosition(){
		return owner.getTransform().transform(
			getTransform().transform(new Point(0,0),null)
		,null);
	}
	public double absoluteOrientation(){
		return owner.getOrientation()+orientation;
	}
	public void move(AbstractMover m){}
	public void paint(Graphics g){}
}
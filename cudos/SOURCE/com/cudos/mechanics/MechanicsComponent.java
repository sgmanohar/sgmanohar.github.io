
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;

public abstract class MechanicsComponent extends BaseComponent{

  public MechanicsComponent() {
  }
	Node top, bottom;
	public int x;
	double tension;
	public void setTop(Node t){
		if(bottom==null)x=t.x; else x=(bottom.x+t.x)/2;
		if(top!=null) top.removeComponent(this);
		top=t;
		top.addComponent(this);
	}
	public void setBottom(Node b){
		if(top==null)x=b.x; else x=(top.x+b.x)/2;
		if(bottom!=null) bottom.removeComponent(this);
		bottom=b;
		bottom.addComponent(this);
	}
	public double getLength(){return bottom.y-top.y;}
	public Node getOtherNode(Node node){
		if(top==node)return bottom; else
		if(bottom==node)return top; else
		return null;
	}
	public abstract double getTension();
	public void pull(){
		double t=getTension();
		top.pull(t);
		bottom.pull(-t);
	}
	public boolean canResizeBy(double changeLength){
		return true;
	}
}
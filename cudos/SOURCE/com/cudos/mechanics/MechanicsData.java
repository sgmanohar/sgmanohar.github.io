
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;
import java.util.*;

public class MechanicsData implements java.io.Serializable{

	public MechanicsData() {
		clearAll();
	}

	public void add(MechanicsComponent c){components.add(c);}
	public void add(Node n){nodes.add(n);}
	public void remove(BaseComponent b){
		if(b instanceof MechanicsComponent)remove((MechanicsComponent)b);
		else if(b instanceof Node)remove((Node)b);
	}
	public void remove(MechanicsComponent c){
		if(c.top.nComponents()==1 && c.top!=root)nodes.remove(c.top);
		if(c.bottom.nComponents()==1 && c.bottom!=root)nodes.remove(c.bottom);
		components.remove(c);
		c.top.removeComponent(c);
		c.bottom.removeComponent(c);
	}
	public void remove(Node n){
		if(n.nComponents()>0)return;
		nodes.remove(n);
	}

	public MechanicsComponent[] getComponents(){
		MechanicsComponent[] ca=new MechanicsComponent[0];
		return (MechanicsComponent[])components.toArray(ca);
	}
	public Node[] getNodes(){
		Node[] na=new Node[0];
		return (Node[])nodes.toArray(na);
	}
	public BaseComponent[] getAllComponents(){
		Vector v=new Vector(components);
		v.addAll(nodes);
		BaseComponent[] b=new BaseComponent[0];
		b=(BaseComponent[])v.toArray(b);
		return b;
	}
	public Node getRoot(){return root;}
	public void joinNodes(Node n1, Node n2){
		if(n2==root){Node t=n1; n1=n2; n2=t;}
		Vector c=n2.getComponents();
		for(int i=0;i<c.size();i++){
			MechanicsComponent co=(MechanicsComponent)c.get(i);
			if(co.top==n2){
				co.setTop(n1);
			}else if(co.bottom==n2){
				co.setBottom(n1);
			}else{throw new Error("Whoops");}
		}
		remove(n2);
	}


	Vector components=new Vector();
	Vector nodes=new Vector();
	Node root;



	public void tick(){
		for(int i=0;i<components.size();i++){
			MechanicsComponent c=(MechanicsComponent)components.get(i);
			c.pull();
		}
		for(int i=0;i<nodes.size();i++){
			Node n=(Node)nodes.get(i);
			n.move();
		}
	}



/*
	Filing operations
*/
	public void clearAll(){
		int ox=100;
		if(root!=null)ox=root.x;
		components=new Vector();
		nodes=new Vector();
		root=new Node();
		add(root);
		root.y=10;
		root.x=ox;
		root.fixed=true;
	}

}
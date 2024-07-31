
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.visual;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import medicine.*;

public class GraphicPane extends JPanel implements Runnable{

  public GraphicPane() {
	/**
	 Add dragging handlers
	*/
	addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			Node n=nodeFromPoint(e.getPoint());
			if(n!=null){
				dragging=n;
				dragx=e.getX();
				dragy=e.getY();
			}
		}
		public void mouseReleased(MouseEvent e){
			dragging=null;
		}
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount()==2){
				Node n=nodeFromPoint(e.getPoint());
				if(n!=null){
					setEntity(n.entity);
				}
			}
		}
	});
	addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(dragging!=null){
				int dx=e.getX()-dragx;
				int dy=e.getY()-dragy;
				dragging.x+=dx;
				dragging.y+=dy;
				dragx=e.getX();
				dragy=e.getY();
			}
		}
	});
	/**
	 Resizing handler
	*/
	addComponentListener(new ComponentAdapter(){
		public void componentResized(ComponentEvent e){
			//initialiseNodes();
			central.x=getWidth()/2;
			central.y=getHeight()/2;
		}
	});

	/**
	 Set colours
	*/
	setBackground(new Color(0,0,128));

	thread.start();

  }

	/**
	 Parameters
	*/


	int dragx,dragy;


	Entity entity;
	public void setEntity(Entity e){
		entity=e;
		initialiseNodes();
	}
	public Entity getEntity(){return entity;}


	public int rangeC=3, rangeH=0;
        boolean forward,backward;
	public void setRange(int c, int h){
		rangeC=c;
		rangeH=h;
		initialiseNodes();
	}



	/**
	 Initialisation
	*/

	Node central, dragging;
	Vector visibleNodes=new Vector();
	void initialiseNodes(){
		visibleNodes=new Vector();
		central=new Node(entity,getWidth()/2,getHeight()/2);
		visibleNodes.add(central);

		if(rangeC>0){
			doBranch(central, Entity.CAUSE, left, rangeC-1);
			doBranch(central, Entity.EFFECT, right, rangeC-1);
		}
		if(rangeH>0){
			doBranch(central, Entity.CHILD, down, rangeH-1);
			doBranch(central, Entity.PARENT, up, rangeH-1);
		}
		repaint();
	}
	void doBranch(Node node, int rel, int[] direction, int count){
		Vector v=node.createBranches(rel, direction);
		if(count>0){
			for(int i=0;i<v.size();i++){
				Node n=(Node)v.get(i);
				doBranch(n, rel, direction, count-1);
			}
		}
		visibleNodes.addAll(v);
	}
	public Node nodeFromPoint(Point p){
		for(int i=0;i<visibleNodes.size();i++){
			Node n=(Node)visibleNodes.get(i);
			if(n.contains(p))return n;
		}
		return null;
	}


	/**
	 Delegate painting
	*/

	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
//		g.setPaint(new GradientPaint(0,0, new Color(0,0,128), 0,getHeight(), Color.black));
//		g.fillRect(0,0,getWidth(), getHeight());
		for(int i=0;i<visibleNodes.size();i++){
			Node n=(Node)visibleNodes.get(i);
			n.paint(g);
		}
	}

  int[] left=new int[]{-1,0}, right=new int[]{1,0}, up=new int[]{0,-1}, down=new int[]{0,1};
  int[] directionForRelation(int relation){
	switch(relation){
		case Entity.CAUSE: return left;
		case Entity.EFFECT: return right;
		case Entity.PARENT: return up;
		case Entity.CHILD: return down;
	}
	return null;
  }


	/**
	 Thread to arrange nodes in lowest energy state
	*/

	Thread thread=new Thread(this, "Arranger");
	boolean running=true;
	public void run(){
		while(running){
			rearrange();
			repaint();
			try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
		}
	}
	double defaultLength=70, rate=0.3, repulsion=100;
	//length=70, rate=0.003, repulsion=100
	void rearrange(){
		//repulsion proportional to number of nodes
		double krepulsion = repulsion / visibleNodes.size();

		for(int i=0;i<visibleNodes.size();i++){
			Node node=(Node)visibleNodes.get(i);
			//do not move the central node, or the node being dragged
			if(dragging==node || node==central)continue;

			double fx=0, fy=0;
			for(int j=0;j<visibleNodes.size();j++){
				Node cnode=(Node)visibleNodes.get(j);
				if(cnode==node)continue;

				double tx=cnode.x-node.x, ty=cnode.y-node.y;
				double d=Math.sqrt(tx*tx+ty*ty);
				if(d==0)continue;
				if(node.rconnections.indexOf(cnode)>=0){
					//directly connected node:
					//strut spring of fixed length
					double forceFrac = rate * (1 - defaultLength / d);
					fx += tx * forceFrac;
					fy += ty * forceFrac;
				}else{
					//repel
					double forceFrac = krepulsion / (d*d + 0.01);
					fx -= tx * forceFrac;
					fy -= ty * forceFrac;
				}
			}
			//apply force
			node.vx+=fx;		node.vy+=fy;
			//damping
			node.vx*=0.8;		node.vy*=0.8;
			//move point
			node.x+=node.vx;	node.y+=node.vy;
			if(node.x<0)node.x=0;	if(node.x>getWidth())node.x=getWidth();
			if(node.y<0)node.y=0;	if(node.y>getHeight())node.y=getHeight();
		}
	}

  /**
   * setDirection
   *
   * @param forward boolean
   * @param backward boolean
   */
  public void setDirection(boolean forward, boolean backward) {
    this.forward=forward;
    this.backward=backward;
    initialiseNodes();
  }
}

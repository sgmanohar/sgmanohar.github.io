
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.visual;
import medicine.*;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class Node {

  public Entity entity;
  String string;
  public double x, y, vx, vy;
  boolean anchored;
  Rectangle rectangle=new Rectangle();
	//connections to other nodes
  public Vector connections=new Vector();
	//all connections to or from other nodes
  public Vector rconnections=new Vector();

  public Node(Entity entity, double x, double y) {
	this.entity=entity;
	this.x=x;
	this.y=y;
	string=entity.toString();
  }

  Color colbox=Color.blue, colline=Color.cyan, coltext=Color.white;

  public void paint(Graphics g_){
	Graphics2D g=(Graphics2D)g_;
	Rectangle2D rect=g.getFont().getStringBounds(string, 0, string.length(), g.getFontRenderContext());
	int w=(int)rect.getWidth(), h=(int)rect.getHeight();
	int tx=(int)(x-w/2), ty=(int)(y-h/2);
	rectangle=new Rectangle(tx, ty, w, h );
	g.setColor(colbox);
	g.fill(rectangle);
	g.setColor(colline);
	g.draw(rectangle);
	g.setColor(coltext);
	g.drawString(string, tx, ty+h-3);
	for(int i=0;i<connections.size();i++){
		Node n=(Node)connections.get(i);
		drawconnectionto(g,n);
	}
  }
  void drawconnectionto(Graphics g, Node n){
    if(rectangle.x+rectangle.width < n.rectangle.x){
      //draw on right edge
      drawArrow(g, rectangle.x+rectangle.width, y, n.rectangle.x, n.y);
    }else
    if(rectangle.x > n.rectangle.x+n.rectangle.width){
      //draw on left edge
      drawArrow(g, rectangle.x, y, n.rectangle.x+n.rectangle.width, n.y);
    }else
    if(y > n.y){
      //draw above
      drawArrow(g, x, rectangle.y, n.x, n.rectangle.y+n.rectangle.height);
    }else
    if(y <= n.y){
      //draw below
      drawArrow(g, x, rectangle.y+rectangle.height, n.x, n.rectangle.y);
    }
  }
  public void drawArrow(Graphics g, double x1, double y1, double x2, double y2){
	g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
/*	g.fillPolygon(new int[]
		{x2, x2-},
		{y2, },
	3);
*/
	g.fillOval((int)x2,(int)y2,6,6);
  }
	public boolean contains(Point p){
		if(rectangle!=null)return rectangle.contains(p);
		return false;
	}

	/**
	 Create a single depth set of branches in the specified direction
	*/

  int distscale=100;
  public Vector createBranches(int relation, int[] direction){
	Vector ents=entity.listOf(relation);
	Vector nodes=new Vector();
	int imax=ents.size();

	for(int i=0;i<imax;i++){
		Entity e=(Entity)ents.get(i);
                boolean alreadyexists=false;
                for(int j=0;j<nodes.size();j++){Node n=(Node)nodes.get(j);if(n.entity.equals(e)){
                    n.rconnections.add(this); alreadyexists=true;
                  }}
                if(!alreadyexists){
                  double nx = direction[0] + ( ( (double) i) / imax) * direction[1];
                  double ny = direction[1] + ( ( (double) i) / imax) * direction[0];
                  Node newnode = new Node(e, x + nx * distscale, y + ny * distscale);
                  newnode.rconnections.add(this);
                  nodes.add(newnode);
                  newnode.setAxis(
                      axisc +
                      ( (relation == Entity.CAUSE) ? -1 : (relation == Entity.EFFECT) ? 1 :
                       0),
                      axish +
                      ( (relation == Entity.PARENT) ? -1 : (relation == Entity.CHILD) ? 1 :
                       0));
                }
	}
	connections.addAll(nodes);
	rconnections.addAll(nodes);

	return nodes;
  }


	int axisc=0, axish=0;
	void setAxis(int c, int h){
		axisc=c; axish=h;
		int p=Math.abs(c)+Math.abs(h);
		int r=42*p;
		int g=(c>0 || h>0)?42*p:0;
		int b=42*(6-p);
		colbox=new Color(r,g,b);
	}

}

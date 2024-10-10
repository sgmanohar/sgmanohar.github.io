
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;

public class FunctionGraphPanel extends JPanel{
	JPanel graph = new JPanel(){
		public void paint(Graphics g_){
			i.xzero=getWidth()/2; i.yzero=getHeight()/2;
			super.paint(g_);
			Graphics2D g=(Graphics2D)g_;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(1));	//axes
			g.setColor(Color.gray);
			g.drawLine(0,i.yzero,getWidth(),i.yzero);
			g.drawLine(i.xzero,0,i.xzero,getHeight());
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.green);
			int last=getVH(0);
			for(int i=0;i<getWidth();i++){
				g.drawLine(i-1,last,i,last=getVH(i) );
			}
			g.setColor(Color.white);
			for(Enumeration e=i.nodes.elements();e.hasMoreElements();){
				Point p=(Point)e.nextElement();
				g.fillOval(p.x-i.handleradius,p.y-i.handleradius,2*i.handleradius,2*i.handleradius);
			}
		}
		public int getVH(int x){	// translate coordinates of f
			double yf=i.function(g2fx(x));
/*			if(Double.NEGATIVE_INFINITY==yf)return -1000;
			if(Double.POSITIVE_INFINITY==yf)return 1000;
*/			if(Double.isNaN(yf))return 0;
			return f2gy(lim(yf,-1000,1000));
		}
	};


		//functions
	void nodesChange(){
		Vector nodes=i.nodes;	//temporary
		if(linear.isSelected()){
			if(nodes.size()>1){
				Point	n1=((Point)nodes.get(0)),
					n2=((Point)nodes.get(1));
				i.a=(g2fy(n2.y)-g2fy(n1.y))/(g2fx(n2.x)-g2fx(n1.x));
				i.a=lim(i.a,-1000,1000);
				i.b=g2fy(n1.y)-i.a*g2fx(n1.x);
			}
		}else if(sigmoid.isSelected()){
			if(nodes.size()>1){
				Point	n1=((Point)nodes.get(0)),
					n2=((Point)nodes.get(1));
				i.d=i.c*Math.atan(1)+g2fy(n1.y);
				i.a=2/(g2fx(n2.x)-g2fx(n1.x));
				i.b=-i.a*g2fx(n1.x)-1;
				i.c=(g2fy(n2.y)-g2fy(n1.y))/(2*Math.atan(1));
			}
		}
	}


		//conversion of coordinates
	double g2fx(int xc){return ((double)(xc-i.xzero))*i.fscale/graph.getWidth();}
	double g2fy(int yc){return ((double)(i.yzero-yc))*i.fscale/graph.getHeight();}
	int f2gx(double xf){return (int)(xf*graph.getWidth()/i.fscale)+i.xzero;}
	int f2gy(double yf){return i.yzero-(int)(yf*graph.getHeight()/i.fscale);}
	public double lim(double a,double min,double max){return a>max?max:(a<min)?min:a;}


  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel2 = new JLabel();
  JRadioButton linear = new JRadioButton();
  JRadioButton sigmoid = new JRadioButton();
  ButtonGroup bg = new ButtonGroup();
  JPanel jPanel1 = new JPanel();
  Border border1;

	transient int nodedrag=-1;

  public FunctionGraphPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	i.nodes.add(new Point(10,10));
	i.nodes.add(new Point(30,30));
	nodesChange();
	graph.repaint();
	graph.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){nodedrag=i.getNodeAt(e.getPoint());}
		public void mouseReleased(MouseEvent e){nodedrag=-1;}
	});
	graph.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(nodedrag>=0  && (graph.getWidth()>e.getX())
					&& (graph.getHeight()>e.getY())
					&& (e.getX()>0) && (e.getY()>0)
			){
				Point p=(Point)i.nodes.get(nodedrag);
				p.setLocation(e.getPoint());
				nodesChange();
				graph.repaint();
			}
		}
	});
        graph.addComponentListener(new ComponentAdapter(){
          public void componentResized(ComponentEvent e){
            nodesChange();
            repaint();
          }
        });
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(0,5,0,5));
    graph.setPreferredSize(new Dimension(100, 100));
    graph.setBackground(Color.black);
    jPanel1.setBorder(border1);
    jPanel1.setPreferredSize(new Dimension(125, 140));
    jPanel1.setLayout(borderLayout1);
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Input");
    jLabel2.setText("Output");
    linear.setText("Linear");
    linear.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    sigmoid.setText("Sigmoid");
    sigmoid.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    linear.setSelected(true);
    this.setPreferredSize(new Dimension(150, 270));
    this.add(jPanel1, null);
    jPanel1.add(graph, BorderLayout.CENTER);
    jPanel1.add(jLabel1, BorderLayout.SOUTH);
    jPanel1.add(jLabel2, BorderLayout.NORTH);
    this.add(linear, null);
    this.add(sigmoid, null);
	bg.add(linear);bg.add(sigmoid);
  }

  void changetype(ActionEvent e) {
	if(linear.isSelected())i.type=GraphInfo.LINEAR;
	else if(sigmoid.isSelected())i.type=GraphInfo.SIGMOID;
	nodesChange();
	graph.repaint();
  }



		//serialisable data


	public void setInfo(Object o){
		if(o instanceof GraphInfo){
			i=(GraphInfo)o;
			switch(i.type){
				case GraphInfo.LINEAR:  linear.setSelected(true);break;
				case GraphInfo.SIGMOID: sigmoid.setSelected(true);break;
			}
		}
		repaint();
	}
	public Object getInfo(){return i;}

	public double function(double a){return i.function(a);}
	GraphInfo i=new GraphInfo();
	class GraphInfo implements Serializable{
		static final int LINEAR=0,SIGMOID=1;
		int type;
		double fscale=4;
		int handleradius=4;
		int xzero,yzero;	// axes in component's space
		double a,b,c,d;
		public Vector nodes=new Vector();
		public int getNodeAt(Point p){
			for(int i=0;i<nodes.size();i++){
				if(p.distance((Point)nodes.get(i))<handleradius)return i;
			}
			return -1;
		}
		public double function(double x){
			if(linear.isSelected()){
				return i.a*x+i.b;	//linear
			}else if(sigmoid.isSelected()){
				return i.c*Math.atan(i.a*x+i.b)+i.d;
			}else return 0;
		}
	}
}

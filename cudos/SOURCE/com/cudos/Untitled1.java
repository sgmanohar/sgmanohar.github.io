
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.Timer;

public class Untitled1 extends CudosExhibit {
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JButton jButton1 = new JButton();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel display = new JPanel(){
		public void paint(Graphics g_){
			super.paint(g_);
			Graphics2D g=(Graphics2D)g_;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			int sx=getWidth()/nx,sy=getHeight()/ny,p,q;
			for(int i=0;i<nx;i++)for(int j=0;j<ny;j++){
				g.drawLine(p=i*sx,q=j*sy,
					p+(int)(length*Math.cos(angle[i][j])) ,
					q+(int)(length*Math.sin(angle[i][j])) );
			}
			for(int i=0;i<charges.size();i++){
				Charge c=(Charge)charges.get(i);
				g.setColor(c.q>0?Color.red:Color.blue);
				g.fillOval(c.x-10,c.y-10,20,20);
			}
		}
	};

	int nx=40,ny=40,length=5;
	double[][] angle=new double[nx][ny];
	Vector charges=new Vector();
	Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			double top,bottom,rcube;
			Charge p;
			Point2D t;
			for(int i=0;i<nx;i++)for(int j=0;j<ny;j++){
					//current point
				t=new Point(i*display.getWidth()/nx,j*display.getHeight()/ny);
				top=bottom=0;
				for(int k=0;k<charges.size();k++){
					p=(Charge)charges.get(k);	//charge location
					rcube=Math.pow(t.distance(p),3);
					top+=p.q*(p.getY()-t.getY())/rcube;
					bottom+=p.q*(p.getX()-t.getX())/rcube;
				}
				angle[i][j]=Math.atan2(top,bottom);	//angle 0..2*PI
			}
		}
	});

	Point2D dragging;
	Border border1;
	JPanel jPanel3 = new JPanel();
	JButton jButton3 = new JButton();
	JButton jButton2 = new JButton();
	JButton jButton4 = new JButton();
	public Untitled1() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	timer.start();
	display.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			for(int i=0;i<charges.size();i++){
				Point2D p=(Point2D)charges.get(i);
				if( p.distance(e.getPoint())<20 ){
					dragging=p;
				}
			}
		}
		public void mouseReleased(MouseEvent e){
			dragging=null;
		}
	});
	display.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(dragging!=null){
				dragging.setLocation(e.getPoint());
				display.repaint();
			}
		}
	});
	}
	class Charge extends Point{
		double q;	//charge
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		this.getContentPane().setLayout(borderLayout1);
		jButton1.setText("Exit");
		jPanel2.setLayout(borderLayout2);
		jPanel1.setBorder(border1);
		jPanel1.setLayout(borderLayout3);
		jButton3.setText("-");
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jButton2.setText("+");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jButton4.setText("Delete");
		jButton4.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jPanel3.setPreferredSize(new Dimension(121, 37));
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(display, BorderLayout.CENTER);
		this.getContentPane().add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jButton1, BorderLayout.SOUTH);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jButton3, null);
		jPanel3.add(jButton2, null);
		jPanel3.add(jButton4, null);
	}

	void command(ActionEvent e) {
	String s=e.getActionCommand();
	if(s.equals("+")){
		Charge c=new Charge();
		c.setLocation(100,100);
		c.q=1;
		charges.add(c);
	}
	if(s.equals("-")){
		Charge c=new Charge();
		c.setLocation(100,100);
		c.q=-1;
		charges.add(c);
	}
	if(s.equals("Delete") && charges.size()>0){
		charges.remove(charges.get(0));
	}
	}
}

/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab;

import java.awt.*;
import javax.swing.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.event.*;

public class PlateauSpiral extends NeurolabExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	JSlider speed = new JSlider();
	Label3D label3D1 = new Label3D();
	JPanel jPanel3 = new JPanel0();
	Border border1;
	BorderLayout borderLayout3 = new BorderLayout();
	JButton jButton1 = new JButton();
	FlowLayout flowLayout1 = new FlowLayout();
	JSlider narms = new JSlider();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	JSlider twist = new JSlider();
	JButton returnbutton = new JButton();


	boolean init=true;
	JPanel graph = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		if(init){myinit();init=false;}
		g.setColor(Color.black);
		if(!testing){
			spiral.transform(rotator);
			if(supportsGraphics2D) ((Graphics2D)g).fill(spiral);
		}else{
			for(int z=0;z<850;z++){
				g.fillRect((int)(getWidth()*Math.random()),
					(int)(getHeight()*Math.random()),
					10,10);
			}
		}
	}
	};
	Timer timer=new Timer(40,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			graph.repaint();
		}
	} );
	GeneralPath spiral=new GeneralPath();
	int sped=10;
	int arms=4;
	double revs=1;
	int cx,cy;
	AffineTransform rotator;
	public void myinit(){
		cx=graph.getWidth()/2;
		cy=graph.getHeight()/2;
		int radius=(cx>cy)?cx*3/2:cy*3/2;	//radius= the lower of the two
		spiral=new GeneralPath();	//delete old one?
		spiral.moveTo(cx,cy);
		int steps=80,osteps=30;
		double px,py,theta=0,r,rt,dx,dy;
		double k=radius/(2*Math.PI*revs);
		for(int a=0;a<arms;a++){
			for(int t=0;t<steps;t++){
				rt=(a%2==0)?t:steps-t;		//rt goes out, then in!
				theta=(revs*rt*2*Math.PI/steps)+2*Math.PI*a/arms;
				r=(rt*radius/steps);
				px=r*Math.cos(theta);
				py=r*Math.sin(theta);
				dx=k*Math.cos(theta)-py;
				dy=k*Math.sin(theta)+px;

				px+=cx;py+=cy;
	//			spiral.curveTo((float)(px+dx),(float)(py+dy),(float)(px-dx),(float)(py-dy),(float)(px),(float)(py));
				spiral.lineTo((float)px,(float)py);
			}
			if(a%2==0){	//even : then go round circle
				double te;
				for(int t=1;t<=osteps;t++){
					te=theta+(t*2*Math.PI/arms/osteps);
					spiral.lineTo( (float)(cx+radius*Math.cos(te)), (float)(cy+radius*Math.sin(te)) );
				}
			}
		}
		rotator=AffineTransform.getRotateInstance(sped*2*Math.PI/360,cx,cy);
		timer.start();
		graph.setBackground(Color.white);
	}
	public PlateauSpiral() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		returnbutton.setBackground(systemGray);
		returnbutton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e){
		getHolder().setExhibit("com.neurolab.Adaptation");
	}
		});
	}

	private void jbInit() throws Exception {
		speed.setBackground(systemGray);
		narms.setBackground(systemGray);
		twist.setMaximum(30);
		twist.setBackground(systemGray);
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(3,3,3,3));
		getMainContainer().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		label3D1.setPreferredSize(new Dimension(60, 18));
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 14));
		label3D1.setText("Speed");
		jPanel3.setBorder(border1);
		jPanel3.setLayout(borderLayout3);
		speed.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				speed_stateChanged(e);
			}
		});
		jButton1.setText("Test");
		jButton1.setBackground(systemGray);
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel2.setLayout(flowLayout1);
		jPanel2.setPreferredSize(new Dimension(180, 37));
		speed.setPreferredSize(new Dimension(80, 24));
		narms.setPreferredSize(new Dimension(80, 24));
		narms.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				arms_stateChanged(e);
			}
		});
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		label3D2.setPreferredSize(new Dimension(50, 25));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 14));
		label3D2.setText("Arms");
		label3D3.setPreferredSize(new Dimension(50, 25));
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 14));
		label3D3.setText("Twist");
		twist.setPreferredSize(new Dimension(80, 24));
		twist.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				twist_stateChanged(e);
			}
		});
		returnbutton.setText("Return");
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(graph, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(label3D1, null);
		jPanel2.add(speed, null);
		jPanel2.add(label3D2, null);
		jPanel2.add(narms, null);
		jPanel2.add(label3D3, null);
		jPanel2.add(twist, null);
		jPanel2.add(returnbutton, null);
		jPanel2.add(jButton1, null);
	}

	void speed_stateChanged(ChangeEvent e) {
	sped=speed.getValue()-50;
	rotator=AffineTransform.getRotateInstance(sped*2*Math.PI/360,cx,cy);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	if(jButton1.getText()=="Test"){
		timer.stop();
		testing=true;
		graph.repaint();
		jButton1.setText("Adapt");
	}else{
		timer.start();
		testing=false;
		jButton1.setText("Test");
	}
	}
	boolean testing=false;

	void arms_stateChanged(ChangeEvent e) {
	timer.stop();
	arms=(narms.getValue()/20)*2;	// 0-10 step 2
	myinit();			//remake spiral shape
	}

	void twist_stateChanged(ChangeEvent e) {
	timer.stop();
	revs=twist.getValue()/15f;
	myinit();
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}

}
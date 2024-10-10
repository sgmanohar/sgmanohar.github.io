
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import com.cudos.common.Oscilloscope;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import com.cudos.common.kinetic.*;
import java.util.*;
import javax.swing.Timer;

//import com.neurolab.common.*;
import javax.swing.event.*;

public class BrownianMotion extends CudosExhibit implements ActionListener{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JButton jButton1 = new JButton();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	JPanel jPanel3 = new JPanel();
	Border border2;
	JPanel jPanel4 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	Border border3;
	BorderLayout borderLayout4 = new BorderLayout();
	KineticPane kpane = new KineticPane();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	JSlider speed = new JSlider();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel7 = new JPanel();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	FlowLayout flowLayout1 = new FlowLayout();
	JLabel jLabel3 = new JLabel();
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JSlider pressure = new JSlider();
	JLabel jLabel6 = new JLabel();

	public String getExhibitName(){return "Brownian Motion";}
	public BrownianMotion() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	MassiveMolecule smoke;
	Oscilloscope osc = new Oscilloscope(5,this){
	public void drawScreenElements(Graphics2D g){
		g.setColor(Color.gray);
		int u=getGutter();
		for(int i=0;i<getColors().length;i++){
			g.drawLine(u,u+getBaseY()[i],getWidth()-2*u,u+getBaseY()[i]);
		}
	}
	};
	public int min(int i,int j){return (i>j)?j:i;}

	public void actionPerformed(ActionEvent e){}
	Timer timer=new Timer(300,new ActionListener(){
		int oldnc;
		int nbuck=30;
		double topspeed=15;
		int[] bucket;
		public void actionPerformed(ActionEvent e){
			double ctot=0,cstot=0;
			int sq,n=0;
			double speed;
			bucket=new int[nbuck];
			int ib;
			Molecule m;
			for(Enumeration mo=kpane.molecules.elements();mo.hasMoreElements();n++){
				m=(Molecule)mo.nextElement();
				cstot+=(sq=(m.vx*m.vx+m.vy*m.vy));
				ctot+=(speed=Math.sqrt(sq));
				ib=(int)(speed*nbuck/topspeed);
				bucket[min(ib,nbuck-1)]++;
			}
			osc.setPosY(new int[]{	-(int)(50*Math.sqrt(smoke.vx*smoke.vx+smoke.vy*smoke.vy)),
						-(int)Math.sqrt((smoke.x-smokestartx)*(smoke.x-smokestartx)+(smoke.y-smokestarty)*(smoke.y-smokestarty)),
						-(int)(50*ctot/n),
						-(int)(50*Math.sqrt(cstot/n)),
						-(int)(5*(kpane.nCollisions-oldnc)*1000/300)
						} );
			oldnc=kpane.nCollisions;
			JPanel h=histogram;		//local shorthand
			histogram.paintImmediately(h.getBounds());
			Graphics2D g=(Graphics2D)h.getGraphics();
			int bl=2;
			g.setColor(Color.green);
			g.drawLine(0,h.getHeight()-bl,h.getWidth(),h.getHeight()-bl);
			for(int i=0;i<nbuck;i++){
				g.fillRect(i*h.getWidth()/nbuck,h.getHeight()-bucket[i],h.getWidth()/nbuck,bucket[i]-2);
			}
		}
	} );
	int smokestartx=100,smokestarty=200;
	Random rand=new Random();
	Rectangle rect;
	public void  postinit(){
		rect=new Rectangle(5,5,kpane.getWidth()-10,kpane.getHeight()/2-10);
		Molecule m;
		for(int i=0;i<300;i++){
			kpane.molecules.add(m=new Molecule(rand,rect));
//			m.Mass=1;
			m.setType(Molecule.WATER);
		}
		kpane.molecules.add(smoke=new MassiveMolecule(smokestartx,smokestarty));
		kpane.swalls.add(new CircularWall(smoke,15));
		smoke.setType(Molecule.PROTEIN);
		smoke.vx=4;smoke.vy=7;
		smoke.s=25;
		smoke.Mass=15;

		osc.setBaseY(new int[]{60,100,150,180,220});
		osc.setColors(new Color[]{Color.red,Color.green,Color.blue,Color.yellow,Color.cyan});
		osc.timer.setDelay(300);	//very slow
		timer.start();
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(5,5,5,5);
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,new Color(255, 255, 235),new Color(94, 93, 80),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(5,5,5,5));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(2,2,2,2));
		border4 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		border5 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(2,2,2,2));
		this.getContentPane().setLayout(borderLayout1);
		jButton1.setText("Exit");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout2);
		jPanel2.setBorder(border1);
		jPanel3.setBorder(border2);
		jPanel3.setLayout(borderLayout3);
		jPanel4.setBorder(border3);
		jPanel4.setLayout(borderLayout4);
		kpane.setBackground(new Color(192, 224, 224));
		jPanel5.setPreferredSize(new Dimension(250, 10));
		jPanel5.setLayout(borderLayout5);
		jPanel6.setPreferredSize(new Dimension(10, 100));
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel1.setForeground(Color.red);
		jLabel1.setText("Speed");
		jPanel7.setBackground(Color.gray);
		jPanel7.setPreferredSize(new Dimension(40, 27));
		jPanel7.setLayout(flowLayout1);
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel2.setForeground(Color.green);
		jLabel2.setText("Dist");
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		flowLayout1.setHgap(0);
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel3.setForeground(Color.blue);
		jLabel3.setText("< c >");
		jLabel4.setText("Animation speed");
		speed.setPreferredSize(new Dimension(150, 24));
		speed.setBorder(BorderFactory.createLineBorder(SystemColor.control,1));
		speed.addChangeListener(new BrownianMotion_speed_changeAdapter(this));
		osc.setPreferredSize(new Dimension(250, 58));
		jLabel5.setText("Gas pressure");
		pressure.setPreferredSize(new Dimension(150, 24));
		pressure.setBorder(BorderFactory.createLineBorder(SystemColor.control,1));
		pressure.addChangeListener(new BrownianMotion_pressure_changeAdapter(this));
		jLabel6.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel6.setForeground(Color.yellow);
		jLabel6.setText("rms");
		jLabel7.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel7.setForeground(Color.cyan);
		jLabel7.setText("Impact");
		jPanel8.setBackground(SystemColor.control);
		jPanel8.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(2,2,2,2)));
		jPanel8.setPreferredSize(new Dimension(130, 60));
		jPanel8.setLayout(borderLayout6);
		histogram.setBackground(Color.black);
		histogram.setBorder(border5);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(jButton1, null);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(kpane, BorderLayout.CENTER);
		jPanel2.add(jPanel5, BorderLayout.EAST);
		jPanel5.add(osc, BorderLayout.CENTER);
		osc.add(jPanel7, BorderLayout.WEST);
		jPanel7.add(jLabel1, null);
		jPanel7.add(jLabel2, null);
		jPanel7.add(jLabel3, null);
		jPanel7.add(jLabel6, null);
		jPanel7.add(jLabel7, null);
		jPanel2.add(jPanel6, BorderLayout.SOUTH);
		jPanel6.add(jLabel4, null);
		jPanel6.add(speed, null);
		jPanel6.add(jLabel5, null);
		jPanel6.add(pressure, null);
		jPanel6.add(jPanel8, null);
		jPanel8.add(histogram, BorderLayout.CENTER);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	kpane.timer.stop();
	getApplet().toChooser();
	}
	JLabel jLabel7 = new JLabel();
	JPanel jPanel8 = new JPanel();
	Border border4;
	JPanel histogram = new JPanel();
	BorderLayout borderLayout6 = new BorderLayout();
	Border border5;

	void speed_stateChanged(ChangeEvent e) {
	kpane.timer.setDelay(100-speed.getValue());
	}

	void pressure_stateChanged(ChangeEvent e) {
	int targ=pressure.getValue()*3;
	Molecule m;int tries=0;
	if(kpane.molecules.size()>targ){
		while(kpane.molecules.size()>targ && tries++<30){
			m=kpane.getAMolecule(rand,Molecule.WATER);
			m.paint((Graphics2D)kpane.getGraphics(),false);
			kpane.molecules.remove(m);
		}
	}else if(kpane.molecules.size()<targ){
		while(kpane.molecules.size()<targ && tries++<30){
			kpane.molecules.add(m=new Molecule(rand,rect));
			m.setType(Molecule.WATER);
		}
	}
	}
}

class BrownianMotion_speed_changeAdapter implements javax.swing.event.ChangeListener {
	BrownianMotion adaptee;

	BrownianMotion_speed_changeAdapter(BrownianMotion adaptee) {
		this.adaptee = adaptee;
	}

	public void stateChanged(ChangeEvent e) {
		adaptee.speed_stateChanged(e);
	}
}

class BrownianMotion_pressure_changeAdapter implements javax.swing.event.ChangeListener {
	BrownianMotion adaptee;

	BrownianMotion_pressure_changeAdapter(BrownianMotion adaptee) {
		this.adaptee = adaptee;
	}

	public void stateChanged(ChangeEvent e) {
		adaptee.pressure_stateChanged(e);
	}
}
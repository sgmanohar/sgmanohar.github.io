
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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.cudos.wind.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class WindTunnelExhibit extends CudosExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JButton jButton1 = new JButton();
	WindTunnel windTunnel = new WindTunnel();
	BorderLayout borderLayout2 = new BorderLayout();
	private JPanel jPanel4 = new JPanel();
	private JButton startbutton = new JButton();
	private JButton clear = new JButton();
	private BorderLayout borderLayout3 = new BorderLayout();
	private JPanel jPanel5 = new JPanel();
	private BorderLayout borderLayout4 = new BorderLayout();
	private JPanel jPanel6 = new JPanel();
	private JCheckBox rotatebox = new JCheckBox();
	private JPanel jPanel7 = new JPanel();
	private JPanel jPanel8 = new JPanel();
	private JRadioButton jRadioButton1 = new JRadioButton();
	private JRadioButton jRadioButton2 = new JRadioButton();
	private FlowLayout flowLayout1 = new FlowLayout();
	private JRadioButton jRadioButton3 = new JRadioButton();
	private JPanel jPanel9 = new JPanel();
	private BorderLayout borderLayout5 = new BorderLayout();
	private JPanel jPanel10 = new JPanel();
	private BorderLayout borderLayout6 = new BorderLayout();
	private JSlider speedslider = new JSlider();


	int rad=60;
	Shape ellipse = new Ellipse2D.Double(60+rad,60+rad,rad*3,rad*2),
				triangle = new Polygon(new int[]{ 60+rad, 60+4*rad, 60+3*rad },
															 new int[]{ 60+2*rad, 60+rad, 60+rad*5/2} ,3),
				rectangle = new Rectangle2D.Double(60+rad, 60+rad, rad*3, rad*2);

	public WindTunnelExhibit() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	Font f=new Font("Dialog",Font.PLAIN,250);
	Shape s=f.createGlyphVector(new FontRenderContext(AffineTransform.getTranslateInstance(60,60),false,false), "S").getOutline();
	windTunnel.addWall(new WindWall(ellipse));
//	windTunnel.addWall(new WindWall(s));
//	windTunnel.addWall(new WindWall(new Polygon(new int[]{200,300,100}, new int[]{100,200,200}, 3)));
	windTunnel.start();
	}

	private void jbInit() throws Exception {
		titledBorder1 = new TitledBorder("");
		this.getContentPane().setLayout(borderLayout1);
		jButton1.setText("Return");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout2);
		windTunnel.setBackground(new Color(212, 255, 255));
		windTunnel.setColor1(Color.blue);
		windTunnel.setColor2(Color.magenta);
		startbutton.setText("Start");
		startbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				startbutton_actionPerformed(e);
			}
		});
		clear.setText("Clear");
		clear.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clear_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout3);
		jPanel5.setLayout(borderLayout4);
		rotatebox.setText("Rotate");
		rotatebox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotatebox_actionPerformed(e);
			}
		});
		jRadioButton1.setText("Rectangle");
		jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapechange(e);
			}
		});
		jPanel8.setBorder(BorderFactory.createEtchedBorder());
		jPanel8.setPreferredSize(new Dimension(100, 100));
		jPanel8.setLayout(flowLayout1);
		jRadioButton2.setRolloverSelectedIcon(new ImageIcon(new java.net.URL("file:///E:/Java/WebRoot/CUDOS/CLASSES/resources/ICONS/arrow_down.gif")));
		jRadioButton2.setSelected(true);
		jRadioButton2.setText("Oval");
		jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapechange(e);
			}
		});
		flowLayout1.setAlignment(FlowLayout.LEFT);
		jRadioButton3.setText("Triangle");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapechange(e);
			}
		});
		jPanel7.setLayout(borderLayout5);
		jPanel9.setLayout(borderLayout6);
		speedslider.setBorder(BorderFactory.createEtchedBorder());
		speedslider.setPreferredSize(new Dimension(100, 24));
		speedslider.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				speedslider_stateChanged(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(windTunnel, BorderLayout.CENTER);
		this.getContentPane().add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel4, BorderLayout.NORTH);
		jPanel4.add(clear, null);
		jPanel4.add(startbutton, null);
		jPanel2.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(jPanel6, BorderLayout.NORTH);
		jPanel6.add(rotatebox, null);
		jPanel5.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(jPanel8, BorderLayout.NORTH);
		jPanel8.add(jRadioButton2, null);
		jPanel8.add(jRadioButton1, null);
		jPanel8.add(jRadioButton3, null);
		jPanel7.add(jPanel9,  BorderLayout.CENTER);
		jPanel9.add(jPanel10, BorderLayout.CENTER);
		jPanel10.add(speedslider, null);
		this.getContentPane().add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jButton1, null);
		bg.add(jRadioButton1); bg.add(jRadioButton2); bg.add(jRadioButton3);
		speedslider.setValue(20);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	windTunnel.stop();
	getApplet().toChooser();
	}

	void startbutton_actionPerformed(ActionEvent e) {
	if(startbutton.getText()=="Start"){
		windTunnel.setBlowerOn(true);
		startbutton.setText("Stop");
	}else{
		windTunnel.setBlowerOn(false);
		startbutton.setText("Start");
	}
	}

	void clear_actionPerformed(ActionEvent e) {
	windTunnel.restart();
	}
	/** inform each wall that rotating is on */
	void rotatebox_actionPerformed(ActionEvent e) {
		Vector v=windTunnel.walls;
		for(int i=0;i<v.size();i++){
			WindWall w = (WindWall)v.get(i);
			w.setRotating(rotatebox.isSelected());
		}
	}

	void shapechange(ActionEvent e) {
		String t = e.getActionCommand();
		Shape ns = null;
		if(t.equals("Oval")){
			ns = ellipse;
		}else if(t.equals("Triangle")){
			ns = triangle;
		}else if(t.equals("Rectangle")){
			ns = rectangle;
		}
		if(ns!=null){
			windTunnel.removeAllWalls();
			WindWall w =new WindWall(ns);
			windTunnel.addWall(w);
			w.setRotating(rotatebox.isSelected());
		}
	}
	private ButtonGroup bg = new ButtonGroup();
	private TitledBorder titledBorder1;

	void speedslider_stateChanged(ChangeEvent e) {
		int f = speedslider.getValue();
		windTunnel.setDrivingPressure( f/20. );
	}
}
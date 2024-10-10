
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
import java.awt.event.*;

public class ControlSystems extends NeurolabExhibit {
 public String getExhibitName() {
    return "Control Systems";
  }
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel0();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel2 = new JPanel0();
  BorderLayout borderLayout3 = new BorderLayout();
  Oscilloscope oscilloscope1 = new Oscilloscope(2,null);
  JPanel jPanel4 = new JPanel0();
  JPanel jPanel5 = new JPanel0();
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel3 = new JPanel0();
  JPanel jPanel6 = new JPanel0();
  Border border1;
  TitledBorder titledBorder1;
  GridLayout gridLayout2 = new GridLayout();
  JPanel jPanel7 = new JPanel0();
  JPanel jPanel8 = new JPanel0();
  JRadioButton controlprop = new JRadioButton0();
  JRadioButton controlrate = new JRadioButton0();
  JRadioButton controlintegral = new JRadioButton0();
  GridLayout gridLayout3 = new GridLayout();
  JSlider gainslider = new JSlider();
  JSlider biasslider = new JSlider();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  Border border2;
  Border border3;
  JPanel jPanel9 = new JPanel0();
  BorderLayout borderLayout4 = new BorderLayout();
  ReturnButton returnButton1 = new ReturnButton();
  JSlider externalslider = new JSlider();
  JLabel jLabel3 = new JLabel();
  JSlider plantgainslider = new JSlider();
  JLabel jLabel4 = new JLabel();
  JPanel jPanel10 = new JPanel0();
  Border border4;
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel jPanel11 = new JPanel0();
  Border border5;
  JPanel jPanel12 = new JPanel0();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  Border border6;
	ButtonGroup bg=new ButtonGroup();
  BorderLayout borderLayout6 = new BorderLayout();
  JPanel jPanel13 = new JPanel0();
  GridLayout gridLayout4 = new GridLayout();
  JPanel jPanel14 = new JPanel0();
  JPanel jPanel15 = new JPanel0();
  GridLayout gridLayout5 = new GridLayout();
  GridLayout gridLayout6 = new GridLayout();
  JRadioButton ballistic = new JRadioButton0();
  JRadioButton directfb = new JRadioButton0();
  JRadioButton internalfb = new JRadioButton0();
  JCheckBox parametricfb = new JCheckBox();
  JCheckBox feedforward = new JCheckBox();
  JCheckBox plantdelay = new JCheckBox();
  JPanel graphicpanel = new JPanel0();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  GraphicComponent graphicComponent2 = new GraphicComponent();
  GraphicComponent graphicComponent3 = new GraphicComponent();
  GraphicComponent graphicComponent4 = new GraphicComponent();
  GraphicComponent graphicComponent1 = new GraphicComponent();
  JPanel gfeedforward = new JPanel0();
  GraphicComponent feedforw1 = new GraphicComponent();
  GraphicComponent feedforw2 = new GraphicComponent();
  JPanel directfbpanel = new JPanel();
  GraphicComponent graphicComponent8 = new GraphicComponent();
  GraphicComponent dfbarrow1 = new GraphicComponent();
  GraphicComponent graphicComponent10 = new GraphicComponent();
  GraphicComponent dfbarrow2 = new GraphicComponent();
  JLabel jLabel12 = new JLabel();
  JPanel modelpanel = new JPanel();
  GraphicComponent graphicComponent7 = new GraphicComponent();
  GraphicComponent graphicComponent12 = new GraphicComponent();
  JPanel directfbpanelr = new JPanel();
  GraphicComponent graphicComponent9 = new GraphicComponent();
  GraphicComponent graphicComponent11 = new GraphicComponent();
  JPanel paramballpanel = new JPanel();
  GraphicComponent graphicComponent13 = new GraphicComponent();
  GraphicComponent graphicComponent14 = new GraphicComponent();
  GraphicComponent graphicComponent15 = new GraphicComponent();
  GraphicComponent graphicComponent16 = new GraphicComponent();
  GraphicComponent graphicComponent17 = new GraphicComponent();
  GraphicComponent graphicComponent18 = new GraphicComponent();
  GraphicComponent dot01 = new GraphicComponent();
  JPanel internalfbpanel = new JPanel();
  GraphicComponent graphicComponent5 = new GraphicComponent();
  GraphicComponent graphicComponent6 = new GraphicComponent();
  GraphicComponent graphicComponent20 = new GraphicComponent();
  GraphicComponent graphicComponent19 = new GraphicComponent();
  GraphicComponent graphicComponent21 = new GraphicComponent();
  GraphicComponent graphicComponent22 = new GraphicComponent();
  GraphicComponent graphicComponent23 = new GraphicComponent();

	ButtonGroup bg2=new ButtonGroup();

  public ControlSystems() {
  }
  public void init(){
    super.init();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    myinit();
		ActionListener al=new ActionListener(){public void actionPerformed(ActionEvent e){t=0;}};
		oscilloscope1.sweep.addActionListener(al);
		oscilloscope1.clear.addActionListener(al);
		oscilloscope1.timer.setDelay(80);
		oscilloscope1.xSpeed=2;
    setupDiagram();
    timer.start();
  }
	public void myinit(){
		bg.add(controlprop);bg.add(controlrate);bg.add(controlintegral);
		bg2.add(ballistic);bg2.add(directfb);bg2.add(internalfb);
		controlprop.doClick();
		ballistic.doClick();
	}
	Timer timer=new Timer(80,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			calc();
//System.out.println(X+","+Y);
			oscilloscope1.setPosY(new int[]{(int)(512+X*450/30),(int)(512+Y*450/30)});
		}
	});



		//CALC

	double oldv,v,sumv;
	double X,Y,m,j;
	double cgain,u,w,rate;
	int model,controller;
	double gain,bias;
	int t=0;
	public void calc(){
			//procure values from GUI
		gain=gainslider.getValue();
		bias=biasslider.getValue();
		model=modeltype();
		controller=contype();

			//Input
		t++;
		j = t++ % 120;
		if (j > 60) j = 120 - j;
		X = limit(2 * j - 60, -30, 30);


			//Output
		oldv = v;
		sumv = sumv * 0.98 + 0.02 * v;
		switch(model){
			case 0:
				v = X;
				break;
			case 1:
				m = 0.9 * m + 0.1 * (gain * w / 500 + bias / 100);
				v = X - m;
				break;
			case 2:
				v = X - Y;
				break;
		}
		if(feedforward.isSelected())  v = v - externalslider.getValue();
		switch(controller){
			case 0:
				w = cgain * v;
				break;
			case 1:
				rate = 0.9 * rate + 0.1 * (v - oldv);
				if(model == 0 ){
					w = cgain * (v + 10 * rate);
				}else{
					if(plantdelay.isSelected() && model == 2){
						w = cgain * (v - 5 * rate);
					}else w = cgain * (v + 10 * rate);
				}
				break;
			case 2:
				w = cgain * (v + 5 * sumv);
				break;
		}

		if (model != 0) w = 3 * w;
		if( model != 1) w = w + biasslider.getValue() / 10;
		u = u * 0.9 + 0.1 * w * plantgainslider.getValue() / 50;
		if (plantdelay.isSelected()){
			Y = externalslider.getValue() + delayer(u);
		}else{
			Y = externalslider.getValue() + u;
		}
		cgain = gain / 500;
		if( model == 1) cgain = 1;
		if (parametricfb.isSelected()) adjust();
	}

	double[] del=new double[6];
	double delayer(double z){
		double d = del[0];
		for(int i=0;i<5;i++)del[i] = del[i+1];
		del[5]=z;
		return d;
	}

	void adjust(){
		switch(model){
			case 0:
				biasslider.setValue((int)limit(bias-(Y-X)/20,-300,300));
				gainslider.setValue((int)limit(gain-(Math.abs(Y)-Math.abs(X))/20,200,700));
				break;
			case 1:
				biasslider.setValue((int)limit((bias+(Y-m))/10,-300,300));
				gainslider.setValue((int)limit(gain+(Math.abs(Y)-Math.abs(m))/10,200,700));
				break;
		}
	}
	public double limit(double x, double min, double max){
		return (x>max)?max:(x<min)?min:x;
	}



  private void jbInit() throws Exception {
    gainslider.setMaximum(700);
    gainslider.setMinimum(200);
    gainslider.setBackground(systemGray);
    biasslider.setMaximum(300);
    biasslider.setMinimum(-300);
    biasslider.setValue(0);
    biasslider.setBackground(systemGray);
    externalslider.setValue(0);
    externalslider.setMaximum(20);
    externalslider.setMinimum(-20);
    externalslider.setBackground(systemGray);
    plantgainslider.setMaximum(60);
    plantgainslider.setMinimum(30);
    plantgainslider.setBackground(systemGray);
    parametricfb.setBackground(systemGray);
    feedforward.setBackground(systemGray);
    plantdelay.setBackground(systemGray);
    border1 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
    titledBorder1 = new TitledBorder(border1,"Controller");
    border2 = BorderFactory.createLineBorder(SystemColor.control,1);
    border3 = BorderFactory.createLineBorder(SystemColor.control,1);
    border4 = BorderFactory.createEmptyBorder(5,5,5,5);
    border5 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
    border6 = BorderFactory.createLineBorder(SystemColor.control,1);
    jPanel1.setLayout(borderLayout2);
    jPanel2.setLayout(borderLayout3);
    oscilloscope1.setPreferredSize(new Dimension(250, 130));
    oscilloscope1.setGutter(3);
    jPanel1.setPreferredSize(new Dimension(150, 125));
    jPanel4.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    jPanel3.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),"Controller"));
    jPanel3.setLayout(gridLayout2);
    jPanel6.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),"Noise"));
    jPanel6.setLayout(null);
    gridLayout2.setRows(2);
    gridLayout2.setColumns(1);
    controlprop.setText("Proportional");
    controlrate.setText("Prop + rate");
    controlintegral.setText("Prop + integral");
    jPanel7.setLayout(gridLayout3);
    gridLayout3.setRows(3);
    gridLayout3.setColumns(1);
    jPanel8.setLayout(null);
    gainslider.setBorder(border2);
    gainslider.setBounds(new Rectangle(-4, 0, 126, 24));
    biasslider.setBorder(border3);
    biasslider.setBounds(new Rectangle(-4, 41, 126, 24));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setText("Gain");
    jLabel1.setBounds(new Rectangle(46, 21, 31, 17));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel2.setText("Bias");
    jLabel2.setBounds(new Rectangle(45, 61, 27, 16));
    jPanel9.setLayout(borderLayout4);
    externalslider.setBorder(border6);
    externalslider.setBounds(new Rectangle(10, 33, 110, 26));
    jLabel3.setText("External");
    jLabel3.setBounds(new Rectangle(44, 58, 51, 17));
    plantgainslider.setBorder(BorderFactory.createLineBorder(SystemColor.control,1));
    plantgainslider.setBounds(new Rectangle(9, 82, 111, 24));
    jLabel4.setText("Plant gain");
    jLabel4.setBounds(new Rectangle(38, 106, 61, 17));
    jPanel10.setBorder(border4);
    jPanel10.setLayout(borderLayout5);
    jPanel11.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)));
    jPanel11.setLayout(borderLayout6);
    jPanel12.setLayout(null);
    jPanel12.setPreferredSize(new Dimension(13, 1));
    jLabel5.setFont(new java.awt.Font("SansSerif", 1, 16));
    jLabel5.setForeground(Color.green);
    jLabel5.setText("x");
    jLabel5.setBounds(new Rectangle(3, 24, 14, 17));
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel6.setForeground(Color.red);
    jLabel6.setText("y");
    jLabel6.setBounds(new Rectangle(3, 58, 13, 17));
    jPanel13.setLayout(gridLayout4);
    gridLayout4.setColumns(2);
    jPanel14.setLayout(gridLayout5);
    gridLayout5.setRows(3);
    gridLayout5.setColumns(1);
    jPanel15.setLayout(gridLayout6);
    gridLayout6.setRows(3);
    gridLayout6.setColumns(1);
    ballistic.setToolTipText("");
    ballistic.setSelected(true);
    ballistic.setText("Ballistic");
    ballistic.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    directfb.setText("Direct FB");
    directfb.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    internalfb.setText("Internal FB");
    internalfb.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    parametricfb.setText("Parametric FB");
    parametricfb.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    feedforward.setText("Feedforward");
    feedforward.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    plantdelay.setText("Plant Delay");
    plantdelay.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        changetype(e);
      }
    });
    graphicpanel.setLayout(null);
    jLabel7.setBackground(Color.red);
    jLabel7.setFont(new java.awt.Font("SansSerif", 1, 18));
    jLabel7.setOpaque(true);
    jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel7.setText("x");
    jLabel7.setBounds(new Rectangle(10, 114, 20, 31));
    jLabel8.setBounds(new Rectangle(272, 113, 23, 31));
    jLabel8.setText("y");
    jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel8.setOpaque(true);
    jLabel8.setFont(new java.awt.Font("SansSerif", 1, 18));
    jLabel8.setBackground(Color.green);
    jLabel9.setBounds(new Rectangle(90, 117, 62, 31));
    jLabel9.setText("Controller");
    jLabel9.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel9.setOpaque(true);
    jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12));
    jLabel9.setForeground(Color.white);
    jLabel9.setBackground(Color.darkGray);
    jLabel10.setBackground(Color.darkGray);
    jLabel10.setForeground(Color.white);
    jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12));
    jLabel10.setOpaque(true);
    jLabel10.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel10.setText("Plant");
    jLabel10.setBounds(new Rectangle(180, 116, 62, 31));
    jLabel11.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel11.setText("Noise");
    jLabel11.setBounds(new Rectangle(268, 61, 37, 20));
    graphicComponent2.setType(2);
    graphicComponent2.setThickness(2);
    graphicComponent2.setArrow(1);
    graphicComponent2.setBounds(new Rectangle(29, 121, 61, 19));
    graphicComponent3.setBounds(new Rectangle(151, 122, 29, 19));
    graphicComponent3.setArrow(1);
    graphicComponent3.setThickness(2);
    graphicComponent3.setType(2);
    graphicComponent4.setBounds(new Rectangle(241, 120, 30, 19));
    graphicComponent4.setArrow(1);
    graphicComponent4.setThickness(2);
    graphicComponent4.setType(2);
    graphicComponent1.setToolTipText("");
    graphicComponent1.setType(3);
    graphicComponent1.setThickness(2);
    graphicComponent1.setArrow(1);
    graphicComponent1.setBounds(new Rectangle(274, 80, 20, 32));
    gfeedforward.setBounds(new Rectangle(76, 60, 190, 57));
    gfeedforward.setLayout(null);
    feedforw1.setType(2);
    feedforw1.setThickness(2);
    feedforw1.setBounds(new Rectangle(50, 6, 136, 13));
    feedforw2.setType(3);
    feedforw2.setThickness(2);
    feedforw2.setArrow(1);
    feedforw2.setBounds(new Rectangle(44, 13, 14, 44));
    directfbpanel.setOpaque(false);
    directfbpanel.setBounds(new Rectangle(41, 117, 61, 99));
    directfbpanel.setLayout(null);
    graphicComponent8.setType(2);
    graphicComponent8.setThickness(2);
    graphicComponent8.setBounds(new Rectangle(24, 60, 39, 18));
    dfbarrow1.setType(3);
    dfbarrow1.setThickness(2);
    dfbarrow1.setArrow(2);
    dfbarrow1.setBounds(new Rectangle(13, 20, 20, 51));
    graphicComponent10.setType(4);
    graphicComponent10.setBounds(new Rectangle(18, 8, 11, 12));
    dfbarrow2.setType(2);
    dfbarrow2.setThickness(2);
    dfbarrow2.setArrow(1);
    dfbarrow2.setBounds(new Rectangle(4, 4, 14, 19));
    jLabel12.setBackground(Color.darkGray);
    jLabel12.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel12.setForeground(Color.white);
    jLabel12.setOpaque(true);
    jLabel12.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel12.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel12.setText("Model");
    jLabel12.setBounds(new Rectangle(-1, 39, 62, 31));
    modelpanel.setOpaque(false);
    modelpanel.setBounds(new Rectangle(90, 132, 86, 73));
    modelpanel.setLayout(null);
    graphicComponent7.setType(3);
    graphicComponent7.setThickness(2);
    graphicComponent7.setBounds(new Rectangle(66, 0, 15, 55));
    graphicComponent12.setType(2);
    graphicComponent12.setThickness(2);
    graphicComponent12.setArrow(2);
    graphicComponent12.setBounds(new Rectangle(61, 43, 13, 20));
    directfbpanelr.setOpaque(false);
    directfbpanelr.setBounds(new Rectangle(96, 144, 195, 72));
    directfbpanelr.setLayout(null);
    graphicComponent9.setToolTipText("");
    graphicComponent9.setType(2);
    graphicComponent9.setThickness(2);
    graphicComponent9.setBounds(new Rectangle(-1, 31, 189, 22));
    graphicComponent11.setType(3);
    graphicComponent11.setThickness(2);
    graphicComponent11.setBounds(new Rectangle(178, 0, 19, 44));
    paramballpanel.setOpaque(false);
    paramballpanel.setBounds(new Rectangle(48, 129, 239, 82));
    paramballpanel.setLayout(null);
    graphicComponent13.setToolTipText("");
    graphicComponent13.setType(3);
    graphicComponent13.setThickness(2);
    graphicComponent13.setBounds(new Rectangle(3, 1, 14, 57));
    graphicComponent14.setType(2);
    graphicComponent14.setThickness(2);
    graphicComponent14.setArrow(2);
    graphicComponent14.setBounds(new Rectangle(81, 50, 153, 15));
    graphicComponent15.setType(3);
    graphicComponent15.setThickness(2);
    graphicComponent15.setBounds(new Rectangle(227, 15, 12, 43));
    graphicComponent16.setBounds(new Rectangle(9, 50, 60, 15));
    graphicComponent16.setThickness(2);
    graphicComponent16.setArrow(1);
    graphicComponent16.setType(2);
    graphicComponent17.setType(4);
    graphicComponent17.setBounds(new Rectangle(69, 51, 12, 12));
    graphicComponent18.setType(3);
    graphicComponent18.setThickness(2);
    graphicComponent18.setBounds(new Rectangle(64, 22, 23, 31));
    dot01.setType(4);
    dot01.setBounds(new Rectangle(73, 19, 5, 5));
    internalfbpanel.setOpaque(false);
    internalfbpanel.setBounds(new Rectangle(56, 143, 234, 83));
    internalfbpanel.setLayout(null);
    graphicComponent5.setType(3);
    graphicComponent5.setThickness(2);
    graphicComponent5.setBounds(new Rectangle(222, 0, 10, 77));
    graphicComponent6.setType(2);
    graphicComponent6.setThickness(2);
    graphicComponent6.setArrow(2);
    graphicComponent6.setBounds(new Rectangle(74, 71, 154, 10));
    graphicComponent20.setBounds(new Rectangle(7, 71, 56, 10));
    graphicComponent20.setThickness(2);
    graphicComponent20.setArrow(1);
    graphicComponent20.setType(2);
    graphicComponent19.setType(3);
    graphicComponent19.setThickness(2);
    graphicComponent19.setBounds(new Rectangle(3, 43, 11, 33));
    graphicComponent21.setType(4);
    graphicComponent21.setBounds(new Rectangle(62, 69, 12, 13));
    graphicComponent22.setType(3);
    graphicComponent22.setThickness(2);
    graphicComponent22.setBounds(new Rectangle(61, 59, 13, 11));
    graphicComponent23.setType(4);
    graphicComponent23.setBounds(new Rectangle(65, 59, 5, 5));
    jPanel1.add(jPanel2, BorderLayout.EAST);
    jPanel2.add(jPanel5, BorderLayout.NORTH);
    jPanel5.add(oscilloscope1, null);
    oscilloscope1.add(jPanel12, BorderLayout.WEST);
    jPanel12.add(jLabel6, null);
    jPanel12.add(jLabel5, null);
    jPanel2.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jPanel3, null);
    jPanel3.add(jPanel7, null);
    jPanel7.add(controlprop, null);
    jPanel7.add(controlrate, null);
    jPanel7.add(controlintegral, null);
    jPanel3.add(jPanel8, null);
    jPanel8.add(gainslider, null);
    jPanel8.add(jLabel1, null);
    jPanel8.add(biasslider, null);
    jPanel8.add(jLabel2, null);
    jPanel4.add(jPanel9, null);
    jPanel9.add(returnButton1, BorderLayout.SOUTH);
    jPanel9.add(jPanel6, BorderLayout.CENTER);
    jPanel6.add(plantgainslider, null);
    jPanel6.add(jLabel4, null);
    jPanel6.add(externalslider, null);
    jPanel6.add(jLabel3, null);
    jPanel1.add(jPanel10, BorderLayout.CENTER);
    jPanel10.add(jPanel11, BorderLayout.CENTER);
    jPanel11.add(jPanel13, BorderLayout.SOUTH);
    jPanel13.add(jPanel14, null);
    jPanel14.add(ballistic, null);
    jPanel14.add(directfb, null);
    jPanel14.add(internalfb, null);
    jPanel13.add(jPanel15, null);
    jPanel15.add(parametricfb, null);
    jPanel15.add(feedforward, null);
    jPanel15.add(plantdelay, null);
    jPanel11.add(graphicpanel, BorderLayout.CENTER);
    modelpanel.add(jLabel12, null);
    modelpanel.add(graphicComponent7, null);
    modelpanel.add(graphicComponent12, null);
    internalfbpanel.add(graphicComponent5, null);
    internalfbpanel.add(graphicComponent6, null);
    internalfbpanel.add(graphicComponent20, null);
    internalfbpanel.add(graphicComponent21, null);
    internalfbpanel.add(graphicComponent19, null);
    internalfbpanel.add(graphicComponent22, null);
    internalfbpanel.add(graphicComponent23, null);
    directfbpanelr.add(graphicComponent9, null);
    directfbpanelr.add(graphicComponent11, null);
    graphicpanel.add(graphicComponent2, null);
    graphicpanel.add(jLabel11, null);
    graphicpanel.add(jLabel8, null);
    graphicpanel.add(jLabel7, null);
    graphicpanel.add(graphicComponent4, null);
    graphicpanel.add(graphicComponent1, null);
    gfeedforward.add(feedforw1, null);
    gfeedforward.add(feedforw2, null);
    graphicpanel.add(jLabel10, null);
    graphicpanel.add(graphicComponent3, null);
    graphicpanel.add(jLabel9, null);
    paramballpanel.add(graphicComponent13, null);
    paramballpanel.add(graphicComponent14, null);
    paramballpanel.add(graphicComponent15, null);
    paramballpanel.add(graphicComponent16, null);
    paramballpanel.add(graphicComponent17, null);
    paramballpanel.add(graphicComponent18, null);
    paramballpanel.add(dot01, null);
    directfbpanel.add(graphicComponent8, null);
    directfbpanel.add(dfbarrow1, null);
    directfbpanel.add(graphicComponent10, null);
    directfbpanel.add(dfbarrow2, null);

    graphicpanel.add(modelpanel, null);
    graphicpanel.add(directfbpanelr, null);
    graphicpanel.add(paramballpanel, null);
    graphicpanel.add(directfbpanel, null);
    graphicpanel.add(internalfbpanel, null);
    graphicpanel.add(gfeedforward, null);


    gainslider.setValue(500);

    getMainContainer().setLayout(borderLayout1);
    getMainContainer().add(jPanel1, BorderLayout.CENTER);
  }
	void setupDiagram(){
		if(directfb.isSelected() && parametricfb.isSelected()){
			parametricfb.setSelected(false);
			//return;
		}
		directfbpanel.setVisible(false);
		paramballpanel.setVisible(false);
		directfbpanelr.setVisible(false);
		internalfbpanel.setVisible(false);
		modelpanel.setVisible(false);
		gfeedforward.setVisible(false);

		if(feedforward.isSelected())gfeedforward.setVisible(true);
		if(directfb.isSelected()){
			directfbpanel.setVisible(true);
			directfbpanelr.setVisible(true);
		}
		if(ballistic.isSelected() && parametricfb.isSelected()){
			paramballpanel.setVisible(true);
		}
		if(internalfb.isSelected()){
				modelpanel.setVisible(true);
				directfbpanel.setVisible(true);
			if(parametricfb.isSelected()){
				internalfbpanel.setVisible(true);
			}
		}
		graphicpanel.repaint();
	}
	int modeltype(){
		if(ballistic.isSelected())return 0;
		if(directfb.isSelected())return 2;
		if(internalfb.isSelected())return 1;
		return -1;
	}
	int contype(){
		if(controlprop.isSelected())return 0;
		if(controlrate.isSelected())return 1;
		if(controlintegral.isSelected())return 2;
		return -1;
	}

  void changetype(ActionEvent e) {
	setupDiagram();
  }

	public void close() {
		timer.stop();
	}
}

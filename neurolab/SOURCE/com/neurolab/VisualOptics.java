
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
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class VisualOptics extends NeurolabExhibit {
 public String getExhibitName() {
		return "Visual Optics";
	}
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel3 = new JPanel0();
	JPanel jPanel4 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	JCheckBox bluered = new JCheckBox();
	JPanel jPanel5 = new JPanel0();
	BorderLayout borderLayout5 = new BorderLayout();
	ReturnButton returnButton1 = new ReturnButton();
	JButton demo = new JButton();
	JPanel jPanel6 = new JPanel0();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel7 = new JPanel0();
	JPanel jPanel8 = new JPanel0();
	Border border1;
	TitledBorder titledBorder1;
	JPanel jPanel9 = new JPanel0();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel10 = new JPanel0();
	JRadioButton dilated = new JRadioButton0();
	GridLayout gridLayout3 = new GridLayout();
	JRadioButton constricted = new JRadioButton0();
	JPanel jPanel11 = new JPanel0();
	GridLayout gridLayout4 = new GridLayout();
	JRadioButton presnone = new JRadioButton0();
	JRadioButton prespositive = new JRadioButton0();
	JRadioButton presnegative = new JRadioButton0();
	GridLayout gridLayout5 = new GridLayout();
	JRadioButton targinfinity = new JRadioButton0();
	JRadioButton targhalf = new JRadioButton0();
	JRadioButton targtenth = new JRadioButton0();
	GridLayout gridLayout6 = new GridLayout();
	JPanel jPanel12 = new JPanel0();
	JPanel jPanel13 = new JPanel0();
	GridLayout gridLayout7 = new GridLayout();
	JRadioButton emmetropia = new JRadioButton0();
	JRadioButton myopia = new JRadioButton0();
	JRadioButton hypermetropia = new JRadioButton0();
	JCheckBox presbyopia = new JCheckBox();
	JSlider accommodation = new JSlider();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel14 = new JPanel0();
	ButtonGroup bg1 = new ButtonGroup();
	JPanel jPanel15 = new JPanel0();
	BorderLayout borderLayout7 = new BorderLayout();
	BorderLayout borderLayout8 = new BorderLayout();
	BorderLayout borderLayout9 = new BorderLayout();
	Border border2;
	JPanel graph = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		int cx1=(int)(326*0.7);int cy1=getHeight()/2;
		int rad1=(int)(302*0.55/2);
		int cx2=(int)(326*0.55);int cy2=cy1;
		int rad2=(int)(rad1*0.6);
                System.out.print(getWidth()+","+getHeight());
		g.setColor(Color.gray);
		g.fillOval(cx1-rad1,cy1-rad1,rad1*2,rad1*2);
		g.fillOval(cx2-rad2,cy2-rad2,rad2*2,rad2*2);
		g.setColor(Color.red);
//		g.drawLine_();

		calc(g);

	}

	};
        int extraWidth=0;
	ButtonGroup bg2 = new ButtonGroup();
	ButtonGroup bg3 = new ButtonGroup();
	ButtonGroup bg4 = new ButtonGroup();


	public void calc(Graphics g){
		int distdio=0,rerr=0,accom,prescdio=0,radius,puptop,pupbot,cornx,targy=8,lensy=8,lensx=100,rety,retx,chrom;
		int pup=0,n,dist=0,presc=0,axis=graph.getHeight()/2;
		if(targinfinity.isSelected()){distdio=0;dist=0;}
		else if(targhalf.isSelected()){distdio=2;dist=1;}
		else if(targtenth.isSelected()){distdio=10;dist=2;}


		if(emmetropia.isSelected())rerr=0;
		else if(myopia.isSelected())rerr=5;
		else if(hypermetropia.isSelected())rerr=-5;

		if(!presbyopia.isSelected())accom=accommodation.getValue()/7+rerr;
		else accom=rerr;

		if(presnone.isSelected()){prescdio=0;presc=0;}
		else if(prespositive.isSelected()){prescdio=5;presc=1;}
		else if(presnegative.isSelected()){prescdio=-5;presc=2;}

		if(dilated.isSelected())pup=0;
		else if(constricted.isSelected())pup=1;

		radius=8;
		cornx=125;
		if(pup==0){radius=25;cornx=133;}
		puptop=axis-radius;
		pupbot=axis+radius;

		n = dist + 3 * presc + 9 * (1 - pup);
		switch(n){
			case 0:             //  ' Constricted
					targy = 8;
					lensy = 8;
				break;
			case 1:
					targy = 5;
					lensy = 7;
				break;
			case 2:
					targy = 4;
					lensy = 7;
				break;

			case 3:
					targy = 9;
					lensy = 9;
				break;
			case 4:
					targy = 7;
					lensy = 9;
				break;
			case 5:
					targy = 5;
					lensy = 7;
				break;

			case 6:
					targy = 6;
					lensy = 6;
				break;
			case 7:
					targy = 4;
					lensy = 6;
				break;
			case 8:
					targy = 3;
					lensy = 5;
				break;

			case 9 :          // ' Dilated
					targy = 25;
					lensy = 25;
				break;
			case 10:
					targy = 20;
					lensy = 24;
				break;
			case 11:
					targy = 15;
					lensy = 23;
				break;

			case 12:
					targy = 27;
					lensy = 27;
				break;
			case 13:
					targy = 23;
					lensy = 26;
				break;
			case 14:
					targy = 20;
					lensy = 27;
				break;

			case 15:
					targy = 23;
					lensy = 23;
				break;
			case 16:
					targy = 19;
					lensy = 23;
				break;
			case 17:
					targy = 12;
					lensy = 18;
				break;
		}
		rety = radius * (accom + prescdio - distdio) / 50;
//		retx = (int)(155 + Math.sqrt(80 * 80 + rety * rety));
		retx=300;
		chrom = radius / 10;


		int z=10;
		JPanel p=graph; //local shorthand
//		p.paintImmediately(p.getBounds());	//sgm

		antiAlias(g);
		setStrokeThickness(g,3);

                int pupx = 155 ;
		g.setColor(new Color(128,0,0));
		g.drawLine(pupx,axis-43,pupx,puptop);
		g.drawLine(pupx,axis+43,pupx,pupbot);

		g.setColor(Color.cyan);
		g.fillOval(171-(8+accom/2),axis-(30-accom/4),16+accom,60-accom/2);
		g.setColor(Color.white);
//		g.drawOval(176-(8+accom/2),axis-(30-accom/4),16+accom,60-accom/2);
		//REMOVED-- white line around lens


		g.setColor(Color.cyan);
		if(presc==1){	//convex
			g.drawArc(lensx+196-200,axis-200,400,400,180-z,2*z);
			g.drawArc(lensx-196-200,axis-200,400,400,-z,2*z);
//			g.drawArc(lensx-196-200,axis-200,400,400,0,z);
		}else if(presc==2){	//.concave
			g.drawArc(lensx+203-200,axis-200,400,400,180-z,2*z);
			g.drawArc(lensx-203-200,axis-200,400,400,-z,2*z);
//			g.drawArc(lensx-203-200,axis-200,400,400,0,z);
			g.drawLine(lensx-5,axis-35,lensx+5,axis-35);
			g.drawLine(lensx-5,axis+35,lensx+5,axis+35);
		}
		g.setColor(Color.white);
		setStrokeThickness(g,1);
		g.drawLine(8,axis-targy,lensx,axis-lensy);
		g.drawLine(8,axis+targy,lensx,axis+lensy);
		g.drawLine(lensx,axis-lensy,cornx,puptop);
		g.drawLine(lensx,axis+lensy,cornx,pupbot);

		if(bluered.isSelected()){
			g.setColor(Color.red);
			g.drawLine(cornx, puptop,retx, axis + rety - chrom);
			g.drawLine(cornx, pupbot,retx, axis - rety + chrom);
			g.setColor(Color.blue);
			g.drawLine(cornx, puptop,retx, axis + rety + chrom);
			g.drawLine(cornx, pupbot,retx, axis - rety - chrom);
		}else{
			g.drawLine(cornx, puptop,retx, axis + rety);
			g.drawLine (cornx, pupbot,retx, axis - rety);
		}
	}

	public VisualOptics() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
	bg1.add(emmetropia);bg1.add(myopia);bg1.add(hypermetropia);
	bg2.add(dilated);bg2.add(constricted);
	bg3.add(presnone);bg3.add(prespositive);bg3.add(presnegative);
	bg4.add(targinfinity);bg4.add(targhalf);bg4.add(targtenth);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void jbInit() throws Exception {
		accommodation.setValue(0);
		accommodation.setBackground(systemGray);
		presbyopia.setBackground(systemGray);
		bluered.setBackground(systemGray);
		demo.setBackground(systemGray);
		border1 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
		titledBorder1 = new TitledBorder(border1,"Target at");
		border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,new Color(255, 255, 235),new Color(94, 93, 80),new Color(135, 133, 115));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		jPanel3.setLayout(borderLayout3);
		jPanel4.setLayout(borderLayout4);
		bluered.setText("Show blue/red");
		bluered.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		jPanel5.setLayout(borderLayout5);
		demo.setText("Demo");
		demo.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				demo_actionPerformed(e);
			}
		});
		jPanel6.setLayout(gridLayout2);
		gridLayout2.setRows(2);
		gridLayout2.setColumns(1);
		jPanel7.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),"Prescription"));
		jPanel7.setLayout(gridLayout4);
		jPanel8.setBorder(titledBorder1);
		jPanel8.setLayout(gridLayout5);
		jPanel9.setLayout(borderLayout6);
		jPanel10.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),"Pupil"));
		jPanel10.setLayout(gridLayout3);
		dilated.setSelected(true);
		dilated.setText("Dilated");
		dilated.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		gridLayout3.setRows(2);
		gridLayout3.setColumns(1);
		constricted.setText("Constricted");
		constricted.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		jPanel11.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),"Refractive state"));
		jPanel11.setLayout(gridLayout6);
		gridLayout4.setRows(3);
		gridLayout4.setColumns(1);
		presnone.setSelected(true);
		presnone.setText("None");
		presnone.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		prespositive.setToolTipText("");
		prespositive.setText("Positive");
		prespositive.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		presnegative.setText("Negative");
		presnegative.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		gridLayout5.setRows(3);
		gridLayout5.setColumns(1);
		targinfinity.setSelected(true);
		targinfinity.setText("Infinity");
		targinfinity.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		targhalf.setText("0.5 m");
		targhalf.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		targtenth.setText("0.1 m");
		targtenth.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		gridLayout6.setRows(2);
		gridLayout6.setColumns(1);
		jPanel12.setLayout(gridLayout7);
		gridLayout7.setRows(3);
		gridLayout7.setColumns(1);
		emmetropia.setSelected(true);
		emmetropia.setText("Emmetropia");
		emmetropia.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		myopia.setText("Myopia");
		myopia.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		hypermetropia.setText("Hypermetropia");
		hypermetropia.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ra(e);
			}
		});
		jPanel13.setLayout(null);
		presbyopia.setText("Presbyopia");
		presbyopia.setBounds(new Rectangle(2, 8, 90, 25));
		presbyopia.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				presbyopia_actionPerformed(e);
			}
		});
		accommodation.setBorder(BorderFactory.createLineBorder(SystemColor.control,1));
		accommodation.setBounds(new Rectangle(1, 35, 105, 28));
		accommodation.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				ra(e);
			}
		});
		jLabel1.setText("Accommodation");
		jLabel1.setBounds(new Rectangle(7, 60, 95, 21));
		jPanel14.setLayout(borderLayout7);
		jPanel14.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		jPanel15.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(5,5,5,5)));
		jPanel15.setLayout(borderLayout9);
		graph.setLayout(borderLayout8);
		graph.setBackground(Color.black);
		graph.setBorder(border2);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel3, null);
		jPanel3.add(bluered, BorderLayout.SOUTH);
		jPanel3.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(jPanel10, BorderLayout.SOUTH);
		jPanel10.add(dilated, null);
		jPanel10.add(constricted, null);
		jPanel9.add(jPanel11, BorderLayout.CENTER);
		jPanel11.add(jPanel12, null);
		jPanel12.add(emmetropia, null);
		jPanel12.add(myopia, null);
		jPanel12.add(hypermetropia, null);
		jPanel11.add(jPanel13, null);
		jPanel13.add(presbyopia, null);
		jPanel13.add(accommodation, null);
		jPanel13.add(jLabel1, null);
		jPanel2.add(jPanel4, null);
		jPanel4.add(jPanel5, BorderLayout.SOUTH);
		jPanel5.add(returnButton1, BorderLayout.SOUTH);
		jPanel5.add(demo, BorderLayout.NORTH);
		jPanel4.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel7, null);
		jPanel7.add(presnone, null);
		jPanel7.add(prespositive, null);
		jPanel7.add(presnegative, null);
		jPanel6.add(jPanel8, null);
		jPanel8.add(targinfinity, null);
		jPanel8.add(targhalf, null);
		jPanel8.add(targtenth, null);
		jPanel1.add(jPanel14, BorderLayout.CENTER);
		jPanel14.add(jPanel15, BorderLayout.CENTER);
		jPanel15.add(graph, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	void presbyopia_actionPerformed(ActionEvent e) {
	if(presbyopia.isSelected()){
		accommodation.setVisible(false);
	}else{
		accommodation.setVisible(true);
	}
	ra(e);
	}

	void ra(ActionEvent e) {
	graph.repaint();
	}

	void ra(ChangeEvent e) {
	graph.repaint();
	}

	void demo_actionPerformed(ActionEvent e) {
		getHolder().setExhibit("com.neurolab.ChromaticAberration");
	}

}

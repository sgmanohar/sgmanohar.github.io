
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.neurolab.common.JPanel0;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;



public class ConductionVelocity extends NeurolabExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	JPanel jPanel4 = new JPanel0();
	JPanel jPanel5 = new JPanel0();
	JLabel jLabel1 = new JLabel();
	Border border1;
	JPanel jPanel6 = new JPanel0();
	BorderLayout borderLayout4 = new BorderLayout();
	Border border2;
	TitledBorder titledBorder1;
	JPanel jPanel7 = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel8 = new JPanel0();
	JPanel jPanel9 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel10 = new JPanel0();
	JPanel jPanel11 = new JPanel0();
	Border border3;
	TitledBorder titledBorder2;
	Border border4;
	JCheckBox myelinated = new JCheckBox();
	BorderLayout borderLayout5 = new BorderLayout();
	Border border5;
	Border border6;
	JTextPane veltext = new JTextPane();
	Border border7;
	JLabel jLabel2 = new JLabel();
	JPanel jPanel13 = new JPanel0();
	JTextPane diamtext = new JTextPane();
	BorderLayout borderLayout6 = new BorderLayout();
	JLabel jLabel3 = new JLabel();
	JPanel jPanel14 = new JPanel0();
	JTextPane myeltext = new JTextPane();
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel15 = new JPanel0();
	JTextPane rltext = new JTextPane();
	JLabel jLabel4 = new JLabel();
	JPanel jPanel16 = new JPanel0();
	JTextPane cmtext = new JTextPane();
	JLabel jLabel5 = new JLabel();
	GridLayout gridLayout3 = new GridLayout();
	JTextPane rmtext = new JTextPane();
	JLabel jLabel6 = new JLabel();
	JPanel jPanel17 = new JPanel0();
	FlowLayout flowLayout1 = new FlowLayout();
	FlowLayout flowLayout2 = new FlowLayout();
	FlowLayout flowLayout3 = new FlowLayout();
	JPanel jPanel18 = new JPanel0();
	FlowLayout flowLayout4 = new FlowLayout();
	JTextPane ttext = new JTextPane();
	JLabel jLabel7 = new JLabel();
	JPanel jPanel19 = new JPanel0();
	FlowLayout flowLayout5 = new FlowLayout();
	JTextPane ltext = new JTextPane();
	JLabel jLabel8 = new JLabel();
	GridLayout gridLayout4 = new GridLayout();
	Border border8;
	JPanel jPanel20 = new JPanel();
	GridLayout gridLayout5 = new GridLayout();
	JButton diamInc = new JButton();
	JButton diamDec = new JButton();
	JButton myelDec = new JButton();
	JButton myelInc = new JButton();
	GridLayout gridLayout6 = new GridLayout();
	JPanel jPanel21 = new JPanel();

	
	
	JPanel picture = new JPanel0(){
		public void paint(Graphics g){
			super.paint(g);
			antiAlias(g);
//			g.setColor(Color.yellow);
//			int r=(int)(middle*10);
//			g.fillOval(getWidth()/2-r, getHeight()/2-r, 2*r,2*r);
//			g.setColor(Color.black);
//			setStrokeThickness(g,(float)(th*19)+1);
//			g.drawOval(getWidth()/2-r, getHeight()/2-r, 2*r,2*r);
			Point centre=new Point(getWidth()/2, getHeight()/2);
			int outerR=(int)(diam*10/2), innerR=(int)(innr*10/2);
			// draw outer circle
			g.setColor(Color.black);
			g.fillOval(centre.x-outerR,centre.y-outerR,2*outerR,2*outerR);
			//draw inner circle
			g.setColor(Color.yellow);
	    g.fillOval(centre.x-innerR,centre.y-innerR,2*innerR,2*innerR);
		}
	};


	public String getExhibitName() {
		return "Conduction velocity";
	}

	public ConductionVelocity() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
			catch(Exception e) {
			e.printStackTrace();
		}
		Icon iinc=new ImageIcon(getImage("resources/bitmaps/UpTriangle.GIF"));
		Icon idec=new ImageIcon(getImage("resources/bitmaps/DownTriangle.GIF"));
		diamInc.setIcon(iinc);
		diamDec.setIcon(idec);
		myelInc.setIcon(iinc);
		myelDec.setIcon(idec);

		reset();
	}

	private void jbInit() throws Exception {
	  veltext.setContentType("text/html");
	  diamtext.setContentType("text/html");
	  myeltext.setContentType("text/html");
	  rltext.setContentType("text/html");
	  cmtext.setContentType("text/html");
	  rmtext.setContentType("text/html");
	  ttext.setContentType("text/html");
	  ltext.setContentType("text/html");

		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(3,3,3,3));
		border2 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
		titledBorder1 = new TitledBorder(border2,"Design parameters");
		border3 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
		titledBorder2 = new TitledBorder(border3,"Myelin");
		border4 = BorderFactory.createCompoundBorder(titledBorder2,BorderFactory.createEmptyBorder(2,2,2,2));
		border5 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(1,1,1,1));
		border6 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(1,1,1,1));
		border7 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.lightGray,Color.gray,Color.darkGray),BorderFactory.createEmptyBorder(2,2,2,2));
		border8 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(3,3,3,3));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setLabelFor(veltext);
		jLabel1.setText("Velocity");
		jPanel3.setBorder(border1);
		jPanel3.setLayout(borderLayout5);
		jPanel4.setLayout(borderLayout4);
		jPanel6.setBorder(titledBorder1);
		jPanel6.setLayout(gridLayout2);
		jPanel7.setLayout(gridLayout1);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(1);
		gridLayout2.setRows(2);
		gridLayout2.setColumns(1);
		jPanel10.setBorder(border4);
		jPanel10.setPreferredSize(new Dimension(130, 90));
		myelinated.setBackground(systemGray);
		myelinated.setText("Myelinated");
		myelinated.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				changeMyel(e);
			}
		});
		picture.setBackground(new Color(128, 255, 255));
		picture.setBorder(border5);
		veltext.setBackground(Color.black);
		veltext.setFont(new java.awt.Font("SansSerif", 1, 20));
		veltext.setForeground(Color.yellow);
		veltext.setBorder(border7);
		veltext.setPreferredSize(new Dimension(120, 35));
		veltext.setCaretColor(Color.yellow);
		veltext.setEditable(false);
		veltext.setText("10.8 m/sec");
		jLabel2.setLabelFor(jPanel13);
		jLabel2.setText("External diameter");
		diamtext.setCaretColor(Color.yellow);
		diamtext.setBorder(border7);
		diamtext.setPreferredSize(new Dimension(80, 30));
		diamtext.setForeground(Color.green);
		diamtext.setFont(new java.awt.Font("SansSerif", 1, 16));
		diamtext.setBackground(Color.black);
		jPanel13.setLayout(borderLayout6);
		jPanel11.setPreferredSize(new Dimension(130, 58));
		jLabel3.setLabelFor(jPanel14);
		jLabel3.setText("Thickness");
		jPanel14.setLayout(borderLayout7);
		myeltext.setCaretColor(Color.yellow);
		myeltext.setBorder(border7);
		myeltext.setPreferredSize(new Dimension(80, 30));
		myeltext.setForeground(Color.green);
		myeltext.setFont(new java.awt.Font("SansSerif", 1, 16));
		myeltext.setBackground(Color.black);
		rltext.setBackground(Color.black);
		rltext.setFont(new java.awt.Font("SansSerif", 1, 12));
		rltext.setForeground(Color.cyan);
		rltext.setBorder(border7);
		rltext.setCaretColor(Color.yellow);
		rltext.setEditable(false);
		rltext.setPreferredSize(new Dimension(110, 30));
		jLabel4.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel4.setText("<HTML>R<sub>L");
		cmtext.setBackground(Color.black);
		cmtext.setFont(new java.awt.Font("SansSerif", 1, 12));
		cmtext.setForeground(Color.cyan);
		cmtext.setBorder(border7);
		cmtext.setCaretColor(Color.yellow);
		cmtext.setEditable(false);
		cmtext.setPreferredSize(new Dimension(110, 30));
		jLabel5.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel5.setText("<HTML>C<sub>M");
		jPanel8.setPreferredSize(new Dimension(150, 60));
		jPanel8.setLayout(gridLayout3);
		gridLayout3.setRows(3);
		gridLayout3.setColumns(1);
		rmtext.setBackground(Color.black);
		rmtext.setFont(new java.awt.Font("SansSerif", 1, 12));
		rmtext.setForeground(Color.cyan);
		rmtext.setBorder(border7);
		rmtext.setCaretColor(Color.yellow);
		rmtext.setEditable(false);
		rmtext.setPreferredSize(new Dimension(110, 30));
		jLabel6.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel6.setText("<HTML>R<sub>M</sub>");
		jPanel16.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		jPanel17.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.RIGHT);
		jPanel15.setLayout(flowLayout3);
		flowLayout3.setAlignment(FlowLayout.RIGHT);
		jPanel18.setLayout(flowLayout4);
		flowLayout4.setAlignment(FlowLayout.RIGHT);
		ttext.setBackground(Color.black);
		ttext.setFont(new java.awt.Font("SansSerif", 1, 16));
		ttext.setForeground(Color.cyan);
		ttext.setBorder(border7);
		ttext.setCaretColor(Color.yellow);
		ttext.setEditable(false);
		ttext.setPreferredSize(new Dimension(110, 30));
		jLabel7.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel7.setText("<HTML><font face='Times New Roman'><I>&#964;"); // tau
		jPanel19.setLayout(flowLayout5);
		flowLayout5.setAlignment(FlowLayout.RIGHT);
		ltext.setBackground(Color.black);
		ltext.setFont(new java.awt.Font("SansSerif", 1, 16));
		ltext.setForeground(Color.cyan);
		ltext.setBorder(border7);
		ltext.setCaretColor(Color.yellow);
		ltext.setEditable(false);
		ltext.setPreferredSize(new Dimension(110, 30));
		jLabel8.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel8.setText("<HTML><font face='Times New Roman'><I>&#955;"); // lambda
		jPanel9.setPreferredSize(new Dimension(111, 50));
		jPanel9.setLayout(gridLayout4);
		gridLayout4.setRows(3);
		jPanel5.setBorder(border8);
		borderLayout5.setHgap(5);
		borderLayout5.setVgap(5);
		borderLayout3.setHgap(5);
		borderLayout3.setVgap(5);
		borderLayout4.setHgap(5);
		borderLayout4.setVgap(5);
		borderLayout2.setHgap(5);
		borderLayout2.setVgap(5);
		jPanel20.setLayout(gridLayout5);
		diamInc.setMargin(new Insets(0, 0, 0, 0));
		diamInc.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				diamInc_actionPerformed(e);
			}
		});
		diamDec.setMargin(new Insets(0, 0, 0, 0));
		diamDec.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				diamDec_actionPerformed(e);
			}
		});
		gridLayout5.setRows(2);
		myelDec.setMargin(new Insets(0, 0, 0, 0));
		myelDec.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				myelDec_actionPerformed(e);
			}
		});
		myelInc.setMargin(new Insets(0, 0, 0, 0));
		myelInc.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				myelInc_actionPerformed(e);
			}
		});
		gridLayout6.setRows(2);
		jPanel21.setLayout(gridLayout6);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(picture, BorderLayout.CENTER);
		jPanel2.add(jPanel5, BorderLayout.SOUTH);
		jPanel5.add(jLabel1, null);
		jPanel5.add(veltext, null);
		jPanel1.add(jPanel4, BorderLayout.EAST);
		jPanel4.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel11, null);
		jPanel11.add(jLabel2, null);
		jPanel11.add(jPanel13, null);
		jPanel13.add(diamtext, BorderLayout.CENTER);
		jPanel13.add(jPanel20, BorderLayout.EAST);
		jPanel20.add(diamInc, null);
		jPanel20.add(diamDec, null);
		jPanel6.add(jPanel10, null);
		jPanel10.add(myelinated, null);
		jPanel10.add(jLabel3, null);
		jPanel10.add(jPanel14, null);
		jPanel14.add(myeltext, BorderLayout.CENTER);
		jPanel14.add(jPanel21, BorderLayout.EAST);
		jPanel21.add(myelInc, null);
		jPanel21.add(myelDec, null);
		jPanel4.add(jPanel7, BorderLayout.EAST);
		jPanel7.add(jPanel8, null);
		jPanel8.add(jPanel16, null);
		jPanel16.add(jLabel5, null);
		jPanel16.add(cmtext, null);
		jPanel8.add(jPanel17, null);
		jPanel17.add(jLabel6, null);
		jPanel17.add(rmtext, null);
		jPanel8.add(jPanel15, null);
		jPanel15.add(jLabel4, null);
		jPanel15.add(rltext, null);
		jPanel7.add(jPanel9, null);
		jPanel9.add(jPanel19, null);
		jPanel19.add(jLabel8, null);
		jPanel19.add(ltext, null);
		jPanel9.add(jPanel18, null);
		jPanel18.add(jLabel7, null);
		jPanel18.add(ttext, null);
		jPanel9.add(returnButton1, null);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}


	double p,q,sa,csa,middle,innr, cm,rm,rl, tau,lambda, v, th, diam;
	void calc(){
		if(myelinated.isSelected()){
			innr = diam - 2 * th;
			middle = (innr + diam) / 2;

			p = 0.625 * th;
			q = 0.25 * th;
		}else{
			innr=middle=diam;
			th=0;
			p = 0.05;
			q = 0.02;
		}
		sa = Math.PI * middle * 0.0001;
		csa = innr * innr * Math.PI * 0.00000001 / 4;

		cm = 0.006 * sa / p;
		rm = q * 200000 / (2 * sa);
		rl = 110 / csa;


		tau = cm * rm / 1000;
		lambda = 10 * Math.sqrt(rm / rl);

		v = 10 * lambda / (tau + 0.2);

		/**
		 update text and graphics
		*/
		String yellow = "#ffff00", green = "#00ff00";
		cmtext.setText( cap(1000000 * cm, " pF/cm", yellow) );
		rmtext.setText( cap(rm / 1000000, " Mohm.cm", yellow ) );
		rltext.setText( cap(rl / 1000000, " Mohm/cm",yellow ) );
		ltext.setText( cap(lambda, " mm", yellow) );
		ttext.setText( cap(tau, " msec", yellow) );
		veltext.setText( cap(v, " m/sec", yellow) );
		diamtext.setText( cap(diam," &#956;", green) );
		myeltext.setText( cap(th," &#956;", green) );
		picture.repaint();
	}

	String cap(double val, String unit, String colour){
		//truncate to 3sf
		String d=String.valueOf(val);
		int p=d.indexOf('.');
		if(p>=0){
			if(p>3)d=d.substring(0,p);
			else {
				int last;
				if(d.length()>4)last=4;else last=d.length();
				d=d.substring(0,last);
				while(d.endsWith("0"))d=d.substring(0,d.length()-1);
				if(d.endsWith("."))d=d.substring(0,d.length()-1);
			}
		}
		if(d.length()==0)d="0";
		return "<HTML><body bgcolor='black'><FONT FACE='arial' COLOR=\""+colour+"\"><B>" + d  + unit +"</body></HTML>";
	}

	void change(ActionEvent e) {
		calc();
	}

	void diamInc_actionPerformed(ActionEvent e) {
		if(diam < 1) diam += 0.2; else diam = (int)(diam + 1);
		if(diam > 20) diam = 20;
		calc();
	}

	void diamDec_actionPerformed(ActionEvent e) {
		if(diam < 2) diam -= 0.2; else diam = (int)(diam - 1);
		if(diam < 0.1) diam = 0.1;
		if(diam < 2 * th + 0.1) th -= 0.1;
		if(th < 0.1) th = 0.1;
		if(diam < 2 * th + 0.1) diam = 2 * th + 0.1;
		calc();
	}

	void myelInc_actionPerformed(ActionEvent e) {
		if(2 * th < diam - 0.2) th += 0.1;
		calc();
	}

	void myelDec_actionPerformed(ActionEvent e) {
		if(th >= 0.2) th -= 0.1;
		calc();
	}
	void reset(){
		diam=5; th=0.5;
		calc();
	}

	double oth=0.5;
	void changeMyel(ActionEvent e) {
		if(myelinated.isSelected())th=oth;
		else {oth=th; th=0;}
		change(e);
	}

  public void close(){
  }
}

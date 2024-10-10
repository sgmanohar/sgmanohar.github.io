
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

import javax.swing.*;
import java.awt.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import java.awt.event.*;

public class PavlovianConditioning extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	PercentageBar pStrength = new PercentageBar();
	PercentageBar pResponse = new PercentageBar();
	JButton forget = new JButton();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel3 = new JPanel0();
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	Label3D label3D4 = new Label3D();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	Border border1;
	JLabel jLabel3 = new JLabel();
	JSlider decayslider = new JSlider();
	JLabel jLabel4 = new JLabel();
	Border border2;
	JPanel picpanel = new JPanel0();
	GraphicComponent graphicComponent1 = new GraphicComponent();
	GraphicComponent graphicComponent2 = new GraphicComponent();
	GraphicComponent graphicComponent3 = new GraphicComponent();
	GraphicComponent graphicComponent4 = new GraphicComponent();
	GraphicComponent graphicComponent5 = new GraphicComponent();
	GraphicComponent graphicComponent6 = new GraphicComponent();
	GraphicComponent graphicComponent7 = new GraphicComponent();
	JButton food = new JButton();
	JButton bell = new JButton();
	GraphicComponent graphicComponent8 = new GraphicComponent();

	public String getExhibitName() {
		return "Conditioning";
	}

	public PavlovianConditioning() {
	}

        public void init() {
          super.init();
          try {
            jbInit();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
          arrowRt = new ImageIcon(getImage("resources/bitmaps/arrow_right.gif"));
          clearValues();
          timer.start();
        }


	double r,s,u,c;
	public void clearValues(){r=s=u=c=0;}

	Timer timer=new Timer(200,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			s= s*(1 - decayslider.getValue()/1000.) + (1694 - s)*0.2*u*c - s*0.05*c;
			u*=0.8;
			c*=0.8;
			r*=0.8;
			percentages();
		}
	});

	void percentages(){
		pResponse.setValue(hundred((int)(r/16.95)));
		pStrength.setValue(hundred((int)(s/16.95)));
	}
	int hundred(int a){return a<0?0:(a>100?100:a);}
        ImageIcon arrowRt;

	private void jbInit() throws Exception {
		forget.setBackground(systemGray);
		decayslider.setBackground(systemGray);
		border1 = BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80));
		border2 = BorderFactory.createLineBorder(SystemColor.control,1);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(null);
		pStrength.setBounds(new Rectangle(161, 211, 167, 20));
		pResponse.setBounds(new Rectangle(426, 91, 131, 19));
		forget.setText("Forget");
		forget.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				forget_actionPerformed(e);
			}
		});
		label3D1.setFont(new java.awt.Font("Dialog", 1, 18));
		label3D1.setText("Response");
		label3D1.setBounds(new Rectangle(435, 113, 123, 23));
		label3D2.setBounds(new Rectangle(148, 236, 207, 23));
		label3D2.setText("Synaptic Strength");
		label3D2.setFont(new java.awt.Font("Dialog", 1, 18));
		label3D3.setFont(new java.awt.Font("Dialog", 1, 18));
		label3D3.setText("UCS");
		label3D3.setBounds(new Rectangle(67, 19, 58, 23));
		label3D4.setBounds(new Rectangle(75, 112, 42, 23));
		label3D4.setText("CS");
		label3D4.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel1.setIcon(arrowRt);
		jLabel1.setBounds(new Rectangle(115, 47, 35, 33));
		jLabel2.setBounds(new Rectangle(114, 138, 33, 33));
		jLabel2.setIcon(arrowRt);
		picpanel.setBorder(border1);
		picpanel.setBounds(new Rectangle(121, 30, 295, 158));
		picpanel.setLayout(null);
		jLabel3.setBounds(new Rectangle(375, 95, 41, 33));
		jLabel3.setIcon(arrowRt);
		decayslider.setValue(0);
		decayslider.setBorder(border2);
		decayslider.setBounds(new Rectangle(482, 213, 64, 24));
		jLabel4.setText("Decay rate");
		jLabel4.setBounds(new Rectangle(483, 237, 68, 17));
		graphicComponent1.setForeground(Color.red);
		graphicComponent1.setType(2);
		graphicComponent1.setBounds(new Rectangle(3, 25, 145, 16));
		graphicComponent2.setForeground(Color.green);
		graphicComponent2.setType(4);
		graphicComponent2.setBounds(new Rectangle(174, 44, 77, 74));
		graphicComponent3.setBounds(new Rectangle(3, 116, 143, 16));
		graphicComponent3.setForeground(Color.blue);
		graphicComponent3.setType(2);
		graphicComponent4.setForeground(Color.blue);
		graphicComponent4.setBounds(new Rectangle(141, 104, 28, 23));
		graphicComponent5.setForeground(Color.blue);
		graphicComponent5.setType(4);
		graphicComponent5.setBounds(new Rectangle(167, 96, 11, 12));
		graphicComponent6.setForeground(Color.red);
		graphicComponent6.setDirection(true);
		graphicComponent6.setBounds(new Rectangle(144, 31, 28, 23));
		graphicComponent7.setBounds(new Rectangle(169, 52, 11, 12));
		graphicComponent7.setType(4);
		graphicComponent7.setForeground(Color.red);
		food.setBackground(Color.red);
		food.setForeground(Color.lightGray);
		food.setIcon(new ImageIcon(getImage("resources/bitmaps/Bone.gif")));
		food.setBounds(new Rectangle(67, 40, 47, 46));
		food.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				food_actionPerformed(e);
			}
		});
		bell.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				bell_actionPerformed(e);
			}
		});
		bell.setBounds(new Rectangle(67, 132, 47, 46));
		bell.setIcon(new ImageIcon(getImage("resources/bitmaps/Bell.gif")));
		bell.setBackground(Color.blue);
		bell.setForeground(Color.lightGray);
		graphicComponent8.setType(2);
		graphicComponent8.setBounds(new Rectangle(251, 73, 45, 16));
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(label3D1, null);
		jPanel2.add(pResponse, null);
		jPanel2.add(jLabel3, null);
		jPanel2.add(label3D2, null);
		jPanel2.add(pStrength, null);
		jPanel2.add(jLabel4, null);
		jPanel2.add(decayslider, null);
		jPanel2.add(jLabel1, null);
		jPanel2.add(jLabel2, null);
		jPanel2.add(food, null);
		jPanel2.add(bell, null);
		jPanel2.add(picpanel, null);
		picpanel.add(graphicComponent1, null);
		picpanel.add(graphicComponent3, null);
		picpanel.add(graphicComponent8, null);
		picpanel.add(graphicComponent5, null);
		picpanel.add(graphicComponent4, null);
		picpanel.add(graphicComponent6, null);
		picpanel.add(graphicComponent7, null);
		picpanel.add(graphicComponent2, null);
		jPanel2.add(label3D3, null);
		jPanel2.add(label3D4, null);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(forget, null);
		jPanel3.add(returnButton1, null);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	void food_actionPerformed(ActionEvent e) {
	u = 1;
	r = r + 0.25 * (1694 - r);
	}

	void bell_actionPerformed(ActionEvent e) {
	c = 1;
	r = r + 0.25 * (1694 - r) * s / 1695;
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}

	void forget_actionPerformed(ActionEvent e) {
	clearValues();
	}

}


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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

public class BlindSpot extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel0();
	Border border1;
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	Border border2;
	BorderLayout borderLayout5 = new BorderLayout();
	Border border3;
	Border border4;
	Border border5;
	Border border6;
	JTextPane comment = new JTextPane();
	JButton clearbutton = new JButton();
	JRadioButton leye = new JRadioButton0();
	JRadioButton reye = new JRadioButton0();
	JCheckBox patternbutton = new JCheckBox();
	JPanel jPanel9 = new JPanel0();
	JPanel jPanel8 = new JPanel0();
	JPanel jPanel7 = new JPanel0();
	JPanel jPanel5 = new JPanel0();
	JPanel jPanel4 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel3 = new JPanel0();
	GridLayout gridLayout3 = new GridLayout();
	GridLayout gridLayout2 = new GridLayout();
	GridLayout gridLayout1 = new GridLayout();
	BorderLayout borderLayout6 = new BorderLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel heart = new JLabel();
	JLabel face = new JLabel();
	ButtonGroup bg1 = new ButtonGroup();



	JPanel mainpanel = new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			if(patternbutton.isSelected()){
				g.setColor(Color.green);
				for(int i=0;i<10;i++)
					g.fillRect(0,10+i*32,getWidth(),10);
				for(int i=0;i<20;i++)
					g.fillRect(20+i*32,0,10,getHeight());
			}
			g.setColor(Color.red);
			g.fillOval(reye.isSelected()?40:getWidth()-40,YCOORD_FIX,8,8);
		}

	};
	int YCOORD_FIX = 100;

	static final String commentString="Using one eye only, fixate the red spot, and move the mouse in the black field. When the cursor falls in the blind spot, it will disappear: click with the mouse to mark its boundary. Later, drag the icons into the blind spot to see how they look, or call up the background pattern.";

	public String getExhibitName() {
		return "Blind spot";
	}
	public BlindSpot() {
	}

	public void init(){
	super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	bg1.add(reye);bg1.add(leye);
	comment.setText(commentString);
	mainpanel.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			Graphics g=(Graphics)mainpanel.getGraphics();
			g.setColor(Color.white);
			g.fillRect(e.getX(),e.getY(),4,4);
		}
	});
	}

	private void jbInit() throws Exception {
		patternbutton.setBackground(systemGray);
		clearbutton.setBackground(systemGray);
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(5,5,5,5));
		border2 = BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(178, 178, 178),new Color(124, 124, 124)),BorderFactory.createEmptyBorder(3,3,3,3));
		border4 = BorderFactory.createEmptyBorder(3,3,3,3);
		border5 = BorderFactory.createEmptyBorder(4,0,0,0);
		border6 = BorderFactory.createEmptyBorder(3,0,0,0);
		jPanel1.setBorder(border1);
		jPanel1.setLayout(borderLayout5);
		jPanel2.setLayout(borderLayout3);
		mainpanel.setBackground(Color.black);
		comment.setText("jTextPane1");
		comment.setBorder(border3);
		comment.setBorder(border3);
		clearbutton.setText("Clear");
		clearbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clearbutton_actionPerformed(e);
			}
		});
		leye.setText("Use left eye");
		leye.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				leye_actionPerformed(e);
			}
		});
		reye.setSelected(true);
		reye.setText("Use right eye");
		reye.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				reye_actionPerformed(e);
			}
		});
		patternbutton.setText("Pattern");
		patternbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				patternbutton_actionPerformed(e);
			}
		});
		jPanel9.setLayout(gridLayout3);
		jPanel8.setLayout(gridLayout2);
		jPanel8.setBorder(border4);
		jPanel7.setLayout(borderLayout6);
		jPanel5.setBorder(border2);
		jPanel5.setLayout(gridLayout1);
		jPanel4.setLayout(borderLayout4);
		jPanel3.setLayout(borderLayout2);
		jPanel3.setBorder(border6);
		gridLayout3.setRows(2);
		gridLayout3.setColumns(1);
		gridLayout2.setRows(2);
		gridLayout2.setColumns(1);
		gridLayout2.setVgap(3);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(1);
		heart.setIcon(new ImageIcon(getImage( "resources/bitmaps/Heart.gif")));
		heart.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				heart_mouseReleased(e);
			}
		});
		heart.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {
				heart_mouseDragged(e);
			}
		});
		face.setIcon(new ImageIcon(getImage("resources/bitmaps/HappyFace.gif")));
		face.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				face_mouseReleased(e);
			}
		});
		face.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {
				face_mouseDragged(e);
			}
		});
		jPanel2.add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(mainpanel, BorderLayout.CENTER);
		jPanel2.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jPanel4, BorderLayout.WEST);
		jPanel4.add(patternbutton, BorderLayout.SOUTH);
		jPanel4.add(jPanel5, BorderLayout.NORTH);
		jPanel5.add(reye, null);
		jPanel5.add(leye, null);
		jPanel3.add(comment, BorderLayout.CENTER);
		jPanel3.add(jPanel7, BorderLayout.EAST);
		jPanel7.add(jPanel8, BorderLayout.EAST);
		jPanel8.add(clearbutton, null);
		jPanel8.add(returnButton1, null);
		jPanel7.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(face, null);
		jPanel9.add(heart, null);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel2, BorderLayout.CENTER);
	}

	void reye_actionPerformed(ActionEvent e) {
	mainpanel.repaint();
	}

	void leye_actionPerformed(ActionEvent e) {
	mainpanel.repaint();
	}

	void patternbutton_actionPerformed(ActionEvent e) {
	mainpanel.repaint();
	}

	void clearbutton_actionPerformed(ActionEvent e) {
	mainpanel.repaint();
	}

	JLabel dropsrc;
	void heart_mouseDragged(MouseEvent e) {
	dropsrc=heart;
	}
	void face_mouseDragged(MouseEvent e) {
	dropsrc=face;
	}
	void face_mouseReleased(MouseEvent e) {
	dropicon(e.getPoint());
	}
	void heart_mouseReleased(MouseEvent e) {
	dropicon(e.getPoint());
	}

	void dropicon(Point p) {
	if(dropsrc!=null){
		Point scpos=new Point(dropsrc.getLocationOnScreen().x+p.x,dropsrc.getLocationOnScreen().y+p.y);
		if(new Rectangle(mainpanel.getLocationOnScreen(),mainpanel.getSize()).contains(scpos)){
			Graphics g=(Graphics)mainpanel.getGraphics();
			dropsrc.getIcon().paintIcon(mainpanel,g,scpos.x-mainpanel.getLocationOnScreen().x-dropsrc.getWidth()/2,scpos.y-mainpanel.getLocationOnScreen().y-dropsrc.getHeight()/2);
		}
		dropsrc=null;
	}
	}
public void close() {}
}
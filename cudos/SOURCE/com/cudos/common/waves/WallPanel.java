
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */

package com.cudos.common.waves;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class WallPanel extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JRadioButton isRect = new JRadioButton();
  JRadioButton isParab = new JRadioButton();
  ButtonGroup bg = new ButtonGroup();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel4 = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  JSlider widthSlider = new JSlider();
  JRadioButton isLine = new JRadioButton();
  JSlider heightSlider = new JSlider();

  public WallPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	bg.add(isParab);bg.add(isRect);bg.add(isLine);bg.add(isStep);
  }

	RippleWall rw;
  JRadioButton isStep = new JRadioButton();
	public void setWall(RippleWall r){
		rw=r;
		widthSlider.setValue(rw.getWidth()/2);
		heightSlider.setValue(rw.getHeight()/2);
		if(rw.getType()==RippleWall.TYPE_RECTANGLE)isRect.setSelected(true);
		else if(rw.getType()==RippleWall.TYPE_LINE)isLine.setSelected(true);
		else if(rw.getType()==RippleWall.TYPE_PARABOLA)isParab.setSelected(true);
		else if(rw.getType()==RippleWall.TYPE_RECTSTEP)isStep.setSelected(true);
	}




  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    isRect.setSelected(true);
    isRect.setText("Rectangle");
    isRect.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isRect_actionPerformed(e);
      }
    });
    isParab.setText("Parabola");
    isParab.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isParab_actionPerformed(e);
      }
    });
    jPanel2.setLayout(borderLayout2);
    jLabel1.setText("Width");
    jPanel3.setLayout(gridLayout1);
    gridLayout1.setRows(2);
    jLabel2.setText("Height");
    jPanel4.setLayout(gridLayout2);
    gridLayout2.setRows(2);
    widthSlider.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        widthSlider_stateChanged(e);
      }
    });
    heightSlider.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        heightSlider_stateChanged(e);
      }
    });
    jPanel1.setPreferredSize(new Dimension(171, 90));
    jPanel1.setToolTipText("");
    isLine.setText("Line");
    isLine.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isLine_actionPerformed(e);
      }
    });
    isStep.setText("Step");
    isStep.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isStep_actionPerformed(e);
      }
    });
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(isRect, null);
    jPanel1.add(isParab, null);
    jPanel1.add(isLine, null);
    jPanel1.add(isStep, null);
    this.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel3, BorderLayout.WEST);
    jPanel3.add(jLabel1, null);
    jPanel3.add(jLabel2, null);
    jPanel2.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(widthSlider, null);
    jPanel4.add(heightSlider, null);
  }

  void widthSlider_stateChanged(ChangeEvent e) {
	rw.setSize(widthSlider.getValue()*2,rw.getHeight());
  }

  void heightSlider_stateChanged(ChangeEvent e) {
	rw.setSize(rw.getWidth(),heightSlider.getValue()*2);
  }

  void isRect_actionPerformed(ActionEvent e) {
	rw.setType(RippleWall.TYPE_RECTANGLE);
	setpos();
  }
	public void setpos(){
		rw.setSize(widthSlider.getValue()*2,heightSlider.getValue()*2);
		rw.setLocation(rw.getX(),rw.getY());
	}
  void isParab_actionPerformed(ActionEvent e) {
	rw.setType(RippleWall.TYPE_PARABOLA);
	setpos();
  }

  void isLine_actionPerformed(ActionEvent e) {
	rw.setType(RippleWall.TYPE_LINE);
	setpos();
  }

  void isStep_actionPerformed(ActionEvent e) {
	rw.setType(RippleWall.TYPE_RECTSTEP);
	setpos();
  }

}
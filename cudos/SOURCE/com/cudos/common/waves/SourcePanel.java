
/**
 * Title:        CUDOS Project<p>
 * Description:  Project led by
 * Roger Carpenter,
 * Department of Physiology,
 * University of Cambridge<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.waves;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class SourcePanel extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JRadioButton isPoint = new JRadioButton();
  JRadioButton isLine = new JRadioButton();
  JPanel jPanel2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JSlider freqSlider = new JSlider();
  JSlider amplSlider = new JSlider();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  ButtonGroup bg=new ButtonGroup();

	RippleSource src;
  JPanel jPanel3 = new JPanel();
  GridLayout gridLayout2 = new GridLayout();

  public SourcePanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	bg.add(isPoint);bg.add(isLine);
  }

	public void setSource(RippleSource rs){
		src=rs;
		if(src.getType()==RippleSource.TYPE_POINT)isPoint.setSelected(true);else isLine.setSelected(true);
		freqSlider.setValue((int)(src.getFrequency()*400));
		amplSlider.setValue((int)(src.getAmplitude()*70));
	}
	public RippleSource getSource(){return src;}

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    isPoint.setSelected(true);
    isPoint.setText("Point");
    isPoint.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isPoint_actionPerformed(e);
      }
    });
    isLine.setText("Line");
    isLine.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        isLine_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    gridLayout1.setRows(2);
    jLabel1.setText("Frequency");
    jLabel2.setText("Amplitude");
    freqSlider.setMajorTickSpacing(50);
    freqSlider.setPaintTicks(true);
    freqSlider.setMinorTickSpacing(10);
    freqSlider.setPaintLabels(true);
    freqSlider.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        freqSlider_stateChanged(e);
      }
    });
    amplSlider.setMajorTickSpacing(50);
    amplSlider.setPaintTicks(true);
    amplSlider.setMinorTickSpacing(10);
    amplSlider.setPaintLabels(true);
    amplSlider.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        amplSlider_stateChanged(e);
      }
    });
    jPanel3.setLayout(gridLayout2);
    gridLayout2.setRows(2);
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(isPoint, null);
    jPanel1.add(isLine, null);
    this.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(amplSlider, null);
    jPanel2.add(freqSlider, null);
    this.add(jPanel3, BorderLayout.WEST);
    jPanel3.add(jLabel2, null);
    jPanel3.add(jLabel1, null);
  }

  void freqSlider_stateChanged(ChangeEvent e) {
	src.setFrequency(freqSlider.getValue()/400.);
  }
  void amplSlider_stateChanged(ChangeEvent e) {
	src.setAmplitude(amplSlider.getValue()/70.);
  }

  void isPoint_actionPerformed(ActionEvent e) {
	src.setType(RippleSource.TYPE_POINT);
  }

  void isLine_actionPerformed(ActionEvent e) {
	src.setType(RippleSource.TYPE_LINE);
  }
}
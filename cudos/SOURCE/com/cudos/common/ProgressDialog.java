
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import javax.swing.*;
import java.awt.*;


public class ProgressDialog extends JDialog {
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JLabel progressString = new JLabel();
  public JProgressBar progressBar = new JProgressBar();

  public ProgressDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	setBounds(10,10,350,120);
  }
	public ProgressDialog(String title,String s){
		this();
		progressString.setText(s);
		this.setTitle(title);
	}
	public ProgressDialog(String s){
		this(s,s);
	}
	public void increment(){
		JProgressBar p=progressBar;
		p.setValue((p.getValue()+1)%p.getMaximum());
	}
  private void jbInit() throws Exception {
    this.getContentPane().setLayout(flowLayout1);
    progressString.setText("jLabel1");
    progressBar.setToolTipText("");
    this.setTitle("Please wait...");
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(progressString, null);
    jPanel1.add(progressBar, null);
  }
}
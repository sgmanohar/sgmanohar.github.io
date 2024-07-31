
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;

import medicine.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;


public class AboutBox extends JDialog {
  JButton jButton1 = new JButton();
  JPanel jPanel1 = new JPanel();
  JTextPane jTextPane1 = new JTextPane();
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();
  Border border2;

  public AboutBox() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	setSize(250,250);
        MainApplication.centreWindow(this);
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(248, 240, 255),Color.white,new Color(121, 117, 151),new Color(84, 81, 105)),BorderFactory.createEmptyBorder(2,2,2,2));
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(248, 240, 255),Color.white,new Color(121, 117, 151),new Color(84, 81, 105)),BorderFactory.createEmptyBorder(3,3,3,3));
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTextPane1.setPreferredSize(new Dimension(150, 80));
    jTextPane1.setBackground(SystemColor.control);
    jTextPane1.setBorder(border1);
    jTextPane1.setText("Hierarchical medical information browser, (C) Sanjay Manohar 2001-2005");
    jTextPane1.setFont(new java.awt.Font("Dialog", 3, 18));
    this.setTitle("About");
    this.setModal(true);
    this.setResizable(false);
    jPanel1.setLayout(borderLayout1);
    jPanel1.setBorder(border2);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jTextPane1, BorderLayout.CENTER);
    this.getContentPane().add(jButton1, BorderLayout.SOUTH);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	hide();
  }
}

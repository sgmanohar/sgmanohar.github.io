
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


public class SynonymInputDialog extends JDialog {
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel synonymfor = new JLabel();
  JTextField synonymtext = new JTextField();
  Box box1;
  JButton cancel = new JButton();
  JButton OK = new JButton();
  Component component1;

  public SynonymInputDialog(Entity ent) {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	setTitle("Add synonym");
	setSize(200,150);
        MainApplication.centreWindow(this);
	synonymfor.setText(ent.name);
  }

  private void jbInit() throws Exception {
    box1 = Box.createHorizontalBox();
    component1 = Box.createHorizontalStrut(8);
    jLabel1.setText("Input synonym for");
    synonymfor.setText("jLabel2");
    synonymtext.setPreferredSize(new Dimension(150, 21));
    synonymtext.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        doOK(e);
      }
    });
    cancel.setText("Cancel");
    cancel.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        doCancel(e);
      }
    });
    OK.setText("OK");
    OK.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        doOK(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1, null);
    jPanel1.add(synonymfor, null);
    jPanel1.add(synonymtext, null);
    jPanel1.add(box1, null);
    box1.add(cancel, null);
    box1.add(component1, null);
    box1.add(OK, null);
  }

	public String synonym="";
  void doOK(ActionEvent e) {
	synonym=synonymtext.getText();
	hide();
  }

  void doCancel(ActionEvent e) {
	hide();
  }
}

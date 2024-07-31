package phic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This allows the user to alter the person's specifications.
 */

public class BuildABod extends ModalDialog {
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton2 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  public BuildABod() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setTitle("Edit patient");
    init();
    this.pack();
  }
  EditableScreensPanel editableScreensPanel ;
  void init(){
    editableScreensPanel = new EditableScreensPanel("BuildABod.txt");
    jPanel1.add(editableScreensPanel, BorderLayout.CENTER);
    OKpressed=false;
  }
  private void jbInit() throws Exception {
    jButton2.setText("OK");
    jButton2.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){OKpressed=true;hide();}});
    jPanel1.setLayout(borderLayout1);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
  }
  boolean OKpressed;

}
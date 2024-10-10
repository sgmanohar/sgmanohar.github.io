
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.cudos.common.*;

public class FourierMembraneExhibit extends CudosExhibit{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  Border border1;
  JButton returnb=new JButton("Exit");
  BorderLayout borderLayout2 = new BorderLayout();
  FourierMembrane fourierMembrane1 = new FourierMembrane();

  public FourierMembraneExhibit() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setBorder(border1);
    jPanel1.setLayout(borderLayout2);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(fourierMembrane1, BorderLayout.CENTER);
    jPanel1.add(returnb, BorderLayout.SOUTH);
    returnb.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){
	getApplet().toChooser();
    }});
  }
}
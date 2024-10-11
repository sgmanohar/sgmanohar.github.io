
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import com.neurolab.common.*;

public class ChromaticAberration extends NeurolabExhibit {
  public String getExhibitName(){ return "Chromatic aberration and depth"; }

  Image image;
  JButton retb = new JButton("Return");
  public void init(){
    super.init();
    image=getImage("resources/RedBlue.GIF");
    setBackground(Color.black);
    setLayout(new BorderLayout());
    add(retb, BorderLayout.SOUTH);
    retb.addActionListener(ret);
    JLabel pic=new JLabel(new ImageIcon(image));
    add(pic,BorderLayout.CENTER);
  }
  public void close() {}
  ActionListener ret = new ActionListener(){public void actionPerformed(ActionEvent e){
    getHolder().setExhibit("com.neurolab.VisualOptics");
  }};
}
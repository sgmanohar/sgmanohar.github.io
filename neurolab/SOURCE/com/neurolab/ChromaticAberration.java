
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
import javax.swing.*;
import com.neurolab.common.*;

public class ChromaticAberration extends NeurolabExhibit {
  public String getExhibitName(){ return "Chromatic aberration and depth"; }

  Image image;
  public void init(){
    super.init();
    image=getImage("resources/RedBlue.gif");
    setBackground(Color.black);
    setLayout(new BorderLayout());
    add(new ReturnButton(), BorderLayout.SOUTH);
    JLabel pic=new JLabel(new ImageIcon(image));
    add(pic,BorderLayout.CENTER);
  }

}
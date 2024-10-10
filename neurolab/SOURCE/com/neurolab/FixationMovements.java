
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import com.neurolab.common.*;

import java.awt.*;
import javax.swing.*;

public class FixationMovements extends NeurolabExhibit {

  public String getExhibitName(){
    return "Fixation movements";
  }

  Image image;
  String string1=
          "Fixate the small black dot for about 30 seconds, long enough to "+
          "generate an after-image. Then fixate the larger, blue, circle.";
  String string2=
          "Because of your micro-movements, you will see the after-image of the "+
          "grid appear to move around: you should notice both drift and micro-"+
          "saccades.";
  public void init(){
    super.init();
    image=getImage("resources/FixationMovement.gif");
    JLabel pic=new JLabel(new ImageIcon(image));
    //JPanel textpanel=new JPanel();
    JTextArea text1=new JTextArea(string1+"\n\n"+string2);
    JScrollPane sc=new JScrollPane(text1);
    getMainContainer().setBackground(Color.white);
    getMainContainer().setLayout(new BorderLayout());
    getMainContainer().add(new ReturnButton(), BorderLayout.SOUTH);
    getMainContainer().add(pic,BorderLayout.CENTER);
    getMainContainer().add(sc, BorderLayout.EAST);
    //textpanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
    sc.setPreferredSize(new Dimension(150,220));
    text1.setFont(new Font("Dialog", Font.PLAIN, 12));
    text1.setLineWrap(true);
    text1.setWrapStyleWord(true);
    text1.setEditable(false);
    text1.setOpaque(true);
    //textpanel.setPreferredSize(new Dimension(140,230));
    //textpanel.setLayout(null);
    //textpanel.add(text1);
  }
}
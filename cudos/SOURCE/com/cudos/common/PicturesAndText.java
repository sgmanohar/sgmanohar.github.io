
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;

public abstract class PicturesAndText extends CudosExhibit {
	boolean testing=false;

  BorderLayout borderLayout1 = new BorderLayout();
  JButton exit = new JButton();
  JButton jButton1 = new JButton();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  JList listbox = new JList();
  Border border1;
  TitledBorder titledBorder1;
  Border border2;
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JTextPane textpane = new JTextPane();
  JPanel imagepane = new JPanel();
  JPanel jPanel5 = new JPanel();
  Border border3;
  TitledBorder titledBorder2;
  protected ImageControl imagecontrol=new ImageControl();

  public PicturesAndText() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
		// Generic text-picture initialisation
	Image[] image;String[] text;String[] itemname;
  BorderLayout borderLayout5 = new BorderLayout();
	public void postinit(){
		String[] imagepath=getImagePaths();
		text=getTexts();
		image=new Image[imagepath.length];
		for(int i=0;i<imagepath.length;i++){
			if((imagepath[i]!=null) &&(imagepath[i]!="")){
				image[i]=getApplet().getImage(imagepath[i]);
				if(image[i]==null)throw new RuntimeException("Image "+imagepath[i]+" not found");
			}
		}
		itemname=getItemNames();
		listbox.setListData(itemname);
	}
		//abstracts
	public abstract String[] getItemNames();	// should return listbox items
	public abstract String[] getTexts();		// should return comments for lower box
	public abstract String[] getImagePaths();	// should return .gif path relative to docbase

  private void jbInit() throws Exception {
    border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
    titledBorder1 = new TitledBorder(border1,"Item");
    border2 = BorderFactory.createCompoundBorder(titledBorder1,BorderFactory.createEmptyBorder(5,5,5,5));
    border3 = BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178));
    titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Select an item");
    this.getContentPane().setLayout(borderLayout1);
    exit.setToolTipText("");
    exit.setText("Exit");
    exit.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        exit_actionPerformed(e);
      }
    });
    jButton1.setText("Test");
    jPanel2.setLayout(borderLayout2);
    jPanel3.setPreferredSize(new Dimension(150, 10));
    jPanel3.setLayout(borderLayout3);
    jPanel4.setLayout(borderLayout4);
    textpane.setPreferredSize(new Dimension(74, 80));
    textpane.setBackground(Color.lightGray);
    textpane.setBorder(BorderFactory.createLoweredBevelBorder());
    textpane.setSelectionColor(SystemColor.textHighlight);
    textpane.setText("jTextPane1");
    textpane.setFont(new java.awt.Font("SansSerif", 1, 14));
    listbox.setBackground(Color.lightGray);
    listbox.setBorder(titledBorder2);
    listbox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listbox.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

      public void valueChanged(ListSelectionEvent e) {
        listboxchanged(e);
      }
    });
    imagepane.setLayout(borderLayout5);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel3, BorderLayout.WEST);
    jPanel3.add(listbox, BorderLayout.CENTER);
    jPanel3.add(jPanel5, BorderLayout.SOUTH);
    jPanel5.add(jButton1, null);
    jPanel5.add(exit, null);
    jPanel2.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(textpane, BorderLayout.SOUTH);
    jPanel4.add(imagepane, BorderLayout.CENTER);
	imagepane.add(imagecontrol, BorderLayout.CENTER);
  }

  void listboxchanged(ListSelectionEvent e) {
	if(!testing){
          Image i = image[listbox.getSelectedIndex()];
		imagecontrol.setImage(i);
		textpane.setText(text[listbox.getSelectedIndex()]);
	}
  }

  void exit_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }
}

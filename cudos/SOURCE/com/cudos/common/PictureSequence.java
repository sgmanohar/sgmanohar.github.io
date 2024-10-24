
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;

public abstract class PictureSequence extends CudosExhibit {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  ImageControl image = new ImageControl();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  Border border1;
  JPanel jPanel4 = new JPanel();
  JSlider position = new JSlider();
  JPanel jPanel1 = new JPanel();
  JButton stop = new JButton();
  JButton start = new JButton();
  JButton exit = new JButton();
  BorderLayout borderLayout4 = new BorderLayout();
  JTextPane textarea = new JTextPane();

  public PictureSequence() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.control,1);
    this.getContentPane().setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    jPanel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(5,5,5,5)));
    jPanel3.setLayout(borderLayout3);
    position.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        position_stateChanged(e);
      }
    });
     position.setBorder(border1);
    stop.setText("Stop");
    stop.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        stop_actionPerformed(e);
      }
    });
    start.setText("Start");
    start.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        start_actionPerformed(e);
      }
    });
    exit.setText("Exit");
    exit.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        exit_actionPerformed(e);
      }
    });
    jPanel4.setLayout(borderLayout4);
    textarea.setText("jTextPane1");
    textarea.setPreferredSize(new Dimension(31, 50));
    textarea.setBackground(SystemColor.control);
    textarea.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178)));
    textarea.setText("test");
    textarea.setFont(new java.awt.Font("SansSerif", 1, 14));
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(image, BorderLayout.CENTER);
    this.getContentPane().add(jPanel4, BorderLayout.SOUTH);
    jPanel4.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(position, null);
    jPanel1.add(start, null);
    jPanel1.add(stop, null);
    jPanel1.add(exit, null);
    jPanel4.add(textarea, BorderLayout.NORTH);
  }

	int frame=0;
	Image[] im;
	String[] path;
	String[] text;
	public void postinit(){
		path=getImagePaths();
		getApplet().paintComponentMessage(image,"Loading images...");
		String otitle=getApplet().getTitle();
		im=new Image[path.length];
		for(int i=0;i<path.length;i++){
			if((path[i]!=null) &&(path[i]!="")){
				im[i]=getApplet().getImage(path[i]);
				if(im[i]==null)throw new RuntimeException("Image "+path[i]+" not found");
				else getGraphics().drawImage(im[i],0,0,1,1,this);//blit with no size preloads image!?
			}
		}
		image.setStretched(false);
 		position.setValue(0);
		image.setImage(im[0]);
		text=getTexts();
		if((text==null)||(text.length==0))textarea.setVisible(false);
		getApplet().setTitle(otitle);
	}
	public abstract String[] getImagePaths();
	public abstract String[] getTexts();

	Timer timer=new Timer(50,new ActionListener(){public void actionPerformed(ActionEvent e){
		image.setImage(im[frame]);
		if(++frame>=im.length)frame%=im.length;
		position.setValue(frame*100/im.length);
	}});
  void start_actionPerformed(ActionEvent e) {
	if(start.getText()=="Start"){
		start.setText("Pause");
		timer.start();
	}else{
		start.setText("Start");
		timer.stop();
		setFrame(frame);	//causes text to appear
	}
  }

  void stop_actionPerformed(ActionEvent e) {
	timer.stop();
	if(start.getText()=="Pause")start.setText("Start");
	setFrame(0);
  }
	public void setFrame(int i){
		frame=i%im.length;
		image.setImage(im[frame]);
		position.setValue(frame*100/im.length);
		if(textarea.isVisible())textarea.setText(text[frame]);
	}
  void position_stateChanged(ChangeEvent e) {
	int nframe=position.getValue()*(im.length-1)/100;
	if(nframe!=frame){
		frame=nframe;
		image.setImage(im[frame]);
		if(textarea.isVisible())textarea.setText(text[frame]);
	}
  }

  void exit_actionPerformed(ActionEvent e) {
	timer.stop();
	getApplet().toChooser();
  }
}

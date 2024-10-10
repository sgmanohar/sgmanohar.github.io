
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

public class SpinalCordSections extends CudosExhibit{
  BorderLayout borderLayout1 = new BorderLayout();
  Border border1;
  JPanel jPanel1 = new JPanel();
  Border border2;
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  Border border3;
  JButton jButton1 = new JButton();
  ImageStackControl graph = new ImageStackControl();
  GridLayout gridLayout1 = new GridLayout();
  IndexedImageControl imagecontrol = new IndexedImageControl();
  JPanel jPanel3 = new JPanel();
  JSlider persp_slider = new JSlider();
  JSlider trans_slider = new JSlider();
  JButton jButton2 = new JButton();
  JLabel jLabel1 = new JLabel();

  public SpinalCordSections() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	Image[] images=new Image[5];
  Border border4;
  JLabel jLabel2 = new JLabel();
	public void postinit(){
		String path="resources/images/sp-lumbar-";
		for(int i=0;i<5;i++){
			graph.addImage(images[i]=getApplet().getImage(path+(i+1)+".jpg"));
		}
		imagecontrol.setImage(getApplet().getImage("resources/images/coronal-01.jpg"));
		imagecontrol.setIndex(getApplet().getImage("resources/images/coronal-01ix.jpg"));
		graph.setIndexedImageListener(new IndexedImageListener(){
			public void indexedImageEvent(int e){
				if(e>-1)imagecontrol.setImage(images[e]);
				imagecontrol.repaint();
			}
		});
	}

  private void jbInit() throws Exception {
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,new Color(255, 255, 235),new Color(94, 93, 80),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(10,10,0,10));
    border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,new Color(255, 255, 235),new Color(94, 93, 80),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(10,10,10,10));
    border4 = BorderFactory.createLineBorder(SystemColor.control,1);
    this.setBorder(border1);
    border1 = BorderFactory.createEmptyBorder(5,5,5,5);
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setBorder(border2);
    jPanel1.setLayout(borderLayout2);
    jPanel2.setBorder(border3);
    jPanel2.setLayout(gridLayout1);
    jButton1.setText("Exit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    gridLayout1.setColumns(2);
    persp_slider.setPreferredSize(new Dimension(100, 26));
    persp_slider.setBorder(border4);
    trans_slider.setPreferredSize(new Dimension(100, 26));
    trans_slider.setBorder(BorderFactory.createLineBorder(SystemColor.control,1));
    jButton2.setText("Redraw");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jLabel1.setText("Transparency");
    jLabel2.setText("Perspective");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(graph, null);
    jPanel2.add(imagecontrol, null);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel3.add(jLabel2, null);
    jPanel3.add(persp_slider, null);
    jPanel3.add(jLabel1, null);
    jPanel3.add(trans_slider, null);
    jPanel3.add(jButton2, null);
    this.getContentPane().add(jButton1, BorderLayout.SOUTH);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }


  void jButton2_actionPerformed(ActionEvent e) {
	graph.transparency=trans_slider.getValue()/100f;
	graph.perspective=persp_slider.getValue()/50.;
	graph.buildGraphic();
	graph.repaint();
  }

}

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

import com.cudos.common.CudosExhibit;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.cudos.common.kinetic.*;
import java.util.Random;

public class CirculationCompartment extends CudosExhibit {
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  Border border2;
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  KineticPane kineticPane = new KineticPane();
  JButton jButton2 = new JButton();

  public CirculationCompartment() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	public void postinit(){
		Random rand=new Random();
		int u=30;	//gutter
		int t=60;	// thickness
		KineticPane k=kineticPane;
		int[][] hwc={	{u,u,k.getWidth()-u},
				{u+t,u+t,k.getWidth()-u-t},
				{k.getHeight()-u-t,u+t,k.getWidth()-u-t},
				{k.getHeight()-u,u,k.getWidth()-u}		};
		int[][] vwc={	{u,u,k.getHeight()-u},
				{u+t,u+t,k.getHeight()-u-t},
				{k.getWidth()-u-t,u+t,k.getHeight()-u-t},
				{k.getWidth()-u,u,k.getHeight()-u}		};
		Wall w;
		for(int i=0;i<hwc.length;i++){
			k.hwalls.add(w=new Wall(hwc[i][0],hwc[i][1],hwc[i][2]));
			w.setOrientation(Wall.HORIZONTAL);
			k.paintables.add(w);
		}
		for(int i=0;i<vwc.length;i++){
			k.vwalls.add(w=new Wall(vwc[i][0],vwc[i][1],vwc[i][2]));
			w.setOrientation(Wall.VERTICAL);
			k.paintables.add(w);
		}
		k.hwalls.add(w=new Wall(getHeight()/2,u,u+t));
		w.setMoleculeListener(new MoleculeListener(){
			public boolean moleculeEvent(Molecule m){
				if(m.vy<0){
					m.vy=m.vy;
					m.y+=2*m.vy;
					return false;
				}
				else return false;
			}
		});
		Rectangle rectu=new Rectangle(u,u,k.getWidth()-2*u-5,t-5);
		Rectangle rectl=new Rectangle(u,k.getHeight()-u-t,k.getWidth()-2*u-5,t-5);
		Molecule m;
		for(int i=0;i<300;i++){
			k.molecules.add(new Molecule(rand,rectu));
			k.molecules.add(m=new Molecule(rand,rectl));
			m.setType(Molecule.SODIUM);
		}
		kineticPane.timer.setDelay(50);
	}

  private void jbInit() throws Exception {
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
    this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2)));
    this.getContentPane().setLayout(borderLayout1);
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
    jPanel1.setBorder(border2);
    jPanel1.setLayout(borderLayout2);
    jButton1.setText("Exit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    kineticPane.setBackground(new Color(192, 224, 224));
    jButton2.setText("Reset");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(kineticPane, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton1, null);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }

  void jButton2_actionPerformed(ActionEvent e) {

  }
}
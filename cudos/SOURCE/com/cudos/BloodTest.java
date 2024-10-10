
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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import com.cudos.common.kinetic.*;
import java.util.*;
import javax.swing.event.*;

public class BloodTest extends CudosExhibit {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JPanel jPanel2 = new JPanel();
  KineticPane k = new KineticPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JRadioButton jRadioButton3 = new JRadioButton();
  JRadioButton jRadioButton2 = new JRadioButton();
  JRadioButton jRadioButton1 = new JRadioButton();
  FlowLayout flowLayout1 = new FlowLayout();
  ButtonGroup bg1 = new ButtonGroup();
  JPanel jPanel5 = new JPanel();
  JSlider speed = new JSlider();
  JLabel jLabel1 = new JLabel();
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel6 = new JPanel();
  JRadioButton jRadioButton4 = new JRadioButton();
  JRadioButton jRadioButton5 = new JRadioButton();
  JRadioButton jRadioButton6 = new JRadioButton();

  public BloodTest() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

	Random rand=new Random();
	Rectangle rect=new Rectangle(25,25);
	int nrbc=7;
	Cell rbc[]=new Cell[nrbc];
	Image rbcim;
	public void postinit(){
		rbcim=getApplet().getImage("resources/icons/rbc.gif");
		rect.setSize(k.getWidth()-50,k.getHeight()-75);
		MassiveMolecule m;
		for(int i=0;i<nrbc;i++){
			int x,y;boolean coll;
			do{
				x=rand.nextInt(rect.width)+rect.x;
				y=rand.nextInt(rect.height)+rect.y;
				coll=false;
				for(int j=0;j<i;j++){
					if(x+rbc[j].s>rbc[j].x && x<rbc[j].x+rbc[j].s && y+rbc[j].s>rbc[j].y && y<rbc[j].y+rbc[j].s)coll=true;
				}
			}while(coll);
			k.molecules.add(rbc[i]=new Cell(x,y,k));
			rbc[i].vx=rand.nextInt(20)-10;
			rbc[i].vy=rand.nextInt(20)-10;
			rbc[i].im=rbcim;
		}
		int ms=6;
		for(int i=0;i<6;i++){
			int x,y;boolean coll;
			do{
				x=rand.nextInt(rect.width)+rect.x;
				y=rand.nextInt(rect.height)+rect.y;
				coll=false;
				for(Enumeration e=k.molecules.elements();e.hasMoreElements();){
					Molecule v=(Molecule)e.nextElement();
					if(x+ms>v.x && x<v.x+v.s && y+ms>v.y && y<v.y+v.s)coll=true;
				}
			}while(coll);
			k.molecules.add(m=new Antibody(x,y));
			m.vx=rand.nextInt(10)-5;
			m.vy=rand.nextInt(10)-5;
			k.swalls.add(new CircularWall(m,6));
		}

	}

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jButton1.setText("Exit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(5,5,5,5)));
    jPanel2.setLayout(borderLayout2);
    k.setBackground(new Color(192, 224, 224));
    jPanel3.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    jPanel3.setPreferredSize(new Dimension(250, 35));
    jPanel3.setLayout(gridLayout1);
    jRadioButton3.setText("jRadioButton3");
    jRadioButton2.setText("jRadioButton2");
    jRadioButton1.setText("jRadioButton1");
    jPanel4.setLayout(flowLayout1);
    jPanel4.setBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)));
    jLabel1.setText("Speed");
    speed.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        speed_stateChanged(e);
      }
    });
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(5);
    gridLayout1.setVgap(5);
    jPanel6.setBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)));
    jRadioButton4.setText("jRadioButton4");
    jRadioButton5.setText("jRadioButton5");
    jRadioButton6.setText("jRadioButton6");
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jButton1, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(k, BorderLayout.CENTER);
    jPanel2.add(jPanel5, BorderLayout.SOUTH);
    jPanel5.add(jLabel1, null);
    jPanel5.add(speed, null);
    this.getContentPane().add(jPanel3, BorderLayout.EAST);
    jPanel3.add(jPanel4, null);
    jPanel4.add(jRadioButton3, null);
    jPanel4.add(jRadioButton2, null);
    jPanel4.add(jRadioButton1, null);
    jPanel3.add(jPanel6, null);
    jPanel6.add(jRadioButton4, null);
    jPanel6.add(jRadioButton5, null);
    jPanel6.add(jRadioButton6, null);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }

  void speed_stateChanged(ChangeEvent e) {
	k.timer.setDelay(100-speed.getValue());
  }

}
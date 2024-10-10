
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import com.cudos.common.molecules.*;
import com.cudos.common.molecules.instance.Phospholipid;

public class LiposomeFormation extends CudosExhibit {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton jButton3 = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  Border border1;
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JTextField attrm = new JTextField();
  JTextField attrr = new JTextField();
  JPanel jPanel5 = new JPanel();
  JTextField repm = new JTextField();
  JTextField repr = new JTextField();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  Color aircolour=new Color(255,255,224);
  FieldMover mover = new FieldMover(){
		public void paintBackgroundElements(Graphics g){
			g.setColor(aircolour);
			g.fillRect(0,0,getWidth(),Phospholipid.waterlevel);
		}
	};

  public LiposomeFormation() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

		//BOUNDARY CONDITIONS
/*
	Phospholipid a=new Phospholipid();
	mover.mols().add(a);
	a.setPos(new Point2D.Double(0,150-15));
	a.heldstill=true;
	a=new Phospholipid();
	mover.mols().add(a);
	a.setPos(new Point2D.Double(0,150+15));
	a.setOrientation(Math.PI);
	a.heldstill=true;
*/
	Phospholipid.waterlevel=50;
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    this.getContentPane().setLayout(borderLayout1);
    jButton1.setText("+");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jButton2.setText("-");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jButton3.setText("Exit");
    jButton3.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jPanel1.setLayout(borderLayout2);
    jPanel1.setBorder(border1);
    mover.setOpaque(true);
    mover.setBackground(new Color(192, 255, 255));
    attrm.setPreferredSize(new Dimension(65, 21));
    attrm.setText(String.valueOf(Phospholipid.attraction));
    attrm.setHorizontalAlignment(SwingConstants.RIGHT);
    attrm.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        change(e);
      }
    });
    attrr.setPreferredSize(new Dimension(65, 21));
    attrr.setText(String.valueOf(Phospholipid.attrscale));
    attrr.setHorizontalAlignment(SwingConstants.RIGHT);
    attrr.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        change(e);
      }
    });
    repm.setPreferredSize(new Dimension(65, 21));
    repm.setText(String.valueOf(Phospholipid.repulsion));
    repm.setHorizontalAlignment(SwingConstants.RIGHT);
    repm.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        change(e);
      }
    });
    repr.setPreferredSize(new Dimension(65, 21));
    repr.setText(String.valueOf(Phospholipid.repscale));
    repr.setHorizontalAlignment(SwingConstants.RIGHT);
    repr.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        change(e);
      }
    });
    jPanel3.setPreferredSize(new Dimension(100, 41));
    jPanel4.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Attraction"));
    jPanel4.setPreferredSize(new Dimension(100, 80));
    jPanel5.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Repulsion"));
    jPanel5.setPreferredSize(new Dimension(100, 80));
    jLabel1.setText("M");
    jLabel2.setText("R");
    jLabel3.setText("M");
    jLabel4.setText("R");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(mover, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton1, null);
    jPanel2.add(jButton3, null);
    this.getContentPane().add(jPanel3, BorderLayout.EAST);
    jPanel3.add(jPanel4, null);
    jPanel4.add(jLabel1, null);
    jPanel4.add(attrm, null);
    jPanel4.add(jLabel2, null);
    jPanel4.add(attrr, null);
    jPanel3.add(jPanel5, null);
    jPanel5.add(jLabel3, null);
    jPanel5.add(repm, null);
    jPanel5.add(jLabel4, null);
    jPanel5.add(repr, null);
  }

  void command(ActionEvent e) {
	String c=e.getActionCommand();
	if(c.equals("Exit")){
		mover.timer.stop();
		getApplet().toChooser();
	}
	if(c.equals("+")){
		addLipid();
	}
	if(c.equals("-") && mover.mols().size()>0){
		mover.mols().remove(0);
	}
  }
	void addLipid(){
		Phospholipid p=new Phospholipid();
		p.setPos(new Point(150,150));
		p.vx=Math.random()*4;
		p.vy=Math.random()*4;
		p.va=Math.random()*0.1;
		mover.mols().add(p);
	}

  void change(ActionEvent e) {
	JTextField f=(JTextField)e.getSource();
	double val=Double.parseDouble(f.getText());
	if(f==attrm)Phospholipid.attraction=val;
	else if(f==attrr)Phospholipid.attrscale=val;
	else if(f==repm)Phospholipid.repulsion=val;
	else if(f==repr)Phospholipid.repscale=val;
  }

}
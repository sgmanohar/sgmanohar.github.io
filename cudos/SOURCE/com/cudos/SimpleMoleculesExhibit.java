
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
import com.cudos.common.molecules.*;
import com.cudos.common.molecules.instance.*;


public class SimpleMoleculesExhibit extends CudosExhibit {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel4 = new JPanel();
  JButton exit = new JButton();
  public MoleculeMover mover = new MoleculeMover();
  BorderLayout borderLayout3 = new BorderLayout();
  JToolBar tools = new JToolBar();
  BorderLayout borderLayout4 = new BorderLayout();
  JButton addm1 = new JButton();
  JButton delete = new JButton();
  JButton addm2 = new JButton();
  JButton addm3 = new JButton();

  public SimpleMoleculesExhibit() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	public void postinit(){
	    addm1.setIcon(new ImageIcon(getApplet().getImage("resources/ICONS/molecules/water.gif")));
	    addm2.setIcon(new ImageIcon(getApplet().getImage("resources/ICONS/molecules/rbc.gif")));
	    addm3.setIcon(new ImageIcon(getApplet().getImage("resources/Icons/molecules/antibody.gif")));
		mover.timer.start();
		AbstractMolecule.applet=getApplet();
	}

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    exit.setText("Exit");
    exit.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        exit_actionPerformed(e);
      }
    });
    jPanel1.setLayout(borderLayout3);
    jPanel3.setLayout(borderLayout4);
    addm1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addm1_actionPerformed(e);
      }
    });
    delete.setToolTipText("");
    delete.setText("delete");
    delete.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        delete_actionPerformed(e);
      }
    });
    jPanel3.setPreferredSize(new Dimension(80, 82));
    mover.setBackground(new Color(222, 202, 202));
    addm3.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addm3_actionPerformed(e);
      }
    });
    addm2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addm2_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(mover, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.EAST);
    jPanel2.add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(delete, BorderLayout.SOUTH);
    jPanel3.add(tools, BorderLayout.WEST);
    tools.add(addm1, null);
    tools.add(addm2, null);
    tools.add(addm3, null);
    jPanel2.add(jPanel4, BorderLayout.SOUTH);
    jPanel4.add(exit, null);
  }

  void exit_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }

  void addm1_actionPerformed(ActionEvent e) {
	mover.mols().add(new Water());
  }

  void delete_actionPerformed(ActionEvent e) {
	mover.mols().remove(0);
  }

  void addm3_actionPerformed(ActionEvent e) {
	mover.mols().add(new Antibody());
  }

  void addm2_actionPerformed(ActionEvent e) {
	mover.mols().add(new RedBloodCell());
  }
}
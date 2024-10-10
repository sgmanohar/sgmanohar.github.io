
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

import javax.swing.*;
import com.cudos.common.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;

import com.cudos.common.CudosExhibit;

public class ModuleCellBiology extends CudosExhibit {
	final String filename="resources/index/Cells.txt";

  Button3D button3D1 = new Button3D();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();

  public ModuleCellBiology() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	public String getExhibitName(){return "Cell biology";}

	JButton[] buttons;
	String[] names;
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JPanel jPanel3 = new JPanel();
  Border border1;
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
	public void postinit(){
		CudosIndexReader r=new CudosIndexReader(getApplet().getResourceURL(filename));
		names=r.getSectionNames();
		buttons=new JButton[names.length];
		ActionListener al=new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getApplet().toExhibit("com.cudos.IndexedPictures",new String[]{filename,e.getActionCommand()});
			}
		};
		Font bigfont=new Font("SansSerif",Font.BOLD,16);
		for(int i=0;i<names.length;i++){
			buttons[i]=new Button3D();
//			buttons[i].setBackground(SystemColor.control);
			buttons[i].setActionCommand(names[i]);
			buttons[i].setText(names[i]);
			buttons[i].addActionListener(al);
			buttons[i].setFont(bigfont);
			jPanel1.add(buttons[i], null);
		}
		jPanel1.doLayout();
	}


  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    this.getContentPane().setLayout(borderLayout1);
    jButton1.setText("Exit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel1.setLayout(flowLayout1);
    jPanel3.setBorder(border1);
    jPanel3.setLayout(borderLayout3);
    jPanel4.setLayout(borderLayout2);
    jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(10,10,10,10)));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton1, null);
    this.getContentPane().add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jPanel1, BorderLayout.CENTER);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }
}

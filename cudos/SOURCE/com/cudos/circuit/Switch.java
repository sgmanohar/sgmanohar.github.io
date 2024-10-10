
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import com.cudos.common.*;

public class Switch extends CircuitComponent {
	static int sid=0;
	int id;
  Border border1;
  JPanel jPanel1 = new JPanel();
  JToggleButton close = new JToggleButton();
  Border border2;

	Image iopen,iclosed;
  public Switch(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Switch"+(id=sid++));
	iopen=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Switch.gif");
	iclosed=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/SwitchClosed.gif");
	i=iopen;
  }

	public double getResistance(){
		return close.isSelected()?0.001:1000000000;
	}
	public boolean passesCurrent(){return close.isSelected();}

  private void jbInit() throws Exception {
    border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
    border2 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
    close.setPreferredSize(new Dimension(115, 45));
    close.setText("Close");
    close.setFont(new java.awt.Font("Dialog", 1, 14));
    close.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        close_actionPerformed(e);
      }
    });
    jPanel1.setBorder(border2);
    this.setPreferredSize(new Dimension(160, 100));
    this.add(jPanel1, null);
    jPanel1.add(close, null);
  }

  void close_actionPerformed(ActionEvent e) {
	if(close.isSelected())i=iclosed;else i=iopen;
	getCircuitboard().repaint();
  }
}

/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import com.cudos.common.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class Resistor extends CircuitComponent {
	static int sid=0;
	int id;
  JLabel jLabel1 = new JLabel();
  JTextField tresistance = new JTextField();
  JSlider sresistance = new JSlider();
  public Resistor(Circuitboard cb) {
	super(cb);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Resistor"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Resistor.gif");
	sresistance.setValue(30);
  }

	String getUnit(){return "Ohm";}

	public double getResistance(){
		return resistance;
	}

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.control,1);
    jLabel1.setLabelFor(tresistance);
    jLabel1.setText("Resistance");
    tresistance.setPreferredSize(new Dimension(85, 21));
    tresistance.setText("100");
    this.setPreferredSize(new Dimension(195, 120));
    sresistance.setValue(10);
    sresistance.setMaximum(60);
    sresistance.setMajorTickSpacing(10);
    sresistance.setPaintTicks(true);
    sresistance.setPreferredSize(new Dimension(180, 51));
    sresistance.setBorder(border1);
    sresistance.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        sresistance_stateChanged(e);
      }
    });
    this.add(jLabel1, null);
    this.add(tresistance, null);
    this.add(sresistance, null);
  }
	double resistance;
  Border border1;
  void sresistance_stateChanged(ChangeEvent e) {
	int v=sresistance.getValue();
	resistance=(v==0)?0.0001:Math.pow(10,v/10)*(1+v%10);
	tresistance.setText(unitString(resistance));
  }
  public void setResistance(double resistance) {
    this.resistance = resistance;
  }
}

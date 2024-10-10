
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
import com.cudos.common.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class Cell extends CircuitComponent {
  JTextField tvoltage = new JTextField();

	static int sid=0;
	int id;
  JLabel jLabel1 = new JLabel();
  JSlider svoltage = new JSlider();
  Border border1;

  public Cell(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Cell"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Cell.gif");
	svoltage.setValue(30);
  }
	public double getEMF(){return voltage;}
	public String getUnit(){return "V";}
	public int getBehaviour(){return B_EMF;}
	public boolean isReversible(){return true;}

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.control,1);
    tvoltage.setPreferredSize(new Dimension(80, 21));
    tvoltage.setText("0");
    jLabel1.setText("Voltage");
    this.setPreferredSize(new Dimension(200, 110));
    svoltage.setMaximum(50);
    svoltage.setMajorTickSpacing(10);
    svoltage.setPaintTicks(true);
    svoltage.setPreferredSize(new Dimension(180, 32));
    svoltage.setBorder(border1);
    svoltage.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        svoltage_stateChanged(e);
      }
    });
    this.add(jLabel1, null);
    this.add(tvoltage, null);
    this.add(svoltage, null);
  }
	double voltage;
        public void setVoltage(double v){
          voltage=v;
          tvoltage.setText(unitString(voltage));
        }
        public double getVoltage(){return voltage;}
  void svoltage_stateChanged(ChangeEvent e) {
	int v=svoltage.getValue()-1;
	if(v==0)voltage=0;else{
		voltage=1E-3*Math.pow(10,v/10)*((v%10)+1);
	}
	tvoltage.setText(unitString(voltage));
  }
}

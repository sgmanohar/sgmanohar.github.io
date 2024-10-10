
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
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;

public class Capacitor extends CircuitComponent {
	static int sid=0;
	int id;
  public Capacitor(Circuitboard cb) {
	super(cb);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Capacitor"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Capacitor.gif");
 	scapacitance.setValue(30);
 }
  JTextField tcapacitance = new JTextField();
  JLabel jLabel1 = new JLabel();
  JSlider scapacitance = new JSlider();
  Border border1;

	double charge=0;
	double capacitance;
	double tEMF=0;
  JLabel jLabel2 = new JLabel();
  JTextField tcharge = new JTextField();
  JButton discharge = new JButton();
		//Overrides
	String getUnit(){return "F";}
	public int getBehaviour(){return B_EMF;}
	public double getResistance(){return 1;}

	public double getResistanceFromEMF(double e){
		tEMF+=e;
		return getResistance();
	}
	public double getEMF(){return charge/capacitance;}
	public void process(){
		if(!Double.isNaN(current)){
			double dQ=current*cb.deltat;
			double Vapplied=tEMF/3+getEMF();
/*
			if((dQ+charge)*charge>=0)charge+=dQ;	// don't let voltage cross zero; discharge must occur first!
				else charge=0;
*/
			if(((charge+dQ)/capacitance-Vapplied)*(charge/capacitance-Vapplied)>=0)
				charge+=dQ;
				else charge=Vapplied*capacitance;
//System.out.println((charge/capacitance)+"V, "+unitString(dQ));
			chargeChange();
			tEMF=0;
		}
	}
	public void chargeChange(){
		String r=unitString(charge);
		r=r.substring(0,r.length()-1);
		tcharge.setText(r+"C");
	}
  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.control,1);
    jLabel1.setText("Capacitance");
    jLabel1.setLabelFor(tcapacitance);
    tcapacitance.setPreferredSize(new Dimension(80, 21));
    tcapacitance.setText("100");
    this.setPreferredSize(new Dimension(200, 160));
    scapacitance.setMaximum(70);
    scapacitance.setPaintTicks(true);
    scapacitance.setPreferredSize(new Dimension(180, 24));
    scapacitance.setBorder(border1);
    scapacitance.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        scapacitance_stateChanged(e);
      }
    });
    jLabel2.setText("Charge");
    tcharge.setPreferredSize(new Dimension(100, 21));
    tcharge.setText("0 C");
    discharge.setPreferredSize(new Dimension(170, 27));
    discharge.setText("Discharge now");
    discharge.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        discharge_actionPerformed(e);
      }
    });
    this.add(jLabel1, null);
    this.add(tcapacitance, null);
    this.add(scapacitance, null);
    this.add(jLabel2, null);
    this.add(tcharge, null);
    this.add(discharge, null);
  }

  void scapacitance_stateChanged(ChangeEvent e) {
	int v=scapacitance.getValue();
	capacitance=1E-9*Math.pow(10,v/10)*((v%10)+1);
	tcapacitance.setText(unitString(capacitance));
  }

  void discharge_actionPerformed(ActionEvent e) {
	charge=0;
	chargeChange();
  }
  public double getCapacitance() {
    return capacitance;
  }
  public void setCapacitance(double capacitance) {
    this.capacitance = capacitance;
    tcapacitance.setText(unitString(capacitance));
  }

}

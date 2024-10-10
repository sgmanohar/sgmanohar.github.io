
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
import javax.swing.event.*;
import com.cudos.common.*;

public class ACSource extends CircuitComponent {
	static int sid;
	int id;
  JPanel jPanel1 = new JPanel();
  Border border1;
  JLabel jLabel1 = new JLabel();
  JSlider svoltage = new JSlider();
  JTextField tvoltage = new JTextField();
  JPanel jPanel2 = new JPanel();
  Border border2;
  JSlider sfrequency = new JSlider();
  JLabel jLabel2 = new JLabel();
  Border border3;
  JTextField tfrequency = new JTextField();
  Border border4;

  public ACSource(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("AC_Source"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/ACSource.gif");
	svoltage.setValue(30);
	sfrequency.setValue(20);
  }
	int ticks;
	double voltage,amplitude;
	double pd;
	double frequency;
  Border border5;
  JLabel percentagebar = new JLabel(){
		public void paint(Graphics g){
			super.paint(g);
			int w=getWidth()/2;
			if(amplitude>=0){
				g.setColor(Color.blue);
				g.fill3DRect(w,2,(int)((w-2)*amplitude),getHeight()-4,true);
			}else{
				g.setColor(Color.red);
				int b=(int)(-amplitude*(w-2));
				g.fill3DRect(2+w-b,2,b,getHeight()-4,true);
			}
		}
	};
		//overriders
	public double getEMF(){
		return pd=amplitude*voltage;
	}
	public void process(){
		ticks++;
		amplitude=Math.sin(frequency*2*Math.PI*ticks*cb.deltat);
		percentagebar.repaint();
	}
	public String getUnit(){return "V";}
	public int getBehaviour(){return B_EMF;}


  private void jbInit() throws Exception {
    border3 = BorderFactory.createLineBorder(SystemColor.control,1);
    border4 = BorderFactory.createLineBorder(SystemColor.control,1);
    border5 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    jLabel2.setText("Frequency");
    sfrequency.setMaximum(50);
    sfrequency.setPreferredSize(new Dimension(120, 28));
    sfrequency.setBorder(border3);
    sfrequency.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        sfrequency_stateChanged(e);
      }
    });
    border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    svoltage.setPreferredSize(new Dimension(130, 28));
    svoltage.setBorder(border4);
    svoltage.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        svoltage_stateChanged(e);
      }
    });
    svoltage.setMaximum(50);
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    jPanel1.setBorder(border1);
    jPanel1.setPreferredSize(new Dimension(195, 70));
    jLabel1.setText("Voltage");
    tvoltage.setPreferredSize(new Dimension(80, 21));
    tvoltage.setText("0 V");
    jPanel2.setBorder(border2);
    jPanel2.setPreferredSize(new Dimension(195, 70));
    tfrequency.setText("0 Hz");
    tfrequency.setPreferredSize(new Dimension(80, 21));
    this.setPreferredSize(new Dimension(205, 215));
    percentagebar.setBorder(border5);
    percentagebar.setPreferredSize(new Dimension(190, 20));
    this.add(jPanel1, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(svoltage, null);
    jPanel1.add(tvoltage, null);
    this.add(jPanel2, null);
    jPanel2.add(jLabel2, null);
    jPanel2.add(sfrequency, null);
    jPanel2.add(tfrequency, null);
    this.add(percentagebar, null);
  }

  void svoltage_stateChanged(ChangeEvent e) {
	int v=svoltage.getValue()-1;
	if(v==0)voltage=0;else{
		voltage=1E-3*Math.pow(10,v/10)*((v%10)+1);
	}
	tvoltage.setText(unitString(voltage));
  }

  void sfrequency_stateChanged(ChangeEvent e) {
	int f=sfrequency.getValue();
	frequency=Math.pow(10,f/10)*((f%10)+1);
	ticks=(int)(Math.asin(amplitude)/(Math.PI*2*frequency*cb.deltat));
	String ft=unitString(frequency);
	ft=ft.substring(0,ft.length()-1);
	tfrequency.setText(ft+"Hz");
  }
  public void setVoltage(double voltage) {
    this.voltage = voltage;
    tvoltage.setText(unitString(voltage));
  }
  public double getVoltage() {
    return voltage;
  }
  public double getFrequency() {
    return frequency;
  }
  public void setFrequency(double frequency) {
    this.frequency = frequency;
    String ft=unitString(frequency);
    ft=ft.substring(0,ft.length()-1);
    tfrequency.setText(ft+"Hz");
  }
}


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

public class Lamp extends CircuitComponent {
  JLabel jLabel1 = new JLabel();
  JTextField tpower = new JTextField();

	static int sid=0;
	int id;
  public Lamp(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Lamp"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Lamp.gif");
  }
	public void paintCircuit(Graphics g_){
		super.paintCircuit(g_);
		if(pcolour>=0){
			int 	mid=(p1.x+p2.x)/2,
				dw=(c1<c2?1:-1)*i.getWidth(this)/2,	// use -dw if component reversed
				dh=i.getHeight(this)/2;
			Graphics2D g=(Graphics2D)g_;
			g.setColor(new Color(pcolour,pcolour,0));
			g.setStroke(new BasicStroke(3));
			g.fillOval(mid-Math.abs(dw)/2,p1.y-dh/2,dw,dh);
		}
	}
	double resistance=100;	//Ohms
	double power=0;
	public double getResistance(){return resistance;}
	public String getUnit(){return "W";}
	int pcolour;
	public void process(){
		power=current*current*resistance;
		tpower.setText(unitString(power));
		int opcolour=pcolour;
		pcolour=powerfn(power);
		if(pcolour!=opcolour)cb.repaintCircuitComponent(this);
	}
	double maxWatts=50E-3; //watts of power at max brightness
	int powerfn(double b){
		if(Double.isNaN(b))return -1;
		int q=(int)(255*b/maxWatts);
		return (q<0)?0:(q>255)?255:q;
	}

  private void jbInit() throws Exception {
    jLabel1.setText("Brightness");
    tpower.setPreferredSize(new Dimension(75, 21));
    tpower.setText("0 W");
    this.setPreferredSize(new Dimension(160, 70));
    this.add(jLabel1, null);
    this.add(tpower, null);
  }
}
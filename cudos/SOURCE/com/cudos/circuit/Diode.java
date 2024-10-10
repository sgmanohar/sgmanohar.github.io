
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
public class Diode extends CircuitComponent {
	static int sid=0;
	int id;

  public Diode(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Diode"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Diode.gif");
 }

	public double getResistanceFromEMF(double emf){
		if(emf<=0)return 0.001;
		else return Double.POSITIVE_INFINITY;
	}
	public boolean isReversible(){return true;}

  private void jbInit() throws Exception {
  }
}
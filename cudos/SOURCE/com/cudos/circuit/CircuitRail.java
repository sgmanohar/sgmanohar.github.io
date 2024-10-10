
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import java.util.*;

public class CircuitRail {

  public CircuitRail(Circuitboard c,int i) {
	cb=c;
	index=i;
  }
	int index;
	public double voltage=Double.NaN;
	Circuitboard cb;
	public Vector getComponentsOnRail(){
		Vector v=new Vector();
		for(Enumeration e=cb.components.elements();e.hasMoreElements();){
			CircuitComponent cc=(CircuitComponent)e.nextElement();
			if(cc.c1==index || cc.c2==index)v.add(cc);
		}
		return v;
	}
}
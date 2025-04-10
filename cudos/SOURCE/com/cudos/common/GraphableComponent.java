
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;
import java.awt.Color;
	//interface for components that can produce a double-valued
	//output that may be monitored by a graphing component
public interface GraphableComponent {
	public double getGraphableValue();
	public double getGraphableMax();
	public double getGraphableMin();
	public void setColour(Color gcol);
}
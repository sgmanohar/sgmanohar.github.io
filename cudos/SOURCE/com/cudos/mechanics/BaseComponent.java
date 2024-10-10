
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;

import java.awt.*;
import javax.swing.*;

public abstract class BaseComponent implements java.io.Serializable{


	public abstract void paint(Graphics g);
	boolean isSelected;
	public void setSelected(boolean sel){isSelected=sel;}
	public boolean isSelected(){return isSelected;}

	abstract public boolean contains(Point p);
	public double getNaturalLength(){return naturalLength;}
	public void setNaturalLength(double d){naturalLength=d;}
	double naturalLength=80;

	public abstract JPanel getPanel();

  public BaseComponent() {
  }
}
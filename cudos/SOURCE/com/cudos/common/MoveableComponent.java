
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

public interface MoveableComponent {
	public void moveto(int dx,int dy);
	public void setSelected(boolean b);
	public boolean isSelected();
	public String getname();
//	public void setLocation(Point p);
}
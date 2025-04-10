
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

import javax.swing.JButton;
import java.awt.geom.Rectangle2D;
import java.awt.*;

public class Button3D extends JButton {

  public Button3D() {
	setForeground(getBackground());// effectively, don't draw the normal text!
  }

	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(getFont());
		String t=getText();
		Rectangle2D r=getFont().getStringBounds(t,0,t.length(),g.getFontRenderContext());
		int o=(getModel().isArmed())?1:0;
		CudosExhibit.getApplet(this).paintText3D(g,getText(),o+(int)(getWidth()-r.getWidth())/2,o+(int)(getHeight()+r.getHeight()/2)/2 );
	}
	public Dimension getPreferredSize(){
	/*	Graphics2D g=(Graphics2D)getGraphics();
		if(g!=null){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			Rectangle2D r=getFont().getStringBounds(getText(),0,getText().length(),g.getFontRenderContext());
			return new Dimension((int)r.getWidth()+8,(int)r.getHeight()+8);
		}
	*/	Dimension d=super.getPreferredSize();
		return new Dimension((int)d.getWidth()+14,(int)d.getHeight());
	}
}

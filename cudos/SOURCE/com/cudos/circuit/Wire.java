
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import java.awt.*;

public class Wire extends CircuitComponent {
	static int sid=0;
	int id;
	public void paintCircuit(Graphics g_){
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3));
		g.setColor(selected?Color.red:Color.black);
		g.drawLine(p1.x,p1.y,p2.x,p1.y);
		g.fillOval(p1.x-4,p1.y-4,8,8);
		g.fillOval(p2.x-4,p2.y-4,8,8);
	}

  public Wire(Circuitboard cb) {
	super(cb);
	cname.setText("Wire"+(id=sid++));
  }

  public Wire() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
  }
}
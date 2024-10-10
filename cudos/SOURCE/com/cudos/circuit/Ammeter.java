
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
import com.cudos.common.*;
import java.awt.*;
import javax.swing.border.*;

public class Ammeter extends CircuitComponent implements GraphableComponent{
	static int sid=0;
	int id;
	double maxDeflection=50E-3;
	double maxAngle=Math.PI/2*0.76;
  JLabel meter = new JLabel(){
	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		g.setColor(Color.black);
		int r=meter.getHeight()-3;
		int mid=meter.getWidth()/2;
		double angle=maxAngle*current/maxDeflection;
		if(angle>maxAngle)angle=maxAngle;else if(angle<-maxAngle)angle=-maxAngle;
		g.drawLine(mid,r,mid+(int)(r*Math.sin(angle)),
				 (int)(r*(1-Math.cos(angle)))  );
		g.drawString(unitString(-maxDeflection),15,75);
		g.drawString(unitString(maxDeflection),getWidth()-CudosExhibit.getApplet(this).getTextWidth(g,unitString(maxDeflection))-15,75);
		g.drawString(unitString(0),(getWidth()-CudosExhibit.getApplet(this).getTextWidth(g,unitString(0)))/2,24);
	}
  };
  JTextField tcurrent = new JTextField();
  JLabel graphcolour = new JLabel();
  Border border1;
  Border border2;
  public Ammeter(Circuitboard c) {
	super(c);
	cname.setText("Ammeter"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Ammeter.gif");
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	public void process(){
		tcurrent.setText(unitString(current));
		repaint();
	}
	String getUnit(){return "A";}
  public Ammeter() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(3, 3, 3),new Color(4, 4, 4),Color.black,Color.black);
    border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    meter.setIcon(new ImageIcon(CudosExhibit.getApplet(getCircuitboard()).getImage("resources/ICONS/Meter.GIF")));
    this.setPreferredSize(new Dimension(205, 180));
    tcurrent.setPreferredSize(new Dimension(80, 21));
    tcurrent.setText("0 A");
    graphcolour.setBackground(Color.black);
    graphcolour.setFont(new java.awt.Font("Dialog", 1, 12));
    graphcolour.setForeground(Color.red);
    graphcolour.setBorder(border2);
    graphcolour.setOpaque(true);
    graphcolour.setPreferredSize(new Dimension(85, 21));
    graphcolour.setHorizontalAlignment(SwingConstants.CENTER);
    graphcolour.setHorizontalTextPosition(SwingConstants.CENTER);
    graphcolour.setText("Graph colour");
    this.add(meter, null);
    this.add(tcurrent, null);
    this.add(graphcolour, null);
  }

	//GraphableComponent
	public double getGraphableValue(){return current;}
	public double getGraphableMin(){return -maxDeflection;}
	public double getGraphableMax(){return maxDeflection;}
	public void setColour(Color gcol){
		graphcolour.setForeground(gcol);
	}
}


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
import javax.swing.*;
import com.cudos.common.*;
import javax.swing.border.*;

public class Voltmeter extends CircuitComponent implements GraphableComponent{
	static int sid=0;
	int id;

	double maxDeflection=5;		//volts
	double maxAngle=Math.PI/2*0.76;
  JTextField tvoltage = new JTextField();
  JLabel meter = new JLabel(){
	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		g.setColor(Color.black);
		int r=meter.getHeight();
		int mid=meter.getWidth()/2;
		double angle=maxAngle*voltage/maxDeflection;
		if(Double.isNaN( angle ))angle=0;	//no voltage=0V
		if(angle>maxAngle)angle=maxAngle;else if(angle<-maxAngle)angle=-maxAngle;
		g.drawLine(mid,r,mid+(int)(r*Math.sin(angle)),
				 (int)(r*(1-Math.cos(angle)))  );
		g.drawString(unitString(-maxDeflection),15,75);
		g.drawString(unitString(maxDeflection),getWidth()-CudosExhibit.getApplet(this).getTextWidth(g,unitString(maxDeflection))-15,75);
		g.drawString(unitString(0),(getWidth()-CudosExhibit.getApplet(this).getTextWidth(g,unitString(0)))/2,24);
	}
  };


  public Voltmeter(Circuitboard c) {
	super(c);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cname.setText("Voltmeter"+(id=sid++));
	i=CudosExhibit.getApplet(getCircuitboard()).getImage("resources/icons/Voltmeter.gif");
  }
	String getUnit(){return "V";}
	double voltage=Double.NaN;
  JLabel graphcolour = new JLabel();
  Border border1;
	public double getResistance(){return 1000000;}
	public void process(){
		double l,r;
		if((l=cb.rail[c1].voltage)!=Double.NaN && (r=cb.rail[c2].voltage)!=Double.NaN)
			voltage=r-l;
		else voltage=Double.NaN;
		tvoltage.setText(unitString(voltage));
		repaint();
	}
	public boolean passesCurrent(){return false;}

  private void jbInit() throws Exception {
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    tvoltage.setPreferredSize(new Dimension(80, 21));
    tvoltage.setText("0 V");
    meter.setPreferredSize(new Dimension(200, 107));
    meter.setIcon(new ImageIcon(CudosExhibit.getApplet(getCircuitboard()).getImage("resources/ICONS/Meter.GIF")));
    this.setPreferredSize(new Dimension(205, 180));
    graphcolour.setBorder(border1);
    graphcolour.setOpaque(true);
    graphcolour.setPreferredSize(new Dimension(85, 21));
    graphcolour.setHorizontalAlignment(SwingConstants.CENTER);
    graphcolour.setHorizontalTextPosition(SwingConstants.CENTER);
    graphcolour.setBackground(Color.black);
    graphcolour.setFont(new java.awt.Font("Dialog", 1, 12));
    graphcolour.setForeground(Color.red);
    graphcolour.setText("Graph colour");
    this.add(meter, null);
    this.add(tvoltage, null);
    this.add(graphcolour, null);
  }
	//GraphableComponent
	public double getGraphableValue(){
		if(Double.isNaN( voltage ))return 0;
		else return voltage;}
	public double getGraphableMin(){return -maxDeflection;}
	public double getGraphableMax(){return maxDeflection;}
	public void setColour(Color gcol){
		graphcolour.setForeground(gcol);
	}
}

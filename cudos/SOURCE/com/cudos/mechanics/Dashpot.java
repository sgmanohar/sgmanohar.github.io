
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.event.*;

public class Dashpot extends MechanicsComponent {

	public Dashpot() {
		createPanel();
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		createPanel();
	}


	transient JPanel panel;
	transient JSlider dslider;
	transient JLabel dlabel=new JLabel();
	void createPanel(){
		panel=new JPanel();
		dslider=new JSlider();
		dlabel=new JLabel("Damping");
		dslider.setMinimum(1);
		dslider.setPreferredSize(new Dimension(50,30));
		dslider.setValue((int)(damping*20));
		dslider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				damping=dslider.getValue()/20.;
			}
		});
		panel.add(dlabel);
		panel.add(dslider);
	}


	public boolean contains(Point p) {
		Rectangle bounds=new Rectangle(x-dx, (int)top.y+4, dx*2, (int)(bottom.y-top.y-8));
		return bounds.contains(p);
	}




	int dx=20;
	int dy=5;	//height between bottom of dashpot and piston
	int ddx=2;	//width of gap between piston and cylinder
	int lineLen=10;	//length of line from base of cylinder


	double maximumLength=2*getNaturalLength()-2*dy-lineLen;
	double minimumLength=getNaturalLength()+dy;

	public void paint(Graphics g) {
		int y1=(int)top.y;
		int y2=(int)bottom.y;
		g.drawLine(top.x,y1,x,y1);	//crosspiecesx
		g.drawLine(x, y2, bottom.x, y2);

		int piston=(int)(y1+naturalLength-dy-lineLen);
		g.drawLine(x,y1, x, piston);
		g.drawLine(x-dx+ddx, piston, x+dx-ddx, piston);

		int topdash=(int)(y2-naturalLength+dy);
		int botdash=(int)(y2-lineLen);
		g.drawLine(x-dx, topdash, x-dx, botdash);
		g.drawLine(x-dx, botdash, x+dx, botdash);
		g.drawLine(x+dx, botdash, x+dx, topdash);

		g.drawLine(x,botdash, x, y2);
	}

	public JPanel getPanel(){
		return panel;
	}


	double oldLen=naturalLength;
	double damping=0.4;
	public double getTension(){
		double newLen=bottom.y-top.y;
		double tension=damping*(newLen-oldLen)/Node.dt;
		oldLen=newLen;
		if(newLen>maximumLength || newLen<minimumLength){
			return tension + bottom.getImpulsiveStop(top);
		}
		return tension;
	}
	public boolean canResizeBy(double dl){
		return dl<0?(getLength()+dl>=minimumLength):(getLength()+dl<=maximumLength);
	}
}
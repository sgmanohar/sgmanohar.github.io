
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
import javax.swing.event.*;
import java.io.*;

public class Spring extends MechanicsComponent {

	public Spring() {
		createPanel();
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		createPanel();
	}


	transient JPanel panel;
	transient JSlider lslider, kslider;
	transient JLabel llabel, klabel;
	void createPanel(){
		panel=new JPanel();
		lslider=new JSlider();
		kslider=new JSlider();
		llabel=new JLabel("Natural length");
		klabel=new JLabel("Spring constant");
		lslider.setPreferredSize(new Dimension(100,30));
		lslider.setMinimum(30);
		lslider.setMaximum(150);
		lslider.setValue((int)getNaturalLength());

		kslider.setPreferredSize(new Dimension(100,30));
		kslider.setMaximum(150);
		kslider.setValue((int)(getSpringConstant()*10));
		panel.add(llabel);
		panel.add(lslider);
		panel.add(klabel);
		panel.add(kslider);
		lslider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				setNaturalLength(lslider.getValue());
			}
		});
		kslider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				setSpringConstant(kslider.getValue()/10.);
			}
		});
	}


	double linefrac=0.1;
	int nzigs=7;
	int dx=20;

	double minimumLength=2*linefrac*getNaturalLength();

	public void paint(Graphics g) {

		int y1=(int)top.y;
		int y2=(int)bottom.y;
		g.drawLine(top.x,y1,x,y1);	//crosspiecesx
		g.drawLine(x, y2, bottom.x, y2);

		double dy=(y2-y1)*(1-2*linefrac)/(nzigs+0.5);
		Point last=new Point(x,y1+(int)((y2-y1)*linefrac+0.5*dy)), next;
		g.drawLine(x,y1,last.x, last.y);

		double cy=y1+(y2-y1)*linefrac;
		for(int i=0;i<nzigs-1;i++){
			cy+=dy;
			if(i%2==0){
				next=new Point(x+dx,(int) cy);
			}else{
				next=new Point(x-dx, (int)cy);
			}
			g.drawLine(last.x,last.y, next.x, next.y);
			last=next;
		}
		next=new Point(x, y1+(int)((y2-y1)*(1-linefrac)-dy));
		g.drawLine(last.x, last.y, next.x, next.y);
		g.drawLine(next.x,next.y, x,y2);
	}
	public boolean contains(Point p){
		Rectangle bounds=new Rectangle(x-dx, (int)top.y+4, dx*2, (int)(bottom.y-top.y-8));
		return bounds.contains(p);
	}
	public JPanel getPanel(){
		return panel;
	}

	public double getSpringConstant(){return springConstant;}
	public void setSpringConstant(double d){springConstant=d;}
	double springConstant=2;
	public double getTension(){
		double length=bottom.y-top.y;
		double tension=springConstant*(length-naturalLength);
		if(length<minimumLength){
			tension+=bottom.getImpulsiveStop(top);
		}
		return tension;
	}
	public boolean canResizeBy(double dl){return
		dl<0?(getLength()>=minimumLength):true;
	}
}
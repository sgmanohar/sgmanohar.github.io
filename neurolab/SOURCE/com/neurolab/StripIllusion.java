
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.neurolab.common.*;

public class StripIllusion extends FullScreenExhibit {

  public StripIllusion() {
  }
	public String getExhibitName(){return "Strip illusion";}
	int bands=400;
	JPanel mainpanel=new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			double bw=getWidth()/(double)bands;	//width of one band
			for(int x=0;x<bands;x++){
				double f=(reverse? x:bands-1-x) / (double)bands ;
if(f<0 || f>=1)System.out.println(x);
				g.setColor( mix(getForeground(),getBackground(),
					f));
				g.fillRect((int)(x*bw),0,bw<1?1:1+(int)bw,getHeight());
			}
			g.setColor(mix(getForeground(),getBackground(), 0.5));
			g.fillRect(getWidth()/9, getHeight()/3, getWidth()*7/9, getHeight()/8);
		}
	};
	Color mix(Color a, Color b, double f){
		return new Color(
			(int)(f*a.getRed()+(1-f)*b.getRed()),
			(int)(f*a.getGreen()+(1-f)*b.getGreen()),
			(int)(f*a.getBlue()+(1-f)*b.getBlue()) );
	}

	public void init(){
		super.init();
		parentExhibit="com.neurolab.LateralInhibition";
		mainpanel.setForeground(Color.green);
		mainpanel.setBackground(Color.black);
		frame.getContentPane().add(mainpanel,BorderLayout.CENTER);
		timer.start();
		frame.validate();
	}

	boolean reverse=false;
	Timer timer=new Timer(3000,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			reverse=reverse==false;
			mainpanel.repaint();
		}
	});
	public void doClose(){
		super.doClose();
		timer.stop();
	}
	public void close() {timer.stop();}
}

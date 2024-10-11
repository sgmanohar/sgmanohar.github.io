
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.neurolab.common.FullScreenExhibit;

public class CornsweetIllusion extends FullScreenExhibit{

  public CornsweetIllusion() {
  }
	public String getExhibitName(){return "Cornsweet illusion";}

	double centre=0.21;
	int bands=80;
	JPanel mainpanel=new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(mix(getForeground(),getBackground(), 0.5));
			g.fillRect(0,0,getWidth(),getHeight());
			int mid=getWidth()/2;
			double mw=getWidth()*centre;
			double bw=mw/bands;
			for(int x=0;x<bands;x++){
				g.setColor( mix(getForeground(),getBackground(),
					(reverse? x:bands-1-x) / (double)bands ));
				if(x<bands/2)
				  g.fillRect((int)(mid+x*bw),0,1+(int)bw,getHeight());
				else
				  g.fillRect((int)(mid-mw/2+(x-bands/2)*bw),0,1+(int)bw,getHeight());
			}
		}
	};
	Color mix(Color a, Color b, double f){
		return new Color(
			(int)(f*a.getRed()+(1-f)*b.getRed()),
			(int)(f*a.getGreen()+(1-f)*b.getGreen()),
			(int)(f*a.getBlue()+(1-f)*b.getBlue()) );
	}

	public void init(){
		parentExhibit="com.neurolab.LateralInhibition";
		super.init();
		mainpanel.setForeground(new Color(128,128,128));
		mainpanel.setBackground(new Color(192,192,192));
		frame.getContentPane().add(mainpanel,BorderLayout.CENTER);
		timer.start();
		frame.validate();
	}

	boolean reverse=false;
	Timer timer=new Timer(2000,new ActionListener(){
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
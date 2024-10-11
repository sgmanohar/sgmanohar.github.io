
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.neurolab.common.FullScreenExhibit;

public class WaterfallIllusion extends FullScreenExhibit {

  public WaterfallIllusion() {
  }
	public String getExhibitName(){return "Waterfall illusion";}
	int phase=0;
	int nsquares=20;
	int speed=10;
	JPanel mainpanel=new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			if(!testing){
				int maxr=Math.min(getHeight(),getWidth());
				int thick=maxr/nsquares;
				int a=0;
				for(int i=-2;i<nsquares;i++){
				  boolean col=(Math.abs(i+100)%2)>0;
					g.setColor(col?getForeground():getBackground());
					a=i*thick+phase%(2*thick);	//inset from edge
					if(maxr>2*a) {
					  g.fillRect(a,a,maxr-2*a,maxr-2*a);
					}
				}
			}else{
				g.setColor(Color.black);
				for(int z=0;z<800;z++){
					g.fillRect((int)(getWidth()*Math.random()),
					  (int)(getHeight()*Math.random()),
					  10,10);
				}
			}
		}
	};

	JPanel controls=new JPanel();
	JSlider slider=new JSlider();
	JToggleButton testbutton=new JToggleButton("Test");
	public void init(){
		super.init();
		parentExhibit="com.neurolab.Adaptation";
		mainpanel.setForeground(Color.black);
		mainpanel.setBackground(Color.white);
		controls.add(testbutton);
		testbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(testbutton.isSelected())test();
				else adapt();
			}
		});
		controls.add(new JLabel("Speed"));
		controls.add(slider);
		slider.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){
			speed=slider.getValue()/5;
		}});
		slider.setValue(10);

frame.setSize(400,450);
		frame.getContentPane().add(mainpanel,BorderLayout.CENTER);
		frame.getContentPane().add(controls,BorderLayout.SOUTH);
		timer.start();
		frame.validate();
	}

	boolean reverse=false;
	Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			phase+=speed;
			mainpanel.repaint();
		}
	});
	public void doClose(){
		super.doClose();
		timer.stop();
	}
	void test(){
		timer.stop();
		testing=true;
		mainpanel.repaint();
	}
	void adapt(){
		testing=false;
		timer.start();
	}
	boolean testing=false;
  public void close(){
    timer.stop();
  }
}
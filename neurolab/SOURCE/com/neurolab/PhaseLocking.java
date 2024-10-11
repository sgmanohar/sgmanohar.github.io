
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.neurolab.common.ActionPotentials;
import com.neurolab.common.JPanel0;
import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;

public class PhaseLocking extends NeurolabExhibit implements ActionListener{

	public static int numcells=20;
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	JLabel jLabel1 = new JLabel();
	JSlider frequency = new JSlider();
	JLabel jLabel2 = new JLabel();
	JSlider intensity = new JSlider();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	JPanel jPanel3 = new JPanel0();
	JPanel jPanel4 = new JPanel0();
	Border border2;
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	Label3D label3D4 = new Label3D();
	Label3D label3D5 = new Label3D();
	Border border3;
	Border border4;
	Oscilloscope osc = new Oscilloscope(numcells+2,this);
	BorderLayout borderLayout3 = new BorderLayout();
	public String getExhibitName(){return "Phase Locking";}
	public PhaseLocking() {
	}
	Timer timer=new Timer(50,this);
	int[] base=new int[numcells+2];
	Color[] cols=new Color[numcells+2];
	ActionPotentials ap;
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
  	base[0]=30;cols[0]=Color.red;
  	for(int i=0;i<numcells;i++){
  		base[i+1]=55+i*110/numcells;
  		cols[i+1]=Color.cyan;
  	}
  	base[numcells+1]=200;cols[numcells+1]=Color.yellow;
  	osc.setBaseY(base);
  	osc.setColors(cols);
  	osc.timer.setDelay(50);
  	osc.xSpeed=2;
  	timer.start();
  	new Thread(new Runnable() {public void run() { try{Thread.sleep(1500);}catch(Exception e) {}
  	  SwingUtilities.invokeLater(new Runnable() {public void run() { osc.sweep.doClick() ; }});
  	}}).start();
  	ap=new ActionPotentials();
  	ap.setRate(0);

	}
	//globals
	int y[]=new int[numcells+2];
	int refractory[]=new int[numcells];
	int t=0;
	Random r=new Random();

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			if(osc!=null)osc.clear.doClick();
		}else{	//timer
			int total=0;
			double s=0.7*intensity.getValue()*(Math.sin(++t*2*Math.PI*frequency.getValue()/2000));
			y[0]=-(int)s;
			for(int i=0;i<numcells;i++){	//for each cell
				y[i+1]=0;
				if(refractory[i]>0)refractory[i]--;	// check not refractory
				else{
					//if(s>35+r.nextInt()%65){		// crosses random threshold
				  if(s>(-1+Math.random()*100)){    
						refractory[i]=20+(int)(Math.random()*40);
						y[i+1]=-20;
					}
				}
				total+=y[i+1];
			}
			y[numcells+1]=total;
			if(total<0 & ap!=null) {
			  ap.doSingleAP();
			}
			osc.setPosY(y);	//set all positions
		}
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(5,5,5,5);
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		border3 = BorderFactory.createLineBorder(Color.lightGray,1);
		border4 = BorderFactory.createLineBorder(Color.lightGray,1);
		jLabel1.setText("Frequency");
		frequency.setBackground(systemGray);
		jLabel2.setText("Intensity");
		frequency.setMaximum(200);
		frequency.setMinimum(20);
		frequency.setPreferredSize(new Dimension(100, 24));
		frequency.setBorder(border3);
		intensity.setPreferredSize(new Dimension(100, 24));
		intensity.setBorder(border4);
		intensity.setBackground(systemGray);
		jPanel2.setLayout(borderLayout2);
		jPanel2.setBorder(border1);
		jPanel4.setBorder(border2);
		jPanel4.setLayout(borderLayout3);
		jPanel3.setPreferredSize(new Dimension(110, 10));
		jPanel3.setLayout(null);
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D1.setText("Stimulus");
		label3D1.setBounds(new Rectangle(19, 25, 83, 22));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D2.setText("Individual");
		label3D2.setBounds(new Rectangle(8, 96, 98, 27));
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D3.setText("fibres");
		label3D3.setBounds(new Rectangle(47, 120, 56, 22));
		label3D4.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D4.setText("Total");
		label3D4.setBounds(new Rectangle(50, 170, 51, 27));
		label3D5.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D5.setText("activity");
		label3D5.setBounds(new Rectangle(30, 192, 71, 24));
		jPanel1.add(jLabel1, null);
		jPanel1.add(frequency, null);
		jPanel1.add(jLabel2, null);
		jPanel1.add(intensity, null);
		jPanel1.add(returnButton1, null);
		jPanel2.add(jPanel3, BorderLayout.WEST);
		jPanel3.add(label3D1, null);
		jPanel3.add(label3D4, null);
		jPanel3.add(label3D5, null);
		jPanel3.add(label3D2, null);
		jPanel3.add(label3D3, null);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(osc, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.SOUTH);
		getMainContainer().add(jPanel2, BorderLayout.CENTER);
	}
	public void finalize() throws Throwable{
	  close();
    super.finalize();
	}
	public void close() {
    timer.stop();
	}
}

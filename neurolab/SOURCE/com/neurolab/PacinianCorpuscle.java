
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;

import com.neurolab.circuit.CircuitDashpot;
import com.neurolab.circuit.CircuitPanel;
import com.neurolab.circuit.CircuitResistor;
import com.neurolab.circuit.CircuitWire;
import com.neurolab.common.JPanel0;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.SignalGenerator;
import com.neurolab.common.SignalListener;

public class PacinianCorpuscle extends NeurolabExhibit implements ActionListener,SignalListener{
 public String getExhibitName() {
		return "Pancinian Corpuscle";
	}
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	ReturnButton returnButton1 = new ReturnButton();
	BorderLayout borderLayout3 = new BorderLayout();
	SignalGenerator signalGenerator = new SignalGenerator();
	Border border1;
	TitledBorder titledBorder1;
	Border border2;
	JPanel jPanel3 = new JPanel0();
	Border border3;
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel5 = new JPanel();
	Oscilloscope osc = new Oscilloscope(2,this);
	BorderLayout borderLayout5 = new BorderLayout();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel graphic = new JPanel0();
	CircuitPanel circuit = new CircuitPanel();


	JRadioButton manualRadio=new JRadioButton("Manual");

	int[] base={80,200};
	public PacinianCorpuscle() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		osc.setBaseY(base);
		osc.timer.setDelay(50);
		osc.xSpeed=2;
		osc.resweepOnClear=false;
		Timer nt=new Timer(50,new ActionListener(){public void actionPerformed(ActionEvent e){
	initCircuit();
		} });nt.setRepeats(false);nt.start();
		signalGenerator.sinusoidal.setSelected(true);
		signalGenerator.setSignalListener(this);	// add a listener
		signalGenerator.removeOption(signalGenerator.squareramp);

		manualRadio.setBackground(systemGray);
		signalGenerator.addOption(manualRadio);

		signalGenerator.timer.start();
		circuit.setBackground(systemGray);
	}

	public void initCircuit(){
		int mid=circuit.getWidth()/2;
		int base=circuit.getHeight()-10;
		int[][] nodes=new int[][]{{0,base},{mid,base},{mid*2,base},
						{mid,base-20},
						{mid,base-130},
						{mid,base-140},
						{mid,base-250} };
		Point np[]=new Point[nodes.length];
		for(int i=0;i<nodes.length;i++){
			np[i]=new Point(nodes[i][0],nodes[i][1]);
			circuit.nodes.addElement(np[i]);
		}
		CircuitDashpot cd;
		circuit.componentlist.addElement(new CircuitWire(circuit,0,2));
		circuit.componentlist.addElement(new CircuitWire(circuit,1,3));
		circuit.componentlist.addElement(new CircuitResistor(circuit,3,4));
		circuit.componentlist.addElement(new CircuitWire(circuit,4,5));
		circuit.componentlist.addElement(cd=new CircuitDashpot(circuit,5,6));

		circuit.addMouseListener(mia);
		circuit.addMouseMotionListener(mia);
		cd.height=80;
	}
	MouseInputAdapter mia=new MouseInputAdapter(){
		int ox;
		int oy=0;
		Timer timer=new Timer(50,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				signalEvent(oy);
			}
		} );
		public void mousePressed(MouseEvent e){
			ox=e.getY()+oy;
			if(signalGenerator.amplitude.getValue()==0)timer.start();
		}
		public void mouseReleased(MouseEvent e){
			timer.stop();
		}
		public void mouseDragged(MouseEvent e){
			oy=-(e.getY()-ox);
			if(oy>70)oy=70;if(oy<-80)oy=-80;
			if(manualRadio.isSelected()) synchronized(this){
				manualSignal=true;
				signalEvent(oy);
				manualSignal=false;
			}
		}
	};

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			//if(osc!=null)osc.clear.doClick();
		}
	}
	public double creep,stim;
	boolean manualSignal=false;
	public void signalEvent(double s){
		synchronized(this){ 	//wait for any manual signal to stop
			if(!(manualRadio.isSelected() && !manualSignal)) stim=s;
		}
		double X,Y;
		X=3+3*stim;
		Y=X+10-creep;
		creep+=(Y-20)/3;
		int[] y=new int[2];
		y[0]=-(int)(stim*1.5-95);
		y[1]=-(int)(Y/2-65);
		osc.setPosY(y);
		int mid=circuit.getWidth()/2;
		int base=circuit.getHeight()-10;
		circuit.moveNode(4,new Point(mid,(int)(base-130-Y/6)));
		circuit.moveNode(5,new Point(mid,(int)(base-140-Y/6)));
		circuit.moveNode(6,new Point(mid,(int)(base-275-X/6)));
		circuit.repaint();
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border1,"Stimulus");
		border2 = BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Stimulus"),BorderFactory.createEmptyBorder(2,2,2,2));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(5,5,5,5));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		signalGenerator.setBorder(border2);
		signalGenerator.setPreferredSize(new Dimension(130, 100));
		jPanel3.setBorder(border3);
		jPanel3.setLayout(borderLayout4);
		graphic.setPreferredSize(new Dimension(80, 10));
		graphic.setLayout(borderLayout6);
		jPanel5.setLayout(borderLayout5);
		jPanel1.add(jPanel2, BorderLayout.WEST);
		jPanel2.add(returnButton1, BorderLayout.SOUTH);
		jPanel2.add(signalGenerator, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(osc, BorderLayout.CENTER);
		jPanel3.add(graphic, BorderLayout.WEST);
		signalGenerator.setBackground(systemGray);
		graphic.add(circuit, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		signalGenerator.timer.stop();
		osc.timer.stop();
	}
}
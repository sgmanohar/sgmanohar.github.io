
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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;

public class EyeMovements extends NeurolabExhibit {
 public String getExhibitName() {
		return "Eye Movements";
	}

	private final static String[][] strings=
		{	{"Head",   "velocity", "Eye", "position", "20", "5"},
			{"Target", "velocity", "Eye", "position", "20", "2"},
			{"Target", "position", "Eye", "position", "20", "2"},
			{"Target", "distance", "Vergence", "",    "2",  "1"},
			{"Target", "position", "Eye", "position", "20", "1"},
			{"Target", "position", "Eye", "position", "0.2","2"},
		};
	private JRadioButton[] radios=new JRadioButton[6];
	private Label3D[] labels=new Label3D[4];

	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JButton fixationdemo = new JButton();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel4 = new JPanel0();
	GridLayout gridLayout2 = new GridLayout();
	JRadioButton jRadioButton1 = new JRadioButton0();
	JRadioButton jRadioButton2 = new JRadioButton0();
	JRadioButton jRadioButton3 = new JRadioButton0();
	JRadioButton jRadioButton4 = new JRadioButton0();
	JRadioButton jRadioButton5 = new JRadioButton0();
	JRadioButton jRadioButton6 = new JRadioButton0();
	JPanel jPanel5 = new JPanel0();
	Border border1;
	JPanel jPanel6 = new JPanel0();
	BorderLayout borderLayout4 = new BorderLayout();
	Border border2;
	Border border3;
	Border border4;
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel7 = new JPanel0();
	Border border5;
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	Label3D label3D4 = new Label3D();

	int t=0;
	Oscilloscope osc = new Oscilloscope(2,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if(osc!=null){
				osc.clear.doClick();
				osc.setPosY(new int[]{0,0});
			}
			X=0;Y=0;dy=0;cn=0;ct=0;
			t=0;
		}
	} ) {
		public void drawScreenElements(Graphics g){
			int w=getGutter();
			int r=getSelectedRadio();
			g.setColor(Color.black);	//erase stuff
			g.fillRect(getWidth()-50-w,w+5,50-w,40);
			g.fillRect(getWidth()-40-w,getHeight()-60-2*w,45-2*w,40-2*w);
			g.setFont(getFont());
			g.setColor(Color.lightGray);		//scales
			g.drawLine(getWidth()-10-w,10+w,getWidth()-10-w,40+w);
			g.drawString(strings[r][4]+" deg",getWidth()-50-w,30+w);
			g.drawLine(getWidth()-60-w,getHeight()-45-2*w,getWidth()-10-w,getHeight()-45-2*w);
			g.drawString(strings[r][5]+" sec",getWidth()-40-w,getHeight()-30-2*w);

			g.setColor(Color.gray);		//baselines
			for(int i=0;i<2;i++){
				g.drawLine(w,baseY[i]+w,getWidth()-w,baseY[i]+w);
				g.drawString("0",5+w,baseY[i]+w+2);
			}
		}
	};

	Random rand=new Random();
	int[] y=new int[2];
	double Y=0,dy=0,X=0;
	int ct=0;

	Timer timer=new Timer(50,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			double k,ox;
			int r=getSelectedRadio();
			t++;
												if (t==1){    // Does something odd at start - but this doesn't cure it!
													X=0;
													Y=0;
													dy=0;
													}
			switch(r){
				case 0: // Vestibular
					X=((t>20)&&(t<150))?20:0;
					if(t==20)dy=10;
					else if(t==150)dy=-10;
					Y=nystag(dy*=0.97,8+rand.nextInt()%15);
					break;
				case 1:  // Optokinetic
					X=(t>20)&&(t<170)?10:0;
					dy=(12*dy+X)/15;
					Y=nystag(dy,8+rand.nextInt()%15);
					break;
				case 2:  // Pursuit
					ox=X;
					X=10*Math.sin(2*2*Math.PI*t/100);
					k=20-t/8;
																				if (k<0) k=0;
					dy=(k*dy+(X-ox+0.5-rand.nextDouble()))/(k+1); // Tau gradually shortens
					if((Math.abs(X-Y)>5)&&(rand.nextInt()%4<2))Y=X;
					Y+=dy;
					break;
				case 3:  // Vergence
					X=((t%100)>50)?-20:20;
					Y=(7*Y+X)/8;
					break;
				case 4:  // Saccades
					if(rand.nextDouble()>0.9){
						X=-10+5*(rand.nextInt()%5);	// -30 to 0 step 5
						ct=5+rand.nextInt()%5;	//latency
					}
					if(ct==0){
						if(Y>X+4)dy=-5;
						if(X-4>Y)dy=5;
						if((Y<X+5)&&(Y>X-5)){
							dy=X-Y;
							ct=8;	//refractory exact saccade
						}
						Y+=dy;
					}else ct--;
					break;
				case 5:  // Fixation
					X=0;
					dy=(dy-2+4*rand.nextDouble())/2;
					Y+=dy;
					if((Y>5)&&(rand.nextInt()%15<Y))Y=-1;
					if((Y<-5)&&(rand.nextInt()%15<-Y))Y=1;
					break;
			}
			y[0]=(int)(6*X);y[1]=(int)(6*Y);
			osc.setPosY(y);
		}
	} );
	int cn;
	public double nystag(double dy,double k){	//cannot modify dy, k can modify Y
		Y+=dy;
		if(cn==0){
			if(Y>k){
				Y=-k+1-2*rand.nextDouble();
				cn=3;
			}
			if(Y<-k){
				Y=k-1+2*rand.nextDouble();
				cn=3;
			}
		}else cn--;
		return Y;
	};

	public EyeMovements() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	myinit();
	}
	ButtonGroup bg=new ButtonGroup();
	int[] basey={60,110};
	public void myinit(){
		radios[0]=jRadioButton1;
		radios[1]=jRadioButton2;
		radios[2]=jRadioButton3;
		radios[3]=jRadioButton4;
		radios[4]=jRadioButton5;
		radios[5]=jRadioButton6;
		labels[0]=label3D1;
		labels[1]=label3D2;
		labels[2]=label3D3;
		labels[3]=label3D4;
		for(int i=0;i<6;i++){
			bg.add(radios[i]);	//add to buttongroup
		}

		radios[0].setSelected(true);	//needed for painting oscilloscope, setting labels
		osc.setBaseY(basey);
		osc.timer.setDelay(50);
		osc.xSpeed=2;
		setLabels();
		timer.start();
	}
	public int getSelectedRadio(){
		for(int i=0;i<6;i++){
			if(radios[i].isSelected())return i;
		}
		return -1;
	}
	public void setLabels(){
		int r=getSelectedRadio();
		for(int i=0;i<4;i++){
			labels[i].setText(strings[r][i]);
			labels[i].repaint();
		}
	}

	private void jbInit() throws Exception {
		fixationdemo.setBackground(systemGray);
		border1 = BorderFactory.createEmptyBorder(5,5,5,5);
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(5,5,5,5));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(5,5,5,5));
		border4 = BorderFactory.createEmptyBorder(5,5,5,5);
		border5 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(10,10,10,10));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		fixationdemo.setText("Demo of fixation movements");
		fixationdemo.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fixationdemo_actionPerformed(e);
			}
		});
		jPanel3.setLayout(gridLayout1);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(1);
		jPanel4.setLayout(gridLayout2);
		gridLayout2.setRows(3);
		gridLayout2.setColumns(2);
		jRadioButton1.setText("Vestibular");
		jRadioButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jRadioButton2.setText("Optokinesis");
		jRadioButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jRadioButton3.setText("Pursuit");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jRadioButton4.setText("Vergence");
		jRadioButton4.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jRadioButton5.setText("Saccades");
		jRadioButton5.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jRadioButton6.setText("Fixation");
		jRadioButton6.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radiochange(e);
			}
		});
		jPanel5.setBorder(border1);
		jPanel5.setLayout(borderLayout4);
		jPanel6.setBorder(border2);
		jPanel6.setLayout(borderLayout5);
		jPanel4.setBorder(border3);
		jPanel3.setBorder(border4);
		jPanel7.setPreferredSize(new Dimension(130, 10));
		jPanel7.setLayout(null);
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D1.setText("label3D1");
		label3D1.setBounds(new Rectangle(18, 24, 112, 27));
		label3D2.setBounds(new Rectangle(17, 51, 112, 27));
		label3D2.setText("label3D1");
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D3.setBounds(new Rectangle(18, 117, 112, 27));
		label3D3.setText("label3D1");
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D4.setBounds(new Rectangle(17, 143, 112, 27));
		label3D4.setText("label3D1");
		label3D4.setFont(new java.awt.Font("SansSerif", 1, 16));
		osc.setGutter(5);
		jPanel1.add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jPanel3, BorderLayout.EAST);
		jPanel3.add(fixationdemo, null);
		jPanel3.add(returnButton1, null);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jRadioButton1, null);
		jPanel4.add(jRadioButton4, null);
		jPanel4.add(jRadioButton2, null);
		jPanel4.add(jRadioButton5, null);
		jPanel4.add(jRadioButton3, null);
		jPanel4.add(jRadioButton6, null);
		jPanel5.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel7, BorderLayout.WEST);
		jPanel7.add(label3D4, null);
		jPanel7.add(label3D1, null);
		jPanel7.add(label3D2, null);
		jPanel7.add(label3D3, null);
		jPanel6.add(osc, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.SOUTH);
		getMainContainer().add(jPanel5, BorderLayout.CENTER);
	}

	void radiochange(ActionEvent e) {
		setLabels();
		osc.drawScreenElements(osc.graph.getGraphics());
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
		osc.timer.stop();
	}

	void fixationdemo_actionPerformed(ActionEvent e) {
		getHolder().setExhibit("com.neurolab.FixationMovements");
	}

}
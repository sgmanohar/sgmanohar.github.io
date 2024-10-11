
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.neurolab.common.AngleControl;
import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.PercentageBar;
import com.neurolab.common.ReturnButton;

public class LineLearning extends NeurolabExhibit{
	public String getExhibitName(){return "Learning lines";}

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JButton forget = new JButton();
	FlowLayout flowLayout1 = new FlowLayout();
	JPanel mainpanel = new JPanel0();
	FlowLayout flowLayout2 = new FlowLayout();
//  BorderLayout borderLayout3 = new BorderLayout();
//  BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel4 = new JPanel0();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel6 = new JPanel0();
	JPanel jPanel7 = new JPanel0();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel8 = new JPanel0();
	Border border1;
	BorderLayout borderLayout8 = new BorderLayout();
	AngleControl orientation = new AngleControl();
	BorderLayout borderLayout10 = new BorderLayout();
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel9 = new JPanel0();
	FlowLayout flowLayout3 = new FlowLayout();
	JPanel orientgraph = new JPanel0(){
	public void paint(Graphics g_){
		super.paint(g_);
		drawStim(this,g_);
	}
	};
	JToggleButton stimulusbutton = new JToggleButton();


	void drawStim(Component c, Graphics g){
		antiAlias(g);
		g.setColor(Color.white);
		setStrokeThickness(g,4);
		double  s=-Math.sin(orientation.getValue()*Math.PI/180),
			cs=Math.cos(orientation.getValue()*Math.PI/180);
		Dimension cd = c.getSize();
		g.drawLine(cd.width/2+(int)(cd.width*s),cd.height/2+(int)(cd.height*cs),
				 cd.width/2-(int)(cd.width*s),cd.height/2-(int)(cd.height*cs) );
	}

	public LineLearning() {
	}
	public void init(){
		super.init();
		try {
	myinit();
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	/*
	 stimulusbutton.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			beginstimulus();
		}
		public void mouseReleased(MouseEvent e){
			endstimulus();
		}
	});
	}
	*/
		stimulusbutton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
		  if   (!stimulusbutton.isSelected()) { endstimulus();  }
		  else { beginstimulus(); stimulusbutton.setEnabled(false); }
		}});
	}
		

	int nunits=5;
	UnitPanel[] unit=new UnitPanel[nunits];
	public void myinit(){
		for(int i=0;i<nunits;i++){
			unit[i]=new UnitPanel();
			mainpanel.add(unit[i]);
		}
		orientation.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e){orientgraph.repaint();}
		});
		timer2.setRepeats(false);
		forget_actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Forget"));
	}

	private void jbInit() throws Exception {
		forget.setBackground(systemGray);
		stimulusbutton.setBackground(systemGray);
		contin.setBackground(systemGray);
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(2,2,2,2));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(flowLayout1);
		forget.setText("Forget");
		forget.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				forget_actionPerformed(e);
			}
		});
		mainpanel.setLayout(flowLayout2);
		jPanel4.setLayout(borderLayout5);
		jPanel6.setBorder(border1);
		jPanel6.setLayout(borderLayout8);
		jPanel7.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(2,2,2,2)));
		jPanel7.setPreferredSize(new Dimension(170, 101));
		jPanel7.setLayout(borderLayout6);
		orientation.setPreferredSize(new Dimension(55, 55));
		orientation.setPrefix("                       ");
		jPanel8.setLayout(borderLayout10);
		orientgraph.setLayout(borderLayout7);
		orientgraph.setBackground(Color.black);
		orientgraph.setPreferredSize(new Dimension(90, 85));
		stimulusbutton.setPreferredSize(new Dimension(100, 27));
		stimulusbutton.setText("Stimulus");
		jPanel8.setPreferredSize(new Dimension(85, 57));
		jPanel9.setLayout(flowLayout3);
		contin.setText("Auto-train");
		contin.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				contin_actionPerformed(e);
			}
		});
		jPanel1.add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(contin, null);
		jPanel2.add(forget, null);
		jPanel2.add(returnButton1, null);
		jPanel1.add(mainpanel, BorderLayout.CENTER);
//    jPanel3.add(unitpanel, null);
		mainpanel.add(jPanel4, null);
		jPanel4.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(jPanel6, BorderLayout.WEST);
		jPanel6.add(orientgraph, BorderLayout.NORTH);
		jPanel7.add(jPanel8, BorderLayout.EAST);
		jPanel8.add(stimulusbutton, BorderLayout.SOUTH);
		jPanel8.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(orientation, null);
	jPanel4.setPreferredSize(new Dimension(195, 100));
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}



/// class unit panel
	Random rand=new Random();
	class UnitPanel extends JPanel0{
		JRadioButton response = new JRadioButton0();
		PercentageBar percentage = new PercentageBar();
		JPanel jP5 = new JPanel0();
		JPanel jPanel111 = new JPanel0();
		JPanel graph = new JPanel0(){
			public void paint(Graphics g){
				super.paint(g);
				if(dots[0]==null)initDots();	// first time round, set up dots.
				for(int i=0;i<ndots;i++){
					g.setColor(new Color((int)(255*strength[i]),0,(int)(255*(1-strength[i]))));
					g.fillOval(dots[i].x-dr,dots[i].y-dr,2*dr,2*dr);
				}
				if(lineVisible)drawStim(graph,g);
			}
		};
		int ndots=40;
		int dr=3;
		Point[] dots=new Point[ndots];
		double[] strength=new double[ndots];
		double[] z=new double[ndots];
		double activity,increment;
		boolean lineVisible;
		public UnitPanel(){
			setLayout(new BorderLayout());
					add(percentage, BorderLayout.CENTER);
			add(response, BorderLayout.SOUTH);
			add(jP5, BorderLayout.NORTH);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,new Color(255, 255, 235),new Color(94, 93, 80),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(4,4,4,4)));
			jP5.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(2,2,2,2)));
			jP5.setPreferredSize(new Dimension(90, 90));
			jP5.setLayout(new BorderLayout());
			percentage.setPreferredSize(new Dimension(70, 10));
			response.setEnabled(false);
			response.setText("Response");
			response.setFont(new java.awt.Font("Dialog", 1, 14));
			jP5.add(jPanel111, BorderLayout.CENTER);
			jPanel111.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)));
			jPanel111.setLayout(new BorderLayout());
			jPanel111.add(graph, BorderLayout.CENTER);
			graph.setBackground(Color.black);
			percentage.color=percentageIncomplete;
		}
		public void initDots(){
			for(int i=0;i<ndots;i++){
				dots[i]=new Point( dr+(rand.nextInt()%(graph.getWidth()-2*dr)),dr+(rand.nextInt()%graph.getHeight()) );
			}
		}
		public void initCalc(){
			increment=activity=0;
			response.setSelected(false);
			double s=Math.sin(Math.PI*orientation.getValue()/180),cs=Math.cos(orientation.getValue()*Math.PI/180);
			for(int i=0;i<ndots;i++){	//set up increment
				z[i]=(dots[i].y-graph.getHeight()/2)*s + (dots[i].x-graph.getWidth()/2)*cs;
				if(Math.abs(z[i])<8)increment+=strength[i];
			};
		}
		public void forget(){
			for(int i=0;i<ndots;i++){
				z[i]=0;
				strength[i]=0.5;
			}
			dots[0]=null;	//this randomises all the dots
			graph.repaint();
		}
		public boolean calc(){
			activity+=increment;
			percentage.setValue((activity>100)?100:(int)activity);
			if(activity>=100){
				response.setSelected(true);
				response.setEnabled(true);
				percentage.color=Color.red; percentage.repaint();
				for(int i=0;i<ndots;i++){		//learning
					if(Math.abs(z[i])<8)strength[i]+=0.5*(1-strength[i]);
					else strength[i]*=0.5;		//unlearn
				}
				graph.repaint();
				return true;
			}else return false;
		}
		Color percentageIncomplete=Color.blue; // i'd like to make this static
	}
///end of class



	Timer timer=new Timer(100,new ActionListener(){public void actionPerformed(ActionEvent e){
		int fired=-1;
		for(int i=0;i<nunits;i++){
			if(unit[i].calc())fired=i;
		}
		if(fired>=0){
			timer.stop();
			timer2.start();
			stimulusbutton.setSelected(false);  
		}
	}});
	// end of outcome period
	Timer timer2=new Timer(1800,new ActionListener(){public void actionPerformed(ActionEvent e){
		for(int i=0;i<nunits;i++){
			unit[i].lineVisible=false;
			unit[i].response.setSelected(false);
			unit[i].response.setEnabled(false);
			unit[i].percentage.color=unit[i].percentageIncomplete; unit[i].percentage.repaint();
			unit[i].graph.repaint();
		}
		stimulusbutton.setEnabled(true);// button up
		if(continuous){
			orientation.setValue(rand.nextInt()%180);
			orientation.repaint();
			orientgraph.repaint();
			autotrainbegin();
		}
	}});
	JButton contin = new JButton();

	public void beginstimulus(){
		for(int i=0;i<nunits;i++){
			unit[i].lineVisible=true;
			unit[i].graph.repaint();
			unit[i].initCalc();
		}
		stimulusbutton.setSelected(true); stimulusbutton.setEnabled(false);
		timer.start();
	}
	public void endstimulus(){
		for(int i=0;i<nunits;i++){
			unit[i].lineVisible=false;
			unit[i].graph.repaint();
		}
		timer.stop();
	}
	public void autotrainbegin(){
		beginstimulus();
	}

	void forget_actionPerformed(ActionEvent e) {
	for(int i=0;i<nunits;i++){
		unit[i].forget();
	}
	}
	boolean continuous=false;
	void contin_actionPerformed(ActionEvent e) {
	continuous=continuous==false;
	if(continuous){
		contin.setText("Stop");
		autotrainbegin();
	}else contin.setText("Auto-train");
	}
	public void close() {
	  timer.stop();
	  timer2.stop();
	}
}
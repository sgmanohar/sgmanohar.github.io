
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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import com.neurolab.common.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.math.* ;

public class StretchReflex extends NeurolabExhibit {
	public String getExhibitName(){return "Stretch Reflex";}
	JSlider load = new JSlider();
	JPanel jPanel1 = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	JRadioButton alphaOnly = new JRadioButton0();
	JRadioButton gammaOnly = new JRadioButton0();
	JRadioButton alphaGamma = new JRadioButton0();
	Border border1;
	TitledBorder titledBorder1;
	Border border2;
	JSlider command = new JSlider();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	ReturnButton returnButton1 = new ReturnButton();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	Border border3;
	Border border4;
	GraphicComponent graphicComponent1 = new GraphicComponent();
	JPanel alphamotor = new JPanel0();
	GraphicComponent graphicComponent2 = new GraphicComponent();
	GraphicComponent graphicComponent3 = new GraphicComponent();
	GraphicComponent graphicComponent4 = new GraphicComponent();
	GraphicComponent graphicComponent5 = new GraphicComponent();
	GraphicComponent graphicComponent6 = new GraphicComponent();
	GraphicComponent graphicComponent7 = new GraphicComponent();
	GraphicComponent graphicComponent8 = new GraphicComponent();
	JPanel commandalpha = new JPanel();
	JPanel commandgamma = new JPanel();
	GraphicComponent graphicComponent9 = new GraphicComponent();
	JLabel jLabel0 = new JLabel();
	JLabel jLabel3 = new JLabel();
	GraphicComponent graphicComponent11 = new GraphicComponent();
	JPanel oneAafferent = new JPanel();
	GraphicComponent graphicComponent12 = new GraphicComponent();
	GraphicComponent graphicComponent13 = new GraphicComponent();
	GraphicComponent loadalpha = new GraphicComponent();
	ButtonGroup bg1=new ButtonGroup();
	int platform=50;  // Muscle length
	int spindle=30;   // Spindle length
	int spindleWidth=30;  // Spindle width

	JPanel loadpanel = new JPanel0(){
		public void paint(Graphics g){
			super.paint(g);
			antiAlias(g);

			g.setColor(Color.gray);
			g.fillRect(30,0,20,platform);         //left pole
			g.fillRect(210,0,10,platform);        // right pole
			g.setColor(Color.blue);
			if (alphaOnly.isSelected()) {
				g.fillOval(200,platform-40,30,30); // Spindle when not in use
			}else{
				spindle = platform + 10 - (int)(10*xg);
				if (spindle<20) spindle=20;
				int spindleTop = platform-20-spindle; //was 10*xg+10
				if(spindleTop<10)spindleTop=10;
				if(spindle+spindleTop > platform-20) spindle = platform-20-spindleTop;
				if (spindle<5) spindle=5;
				spindleWidth=(int)Math.sqrt(27000/spindle);
				g.fillOval((int)(200+15-spindleWidth/2),spindleTop,spindleWidth,spindle); // Spindle when activated
			}
			g.setColor(Color.black);
			g.fillRect(0,platform,250,20);
			g.setColor(Color.magenta);
			int sload=load.getValue()/2+10;
			g.fillRect(125-sload/2,platform-sload,sload,sload);     //square side sload
			setStrokeThickness(g,5);
			if(oneAafferent.isVisible()){
						g.setColor(getComponentColor(oneAafferent));
						g.drawLine(230,platform/2,getWidth(),45);
			}
		}
	};
	Timer timer;
	double basis,xa,xe,xg,xs,X;
	public StretchReflex() {
	}
	public void init(){
		super.init();
		initComponents();
		alphaOnly.setSelected(true);
		alphaOnly.doClick();
		timer=new Timer(50,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				basis=limit(basis+(10-load.getValue()/10)-xa,170,220); // xa = alpha activity
				if(alphaOnly.isSelected()){
					xa=10-command.getValue()/10;  // Alpha only
					xe=xs=xg=0;
					X=33;
				}else{
					xg=10-command.getValue()/10;  // Gamma
					xa=xe;
					if(alphaGamma.isSelected())xa+=0.95*(10-load.getValue()/10)-5; // Alpha + gamma
					xe=xs;
					X=limit((basis+4*xg-170),33,60);
					xs=(X-33)/3;
				}
				Color tint=mixColor(xa);
				if(getComponentColor(commandalpha)!=tint){
					setComponentColor(commandalpha,tint);
					setComponentColor(loadalpha,tint);
					setComponentColor(alphamotor,tint);
				}
				tint=mixColor(xe);
				if(getComponentColor(oneAafferent)!=tint)
					setComponentColor(oneAafferent,tint);
				tint=mixColor(xg);
				if(getComponentColor(commandgamma)!=tint)
					setComponentColor(commandgamma,tint);
				if(150-(int)((basis-170)*2)!=platform){ // Basis in range 170-220
					platform=150-(int)((basis-170)*2);    // Platform is 50 - 150
					loadpanel.repaint();
				}
			}
		} );
		timer.start();
	}
	public double limit(double a,double b,double c){
		if(a<b)return b;
		if(a>c)return c;
		return a;
	}
	public Color mixColor(double c){
		double q=limit((10-c),0,10);
		return new Color(60 + (int)(18 * q), 60 - (int)(6 * q), 60 - (int)(6 * q));
	}
	public void setComponentColor(Container p,Color c){
		if(p instanceof GraphicComponent)p.setForeground(c);
		Component[] a=p.getComponents();
		for(int i=0;i<a.length;i++){
			if(a[i] instanceof GraphicComponent)
				a[i].setForeground(c);
		}
	}
	public Color getComponentColor(Container p){
		if(p instanceof GraphicComponent)return p.getForeground();
		Component[] a=p.getComponents();
		for(int i=0;i<a.length;i++)
			if(a[i] instanceof GraphicComponent)
				return a[i].getForeground();
		return null;
	}
	public void initComponents(){
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		command.setBackground(systemGray);
		load.setBackground(systemGray);
		border3 = BorderFactory.createLineBorder(Color.lightGray,1);
		border4 = BorderFactory.createLineBorder(Color.lightGray,1);
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel2.setText("Command");
		jLabel2.setBounds(new Rectangle(453, 128, 62, 17));
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel1.setText("Load");
		jLabel1.setBounds(new Rectangle(395, 294, 41, 17));
		jPanel3.setBounds(new Rectangle(462, 190, 123, 133));
		jPanel3.setLayout(borderLayout2);
		border1 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border1,"Mode");
		border2 = BorderFactory.createCompoundBorder(titledBorder1,BorderFactory.createEmptyBorder(5,5,5,5));
		load.setOrientation(JSlider.VERTICAL);
		load.setBorder(border4);
		load.setBounds(new Rectangle(400, 166, 26, 124));
		load.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				sliderchanged(e);
			}
		});
		jPanel1.setBorder(border2);
		jPanel1.setLayout(gridLayout1);
		alphaOnly.setText("Alpha only");
		alphaOnly.setActionCommand("AlphaOnly");
		alphaOnly.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modechanged(e);
			}
		});
		gammaOnly.setText("Gamma only");
		gammaOnly.setActionCommand("GammaOnly");
		gammaOnly.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modechanged(e);
			}
		});
		gridLayout1.setRows(3);
		gridLayout1.setColumns(1);
		alphaGamma.setText("Alpha/gamma");
		alphaGamma.setActionCommand("AlphaGamma");
		alphaGamma.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				modechanged(e);
			}
		});
		command.setOrientation(JSlider.VERTICAL);
		command.setBorder(border3);
		command.setBounds(new Rectangle(468, 18, 27, 110));
		jPanel2.setLayout(null);
		graphicComponent1.setType(4);
		graphicComponent1.setBounds(new Rectangle(308, 25, 39, 40));
		alphamotor.setOpaque(false);
		alphamotor.setBounds(new Rectangle(23, 9, 349, 72));
		alphamotor.setLayout(null);
		graphicComponent2.setType(2);
		graphicComponent2.setBounds(new Rectangle(25, 10, 274, 17));
		graphicComponent3.setDirection(true);
		graphicComponent3.setBounds(new Rectangle(64, 20, 19, 20));
		graphicComponent4.setDirection(true);
		graphicComponent4.setBounds(new Rectangle(297, 16, 18, 18));
		graphicComponent5.setToolTipText("");
		graphicComponent5.setType(3);
		graphicComponent5.setBounds(new Rectangle(21, 18, 13, 20));
		graphicComponent6.setType(1);
		graphicComponent6.setBounds(new Rectangle(12, 45, 274, 14));
		graphicComponent7.setType(1);
		graphicComponent7.setBounds(new Rectangle(27, 59, 47, 100));
		graphicComponent8.setType(1);
		graphicComponent8.setBounds(new Rectangle(218, 58, 14, 101));
		commandalpha.setOpaque(false);
		commandalpha.setBounds(new Rectangle(375, 30, 98, 41));
		commandalpha.setLayout(null);
		commandgamma.setOpaque(false);
		commandgamma.setBounds(new Rectangle(232, 67, 233, 79));
		commandgamma.setLayout(null);
		graphicComponent9.setType(4);
		graphicComponent9.setBounds(new Rectangle(146, 31, 39, 40));
		jLabel0.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel0.setText("Alpha");
		jLabel0.setBounds(new Rectangle(315, 2, 39, 19));
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel3.setText("Gamma");
		jLabel3.setBounds(new Rectangle(143, 15, 44, 16));
		graphicComponent11.setType(2);
		graphicComponent11.setBounds(new Rectangle(4, 44, 145, 15));
		oneAafferent.setOpaque(false);
		oneAafferent.setBounds(new Rectangle(221, 68, 144, 195));
		oneAafferent.setLayout(null);
		graphicComponent12.setType(3);
		graphicComponent12.setBounds(new Rectangle(115, 11, 26, 120));
		graphicComponent13.setType(2);
		graphicComponent13.setBounds(new Rectangle(62, 126, 69, 10));
		loadpanel.setBounds(new Rectangle(10, 154, 274, 170));
		loadalpha.setDirection(true);
		loadalpha.setBounds(new Rectangle(355, 77, 43, 146));
		jLabel4.setFont(new java.awt.Font("Dialog", 1, 12));
		jLabel4.setText("Ia, II");
		jLabel4.setBounds(new Rectangle(71, 98, 29, 15));
		cline1.setType(2);
		cline1.setBounds(new Rectangle(-1, 11, 67, 23));
		graphicComponent10.setBounds(new Rectangle(184, 2, 43, 35));
		getMainContainer().setLayout(borderLayout1);

		bg1.add(alphaOnly);bg1.add(gammaOnly);bg1.add(alphaGamma);
		this.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel3, null);
		jPanel3.add(returnButton1, BorderLayout.SOUTH);
		jPanel3.add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(alphaOnly, null);
		jPanel1.add(gammaOnly, null);
		jPanel1.add(alphaGamma, null);
		jPanel2.add(load, null);
		jPanel2.add(jLabel1, null);
		jPanel2.add(oneAafferent, null);
		oneAafferent.add(jLabel4, null);
		oneAafferent.add(graphicComponent13, null);
		oneAafferent.add(graphicComponent12, null);
		jPanel2.add(loadalpha, null);
		jPanel2.add(alphamotor, null);
		alphamotor.add(graphicComponent2, null);
		alphamotor.add(graphicComponent5, null);
		alphamotor.add(graphicComponent4, null);
		alphamotor.add(jLabel0, null);
		alphamotor.add(graphicComponent1, null);
		jPanel2.add(command, null);
		jPanel2.add(jLabel2, null);
		jPanel2.add(graphicComponent8, null);
		jPanel2.add(graphicComponent6, null);
		jPanel2.add(graphicComponent7, null);
		jPanel2.add(loadpanel, null);
		jPanel2.add(commandalpha, null);
		commandalpha.add(graphicComponent3, null);
		commandalpha.add(cline1, null);
		jPanel2.add(commandgamma, null);
		commandgamma.add(graphicComponent9, null);
		commandgamma.add(jLabel3, null);
		commandgamma.add(graphicComponent11, null);
		commandgamma.add(graphicComponent10, null);
	}

	void modechanged(ActionEvent e) {
		if(alphaOnly.isSelected()){
			commandgamma.setVisible(false);
			commandalpha.setVisible(true);
			oneAafferent.setVisible(false);
			loadalpha.setVisible(false);
		}else if(gammaOnly.isSelected()){
			commandgamma.setVisible(true);
			commandalpha.setVisible(false);
			oneAafferent.setVisible(true);
			loadalpha.setVisible(false);
		}else if(alphaGamma.isSelected()){
			commandgamma.setVisible(true);
			commandalpha.setVisible(false);
			oneAafferent.setVisible(true);
			loadalpha.setVisible(true);
		}
		loadpanel.repaint();
	}

	void sliderchanged(ChangeEvent e) {
			 loadpanel.repaint();
	}
	JLabel jLabel4 = new JLabel();
	GraphicComponent cline1 = new GraphicComponent();
	GraphicComponent graphicComponent10 = new GraphicComponent();
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}
}
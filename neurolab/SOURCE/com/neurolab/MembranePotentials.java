
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
import com.neurolab.common.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;

import java.beans.*;

public class MembranePotentials extends NeurolabExhibit

{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel();
	ReturnButton returnButton1 = new ReturnButton();
	BorderLayout borderLayout4 = new BorderLayout();
	JButton reset = new JButton();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	Border border1;
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel6 = new JPanel();
	Border border2;
	BorderLayout borderLayout6 = new BorderLayout();
	Label3D label3D1 = new Label3D();
	Border border3;
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel7 = new JPanel();
	Border border4;
	BorderLayout borderLayout8 = new BorderLayout();
	Border border5;
	JPanel jPanel9 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel10 = new JPanel();
	JPanel jPanel11 = new JPanel();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel12 = new JPanel();
	Border border6;
	TitledBorder titledBorder1;
	Border border7;
	TitledBorder titledBorder2;
	Border border8;
	TitledBorder titledBorder3;
	BorderLayout borderLayout9 = new BorderLayout();
	JPanel jPanel15 = new JPanel();
	GridLayout gridLayout3 = new GridLayout();
	JRadioButton rbc = new JRadioButton();
	JRadioButton squid = new JRadioButton();
	JCheckBox pump = new JCheckBox();
	JPanel jPanel16 = new JPanel();
	Border border9;
	TitledBorder titledBorder4;
	JTextPane potential = new JTextPane();
	GridLayout gridLayout5 = new GridLayout();
	JPanel jPanel19 = new JPanel();
	JPanel jPanel20 = new JPanel();
	JPanel jPanel21 = new JPanel();
	BorderLayout borderLayout12 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	BorderLayout borderLayout13 = new BorderLayout();
	BorderLayout borderLayout14 = new BorderLayout();
	JLabel jLabel3 = new JLabel();
	BorderLayout borderLayout15 = new BorderLayout();
	Border border10;
	JTextPane ni = new JTextPane();
	JTextPane ki = new JTextPane();
	JTextPane ai = new JTextPane();
	Border border11;
	Border border12;
	Border border13;
	Border border14;
	TitledBorder titledBorder5;
		Spinner ao = new Spinner();
		Spinner ko = new Spinner();
	Spinner no = new Spinner();
	JPanel jPanel13 = new JPanel();
	BorderLayout borderLayout17 = new BorderLayout();
	BorderLayout borderLayout18 = new BorderLayout();
	BorderLayout borderLayout19 = new BorderLayout();
	GridLayout gridLayout6 = new GridLayout();
	JPanel jPanel24 = new JPanel();
	JPanel jPanel25 = new JPanel();
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel6 = new JLabel();
	JPanel jPanel110 = new JPanel();
	 Spinner ga = new Spinner();
	 Spinner gk = new Spinner();
	 Spinner gn = new Spinner();
	JPanel jPanel14 = new JPanel();
	BorderLayout borderLayout110 = new BorderLayout();
	BorderLayout borderLayout111 = new BorderLayout();
	BorderLayout borderLayout112 = new BorderLayout();
	GridLayout gridLayout7 = new GridLayout();
	JPanel jPanel26 = new JPanel();
	JPanel jPanel27 = new JPanel();
	JLabel jLabel7 = new JLabel();
	JLabel jLabel8 = new JLabel();
	JLabel jLabel9 = new JLabel();
	JPanel jPanel111 = new JPanel();
	Border border15;
	TitledBorder titledBorder6;
	Border border16;
	TitledBorder titledBorder7;
	JPanel jPanel8 = new JPanel();
	BorderLayout borderLayout10 = new BorderLayout();
	MultiPercentageBar percentin = new MultiPercentageBar();
	Label3D label3D2 = new Label3D();
	JPanel jPanel17 = new JPanel();
	BorderLayout borderLayout20 = new BorderLayout();
	BorderLayout borderLayout16 = new BorderLayout();
	Spacer spacer1 = new Spacer(50,10);
	JPanel jPanel23 = new JPanel();
	JPanel jPanel22 = new JPanel();
	BorderLayout borderLayout11 = new BorderLayout();
	Label3D label3D3 = new Label3D();
	MultiPercentageBar percentout = new MultiPercentageBar();
	JPanel jPanel18 = new JPanel();
	JPanel jPanel28 = new JPanel();
	JLabel jLabel10 = new JLabel();
	JLabel jLabel11 = new JLabel();
	JLabel jLabel12 = new JLabel();
	JLabel jLabel13 = new JLabel();
	JPanel picture = new JPanel()

	{public void paint(Graphics g)
							 {
		int sno=lim((int)(1.5 * cell.no),0,255),sko=lim((int)(1.5 * cell.ko),0,255);
		setBackground( new Color(sno, 0, sko));
		super.paint(g);
		g.setColor( new Color(lim((int)(1.5 * cell.ni),0,255), 0, lim((int)(1.5 * cell.ki),0,255)) );
		if(!burst)
												{
			int r=(int)(cell.vol*1.3)/4;
			g.fillOval( getWidth()/2-r, getHeight()/2-r, 2*r,2*r);
						}else
												{
			g.setColor(Color.black);
			g.setFont(new Font("SansSerif",Font.BOLD,34));
			g.drawString("BURST!",0,getHeight()/2);
						}
						}
				 };


	public int lim(int a,int b,int c)
								{
		if(b>a)return b;
		if(a>c)return c;
		return a;
								}


	class Spinner extends JTextPane
							 {
		MouseInputAdapter p=new MouseInputAdapter()
											 {int start;
			public void mousePressed(MouseEvent e)
				{
				start=e.getY();
				}
			public void mouseDragged(MouseEvent e)
				{
				int v=Integer.parseInt(getText());
				v-=(e.getY()-start)/2;
				if((v>0)&&(v<300)){
					setText(String.valueOf(v));
					start=e.getY();
				}
				updateTexts();
			}
		} ;
		KeyListener k=new KeyAdapter(){
			public void keyTyped(KeyEvent e){
				if(e.getKeyChar()==10 || e.getKeyChar()==13)
				updateTexts();
			}
		};
		public Spinner()
											 {addMouseListener(p);
			addMouseMotionListener(p);
			setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			addKeyListener(k);
		}
	}

IonicCell cell= new IonicCell();
	public MembranePotentials() {
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
	public String getExhibitName(){return "Membrane Potentials";}
	ButtonGroup bg=new ButtonGroup();
	boolean burst=false;
	JTextPane[] textbox;
	public void myinit(){
		cell.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand()=="TextChange"){
					for(int i=9;i<=12;i++){
						if(textbox[i]!=null)
						textbox[i].setText(String.valueOf((int)(cell.Text1[i])) +((i==12)?"mV":"") );
					}
				}else if(e.getActionCommand()=="AllTextChange"){
					for(int i=0;i<=12;i++){
						if(textbox[i]!=null)
						textbox[i].setText(String.valueOf((int)(cell.Text1[i])) +((i==12)?"mV":"") );
					}
				}else if(e.getActionCommand()=="RedrawCell"){
					picture.repaint();
				}else if(e.getActionCommand()=="Burst"){
					if(!burst){
						burst=true;
						picture.repaint();
					}
				}else if(e.getActionCommand()=="BarChange"){
					int max=350;
					percentout.setValues(new int[]{cell.no*100/max,cell.ko*100/max,cell.ao*100/max});
					percentin.setValues(new int[]{cell.ni*100/max,cell.ki*100/max,cell.ai*100/max,cell.xi*100/max});
				}
			}
		} );
		bg.add(rbc);bg.add(squid);
		rbc.setSelected(true);
		pump.setSelected(true);
		textbox=new JTextPane[]{null,ko,gk,null,no,gn,null,ao,ga,ai,ni,ki,potential};
		Color[] cols=new Color[]{Color.red,Color.blue,new Color(0,192,0),Color.black};
		percentin.setColors(cols);
		percentout.setColors(cols);
	}
	public void updateTexts(){
		String s;
		for(int i=0;i<12;i++){
			if(textbox[i]!=null){
				s=textbox[i].getText();
				if(s.length()>0)try{
					cell.Text1[i]=Double.valueOf(s).doubleValue();
				}catch(Exception e){e.printStackTrace();}	// if we have invalid text here!
			}
		}
		cell.spinChange(1);
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(5,5,2,5));
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2));
		border3 = BorderFactory.createEmptyBorder(5,5,5,5);
		border4 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(5,5,5,5));
		border5 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
		border6 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Permeability");
		border7 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder2 = new TitledBorder(border7,"Permeability");
		border8 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder3 = new TitledBorder(border8,"External mM");
		border9 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder4 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Concentrations, mM");
		border10 = BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Concentrations, mM"),BorderFactory.createEmptyBorder(0,10,15,10));
		border11 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.red,Color.red,new Color(178, 0, 0),new Color(124, 0, 0));
		border12 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.blue,Color.blue,new Color(124, 124, 124),new Color(178, 178, 178));
		border13 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.green,Color.green,new Color(0, 134, 0),new Color(0, 93, 0));
		border14 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.blue,Color.blue,new Color(0, 0, 178),new Color(0, 0, 124));
		titledBorder5 = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.blue,Color.blue,new Color(0, 0, 124),new Color(0, 0, 178)),"");
		border15 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder6 = new TitledBorder(border15,"Internal mM");
		border16 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder7 = new TitledBorder(border16,"External mM");
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		jPanel2.setPreferredSize(new Dimension(160, 0));
		jPanel3.setLayout(borderLayout4);
		jPanel3.setBackground(systemGray);
		squid.setBackground(systemGray);
		rbc.setBackground(systemGray);
		pump.setBackground(systemGray);
		reset.setText("Reset");
		reset.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				reset_actionPerformed(e);
			}
		});
		jPanel1.setBackground(systemGray);
		jPanel2.setBackground(systemGray);
		jPanel4.setBorder(border1);
		jPanel4.setPreferredSize(new Dimension(10, 100));
		jPanel4.setLayout(borderLayout5);
		jPanel4.setBackground(systemGray);
		jPanel6.setBorder(border2);
		jPanel6.setLayout(borderLayout6);
		jPanel6.setBackground(systemGray);
		label3D1.setPreferredSize(new Dimension(1, 25));
		label3D1.setFont(new java.awt.Font("Dialog", 1, 12));
		label3D1.setText("Membrane potential");
		jPanel5.setBorder(border3);
		jPanel5.setLayout(borderLayout7);
		jPanel5.setBackground(systemGray);
		jPanel7.setBorder(border4);
		jPanel7.setLayout(borderLayout8);
		jPanel7.setBackground(systemGray);
		picture.setBackground(Color.red);
		picture.setBorder(border5);
		jPanel9.setLayout(gridLayout1);
		jPanel9.setBackground(systemGray);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(1);
		jPanel10.setLayout(gridLayout2);
		jPanel10.setBackground(systemGray);
		jPanel12.setBorder(titledBorder6);
		jPanel12.setLayout(gridLayout5);
		jPanel12.setBackground(systemGray);
		jPanel11.setLayout(borderLayout9);
		jPanel11.setBackground(systemGray);
		jPanel15.setLayout(gridLayout3);
		jPanel15.setBackground(systemGray);
		jPanel110.setBackground(systemGray);
		rbc.setText("Red blood cell");
		rbc.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				rbc_actionPerformed(e);
			}
		});
		squid.setText("Nerve axon");
		squid.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				squid_actionPerformed(e);
			}
		});
		pump.setText("Sodium pump");
		pump.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pump_actionPerformed(e);
			}
		});
		jPanel16.setBorder(border10);
		jPanel16.setLayout(borderLayout15);
		jPanel16.setBackground(systemGray);
		potential.setText("-74 mV");
		potential.setCaretColor(Color.white);
		potential.setToolTipText("");
		potential.setSelectionColor(Color.blue);
		potential.setForeground(Color.green);
		potential.setFont(new java.awt.Font("SansSerif", 1, 36));
		potential.setSelectedTextColor(Color.white);
		potential.setEditable(false);
		potential.setSelectedTextColor(Color.black);
		potential.setBackground(Color.black);
		gridLayout5.setRows(3);
		gridLayout5.setColumns(1);
		gridLayout5.setVgap(15);
		jPanel19.setLayout(borderLayout12);
		jPanel19.setBackground(systemGray);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel1.setForeground(Color.red);
		jLabel1.setText("Na");
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel2.setForeground(Color.blue);
		jLabel2.setText("K");
		jPanel20.setLayout(borderLayout13);
		jPanel20.setBackground(systemGray);
		jPanel21.setLayout(borderLayout14);
		jPanel21.setBackground(systemGray);
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel3.setForeground(new Color(0, 192, 0));
		jLabel3.setText("Cl");
		ni.setText("6");
		ni.setPreferredSize(new Dimension(60, 21));
		ni.setBackground(Color.lightGray);
		ni.setBorder(BorderFactory.createLoweredBevelBorder());
		ni.setEnabled(false);
		ni.setForeground(Color.red);
		ni.setFont(new java.awt.Font("SansSerif", 1, 18));
		ki.setText("jTextPane3");
		ki.setToolTipText("");
		ki.setPreferredSize(new Dimension(60, 21));
		ki.setBackground(Color.lightGray);
		ki.setBorder(BorderFactory.createLoweredBevelBorder());
		ki.setText("144");
		ki.setForeground(Color.blue);
		ki.setSelectedTextColor(Color.blue);
		ki.setFont(new java.awt.Font("SansSerif", 1, 18));
		ki.setBorder(BorderFactory.createLoweredBevelBorder());
		ki.setEnabled(false);
		ai.setText("jTextPane4");
		ai.setPreferredSize(new Dimension(60, 23));
		ai.setBackground(Color.lightGray);
		ai.setBorder(border13);
		ai.setText("10");
		ai.setForeground(new Color(0,192,0));
		ai.setFont(new java.awt.Font("SansSerif", 1, 18));
		ai.setBorder(BorderFactory.createLoweredBevelBorder());
		ai.setEnabled(false);
		ao.setBorder(BorderFactory.createLoweredBevelBorder());
		ao.setFont(new java.awt.Font("SansSerif", 1, 18));
		//ao.setForeground(Color.white);
		ao.setForeground(new Color(0,192,0));
		ao.setText("10");
		ao.setBorder(BorderFactory.createLoweredBevelBorder());
		//ao.setBackground(new Color(0, 192, 0));
		ao.setBackground(systemGray);
		ao.setPreferredSize(new Dimension(60, 23));
		ao.setText("jTextPane4");
		ko.setBorder(border12);
		ko.setBackground(systemGray);
		ko.setFont(new java.awt.Font("SansSerif", 1, 18));
		//ko.setForeground(Color.white);
		ko.setForeground(Color.blue);
		ko.setText("144");
		ko.setBorder(BorderFactory.createLoweredBevelBorder());
		//ko.setBackground(new Color(0, 0, 192));
		ko.setPreferredSize(new Dimension(60, 21));
		ko.setText("jTextPane3");
		no.setFont(new java.awt.Font("SansSerif", 1, 18));
		no.setBackground(systemGray);
		no.setForeground(Color.red);
		//no.setForeground(Color.white);
		no.setBorder(BorderFactory.createLoweredBevelBorder());
		//no.setBackground(new Color(192, 0, 0));
		no.setPreferredSize(new Dimension(60, 21));
		no.setText("6");
		jPanel13.setLayout(gridLayout6);
		jPanel13.setBorder(titledBorder7);
		jPanel13.setBackground(systemGray);
		gridLayout6.setRows(3);
		gridLayout6.setColumns(1);
		gridLayout6.setVgap(15);
		jPanel24.setLayout(borderLayout17);
		jPanel24.setBackground(systemGray);
		jPanel25.setLayout(borderLayout18);
		jPanel25.setBackground(systemGray);
		jLabel4.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel4.setForeground(new Color(0, 192, 0));
		jLabel4.setText("Cl");
		jLabel5.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel5.setForeground(Color.blue);
		jLabel5.setText("K");
		jLabel6.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel6.setForeground(Color.red);
		jLabel6.setText("Na");
		jPanel110.setLayout(borderLayout19);
		jPanel110.setBackground(systemGray);
		ga.setBorder(BorderFactory.createLoweredBevelBorder());
		ga.setFont(new java.awt.Font("SansSerif", 1, 18));
		//ga.setForeground(Color.white);
		ga.setForeground(new Color(0,192,0));
		ga.setText("10");
		ga.setBorder(BorderFactory.createLoweredBevelBorder());
		//ga.setBackground(new Color(0, 192, 0));
		ga.setBackground(systemGray);
		ga.setPreferredSize(new Dimension(60, 23));
		ga.setText("jTextPane4");
		gk.setBorder(border12);
		gk.setFont(new java.awt.Font("SansSerif", 1, 18));
		//gk.setForeground(Color.white);
		gk.setBackground(systemGray);
		gk.setText("144");
		gk.setBorder(BorderFactory.createLoweredBevelBorder());
		//gk.setBackground(new Color(0, 0, 192));
		gk.setForeground(Color.blue);
		gk.setPreferredSize(new Dimension(60, 21));
		gk.setText("jTextPane3");
		gn.setFont(new java.awt.Font("SansSerif", 1, 18));
		//gn.setForeground(Color.white);
		gn.setBackground(systemGray);
		gn.setBorder(BorderFactory.createLoweredBevelBorder());
		//gn.setBackground(new Color(192, 0, 0));
		gn.setForeground(Color.red);
		gn.setPreferredSize(new Dimension(60, 21));
		gn.setText("6");
		jPanel14.setLayout(gridLayout7);
		jPanel14.setBorder(titledBorder1);
		jPanel14.setBackground(systemGray);
		gridLayout7.setRows(3);
		gridLayout7.setColumns(1);
		gridLayout7.setVgap(15);
		jPanel26.setLayout(borderLayout110);
		jPanel26.setBackground(systemGray);
		jPanel27.setLayout(borderLayout111);
		jPanel27.setBackground(systemGray);
		jLabel7.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel7.setForeground(new Color(0, 192, 0));
		jLabel7.setText("Cl");
		jLabel8.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel8.setForeground(Color.blue);
		jLabel8.setText("K");
		jLabel9.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel9.setForeground(Color.red);
		jLabel9.setText("Na");
		jPanel111.setLayout(borderLayout112);
		jPanel111.setBackground(systemGray);
		label3D2.setPreferredSize(new Dimension(50, 25));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D2.setText("In");
		jPanel17.setLayout(borderLayout10);
		jPanel17.setBackground(systemGray);
		jPanel8.setLayout(borderLayout20);
		jPanel8.setBackground(systemGray);
		spacer1.setMinimumSize(new Dimension(500, 10));
		jPanel23.setBackground(systemGray);
		jPanel22.setLayout(borderLayout16);
		jPanel22.setBackground(systemGray);
		label3D3.setPreferredSize(new Dimension(50, 25));
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D3.setText("Out");
		jPanel18.setLayout(borderLayout11);
		jPanel18.setBackground(systemGray);
		jPanel28.setPreferredSize(new Dimension(10, 25));
		jPanel28.setBackground(systemGray);
		jLabel10.setFont(new java.awt.Font("SansSerif", 1, 16));
		jLabel10.setForeground(Color.red);
		jLabel10.setText("Na");
		jLabel11.setFont(new java.awt.Font("SansSerif", 1, 16));
		jLabel11.setForeground(Color.blue);
		jLabel11.setText("K");
		jLabel12.setFont(new java.awt.Font("SansSerif", 1, 16));
		jLabel12.setForeground(new Color(0, 192, 0));
		jLabel12.setText("Cl");
		jLabel13.setFont(new java.awt.Font("SansSerif", 1, 16));
		jLabel13.setText("Other -");
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(returnButton1, BorderLayout.EAST);
		jPanel3.add(reset, BorderLayout.CENTER);
		jPanel2.add(jPanel4, BorderLayout.NORTH);
		jPanel4.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(potential, BorderLayout.CENTER);
		jPanel4.add(label3D1, BorderLayout.SOUTH);
		jPanel2.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(picture, BorderLayout.CENTER);
		jPanel1.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(jPanel10, null);
		jPanel10.add(jPanel12, null);
		jPanel12.add(jPanel19, null);
		jPanel19.add(jLabel1, BorderLayout.WEST);
		jPanel19.add(ni, BorderLayout.EAST);
		jPanel12.add(jPanel20, null);
		jPanel20.add(jLabel2, BorderLayout.WEST);
		jPanel20.add(ki, BorderLayout.EAST);
		jPanel12.add(jPanel21, null);
		jPanel21.add(jLabel3, BorderLayout.WEST);
		jPanel21.add(ai, BorderLayout.EAST);
		jPanel10.add(jPanel14, null);
		jPanel14.add(jPanel111, null);
		jPanel111.add(jLabel9, BorderLayout.WEST);
		jPanel14.add(jPanel27, null);
		jPanel27.add(jLabel8, BorderLayout.WEST);
		jPanel14.add(jPanel26, null);
		jPanel26.add(jLabel7, BorderLayout.WEST);
		jPanel111.add(gn, BorderLayout.EAST);
		jPanel27.add(gk, BorderLayout.EAST);
		jPanel26.add(ga, BorderLayout.EAST);
		jPanel10.add(jPanel13, null);
		jPanel13.add(jPanel110, null);
		jPanel110.add(jLabel6, BorderLayout.WEST);
		jPanel110.add(no, BorderLayout.EAST);
		jPanel13.add(jPanel25, null);
		jPanel25.add(jLabel5, BorderLayout.WEST);
		jPanel13.add(jPanel24, null);
		jPanel24.add(jLabel4, BorderLayout.WEST);
		jPanel25.add(ko, BorderLayout.EAST);
		jPanel24.add(ao, BorderLayout.EAST);
		jPanel9.add(jPanel11, null);
		jPanel11.add(jPanel15, BorderLayout.NORTH);
		jPanel15.add(rbc, null);
		jPanel15.add(squid, null);
		jPanel15.add(pump, null);
		jPanel11.add(jPanel16, BorderLayout.CENTER);
		jPanel16.add(jPanel8, BorderLayout.CENTER);
		jPanel8.add(jPanel17, BorderLayout.SOUTH);
		jPanel17.add(label3D2, BorderLayout.WEST);
		jPanel17.add(percentin, BorderLayout.CENTER);
		jPanel8.add(jPanel22, BorderLayout.CENTER);
		jPanel22.add(spacer1, BorderLayout.WEST);
		jPanel22.add(jPanel23, BorderLayout.CENTER);
		jPanel8.add(jPanel18, BorderLayout.NORTH);
		jPanel18.add(label3D3, BorderLayout.WEST);
		jPanel18.add(percentout, BorderLayout.CENTER);
		jPanel16.add(jPanel28, BorderLayout.NORTH);
		jPanel28.add(jLabel10, null);
		jPanel28.add(jLabel11, null);
		jPanel28.add(jLabel12, null);
		jPanel28.add(jLabel13, null);
		reset.setBackground(systemGray);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	void reset_actionPerformed(ActionEvent e) {
	cell.Restart();
	burst=false;
	}

	void pump_actionPerformed(ActionEvent e) {
	cell.PumpOn(pump.isSelected());
	}

	void rbc_actionPerformed(ActionEvent e) {
	cell.RBC_Click();
	}

	void squid_actionPerformed(ActionEvent e) {
	cell.Squid_Click();
	}
}
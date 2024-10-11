
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
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.PercentageBar;
import com.neurolab.common.ReturnButton;


public class RetinalReceptors extends NeurolabExhibit implements ActionListener{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel0();
  JPanel jPanel2 = new JPanel0();
  JPanel jPanel3 = new JPanel0();
  ReturnButton returnButton1 = new ReturnButton();
  PercentageBar pdebar = new PercentageBar();
  PercentageBar cgmpbar = new PercentageBar();
  PercentageBar pnabar = new PercentageBar();
  PercentageBar cabar = new PercentageBar();
  JPanel jPanel4 = new JPanel0();
  JPanel jPanel5 = new JPanel0();
  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel2 = new JLabel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  GridLayout gridLayout2 = new GridLayout();
  JRadioButton jRadioButton1 = new JRadioButton0();
  JRadioButton jRadioButton2 = new JRadioButton0();
  JRadioButton jRadioButton3 = new JRadioButton0();
  JRadioButton jRadioButton4 = new JRadioButton0();
  JRadioButton jRadioButton5 = new JRadioButton0();
  JRadioButton jRadioButton6 = new JRadioButton0();
  JRadioButton jRadioButton7 = new JRadioButton0();
  JRadioButton jRadioButton8 = new JRadioButton0();
  JRadioButton jRadioButton9 = new JRadioButton0();
  JRadioButton jRadioButton10 = new JRadioButton0();
  JRadioButton jRadioButton11 = new JRadioButton0();
  JRadioButton jRadioButton12 = new JRadioButton0();
  JRadioButton jRadioButton13 = new JRadioButton0();
  JRadioButton jRadioButton14 = new JRadioButton0();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  Oscilloscope osc = new Oscilloscope(2,this);
  //Oscilloscope osc = new Oscilloscope();
  BorderLayout borderLayout4 = new BorderLayout();
  Label3D label3D1 = new Label3D();
  JCheckBox autozero = new JCheckBox();
  Label3D label3D2 = new Label3D();

	JRadioButton[] back=new JRadioButton[]{jRadioButton7,jRadioButton6,jRadioButton5,jRadioButton4,jRadioButton3,jRadioButton2,jRadioButton1};
	JRadioButton[] incr=new JRadioButton[]{jRadioButton14,jRadioButton13,jRadioButton12,jRadioButton11,jRadioButton10,jRadioButton9,jRadioButton8};

  public String getExhibitName(){return "Photoreceptors";}

  public RetinalReceptors() {
  }
  public void init(){
    super.init();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
		jPanel6.setBackground(systemGray);
		jPanel7.setBackground(systemGray);
    myinit();
  }
	int t;
	Timer timer=new Timer(50,new ActionListener(){public void actionPerformed(ActionEvent e){
		if(++t==8){
			light = backv() + incrv();
			n = 800;
		}else{
			light = backv();
			n = 880;
		}
		gprot = limit((0.8 * gprot + 200 * light / (light + 1000)), 0, 100);
		pde = limit((0.8 * pde + 0.2 * gprot), 0, 100);
		calin = Math.pow(0.1 , (ca / 50));
		cgmp = limit((cgmp * (1 - 0.03 * pde) + calin * (100 - cgmp)), 0, 100);
		pna = 100 * Math.pow(cgmp / 100,3) ;
		ca = limit((0.995 * ca + 0.005 * pna), 0, 100);
		pdebar.setValue((int)pde);
		cgmpbar.setValue((int)cgmp);
		cabar.setValue((int)ca);
		pnabar.setValue((int)pna);

		X = 58 * Math.log((10 + 0.005 * pna) / (1 + 0.05 * pna));
		if( t== 1) oldx = X;
		if( autozero.isSelected() ) X = X - oldx + 30;
		if(t>1) osc.setPosY(new int[]{(int)(n),(int)(12*X/2)});
	}} );
	public double limit(double a,double b,double c){
		if(b>a)return b;
		if(a>c)return c;
		return a;
	}
	public double backv(){
		int j=0;
		for(int i=0;i<back.length;i++)
			if(back[i].isSelected())j=i;
		return Math.pow(10,j/2.-2);
	}
	public double incrv(){
		int j=0;
		for(int i=0;i<incr.length;i++)
			if(incr[i].isSelected())j=i;
		return Math.pow(10,j/2.);
	}
	double pde, cgmp , ca , pna , gprot , light , calin ;
  private LineOrArrowPanel lineOrArrowPanel6;
  private LineOrArrowPanel lineOrArrowPanel5;
  private LineOrArrowPanel lineOrArrowPanel4;
  private LineOrArrowPanel lineOrArrowPanel3;
  private LineOrArrowPanel lineOrArrowPanel1;
  private LineOrArrowPanel arrow1;
	double X,n,oldx;
	ButtonGroup bg1=new ButtonGroup(),bg2=new ButtonGroup();
	public void myinit(){
		for(int i=0;i<back.length;i++){
			bg1.add(back[i]);
			bg2.add(incr[i]);
		}
		back[1].setSelected(true);
		incr[2].setSelected(true);
		autozero.setSelected(true);
		light = backv();
		ca = 100;
		pna = 100;
		gprot = 0;
		cgmp = 100;
		pde = 0;
		X = 30;
		n = 75;
		osc.xSpeed=2;
		osc.timer.setDelay(50);
    osc.setPosY(new int[]{(int)(n),(int)(12*X/2)});
		timer.start();
	}

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			t=0;
			if(osc!=null){
				oldx = X;
				if( autozero.isSelected() ) X = X - oldx + 30;
				osc.setPosY(new int[]{(int)(n),(int)(12*X/2)}); // use 830-40*light if you want true light
			}
		}

	}

  private void jbInit() throws Exception {

    osc.resweepOnClear = false; // pressing clear does not reset the oscilloscope x
    osc.stopOnClear = true;
    autozero.setBackground(systemGray);
    jPanel1.setLayout(null);
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(5,5,5,5)));
    jPanel2.setBounds(274, 1, 301, 170);
    jPanel2.setLayout(borderLayout4);
    jPanel3.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Light (relative log units)"));
    jPanel3.setBounds(3, 9, 252, 104);
    jPanel3.setLayout(null);
    returnButton1.setBounds(464, 258, 96, 38);
    pdebar.setBounds(75, 145, 127, 16);
    cgmpbar.setBounds(new Rectangle(76, 208, 127, 17));
    pnabar.setBounds(274, 208, 127, 17);
    cabar.setBounds(187, 272, 126, 18);
    jPanel4.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)));
    jPanel4.setBounds(11, 67, 231, 26);
    jPanel4.setLayout(borderLayout3);
    jPanel5.setBounds(11, 20, 235, 27);
    jPanel5.setLayout(borderLayout2);
    jPanel5.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setText("Background");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel2.setText("Increment");
    jPanel6.setPreferredSize(new Dimension(150, 10));
    jPanel6.setLayout(gridLayout1);
    jPanel7.setPreferredSize(new Dimension(150, 10));
    jPanel7.setLayout(gridLayout2);
    gridLayout1.setColumns(7);
    gridLayout2.setColumns(7);
    jLabel4.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel4.setText(" 3");
    jLabel5.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel5.setText("2.5");
    jLabel6.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel6.setText(" 2");
    jLabel7.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel7.setText("1.5");
    label3D1.setPreferredSize(new Dimension(1, 25));
    label3D1.setFont(new java.awt.Font("Dialog", 1, 14));
    label3D1.setText("Photocurrent");
    autozero.setText("Auto-zero");
    autozero.setFont(new java.awt.Font("Dialog", 1, 12));
    autozero.setBounds(459, 178, 91, 19);
    label3D2.setFont(new java.awt.Font("SansSerif", 1, 20));
    label3D2.setText("Toad rod");
    label3D2.setBounds(340, 266, 105, 30);
    jLabel10.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel10.setText("PDE");
    jLabel10.setBounds(35, 143, 35, 23);
    jLabel11.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel11.setText("cGMP");
    jLabel11.setBounds(new Rectangle(26, 206, 46, 23));
    jLabel12.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel12.setText("[Ca]");
    jLabel12.setBounds(150, 270, 32, 25);
    jLabel13.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel13.setText("<HTML>P<sub>Na</sub> / P<sub>Ca</sub>");
    jLabel13.setBounds(343, 225, 74, 25);
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel3.setText(" 0");
    jLabel8.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel8.setText(" 1");
    jLabel9.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel9.setText("0.5");
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.setPreferredSize(new java.awt.Dimension(601, 333));
    jPanel1.add(jPanel2, null);
    jPanel2.add(osc, BorderLayout.CENTER);
    jPanel2.add(label3D1, BorderLayout.SOUTH);
    jPanel1.add(returnButton1, null);
    jPanel1.add(jPanel3, null);
    jPanel3.add(jPanel5, null);
    jPanel5.add(jPanel6, BorderLayout.EAST);
    jPanel6.add(jRadioButton7, null);
    jPanel6.add(jRadioButton6, null);
    jPanel6.add(jRadioButton5, null);
    jPanel6.add(jRadioButton4, null);
    jPanel6.add(jRadioButton3, null);
    jPanel6.add(jRadioButton2, null);
    jPanel6.add(jRadioButton1, null);
    jPanel5.add(jLabel1, BorderLayout.WEST);
    jPanel3.add(jPanel4, null);
    jPanel3.add(jLabel4);
    jLabel4.setBounds(225, 47, 22, 19);
    jPanel3.add(jLabel5);
    jLabel5.setBounds(204, 47, 22, 19);
    jPanel3.add(jLabel6);
    jLabel6.setBounds(182, 47, 22, 19);
    jPanel3.add(jLabel7);
    jLabel7.setBounds(160, 47, 22, 19);
    jPanel3.add(jLabel8);
    jLabel8.setBounds(142, 47, 22, 19);
    jPanel3.add(jLabel9);
    jLabel9.setBounds(120, 47, 22, 19);
    jPanel3.add(jLabel3);
    jLabel3.setBounds(98, 47, 22, 19);
    jPanel4.add(jPanel7, BorderLayout.EAST);
    jPanel4.add(jLabel2, BorderLayout.WEST);
    jPanel7.add(jRadioButton14, null);
    jPanel7.add(jRadioButton13, null);
    jPanel7.add(jRadioButton12, null);
    jPanel7.add(jRadioButton11, null);
    jPanel7.add(jRadioButton10, null);
    jPanel7.add(jRadioButton9, null);
    jPanel7.add(jRadioButton8, null);
    jPanel1.add(pdebar, null);
    jPanel1.add(cgmpbar, null);
    jPanel1.add(jLabel11, null);
    jPanel1.add(jLabel10, null);
    jPanel1.add(pnabar, null);
    jPanel1.add(jLabel13, null);
    jPanel1.add(autozero, null);
    jPanel1.add(cabar, null);
    jPanel1.add(jLabel12, null);
    {
      arrow1 = new LineOrArrowPanel();
      jPanel1.add(arrow1);
      arrow1.setBounds(132, 113, 18, 26);
      arrow1.setLocations(LineOrArrowPanel.LOC_V);
      arrow1.setArrowEnd(true);
    }
    {
      lineOrArrowPanel1 = new LineOrArrowPanel();
      jPanel1.add(lineOrArrowPanel1);
      lineOrArrowPanel1.setLocations(LineOrArrowPanel.LOC_V);
      lineOrArrowPanel1.setBounds(132, 172, 18, 30);
      lineOrArrowPanel1.setArrowEnd(true);
    }
    {
      lineOrArrowPanel3 = new LineOrArrowPanel();
      jPanel1.add(lineOrArrowPanel3);
      lineOrArrowPanel3.setArrowEnd(true);
      lineOrArrowPanel3.setLocations(LineOrArrowPanel.LOC_V);
      lineOrArrowPanel3.setBounds(302, 234, 16, 32);
    }
    {
      lineOrArrowPanel4 = new LineOrArrowPanel();
      jPanel1.add(lineOrArrowPanel4);
      lineOrArrowPanel4.setBounds(222, 208, 33, 17);
      lineOrArrowPanel4.setArrowEnd(true);
    }
    {
      lineOrArrowPanel5 = new LineOrArrowPanel();
      jPanel1.add(lineOrArrowPanel5);
      lineOrArrowPanel5.setBounds(187, 231, 16, 35);
      lineOrArrowPanel5.setArrowStart(true);
      lineOrArrowPanel5.setLocations(LineOrArrowPanel.LOC_V);
    }
    {
      lineOrArrowPanel6 = new LineOrArrowPanel();
      jPanel1.add(lineOrArrowPanel6);
      lineOrArrowPanel6.setBounds(333, 171, 17, 32);
      lineOrArrowPanel6.setArrowStart(true);
      jPanel1.add(label3D2);
      lineOrArrowPanel6.setLocations(LineOrArrowPanel.LOC_V);
    }
    getMainContainer().setLayout(borderLayout1);
    }
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
		osc.timer.stop();
	}

}
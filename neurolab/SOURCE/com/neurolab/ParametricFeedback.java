
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

import java.awt.*;
import javax.swing.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import java.beans.*;
import java.awt.event.*;

public class ParametricFeedback extends NeurolabExhibit implements ActionListener{
 public String getExhibitName() {
    return "Parametric Feedback";
  }
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel0();
  ReturnButton returnButton1 = new ReturnButton();
  JPanel jPanel2 = new JPanel0();
  Border border1;
  GridLayout gridLayout1 = new GridLayout();
  JRadioButton jRadioButton1 = new JRadioButton0();
  JRadioButton jRadioButton2 = new JRadioButton0();
  JRadioButton jRadioButton3 = new JRadioButton0();
  JRadioButton jRadioButton4 = new JRadioButton0();
  JRadioButton jRadioButton5 = new JRadioButton0();
  JRadioButton jRadioButton6 = new JRadioButton0();
  JPanel jPanel3 = new JPanel0();
  Border border2;
  JScrollBar gain = new JScrollBar();
  Label3D label3D1 = new Label3D();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel4 = new JPanel0();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel gaintext = new JLabel();
  Oscilloscope ohead = new Oscilloscope(1,this);
  Oscilloscope obackground = new Oscilloscope(1,this);
  Oscilloscope oslip = new Oscilloscope(1,this);
  Oscilloscope oeye = new Oscilloscope(1,this);
  Border border3;
  Label3D label3D2 = new Label3D();
  Label3D label3D3 = new Label3D();
  BorderLayout borderLayout3 = new BorderLayout();
  Label3D lbackground = new Label3D();
  Label3D label3D5 = new Label3D();
  Label3D lslip = new Label3D();
  Label3D label3D7 = new Label3D();
  JCheckBox learn = new JCheckBox();
  JPanel gazepos = new JPanel(){public void paint(Graphics g){super.paint(g);
	g.setColor(Color.blue);
	g.fillRect((int)(oX+Y)+getWidth()/2,2,5,getHeight()-4);
  }};
  JPanel headpos = new JPanel(){public void paint(Graphics g){super.paint(g);
	g.setColor(Color.blue);
	g.fillRect((int)(oX)+getWidth()/2,2,5,getHeight()-4);
  }};
  JPanel backpos = new JPanel(){public void paint(Graphics g){super.paint(g);
	g.setColor(Color.blue);
	g.fillRect((int)(background)+getWidth()/2,2,5,getHeight()-4);
  }};
  public ParametricFeedback() {
  }

	JRadioButton[] mode=new JRadioButton[]{jRadioButton1,jRadioButton2,jRadioButton3,jRadioButton4,jRadioButton5,jRadioButton6};
	ButtonGroup bg=new ButtonGroup();
  public void init(){
    super.init();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	ohead.remove(ohead.buttons);	//remove all buttons.
	oeye.remove(oeye.buttons);
	obackground.remove(obackground.buttons);
	oslip.remove(oslip.buttons);
	for(int i=0;i<mode.length;i++){
		bg.add(mode[i]);
	}
	myinit();
  }

	ActionListener buttonlistener=new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if(getModetype()==0){
				obackground.setVisible(false);
				oslip.setVisible(false);
				lbackground.setVisible(false);
				lslip.setVisible(false);
			}else{
				obackground.setVisible(true);
				oslip.setVisible(true);
				lbackground.setVisible(true);
				lslip.setVisible(true);
			}
		}
	};


	SignalGenerator sig=new SignalGenerator();
	double gainval;
	double vgain,Y,rsv,background,oX;
	public int min(int a,int b){return (a>b)?b:a;}
	public void myinit(){
		sig.setSignalListener(new SignalListener(){
			public void signalEvent(double X){oX=X;
				Y = X * vgain;
				String gt = "0.00";
                                // Problem here as gain gets v. small
                                if (Math.abs(gainval)>0.2) {
                                gt=String.valueOf(gainval / 50.);
                                };
				gaintext.setText(gt.substring(0,min(gt.length(),5)));
				switch(getModetype()){
					case 0:             //' dark
						rsv = 0;
						break;
					case 1:             //' assisted
						background = 0;
						rsv = X + Y;
						vgain = 0.99 * vgain - 0.01;
						break;
					case 2:             //' suppressed
						background = X;
						rsv = X + Y - background;
                                                Y = Y + 0.9 * rsv;
						if (Math.abs(vgain)>0.00001) {
                                                vgain = 0.99 * vgain;
						}
                                                break;
					case 3:             //' reversed
						background = 0;
						rsv = X - Y;
						vgain = 0.99 * vgain + 0.01;
						break;
					case 4:             //' magnifying
						background = 0;
						rsv = X + 0.75 * Y;
						vgain = 0.99 * vgain - 0.0133;
						break;
					case 5:             //' minifying
						background = 0;
						rsv = X + 1.33 * Y;
						vgain = 0.99 * vgain - 0.0075;
						break;
				}
				Y = Y - 0.9 * rsv;
				if(learn.isSelected()){
					gainval =50 * vgain;
					gain.setValue((int)(45+gainval/2));
				}
				ohead.setPosY(new int[]{(int)(X)*10});
				obackground.setPosY(new int[]{(int)(background*10)});
				oslip.setPosY(new int[]{(int)(rsv*20)});
				oeye.setPosY(new int[]{(int)(Y*10)});
				headpos.repaint();gazepos.repaint();backpos.repaint();

				if(ohead.getPosX()>=ohead.graph.getDataWidth())sweepall();
			}
		});
		for(int i=0;i<mode.length;i++){
			mode[i].addActionListener(buttonlistener);
		}
		sig.frequency.setValue(15);
		sig.amplitude.setValue(20);
		sig.sinusoidal.setSelected(true);

		gain.setValue(30);
		learn.setSelected(true);
		mode[0].doClick();
		sig.timer.start();

		int[] base=new int[]{30};
		ohead.setBaseY(base);
		obackground.setBaseY(base);
		oslip.setBaseY(base);
		oeye.setBaseY(base);
		sweepall();
	}
	public void sweepall(){	// gone past screen edge?
		ohead.clear.doClick();
		obackground.clear.doClick();
		oslip.clear.doClick();
		oeye.clear.doClick();
		ohead.sweep.doClick();
		obackground.sweep.doClick();
		oslip.sweep.doClick();
		oeye.sweep.doClick();
	}
	public int getModetype(){
		int j=0;
		for(int i=0;i<mode.length;i++){
			if(mode[i].isSelected())j=i;
		}
		return j;
	}
	public void actionPerformed(ActionEvent e){
	}
  private void jbInit() throws Exception {
    learn.setBackground(systemGray);
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,5,2,5));
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
    border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
    getMainContainer().setLayout(borderLayout1);
    jPanel1.setLayout(null);
    returnButton1.setBounds(new Rectangle(491, 282, 81, 30));
    jPanel2.setBorder(border1);
    jPanel2.setBounds(new Rectangle(248, 143, 320, 115));
    jPanel2.setLayout(gridLayout1);
    gridLayout1.setRows(3);
    gridLayout1.setColumns(2);
    jRadioButton1.setText("Dark");
    jRadioButton1.setFont(new java.awt.Font("SansSerif", 1, 12));
    jRadioButton2.setText("Visual assistance");
    jRadioButton2.setFont(new java.awt.Font("SansSerif", 1, 12));
    jRadioButton3.setText("Visual suppression");
    jRadioButton3.setFont(new java.awt.Font("SansSerif", 1, 12));
    jRadioButton4.setText("Reversing prisms");
    jRadioButton4.setFont(new java.awt.Font("SansSerif", 1, 12));
    jRadioButton5.setText("Magnifying lenses");
    jRadioButton5.setFont(new java.awt.Font("SansSerif", 1, 12));
    jRadioButton6.setText("Minifying lenses");
    jRadioButton6.setFont(new java.awt.Font("SansSerif", 1, 12));
    jPanel3.setBorder(border2);
    jPanel3.setBounds(new Rectangle(396, 15, 174, 97));
    jPanel3.setLayout(borderLayout2);
    gain.setValue(45);
    gain.setOrientation(JScrollBar.HORIZONTAL);
    gain.setBounds(new Rectangle(21, 25, 123, 16));
    gain.addAdjustmentListener(new java.awt.event.AdjustmentListener() {

      public void adjustmentValueChanged(AdjustmentEvent e) {
        gain_adjustmentValueChanged(e);
      }
    });
    label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
    label3D1.setText("VOR");
    label3D1.setBounds(new Rectangle(62, 7, 50, 18));
    jPanel4.setLayout(null);
    jPanel4.setBackground(Color.darkGray);
    jLabel1.setForeground(Color.green);
    jLabel1.setText("Gain");
    jLabel1.setBounds(new Rectangle(72, 47, 30, 11));
    jLabel2.setForeground(Color.lightGray);
    jLabel2.setText("+");
    jLabel2.setBounds(new Rectangle(120, 44, 11, 11));
    jLabel3.setForeground(Color.lightGray);
    jLabel3.setText("-");
    jLabel3.setBounds(new Rectangle(48, 42, 10, 11));
    gaintext.setFont(new java.awt.Font("SansSerif", 1, 20));
    gaintext.setForeground(Color.green);
    gaintext.setHorizontalAlignment(SwingConstants.CENTER);
    gaintext.setText("0");
    gaintext.setBounds(new Rectangle(58, 63, 56, 19));
    ohead.setBounds(new Rectangle(109, 89, 120, 67));
    ohead.setGutter(2);
    obackground.setBounds(new Rectangle(110, 13, 120, 67));
    obackground.setGutter(2);
    oslip.setBounds(new Rectangle(109, 164, 120, 67));
    oslip.setGutter(2);
    oeye.setBounds(new Rectangle(109, 238, 120, 67));
    oeye.setGutter(2);
    gazepos.setBorder(border3);
    gazepos.setBounds(new Rectangle(248, 53, 140, 21));
    gazepos.setLayout(null);
    headpos.setBorder(border3);
    headpos.setBounds(new Rectangle(248, 91, 140, 21));
    headpos.setLayout(null);
    label3D2.setFont(new java.awt.Font("Dialog", 1, 14));
    label3D2.setText("Head");
    label3D2.setBounds(new Rectangle(2, 4, 51, 16));
    label3D3.setFont(new java.awt.Font("SansSerif", 1, 14));
    label3D3.setText("Gaze");
    label3D3.setBounds(new Rectangle(2, 5, 40, 16));
    lbackground.setFont(new java.awt.Font("SansSerif", 1, 13));
    lbackground.setText("Background");
    lbackground.setBounds(new Rectangle(4, 16, 96, 18));
    label3D5.setBounds(new Rectangle(60, 92, 53, 16));
    label3D5.setText("Head");
    label3D5.setFont(new java.awt.Font("SansSerif", 1, 13));
    lslip.setBounds(new Rectangle(10, 166, 93, 20));
    lslip.setText("Retinal slip");
    lslip.setFont(new java.awt.Font("SansSerif", 1, 13));
    label3D7.setFont(new java.awt.Font("SansSerif", 1, 13));
    label3D7.setText("Eye-in-head");
    label3D7.setBounds(new Rectangle(5, 238, 99, 19));
    learn.setText("Learn");
    learn.setBounds(new Rectangle(293, 282, 64, 27));
    label3D4.setBounds(new Rectangle(3, 5, 106, 16));
    label3D4.setText("Background");
    label3D4.setToolTipText("");
    label3D4.setFont(new java.awt.Font("Dialog", 1, 14));
    backpos.setLayout(null);
    backpos.setBorder(border3);
    backpos.setBounds(new Rectangle(248, 15, 140, 21));
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(returnButton1, null);
    jPanel1.add(lbackground, null);
    jPanel1.add(oslip, null);
    jPanel1.add(oeye, null);
    jPanel1.add(learn, null);
    jPanel1.add(ohead, null);
    jPanel1.add(label3D5, null);
    jPanel1.add(lslip, null);
    jPanel1.add(label3D7, null);
    jPanel1.add(obackground, null);
    jPanel1.add(headpos, null);
    headpos.add(label3D2, null);
    jPanel1.add(jPanel2, null);
    jPanel2.add(jRadioButton1, null);
    jPanel2.add(jRadioButton4, null);
    jPanel2.add(jRadioButton2, null);
    jPanel2.add(jRadioButton5, null);
    jPanel2.add(jRadioButton3, null);
    jPanel2.add(jRadioButton6, null);
    jPanel1.add(backpos, null);
    backpos.add(label3D4, null);
    jPanel1.add(gazepos, null);
    gazepos.add(label3D3, null);
    jPanel1.add(jPanel3, null);
    jPanel3.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jLabel3, null);
    jPanel4.add(jLabel2, null);
    jPanel4.add(gain, null);
    jPanel4.add(label3D1, null);
    jPanel4.add(gaintext, null);
    jPanel4.add(jLabel1, null);
  }


  void gain_adjustmentValueChanged(AdjustmentEvent e) {
	if(gain.getValue()!=(int)(45+gainval/2)){
		gainval=gain.getValue()*2.-100;
		vgain=gainval/50.;
	}
  }
  Label3D label3D4 = new Label3D();
 }
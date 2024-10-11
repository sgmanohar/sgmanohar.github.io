
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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.SignalGenerator;
import com.neurolab.common.SignalListener;


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
  Oscilloscope ohead = new Oscilloscope();
  Oscilloscope obackground = new Oscilloscope();
  Oscilloscope oslip = new Oscilloscope();
  Oscilloscope oeye = new Oscilloscope();
  Border border3;
  Label3D label3D2 = new Label3D();
  Label3D label3D3 = new Label3D();
  BorderLayout borderLayout3 = new BorderLayout();
  Label3D lbackground = new Label3D();
  private LineOrArrowPanel inhib1;
  private LineOrArrowPanel arrow5;
  private LineOrArrowPanel line2;
  private LineOrArrowPanel arrow4;
  private LineOrArrowPanel arrow3;
  private LineOrArrowPanel line1;
  private LineOrArrowPanel arrow2;
  private LineOrArrowPanel arrow1;
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
  JComponent[] hideInTheDark;
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
				for(int i=0;i<hideInTheDark.length;i++) hideInTheDark[i].setVisible(false);
			}else{
        for(int i=0;i<hideInTheDark.length;i++) hideInTheDark[i].setVisible(true);
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
					gainval =50 * vgain; // to [-100 +100]
					gain.setValue((int)(50+gainval/2));
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
    returnButton1.setBounds(488, 274, 81, 30);
    jPanel2.setBorder(border1);
    jPanel2.setBounds(48, 243, 314, 70);
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
    jPanel3.setBounds(230, 134, 173, 98);
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
    ohead.setBounds(38, 160, 123, 68);
    ohead.setGutter(2);
    obackground.setBounds(35, 38, 121, 68);
    obackground.setGutter(2);
    oslip.setBounds(252, 39, 120, 67);
    oslip.setGutter(2);
    oeye.setBounds(449, 164, 120, 67);
    oeye.setGutter(2);
    gazepos.setBorder(border3);
    gazepos.setBounds(439, 38, 140, 21);
    gazepos.setLayout(null);
    headpos.setBorder(border3);
    headpos.setBounds(439, 65, 140, 21);
    headpos.setLayout(null);
    label3D2.setFont(new java.awt.Font("Dialog", 1, 14));
    label3D2.setText("Head");
    label3D2.setBounds(new Rectangle(2, 4, 51, 16));
    label3D3.setFont(new java.awt.Font("SansSerif", 1, 14));
    label3D3.setText("Gaze");
    label3D3.setBounds(new Rectangle(2, 5, 40, 16));
    lbackground.setFont(new java.awt.Font("SansSerif", 1, 13));
    lbackground.setText("Target");
    lbackground.setBounds(35, 20, 96, 18);
    label3D5.setBounds(38, 144, 53, 16);
    label3D5.setText("Head");
    label3D5.setFont(new java.awt.Font("SansSerif", 1, 13));
    lslip.setBounds(252, 19, 120, 20);
    lslip.setText("Retinal slip");
    lslip.setFont(new java.awt.Font("SansSerif", 1, 13));
    label3D7.setFont(new java.awt.Font("SansSerif", 1, 13));
    label3D7.setText("Eye-in-head");
    label3D7.setBounds(450, 143, 99, 19);
    learn.setText("Learn");
    learn.setBounds(418, 276, 64, 27);
    label3D4.setBounds(new Rectangle(3, 5, 106, 16));
    label3D4.setText("Background");
    label3D4.setToolTipText("");
    label3D4.setFont(new java.awt.Font("Dialog", 1, 14));
    backpos.setLayout(null);
    backpos.setBorder(border3);
    backpos.setBounds(439, 11, 140, 21);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.setPreferredSize(new java.awt.Dimension(374, 323));
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
    {
      arrow1 = new LineOrArrowPanel();
      jPanel1.add(arrow1);
      arrow1.setBounds(157, 59, 95, 16);
      arrow1.setArrowEnd(true);
    }
    {
      arrow2 = new LineOrArrowPanel();
      jPanel1.add(arrow2);
      arrow2.setBounds(161, 189, 69, 14);
      arrow2.setArrowEnd(true);
    }
    {
      line1 = new LineOrArrowPanel();
      jPanel1.add(line1);
      line1.setBounds(199, 87, 10, 109);
      line1.setLocations(LineOrArrowPanel.LOC_V);
    }
    {
      arrow3 = new LineOrArrowPanel();
      jPanel1.add(arrow3);
      arrow3.setBounds(203, 81, 49, 16);
      arrow3.setArrowEnd(true);
    }
    {
      arrow4 = new LineOrArrowPanel();
      jPanel1.add(arrow4);
      arrow4.setBounds(403, 182, 46, 14);
      arrow4.setArrowEnd(true);
    }
    {
      line2 = new LineOrArrowPanel();
      jPanel1.add(line2);
      line2.setBounds(451, 97, 10, 40);
      line2.setLocations(LineOrArrowPanel.LOC_V);
    }
    {
      arrow5 = new LineOrArrowPanel();
      jPanel1.add(arrow5);
      arrow5.setBounds(374, 92, 84, 15);
      arrow5.setArrowStart(true);
    }
    {
      inhib1 = new LineOrArrowPanel();
      jPanel1.add(inhib1); 
      inhib1.setBounds(304, 106, 10, 22);
      inhib1.setInhibEnd(true);
      inhib1.setLocations(LineOrArrowPanel.LOC_V);
    }
    jPanel3.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jLabel3, null);
    jPanel4.add(jLabel2, null);
    jPanel4.add(gain, null);
    jPanel4.add(label3D1, null);
    jPanel4.add(gaintext, null);
    jPanel4.add(jLabel1, null);
    hideInTheDark = new JComponent[]{arrow1, arrow3, arrow5, line1, line2, inhib1, obackground, oslip, lbackground, lslip};

  }


  void gain_adjustmentValueChanged(AdjustmentEvent e) {
	if(gain.getValue()!=(int)(45+gainval/2)){
		gainval=gain.getValue()*2.-100;  // convert slider val to [ -100, 100 ]
		vgain=gainval/50.; // -2 to +2
	}
  }
  Label3D label3D4 = new Label3D();
  public void close(){
    sig.timer.stop();
  }

 }
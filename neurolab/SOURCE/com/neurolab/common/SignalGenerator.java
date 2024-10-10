
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab.common;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class SignalGenerator extends JPanel implements ActionListener{
  BorderLayout borderLayout1 = new BorderLayout();
  public JPanel optionPanel = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  public JRadioButton step = new JRadioButton();
  public JRadioButton ramp = new JRadioButton();
  public JRadioButton sinusoidal = new JRadioButton();
  public JPanel sliderPanel = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  public JPanel frequencyPanel = new JPanel();
  public JSlider frequency = new JSlider();
  public JLabel freqLabel = new JLabel();
  BorderLayout borderLayout3 = new BorderLayout();
  public JLabel amplLabel = new JLabel();
  public JSlider amplitude = new JSlider();
  BorderLayout borderLayout2 = new BorderLayout();
  public JRadioButton squareramp = new JRadioButton();

  public ButtonGroup bg=new ButtonGroup();

	final int tickspersecond=20;
	public Timer timer=new Timer(1000/tickspersecond,this);

  public SignalGenerator() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public JPanel amplitudePanel = new JPanel();

	int t=0;
	int maxfreq=2;	//  Hz
	SignalListener listener=null;
	double os;
/*
	public void actionPerformed(ActionEvent e){
		double s=0;
		double period=100./(frequency.getValue()*maxfreq);
		int ticksperiod=(int)(period*tickspersecond);
		if(sinusoidal.isSelected())
			s=amplitude.getValue()*Math.sin(
			++t*2*Math.PI*frequency.getValue()*maxfreq/(tickspersecond*100) );
		else if(step.isSelected()){
//			int ticksperiod=(int)(period*tickspersecond);
			s=((++t%ticksperiod)<(ticksperiod/2))?amplitude.getValue():-amplitude.getValue();
		}else if(ramp.isSelected()){
//			double period=100./(frequency.getValue()*maxfreq);
			int a=amplitude.getValue();
			int rtp=ticksperiod;if(rtp%2>0)rtp++;	//even number value of ticksperiod
			s=((++t%rtp)<(rtp/2))?
				a*4*(t%rtp)/(double)rtp-1.0*a : a*4*(rtp-(t%rtp))/(double)rtp-1.0*a;
		}else if(squareramp.isSelected()){
			int p=++t%ticksperiod;
			int a=amplitude.getValue();
			if(p<ticksperiod/4)s= a*(8.*p/ticksperiod-1);
			else if (p<ticksperiod/2)s= a;
			else if (p<3*ticksperiod/4)s= -a*(8.*p/ticksperiod-4.9);
			else s= -a;
		}
		if(amplitude.getValue()!=0)fireSignalEvent(s);
	}
*/
	double phase=0;
	public void actionPerformed(ActionEvent e){
		double freq=frequency.getValue()*maxfreq/100.;
		phase += freq/tickspersecond;
		phase=phase-(double)(int)phase;	//limit (0-1)
		double a=amplitude.getValue();

		double s=0;
		if(sinusoidal.isSelected()){
			s=a*Math.sin(2*Math.PI*phase);
		}else if(step.isSelected()){
			s=a*((phase<0.5)?1:-1);
		}else if(ramp.isSelected()){
			if(phase<0.5) s=4*a*(phase-0.25);
			else s=4*a*(0.75-phase);
		}else if(squareramp.isSelected()){
			if(phase<0.25) s=8*a*(phase-0.125);
			else if(phase<0.5) s=a;
			else if(phase<0.75) s=8*a*(0.625-phase);
			else s=-a;
		}
		fireSignalEvent(s);
	}


	public void fireSignalEvent(double s){
		if(listener!=null)
			listener.signalEvent(s);
	}
	public void setSignalListener(SignalListener l){
		listener=l;
	}

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    optionPanel.setBackground(Color.lightGray);
    optionPanel.setLayout(gridLayout1);
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    step.setText("Step");
    ramp.setText("Ramp");
    sinusoidal.setText("Sinusoidal");
    sliderPanel.setLayout(gridLayout2);
    gridLayout2.setRows(2);
    gridLayout2.setColumns(1);
    frequencyPanel.setLayout(borderLayout3);
    freqLabel.setHorizontalAlignment(SwingConstants.CENTER);
    freqLabel.setText("Frequency");
    amplLabel.setHorizontalAlignment(SwingConstants.CENTER);
    amplLabel.setText("Amplitude");
    amplitudePanel.setLayout(borderLayout2);
    frequency.setMinimum(1);
    frequency.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        frequency_stateChanged(e);
      }
    });
    squareramp.setText("Square-ramp");
    this.add(optionPanel, BorderLayout.CENTER);
    optionPanel.add(step, null);
    optionPanel.add(ramp, null);
    optionPanel.add(sinusoidal, null);
    optionPanel.add(squareramp, null);
    this.add(sliderPanel, BorderLayout.SOUTH);
    sliderPanel.add(frequencyPanel, null);
    frequencyPanel.add(frequency, BorderLayout.CENTER);
    frequencyPanel.add(freqLabel, BorderLayout.SOUTH);
    frequencyPanel.setBackground(Color.lightGray);
    sliderPanel.add(amplitudePanel, null);
    sliderPanel.setBackground(Color.lightGray);
    amplitudePanel.add(amplLabel, BorderLayout.SOUTH);
    amplitudePanel.add(amplitude, BorderLayout.CENTER);
    amplitudePanel.setBackground(Color.lightGray);
    step.setBackground(Color.lightGray);
    ramp.setBackground(Color.lightGray);
    sinusoidal.setBackground(Color.lightGray);
    squareramp.setBackground(Color.lightGray);
    frequency.setBackground(Color.lightGray);
    amplitude.setBackground(Color.lightGray);
    bg.add(step);
    bg.add(ramp);
    bg.add(sinusoidal);
    bg.add(squareramp);
  }

	double oldf=50;
  void frequency_stateChanged(ChangeEvent e) {
		double oticksperiod=(100./(oldf*maxfreq))*tickspersecond;
		oldf=frequency.getValue();
		double nticksperiod=(100./(oldf*maxfreq))*tickspersecond;
		t=(int)((t/oticksperiod)*nticksperiod);
  }

	int nButtons=4;
	public void addOption(AbstractButton c){
		optionPanel.add(c);
		bg.add(c);
		nButtons++;
		gridLayout1.setColumns(1);
		gridLayout1.setRows(nButtons);
	}
	public void removeOption(AbstractButton c){
		optionPanel.remove(c);
		nButtons--;
		gridLayout1.setColumns(1);
		gridLayout1.setRows(nButtons);
	}
}
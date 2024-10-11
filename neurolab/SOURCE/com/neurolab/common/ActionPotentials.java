// ActionPotentials.class by Sanjay Manohar
package com.neurolab.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/*example of ActionPotentials
	ActionPotentials ap=new ActionPotentials();
	JSlider s;
	getMainContainer().add(s=new JSlider(JSlider.HORIZONTAL,0,20,1));
	s.addChangeListener(this);
	ap.timer.start();
	public void stateChanged(ChangeEvent e){
		ap.setRate(((JSlider)e.getSource()).getValue());
	}
*/

public class ActionPotentials extends Object implements ActionListener{
	private CustomSound16 apsound;
	public javax.swing.Timer timer;
	private static byte[] buffer={
	  (byte)0,(byte)0,
	  (byte)127,(byte)127,(byte)127,(byte)127,(byte)127,(byte)127,
	  };
	private Random rand;
	private int newdelay;
	public ActionPotentials(){
		timer=new javax.swing.Timer(1000,this);
		timer.setInitialDelay(0);
		timer.stop();	// initialise timer
		double buf[] = {0, 1,0};
		apsound=new CustomSound16(buf);
		apsound.loops=1;
		apsound.open(buf);
		rand=new Random();
	}
	double frequency;
	public void setRate(double newfreq){
    frequency=newfreq;
		if(newfreq<=0)timer.stop();
		else {
			newdelay=(int)(1000./newfreq);
			if(newdelay>10000 || newdelay<0){ timer.stop(); return;}
			timer.setDelay(newdelay);
			if((System.currentTimeMillis()-mstimer)>newdelay) {
			  timer.setDelay(1);
				timer.restart();
			}
			else timer.start();
		}
	}
	public void doSingleAP() {
	  if(!apsound.isActive())
	    apsound.playOnce();
	}
	public double getRate(){
		int delay=timer.getDelay();
		if(delay!=0)
			return 1000.0/delay;
		else return 1000.0;
	}
	long mstimer;
	int VAR = 50; // milliseconds variability in spike timing, around the frequency 
	public void actionPerformed(ActionEvent e){
	  long ct =System.currentTimeMillis(),  dt=ct-mstimer;
		mstimer=ct;
		if(frequency>0)		doSingleAP();
		int ndel=Math.max(1, newdelay + (rand.nextInt()%VAR)-VAR/2 );
		timer.setDelay(ndel); 
//System.out.println(frequency+": "+ndel+", dt="+dt);
	}
}

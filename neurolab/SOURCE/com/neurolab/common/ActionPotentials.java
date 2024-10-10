// ActionPotentials.class by Sanjay Manohar
package com.neurolab.common;

import javax.swing.Timer;
import java.awt.event.*;
import java.util.Random;
import com.neurolab.common.CustomSound;

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
	private CustomSound apsound;
	public javax.swing.Timer timer;
	private static byte[] buffer={(byte)127,(byte)127,(byte)127,(byte)127,(byte)127,(byte)127};
	private Random rand;
	private int newdelay;
	public ActionPotentials(){
		timer=new javax.swing.Timer(1000,this);
		timer.setInitialDelay(0);
		timer.stop();	// initialise timer
		apsound=new CustomSound(buffer);
		apsound.loops=1;
		apsound.open(buffer);
		rand=new Random();
	}
	public void setRate(double newfreq){
		if(newfreq<=0)timer.stop();
		else {
			newdelay=(int)(1000./newfreq);
			if(newdelay>10000 || newdelay<0){ timer.stop(); return;}
			timer.setDelay(newdelay);
			if((System.currentTimeMillis()-mstimer)>newdelay)
				timer.restart();
			else timer.start();
//System.out.println("Timer="+String.valueOf(newdelay));
		}
	}
	public double getRate(){
		int delay=timer.getDelay();
		if(delay!=0)
			return 1000.0/delay;
		else return 1000.0;
	}
	long mstimer;
	public void actionPerformed(ActionEvent e){
		mstimer=System.currentTimeMillis();
		apsound.playOnce();
		timer.setDelay(Math.max(1,newdelay+(rand.nextInt()%15)-7));
	}
}

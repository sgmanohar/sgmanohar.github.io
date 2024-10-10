// CustomSound.class by Sanjay Manohar

///IMPLEMENTED VERSION

/*example of CustomSound:
	byte[] buf=new byte[256];
	for(int i=0;i<256;buf[i++]=Math.Sin(Math.PI/128));
	CustomSound sound=new CustomSound(buf);
	Button but=new Button("Play");
	but.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){sound.start();}
		public void mouseReleased(MouseEvent e){sound.stop();}
	} );
	add(but);
	// Mixer will destroy open clips, so close() not required
	// default quality is 22.05 kHz so frequency = 86 Hz
*/
package com.neurolab.common;

import javax.swing.Timer;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.lang.reflect.*;
//v2	with class ActionPotentials

public class CustomSound extends Object{
	private byte[] pBuffer;
	private int length;
	private Clip clp;
	private Mixer mix;
	private boolean silent=false;
	public CustomSound(byte[] p){
		open(p);
	}
	int loops=64;
	public void open(byte[] p){
		pBuffer=new byte[loops*p.length];
		for(int i=0;i<p.length;i++){
			byte value=(byte)((int)p[i]-192);
			for(int j=0;j<loops;j++) pBuffer[j*p.length+i]=value;
		}
		length=Array.getLength(pBuffer);
		mix=AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
		try {clp=(Clip)mix.getLine(mix.getSourceLineInfo(
			new Line.Info(Class.forName("javax.sound.sampled.Clip")) )[0]);
			AudioFormat af=new AudioFormat((float)22050.,8,1,true,false);
			clp.open(af,pBuffer,0,length);
		} catch (Exception e) {
			e.printStackTrace();
			silent=true;
		}
		FloatControl gain=(FloatControl)clp.getControl(FloatControl.Type.MASTER_GAIN);
		gain.setValue(gain.getMaximum()/8);
	}
	public void close(){
		if(clp!=null){
			if(isActive())stop();
			if(clp.isOpen())clp.close();
		}
	}
	public void start(){
		if(!silent)clp.loop(clp.LOOP_CONTINUOUSLY);
	}
	public void playOnce(){
		if(!silent)clp.loop(1);
	}
	public void stop(){
		if(!silent){
			clp.stop();
			clp.setFramePosition(0);
		}
	}
	public boolean isActive(){
		if(!silent)return clp.isActive();
		else return false;
	}
	public void finalize() throws Throwable{
		if(!silent)clp.close();
		super.finalize();
	}
}

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
/*
class ActionPotentials extends Object implements ActionListener{
	private CustomSound apsound;
	public javax.swing.Timer timer;
	private static byte[] buffer={(byte)127,(byte)127,(byte)127,(byte)127,(byte)127,(byte)127};
	public ActionPotentials(){
		timer=new javax.swing.Timer(1000,this);
		timer.setInitialDelay(0);
		timer.stop();	// initialise timer
		apsound=new CustomSound(buffer);
	}
	public void setRate(float newfreq){
		if(newfreq==0)timer.stop();
		else {
			timer.setDelay((int)(1000./newfreq));
			timer.start();
		}
	}
	public float getRate(){
		int delay=timer.getDelay();
		if(delay!=0)
			return 1000/delay;
		else return 1000;
	}
	public void actionPerformed(ActionEvent e){
		apsound.playOnce();
	}
}
*/

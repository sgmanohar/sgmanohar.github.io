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

import java.lang.reflect.Array;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class CustomSound extends Object{
	private byte[] pBuffer;
	private int length;
	private Clip clp;
	private Mixer mix;
	private boolean silent=false;
	public CustomSound(byte[] p){
    mix=AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
		open(p);
	}
	int loops=1;
	public void open(byte[] p){
		pBuffer=new byte[loops*p.length];
		for(int i=0;i<p.length;i++){
			byte value=(byte)((int)p[i]);
			for(int j=0;j<loops;j++) pBuffer[j*p.length+i]=value;
		}
		reopen();
	}
	void reopen() { // call clip.open if needed
	   length=Array.getLength(pBuffer);
	    try {
	      if(clp==null || !clp.isOpen()) {
	        clp=(Clip)mix.getLine(mix.getSourceLineInfo(
	            new Line.Info(Class.forName("javax.sound.sampled.Clip")) )[0]);
	      }
	      AudioFormat af=new AudioFormat(44100f,8,1,  false /* SIGNED */ ,false);
	      if(clp.isOpen())clp.close();
	      clp.open(af,pBuffer,0,length);
	    } catch (Exception e) {
	      e.printStackTrace();
	      silent=true;
	    }
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
    if(clp.isActive())
      clp.stop();
    if(!clp.isOpen())reopen();
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
		mix.close();
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

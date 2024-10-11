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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class CustomSound16 extends Object{
	private byte[] pBuffer;
	private int length;
	private Clip clp;
	private Mixer mix;
	private boolean silent=false;
	public CustomSound16(double[] p){
    mix=AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
		open(p);
	}
	int loops=1;
	/** this version takes a signed double from -1 to +1 */
	public void open(double[] p){
		pBuffer=new byte[loops*p.length*2];
		for(int i=0;i<p.length;i++){
		  int x = (int)(Math.max(-1,Math.min(1,p[i]))*32767); 
			byte lo=(byte)((int)(x & 0xff)),
			     hi=(byte)((int)( (x>>8) & 0xff ));
			for(int j=0;j<loops;j++) {
			  pBuffer[j*p.length*2+i*2]   = lo;
        pBuffer[j*p.length*2+i*2+1] = hi; // little-endian 16bit
			}
		}
		reopen();
	}
	void reopen() { // call clip.open if needed
	   length=pBuffer.length;
	    try {
	      if(clp==null || !clp.isOpen()) {
	        clp=(Clip)mix.getLine(mix.getSourceLineInfo(
	            new Line.Info(Class.forName("javax.sound.sampled.Clip")) )[0]);
	      }
	      AudioFormat af=new AudioFormat(44100f,16,1,  true /* SIGNED */ ,false);
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
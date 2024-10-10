
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

//import javax.sound.midi.*;

import javax.swing.Timer;
import java.awt.event.*;


public class MidiGenerator {

//	MidiDevice md;
//	Receiver rec;
	public MidiGenerator() {
		timer.setRepeats(false);
/*		try{
			md=MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[0]);
			md.open();
			rec=md.getReceiver();
		}catch(Exception e){e.printStackTrace();}
*/	}
	int currnote=-1;
	public void playNote(int note){
		if(currnote!=-1){
			stopNote();
		}
/*		ShortMessage m=new ShortMessage();
		try{
			m.setMessage(ShortMessage.NOTE_ON,1,note,127);
			rec.send(m,-1);
		}catch(Exception e){e.printStackTrace();}
*/		timer.start();
	}
	Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			stopNote();
		}
	});
	public void stopNote(){
		if(currnote!=-1){
/*			ShortMessage m=new ShortMessage();
			try{
				m.setMessage(ShortMessage.NOTE_OFF,1,currnote,0);
				rec.send(m,-1);
			}catch(Exception e){e.printStackTrace();}
*/			currnote=-1;
			timer.stop();
		}
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
/*		rec.close();
		md.close();
*/	}
}
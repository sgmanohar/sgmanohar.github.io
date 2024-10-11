package com.neurolab.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.Timer;

public class MidiImpl{
  MidiDevice md;
  Receiver rec;
  public MidiImpl() {
    timer.setRepeats(false);
    try{
      md=MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[0]);
      md.open();
      rec=md.getReceiver();
    }catch(Exception e){e.printStackTrace();}
  }
  int currnote=-1;
  public void playNote(int note){
    if(rec==null)return;
    if(currnote!=-1){
      stopNote();
    }
    ShortMessage m=new ShortMessage();
    try{
      m.setMessage(ShortMessage.NOTE_ON,1,note,127);
      rec.send(m,-1);
    }catch(Exception e){e.printStackTrace();}
    timer.start();
  }
  Timer timer=new Timer(100,new ActionListener(){
    public void actionPerformed(ActionEvent e){
      stopNote();
    }
  });
  public void stopNote(){
    if(currnote!=-1){
      ShortMessage m=new ShortMessage();
      try{
        m.setMessage(ShortMessage.NOTE_OFF,1,currnote,0);
        rec.send(m,-1);
      }catch(Exception e){e.printStackTrace();}
      currnote=-1;
      timer.stop();
    }
  }
  public void finalize() throws Throwable{
    close();
    super.finalize();
  }
  public void close(){
    if(rec!=null)rec.close();
    if(md!=null)md.close();
  }
}


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

import javax.sound.midi.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.lang.reflect.Method;


public class MidiGenerator {
  public MidiGenerator() {
    try {
      clas=Class.forName("com.neurolab.common.MidiImpl");
      playmethod=clas.getMethod("playNote", new Class[] {int.class});
      midiimpl  =clas.newInstance();
    }catch(Exception e) {e.printStackTrace();}
  }
  Class clas;
  Object midiimpl;
  Method playmethod=null;
  public void playNote(int note) {
    if(playmethod==null)return;
    try {
      playmethod.invoke(midiimpl, new Object[] {new Integer(note)});
    }catch(Exception e) {e.printStackTrace();}
  }
  public void close() {
    if(midiimpl!=null) {
      Method cl;
      try{
        cl = clas.getMethod("close", new Class[] {});
        cl.invoke(midiimpl, new Object[] {});
      }catch(Exception e) {e.printStackTrace();}
    }
  }
}
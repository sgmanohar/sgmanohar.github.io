package com.neurolab.common;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputAdapter;


public class Spinner extends JSpinner
             {
  MouseInputAdapter p=new MouseInputAdapter()
                     {int start;
    public void mousePressed(MouseEvent e)
      {
      start=e.getY();
      }
    public void mouseDragged(MouseEvent e)
      {
      int n=(e.getY()-start);
      if(n>0) for (int i=0;i<n/4;i++)
        setValue(getModel().getNextValue());
      else if(n<0) for(int i=0;i<-n/4;i++)
        setValue(getModel().getPreviousValue());
    }
  } ;
  KeyListener k=new KeyAdapter(){
    public void keyTyped(KeyEvent e){
      if(e.getKeyChar()==10 || e.getKeyChar()==13)
        fireStateChanged();
    }
  };
  public Spinner()
                     {addMouseListener(p);
    addMouseMotionListener(p);
    setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
    addKeyListener(k);
  }

}
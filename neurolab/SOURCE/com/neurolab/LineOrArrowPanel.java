package com.neurolab;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class LineOrArrowPanel extends JPanel{
  LineOrArrowPanel(){
    setOpaque(false);
  }
  public int lineThickness = 3;
  private int locations = LOC_H;
  private boolean arrowStart = false;
  private boolean arrowEnd = false;
  private boolean inhibEnd = false;
  private boolean inhibStart=false;
  private int arrowSize = 6;
  public static final int LOC_L=1, LOC_R=2, LOC_T=4, LOC_B=8, LOC_D1=16, LOC_D2=32, LOC_H=64, LOC_V=128;
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2=(Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(new BasicStroke(lineThickness));
    int w=getWidth(), h=getHeight(), a=getArrowSize(), 
    m=2; // margin
    if((getLocations() & LOC_L) > 0)  g2.drawLine(m,m,m,h-m);
    if((getLocations() & LOC_R) > 0)  g2.drawLine(w-m,m,w-m,h-m);
    if((getLocations() & LOC_T) > 0)  g2.drawLine(m,m,w-m,m);
    if((getLocations() & LOC_B) > 0)  g2.drawLine(m,h-m,w-m,h-m);
    if((getLocations() & LOC_D1) > 0) g2.drawLine(m,m,w-m,h-m);
    if((getLocations() & LOC_D2) > 0) g2.drawLine(w-m,m,m,h-m);
    if((getLocations() & LOC_H) > 0) {
      g2.drawLine(m,h/2,w-m,h/2);
      if(isArrowEnd())  {g2.drawLine(w-m,h/2,w-a,h/2-a); g2.drawLine(w-m,h/2,w-a,h/2+a); }
      if(isArrowStart()){g2.drawLine(m,h/2,a,h/2-a);   g2.drawLine(m,h/2,a,h/2+a); }
      if(isInhibEnd())  {g2.fillOval(w-a,h/2-a/2,a,a); }
      if(isInhibStart()){g2.fillOval(0,  h/2-a/2,a,a);}
    }
    if((getLocations() & LOC_V) > 0) {
      g2.drawLine(w/2,m,w/2,h-m);
      if(isArrowEnd())  {g2.drawLine(w/2,h-m,w/2-a,h-a); g2.drawLine(w/2,h-m,w/2+a,h-a); }
      if(isArrowStart()){g2.drawLine(w/2,m,w/2-a,a);   g2.drawLine(w/2,m,w/2+a,a); }      
      if(isInhibEnd())  {g2.fillOval(w/2-a/2,h-a,a,a); }
      if(isInhibStart()){g2.fillOval(w/2-a/2,0,  a,a);}
    } 
  }
  public void setArrowStart(boolean arrowStart){
    this.arrowStart = arrowStart;
  }
  public boolean isArrowStart(){
    return arrowStart;
  }
  public void setArrowEnd(boolean arrowEnd){
    this.arrowEnd = arrowEnd;
  }
  public boolean isArrowEnd(){
    return arrowEnd;
  }
  public void setLocations(int locations){
    this.locations = locations;
  }
  public int getLocations(){
    return locations;
  }
  public void setArrowSize(int arrowSize){
    this.arrowSize = arrowSize;
  }
  public int getArrowSize(){
    return arrowSize;
  }
  public void setInhibEnd(boolean inhibEnd){
    this.inhibEnd = inhibEnd;
  }
  public boolean isInhibEnd(){
    return inhibEnd;
  }
  public void setInhibStart(boolean inhibStart){
    this.inhibStart = inhibStart;
  }
  public boolean isInhibStart(){
    return inhibStart;
  }
}

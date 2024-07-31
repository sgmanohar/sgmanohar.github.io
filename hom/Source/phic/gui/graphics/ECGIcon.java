package phic.gui.graphics;

import phic.gui.*;
import phic.common.*;
import phic.Body;
import phic.ecg.*;
import javax.sound.midi.*;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import phic.gui.exam.Auscultation;

/**
 * Dynamic small ECG diagram, which produces a heart rate / saturation bleep
 */

public class ECGIcon extends JPanel implements Ticker {
  Body body;
  public ECGIcon(Body body) {
    this();
    setBody(body);
  }
  public ECGIcon(){
    addComponentListener(compListen);
    jbInit();
  }
  /** set the parameters of this ECG from the given body variables */
  public void setBody(Body body){
    this.body=body;
    heart=new PhicHeart(body);
    setBackground(Color.black);
    setForeground(Color.green);
  }

  ComponentListener compListen = new ComponentAdapter(){
    public void componentHidden(ComponentEvent e) {
      HorizontalBar.removeBar(ECGIcon.this);
      if (noteOn >= 0) chan.noteOff(noteOn);
      if(auscultation!=null)auscultation.stopSound();
    }
    public void componentShown(ComponentEvent e) {
      HorizontalBar.addBar(ECGIcon.this);
      if(auscultation!=null)auscultation.startSound();
    }
  };
  public void addNotify(){
    super.addNotify();
    try {
      synth = MidiSystem.getSynthesizer();
      synth.open();
      chan=synth.getChannels()[0];
      chan.programChange(80);//square wave?
      chan.controlChange(6,0);//turn off reverb?
      sound=true;
    }
    catch (MidiUnavailableException ex) {
      ex.printStackTrace();
      sound=false;
      soundcheck.setEnabled(false);
      soundcheck.setSelected(false);
    }
    if(auscultation!=null)auscultation.setBody(body);
  }
  public void removeNotify(){
    abortNote();
    super.removeNotify();
  }
  public void abortNote(){
    if(synth!=null){
      if (noteOn >= 0) chan.noteOff(noteOn);
      synth.close();
    }
  }

  Auscultation auscultation = /* Auscultation.createAuscultation() */ null;
  Color lineCol = new Color(0,128,0);
  /** This is set to true when the synthesiser is created */
  boolean sound=false;
  int noteOn = -1;
  int volume = 60;
  Synthesizer synth;
  MidiChannel chan;
  PhicHeart heart;
  double[][] fields;
  double[][] trace;
  Lead lead = Trace.leads[2];
  public void refreshBeat(){
    fields=heart.getBeatsField(60 / body.CVS.heart.rate.get());
  }
  double secPerBeat = 1;
  int illuminated=0;
  double soundCumTime = 0;
  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JCheckBoxMenuItem soundcheck = new JCheckBoxMenuItem();
  JCheckBoxMenuItem animcheck = new JCheckBoxMenuItem();
  /** set to true for the start of each heart beat. */
  boolean needsCalc = true;
  /** set to true if animation is enabled */
  boolean animate = true;
  /** set to true if sound is enabled */
  boolean soundEnabled = true;
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  //previous phase
  double oph;
  /**
   * Calculate the next frame of the ECG, and play appropriate sound if
   * necessary
   */
  public void tick(double t){
    double ph=body.CVS.heart.phase;
    //bleeper
    if(noteOn>=0) soundCumTime+=t;
    if(sound && soundEnabled && body.getClock().isSlowMode){
      if(noteOn<0 && ph<oph){
        noteOn = (int) (body.blood.SatO2.get() * 100) - 14;
        chan.noteOn(noteOn, volume);
        soundCumTime=0;
      }else if(noteOn>=0 && (ph>oph || soundCumTime>0.2)){
        chan.noteOff(noteOn);
        if(ph>0.2)noteOn = -1;
      }
    }
    if(ph<oph) {
      recalculateECG();
      needsCalc=false;
      if(!animate)repaint();
    } else if(ph>0.2) needsCalc=true;
    if(animate){
      secPerBeat = 60/body.CVS.heart.rate.get();
      if(trace!=null){
        for (int i = 0; i < trace.length; i++) {
          if (trace[i][0] / secPerBeat >= ph) {illuminated = i;break;}
        }
        repaint();
      }
    }
    oph=ph;
    if(auscultation!=null)auscultation.tick(t);
  }
  void recalculateECG(){
    fields = heart.getBeatsField(secPerBeat);
    trace = lead.getPoints(fields);
  }
  public void paint(Graphics g){
    super.paint(g);
    if(trace!=null){
      g.setColor(lineCol);
      Graphics2D g2=(Graphics2D)g;
      g2.setStroke(new BasicStroke(1));
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      double xscale = getWidth() / secPerBeat, yscale = -getHeight()/2., y0=getHeight()/2.;
      int ox=0,oy=getHeight()/2;
      for(int i=0;i<trace.length;i++){
        if(i==illuminated) g.setColor(getForeground()); else g.setColor(lineCol);
        g.drawLine(ox,oy,ox=(int)(trace[i][0]*xscale), oy=(int)(trace[i][1]*yscale+y0));
      }
    }
  }
  private void jbInit() {
    soundcheck.setSelected(true);
    soundcheck.setText("Sound");
    animcheck.setSelected(true);
    animcheck.setText("Animation");
    animcheck.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
        animate = animcheck.isSelected();
        if(!animate) illuminated=-1;
    }});
    soundcheck.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
        soundEnabled=soundcheck.isSelected();
        if(!soundEnabled)abortNote();
      }});
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getButton()==MouseEvent.BUTTON3)jPopupMenu1.show(ECGIcon.this,e.getX(),e.getY());
      }
    });
    jMenu1.setText("Lead");
    jPopupMenu1.add(soundcheck);
    jPopupMenu1.add(animcheck);
    jPopupMenu1.add(jMenu1);
    for(int i=0;i<Trace.leads.length;i++){
      jMenu1.add(new AbstractAction(Trace.leads[i].name){public void actionPerformed(ActionEvent e){
          setLead(e.getActionCommand());
      }});
    }
  }
  void setLead(String lead){
    for(int i=0;i<Trace.leads.length;i++){
      if(Trace.leads[i].name.equalsIgnoreCase(lead)) {
        this.lead = Trace.leads[i];
        recalculateECG();
      }
    }
  }



}

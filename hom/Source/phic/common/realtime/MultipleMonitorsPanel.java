package phic.common.realtime;

import java.awt.*;
import phic.common.*;
import phic.Current;
import javax.swing.*;
import java.util.Vector;
import java.awt.event.*;

/**
 * A panel to show monitors of the realtime variables.
 * They are displayed in a horizontal manner, overwriting from left-to-right.
 */

public class MultipleMonitorsPanel extends JPanel {
  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JMenuItem zerocommand = new JMenuItem();

  /** List of currently monitored DisplayVariables */
  Vector vars = new Vector();

  public MultipleMonitorsPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    thread.start();
  }
  private void jbInit() throws Exception {
    this.setBackground(Color.black);
    this.setForeground(Color.green);
    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        this_mouseReleased(e);
      }
    });
    this.setLayout(null);
    zerocommand.setText("Zero");
    zerocommand.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
        command(e);
    }});
    jPopupMenu1.add(zerocommand);
  }
  /** Add a variable to the monitors */
  public void addVariable(Variable v, String n){
    DisplayVariable dv=new DisplayVariable(v,n);
    vars.add(dv);
    dv.color = colors[serial++%colors.length];
    dv.label.setForeground(dv.color);
    rescale();
  }
  /** Repositions the labels. Should be called when the panel changes size. */
  void rescale(){
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv=(DisplayVariable)vars.get(i);
      dv.resetPoints();
      Dimension d=dv.label.getPreferredSize();
      dv.label.setBounds(0,i*getHeight()/vars.size()+10,d.width,d.height);
      dv.baseY = (int)((i+0.5)*getHeight()/vars.size());
    }
  }
  /** Call super.paint and then each displayVariable's paint. */
  public void paint(Graphics g){
    super.paint(g);
    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv = (DisplayVariable)vars.get(i);
      g.setColor(dv.color);
      dv.paint(g);
    }
  }

  final Color[] colors={Color.red,Color.green,Color.magenta,
    Color.yellow,Color.cyan,Color.pink,Color.gray,Color.orange};
  int serial=0;


  /** Respond to user */
  void command(ActionEvent e) {
    if(e.getActionCommand().equals("Zero")){
      for(int i=0;i<vars.size();i++){
        DisplayVariable dv= (DisplayVariable)vars.get(i);
        dv.zero();
      }
    }
  }
  void this_mouseReleased(MouseEvent e) {
    if(e.isPopupTrigger()){
      jPopupMenu1.show(this, e.getPoint().x, e.getPoint().y);
    }
  }

  void this_componentResized(ComponentEvent e) {
    rescale();
  }

  /** Timebase */
  public int timeDelay = 100;

  /** Timer to tick each variable regularly */
  Thread thread = new Thread(new Runnable(){public void run(){
    boolean running=true;
    while(running){
      while(Current.body.getClock().running){
        for(int i=0;i<vars.size();i++){
          DisplayVariable dv=(DisplayVariable)vars.get(i);
          dv.tick();
        }
        repaint();
        try{
          Thread.sleep(timeDelay);
        } catch(InterruptedException ex){}
      }
      try{
        Thread.sleep(300);
      } catch(InterruptedException ex1){}
    }
    }
  },"MonitoringScreen"
  );



  /** This represents on displayed variable on the monitor. */
  class DisplayVariable{
    Variable variable;
    JLabel label;
    /** Color of this line */
    Color color;

    /** How many points have we got so far in the current zeroing process */
    int zeroingCount = 0;

    /**
     * The running total, min, and max of all the points we have sampled
     * so far in the current zeroing process
     */
    double zeroTotal = 0, zeroingMax=Double.MIN_VALUE, zeroingMin = Double.MAX_VALUE;

    /** How many points to sample to get a zero */
    int maxZeroingCount=180;

    /** indicates whether we are waiting to get a zero */
    boolean zeroing=true;

    /** The number of pixels of horizontal motion per tick of time */
    int timebase=4;

    public DisplayVariable(Variable variable, String name){
      this.variable = variable;
      label=new JLabel(name);
      add(label);
      resetPoints();
    }
    /** The current horizontal position of the trace */
    int x;
    /** The baseline Y position, depends upon the position of this variable on the screen */
    int baseY;
    /**
     * Value of the variable corresponding to the baseline.
     * This is altered with rescaling, but initially is the initialValue of the variable.
     */
    double baseValue;
    /** The value by which to multiply the y coordinate. */
    double scaleY;

    /** The data for all the current points being displayed */
    int[] y;
    /** Clear all displayed points */
    void resetPoints(){
      y = new int[getWidth()/timebase];
      for(int i=0;i<y.length;i++)y[i]=baseY;
    }
    /** Paint all the points in y */
    public void paint(Graphics g){
      for(int i=1;i<y.length;i++){
        g.drawLine((i-1)*timebase,y[i-1],i*timebase,y[i]);
      }
      if(zeroing){
        g.drawString("Zeroing...",0,baseY+10);
      }
    }
    /** Move to the next point */
    void tick(){
      double value=variable.get();
      if(!Current.body.clock.isSlowMode) return;
      if(zeroing){
        zeroTotal+=value;
        zeroingMin = Math.min(zeroingMin, value);
        zeroingMax = Math.max(zeroingMax, value);
        zeroingCount++;
        if(zeroingCount>maxZeroingCount) finishedZeroing();
      }else{
        if(y.length==0)return;
        x+=timebase;x%=y.length*timebase;
        y[x/timebase]=(int)(-(value-baseValue)*scaleY+baseY);
      }
    }
    /** Turn on the zeroing, so that for the next series of tick()s, zeroing is awaited. */
    public void zero(){
      zeroing=true;
      zeroingCount=0;
      zeroTotal=0;
    }
    /** Called once all the points have been sampled in the zeroing process. */
    void finishedZeroing(){
      if(zeroingMax==zeroingMin){ zeroingMax+=1; System.out.println("Did not zero successfully"); }
      zeroing=false;
      baseValue = zeroTotal/zeroingCount;
      scaleY = (getHeight()/vars.size())/(zeroingMax-zeroingMin);
    }
  }

}

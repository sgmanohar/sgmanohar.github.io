package phic.gui;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import phic.Current;
import phic.common.Clock;
import java.awt.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Control to set the time-compression rate of the clock
 */
public class RatePanel extends JPanel{
  public RatePanel(){
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    init();
    timer.start();
  }

  static final String[] timeunit={"second","minute","hour","day","week"
  };
  static final long[] millis={1000,60000,3600000,86400000,604800000
  };
  private DefaultComboBoxModel model1=new DefaultComboBoxModel(timeunit);
  private DefaultComboBoxModel model2=new DefaultComboBoxModel(timeunit);
  private void jbInit() throws Exception{
    ok.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        pp.hide();
      }
    });
    ok.setText("OK");
    jLabel4.setText("  s/f");
    jLabel2.setText("1");
    drop1.setModel(model1);
    drop1.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        change(e);
      }
    });
    jLabel1.setText("per");
    fpslabel.setText("0");
    jLabel5.setText("Body timeslice:");
    drop2.setModel(model2);
    drop2.addActionListener(new  ActionListener(){
      public void actionPerformed(ActionEvent e){
        change(e);
      }
    });
    single.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        Clock c=Current.body.getClock();
        double sc=millis[drop1.getSelectedIndex()];
        c.elapseTime(sc/1000);
        Current.thread.tickOnce();
      }
    });
    this.setLayout(borderLayout1);
    jLabel3.setText("Bunching correction");
    correctionTxt.setText("0");
    jLabel7.setText("s");
    single.setToolTipText("Execute a single time step of the size selected in dropdown 1");
    single.setText("Step");
    jPanel2.setLayout(borderLayout2);
    jCheckBox1.setToolTipText("This makes every frame of computation last a fixed duration of 10 " +
    "ms");
    jCheckBox1.setText("Freeze @100Hz!");
    jCheckBox1.addActionListener(freezeFramerateAction);
    jPanel6.setLayout(borderLayout3);
    jPanel7.setLayout(borderLayout4);
    jLabel6.setBackground(SystemColor.activeCaption);
    jLabel6.setForeground(SystemColor.activeCaptionText);
    jLabel6.setBorder(null);
    jLabel6.setOpaque(true);
    jLabel6.setToolTipText("These options are for accurate control of the engine\'s timing parameters, " +
    "for debugging purposes");
    jLabel6.setText("Advanced timing settings");
    this.add(jPanel1,BorderLayout.CENTER);
    jPanel1.add(jLabel2,null);
    jPanel1.add(drop1,null);
    jPanel1.add(jLabel1,null);
    jPanel1.add(drop2,null);
    this.add(jPanel2,BorderLayout.SOUTH);
    jPanel2.add(jPanel5,  BorderLayout.EAST);
    jPanel5.add(jCheckBox1, null);
    jPanel2.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jLabel5, null);
    jPanel4.add(fpslabel, null);
    jPanel4.add(jLabel4, null);
    jPanel3.add(jLabel3,null);
    jPanel3.add(correctionTxt,null);
    jPanel3.add(jLabel7,null);
    jPanel6.add(jPanel7, BorderLayout.NORTH);
    jPanel7.add(jLabel6,  BorderLayout.CENTER);
    this.add(eastpanel,  BorderLayout.EAST);
    eastpanel.setLayout(new BorderLayout());
    eastpanel.add(ok);
    eastpanel.add(single, BorderLayout.NORTH);
    this.add(jPanel6,  BorderLayout.NORTH);
    jPanel6.add(jPanel3, BorderLayout.CENTER);
  }

  /**
   * Find the nearest combination of the two fields that will match a
   * given compression, as specified by phic.common.Clock. The two fields
   * are set to this setting, and the return value is the actual value of
   * the second.
   *
   * @param second milliseconds of real time that correspond to one second
   * of body time. 1000 => no compression.
   *
   * @return the actual value of 'second' that has been found that closest
   * matches the parameter supplied.
   */
  double setFromSecond(double second){
    double minDist=Double.MAX_VALUE;
    int a=-1,b=-1;
    double compression=1000/second;
    for(int i=0;i<millis.length;i++){
      for(int j=0;j<millis.length;j++){
        double comp2=millis[i]/(double)millis[j];
        double dist=Math.abs(comp2-compression);
        if(dist<minDist){
          minDist=dist;
          a=i;
          b=j;
        }
      }
    }
    drop1.setSelectedIndex(a);
    drop2.setSelectedIndex(b);
    return 1000/(millis[a]/(double)millis[b]);
  }
  NumberFormat nf=new DecimalFormat("0.00E0");

  /**
   * Timer to update the seconds per frame counter.
   */
  Timer timer=new Timer(750,new ActionListener(){
    public void actionPerformed(ActionEvent e){
      double et=Clock.getAverageElapsedTime();
      fpslabel.setText(nf.format(et));
      correctionTxt.setText(String.valueOf(((int)(100*(Clock.getExactElapsedTime()-
        et)))/100));
    }
  });
  private JPanel jPanel1=new JPanel();
  private JLabel jLabel4=new JLabel();
  private JLabel jLabel2=new JLabel();
  private JComboBox drop1=new JComboBox();
  private JLabel jLabel1=new JLabel();
  private JLabel fpslabel=new JLabel();
  private JLabel jLabel5=new JLabel();
  private JComboBox drop2=new JComboBox();
  private JPanel jPanel2=new JPanel();
  private BorderLayout borderLayout1=new BorderLayout();
  private JPanel jPanel3=new JPanel();
  private JLabel jLabel3=new JLabel();
  private JLabel correctionTxt=new JLabel();
  private JLabel jLabel7=new JLabel();
  public void finalize() throws Throwable{
    timer.stop();
    super.finalize();
  }

  /**
   * Static method to set the clock to the closest value that matches a
   * compression ratio; Return a string representing the compression.
   */
  static String setClockToClosest(double second){
    double minDist=Double.MAX_VALUE;
    int a=-1,b=-1;
    double compression=1000/second;
    for(int i=0;i<millis.length;i++){
      for(int j=0;j<millis.length;j++){
        double comp2=millis[i]/(double)millis[j];
        double dist=Math.abs(comp2-compression);
        if(dist<minDist){
          minDist=dist;
          a=i;
          b=j;
        }
      }
    }
    double newSecond=1000/(millis[a]/(double)millis[b]);
    Current.body.getClock().setSecond(newSecond);
    return "1 "+timeunit[a]+" per "+timeunit[b];
  }
  JButton ok=new JButton();
  JPanel eastpanel = new JPanel();

  JWindow pp;
  JButton single = new JButton();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JCheckBox jCheckBox1 = new JCheckBox();
  JPanel jPanel6 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JLabel jLabel6 = new JLabel(); // the currently displayed window for this instance
  public static void showSmallDialog(int x,int y){
    RatePanel r=new RatePanel();
    r.setPanelLocation(x,y);
  }
  public void setPanelLocation(int x,int y){
    pp.setLocation(x,y);
  }
  public void init(){
    pp=new JWindow(PhicApplication.frame.getJFrame());
    JPanel panel=new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(new BevelBorder(BevelBorder.RAISED));
    pp.getContentPane().add(panel);
    setFromSecond(Current.body.getClock().getSecond());
    jCheckBox1.setSelected(Current.thread.fixedFrameDuration);
    panel.add(this,BorderLayout.CENTER);
    panel.add(ok,BorderLayout.EAST);
    pp.setSize(320,120);
    panel.addFocusListener(new FocusAdapter(){
      public void focusLost(FocusEvent e){
        pp.hide();
        pp.dispose();
      }
    });
    pp.show();
    pp.requestFocus();
  }

  /**
   * Called when the units in the dropdown boxes are changed.
   * This calculates the required time compression in milliseconds of
   * real time per second of body time, and uses this value to set the
   * rate of time compression in the clock Current.body.getClock()
   */
  void change(ActionEvent e){
    double compression=millis[drop1.getSelectedIndex()]/
      (double)millis[drop2.getSelectedIndex()];
    double second=1000/compression;
    Current.body.getClock().setSecond(second);
  }

  Action freezeFramerateAction = new AbstractAction("Freeze framerate"){
    public void actionPerformed(ActionEvent e){
      Current.thread.fixedFrameDurationMillis = 10;
      Current.thread.fixedFrameDuration = jCheckBox1.isSelected();
    }
  };

}

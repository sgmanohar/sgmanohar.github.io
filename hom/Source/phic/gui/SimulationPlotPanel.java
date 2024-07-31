package phic.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import phic.modifiable.Script;
import evaluator.Expression;
import evaluator.*;
import evaluator.*;
import evaluator.*;
import phic.Body;
import phic.Current;
import phic.common.Clock;
import phic.common.Quantity;
import java.lang.reflect.Field;
import phic.common.VDouble;
import phic.common.CommonThread;

/**
 * Simulation plotter
 * Runs the HOM model multiple times with a given script,
 * allowing the user to systematically vary any parameter
 * and plot the value of an expression on a graph.
 */

public class SimulationPlotPanel extends JPanel{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JSpinner samplestxt = new JSpinner();
  BorderLayout borderLayout3 = new BorderLayout();
  JSpinner divisionstxt = new JSpinner();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JPanel jPanel6 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea scripttxt = new JTextArea();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  JLabel jLabel3 = new JLabel();
  JTextField xmintxt = new JTextField();
  JTextField xmaxtxt = new JTextField();
  JSplitPane jSplitPane1 = new JSplitPane();
  JLabel jLabel1 = new JLabel();
  JTextField timetxt = new JTextField();
  JLabel jLabel4 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea ytxt = new JTextArea();
  JLabel jLabel5 = new JLabel();
  JPanel jPanel13 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel6 = new JLabel();
  JPanel jPanel10 = new JPanel();
  JLabel statustxt = new JLabel();
  BorderLayout borderLayout7 = new BorderLayout();
  Border border1;
  GraphPaper graphPaper1 = new GraphPaper(){
    public void paint(Graphics g){
      super.paint(g);
    }
  };
  public SimulationPlotPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jPanel3.setLayout(borderLayout3);
    jPanel4.setLayout(borderLayout4);
    scripttxt.setToolTipText("The script to run at the start of each simulation period. Use \'x\' " +
    "to get the value of x.");
    scripttxt.setText("air.O2=0.2+(x/15)");
    scripttxt.setColumns(12);
    scripttxt.setLineWrap(true);
    scripttxt.setRows(4);
    jLabel2.setText("Script:");
    jPanel7.setLayout(borderLayout5);
    jPanel8.setLayout(borderLayout6);
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setHorizontalTextPosition(SwingConstants.TRAILING);
    jLabel3.setText("< x <");
    xmintxt.setText("0");
    xmintxt.setColumns(8);
    xmaxtxt.setText("10");
    xmaxtxt.setColumns(8);
    jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    jLabel1.setText("Time per run (s)");
    timetxt.setToolTipText("How long to run the simulation before retrieving the value of y");
    timetxt.setText("60");
    timetxt.setColumns(8);
    jLabel4.setText("y=");
    ytxt.setToolTipText("The expression to evaluate to get the value of y at each point x");
    ytxt.setText("APO2");
    ytxt.setColumns(8);
    ytxt.setLineWrap(true);
    ytxt.setRows(3);
    jLabel5.setText("Samples per x");
    samplestxt.setToolTipText("How many times to run the simulation at each value of x");
    jPanel13.setLayout(gridBagLayout1);
    jLabel6.setText("Number of x\'s");
    divisionstxt.setToolTipText("Number of different values of x to try");
    statustxt.setText("Ready");
    jPanel10.setLayout(borderLayout7);
    jPanel3.setBorder(border1);
    graphPaper1.setLayout(null);
    jPanel5.add(jPanel13, null);
    jPanel13.add(jLabel2,          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jScrollPane1,                     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jLabel1,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(timetxt,        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jLabel4,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jLabel5,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(samplestxt,    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jLabel6, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(divisionstxt,  new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jPanel13.add(jScrollPane2,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane2.getViewport().add(ytxt, null);
    jScrollPane1.getViewport().add(scripttxt, null);
    jPanel4.add(jPanel6, BorderLayout.NORTH);
    jSplitPane1.add(jPanel7, JSplitPane.BOTTOM);
    jPanel4.add(jPanel5, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel7.add(graphPaper1, BorderLayout.CENTER);
    jPanel7.add(jPanel8, BorderLayout.SOUTH);
    jPanel8.add(jLabel3, BorderLayout.CENTER);
    jPanel8.add(xmintxt, BorderLayout.WEST);
    jPanel8.add(xmaxtxt, BorderLayout.EAST);
    jPanel1.add(jSplitPane1,  BorderLayout.CENTER);
    jSplitPane1.add(jPanel4, JSplitPane.TOP);
    jPanel3.add(jPanel10,  BorderLayout.CENTER);
    jPanel10.add(statustxt, BorderLayout.CENTER);
    samplestxt.setValue(new Integer(3));
    divisionstxt.setValue(new Integer(5));
  }

  public Action runAction = new AbstractAction("Start"){
    public void actionPerformed(ActionEvent e){
      if(!started){
        runSimulation(); putValue(NAME, "Stop");
      }
      else if(currentSim!=null) currentSim.halt();
      putValue(NAME, "Start");
    }
  };

  double minx, maxx, miny, maxy, dx, simtime;
  int nx, nsamp;
  Script script;
  Expression yexpr;
  boolean started= false;
  VisibleVariable yVariable=null;
  /** Parse the values from the dialog, then run simulator in a new thread */
  void runSimulation(){
    statustxt.setText("Initialising...");
    try{
      minx = Double.parseDouble(xmintxt.getText());
      maxx = Double.parseDouble(xmaxtxt.getText());
    }catch(NumberFormatException x){ error("Please enter valid minimum and maximum x values.");}
    try{
      Object v=divisionstxt.getValue();
      if(v instanceof Integer) nx=(int)((Integer)v).intValue();
      else nx=Integer.parseInt(v.toString());
      dx = (maxx-minx)/nx;
    }catch(Exception x){ error("Please enter a valid number of x-values to try");}
    try{
      Object v= samplestxt.getValue();
      if(v instanceof Integer) nsamp = (int) ((Integer)v).intValue();
      else nsamp = Integer.parseInt(v.toString());
    }catch(Exception x){ error("Please enter a valid number of x-values to try");}
    try {
      simtime = new Expression(timetxt.getText()).value();
    }catch (Exception x) { error("Error in time expression: "+x.toString()); x.printStackTrace();   }
    try{
      script=new Script("PlotScript"+serial++,"Plot script", scripttxt.getText(), false );
    }catch (Exception x) { error("Error in script expression: "+x.toString()); x.printStackTrace(); }
    try{
      yexpr = new Expression(ytxt.getText());
      yVariable = null;  // attempt to see if a single variable was entered
      if(yexpr.getCodeLength()==1 && yexpr.getCode()[0] instanceof evaluator.Variable){
        evaluator.Variable v=(evaluator.Variable)yexpr.getCode()[0];
        if(v.getMember() instanceof Field){
          Field f=(Field)v.getMember();
          if(f.getType()==VDouble.class){
            VDouble vd=(VDouble)f.get(v.getObject());
            try{
              yVariable = Variables.forVDouble(vd);
            }catch(Exception x){}
          }
        };
      }
    }catch(Exception x){ error("Error in y expression: "+x.toString()); x.printStackTrace();    }


    graphPaper1.removeAll();
    px=new double[nx][nsamp];
    py=new double[nx][nsamp];
    for(int i=0;i<nx;i++)for(int j=0;j<nsamp;j++){ px[i][j]=Double.NaN; py[i][j]=Double.NaN; }
    graphPaper1.setXRange(minx,maxx);

    new Thread(currentSim=new Simulator()).start();
  }
  void updateGraph(){
    if(npts>=2){
      graphPaper1.removeAll();
      double miny=Double.MAX_VALUE, maxy=Double.MIN_VALUE, meany, sy=0;
      for(int i=0;i<py.length;i++)for(int j=0;j<py[i].length;j++){
        if(!Double.isNaN( py[i][j] )){
          miny = Math.min(py[i][j], miny);
          maxy = Math.max(py[i][j], maxy);
          sy += py[i][j];
        }
      }meany=sy/npts;
      graphPaper1.setYRange(miny,maxy);
      for(int i=0;i<py.length;i++)for(int j=0;j<py[i].length;j++){
        if(!Double.isNaN(px[i][j]) && !Double.isNaN(py[i][j]))
          graphAddPoint(px[i][j],py[i][j]);
      }
      graphPaper1.repaint();
    }
  }
  void graphAddPoint(double x, double y){
    JPanel c = new JPanel();
    graphPaper1.add(c);
    c.setBounds( graphPaper1.xS(x)-ps, graphPaper1.yS(y)-ps, ps*2, ps*2  );
    c.setOpaque(true);
    c.setBackground(Color.red);
    c.setBorder(new BevelBorder(BevelBorder.RAISED));
    String xstr=Quantity.toString( x), ystr=Quantity.toString(y);
    if(yVariable!=null){
      ystr=yVariable.formatValue(y, true,false);
    }
    c.setToolTipText("x="+xstr+", y="+ystr);
  }

  int ps=4;
  double[][] py;
  double[][] px;
  int npts = 0;

  /** This class runs the simulation with different values of x, and stores the y-value */
  class Simulator implements Runnable{
    Body body=Current.body;
    Clock clock= body.getClock();
    CommonThread thread = Current.thread;
    boolean deathDisabled;
    public void run() {
      if(started) error("Simulation already running");  // should never happen!
      started=true;
      isHalted=false;
      deathDisabled = body.brain.conscious.disableDeath;
      body.brain.conscious.disableDeath = true;
      try{
        clock.stop();
        npts=0;
        for(int xi=0;xi<nx;xi++){
          double xval=minx+xi*dx;                                   // for each value of x, and
          for (int si = 0; si < nsamp; si++) {                      // for each sample point,
            body.resetBodyValues();                                  // reset the body variables
            new Statement("x="+String.valueOf(xval)).evaluate();    // set the value of x
            script.executeOnce();                                   // evaluate the script
            statustxt.setText("Simulating point "+npts+": x="+Quantity.toString(xval));
            synchronized (this) {
              body.getClock().requestNotifyAfter(simtime, this);    // until simtime elapses
              clock.start();                                          // and run the simulation
              wait();                                               // (in body time)
            }
            clock.stop();
            while(thread.isInCycle()) Thread.sleep(100);            // wait until clock cycle has actually stopped
            px[xi][si]=xval;
            py[xi][si]=yexpr.value();                               // then evaluate yexpr and store value
            npts++;
System.out.println(px[xi][si]+"\t= "+py[xi][si]);
            updateGraph();                                          // and request repaint/
            if(isHalted)break;
          }
          if(isHalted)break;
        }
      }catch(Exception x){
        error("Error during simulation: "+x.toString()); x.printStackTrace();
      }
      clock.stop();
      started=false;
      body.brain.conscious.disableDeath = deathDisabled;
      statustxt.setText("Done");
    }
    private boolean isHalted;
    public synchronized void halt(){
      isHalted=true;
      notify();
    }
  }
  Simulator currentSim;
  void error(String x){ JOptionPane.showMessageDialog(this, x, "Error in simulation", JOptionPane.ERROR_MESSAGE);}
  int serial =0 ;
  public void halt(){
    if(currentSim!=null && started) currentSim.halt();
  }
}

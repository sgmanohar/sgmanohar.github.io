package phic.gui;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;

import phic.*;
import phic.common.*;
import phic.drug.*;

/**
 * New class to supercede ScrollGraphPane -
 * Does the job for multiple panes. A single panel contains the graphs which may
 * cross over one another but are one their own x-scale. This gives a much larger
 * range over which they are observable.
 * They all share the same y-scale (time).
 */

public class CombinedScrollGraph
    extends JPanel
    implements Runnable {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel controlpane = new JPanel();
  YScalePane tscalepane = new YScalePane();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel xscalepane = new JPanel();
  public MainPane mainpane = new MainPane();
  JPanel varnamepane = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  public JPopupMenu popupmenu = new JPopupMenu();
  public CombinedScrollGraph() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    thread.start();
    //create popup menu items
    for (int i = 0; i < speeds.length; i++) {
      JMenuItem m = new JMenuItem(new PopupAction(speeds[i]));
      popupmenu.add(m);
    }
  }

  /**
   * The thread that keeps it scrolling
   */
  Thread thread = new Thread(this, "ScrollGraph");
  /** Whether scrolling is taking place */
  boolean running = true;

  /** milliseconds to wait each scroll update */
  int timer = 50;

  /** pixels to scroll per tick */
  int scrollRate = 4;

  /**
   * if this is enabled, allows scrolling through past graphs,
   * However this can be memory-expensive for long runs.
   */
  protected boolean enableHistory = true;

  /**
   * When this is true, the scroll graph history is not erased when reset is
   * pressed, thus storing everything that is drawn during this session.
   * This is clearly very memory expensive!
   */
  public boolean enableWholeSessionHistory = false;

  /**
   * The notify-object that waits for new graphs to be added if there
   * are none presently being displayed.
   */
  Object waiter = new Object();
  public phic.common.Clock clock;

  /**
   * Run method for thread that keeps it scrolling
   * @todo this is not working: sometimes gets stuck when changing from zero vars.
   */
  public void run() {
    try {
      while (running) {
        clock = Current.body.getClock(); //needs to get this in case loaded new Person.
        if (vars.size() == 0) { // scrollpane is empty
          synchronized (this) {
            if (vars.size() == 0) {
              wait(); //notified when new var is added
            }
          }
        }
        if (clock.running) {
          mainpane.tick();
          updateValues();
        }
        else {
          //?wait for longer if paused
        }
        synchronized (waiter) {
          waiter.wait(timer);
        }
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Terminating");
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    mainpane.setBackground(Color.black);
    mainpane.setForeground(Color.white);
    controlpane.setLayout(borderLayout3);
    xscalepane.setLayout(flowLayout1);
    flowLayout1.setHgap(0);
    flowLayout1.setVgap(0);
    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        rescaleAll();
      }
    });
    varnamepane.setLayout(flowLayout2);
    flowLayout2.setHgap(0);
    flowLayout2.setVgap(0);
    jMenuItem1.setBackground(SystemColor.activeCaption);
    jMenuItem1.setFont(new java.awt.Font("Dialog", 1, 12));
    jMenuItem1.setForeground(SystemColor.activeCaptionText);
    jMenuItem1.setText("Timebase");
    this.add(controlpane, BorderLayout.NORTH);
    controlpane.add(varnamepane, BorderLayout.CENTER);
    tscalepane.setPreferredSize(new Dimension(8, 10));
    this.add(tscalepane, BorderLayout.WEST);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(xscalepane, BorderLayout.NORTH);
    jPanel1.add(mainpane, BorderLayout.CENTER);
    popupmenu.add(jMenuItem1);
    popupmenu.addSeparator();
    mainpane.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popupmenu.show(mainpane, e.getX(), e.getY());
        }
      }
    });
  }

  /**
   * The list of DisplayVariables that will be drawn
   */
  Vector vars = new Vector();
  /** Adds the variable to the display */
  public void addNewVariable(VisibleVariable v) {
    DisplayVariable vv = new DisplayVariable(v);
    addingNewVariable(vv);
  }

  /**
   * Adds the variable to the display, given the VDouble. Useful for
   * non-nodal values such as Drug concentrations.
   */
  public void addNewVariable(VDouble v, String name) {
    DisplayVariable vv = new DisplayVariable(v, name);
    addingNewVariable(vv);
  }

  /**
   * Adds the variable to the display, given the node. Useful for
   * non-visible variables
   */
  public void addNewVariable(Node n) {
    DisplayVariable vv = new DisplayVariable(n);
    addingNewVariable(vv);
  }

  /** After creating the displayVariable, the screen + values are set up. */
  private synchronized void addingNewVariable(DisplayVariable vv) {
    JPanel controls;
//		vv.variable=v; vv.node=v.node;
    vv.xscale = new XScalePane();
    vv.xscale.variable = vv;
    vars.add(vv);
    int n = vars.size(), width = columnWidth();
    createControls(vv);
    xscalepane.add(vv.xscale);
    vv.controls.setPreferredSize(new Dimension(width,
                                               vv.controls.getPreferredSize().
                                               height));
    vv.xscale.setPreferredSize(new Dimension(width, XScalePane.fixedHeight)); //initial value
    rescaleAll();
    notifyAll(); // in case we are idling
  }

  /**
   * New class to link between old system of VisibleVariables and new
   * system of VDoubles
   */
  int columnWidth() {
    if (vars.size() == 0) {
      return 0;
    }
    return (getWidth() - 20) / vars.size(); //width of a column
  }

  /** Removes the variable from the display */
  public void remove(VisibleVariable v) {
    removeVariable(v);
  }

  /** Removes the VDouble from the display */
  public void remove(VDouble v) {
    removeVariable(v);
  }

  /** Method to which remove(VDouble) and remove(VisibleVariable) are delegated */
  private synchronized void removeVariable(Object o) {
    Vector removal = new Vector();
    for (int i = 0; i < vars.size(); i++) {
      DisplayVariable vv = (DisplayVariable) vars.get(i);
      if (vv.vv == o || vv.vd == o) {
        xscalepane.remove(vv.xscale);
        removal.add(vv);
      }
    }
    vars.removeAll(removal);
    validate();
  }

  void remove(DisplayVariable v) {
    xscalepane.remove(v.xscale);
    varnamepane.remove(v.controls);
    vars.remove(v);
    rescaleAll();
  }

  public void removeAllGraphs() {
    while (vars.size() > 0) {
      remove( (DisplayVariable) vars.get(0));
    }
  }

  /** Rescales all the variable name panes and scale panes */
  void rescaleAll() {
    int n = vars.size(), width = columnWidth();
    for (int i = 0; i < n; i++) {
      DisplayVariable a = (DisplayVariable) vars.get(i);
      a.xscale.setPreferredSize(new Dimension(width - 12 / n,
                                              XScalePane.fixedHeight));
      a.controls.setPreferredSize(new Dimension(width,
                                                a.controls.getPreferredSize().
                                                height));
//(30/9/02)			a.rescaleRange(); // rescale the variable ranges
      a.rescaleRange();
    }
    validateTree();
    xscalepane.doLayout();
    repaint();
  }

  /** Creates a name, remove button, and value display for the variable */
  void createControls(DisplayVariable v) {
    JPanel panel = new JPanel();
    v.controls = panel;
    panel.setLayout(new BorderLayout());
    //'titlebar'
    JPanel titlebar = new JPanel();
    titlebar.setLayout(new BorderLayout());
    JLabel title = new JLabel(v.getLongName());
    if(OldNodeView.TitleBar.allowClose){
      JButton closebutton = new JButton();
      closebutton.setToolTipText("Close this graph");
      closebutton.setIcon(new ImageIcon(Resource.loader.getImageResource(
          "SmallCross.gif")));
      closebutton.setMargin(new Insets(0, 0, 0, 0));
      closebutton.addActionListener(new CloseListener(v));
      titlebar.add(closebutton, BorderLayout.EAST);
    }
    title.setOpaque(true);
    title.setBackground(SystemColor.activeCaption);
    title.setForeground(SystemColor.activeCaptionText);
    if (v.vv != null) {
      title.addMouseListener(new TitleMouseListener(v));
    }
    if (v.vv != null) {
      title.setToolTipText(v.vv.longName + " (" + v.vv.canonicalName +
                           ")");
    }
    else {
      title.setToolTipText(v.getLongName());
    }
    titlebar.add(title, BorderLayout.CENTER);
    panel.add(titlebar, BorderLayout.NORTH);
    //text
//JTextField field=new JTextField("0.0", 10);
    JLabel field = new JLabel("0.0");
    field.setOpaque(true);
    field.setBackground(Color.black);
    field.setForeground(v.colour);
    field.setFont(new Font("Arial", Font.BOLD, 13));
    panel.add(field, BorderLayout.CENTER);
    v.field = field;
    panel.setPreferredSize(new Dimension(columnWidth(),
                                         panel.getPreferredSize().height));
    panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    varnamepane.add(panel);
    validate();
  }

  /** Displays the numbers above the graphs */
  void updateValues() {
    for (int i = 0; i < vars.size(); i++) {
      DisplayVariable v = (DisplayVariable) vars.get(i);
      if (v.field != null) {
        double val = v.getCurrentValue();
        String s = v.formatValue(val, true, true);
        v.field.setText(s);
        // Flash if out of range
        if (v.vv != null) {
          if (flashIfOutOfRange) {
            if (val < v.vv.minimum || val > v.vv.maximum) {
              if (!v.isOutOfRange) {
                v.isOutOfRange = true;
                //flash when variable goes out of range
                v.field.setBackground(Color.white);
                new StopFlashTimer(300, v).start();
              }
            }
            else {
              if (v.isOutOfRange) {
                v.isOutOfRange = false;
              }
            }
          }
        } //Graphics g=v.field.getGraphics();g.setColor(v.field.getForeground()); g.drawString(s,0,13);
      }
    }
  }
  /**
   * This is called when the body is reset
   * it is used to clear the scrollgraph history if needed.
   */
  public void reset(){
    if(!enableWholeSessionHistory)mainpane.previousImages.removeAllElements();
  }

  /** This turns of the flash */
  class StopFlashTimer
      extends Timer {
    StopFlashTimer(int time, final DisplayVariable variable) {
      super(time, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          variable.field.setBackground(Color.black);
        }
      });
      setRepeats(false);
    }
  }

  class TitleMouseListener
      extends MouseAdapter {
    DisplayVariable variable;
    public TitleMouseListener(DisplayVariable v) {
      variable = v;
    }

    public void mouseClicked(MouseEvent e) {
      //if(e.getClickCount()==2){
      variable.vv.displayVariableDialog();
      //}
    }
  }

  class CloseListener
      implements ActionListener {
    DisplayVariable v;
    CloseListener(DisplayVariable v) {
      this.v = v;
    }

    public void actionPerformed(ActionEvent e) {
      remove(v);
    }
  }

  public void mark(Object event) {
    mainpane.drawArrow = true;
    mainpane.drawObject = event;
  }

  /**
   * Class responsible for drawing the graphs
   */
  class MainPane
      extends JPanel {
    BufferedImage i1, i2, lower, upper;
    Vector previousImages = new Vector();
    int switchPoint = 0;
    int scrollPoint = 0;
    MainPane() {
      addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          createImages();
        }
      });
      addMouseListener(drawAdapter);
      addMouseMotionListener(drawAdapter);
      setDoubleBuffered(false);
    }

    MouseInputAdapter drawAdapter = new MouseInputAdapter() {
      Point o;
      int oSw;
      boolean oflip = i1 == upper;
      boolean dragging=false;
      public void mouseDragged(MouseEvent e) {
        dragging=true;
       if(enableHistory && (((e.getModifiersEx() & e.BUTTON2_DOWN_MASK) > 0) ||
                            ((e.getModifiersEx() & e.CTRL_DOWN_MASK) > 0)           ) ){
         scrollDrag(e);return;
       }
       if(scrollPoint>0)return; //don't allow drawing if scrolled
       boolean flip = i1 == upper;
       Point p = e.getPoint();
       if (o != null && flip == oflip) {
         Graphics g = upper.getGraphics();
          ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor(  Color.white);
          g.drawLine(o.x, o.y - oSw + getHeight(), p.x, p.y - switchPoint + getHeight());
          g = lower.getGraphics();
          ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor( Color.white);
          g.drawLine(o.x, o.y - oSw, p.x, p.y - switchPoint);
          repaint();
        }
        o = p;
        oSw = switchPoint;
        oflip = flip;
      }
      /** Allow reviewing old graphs */
      void scrollDrag(MouseEvent e){
        Point p=e.getPoint();
        int dx=p.x-o.x, dy=p.y-o.y;
        scrollPoint = Math.min(Math.max(scrollPoint-dy,0), previousImages.size() * getHeight());
        o=p;
        repaint();
      }
      public void mouseReleased(MouseEvent e){
        if(dragging){ dragging=false; e.consume(); }
      }
      public void mousePressed(MouseEvent e) {
        o = e.getPoint();
        oSw = switchPoint;
      }
    };
    void createImages() {
      int w = getWidth(), h = getHeight();
      if (w > 0 && h > 0) {
        i1 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        i2 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        if (upper != null && upper.getHeight() == h) {
          i1.getGraphics().drawImage(upper, 0, 0, null);
        }
        lower = i2;
        upper = i1;
      }
    }

    /** Dual canvas system for upper and lower portions */
    public void paint(Graphics g) {
      g.drawImage(upper, 0, -scrollPoint + switchPoint - upper.getHeight(), this);
      g.drawImage(lower, 0, -scrollPoint + switchPoint, this);
      int n=previousImages.size();
      if(enableHistory && scrollPoint > 0) for(int i=0;i<n;i++){
        BufferedImage prev = (BufferedImage)previousImages.get(i);
        g.drawImage(prev, 0, -scrollPoint + switchPoint + (n-i)*upper.getHeight(), this);
      }
    }

    /** For grid painting: what to draw */
    boolean isMajorTick, isMinorTick, isMinuteTick;
    long prevTimeMins, prevTimeHours, prevTimeDays;
    boolean drawArrow;
    Object drawObject;
    /**
     * Should the main graphs be anti-aliased? May make painting slower but
     * graphs look smoother when this is on.
     */
    public boolean antiAlias = true;

    /**
     * Thickness of graph lines drawn
     */
    int thickness = 2;

    /**
     * Advance the image, then for each variable, draw the next grid segment
     * and plot the variable's value
     */
    void tick() {
      if (lower == null) {
        return;
      }
      scrollPoint=0;
      advanceImages();
      //work out how often to draw ticks
      double pixrate = scrollRate / (double) timer * clock.getSecond();
      minutelyDisplay = (pixrate > 0.5) ? 1 : (pixrate > 0.2) ? 5 :
          (pixrate > 0.04) ? 30 : 0;
      hourlyDisplay = (pixrate > 0.01) ? 1 : (pixrate > 0.002) ? 6 : 24;
      //work out whether major or minor tick
      long timeMins = clock.getTime() / (1000 * 60),
          timeHours = timeMins / 60,
          timeDays = timeHours / 24;
      isMajorTick = prevTimeDays != timeDays;
      isMinorTick = prevTimeHours != timeHours;
      if (minutelyDisplay > 0) {
        isMinuteTick = (prevTimeMins / minutelyDisplay !=
                        timeMins / minutelyDisplay);
      }
      prevTimeDays = timeDays;
      prevTimeHours = timeHours;
      prevTimeMins = timeMins;
      //events
      if (drawArrow) {
        drawArrow = false;
        Graphics g = upper.getGraphics();
        g.setColor(Color.white);
        int h = getHeight() - switchPoint;
        g.drawLine(getWidth() - 4, h, getWidth(), h);
        String s = drawObject.toString();
        Rectangle2D rr = g.getFont().getStringBounds(s,
            ( (Graphics2D) g).getFontRenderContext());
        g.drawString(s, getWidth() - (int) rr.getWidth() - 5,
                     h + (int) rr.getHeight() / 2);
      }
//if(isMinorTick)PhicApplication.frame.message("ticked "+Organ.completedCycles);
      //for each variable
      for (int i = 0; i < vars.size(); i++) {
        DisplayVariable v = (DisplayVariable) vars.get(i);
        doGrid(v);
        Graphics g = upper.getGraphics();
        if (antiAlias) {
          ( (Graphics2D) g).setRenderingHint(RenderingHints.
                                             KEY_ANTIALIASING,
                                             RenderingHints.VALUE_ANTIALIAS_ON);
        }
        if(thickness>0){
          ((Graphics2D)g).setStroke(new BasicStroke(thickness));
        }
        g.setColor(v.colour);
        int lastpos = v.lastPosition, pos = v.getCurrentPosition();
        int h = getHeight();
        pos = Math.max(0, Math.min(pos, getWidth() - 1));
        g.drawLine(lastpos, h - switchPoint + scrollRate, pos,
                   h - switchPoint);
        if (switchPoint > getHeight() - scrollRate) { //across two panes?
          Graphics g2 = lower.getGraphics();
          g2.setColor(v.colour);
          g2.drawLine(v.lastPosition, -switchPoint + scrollRate,
                      v.getCurrentPosition(), -switchPoint);
        }
      }
      repaint();
    }

    /**
     * The number of hours between printing the time of day
     */
    int hourlyDisplay = 6;

    /** The number of minutes between printing the time in minutes */
    int minutelyDisplay = 0;

    /**	Draw the lines of the grid */
    final void doGrid(DisplayVariable v) {
      Graphics g = upper.getGraphics();
      g.setColor(Color.gray);
      int gw = /*v.xscale.majorPerMinor**/ v.xscale.minorSpacing;
      int gh = /*tscalepane.majorPerMinor**/ tscalepane.minorSpacing;
      int h = getHeight() - switchPoint;
      if (isMajorTick) { //solid line on days => major tick
        g.drawLine(v.xscale.getX(), h,
                   v.xscale.getX() + v.xscale.getWidth(), h);
        g.drawString(clock.getTimeString(clock.DATE), 0, h - 2);
      } else if (isMinorTick && gw > 0) { //draw dots on the hour => minor tick
        for (int x = 0; x < v.xscale.getWidth(); x += gw) {
          g.drawLine(v.xscale.getX() + x, h, v.xscale.getX() + x, h);
        }
        if (prevTimeHours % hourlyDisplay == 0) {
          g.drawString(String.valueOf(prevTimeHours % 24) + "h", 0,
                       h - 2);
        }
      } else if (minutelyDisplay > 0 && isMinuteTick) {
        g.drawString(String.valueOf(minutelyDisplay *
                                    ((prevTimeMins%60) / minutelyDisplay)) + "m", 0,
                     h - 2);
      } else { //just tramlines other times
        int x = v.xscale.getX();
        g.drawLine(x, h, x, h);
        x += v.xscale.getWidth();
        g.drawLine(x, h, x, h);
      }
      /*if(switchPoint%gh<scrollRate){	//major tick
       }else{	//not a tick
       }*/
    }

    /** Advance the position of the scrolling window */
    final void advanceImages() {
      switchPoint += scrollRate;
      if (switchPoint > getHeight()) { //wrap
        switchPoint -= getHeight();
        BufferedImage t = lower;
        lower = upper;
        upper = t;
        if(enableHistory) { //save old images
          previousImages.add(upper);
          upper =  new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }else{ // simple re-use old buffers
          Graphics g = upper.getGraphics();
          g.setColor(getBackground());
          g.fillRect(0, 0, upper.getWidth(), upper.getHeight());
        }
      }
    }
  }

  /**
   * Class containing the data for a variable displayed on the pane
   */
  final class DisplayVariable {
    DisplayVariable(VDouble vd, String longName) {
      this.vd = vd;
      _longname = longName;
      colour = colours[serial++ % colours.length];
    }

    DisplayVariable(Node n) {
      this.node = n;
      _longname = n.canonicalName();
      colour = colours[serial++ % colours.length];
    }

    DisplayVariable(VisibleVariable vv) {
      this.vv = vv;
      node = vv.node;
      colour = colours[serial++ % colours.length];
    }

    private Node node;
    VisibleVariable vv;
    VDouble vd;
    private String _longname;
    int lastPosition;
    double origin, scale, maximum, minimum;
    Color colour;
    XScalePane xscale;
    JPanel controls;
//JTextField field; // Allows editing
    JLabel field;
    public boolean isOutOfRange = false;
    int getCurrentPosition() {
      lastPosition = (int) ( (getCurrentValue() - origin) * scale) +
          xscale.getX();
      return lastPosition;
    }

    double getCurrentValue() {
      synchronized (Organ.cycleLock) {
        if (node != null) {
          return node.doubleGetVal();
        }
        else {
          return vd.get();
        }
      }
    }

    private double getCurrentMinimum() {
      return Math.min(getMinimum(), getCurrentValue());
    }

    private double getCurrentMaximum() {
      return Math.max(getMaximum(), getCurrentValue());
    }

    double getInitial() {
      if (vv != null) {
        return vv.initial;
      }
      else if (vd != null) {
        return vd.initialValue;
      }
      return 0;
    }

    double getMaximum() {
      if (vv != null) {
        return vv.maximum;
      }
      else if (vd != null) {
        return vd.maximum;
      }
      return 1;
    }

    double getMinimum() {
      if (vv != null) {
        return vv.minimum;
      }
      else if (vd != null) {
        return vd.minimum;
      }
      return 0;
    }

    String getLongName() {
      if (vv != null) {
        return vv.longName;
      }
      else {
        return _longname;
      }
    }

    String formatValue(double value, boolean showUnit, boolean fixed) {
      if (vv != null) {
        return vv.formatValue(value, showUnit, fixed);
      }
      else if (vd != null) {
        return vd.formatValue(value, showUnit, fixed);
      }
      return Quantity.toString(value);
    }

    //@todo rewrite using phic.modifiable.Range
    void rescaleRange() {
      double current = getCurrentValue();
      //decade is 10^x such that 10^x > value > 10^(x-1)
      double decade = Math.pow(10,
                               1 +
                               (int) Math.floor(Math.log(current) /
                                                Math.log(10)));
      if (decade == 0) {
        decade = 1;
      }
      if (zoomed && current > 0) {
        minimum = 0;
        //added 23/6/3: zoomed always includes maximum value of variable
        current = Math.max(current, getMaximum());
        decade = Math.pow(10,
                          1 +
                          (int) Math.floor(Math.log(current) /
                                           Math.log(10)));
        int i = findFirstIndexAbove(current / decade, triads);
        maximum = decade * triads[i];
      }
      else {
        int i = findFirstIndexAbove(current / decade, decades);
        if (i == 0) {
          minimum = decades[i] * decade;
          maximum = decades[i + 1] * decade;
        }
        else {
          minimum = decades[i - 1] * decade;
          maximum = decades[i] * decade;
        }
      }
      if (maximum == minimum) {
        if (vv != null) {
          maximum = minimum + vv.initial;
        }
        else {
          maximum = minimum + 1;
        }
        System.out.println("Scaling of " + node + " failed"); //@todo problems!
      }
      origin = minimum;
      rescalePanel();
      xscale.setToolTipText("Scale " + formatValue(minimum,
          true, false) + " - " + formatValue(maximum, true, false));
    }

    public void rescalePanel() { // called if the width changes.
      scale = xscale.getPreferredSize().width / (maximum - origin);
    }

    boolean zoomed = true;
    double[] decades = new double[] {
        0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4,
        0.45, 0.5, 0.6,
        0.7, 0.8, 0.9, 1.0}
        , triads = new double[] {
        0.1, 0.2, 0.5, 1.0};
    int findFirstIndexAbove(double value, double[] list) {
      for (int i = 0; i < list.length; i++) {
        if (list[i] >= value) {
          return i;
        }
      }
      return list.length - 1;
    }

    Color[] colours = new Color[] {
        Color.red, Color.cyan, Color.pink, Color.yellow,
        Color.green,
        Color.magenta, Color.orange};
  }

  static int serial = 0;

  /**
   * Class to draw a vertical ruler
   */
  class YScalePane
      extends JPanel {
    int majorPerMinor = 5, minorSpacing = 30;
    public void paint(Graphics g) {
      super.paint(g);
      int yh = getHeight(), i = 0, xw = getWidth();
      g.setColor(SystemColor.controlShadow);
      g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
      for (int y = yh; y > 0; y -= minorSpacing) {
        drawTick(g, this, y, false, i++ % majorPerMinor == 0, xw);
      }
    }
  }

  /**
   * Class to draw a horizontal ruler
   */
  class XScalePane
      extends JPanel {
    boolean labelInitialValue = false;
    int majorPerMinor = 5, minorSpacing, nMinors = 10; //ten ticks
    DisplayVariable variable;
    public XScalePane() {
      addMouseListener(new MouseAdapter() {
        public void mouseReleased(MouseEvent e) {
          variable.zoomed = variable.zoomed == false;
          variable.rescaleRange();
          repaint();
        }
      });
    }

    /**
     * The height of the X-scale in pixels
     */
    static final int fixedHeight = 20;

    /** The height of a major tick, in pixels */
    final int majorDepth = 10;

    /** The height at which the labels are displayed */
    final int textDepth = 13;

    /** The horizontal gap between the max of one pane, and the min of the next. */
    final int hTextGap = 6;

    public void paint(Graphics g) {
      super.paint(g);
      minorSpacing = getWidth() / nMinors;
      int yh = getHeight(), i = 0, xw = getWidth();
      g.setColor(SystemColor.controlShadow);
      g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
      for (int x = 0; x < xw; x += minorSpacing) {
        drawTick(g, this, x, true, i++ % majorPerMinor == 0, majorDepth);
      }
      ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
      g.setFont(new Font("Arial",
                         Font.PLAIN, 10));
      if (labelInitialValue) {
        labelTick(g, variable.getInitial());
      }
      drawArrow(g, variable.getInitial(), Color.red);
      labelTick(g, variable.minimum);
      labelTick(g, variable.maximum);
    }

    int xcoord(double value) {
      int a = (int) ( (value - variable.origin) * variable.scale);
      if (a < 1E5) {
        return a;
      }
      else {
        return 0;
      }
    }

    final void labelTick(Graphics g, double value) {
      int x = xcoord(value);
      drawTick(g, this, x, true, true, majorDepth);
      //get label text and remove trailing zeros
      String label = variable.formatValue(value, false, false);
      if (label.endsWith(".0")) {
        label = label.substring(0, label.length() - 2);
      }
      Rectangle2D rr = getFont().getStringBounds(label,
                                                 ( (Graphics2D) g).
                                                 getFontRenderContext());
      Dimension r = new Dimension( (int) rr.getWidth(), (int) rr.getHeight());
      if (x + r.width / 2 > getWidth()) {
        x = getWidth() - r.width / 2 - hTextGap / 2;
      }
      if (x - r.width / 2 < 0) {
        x = r.width / 2 + hTextGap / 2;
      }
      g.setColor(Color.black);
      //draw label centred on position x, on the baseline
      g.drawString(label, x - r.width / 2, textDepth);
    }

    final void drawArrow(Graphics g, double value, Color colour) {
      g.setColor(colour);
      int x = xcoord(value), y = 8;
      g.fillPolygon(new int[] {x, x - 4, x + 4}
                    , new int[] {y, y - 8, y - 8}
                    , 3);
    }
  }

  void drawTick(Graphics g, Component c, int pos, boolean Xaxis,
                boolean major,
                int thickness) {
    for (int a = 0; a < 2; a++) {
      if (a == 0) {
        g.setColor(SystemColor.controlShadow);
      }
      else {
        g.setColor(SystemColor.controlLtHighlight);
      }
      if (Xaxis) {
        int yh = thickness, ym = c.getHeight();
        if (major) {
          g.drawLine(pos + a, yh, pos + a, ym);
        }
        else {
          g.drawLine(pos + a, yh + (ym - yh) / 2, pos + a, ym);
        }
      }
      else {
        int xw = thickness;
        if (major) {
          g.drawLine(0, pos + a, xw, pos + a);
        }
        else {
          g.drawLine(xw / 2, pos + a, xw, pos + a);
        }
      }
    }
  }

  double[] speeds = new double[] {
      0.1, 0.25, 1, 4, 10
  };
  int defaultTimerInterval = 50;
  class PopupAction
      extends AbstractAction {
    double speed;
    PopupAction(double speed) {
      super(String.valueOf(speed));
      this.speed = speed;
    }

    public void actionPerformed(ActionEvent e) {
      setYSpeed(speed);
    }
  };
  public void setYSpeed(double speed) {
    if (speed >= 1) {
      scrollRate = (int) speed;
      timer = defaultTimerInterval;
    }
    else {
      scrollRate = 1;
      timer = (int) (defaultTimerInterval / speed);
    }
  }

  JMenuItem jMenuItem1 = new JMenuItem();
  /**
   * This replaces all the variables in this scrollgraph with the corresponding
   * ones in the new person. Called by the frame when a new person is
   * loaded. It uses the variables from Variables.class
   */
  public boolean flashIfOutOfRange = true;
  public void replaceVariables() {
    for (int i = 0; i < vars.size(); i++) {
      DisplayVariable v = ( (DisplayVariable) vars.get(i));
      if (v.vv != null) {
        v.vv = Variables.forName(v.vv.canonicalName);
        v.node = v.vv.node;
      }
      else {
        v.node = Node.findNodeByName(v.node.canonicalName());
      }
    }
  }

  /**
   * Gets rid of any graphs showing drug concentrations.
   * This is called by the SimplePhicFrame when the body is reset.
   */
  public void removeAnyDrugQuantities() {
    for (int i = 0; i < vars.size(); i++) {
      DisplayVariable v = ( (DisplayVariable) vars.get(i));
      if (v == null) {
        continue;
      }
      if (v.vv == null && v.vd instanceof Drug) {
        remove(v);
        i--;
      }
      if (this.isVisible()) {
        validate();
      }
    }
  }


  public boolean getEnableHistory() {
    return enableHistory;
  }
  /**
   * if this is enabled, allows scrolling through past graphs,
   * However this can be memory-expensive for long runs.
   * If disabled, this frees up the memory used to store old graphs.
   */
  public void setEnableHistory(boolean enableHistory) {
    this.enableHistory = enableHistory;
    if(!enableHistory) mainpane.previousImages.removeAllElements();
  }
}

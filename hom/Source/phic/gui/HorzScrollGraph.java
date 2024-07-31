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

public class HorzScrollGraph
    extends JPanel
    implements Runnable, CreateGraphTarget {


  /**
   * The time in milliseconds between updates of the graph (when running
   * at a timebase speed of 1.0 or above).
   */
  int defaultTimerInterval = 50;

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel controlpane = new JPanel();
  TScalePane tscalepane = new TScalePane();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  public MainPane mainpane = new MainPane();
  BorderLayout borderLayout3 = new BorderLayout();
  public JPopupMenu popupmenu = new JPopupMenu();
  JPanel yscalepane = new JPanel();
  GridLayout yscalelayout = new GridLayout();
  Border border1;
  JPanel scalespacer = new JPanel();
  JMenuItem jMenuItem1 = new JMenuItem();
  Border border2;
  ImageIcon closeIcon = new ImageIcon(Resource.loader.getImageResource(
      "SmallCross.gif"));
  public HorzScrollGraph() {
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
  int timer = 60;

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
          //updateValues();
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
    border1 = BorderFactory.createEmptyBorder(15,0,15,0);
    border2 = BorderFactory.createEmptyBorder(15,0,15,0);
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    mainpane.setBackground(Color.black);
    mainpane.setForeground(Color.white);
    controlpane.setLayout(borderLayout3);
    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        rescaleAll();
      }
    });
//    varnamepane.setLayout(varnamelayout);
    jMenuItem1.setBackground(SystemColor.activeCaption);
    jMenuItem1.setFont(new java.awt.Font("Dialog", 1, 12));
    jMenuItem1.setForeground(SystemColor.activeCaptionText);
    jMenuItem1.setText("Timebase");
    yscalepane.setLayout(yscalelayout);
    yscalelayout.setColumns(1);
    yscalelayout.setRows(0);
    yscalepane.setBorder(border1);
    this.add(controlpane,  BorderLayout.EAST);
    controlpane.add(yscalepane,  BorderLayout.WEST);
    controlpane.add(scalespacer, BorderLayout.NORTH);
    jPanel1.add(tscalepane, BorderLayout.NORTH);
    this.add(jPanel1, BorderLayout.CENTER);
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
    tscalepane.setPreferredSize(new Dimension(8, 10));
    scalespacer.setPreferredSize(new Dimension(0,10));
  }

  /**
   * The list of DisplayVariables that will be drawn
   */
  Vector vars = new Vector();

  /** Adds the variable to the display */
  public void addNewVariable(VisibleVariable v) {
    DisplayVariable dv = new DisplayVariable(v);
    addingNewVariable(dv);
  }

  /**
   * Adds the variable to the display, given the VDouble. Useful for
   * non-nodal values such as Drug concentrations.
   */
  public void addNewVariable(VDouble v, String name) {
    DisplayVariable dv = new DisplayVariable(v, name);
    addingNewVariable(dv);
  }

  /**
   * Adds the variable to the display, given the node. Useful for
   * non-visible variables
   */
  public void addNewVariable(Node n) {
    DisplayVariable dv = new DisplayVariable(n);
    addingNewVariable(dv);
  }

  /** After creating the displayVariable, the screen + values are set up. */
  private synchronized void addingNewVariable(DisplayVariable vv) {
    JPanel controls;
    vv.yscale = new YScalePane();
    vv.yscale.setBorder(BorderFactory.createLineBorder(vv.colour,2));
    vv.yscale.variable = vv;
    vv.yscale.updatemenu();
    vars.add(vv);
    int height = rowHeight();
    yscalepane.add(vv.yscale);
    rescaleAll();
    notifyAll(); // in case we are idling
  }

  /** Calculate allocated height for each variable's graph space */
  int rowHeight() {
    if (vars.size() == 0) {
      return 0;
    }
    return (getHeight() - 20) / vars.size(); //width of a column
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
        yscalepane.remove(vv.yscale);
        removal.add(vv);
      }
    }
    vars.removeAll(removal);
    yscalelayout.setRows(vars.size());
    yscalepane.doLayout();
    validateTree();
  }

  void remove(DisplayVariable v) {
    yscalepane.remove(v.yscale);
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
    int n = vars.size(), height = rowHeight();
    for (int i = 0; i < n; i++) {
      DisplayVariable a = (DisplayVariable) vars.get(i);
      a.yscale.setPreferredSize(new Dimension(YScalePane.fixedWidth, 0));
    }
    yscalelayout.setRows(n);
    yscalepane.doLayout();
    validateTree();
    for (int i = 0; i < n; i++) {
      DisplayVariable a = (DisplayVariable) vars.get(i);
      a.rescaleRange();
    }
    repaint();
  }

  public Color getColor(Node node){
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv=(DisplayVariable)vars.get(i);
      if(dv.node == node
         || (node instanceof VDoubleNode && ((VDoubleNode)node).getVDouble() == dv.vd) ){
        return dv.colour;
      }
    }
    throw new IllegalArgumentException("Node "+node.canonicalName()+" does not have a graph");
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


  class CloseAction extends AbstractAction{
    DisplayVariable v;
    CloseAction(DisplayVariable v) {
      super("Close graph of "+v.getLongName(), closeIcon );
      this.v = v;
    }

    public void actionPerformed(ActionEvent e) {
      remove(v);
    }
  }
  /**
   *  A list of objects that are waiting to be marked on the graph, at the
   * earliest opportunity. They will be shifted, in turn, to mainpane.drawObject
   */
  Vector markQueue=new Vector();
  /**
   * A register of how many more ticks we must wait until this line becomes blank.
   * Lines are numbered up from the bottom of the screen. Currently 10 is the
   * maximum number of lines. After this, the text will continue to build up on
   * the top line.
   */
  int[] markLineTimeOuts = new int[10];
  public void mark(Object event) {
    if(mainpane.drawArrow) {
      markQueue.add(event);
    }else{
      mainpane.drawArrow = true;
      mainpane.drawObject = event;
    }
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
    boolean allowDrawOnGraph = true;
    MainPane() {
      addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          createImages();
        }
      });
      addMouseListener(drawAdapter);
      addMouseMotionListener(drawAdapter);
      addMouseWheelListener((MouseWheelListener)drawAdapter);
      setDoubleBuffered(false);
    }

    /** Control the response to mouse wheel - smooth scrolling back in time */
    int smoothScrollRate=0;
    double smoothScrollVel=0;

    /** Timer to control the response to mouse wheel - smooth scrolling back in time */
    Timer smoothScroller = new Timer(50,new ActionListener(){
      public void actionPerformed(ActionEvent e){
        scrollPoint = Math.min(Math.max(scrollPoint-(int)smoothScrollVel,0),previousImages.size()*getWidth());
        smoothScrollVel+=smoothScrollRate;
        smoothScrollVel *= 0.5;
        smoothScrollRate /=2;
        repaint();
        if (smoothScrollVel == 0) smoothScroller.stop();
      }
    });

    MouseInputAdapter drawAdapter = new GraphMouseAdapter();
    class GraphMouseAdapter extends MouseInputAdapter implements MouseWheelListener{
      Point o;
      int oSw;
      BufferedImage oupper = upper;
      boolean dragging=false;
      public void mouseWheelMoved(MouseWheelEvent e){
        if(enableHistory) {
          smoothScrollRate += e.getWheelRotation() * 45;
          smoothScroller.start();
          //scrollPoint = Math.min(Math.max(scrollPoint-e.getWheelRotation()*45,0),previousImages.size()*getWidth());
          repaint();
        }
      }
      public void mouseDragged(MouseEvent e) {
        dragging=true;
       if(enableHistory && (((e.getModifiersEx() & e.BUTTON2_DOWN_MASK) > 0) ||
                            ((e.getModifiersEx() & e.CTRL_DOWN_MASK) > 0)           ) ){
         scrollDrag(e);return;
       }
       if(scrollPoint>0)return; //don't allow drawing if scrolled
       boolean flip = i1 == upper;
       Point p = e.getPoint();
       if (o != null && upper == oupper && allowDrawOnGraph) {
         Graphics g = upper.getGraphics();
          ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor(  Color.white);
          g.drawLine(o.x + oSw - getWidth(), o.y , p.x + switchPoint - getWidth(), p.y );
          g = lower.getGraphics();
          ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor( Color.white);
          g.drawLine(o.x + oSw, o.y, p.x + switchPoint, p.y);
          repaint();
        }
        o = p;
        oSw = switchPoint;
        oupper=upper;
      }
      /** Allow reviewing old graphs */
      void scrollDrag(MouseEvent e){
        Point p=e.getPoint();
        int dx=p.x-o.x, dy=p.y-o.y;
        scrollPoint = Math.min(Math.max(scrollPoint+dx,0), previousImages.size() * getWidth());
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

    /**
     * Create new canvases. Keep old canvases if needed.
     * called during componentResize
     * Original 24-bit: TYPE_3BYTE_BGR
     */
    void createImages() {
      int w = getWidth(), h = getHeight();
      if (w > 0 && h > 0) {
        i1 = createNewImage(w, h);
        i2 = createNewImage(w, h);
        if (upper != null && upper.getWidth() == w) {
          i1.getGraphics().drawImage(upper, 0, 0, null);
        }
        lower = i2;
        upper = i1;
        future = null;
      }
    }

    /** Dual canvas system for upper and lower portions */
    public void paint(Graphics g) {
      super.paint(g);
      if(upper!=null)
    	  g.drawImage(upper, scrollPoint - switchPoint + upper.getWidth(),0, this);
      if(lower!=null)
    	  g.drawImage(lower, scrollPoint - switchPoint, 0, this);
      int n=previousImages.size();
      int cum_t=0;
      if(enableHistory && scrollPoint > 0) for(int i=n-1;i>=0;i--){
        BufferedImage prev = (BufferedImage)previousImages.get(i);
        if(prev==null)continue;
        cum_t += prev.getWidth();
        g.drawImage(prev, scrollPoint - switchPoint - cum_t, 0, this);
      }
      paintOverlayComponents(g); // draw non-scrolling elements
    }

    /** Draws non-scrolling elements on top of the graph */
    public void paintOverlayComponents(Graphics g){
      if(drawXHair){
        g.setColor(Color.white);
        g.drawLine(xHair,0,xHair,getHeight());
      }
      if(overlayGraphVariables){
        for (int i = 0; i < vars.size(); i++) {
          DisplayVariable v = (DisplayVariable) vars.get(i);
          g.setColor(v.colour);
          g.setFont(new Font("Dialog", Font.BOLD, overlayFontSize));
          int y = v.yscale.getY();
          g.drawString(v.getLongName(), 10, y + overlayFontSize + 2);
          double val = v.currentValue; // ensure that this is already set with tick()?
          String s = v.formatValue(val, true, true);
          g.drawString(s, 10, y + overlayFontSize*2 + 4);
        }
      }
    }
    /**
     * If this is set to true, then draw the values and names of the variables
     * on the graph pane, overlaying the graphs.
     */
    public boolean overlayGraphVariables = true;

    /**
     * The size of the font used to draw the overlay text - i.e. the variable
     * name and value
     */
    public int overlayFontSize = 10;

    /** For grid painting: what to draw on each frame: */
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
    public int thickness = 0;

    public boolean DONT_DRAW_IF_NO_CALCULATION = false;

    boolean canFitWholeDateIn = true;
    /** Keep track of whether data is dirty or clean */
    int cycleLockID = Organ.cycleLock.createID();
    /**
     * Advance the image, then for each variable, draw the next grid segment
     * and plot the variable's value
     */
    void tick() {
      if (lower == null) {
        return;
      }
      /** If data is clean then return. Otherwise, read the new data into the vars */
      synchronized(Organ.cycleLock){
        if(DONT_DRAW_IF_NO_CALCULATION && !Organ.cycleLock.isDirty(cycleLockID)) return;
        for(int i=0;i<vars.size();i++) ((DisplayVariable)vars.get(i)).updateValue();
        Organ.cycleLock.clean(cycleLockID);
      }
      scrollPoint=0;
      /** Decrement the counts for each marked line, so that more markers can
       * be displayed */
      for(int i=0;i<markLineTimeOuts.length;i++)if(markLineTimeOuts[i]>0)markLineTimeOuts[i]-=scrollRate;
      advanceImages();
      //work out how often to draw ticks
      //pixrate is the number of pixels per millisecond (roughly)
      double pixrate = scrollRate / (double) timer * clock.getSecond();
      minutelyDisplay = (pixrate > 0.5) ? 1 : (pixrate > 0.2) ? 5 :
          (pixrate > 0.04) ? 30 : 0;
      hourlyDisplay = (pixrate > 0.01) ? 1 : (pixrate > 0.002) ? 6 : 24;
      canFitWholeDateIn = pixrate > 0.002;
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
        Graphics g = upper.getGraphics();
        g.setColor(Color.white);
        int w = switchPoint;
        // find a blank line to put the mark on
        int markHeight = 0; while(markLineTimeOuts[markHeight]>0 && markHeight<markLineTimeOuts.length-1)markHeight++;
        int dist = 8 + markHeight*10; //length of dash
        g.drawLine(w,getHeight() - dist , w, getHeight());
        String s = drawObject.toString();
        int width = drawStringCenteredOn(g, w, getHeight()-dist-8 , s, false);
        if(markQueue.isEmpty())  drawArrow = false;
        else { drawObject = markQueue.get(0); markQueue.remove(0); }
        markLineTimeOuts[markHeight] = width + 4; //allow 4 pixel gap
      }
      //for each variable
      for (int i = 0; i < vars.size(); i++) {
        DisplayVariable v = (DisplayVariable) vars.get(i);
        doGrid(v);
      }
      for (int i = 0; i < vars.size(); i++) {
        DisplayVariable v = (DisplayVariable) vars.get(i);
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
        int w = getWidth();
        pos = Math.max(0, Math.min(pos, getHeight() - 1));
        g.drawLine(switchPoint, lastpos, switchPoint+ scrollRate, pos);
        if (switchPoint + scrollRate > w) { //across two panes?
          Graphics g2 = getFutureCanvas().getGraphics();
          if (antiAlias) {
            ( (Graphics2D) g2).setRenderingHint(RenderingHints.
                                               KEY_ANTIALIASING,
                                               RenderingHints.VALUE_ANTIALIAS_ON);
          }
          if(thickness>0){
            ((Graphics2D)g).setStroke(new BasicStroke(thickness));
          }
          g2.setColor(v.colour);
          g2.drawLine(switchPoint - w, lastpos, switchPoint + scrollRate - w, pos);
        }
      }
      repaint();
    }

    /**
     * Draw the text s to the graphics g. Centres text at point (x,y) if centreX
     * is true, otherwise centres on the y-axis only, and the x coordinate is
     * the left hand edge of the text.
     * Returns the width of the string on the canvas.
     */
    private int drawStringCenteredOn(Graphics g, int x, int y, String s, boolean centreX) {
      Rectangle2D rr = g.getFont().getStringBounds(s,
          ( (Graphics2D) g).getFontRenderContext());
      int txtx= x - (centreX?  (int)rr.getWidth() / 2 : 0);
      int txty= y + (int)rr.getHeight() / 2;
      g.drawString(s, txtx , txty);
      if(txtx+rr.getWidth()>getWidth()){ //spill to second canvas
        Graphics g2=getFutureCanvas().getGraphics();
        g2.setColor(g.getColor());
        g2.drawString(s, txtx-getWidth(),txty);
      }
      if(txtx<0){
        Graphics g2=lower.getGraphics();
        g2.setColor(g.getColor());
        g2.drawString(s, txtx+getWidth(),txty);
      }
      return (int)rr.getWidth();
    }

    /**
     * The number of previous screens to store for scrolling back.
     * Runs out of memory if number reaches 49.
     */
    static final int MAX_SCROLL_HISTORY = 25;

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
      int gh = /*v.xscale.majorPerMinor**/ v.yscale.minorSpacing;
      int gw = /*tscalepane.majorPerMinor**/ tscalepane.minorSpacing;
      int w = switchPoint;
      if (isMajorTick) { //solid line on days => major tick
        g.drawLine(w, v.yscale.getY(), w,
                   v.yscale.getY() + v.yscale.getHeight());
        String datetxt;
        if(canFitWholeDateIn){
          datetxt=clock.getTimeString(clock.DATE);
        }else{
          datetxt=clock.getTimeString(clock.DAYONLY);
        }
        drawStringCenteredOn(g, w, 19, datetxt,false);
      } else if (isMinorTick && gh > 0) { //draw dots on the hour => minor tick
        for (int y = 0; y < v.yscale.getHeight(); y += gh) {
          g.drawLine(w, v.yscale.getY() + y, w, v.yscale.getY() + y);
        }
        if (prevTimeHours % hourlyDisplay == 0) {
          drawStringCenteredOn(g, w, 7, String.valueOf(prevTimeHours % 24) + "h", true);
        }
      } else if (minutelyDisplay > 0 && isMinuteTick) {
        drawStringCenteredOn(g, w, 7, String.valueOf(minutelyDisplay *
                                    ((prevTimeMins%60) / minutelyDisplay)) + "m",
                     true);
      } else { //just tramlines other times
        int y = v.yscale.getY();
        g.drawLine(w,y,w,y);
        y += v.yscale.getHeight();
        g.drawLine(w,y,w,y);
      }
      /*if(switchPoint%gh<scrollRate){	//major tick
       }else{	//not a tick
       }*/
    }

    /** Advance the position of the scrolling window */
    final void advanceImages() {
      switchPoint += scrollRate;
      if (switchPoint > getWidth()) { //wrap
        switchPoint -= getWidth();
        BufferedImage t = lower;
        lower = upper;
        upper = t;
        if(enableHistory) { //save old images
          previousImages.add(upper);
          upper =  getFutureCanvas();
          if(previousImages.size()>MAX_SCROLL_HISTORY) previousImages.remove(0);
        }else{ // simple re-use old buffers
          Graphics g = upper.getGraphics();
          g.setColor(getBackground());
          g.fillRect(0, 0, upper.getWidth(), upper.getHeight());
        }
      }
    }
    /**
     * Stores the next canvas that will be used when current one runs out
     * - useful for drawing things that overlap into the future
     * Use getFutureCanvas to ensure this is a valid image
     */
    private BufferedImage future= null;

    /**
     * Return a canvas representing the immediate future. If none exists,
     * then create it. This call is used in AdvanceImages to create the new
     * canvas.
     */
    protected BufferedImage getFutureCanvas(){
      if(!enableHistory) return lower;
      if(future==upper || future == null){
        future = createNewImage(getWidth(), getHeight());
      }
      return future;
    }
    /**
     * Create a new buffered image of the given size, using the
     * requiured colour model etc.
     */

    BufferedImage createNewImage(int x, int y){
      return new BufferedImage(x,y, BufferedImage.TYPE_BYTE_INDEXED);
    }

    /** Mark a full vertical line at the specified screen-X coordinate */
    void markFullXLine(int x){
      BufferedImage b=null;
      int xp=x+switchPoint-scrollPoint;
      if(xp>getWidth()){
        b=upper;
        xp -= getWidth();
      }else if (xp>=0){
        b=lower;
      }else{
        int i=previousImages.size();
        while(xp < 0){
          xp += getWidth();
          b=(BufferedImage)previousImages.get(--i);
        }
      }
      assert b!=null;
      Graphics g=b.getGraphics();
      g.setColor(Color.white);
      g.drawLine(xp,0,xp,getHeight());
    }

    /**
     * paints the new scale on the scrolling pane, for future reference.
     *
     * @param displayVariable the DisplayVariable whose scale has changed.
     */
    protected void paintRescalingInformation(DisplayVariable dv) {
      if(upper==null)return;
      int y0=dv.yscale.getY(), y1 = y0 + dv.yscale.getHeight(), xp=switchPoint;
      String top=dv.formatValue(dv.maximum, false, false),
          bottom=dv.formatValue(dv.minimum, false, false);
      Graphics g=upper.getGraphics();
      g.setColor(Color.lightGray);
      g.drawLine(xp,y1,xp,y0);
      drawStringCenteredOn(g, xp+2, y0+4, top,false);
      drawStringCenteredOn(g, xp+2, y1-8, bottom,false);
    }
  }

  /**
   * New class to link between old system of VisibleVariables and new
   * system of VDoubles
   *
   * Containing the data for a variable displayed on the pane
   */
  final public class DisplayVariable {
    DisplayVariable(VDouble vd, String longName) {
      this.vd = vd;
      _longname = longName;
      init();
    }

    DisplayVariable(Node n) {
      this.node = n;
      _longname = n.canonicalName();
      init();
    }

    DisplayVariable(VisibleVariable vv) {
      this.vv = vv;
      node = vv.node;
      init();
    }

    void init(){
      colour = colours[serial++ % colours.length];
      if (vv != null) {
        friendlyName= Resource.identifierToText( vv.longName );
        zoomed = (vv.maximum/vv.minimum) > 31;
      }
      else {
        friendlyName= Resource.identifierToText( _longname );
      }
    }

    protected String friendlyName="";
    protected Node node;
    VisibleVariable vv;
    VDouble vd;
    protected String _longname;
    int lastPosition;
    double origin, scale, maximum, minimum;
    Color colour;
    YScalePane yscale;
    JPanel controls;
//JTextField field; // Allows editing
    JLabel field;
    public boolean isOutOfRange = false;
    /** temp variable storing the current value, when last checked */
    double currentValue;
    /** Return position of graph line relative to whole graph area */
    int getCurrentPosition() {
      lastPosition = - (int) ( (currentValue - origin) * scale) +
          yscale.getY() + yscale.getHeight();
      return lastPosition;
    }
    /** This method requires locking of the values */
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
    /** This method requires locking of the values */
    public void updateValue(){ this.currentValue = getCurrentValue();}

    protected double getCurrentMinimum() {
      return Math.min(getMinimum(), currentValue);
    }

    protected double getCurrentMaximum() {
      return Math.max(getMaximum(), currentValue);
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
      return friendlyName;
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
      updateValue();
      double current = currentValue;
      if (zoomed) {
        minimum = Math.min(currentValue,Math.min(0,getMinimum()));
        //added 23/6/3: zoomed always includes maximum value of variable
        maximum = Math.max(current, getMaximum());
        double maxdecade = Math.pow(10,
                          1 +
                          (int) Math.floor(Math.log(maximum) /
                                           Math.log(10)));
        int i = findFirstIndexAbove(maximum / maxdecade, triads);
        maximum = maxdecade * triads[i];
        if(minimum<0){ // allow negative minimum value
          double mindecade = Math.pow(10,1 +(int) Math.floor(Math.log(Math.abs(minimum)) / Math.log(10)));
          i = findFirstIndexAbove(minimum / mindecade, triads);
          minimum = - mindecade * triads[i];
        }
      }
      else {
        boolean negative = current<0;
        current = Math.abs(current);
        //decade is 10^x such that 10^x > value > 10^(x-1)
        double decade = Math.pow(10, 1 + (int) Math.floor(Math.log(current) / Math.log(10)));
        if (decade == 0) {
          decade = 1;
        }

        int i = findFirstIndexAbove(current / decade, decades);
        if (i == 0) {
          minimum = decades[i] * decade;
          maximum = decades[i + 1] * decade;
        } else {
          minimum = decades[i - 1] * decade;
          maximum = decades[i] * decade;
        }
        if(negative){ double tmp=minimum; minimum=-maximum; maximum=-tmp; }
      }
      if (maximum == minimum) { //fudge for variables that are zero and unzoomed
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
    }

    public void rescalePanel() { // called if the width changes.
      yscale.setToolTipText("Scale " + formatValue(minimum,
          true, false) + " - " + formatValue(maximum, true, false));
      scale = yscale.getHeight() / (maximum - origin);
      /** Paint rescaling information here */
      mainpane.paintRescalingInformation(this);
      yscale.repaint();
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
        Color.red, Color.yellow, Color.pink,
        Color.green, Color.magenta, Color.orange,
        new Color(128,255,128), new Color(0,128,255),
        new Color(128,255,255)
    };
    ThinNodeView thinNodeView;
  }

  static int serial = 0;
  int xHair = 0;
  boolean drawXHair = false;

  /**
   * Class to draw a time ruler
   */
  class TScalePane
      extends JPanel {
    TScalePane(){
      addMouseMotionListener(new MouseMotionAdapter(){
        public void mouseMoved(MouseEvent e){
          xHair = e.getX();
          drawXHair = true;
          mainpane.repaint();
        }
      });
      addMouseListener(new MouseAdapter(){
        public void mouseExited(MouseEvent e){
          drawXHair=false;
          mainpane.repaint();
        }
        public void mouseClicked(MouseEvent e){
          mainpane.markFullXLine(xHair);
        }
      });
    }
    int majorPerMinor = 5, minorSpacing = 30;
    public void paint(Graphics g) {
      super.paint(g);
      int xw = getWidth(), i = 0, yh = 2;
      g.setColor(SystemColor.controlShadow);
      g.drawLine(getHeight() - 1, 0, getHeight() - 1, getWidth());
      for (int x = xw; x > 0; x -= minorSpacing) {
        drawTick(g, this, x, true, i++ % majorPerMinor == 0, yh);
      }
    }
  }

  /**
   * Class to draw a y-axis ruler
   */
  class YScalePane
      extends JPanel {
    boolean labelInitialValue = false;
    int majorPerMinor = 5, minorSpacing, nMinors = 10; //ten ticks
    DisplayVariable variable;
    public YScalePane() {
      addMouseListener(new ScaleMouseAdapter());
      addComponentListener(new ComponentAdapter(){
        public void componentResized(ComponentEvent e){
          variable.rescalePanel();
        }
      });
      jbInit();
    }
    class ScaleMouseAdapter extends MouseAdapter implements MouseMotionListener{
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()==2){
          variable.zoomed = variable.zoomed == false;
          variable.rescaleRange();
          updatemenu();
          repaint();
        }else if(e.getButton()==e.BUTTON3){
          if( closeItem==null  && OldNodeView.TitleBar.allowClose){
            closeItem = new JMenuItem(new CloseAction(variable));
            jPopupMenu1.add(closeItem);
          }
          jPopupMenu1.show(YScalePane.this, e.getX(),e.getY());
        }
      }
      public void mouseEntered(MouseEvent e){
        if(variable.thinNodeView!=null){
          variable.thinNodeView.varnamelabel.setBorder(variable.thinNodeView.highlightBorder);
        }
      }
      public void mouseExited(MouseEvent e){
        if(variable.thinNodeView!=null){
          variable.thinNodeView.varnamelabel.setBorder(null);
        }
      }
      public void mouseMoved(MouseEvent e){
      }
      public void mouseDragged(MouseEvent e){      }
    }
    /**
     * The width of the Y-scale in pixels
     */
    static final int fixedWidth = 30;

    /** The height of a major tick, in pixels */
    final int majorDepth = 10;

    /** The depth at which the labels are displayed */
    final int textDepth = 13;

    /** The vertical gap between the max of one pane, and the min of the next. */
    final int vTextGap = 6;

    public void paint(Graphics g) {
      super.paint(g);
      minorSpacing = getHeight() / nMinors;
      int xw = getWidth(), i = 0, yh = getHeight();
      g.setColor(SystemColor.controlShadow);
      g.drawLine(0, 0, 0,getHeight() - 1);
      for (int y = 0; y < yh; y += minorSpacing) {
        drawTick(g, this, y, false, i++ % majorPerMinor == 0, majorDepth);
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

    int ycoord(double value) {
      int a = (int) ( (value - variable.origin) * variable.scale);
      a = getHeight()-a;
      if (a < 1E5) {
        return a;
      }
      else {
        return 0;
      }
    }

    final void labelTick(Graphics g, double value) {
      int y = ycoord(value);
      drawTick(g, this, y, false, true, majorDepth);
      //get label text and remove trailing zeros
      String label = variable.formatValue(value, false, false);
      if (label.endsWith(".0")) {
        label = label.substring(0, label.length() - 2);
      }
      Rectangle2D rr = getFont().getStringBounds(label,
                                                 ( (Graphics2D) g).
                                                 getFontRenderContext());
      Dimension r = new Dimension( (int) rr.getWidth(), (int) rr.getHeight());
      if (y + r.height / 2 > getHeight()) {
        y = getHeight() - r.height / 2 - vTextGap / 2;
      }
      if (y - r.height / 2 < 0) {
        y = r.height / 2 + vTextGap / 2;
      }
      g.setColor(Color.black);
      //draw label centred on position x, on the baseline
      g.drawString(label, textDepth, y + r.height / 2);
    }

    final void drawArrow(Graphics g, double value, Color colour) {
      g.setColor(colour);
      int y = ycoord(value), x = 8;
      g.fillPolygon(new int[] {x, x + 8, x + 8}
                    , new int[] {y, y - 4, y + 4}
                    , 3);
    }

    /** The menu for the scale pane */
    JPopupMenu jPopupMenu1 = new JPopupMenu();
    JRadioButtonMenuItem jRadioButtonMenuItem1 = new JRadioButtonMenuItem();
    JRadioButtonMenuItem jRadioButtonMenuItem2 = new JRadioButtonMenuItem();
    JRadioButtonMenuItem customscaleradio=new JRadioButtonMenuItem();
    JMenuItem closeItem;
    ButtonGroup buttonGroup1 = new ButtonGroup();
    void jbInit() {
      jRadioButtonMenuItem1.setText("Scale to current value");
      jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          variable.zoomed =false;
          variable.rescaleRange();
          repaint();
        }
      });
      jRadioButtonMenuItem2.setText("Scale to full range");
      jRadioButtonMenuItem2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          variable.zoomed = true;
          variable.rescaleRange();
          repaint();
        }
      });
      customscaleradio.setText("Custom scale...");
      customscaleradio.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          CustomRescaleDialog crd = new CustomRescaleDialog();
          crd.setVariable(variable);
          crd.show();
        }
      });
      jPopupMenu1.add(jRadioButtonMenuItem1);
      jPopupMenu1.add(jRadioButtonMenuItem2);
      jPopupMenu1.add(customscaleradio);
      buttonGroup1.add(jRadioButtonMenuItem1);
      buttonGroup1.add(jRadioButtonMenuItem2);
      buttonGroup1.add(customscaleradio);
      updatemenu();
    }
    void updatemenu(){
      if(variable!=null){
        jRadioButtonMenuItem2.setSelected(variable.zoomed);
        jRadioButtonMenuItem1.setSelected(!variable.zoomed);
      }
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
          g.drawLine(0, pos + a, xw/2, pos + a);
        }
      }
    }
  }

  double[] speeds = new double[] {
      0.1, 0.25, 1, 4, 10
  };

  class PopupAction
      extends AbstractAction {
    double speed;
    PopupAction(double speed) {
      super(String.valueOf(speed));
      this.speed = speed;
    }

    public void actionPerformed(ActionEvent e) {
      setTSpeed(speed);
    }
  };
  public void setTSpeed(double speed) {
    if (speed >= 1) {
      scrollRate = (int) speed;
      timer = defaultTimerInterval;
    }
    else {
      scrollRate = 1;
      timer = (int) (defaultTimerInterval / speed);
    }
  }

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
  public ThinNodeView getThinNodeView(VisibleVariable vv) {
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv = (DisplayVariable)vars.get(i);
      if(dv.vv==vv) {}
      return dv.thinNodeView;
    }
    throw new IllegalArgumentException(vv+" is not graphed");
  }
  public DisplayVariable getDisplayVariable(VisibleVariable vv) throws IllegalArgumentException{
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv = (DisplayVariable)vars.get(i);
      if(dv.vv==vv) {}
      return dv;
    }
    throw new IllegalArgumentException(vv+" is not graphed");
  }
  public void setThinNodeView(VisibleVariable vv, ThinNodeView thinNodeView) {
    for(int i=0;i<vars.size();i++){
      DisplayVariable dv = (DisplayVariable)vars.get(i);
      if(dv.vv==vv) {dv.thinNodeView=thinNodeView;return;}
    }
    throw new IllegalArgumentException(vv+" is not graphed");
  }

  public void zoomAllGraphs(boolean zoomState){
   for(int i=0;i<vars.size();i++){
     DisplayVariable v=(DisplayVariable)vars.get(i);
     if(v.zoomed!=zoomState){
       v.zoomed=zoomState;
       v.rescaleRange();
     }
   }
   yscalepane.repaint();
  }

  public void setTimer(int i) {
    this.timer=i;
  }
  public int getTimer(){return timer;}

}

package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import phic.common.Organ;
import phic.modifiable.Range;
import java.awt.image.BufferedImage;
import java.util.Vector;
import phic.*;
import phic.common.Ticker;

/**
 * Graphics component for orbit plot of the errors of two variables from their
 * initial values
 */
public class OrbitPanel
    extends NodeView implements Ticker {
  BorderLayout borderLayout1 = new BorderLayout();
  GraphPaper graph = new GraphPaper() {
    public void paint(Graphics g) {
      Graphics2D g2= (Graphics2D)g;
      super.paint(g);
      g.setColor(OrbitPanel.this.getForeground());
      g.drawString(yvar.longName, getWidth() / 2, 15);
      g.drawString(xvar.longName, getWidth() - 40, getHeight() / 2);
      g.setColor(getForeground());
      drawLine(g, minx, 0, maxx, 0);
      drawLine(g, 0, miny, 0, maxy);
      if (points.size() > 1) { //plot the line
        g.setColor(OrbitPanel.this.getForeground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point last=null;
        for (int i = 0; i < points.size(); i++) {
          Point p1 = graph.toScreen((Point2D)points.get(i));
          if(last != null && p1 != null){
            g.drawLine(last.x,last.y,p1.x,p1.y);
          }
          last = p1;
        }
      }
    }
  };
  VisibleVariable xvar, yvar;
  /** Change the visible variable that is plotted along the X-axis of this orbit.*/
  public void setXVariable(VisibleVariable v) {
    xvar = v;
    Range r = Range.findRange(v.node.doubleGetVal(), scaleMode);
    xRadius = Math.max(v.initial - r.minimum, r.maximum - v.initial);
    if (scaleMode==Range.ZOOM_OUT) xRadius=Math.max(Math.max(xRadius,v.initial-v.minimum), v.maximum-v.initial);
    graph.setXRange( -xRadius, xRadius);
    repaint();
    lastPoint = null;
  }

  /** Returns the visible variable that is plotted along the X-axis of this orbit.*/
  public VisibleVariable getXVariable() {
    return xvar;
  }

  protected double xRadius, yRadius;
  /** Change the visible variable that is plotted along the Y-axis of this orbit.*/
  public void setYVariable(VisibleVariable v) {
    yvar = v;
    Range r = Range.findRange(v.node.doubleGetVal(), scaleMode);
    yRadius = Math.max(v.initial - r.minimum, r.maximum - v.initial);
    if (scaleMode==Range.ZOOM_OUT) yRadius=Math.max(Math.max(yRadius,v.initial-v.minimum), v.maximum-v.initial);
    graph.setYRange( -yRadius, yRadius);
    repaint();
    lastPoint = null;
  }

  /** Returns the visible variable that is plotted along the X-axis of this orbit.*/
  public VisibleVariable getYVariable() {
    return yvar;
  }

  public OrbitPanel(CreateGraphTarget cgt) {
    super(null, -1, cgt);
    init();
  }

  public OrbitPanel(VisibleVariable xVariable, VisibleVariable yVariable, CreateGraphTarget cgt) {
    super(yVariable.node, -1, cgt);
    init();
    setXVariable(xVariable);
    setYVariable(yVariable);
    /*
    timer.start();
    */
   HorizontalBar.addBar(this);
  }

  void init() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    //graph.setToolTipText("Click or drag to rescale");
    this.addMouseListener(mouse);
    this.addMouseMotionListener(mouseml);

  }

  /*
  int delay = 140;
  */
  /** The timer object that controls when the next pixel is redrawn.
   * by default, this is set to 100 milliseconds. */
  /*
  public Timer timer = new Timer(delay, new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      tick(1);
    }
  });
  */

  public void tick(double time){
    if(Current.body.getClock().running){
      plotPoint();
      repaint();
    }
  }

  Point lastPoint;
  boolean plotting = false;

  Vector points = new Vector();
  /** Add a new point to the list, representing the current engine's state */
  void plotPoint() {
    // Retrieve current values
    Point2D gpoint = null;
    synchronized (Organ.cycleLock) {
      gpoint = new Point2D.Double(xvar.node.doubleGetVal() - xvar.initial,
                                  yvar.node.doubleGetVal() - yvar.initial);
    }
    points.add(gpoint);
  }

  private void jbInit() throws Exception {
    this.setForeground(Color.yellow);
    graph.setBorder(BorderFactory.createLoweredBevelBorder());
    //this.setLayout(borderLayout1);
    graph.setBackground(Color.black);
    graph.setForeground(new Color(0, 0, 92));
    graph.setPreferredSize(new Dimension(150, 150));
    jMenu1.setText("X");
    jMenuItem1.setText("Zoom In");
    jMenuItem1.setToolTipText("");
    jMenuItem1.setActionCommand("Zoom in X");
    jMenuItem1.setText("Zoom in");
    jMenuItem1.addActionListener(menuAction);
    jMenuItem3.setActionCommand("Zoom out X");
    jMenuItem3.setText("Zoom out");
    jMenuItem3.addActionListener(menuAction);
    jMenuItem4.setActionCommand("Variable...X");
    jMenuItem4.setText("Variable...");
    jMenuItem4.addActionListener(menuAction);
    jMenu2.setText("Y");
    jMenuItem2.setActionCommand("Zoom in Y");
    jMenuItem2.setText("Zoom in");
    jMenuItem2.addActionListener(menuAction);
    jMenuItem5.setActionCommand("Zoom out Y");
    jMenuItem5.setText("Zoom out");
    jMenuItem5.addActionListener(menuAction);
    jMenuItem6.setActionCommand("Variable... Y");
    jMenuItem6.setText("Variable...");
    jMenuItem6.addActionListener(menuAction);
    clearmenu.setText("Clear");
    this.content.add(graph);
    jPopupMenu1.add(jMenu1);
    jPopupMenu1.add(jMenu2);
    jPopupMenu1.add(clearmenu);
    clearmenu.addActionListener(menuAction);
    jMenu1.add(jMenuItem1);
    jMenu1.add(jMenuItem3);
    jMenu1.add(jMenuItem4);
    jMenu2.add(jMenuItem2);
    jMenu2.add(jMenuItem5);
    jMenu2.add(jMenuItem6);
  }
  ActionListener menuAction = new ActionListener(){
    public void actionPerformed(ActionEvent e){ menuaction(e); }
  };
  MouseListener mouse = new MouseAdapter(){
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        jPopupMenu1.show(OrbitPanel.this, e.getX(), e.getY());
      }
      else if(!dragging){
        scaleMode = Range.ZOOM_RANGES[scaleIndex++];
        scaleIndex %= Range.ZOOM_RANGES.length;
        setXVariable(xvar);
        setYVariable(yvar);
      }
      dragging=false;
    }
    public void mousePressed(MouseEvent e){ mousepos=e.getPoint(); }
  };
  Point mousepos = null;
  boolean dragging;
  MouseMotionListener mouseml = new MouseMotionAdapter(){
    public void mouseDragged(MouseEvent e){
      if(mousepos!=null){
        double dx=e.getX()-mousepos.x;
        double dy=e.getY()-mousepos.y;
        xRadius *= 1+(-dx*0.05);
        yRadius *= 1+(dy*0.05);
        if(dx!=0 || dy!=0){
          graph.setYRange( -yRadius, yRadius);
          graph.setXRange( -xRadius, xRadius);
          repaint();
        }
        mousepos=e.getPoint();
        dragging=true;
      }
    }
  };
  int scaleIndex = 2;
  int scaleMode = Range.ZOOM_OUT;
  private JPopupMenu jPopupMenu1 = new JPopupMenu();
  private JMenu jMenu1 = new JMenu();
  private JMenuItem jMenuItem1 = new JMenuItem();
  private JMenuItem jMenuItem3 = new JMenuItem();
  private JMenuItem jMenuItem4 = new JMenuItem();
  private JMenu jMenu2 = new JMenu();
  private JMenuItem jMenuItem2 = new JMenuItem();
  private JMenuItem jMenuItem5 = new JMenuItem();
  private JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem clearmenu = new JMenuItem();

  void menuaction(ActionEvent e) {
    String s = e.getActionCommand();
    boolean isX = s.endsWith("X");
    if (s.startsWith("Zoom out")) {
      if (isX) {
        xRadius *= 2;
      }
      else {
        yRadius *= 2;
      }
    }
    else if (s.startsWith("Zoom in")) {
      if (isX) {
        xRadius /= 2;
      }
      else {
        yRadius /= 2;
      }
    }
    else if (s.startsWith("Var")) {
      // show var dialog box
      VisibleVariable vcc = VariableChooser.selectVariable(null);
      if (vcc != null) {
        if (isX) {
          setXVariable(vcc);
        }
        else {
          setYVariable(vcc);
        }
      }
    }
    if (s.startsWith("Zoom")) {
      graph.setYRange( -yRadius, yRadius);
      graph.setXRange( -xRadius, xRadius);
      repaint();
    }else if(s.startsWith("Clear")){
      points.removeAllElements();
      repaint();
    }
  }
  public void removeNotify(){
    super.removeNotify();
    HorizontalBar.removeBar(this);
    /* timer.stop(); timer=null; */
  }
}

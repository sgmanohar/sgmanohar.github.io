package phic.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import phic.Resource;
import java.awt.event.*;
import phic.common.Ticker;
import phic.common.VDouble;
import phic.common.Quantity;
import phic.*;
import javax.swing.event.*;

public class ThinNodeView
    extends JPanel
    implements INodeView, Ticker {
  JLabel valuetxt = new JLabel();
  JButton closebutton = new JButton();
  JButton actionbutton = new JButton();
  JPanel extrapanel = new JPanel();
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel iconLabel = new JLabel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JSlider slider = new JSlider();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JCheckBox graphcheckbox = new JCheckBox();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  JLabel varnamelabel = new JLabel();

  static final ImageIcon closeIcon = new ImageIcon(Resource.loader.getImageResource("Cross8px.gif")),
      highIcon = new ImageIcon(Resource.loader.getImageResource("HighSymbol.gif")),
      lowIcon = new ImageIcon(Resource.loader.getImageResource("LowSymbol.gif")),
      actionIcon = new ImageIcon(Resource.loader.getImageResource("Method.gif")),
      valueIcon = new ImageIcon(Resource.loader.getImageResource("Double.gif")),
      tickIcon =  new ImageIcon(Resource.loader.getImageResource("Tick.gif")),
      crossIcon = new ImageIcon(Resource.loader.getImageResource("Cross.gif"));
  public ThinNodeView() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void addNotify(){
    HorizontalBar.addBar(this);
    HorizontalBar.addBar(this);
    super.addNotify();
  }
  public void removeNotify(){
    HorizontalBar.removeBar(this);
    super.removeNotify();
  }

  boolean isReadOnly = false;
  public void setReadOnly(boolean b){
    isReadOnly=b;
    if(isReadOnly){
      slider.setEnabled(false);
    }else{
      slider.setEnabled(true); // Disable slider for VDoubleReadOnly
    }
  }


  /**
   * replaceVariables
   */
  public void replaceVariables() {
    node = Node.findNodeByName(node.canonicalName());
    initialiseFromNode();
    updateValue();
  }

  protected Node node;
  /** Set the node */
  public void setNode(Node n) {
    node = n;
    initialiseFromNode();
  }

  /** Called when the node that this item refers to has changed */
  protected void initialiseFromNode() {
    String s = node.getFriendlyName();
    varnamelabel.setText(s);
    varnamelabel.setToolTipText(s+" ("+node.canonicalName()+")");
    if (node instanceof VDoubleNode) {
      theVDouble = ( (VDoubleNode) node).getVDouble();
    }
    type = node.getType();
    theVV = Variables.forNode(node);
    if (theVV == null) {
      if (type == Node.BOOLEAN) {
        setupBoolean();
      }
      else if (type == Node.SIMPLE_METHOD) {
        setupMethod();
      }
      else if (type == Node.DOUBLE) {
        setupNumeric();
        oldVal=node.doubleGetVal();
        slider.setToolTipText("Range: 0 - "+Quantity.toString( oldVal*2 ));
        valuetxt.setToolTipText("Previously: "+Quantity.toString(oldVal));
      }
    }
    else {
      slider.setToolTipText("Range: "+theVV.formatValue(theVV.minimum, true,false) +
                            " - " + theVV.formatValue(theVV.maximum, true,false));
      valuetxt.setToolTipText("Normal: "+theVV.formatValue(theVV.initial, true, false));
      setupNumeric();
    }
    updateValue();
  }

  protected void setupBoolean() {
    slider.setVisible(false);
    extrapanel.removeAll();
    actionbutton.setEnabled(true);
    graphcheckbox.setVisible(false);
    actionbutton.setIcon(tickIcon);
    actionbutton.setToolTipText("Change Yes/No value");
  }



  protected void setupNumeric() {
    slider.setVisible(true);
    extrapanel.setVisible(true);
    extrapanel.removeAll();
    extrapanel.add(iconLabel);
    iconLabel.setIcon(null);
    actionbutton.setEnabled(false);
    graphcheckbox.setVisible(true);
    slider.setValue(50);
    setReadOnly(!node.isSettable());
    actionbutton.setIcon(valueIcon);
    actionbutton.setToolTipText("");
    oldVal=Double.NaN;
  }

  protected void setupMethod() {
    slider.setVisible(false);
    extrapanel.removeAll();
    extrapanel.setVisible(false);
    actionbutton.setEnabled(true);
    graphcheckbox.setVisible(false);
    valuetxt.setVisible(false);
    actionbutton.setIcon(actionIcon);
    actionbutton.setToolTipText("Invoke action");
  }

  /** The foreground colour of a variable that is not being graphed. */
  Color defaultForeground = Color.lightGray;
  /** The Node type number */
  int type;
  /** represents the current value */
  double currentdouble;
  /** The VDouble if this node represents a VDouble */
  VDouble theVDouble;
  /** The visible variable, if this node represents a VisibleVariable */
  VisibleVariable theVV;

  /** Update the interface */
  public void tick(double time) {
    if (type == Node.DOUBLE || type == Node.BOOLEAN) {
      updateValue();
    }
  }
  /** Keep old value of double in case changed */
  double oldVal;
  /**
   * This is true if we are changing the value of the slider because the
   * variable's value has changed. It is false when the slider is moved by
   * the user.
   */
  boolean automaticAdjust = false;

  /**
   * This reflects whether a graph is being displayed or not, in the ScrollGraph
   * window.
   */
  boolean graphDisplayed = false;



  /** Update the slider and text depending on value of variable */
  public void updateValue() {
    if (type == Node.DOUBLE) {
      double val = node.doubleGetVal();
      if (theVV!=null){
        valuetxt.setText(theVV.formatValue(theVDouble.get(), true, true));
        iconLabel.setIcon(getIcon(val, theVDouble));
      }else if (theVDouble != null) {
        // if(!graphDisplayed) valuetxt.setForeground(getColour(val, theVDouble));
        valuetxt.setText(theVDouble.formatValue(true, true));
        iconLabel.setIcon(getIcon(val, theVDouble));
      }
      else {
        if (currentdouble != val) {
          valuetxt.setText(Quantity.toString(val));
          currentdouble=val;
        }
      }
      //update slider -  only implemented for vdoubles
      if(theVDouble!=null){
        automaticAdjust = true;
        if (val != oldVal) {
          oldVal = val;
          slider.setValue( (int) (100 * (val - theVDouble.minimum) /
                                  (theVDouble.maximum - theVDouble.minimum)));
        }
        automaticAdjust = false;
      }
    }
    else if (type == Node.BOOLEAN) {
      boolean b = node.booleanGetVal();
      valuetxt.setText(b ? "Yes" : "No");
      actionbutton.setIcon(b ? tickIcon : crossIcon);
    }
  }

  public JComponent getComponent() {
    return this;
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),BorderFactory.createEmptyBorder(2,2,2,2));
    this.setLayout(borderLayout1);
    valuetxt.setBackground(Color.black);
    valuetxt.setFont(new java.awt.Font("Dialog", 1, 12));
    valuetxt.setForeground(defaultForeground);
    valuetxt.setBorder(border1);
    valuetxt.setOpaque(true);
    valuetxt.setPreferredSize(new Dimension(100, 20));
    valuetxt.setText("0.000");
    valuetxt.addMouseListener(new ThinNodeView_valuetxt_mouseAdapter(this));
    closebutton.setToolTipText("Remove this variable from display");
    closebutton.setIcon(closeIcon);
    closebutton.setMargin(new Insets(0, 0, 0, 0));
    closebutton.addActionListener(new ThinNodeView_closebutton_actionAdapter(this));
    actionbutton.setPreferredSize(new Dimension(22, 20));
    actionbutton.setActionCommand("*");
    actionbutton.setHorizontalAlignment(SwingConstants.LEFT);
    actionbutton.setHorizontalTextPosition(SwingConstants.LEFT);
    actionbutton.setMargin(new Insets(2, 1, 2, 1));
    actionbutton.addActionListener(new ThinNodeView_namebutton_actionAdapter(this));
    this.setMinimumSize(new Dimension(210, 20));
    this.setPreferredSize(new Dimension(350, 20));
    jPanel1.setLayout(borderLayout2);
    extrapanel.setBackground(Color.black);
    extrapanel.setForeground(Color.white);
    extrapanel.setPreferredSize(new Dimension(6, 0));
    extrapanel.setLayout(borderLayout4);
    iconLabel.setPreferredSize(new Dimension(34, 15));
    iconLabel.setText("");
    slider.setPreferredSize(new Dimension(80, 20));
    slider.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent d){ slider_stateChanged(d);}});
    jPanel2.setLayout(borderLayout3);
    jPanel2.setBorder(null);
    graphcheckbox.setToolTipText("Display graph");
    graphcheckbox.setText("");
    graphcheckbox.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){graphcheckbox_actionPerformed(e);}});
    jPanel3.setLayout(borderLayout5);
    jPanel4.setLayout(borderLayout6);
    varnamelabel.setPreferredSize(new Dimension(120, 15));
    varnamelabel.setText("Full Name of Variable");
    varnamelabel.addMouseListener(new ThinNodeView_varnamelabel_mouseAdapter(this));
    this.add(closebutton,  BorderLayout.EAST);
    this.add(jPanel1,  BorderLayout.CENTER);
    jPanel1.add(valuetxt,  BorderLayout.WEST);
    jPanel1.add(jPanel3,  BorderLayout.CENTER);
    jPanel3.add(extrapanel, BorderLayout.WEST);
    extrapanel.add(iconLabel, BorderLayout.CENTER);
    jPanel3.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(graphcheckbox, BorderLayout.EAST);
    jPanel2.add(slider, BorderLayout.CENTER);
    this.add(jPanel4,  BorderLayout.WEST);
    jPanel4.add(actionbutton,  BorderLayout.EAST);
    jPanel4.add(varnamelabel,  BorderLayout.WEST);
  }

  void closebutton_actionPerformed(ActionEvent e) {
    if(graphcheckbox.isSelected()) {
      graphcheckbox.doClick();
    }
    HorizontalBar.removeBar(this);
    Container p = getParent();
    if(p==null)return;
    Container p2 = p.getParent();
    if(p2!=null){p2.remove(p);p2.validate();}
  }

  /** Set which graph item will receive commands to create and remove graphs from this view */
  public void setCreateGraphTarget(CreateGraphTarget cgt) {
    this.cgt = cgt;
  }

  /** The target graph window that will receive commands to add and remove graphs */
  CreateGraphTarget cgt;


  /**
   * clicking the value text displays the VariableProperties dialogc
   */
  void displayVariableProperties() {
    if (theVV == null) {
      return;
    }
    theVV.displayVariableDialog();
  }

  Action readOnlyAction = new AbstractAction("Read-only"){
    public boolean isEnabled(){
      return theVV.type!=2 && theVV.type !=0;
    }
    public Object getValue(String key){
      boolean f=isReadOnly;
      if(key.equals(NAME))
        return !f?  "Make read-only" : "Allow modification";
      else if(key.equals(SHORT_DESCRIPTION))
        return !f?  "Prevents modification to variable" : "Allows the variable's value to be changed";
      return super.getValue(key);
    }
    public void actionPerformed(ActionEvent e){
      setReadOnly(!isReadOnly);
    }

  };


  /**
   * Clicking button invokes method or alters the boolean
   */
  void namebutton_actionPerformed(ActionEvent e) {
    if (type == Node.BOOLEAN) {
      boolean b = !node.booleanGetVal();
      node.booleanSetVal(b);
      updateValue();
      String comment = node.getLeafName() + (b ? " on" : " off");
      PhicApplication.markEvent(comment);
      Current.body.message(comment);
    }
    else if (type == node.SIMPLE_METHOD) {
      node.methodInvoke();
      String comment = node.getLeafName();
      PhicApplication.markEvent(comment);
      Current.body.message(comment);
    }
    else if (type == node.DOUBLE) {
      displayVariableProperties();
    }
  }

  /**
   * Moving the slider can alter the value of the variable.
   */
  void slider_stateChanged(ChangeEvent e) {
    if (automaticAdjust)  return;
    String newString;
    double newVal;
    if (theVDouble != null) {
        newVal = theVDouble.minimum
                 + (theVDouble.maximum - theVDouble.minimum) * slider.getValue()
                 / 100.;
        newString = theVDouble.formatValue(newVal, true, true);
    } else {
        newVal = oldVal * (1 + (slider.getValue() - 50) / 50.);
        newString = Quantity.toString(newVal);
    }
    node.doubleSetVal(newVal);
    valuetxt.setText(newString);
  }
  Border highlightBorder = null;
  /**
   * Altering the checkbox adds or removes a graph. It calls the addNewVariable
   * method of a CreateGraphTarget object (such as the ScrollGraph pane)
   * and sets the field graphDisplayed accordingly
   */

  void graphcheckbox_actionPerformed(ActionEvent e) {
    if (cgt != null) {
      if (theVV != null) {
        if (graphcheckbox.isSelected()) {
          cgt.addNewVariable(theVV);
          cgt.setThinNodeView(theVV, this);
          graphDisplayed=true;
          Color graphColor = cgt.getColor(node);
          valuetxt.setForeground(graphColor);
          highlightBorder = BorderFactory.createMatteBorder(2,2,2,2,graphColor);
        }
        else {
          cgt.remove(theVV);
          graphDisplayed=false;
          valuetxt.setForeground(defaultForeground);
          highlightBorder = null;
        }
      }
      else if (theVDouble != null) {
        if (graphcheckbox.isSelected()) {
          cgt.addNewVariable(theVDouble, node.getFriendlyName());
          graphDisplayed=true;
          Color graphColor = cgt.getColor(node);
          valuetxt.setForeground(graphColor);
          highlightBorder = BorderFactory.createMatteBorder(2,2,2,2,graphColor);
        }
        else {
          cgt.remove(theVDouble);
          graphDisplayed=false;
          valuetxt.setForeground(defaultForeground);
          highlightBorder = null;
        }
      }
    }
  }
  /**
   * The colour of the bar is
   *   red:    too high - above the maximum
   *   cyan:   OK - within the range minimum -- maximum
   *   yellow: too low - below the minimum
   */
  /* // obsolete since bar is replaced by slider. Now, an arrow icon shows out of range.
  protected final Color getColour(double val, VDouble variable){
          if(val<variable.minimum){
                  return Color.yellow;
          }
          if(val<=variable.maximum){
                  return Color.cyan;
          }
          return Color.red;
  }*/

  /**
   * The icon is high or low depending on the value of the variable-
   * this is currently coloured arrow
   */
  public static final ImageIcon getIcon(double val, VDouble variable) {
    if (val < variable.minimum) {
      return lowIcon;
    }
    if (val <= variable.maximum) {
      return null;
    }
    return highIcon;
  }

  void valueClicked(MouseEvent e) {
    if(e.isPopupTrigger())     {
      if (theVV == null)return;
      JPopupMenu m = new JPopupMenu();
      JMenu umenu= theVV.createUnitsMenu();
      if(umenu!=null)m.add(umenu);
      m.add(theVV.showVariableInfoBox);
      if(!isReadOnly) {
        m.add(theVV.resetVariableValue);
        m.add(theVV.clampVariable);
      }
      if(PhicFrameSetup.treeIsShowing && theVV.type!=2 && theVV.type !=0) m.add(readOnlyAction);
      m.show((Component)e.getSource(),e.getX(),e.getY());
    }else if(e.getClickCount()==2){
      displayVariableProperties();
    }
  }

}

class ThinNodeView_closebutton_actionAdapter
    implements java.awt.event.ActionListener {
  ThinNodeView adaptee;

  ThinNodeView_closebutton_actionAdapter(ThinNodeView adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.closebutton_actionPerformed(e);
  }
}


class ThinNodeView_valuetxt_mouseAdapter
    extends java.awt.event.MouseAdapter {
  ThinNodeView adaptee;

  ThinNodeView_valuetxt_mouseAdapter(ThinNodeView adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
    adaptee.valueClicked(e);
  }
  public void mouseReleased(MouseEvent e){
    adaptee.valueClicked(e);
  }
}

class ThinNodeView_namebutton_actionAdapter
    implements java.awt.event.ActionListener {
  ThinNodeView adaptee;

  ThinNodeView_namebutton_actionAdapter(ThinNodeView adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.namebutton_actionPerformed(e);
  }
}

class ThinNodeView_varnamelabel_mouseAdapter extends java.awt.event.MouseAdapter {
  ThinNodeView adaptee;

  ThinNodeView_varnamelabel_mouseAdapter(ThinNodeView adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.displayVariableProperties();
  }
}

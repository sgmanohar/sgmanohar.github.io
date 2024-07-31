package phic.gui;

import java.lang.reflect.*;
import java.util.StringTokenizer;
import phic.Current;
import phic.common.*;
import javax.swing.JMenu;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.AbstractAction;

/**
 * Visible variables are variables that are visualisable by the user. They
 * Must have an entry in the table of variables, specifying their initial,
 * minimum and maximum values, and their unit and name.
 *
 * Effectively they become like VDoubles, but without the implementation of
 * Variable - i.e. they do not have a value, unless thay are bound to an object
 * using Node. In addition, they are all uniquely named, and know their location
 * in the variable tree.
 *
 * Thus they are links to a subset of all VDouble variables.
 */
public class VisibleVariable {
  /** The constructor does two things. It reads the details of the minimum,
   * maximum and initial values, the unit, and the name from a line of the table.
   * Then it searches the object tree for the specified variable name, and stores
   * the path to access this variable. This information is used to create
   * the Node object for this visible variable. The parser initially looks for
   * fields of the name specified, and then methods.
   * @see phic.gui.Node#Node
   * @todo ? useful to refactor the 'find node from name' part of this method
   */
  public VisibleVariable(Table t, int i) {
    canonicalName = t.getString(i, 7);
    longName = t.getString(i, 6);
    shortName = t.getString(i, 5);
    initial = t.getDouble(i, 0);
    minimum = t.getDouble(i, 1);
    maximum = t.getDouble(i, 2);
    units = (int) t.getDouble(i, 3);
    type = (int) t.getDouble(i, 4);
    //parse name for a 'node' object
    StringTokenizer e = new StringTokenizer(canonicalName, ".");
    String node;
    Member member = null;
    Object object;
    boolean isfield = true, firstnode = true;
    node = e.nextToken();
    if (node.equals("body")) {
      object = Current.body;
    }
    else {
      object = Current.environment;
    }
    for (; e.hasMoreElements(); ) {
      //compute next object in path
      node = e.nextToken();
      if (node == null) {
        Current.body.error("null node string");
        break;
      }
      if (!firstnode) {
        if (member == null) {
          Current.body.error("null node member " + object + "=" + canonicalName);
          break;
        }
        try {
          if (isfield) {
            object = ( (Field) member).get(object);
          }
          else {
            object = ( (Method) member).invoke(object, new Object[0]);
          }
        }
        catch (Exception x) {
          x.printStackTrace();
          break;
        }
      }
      firstnode = false;
      //compute next member
      try {
        member = object.getClass().getField(node);
        isfield = true;
      }
      catch (NoSuchFieldException x) {
        try {
          member = object.getClass().getMethod(node, new Class[0]);
          isfield = false;
        }
        catch (NoSuchMethodException y) {
          Current.body.error("cannot find node " + node + " at " +
                             canonicalName);
        }
      }
    }
    //was previously a LimitedNode, subclassed to VDoubleNode
    // on 16.6.5 since all VisibleVariables should be VDoubles?
    this.node = new VDoubleNode(member, object, null);
    this.node.getVDouble().setUnit(this.units);

    checkOverridden();
  }

  /**
   * Set the value of this variable to its initial value, as
   * defined in Variables.txt . This is a 'hard' reset, and ignores
   * any changes that have been made to the VDouble's set-point etc.
   * e.g. via scripts, diseases, or Person parameter modification.
   */
  public void initialiseFromScratch() {
    if (node != null) {
      if (node.getType() == Node.DOUBLE) {
        node.doubleSetVal(initial);
        node.vdoubleSetRanges(initial, minimum, maximum);
      }
      else if (node.getType() == Node.BOOLEAN) {
        node.booleanSetVal(initial != 0);
      }
    }
    else {
      Current.body.error("Could not initialise " + canonicalName);
    }
  }

  private boolean hasInitialisedOnce = false;

  /**
   * Sets the value of the variable to its initial value as
   * defined in the VDouble itself; this might be different to the value
   * in the VisibleVariable (which comes directly from the text file),
   * as it could have been changed by scripts, or by Person modification.
   */
  public void initialise() {
    if (!hasInitialisedOnce) {
      initialiseFromScratch();
      hasInitialisedOnce = true;
      return;
    }
    if (node != null) {
      if (node.getType() == Node.DOUBLE) {
        node.doubleSetVal(node.getVDouble().initialValue);
      }
      else if (node.getType() == Node.BOOLEAN) {
        node.booleanSetVal(node.getVDouble().initialValue != 0);
      }
    }
  }

  /**
   * Sets the global unit override status for this variable.
   * Sets to true if an alternate unit is specififed in the text file, and
   * the global overrides flag is switched on in the UnitConstants class.
   * This overwrites the variable's individual override status.
   */
  public void checkOverridden() {
    setOverridden(  UnitConstants.getUseUnitOverrides() &&
                    UnitConstants.canVariableBeOverridden(canonicalName));
  }

  /** Unit strings and display */
  static String[] ustring = new String[] {
      "", "1", "2", "g", "l", "5", "mHg", "7", "°C",
      "9", "10", "Osm", "l/min", "13", "M", "/min", "PRU", "%", "J/min", "l/l",
      "g/l", "21",
      "22", "cal/d", "24"
  };

  /** Prefixes for units, in factors of 1000 */
  static String[] uprefix = new String[] {
      "p", "n", "µ", "m", "", "k", "M"};

  /**
   * Method returning a string representing the value of this variable.
   * The unit is displayed with a magnitude character e.g. 'm' or 'k'.
   *
   * This method calls UnitString.formatValue()
   * @see UnitString#formatValue(double, int, boolean, boolean)
   */
  public String formatValue(double v, boolean showUnit, boolean fixed) {
    if (!isOverridden) {
      return UnitConstants.formatValue(v, this.units, showUnit, fixed);
    }
    else {
      String s = UnitConstants.formatValue(v * overriddenConversion,
                                           UnitConstants.MOLAR, showUnit, fixed);
      if (showUnit) {
        s = s.substring(0, s.length() - 1) + overriddenUnit;
      }
      return s;
    }
  }

  /**
   *
   * @param v the value to format
   * @param fixed uses fixed 4 sig figs
   * @return a string representation of the given value
   */
  /*public final String formatValue(double v, boolean fixed){
   return formatValue(v,true,fixed);
    }*/

  /* //migrated to phic.common.UnitConstants
   boolean canPrefix(int u){
   if(u==8 || u==17)return false;
   return true;
   }
   */

  /**
   * Displays a variable properties dialogue box
   */
  public void displayVariableDialog() {
    VariablePropertiesDialog d = new VariablePropertiesDialog();
    d.setVariable(this);
    d.show();
  }

  //CONTENT
  /** The range of the variable, read from Variables.txt in Variables.class */
  public double minimum, initial, maximum;

  /** The unit and the type of the variable, read from the Variables.txt file */
  public int units, type;

  /** The node in the body tree corresponding to this variable */
  public VDoubleNode node;

  /** The various names of the variable */
  public String canonicalName, longName, shortName;

  /** Checks whether the unit has a overridden unit conversion. */
  public void setOverridden(boolean b) {
    isOverridden = b;
    if (isOverridden) {
      overriddenUnit = UnitConstants.getOverriddenUnit(canonicalName);
      overriddenConversion = UnitConstants.getOverridenConversion(canonicalName);
    }
  }

  private boolean isOverridden = false;
  private String overriddenUnit = null;
  private double overriddenConversion = Double.NaN;

  public String toString() {
    if (node == null) {
      return longName;
    }

    return longName
        //   +" ("+formatValue(node.doubleGetVal(),true,false)+")"
        ;
  }
  /** Actions for menus to change units */
  Action setNormalUnitAction, setOverriddenUnitAction;
  /** Class for actions to change units */
  class SetUnitAction extends AbstractAction { boolean override;
    SetUnitAction(boolean override, String name){ super(name); this.override=override;}
    SetUnitAction(boolean override, String name, Icon icon){super(name,icon);this.override=override; }
    public void actionPerformed(ActionEvent e){setOverridden(override);}
  }
  /** Create menus to change units, with appropriate actions */
  public JMenu createUnitsMenu(){
    if(!UnitConstants.canVariableBeOverridden(canonicalName))return null;
    JMenu m=new JMenu("Unit");
    if(setNormalUnitAction==null){
      setNormalUnitAction = new SetUnitAction(false, ustring[units]);
      setOverriddenUnitAction = new SetUnitAction(true, UnitConstants.getOverriddenUnit(canonicalName));
    }
    m.add(setNormalUnitAction);
    m.add(setOverriddenUnitAction);
    return m;
  }

  public Action showVariableInfoBox = new AbstractAction("Info"){ public void actionPerformed(ActionEvent e){
      displayVariableDialog();
    }};
  public Action resetVariableValue = new AbstractAction("Reset"){
    public boolean isEnabled(){
      return type!=2 && type !=0;
    }
    public void actionPerformed(ActionEvent e){
      Current.environment.getVariableClamps().setFudgeVariable(VisibleVariable.this, false);
      initialise();
  }};
  public Action clampVariable = new AbstractAction("Clamp value"){
    public boolean isEnabled(){
      return type!=2 && type !=0;
    }
    public Object getValue(String key){
      boolean f=Current.environment.getVariableClamps().isFudgeVariable(VisibleVariable.this);
      if(key.equals(NAME))
        return !f?  "Clamp value" : "Unclamp value";
      else if(key.equals(SHORT_DESCRIPTION))
        return !f?  "Fix the variable's value at its current value" : "Calculate variable's value normally";
      return super.getValue(key);
    }
    public void actionPerformed(ActionEvent e){
      LifeSupport ls=Current.environment.getVariableClamps();
      VisibleVariable v=VisibleVariable.this;
      if(!ls.isFudgeVariable(v)){
        ls.setFudgeVariable(v, true);
        ls.setFudgeValue(v, v.node.doubleGetVal());
        LifeSupportDialog lsd= new LifeSupportDialog(ls);
        lsd.setSelection(v);
        lsd.valuetxt.grabFocus();
        lsd.show();
      } else {
        ls.setFudgeVariable(v, false);
      }
  }};

}

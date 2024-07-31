package phic.modifiable;

import java.util.*;

import phic.*;
import phic.common.*;
import phic.gui.*;
import evaluator.MathExtra;
/**
 * A controller represents a relationship between two variables, the controlled
 * variable and the controlling variable.
 *
 * The controller contains a calculate() method that should be invoked regularly
 * in order to perform the calculation, according to the elapsedTime.
 *
 * The default control type causes a tendency for the controlled variable to
 * move towards a target value determined by the error of the controlling
 * variable (error being the deviation from the set point)
 *
 */
public final class Controller {
  /**
   * The enum class representing a type of controller - these represent the
   * different calculations that can be performed when <code>calculate()</code>
   * is called.
   */
  public static class Type{
    String name, abbreviation;
    private Type(String abbrev,String name){
      this.abbreviation=abbrev;
      this.name=name;
      listAll.add(this);
    }
    public String toString(){return name;}
    /** Return a type for a given string representing the name of the type */
    public static Type forName(String s){
      for(int i=0;i<listAll.size();i++){
        Type t=(Type)listAll.get(i);
        if(t.name.equalsIgnoreCase(s) || t.abbreviation.equalsIgnoreCase(s)) return t;
      } throw new IllegalArgumentException("No such controller type, "+s);
    }
    private static Vector listAll=new Vector();

    /** Return a list of the possible types */
    public static Type[] getTypes(){ return (Type[])listAll.toArray(new Type[listAll.size()]); }

    /**
     * Controller moves the variable towards a target value, calculateed by
     * multiplying the controlling variable's error by a  gain, and
     * adding this to the controlled variable's set-point.
     * G * error + C
     */
    public static Type ERROR_GAIN_UNLIMITED = new Type("EGU","Error gain (unlimited)");

    /**
     * Same as error gain unlimited, except that the targed value is limited
     * to be within the controlled variable's normal range (as defined by
     * VDouble.minimum and VDouble.maximum)
     * max( controlled.min( min( controlled.max,  G * error + C ) ) )
     */
    public static Type ERROR_GAIN_LIMITED = new Type("EGL","Error gain (limited)");

    public static Type ERROR_ADD_LIMITED = new Type("EAL", "Error add (limited)");

    /**
     * Same as error gain limited, but the error is only effective if be positive
     * max(0, G * error + C )
     */
    public static Type ERROR_POSITIVE_LIMITED = new Type("EPL", "Error positive (limited)");

    /** Same as error gain limited, but the error is only effective if be positive */
    public static Type ERROR_NEGATIVE_LIMITED = new Type("ENL", "Error negative (limited)");


    /**
     * Uses the ratio of the controlling variables value to its initial value
     * to calculate the target value of the controlled variable.
     */
    public static Type PROPORTION_UNLIMITED = new Type("PRU","Proportional (unlimited)");
    /**
    * Also uses the ratio of the controlling variables value to its initial value
    * to calculate the target value of the controlled variable, but limits
    * the target to the normal range of the controlled variable.
    */
    public static Type PROPORTION_LIMITED = new Type("PRL","Proportional (limited)");

    /**
     * Controls a variable on a power of the controlling variable's error,
     * e.g. error squared (uses the correct sign)
     */
    public static Type ERROR_POWER_LIMITED = new Type("EPWL", "Error power (limited)");

    /**
     * A sigmoid function that is centred over the normal value
     * G * sigmoid ( error / C )
     */
    public static Type ERROR_GAIN_SIGMOID = new Type("SIG", "Error gain sigmoid");

    public static Type ERROR_POWER_POSITIVE_LIMITED = new Type("EPPL", "Error power positive (limited)");
    public static Type ERROR_POWER_NEGATIVE_LIMITED = new Type("EPNL", "Error power negative (limited)");


  }

  /**
   * Enum for contributions: the ways in which multiple controllers can control
   * a single variable.
   */
  public static class  Contribution {
    private Contribution(String abbrev, String name){
      this.abbrev=abbrev; this.name=name;
      listAll.add(this);
    }
    private String name; private String abbrev;
    private static Vector listAll = new Vector();
    public String toString(){return name;}
    /**
     * Additive method additively alters the target value before moving the
     * controlled variable
     */
    public static Contribution ADDITIVE = new Contribution("+","Additive");
    /**
     * Multiplicative multiplies the current deviation of the target.
     */
    public static Contribution MULTIPLICATIVE = new Contribution("*","Multiplicative");
    /**
     * This controller executes independly of other controllers, hence it
     * competes individually.
     */
    public static Contribution COMPETITIVE = new Contribution(".",    "Competitive");
    public static Contribution forName(String s) {
      for (int i = 0; i < listAll.size(); i++) {
        Contribution t = (Contribution) listAll.get(i);
        if (t.name.equalsIgnoreCase(s) || t.abbrev.equalsIgnoreCase(s)) return t;
      }
      throw new IllegalArgumentException("No such controller type, " + s);
    }
  }

  /**
   * The contribution describes what will happen when two or more variables
   * try to act as controllers of a single controlled variable.
   */
  protected Contribution contribution = Contribution.ADDITIVE;


  /**
   * The type of controller determines what calculation is performed
   * when the calculate function is called.
   */
  protected Type type = Type.ERROR_GAIN_UNLIMITED;

  /** The variables that are controlled, and that do the controlling */
  protected VDouble controlling, controlled;
  protected VisibleVariable controllingVariable, controlledVariable;

  /** A description of the controller   */
  protected String description;

  /**
   * Gain is the amount by which the controlling variable's error is multiplied
   * by when determining the target value.
   */
  protected double gain = 0;

  /**
   * This is the low-pass filter rate at which the controlled variable moves
   * towards the target value. The value represents the fraction of the error
   * that will be corrected in one minute.
   */
  protected double rateFractionPerMinute = 0;

  /**
   * This is used as the controlling variable if a drug-binding value is needed.
   */
  protected String drugProperty = null;




  /**
   * This parameter is the offset added on to the error, or in the case of a
   * exponent controller, is the multiplying factor.
   * -- currently unused.
   */
  protected double constant = 0;

  public Controller(VisibleVariable controlledV, VisibleVariable controllingV) {
    this.controllingVariable = controllingV;
    this.controlledVariable  = controlledV;
    this.controlling = controllingV.node.getVDouble();
    this.controlled  = controlledV.node.getVDouble();
  }
  public Controller(VisibleVariable controlledV, String drugProperty){
    this.controllingVariable = null;
    this.controlledVariable  = controlledV;
    this.controlled  = controlledV.node.getVDouble();
    this.drugProperty = drugProperty;
  }

  /** Do the processing */
  public void calculate(double elapsedTimeSecs){
    double r=Organ.fractionDecayPerMinute(rateFractionPerMinute, elapsedTimeSecs);
    if(controllingVariable!=null){
      if(type == Type.ERROR_GAIN_LIMITED){
        controlled.regulateQuantity(controlling, gain, r, constant);
        return;
      }else if(type == Type.ERROR_GAIN_UNLIMITED){
        controlled.regulate(controlling, gain, r, constant);
        return;
      }else if(type == Type.PROPORTION_LIMITED){
        controlled.lowPass(constant+controlled.initialValue * (1+gain*(controlling.get()/controlling.initialValue-1)), r);
        return;
      }else if(type == Type.PROPORTION_UNLIMITED){
        controlled.lowPassQuantity(controlled.initialValue + gain * controlling.get()/controlling.initialValue, r);
        return;
      }else{
        double desiredError = getDesiredError();
        controlled.lowPassQuantity(controlled.initialValue + desiredError, r);
        return;
      }
    }else if(drugProperty!=null){
      double binding = Current.body.blood.getDrugBinding(drugProperty);
      if (type == Type.ERROR_GAIN_LIMITED) {
        controlled.lowPassQuantity(controlled.initialValue + gain * binding, r);
        return;
      }
      else if (type == Type.ERROR_GAIN_UNLIMITED) {
        controlled.lowPass(controlled.initialValue + gain * binding, r);
        return;
      }
      else if (type == Type.ERROR_POWER_LIMITED) {
        controlled.lowPassQuantity(controlled.initialValue + getDesiredError(), r);
        return;
      }
    }
    throw new UnsupportedOperationException("Can't perform "+type+" with "+controllingVariable+" controlling "+controlledVariable);
  }

  public double getDesiredError(){
    if(controlling != null){
      if (type == Type.ERROR_GAIN_LIMITED || type == Type.ERROR_GAIN_UNLIMITED) {
        return controlling.getError() * gain + constant;
      }else if (type == Type.PROPORTION_UNLIMITED || type == Type.PROPORTION_LIMITED) {
        return controlled.initialValue * gain * (controlling.get() / controlling.initialValue - 1) + constant;
      }else if(type == Type.ERROR_POWER_LIMITED){
        double d=controlling.getError(), q=Math.pow(Math.abs(d), gain);
        return (constant==0?1:constant) * ( (d<0) ? -1 : 1 ) * q;
      }else if(type == Type.ERROR_POSITIVE_LIMITED){
        return Math.max(0,controlling.getError() * gain + constant);
      }else if(type == Type.ERROR_NEGATIVE_LIMITED){
        return Math.min(0, controlling.getError() * gain + constant);
      }else if(type==Type.ERROR_GAIN_SIGMOID){
        return gain * MathExtra.sigmoid(controlling.getError() / constant);
      }else if(type==Type.ERROR_POWER_POSITIVE_LIMITED){
        double d=controlling.getError(), q=Math.pow(Math.abs(d), gain);
        return Math.max(0, (constant==0?1:constant) * ( (d<0) ? -1 : 1 ) * q );
      }else if(type==Type.ERROR_POWER_NEGATIVE_LIMITED){
        double d=controlling.getError(), q=Math.pow(Math.abs(d), gain);
        return Math.min(0, (constant==0?1:constant) * ( (d<0) ? -1 : 1 ) * q );
      }
    }else if(drugProperty != null){
      double binding = Current.body.blood.getDrugBinding(drugProperty);
      if (type == Type.ERROR_GAIN_LIMITED || type == Type.ERROR_GAIN_UNLIMITED) {
        return  binding * gain;
      }
      else if (type == Type.ERROR_POWER_LIMITED) {
        return Math.pow(binding, gain);
      }
    }
    throw new UnsupportedOperationException("Can't perform "+type+" with "+controllingVariable+" controlling "+controlledVariable);
  }

  /** Getters and setters */
  public VDouble getControlled() {    return controlled;  }
  public VDouble getControlling() {    return controlling;  }
  public void setControlling(VDouble controlling) {    this.controlling = controlling;  }
  public void setControlled(VDouble controlled) {    this.controlled = controlled;  }
  public double getGain() {    return gain;  }
  public void setGain(double gain) {    this.gain = gain;  }
  public double getRateFractionPerMinute() {    return rateFractionPerMinute;  }
  public void setRateFractionPerMinute(double rateFractionPerMinute) {    this.rateFractionPerMinute = rateFractionPerMinute;  }
  public Type getType() {    return type;  }
  public void setType(Type type) {    this.type = type;  }
  public void setDescription(String string) { this.description=string;  }
  public String getDescription(){ return description; }
  public String getDrugProperty(){ return drugProperty; }
  public void setControllingVariable(VisibleVariable controllingVariable) {
    this.controllingVariable = controllingVariable;
    if(controllingVariable!=null) {
      this.controlling = controllingVariable.node.getVDouble();
      drugProperty=null;
    }
  }
  public void setControlledVariable(VisibleVariable controlledVariable) {
    this.controlledVariable = controlledVariable;
    this.controlled=controlledVariable.node.getVDouble();
  }
  public void setDrugProperty(String s){
    drugProperty = s;
    if(drugProperty!=null){ controllingVariable = null; controlling = null; }
  }
  public VisibleVariable getControlledVariable() {    return controlledVariable;  }
  public VisibleVariable getControllingVariable() {    return controllingVariable;  }
  public void setConstant(double v){ constant = v;}
  public double getConstant(){ return constant; }
  /** Convert to a displayable string representing the values in this controller */
  public String toString(){
    return controlledVariable+"\t"+controllingVariable+"\t"+type+"\t"+gain+"\t"
        +rateFractionPerMinute+"\t"+constant+"\t\""+description+"\"";
  }

  /**
   * Refreshes the variable objects in this Controller with new ones from the
   * phic.gui.Variables class - useful when objects pointed to by the variables
   * have changed, e.g. after a load or reset.
   */
  public void replaceVariables() {
    setControlledVariable(Variables.forName(getControlledVariable().
                                              canonicalName));
    if(controllingVariable!=null)setControllingVariable(Variables.forName(getControllingVariable().
        canonicalName));
  }
}

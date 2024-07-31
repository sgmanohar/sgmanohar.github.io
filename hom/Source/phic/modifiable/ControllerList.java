package phic.modifiable;

import phic.common.IniReader;
import phic.common.Table;
import phic.gui.Variables;
import phic.gui.VisibleVariable;
import java.util.Vector;
import phic.common.VDouble;
import phic.common.Organ;
import java.io.Serializable;
import phic.drug.Drug;

/**
 * Class that reads a list of controllers from a text definition file,
 * and has the facility to execute all the controllers in one call.
 */
public class ControllerList {
  public ControllerList(String file) {
    Table r = new phic.common.Table(file, 7);
    controllers = readFromTable(r);
  }

  /**
   * Reads formatted controller data from a table.
   *
   * format:
   * Var1_name   Var2_name    ControllerTypeName   p1    p2    p3     commentText
   * e.g.:
   * heart.rate  RAP          ERROR_GAIN_LIMITED   0.1   0.04  0      "Chronotropic effect of venous filling pressure"
   *
   * For controller type ERROR_GAIN_..., p1 is the gain and p2 is the fractional
   * rate of change per minute. Comments should be in inverted commas if they
   * are multi-word.
   *
   * This allows controllers to be 'coupled' - i.e. if two controllers control
   * the same variable, and have the same rate parameter, their error values
   * will be added together before the error correction is performed. This allows
   * linear additive effects of multiple variables.
   *
   * @param t the Table from which the data is read
   * @return a Controller[] array of the controllers representing the specifications
   * in the given table.
   */
  Vector readFromTable(Table t) {
    int n = t.nRows;
    Vector v = new Vector();
    nextcontroller:for (int i = 0; i < n; i++) {
      VisibleVariable v1 = Variables.forName(t.getString(i, 0));
      Controller c;
      try {
        VisibleVariable v2 = Variables.forName(t.getString(i, 1));
        c = new Controller(v1, v2);
      }
      catch (IllegalArgumentException e) {
        String v2 = t.getString(i, 1);
        c = new Controller(v1, v2);
      }
      Controller.Type type = Controller.Type.forName(t.getString(i, 2));
      c.setType(type);
      if (type == Controller.Type.ERROR_GAIN_LIMITED ||
          type == Controller.Type.ERROR_GAIN_UNLIMITED
          || type == Controller.Type.PROPORTION_LIMITED ||
          type == Controller.Type.PROPORTION_UNLIMITED
          || type == Controller.Type.ERROR_POWER_LIMITED ||
          type == Controller.Type.ERROR_NEGATIVE_LIMITED
          || type == Controller.Type.ERROR_POSITIVE_LIMITED) {
        c.setGain(t.getDouble(i, 3));
        c.setRateFractionPerMinute(t.getDouble(i, 4));
        c.setConstant(t.getDouble(i, 5));
      }
      c.setDescription(t.getString(i, 6));
      //Now insert c into the list of controllers...
      //First check for multiple controllers that control the same variable -
      //these are handled differently
      for (int j = 0; j < v.size(); j++) {
        if (v.get(j) instanceof Controller) { //a controller for this var exists already as isolated controller
          Controller c2 = (Controller) v.get(j);
          if (c2.getControlled().equals(c.getControlled())
              && c2.getRateFractionPerMinute() == c.getRateFractionPerMinute()) {
            v.remove(c2);
            Vector nv = new Vector();
            nv.add(c2);
            nv.add(c);
            v.add(nv);
            continue nextcontroller;
          }
        }
        else if (v.get(j) instanceof Vector) { //list of controllers for this var already exists
          Vector ov = (Vector) v.get(j);
          Controller c2 = (Controller) ov.get(0);
          if (c2.getControlled().equals(c.getControlled())
              && c2.getRateFractionPerMinute() == c.getRateFractionPerMinute()) {
            ov.add(c);
            continue nextcontroller;
          }
        }
      } // next j
      v.add(c); //no other controllers for this var.
    }
    return v;
  }

  /** For each controller, ensure each variable is the correct item from Variables */
  public void replaceAllVariables(){
    for(int i=0;i<controllers.size();i++){
      Object o= controllers.get(i);
      if(o instanceof Controller){
        Controller c = (Controller) o;
        c.replaceVariables();
      }else if(o instanceof Vector) {
        Vector v=(Vector)o;
        for(int j=0;j<v.size();j++){
          ((Controller)v.get(j)).replaceVariables();
        }
      }else throw new IllegalStateException("Object "+o+" in 'controllers' is not a Vector or Controller");

    }
  }

  /**
   * This method is never actually used in normal running, but could be
   * invoked by the user to create new controllers not already in Phic.
   * @param controlled the VisibleVariable that is controlled
   * @param controlling the VisibleVariable that is controlling the controlled variable
   * @param type the Controller.Type indicating what control mechanism to use
   * @param rateFractionPerMinute the rate at which the value of the controlled
   * variable is moved towards the target value, as a fraction of the required
   * distance per minute of elapsed time.
   * @param gain the gain of the controller
   * @param description a description of the controller's physiological function
   */
  public void addNewController(VisibleVariable controlled,
                               VisibleVariable controlling,
                               Controller.Type type, double gain,
                               double rateFractionPerMinute,
                               double offset, String description) {
    Controller c = new Controller(controlled, controlling);
    c.setGain(gain);
    c.setType(type);
    c.setRateFractionPerMinute(rateFractionPerMinute);
    c.setConstant(offset);
    c.setDescription(description);
    controllers.add(c);
  }
  /**
   * Add a new controller, similar to addNewController, except parameters are
   * strings that represent names of variables or types of controller.
   *
   * If the controlling variable's name cannot be resolved as a variable name,
   * then a drug property with the given name is queried.
   */
  public void addNewControllerByName(String controlled,
                               String controlling,
                               String type, double gain,
                               double rateFractionPerMinute,
                               double offset, String description) {
    Controller c;
    try{
      c = new Controller(Variables.forName(controlled),
                                    Variables.forName(controlling));
    }catch(IllegalArgumentException e){
      c = new Controller(Variables.forName(controlled), controlling);
    }
    c.setGain(gain);
    c.setType(Controller.Type.forName(type));
    c.setRateFractionPerMinute(rateFractionPerMinute);
    c.setConstant(offset);
    c.setDescription(description);
    controllers.add(c);
  }

  /**
   * Return a vector of all the controllers that control a given variable.
   * Useful in determining causes of problems!
   */
  public Vector forControlledVariable(VisibleVariable v){
    return forVariable(v, true);
  }
  /**
   *  if Controlled is true, find controllers that are controlled by the
   * given variable, otherwise return controllers that control the given
   * variable.
   */
  public Vector forVariable(VisibleVariable v, boolean controlled){
    Vector result = new Vector();
    for(int i=0;i<controllers.size();i++){
      Object o = controllers.get(i);
      if(o instanceof Controller){
        Controller c = (Controller) o;
        if (controlled){ if(c.controlledVariable!=null &&
                            c.controlledVariable.equals(v)) result.add(c); }
        else { if(c.controllingVariable!=null &&
                  c.controllingVariable.equals(v)) result.add(c); }
      }else if(o instanceof Vector){
        Vector r = (Vector)o;
        for(int j=0;j<r.size();j++){
          Controller c=(Controller)r.get(j);
          if (controlled){ if(c.controlledVariable!=null &&
                              c.controlledVariable.equals(v)) result.add(c); }
          else { if(c.controllingVariable!=null &&
                    c.controllingVariable.equals(v)) result.add(c); }
        }
      }
    }
    return result;
  }

  /**
   * Return a vector of all the controllers that are controlled by a given variable.
   * Useful in determining effects of problems!
   */
  public Vector forControllingVariable(VisibleVariable v){
    return forVariable(v, false);
  }


  /**
   * Find a controller corresponding to the variable named 'target' being
   * controlled by a variable named 'controller'.
   * An IllegalArgumentException is thrown if no such controller exists.
   */
  public Controller forVariableNames(String target, String controller)
      throws IllegalArgumentException{
    VisibleVariable t=Variables.forName(target);
    VisibleVariable c=Variables.forName(controller);
    return forVariables(t,c);
  }
  /**
   * Find a controller corresponding to the variable 'target' being controlled
   * by the variable 'controller'.
   * An IllegalArgumentException is thrown if no such controller exists
   */
  public Controller forVariables(VisibleVariable target, VisibleVariable controller){
    for(int i=0;i<controllers.size();i++){
      Object o = controllers.get(i);
      if(o instanceof Controller){
        Controller c=(Controller)o;
        if(c.controlledVariable==target && c.controllingVariable==controller) return c;
      }else{
        Vector v=(Vector)o;
        for(int j=0;j<v.size();j++){
          Controller c=(Controller)v.get(j);
          if(c.controlledVariable==target && c.controllingVariable==controller) return c;
        }
      }
    }
    throw new IllegalArgumentException(target.longName + " does not control "+controller.longName);
  }

  /**
   * Find a controller with the given name (i.e. description), using
   * equalsIgnoreCase.
   */
  public Controller findName(String s){
    for(int i=0;i<controllers.size();i++){
      Object o = controllers.get(i);
      if(o instanceof Controller){
        if(((Controller)o).getDescription().equalsIgnoreCase(s)) return (Controller) o;
      }else if(o instanceof Vector){
        Vector v=(Vector)o;
        for(int j=0;j<v.size();j++){
          if(((Controller)v.get(j)).getDescription().equalsIgnoreCase(s)) return (Controller) v.get(j);
        }
      }
    }
    throw new IllegalArgumentException("No controllers named "+s);
  }

  /** The List of controllers */
  private Vector controllers;

  /**
   * Return an array of all the controllers. note this does not give information
   * on which controllers are coupled to each other.
   * (useful for a tablemodel view of all controllers).
   */
  public Controller[] getControllers() {
    Vector v = new Vector();
    for (int i = 0; i < controllers.size(); i++) {
      Object o = controllers.get(i);
      if (o instanceof Controller) {
        v.add(o);
      }
      else if (o instanceof Vector) {
        Vector v2 = (Vector) o;
        for (int j = 0; j < v2.size(); j++) {
          v.add(v2.get(j));
        }
      }
    }
    return (Controller[]) v.toArray(new Controller[v.size()]);
  }

  /**
   * Calls calculate() on each controller with the given elapsedTime.
   * They are calculated in sequential order as defined in the text file.
   */
  public void calculateAll(double elapsedTimeSecs) {
    for (int i = 0; i < controllers.size(); i++) {
      Object o = controllers.get(i);
      if (o instanceof Controller) {
        Controller c = (Controller) o;
        c.calculate(elapsedTimeSecs);
      }
      else if (o instanceof Vector) {
        //Multiple controllers for one variable: add the errors up
        //in order to calculate the target value.
        //Note that the calculate() method is not called in this case
        Vector v = (Vector) o;
        Controller c0 = (Controller) v.get(0);
        VDouble controlled = c0.getControlled();
        double target = controlled.initialValue;
        boolean limit=false;
        for (int j = 0; j < v.size(); j++) {
          Controller c = (Controller) v.get(j);
          double diff = c.getDesiredError();

          //limit this target value if required.
          if (c.getType() == Controller.Type.ERROR_GAIN_LIMITED
              || c.getType() == Controller.Type.PROPORTION_LIMITED
              || c.getType() == Controller.Type.ERROR_NEGATIVE_LIMITED
              || c.getType() == Controller.Type.ERROR_POSITIVE_LIMITED
              || c.getType() == Controller.Type.ERROR_POWER_NEGATIVE_LIMITED
              || c.getType() == Controller.Type.ERROR_POWER_POSITIVE_LIMITED
              || c.getType() == Controller.Type.ERROR_POWER_LIMITED) {
            diff = controlled.limitValue(target+diff)-target;
            limit=true;
          }
          target = target + diff;
        }
        if(limit) target=controlled.limitValue(target);
        controlled.lowPass(target,
                           Organ.fractionDecayPerMinute(c0.
            getRateFractionPerMinute(), elapsedTimeSecs));
      }
    }
  }

}

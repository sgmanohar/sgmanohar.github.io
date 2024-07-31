package evaluator;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class representing a variable object within an expression.
 * It accesses a variable either as an 'internal' or 'external' variable.
 * Internal variables are stored in a static table within this class. External
 * variables are found using the ObjectPath, and can access fields and
 * methods in other classes.
 */
public class Variable
    extends ObjectPath
    implements Operator {
  static final int INTERNAL = 0, EXTERNAL = 1;
  int type;
  String name;
  public static Hashtable table = new Hashtable();

  /** Set a variable in the table */
  public static void set(String name, Object value) {
    table.put(name, value);
  }

  /** Get a variable's value from the table */
  public static Object get(String name) {
    Object d = table.get(name);
    if (d != null) {
      return d;
    }
    else {
      return new Double(Double.NaN);
    }
  }

  public static void remove(String name){
    table.remove(name);
  }

  public final int getStackCount() {
    return 1;
  } //pushes one number

  public Variable(String name) throws ParseException {
    //is it in the list or not?
    this.name = name;
    /* //old version, prioritises internal variables
       if(table.get(name)!=null){
        type=INTERNAL;
       }else{
        createPath(name);
                        type=EXTERNAL;
                       }*/
    // new version: prioritises external variables
    try {
      createPath(name);
      type = EXTERNAL;
    }
    catch (Exception ex) {
      type = INTERNAL;
    }
    if (member == null) {
      type = INTERNAL;
    }
  }

  //get the value of the member and push onto stack
  public void doStackOp(Stack stack) throws MathException, StackException {
    Object ans = null;
    switch (type) {
      case INTERNAL: //to be read from the table
        ans = table.get(name);
        if (ans == null) {
          if(namespaceLookup!=null) ans = namespaceLookup.getVariableValue(name);
          if(ans==null){
            throw new MathException(stack,"Variable '" + name +"' has not been assigned.");
          }
        }
        break;

      case EXTERNAL:
        if (member instanceof Method) { //check for double method without params
          Method method = (Method) member;
          Class[] paramtypes = method.getParameterTypes();
          if (paramtypes.length != 0) {
            throw new MathException(stack, method + " takes parameters");
          }
          try {
            Object d = method.invoke(object, null);
            ans = d;
          }
          catch (Exception e) {
            throw new MathException(stack, method + " could not be invoked" +
                                    " because of " + e.getMessage());
          }
        }
        else { //check for field
          Field field = (Field) member;
          if (field.getType() != Double.TYPE) { //handle objects wrapping doubles, e.g. Variable
            Method test = null;
            try {
              test = field.getType().getMethod("get", null); //throws exception if no get method
              ans = test.invoke(field.get(object), null); //throws exception if get method is not a double
            }
            catch (Exception e) {
              try {
                ans = field.get(object);
              }
              catch (Exception ex) {
                throw new MathException(stack,
                                        field + " could not be read because of "
                                        + ex.getMessage());
              }
            }
          }
          else {
            try {
              ans = new Double(field.getDouble(object));
            }
            catch (Exception e) {
              throw new MathException(stack, field + " could not be read " +
                                      "because of " + e.getMessage());
            }
          }
        }
    }
    stack.push(ans);
  }
  public static VariableLookup namespaceLookup = null;
}

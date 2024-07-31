/**
 * This extension of EvaluatorConsole allows the interpreter to access
 * Phic objects.
 */
package phic.gui;
import evaluator.*;
import phic.Current;
import phic.common.Container;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.event.*;
import phic.common.Clock;
import javax.swing.KeyStroke;
import java.util.Hashtable;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import phic.common.VDouble;
import phic.common.Quantity;
import phic.common.TimedEvent;
import sanjay.common.Utils;
import phic.drug.Pharmacy;
import phic.drug.NoSuchDrugException;
import phic.common.LifeSupport;
import javax.swing.JPopupMenu;
import javax.swing.Action;

/**
 * An extension of EvaluatorConsole that allows printing, allows Phic Variables
 * to be looked up by setting the object roots for Body and Environment,
 * allows the variables to be looked up in short form directly from the
 * phic.Variables list, and consumes key events that are not supposed to invoke
 * their Phic shortcuts when typed into a console.
 */
public class PhicEvaluator extends EvaluatorConsole implements VariableLookup{
  public PhicEvaluator(){
    restoreObjectRoots();
    evaluator.ObjectPath.setPackage(new String[]{"java.lang","phic",
      "phic.common","phic.gui","phic.drug","phic.doctor",
      "java.awt","javax.swing","java.util"
    });
    this.setFont(new Font("Monospaced",getFont().getStyle(),getFont().getSize()));
    addKeyListener(kl);
    evaluator.Variable.namespaceLookup = this;
    addMouseListener(ml);
    Action[] a=getActions();
    for(int i=0;i<a.length;i++){
      Object t=a[i].getValue(Action.NAME);
      if(t!=null && t.toString().indexOf("clipboard") >= 0)
        menu.add(a[i]);
    }
  }


  /**
   * Lookup objects from the Phic Variables list, on behalf of the
   * evaluator.Variables class.
   * Also allows special values of variables.
   */
  public Object getVariableValue(String variableName){
    if(variableName.equalsIgnoreCase("Time")){
      return Current.body.getClock().getTimeString(Clock.DATETIME);
    }
    try {
      return new Double(Variables.forName(variableName).node.doubleGetVal());
    }catch(IllegalArgumentException e){
      Object val = Variable.table.get(variableName);
      if(val!=null)return val;
      //e.printStackTrace();
      throw new EquationException("Variable "+variableName+" not found");
    }
  }

  /**
   * This consumes events that, when typed in the console, should not invoke
   * their default actions - e.g. P and R, which would otherwise pause or reset
   * the engine.
   */
  KeyListener kl = new KeyAdapter(){
    public void keyPressed(KeyEvent e){
      if(!e.isActionKey() && e.getModifiers()==0 && e.getKeyChar()>=' ' && e.getKeyChar()<'\u007f')
        e.consume();
    }
  };
  /**
   * This creates a menu on right-click
   */
  MouseListener ml = new MouseAdapter(){
    public void mousePressed(MouseEvent e){  if(e.isPopupTrigger()) popupmenu(e.getPoint()); }
    public void mouseReleased(MouseEvent e){ if(e.isPopupTrigger()) popupmenu(e.getPoint()); }
  };
  void popupmenu(Point x){ menu.show(this,x.x,x.y); }
  JPopupMenu menu = new JPopupMenu();


  public void restoreObjectRoots(){
    try{
      evaluator.ObjectPath.setRoot(new Object[]{
        Current.body,
        Current.environment,
        Class.forName("java.lang.Math"),
        evaluator.MathExtra.class,
        Current.person,
        this
      });
    } catch(ClassNotFoundException e){
      e.printStackTrace();
    }
  }

  /**
   *  Help for the console: outputs a list of the methods and fields
   * contained in the given object.
   */

  public String help(Object object){
    Class c = object.getClass();
    StringBuffer s=new StringBuffer(c.getName());
    s.append('{');
    Method[] m=c.getMethods();
    for (int i = 0; i < m.length;i++) {
      s.append(m[i]);
      if(i<m.length-1) s.append(',');
    }
    s.append("}\n{");
    Field[] f=c.getFields();
    for(int i=0;i<f.length;i++){
      s.append(f[i]);
      if(i<f.length-1) s.append(',');
    }
    s.append('}');
    return s.toString();
  }

  /**
   * display the variable info dialog for the given VisibleVariable or Node.
   *
   */
  public String info(Object o){
    VariablePropertiesDialog d=new VariablePropertiesDialog();
    if(o instanceof VisibleVariable){
      d.setVariable((VisibleVariable)o);
      d.show();
      return "";
    }else if(o instanceof Node){
      d.setVariable(Variables.forNode((Node)o));
      d.show();
      return "";
    }else if(o instanceof String){
      d.setVariable(Variables.forName((String)o));
      d.show();
      return "";
    }
    return "Not a variable";
  }
  /**
   * same as 'help()': returns a list of members of the given object
   *
   */
  public String dir(Object o){return help(o);}
  /**
   * Show the list of variables that are stored directly under an object
   * in the variable hierarchy - e.g. dump(CVS.heart) gives the values
   * in the heart object.
   *
   * @param o the Object to query
   * @return String - the list of values contained in object o.
   */
  public String dump(Object o){
    StringBuffer s=new StringBuffer();
    int fieldw=25;
    Field[] f=o.getClass().getFields();
    for(int i=0;i<f.length;i++){
      int w=0;
      if(VDouble.class.isAssignableFrom( f[i].getType() ))  {
        try {
          VDouble vd = (VDouble) f[i].get(o);
          try{ VisibleVariable vv=Variables.forVDouble(vd); { s.append(vv.shortName+": "); w+=vv.shortName.length()+2; }
          }catch(IllegalArgumentException x){}
          String val = vd.formatValue(true,true);
          String fn=f[i].getName();
          s.append(fn); w+=fn.length();
          s.append(Utils.strstr(" ", fieldw-w)); s.append("\t= "+val+"\n");
        }catch (Exception ex) {}
      }
      if(f[i].getType()==Double.TYPE){
        try{
          double d=f[i].getDouble(o);
          String fn=f[i].getName();
          s.append(fn); w+=fn.length();
          s.append(Utils.strstr(" ", fieldw-w)); s.append("\t= "+Quantity.toString(d)+"\n");
        }catch (Exception ex) {}
      }
    }
    return s.toString();
  }

  /**
   * Create a timed event using TimedEvent -
   * this executes the command "command" as a script language command
   * after the time interval secondsDelay of body time has elapsed.
   */
  public void afterSeconds(double secondsDelay, String command){
    new TimedEvent(secondsDelay, command);
  }

  /** Create a drug from pharmacy using Pharmacy.dispense() */
  public Container getDrug(String s) throws NoSuchDrugException { return Pharmacy.dispense(s); }

  /** Return the full name of a variable */
  public String fullName(String var){
    Node n;
    try{VisibleVariable vv=Variables.forName(var); n= vv.node; }
    catch(Exception e){
      n=Node.findNodeByName(var);
    }
    return n.canonicalNameReplaced();
  }

  /** Clamp value of a variable, using the body's LifeSupport object */
  public String clamp(String varName, double value){
    LifeSupport ls = Current.environment.getVariableClamps();
    VisibleVariable vv = Variables.forName(varName);
    ls.setFudgeVariable(vv, true);
    ls.setFudgeValue(vv,value);
    return vv.longName+" clamped at "+vv.formatValue(value, true, false);
  }
  public void unclamp(String varName){
    LifeSupport ls = Current.environment.getVariableClamps();
    VisibleVariable vv = Variables.forName(varName);
    ls.setFudgeVariable(vv, false);
  }

  public void set(String vname,Object value) throws EquationException{
    VisibleVariable v;
    try{
      v=Variables.forName(vname);
    } catch(IllegalArgumentException e1){
      try{
        v=Variables.forName("body."+vname);
      } catch(IllegalArgumentException e2){
        try{
          v=Variables.forName("environment."+vname);
        } catch(IllegalArgumentException e3){
          v=null;
        }
      }
    }
    Node node;
    if(v==null){
      try{
        node=Node.findNodeByName(vname);
      }catch (IllegalArgumentException x){
        int idot=vname.lastIndexOf('.');
        try{ // hack for allowing editing the initial values of visible variables
          VisibleVariable ivv=Variables.forName(vname.substring(0,idot));
          String iname=vname.substring(idot+1);
          double dval=((Double)value).doubleValue();
          VDouble vd=ivv.node.getVDouble();
          if(iname.equalsIgnoreCase("initialValue")){
            vd.initialValue=dval; return;
          }else if(iname.equalsIgnoreCase("maximum")){
            vd.maximum=dval; return;
          }else if(iname.equalsIgnoreCase("minimum")){
            vd.minimum=dval; return;
          } throw new IllegalArgumentException("");
        }catch(Exception x2){ //  Last resort!!!!
          Variable.set(vname, value);
          return;
          //throw new EquationException("Variable "+vname+" not found");
        }
      }
    } else { node = v.node; }

    int type=node.getType();
    if(type==Node.DOUBLE){
      if(value instanceof Double)
        node.doubleSetVal(((Double)value).doubleValue());
      else
        node.doubleSetVal(Double.parseDouble(value.toString()));
    } else if(type==Node.CONTAINER){
      if(value instanceof Container){
        ((Container)node.objectGetVal()).set((Container)value);
      } else
        throw new EquationException("Result '"+value.toString()+
          "' is not a Container");
    } else if(type==Node.BOOLEAN){
      if(value instanceof Boolean)
        node.booleanSetVal(((Boolean)value).booleanValue());
      else
        throw new EquationException("Result '"+value.toString()+
          "' is not a Boolean");
    } else
      throw new EquationException("Type mismatch between "+value.toString()+
        " and "+v.node.canonicalName());
  }

}

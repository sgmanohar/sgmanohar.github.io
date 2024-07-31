package evaluator;
import phic.gui.*;
import phic.common.*;
import java.io.Serializable;
/**
 * This class represents a generalised expression that may be an assignment.
 *
 * A statement that either is a simple expression, or has the form
 * 'variable = expression'.
 * Evaluating this statement evaluates the expression, and if necessary,
 * performs the required assignment
 */

public class Statement implements Serializable {
  String string;
  transient Expression expr;
  String left=null;
  public Statement(String s) throws ParseException{
    this.string=s;
    compile();
  }

  /** store string as an expression */
  void compile(){
    strip();
    String s=string;
    int eq=s.indexOf('=');
    left=null;
    if(eq>=0){
      left=s.substring(0,eq).trim();
      String right=s.substring(eq+1).trim();
      s=right;
    }
    boolean needDummyVar = (left!=null) && (tryFindVariableByName(left)==null)
        && (Variable.table.get(left)==null);
    if(needDummyVar)       Variable.set(left,"");
    expr=new Expression(s);
    if(needDummyVar)       Variable.remove(left);
  }

  /** evaluate compiled expression */
  public Object evaluate() throws MathException, StackException{
    if(expr==null) compile();
    Object ans=expr.evaluate();
    if(left!=null){
      set(left, ans);
    }
    return ans;
  }



  VisibleVariable tryFindVariableByName(String vname) {
    VisibleVariable v;
    try {
      v = Variables.forName(vname);
    }
    catch (IllegalArgumentException e1) {
      try {
        v = Variables.forName("body." + vname);
      }
      catch (IllegalArgumentException e2) {
        try {
          v = Variables.forName("environment." + vname);
        }
        catch (IllegalArgumentException e3) {
          v = null;
        }
      }
    }
    return v;
  }
    public void set(String vname , Object value) throws EquationException{
      VisibleVariable v=tryFindVariableByName(vname);
      Node n;
      if(v==null){
        try{
          n = Node.findNodeByName(vname);
        }catch(IllegalArgumentException x){
          Variable.set(vname, value);
          return;
          //throw new EquationException("Variable "+vname+" not found");
        }
      }else n = v.node;
      int type=n.getType();
      if(type==Node.DOUBLE){
        if(value instanceof Double)
          n.doubleSetVal(((Double)value).doubleValue());
          else n.doubleSetVal(Double.parseDouble(value.toString()));
      }else if(type==Node.CONTAINER){
        if(value instanceof Container){
          ((Container)n.objectGetVal()).set((Container)value);
        }else throw new EquationException("Result '"+value.toString()+"' is not a Container");
      }else if(type==Node.BOOLEAN){
        if(value instanceof Boolean)
          n.booleanSetVal(((Boolean)value).booleanValue());
        else throw new EquationException("Result '"+value.toString()+"' is not a Boolean");
      }else throw new EquationException("Type mismatch between "+value.toString()+
          " and "+v.node.canonicalName());
    }
    String makeword(String s){
      if(s.length()==0)return null;
      String n=new String();
      char c=s.charAt(0);
      if(Character.isJavaIdentifierStart(c))n+=c;
      else return null;
      for(int i=1;i<s.length();i++){
        c=s.charAt(i);
        if(Character.isIdentifierIgnorable(c))continue;
        if(Character.isJavaIdentifierPart(c) || c=='.')n+=c;
        else return null;
      }
      return n;
    }
	public String getDefinition(){
		return string;
  }
    /**
     * Remove surrounding white space, carriage returns, and semicolons
     * from the definition string
     */
    public void strip(){
      String oldString = string;
      string=string.trim();
      if(string.startsWith(";") || string.startsWith("\n")) string=string.substring(1);
      if(string.endsWith(";")   || string.endsWith("\n"  )) string=string.substring(0,string.length()-1);
      if(!string.equals(oldString)) strip();
    }
}

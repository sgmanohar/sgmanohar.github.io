package evaluator;
import java.lang.ref.*;
/**
 * Operator that accesses a method, with the given number of parameters.
 */

public class MethodAccessOperator implements Operator {
  public MethodAccessOperator(String methodName, int parameters) {
    this.parameters = parameters;
    this.methodName = methodName;
  }
  int parameters;
  String methodName;
  public void doStackOp(Stack stack) throws MathException, StackException {
    Object[] p = new Object[parameters];
    Class[] c = new Class[parameters];
    for(int i=parameters-1;i>=0;i--) { p[i]=stack.pop(); c[i]=p[i].getClass(); }
    Object o = stack.pop();
    try{
			Object result = o.getClass().getMethod(methodName,c).invoke(o,p);
      stack.push(result);
		}catch(Exception e){ //try replacing Double with Double.TYPE
      try{
        for(int j=0;j<parameters;j++){
          if(c[j]==Double.class) c[j]=Double.TYPE;
        }
        Object result=o.getClass().getMethod(methodName,c).invoke(o,p);
				stack.push(result);
			}catch(Exception ex){
				throw new TypeException(stack,"Cannot invoke method '"+methodName+"(<<"+
				parameters+">>)' on object '"+o.toString()+"' :\n"+
				ex.toString());
			}
    }
  }

  /** The invocation consumes parameters+1 elements from the stack, and returns one. */
  public int getStackCount() {
    return -parameters;
  }

}
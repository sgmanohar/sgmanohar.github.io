package evaluator;

/**
 * This operator accesses a field of the given name in an object on the stack.
 */

public class FieldAccessOperator implements Operator {
  public FieldAccessOperator(String fieldName) {
  this.fieldName = fieldName;
  }
    String fieldName;
  public void doStackOp(Stack stack) throws MathException, StackException {
    Object o = stack.pop();
    try{
			Object result=o.getClass().getField(fieldName).get(o);
      /** Begin PHIC addition for Variable
       * Wrap VDoubles as Doubles. */
      if(result instanceof phic.common.Variable){
        result=new Double(((phic.common.Variable)result).get());
      }
      /** End PHIC addition */
      stack.push(result);
		}catch(Exception x){
      throw new TypeException(stack, "Cannot access field '"+fieldName+
        "' in object '"+o.toString()+"' :\n" + x.toString());
    }
  }
  public int getStackCount() {
    return 0;
  }

}
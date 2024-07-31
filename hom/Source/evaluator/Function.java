package evaluator;
import java.lang.reflect.*;

public class Function extends ObjectPath implements Operator{
	String function;
    int parameters;
		//Constructor takes the java path to the method to invoke
	public Function(String function, int parameters) throws ParseException{
		this.function=function;
        this.parameters = parameters;
    this.nParams=parameters;
		createPath(function);
	}

		//functions pop one then push one (=0)
	public int getStackCount(){return 1-parameters;}

		//invoke the method using the top value on stack as parameter
	public void doStackOp(Stack stack) throws MathException, StackException{
		Object ans = null;
		if(!(member instanceof Method))
		  throw new MathException(stack,member+" is not a method");
		Method method=(Method)member;
		Class[] paramtypes=method.getParameterTypes();
    Object[] o = new Object[parameters];
    for(int i=parameters-1;i>=0;i--) o[i]=stack.pop();
		if(paramtypes.length!=parameters)
		  throw new MathException(stack,method+" does not take "+parameters+" parameter(s).");
		try{
			ans=method.invoke( object, o );
		}catch (Exception e){
                  e.printStackTrace();
			throw new MathException(stack, method+" could not be invoked"+
			  " because of "+e.getMessage() );
		}
		stack.push(ans);
	}
}

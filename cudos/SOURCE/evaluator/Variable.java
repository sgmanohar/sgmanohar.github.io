package evaluator;

import java.util.*;
import java.lang.reflect.*;
import unit.*;

public class Variable extends ObjectPath implements Operator{
	static final int INTERNAL=0,EXTERNAL=1;
	int type;
	String name;
	Unit unit = null;
	public static Hashtable table = new Hashtable();

		//Set a variable in the table
	public static void set(String name, double value){
		table.put(name,new Double(value));
	}
		//Get a variable's value from the table
	public static double get(String name){
		Double d=(Double)table.get(name);
		if(d!=null)return d.doubleValue();
		else return Double.NaN;
	}

	public final int getStackCount(){return 1;}	//pushes one number
	public Variable(String name) throws ParseException{
		//is it in the list or not?
		this.name=name;
		if(table.get(name)!=null){
			type=INTERNAL;
			unit = new Unit("");
		}else{
			type=EXTERNAL;
			try{
				createPath(name);
			}catch(ParseException e){
				throw new NoSuchVariableException(name);
			}
			//work out the unit represented by this variable
			try{
				Object o = get();
				if(o instanceof Variable){
					unit = ((Variable)o).getUnit();
				}else if(o instanceof Number){
					unit = new Unit("");
				}
			}catch(Exception e){
				if(getType().isAssignableFrom(Number.class))
					unit = new Unit("");
				else unit = null;
			}
		}
	}

	public Unit getUnit(){return unit;}

		//get the value of the member and push onto stack
	public void doStackOp(Stack stack) throws MathException, StackException{
		double ans=Double.NaN;
		switch(type){
			case INTERNAL:		//to be read from the table
			try{ans=((Double)table.get(name)).doubleValue();}
			catch(NullPointerException e){ throw new MathException(stack, "Variable has been removed: "+name); }
			break;

			case EXTERNAL:
			if(member instanceof Method){	//check for double method without params
				Method method=(Method)member;
				if(method.getReturnType()!=Double.TYPE)
					throw new MathException(stack, method+" is not a double");
				Class[] paramtypes = method.getParameterTypes();
				if(paramtypes.length!=0)
					throw new MathException(stack, method+" takes parameters");
				try{
					Double d=(Double)method.invoke(object, null);
					ans=d.doubleValue();
				}catch(Exception e){
					throw new MathException(stack, method+" could not be invoked"+
						" because of "+e.getMessage() );
				}
			}else if(member instanceof Field){		//check for double field
				Field field=(Field)member;
				if(field.getType()!=Double.TYPE)
					throw new MathException(stack, field+" is not a double");
				try{
					ans=field.getDouble(object);
				}catch(Exception e){
					throw new MathException(stack, field+" could not be read "+
						"because of "+e.getMessage() );
				}
			}else if(member instanceof PathHelper){
				ans=((PathHelper)member).getObjectValue(object);
			}else ans=Double.NaN;
		}
		stack.push(ans);
	}
}
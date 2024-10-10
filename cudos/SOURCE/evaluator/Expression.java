package evaluator;

import unit.*;

/*  An object beloning to the class Expr is a mathematical expression that
		can involve:

						-- real numbers such as 2.7, 3, and 12.7e-12
						-- arithmetic operators  +,  -,  *,  /,  and  ^ , where
							 the last of these represents raising to a power
						-- the mathematical functions
						-- parentheses

		 Some examples would be:   sin(2.3*x-7.1) - cos(7.1*x-2.3)
															 42
															 exp(x^2) - 1

		 The trigonometric funtions work with radians, not degrees.  The parameter
		 of a function must be enclosed in parentheses, so that "sin x" is NOT allowed.

		 An Expr is constructed from a String that contains its definition.  If an
		 error is found in this definition, the constructor will throw a
		 ParseException with a message that describes the error and
		 the position of the error in the string.  After an Expr has been
		 constructed, its defining string can ge retrieved using the
		 method getDefinition().

		 The main operation on an Expr object is to find its value.
		 The value is computed by the value() method.
		 If the expression is undefined, then the
		 number returned by value() method will be the special "non-a-number number",
		 Double.NaN.  (The boolean-valued function Double.isNaN(v) can be used to
		 test whether the double value v is the special NaN value.  For technical
		 reasons, you can't just use the == operator to test for this value.)

		 David Eck, 8 October 1998
*/

/*  Extended September 2001 Sanjay Manohar
		1.	Removed all functions.
		2.	Variable table lookup
	The variable names used with Expression are now stored in a static lookup
	table in Variable, and can be modified using Variable.set(name, value) and
	Variable.get(name).
		3.	Object path lookup
	Variable names and functions can consist of Java object paths. The variable
	and function names are searched using reflection, firstly starting from a
	set of objects defined by objectPath.setRoot(Object[]), and then from a
	static context. For example, 'Math.exp(1)', or 'Double.NaN' will be
	parsed normally, unless one of the root objects contains a member field
	called 'Math' or 'Double' with the appropriate function or field defined
	in it.
*/

import java.lang.reflect.*;

public class Expression {

	 //----------------- public interface ---------------------------------------

	 public Expression(String definition) throws ParseException{
				 // Construct an expression, given its definition as a string.
				 // This will throw an IllegalArgumentException if the string
				 // does not contain a legal expression.
			try{parse(definition);}
			catch(ParseException e){	//inform the user of errors in parsing
				e.position=pos;			//fill in location in text
				e.parseString=definition;	//fill in actual text
				throw e;			//rethrow back to user
			}
	 }

	 public double value() throws StackException, MathException{
				 // Return the value of this expression, when the variable x
				 // has the specified value.  If the expression is undefined
				 // for the specified value of x, then Double.NaN is returned.
			return eval();
	 }

	 public String getDefinition() {
				 // Return the original definition string of this expression.  This
				 // is the same string that was provided in the constructor.
			return definition;
	 }
	 public String toString(){ return definition; }

	 public Unit getUnit(){
		 return unit;
	 }
	 //------------------- private implementation details ----------------------------------



	 private String definition;  // The original definition of the expression,
															 // as passed to the constructor.

	 private Operator[] code;      // A translated version of the expression, containing
															 //   stack operations that compute the value of the expression.

	 private Stack stack;        // A stack to be used during the evaluation of the expression.

	 private Unit unit;







	 private double eval() throws MathException, StackException{
				 // evaluate this expression for this value of the variable
			double ans=Double.NaN;
			stack.reset();
			for (int i = 0; i < codeSize; i++) {
	try{
					code[i].doStackOp(stack);
	}catch (MathException e) {	//for any math errors
					e.code=code;		//fill in the code
		e.location=i;		//and the location within the code
		throw e;			//and rethrow to user
	}
			}
			ans=stack.pop();
			return ans;
	 }


	 private int pos = 0, codeSize = 0;  // data for use during parsing







	 private void parse(String definition) throws ParseException {
							// Parse the definition and produce all
							// the data that represents the expression
							// internally;  can throw IllegalArgumentException
			if (definition == null || definition.trim().equals(""))
				 throw new ParseException("No data provided to Expression constructor");
			this.definition = definition;
			code = new Operator[definition.length() * 2];
			unit = parseExpression();
			skip();
			if (next() != 0)
				 throw new ParseException("Extra data found after the end of the expression.");
			//int stackSize = computeStackUsage();
			stack = new Stack();
			Operator[] c = new Operator[codeSize];
			System.arraycopy(code,0,c,0,codeSize);
			code = c;
	 }

	 private char next() {  // return next char in data or 0 if data is all used up
			if (pos >= definition.length())
					return 0;
			else
				 return definition.charAt(pos);
	 }

	 private void skip() {  // skip over white space in data
			while(Character.isSpaceChar(next()))
				 pos++;
	 }






				// remaining routines do a standard recursive parse of the expression

	 private Unit parseExpression() throws ParseException{

			//for the first term of expression
			boolean neg = false;
			skip();
			if (next() == '+' || next() == '-') {
				 neg = (next() == '-');
				 pos++;
				 skip();
			}
			Unit firstUnit = parseTerm();  // note the unit of the term

			if (neg)
				 code[codeSize++] = new UnaryMinus();
			skip();

			//for each subsequent term
			while (next() == '+' || next() == '-') {
				 char op = next();
				 pos++;
				 Unit unit = parseTerm();

				 //check units of this term match previous terms (as long
				 //as there was no ^
				 if( unit!=null && firstUnit!=null ){
					 if( !unit.equalsInBasis(firstUnit) ){
						 throw new ParseException("Unit of term '"+
							 unit + "'does not match unit '"+ firstUnit + "'");
					 }
					 if(!unit.equals(firstUnit)){
						 //convert the term's value into the units of the first term
						 code[codeSize++] = new Value( unit.getConversionTo(firstUnit) );
						 code[codeSize++] = new BinaryOperator(BinaryOperator.MULTIPLY);
					 }
				 }

				 // add or subtract the term
				 code[codeSize++] = (op == '+')
											? new BinaryOperator(BinaryOperator.PLUS)
											: new BinaryOperator(BinaryOperator.MINUS);
				 skip();
			}

			return firstUnit;
	 }

	 private Unit parseTerm() throws ParseException{
			Unit u = new Unit("");
			Unit v = parseFactor();
			if(v==null) u=null;
			else u.multiplyBy(v,1);

			skip();

			while (next() == '*' || next() == '/') {
				 char op = next();
				 pos++;
				 v = parseFactor();
				 code[codeSize++] = (op == '*')
		? new BinaryOperator(BinaryOperator.MULTIPLY)
		: new BinaryOperator(BinaryOperator.DIVIDE);

				 if(v==null)u=null;
				 else if(u!=null) u.multiplyBy(v,
					 (op=='*')?1 : -1);

				 skip();
			}
			return u;
	 }

	 private Unit parseFactor() throws ParseException{
			Unit u;
			u = parsePrimary();
			skip();
			while (next() == '^') {
				 u = null;
				 pos++;
				 parsePrimary();
				 code[codeSize++] = new BinaryOperator(BinaryOperator.POWER);
				 skip();
			}
			return u;
	 }

	 private Unit parsePrimary() throws ParseException{
			Unit u = null;

			skip();
			char ch = next();

			if (Character.isLetter(ch))
				 return parseWord();

			else if (Character.isDigit(ch) || ch == '.')
				 return parseNumber();

			else if (ch == '(') {
				 pos++;
				 u = parseExpression();
				 skip();
				 if (next() != ')')
						throw new ParseException("Exprected a right parenthesis.");
				 pos++;
				 return u;
			}

			else if (ch == ')')
				 throw new ParseException("Unmatched right parenthesis.");

			else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^')
				 throw new ParseException("Operator '" + ch + "' found in an unexpected position.");

			else if (ch == 0)
				 throw new ParseException("Unexpected end of data in the middle of an expression.");

			else
				 throw new ParseException("Illegal character '" + ch + "' found in data.");
	 }

		private Unit parseWord() throws ParseException{
			 String w = "";
			 while (Character.isLetterOrDigit(next()) || next()=='.') {
					w += next();
					pos++;
			 }
//     w = w.toLowerCase();


	skip();
	if(next() == '('){
		//found a function
		pos++;	//sgm
		skip();
		int parameters=0;
		while(next()!=')'){
			Unit z = parseExpression();
			parameters++;
			skip();
			if(next()==',')pos++;
			else if(next()!=')')
				throw new ParseException("Missing right parenthesis after parameter of function '"+w+"'.");
		}
		if(next() != ')')
			throw new ParseException("Missing right parenthesis after parameter of function '"+w+"'.");
		pos++;
		Function f = new Function(w,parameters);
		code[codeSize++] = f;
		return f.getUnit();
	} else {
		//found a variable
		Variable v = new Variable(w);
		code[codeSize++] = v;
		return v.getUnit();
	}


		}

		private Unit parseNumber() throws ParseException{
			 String w = "";
			 while (Character.isDigit(next())) {
					w += next();
					pos++;
			 }
			 if (next() == '.') {
					w += next();
					pos++;
					while (Character.isDigit(next())) {
						 w += next();
						 pos++;
					}
			 }
			 if (w.equals("."))
					throw new ParseException("Illegal number found, consisting of decimal point only.");
			 if (next() == 'E' || next() == 'e') {
					w += next();
					pos++;
					if (next() == '+' || next() == '-') {
						 w += next();
						 pos++;
					}
					if (! Character.isDigit(next()))
						 throw new ParseException("Illegal number found, with no digits in its exponent.");
					while (Character.isDigit(next())) {
						 w += next();
						 pos++;
					}
			 }
			 double d = Double.NaN;
			 try {
					d = Double.valueOf(w).doubleValue();
			 }
			 catch (Exception e) {
			 }
			 if (Double.isNaN(d))
					throw new ParseException("Illegal number '" + w + "' found in data.");
			 code[codeSize++] = new Value(d);

			 return new Unit("");
		}


} // end class Expr






//Stack

class Stack{
	static final int MAX=100;
	double[] s=new double[MAX];
	int top=0;
	public double pop() throws StackException{
		if(top<=0)throw new StackException();
		return s[--top];
	}
	public void push(double d) throws StackException{
		if(top==MAX)throw new StackException();
		s[top++]=d;
	}
	public void reset(){
		top=0;
	}
	public String toString(){
		String str="";
		for(int i=top;i>=0;i--)str += String.valueOf(s[i]) + '\n';
		return str;
	}
}






// classes of operators
//  sgm



interface Operator{
	public void doStackOp(Stack stack) throws MathException,StackException;
	public int getStackCount();
}



class BinaryOperator implements Operator{	//All operators that have stack-count of -1
	static final int PLUS=0,MINUS=1,MULTIPLY=2,DIVIDE=3,POWER=4;
	int type;
	public BinaryOperator(int type){
		this.type=type;
	}
	public void doStackOp(Stack stack) throws MathException, StackException{
		double	y=stack.pop(),		//pop one
			x=stack.pop(),		//pop another
			ans=Double.NaN;
		switch(type){
			case PLUS:	ans=x+y;break;
			case MINUS:	ans=x-y;break;
			case MULTIPLY:	ans=x*y;break;
			case DIVIDE:	ans=x/y;break;
			case POWER:	ans=Math.pow(x,y);break;
		}
		if(Double.isNaN(ans))throw new MathException(stack,"Operator "+type+" not found");
		stack.push(ans);
	}
	public int getStackCount(){return -1;}	//reduces stack size by one
}

class UnaryMinus implements Operator{
	public void doStackOp(Stack stack) throws StackException {
		stack.push(-stack.pop());
	}
	public int getStackCount(){return 0;}
}






	//These extensions of ObjectPath implement Operator
	//for use in the Expression R-P code

class Function extends ObjectPath implements Operator{
	String function;
	int parameters;
	Unit unit = null;
		//Constructor takes the java path to the method to invoke
	public Function(String function, int parameters) throws ParseException{
		this.function=function;
		this.parameters=parameters;
		createPath(function);
		if(member instanceof Method &&
			 ((Method)member).getReturnType().isAssignableFrom(Number.class) ){
			unit = new Unit("");
		}
	}

	public Unit getUnit(){ return unit; }

		//functions pop one then push one (=0 for a 1-parameter function)
	public int getStackCount(){return 1-parameters;}

		//invoke the method using the top value on stack as parameter
	public void doStackOp(Stack stack) throws MathException, StackException{
		Object[] parameterList=new Object[parameters];
		for(int i=parameters-1;i>=0;i--){
			parameterList[i]=new Double(stack.pop());	//pop one
		}
		double ans=Double.NaN;
		if(!(member instanceof Method))
			throw new MathException(stack,member+" is not a method");
		Method method=(Method)member;
		if(method.getReturnType()!=Double.TYPE)
			throw new MathException(stack,method+" is not a double");
		Class[] paramtypes=method.getParameterTypes();
		if(paramtypes.length!=parameters)
			throw new MathException(stack,method+" does not take "+parameters+" parameters");
		try{
			Double d=(Double)method.invoke( object, parameterList );
			ans=d.doubleValue();
		}catch (Exception e){
			throw new MathException(stack, method+" could not be invoked"+
				" because of "+e.getMessage() );
		}
		stack.push(ans);
	}
}



class Value implements Operator{
	double value;
	public Value(double value){
		this.value=value;
	}
	public void doStackOp(Stack stack) throws StackException{
		stack.push(value);
	}
	public int getStackCount(){return 1;}	//this pushes one number on the stack
}






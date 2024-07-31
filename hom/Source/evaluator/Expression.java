package evaluator;

/*
 An object beloning to the class Expr is a mathematical expression that
 can involve:
  -- real numbers such as 2.7, 3, and 12.7e-12
  -- the variable x
  -- arithmetic operators  +,  -,  *,  /,  and  ^ , where
  the last of these represents raising to a power
  -- the mathematical functions sin, cos, tan, sec, csc, cot,
  arcsin, arccos, arctan, exp, ln, log2, log10, abs, and sqrt,
  where ln is the natural log, log2 is the log to the base 2,
  log10 is the log to the base 10, abs is absolute value,
  and sqrt is the square root
  -- parentheses
  Some examples would be:   x^2 + x + 1
   sin(2.3*x-7.1) - cos(7.1*x-2.3)
   x - 1
   42
   exp(x^2) - 1
  The trigonometric funtions work with radians, not degrees.  The parameter
 of a function must be enclosed in parentheses, so that "sin x" is NOT allowed.
  An Expr is constructed from a String that contains its definition.  If an
  error is found in this definition, the constructor will throw an
  IllegalArgumentException with a message that describes the error and
  the position of the error in the string.  After an Expr has been
  constructed, its defining string can ge retrieved using the
  method getDefinition().
  The main operation on an Expr object is to find its value, given
  a value for the variable x.  The value is computed by the value() method.
  If the expression is undefined at the given value of x, then the
 number returned by value() method will be the special "non-a-number number",
  Double.NaN.  (The boolean-valued function Double.isNaN(v) can be used to
  test whether the double value v is the special NaN value.  For technical
  reasons, you can't just use the == operator to test for this value.)
  David Eck, 8 October 1998
 */
/*
 Extended September 2001 Sanjay Manohar
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
import java.util.Hashtable;
import java.util.StringTokenizer;
import sanjay.common.Utils;

public class Expression {
  //----------------- public interface ---------------------------------------
  public Expression(String definition) throws ParseException {
    // Construct an expression, given its definition as a string.
    // This will throw an IllegalArgumentException if the string
    // does not contain a legal expression.
    try {
      parse(definition);
    }
    catch (ParseException e) { //inform the user of errors in parsing
      e.position = pos; //fill in location in text
      e.parseString = definition; //fill in actual text
      throw e; //rethrow back to user
    }
  }

  /**
   * Return the value of this expression, when the variable x
   * has the specified value.  If the expression is undefined
   * for the specified value of x, then Double.NaN is returned.
   */
  public double value() throws StackException, MathException {
    Object o = eval();
    if (o instanceof Double) {
      return ( (Double) o).doubleValue();
    }
    else {
      return Double.parseDouble(o.toString());
    }
  }

  /**
   * Evaluates the expression and returns the result.
   *
   * @return the object that represents the value of the expression
   * @throws StackException if the stack either underflows or overflows
   * during the computation
   * @throws MathException if there is a numerical exception, such as
   * divide by zero.
   */
  public Object evaluate() throws StackException, MathException {
    return eval();
  }

  public String getDefinition() {
    // Return the original definition string of this expression.  This
    // is the same string that was provided in the constructor.
    return definition;
  }

  //------------------- private implementation details ----------------------------------
  private String definition; // The original definition of the expression,

  // as passed to the constructor.
  private Operator[] code; // A translated version of the expression, containing

  //   stack operations that compute the value of the expression.
  private Stack stack; // A stack to be used during the evaluation of the expression.

  public Operator[] getCode(){ return code; }
  public int getCodeLength() { return code.length; }

  /** Evaluate the expression and return the object result */
  private Object eval() throws MathException, StackException {
    // evaluate this expression for this value of the variable
    Object ans = null;
    stack.reset();
    for (int i = 0; i < codeSize; i++) {
      try {
        code[i].doStackOp(stack);
      }
      catch (MathException e) { //for any math errors
        e.code = code; //fill in the code
        e.location = i; //and the location within the code
        throw e; //and rethrow to user
      }
    }
    ans = stack.pop();
    return ans;
  }

  private int pos = 0, /*constantCt = 0,*/ codeSize = 0; // data for use during parsing

  /*
   private void error(String message) {  // called when an error occurs during parsing
    throw new IllegalArgumentException("Parse error:  " + message + "  (Position in data = " + pos + ".)");
   }
   */
  private int computeStackUsage() { // call after code[] is computed
    int s = 0; // stack size after each operation
    int max = 0; // maximum stack size seen
    for (int i = 0; i < codeSize; i++) {
      s += code[i].getStackCount();
      if (s > max) {
        max = s;
      }
    }
    return max;
  }

  private void parse(String definition) throws ParseException {
    // Parse the definition and produce all
    // the data that represents the expression
    // internally;  can throw IllegalArgumentException
    if (definition == null || definition.trim().equals("")) {
      throw new ParseException("No data provided to Expression constructor");
    }
    this.definition = definition;
    code = new Operator[definition.length()];
    parseExpression();
    skip();
    if (next() != 0) {
      throw new ParseException(
          "Extra data found after the end of the expression.");
    }
    int stackSize = computeStackUsage();
//    stack = new double[stackSize];
    stack = new Stack();
    Operator[] c = new Operator[codeSize];
    System.arraycopy(code, 0, c, 0, codeSize);
    code = c;
  }

  private char next() { // return next char in data or 0 if data is all used up
    if (pos >= definition.length()) {
      return 0;
    }
    else {
      return definition.charAt(pos);
    }
  }

  private void skip() { // skip over white space in data
    while (Character.isSpaceChar(next())) {
      pos++;
    }
  }

  // remaining routines do a standard recursive parse of the expression
  /**
   * Expressions are a series of terms separated by + or -.
   */
  private void parseExpression() throws ParseException {
    boolean neg = false;
    skip();
    if (next() == '+' || next() == '-') {
      neg = (next() == '-');
      pos++;
      skip();
    }
    parseTerm();
    if (neg) {
      code[codeSize++] = new UnaryMinus();
    }
    skip();
    while (next() == '+' || next() == '-') {
      char op = next();
      pos++;
      parseTerm();
      code[codeSize++] = (op == '+') ? new BinaryOperator(BinaryOperator.PLUS) :
          new BinaryOperator(BinaryOperator.MINUS);
      skip();
    }
  }

  /**
   * Terms are a series of factors separated by * or /
   */
  private void parseTerm() throws ParseException {
    parseFactor();
    skip();
    while (next() == '*' || next() == '/') {
      char op = next();
      pos++;
      parseFactor();
      code[codeSize++] = (op == '*') ?
          new BinaryOperator(BinaryOperator.MULTIPLY) :
          new BinaryOperator(BinaryOperator.DIVIDE);
      skip();
    }
  }

  /** A Factor is a series of Primaries separated by ^ */
  private void parseFactor() throws ParseException {
    parsePrimary();
    skip();
    while (next() == '^') {
      pos++;
      parsePrimary();
      code[codeSize++] = new BinaryOperator(BinaryOperator.POWER);
      skip();
    }
  }

  /**
   * A primary may be a numerical value,
   * a parenthesised Expression,
   * a Word
   * a member-access operator (starting with '.') that is evauated at run-time,
   * a string delimited by "double-quotes".
   */
  private void parsePrimary() throws ParseException {
    skip();
    char ch = next();
    if (Character.isLetter(ch)) {
      parseWord();
    }
    else if (Character.isDigit(ch)) {
      parseNumber();
    }
    else if (ch == '(') {
      pos++;
      parseExpression();
      skip();
      if (next() != ')') {
        throw new ParseException("Exprected a right parenthesis.");
      }
      pos++;
    }
    else if (ch == '.') {
      pos++;
      if (Character.isDigit(next())) {
        pos--;
        parseNumber();
      }
      else {
        parseMemberOperator();
      }
    }
    else if (ch == '"') {
      StringBuffer str = new StringBuffer();
      boolean prevSlash=false;
      pos++;
      //allow escaped quotes in strings
      while (next() != '"' || prevSlash) {
        str.append( next() );
        if(prevSlash) prevSlash=false; else prevSlash=next()=='\\';
        pos++;
      }
      pos++;
      code[codeSize++] = new Value(Utils.unescape(str.toString()));
    }
    else if (ch == ')') {
      throw new ParseException("Unmatched right parenthesis.");
    }
    else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
      throw new ParseException("Operator '" + ch +
                               "' found in an unexpected position.");
    }
    else if (ch == 0) {
      throw new ParseException(
          "Unexpected end of data in the middle of an expression.");
    }
    else {
      throw new ParseException("Illegal character '" + ch + "' found in data.");
    }

    skip();
    if (next() == '.') {
      parsePrimary();
    }

  }

  /**
   * A word is a series of alphanumeric characters, including '.'
   * It may represent
   * a variable, which may ocntain '.', evaluated at compile-time to point to a variable,
   * a function, which is a name followed by '(' and a series of further expressions.
   */
  private void parseWord() throws ParseException {
    String w = "";
    while (Character.isLetterOrDigit(next()) || next() == '.') {
      w += next();
      pos++;
    }
//     w = w.toLowerCase();
    skip();
    if (next() == '(') {
      //found a function
      int parameters = 0;
      pos++;
      skip(); // skip the '('
      while (next() != ')') { //parse the parameters onto code stack
        parseExpression();
        parameters++;
        skip();
        if (next() != ',' && next() != ')') {
          throw new ParseException("Missing right parenthesis after parameter " +
                                   parameters + " of function '" + w + "'.");
        }
        if (next() == ',') {
          pos++;
          skip();
        } // skip the ','
      }
      pos++; //skip the ')'
      skip();
      code[codeSize++] = new Function(w, parameters);
    }
    else {//no brackets after word
      // deal with some simple literals here:
      if(w.equals("false")) { code[codeSize++] = new Value(Boolean.FALSE); return; }
      if(w.equals("true"))  { code[codeSize++] = new Value(Boolean.TRUE); return; }
      // found a variable
      code[codeSize++] = new Variable(w);
    }
  }

  private void parseNumber() throws ParseException {
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
    if (w.equals(".")) {
      throw new ParseException(
          "Illegal number found, consisting of decimal point only.");
    }
    if (next() == 'E' || next() == 'e') {
      w += next();
      pos++;
      if (next() == '+' || next() == '-') {
        w += next();
        pos++;
      }
      if (!Character.isDigit(next())) {
        throw new ParseException(
            "Illegal number found, with no digits in its exponent.");
      }
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
    if (Double.isNaN(d)) {
      throw new ParseException("Illegal number '" + w + "' found in data.");
    }
    code[codeSize++] = new Value(new Double(d));
  }

  /**
   * called after a '.' has been parsed at the beginning of a word. This acts
   * like a postfix unary operator
   */
  private void parseMemberOperator() {
    String w = "";
    while (Character.isLetterOrDigit(next())) {
      w += next();
      pos++;
    }
    skip();
    if (next() == '(') {
      //found a member method
      pos++;
      skip();
      int parameters = 0;
      while (next() != ')') { //parse the parameters onto code stack
        parseExpression();
        parameters++;
        skip();
        if (next() != ',' && next() != ')') {
          throw new ParseException("Missing right parenthesis after parameter " +
                                   parameters + " of function '" + w + "'.");
        }
      }
      pos++;
      code[codeSize++] = new MethodAccessOperator(w, parameters);
    }
    else {
      //found a member field
      code[codeSize++] = new FieldAccessOperator(w);
    }
  }
} // end class Expr

/** Represents a stack of values */
class Stack {
  static final int MAX = 100;

  Object[] s = new Object[MAX];

  int top = 0;

  public final Object pop() throws StackException {
    if (top <= 0) {
      throw new StackException();
    }
    return s[--top];
  }

  public final double popDouble() throws StackException, TypeException {
    Object o = pop();
    try {
      return ( (Double) o).doubleValue();
    }
    catch (ClassCastException x) {
      throw new TypeException(this,
                              "Cannot convert '" + o.toString() +
                              "' to a number.");
    }
  }

  public final void push(Object d) throws StackException {
    if (top == MAX) {
      throw new StackException();
    }
    s[top++] = d;
  }

  public final void push(double d) throws StackException {
    push(new Double(d));
  }

  public final void reset() {
    top = 0;
  }

  public final String toString() {
    String str = "";
    for (int i = top; i >= 0; i--) {
      str += String.valueOf(s[i]) + '\n';
    }
    return str;
  }
}

// classes of operators
interface Operator {
  public void doStackOp(Stack stack) throws MathException, StackException;

  public int getStackCount();
}

class BinaryOperator
    implements Operator { //All operators that have stack-count of -1
  static final int PLUS = 0, MINUS = 1, MULTIPLY = 2, DIVIDE = 3, POWER = 4;

  int type;

  public BinaryOperator(int type) {
    this.type = type;
  }

  public void doStackOp(Stack stack) throws MathException, StackException {
    double y = stack.popDouble(), x = stack.popDouble(), ans = Double.NaN; //pop another
    switch (type) {
      case PLUS:
        ans = x + y;
        break;
      case MINUS:
        ans = x - y;
        break;
      case MULTIPLY:
        ans = x * y;
        break;
      case DIVIDE:
        ans = x / y;
        break;
      case POWER:
        ans = Math.pow(x, y);
        break;
    }
    if (Double.isNaN(ans)) {
      throw new MathException(stack, "Operator " + type + " not found");
    }
    stack.push(ans);
  }

  public int getStackCount() {
    return -1;
  } //reduces stack size by one
}

class UnaryMinus
    implements Operator {
  public void doStackOp(Stack stack) throws StackException, TypeException {
    stack.push( -stack.popDouble());
  }

  public int getStackCount() {
    return 0;
  }
}
/*
// ObjectPath.class
//uses java reflection to find an object
//addresed by object.member
//We do not check the return type of reflected objects at
//parse time. If the object is not of the required type
//a MathError will be generated on evaluation.
//The tree is only traversed during parse time, and as such,
//the object which contains the last method/field in the tree
//is fixed at parse time.
 abstract class ObjectPath{
 Object object;
 Member member;
//these objects are used as root nodes when
//finding java objects (optional)
 static Object[] root=new Object[0];
 static String[] packages=new String[]{
 "java.lang"
 };
 static public void setRoot(Object[] rroot){root=rroot;}
 static public Object[] getRoot(){return root;}
 static public void setPackage(String[] rpackages){packages=rpackages;}
 static public String[] getPackage(){return packages;}
// find a java object from a static context
// and parse up to the name of the field or method
// specified in the path string
 void createPath(String path) throws ParseException{
//first try all root objects
 for(int i=0;i<getRoot().length;i++){
 StringTokenizer st=new StringTokenizer(path,".");
 Object o=getRoot()[i];
//is it an object?
 if(find(st,o,o.getClass()))break;
 st=new StringTokenizer(path,".");
//or a class for static members?
 if(o instanceof Class) if(find(st,null,(Class)o))break;
 }
//then try start on a static member/method
 if(member==null){
 String[] packages=getPackage();
 boolean found=false;
 for(int i=0;i<packages.length;i++){
 try{
  StringTokenizer st=new StringTokenizer(path,".");
  String token=st.nextToken();
  Class cls=Class.forName(packages[i]+'.'+token);
  if(!(found=find(st, null , cls))){continue;}
 }catch(ClassNotFoundException e){continue;}
 break;
 }
 if(!found) throw new ParseException("Cannot find object "+path);
 }
 }
//Pass a string tokenizer whose tokens are member names
//starting in newobject, OR if newobject is null,
//cls represents the class of whom st's first token
//is a static member
 boolean find(StringTokenizer st, Object newobject, Class cls){
 try{
 while(st.hasMoreElements()){
 object=newobject;
 String token=st.nextToken();
 try{	//find any fields
  Field field=cls.getField(token);
  member=field;
  if(st.hasMoreElements()) newobject=field.get(object);
 }catch(Exception e){
  try{	//find any 0- or 1-parameter methods
  Method method;
  try{method=cls.getMethod(token, null);}
  catch(NoSuchMethodException ex){
  method=cls.getMethod(token,new Class[]{Double.TYPE});
  }
  member=method;
  if(st.hasMoreElements())
  newobject=method.invoke(object, null);
  }catch(Exception ex){
  throw new ParseException("Can't find "+token+" in "+object);
  }
  if(newobject==null && st.hasMoreElements())
  throw new ParseException("Null object "+token+" in "+object);
 }
 if(object!=null)cls=object.getClass();
 }
 if(member==null) throw new ParseException("Error finding object "+st);
 }catch(ParseException e){
 object=null;
 return false;
 }
 return true;
 }
 }
 */
//These extensions of ObjectPath implement Operator
//for use in the Expression R-P code
/** migrated to separate file
 class Function extends ObjectPath implements Operator{
 String function;
//Constructor takes the java path to the method to invoke
 public Function(String function) throws ParseException{
 this.function=function;
 createPath(function);
 }
//functions pop one then push one (=0)
 public int getStackCount(){return 0;}
//invoke the method using the top value on stack as parameter
 public void doStackOp(Stack stack) throws MathException, StackException{
 double	x=stack.pop(),	//pop one
 ans=Double.NaN;
 if(!(member instanceof Method))
 throw new MathException(stack,member+" is not a method");
 Method method=(Method)member;
 if(method.getReturnType()!=Double.TYPE)
 throw new MathException(stack,method+" is not a double; it is a "+method.getReturnType());
 Class[] paramtypes=method.getParameterTypes();
 if(paramtypes.length!=1 || paramtypes[0]!=Double.TYPE)
 throw new MathException(stack,method+" does not take 1 parameter");
 try{
 Double d=(Double)method.invoke( object, new Object[]{new Double(x)} );
 ans=d.doubleValue();
 }catch (Exception e){
 throw new MathException(stack, method+" could not be invoked"+
  " because of "+e.getMessage() );
 }
 stack.push(ans);
 }
 }
 */
/* //Migrated to separate file
 class Variable extends ObjectPath implements Operator{
 static final int INTERNAL=0,EXTERNAL=1;
 int type;
 String name;
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
 }else{
 createPath(name);
 type=EXTERNAL;
 }
 }
//get the value of the member and push onto stack
 public void doStackOp(Stack stack) throws MathException, StackException{
 double ans=Double.NaN;
 switch(type){
 case INTERNAL:		//to be read from the table
 ans=((Double)table.get(name)).doubleValue();
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
 }else{		//check for double field
 Field field=(Field)member;
 if(field.getType()!=Double.TYPE){ //handle objects wrapping doubles
  Method test=null;
  try{
  test = field.getType().getMethod("get",null); //throws exception if no get method
  ans = ((Double)test.invoke( field.get(object), null )).doubleValue(); //throws exception if get method is not a double
  }catch(Exception e){
  throw new MathException(stack, field+" is not a double");
  }
 } else try{
  ans=field.getDouble(object);
 }catch(Exception e){
  throw new MathException(stack, field+" could not be read "+
  "because of "+e.getMessage() );
 }
 }
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
 */
//Exceptions
/** Migrated to separate file
 class ParseException extends IllegalArgumentException{
 String parseString="";
 int position=0;
 public ParseException(){
 }
 public ParseException(String s){
 super(s);
 }
 public String getMessage(){		//add a location indicator
 String s=super.getMessage();
 s+='\n'+parseString+'\n';
 for(int i=0;i<position;i++)s+=' ';
 s+='^';
 return s;
 }
 }
 class MathException extends Exception{
 public Operator[] code;
 public int location=0;
 public Stack stack;
 public MathException(Stack stk, String str){
 super("Mathematical error: "+str+stk);
 stack=stk;
 }
 }
 class StackException extends Exception{
 public StackException(){
 super("Stack overflow");
 }
 }
 */

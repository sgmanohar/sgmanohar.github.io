package evaluator;

/**
 * An exception indicating that the left hand side of an equation
 * is of an invalid form.
 */

public class EquationException extends ParseException{
  public EquationException(){
    super("Equation exception");
  }
  public EquationException(String s){
    super(s);
  }
}
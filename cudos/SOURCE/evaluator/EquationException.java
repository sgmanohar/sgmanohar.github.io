package evaluator;

public class EquationException extends ParseException{
        public EquationException(){
                super("Equation exception");
        }
        public EquationException(String s){
                super(s);
        }
}
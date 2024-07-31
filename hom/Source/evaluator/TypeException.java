package evaluator;
/**
 * Cast when the type of a value does not match the expected type.
 */
public class TypeException extends MathException{
	public TypeException(Stack stk,String str){
		super(stk, str);
	}
}
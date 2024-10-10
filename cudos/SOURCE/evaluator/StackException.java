package evaluator;

/** Thrown if there is stack overflow or the stack is empty. */
public class StackException extends Exception{
	public StackException(){
		super("Stack overflow");
	}
}
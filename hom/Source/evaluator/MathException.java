package evaluator;

public class MathException extends Exception{
	public Operator[] code;
	public int location=0;
	public Stack stack;
	public MathException(Stack stk, String str){
		super("Mathematical error: "+str+stk);
		stack=stk;
	}
}

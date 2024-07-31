package evaluator;

public class Value implements Operator{
	Object value;
	public Value(Object value){
		this.value=value;
	}
	public void doStackOp(Stack stack) throws StackException{
		stack.push(value);
	}
	public int getStackCount(){return 1;}	//this pushes one number on the stack
}

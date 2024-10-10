package evaluator;

/**
 * Thrown if a variable is constructed but no data is found.
 */

public class NoSuchVariableException extends ParseException{
	public String variableName;

	public NoSuchVariableException() {  }
	public NoSuchVariableException(String name) { this.variableName = name; }
}
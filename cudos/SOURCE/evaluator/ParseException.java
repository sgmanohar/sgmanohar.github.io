
/**
 * Exception thrown while parsing an expression
 */
package evaluator;

public class ParseException extends IllegalArgumentException{
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

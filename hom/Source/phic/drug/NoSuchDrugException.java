/**
 * Thrown if a drug name is not found.
 */
package phic.drug;
public class NoSuchDrugException extends Exception{
	public NoSuchDrugException(){
	}

	public NoSuchDrugException(String s){
		super(s);
	}
}
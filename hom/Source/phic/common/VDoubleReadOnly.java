package phic.common;
/**
 * Simply overrides VDouble and provides a dummy set method.
 */
public class VDoubleReadOnly extends VDouble{
	public VDoubleReadOnly(){}

	public void set(double value){
		cannotSet();
	}

	public boolean isSettable(){
		return false;
	}
}
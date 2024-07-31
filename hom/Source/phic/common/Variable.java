package phic.common;
/**
 * Variable is a simple interface allowing getting and setting of values,
 * as double precision (32bit floating point) values.
 */
public interface Variable extends java.io.Serializable{
	/**
	 * Method to get the value of the variable.
	 */
	public double get();

	/**
	 * Method that sets the value of the variable.
	 */
	public void set(double v);
}

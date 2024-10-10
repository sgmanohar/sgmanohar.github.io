
/**
 * Helpers provide the capability of finding external objects in a user-determined
 * database. The objects are
 */
package evaluator;

public interface PathHelper {
	public Object findObjectFromPath(String pathName);
	public double getObjectValue(Object object);
	public void setObjectValue(Object object, double value);
}
package sanjay;

/**
 * a list of flags that can be used as a propertyeditor
 *
 */

public interface FlagList {
	public Object[] getFlags();
	public void setFlag(int i, boolean set);
	public boolean getFlag(int flag);
}
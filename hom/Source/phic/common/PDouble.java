package phic.common;
/**
 * The simplest implementation of a Variable. it retains a protected double
 * value, and allows access via get() and set() methods.
 *
 * 21-1-03 @todo - should phase out PDouble. There are 3 references to it:
		 * Body.Glycogen, Body.Fat, Kidney.urineFlow. They may all now be safely replaced
 * with VDoubles.
 *
 * 16.6.05 this class is now redundant: remove it.
 */
public class PDouble implements Variable{
	protected double value;

	/** Create a value initialised to 0. */
	public PDouble(){
	}

	/** Create a value initialised to the specified value. */
	public PDouble(double value){
		this.value=value;
	}

	public final void set(double value){
		this.value=value;
	}

	public final double get(){
		return value;
	}

	public final void add(double amount){
		value+=amount;
	}
}

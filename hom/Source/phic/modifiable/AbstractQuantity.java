package phic.modifiable;
import java.text.*;
import phic.common.*;

/**
 * Defines many methods on quantities.
 * The abstract setC, getC, setQ, getQ methods must be filled in.
 *
 * A quantity is the basic object which expresses an amount of substance in a
 * container of specified volume. In fact, the quantity stores not quantities,
 * but concentrations. Use getC, setC, getQ, setQ to manipulate the contents.
 * All operations are  synchronized internally on the parent container.
 * Never get or set a quantity when another container is locked.
 */
public abstract class AbstractQuantity extends VDouble{
	/** The quantity must be contained in a phic.Container object, the parent. */
	public AbstractQuantity(Container parent){
		super(parent);
		this.parent=parent;
	}

	Container parent;
	//easy access methods
	/** Set the concentration of this quantity.*/

	public void setC(double cc){
		set(cc);
	}

	/** Sets the quantity of this substance in the container. If the container
	 * volume is zero, a divide by zero exception will be thrown. */
	public void setQ(double qq){
		if(qq==0){
			set(0);
		} else{
			set(qq/parent.volume.get());
		}
	}

	/** Get the concentration of this quantity in the container. */
	public double getC(){
		return get();
	}

	/** Get the quantity of substance contained in the container. */
	public double getQ(){
		return get()*parent.volume.get();
	}

	/** Shorthand for getC() */
	public double C(){
		return getC();
	}

	/** Shorthand for getQ() */
	public double Q(){
		return getQ();
	}

	Variable Q=new Variable(){
		public double get(){
			synchronized(parent){
				return AbstractQuantity.this.get()*parent.volume.get();
			}
		}

		public void set(double quantity){
			synchronized(parent){
				AbstractQuantity.this.set(quantity/parent.volume.get());
			}
		}
	};
	/**
	 * Add a quantity of the substance to this quantity variable.
	 * @param qq the amount of substance to add to this quantity.
	 */

	 public void addQ(double qq){
		//negative values supported
		if(parent.volume.get()==0){
			return;
		}
		set(Math.max(getQ()+qq,0)/parent.volume.get());
	}

	/**
	 * Add a value to the concentration of the substance.
	 * Since the value of the Variable is the concentration, this method
	 * simply calls Variable.add().
	 */
	public void addC(double cc){
		super.add(cc);
	}

	/**
	 * Multiply the quantity (or concentration) of substance in this container by the factor.
	 * @param factor the factor by which to multiply the current quantity
	 */
	public void multiplyC(double factor){
		set(get()*factor);
	}

	/**
	 * Moves the entire amount of substance in this quantity to the specified
	 * quantity.
	 * @param to The quantity to which this quantity is transferred.
	 */
	public void moveTo(Quantity to){
		//Volumes must NOT change during this call!
		to.addQ(getQ());
		setQ(0);
	}

	/**
	 * Move some of this quantity to another quantity variable.
	 * @param to The quantity to which to transfer.
	 * @quantity the amount of substance to be transferred.
	 */
	public void moveTo(AbstractQuantity to,double quantity){
		//negative values supported
		synchronized(to.parent){
			synchronized(parent){
				quantity=Math.max(Math.min(quantity,getQ()),-to.getQ());
				to.addQ(quantity);
				addQ(-quantity);
			}
		}
	}

	//these are now replaced by VDouble.
	/**	Implementation of interface Variable. get() returns the concentration of
	 *	this quantity.*/
//	public double get(){return c;}
	/**	Implementation of interface Variable. set() sets the concentration of
	 *	this quantity.*/
//	public void set(double d){c=d;}
	static final NumberFormat numformat=new DecimalFormat("0.##E0");

	/**
	 *  Return a string representation of this quantity, using the number
	 *  format "0.##E0". Used primarily for debugging.
	 */
	public String toString(){
		return numformat.format(get());
	}

	public static String toString(double d){
		return numformat.format(d);
	}

	/** Sign of a double */
	static final double sgn(double x){
		return x>0?1:(x<0?-1:0);
	}

	/** These methods find the nearest power of ten above/below. */
	public static double getNearestRoundBelow(double d){
		int p=(int)(Math.log(d)/Math.log(10));
		return Math.pow(10,p);
	}

	public static double getNearestRoundAbove(double d){
		double p=(Math.log(d)/Math.log(10));
		p=Math.floor(p);
		return Math.pow(10,p+1);
	}
}
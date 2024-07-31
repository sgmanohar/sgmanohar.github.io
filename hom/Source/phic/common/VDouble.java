package phic.common;

import phic.gui.Variables;
import phic.modifiable.Range;
import phic.common.*;
import phic.*;
/**
 * VDouble is a 'visible' double -- i.e. like a PDouble variable that has an entry
 * in the external table that specifies initial, minimum and maximum values.
 * Many methods are final for speed & will be inlined.
 * If constructor is called with an object as a parameter, all get and set methods
 * will be synchronised on this object.
 *
 * VDoubles are graphable in a scrollgraph (See CombinedScrollGraph), and now
 * contain all the initialValue, minimum and maximum values needed.
 */
public class VDouble implements Variable{
	/**
	 * Values governing the range of the variable
	 */
	public double initialValue=0,minimum=0,maximum=1;

			/* This sets the initialValue, minimum and maximum members of this variable. */
	public final void setRanges(double initialValue,double minimum,double maximum){
		this.initialValue=initialValue;
		this.minimum=minimum;
		this.maximum=maximum;
	}

	/** The lock object, which all get and set calls must synchronized upon. May be null. */
	protected Object lock=null;

	/** Create an unsynchronized VDouble */
	public VDouble(){}

	/** Create a VDouble synchronized on the given lock */
	public VDouble(Object lock){
		this.lock=lock;
	}
        /** Create a VDouble with the given units from UnitConstants.java */
        public VDouble(int unit){
          this.unit=unit;
        }

	/**
	 * The actual value of the variable
	 */
	private double value;

	public double get(){
		if(lock==null){
			return value;
		} else{
      //disabled 19-10-3 -- trouble with earlier jre
			//synchronized(lock){
				return value;
			//}
		}
	}

	public void set(double v){
          if(Current.thread.DEBUG_ERRORS && Double.isNaN(v) || Math.abs(v)>1e8)
            throwSettingError();
          if (lock == null) {
            value = v;
          } else {
            //disabled 19-10-3 -- trouble with earlier jre
            //synchronized(lock){
            value = v;
            //}
          }
        }

        /**
         * Called if debugging when a not-a-number value is used to set the
         * variable. It dumps the name of the variable.
         */
        protected void throwSettingError(){
          String err = null;
         try{
           err = "Error setting "+Variables.forVDouble(this).longName;
         }catch(IllegalArgumentException ex){
           err = "Error setting VDouble "+formatValue(true,false);
         }
         Thread.currentThread().dumpStack();
         throw new ArithmeticException(err);
        }

	/**
	 * Convenience method to add a double value to the current value.
	 * Does not chain to set & get; uses internal value.
	 */
	public void add(double amount){
		if(lock==null){
			set(get()+amount);
		} else{
			//synchronized(lock){
				set(get()+amount);
			//}
		}
	}
        /**
         * Convenience method to add a double value to the current value, but
         * not letting the value fall below 0.
         * @param amount the amount to add.
         * @todo not synchronised
         */
        public void addQuantity(double amount){
          set( Math.max(0,get()+amount));
        }
        /**
         * Convenience method to add a double value to the current value, but
         * not letting the value fall outside the normal range for this variable.
         * @param amount the amount to add.
         */

        public final void addLimited(double amount){
          set( Math.max(Math.min(get() + amount, maximum),minimum) );
        }




	/**
	 * Convenience method to multiply the value of the variable by another value.
	 * Chains call to set() and get().
	 */
	public void multiplyBy(double ratio){
		set(ratio*get());
	}

	/**
	 * This sets the current value of the variable to its initial value.
	 */
	public final void initialise(){
		set(initialValue);
	}

	/**
	 * Return the amount by which the variable's value exceeds the initial
	 * value.
	 */
	public final double getError(){
		return get()-initialValue;
	}

	/** This 'regulates' the value of this variable towards the value of another
	 *  controller variable. Effectively, the deviation of the controller
	 *  variable from its initial value (its error) is multiplied by the gradient, and
	 *  this is then the desired value for thie variable's error. The change
	 *  low passed.
	 *  @param controller the variable that will determimne this variable.
	 *  @param gradient the scale coefficient of this variable to the controller.
			 *  @lowPassRate in the range 0-1, rate at which value approaches desired value.
	 *  @see #lowPass
	 */
	public void regulate(VDouble controller,double gradient,double lowPassRate){
		lowPass(initialValue+gradient*controller.getError(),lowPassRate);
	}

        /**
         * Identical to regulate(VDouble, double, double), except that the constant
         * value is added to the target value.
         */
        public void regulate(VDouble controller,double gradient,double lowPassRate, double constant){
                lowPass(initialValue+gradient*controller.getError() + constant,lowPassRate);
        }

	/**
	 * This regulates the value of the variable depending on the error of another
	 * variable. It is the same as regulate() with the same three parameters,
	 * except this method ensures the variable that is to be changed stays within
	 * its limits (maximum - minimum).
	 */
	public void regulateQuantity(VDouble controller,double gradient,
			double lowPassRate){
		double target=initialValue+gradient*controller.getError();
		target=Math.min(Math.max(target,minimum),maximum);
		lowPass(target,lowPassRate);
	}
        /**
         * Identical to regulateQuantity(VDouble, double, double), except that
         * the constant value is added to the target after it has been fitted
         * into the correct range. This means the value could actually be out of
         * the range specified by maximum and minimum.
         */
        public void regulateQuantity(VDouble controller,double gradient,
                        double lowPassRate, double constant){
                double target=initialValue+gradient*controller.getError();
                target=Math.min(Math.max(target,minimum),maximum) + constant;
                lowPass(target,lowPassRate);
        }

	/** This is a low-pass filtered version of set(). The value of this
	 * variable changes gradually towards the specified value.
	 * @param desiredValue the value towards which this variable should tend.
	 * @param lowPassRate in the range 0-1, the rate at which the variable's
	 * value tends towards the specified value; 0 = no change, 1 = instant
	 * change. */
	public final void lowPass(double desiredValue,double lowPassRate){
		set(get()*(1-lowPassRate)+desiredValue*lowPassRate);
	}

	/**
	 * A low pass filtered version of set, that does not allow the value
	 * of the variable to exceed the limits set by minimum and maximum.
	 * The value is low passed towards the minimum or maximum, if the
	 * desiredValue is out of range.
	 */
	public final void lowPassQuantity(double desiredValue,double lowPassRate){
		double target=Math.min(Math.max(desiredValue,minimum),maximum);
		set(get()*(1-lowPassRate)+target*lowPassRate);
	}

	/** Call this method if you are overriding the set() method, and wish
	 *  this variable not to be set. For example, variables like TPR will
	 *  always depend on the values of other variables.
	 */
	protected void cannotSet(){
		System.out.println("Cannot set this variable.");
	}

	/** Returns true if this variable can be set. */
	public boolean isSettable(){
		return true;
	}

	/**
			 * Return a string representation of this VDouble., using Quantity.toString().
	 * Note that Quantity overrides this method! [We're effectively inheriting from a subclass]
	 * This is essentially for debugging now, and has been replaced with
	 * formatValue(), which deals in the units of the variable.
	 */
	public String toString(){
		return Quantity.toString(get());
	}

	/**
	 * New unit capability added 4/1/03
	 * This is a constant defined in phic.common.UnitConstants
         * this is a constant as defined in phic.common.UnitConstants
	 */
	protected int unit;
        /**
         *  Set the unit of this variable
         */
        public void setUnit(int u){unit = u; }
        /**
         *  Get the unit of this variable.
         * this is a constant as defined in phic.common.UnitConstants
         * */
        public int getUnit(){return unit;}

	/**
	 * Method returning a string representing the value of this variable
	 * at a given value.
	 * The unit is displayed if showUnit is true.
	 *
	 * This method now uses UnitString.formatValue()
	 * @see UnitString#formatValue(double, int, boolean)
	 */
	public String formatValue(double v,boolean showUnit, boolean fixed){
		return UnitConstants.formatValue(v,this.unit,showUnit, fixed);
	}

	/**
	 * Method returning a string representing the value of this variable at
	 * a given value.
	 * The unit is displayed with a magnitude character e.g. 'm' or 'k'.
	 *
	 * This method now uses UnitString.formatValue()
	 * @see UnitString#formatValue(double, int, boolean)
	 */
        /*
	public final String formatValue(double v,boolean fixed){
		return formatValue(v,true,fixed);
	}*/

	/**
	 * This returns a string representing the current value of the variable.
	 * If showUnit is true, then the unit is suffixed to the end of the
	 * value.
	 * The call is delegated to formatValue(double, boolean) using get().
	 */
	public final String formatValue(boolean showUnit,boolean fixed){
		return formatValue(get(),showUnit,fixed);
	}

  /**
   * limitValue returns a value that is limited to the range of this variable;
   * i.e. max(min(target,maximumValue),minimumValue)
   */
  public final double limitValue(double target) {
    return Math.max(Math.min(target,maximum),minimum);
  }

  /**
   * getErrorRatio returns the ratio of the current value to the initial value.
   * No error gives a ratio of 1.
   */
  public double getErrorRatio() {
    return get()/initialValue;
  }

  public Range getRange(){
    if(range == null) range=new Range(minimum, maximum);
    return range;
  }
  private Range range = null;
}

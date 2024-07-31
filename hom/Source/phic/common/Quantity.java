package phic.common;

import java.text.*;

/**
 * A quantity is the basic object which expresses an amount of substance in a
 * container of specified volume. In fact, the quantity stores not quantities,
 * but concentrations. Use getC, setC, getQ, setQ to manipulate the contents.
 * All operations are  synchronized internally on the parent container.
 * Never get or set a quantity when another container is locked.
 *
 * Implementation of interface Variable. get() and Variable. set() are
 * inherited from VDouble; the result returns the concentration of
 * this quantity.
 */
public class Quantity
    extends VDouble implements Cloneable {
  /** The quantity must be contained in a phic.Container object, the parent. */
  public Quantity(Container parent) {
    super(/*parent*/ ); //had an object lock before
    this.parent = parent;
  }
  /** Doesn't clone the parent container */
  public Object clone() throws CloneNotSupportedException{
    return super.clone();
  }

  Container parent;
  //easy access methods
  /** Set the concentration of this quantity.*/

  public void setC(double cc) {
    set(cc);
  }

  /** Sets the quantity of this substance in the container. If the container
   * volume is zero, a divide by zero exception will be thrown. */
  public void setQ(double qq) {
    if (qq == 0) {
      set(0);
    }
    else {
      set(qq / parent.volume.get());
    }
  }

  /** Get the concentration of this quantity in the container. */
  public double getC() {
    return get();
  }

  /** Get the quantity of substance contained in the container. */
  public double getQ() {
    return get() * parent.vol;
  }

  /** Shorthand for getC() */
  public double C() {
    return getC();
  }

  /** Shorthand for getQ() */
  public double Q() {
    return getQ();
  }

  public Variable Q = new VDouble() {
    public double get() {
//      synchronized (parent) {
        return Quantity.this.get() * parent.volume.get();
//      }
    }

    public void set(double quantity) {
//      synchronized (parent) {
        Quantity.this.set(quantity / parent.volume.get());
//      }
    }

    /** by default, revert to moles for quantities */
    {
      unit = UnitConstants.MOLES;
    }
  };
  /**
   * Add a quantity of the substance to this quantity variable.
   * Doesn't allow the concentration to go below 0
   * @param qq the amount of substance to add to this quantity.
   */

  public void addQ(double qq) {
    //negative values supported
    if (parent.volume.get() == 0) {
      return;
    }
    set(Math.max(getQ() + qq, 0) / parent.volume.get());
  }

  /**
   * Add a value to the concentration of the substance.
   * Since the value of the Variable is the concentration, this method
   * simply calls Variable.add().
   */
  public final void addC(double cc) {
    super.add(cc);
  }

  /**
   * Multiply the quantity (or concentration) of substance in this container by the factor.
   * @param factor the factor by which to multiply the current quantity
   */
  public final void multiplyC(double factor) {
    set(get() * factor);
  }

  /**
   * Moves the entire amount of substance in this quantity to the specified
   * quantity.
   * @param to The quantity to which this quantity is transferred.
   */
  public final void moveTo(Quantity to) {
    //Volumes must NOT change during this call!
    to.addQ(getQ());
    setQ(0);
  }

  /**
   * Move some of this quantity to another quantity variable.
   * @param to The quantity to which to transfer.
   * @quantity the amount of substance to be transferred.
   */
  public final void moveTo(Quantity to, double quantity) {
    //negative values supported
        quantity = Math.max(Math.min(quantity, getQ()), -to.getQ());
        to.addQ(quantity);
        addQ( -quantity);
  }

  /**
   * Equilibrates the two quantities of a substance in different containers.
   * @param to the other quantity of the same substance in a different container
   * @param rate the coefficient of diffusion between the containers, in moles per
   * minute per Molar difference in concentration.
   * @param fraction the fraction of change towards the equilibrium concentration.
   * e.g. blood.K.equilibrateConcentration( ecf.K , 0.1 );
   */
  public final void equilibrateConcentration(Quantity to, double fraction) {
    double equilConc = (to.getQ() + getQ()) / (parent.vol + to.parent.vol);
    double lostQ = (getC() - equilConc) * parent.vol;
    moveTo(to, lostQ * fraction);
  }

  static final NumberFormat numformat = new DecimalFormat("0.##E0");

  /**
   *  Return a string representation of this quantity, using the number
   *  format "0.##E0". Used primarily for debugging.
   */
  public String toString() {
    return numformat.format(get());
  }

  public static String toString(double d) {
    return numformat.format(d);
  }

  public static double getNearestRoundAbove(double d) {
    double p = (Math.log(d) / Math.log(10));
    p = Math.floor(p);
    return Math.pow(10, p + 1);
  }

  /** Sign of a double */
  static final double sgn(double x) {
    return x > 0 ? 1 : (x < 0 ? -1 : 0);
  }

  public static double getNearestRoundBelow(double d) {
    int p = (int) (Math.log(d) / Math.log(10));
    return Math.pow(10, p);
  }

  /** Set the unit to concentration */
  {
    unit = UnitConstants.MOLAR;
  }
}

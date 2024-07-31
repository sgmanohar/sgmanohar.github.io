package phic.common;
import phic.Blood;

/**
 * Gas concentration : supersedes GasConc.
 * It is specific to blood.
 * Instead of a lookup table, this version uses a formula to calculate
 * concentration and partial pressure of gas.
 * @author Sanjay Manohar
 */

public class GasConcentration
    extends Gas {


    /** The blood that contains this GasConcentration */
    private Blood blood;


  /**
   * Gas constructor with with haematocrit correction. If the container
   * does not require haematocrit correction, use null.
   */

  public GasConcentration(Blood blood) {
    this.blood = blood;
  }

  /** Calculate the partial pressure of oxygen in the gas. */
  public VDouble PO2 = new VDouble() {
    public double get() {
      return PO2();
    }

    /**
     * Set value of O2
         * @deprecated This is an inherently inaccurate function and partial pressures
     * should only be read not set.
     */
    public void set(double value) {
      O2.set(cO2(value, PCO2.get()));
    }
  };

  public VDouble PCO2 = new VDouble() {
    public double get() {
      return PCO2();
    }

    /**
         * @deprecated This is an inherently inaccurate function and partial pressures
     * should only be read not set.
     */
    public void set(double value) {
      CO2.set(cCO2(PO2.get(), value));
    }
  };

  /**
   * Oxygen carrying capacity, as a function of Hct and CO2
   * in millilitres / litre. was 489*Hct on 10/7/3
   */
  private double getO2Capacity() {
    if(blood==null) return Double.NaN;
    return getO2Capacity(blood);
  }

  /**
   * Oxygen carrying capacity of haemoglobin calculated for a molecular weight
   * of haemoglobin of 64,458.
   * measured in L/g
   */
  protected static final double HB_O2_CAPACITY = 1.39E-3;


  /** MODEL:
   *   SatO2 = 100*( 1 - (a/(x^c + a))^b)
   *   [CO2] = ( 1 - a x ) ( b y + c (1 - d / y ) )
   */
  protected final long A = 53831;

  /** Calculate the partial pressure of oxygen in the gas */
  private final double PO2() {
    // convert to ml/l
    double satO2 = 100 * getSatO2(),
        c = CO2.get() * 1000;
    return (904 - c) / 904 *
        Math.pow(10,
                 ( (c - 119) / 300 + satO2 / 135.6 + 1 / (101.8 - satO2) + 0.13)) /
        1000;
  }

  /** Calcluate the partial pressure of carbon dioxide in the gas. */
  private final double PCO2() {
    double satO2 = 100 * getSatO2(),
        c = CO2.get() * 1000;
    return ( (1804 + 5.98 * satO2) *
            (Math.pow(A * A * A + c * c * c, 1 / 3.) - A) + 5.86) / 1000;
  }


  /**
   * Calculates the oxygen carrying capacity of blood.
   * @param blood the blood whose oxygen capacity to return
   * @return the oxygen capacity in Litres per Litre
   */

  public static double getO2Capacity(Blood blood){
    return blood.Hb.get() * HB_O2_CAPACITY;
  }

  /**
   * Calculate the concentration of O2 in a gas with a given partial pressure
   * of O2 and CO2 (in mmHg), using the supplied blood parameters.
   *
   * @return in ml/L
   */
  private static final double  cO2(double ppO2, double ppCO2, Blood blood) {
    double satO2 = 1 -
        Math.pow(2992 /
                 (Math.pow(ppO2, 2.23) * Math.pow(40 / ppCO2, 2.29) + 2992),
                 1.69);
    return satO2 * getO2Capacity(blood);
  }
  /**
   * Calculate the concentration of O2 in a gas with a given partial pressure
   * of O2 and CO2 (in mmHg), given the current blood parameters
   *
   * @return in ml/L
   */
  private double cO2(double ppO2, double ppCO2){
    return cO2(ppO2,ppCO2, blood);
  }

  /**
   * Calculate the concentration of CO2 in a gas with given partial pressures
   * of O2 and CO2 (in mmHg)
   * @return in ml/L
   */
  private static final double cCO2(double ppO2, double ppCO2) {
    return (1 - 0.000978 * ppO2) * (4.40 * ppCO2 + 384.3 * (1 - 3.82 / ppCO2));
  }

  /**
   * Estimation function for concentrations of gas at a given set of PPs
   * @return estimated concentration of O2 in L/L
   */
  public double estimateConcO2(double ppO2, double ppCO2) {
    ppO2*=1000; ppCO2*=1000;
    return cO2(ppO2, ppCO2)/1000;
  }

  /**
   * Estimate the concentration of oxygen at a given set of partial pressures of
   * oxygen and CO2, and a given set of blood parameters.
   * @param ppO2 partial pressure of O2
   * @param ppCO2 partial pressure of CO2
   * @param blood the blood parameters
   */
  public static double estimateConcO2(double ppO2, double ppCO2, Blood blood){
    return cO2(ppO2*1000,ppCO2*1000,blood);
  }

  /**
   * Estimate the concentration change of oxygen when the oxygen tension moves
   * between the two given partial pressures.
   * Also needs to know the current CO2 tension, and the red cell parameters
   * (i.e. RCM, MCV, Hct, Hb)
   * @param ppO2from initial partial pressure of O2
   * @param ppO2to final partial pressure of O2
   * @param ppCO2 partial pressure of CO2 (assumed constant during the change process)
   * @param blood the instance of blood that will determine the haemoglobin.
   * @return the change in concentration moving from ppO2from to ppO2to. The
   * result is negative is O2 will be lost from the blood.
   */
  public static double estimateO2ConcChange(double ppO2from, double ppO2to, double ppCO2, Blood blood){
    return estimateConcO2(ppO2from,ppCO2, blood) - estimateConcO2(ppO2to,ppCO2,blood);
  }

  /**
   * Estimation function for concentrations of gas at a given set of PPs
   * @return estimated concentration of CO2 in L/L
   */
  public double estimateConcCO2(double ppO2, double ppCO2) {
    ppO2*=1000; ppCO2*=1000;
    return cCO2(ppO2, ppCO2)/1000;
  }

  /**
   * Saturation of Oxygen as as a fraction 0-1
   * @return saturation of oxygen in this gas
   */
  public double getSatO2() {
    return O2.get() / getO2Capacity();
  }
  /**
   * Change the percentage of erythrocytes in this concentration
   * @param Hct the new heamatocrit variable
   */
  public void setBlood(Blood blood) {
    this.blood=blood;
  }
  /**
   * Get the current percentage of erythrocytes in this concentration.
   * @return The current haematocrit variable
   */
  public Blood getBlood() {
    return blood;
  }
}
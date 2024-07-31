package phic.common;
import phic.*;
/**
 *  Gases with concentration. This extension of phic.Gas has the values as
     *  concentrations. The concentration in l/l (in a liquid) can be converted into
 *  partial pressure. If the concentration is for a container with haemoglobin
 *  carrying the oxygen, the haematocrit variable can be specified in the
 *  constructor too, and the relavant fractional conversion is performed.
 */
public class GasConc
    extends Gas {
  /** The tables for conversion are in pO2Table.txt and pCO2Table.txt */
  public static TwoVarTable pO2Table = new TwoVarTable("pO2Table.txt", 14),
      pCO2Table = new TwoVarTable("pCO2Table.txt", 13);

  Blood blood = null;
  /**
   *  Gas constructor with with haematocrit correction. If the container
   * does not require haematocrit correction, use null.
   * @param blood the object requires the haematocrit of the container for this gas conc.
   */

  public GasConc(Blood blood) {
    this.blood=blood;;
    pp.O2=PO2;
    pp.CO2=PCO2;

  }

  /** Calculate the partial pressure of oxygen in the gas. */
  public VDoubleReadOnly PO2 = new VDoubleReadOnly() {
    public double get() {
      return PO2();
    }

  };

  public VDoubleReadOnly PCO2 = new VDoubleReadOnly() {
    public double get() {
      return PCO2();
    }

  };
  /** A Gas object referring to the PO2 and PCO2 */
  protected Gas pp = new Gas();
  /** Return a gas object referring to the PO2 and PCO2 */
  public Gas getPartials(){
    return pp;
  }

  /**
   * Calculate p02 from internal O2 and CO2 concentration
   * lookup from the pO2Table
   * correct for Hb and carbon monoxide concentration from the blood
   */
  private double PO2() {
    // convert to ml/l
    double o = O2.get() * 1000, c = CO2.get() * 1000;
    if (blood != null) { // correct oxygen concentration for haematocrit and carbon monoxide
      o *= 0.45 / ( blood.Hct.get() - blood.CO.get());
    }
    /** @todo this is fudged */
    return pO2Table.lookUp(o, 484) / 10000;
  }

  /**
   * Calcluate the partial pressure of carbon dioxide in the gas.
   * @return partial pressure O2 in mHg.
   */
  private double PCO2() {
    double o = O2.get() * 1000, c = CO2.get() * 1000;
    if (blood != null) { //correct oxygen concentration for haematocrit
      o *= 0.45 / blood.Hct.get();
    }
    return pCO2Table.lookUp(o, c) / 10000;
  }

  public VDouble SatO2 = new VDouble() {
    public double get() {
      double o = O2.get() * 1000;
      if (blood != null) {
        o *= 0.45 / blood.Hct.get();
      }
      //20/11/3 was max 204.5
      return Math.min(1, o / 203);
    }
    public void set(double d){
      O2.set(203 * d * blood.Hct.get()/0.45 / 1000);
    }
  };

  public void setBlood(Blood blood) {
    this.blood=blood;
  }
  public Curve O2Dissociation = new DissocCurve();
  public class DissocCurve extends Curve{
    public double getValue(double x){
      x*=1000;
      double c = CO2.get() * 1000;
      if (blood != null) { //correct oxygen concentration for haematocrit
        x *= 0.45 / blood.Hct.get();
      }
      return pO2Table.lookUp(x,c)/10000;
    }
    public double getInverse(double y){
      throw new IllegalStateException("o2 dissoc: noninvertible function");
    }
  };

}

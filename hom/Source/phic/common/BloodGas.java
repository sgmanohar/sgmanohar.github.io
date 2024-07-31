package phic.common;
import phic.*;
/**
 * Essentially this is a re-implementation of GasConc and GasConcentration,
 * but with PO2 and PCO2 as the 'known' quantities, and O2 and CO2 are
 * calculated.
 *
 * Not implemented yet, as lacking exact inverses
 * (needed for altering O2 and CO2 concentrations).
 */

public class BloodGas extends Gas {
  Blood blood;
  /** b may be null as long as setBlood() is called. */
  public BloodGas(Blood b) {
    blood=b;
    O2=new ClientO2();
    CO2=new ClientCO2();
    PO2=new VDouble();
    PCO2=new VDouble();
  }
  VDouble PO2 = new VDouble(), PCO2 = new VDouble();

  public void setBlood(Blood b){ blood=b; }

  class ClientO2 extends VDouble{
    public double get(){
      return 0;
    }
    public void set(double value){

    }
  }
  class ClientCO2 extends VDouble{
    public double get(){
      return 0;
    }
    public void set(double value){

    }
  }
}
package phic;

import java.util.*;

import phic.common.*;
import phic.drug.*;
import phic.drug.DrugContainer;


/**
 * An implementation of Fat. Its mass is calculated from the volume and density.
 * Its values are stored mainly in the private container theDrugs, and calls are
 * chained to this.
 */

public class Fat extends PerfusedOrgan {


  public void tick(){
    equilibrateDrugs();
  }

  /**
   * This represents the drugs dissolved in this Fat. It is private because
   * most of the methods implemented in it are not valid for fat. E.g., adding
   * a water-based fluid to fat would increase the volume of fat; and most of
   * the usual container's constituents (salts, urea etc.) are not modelled.
   */
  private DrugContainer theDrugs = new DrugContainer();

  /**
   * The relative density of fat. (Relative to water.)
   * measured in kilograms per litre etc.
   */
  private final double DENSITY = 0.8;

  /** The mass is represented in the volume of the container theDrugs. */
  public VDouble mass=new VDouble(){
    public double get(){
      return theDrugs.volume.get() * DENSITY * 1000;
    }
    public void set(double m){
      theDrugs.volume.set(m / DENSITY /1000);
    }
    public void add(double m){
      theDrugs.volume.add(m / DENSITY /1000);
    }
  };

  /** This chains to the DrugContainer's method of the same name. */
  public final double getDrugBinding(Object property){
    return theDrugs.getDrugBinding(property);
  }

  /**
   * This equilibrates drugs from plasma with the Fat drug container. The
   * rate of equilibration is determined by the drug's LIPID_SOLUBILITY
   * and the blood flow to the fat.
   */
  void equilibrateDrugs(){
    //Get the blood drugs
    Vector bds=body.blood.drugqs;
    for(int i=0;i<bds.size();i++){
      DrugQuantity bd=(DrugQuantity)bds.get(i),
        fd=theDrugs.findOrCreateMatchingDrug(bd);
      equilibrate(bd, fd);
    }
  }

  /**
   * Equilibrates two drug quantities between blood and fat.
   * bQ and fQ MUST be the same type of drug!
   * The 'solubility' should be interpreted as a partition coefficient.
   *
   * @param bQ the DrugQuantity in blood
   * @param fQ the DrugQuantity in fat
   */
  void equilibrate(DrugQuantity bQ, DrugQuantity fQ){
    if(!bQ.isSameDrug(fQ)) throw new IllegalArgumentException(
        "Cannot equilibrate two different drugs "+bQ+" and "+fQ);
    double sol = bQ.getProperty(bQ.LIPID_SOLUBILITY);
    if(sol==0)return;
    // transfer rate from blood to fat, if concs were fully equilibrated.
    double transferRate = flow.get() * (bQ.getC() - fQ.getC()/sol);
    double tx = transferRate * fractionDecayPerMinute(0.8);
    bQ.addQ(-tx);
    fQ.addQ(tx);
  }

  public void reset(){
    theDrugs=new DrugContainer();
  }

  public DrugContainer getDrugContainer(){
    return theDrugs;
  }
}

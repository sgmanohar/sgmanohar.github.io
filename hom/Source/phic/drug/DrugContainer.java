package phic.drug;

import java.util.Vector;
import phic.common.Container;

/**
 * An extension of Container that can take DrugQuantities as extra substances.
 * Overrides certain Container methods, so that drugs can be added and removed
 * to and from DrugContainers.
 */
public class DrugContainer
    extends Container {
  /** A vector of DrugQuantity objects representing the quantities of drugs */
  public Vector drugqs = new Vector();

  protected final DrugQuantity dq(int i) {
    return (DrugQuantity) drugqs.get(i);
  }

  public DrugContainer() {
  }

  /**
   * This allows an organ to 'show a surface receptor' to this container.
   * and see what binds. This is a product of the property value and the
   * concentration of drug, for each drug.
   * Sum ( DrugConcentration * ValueOf( Property ) )
   */
  public final double getDrugBinding(Object property) {
    double activity = 0;
    for (int i = 0; i < drugqs.size(); i++) {
      DrugQuantity d = dq(i);
      activity += d.getProperty(property) * d.get();
    }
    return activity;
  }

  /** Overrides to withdraw drugs too */
  public Container withdrawVol(double volume) {
    //call parent withdrawVolume, and place contents in a DrugContainer
    DrugContainer c = new DrugContainer();
    c.add(super.withdrawVol(volume));
    //then add the drugs to the new container
    for (int i = 0; i < drugqs.size(); i++) {
      c.addDrug(dq(i), dq(i).getC());
    }
    return c;
  }

  public void withdrawVol_overwrite(double volume, Container into) {
    super.withdrawVol_overwrite(volume, into);
    if(into instanceof DrugContainer){
      DrugContainer dinto = (DrugContainer)into;
      dinto.drugqs.removeAllElements();
      for (int i = 0; i < drugqs.size(); i++) {
        dinto.addDrug(dq(i), dq(i).getC());
      }
    }
  }

  public void withdrawFrac_overwrite(double fraction, DrugContainer into) {
    double frac = Math.max(Math.min(fraction, 1), 0);
    withdrawVol_overwrite(vol * frac, into);
  }

  public void ultraFiltrate_overwrite(double volume, DrugContainer into) {
    withdrawVol_overwrite(volume, into);
    into.filterSolidsInto(this);
    into.prot.moveTo(prot);
  }

  /**
   * Should only be called if no drug already exists of the same kind!
   * This is an internal method only.
   */
  protected DrugQuantity addDrug(DrugQuantity typeOfDrug, double concentration) {
    DrugQuantity d = new DrugQuantity(this, typeOfDrug);
    d.setC(concentration);
    drugqs.add(d);
    return d;
  }

  /**
   * Overrides to dilute drugs too.
   * Was protected. Should be protected.
   */
  public synchronized void increaseVolume(double volume) {
    volume = Math.max(volume, -this.vol);
    double cfrac;
    if (volume + this.vol <= 0) {
      cfrac = 0; //if removing all fluid, set fraction to zero
    }
    else {
      cfrac = this.vol / (this.vol + volume);
      //call parent increaseVolume
    }
    super.increaseVolume(volume);
    //then dilute the concentrations of drugs accordingly
    for (int i = 0; i < drugqs.size(); i++) {
      dq(i).multiplyC(cfrac);
    }
  }

  public final DrugQuantity findMatchingDrug(DrugQuantity toMatch) {
    for (int j = 0; j < drugqs.size(); j++) {
      if (toMatch.isSameDrug(dq(j))) {
        return dq(j);
      }
    }
    return null;
  }

  public final DrugQuantity findOrCreateMatchingDrug(DrugQuantity toMatch) {
    DrugQuantity t = findMatchingDrug(toMatch);
    if (t != null) {
      return t;
    }
    return addDrug(toMatch, 0);
  }

  /**
   * Add all of a container 'from' into this container. Locks 2 containers at once.
   * Extension of normal container function, adding drugs too.
   */
  public void add(DrugContainer from) {
    Object lock1 = this, lock2 = from;
    if (hashCode() > from.hashCode()) {
      lock1 = from;
    }
    lock2 = this;
//    synchronized (lock1) {
//      synchronized (lock2) {
        increaseVolume(from.vol);
        int n = compatibleQSize(from);
        for (int i = 0; i < n; i++) { //do normal quantities
          substance(i).addQ(from.substance(i).getQ());
          from.substance(i).setQ(0);
        }
        for (int i = 0; i < from.drugqs.size(); i++) {
          boolean found = false;
          for (int j = 0; j < drugqs.size(); j++) { //for each drug in 'from',
            if (from.dq(i).isSameDrug(this.dq(j))) { //find a matching drug in this
              //and transfer from to this
              this.dq(j).addQ(from.dq(i).getQ());
              found = true;
              break;
            }
          }
          if (!found) { //otherwise, add after recalculating concentration
            addDrug(from.dq(i), from.dq(i).getQ() / this.volume.get());
          }
          from.dq(i).setQ(0);
        }
        from.vol = 0;
//      }
//    }
  }

  /**
   * This overrides the usual Container.add(Container) and checks at runtime
   * whether the source container is a DrugContainer. If so, it reroutes to
   * DrugContainer.add(DrugContainer). Otherwise the original add(Container)
   * is called.
   */
  public void add(Container from) {
    if (from instanceof DrugContainer) {
      add( (DrugContainer) from);
    }
    else {
      super.add(from);
    }
  }

  /**
   * Overrides the Container.add(Container, volume), replacing it with
   * a slower, but drug-friendly routine.
   */
  public void add(Container from, double volume) {
    if (volume < 0 && this.solids.getC() > Math.abs(volume)) {
      throw new RuntimeException("Withdrawal of " + Math.abs(volume) +
                                 "L from "
                                 + this.vol + "L, " + this.solids.getQ() +
                                 " of which is solids.");
    }
    boolean fromDrug = from instanceof DrugContainer;
    //this can be done, but let's avoud it for now:
    if (fromDrug) {
      throw new RuntimeException("A DrugContainer has called add(DrugContainer"
                                 +
          ",volume). Please use withdrawVol() then add() instead.");
    }
    Object lock1 = this, lock2 = from;
    if (hashCode() > from.hashCode()) {
      lock1 = from;
    }
    lock2 = this;
//    synchronized (lock1) {
//      synchronized (lock2) {
        double fromvol = from.volume.get();
        volume = Math.min(volume, fromvol); //cannot add more than present in source container.
        double p = volume / fromvol; //proportion of source container. always <= 1
        for (int i = 0; i < qs.size(); i++) {
          double t = p * from.substance(i).getQ();
          // If removing substance, is there enough in this container?
          if (p < 0) {
            t = Math.max(t, -substance(i).getQ());
            // move substance
          }
          this.substance(i).setC( (substance(i).getQ() + t) /
                                 (this.vol + volume));
          from.substance(i).setC( (from.substance(i).getQ() - t) /
                                 (fromvol - volume));
        }
        // go through our drugs, diluting them add adding any extra from from
        Vector fromFound = new Vector(); // which of the 'from' drugs do we also have?
        for (int i = 0; i < drugqs.size(); i++) {
          DrugQuantity thisq = (DrugQuantity) drugqs.get(i);
          double t = 0;
          if (fromDrug) {
            DrugContainer fromd = ( (DrugContainer) from);
            for (int j = 0; j < fromd.drugqs.size(); j++) {
              DrugQuantity fromq = (DrugQuantity) fromd.drugqs.get(j);
              if (thisq.isSameDrug(fromq)) {
                t = p * fromq.getQ();
                if (p < 0) {
                  t = Math.max(t, -thisq.getQ());
                }
                fromq.setC( (fromq.getQ() - t) / (fromvol - volume));
                fromFound.add(fromq);
                break;
              }
            }
          }
          thisq.setC( (thisq.getQ() + t) / (this.vol + volume));
        }
        //now add any drugs that we don't already have. this makes no sense if p<=0
        if (fromDrug && p > 0) {
          DrugContainer fromd = (DrugContainer) from;
          for (int i = 0; i < fromd.drugqs.size(); i++) {
            DrugQuantity fromq = (DrugQuantity) fromd.drugqs.get(i);
            //check we haven't already got this
            if (fromFound.contains(fromq)) {
              continue;
            }
            addDrug(fromq, p * fromq.getQ() / (this.vol + volume));
          }
        }
        // update new volumes
        from.volume.set(fromvol - volume);
        this.vol += volume;
//      }
//    }
  }

  /**
   * This will override Container.empty() to remove drugs as well
   */
  public void empty() {
    super.empty();
    /* Method 1, removed because we want to get rid of any residual
     * drugs completely, they slow down the computations:
     for(int i=0;i<drugqs.size();i++)
     dq(i).setC(0);
     * Method 2, quicker and simpler. */
    drugqs.removeAllElements();
  }

  /**
   * Overrides set(Container) to copy drug concentrations too, if it
   * is a drug container.
   * @deprecated this should not be used.
   */
  public void set(Container from) {
    super.set(from);
    drugqs.removeAllElements();
    if (from instanceof DrugContainer) {
      DrugContainer d = (DrugContainer) from;
      for (int i = 0; i < d.drugqs.size(); i++) {
        //drugqs.add(d.drugqs.get(i).clone());
      }
    }
  }

  /**
   * Override the container default osmolarity method, and include the osmolarity
   * of drugs with OSMOTIC_EFFECT.
   */
  protected double getOsmolarity() {
    return H.getC() + glucose.getC() + urea.getC() + prot.getC() + 2 * Na.getC() +
        2 * K.getC()
        + getDrugBinding(Drug.OSMOTIC_EFFECT);
  }
}

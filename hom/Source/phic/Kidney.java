package phic;

import java.util.Iterator;
import phic.common.*;
import phic.drug.*;

/**
 * Kidneys: a most intricate organ to model. Removes ultrafiltrate from the circulation
 * every minute, and by passing it through system of containers, transfers most
 * back into the bloodstream. The remaining fluid is dumped to the bladder.
 * Also responsible for Aldosterone, Erythropoietin, Angiotensin.
 * @todo move erythropoiesis here
 */
public class Kidney
    extends PerfusedOrgan {
  /**
   * Concentration of sodium at the macula densa.
   * @todo MacDn Unused: should eventually determine Aldo
   */
  public VDouble MacDn = new VDouble();

  /**
   * Renal glucose transport maximum: The maximum rate at which glucose
   * can be reabsorbed in the proximal convoluted tubule.
   *
   * The rate is in moles per minute, and allows complete reabsorption of
   * 10 mM blood glucose level at a GFR of 120 mL a minute
   * Currently 0.00216 millimoles per minute
   */
  protected static final double GLUCOSE_TRANSPORT_MAXIMUM = 0.00225;

  /**
   * Glomerular filtration rate
   */
  public VDouble GFR=new VDouble();

  public void tick() {
    blood = body.blood;
    autoregulate();
    doNephron();
    hormones();
    waitMinutes(1);
  }

  /**
   * Regulation of renal blood flow: renal resistance increases with
   * the presence of angiotensin II.
   * Also calculate the blood flow deficit and decrease the tubular function
   * accordingly.
   */
  void autoregulate() {
    //used to be 1E9.
//!    resistance.regulateQuantity(body.blood.AngII, 1E12,
//!                                fractionDecayPerMinute(0.01));
    double deficit = Math.max(0,0.200-flow.get());
    //restore after a few days.
    tubularFunction.lowPass(1,fractionDecayPerMinute(0.0005));
    tubularFunction.addQuantity(- 0.01*elapsedTime/60 * deficit );
  }

  /**
   * Containers representing sections of the nephron
   */
  public Container glomerulus = new DrugContainer(), PCT = new DrugContainer(),
      Loop = new DrugContainer(), DCT = new DrugContainer(),
      urine = new DrugContainer();


  /**
   * Urinary ketone concentration in mol/L
   */
  public VDouble urinaryKetones = new VDouble();
  protected double urKetQ = 0;

  /**
   * The leakiness of glomeruli to protein - useful for simulating nephrotic
   * syndrome. This is a flow rate in litres/minute of unfiltered plasma across
   * the glomerular basement membrane.
   * Proteinuria of 3 g/day is equivalent to a leak of at least 260 microL per min
   * or 260e-6 L/min (with a normal plasma albumin)
   */
  public VDouble glomerularLeak = new VDouble();

  /**
   * Example of ideal fluid to remove from PCT in isotonic reabsorption. As
   * defined in Fluids.txt.
   */
  protected static final Container IDEAL_FLUID = Fluids.get("Ideal", 1);

  /**
   * @todo Will be moved to generic organ, in some way.
   */
  Blood blood;

  /**
   * Chain of events in the nephron:
   *<BR>
   *<PRE>  	getGlom		   doProx      doHenle	     doDist	  doDuct
   *      blood -----> glomerulus ------> PCT -------> Loop --------> DCT --------> urine --> bladder
   * 			     |		  |		 |	     |
   * 			  all glu      80% Na,K	    Aldo:2Na/K	  ADH:osm to 0.5M
   * 			 +90%ideal     +all but	    pHy :H excr	  H,HCO3
   *                           			     20ml H2O	 neutralised
   *
   *</PRE>
   *
   * Order of transit: blood, glomerulus, proximal convoluted tubule,
   * ascending loop of henle, distal convoluted tubule, collecting
   * duct, urine, bladder.
   */

  void doNephron() { //a chain where container contents are transferred
    //Preferred implementation:
    //in reverse order giving stepwise processing
    //note: not working because different volumes are being added and removed
    //from the blood in each timeframe, and causing unacceptable fluctuations
    //in blood volume.
    /*
     body.bladder.add(urine);
     urine.set( doDuct(DCT) );
     DCT.set( doDist(Loop) );
     Loop.set( doHenle(PCT) );
     PCT.set( doProx( glomerulus ) );
     glomerulus.set( getGlom(blood) );
     */
    //New implementation:
    //in forwards order giving unitary processing
    body.bladder.add(urine);
    urine.add(doDuct(doDist(doHenle(doProx(getGlom(blood))))));
    double uvol=urine.volume.get();
    urineflowrate = (uvol / (elapsedTime / 60));
    if(uvol>0)  urinaryKetones.set(urKetQ/uvol);
    else urinaryKetones.set(0);
  }
  private double urineflowrate = 0;

  /**
   * Aldosterone and angiotensin are released slowly:
   * Aldosterone is regulated against changes in blood potassium:
   *   An increase of 1M K+ gives an increase of 2E-7g/L aldosterone.
   *   rate = 0.1 per minute
   *  A decrease of 1M Na+ gives an increase of aldosterone, only for low Na.
   *  angiotensin II should affect aldosterone.
   *
   * Angiotensin II is negatively regulated against changes in body.ecf.volume:
   *   A change of 1L in ECF gives a change on -2E-9M angiotensin.
   *
   * @todo implement ACE inhibitors
   * @todo ACTH influences aldosterone
   * @todo implement AngII's effect on Aldosterone.
   */
  void hormones() {
//!    blood.Aldo.regulateQuantity(blood.PK, 2E-6, fractionDecayPerMinute(0.1));
    //		blood.Aldo.add( Math.max(0,
    //			(blood.Na.getError() + 0.020) * -2E-11 * elapsedTime/60) );
    //  Original:
    // blood.AngII.addQuantity(Math.max(0,-5E-11 * MacDn.getError()) * fractionDecayPerMinute(0.05));


    // altered  30/4/05 to allow fluid retention in nephrotic syndrome
//!    blood.AngII.regulateQuantity(body.ecf.volume, -2E-9,
//!                                 fractionDecayPerMinute(0.05)); //correct one

    //reduced 12/1/03 to -6E-10
    //blood.AngII.lowPassQuantity( Math.pow(10, -2*body.ecf.volume.getError() -10.7) ,
    //	fractionDecayPerMinute(0.3));
    //altered 19/6/3
    //blood.AngII.lowPassQuantity( 1E-9 - 1E-9* Math.pow(10,body.ecf.volume.getError()) ,
    //		fractionDecayPerMinute(0.3));
    //		blood.AngII.regulateQuantity(MacDn,  -2E-7, fractionDecayPerMinute(0.05));
  }

  /**
   * This is the target concentration of urine passing through the medulla;
   * It should be 1.2 Osm = 4 times plasma Osm. Currently set to 5 Osm.
   * It is currently constant. Actual medulla osmolarity is modulated by
   * loop diuretics.
   */
  public double maximumMedullaOsmolarity = 5.0;

  /**
   * Factor that the ADH effect is multiplied by, when calculating the amount of
   * osmosis out of the collecting duct. This controls how quickly ADH will return
   * POsm to its set-point.
   * Originally 1.0
   */
  public double adhPotency = 1;

  //Nephron components
  public boolean doProx = true, doDuct = true, doDist = true, doHenle = true;

  /**
   * Collecting duct absorption
   *   <BR> ADH dependent osmosis to blood
   *           determined also by the medullary osmolarity, which is dependent
   *           on loop of Henle reabsorption.
   *   <BR> Acid neutralised by bicarbonate
   *
   *  @todo implement equilibration of drugs that are lipid soluble. Currently
   *   circumventable by using a high value for RENAL_REABSORPTION.
   */
  protected Container doDuct(Container duct) {
    if (doDuct) {
      // osmose out water, ADH-dependent
      // loop reabsorption determines medulla osmolarity
      double adh = Math.max(blood.ADH.get()
                            + blood.getDrugBinding(Drug.ADH_RECEPTOR), 0);

      double fraction = Math.max(0, Math.min(1, 1-adhPotency * 4E-13 / adh));
      //24.6.5 was Math.max(0, Math.min(1, adhPotency * adh / 0.8E-11));
      //7/7/3 was adh/1.4E-11; 16/8/3 was adh/0.8E-11
      //double fraction = Math.min( adh / (1E-12 + adh) , 1);
      // originally Math.min( adh / (1E-14 + adh) , 1), changed to 1E-12
      // to make low ADH more potent diuretic!
      if(verbose)inform("osmosis "+fraction+" from duct");
      double medullaOsmolarity = maximumMedullaOsmolarity * Math.max(0.05,
          (1 + blood.getDrugBinding(Drug.LOOP_REABSORPTION)));
      duct.osmose(blood, medullaOsmolarity, fraction);

      // reabsorption or excretion of drugs
      DrugContainer dduct = (DrugContainer) duct;
      DrugQuantity dq = null;
      for (Iterator i = dduct.drugqs.iterator(); i.hasNext(); ) {
        dq = (DrugQuantity) i.next();
        double reabs = dq.getProperty(Drug.RENAL_REABSORPTION);
        if (reabs > 0) {
          dq.moveTo(blood.findMatchingDrug(dq), reabs * dq.getQ());
        }
        if (reabs < 0) {
          DrugQuantity bloodq = blood.findMatchingDrug(dq);
          bloodq.moveTo(dq,
                        -reabs * bloodq.get() * flow.get() * elapsedTime / 60);
        }
      }
      // H+ excretion proportional to plasma H+ concentration
      // currently 1 litre of plasma's worth of H+ is excreted!
      // per minute.
      // 30/9/02 200 mL of plasma H+ (2.0E-1)
      // 8/10 back to 1 L
      //duct.H.equilibrateConcentration(blood.H, fractionDecayPerMinute(1));
      //double error = blood.H.getError();
      //double transferRate = 50 * error * flow.get();
      //if(duct.volume.get()>0) blood.H.moveTo(duct.H, transferRate * elapsedTime/60);

      /** Ketone excretion */
      double excretedKetones =  Math.max(blood.ketones.getError(),0) * GFR.get() * 0.01 * elapsedTime/60;
      blood.ketones.add( -excretedKetones/blood.volume.get() );
      urKetQ = excretedKetones;
    }
    return duct;
  }

  /** @todo Private */
  public VDouble bicarbReabsRate = new VDouble();
  public VDouble HexcretionRate = new VDouble();
  public double RENAL_PH_SET_POINT = 7.4;

  /**
   * Distal convoluted tubule absorption
   *   <BR> Aldosterone dependent Na absorption and K excretion
   *   <BR> Water reabsorption to leave 13 ml
   *   <BR> Secretes H+ from 1 litre of plasma
   *           1 litre of plasma's worth of H+ ions are pumped out per
   *           minute.
   *
   */
  protected Container doDist(Container dist) {
    if (doDist) {
      // 2Na:1K aldosterone dependent pump
      double aldo = Math.max(blood.Aldo.get()
                             + blood.getDrugBinding(Drug.ALDOSTERONE_RECEPTOR),
                             0);
      //6/7/3 was /5E-11
      double pump = (Math.min(aldo / 32E-11, 1)) * Math.min(dist.Na.getQ() / 2,
          blood.K.getC() * flow.get() * elapsedTime / 60);
      dist.Na.moveTo(blood.Na, 2 * pump);
      blood.K.moveTo(dist.K, pump);

      //Reabsorb water to leave 13 ml/min
      double water = Math.max(0, dist.volume.get() - 0.013 * elapsedTime / 60);
      //water=Math.min(water, 5);  //impose limit on dist reabsorption
      //dist.addWater(-water); blood.addWater(water);
      //originally:
      dist.increaseVolume( -water);
      blood.increaseVolume(water);

      //reabsorb bicarb proportional to (1 - pH error)
      //double fractionBic =Math.min(1,(1 - 0.2*(Math.min(blood.pH.get()-RENAL_PH_SET_POINT , 0.4))));
      //dist.bicarb.moveTo(blood.bicarb, Math.max(dist.bicarb.getQ()
      //        * fractionBic , 0 ));



      // 24/6/3 now:
      //blood.H.moveTo(dist.H, 0.8 * blood.PHy.get() * elapsedTime/60); //rate 0.002
      //double totalbasedeficit = blood.PHy.getError() * blood.volume.get() + 1 * blood.PHy.get();
      //blood.H.moveTo(dist.H, fractionDecayPerMinute(0.001) * totalbasedeficit);
      //25/12/3 hexcretion was 4e-6*pH error lowpassed 0.02
      HexcretionRate.set( Math.min(1e-12,Math.max(0, 8E-6 * blood.PHy.getError())) );
      blood.H.moveTo(dist.H, HexcretionRate.get() * elapsedTime / 60);

      // H2CO3 ~= 0.00135 M ~= 45 mmHg CO2
      // reabsorption of bicarb proportional to CO2, in moles per L of filtrate
      //bicarbExcretionRate.lowPass((blood.PBic.getError()) + 10.0*blood.pH.getError() /* * (blood.H2CO3.get() / 0.00135 )*/,
      //                               fractionDecayPerMinute(0.1));
      //double bicarbReabsorptionRate = 0.024 / blood.H2CO3.get() / 0.00135;
      // quantity reabsorbed
      //double bicarbReabsorbed = dist.bicarb.getQ() - bicarbExcretionRate.get() * GFR *
      //    elapsedTime / 60;
      //dist.bicarb.moveTo(blood.bicarb, bicarbReabsorbed);
      //18/9/3 was bic - t * 0.1 * (pH-7.43)

      //reabsorb bicarb inv prop to pH 1/11/3
      //22.7.5 max rate was 1.10
      bicarbReabsRate.lowPass( Math.min(1.11,(1 - 2*blood.pH.getError())),  0.0002);
      double reabsFrac=bicarbReabsRate.get();
      //dist.bicarb.moveTo(blood.bicarb, Math.max(dist.bicarb.getQ()
      //       * Math.min(1,(1 - 30*blood.PBic.getError())) , 0 ));    //lowpass added 25/12/3
      dist.bicarb.moveTo(blood.bicarb, Math.max(dist.bicarb.getQ() * reabsFrac , 0 ));
      //Synthesis of bicarbonate within the tubule (20/2/4)
      blood.bicarb.addQ(0.00005*Math.max(0,(reabsFrac-1))*elapsedTime/60);






      // extra Na reabsorption added 23/6/3: now can reabsorb 0.8 then 0.9
      dist.Na.moveTo(blood.Na,
                     dist.Na.getQ() * 0.8 * angiotensinFraction *
                     angiotensinFraction);
      dist.Na.moveTo(blood.Na,
                     dist.Na.getQ() * 0.5 * angiotensinFraction *
                     angiotensinFraction
                     * angiotensinFraction);
      //dist.K.moveTo(blood.K, 0.8 * dist.K.getQ());
      /** @todo potassium or hydrogen to be exchanged for this sodium?? */
      //(1-Math.max(0,Math.min((aldo-1E-12) / 160E-12, 1)))* dist.K.getQ())
      dist.K.moveTo(blood.K, (1-Math.max(0,Math.min((aldo+170E-12) / 160E-12, 1)))
                    * dist.K.getQ());
    }
    return dist;
  }

  /**
   * Loop of henle absorption
   *   <BR> 80% of remaining Na and K reabsorbed.
   *           determined by drugs.
   *   <BR> Water reabsorption to leave 20 ml
   * @todo potassium does not seem to be excreted in a properly time-dependent
   * manner.
   */
  protected Container doHenle(Container henle) {
    if (doHenle) {
      //reabsorb 80% Na and 80% K
      double loopFrac = Math.min(1, Math.max(0,
                                             0.8 +
                                             blood.getDrugBinding(Drug.LOOP_REABSORPTION)));
      loopFrac *= tubularFunction.get();
      henle.Na.moveTo(blood.Na, loopFrac * henle.Na.getQ());
      henle.K.moveTo(blood.K, loopFrac * henle.K.getQ());
      //added 6/7/3 because K falls too quickly (was 1.0*K)
      henle.K.moveTo(blood.K, 1.0 * loopFrac * henle.K.getQ());
      //reabsorb all but 20ml/min water
      double waterReabs = Math.max(0,
                                   henle.volume.get() - 0.020 * elapsedTime / 60);
      //henle.addWater(-waterReabs); blood.addWater(waterReabs);
      //originally
      henle.increaseVolume( -waterReabs);
      blood.increaseVolume(waterReabs);

      //note sodium concentration that reaches macula densa
      MacDn.set(henle.Na.getC());
    }
    return henle;
  }

  /** A fraction dependent on AngII, indicating what proportion of reabsorption takes place */
  protected double angiotensinFraction;

  /**
   * Proximal convoluted tubule absorption.
   *   <BR> Glucose reabsorption upto transport maximum
   *   <BR> Angiotensin dependent reabsorption of ideal fluid
   */
  protected Container doProx(Container prox) {
    if (doProx) {
      //reabsorb up to 320mg/min glucose
      prox.glucose.moveTo(blood.glucose,
                          Math.min(GLUCOSE_TRANSPORT_MAXIMUM * elapsedTime / 60,
                                   prox.glucose.getQ()));
      // reabsorb up to 90% 'ideal' plasma (AngII dependent)
      // 0-100% for AngII = E-16 - E-12
      double angiotensin = blood.getDrugBinding(Drug.ANGIOTENSIN_II_RECEPTOR)
          + blood.AngII.get();
      angiotensin = Math.max(angiotensin, 0);
      /* original log scale for angiotensin:
      // 0.1 pM = 0
      // 1   pM = 0.5
      // 10  pM = 1
      double fraction = Math.min(Math.max(13 +
                                          Math.log(angiotensin) / Math.log(10),
                                          0),
                                 4) / 2;
      // that is, 0 < 0.5*(13+log(AngII)) < 0.9
      //was 0.9 (12/1/03)
      */
      double fraction = Math.max(Math.min(angiotensin * 1e11,1),0);
      angiotensinFraction = fraction = Math.min(fraction, 0.97);
      fraction *= tubularFunction.get();
      double reabsorbVolume = fraction * prox.volume.get();
      prox.withdrawFracII_overwrite(IDEAL_FLUID, fraction,proxAbsorbTemp);
      blood.add(proxAbsorbTemp);
    }
    return prox;
  }
  private DrugContainer proxAbsorbTemp = new DrugContainer();

  /**
   * Glomerular filtration:
   * <BR>  GFR = 2.252 * ( Glomerular pressure - Plasma colloidal osmotic pressure )
   * <BR>  Returns the appropriate volume of glomerular filtrate.
   *
   * @param renArtery The source of the filtrate.
   */
  protected Container getGlom(Container renArtery) {
    //Effective glomerular pressure
    double ap = body.CVS.APL.get();
    double tmp;
    //13.6.6 was pglom=AP^2/0.100 * max(ReRes-0.01,0.01) / ReRes / (1 + 0.4*max(VCT^3-1,0))
    double pglom = ap * ap/0.069 * Math.max(resistance.get() - 0.02, 0.01)
                                      / resistance.get()
                                      / (1 + 0.4 * Math.max(0, (tmp=body.CVS.VCT.get())*tmp*tmp-1));
    //GFR = (pglom - body.CVS.PCOP.get()) * 2.252; //original
    double gfr = (pglom - body.CVS.PCOP.get()) * flow.get() * 1.8;
    gfr = Math.max(overallRenalFunction.get() * gfr, 0); //cannot be negative!
    //need to ultrafilter more blood than filtrate needed, due to Hct
    renArtery.ultraFiltrate_overwrite(gfr / blood.AHct() * elapsedTime / 60, filtertmp);
    double leakVol=glomerularLeak.get();
    if(leakVol>0){
      Container leak = renArtery.withdrawVol(leakVol / blood.AHct()*elapsedTime / 60);
      renArtery.add(leak.filterSolids());
      filtertmp.add(leak);
    }
    GFR.set(gfr);
    return filtertmp;
  }
  private DrugContainer filtertmp = new DrugContainer();

  /**
   * This is a shortcut to urine.volume, representing the amount of urine
   * produced per minute. It is calculated by dividing the urine volume that
   * has been most recently produced by the time interval elapsed.
   */
  public VDoubleReadOnly urineFlow = new VDoubleReadOnly(){
    public double get(){    return urineflowrate;
  }};;

  /**
   * Overall renal function is the coefficient by which the GFR is multiplied by
   * when filtering blood. A value of 0.50, for example, represents nephrectomy.
   */
  public VDouble overallRenalFunction = new VDouble();
  public VDouble tubularFunction = new VDouble();

  public void reset() {
    glomerulus.empty();
    PCT.empty();
    Loop.empty();
    DCT.empty();
    urine.empty();
    bicarbReabsRate.set(0.986);
    HexcretionRate.set(1E-8);
    urinaryKetones.set(0);
    proxAbsorbTemp.empty();
  }
}

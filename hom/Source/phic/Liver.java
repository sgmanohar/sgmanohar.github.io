package phic;

import phic.common.*;
import phic.drug.*;


/**
 * Implements the metabolic functions of the liver.
 *
 * 1. Excess blood protein broken down into urea. (1 protein -> 100 urea)
 * 2. Metabolism (MBR) uses extracellular glucose.
 * 3. ECF and blood glucose equilibrated by passive diffusion.
 * 4. Excess stored as glycogen; Excessive excess stored as fat.
 *    If insufficient blood glucose, glycogen then fat mobilised
 *    at limiting rate of 10% of store per minute.
 * 5. Metabolism of drugs: first pass metabolism from portalVein
 *    and metabolism of drugs from blood from visceral flow
 * @todo replace acid secretion when acid-base balance is sorted.
 */


public class Liver extends Organ {
  public Liver() {
  }

  /**
   * Gets the portal blood from the gut,
   * puts extracts amino acids then mixes with systemic blood.
   * Drugs metabolised, Plasma protein broken down and synthesised,
   * then glucose/glycogen/fat storage.
   */
  public void tick() {
    portalVein=Current.body.gitract.portalVein;

    //digest protein
    aminoAcidQ += portalVein.prot.getQ();
    portalVein.prot.setC(0);

    //Mix portal blood with systemic blood
    metaboliseDrugs(portalVein, null);
    body.blood.add(portalVein);

    correctPlasmaProtein();

    sugars();
    metaboliseDrugs(body.blood, body.viscera.flow);
  }

  DrugContainer portalVein;
  /**
   * Controls liver production of protein from amino acids
   */
  public VDouble syntheticFunction = new VDouble();
  /**
   * Controls liver metabolism of amino acids, ammonia
   */
  public VDouble metabolicFuction = new VDouble();



  /**
   * Initial and set-point size of amino acid stores
   */
  protected double aminoAcidReserve = 0.5;

  /**
   * Obligatory rate of amino acid breakdown, in Osm of protein per minute
   * protein is 14% nitrogen w/w.
   */
  public double obligatoryAABreakdown = 0.23e-3;

  /**
   * Maximum rate of protein synthesis, given enough amino acids.
   * in osmoles per minute.
   * a value of 313 microOsm/minute corresponds to 3 grams per day.
   */
  public double maximumProteinSynthesisRate = 1.75e-6;

  /**
   * Quantity of amino acids stored - it is eventually used for producing protein.
   * Its units are Osm of protein.
   * @todo should be private
   */
  public double aminoAcidQ = aminoAcidReserve;

  public void reset(){
    aminoAcidQ = aminoAcidReserve;
  }

  /**
   * Calculates total body glucose use, and removes from blood.
   * Equilibrates ECF and blood glucose.
   * Glucose excess converted to/from glycogen depending on Insulin
   * Further glucose excess converted to/from fat depending on Insulin.
   * RQ calculated based on fat breakdown rate
   *
   */
  void sugars() {

    Body b = body;
    criticalPeriod.enter();
        //Sugar use
        // 1E-9 mol/min glucose -> 1 Cal/d ?
        // now added metabolic water production. 1 mole H20 = 18 g
        double glucoseUseRate = 1E-9 * b.MBR.get();
        double molesGlucoseUse =  glucoseUseRate * elapsedTime / 60;
        b.ecf.glucose.addQ( -molesGlucoseUse); //deplete ECF glucose
        b.ecf.addWater(6 * molesGlucoseUse * 0.018);
        //equilibrate ECF & blood glucose
        double R = (b.blood.glucose.getC()
                    - b.ecf.glucose.getC()) * fractionPerMinute(0.9);
        //correct version
        R /= b.blood.volume.get() + b.ecf.volume.get();
        double Q = R * b.blood.volume.get() * b.ecf.volume.get();
        // Semi-correct version?
        //double Q = R * 6/7; //originally
        b.blood.glucose.moveTo(b.ecf.glucose, Q);
        //b.blood.glucose.setC( b.blood.glucose.getC() - R*6/7 );
        //b.ecf.glucose.setC( b.ecf.glucose.getC() + R*1/7);


        // Store excess blood glucose
        double effectiveInsulin = b.blood.Insul.get()
            + b.blood.getDrugBinding(Drug.INSULIN_EFFECT);
        //target decrease in concentration
        double gluConcDecrease = b.ecf.glucose.getError() * fractionDecayPerMinute(0.3);
        //Corrected so that breakdown not insulin dependent
        //8/1/03 Corrected to allow breakdown faster than needed if extra insulin present
        //assimilation is insulin dependent (permissive effect)
        if (gluConcDecrease > 0) {
          gluConcDecrease *= Math.min(effectiveInsulin * 0.5, 2);
        }
        else {
          /**
           * added to stop overcorrection when insulin present
           * additive effect
           */
          //gluConcDecrease = gluConcDecrease * 0.3;
        }
        /**
         * Insulin's pro-glycogenolytic action:
         * glucose is produced from glycogen whenever insulin very low (only
         * significant in diabetes), irrespective of glucose,
         * giving rise to DKA. added 17/8/3 converts 0.015 * (0.01-insulin).
         */
        if(effectiveInsulin<0.01) gluConcDecrease -= 0.02*(0.01 - effectiveInsulin) * elapsedTime/60;
          /**
           * Insulin increases storage of glucose
           */
        gluConcDecrease += 0.001 * Math.min(effectiveInsulin, 1) * elapsedTime / 60;
        //originally:
        //decr *= Math.min(effectiveInsulin * 0.5, 0.5);
        gluStore(gluConcDecrease, b.Glycogen);


        //note the total fat made into glucose and glycogen
        double fatBreakdownRate = 0;

        //fat buffering
        /** @todo notice that this is all false, because Fat is converted to glucose! */
        gluConcDecrease = b.ecf.glucose.getError() * fractionDecayPerMinute(0.02);
        //added insulin dependence for fat assim 18/10/02
        //if(decr>0) decr *= Math.min(effectiveInsulin * 3, 1);
        //8/1/03 Also corrected to allow purely insulin-dependent assimilation
        //assimilation is insulin dependent (permissive effect)
        if (gluConcDecrease > 0) {
          gluConcDecrease *= Math.min(effectiveInsulin * 0.5, 2);
        }
        else {
          //gluConcDecrease = gluConcDecrease * 0.2; // added to stop overcorrection when insulin present
          // additive effect
        }
        /** Effect of low insulin on causing catabolism of fat */
        if(effectiveInsulin<0.01) gluConcDecrease -= 0.018*(0.01 - effectiveInsulin) * elapsedTime/60;

        /** effect of insulin directly on fat uptake of glucose */
        //gluConcDecrease = gluConcDecrease + 0.001 * Math.min(effectiveInsulin, 1) * elapsedTime / 60;

        gluStore(gluConcDecrease, b.Fat.mass);
        fatBreakdownRate = -gluConcDecrease * body.ecf.volume.get() * 179 /elapsedTime*60;

        /**
         * glycogen -> fat
         * added 20/7/3: Conversion between fat and glycogen
         * regulated against Glycogen.error()
         * Rate was 0.01 * glyError per minute.
         */
        double glycogenConversionRate = Math.max(Math.min(
            body.Glycogen.getError() * 0.00025,
            body.Glycogen.get()  // disallow conversion of glycogen to fat
            ), -body.Fat.mass.get());
        body.Glycogen.add( -glycogenConversionRate* elapsedTime/60);
        body.Fat.mass.add(glycogenConversionRate* elapsedTime/60);
        //fatBreakdownRate -= Math.min(glycogenConversionRate,0) ; // don't count fat synthesis as negative breakdown of fat!

        /**
         * Ketone production
         */
        body.blood.ketones.regulateQuantity( body.blood.Insul, -0.2, fractionDecayPerMinute(0.0003));

        /**
         * Calculate effective resp quotient from fat breakdown
         * @todo Re-implement all of this using pyruvate: glucose cannot be resynthesised
         * from fat. Under anaerobic conditions and during fasting pyruvate is used
         * with an RQ of 0.71
         * rate constant = 0.004 per minute
         */
        double fracFat = Math.min(1,Math.max(0,fatBreakdownRate / glucoseUseRate));
        body.RQ.lowPassQuantity( 1-0.29*fracFat, fractionDecayPerMinute(0.004) );

    criticalPeriod.exit();

  }

  /**
   * This breaks down plasma protein depending on the amount of excess protein,
   * converting it into amino acids (added to those from the portal vein),
   * and then synthesises new protein depending on the plasma protein deficit.
   * Excess amino acids are converted to urea and creatinine.
   */
  void correctPlasmaProtein() {
    Body b = body;
    double proteinExcess = b.blood.PPr.getError();
    if (proteinExcess > 0) {
      //break down plasma protein into amino acids as needed
      //rate was 1% per minute, now increased.
      double breakdown = fractionDecayPerMinute(0.1) * proteinExcess;
      b.blood.setPlasma(b.blood.prot, b.blood.PPr.get() - breakdown);
      aminoAcidQ += breakdown;
    }
    else {
      //synthesise plasma protein from amino acids as needed: slower.
      double synthesise = -fractionDecayPerMinute(0.05) *
          (proteinExcess + 0.6e-3*body.brain.fever.get());
      synthesise = Math.min(synthesise, aminoAcidQ); // cannot produce more than available amino acids
      synthesise = Math.min(synthesise, maximumProteinSynthesisRate * elapsedTime/60);
      b.blood.setPlasma(b.blood.prot, b.blood.PPr.get() + synthesise);
      aminoAcidQ -= synthesise;
    }
    // amino acid breakdown
    double convert = Math.min(0.0001 * elapsedTime / 60, Math.max(0,
        aminoAcidQ - aminoAcidReserve));
    if(verbose) if(convert>0) inform("protein destroyed "+1000*convert/elapsedTime*60+" mOsm/min");
    // 1 prot -> 100 urea + 100 creat
    b.blood.PUN.add( 1.3 * convert + obligatoryAABreakdown * elapsedTime/60 );
    /** @todo proportional to muscle bulk */
    b.blood.PCreat.add( 0.01 * convert + obligatoryAABreakdown*0.01 * elapsedTime/60 );
    // 1 prot -> 0.001 inorganic acid
    //b.blood.setPlasma(b.blood.H, b.blood.PHy.get() + 0.001 * /* convert* */ 0.00000002);
    aminoAcidQ -= convert;
  }

  /**
   * Metabolise drugs
   * at present, the HEPATIC_METABOLISM is the fraction of the total
   * amount of drug that passes through the liver that is removed.
   * ceiling values are not supported yet, although most real drugs have this
   * property...
   */
  public void metaboliseDrugs(DrugContainer d, VDouble flow){
    for(int i=0;i<d.drugqs.size();i++){
     DrugQuantity dq=(DrugQuantity)d.drugqs.get(i);
     double hmet = dq.getProperty(Drug.HEPATIC_METABOLISM);
     if(hmet>0){
       double q;
       if(flow!=null)
         q = body.viscera.flow.get() * dq.getC() * hmet * elapsedTime/60;
       else
         q = dq.getQ() * hmet;
       dq.addQ(-q);
     }
   }
  }

  /**
   * Takes some of ECF.glucose and puts it in store, or the reverse.
   * Limit of 10% /min imposed on mobilisation.
   * @param decr The concentration change required.
   * needs external synchronizations
   */
  public void gluStore(double decr, Variable store) {
    Body b = Current.body;
    //equivalent mass of store, in g
    double masseq = decr * b.ecf.volume.get() * 179;
    if (masseq < 0) {
      //maximum rate at which glycogen/fat can be broken down:
      // 10% of current store per minute
      masseq = Math.max(masseq, -0.1 * elapsedTime / 60 * store.get());
      //recalculate concentration change
      decr = masseq / b.ecf.volume.get() / 179;
    }
    //store or deplete glucose
    store.set(store.get() + masseq);
    b.ecf.glucose.setC(b.ecf.glucose.getC() - decr);
  }

  /**
   * The endocrine pancreas, controlling insulin release. Blood.Insul
   * decays by 5% per minute.
   * Release governed by 1000 * (ECF glucose excess) +
   * stomach.glucose concentration > 1 Molar.
   */

}

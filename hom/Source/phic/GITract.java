
package phic;

import phic.common.*;
import phic.drug.*;

/**
 * Gastrointestinal tract, containing the elements:
 *
 *  <BR>   1. digestive system, i.e. stomach and colon
 *  <BR>   2. liver
 *  <BR>   3. endocrine pancreas.
 *  <BR>
 *
 * 1% of stomach solids, 0.1% of stomach fluids drain to colon per minute.
 * 1% of stomach fluids reabsorbed into blood per minute.
 */
public class GITract
    extends Organ {


  public void tick() {
    gut();
    pancreas();
    waitMinutes(1);
  }
  /**
   * A container representing the contents of the hepatic portal vein.
   * Substances absorbed from 'ileum' are added to this container, before
   * the liver metabolises these and returns the container to 'blood'.
   */
  DrugContainer portalVein = new DrugContainer();


  /**
   * The rate at which acid is secreted into the stomach. It is measured in in
   * moles per 10 seconds per unit of pH difference of the stomach from the set-point
   * of stomach pH.
   */
  private final double acidSecretionRate=3E-8;

  /**
   * This is the fraction of gastric fluid that is transferred from the stomach
   * to the ileum per minute, and also the fraction of ileal fluid that is
   * transferred from ileum to colon per minute.
   *
   * To simulate diarrhoea, this value can be increased.
   */
  public double motilityRate = 0.01;


  /**
   * This is the maximum volume of the stomach before nausea sets in.
   * It also determines the maximum size of a meal
   */
  public VDouble stomachCapacity = new VDouble();

  /**
   * This is the fractional decay per minute of carbohydrate into glucose
   */
  public double amylaseConcentration = 0.02;

    /**
   * Gut reabsorption : move food from stomach to colon, adding some to blood.
   * 1% of stomach ceontents are removed each minute; 1% of this and all
   * the solid is placed in the colon, the rest is reabsorbed into the blood.
   *
   */

  void gut() {
    /** if 1% removed per minute, how much in the elapsed time?
     * was simply 0.01...
     * @todo need to correct the approximation here
     */
    double motilityPercent = fractionDecayPerMinute(motilityRate);
    /** 4% per minute is 70% in half hour or 90% in 1 hr */
    stomach.digestFraction(fractionDecayPerMinute(amylaseConcentration));
    if (stomach.volume.get() > 0.005) {
      //take 1% of stomach contents, to maximum of 8ml per minute (31/6/02)
      //double volumeFromStomach = Math.min(0.01*stomach.volume.get(), 0.008);
      double volumeFromStomach = motilityPercent * stomach.volume.get();
      stomach.withdrawVol_overwrite(volumeFromStomach, ileum);
      // secrete bicarb (i.e. remove H+)
      ileum.H.moveTo( body.blood.H, /*ileum.volume.get() * */ 1.0 * ( ileum.H.get() - 1E-7 ) * fractionDecayPerMinute(0.4) );
      ileum.carbohydrate.moveTo(stomach.carbohydrate);
      //dump solid and 1% rest to colon
      abstmp.empty();
      ileum.withdrawFrac_overwrite(motilityRate, abstmp);
      ileum.filterSolidsInto(abstmp);
      colon.addAndDiscardExtras(abstmp);

      colonic_secretion_example.volume.set(colonSecretion.get() * elapsedTime/60);
      body.blood.withdrawVolExample_overwrite(colonic_secretion_example , abstmp);
      colon.add(abstmp);


      double ileumOverload = ileum.volume.get() - MAX_ABSORPTION_RATE*elapsedTime/60;
      if( ileumOverload > 0 ){
        ileum.withdrawVol_overwrite(ileumOverload, abstemp2);
        colon.add(abstemp2);
      }
      //reasorb the rest
      portalVein.add(ileum);
    }
    secreteAcid();
  }
  /** Maximum volume that can be absorbed by the gut in 1 minute */
  public double MAX_ABSORPTION_RATE = 0.010;
  /**
   *  The fluid type that is transferred from blood to colon in secretory
   * diarrhoea.
   */
  protected static final Container colonic_secretion_example = Fluids.get("ColonicSecretions", 1);
  /** The rate of secretion of fluid from the colon, in litres/minute */
  public VDouble colonSecretion = new VDouble();

  /** Ileum is a temporary storage while contents are transfered from stomach to colon */
  private FoodContainer ileum = new FoodContainer();
  /** abstemp is a temporary storage while food is absorbed from the ileum */
  private FoodContainer abstmp = new FoodContainer();
  /** abstemp2 is a temporary storage while food is absorbed in the ileum */
  private FoodContainer abstemp2 = new FoodContainer();

  /**
   * gastric acid secretion: attempt to keep gastric pH at the set point
   * by moving 'GastricSecretions' (in the fluids list) from blood to stomach.
   * added 16/6/3
   */
  protected void secreteAcid() {
    //removed 29/6/3 because upsetting acid base balance. was pH.error - 3E-8 * fractiondecay(0.4)
    parietalCell_example.volume.set(0.0017 * elapsedTime/60);
    body.blood.withdrawVolExample_overwrite(parietalCell_example, parietalCell_temp);
    stomach.add( parietalCell_temp );
    //body.blood.H.moveTo(stomach.H, stomach.pH.getError() * acidSecretionRate * fractionDecayPerMinute(0.1));
  }

  private Container parietalCell_temp = new Container();
  private Container parietalCell_example = Fluids.get("GastricSecretions", 1);

  /** Container representing the contents of the stomach. @see #gut */
  public FoodContainer stomach = new FoodContainer();

  /** Container representing the contents of the colon. @see #gut*/
  public Container colon = new DrugContainer();

  /**
   * Obligatory rate of urea production, above that of dietary protein excess,
   * in moles per minute
   * This urea comes from other protein reserves in the body.
   * @todo implement other protein reserves e.g. in muscle, that contribute to body mass
   * @todo private / final
   */
  //public double obligatoryUreaProduction = 0.23E-3;

  /**
   * Obligatory rate of creatinine production, above that of dietary protein excess,
   * in moles per minute
   * This comes from other protein reserves in the body.
   * @todo implement other protein reserves e.g. in muscle, that contribute to body mass
   * @todo private / final
   */
  //public double obligatoryCreatProduction = 4E-6;

  void pancreas() {
    Blood b = Current.body.blood;
    // Insulin concentration decays with time, by 5% per minute
    b.Insul.multiplyBy( fractionPerMinute(0.90) );
    double gluTol = glucoseTolerance.get();
    double ecfgluerr = body.ecf.glucose.getError();
    if (gluTol>0) {
      double secretedInsulinPerMinute =
          // negative feedback. 1% increase per mMol
          // error in glucose concentration
          ( ecfgluerr * 7.3                     // was 10.0   on 31/7/7
           // effect of food: +1% if stomach glucose >0.400M
           + ((ecfgluerr<-0.003)?0:(
             Math.max(0,Math.min( 0.006,
               (stomach.glucose.getQ() +
                stomach.carbohydrate.getQ()*stomach.MOLES_GLUCOSE_PER_GRAM_CARB - 0.400 ))) * 0.014
           + baselineInsulin)))
          * Math.pow(gluTol,3);
      double secretedInsulin = secretedInsulinPerMinute * elapsedTime / 60;
      b.Insul.addQuantity( secretedInsulin );
    }
  }

  /**
   * Indicates the patient's baseline insulin secretion rate. This should be
   * roughly the amount of insulin that will keep the blood glucose at 4 mmol/L
   */
  public double baselineInsulin = 0.005;

  /**
   * Measures the patients glucose tolerance. This alters the set-point for
   * insulin production; 0 is no insulin, 1 is normal insulin secretion
   */
  public VDouble glucoseTolerance=new VDouble();

  public void reset() {
    stomach.empty();
    colon.empty();
    portalVein.empty();
    motilityRate = 0.01;
  }
}

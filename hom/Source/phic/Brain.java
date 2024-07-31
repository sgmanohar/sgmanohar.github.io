package phic;

import phic.common.*;
import phic.drug.Drug;

/**
 * The brain performs many of the homeostatic functions, and also checks the
 * wellbeing of the other organs. The main homeostatic centres are currently
 *
 * 1) Respiratory centre to control breathing rate and volume
 *     (@see Lung#RespR and @see Lung#TidV)
 * 2) Autonomic centre to control the sympathetic tone @see Brain#Symp
 * 3) Posterior pituitary to release appropriate ADH (vasopressin)
 * 4) Hunger and thirst centres to control when Body.eat() and Body.drink() are
 *    called
 * 5) Micturition and defaecation centres to control when Body.wee() and
 *    Body.poo() are called.
 * 6) Conscious level determination, from blood oxygen and glucose.
 */
public class Brain
    extends PerfusedOrgan {
  /** Constants representing the conscious level of the patient */
  public static final int WELL = 0, ILL = 1, UNCONSCIOUS = 2, DEAD = 3;

  /** Current feeling of the patient */
  private int feeling = WELL;

  /** Becomes true when the patient is asleep. Controlled by setAsleep */
  private boolean asleep;

  /** Levels of current feelings */
  public VDouble hunger=new VDouble(),
      thirst=new VDouble(),
      nausea=new VDouble(),
      sedation=new VDouble();

  /**
   * Level of pain, from 0 to 1.
   */
  public VDouble pain = new VDouble();

  /** Sympathetic activation */
  public VDouble Symp = new VDouble();
  /** Capacity of rectum, litres */
  final double maxColonVol = 0.5;
  /** Capacity of bladder, in litres */
  final double maxBladderVol = 0.5;
  /** Fraction representing frequency of eating meals; 1.0 is normal */
  public VDouble greed = new VDouble();
  /** Fraction representing frequency of drinking water; 1.0 is normal */
  public VDouble thirstiness = new VDouble();

  protected double opiateBinding;

  /**
   * Call the functions of the brain.
   */
  public void tick() {
    opiateBinding = body.blood.getDrugBinding(Drug.MU_OPIATE_RECEPTOR);
    breathing();
    sympathetics();
    pituitary();
    //!thyroid();
    checkPain();
    checkFeelings();
    checkDesires();
    waitMinutes(1);
    conscious.tick(elapsedTime);
  }

  /** Temporary variable used to return how we are feeling */
  private String predicate = null;
  /** Temporary variable to return how we are feeling */
  private int newFeeling = WELL;
  /** Temporary store for cardiac ischaemia, calculated in checkFeelings, and used in Pain */
  double cardiacIsch = 0;
  /**
   * Checks vital parameters for wellbeing. Body temperature, Cardiac
   * output, arterial pressure, brain oxygenation, blood glucose,
   * haematocrit, stomach volume are used to determine
   * wellness and conscious level.
   * Desires are calculated: hunger volume increases with low blood glucose
   * and low stomach volume. Thirst volume increases with low blood volume
   * and high plasma sodium.
   */
  boolean checkFeelings() {
    int oldfeeling = feeling;
    String lastpredicate = predicate;
    predicate = null;
    newFeeling = WELL;
    if(!Current.thread.DEBUG_ERRORS && Current.thread.completedCycles<3) return false;
    //calculate feelings
    //Range used to be 32=unconscious, 22=dead.
    double bodyTemp = body.Temp.get();
    if (bodyTemp < 22) {
      feel(DEAD, "has died of hypothermia");
    }
    else if (bodyTemp < 28) {
      feel(UNCONSCIOUS);
    }
    else if (bodyTemp < 40) {

    }
    else if (bodyTemp < 44) {
      feel(ILL, "is delirious");
    }
    else if (bodyTemp >= 44) {
      feel(DEAD, "has died of hyperthermia");
    }
    else {
      feel(DEAD); // not a number
    }

    double co2=body.blood.arterial.PCO2.get();
    double pH=body.blood.pH.get();
    if(co2>.130) feel(UNCONSCIOUS);
    else if(co2>.080 && pH<7.2) feel(ILL, "is confused");
    if(co2<0.020 && pH>7.5) feel(ILL, "is feeling light-headed");

    //High output heart failure
    // removed 19/6/3: should be redundant since new starling curve implemented.
    //if(body.CVS.heart.CO()>140)  feel(DEAD, "has died of heart failure");

    //coronary ischaemia
    cardiacIsch=body.CVS.heart.ischaemia();
    if(cardiacIsch>0){
      feel(ILL, "has chest pain");
    }

    double icfVol = body.icf.volume.get();
    if (icfVol > 35.0) {
      feel(ILL, "is confused");
    }
    if (icfVol > 45.0) {
      feel(DEAD, "has died of cerebral oedema");
    }
    double ap = body.CVS.AP.get();
    if (ap > 0.150) {
      feel(ILL, "has a headache");
    }
    if (ap < 0.070) {
      feel(ILL, "is feeling faint");
    }
    if (ap > 0.200) {
      feel(DEAD, "has died of a brain haemorrhage");
    }
    double o2 = O2Supply.get();
    //Basic brain O2 requirements. requirement falls if Temp < 33
    double O2req = 0.065 * Math.min(1, Math.max(0.4, 1 + 0.2*(body.Temp.get()-33)));
    if (o2 < 1*O2req) {
      feel(ILL, "is drowsy");
    }
    if (o2 < 0.9*O2req) {
      feel(UNCONSCIOUS, "has become unconscious");
    }
    if (o2 < 0.8*O2req) { // Dies of cerebral hypoxia
      //congestive heart failure (sgm 10-6-02)
      double rap = body.CVS.heart.right.atrialP.get(), lap = body.CVS.heart.left.atrialP.get();
      if (rap > 0.009 && lap > 0.009) {
        feel(DEAD,
             "has died of cerebral hypoxia due to congestive cardiac failure");
      }else if(rap > 0.009) feel(DEAD, "has died of right ventricular failure");
      else if(lap > 0.009) feel(DEAD, "has died of left ventricular failure");
      else if (rap < 0.0020) {
        //dehydration (22/6/3)
        feel(DEAD, "has died of cerebral hypoxia due to hypovolaemia");
      }
      else {
        feel(DEAD, "has died of cerebral hypoxia");
      }
    }

    //Brain needs not only sufficient O2 flow, but also sufficient O2 tension
    //to supply cells across diffusion gradient. This is important at altitudes.
    double pO2=body.blood.arterial.PO2.get();
    if(pO2 < 0.038){ // about 5 kPa
      feel(UNCONSCIOUS);
    }else if(pO2 < 0.03){
      feel(DEAD, "has died of cerebral hypoxia");
    }

    if (body.blood.glucose.getC() < 0.003) {
      feel(ILL, "is feeling faint");
    }
    if (body.blood.glucose.getC() > 0.02) {
      feel(ILL, "is feeling bloated");
    }
    if (body.blood.glucose.getC() < 0.002) {
      feel(UNCONSCIOUS);
    }
    if (body.blood.glucose.getC() < 0.0002) {
      feel(DEAD, "has died of cerebral hypoglycaemia");
    }
    ///Nausea for large stomach volumes, salt in stomach, opiates, uraemia
    double naus=Math.max(0,body.gitract.stomach.volume.get()
                      - body.gitract.stomachCapacity.get())
        + 0.2 * body.gitract.stomach.Na.getQ()
        + 100 * opiateBinding
        + Math.min(1,Math.max(0,(body.blood.urea.get()-0.40)*80))
;
    nausea.set( naus );
    // was: feel sick if stomach.volume > 1.5 or stomach.Na > 1.5
    if (naus > 0.1) {
      feel(ILL, "feels nauseated");
    }
    //wsas: vomit if stomach.volume >2 or stomach.Na.Q > 8
    if (naus > 0.4) {
      Current.body.vomit();
    }
    if (body.blood.Hct.get() < 0.30) {
      feel(ILL, "has an anaemic complexion");
    }
    if (body.blood.Hct.get() < 0.35) {
      feel(ILL, "is feeling tired");
    }
    /** @todo transfer to heart? */
    if (body.blood.PK.get() > 0.009) {
      feel(DEAD, "has died from a fatal arrhythmia");
    }
    if (body.blood.PK.get() < 0.002) {
      feel(ILL, "is feeling weak");
    }
    if (body.blood.PK.get() < 0.0015) {
      feel(DEAD, "has died from fatal arrhythmia");
    }
    if (oldfeeling == UNCONSCIOUS && predicate == null) {
      feel(WELL, "has regained consciousness");
    }
    //opiate induced conscious level
    double sedn = opiateBinding * 2.7 +
        body.blood.getDrugBinding(Drug.GABA_RECEPTOR) * 1.0 +
        Math.max(0,(co2-0.05))*0.02 +
        hypoxia.get()*0.1 +
        body.blood.NH3.get() * 10;
    sedation.set(sedn);
    if (sedn > 0.0013) {
      feel(UNCONSCIOUS, "has become unconscious");
    }
    else if (sedn > 0.001) {
      feel(ILL, "is feeling drowsy");
    }
    // Seizures
    double ca=body.blood.Ca.get();
    double szthresh = Math.min(0,body.blood.PNa.get()-0.110) * 100 +
                      Math.min(0,ca-0.0020) * 2200 +
                      Math.max(0,ca-0.0025) * 2200 +
                      sedn * 600;
    if( szthresh < -0.5 ) feel(UNCONSCIOUS, "Has had a seizure");


    feeling = newFeeling;

    //Check whether the state has changed
    if (feeling == DEAD && oldfeeling < DEAD) {
      return true;
    }
    boolean changed = oldfeeling != feeling;
    // If it has changed, display the occurrence
    if ( (changed || lastpredicate != predicate) && predicate != null) {
      /** Suppress excessive messages: if more than 20 in 30 cycles then
       * suppress messages for 10 cycles. */
      N_MESSAGES++;if(N_MESSAGES<20){
        body.message(body.getName() + " " + predicate);
      }else{N_CYCLES=Math.max(0,N_CYCLES-20);}
    }
    if(++N_CYCLES>30){N_MESSAGES=0;N_CYCLES=0;}
    return changed;
  }
  /** values to prevent excessive messages */
  protected int N_MESSAGES=0, N_CYCLES=0;

  public final int getFeeling() {
    return feeling;
  }

  /**
   * Represents the increase of threshold for defaecating or urinating
   * while asleep.
   */

  public double gainThresholdWhenAsleep = 1.5;
  public VDouble Consc = new VDouble();

  /** The threshold volume of hunger above which the patient will eat */
  public double preferredEatVolume = 0.6;
  /** The threshold volume of thirst above which the patient will drink */
  public double preferredDrinkVolume = 0.3;

  /**
   * calculate desires:
   * (all working units in litres)
   *   hunger: normal glucose==0.004
   *	    req up to 0.016 ml food per minute
   *   Thirst: normal volume==5, normal PNa==0.144
   *     req up to 0.011 ml water per minute
   *
   * Defaecation and urination are triggered when the bladder volume or colon
   * volume is greater than the threshold maxBladderVol or maxColonVol.
   * The threshold is higher when asleep, (multiplied by gainThresholdWhenAsleep)
   * but note that when the patient is unconscious, defaecation and urination
   * occur normally spontaneously.
   */
  void checkDesires() {
    Consc.lowPass( Math.min(Math.max(
        (asleep?0:1) * ((feeling==UNCONSCIOUS)?0:1) + pain.get() - sedation.get()
        ,0),1),0.5);

    double stomachVolume = body.gitract.stomach.volume.get();
    //hunger: v = rate of huneger measured as   food ml / min
    // was 0.002 * [0-4] 6-1000*bglu
    double v = 0.002 * (
        Math.max(Math.min(6 - (int) (1100 * body.blood.glucose.getC()),
                          4), 0)
        + Math.max(0,Math.min(4, -40*body.blood.Na.getError()))
        ) ;
    if (stomachVolume < 0.01) {
      v += 0.008;
    }
    v *= 0.6; //to reduce appetite artificially
    hunger.set( Math.min(1.5,
                      hunger.get() + v * greed.get() * elapsedTime / 60)); //cannot eat more than 1.5 litre
    //thirst
    int dvol = Math.max(Math.min(6 - (int) body.blood.volume.get(),
                                 6), 0);
    //was 0.001*dvol (11/1/03)
    //1 ml/minute for each L of blood
    double w = 0.001 * dvol;
    //1 ml/minute for each 20 mOsm error in POsm
    w += Math.max(0, (body.blood.POsm.get() - 0.295) * 0.001 / 0.020);
    w *= 0.7; // added 7/7/3 to reduce thirst! was 0.45 then, 20/11/3 changed to 0.7 because hyperNa
    thirst.set( Math.min(2,
                      thirst.get() + w * thirstiness.get() * elapsedTime / 60)); //cannot drink more than 2 litres
    //random trigger of desires and dispatch commands to the body

    /** Eat if needed */
    //added 10/10/02 Cannot eat or drink while unconscious
    if ( (hunger.get() > preferredEatVolume
          && ! (environment.starve || environment.NBM)
          && ((feeling != UNCONSCIOUS && !asleep) || conscious.eat))
         && randomEventOccurs(0.025)){
      body.eat(Math.max(0, Math.min(hunger.get(),
                                    body.gitract.stomachCapacity.get() - stomachVolume)));
   }

   /** Drink if needed */
   if ( (thirst.get() > preferredDrinkVolume
         && !environment.NBM
         && ( (feeling != UNCONSCIOUS && !asleep) || conscious.drink))
        && randomEventOccurs(0.025)) {
     body.drink(Math.max(0, Math.min(thirst.get(), 1.5 - stomachVolume)));
   }


   /** defaecate if needed */
   double colvol = body.gitract.colon.volume.get();
   if ( ( (colvol > maxColonVol && !asleep)
          || (colvol > maxColonVol * gainThresholdWhenAsleep))
        && randomEventOccurs(0.025)) {
     body.defaecate();
   }

   /** urinate if needed */
   double bladvol = body.bladder.volume.get();
   if ( ( (bladvol > maxBladderVol && !asleep)
          || (bladvol > maxBladderVol * gainThresholdWhenAsleep))
        && randomEventOccurs(0.025)) {
     body.urinate();
   }

 }

  /**
   * Makes the subject feel well or ill
   */
  public void feel(int nfeeling) { //work out a suitable predicate and chain on
    String p = "";
    if (nfeeling == UNCONSCIOUS) {
      p = "has fainted and is unconscious";
    }
    if (nfeeling == DEAD) {
      p = "has suffered a mysterious fatality";
    }
    if (nfeeling < UNCONSCIOUS && feeling >= UNCONSCIOUS) {
      p = "has regained consciousness";
    }
    feel(nfeeling, p);
  }

  /**
   * Makes the subject feel well or ill, supplying a reason/comment.
   * In addition, transition to ILL while asleep wakes the patient,
   * and transition to and from UNCONSCIOUS causes the appropriate actions.
   * Transition to DEAD causes body.die() to be invoked.
   */
  public void feel(int nfeeling, String npredicate) {
    predicate = npredicate;
    newFeeling = Math.max(nfeeling, newFeeling);
    if (nfeeling == DEAD && feeling != DEAD) {
      body.die();
      body.message(body.getName() + " " + predicate);
    }
    else if (asleep && feeling < ILL && newFeeling == ILL){
      wakeUp(); // if feels ill
    } else if (conscious.isExercising() && conscious.mobile
               && nfeeling < ILL & newFeeling == ILL){
      conscious.stopExercising(); // if feels ill
    } else if (nfeeling == UNCONSCIOUS && feeling < UNCONSCIOUS) {
      conscious.becomeUnconscious();
    }
    else if (nfeeling < UNCONSCIOUS && feeling == UNCONSCIOUS) {
      conscious.recoverConsciousness();
    }
    feeling = newFeeling;
  }

  public void reset() {
    newFeeling = feeling = WELL;
    //hunger = 0;
    //thirst = 0;
    //nausea = 0;
    //sedation = 0;
    conscious.reset();
    environment.Uprt.initialise();
    asleep=false;
    N_CYCLES=N_MESSAGES=0;
  }

  /**
   * Current respiratory drive calculated by breathing(). It is the
   * coefficient of the normal breathing rate and tidal volume; i.e.
   * A value of 1 gives a normal respiratory rate and tidal volume.
   */
  public VDouble respiratoryDrive = new VDouble();

  /** The respiratory drive from oxygen, CO2, and acid */
  public VDouble
      CO2drive = new VDouble(),
      pHdrive = new VDouble(),
      O2drive = new VDouble();

  /**
   * Respiratory drive increases with high arterial CO2, low arterial O2,
   * and low blood pH.
   *  CO2: (PCO2-37.5)/2.5
   *  O2: 40/(PO2-35)
   *  pH:
   *  1% pain increases RespDr by 1%. (added 11/11/3)
   *  high levels of opiateBinding reduces RespDr
   */
  void breathing() {
    //Chemoreceptors respond to various stimuli in the blood
    Blood blood = body.blood;

    previousVoluntaryOverride = environment.BrHld
        | (environment.Hyperv.get() > 0);
    //Respiratory centres calculate desired lung commands
    double respiratoryRate = Math.max(Math.min(13  * (1 + pain.get()*4)
                                               * respiratoryDrive.get(), 60), 0);
    //double tidalVolume = Math.max(Math.min(0.51 * respiratoryDrive.get(),
    //                                       3.5), 0);

    //Then send command to lungs via phrenic nerve
    Lung lung = body.lungs;
    lung.RespR.set(respiratoryRate);
 //!   lung.TidV.set(tidalVolume);

 //previously double O2drive = 0.04 / (Math.max(0.036,blood.arterial.PO2.get()) - 0.035);
//!        O2drive = Math.max(1,
//!                                  0.100 /
//!                                  (Math.max(0.100, blood.arterial.O2.get()) -
//!                                   0.0975))
//!                         - Math.max(1, 10 * blood.arterial.PO2.getError());
//!        if(O2drive<0)O2drive=0;
// pH effect: was -2 * blood.pH.getError() + 1
//      (i.e. x1.2 for a pH drop of 0.1)
//!        pHdrive = -10 * blood.pH.getError() + 1;
//Find total respiratory drive
//2/7/3 squared o2drive
 double tmp;
 double targetDrive = Math.max(CO2drive.get(), 0.55)
     * Math.max( (tmp = O2drive.get()) * tmp, 1.0)
     * Math.max(pHdrive.get(), 1.0)
     * (1 - 1500 * opiateBinding);
//uses Max(pHdrive,0.6), since cannot be negative or zero
//and we don't want alkalosis to inhibit breathing!
//added square root: makes it less dramatic
//Respiratory drive is modified by voluntary override

//previously CO2drive = (blood.arterial.PCO2.get() - 0.0375) / 0.0025;
//then (4/10/3) (apCO2 > 0.040) ? (apCO2 - 0.03975) / 0.00025 : (apCO2 - 0.010) / 0.030
 double apCO2 = blood.arterial.PCO2.get();
 double co2drive = (apCO2 > 0.040) ? (apCO2 - 0.0375) / 0.0025 :
     Math.max(0, Math.min(1, apCO2 / 0.040)) / 10 + 0.9;
 if (co2drive > 1) {
   co2drive *= co2drive;
 }
 CO2drive.set(co2drive);
 if (environment.BrHld) {
   respiratoryDrive.set(0);
 }
 else if (environment.Hyperv.get() != 0) {
   respiratoryDrive.set(1 + 2 * environment.Hyperv.get());
 }
 else if (previousVoluntaryOverride) {
   respiratoryDrive.set(targetDrive);
 }
 else {
   respiratoryDrive.lowPass( /*Math.sqrt*/ (targetDrive)
                                   //      - 0.1*respiratoryDrive.getError()
                                        ,  fractionDecayPerMinute(0.4));
   // if voluntary override ends on next cycle, return
   // immediately to normal without low-pass.
 }


    if (verbose) {
      inform("drive:pH:" + pHdrive.formatValue(true, false) + ", O2:"
             + O2drive.formatValue(true, false) + ", CO2:"
             + CO2drive.formatValue(true, false));
    }
  }

  /**
   * Rate at which oxygen is delivered to the brain, in L/min.
   * Calculated from AO2 and brain.flow
   * @todo ?protected
   */
  public VDouble O2Supply = new VDouble();

  /**
   * This is calculated from the required O2 minus supplied O2
   */
  public VDouble hypoxia=new VDouble();

  /**
   * This increases with cytokines from a default value of 0.
   * This value is added to the set-point of skin vasoconstriction and sweating.
   */
  public VDouble fever = new VDouble();

  /**
   * Persists between cycles to indicate whether the last cycle's
   * breathing was overridden
   */
  private boolean previousVoluntaryOverride = false;
  /**
   * Sympathetic activity proportional to excess oxygen supply to brain.
   * One millilitre of excess oxygen gives a
   *
   * 11/1/03
   * low tissue O2 -> vasodilatation -> high flow -> fall AP -> high HR,SV -> high CO.
   *
   * Altered so that it is AO2 concentration that is important - so increased
   * in anaemia. The main effect of Symp is to increase VCT: When the
   * oxygen concentration is low, (anaemia or hypoxia), there is
   * peripheral vasodilation. There is also some effect on the heart.
   *
   * This also controls brain resistance. (25/10/3)
   * 10% of pain increases sympathetics by 1% (11/11/3)
   */
  void sympathetics() {
    double dO2 = body.blood.arterial.O2.get() * flow.get();
    double pO2 = body.blood.arterial.PO2.get();
    O2Supply.set(dO2);
    double hypox=Math.max(0.035-dO2,0);
    hypox=Math.max(0.055-pO2,hypox);
    hypoxia.set(hypox);
    double excessO2 = Math.max(dO2 - 0.04, 0);
    //Quite fast recovery
    // low tissue O2 -> vasodilatation -> high flow
    // -> fall AP -> high HR,SV -> high CO.
    //9/7/3 added: low brain O2 causes increased symp tone. was -0.8*(brO2-0.1)
    // 18.8.05 Half life decreased to minutes


    /*  //19.8.5 was
    double rateOfProduction = excessO2 / 100 -
        0.4 * Math.min(0, dO2 - 0.14) +
        pain.get() * 0.02 +
        Math.max(0,environment.Exer.get()-1000)/250000;
    Symp.set(Math.max(fractionPerMinute(0.6) * Symp.get()
                      + rateOfProduction * elapsedTime / 60,
                      0.001));

    double rateOfProduction = excessO2 / 100 -
        0.4 * hypox +
        pain.get() * 0.02 +
        Math.max(0,environment.Exer.get()-1000)/250000;
//!    Symp.set(Math.max(fractionPerMinute(0.6) * Symp.get()
                      + rateOfProduction * elapsedTime / 60,
                      0.001));
     */

//!        resistance.regulateQuantity(O2Supply, 3 ,fractionDecayPerMinute(0.1));
    if (verbose) {
      inform("Symp tone " + Quantity.toString(Symp.get()));
    }
  }

  /**
   * The cranial diabetes insipidus flag switches of ADH production by the
   * pituitary gland
   */
  public boolean diabetesInsipidus = false;
  /**
   * ADH is regulated against changes in blood osmolarity.
   * A plasma change of 1 mOsm should give a 0.35 pM change of ADH
   * Also secreted when plasma vol is low.
   */
  void pituitary() {
    //originally
    // body.blood.ADH.regulate( body.blood.Osm, 1.85E-10, 0.01 );
    //
    if (!diabetesInsipidus) {
      /**
       * 17/8/3 Now dependent on PNa rather than POsm: this causes diuresis
       * in diabetes mellitus.
       * 10/11/3 restored to POsm
       * 11/11/3 reverted to PNa
       */
//!      body.blood.ADH.regulateQuantity(body.blood.POsm, 30E-10,
//!                                      fractionDecayPerMinute(0.01));
      //added 11/1/03 to stop diuresis in hypovolaemia. Actually dependent
      //on venous and atrial stretch, and includes effect of ANP
      body.blood.ADH.add(Math.max(0, -0.3 - body.blood.volume.getError())
                         * 0.01E-13 * elapsedTime / 60);
      //added 6/8/3 to help diuresis when fluid overloaded
      //body.blood.ADH.add(Math.max(Math.min(0,
      //                                     ( -body.blood.volume.getError() +
      //                                      1.7)
      //                                     * 10E-13 * elapsedTime / 60),
      //                            -body.blood.ADH.get()));
    }
    else {
      body.blood.ADH.lowPass(0, fractionDecayPerMinute(0.05));
    }
  }

  /**
   * Sets the level of pain
   */
  void checkPain() {
    pain.set(Math.min(1, Math.max(0,
                                  environment.pain.get()
                                  - 10 * opiateBinding
                                  + cardiacIsch
                                  )));
  }

  /**
   * Controls the level of thyroxine.
   * 1 deg increase in body.Temp gives 3% decrease in blood.Thyr
   * Regulated very slowly (reaches target value in ~12 days)
   */

//! void thyroid() {
//!   body.blood.Thyr.regulateQuantity(body.Temp, -0.03,
//!                                  fractionDecayPerMinute(1E-6));
//! }

  /**
   * Brain flow depends on intracranial pressure, but not on alpha activity
   */
  {
    flow = new VDoubleReadOnly(){
      public double get(){
        return (body.CVS.AP.get() - ICP.getError())
            /resistance.get()/body.blood.Visc.get();
      }
    };
  }

  /**
   * Intracranial pressure
   */
  public VDouble ICP = new VDouble();

  /**
   * Makes the patient wake up or go to sleep, delegating the relevant
   * changes to the ConsciousLevelOptions
   */
  public void setAsleep(boolean asleep){
    if(this.asleep==asleep) return;
    this.asleep=asleep;
    if(asleep){
      if(body.isLogging()) body.message(body.getName() + " has fallen asleep");
      conscious.becomeUnconscious();
    }else{
      if(body.isLogging()) body.message(body.getName() + " has woken up");
      boolean tmp = conscious.revertPreviousPosture;
      conscious.revertPreviousPosture=true;
      conscious.recoverConsciousness();
      conscious.revertPreviousPosture=tmp;
    }
  }
  public void fallAsleep(){setAsleep(true);}
  public void wakeUp(){setAsleep(false);}
  public boolean isAsleep(){return asleep;}

  public ConsciousLevelOptions conscious = new ConsciousLevelOptions(Current.
      person);
  public ConsciousLevelOptions getUnconscious() {
    return conscious;
  }
}

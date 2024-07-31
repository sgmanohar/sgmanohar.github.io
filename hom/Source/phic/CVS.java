/**
 * CVS contains elements of the cardiovascular system
 * Responsible for capillaries and ECF balance
 */
package phic;

import phic.common.*;
import phic.drug.Drug;
import phic.drug.DrugContainer;

/**
 * Cardiovascular system. Contains the heart, and calculates total peripheral
 * resistance, blood pressure.
 * Also responsible for moving fluid between circulating and
 * extracellular compartments, and between ECF and ICF.
 * CVS is also the interface where environmental procedures may be called, for
 * example, Intravenous infusions and Life Support are checked here.
 * This also calls {@link #erythropoiesis() erythropoiesis}
 */
public class CVS
    extends Organ {
  public void tick() {
    calculateP();
    calculateCapillaries();
    calculateIntracellular();
    environment.doIntravenousInfusions(elapsedTime);
    environment.getVariableClamps().tick();
    erythropoiesis();
    calculateVenousReturn();
    waitMinutes(1);
  }

  /** The heart */
  public Heart heart = new Heart();

  /**
   * Diastolic and systolic blood pressures.
   */
  public VDoubleReadOnly DiaBP = new VDoubleReadOnly(){
    public double get(){ return AP.get()-pulsePressure/3; }
  },
  SysBP = new VDoubleReadOnly(){
    public double get(){
      return AP.get()+pulsePressure*2/3; //26/4/6 using LV
      //return heart.LV.sysP.get();
    }
  };

  /**
   * Capillary pressure
   */
  public VDouble Pcap = new VDouble();

  /**
   * Interstitial gas partial pressures, calculated from the venous and
   * arterial values.
   */
  public Gas InterstitialP = new Gas();

  /**
   * The fractional equilibration of gases in the steady state from
   * interstitial to venous, compared with the interstitial to arterial gradient.
   */
  protected double interstitialGasDiffusion = 0.3;

  /**
   * Set interstitial pressures to calculate read-only values from the venous
   * and arterial values.
   */
  {
    InterstitialP.O2 = new VDoubleReadOnly(){public double get(){
        GasConc a=body.blood.arterial, v=body.blood.venous;
        double apo2 = a.PO2.get(), vpo2=v.PO2.get();
        return vpo2 - interstitialGasDiffusion * (apo2-vpo2);
    }};
    InterstitialP.CO2 = new VDoubleReadOnly(){public double get(){
        GasConc a=body.blood.arterial, v=body.blood.venous;
        double apco2 = a.PCO2.get(), vpco2=v.PCO2.get();
        return vpco2 + interstitialGasDiffusion * (vpco2-apco2);
    }};
  };

  /**
   * Pulmonary vascular resistance
   */
  public VDouble PVR = new VDouble();




  /**
   * Pulmonary mean arterial pressure
   */
  public VDouble MPAP = new VDouble();


  /**
   * Constant representing osmotic pressure (mHg) per Molar protein concentration
   * may be closer to 19.4 or 20.7 (PCOP is 27 mmHg at 1.3 mOsm protein).
   * Interstitial fluid may be as high as 15.8 mmHg
   * Scand J Clin Lab Invest. 1982 Apr;42(2):123-30
   */
  static final double osm2p = 19.02;

  /**
   * Extracellular colloidal osmotic pressure, proportional to ECF protein.
   * @todo This ought to be called ECOP?
   */
  public VDouble ICOP = new VDouble() {
    public double get() {
      return body.ecf.prot.getC() * osm2p;
    }

    public void set(double d) {
      body.ecf.prot.setC(d / osm2p);
    }
  };

  /**
   * Plasma colloidal osmotic pressure, proportional to plasma protein.
   * @todo this ought to be in Blood (plasma)
   */
  public VDouble PCOP = new VDouble() {
    public double get() {
      return body.blood.PPr.get() * osm2p;
    }

    public void set(double d) {
      body.blood.setPlasma(body.blood.prot, d / osm2p);
    }
  };

  /**
   * Oedema as a percentage proportional to volume of fluid transferred to ECF
   * from plasma per minute.
   * ( 1% of oedema corresponds to 1 mL of fluid that transudes from the
   *   capillaries in one minute; thus 97% indicates 3 mL per minute
   *   enters the capillaries  )
   */
  public VDouble Oedema = new VDouble();

  /** Vasoconstrictor tone. */
  public VDouble VCT = new VDouble();

  /**
   * Hardness of arteries. This is a constant for a given patient
   */
  public VDouble Hard = new VDouble();


  /**
   * This is the desired value of AP before low-pass filtering.
   */
  public VDouble AP = new VDouble() {
    double p;
    public void set(double v) {
      super.set(v);
      p = v;
    }

    public double get() {
      if (!body.getClock().isSlowMode) {
        return super.get();
      }
      else {
        double ph = heart.phase;
        return (ph * Math.exp( -ph * 4) - 0.075) * pulsePressure + super.get();
      }
    }
  };

  /** The long-term arterial pressure, average over about 10 mins */
  public VDouble APL = new VDouble();

  /** Pulse pressure is difference between systolic and diastolic.
   * Proportional to hardness of arteries, stroke volume, and AP */
  public double pulsePressure;

  /**
   * Peripheral resistance is calculated from individual resistances of
   * vessels in kidney, skin, viscera, muscle, brain, heart. Also proportional
   * to blood viscosity.
   * @todo Make this method use a list of existing PerfusedOrgans.
   */
  public VDoubleReadOnly PR = new VDoubleReadOnly() { //should this be done regularly rather than on request?
    public double get() {
      Body b = body;
      return b.blood.Visc.get() / (1 / b.kidney.resistance.get() +
                                   1 / b.muscle.resistance.get() +
                                   1 / b.skin.resistance.get() +
                                   1 / b.brain.resistance.get() +
                                   1 / b.viscera.resistance.get() +
                                   1 / b.CVS.heart.resistance.get() +
                                   1 / b.Fat.resistance.get());
    }
  };

  /**
   *  This calculates the pressures in the vessels. Called by Heart.
   *  probably to be moved into Heart.
   *  AP=PR*CO, low passed. Vasoconstriction= 1 - 1.5 (AP error) + Sympathetic*10
   *  Pulse pressure = Stroke volume * Hardness of arteries * Arterial pressure * 10.0
   * with correction for AS / AR
   * @todo VCT depend on AngII?
   */
  protected void calculateP() {
    //arterial pressure
    AP.set(PR.get() * heart.CO.get());
    double ap = AP.get();
    APL.lowPass(ap, fractionDecayPerMinute(0.1));
    //vasoconstrictor tone
    //originally 1 + 10 * symp - 1.5 * APL.error
    //then became 1 - 0.2*AP.error + 50*(Symp-0.02) + 3*ALPHA_ADRENO
//!		double vct=1-4.0*APL.getError()+10*(body.brain.Symp.get() - 0.020)
//!				+3*body.blood.getDrugBinding(Drug.ALPHA_ADRENOCEPTOR)
//!                                +0.001*body.blood.AngII.get()/body.blood.AngII.initialValue;
//!		//was originally 0.01 < VCT < 100 (12/1/03)
//!		VCT.lowPass(Math.max(0.01,Math.min(1.5,vct)), fractionDecayPerMinute(0.3));
    //systolic and diastolic
    pulsePressure = (heart.SV.get() + 0.125) * Hard.get() * ap * 10.0
        * (1-0.7*heart.aorticStenosis.get()) * (1+1.6*heart.aorticRegurg.get())    ;
    MPAP.set( PVR.get() * heart.CO.get() );
  }

  /**
   * Pulmonary artery systolic pressure
   */
  public VDoubleReadOnly SysPAP = new VDoubleReadOnly() {
    public double get() {
      return MPAP.get() * 1.8;
    }
  };

  /**
   * Pulmonary artery diastolic pressure
   */
  public VDouble DiaPAP = new VDoubleReadOnly() {
    public double get() {
      double m = MPAP.get();
      return m * 0.25 + heart.left.atrialP.get();
    }
  };



  /**
   *  Capillary pressure as a fraction of current Arterial Pressure
   *  was 0.22 (23/6/3), and 0.14 (16/6/05)
   * Ought to yield hydrostatic pressure of 23.5 mmHg at MAP of 86. -
   * i.e should be around 0.24; but note various estimates of PCOP and ICOP
   * 28/7/7 was 0.158 but after fiddling with Hct and viscosity, needs to be lower?
   */
  public double capillaryPressureFraction = 0.145;

  /**
   * Capillary permeability
   * extravasation
   *     = 1 L/min for a unit pressure difference (1000 mmHg)
   */
  public double capillaryPermeability = 1.2;

  /**
   * This exchanges ECF and Plasma.
   * One litre of blood is ultrafiltered into ECF.
   * Then a volume that depends on Starling's law of capillaries
   * is ultrafiltered back to blood.
   *
   * Thus there is free exchange of all substances except protein.
   */
  protected void calculateCapillaries() {
    Blood blood = body.blood;
    Container ECF = body.ecf;
    //Pressure in capillary
    double rap=heart.right.atrialP.get();
    double pcap= rap +  capillaryPressureFraction * (AP.get()-rap);
    Pcap.set(pcap);
    // added oct06: ecf has a hydrostatic pressure too
    double mobility = environment.Exer.get()/500+0.1;
    double ecfp = Math.max(0,ECF.volume.getError() * 0.30 * mobility - 0.100);
    //Difference in pressure across capillary wall (+ve gives oedema)
    // = volume in litres that transudes
    // correction added 28/7/7 to compensate for increased viscosity
    double correctedPcap =  pcap*Math.min(0.45/blood.Hct.get(),1);
    double dP = ICOP.get() - PCOP.get() + correctedPcap      - ecfp ;
    //oedema rises by 1% for a 1 mL per minute extravasation
    double extravasation = dP * capillaryPermeability;
    Oedema.set(1 + extravasation * 10);
    //ultrafilter 1 liter of blood into ECF
    criticalPeriod.enter();
        blood.ultraFiltrate_overwrite(1, filter_temp);
        double volumeOut = filter_temp.volume.get();
        ECF.add(filter_temp);
        //amount returned to blood depends on starling
        //currently,
        double volumeIn = volumeOut - extravasation * elapsedTime / 60;
        //added 22/10/02
        volumeIn = Math.max(0, volumeIn);
        ECF.ultraFiltrate_overwrite(2, filter_temp);
        filter_temp.withdrawVol_overwrite(volumeIn, filter_temp2);
        blood.add(filter_temp2);
        ECF.add(filter_temp);
    criticalPeriod.exit();
  }

  private DrugContainer filter_temp = new DrugContainer();
  private DrugContainer filter_temp2 = new DrugContainer();

  /**
   * Rate of potassium flowing into cells in mol/min
   * @todo private
   */
  public VDouble KinfluxRate = new VDouble();

  /**
   * Take ECF water and put into ICF, proportional to DOsm, the difference
   * in osmolarity of intra- and extracellular fluid.
   * 10 mL  of fluid transferred for a 1 mM difference per minute
   * @todo add potassium balance, and dependence on B2 agonists and insulin.
   */
  protected void calculateIntracellular() {
    // volume to transfer into ICF: 10 L per Molar difference per minute
    body.ecf.urea.equilibrateConcentration(body.ecf.urea,fractionDecayPerMinute(0.3));

    double volume = 10.0 * body.DOsm.get() * elapsedTime / 60;
    // disallow transferring more than 1/5 of total volume at once!
    volume = Math.max(-body.icf.volume.get()*.2, Math.min(volume, body.ecf.volume.get()*.2));
    criticalPeriod.enter();{
        body.ecf.addWater( -volume);
        body.icf.addWater(volume);
    }criticalPeriod.exit();
    //ICF K+ regulated against both insulin and beta agonists
    double Keffect = body.blood.Insul.get() +
        body.blood.getDrugBinding(Drug.INSULIN_EFFECT) - 0.002
        + 1.0 * body.blood.getDrugBinding(Drug.BETA_ADRENOCEPTOR);
    double IKerror = body.icf.K.Q.get() -
        body.icf.K.initialValue * body.icf.volume.initialValue;
    KinfluxRate.lowPass(0.008 * Keffect + body.ecf.K.getError()
                        - IKerror * 0.3, fractionDecayPerMinute(0.3));
    body.ecf.K.moveTo(body.icf.K, KinfluxRate.get() * elapsedTime / 60);
  }

  /**
   * Creation and destruction rates of red blood cells are determined by
   * the level of erythropoietin.
   * Erythropoietin levels are regulated on the concetration of oxygen in
   * arterial blood.
   *
   * A 1 ml/L increase in [O2] gives 10% decrease in erythropoietin
   * A 1% decrease in erythropoietin gives a 0.1 L decrease in red cell mass
   *
   * @todo erythropoietin production should move to kidney
   */
  protected void erythropoiesis() {
    //originally regulated against arterial.PO2; changed to arterial.O2
    //originally time constant of Epo secretion and eryth production
    //were both 0.003 per minute.
    //Now uses 0.0003, since it reflects days rather than hours
  //!  body.blood.Eryth.regulateQuantity(body.blood.arterial.O2, -2E1,
  //!                                    fractionDecayPerMinute(0.003));
  //!  body.blood.RCM.regulateQuantity(body.blood.Eryth, 10,
  //!                                  fractionDecayPerMinute(0.0001));
    if (body.blood.Hct.get() > 0.90) {
      body.brain.feel(Brain.DEAD, "has died of dehydration");
    }
    body.blood.Visc.set( // (12/1/03) Reduced the effect of haematocrit.
        // was  1 + 4 * Math.max( Hct.get()-0.5,0 ), now 1+1.3*...
        1 + 1.3 * Math.max(body.blood.Hct.get() - 0.5, 0) );

  }

  /** Net tissue oxygen delivery */
  public double O2delivery() {
    return heart.CO.get() * body.blood.arterial.O2.get();
  }

  public void reset() {
    body.ecf.solids.set(0);
    KinfluxRate.set(0);
    pulsePressure=0.050;
    //this is not a visible variable; it is a quantity, and thereby
    //depends upon the timescale
    body.blood.CarbonicProduction.set(0);
    capillaryPermeability=1.2;
    capillaryPressureFraction=0.158;
    //MPAP=0.011;
  }

  /**
   * Return a waveform in the range -1 to +1 , with an average of zero.
   */
  public static final double getPulsePressureWaveform() {
    double p = Current.body.CVS.heart.phase;
    return p * Math.exp( -p * 4) - 0.075;
  }

  /**
   * litres per mHg - units of capacity / compliance
   */
  public VDouble Venoconstriction = new VDouble();
  public void calculateVenousReturn() {
    heart.venousCompliance.lowerGradient = Venoconstriction.get();
  }

}

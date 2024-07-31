package phic;
import phic.common.*;
import phic.drug.DrugContainer;

/**
 * Blood is an extended container, with hormones and plasma.
 * Blood also includes arterial and venous gases, which are coupled to the
 * haematocrit so as to give corrected values of PO2 and PCO2.
 *
 * Quantity extensions: erythropoietin, insulin, ADH, angiotensin II, aldosterone,
 * thyroxine.
 *
 * Value extensions: haematocrit, antihaematocrit, viscosity, acid-base correction
 *
 * Gas extensions: arterial, venous
 */
public class Blood extends DrugContainer{
  /** Hormone concentrations, Molar */
  public VDouble Eryth=new VDouble();
  /** Insulin concentration, Molar */
  public VDouble Insul=new VDouble();
  /** Anti-diuretic hormone (vasopressin) concentration, molar*/
  public VDouble ADH=new VDouble();
  /** Angiotensin II concentration, Molar */
  public VDouble AngII=new VDouble();
  /** Aldosterone concentration, Molar */
  public VDouble Aldo=new VDouble();
  /** Thyroxine (T4) concentration, Molar @todo not implemented yet */
  public VDouble Thyr=new VDouble();
  /**
   * Ketones, including butylhydroxybutyrate and acetoacetate
   */
  public VDouble ketones=new VDouble();

  /**
   * Ammonia - in moles/L
   */
  public VDouble NH3 = new VDouble();

  /**
   * Override the method addQ in the hydrogen ion concentration:
   * this means that when H+ is added, it is automatically buffered
   * into the bicarbonate and H2CO3 concentrations.
   */
/*
  { H = new Quantity(this){
    public void addQ(double q){
      q=q/PV.get(); //effective plasma concentration change
      double pbic = PBic.get(), phy = PHy.get();
      //actual plasma concentration change:
      double dH = (BICARB_DISSOC_K * (H2CO3.get() + q) + phy * (q - pbic))
          / (phy + pbic + BICARB_DISSOC_K - q);
      super.addC(dH);
      bicarb.addC(dH-q);
      H2CO3.addC(q-dH);
    }
  };}
*/
  /** Arterial Blood Gases: oxygen and carbon dioxide concentrations
   * and partial pressures. */
  public GasConc arterial=new GasConc(this);
  /** Venous blood gases: oxygen and carbon dioxide concentrations and
   * partial pressures. */
  public GasConc venous=new GasConc(this);
  //special values
  /** Haematocrit as percentage volume red cells */
  public VDouble Hct=new VDouble(this){
    public double get(){
        return solids.get();
    }

    public void set(double d){
        solids.set(d);
     }
  };
  /** Antihaematocrit as percentage volume fluid */
  public  double AHct(){
    return 1-solids.getC();
  }

  /** Oxygen Saturation of haemoglobin */
  public VDouble SatO2=new VDoubleReadOnly(){
    public double get(){
      if(Current.body.getClock().isSlowMode){
        double p = Current.body.CVS.heart.phase;
        return (1 + (p * Math.exp(-4*p) - 0.075) * satFluct) * arterial.SatO2.get();
      }else return arterial.SatO2.get();
    }
  };

  /** Variation between peak and trough saturation in waveform, as a fraction of  */
  final double satFluct=0.05;

  /**
   * Mean cell haemoglobin, in grams. Normal range 27-32 picograms.
   * This variable is currently fixed.
   */
  public double MCH=30E-12;
  /**
   * Mean cell volume, in litres. Normal range 76-100 femtolitres.
   * This variable is currently fixed
   */
  public double MCV=88E-15;

  /**
   * Variability in size of erythrocytes
   */
  public double anisocytosis = 0.1;

  /**
   * Measured in cells per litre.
   * Calculated from RCM (depends on solid) and MCV.
   */
  public double redCellCount(){
    return this.RCM.get()/MCV;}

  /**
   * Viscosity depends on haematocrit above 50%; calculated in CVS.erythropoiesis
   */
  public VDouble Visc = new VDouble();

  /** Plasma calcium concentration */
  public VDouble Ca = new VDouble();

  /**
   * Carbon monoxide, as a percentage saturation of total haemoglobin binding sites.
   */
  public VDouble CO=new VDouble();

  /**
   * Concentration of haemoglobin in g/dl, calculated as directly proportional
   * to the blood.Hct, using the mean cell haemglobin and mean cell volume
   * to calculate the concentration.
   *
   * Setting the Hb actually changes the number of red cells, not their mean
   * volume or heamoglobin.
   */
  public VDouble Hb=new VDouble(){
    public double get(){
      return solids.get()*MCH/MCV;
    }

    public void set(double d){
      solids.set(d*MCV/MCH);
    }
  };

  /**
   * Lactic acid
   */
  public Quantity lactate = new Quantity(this);



  //Acid-base balance





  // abstracted to Container 22/1/03
   /**
    * Acid-base correction (concentration increase of bicarb).
    * When ABC is positive, H+ and bicarb are being produced from H2CO3.
    * The value is the concentration increase of HCO3- per minute.
    * public double ABC;
    */

   /**
    * Calculate amount of transfer between venous CO2 and plasma ions (hydrogen and
    * bicarbonate) due to small change in one of these quantites.
    *    <BR>	    K.[H2CO3] - [H].[HCO3]
    *    <BR>	    ----------------------
    *    <BR>	       [H] + [HCO3] - K
    *
    * Freely dissolved carbon dioxide is directly proportional to pCO2.
    * @todo Implement carbonic anhydrase inhibitors
    * @todo Replace with Container.doBicarbonateBuffering()
    */
   void acidbase(double elapsedSeconds){ // needs monopoly over blood
     double carbonicAcid=0.0301*venous.PCO2.get();
     H2CO3.setC(carbonicAcid);
     double pbic=PBic.get(),phy=PHy.get();
     // calculate how much bicarb is produced per minute.
     //ABC=(BICARB_DISSOC_K*carbonicAcid-phy*pbic)/(pbic+phy+BICARB_DISSOC_K);
     ABC=bufferConcentrationChange(H2CO3, PBic, BICARB_DISSOC_K);
     /**
      * 19/9/3: previously, quantity = ABC * elapsedSeconds/60
      * and 20.7.5 quantity = 0.2* volume.get() * ABC * fractionDecayPerMinute(0.99999)
      * now - uses (seconds * 60) for extra-rapid correction!
     */
     double concChange=1.0*ABC*Organ.fractionDecayPerMinute(0.2,elapsedSeconds*60);
     H.addC(concChange);
     bicarb.addC(concChange);
     /**
      * Add CO2 from bicarbonate. This goes to venous.CO2 in Muscle.
      * The unit is rate of change of concentration.
      * @todo check this coefficient at body temperature --
      * is this 22.4 l/mol at 0K?
      */
     CarbonicProduction.set(-ABC/0.0301/**22.4*/);

   }
   /**
    * Quadratic version of bicarbonate buffering:
    * Returns the quantity per litre of acid that is converted into base form
    * in order to attain equilibrium.
    * I.e., the concentration increase of H+
    * @param acid the concentration of conjugate acid in the solution
    * @param base the concentration of conjugate base in the solution
    * @param k the dissociation constant of this buffer
    */
   protected double bufferConcentrationChange(VDouble acid, VDouble base, double k) {
     double h=PHy.get(), b = base.get(), c = acid.get();
     //return 0.5*(-b-h-k+Math.sqrt(b*b-2*b*h+h*h+2*b*k+4*c*k+2*h*k+k*k));
     return 0.5*(-b-h-k+Math.sqrt((b+h+k)*(b+h+k) - 4*(b*h-c*k)) );
     //simple version = (k*c - h*b) / (b + h + k)
   }

  /**
   * Concentration increase rate of CO2, from carbonic acid production. Used by
   * muscle to calculate new value of venous CO2
   *
   * @todo replace this with addition to Container.H2CO3, deriving from venous.CO2 ?
   * @todo private
   */
  public VDouble CarbonicProduction=new VDouble();
  /**
   * Calculate how acid needs to be added to restore the pH
   * to normal, if pCO2 were restored to 5 kPa (40 mmHg).
   * @return the base excess in mmol/L
   */
  public VDoubleReadOnly baseExcess = new VDoubleReadOnly(){
    public double get(){
      //return bicarb.get()-0.0301*arterial.PCO2.get()-0.012;

      /** Excess bicarbonate above that expected at this pH at atmospheric CO2 */
      //return PBic.get() - BICARB_DISSOC_K * 0.0301 * 0.040  / PHy.get();


      /** Correct formula for titratable H+ to reach 7.4 at atmospheric PCO2 */
      //return 1e-8*(3.981-1e8*PHy.get()+3.981e8*PBic.get()
      //             - 1e16*H2CO3.get()*BICARB_DISSOC_K)
      //             /(3.981+BICARB_DISSOC_K*1e8);

      /** guestimate 22.7.5 */
      //return PBic.get() - 0.024;
      final double oH=Math.pow(10,-7.4);
      final double oCO2 = 0.040 * 0.0301;

      return PBic.get() + oH - PHy.get() -
               0.024266;  // equals BICARB_DISSOC_K * oCO2 / oH;

    }
  };







  //***************** PLASMA




   /**
    * Calculate the plasma concentrations of a given quantity, i.e. the
    * concentration of the quantity of substance in the volume of the non-
    * solid volume of this container.
    * @param substance The substance must be in this container!
    */
   public double getPlasma(Quantity substance){
     return substance.get()*1/AHct();
   }

  /**
   * Set the plasma concentrations of quantites.
   * The method sets the concentration in the container to the given
   * value adjusted for the haematocrit of the container, i.e. to
   * AHct() * value.
   *
   * @param substance the substance in this container to set the plasma
   * concentration of. The substance Must be in this container!
   * @param value the concentration in the plasma of the substance.
   *
   * @todo reimplement with tag instead of Quantity. Or, separate object
   * for plasma?
   */
  public void setPlasma(Quantity substance,double value){
    substance.set(value*AHct());
  }

  /** Shorthand for calculating plasma concentration of the substances. */

  /** Plasma Hydrogen ion */
  public VDouble PHy=new VDouble(){
    public double get(){      return getPlasma(H);}
    public void set(double value){      setPlasma(H,value);}
  };

  /** Plasma bicarbonate : acceses blood.bicarb*/
  public VDouble PBic=new VDouble(){
    public double get(){      return getPlasma(bicarb);    }
    public void set(double value){      setPlasma(bicarb,value);    }
  };

  /** Plasma potassium */
  public VDouble PK=new VDouble(){
    public double get(){      return getPlasma(K);    }
    public void set(double value){      setPlasma(K,value);    }
  };

  /** Plasma sodium */
  public VDouble PNa=new VDouble(){
    public double get(){      return getPlasma(Na);    }
    public void set(double value){      setPlasma(Na,value);    }
  };

  /** Plasma osmolarity. Can't be set, of course! */
  public VDouble POsm=new VDoubleReadOnly(){
    public double get(){
      //return Osm.get()/AHct();
      return PHy.get() + glucose.getC() + PUN.get() + PPr.get() + 2*PNa.get() + 2*PK.get();
    }
  };
  /** Plasma glucose */
  public VDouble PGlu = new VDouble(){
    public double get(){    return getPlasma(glucose); }
    public void set(double value){ setPlasma(glucose,value); }
  };

  /** Plasma protein */
  public VDouble PPr=new VDouble(){
    public double get(){      return getPlasma(prot);    }
    public void set(double value){      setPlasma(prot,value);    }
  };
  /** Plasma albumin concentration directly linked to plasma protein */
  public VDouble PAlb = new VDouble(){
    public double get(){ return getPlasma(prot) * 32300; }
    public void set(double value){ setPlasma(prot, value/32300); }
  };

  /** Plasma urea nitrogen (urea and creatinine) */
  public VDouble PUN = new VDouble(){
    public double get(){   return getPlasma(urea);  }
    public void set(double value){ setPlasma(urea,value); }
  };

  /** Plasma creatinine */
  public VDouble PCreat = new VDouble(){
    public double get(){ return getPlasma(creat); }
    public void set(double value){ setPlasma(creat, value);}
  };

  /** Plasma volume */
  public VDouble PV = new VDoubleReadOnly(){
    public double get() {      return volume.get() * AHct();    }
    //public void set(double value){ volume.set(value/AHct()); }
  };

  public VDouble PCl = new VDoubleReadOnly(){
    public double get(){     return Cl.get()/AHct();  }
  };


    /*
       /**
        * Red cell mass, as total volume of red cells. The RCM is altered directly
        * by erythropoiesis.
        */
     public VDouble RCM=new VDouble(){
       public double get(){
         return solids.getQ();
       }

       public void set(double value){
         //set total blood volume  =  BV * AHct + new RCM
         volume.set(volume.get()*(1-solids.getC())+value);
         //set haematocrit percentage, using new volume
         solids.setQ(value);
       }
     };


    /**
     * Haemoglobin dissociation curve
     */
  public Curve HbO2DissocArt = arterial.O2Dissociation;

  /** Add a fluid with no oxygen or CO2 in */
  public void addWithoutO2(Container c) {
      double volratio = vol;
      super.add(c);
      volratio /= vol;
      arterial.O2.multiplyBy(volratio);
      venous.O2.multiplyBy(volratio);
      //arterial.CO2.multiplyBy( volratio );
      //venous.CO2.multiplyBy( volratio );
  }

}

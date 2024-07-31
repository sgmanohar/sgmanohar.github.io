package phic;
import phic.common.*;
import evaluator.MathExtra;

/**
 * Lungs are responsible for maintaining arterial gases in response to a ventilation
	 * signal from the CNS. They exchange gases from blood.venous, and the resulting
 * gases go to blood.arterial.
 * This is one half of the cycle, which is completed by
 * @link phic.Muscle Muscle.
 */
public final class Lung extends Organ{
	public Lung(){}

	public void tick(){
		breathe();
                calculatePhase();
                volumes();
                body.blood.acidbase(elapsedTime);
		if(verbose)inform(Quantity.toString(body.blood.ABC)+" M increase HCO3 & H+");
		waitSeconds(60);
	}

	/**
	 * Dead space is represented by a constant value, the volume in L of the
	 * mouth, trachea, bronchi and non-respiratory bronchioles.
	 */
	public VDouble DdSp= new VDouble();

	/**
	 * Tidal volume is the volume of air taken in on each breath, in L.
	 * It is controlled by the @link phic.Brain brain.
	 */
	public VDouble TidV=new VDouble();

	/**
	 * The respiratory rate is the rate of taking breaths, in Hz.
	 * It is controlled by the brain.
	 */
	public VDouble RespR=new VDouble();

	/** Respiratory water loss rate, in litres per minute */
	public VDouble H2Oloss=new VDouble();

	/**
	 * Alveolar ventilation rate, in litres
	 */
	protected double AVentAir;

	/**
	 * The alveolar blood is the local intermediate, which begins by containing
	 * venous blood, and ends up with the new arterial blood.
	 * Alveolar blood has same haematocrit as normal blood -- so use blood for
         * Hct. Cannot initialise the Blood part until 'body' has been defined.
	 */
	public GasConc alvBlood=new GasConc(null);

	/**
		 * Temporary values of the difference in partial pressures between the alveolar
	 * gas and alveolar blood
	 */
	public Gas dpp=new Gas();

	/**
	 * Alveolar partial pressures of gas
	 */
	public Gas alvP=new Gas();

	/**
	 * Alveolar minute volumes of gas.
         * At the start of each cycle, it is proportional to environment.air
         * and AVentAir.
	 */
	public Gas alvV=new Gas();

	/**
	 * The container that logs the water that is exhaled.
	 * @todo move this into Environment?
	 */
	protected Container exhaledWater=new Container();

	/**
	 * Expired CO2 percentage = atmospheric CO2 percentage +
	 * produced CO2 volume / respiratory rate
         *
         * Expired O2 percentage = atmospheric O2 percentage - consumed O2 volume
	 * / respiratory rate.
	 */
	public Gas.WithCO expired = new Gas.WithCO(){
          {O2 = new VDoubleReadOnly(){ public double get(){
              double v=Vent.get(); if(v==0)return 0;
                return environment.air.O2.get()-body.O2Use.get()/Vent.get();
              }};
            CO2 = new VDoubleReadOnly(){ public double get(){
                double v=Vent.get(); if(v==0)return 0;
                return environment.air.CO2.get()+body.CO2Production.get()/Vent.get();
              }};
          }
        };
        /*
  public double XCO2(){
		return environment.air.CO2.get()+body.CO2Production()/Vent();
	}
       */
	/**
	 * Expired O2 percentage = atmospheric O2 percentage - consumed O2 volume
	 * / respiratory rate.
	 */
        /*
	public double XO2(){
		return environment.air.O2.get()-body.O2Use.get()/Vent();
	}
       */


      /**
       * The difference between alveolar gas pO2 and alveolar blood pO2 should
       * not be more than this value (in mHg)
       * was 0.000001, then 0.0001
       */
      public double  pO2Accuracy = 0.0001;
      /**
       * The difference between alveolar gas pCO2 and alveolar blood pCO2
       * should not be more than this value (in mHg)
       */
      public double pCO2Accuracy = 0.001;
	/**
	 * Indicates whether the lungs will be ventilated. If false, the alveoli will
	 * receive no air.
	 */
	public boolean ventilate=true;

	/**
	 * Ventilation rate = respiratory rate * tidal volume, the rate of air entry.
	 */
	public VDoubleReadOnly Vent = new VDoubleReadOnly(){
		public double get(){return RespR.get()*TidV.get();}
	};

	/**
	 * Alveolar ventilation = respiratory rate * (tidal volume - dead space)
	 */
	public VDoubleReadOnly AVent = new VDoubleReadOnly(){
          public double get(){
            return Math.max(RespR.get() * (TidV.get() - DdSp.get()), 0);
          }
	};

        /**
         * Value to represent volume of interstitial fluid in the lung, in Litres.
         * @todo implement effect on gas exchange
         */
        public VDouble Oedema = new VDouble();

        /** Temporary values of old alvPO2/PCO2 for non-ventilating person*/
        protected Gas alvTmp=new Gas();

        /** Alveolar air pressure */
        protected double Pa;

	/**
	 * Performs the calculations involved in breathing.
	 *
	 * Moisture is exchanged with the atmosphere:
	 *  Ambient SVP depends on environment temperature.
	 *  Partial pressure of water vapour is calculated from ambient SVP and %-humidity.
	 *  Volume of water inhaled per min = ventilation rate * partial pressure H2O / atmospheric pressure.
	 *  Respiratory water loss =
	 *
	 * Gases are exchanged by allowing equilibration between the air and blood.
	 *  Alveoli receive atmospheric air, and blood.venous.
	 *  The partial pressure difference determines the amount of gas transfer.
	 *  The gases equilibrate over 5 cycles @see exchange().
	 *  Alveolar gas is exhaled, and the blood goes to blood.arterial.
	 */
	public void breathe(){
		double asvp=environment.getSVP(environment.Temp.get()); //ambient SVP
		//evaporation
		double volumePerMinute=Vent.get() * (asvp/0.76)*(1-environment.Hum.get());
		double volume=volumePerMinute*elapsedTime/60;
		//calculate volume of H2O lost in litres, correct to STP
		double qH2Oloss=volume*273/(273+environment.Temp.get())*environment.H2OX.get();
		//rate of water loss
		H2Oloss.set (qH2Oloss/(elapsedTime/60) );
                exhaled_example.volume.set(qH2Oloss);                                        //*time dependent
                body.blood.withdrawVolExample_overwrite(exhaled_example,exhaled_temp);
  		exhaledWater.add(exhaled_temp);
		//log daily water loss
		if(body.getClock().dayChanged()){
			body.eventLog.document(EventLog.FLUID_EVENT,"Expired",
				new Double(-exhaledWater.volume.get()));
			environment.rubbish.add(exhaledWater);
		}
                //alveolar blood
                Blood blood=body.blood;
                alvBlood.setBlood(blood);
                alvBlood.set(blood.venous); //concs from venous blood gases
		//alveolar gases
                ventilate = AVent.get()>0;
                if(ventilate){
                  double dpH2O=environment.getSVP(body.Temp.get())-environment.Hum.get()*asvp; //difference in water vapour pressure
                  barp=environment.BarP.get();
                  Pa=barp-asvp;                        //alveolar air pressure without water
                  AVentAir=AVent.get()*(dpH2O+barp)/barp;     //alveolar inspired gas volume
                  alvP.O2.set(environment.air.O2.get()*Pa);   //freash alveolar gases
                  alvP.CO2.set(environment.air.CO2.get()*Pa);
		} else{
                  AVentAir=FRC.get();   //leave alvP as after previous cycle.
                  alvTmp.set(alvP);
                  /**
                   * @todo alvP should only decrease by an amount proportional
                   * to elapsed time if no ventilation takes place - e.g.
                   * to demonstrate preoxygenation
                   */

                }

                //calculate efficiency of gas exchange

                // LAP increase of 1 mmHg gives 50 mL of pulmonary oedema
                // Oedema.regulateQuantity( body.CVS.heart.left.atrialP, 50, fractionDecayPerMinute(0.3) );
                double target= Math.min(Math.max(
                  50*body.CVS.heart.left.atrialP.getError() - 0.250,0),2);
                Oedema.lowPass(target,fractionDecayPerMinute(0.13));


                double shunt = Shunt.get();
		tto2=0;
		ttco2=0; //start logging how much gas is exchanged

                // convert pps to equivalent minute volumes, using the alveolar air
                // ventilation volume, AVentAir (or FRC if not ventilating)
                alveolarPtoV();

              //the effective current cardiac output to the lung
              cardout=body.CVS.heart.CO.get() * (1-shunt);

              //nine iterations of gas exchange should
              //suffice for equilibrium.
              int extraCycles=0;
              double otto2=0, ottco2=0;
              fractionEquilibration=initialFractionEquilibration;
              for(int i=0;i<DEFAULT_N_CYCLES;i++){
                otto2=tto2;  ottco2=ttco2;
                exchange();
                if(i==DEFAULT_N_CYCLES-1 && (
                    // 22.7.5 thresholds were (0.030, 0.001)
                    Math.abs(alvP.O2.get() -alvBlood.PO2.get()) > pO2Accuracy ||
                    Math.abs(alvP.CO2.get()-alvBlood.PCO2.get())> pCO2Accuracy )
                   ){      //repeat if unsuccessful
                  extraCycles++;
                  if(extraCycles<MAX_EXTRA_CYCLES) i--;
                  if(extraCycles>4)fractionEquilibration*=0.95;
                }
              }
//if(DEBUG_ERRORS) if( Math.abs(alvP.O2.get() -alvBlood.PO2.get()) > pO2Accuracy)
//                body.message("A-a grad = "+1000*(alvP.O2.get() -alvBlood.PO2.get())+" mmHg");


              if(!ventilate){ // decrease amount of gases in the residual volume
                //tto2=0;ttco2=0;
                alvP.O2.set(alvTmp.O2.get() - tto2*Pa/AVentAir*elapsedTime/60);
                alvP.CO2.set(alvTmp.CO2.get() - ttco2*Pa/AVentAir*elapsedTime/60);
              }


              if(verbose){ //display A-a gradient
                double AaO2 = Math.abs(alvP.O2.get() - alvBlood.PO2.get()),
                    AaCO2 = Math.abs(alvP.CO2.get() - alvBlood.PCO2.get());
                if (AaO2 > pO2Accuracy) {
                  inform("A-a O2 = " + Quantity.toString(AaO2 * 1000) + " mmHg");
                }
                else if (AaCO2 > pCO2Accuracy) {
                  inform("A-a CO2 = " + Quantity.toString(AaCO2 * 1000) + " mmHg");
                  if (extraCycles > 0) inform("extra cycles = " + extraCycles);
                  if (extraCycles > 9) inform("last transfer = " + (tto2 - otto2));
                }
                if(extraCycles>0){inform(extraCycles+" extra resp cycles");}
              }

              //Carbon monoxide
              /** @todo does not currently include Hb! need to multiply fraction
               * by Hb to get quantity, then perform alveolar transport, then re-convert
               * to fraction (dependent on cardiac output)
               */

              //error in blood CO concentration
               //double dco = environment.air.CO.get()
               //    - blood.CO.get() / (CO_SOLUBILITY - alvP.O2.get()/CO_AFFINITY );
               double dco = environment.air.CO.get() * environment.BarP.get() * CO_AFFINITY / alvP.O2.get()
                              - blood.CO.get() ;
               //concentration-change (gas flow per litre of blood) per minute into blood
               double ttco = Math.max(0,dco) * CO2Permeability*6 //flow into blood
                   + Math.min(0,dco)*CO2Permeability*0.65; //flow out to air
               double fdpm= fractionDecayPerMinute(0.01);
               double qco = ttco * fdpm;
               qco = Math.max(qco, -blood.CO.get());
               blood.CO.add(qco);
               double avent=AVent.get();
               if(avent!=0){
                 expired.CO.set(Math.max(0,
                                         environment.air.CO.get() - qco / fdpm / AVent.get()));
               }
               //alvBlood.O2.add(-blood.CO.get());


              // effect of pulmonary oedema: if >300ml then each 10ml gives 2% shunt
              // e.g. 400ml gives a 20% shunt
              shunt += Math.max(0,Oedema.get()-0.300) * 2;
              //send the alveolar blood gas concentrations to Blood.arterial
              //low passing to account for residual volume
              //added 26-9-02: 0 < concentrations < 1 Litre/Litre
              blood.arterial.O2.lowPass(Math.max(0,Math.min(1,
                  alvBlood.O2.get() * (1-shunt) + blood.venous.O2.get() * shunt
                  )),fractionDecayPerMinute(0.7));//.7
              blood.arterial.CO2.lowPass(Math.max(0,Math.min(1,
                  alvBlood.CO2.get() * (1-shunt) + blood.venous.CO2.get() * shunt
                  )),fractionDecayPerMinute(0.7));
              if(verbose) inform("Exchanged "+UnitConstants.formatValue(tto2, UnitConstants.LITRES_PER_ML,false)+" O2, "+
                                 UnitConstants.formatValue(ttco2, UnitConstants.LITRES_PER_ML,false)+" CO2");

             }



            private Container exhaled_example = Fluids.get("Water", 1);
            private Container exhaled_temp = new Container();

            /**
             * Relative solubility of CO in blood - i.e.
             * the ratio of atmospheric CO to blood CO concentration
             * at equilibrium.
             */
            public double CO_SOLUBILITY = 0.90;


            /**
             * relative Affinity of CO to haemoglobin, relative to oxygen.
             * i.e. the ratio of blood oxygen to CO that is needed at
             * equilibrium.
             */
            public double CO_AFFINITY = 9.5;

            /**
             * This is the value of fractionEquilibration at the start of each gas
             * exchange loop.
             */
            public double initialFractionEquilibration=1.0;

	/**
	 * Fraction between the partial pressures to equilibrate to.
	 * Ought to be ~0.5. This value is changed in the gas exchange loop
         * to equilibrate the gases accurately.
	 * @todo should be private
	 */
	protected double fractionEquilibration=0.2;

        public static final int DEFAULT_N_CYCLES = 5;
        public static final int MAX_EXTRA_CYCLES = 64;

        /**
         * Alveolar permeability to carbon dioxide. Unit is gas flow rate per
         * unit difference in partial pressure
         * 7.5.6 - was 1.32, changed to 1.20 to give a normal co2
         * @todo private final
         */
        public double CO2Permeability = 1.20;
        /**
         * Alveolar permeability to oxygen. Unit is a constant, representing
         * the fraction of the expected flow rate requirement.
         * @todo private final
         */
        // double O2_PERMEABILITY = 0.503;


	/**
	 * Convert the difference in partial pressures between blood and air
	 * into the target concentration change.
	 * @todo should be private
         * @todo not used now.
	 */
	public double PtoDC=0.25;

	/**
	 * Temporary variable storing current cardiac output,
	 * which is constant across successive calls to exchange()
	 */
	private double cardout;

        /** Temporary variable for environmental barometric pressure */
        private double barp;


	/**
	 * Temporary record of cumulative exchange rates of O2 and CO2.
	 * @todo should be private
	 */
	public double tto2,ttco2;

	/**
	 * This routine iterated five times to equilibrate partial pressures of
	 * gases in alveolar gas and capillary gas. Ratio 'PtoDC' is used to convert
	 * a difference in partial pressure to a volume.
	 */
	public void exchange(){
		//find alveolar gas flows
		alveolarPtoV();
		//difference in gas & blood partials
		dpp.O2.set(alvP.O2.get()-alvBlood.PO2.get());
		dpp.CO2.set(alvP.CO2.get()-alvBlood.PCO2.get());
                double currentBloodPO2 = alvBlood.PO2.get(), currentBloodPCO2 = alvBlood.PCO2.get();
                // estimate the O2 conc change needed as:
                // the difference in O2 conc if blood tension = current alveolar tension
		double neededChangeO2Conc=estimateO2ConcChange(alvP.O2.get());
               // double neededChangeCO2Conc=                    estimateCO2ConcChange(alvP.CO2.get());
		//double targetPPCO2=alvP.CO2.get()-dpp.CO2.get()*fractionEquilibration ;
		// estimation based upon alveolar partial pressure reaching target

                //rates of gas transfer (L/min)
		double transO2=dpp.O2.get() *fractionEquilibration /barp,
                      transCO2=dpp.CO2.get()*CO2Permeability * fractionEquilibration/barp  ;

                transO2 =neededChangeO2Conc * cardout * 1 * fractionEquilibration;
                //transCO2=neededChangeCO2Conc* cardout * 1 * fractionEquilibration;
		// rate of gas transfer from air to blood
		tto2+=transO2;
		ttco2+=transCO2;
		//transfer gases between alveoli and capillaries using Fick principle
		alvBlood.O2.set(alvBlood.O2.get()+transO2/cardout);
		alvV.O2.set(alvV.O2.get()-transO2);
		alvBlood.CO2.set(alvBlood.CO2.get()+transCO2/cardout);
		alvV.CO2.set(alvV.CO2.get()-transCO2);
		//convert back to pp
		alveolarVtoP();
	}


        /**
         * add a tiny amount of O2 volume to the alvBlood, and judge the change
         * in PO2. then estimate how much you need to add to bring PO2 to the
         * desired value.
         * Basically, it's newton-raphson method, but costly because it requires
         * an extra O2-table lookup.
         */
        protected double estimateO2ConcChange(double ppO2to){
          double oPO2 = alvBlood.PO2.get();
          alvBlood.O2.add(ESTIMATE_DELTA);
          double gradPO2 = (alvBlood.PO2.get() - oPO2)/ESTIMATE_DELTA;
          return (ppO2to-oPO2) / gradPO2;
        }

        /**
         * add a tiny amount of CO2 volume to the alvBlood and judge the change
         * in PCO2. Then estimate how much is needed to bring PCO2 to the desired
         * value, using first order (linear) approximation, assuming that the
         * O2 concentration is held constant.
         */
        protected double estimateCO2ConcChange(double ppCO2to){
          double oPCO2 = alvBlood.PCO2.get();
          alvBlood.CO2.add(ESTIMATE_DELTA);
          double gradPCO2 = (alvBlood.PCO2.get()-oPCO2)/ESTIMATE_DELTA;
          return (ppCO2to-oPCO2)/gradPCO2;
        }

        /**
         * Tha volume of gas to add in the 'estimateCO2ConcChange' and
         * estimateO2ConcChange' functions.
         * default = 1 microlitre of O2 per litre! = 1e-6
         */
        double ESTIMATE_DELTA = 1E-9;

        /**
         * Pulmonary arterial to venous shunting - this is the mechanism for
         * hypoxia in pneumonia etc. - i.e., this is the proportion of the
         * cardiac output that passes through unventilated lung or does not
         * pass through the lung at all.
         */
        public VDouble Shunt=new VDouble();

        /** Convert alveolar partial pressures to alveolar minute volumes of gases */
        private void alveolarPtoV(){
		alvV.O2.set(aPtoV(alvP.O2.get()));
		alvV.CO2.set(aPtoV(alvP.CO2.get()));
	}
        /** Convert alveolar minute volumes to alveolar partial pressures  */
        private void alveolarVtoP(){
                alvP.O2.set(aVtoP(alvV.O2.get()));
                alvP.CO2.set(aVtoP(alvV.CO2.get()));
        }

	/** Alveolar pressure-volume conversion */
	protected double aPtoV(double p){
		return p*AVentAir/barp;
	}

	/** Alveolar volume-pressure conversion */
	protected double aVtoP(double v){
		return v*barp/AVentAir;
	}

        public void reset() {
          super.reset();
          AVentAir = 0;
          alvBlood.empty();
          dpp.empty();
          alvP.empty();
          alvV.empty();
          alvTmp.empty();
          lungVolume.set(FRC.get());
          flow.set(0);
          phase = 0;
          initialFractionEquilibration = 1.00;
        }

	private static final double min_abs(double a,double b){
		return(Math.abs(a)<Math.abs(b))?a:b;
	}

        /** Phase of respiration, from 0 to 1. */
        public double phase =0;
        /** work out the phase in the respiratory cycle */
        void calculatePhase(){
          phase += elapsedTime/60 * RespR.get();
          phase -= (int)phase;
        }

        public VDouble lungVolume=new VDouble();
        /** functional residual capacity.
         * Lies between Residual volume and TLV
         */
        public VDouble FRC = new VDouble();
        /** Forced Vital capacity in L, equal to FVC - Residual volume */
        public VDouble FVC = new VDouble();
        /** Total lung capacity in L */
        public VDouble TLV = new VDouble();
        /** airway pressure in cmH2O */
        public VDouble Pairway=new VDouble();
        /** airway flow, L/min */
        public VDouble flow=new VDouble();
        /** Ventilation modes */
        static final int SPONTANEOUS=0, BIPAP=1;
        /** the mode of ventilation, one of SPONTANEOUS, BIPAP */
        int ventilationMode = SPONTANEOUS;
        /**
         * Compliance, in litres per cmH20.
         *  @todo this is a function of lungVolume (static compliance)
         *  and flow (dynamic compliance)
         */
        public VDouble compliance = new VDouble(); /*{
          public double get(){ return maximumCompliance; }
        }; */

        /** Maximum compliance, in litres per cmH20 */
        public double maximumCompliance = 0.080;
        /** Resistance, in cmH2O per L/min */
        public VDouble airwayResistance = new VDouble();
        /** The inspiratory time divided by the expiratory time. */
        public VDouble IEratio = new VDouble();

        /** @todo these are actually protected */
        public double effectiveCompliance;
        /** intraalveolar pressure @todo protected */
        public double iap;



        /** Calculate the volumes */
        void volumes(){
          if(Current.body.getClock().isSlowMode){
            double frc=FRC.get();
            double v=lungVolume.get();
            double tlv=TLV.get();
            /** compliance falls at 77% of TLV, down to 10% at TLV */
            effectiveCompliance = compliance.get()
                * Math.min(1, (tlv - v)/tlv  *4 + 0.10) ;
            iap = (v-frc)/effectiveCompliance    // intraalveolar pressure
                /** when you stand it's unchanged; when you lie flat it is * 1.5 */
                * (2 - environment.Uprt.get());
            ITP.set(-iap-0.005);
            if(ventilationMode == BIPAP){
              /** square wave */
              double paw=  TidV.get()/70 * ((phase-IEratio.get()<0)?1:0);
              Pairway.set( paw );
              flow.set((paw-iap)/airwayResistance.get());
              double et=(elapsedTime>2)?0.1:elapsedTime;
              lungVolume.addLimited(flow.get()*et/60);
            }else if(ventilationMode == SPONTANEOUS){
              /** Pressure increases to plateau.
               * What pressure is needed to reach the correct tidal volume?
               * max reach = tv*(1-e^(1/rr * )
               * dv/dt = dp/res = (p0-p)/res = (p0-v/c)/res
               * u=p0-v/c, du/dv=-1/c
               * res dv/dt = u = res du/dt dv/du = -c res du/dt
               * -c res 1/u du = dt
               * ln u = -t/(c res) + K
               * u = A e^(-t/(c res)) = p0-v/c
               * t=0, v=v0 : A=p0-v0/c
               * v/c=(p0-v0/c)(1-e^-t/cres) : L=(1-e^-t/cres)
               * p0 = (L v0 + v) / (L c) = (v0 L + v0 + TV) /Lc
               */
              double compl = effectiveCompliance;
              double effectiveResistance = airwayResistance.get() * ((phase-IEratio.get()<0)?1:expiratoryResistanceRatio);
              double L = 1-Math.exp(-1/(compl*effectiveResistance*RespR.get()));
              double targetP =
                  /* peakPressure * TidV.get()/0.5 /compliance.getErrorRatio() */
                   TidV.get()/compl/(1-Math.exp(-IEratio.get()/(compl*effectiveResistance*RespR.get())))
                  * ((phase-IEratio.get() < 0)?1:0) ;
              /** use elapsedtime * 60 for extrafine intervals! */
              double frate = fractionDecayPerMinute(0.9997, elapsedTime * 60);
              Pairway.lowPass(targetP, frate);
              double paw = Pairway.get();
              flow.set((paw-iap)/effectiveResistance);
              double et=(elapsedTime>2)?0.1:elapsedTime;
              lungVolume.addLimited(flow.get()*et/60);
            } else {}
          }else{ // Fast mode
            lungVolume.set(TidV.get()/2 + FRC.get());
            flow.set(0);
            double hp = IEratio.get() * TidV.get() / compliance.get();
            // ITP.set(hp); // what's this doing here?
            ITP.set(ITP.initialValue-hp);
            Pairway.set(hp);
           }
        }
        /** Peak pressure of intrapleural space during inspiration (mmHg) */
        public double peakPressure = 0.0052;
        /** Expiratory resistance is higher than inspiratory resistance */
        public double expiratoryResistanceRatio = 1.3;
        /** Intrathoracic pressure */
        public VDouble ITP = new VDouble();


}

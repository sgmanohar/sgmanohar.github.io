package phic;
import phic.common.*;
import phic.drug.*;
import phic.doctor.DrugParser;
import phic.drug.NoSuchDrugException;
import phic.gui.VisibleVariable;
import phic.gui.Variables;
/**
 * This class provides a set of actions that can be performed on the body.
 *
 * They are simply quick shortcuts to common useful features, all of which
 * can also be accessed a different way, by directly altering other body
 * parameters, or giving infusions.
 *
 * There is an instance of this in Environment.
 */
public class BasicActions implements HasContent{
	protected Body body;

	public BasicActions(Body body){
		this.body=body;
	}

	/**
	 * The amount of increase in Symp when frighten is called
	 */
	public VDouble fright=new VDouble();

        /**
         * Alters the adrenaline level
         */
	public void frighten(){
		//body.brain.Symp.add( fright.get() );
                try{
                  body.blood.add(Pharmacy.dispenseAmpoule("Adrenaline", 0.001 * fright.get()));
                }catch(NoSuchDrugException e){e.printStackTrace(); }
	}

	/** How much amyl nitrate to administer */
	public VDouble amountAmylNitrite= new VDouble();

	/**
	 * Increases vasoconstrictor tone
	 */
	public void amylNitrite(){
		body.CVS.VCT.add(amountAmylNitrite.get());
	}

        /**
         * Decreases vasoconstrictor tone
         */
        public void glycerylTrinitrate(){
          try{
            ivBolus("GTN",Pharmacy.dispenseAmpoule("Glyceryl trinitrate", 0.010));
          }catch(NoSuchDrugException x){x.printStackTrace();}
        }

	/** Volume of fluid to drink */
	public VDouble drinkVolume=new VDouble();

	/** Puts water in the stomach */
	public void drinkWater(){
		drink("Water",Fluids.get("Water",drinkVolume.get()));
	}

	/** Put alkali in the stomach */
	public void drinkAlkali(){
		drink("Alkali",Fluids.get("Alkali",drinkVolume.get()));
	}

	/** Put acid in the stomach */
	public void drinkAcid(){
          Container acid = Fluids.get("Water", drinkVolume.get());
          acid.pH.set(3.5);
          drink("Acid",acid);
	}

	/** Put seawater in stomach */
	public void drinkSeawater(){
		drink("Seawater",Fluids.get("Seawater",drinkVolume.get()));
	}

	/** Put 500ml of food in stomach */
	public void eatMeal(){
		Current.body.eat(0.500);
	}

        /**
         *  Put 62 g of sugar into the stomach, which is 344 mmol
         * was previously 30g, which was 167 mmol = 344 mmol/L for 50ml
         */
        public void eatChocolateBar(){
          try{
            drink("Chocolate bar", Pharmacy.dispenseSubstance("Water 0.050 + glucose 0.600"));
          }catch(NoSuchDrugException x){}
        }

        /**
         * 50 g of carbohydrate
         */
        public void eatBread(){
          try{
            FoodContainer f=new FoodContainer();
            f.add(Pharmacy.dispenseSubstance("Water 0.010 + prot 0.100 + Na 0.080"));
            f.carbohydrate.setQ(800);
            drink("Bread", f);
          }catch(NoSuchDrugException x){}
        }

	/** Bolus of 500ml normal saline IV */
	public void salineBolus(){
		ivBolus("Saline",Fluids.get("Saline",0.500));
	}

	/** Bolus of 500ml 5% dextrose IV */
	public void dextroseBolus(){
		ivBolus("Dextrose",Fluids.get("Dextrose",0.500));
	}

	/** Bolus of 500ml Colloid IV */
	public void colloidBolus(){
		ivBolus("Colloid",Fluids.get("Colloid",0.500));
	}

	/** Bolus of 40mg Frusemide IV */
	public void frusemideBolus(){
		try{
			ivBolus("Frusemide 40mg",Pharmacy.dispenseAmpoule("Frusemide",0.040));
		} catch(NoSuchDrugException e){
			e.printStackTrace();}
	}

        /** Boluz of insulin IV: increase by 40%*/
        public void insulinBolus(){
          body.blood.Insul.add(0.40);
        }
        /** Bolus of erythropoietin: increase by 50% */
        public void erythropoietinBolus(){
          body.blood.Eryth.add(0.50);
        }

        /** Bolus of blood - transfuse one unit*/
        public void transfuse(){
          ivBolus("Blood", Fluids.get("PackedCells", 0.470));
        }
        public void potassiumBolus() throws NoSuchDrugException{
          ivBolus("5 mmol K+",DrugParser.createSubstance("Water 0.010 + K 0.005"));
        }

	/**
         *  Add the specified container to the stomach.
         * Also displays the event in the console, and records the fluid in the event log.
         */
	public void drink(String fluidName,Container c){
		double vol=c.volume.get();
		body.gitract.stomach.add(c);
		body.message("Consumed "+UnitConstants.formatValue(vol,UnitConstants.LITRES,
			false)+" of "+fluidName);
		body.eventLog.document(EventLog.FLUID_EVENT,"Drank "+fluidName,
			new Double(vol));
	}
	/**
	 * Add the specified container to the blood
         * Also displays the event in the console, and records the fluid in the event log.
	 */
	public void ivBolus(String fluidName,Container c){
		double vol=c.volume.get();
		body.blood.addWithoutO2(c);
		body.message(UnitConstants.formatValue(vol,UnitConstants.LITRES,
			false)+" of "+fluidName+" as IV bolus");
		body.eventLog.document(EventLog.FLUID_EVENT,
			"IV "+fluidName+" bolus", new Double(vol));
	}

        /**
         * Drink a pint of beer
         */
        public void drinkBeer(){
          try{
            drink("Beer", Pharmacy.dispenseSubstance("Water 0.568 + glucose 0.140 + Alcohol 0.025"));
          }catch(NoSuchDrugException x){x.printStackTrace();}
        }

        public void interferon(){
          try{
            drink("Interferon", Pharmacy.dispenseAmpoule("Interferon", 0.010));
          }catch(NoSuchDrugException x){x.printStackTrace();}
        }


        /**
         * Create a continuous change event that gradually performs a certain
         * change of value for a variable
         */
        public void startGradualChange(String variable, double target, double fractionPerMinute){
          VisibleVariable v = Variables.forName(variable);
          Current.body.brain.conscious.invokeGradualChange(
              (VDouble)v.node.objectGetVal(), target, fractionPerMinute);
        }


}

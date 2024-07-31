package phic;
import phic.common.*;
import phic.drug.Drug;

/**
 * A basic PerfusedOrgan that has a temperature regulation mechanism.
 * The Skin has a temperature and sweat rate. Sweat is saline.
 */
public class Skin extends PerfusedOrgan{
	/** Sweating rate */
	public VDouble SwR = new VDouble();

	/** Temperature of the skin */
	public VDouble Temp=new VDouble();

	/**
	 * This container traps all sweat that has been lost over the last 24
	 * hours, for the purposes of fluid balance logging.
	 */
	Container sweatLost=new Container();

        /** Total body surface area */
        public VDouble area=new VDouble();

        public Skin(){}

	/**
	 * Every tick, a quantity of saline is removed from the blood, equal
	 * the sweating rate SwR.
	 *
	 * The body heat change is calculated from Body.Cals() per Body.Mass().
	 * Some body heat flows to the skin, at a rate proportional to the
	 * skin blood flow.
	 * Sweat rate and specific latent heat of evaporation
	 * plus radiation and conduction to environmental Temp determine heat loss
	 * from skin.
	 * Regulation of skin vascular resistance is from CVS vasoconstrictor
	 * tone and blood viscosity.
	 *  resistance proportional to core temp error + skin temp error
	 *
	 * Currently, the evaporation is limited by atmospheric humidity,
	 * but this does not alter the variable SwR (the volume of sweat)
	 * actually secreted.
	 *
	 * @todo correct skin temperature fluctuations
	 */
	public void tick(){
		Environment e=Current.environment;
		//remove sweat from blood
		criticalPeriod.enter();
		double skinDT=(Temp.get()-e.Temp.get());
	//	synchronized(body.blood){
                       sweat_example.volume.set( SwR.get()*elapsedTime/60 );
                       body.blood.withdrawVolExample_overwrite(sweat_example, sweat_temp);
                       sweatLost.add(sweat_temp);
			//body gains heat from metabolism at rate 'Cals'
			//per minute
			// increase in temp caused by heat:
			// assume 1 Cal raises temp of 1 kg by 1 degree
			double metabolicHeat=body.Cals.get()/body.Mass.get();
			//heat transfer rate from body to skin
			double R=(body.Temp.get()-this.Temp.get())
                            *(flow.get()+0.045) / 10;
			//skin is 5% mass of body.
			//double equilFrac = fractionDecayPerMinute(0.05);//mistake. delete
			body.Temp.set(body.Temp.get()+(metabolicHeat-0.05*R)*elapsedTime/60);
			Temp.set(Temp.get()+0.95*R*elapsedTime/60);
	//	}
		criticalPeriod.exit();
		//Log the daily sweat loss
		if(body.getClock().dayChanged()){
			body.eventLog.document(EventLog.FLUID_EVENT,"Sweated",
				new Double(-sweatLost.volume.get()));
			environment.rubbish.add(sweatLost);
		}
		//these values are all rates per minute.
		//heat loss from skin by radiation and conduction
		//in calories?
		double radiationPerMin=skinDT*(1-e.Togs.get())/2.0;

		//heat loss from skin through sweating
		// Calculate effective humidity between environment.Hum and 100%,
		// due to insulation of Togs
		double effectiveHumidity=e.Togs.get()*(1-e.Hum.get())+e.Hum.get();

		// Calculate maximum possible water loss, assuming 40 litres of air are
		// cleared per minute (constant was 0.05 before, 11/1/03)
		double maxVaporPerMin=e.getSVP(e.Temp.get())/e.BarP.get()*0.5*(1-effectiveHumidity); //?*volume
		double actualSweatVolume=Math.min(SwR.get(),maxVaporPerMin);
                evaporationRate.set(actualSweatVolume);

		//Take skin mass = 5kg, and latent heat of vaporisation = 500cal/g
		double sweatHeatPerMin=actualSweatVolume*500.;
		Temp.set(Temp.get()-(sweatHeatPerMin+radiationPerMin)/5.*elapsedTime/60);

		//work out new vascular resistance based on temperature errors
		double error=body.Temp.getError()+0.1*Temp.getError()-6*body.brain.fever.get();
		double resError=Math.min(Math.max(error*-0.125+0.25,0.01),5);
                double vct = body.CVS.VCT.get();
                double apco2 = body.blood.arterial.PCO2.get();
		double resProduct=(1+(vct-1)*15) * resError /* *body.blood.Visc() */
			+ body.blood.getDrugBinding(Drug.ALPHA_ADRENOCEPTOR);
//!		resistance.lowPassQuantity(resProduct,fractionDecayPerMinute(0.4));

		//work out new sweat rate based on errors
		double swError=Math.min(Math.max(5E-4+error/100,0.0001),0.03);
		SwR.set(swError);
                if(verbose)  inform("lost "+(radiationPerMin+sweatHeatPerMin)+" deg/min");
		waitMinutes(1);
	}
        VDouble evaporationRate = new VDouble();
        private Container sweat_example = Fluids.get("Saline",1);
        private Container sweat_temp = new Container();
}

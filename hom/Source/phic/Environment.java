package phic;
import java.util.*;

import phic.common.*;
import phic.gui.*;
import phic.drug.DrugContainer;

/**
 * The environment in which the body is situated, implementing the main ways
 * in which the user can manipulate the patient.
 *
 * The environment is created in the static initialiser of Current. Then the
		 * setBody() method is called with the body that this environment should connect
 * to.
 * The environment is the accessible exterior from which the atmosphere,
 * actions, intravenous infusions,
 */
public class Environment implements java.io.Serializable{
	/**
	 * This table gives the saturated vapour pressure of water, at various
	 * temperatures.
	 */
	private static Table SVPTable=new Table("SVP.txt",1);

	/**
	 * Return the saturated vapour pressure of water at the given temperature,
	 * read from the table in SVP.txt.
	 */
	public static double getSVP(double temperature){
		//interpolate the SVP table
		//20-entries: d=5
		int n=SVPTable.nRows-1,d=100/n;
		double T=(temperature<0)?0:((temperature>99)?99:temperature);
		int r=(int)(T/d);
		double f=SVPTable.getDouble(r,0);
		double S=f+(SVPTable.getDouble(r+1,0)-f)*(T-d*r)/d;
		return S*1E-3;
	}

	/**
	 * Ambient air temperature
	 */
	public VDouble Temp = new VDouble();

	/**
	 * Humidity
	 */
	public VDouble Hum = new VDouble();

	/** Air pressure */
	public VDouble BarP = new VDouble();

	/**
	 * Is the patient upright? ranges from 0-100%.
	 */
	public VDouble Uprt=new VDouble();

	/**
	 * Thermal insulation (clothing index)
	 */
	public VDouble Togs = new VDouble();

	/**
	 * Exercise level measured in Joules per minute
	 */
	public VDouble Exer = new VDouble();

	/**
	 * ??
	 */
	public VDouble H2OX= new VDouble();

	/**
	 * Is the patient hyperventilating? If this is greater than zero, it is the
	 * fractional increase in respiratory drive.
	 * @see phic.Brain#breathing
	 */
	public VDouble Hyperv = new VDouble();

	/**
	 * The composition of inspired air. The values represent percentages of
	 * the volume. e.g. air.O2 = 0.21
	 */
	public Gas.WithCO air=new Gas.WithCO();


        /**
         * These values represent the partial pressures in inspired air.
         * They are calculated from the fractional compositions in Environment.air,
         * and can be set directly to alter the composition.
         */

        public Gas airP = new Gas();{
          airP.O2=new VDouble(){
            public double get(){
              return air.O2.get() * BarP.get();
            }
            public void set(double d){
              air.O2.set(d/BarP.get());
            }
          };
          airP.CO2 = new VDouble(){
            public double get(){
              return air.CO2.get() * BarP.get();
            }
            public void set(double d){
              air.CO2.set(d/BarP.get());
            }
          };
        }


	/**
	 * Breath holding: When this is true, the respiratory drive is set to zero,
	 * and thus the respiratory rate and tidal volume become zero.
	 * @see phic.Brain#breathing
	 */
	public boolean BrHld;

	/**
	 * If starve is true, No food allowed, but drink OK
	 */
	public boolean starve;

	/**
	 * Nil by mouth, ie no food nor drink.
	 */
	public boolean NBM;

	/**
	 * When Hmrg is true, this is the Rate of haemorrhage, in litres per minute.
	 * Default is 1 ml per minute. Bleeding is controlled in the method
	 * @link #intravenousInfusions Environment.intravenousInfusions()
	 *
	 */
	public VDouble bleedingRate =new VDouble();

	/**
	 * Whether there is a haemorrhage. If true, then blood will be lost
	 * at the rate {@link  #bleedingRate bleedingRate}. This is controlled
	 * by the method {@link #intravenousInfusions Environment.intravenousInfusions()}.
	 * Lost blood is placed in the rubbish.
	 */
	public boolean Hmrg=false;

	/**
	 * Waste excreted here. Each time waste is passed, the current contents
	 * of this container are dumped in rubbish, and the new waste is placed
	 * here. This is thereby a way of seeing what was last excreted.
	 */
	public Container urine=new Container();
        public FoodContainer stool=new FoodContainer();

	/**
	 * Rubbish: This is where all excreted and lost substances go when they
	 * are no longer needed. Once new wee or poo is produced, the old contents
	 * are dumped here. Evaporated sweat, exhaled water, and bleeding from
	 * heamorrhage ( @see #intravenousInfusions() ) also
	 * end up here.
	 */
	public Container rubbish=new FoodContainer();

  /**
   * Container containing the contents of a vomit. This is emptied into 'rubbish'
   *  and filled with the stomach contents each time body.vomit is called.
   */
  public Container vomit;



	/** External actions that can be performed upon the body */
	public BasicActions actions;

	/** Initialisations that depend upon the body. This method is called
	 * after both the Environment and the body have been created. */
	public void setBody(Body b){
		actions=new BasicActions(b);
		variableClamps=new LifeSupport();
		variableClamps.setBody(b);
	}

	/**
	 * List of intravenous infusions currently running.
	 * This is protected, so as not viewable in the tree.
	 *
	 * @todo when the tree is made to dynamically handle Vectors, make this public.
	 * @see #addInfusion addInfusion()
	 * @see #intravenousInfusions intravenousInfusions()
	 */
	protected Vector infusions=new Vector();

	/**
	 * Adds an infusion to the list of infusions currently hanging.
	 * Called by the GUI when the user sets up an infusion. Once added, the fluid
	 * will begin to be infused.
	 */
	public void addInfusion(IntravenousInfusion i){
		infusions.add(i);
	}

        /**
         * Method to construct infusions from scripts. It starts the infusion
         * immediately.
         */
        public IntravenousInfusion createInfusion(Container container, double rate){
          IntravenousInfusion i=new IntravenousInfusion(Current.body.blood, container, rate);
          addInfusion(i);
          i.start();
          return i;
        }

	/**
	 * This is called by the CVS each tick, and calls the tick method on each
	 * infusion that is currently running. When the infusion is over, it removes
	 * the drip from the list, and informs the GUI.
	 *
	 * Also responsible for blood-letting: If Hmrg is true, then blood is
	 * moved to rubbish at a rate of bleedingRate.
	 * @param time the amount of time elapsed this tick (in seconds).
	 */
	protected void doIntravenousInfusions(double time){
		for(int i=0;i<infusions.size();i++){
			IntravenousInfusion n=(IntravenousInfusion)infusions.get(i);
			if(n.isRunning()){
				n.tick(time);
			}
			if(n.empty){
				n.stop();
				if(PhicApplication.frame!=null){
					PhicApplication.frame.finishDrip(n);
				}
				infusions.remove(i--);
			}
		}
		if(Hmrg){
                  Current.body.blood.withdrawVol_overwrite(bleedingRate.get()*time/60, hmrg_temp);
                  rubbish.add(hmrg_temp);
		}
	}
        private Container hmrg_temp = new Container();
	/**
	 * The life support machine maintains a selected list of variables at
	 * fixed values. It may be modified by the LifeSupportDialog.
	 */
	protected LifeSupport variableClamps;

	/**
	 * Retrieve the current life support machine.
	 */
	public LifeSupport getVariableClamps(){
		return variableClamps;
	}

        public VDouble pain = new VDouble();

        public FoodContainer food = new FoodContainer();

  /**
   * resetAllBooleans - called when a full reset is performed.
   * Should reset any values that are not reset by Variables.initialise()
   * i.e. anything that is not a VDouble listed in Variables.txt
   */
  public void resetAll() {
    BrHld=starve=NBM=Hmrg=false;
    vomit.empty();rubbish.empty();
    urine.empty();stool.empty();
    initialiseFood();
    variableClamps.reset();
  }
  private void initialiseFood(){
    food.empty();
    food.add(Fluids.get("Food", 1.0));
    double cgluc = food.glucose.getC(); //get the total glucose from Fluids.txt
    food.glucose.multiplyBy(1-foodFractionCarb); //and convert some to larger carbs
    food.carbohydrate.set(cgluc * foodFractionCarb / food.MOLES_GLUCOSE_PER_GRAM_CARB);
  }
  /** The fraction of the food's glucuse which is in the form of large carbs */
  double foodFractionCarb = 0.95;


}

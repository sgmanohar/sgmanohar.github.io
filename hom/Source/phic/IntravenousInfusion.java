package phic;
import phic.common.*;
import phic.drug.DrugContainer;
import phic.gui.PhicApplication;

/**
 * Intravenous infusion at a steady rate. It is a drug container, and contains
 * the fluid in the infusion bag. Once created, use the add() method to put
		 * a fluid into the infusion, which will run at the rate specified by the member
 * 'rate'.
 */
public class IntravenousInfusion extends DrugContainer{
	/**
	 * The blood object into which this intravenous infusion injects
	 * its contents. This usually points to Current.body.blood.
	 */
	protected Blood blood;

	/**
	 * Create an intravenous infusion to inject fluid into the specified
	 * blood.
	 */
	public IntravenousInfusion(Blood blood){
		this.blood=blood;
	}

        /**
         * Create an infusion from injecting into the given blood, using fluid
         * from the given container, running at the given rate.
         */

        public IntravenousInfusion(Blood blood, Container fluid, double rate){
          this(blood);
          this.add( fluid );
          this.rate = rate;
        }


	/**
	 * Rate of infusion in litres / min
	 */
	public double rate=0.002;

	/**
	 * Whether fluid is running at the moment
	 * The drip may be stopped or started at any point by the interface, using
	 * the stop() and start() methods.
	 */
	private boolean running=false;

	/**
	 * This is set when the drip has run out (i.e. volume = 0). Once the
	 * drip has been specified as empty, it may be safely removed.
	 * The drip is always stopped once it is empty.
	 */
	public boolean empty=false;

	/**
	 * If this is set, then the infusion will be refilled when it is empty
	 * This is currently not working, as no safe way has been implemented
	 * to clone containers.
	 */
	public boolean autoRefill=false;

	/**
	 * Returns true if the infusion is running, i.e. has been started by a call
	 * to the start() method.
	 */
	public boolean isRunning(){
		return running;
	}

	/**
	 * The default volume for an infusion, in litres. This is the volume of
	 * fluid that will be created each time the drip is automatically refilled.
	 * It is by default, 1 litre.
	 */
	public int defaultStartVolume=1;

	/**
	 * Variable that remembers what level the infusion was at when the start
	 * button was last pressed. It is used for logging how much fluid has
	 * been infused in one go.
	 */
	protected double startVolume=defaultStartVolume;

	/**
	 * This holds a sample of the fluid being infused, so that more may be
	 * created when the drip runs out, if needed.
	 */
	protected DrugContainer sample;

	/** Start the infusion running */
	public void start(){
          if(running!=true){
            PhicApplication.markEvent("IVI on");
          }
		running=true;
		startVolume=volume.get();
		if(startVolume>0){
			sample=(DrugContainer)this.withdrawVol(0.0001); // keep 0.1 ml  as a sample
		}
	}

	/**
	 * Stop the infusion, and log the infused volume in the body's event log.
	 * This uses the startVolume to calculate how much has been infused since
	 * the start button was last pressed.
	 */
	public void stop(){
          if(running==true){
            PhicApplication.markEvent("IVI off");
          }
		running=false;
		double infusedVolume=startVolume-volume.get();
		Current.body.eventLog.document(EventLog.FLUID_EVENT,"Infused",
				new Double(infusedVolume));
                Current.body.message("Infused "+UnitConstants.formatValue(infusedVolume, UnitConstants.LITRES, false));
	}

	/**
	 * Add a part of the bag to the blood.
	 * This is called by Environment.intravenousInfusions() every cycle.
	 * @param time The amount of time that has elapsed since the last cycle, in seconds.
	 */
	public void tick(double time){
		if(volume.get()==0){
			if(autoRefill&&sample!=null){
				stop();
				this.add(sample.createMore(defaultStartVolume));
				sample=null;
				start();
			} else{
				empty=true;
				running=false;
			}
		} else{
			Container c=withdrawVol(rate*time/60);
			blood.addWithoutO2(c); // each minute
		}
	}

	/** Return the volume of the bag when the start button was last pressed */
	public double getStartVolume(){
		return startVolume;
	}
}

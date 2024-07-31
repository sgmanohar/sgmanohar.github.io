package phic;

import java.util.*;

import phic.common.*;
import phic.drug.*;
import phic.gui.*;
import phic.modifiable.*;
import evaluator.*;
import evaluator.*;

/**
 * The main class containg all Body elements. Phic is divided into Body and
 * Environment; the body contains the 'internal' physiology.
 * Divides into organs, compartments and other global body
 * variables.
 */
public class Body
    implements java.io.Serializable {
  /**
   * Send a global message. By default this is sent to the application
   * frame's console, and to the System output console.  The event time
   * is prepended.
   */
  public void message(String s) {
    if(clock.getSecond()<0.05){
      s = clock.getTimeString(Clock.SHORTDATE) +" " + clock.getHourString() + ": "+s;
    }else{
      s = clock.getTimeString(Clock.TIME) + ": " + s;
    }
    System.out.println(s);
    if (receiver != null) {
      receiver.message(s);
    }
  }

  /**
   * The receiver to which messages and errors will be output. This should
   * be set by the creator of this Body. This is non-serialisable, and so
   * should be set during loading from file.
   */
  public transient TextReceiver receiver;

  /**
   * By default, this sends a message with th error, and throws a runtime
   * exception. This could be caught by the dispatching thread, or
   * can be stopped as a breakpoint in the debugger
   */
  public void error(String s) {
    System.out.println(s);
    if (receiver != null) {
      receiver.error(s);
    }

  }

  /** Return the name of the person associated with this body */
  public String getName() {
    return Current.person.name;
  }

  public Body() {
    initialiseContainers();
    initialiseOrgans();
  }

  //CONTENTS

  //list of variables

  /** Body temperature. Changed by Skin  */
  public VDouble Temp = new VDouble();

  /**
   * Basal metabolic rate, in cal/d. Initially 2E6
   * 1% increase Thyr gives 1% increase in BMR
   * 1 degree increase Temp gives 10% increase BMR
   * 26/7/7 depends only on body mass components that involve metabolism!
   */
  public VDoubleReadOnly BMR = new VDoubleReadOnly() {
    public double get() {
      //return initialValue + 2E6 * blood.Thyr.getError() + 1E5 * Temp.getError();
      return Math.max(0,
                      initialValue + 1E17 * blood.Thyr.getError()
                      + 5E4 * Temp.getError()
                      + 5E4 * skin.Temp.getError()
                      + 5000 * icf.volume.getError()
                      + 5 * Fat.mass.getError()
                      + 5 * (dryMuscleMass+dryBoneMass- 15000)
                      );
    }
  };

  /**
       * The quantity of osmolar ions in intracellulare fluid. This is usually fixed
   * for an individual
   * 15/1/03 Replaced by (icf.Osm * icf.volume)
   */
  // public double IQ;
  /**
   * DOsm is the difference in intra- and extracellular osmolarity
   * calculated directly from
   * intracellular ion Q / intracellular water - ECF osmolarity.
   * Thus a positive value indicates excess intracellular ions.
   * Cannot be set.
   */
  public VDoubleReadOnly DOsm = new VDoubleReadOnly() {
    public double get() {
      return icf.Osm.get() - ecf.Osm.get();
    }

    public void set(double d) {
      this.cannotSet();
    }
  };

  /** Respiratory quotient */
  public VDouble RQ = new VDouble();

  //Read only values
  /**
   * Total body water calculated as blood.volume + ecf.volume + intracellular water.
   */
  public VDoubleReadOnly H2O = new VDoubleReadOnly() {
    public double get(){    return
          blood.volume.get() + ecf.volume.get() + icf.volume.get();
    }
  };

  /**
   * Total body mass, calculated from combined masses of fat, total body
   * water (H20), and the GI tract stomach and colon, bladder volume;
   * plus 10 kg of other body mass.
   */
  public VDoubleReadOnly Mass = new VDoubleReadOnly() {
    public double get(){
      return (gitract.stomach.volume.get() + gitract.colon.volume.get()
              + bladder.volume.get() + H2O.get()) * 1000 + Fat.mass.get()
          + Glycogen.get()
          + dryMuscleMass
          + dryBoneMass;
    }
  };

  /** The other body mass added to the fluids */
  public double dryMuscleMass = 10.0E3;
  public double dryBoneMass   =  5.0E3;

  /** Non-muscle oxygen use, is in fact the basal metabolic rate. */
  public VDoubleReadOnly NMO2Use = new VDoubleReadOnly() {
    public double get(){return BMR.get() / 8E6;}
  };

  /**
   * Total rate of oxygen use, the sum of non-muscle oxygen use and muscle oxygen use.
   * calculated in Muscle. Measured in litres per minute.
   */
  public VDouble O2Use = new VDouble();

  /**
   * Body's carbon dioxide production rate, litres per minute. Calculated
   * from oxygen usage * respiratory quotient.
   */
  public VDoubleReadOnly CO2Production = new VDoubleReadOnly() {
    public double get(){ return O2Use.get() * RQ.get(); }
  };

  /**
   * Metabolic rate in Watts calculated from muscle energy use + basal
   * metabolic rate, and fever.
   * Equal to 'Cals' in Watts
   */
  public VDoubleReadOnly MBR = new VDoubleReadOnly() {
    public double get(){return
          1440 * muscle.Cal.get()
          + (1+0.5*brain.fever.get()) * BMR.get()
    ;}
  };

  /**
   * Body heat produced per minute, in calories.
   * Equal to Metabolic rate MBR in calories / min.
   */
  public VDoubleReadOnly Cals = new VDoubleReadOnly() {
    public double get() {      return MBR.get() / 1440 ;  }
  };

  /**
   * Total body sodium, in moles
   */
  public VDoubleReadOnly NaQ = new VDoubleReadOnly(){
    public double get(){ return blood.Na.getQ() + ecf.Na.getQ() + icf.Na.getQ();
    }
  };

  /**
   * Total body potassium, in moles
   */
  public VDoubleReadOnly KQ = new VDoubleReadOnly() {
    public double get() {
      return blood.K.getQ() + ecf.K.getQ() + icf.K.getQ();
    }
  };


  /**
   * Body's glycogen storage. Glycogen mass in grams.
   */
  public VDouble Glycogen = new VDouble();

  /**
   * Body's fat storage. Fat mass in grams.
   */
  public Fat Fat = new Fat();

  //containers
  /**
   * The Blood object is an extension of a container, which has two
   * divisions, the blood per se, and the plasma.
   * @see phic.Blood
   */
  public Blood blood;

  /**
   * The extracellular fluid compartment.
   */
  public Container ecf;

  /**
   * The intracellular fluid compartment.
   */
  public Container icf;

  /**
   * Container with the last stool excreted
   */
  //public Container stool;

  /**
   * Container representing current contents of the urinary bladder
   */
  public Container bladder;

  /** Creates the containers ecf, vomit, stool, bladder, rubbish, blood. */
  void initialiseContainers() {
    ecf = new DrugContainer();
    icf = new Container();
    Current.environment.vomit = new DrugContainer();
    //stool = new DrugContainer();
    bladder = new DrugContainer();
    blood = new Blood();
  }

  /** Empties the containers vomit, stool, bladder and rubbish. */
  void resetContainers() {
    environment.vomit.empty();
    //stool.empty();
    bladder.empty();
    Current.environment.rubbish.empty();
    blood.empty();
    ecf.empty();
    icf.empty();
  }

  /** Calls the reset method on each organ. */
  void resetOrgans() {
    Iterator e = Organ.getList().iterator();
    while (e.hasNext()) {
      ( (Organ) e.next()).reset();
    }
    Organ.resetRandom();
  }

  /**
   * Clock:
   * Should be protected access, but how do we overcome this??
   */
  public Clock clock = new Clock();

  //organs
  /** The GI Tract, including liver, pancreas and gut */
  public GITract gitract;

  /** The GI Tract, including liver, pancreas and gut */
   public Liver liver;

  /** The kidneys */
  public Kidney kidney;

  /** The cardiovascular system, managing the peripheral arteries and capillaries */
  public CVS CVS;

  /** The lungs */
  public Lung lungs;

  /** The skin, and temperature control mechanisms */
  public Skin skin;

  /** The muscle, and other energy expenditure */
  public Muscle muscle;

  /** The brain */
  public Brain brain;

  /**
   * This represents the organs which do very little.
   * It is subclassed directly from PerfusedOrgan.
   * Vascular resistance of viscera depends on Vasoconstrictor tone and
   * vasodilator drugs.
   */
  public PerfusedOrgan viscera = new PerfusedOrgan() {
    public void tick() {
//!      double vct=CVS.VCT.get();
//!      resistance.set(0.056
//!                     * (1+(vct*vct-1)*15)
//!                     + 0.050
//!                     * blood.getDrugBinding(Drug.ALPHA_ADRENOCEPTOR));
      controllers.calculateAll(elapsedTime);
      waitMinutes(1);
    }
  };
  static final String controllersFile = "Controllers.txt";
  public transient ControllerList controllers;
  public ControllerList getControllerList(){return controllers; }

  /**
   * Creates a new set of organs, then calls Organ.setBody(this).
   * If the serial organ tick order is followed, this list determines
   * the order in which the tick() methods will be called.
   */
  void initialiseOrgans() {
    skin = new Skin();
    brain = new Brain();
    muscle = new Muscle();
    lungs = new Lung();
    kidney = new Kidney();
    CVS = new CVS();
    gitract = new GITract();
    liver=new Liver();
    Organ.setBody(this);
  }

  //bodily functions
  //these mainly perform the task, then post a message to the console.
  /**
   * Urinate empties wee into rubbish, and the bladder into wee. The volume
   * is displayed, and logged.
   */
  public void urinate() {
    Container wee = Current.environment.urine;
    environment.rubbish.add(wee);
    wee.add(bladder);
    double volume = wee.volume.get();
    eventLog.document(EventLog.FLUID_EVENT, "Urinated ",
                      new Double( -volume));
    if(isLogging())message("Urinated "
            + UnitConstants.formatValue(volume, UnitConstants.LITRES,false));
  }

  /**
   * This empties poo into rubbish, and colon into poo. The event is
   * displayed and the volume logged.
   */
  public void defaecate() {
    environment.rubbish.add(Current.environment.stool);
    environment.stool.add(gitract.colon);
    double volume = environment.stool.volume.get();
    eventLog.document(EventLog.FLUID_EVENT, "Defaecated ",
                      new Double( -volume *
                                 (1 - environment.stool.solids.get())));
    if(isLogging())message("Defaecated");
  }

  /**
   * This empties vomit into rubbish, and 80% of stomach into vomit. The event is
   * displayed and the volume logged.
   */
  public void vomit() {
    environment.rubbish.add(environment.vomit);
    environment.vomit.addAndDiscardExtras(gitract.stomach.withdrawFrac(0.80));
    double volume = environment.vomit.volume.get();
    message("Vomited "+UnitConstants.formatValue(volume, UnitConstants.LITRES,false));
    eventLog.document(EventLog.FLUID_EVENT, "Vomited ",
                      new Double( -volume));
    PhicApplication.markEvent("V");
  }

  /**
   * The specified volume of 'Food' is retrieved from Fluids.txt. The
   * fluid is added to the stomach, and the hunger and thirst are reduced.
   * The volume is displayed and logged, and the event is drawn to the graphs.
   * @todo implement user-controlled diet
   */
  public void eat(double volume) {
    Container food = environment.food.createMore(volume);
    double fluid = volume * (1 - food.solids.get());
    gitract.stomach.add(food);
    brain.hunger.add(-volume);
    //added to remove excessive consumption 30/9/02. How much does eating satisfy thirst?
    brain.thirst.set(Math.max(0, brain.thirst.get() - volume/6));
    if(isLogging())message("Eaten "
            + UnitConstants.formatValue(volume, UnitConstants.LITRES,false));
    eventLog.document(EventLog.FLUID_EVENT, "Eaten ",
                      new Double(fluid));
    PhicApplication.markEvent("E");
  }

  static final double MAX_SPEED_FOR_LOGGING = 0.27;
  protected boolean logRoutineEventsIfFast = false;
  public void setLogRoutineEventsIfFast(boolean b){ logRoutineEventsIfFast=b; }
  public boolean getLogRoutineEventsIfFast(){return logRoutineEventsIfFast;}
  /** stop logging routine events if quicker than or equal to 1 hour per second. */
  protected final boolean isLogging(){ return logRoutineEventsIfFast ||
      clock.getSecond()>MAX_SPEED_FOR_LOGGING ;}

  /**
   * The specified volume of 'Water' is retrieved from Fluids.txt, and is
   * emptied into gitract.stomach. The thirst is reduced by this volume.
   * The volume is displayed, logged, and the event is drawn on any graphs.
   */
  public void drink(double volume) {
    gitract.stomach.add(Fluids.get("Water", volume));
    brain.thirst.add( - volume );
    if(isLogging())message("Drank "
            + UnitConstants.formatValue(volume, UnitConstants.LITRES,false));
    eventLog.document(EventLog.FLUID_EVENT, "Drank ",
                      new Double(volume));
    PhicApplication.markEvent("D");
  }

  /**
   * Called when the patient is about to die. The clock is stopped, and
   * the message is printed.
   */
  public void die() {
    if(!brain.getUnconscious().disableDeath){
      clock.stop();
      message("Dead.");
    }
  }

  /**
   * Called when the patient is about to die. The clock is stopped and the
   * message is printed.
   */
  public void die(String reason) {
    clock.stop();
    message("Dead: " + reason);
  }

  /**
   * Sets whether the body's clock is running.
   * Delegates to @link Clock#start() clock.start() and clock.stop()
   */
  public void setRunning(boolean r) {
    if (r) {
      if(!Current.thread.isAlive())Current.thread.start();
      clock.start();
    }
    else {
      clock.stop();
    }
  }

  /**
   * Return the body clock
   */
  public Clock getClock() {
    return clock;
  }



  /**
   * Called by 'doPartialReset' in SimplePhicFrame. This is called when the
   * quick reset is performed; controller values are not reset, just the Variables
   * from the Variables.txt file, the event log, and the individual organ variables.
   *
   * The time is also reset to the current system clock time, and the number of
   * completed cycles is zeroed, used for debugging purposes.
   */
  public void resetBodyValues(){
    resetOrgans(); //call reset on each organ
    resetContainers(); //call empty on each container
    Variables.initialiseBodyValues();
    clock.resetTime();
    Current.thread.completedCycles=0;
    eventLog = new EventLog(this);
    controllers.replaceAllVariables(); //reload controllers from file
    if(startupScript!=null)try {
      startupScript.executeOnce();
    }catch (StackException ex) { ex.printStackTrace();   }
    catch (MathException ex) {  ex.printStackTrace();    }
  }

  /**
   * The method that resets elements of the body to their state before
   * initialisation. Containers are emptied, the clock is reset, and
   * each organ is reset by calling the reset() method.
   * The event log is then cleared. Variables are not initialised to their
   * initial values; this is the job of the interface, which has knowledge
   * of which file contains the values to use.
   *
   * This method is called by the interface, and must always be
   * followed by Variables.initialise() to restore initial values.
   */
  public void resetAllValues() {
    resetOrgans(); //call reset on each organ
    resetContainers(); //call empty on each container
    environment.resetAll();
    Variables.initialise();
    clock.resetTime();
    Current.thread.completedCycles=0;
    eventLog = new EventLog(this);
    setupControllers(); //reload controllers from file
    if(startupScript!=null) try {
      startupScript.executeOnce();
    }catch (StackException ex) { ex.printStackTrace();  }
    catch (MathException ex) {  ex.printStackTrace();  }
  }

  /**
   * The event log associated with this Body. Records events such as
   * fluid intake and excretion.
   */
  public EventLog eventLog = new EventLog(this);

  /**
   * The current environment in which this body is. It is currently used
   * only to dispose of rubbish.
   */
  private Environment environment;

  /**
   * A script that is run every time reset is pressed.
   */
  public Script startupScript = null;

  /**
   * The environment is set when the body and environment are created -
   * this takes place in Current.
   */
  public void setEnvironment(Environment e) {
    environment = e;
  }

  /**
   * setupControllers - called after initialising the body and environment,
   * to initialise the controllers
   */
  public void setupControllers() {
    controllers = new ControllerList(controllersFile);
  }
}

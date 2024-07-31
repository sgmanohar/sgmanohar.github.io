package phic.common;
import java.util.*;

import phic.*;

/**
 *  Organ is the base class of all organs in the body.
 * Each organ has a thread which can be turned on and off as
 * required.  The organ should override the tick() method is order
 * to implement the organ's functions. The thread should use Body.clock.wait()
 * to time its actions -- this must be called once and once only each time the
 * {@link #tick() tick()} method is invoked.
 * Use body and environment variables to access objects outside the current organ.
 */
public abstract class Organ implements Runnable,HasContent{


  /** This static list contains all the organs that are created.
   *  The constructor of Organ adds the new object to this list. */
  private static Vector list=new Vector();
  /**
   * This sets the list of organs to a new set. Called when a body is
   * deserialised, by ??.
   */
  public static void setList(Vector v){
    list=v;
    Clock clock=Current.body.getClock();
    clock.clearThreads();
    //for(int i=0;i<v.size;i++){clock.latch(v.get(i).thread);}
    //relatch common Organ thread?
  }

  /** Return the list of organs that are currently running. */
  public static Vector getList(){
    return list;
  }

  /**
   * A dummy object that records when the model's data has been or is being
   * modified. Data targets can ask for a unique ID, with which they can store
   * whether the data is dirty from their point of view.
   *
   * This object is dirtied by the CommonThread when modifying values,
   * and cleaned by each data target, once they have processed changes in the
   * data.
   */
  public static class DataLock {
    /** Internal store of whether data is dirty, for each data target */
    protected int dirt;
    /** the current serial for creating new IDs */
    protected static int sn=0;
    /** Called once by a new data target, to give a new unique ID for a data target that processes data */
    public int createID(){
      assert sn<32:"Too many targets have registered with this data broker.";
      return sn++;
    }
    /** called by the common thread when it modifies some values */
    public synchronized void dirty(boolean b) {
      dirt= b?-1:0;
    }
    /** Called by a data target to read the dirty state for its own processing */
    public synchronized boolean isDirty(int s){ return (dirt & (1<<sn)) > 0; }
    /** Called by a data target when it has processed the data. */
    public synchronized void clean(int s){ dirt &= ~(1<<sn); }
  }

  /**
   * The lock object, which ensures no data is manipulated by organs
   * while it is being read by the GUI.
   */
  public static DataLock cycleLock = new DataLock();

  /**
   * The critical period object that manages locking of data when each
   * organ runs in its own thread. The critical period should be entered
   * any time an organ needs to lock more than one piece of data at once.
   * @todo This is not needed now, with a single organ thread.
   */
  public static CriticalPeriod criticalPeriod= CriticalPeriod.newCriticalPeriod();
  /**
   * Called by the body after all the organs are initialised, and the
   * Current variables are initialised. This static method goes through all
   * the registered organs, and sets their body variable.
   */
  public static void setBody(Body b){
    for(int i=0;i<list.size();i++){
      ((Organ)list.get(i)).body=b;
    }
  }

  /** Create a basic organ, adding it to the list of existing organs.
   * The organ's thread is started. */
  public Organ(){
    name=getClass().getName();
    name=name.substring(name.lastIndexOf('.')+1);
    body=Current.body;
    environment=Current.environment;
    list.add(this);
//		t.start();
  }

  /**
   * Name of the organ. This is set in the default constructor, from the class
   * name.
   */
  String name;
  /** Specifies whether this organ's thread is switched on. Changes to this
   * variable will affect the thread only after the current cycle has
   * finished and the loop polls this variable. */
  public boolean active=true;
  /** Specifies whether the organ should produce output during organ-specific
   * processing. These messages are output using the {@link #inform(String) inform()}
   * method. */
  public boolean verbose=false;
  /**
   * To give information on exactly what the organ is doing. Output is only produced
   * when verbose is enabled, and is prefixed by the source organ name.
   */
  public final void inform(String message){
    if(verbose){
      body.message('['+name+"] "+message);
    }
  }

  /** This is for use in the organ's thread, indicating the body it is
   *  contained within. By default this is set to Current.body by setBody method. */
  public Body body;
  /** This is for use in the organ's thread, and contains the environment
   *  in which the body to which the organ belongs is. By default, is is
   *  Current.environment */
  public Environment environment;
  /** The thread to dispatch events to the organ. */
//	transient Thread t=new Thread(this,getClass().getName()+".thread");
  boolean kill=false;
  /** The thread's run method polls the 'running' and protected 'kill'
   * variables while calling tickOrgan() repeatedly. If running is false,
   * the thread waits until it it set to true, or kill is set to true. */
  public void run(){
    while(!kill){
      if(active&&Current.body.clock.running){
        tickOrgan();
      } else{
        Thread.yield();
        try{
          Thread.sleep(100);
        } catch(Exception e){
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * This can be overridden if manual control of latching is required.
   * The default implementation is to call the current body clock's
   * latch() method, then to call the tick() method. */
  void tickOrgan(){
    Clock clock=Current.body.clock;
    clock.latch();
    elapsedTime=clock.elapsedTime()/1000.;
    tick(); //millis to seconds
  }

  /**
   * The implementor of the Organ must add their code here.
   * This method contains the essential moment-to-moment processing of the
   * organ. The method should use the elapsedTime variable to find out how
   * much time has passed since the last time tick() was called, (measured
   * in body clock seconds).
   *
   * OLD: @todo remove this bit?
   * Before returning
   * from the tick() method, the implementation should call
   * Current.body.clock.waitSeconds() or waitMinutes() to specify how long
   * is required before another tick() is performed.
   */
  public abstract void tick();

  /**
   * The implementor should override this to reset organ-specific
   * variables. This is called by the Body whenever it is reset by the
   * user. Organs are the first items to be reset; the state of Body's
   * containers and the clock may be undefined at this point.
   */
  public void reset(){
    body=Current.body;
    environment=Current.environment;
  }

  //Static utilities
  /** A static utility to low-pass filter a value.
   * @param target The final target value towards which the result will tend.
   * @param oldvalue The previous value of the low-passed variable.
   * @param rate The rate at which the new value of the variable will approach
   * the target value, expressed in the range 0-1, where 0 represents no
   * change, and 1 is instant change to the target value.
   * @example ap=lowpass(co*sv, ap, 0.4); */
  static final double lowpass(double target,double oldvalue,double rate){
    return target*rate+oldvalue*(1-rate);
  }

  /** A static utility that confines a value to limits. The returned value
   * is less than or equal to maximum, and greater than or equal to
   * minimum.
   * @example double symp = limit( cs, 0, 1 ); 	*/
  static final double limit(double value,double minimum,double maximum){
    return Math.min(Math.max(value,minimum),maximum);
  }


  /** The single instance of the common thread */
  public CommonThread getCommonThread=Current.thread;

  /** Old wait method called by each organ thread at the end of the cycle. */
  final protected void waitSeconds(double seconds){
  //Current.body.clock.IwaitSeconds(seconds);
  }

  /** Old wait method called by each organ thread at the end of the cycle. */
  final protected void waitMinutes(double minutes){
  //Current.body.clock.IwaitMinutes(minutes);
  }

  /**
   * Elapsed time in seconds since the last tick.
   * This is set just before the tick() method is called, and is also
   * the parameter to the tick() method.
   */
  protected double elapsedTime=60.0;
  /**
   * Return the fraction of a decay curve that occurs after the current
   * elapsed time, if the decay is a rate of 'fraction' per minute.
   *
   * e.g. if a value gets 10% closer to an asymptote each minute,
   *      use fractionDecayPerMinute(0.1) as a coefficient for the change.
   *      if 3 minutes have elapsed, for example, the function will return
   *      1*0.1 + 0.9*0.1 + 0.81*0.1 = 0.271,
   *      i.e. the value gets 27.1% closer to the asymptote.
   * Note that a small value is a slow change.
   * @return If the time elapsed is very long, the function approaches 1.0;
   * If the time elapsed is zero, the function returns 0
   * @param fraction the rate of decay of the curve, i.e. the value after
   * one minute.
   */
  protected final double fractionDecayPerMinute(double fraction){
    return 1-Math.pow(1-fraction,elapsedTime/60);
  }

  /**
   * Return the fraction of a decay curve that occurs after the given
   * elapsed time, if the decay is at a rate of 'fraction' per minute.
   * e.g., if the parameters (0.75, 30) are supplied, then this means
   * you want the value to alter 3/4 of the way to a desired value within an interval
   * of 1 minute, but only 30 seconds have elapsed. Therefore value returned is
   * 1 - (0.25 ^ 1/2) = 0.5
   */
  public static final double fractionDecayPerMinute(double fraction,
    double seconds){
    return 1-Math.pow(1-fraction,seconds/60);
  }

  /**
   * Returns a random true value if an event occurs with flat likelihood of
   * probabilityPerMinute over the interval elapsedSeconds.
   *
   * It is useful for events which have a low likelihood of occurring, or events
   * which when they do occur, prevent another event of the same type occurring
   * in the near future. For example, drinking when thirsty.
   *
   * The random number generator used is a static field in Organ, and its seed
   * can be initialised on reset if required.
   */
  public static final boolean randomEventOccurs(double probabilityPerMinute, double elapsedSeconds){
    return rand.nextDouble() < fractionDecayPerMinute(probabilityPerMinute, elapsedSeconds);
  }


  /**
   * Returns a random true value if an event occurs with flat likelihood of
   * probabilityPerMinute over the interval elapsedSeconds.
   *
   * It is useful for events which have a low likelihood of occurring, or events
   * which when they do occur, prevent another event of the same type occurring
   * in the near future. For example, drinking when thirsty.
   */

  public final boolean randomEventOccurs(double probabilityPerMinute){
    return randomEventOccurs(probabilityPerMinute, elapsedTime);
  }

  /**
   * Returns the fraction raised to the power of the number of minutes
   * that have elapsed. Use for a value that is multiplied by a constant
   * each minute.
   */
  protected final double fractionPerMinute(double fraction){
    return Math.pow(fraction,elapsedTime/60);
  }
  /**
   * Generate a pseudorandom double number using the current generator. This
   * generator can be reseeded at resets.
   */
  protected static final double random(){ return rand.nextDouble(); }


  /** Method to reset the random number generator */
  public static void resetRandom(){
    if(!useRandomSeed) randomSeed=System.currentTimeMillis();
    rand.setSeed(randomSeed);
  }
  /**
   *  If this is true, then use a fixed random seed every time the clock is reset
   */
  public static boolean useRandomSeed = false;

  /**
   * This is the value to which the random seed is reset every time the clock is reset.
   * if useRandomSeed is true, it is kept constant, otherwise it is itself randomised
   * at reset.
   */
  public static long randomSeed = System.currentTimeMillis();
  /**
   * The random number generator. Initially random seed
   */
  protected static Random rand = new Random(randomSeed);
}

package phic.common;

import phic.*;
import java.util.Vector;

/**
 * This version of Organ runs the organs in a single thread, in a
 * serial order. The thread will continue to execute as long as killOrgans
 * is false. However, processing only occurs when body.clock.running is true.
 *
 * Note about elapsed time: currently the elapsedTime is averaged at a rate of 0.1,
 * and will never exceed 60 seconds of body time per tick.
 *
 * The organs are called in a fixed sequence (determined by the order of
 * Organ.list). While the set of organs is being called, the field
 * Organ.cycleLocks is locked, so that interface elements that wish to access
 * the variables in the engine do not do so while processing is taking place.
 *
 * After the organs are called, the notifyObjects (set using addNotifyObject)
 * are notified if the required body time has elapsed. Then any Ticker objects
 * that wish to be called on a regular basis (e.g. Scripts) are called.
 * @todo LifeSupportMachine should be made a Ticker rather than an Organ.
 *
 * If the processing in an organ causes an exception, this error will be
 * displayed using Body.error(). The thread may then throw a RuntimeException
 * if the DEBUG_ERRORS field is true, and all processing will stop. Otherwise,
 * processing continues.
 */

//  SINGLE ORGAN THREAD - replaces organs' individual threads.
public class CommonThread
    extends Thread
    implements java.io.Serializable {
  /**
   * The maximum allowed seconds of body time that are permitted to elapse
   * in one single tick of the clock. This is a 'safety measure' - if the
   * system slows suddenly, then there won't be a sudden single tick of
   * a large amount of time.
   * Default value = 60 seconds.
   */
  public static transient int MAXIMUM_ELAPSED_SECONDS = 60 * 2;
  /**
   * This is the delay between one cycle and the next. This should be
   * set to a value that is optimum for the machine running the program.
   * The real-time clock and the body-clock are not affected by this value,
   * only the smoothness (temporal resolution) of the running is changed.
   *
   * A value of 10 ms is approximately right on a 1 MHz computer.
   */
  public  int CYCLE_LENGTH_MILLIS = 1;

  /** internal count of how many cycles since thread was started */
  public  int completedCycles = 0;

  /**
   * When true, this terminates the organ thread, ready for exiting the program
   */
   boolean killOrgans = false;
  /**
   * Keeps track of the average time between one cycle and the next,
   * measured in body clock seconds. The lower this is, the more accurate
   * the simulation.
   */
   double averageElapsedTime = 0;
  /**
   * The elapsedTime is corrected from the actual body clock.
   * The correction occurs because of averaging and because of cycles that take
   * longer than the MAXIMUM_ELAPSED_SECONDS (which are truncated).
   * This method gives the uncorrected time elapsed.
   *
   * Effectively it provides a measure of how well the computer is coping with
   * the current time compression setting. If it is very different from the
   * elapsedTime, it means that the computer cannot keep up with the current time compression.
   */
   double averageUncorrectedTime = 0;

  /**
   * The amount by which the time elapsed each cycle is low-passed.
   * Note that, in an OS such as Windows, the actual time between successive cycles
   * can vary between something like 0.1 microseconds and 10 seconds - a
   * factor of 10^8 !
   */
  public  transient double TIME_SMOOTHING = 0.9;
  /**
   * If this field is true, then re-throw all errors in the organ thread, so
   * they can be caught in the debugger in the correct stack frame.
   */
  public static final transient boolean DEBUG_ERRORS = false;
  public CommonThread() {
    super("Common organ thread");
  }

  double elapsedTime = 0;
  public boolean fixedFrameDuration=false;
  public double  fixedFrameDurationMillis = 10;
  int containertmp;
  public void run() {
    while (!killOrgans) {
      try {
        while (Organ.getList() == null) {
          Thread.sleep(100);
        }
        ; ///wait till initialised
        double bodySeconds;
        while (Current.body.clock.running) {
          inCycle=true;
          tickOnce();
          completedCycles++;
          if (DEBUG_ERRORS) {
            //debug container creation
//System.out.println(Container.serial-containertmp);
            containertmp = Container.serial;
            // debug elapsed timer
//System.out.println("cycle "+completedCycles+
//                ": elapsed "+bodySeconds+" s (av "+elapsedTime+" s)");
          }
          averageElapsedTime = elapsedTime;
          //Current.body.clock.IwaitMinutes(1);
          Thread.sleep(CYCLE_LENGTH_MILLIS); //delay independent of timescale
          //this delay ought to depend on the speed of the computer.
        }
        inCycle=false;
        Thread.yield();
        Thread.sleep(100);
      }
      catch (RuntimeException e) {
        e.printStackTrace();
        if (DEBUG_ERRORS) Current.body.error("Error: " + e.toString());
      }
      catch (InterruptedException x) {
        x.printStackTrace();
        Current.body.error("Error: " + x.toString());
      }
    }
  }
  /**
   * This is set to true while the thread is calculating the physiology.
   * Typically, when the clock is started there is a delay before this becomes true,
   * and if the clock is stopped, there is a delay
   * before this becomes false. This is the time it takes the common thread to
   * respond to the commends.
   */
  public boolean isInCycle(){ return inCycle; }
  boolean inCycle;
  /**
   * Calculate elapsed time,
   * Lock all other threads from accessing the data,
   * Call tick() on each organ in turn, in the order they are listed in Organ.list,
   * Call tick() on each Ticker in the clock's list of tickers,
   * Then mark the cycle lock as 'dirty'
   */
  public void tickOnce() {
    double bodySeconds;
    Current.body.clock.latch(); //calculate the elapsed time
    Clock clock = Current.body.getClock();
    synchronized (Organ.cycleLock) {
      bodySeconds = clock.elapsedTime() / 1000.;
      tickFor(bodySeconds);
    }
  }
  public void tickFor(double sec){
    synchronized (Organ.cycleLock) {
      Organ.cycleLock.dirty ( false );

      if(!fixedFrameDuration){
        elapsedTime = TIME_SMOOTHING * elapsedTime + (1 - TIME_SMOOTHING) *
            Math.min(sec, MAXIMUM_ELAPSED_SECONDS);
        if (elapsedTime == 0)return;
        if(Double.isNaN(elapsedTime)) throw new IllegalStateException("Elapsed time is not a number");
      } else { // assume 24 Hz
        elapsedTime = fixedFrameDurationMillis / Current.body.getClock().getSecond();
      }

      // average time elapsed in the actual body clock.
      averageUncorrectedTime = TIME_SMOOTHING * averageUncorrectedTime +
          (1 - TIME_SMOOTHING) * sec;
      Clock clock = Current.body.getClock();
      clock.checkNotifyObejcts(); //anyone waiting to be notified of the time?
      Vector list = Organ.getList();
      for (int i = 0; i < list.size(); i++) {
        Organ o = ( (Organ) list.get(i));

        o.elapsedTime = elapsedTime;
        if (o.active)
          o.tick();
      }

      //Other objects that request to be called every clock cycle in Clock.tickers.
      if (clock.tickers != null)for (int i = 0; i < clock.tickers.size();
                                     i++) {
        ( (Ticker) clock.tickers.get(i)).tick(elapsedTime);
      }
      Organ.cycleLock.dirty(true);
    }

  }
}

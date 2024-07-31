package phic.common;

import java.io.*;
import java.text.*;
import java.util.*;

import phic.*;

/**
 * The class responsible for coordinating the timing relations between organs.
 * Each organ has a thread, which calls wait methods in the current body.clock
 * object. The body's time can be faster or slower than reality.
 *
 * The clock is calculated by multiplying the elapsed physical time (in real
 * milliseconds) by a conversion factor, to obtain the elapsed logical time.
 * The clock starts at the present moment, but proceeds faster or slower than
 * real life as determined by the methods setSecond() and getSecond().
 *
 * A note on units: All units used inside the class are in milliseconds (long
     * value); units expressing the duration of a second - as in setSecond(double) -
 * are also in milliseconds.
 * However, elapsed times - as in elapsedTime() - return time in seconds. This
 * is for the use of organs to calculate rates of change.
 */
public class Clock
    implements Serializable {
  /**
   * Milliseconds of real time that correspond to one second of body time.
   * e.g. 166.67 indicates time occurs time is accelerated by a factor
   * of 6 for the body.
   */
  private double second;

  /**
   * Time units in milliseconds, calculated from second.
   * Theses are the number of milliseconds of physical (real) time that
   * correspond to the appropriate duration of body time.
   */
  private double minute, hour, day;

  public Clock() {
    setSecond(1000); //default: realtime
  }

  /**
   * The global switch for the body clock.
   * True when the clock is running, and is controlled by the start() and
   * stop() methods.
   */
  public boolean running = false;

  /**
   * The body's time, measured in the same units as the System clock
   * (i.e. elapsed milliseconds since 1 Jan 1900.)
   */
  public long time = System.currentTimeMillis();

  /**
   * Time at which current simulation started
   * (millis as from System clock).
   */
  public long simulationStartTime = time;

  /**
   * This is a set of objects of type Ticker, which are executed every time
   * the body clock ticks. The tick method is called with the elapsed time
   * by the Object.CommonThread after each cycle.
   *
   * Items are added to this by the Script class.
   * This field is transient; any items added to this list will not be
   */
  transient public Vector tickers = new Vector();

  /** Recreate the transient fields on load */
  private void readObject(ObjectInputStream s)throws ClassNotFoundException,IOException{
    s.defaultReadObject();
    tickers=new Vector();
    notifyList=new Vector();
  }

  /**
   * Set the clock to the current system time.
   * This is called during a full reset.
   */
  public void resetTime() {
    time = lasttime = System.currentTimeMillis();
    simulationStartTime = time;
  }

  /**
   * Starts the clock. The threads will only wake when the Organ's wait loop
   * polls this clock object.
   *
   * Lets each thread know, also, the time at which the clock was restarted,
   * so that the next period of elapsed time begins from the time of
   * this call.
   */
  public void start() {
    lasttime = System.currentTimeMillis();
    for (int i = 0; i < threads.size(); i++) {
      ( (WaitingThread) threads.get(i)).latchtime = lasttime;
    }
    running = true;
  }

  /**
   * Stops the clock. The organ's threads will only stop when the Organ's
       * processing finishes one cycle, and the Organ's loop polls this clock object.
   */
  public void stop() {
    String z = getTimeString(TIME); //reset variable 'time' from 'lasttime'
    running = false;
  }

  /**
   * Return the current body time in milliseconds. Measured since 1 Jan
   * 1970 or something. @see java.lang.System#currentTimeMillis()
   */
  public long getTime() {
    long ctm = System.currentTimeMillis();
    long diff = ctm - lasttime;
    lasttime = ctm;
    if (!running) {
      diff = 0;
    }
    time += 1000 * diff / second;
    return time;
  }

  /**
   * Returns true if the day has changed within the last tick.
   * Used to perform tasks such as daily logging of fluid losses,
   * by Skin and Lung
   */
  public boolean dayChanged() {
    WaitingThread t = getThread();
    if(t==null) t=getThread(Current.thread);
    long lt = t.latchtime, pt = lt - t.elapsed; //these are in physical milliseconds.
    //'day' is number of physical millis corresponding to one body-clock day.
    int d1 = (int) (lt / day), d2 = (int) (pt / day);
    return d1 != d2;
  }

  /**
   * Set the length of a logical second, in physical milliseconds.
   */
  public synchronized void setSecond(double s) {
    second = s;
    minute = 60 * second;
    hour = 60 * minute;
    day = 24 * hour;
    isSlowMode = second>=200;
    notifyAll();
  }

  /**
   * Get the length of one second of body time, in milliseconds (of real time).
   */
  public double getSecond() {
    return second;
  }

  /**
   * Called by the Organ's thread, when it is to begin the waiting period.
   *
   * This is usually before any organ-specific
   * processing is to be performed, in order that the cycle time is
   * independent of processing duration. The method registers the object
   * along with the time the call was made.
   *
   */
  //must obtain lock on Vector 'threads'
  public synchronized void latch() {
    WaitingThread t = getThread();
    if (t == null) {
      t = new WaitingThread();
      t.thread = Thread.currentThread();
      allThreads.add(t);
      t.latchtime = System.currentTimeMillis()-1000; //initial elapsed time is 1 second
      threads.add(t); // set up data for time of latch
    }
    long scm = System.currentTimeMillis();
    t.elapsed = scm -  t.latchtime; //calculate time elapsed since last tick
    t.latchtime = scm;
  }

  /**
   * Return the time that has elapsed since this thread was last latched.
   *
   * The time is measured in milliseconds relative to the body clock;
   * i.e. if 1 'second' in body time      = 166.67 ms  (of real time)
   *     and actual physical elapsed time = 5 s
       *    then return 5000 * 1000 / 166.67  = 30,000 ms  (30 seconds of body time)
   * since 30,000 milliseconds have effectively passed in the space of 5,000.
   */
  public double elapsedTime() {
    WaitingThread w = getThread();
    if(w==null)w=getThread(Current.thread);
    return (long) (w.elapsed * 1000 / second);
  }

  /**
   * This prevents time elapsing on the next cycle.
   * Useful when you want to change the time of the clock, without the body
   * knowing.
   */
  public void preventTimeLapse(){
    lasttime = System.currentTimeMillis() - 100;
  }

  //internal methods
  /**
   * ?? Last time of what?
   */
  protected long lasttime = time;

  /**
   * Waiting list: the list of threads that have latched and are currently
   * doing processing. They are removed from this list once they have finished
   * processing.
   */
  public transient Vector threads = new Vector();

  /**
   * The list of all WaitingThread objects.
       * All existing organ threads are wrapped in WaitingThread objects, and stored.
   * This allows us to identify which organ a thread belongs to.
   */
  private transient Vector allThreads = new Vector();

  /**
   * Clears the threads and allThreads vectors.
   * Used on deserialisation, since these are transient fields.
   */
  public void clearThreads() {
    allThreads = new Vector();
    threads = new Vector();
  }

  /**
   * Return the current thread's WaitingThread object.
   * Note: Should only be called from a thread which has been latched.
   */
  protected final WaitingThread getThread() {
    synchronized (allThreads) { //lock the vector
      for (int i = 0; i < allThreads.size(); i++) { //search for current thread
        if ( ( (WaitingThread) allThreads.get(i)).thread.equals(Thread.
            currentThread())) {
          return (WaitingThread) allThreads.get(i);
        }
      }
    }
    //disabled for new threadless design
    // Current.body.error("Thread not latched");
    return null;
  }
  /**
   * Return the WaitingThread corresponding to the specified thread
   */
  protected final WaitingThread getThread(Thread thread) {
    synchronized (allThreads) { //lock the vector
      for (int i = 0; i < allThreads.size(); i++) { //search for current thread
        if ( ( (WaitingThread) allThreads.get(i)).thread.equals(thread)) {
          return (WaitingThread) allThreads.get(i);
        }
      }
    }
    //disabled for new threadless design
    // Current.body.error("Thread not latched");
    return null;
  }

  /**
   * Internal waiting call:
   *
   * This call does not return until the period specified in seconds has
   * elapsed since the organ last called the latch() method. The thread
   * then unregistered from the latch list. Note that
   * the latch method MUST have been called by the current thread prior
   * to this method being called. If this is not the case, then a
   * 'Thread not latched' error will be generated.
   * In the case that a thread has called latch() more than once since the
   * last call, then the first call to latch() is unregistered, and it
   * is the length of time from that point that is waited.
   */
  protected void IwaitSeconds(double s) {
    WaitingThread w = getThread();
    if (w == null) {
      Current.body.error("Thread not latched");
    }
    threads.remove(w); //remove from waiting list
    long millis = (long) (s * second) -
        (System.currentTimeMillis() - w.latchtime);
    if (millis > 0) {
      try { //do the wait
        synchronized (this) {
          this.wait(millis);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
    return;
  }

  /**
   *
   * @param seconds the time to advance the clock by, in body-clock seconds.
   */
  public void elapseTime(double seconds){
    /*WaitingThread t = getThread();
    if(t==null) t=getThread(Current.thread);
    t.elapsed = (long)(1000*seconds);
    t.latchtime = t.latchtime-t.elapsed;
    this.time += 1000*seconds;
    */
    running=true;
    lasttime = System.currentTimeMillis() - (long)(seconds*second);
    getTime();
    running=false;
  }

  /**
   * Internal wait in minutes. @see IwaitSeconds(int)
   */
  protected void IwaitMinutes(double m) {
    IwaitSeconds(m * 60);
  }

  /**
   * The class that wraps all Organ threads.
   */
  protected class WaitingThread
      implements java.io.Serializable {
    Thread thread;

    long latchtime;

    long elapsed;
  }

  /**
   * Requests for notification:
   * This allows external program to be notified after a certain amount
   * of phic-time has elapsed.
   * The object's notify() method is called as soon as possible
   * after the given time has passed.
   */
  public void requestNotifyAfter(double seconds, Object waitingObject) {
    notifyList.add(new NotifyObject(time + (long) (seconds * 1000),
                                    waitingObject));
  }

  /**
   * This is a list of NotifyObjects. These objects are notified after a
   * specified amount of time has passed. The list is transient; notifies
   * will not be carried over across saves, since it involves waiting threads!
   */
  transient Vector notifyList = new Vector();

  /** Checks for elapsed objects and notifies them */
  public final void checkNotifyObejcts() {
    for (int i = 0; i < notifyList.size(); i++) {
      NotifyObject o = ( (NotifyObject) notifyList.get(i));
      if (o.time < time) {
        o.elapsed();
      }
    }
  }

  final class NotifyObject {
    Object object;

    long time;

    NotifyObject(long t, Object o) {
      time = t;
      object = o;
    }

    final void elapsed() {
      synchronized (object) {
        object.notify();
        notifyList.remove(this);
      }
    }
  }

  //Date and time formatting
  /**
   * Format specifiers for date/time
   */
  public static final int DATETIME = 0, DATE = 1, TIME = 2, SHORTDATE = 3,
      DAYONLY=4, SHORTDATETIME=5;

  /**
   * Get a string representing the current body time in the specified format.
   * @param type The format of date required, either DATETIME, DATE or
   * TIME.
   */
  public String getTimeString(int type) {
    long ctime = getTime();
    return getDateFormatForDateType(type).format(new Date(ctime));
  }
  public DateFormat getDateFormatForDateType(int type){
    DateFormat d = null;
    switch (type) {
      case DATETIME:
        d = datetimeformat;
        break;
      case DATE:
        d = dateformat;
        break;
      case TIME:
        d = timeformat;
        break;
      case SHORTDATE:
        d= shortdateformat;
        break;
      case DAYONLY:
        d=dayonlyformat;
        break;
      case SHORTDATETIME:
        d=shortdatetimeformat;
        break;
    }
    return d;
  }

  /**
   * Get a string representing the given time in the specified format.
   * @param type the format of date required, one of DATETIME, DATE, or TIME.
   * @param time The time, in milliseconds since 1 Jan 1980, to display in
   * the format requested.
   */
  public String getTimeAsString(long time, int type) {
    return getDateFormatForDateType(type).format(new Date(time));
  }

  /**
   * Date and time format objects that correspond to the constants DATETIME,
   * DATE, and TIME respectively.
   */
  protected static DateFormat datetimeformat = DateFormat.getDateTimeInstance(
      DateFormat.LONG, DateFormat.MEDIUM),
      dateformat =DateFormat.getDateInstance(DateFormat.MEDIUM),
      timeformat = DateFormat.getTimeInstance(DateFormat.MEDIUM),
      shortdateformat = new SimpleDateFormat("d MMM"),
      dayonlyformat = new SimpleDateFormat("dd"),
      shortdatetimeformat = new SimpleDateFormat("dd/MM, hha");
  /**
   * Average elapsed time per cycle
   * @return the time elapsed between one frame and the next, in
   * body seconds.
   */
  public boolean isSlowMode=false;
  public static double getAverageElapsedTime() {
    return Current.thread.averageElapsedTime;
  }

  /**
   *
   */
  public static double getExactElapsedTime(){
    return Current.thread.averageUncorrectedTime;
  }

  /**
   * addTicker adds a ticker to the list of objects that are called every tick
   * that the body changes
   */
  public void addTicker(Ticker ticker) {    tickers.add(ticker);  }

  /**
   * removes a ticker from the list of objects that are called every tick.
   */
  public void removeTicker(Ticker ticker) {    tickers.remove(ticker);  }

  public int getHour() {
    return (int)(getTime() / (1000 * 60 * 60)) % 24;
  }

  public String getHourString() {
    int h=getHour(); return (((h+11)%12)+1) + (h>=12?"pm":"am");
  }

}

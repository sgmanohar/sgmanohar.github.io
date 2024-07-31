package phic.common;

/**
 * Implement a global data lock for critical multi-object manipulations
 * should be called each time multiple locks must be obtained.
 *
 * Any code executed by a thread, in between enter() and leave() calls, is
 * guaranteed to be exclusive to any other thread for this particular
 * CriticalPeriod.
 */
public abstract class CriticalPeriod {
  protected int semaphore;

  Thread owner;
  /**
   * Enters a critical period. Once this call has been made,
   * any other thread calling this method will be blocked until this thread
   * calls the exit() method.
   *
   * Once this method has returned, the thread is guaranteed that no other
   * thread has concurrently entered a critical period without leaving
   * it.
   *
   * @note A call to this method may be blocked until another thread,
   * which has called the enter() method, calls the exit() method.
   *
   * @note AS OF 23.6.05 ALL METHODS REPLACED BY DUMMY METHODS FOR SPEED PURPOSES
   */
  public abstract void enter();

  /**
   * Leave a critical period. Once this call is made, another thread may
   * be allowed to continue from the enter() call, if it is waiting.
   */
  public abstract void exit();

  public static CriticalPeriod newCriticalPeriod() {
    return new DummyCriticalPeriod();
    //return new CriticalPeriodImpl();
  }
}

class CriticalPeriodImpl
    extends CriticalPeriod {
  synchronized public final void enter() {
    while (Thread.currentThread() != owner && semaphore > 0) {
      try {
        wait(); //if locked, wait until another thread releases lock
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    semaphore++; //prevent another thread from locking until we complete
    owner = Thread.currentThread();
  }

  synchronized public final void exit() {
    notify();
    semaphore--;
    if (semaphore == 0) {
      owner = null;
    }
  }
}

class DummyCriticalPeriod
    extends CriticalPeriod {
  public void enter() {}

  public void exit() {}
}

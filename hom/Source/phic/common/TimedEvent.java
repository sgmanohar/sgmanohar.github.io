package phic.common;

import phic.*;
import evaluator.*;

/**
 * This represents an event that occurs at a specific time.
 * It executes a command in the script language.
 */

public class TimedEvent implements Runnable {
  double afterSeconds;
  String cmd;
  Statement st;
  Thread thread = new Thread(this);
  boolean done;
  Object result;
  /** Can throw parse exception  */
  public TimedEvent(double afterSeconds, String command)
      throws ParseException {
    this.afterSeconds=afterSeconds;
    cmd=command;
    st=new Statement(cmd);
    if(st!=null){
      synchronized (this) {
        thread.start();
        Current.body.getClock().requestNotifyAfter(afterSeconds, this);
      }
    }
  }

  public synchronized void run() {
    try{
      wait();
      result=st.evaluate();
      done = true;
    }catch(Exception x){throw new RuntimeException(
       "Error executing timed command '"+cmd+"' at t="+afterSeconds, x);
    }
  }

}

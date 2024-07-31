package phic.modifiable;


import evaluator.ParseException;
import evaluator.Statement;
import evaluator.StackException;
import evaluator.MathException;

/**
 * Allow recording of events
 */

public  class RecordedEvent {
  protected Statement stm;
  protected String name;
  protected long time;
  public void playbackEvent() throws StackException, MathException {
    stm.evaluate();
  }
  public void setName(String s){ name = s; }
  /**
   * format as
   *     0h 0m 10s: eat()
   */
  public String toString(){
    return time/3600+"h "+(time/60)%60+"m "+time%60+"s: "+getStatement();
  }
  /**
   * read formatted string as produced by toString();
   * and store values in this RecordedEvent
   */
  public void fromString(String s){
    String t=s.substring(0,s.indexOf(':'));
    int i=0;long tt=0;
    tt =Integer.parseInt(t.substring(i,i=t.indexOf('h')).trim())*3600;
    tt+=Integer.parseInt(t.substring(i,i=t.indexOf('m')).trim())*60;
    tt+=Integer.parseInt(t.substring(i,i=t.indexOf('s')).trim());
    setTime(tt);
    setStatement(s.substring(s.indexOf(':')+1).trim());
  }
  /**
   * Time of event after model started to run, in milliseconds
   */
  public long getTime(){return time;}
  public void setTime(long t){time=t;}
  /**
   * the code of the script that is run at this event time
   */
  public String getStatement(){return stm.getDefinition();}
  public void setStatement(String s) throws ParseException { stm = new Statement(s);  }

}

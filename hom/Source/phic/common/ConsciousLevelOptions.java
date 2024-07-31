package phic.common;

import java.io.*;
import java.util.*;

import phic.*;
import phic.gui.*;
/**
 * Decides the course of actions should the patient become unconscious.
 * Note: implements Serializable, and is part of the Body.
 * Also decides on patient's mobility and controls patients voluntary
 * exercise, sleep and wake cycle and posture during the day.
 */

public class ConsciousLevelOptions implements Serializable, Ticker, HasContent{
  Person p;
  public ConsciousLevelOptions(Person p) {
    this.p = p;
  }
  public boolean flat=true, eat=false, drink=false,
    normalBreathing=false, revertPreviousPosture=false,  notify=false,
    stopExercise=true;
  double lastPosture = Double.NaN;
  public double notifyHours = 8;
  public boolean disableDeath = false;
  public boolean mobile = false, allowSleep = false;


  protected long timeBecameUnconscious = -1;
  protected boolean hasBeenWarned = false;

  /** perform tasks that need to be done when patient becomes unconscious */
  public void becomeUnconscious(){
    if(flat){
      lastPosture = p.environment.Uprt.get();
      if(p.body.brain.isAsleep())
        invokeGradualChange(p.environment.Uprt,0.50, 0.5);
      else p.environment.Uprt.set(0.50);
    }
    if(normalBreathing){
      p.environment.BrHld=false;
      p.environment.Hyperv.set(0);
    }
    if(stopExercise){
      stopExercising();
    }
    /** request notify after 8 hours */
    timeBecameUnconscious = Current.body.getClock().getTime();
    hasBeenWarned = false;
    //Waiter waiter = new Waiter();
  }

  /**
   * This decides whether it is time for the brain to wake up or fall asleep
   * based on the times 'bedtime' and 'waketime', using the body clock.
   * This is only effective if allowSleep is true. Also decides on when to start
   * and stop exercising.
   */
  public void tick(double elapsedTimeSeconds){
    Brain br = p.body.brain;
    long time = p.body.getClock().getTime() % day;
    if(allowSleep && Organ.random() < elapsedTimeSeconds/sleepWakeRandom){
      if((time > bedtime || time < waketime) && !br.isAsleep()){
        br.fallAsleep();
      }else if((time > waketime && time < bedtime) && br.isAsleep()){
        br.wakeUp();
      }
    }
    double exer = p.environment.Exer.get();
    if (!br.isAsleep() && mobile) {
      if (exer > 0) {
        tiredness += exer * elapsedTimeSeconds;
        if (tiredness > tirednessThreshold && Organ.randomEventOccurs(0.4, elapsedTimeSeconds)) stopExercising();
      }
      else {
        if (tiredness <= 0 && Organ.randomEventOccurs(0.2, elapsedTimeSeconds)) startExercising();
      }
    }
    if (tiredness > 0 && exer <= 0) {
      tiredness -= elapsedTimeSeconds * recoveryRate;
    }
    for(int i=0;i<currentVoluntaryActions.size();i++){
      Object o=currentVoluntaryActions.get(i);
      ((GradualVoluntaryAction)o).tick(elapsedTimeSeconds);
    }
    //check if unconscious for too long
    if(timeBecameUnconscious>0
       &&  Current.body.brain.getFeeling()==Brain.UNCONSCIOUS && !hasBeenWarned
       && Current.body.getClock().getTime() > timeBecameUnconscious+notifyHours*60*60*1000){
      phic.gui.PhicApplication.frame.unconsciousForAges();
      hasBeenWarned=true;
    }
  }


  /**
   *  Starts exercising at a random rate - currently multiples of 40 up to 200.
   *  Stand up.
   */
  public void startExercising(){
    if(startedExercising==1) return;
    invokeGradualChange(p.environment.Uprt, 1, 0.2);//p.environment.Uprt.set(1);
    double exer = (1+(int)(Math.random()*5))*40; //p.environment.Exer.set((1+(int)(Math.random()*5))*40);
    invokeGradualChange(p.environment.Exer, exer, 0.15);
    startedExercising=1;
  }

  /** Stops exercising because of tiredness. Sit down. */
  public void stopExercising(){
    if(startedExercising==0)return;
    invokeGradualChange(p.environment.Exer, 0, 0.2);//p.environment.Exer.set(0);
    //p.environment.Uprt.set(0.75);
    startedExercising=0;
  }

  /** The time at which patient falls asleep, in milliseconds after midnight */
  public long bedtime = 22 * 60 * 60 * 1000;
  /** The time at which patient wakes up, in milliseconds after midnight */
  public long waketime = 6 * 60 * 60 * 1000;
  /** The length of a day in milliseconds */
  final long day = 24 * 60 * 60 * 1000;
  /** The current level of tiredness, which increases with exercise */
  public double tiredness = 0;
  /** The level of tiredness at which patient stops exercising */
  public double tirednessThreshold = 100000;
  /** Rate of recovery from exercise - joules per second */
  public double recoveryRate = 40;
  /** Randomness in sleep / wake time, in seconds */
  public double sleepWakeRandom = 1800;
  /** This is 1 when patient has decided of their own will to start exercising,
   * and is 0 when they have decided to stop. It is -1 when the volitional
   * state of the exercise is unknown.  */
  private int startedExercising = -1;
  /** This is true when patient has decided of their own will to start exercising */
  public boolean isExercising(){ return startedExercising==1; }

  /** Perform tasks that need to be done when patient recovers consciousness */
  public void recoverConsciousness(){
    if(revertPreviousPosture && !Double.isNaN(lastPosture))
      invokeGradualChange(p.environment.Uprt, lastPosture,0.15);//p.environment.Uprt.set(lastPosture);

    lastPosture=Double.NaN;
    timeBecameUnconscious = -1;
    cancelWait++;
  }

  int cancelWait=0;
  /** Waits for the specified number of hours, and calls PhicFrame.unconsciousForAges */
  class Waiter implements Runnable{
    public Waiter(){
      Thread thread = new Thread(this);
      thread.start();
      p.body.getClock().requestNotifyAfter(notifyHours*60*60,this);
    }
    public synchronized void run(){
      try{
        wait();
        if(cancelWait==0 && notify) phic.gui.PhicApplication.frame.unconsciousForAges();
        else cancelWait--;
      } catch(InterruptedException ex){}
    }
  }

  private Vector currentVoluntaryActions = new Vector();

  /**
   * Begin a one-off change in a given variable that occurs at a set relaxation
   * rate. For example, if you want the value of Exer to change to zero over 5
   * minutes, use invokeGradualVoluntaryAction(environment.Exer, 0, 1);
   * @param valueToChange VDouble the variable whose value will be gradually changed
   * @param targetValue double the value that this variable will aim towards.
   * @param fractionPerMinute double the rate at which to change.
   */
  public void invokeGradualChange(VDouble valueToChange, double targetValue, double fractionPerMinute){
    if(valueToChange.get()==targetValue)return;
    new GradualVoluntaryAction(valueToChange, targetValue, fractionPerMinute);
  }

  /**
   * An action that, once created, continues to execute in the tick() method
   * until the value of a variable reaches a target.
   * Used, for example, to cause the patient
   * to commence exercising, but without a sudden leap in the value of Exer.
   */

  class GradualVoluntaryAction implements Ticker , Serializable{
    VDouble var; double target; double rate; double initialDifference;
    static final double accuracyThreshold = 0.01; // continue modification until we are
    double execTime = 0;
    public GradualVoluntaryAction(VDouble variableToChange, double targetValue, double fractionPerMinute){
      //remove any previous actions on the same variable
      for(int i=0;i<currentVoluntaryActions.size();i++){
        GradualVoluntaryAction o = (GradualVoluntaryAction)currentVoluntaryActions.get(i);
        if (o.var==variableToChange){
          currentVoluntaryActions.remove(o);
          System.out.print("concurrent gradual modification of ");
          System.out.print(Variables.forVDouble(variableToChange));
          System.out.println(" to "+target+"; discarding old value of "+o.target);
        }
      }
      var=variableToChange; target=targetValue; this.rate=fractionPerMinute;
      initialDifference = Math.abs(var.get() - targetValue);
      currentVoluntaryActions.add(this);
    }
    /**
     * Gradually modify the variable towards the target, until we predict
     * the value will be within the accuracy threshold.
     * When finished, it matches value exactly if it's pretty close already
     * if it isn't, that means the variable has changed for some OTHER reason
     * so don't make it suddenly jump.
     * Then stop the regular processing.
     */

    public void tick(double elapsedSeconds){
      double val=var.get();
      execTime+=elapsedSeconds;
      var.lowPass(target, Organ.fractionDecayPerMinute(rate, elapsedSeconds));
      if( (1-Organ.fractionDecayPerMinute(rate, execTime) < accuracyThreshold) ){
        currentVoluntaryActions.remove(this);
        if(Math.abs(val-target) < 1.2 * accuracyThreshold * initialDifference)
          var.set(target);
      }
    }
  }
  public void reset(){
    currentVoluntaryActions.removeAllElements();
    startedExercising=-1;
  }
}

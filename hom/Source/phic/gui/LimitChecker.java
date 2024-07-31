package phic.gui;
import java.util.Vector;
import javax.swing.JLabel;
import phic.Current;
import phic.common.*;

/**
 * Limit checker checks each visible variable to see if it is within
 * the normal range. If not, it displays a warning in the label.
 * The text changes dynamically as variables change in value.
 * @todo rewrite using timer not thread
 */
public class LimitChecker extends JLabel implements Runnable{
  public LimitChecker(){}

  /** This will start the thread that checks for variables' values. */
  public void start(){
    thread.start();
  }

  /** The thread that checks values */
  Thread thread=new Thread(this,"LimitChecker");
  /**
   * This delay determines the frequency with which the thread will
   * check the values of the variables.
   */int delay=500;
  /**
   *  The list of variables which have significant errors
   */
  Vector errorVars=new Vector();
  /** The array of whether a variable has an error */boolean error[]=new boolean[
    Variables.variable.length];
  /** The flag signifying, when there is an error, whether it is high or low */
  boolean high[]=new boolean[Variables.variable.length];
  boolean running=true;
  /**
   * When this is true, only show errors in nodes which are currently visible
     * to the user, as defined by the VisibleNode class. The set of nodes that are
   * visible is determined by the VisibleNodes.txt file and the current
   * FrameSetup.
   * This option can be set from the InterfaceOptions dialog.
   */
  public static boolean showOnlyVisibleNodes=true;
  /**
   * If this is true, then the limit checker bar will reduce to zero size when
   * there are no more errors.
   * This shrinking may cause resizing of other GUI elements which is undesirable if
   * it is near a ScrollGraph. In this situation, it should be false.
   */
  public static boolean vanishWithoutErrors=false;
  /**
   * If this is true, then the error label will display the shortened forms of
   * the variables' names. These names are sometimes uninformative, and if
   * few erroneous variables are expected (e.g. when using a small set of
   * VisibleNodes), then set this to false.
   */
  public static boolean useShortNames=true;
  /**
   * The thread that updates the label with the list of variables that have
   * errors.
   */
  public void run(){
    while(running){
      while(Current.body.getClock().running){
        try{
          Thread.sleep(delay);
        } catch(InterruptedException e){
          e.printStackTrace();
        }
        VisibleVariable[] v=Variables.variable;
        for(int i=0;i<v.length;i++){
          if(!((LimitedNode)v[i].node).isVisible()&&showOnlyVisibleNodes)
            continue;
          double val;
          synchronized(Organ.cycleLock){
            val=v[i].node.doubleGetVal();
          }
          if(Double.isNaN(val)){ // Die if not a number
            Current.body.die(v[i].longName+" is not a number after "+
              Current.thread.completedCycles);
            break;
          }
          if(v[i].minimum>val||v[i].maximum<val){
            if(!error[i]||(high[i]^(v[i].maximum<val))){
              //either not registered or registered wrongly
              if(!error[i]){ //not registered
                errorVars.add(v[i]);
                error[i]=true;
              }
              if(v[i].minimum>val){ //registered wrongly
                high[i]=false;
              } else if(v[i].maximum<val){
                high[i]=true;
              }
              change();
            }
          } else if(error[i]){ //unregister
            error[i]=false;
            errorVars.remove(v[i]);
            change();
          }
        }
      }
      try{
        Thread.sleep(delay);
      } catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }

  void change(){
    String t="";
    boolean begin=true;
    int j=0;
    for(int i=0;i<error.length;i++){
      if(error[i]){
        if(begin){
          begin=false;
        } else{
          t+=", ";
        }
        VisibleVariable v=(VisibleVariable)Variables.variable[i];
        t+=(high[i]?"High ":"Low ")+(useShortNames?v.shortName:v.longName);
      }
    }
    if(!vanishWithoutErrors)
      if(t.length()==0)
        t=" ";
    setText(t);
  }
}

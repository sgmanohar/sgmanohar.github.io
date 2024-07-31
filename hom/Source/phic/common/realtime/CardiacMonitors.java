package phic.common.realtime;
import phic.common.*;
import phic.ecg.*;
import phic.*;
/**
 *
 */

public class CardiacMonitors {
  Body body;
  public CardiacMonitors(Body body) {
    this.body=body;
    data=LII.getPoints(ph.createField());
  }

  PhicHeart ph = new PhicHeart(body);
  Lead LII = Trace.leads[Trace.II];

  double[][] data;

  public LeadII leadII = new LeadII();

  double lastPhase=0;
  int lastPos=0;
  double lastVal=0;
  class LeadII implements Variable{
    public double get(){
      Clock c=body.getClock();
      double millis=c.elapsedTime();
      c.latch();
      double phase = Current.body.CVS.heart.phase;

      if(phase<lastPhase){
        data=LII.getPoints(ph.createField());
      }//recreate trace for each new beat.

      int pos=0;
      //find the position corresponding to the current time.
      double cumTime=0;
      for(int i=0;i<data.length;i++){
        cumTime+=data[i][0];
        if(cumTime>phase){ pos=i; break; }
      }
      if (lastPos==pos-1) return lastVal;
      int a,b;
      a=lastPos;b=pos;
      //if(lastPos<pos){a=lastPos;b=pos;} else {a=pos;b=lastPos;}
      //if(a==b)b--; //completed cycle.
      double peak=0;
      for(int i=a;i!=b;i=(i+1)%data.length) if(peak<Math.abs(data[i][1])) peak=data[i][1];
      lastPos=pos-1; if(lastPos<0) lastPos+=data.length;
      return lastVal=peak;
    }
    public void set(double d){}
  }
}
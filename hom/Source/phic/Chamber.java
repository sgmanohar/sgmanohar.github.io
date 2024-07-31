package phic;

import phic.common.*;
import phic.common.*;

/**
 * This class contains a volume of fluid, under a certain pressure, that
 * fluctuates between systole and diastole, as determined by a systolic
 * and diastolic compliance curve.
 */

public class Chamber implements HasContent{
  public VDouble sysP = new VDouble(UnitConstants.METRES_HG);
  public VDouble diaP = new VDouble(UnitConstants.METRES_HG);
  public VDouble sysV = new VDouble(UnitConstants.LITRES);
  public VDouble diaV = new VDouble(UnitConstants.LITRES);
  public VDouble atrialP = new VDouble(UnitConstants.METRES_HG);
  public void setStrokeVolume(double vol){ strokeVolume = vol;  }
  public void setRate(double rate){this.rate=rate;}
  public void setMeanP(double meanP){this.map=meanP;}
  protected double strokeVolume;
  protected double map;
  protected double rate;
  /**
   * This is calculated from heart rate and represents the proportion of the
   * total possible filling volume that is actually accomplished in the time
   * available for diastole.
   */
  public double fillingFraction;

  /**
   * This is the percentage of filling as dependent on diastolic time.
   * As decay constant increases, filling is faster.
   */
  public Curve.ExponentialApproach fillingCurve = new Curve.ExponentialApproach();


  public void reset(){map=0;rate=72;strokeVolume=0.065;fillingFraction=1.0;}
  /** Used to calculate the systolic blood pressure from the end diastolic volume */
  //public Curve sysCompliance=new Curve.Linear(0.8,0.020);
  /** Gives diastolic ventricular volumes given the pressure during diastole */
  public Curve.TwoGradients diaCompliance=new Curve.TwoGradients();

  /** Allows extra filling pressure e.g. in aortic regurg */
  protected double extraFillingPressure = 0;

  /**
   * from the diastolic pressure (DiaP = CVP), stroke volume, and
   * compliances -
   * calculates the diastolic volume, systolic volume, systolic pressure
   */
  public void tick(){
    double diav,sysv, sysp;
    fillingFraction = fillingCurve.getValue(60/rate);
    diaV.set( diav=fillingFraction
              * diaCompliance.getValue(
                            atrialP.get() + extraFillingPressure
                          ));
    sysV.set( sysv=Math.max(0,diav-strokeVolume) );
    //sysP.set( sysp=sysCompliance.getInverse(diav));
    diaP.set(diaCompliance.getInverse( sysv ));
  }
  /** gives the power of this chamber, in J/min */
  public VDoubleReadOnly power=new VDoubleReadOnly(){public double get(){
    return map * UnitConstants.Pa_per_mHg * strokeVolume * rate /60000;
  }};
  /**
   * Calculate the stroke work, the useful energy in one cardiac cycle. This is
   * approximated as the stroke volume * mean arterial pressure.
   * @return double the stroke work in joules.
   */
  public VDoubleReadOnly SW = new VDoubleReadOnly() {public double get(){
    return strokeVolume * (map - diaP.get()) *
        UnitConstants.Pa_per_mHg /1000;
  }};

  public VDoubleReadOnly EF = new VDoubleReadOnly(){ public double get(){
      return (diaV.get()-sysV.get())/diaV.get();
    }};

  /**
   * The starling curve for this ventricle
   */
  public Curve.Starling starling = new Curve.Starling();

  protected double suctionPressure(){
    return atrialP.initialValue - diaCompliance.getInverse(sysV.get());
  }
}

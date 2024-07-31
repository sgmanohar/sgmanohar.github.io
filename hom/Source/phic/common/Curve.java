package phic.common;

import evaluator.*;
import phic.Current;

/**
 * Basic curves, providing simple relations form one variable to another.
 */

public abstract class Curve implements HasContent{
  public Curve() {}
  protected double[] params;
  /** return the value of y at the given x value */
  public abstract double getValue(double x);
  /** return the value of x at the given y value */
  public abstract double getInverse(double y);


  /** A sigmoid curve */
  public static class Sigmoid extends Curve{
    public Sigmoid(double xmin, double xmax, double ymin, double ymax){
      params = new double[]{xmin, xmax, ymin, ymax};
    }
    public double getValue(double x){
      return params[2]+(params[3]-params[2]) *
          MathExtra.sigmoid((x*(params[1]-params[0]) - params[0]));
    }
    public double getInverse(double y){
      return (MathExtra.inverseSigmoid( (y-params[2])/(params[3]-params[2]) )
              +params[0]) / (params[1]-params[0]);
    }
  }

  /** A linear curve */
  public static class Linear extends Curve{
    public Linear(double gradient, double constant){
      //params = new double[]{gradient, constant};
      this.gradient=gradient; this.constant=constant;
    }
    public double gradient, constant;
    public double getValue(double x){      return gradient*x+constant;    }
    public double getInverse(double y){    return (y-constant)/gradient;  }
  }

  /**
   * A starling shaped curve, that translates end diastolic volume in Litres
   * into a stroke volume in Litres. Assume that
   * 1) EDV = 0 gives SV = 0
   * 2) curve is a straight line up to optimum
   * 3) EDV = optimumEDV gives SV = maximalSV
   * 4) above optimumEDV, SV falls linearly (with half the gradient of the
   *    first portion of the curve)
   */
  public static class Starling extends Curve{
    /** Value of EDV that gives the largest stroke volume / Litres*/
    public double optimumEDV = 0.120;
    /** Stroke volume at the optimum EDV / Litres */
    public double maximalSV  = 0.110;

    /** Minimum end-diastolic volume to generate any force at all / Litres */
    public double minimumEDV = 0.015;

    public double getValue(double EDV){
if(Current.thread.DEBUG_ERRORS && (EDV>optimumEDV*3 || EDV<0))throw new RuntimeException("EDV was "+EDV);
      if (EDV < optimumEDV) return maximalSV * (EDV-minimumEDV) / (optimumEDV-minimumEDV);
      else return Math.max(
          // maximalSV * (1 - (EDV - optimumEDV)/optimumEDV / 2)  // downwards slope - more physiological less realistic!
          maximalSV * (1 + 0.2 * (EDV-optimumEDV)/optimumEDV)     // one fifth gradient upwards...
        ,0);
    }
    public double getInverse(double y) {throw new IllegalStateException("Starling curve is a noninvertible function"); }
  }
  /**
   * Different gradients above and below the given split point
   */
  public static class TwoGradients extends Curve{
    public double splitX=0;
    public double splitY=0;
    public double lowerGradient=1;
    public double higherGradient=1;
    public boolean isSmooth = true;
    public double smoothing = 0.4;
    public double getValue(double x){
      double linear = (x>splitX ?  higherGradient : lowerGradient) * (x-splitX);
      return splitY + linear
                    - (isSmooth?smoothing*Math.exp(-Math.abs(linear)/smoothing):0)
      ;
    }
    public double getInverse(double y){
      if(lowerGradient*higherGradient<=0) throw new IllegalStateException("Noninvertible twogradient function");
      return (y>splitY?   (higherGradient>0 ?    (y-splitY)/higherGradient
                                           :    (y-splitY)/lowerGradient
                   ) :   (higherGradient>0 ?    (y-splitY)/lowerGradient
                                           :    (y-splitY)/higherGradient
                   )
             ) + splitX;
    }
  }

  /**
   * An exponential curve that rises up towards a limit
   * as decayConstant increases, rate of approaching maximum also increases.
   */
  public static class ExponentialApproach extends Curve{
    public double decayConstant = 1;
    public double maximalValue = 1;
    public double getValue(double x){
      return maximalValue*(1-Math.exp(-decayConstant*x));
    }
    public double getInverse(double y){
      return -Math.log(1-y/maximalValue)/decayConstant;
    }
  }
}

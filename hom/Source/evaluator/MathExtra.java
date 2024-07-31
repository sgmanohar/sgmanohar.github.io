package evaluator;

/**
 * Statics for extra maths functions
 */

public class MathExtra {

	//Hyperbolics

	public static final double cosh(double x)   { return (Math.exp(x) + Math.exp(-x))/2; }
	public static final double sinh(double x)   { return (Math.exp(x) - Math.exp(-x))/2; }
	public static final double tanh(double x)   { return sinh(x)/cosh(x); }

	//Trig Reciprocals

	public static final double cosec(double x)  { return 1/Math.sin(x); }
	public static final double sec(double x)    { return 1/Math.cos(x); }
	public static final double cot(double x)    { return 1/Math.tan(x); }

	//Hyperbolic reciprocals

	public static final double cosech(double x) { return 1/sinh(x); }
	public static final double sech(double x)   { return 1/cosh(x); }
	public static final double coth(double x)   { return cosh(x)/sinh(x); }

	public static final double log10(double x)  {return Math.log(x) / Math.log(10); }
	public static final double sgn(double x)    {return x / Math.abs(x); }
	public static final double factorial(double x) {
		int r = 1; for(int i=1;i<x;i++) r*=i; return r;
	}
	public static final double eulerGamma =
		0.577215664901532860606512090082402431042159335939923598805767234884867726777;
        public static final double frac(double x){ return x-Math.floor(x); }
        public static final double nCr(double n, double k){
          return factorial(n) / ( factorial(k) * factorial(n-k));
        }
        /** ramp between (0,0)  (1,1)  (2,0) (3,1) etc */
        public static final double ramp(double x){
          return (Math.floor(x)%2==0)?frac(x):1-frac(x);
        }
        public static final double truncatedRamp(double x){
          return Math.min(Math.max((ramp(x)*2)-0.5,0),1);
        }
        public static final double mod(double x, double b){          return x%b;        }

        /**
         * Gaussian function of the form exp(-x*x)
         */
        public static final double gaussian(double x) {    return Math.exp(-x*x);  }
  /**
   * Gabor function of the form A exp(-x*x) cos( w x )
   */
  public static final double gabor(double x, double frequency, double phase){        return gaussian(x)*Math.cos(x * frequency + phase); }
  /**
   * Sigmoid function that passes through (0,0), (0.5,0.5) and (1,1), with
   * gradient of zero at x=0 and x=1, and gradient of 1 at x=0.5
   */
  public static final double sigmoid(double x) { return x<0?0:(x>1?1:(
      0.5 - Math.cos(x * Math.PI) / 2));
  }

  public static final double inverseSigmoid(double y) {
    return y<=0?0:(y>=1?1:( Math.acos(1 - 2*y)/Math.PI ));
  }
}

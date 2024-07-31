package phic.common;

/**
 * A curve that has asymptotes at two straight lines.
 */

public class TwoGradientFunction {

  /**
   * A two-gradient function, based on a hyperbola, with asymptotes of gradient
   * m1 and m2, going through the origin, and a radius of curvature given by d.
   *
   * ( ( -x) * cos( (1 / 2) * ( -atan(m1) - atan(m2))) *  sin( (1 / 2) * ( -atan(m1) - atan(m2))) - x * cos( (1 / 2) * ( -atan(m1) - atan(m2))) * sin( (1 / 2) * ( -atan(m1) - atan(m2))) *            pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) , 2) -            sqrt(d * pow(  cos( (1 / 2) * ( -atan(m1) - atan(m2))) , 2) +                 x *x * pow( cos( (1 / 2) * ( -atan(m1) - atan(m2))) , 4) *                 pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) , 2) -                 d * pow( sin( (1 / 2) * ( -atan(m1) - atan(m2))) , 2) *                 pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) , 2) +                 2 * x *x * pow( cos( (1 / 2) * ( -atan(m1) - atan(m2))) ,2)                 * pow( sin( (1 / 2) * ( -atan(m1) - atan(m2))) , 2) *                 pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) , 2) +                 x *x * pow( sin( (1 / 2) * ( -atan(m1) - atan(m2))) ,4) *                 pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) , 2) )) /           (pow( cos( (1 / 2) * ( -atan(m1) - atan(m2))) ,2 )             - pow( sin( (1 / 2) * ( -atan(m1) - atan(m2))) , 2) *            pow( tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)) ,2) )
   *
   * @param x double the x coordinate of the function
   * @param d double the radius of curvature
   * @param m1 double the gradient when x is large and negative
   * @param m2 double the gradient when x is large and positive
   * @return double the y value of the function
   */
  public static double yy(double x, double d, double m1, double m2) {
    return
        ( ( -x) * cos( (1 / 2) * ( -atan(m1) - atan(m2))) *
         sin( (1 / 2) * ( -atan(m1) - atan(m2))) -
         x * cos( (1 / 2) * ( -atan(m1) - atan(m2))) *
         sin( (1 / 2) * ( -atan(m1) - atan(m2))) *
         pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2) -
         sqrt(d * pow(cos( (1 / 2) * ( -atan(m1) - atan(m2))), 2) +
              x * x * pow(cos( (1 / 2) * ( -atan(m1) - atan(m2))), 4) *
              pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2) -
              d * pow(sin( (1 / 2) * ( -atan(m1) - atan(m2))), 2) *
              pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2) +
              2 * x * x * pow(cos( (1 / 2) * ( -atan(m1) - atan(m2))), 2) *
              pow(sin( (1 / 2) * ( -atan(m1) - atan(m2))), 2) *
              pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2) +
              x * x * pow(sin( (1 / 2) * ( -atan(m1) - atan(m2))), 4) *
              pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2))) /
        (pow(cos( (1 / 2) * ( -atan(m1) - atan(m2))), 2) -
         pow(sin( (1 / 2) * ( -atan(m1) - atan(m2))), 2) *
         pow(tan( (1 / 2) * ( -atan(m1) - atan(m2)) + atan(m2)), 2));
  }

  private static double sqrt(double a) {
    return Math.sqrt(a);
  }

  private static double sin(double a) {
    return Math.sin(a);
  }

  private static double tan(double a) {
    return Math.tan(a);
  }

  private static double atan(double a) {
    return Math.atan(a);
  }

  private static double cos(double a) {
    return Math.cos(a);
  }

  private static double pow(double a, double b) {
    return Math.pow(a, b);
  }
}

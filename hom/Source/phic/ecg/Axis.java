package phic.ecg;
/**
 * Class to represent 3D angles.
 */
public class Axis{
  /**
   * Create an axis from the two angles.
   * @param theta the angle in the coronal plane.
   * @param phi the angle in the transverse plane.
   */
  public Axis(double theta,double phi){
		this.theta=theta;
		this.phi=phi;
	}

        /**
         * Create an axis at the given angles from a starting axis.
         * This constructor adds the angles together. */
	public Axis(Axis initial,double dtheta,double dphi){
		this.theta=initial.theta+dtheta;
		this.phi=initial.phi+dphi;
	}

        /**
         * This value is the angle in the coronal plane, clockwise from the right arm
         */
	public double theta=0;
        /**
         * This value is the angle in the transverse plane
         */
	public double phi=0;

        /** return an x-direction cosine - the x-coordinate of the unit vector */
	public double x(){
		return Math.cos(theta)*Math.cos(phi);
	}

        /** return an y-direction cosine - the y-coordinate of the unit vector */
	public double y(){
		return Math.sin(theta)*Math.cos(phi);
	}

        /** return an z-cosine - the z-coordinate of the unit vector */

	public double z(){
		return Math.sin(phi);
	}

	/**
	 * Return a 4-vector in the direction of this axis, with
	 * the given magnitude, and the given duration.
	 */
	public double[] vector(double magnitude,double duration){
		return new double[]{magnitude*x(),magnitude*y(),magnitude*z(),duration
		};
	}
}

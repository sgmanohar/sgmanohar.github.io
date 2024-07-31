package phic.ecg;
/**
 * ECG lead: resolves a three-dimensional vector field timecourse into a
 * one-dimensional electrical recording.
 */
public class Lead{
	public Lead(){}

	public Lead(double positionX,double positionY,double positionZ,String name){
		x=positionX;
		y=positionY;
		z=positionZ;
		this.name=name;
	}

	public Lead(double theta,double phi,String name){
		this(new Axis(theta,phi),name);
	}

	public Lead(Axis a,String name){
		x=a.x();
		y=a.y();
		z=a.z();
		this.name=name;
	}

	double x,y,z;

	public String name;
	/** Convert a 3-vector to a scalar magnitude on this lead */

	double resolve(double[] v){
		double magn=Math.sqrt(x*x+y*y+z*z);
		double dot=v[0]*x+v[1]*y+v[2]*z;
		return dot/magn;
	}

	/**
	 * Given an electric field vector from the heart, calculate a series
	 * of points (time, magnitude) representing the output of this lead.
	 */
	public double[][] getPoints(double[][] field){
		double[][] p=new double[field.length][2];
		for(int i=0;i<field.length;i++){
			double[] v=new double[]{field[i][0],field[i][1],field[i][2]};
			p[i][0]=field[i][3];
			p[i][1]=resolve(v);
		}
		return p;
	}
}

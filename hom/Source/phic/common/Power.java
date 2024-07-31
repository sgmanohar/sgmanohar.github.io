package phic.common;
/**
 * Variable representing a power, with utility methods to convert the power
 * betwen calories per second, minute, and watts.
 * @todo Redundant. Use or remove.
 */
public class Power implements Variable{
	//One calorie = energy required to raise 1g water by 1K
	static final double JOULES_PER_CALORIE=4.1868;

	public Power(){
	}

	public double watts;

	public final double joulesPerMinute(){
		return 60*watts;
	}

	public final double calsPerSecond(){
		return watts/JOULES_PER_CALORIE;
	}

	public final double calsPerMinute(){
		return calsPerSecond()*60;
	}

	public double calsPerDay(){
		return calsPerSecond()*86400;
	}

	/**
	 * Implementation of interface Variable: the value is in Joules
	 *	per minute.
	 */
	public double get(){
		return joulesPerMinute();
	}

	public void set(double joulesPerMinute){
		watts=joulesPerMinute/60;
	}
}
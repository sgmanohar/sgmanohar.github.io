package phic.ecg;
/**
 * Interface of heart for ECG.
 * The field is an array of 4-vectors, whose coordinates are
 *  (Ex, Ey, Ez, t).
 */
public interface Heart{
	public double getRate();

	public double[][] getBeatsField(double time);
}
package phic.common;

/**
 * Any class that has a 'tick' method, which forces the class to update at a
 * dependent rate.
 * Typically, a thread that manages tickers will have a Vector that represents
 * a list of Tickers that wish to be called at a certain point in time.
 */

public interface Ticker
{
  /**
   * The function that is called regularly.
   *
   * @param time the amount of time that has elapsed, usually measured in seconds
   * (of body time) since the last call to this function
   */
	public void tick(double time);
}
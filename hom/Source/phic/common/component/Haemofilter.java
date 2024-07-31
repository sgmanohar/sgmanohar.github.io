package phic.common.component;

import phic.common.*;


/**
 * Filters the blood by removing 'tissue fluid' and replacing with 'ideal'
 * fluid.
 */

public class Haemofilter extends Organ {
  /**
   *  Filtration rate, in litres per minute. This is the rate at which blood is
   * taken up by the filter.
   * 0.016 = 16ml/min = 1 L per hour
   */
  public double filtrationRate = 0.017;
  /**
   * Overall renal fluid balance, in litres per minute. 0 is even balance, i.e.
   * filtered volume = added volume. Positive values mean more fluid is added
   * than removed from the blood.
   */
  public double balance = 0;

  /**
   * This executes the filtration.
   */
  public void tick() {
    Container filtrate = body.blood.ultraFiltrate(filtrationRate * elapsedTime/60);
    body.blood.add(Fluids.get("Lactasol", filtrate.volume.get()+balance));
  }

}
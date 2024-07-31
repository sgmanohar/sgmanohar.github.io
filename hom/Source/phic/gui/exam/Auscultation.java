package phic.gui.exam;

import phic.Body;
import phic.common.Ticker;

/**
 * Generate sounds of auscultation
 */

public abstract class Auscultation implements Ticker{

  protected static final String auscultationImplClass="phic.gui.exam.AuscultationImpl";
  /** Factory Method */
  public static Auscultation createAuscultation(){
    try {
      return (Auscultation) Class.forName(auscultationImplClass).newInstance();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }


  Body body;
  /**
   *  Location at which we are auscultating, centred on the middle of the
   * sternum, with +1 and -1 being the borders of the thorax.
   */
  double xcoord=0, ycoord=0;


  public void setCoordinates(double x, double y) {
    xcoord=x; ycoord=y;
  }
  public abstract void startSound();
  public abstract void stopSound();
  /** Set the body object that provides the data for what is heard */
  public void setBody(Body body) {
    this.body = body;
  }

}

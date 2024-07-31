package phic.common;

import phic.drug.DrugContainer;
import java.util.List;

/**
 * Container that can be used for ingestion.
 */

public class FoodContainer extends DrugContainer {
  public FoodContainer() {
    qs.add(carbohydrate);
    carbohydrate.setUnit(UnitConstants.GRAMS_PER_LITRE);
    //List l=java.util.Arrays.asList(quantityname);
    //l.add("Carbohydrate");
    //quantityname=(String[])l.toArray();
  }
  public FoodContainer(Container source){
    this();
    this.add(source);
  }

  /**
   * convert a fraction of the carbohydrate in the container into glucose.
   */
  public void digestFraction(double f){
    double cglu = f * carbohydrate.getQ() * MOLES_GLUCOSE_PER_GRAM_CARB;
    carbohydrate.multiplyBy(1-f);
    glucose.addC(cglu);
  }
  public Container withdrawVol(double volume){
    FoodContainer f = new FoodContainer();
    withdrawVol_overwrite(volume, f);
    return f;
  }

  /** @todo what is the osmolarity of starch, relative to glucose ?*/
  protected double getOsmolarity(){
          return super.getOsmolarity() + carbohydrate.getC() * 0.8 /180;
  }

  public static final double MOLES_GLUCOSE_PER_GRAM_CARB = 1/180.;
  /** Quantity of carbohydrate, in grams */
  public Quantity carbohydrate = new Quantity(this);
}

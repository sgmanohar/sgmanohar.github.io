
package phic.drug;
import java.util.HashMap;
import phic.common.Quantity;

/**
 * An implementation of drug, with a quantity of the substance in a container.
 * The fields are the drug name, and the property map.
 */

public class DrugQuantity extends Quantity implements Drug,Cloneable{
	public DrugQuantity(DrugContainer c){
		super(c);
	}

  /**
   * Create a drug quantity, given an example of the drug (from which the
   * properties are extracted) and a container to put it in.
   */

  public DrugQuantity(DrugContainer c,DrugQuantity typeOfDrug){
		super(c);
		properties=typeOfDrug.properties;
		name=typeOfDrug.name;
    //approximate maximum value of drug is SINGLE_DOSE / 15 litres
		maximum=typeOfDrug.getProperty(Drug.SINGLE_DOSE)/15.0;
	}

	/** The common name of the drug */
	protected String name;

	/** Retrieve the name of the drug. */
	public String getName(){
		return name;
	}

	void setName(String s){
		name=s;
	}

	/** Returns whether the drug is the same as the one specified. */
	public boolean isSameDrug(DrugQuantity q){
		return(q.name==name);
	}

	/** These are the properties of the drug, that can be obtained by getProperty() */
	protected HashMap properties=new HashMap();

	/**
	 * Retrieve a property about the drug, as stored by the setProperty() method.
	 * If the property is not set, then return 0.0
	 */
	public double getProperty(Object key){
		Object value=properties.get(key);
		if(value!=null){
			return((Double)value).doubleValue();
		} else{
			return 0.0;
		}
	}

	/** Set a property of the drug. This is package-public. */
	void setProperty(Object key,double value){
		properties.put(key,new Double(value));
	}

	/**
	 * Displays the drug name, followed by the concentration.
	 * Converts the drug concentration into a string, using the correct units,
	 * through UnitConstants. This recognises grams and moles.
	 */
	public String toString(){
		return name+" "+phic.common.UnitConstants.formatValue(get(),unit,false);
	}

	/**
	 * By default, the unit reverts to grams per litre. This value is set up
	 * by the pharmacy from the line Unit = ... in the drug definition. It
	 * is used by toString().
	 */
	{
		unit=phic.common.UnitConstants.GRAMS_PER_LITRE;
	}

	/**
	 * Set the unit that this drug uses to display its value. It should
	 * be a concentration such as M or g/L.
	 */
	public void setUnit(int unit){
		this.unit=unit;
	}
}
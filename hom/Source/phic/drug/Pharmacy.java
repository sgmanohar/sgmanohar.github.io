package phic.drug;
import java.util.*;
import phic.common.*;
import phic.doctor.DrugParser;

/**
 * Helper class to dispense drugs that are listed in Pharmacy.txt. The pharmacy
		 * also gives information of which drugs are available and provides descriptions
 * of them.
 */
public class Pharmacy{
	/**
	 * The table file that contains drug data
	 */
	protected static IniReader drugFile=new IniReader("Pharmacy.txt");

	/**
	 * Returns a container with the drug dissolved in the specified volume of
	 * fluid, at the specified concentration.
	 * @param drugName the name of the drug, as specified in Pharmacy.txt
	 * @param volume the volume of the container to be returned
	 * @param concentration the concentration of drug in grams per litre
	 * @return A DrugContainer containing the drug in the given volume of water.
	 */
	public static DrugContainer dispenseDrug(String drugName,double volume,
			double concentration) throws NoSuchDrugException{
		DrugContainer c=new DrugContainer();
		c.volume.set(volume);
		DrugQuantity d=new DrugQuantity(c);
		setDrugFromFile(drugName,d);
		d.setC(concentration);
		c.drugqs.add(d);
		return c;
	}

	/**
	 * Returns a container with the specified quantity of drug dissolved in a 5ml
	 * ampoule.
	 * @param drugName the name of the drug
	 * @param quantity the quantity of drug in grams.
	 * @return a DrugContainer with the drug in 5ml of water.
	 */
	public static DrugContainer dispenseAmpoule(String drugName,
			double quantity) throws NoSuchDrugException{
		DrugContainer ampoule=new DrugContainer();
		ampoule.volume.set(0.005);
		DrugQuantity drugq=new DrugQuantity(ampoule);
		setDrugFromFile(drugName,drugq);
		drugq.setQ(quantity);
		ampoule.drugqs.add(drugq);
		return ampoule;
	}

        public static Container dispenseSubstance(String substanceToParse) throws
            NoSuchDrugException {
          return DrugParser.createSubstance(substanceToParse);
        }
        public static Container dispense(String substanceToParse) throws
            NoSuchDrugException {
          return DrugParser.createSubstance(substanceToParse);
        }
        /**
         * Shortcut to parses
         */
        public static Container get(String substanceToParse) throws NoSuchDrugException{
          return dispenseSubstance(substanceToParse);
        }


	/**
	 * Adds the given quantity of the specified drug to a container.
	 * If the container is not an instance of DrugContainer, the contents
	 * are transferred into a new DrugContainer.
	 *
	 * @param container the container to which the drug will be added
	 * @param quantity the quantity of the drug
	 * @param drugName the name of the drug
	 *
	 * @return the container containing the drug. This will be the same
	 * as 'container' if 'container' is an instance of DrugContainer; otherwise
	 * a new DrugContainer is returned.
	 */
	public static DrugContainer addDrugToContainer(String drugName,
			double quantity,Container container) throws NoSuchDrugException{
		DrugContainer c;
		if(container instanceof DrugContainer){
			c=(DrugContainer)container;
		} else{
			c=new DrugContainer();
			c.add(container);
		}
		DrugQuantity drugq=new DrugQuantity(c);
		setDrugFromFile(drugName,drugq);
		drugq.setQ(quantity);
		c.drugqs.add(drugq);
		return c;
	}

	/**
	 * Writes properties to a DrugQuantity object, using tags from the data file
	 * This is used internally only, to initialise the DrugQuantity.
	 */
	protected static void setDrugFromFile(String drugName,
			DrugQuantity drug) throws NoSuchDrugException{
		Map map;
		try{
			map=drugFile.getSectionMap(drugName);
		} catch(IllegalArgumentException e){
			throw new NoSuchDrugException("Can't find drug "+drugName);
		}
		for(Iterator i=map.keySet().iterator();i.hasNext();){
			Object key=i.next();
			Object value=map.get(key);
			if(value instanceof Double){
				drug.setProperty(key,((Double)value).doubleValue());
			}
		}
		//label the drug, making sure label is same as existing drugs.
		//this allows Drug.java to test equality of two drugs by comparing
		//the names with ==.
		drug.setName(drugName.intern());
		//units set up
		String u=(String)map.get("Unit");
		if(u!=null){
			if(u.equals("g")){
				drug.setUnit(UnitConstants.GRAMS_PER_LITRE);
			} else if(u.equals("mol")){
				drug.setUnit(UnitConstants.MOLAR);
			} else if(u.equals("U")){
				drug.setUnit(UnitConstants.UNITS_PER_LITRE);
			}
		}
	}

	/**
	 * Return the map of properties for a given drug, read from the file.
	 * @param drugName the name of the drug
	 * @return a Map object containing the properties of this drug.
	 */
	public static Map getDrugMap(String drugName) throws NoSuchDrugException{
		Map map=null;
		try{
			map=drugFile.getSectionMap(drugName);
		} catch(IllegalArgumentException e){
			throw new NoSuchDrugException("Can't find drug "+drugName);
		}
		return map;
	}

	public static String[] getDrugList(){
		return drugFile.getSectionHeaders();
	}

	/**
			 * Return the description of the drug. This is from the line "Description=..."
	 * in the pharmacy file.
	 * @param drugName the drug's name
	 * @return A String representing the description of the drug. This will
	 * not contain newline characters.
	 */
	public static String getDrugDescription(String drugName) throws
			NoSuchDrugException{
		Map map=getDrugMap(drugName);
		Object o=map.get("Description");
                String d;
		if(o!=null){
			d= o.toString();
		} else{
			d= "No description";
		}

                return d;
	}

	/**
	 * Return the units in which quantities of the drug are measured. Note
	 * this is different from the concentration units, which are used in
	 * DrugContainers and DrugQuantities.
	 */
	public static int getDrugUnit(String drugName) throws NoSuchDrugException{
		Map map=getDrugMap(drugName);
		Object o=map.get("Unit");
		if(o!=null){
			if(o.equals("g")){
				return UnitConstants.GRAMS;
			} else if(o.equals("U")){
				return UnitConstants.UNITS;
			} else if(o.equals("mol")){
				return UnitConstants.MOLES;
			} else{
				throw new RuntimeException("Bad unit '"+o.toString()+"' for drug "
						+drugName);
			}
		} else{
			return UnitConstants.GRAMS;
		}
	}

	/**
	 * Find the property of a drug that describes its usual single dose.
	 * Default dose is 1mg, 0.001 U, or 1 mmol (Depending on the specified
	 * units for the drug. If no unit is specified, grams are used).
	 */
	public static double getSingleDrugDose(String drugName) throws
			NoSuchDrugException{
		Map map=getDrugMap(drugName);
		Object o=map.get(Drug.SINGLE_DOSE);
		if(o instanceof Double){
			return((Double)o).doubleValue();
		} else{
			return 0.001;
		}
	}
}

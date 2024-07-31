package phic.doctor;
import java.util.StringTokenizer;
import phic.common.*;
import phic.drug.*;

/**
 * Translate descriptions of substances into containers.
 */
public class DrugParser{
	private static String[] fluidNames=Fluids.getNames(),
			drugNames=Pharmacy.getDrugList();

	private static String delimiters="+,";

	/**
	 * Creates a substance in a container, given a description
	 * Format of description:
	 *   Fluid_type fluid_volume + Drug_type drug_quantity
	 * e.g.
	 *   "Water 0.050 + Insulin 10.0 + glucose 0.278"
	 *   gives 10 units insulin in 50 ml of 50% glucose solution
	 * Note: units of drug depend on how drug is defined in Pharmacy.txt
	 */
	public static Container createSubstance(String description) throws
			NoSuchDrugException{
		Container substance=new Container();
		StringTokenizer st=new StringTokenizer(description);
		while(st.hasMoreTokens()){
			String t=st.nextToken();
			// ignore plus and comma
			if(delimiters.indexOf(t)>=0){
				continue;
			}
			// is it a fluid?
			if(isFluid(t)){
				String name=t;
				if(!st.hasMoreTokens()){
					throw new IllegalArgumentException("Volume expected after fluid '"+t
							+"'");
				}
				double volume=Double.parseDouble(st.nextToken());
				substance.add(Fluids.get(name,volume));
			}
			// is it a drug?
			else if(isDrug(t)){
				String name=t;
				if(!st.hasMoreTokens()){
					throw new IllegalArgumentException("Quantity expected after drug '"+t
							+"'");
				}
				double quantity=Double.parseDouble(st.nextToken());
				substance=Pharmacy.addDrugToContainer(name,quantity,substance);
			}
			// is it an elementary constituent of containers?
			else if(isConstituent(t)){
				Quantity q=null;
				for(int i=0;i<Container.quantityname.length;i++){
					if(Container.quantityname[i].equalsIgnoreCase(t)){
						q=(Quantity)substance.qs.get(i);
					}
				}
				if(!st.hasMoreTokens()){
					throw new IllegalArgumentException("Quantity expected after drug '"+t
							+"'");
				}
				double quantity=Double.parseDouble(st.nextToken());
				q.addQ(quantity);
			} else{
				throw new NoSuchDrugException("'"+t+"' is not a fluid nor a drug");
			}
		}
		return substance;
	}

	/**
	 * Is the given word the name of a fluid?
	 */
	public static boolean isFluid(String name){
		for(int i=0;i<fluidNames.length;i++){
			if(fluidNames[i].equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Is the given word the name of a drug?
	 */
	public static boolean isDrug(String name){
		for(int i=0;i<drugNames.length;i++){
			if(drugNames[i].equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}

	public static boolean isConstituent(String name){
		for(int i=0;i<Container.quantityname.length;i++){
			if(Container.quantityname[i].equals(name)){
				return true;
			}
		}
		return false;
	}
}

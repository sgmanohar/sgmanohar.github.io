package phic.common;
import phic.Current;

/**
 *  Fluids
 *  This class is used to retrieve containers of standard fluids,
 *  read in from a file listing their constituents.
 */
public class Fluids{
	/**
	 * The table read from the file "resources/Fluids.txt"
	 * (9 columns)
	 */
	private static Table table=new Table("Fluids.txt",9); //load data

	/**
	 * Retrieve the fluid of the given name, in a new container.
	 */
	static public Container get(String name,double volume){
		boolean found=false; //search data for fluid name
		int i=table.findRowFromColumn(8,name);
		if(i<0){
			Current.body.error("No such fluid");
			return null;
		}
		Container c=new Container(); //create a container
		for(int j=0;j<table.nCols-1;j++){ //fill it with the fluid
			((Quantity)(c.qs.get(j))).setC(table.getDouble(i,j)*1E-6);
		}
		c.volume.set(volume);
		return c;
	}


	/**
	 * Return a list of the fluids' names from the file Fluids.txt
	 */
	public static String[] getNames(){
		return table.getColumn(8);
	}
}

package phic.gui;
import phic.common.Table;
import phic.common.VDouble;

/**
 * Variables class that contains a static list of all loaded VisibleVariable
 * objects. They are loaded statically from the resource file Variables.txt.
 * When reading the names from the file, the name is parsed and the object
 * hierarchy is scanned for such an object -- this is performed in the
 * constructor of the VisibleVariable.
 * @see phic.gui.VisibleVariable#VisibleVariable
 */
public class Variables{
	private static Table table=new Table("Variables.txt",8);

	public static VisibleVariable[] variable=new VisibleVariable[table.nRows];

	static{
		initialiseVariables();
	}

	/** Create variables afresh from the table */
	public static void initialiseVariables(){
		System.out.print("Loading variables...");
		for(int i=0;i<table.nRows;i++){
                  try{
                    variable[i] = new VisibleVariable(table, i);
                  }catch(IllegalArgumentException x){
                    System.out.println("Error in Variables.txt: item "+i+", "+
                                       table.getString(i,6)+". Line reads\n"+
                               join(table.line(i),","));
                    throw x;
                  }
		}
		System.out.println("complete.");
	}
public static String join(String[] s,String d){
  StringBuffer t=new StringBuffer();for(int i=0;i<s.length;i++)t.append(s[i]+d);return t.toString();
}
	/**
	 * Gets a visible variable by its name, e.g. "body.blood.pH"
	 * Also tries to semi-intelligently match the string to names of variables
	 * by prepending body., and environment.
         * Throws an illegal argument exception if not found.
	 */
	public static VisibleVariable forName(String canonicalName){
		//convert all names to dot delimited format
		String c=canonicalName.replace('/','.');
		if(c.startsWith(".")){
			c=c.substring(1);
		}
		for(int i=0;i<variable.length;i++){
			if(variable[i].canonicalName.equalsIgnoreCase(c)){
				return variable[i];
			}
		}
		//added 24.10.02: if not found, try and prepend 'body.' or 'environment.'
		//and try the long and short names too, in a non-path-specific fashion.
		//they imply that no two variables may have the same long
		//or short names.
		for(int i=0;i<variable.length;i++){
			if(variable[i].shortName.equalsIgnoreCase(c)){
				return variable[i];
			}
                        if(variable[i].shortName.replace('$','2').equalsIgnoreCase(c)){
                          return variable[i];
                        }
			if(variable[i].longName.equalsIgnoreCase(c)){
				return variable[i];
			}
			if(variable[i].canonicalName.equalsIgnoreCase("body."+c)){
				return variable[i];
			}
			if(variable[i].canonicalName.equalsIgnoreCase("environment."+c)){
				return variable[i];
			}
		}
		throw new IllegalArgumentException("No such variable "+canonicalName);
	}

	/** Gets a visible variable by examining the 'object' and 'member' of a
	 tree Node object. */
	public static VisibleVariable forNode(Node n){
		if(n==null){
			return null;
		}
		for(int i=0;i<variable.length;i++){
			if(variable[i]==null||variable[i].node==null||variable[i].node.member==null
					||variable[i].node.object==null){
				continue;
			}
			if(variable[i].node.member.equals(n.member)
					&&variable[i].node.object.equals(n.object)){
				return variable[i];
			}
		}
		return null;
	}

        public static VisibleVariable forVDouble(VDouble vd) throws IllegalArgumentException{
          for(int i=0;i<variable.length;i++)
            if(variable[i].node.getVDouble() == vd) return variable[i];
          throw new IllegalArgumentException();
        }

	/** This initialises the variables to their starting values. */
	public static void initialise(){
		for(int i=0;i<variable.length;i++){
			variable[i].initialise();
		}
	}

        /**
         * Initialises only the variables in the body to their starting
         * values, leaving the environment variables unchanged.
         */
        public static void initialiseBodyValues(){
          for(int i=0;i<variable.length;i++){
            if(variable[i].node.canonicalName().startsWith("/Body")) variable[i].initialise();
          }
        }
        public static void recheckOverridden(){
          for(int i=0;i<variable.length;i++){
            variable[i].checkOverridden();
          }
        }

}

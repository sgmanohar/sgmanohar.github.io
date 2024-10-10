package unit;

import java.util.*;

public class Unit implements java.io.Serializable, Cloneable{
	class Basis implements java.io.Serializable{
		Unit baseUnit;
		int power=0;
	}
	Vector bases=new Vector();
	boolean isBasis=false;

		/**
		 * retrieve the power of the given unit in the bases
		 * if not already contained, create!
		 * should only be used for 'base' units.
		 */
	Basis getBasis(Unit u){
		for(int i=0;i<bases.size();i++){
			Basis b=(Basis)bases.elementAt(i);
			if(b.baseUnit == u)return b;
		}
		Basis b=new Basis();
		bases.addElement(b);
		b.baseUnit=u;
		return b;
	}

	public double multiplier=1;
	public String commonName;
	public String abbreviation;
	public String quantity;
	public boolean canPrefix = true;

	/**
	 * Constructor for base units
	 */
	protected Unit(String name, String abbr, String q){
		commonName=name;
		abbreviation=abbr;
		quantity=q;
		isBasis=true;
	}

	private static final String digits="012345";



	public Unit(String unitString, double multiplier, String name, String abbr,
							String q, boolean canPrefix){
		this(unitString, name, abbr, q);
		this.multiplier=multiplier;
		this.canPrefix = canPrefix;
	}


	/**
	 * Constructor for derived units with a multiplier
	 */
	public Unit(String unitString, double multiplier, String name, String abbr, String q){
		this(unitString, name, abbr, q);
		this.multiplier=multiplier;
	}
	/**
	 * Constructor for derived units
	 */
	public Unit(String unitString,String name,String abbr,String q){
		this(name,abbr,q);
		isBasis=false;
		int index=1;
		boolean minus=false,power=false;
		Unit lastUnit=null;
		for(StringTokenizer e=new StringTokenizer(unitString,"* ./^-12345",true);
			e.hasMoreTokens();){
			String token=e.nextToken();
				//test if it's already defined unit
			for(Enumeration ee=Units.elements();ee.hasMoreElements();){
				Unit u=(Unit)ee.nextElement();
				if( u.commonName.equals(token) ||
						token.endsWith(u.abbreviation) ){
					//found matching unit
					multiplyBy(u,index);
					//is multiplier prefix present?
					if(!(token.equals(u.abbreviation) || token.equals(u.commonName))){
						String prefix = token.substring(0,
							token.length() - u.abbreviation.length());
						double mul = getMultiplierFromPrefix(prefix);
						if(!Double.isNaN(mul))
							multiplier *= Math.pow( getMultiplierFromPrefix( prefix )
								,index);

					}
					if(index!=-1)index=1;
					lastUnit=u;minus=false;
					power=false;
					break;
				}
			}
			if(token.equals("/"))index=-1;else
			if(token.equals("-"))minus=true;else
			if(token.equals("^"))power=true;
			if(power){int i=digits.indexOf(token);
				if(i>=0){
					multiplyBy(lastUnit,index*(i-1));
					minus=false;power=false;index=1;
				}
			}
		}
	}

	/**
	 * Constructor for temporary units
	 */
	public Unit(String unitString){
		this(unitString, "","","");
	}


		//multiply my unit by the u^index
	public void multiplyBy(Unit u,int index){
		if(u.isBasis){
			Basis b=getBasis(u);
			b.power+=index;
		}else{
			multiplier *= Math.pow(u.multiplier, index);
			for(int i=0;i<u.bases.size();i++){
				Basis b = (Basis)u.bases.elementAt(i);
				multiplyBy(b.baseUnit, b.power * index);
			}
		}
	}




	//STRING CONVERSION


	public String baseString(){
		String s="";
		s+=getMultiplierString();
		boolean first=true;
		for(int i=0;i<bases.size();i++){
			Basis b=(Basis)bases.elementAt(i);
			if(b.power!=0) {
				s += istr(b.power, b.baseUnit.abbreviation, first) + ' ';
				first=false;
			}
		}
		return s;
	}
	String istr(int i,String s, boolean first){
		if(i==0)return "";
		if(i==1)return s;
		if(first || i>0)return s+"^"+i;
		else if(i==-1) return "/"+s;
		else return "/"+s+"^"+-i;
	}
	public String getDescription(){
		String s="";
		s+=getMultiplierString();
		for(int i=0;i<bases.size();i++){
			Basis b=(Basis)bases.elementAt(i);
			s+=b.baseUnit.abbreviation + ": " + b.power;
		}
		return s;
	}

	String[] prefix=new String[]{"n","µ","m","","k","M","T"};

	public String getMultiplierString(){
		if(!canPrefix)return "";
		if(multiplier==1)return"";
		double k=Math.log(multiplier)/Math.log(10);
		int kk=(int)Math.round(k);
		if(Math.abs(kk-k) < 1E-3){	//is it a multiple of 10?
			if(kk/3 == kk/3. && kk < 10 && kk > -10){	//is it a multiple of 1000?
				return prefix[kk/3+3];
			}
			return"E"+kk+" ";
		}
		return "x"+multiplier+" ";
	}

	public double getMultiplierFromPrefix(String p){
		String test=p.trim();
		for(int i=0;i<prefix.length;i++){
			if(test.equals(prefix[i])) return Math.pow(10, i*3-9);
		}
		return Double.NaN;
	}

	public String getSimpleName(){
		String s=tryFindSingleName();
		if(s!=null)return s;
		else return baseString();
	}

	public String toString(){return getSimpleName();}



	//COMPARING & SIMPLIFYING


	/**
	 * Routine to compare whether two units are equal,
	 * in terms of their base units. This method ignores whether
	 * the units have the same multiplier
	 */
	public boolean equalsInBasis(Unit u){
		if(u==null)return false;
			//comparing basis to a basis
		if(u.isBasis && isBasis){
			return commonName.equals( u.commonName );
		}
			//comparing non-basis to a basis?
		if(u.isBasis){
			if(bases.size()!=1)return false;
			Basis b=(Basis)bases.get(0);
			if( !b.baseUnit.commonName.equals(u.commonName) )return false;
			if( b.power != 1 )return false;
			return true;
		}
		 //comparing basis to non-basis?
		else if(isBasis) return u.equalsInBasis(this);

		if(bases.size() != u.bases.size())return false;
		int found=0;
		for(int i=0;i<bases.size();i++){
			Basis b=(Basis)bases.get(i);
			for(int j=0;j<bases.size();j++){
				Basis bb=(Basis)u.bases.get(j);
				if(b.baseUnit == bb.baseUnit){
					found++;
					if(b.power != bb.power) return false;
					break;
				}
			}
		}
		return found == bases.size();
	}

	/**
	 * This is an identity operation, and two equal units must have the same
	 * multiplier.
	 */
	public boolean equals(Unit u){
		if(!equalsInBasis(u))return false;
		if(multiplier != u.multiplier) return false;
		else return true;
	}

	private static String CONST = "None";

	/**
	 * Search through the units defined in Units.units, and returns the unit's
	 * name if it is identical to this unit.
	 */
	String tryFindSingleName(){
		for(Enumeration e=Units.elements();e.hasMoreElements();){
			Unit u=(Unit)e.nextElement();
			if(equals(u))return u.abbreviation;
		}
		if(bases.size() == 0) return CONST;
		return null;
	}


	/**
	 * Returns a list of Units which this unit can be converted to.
	 * Only returns units defined in Units.units that are equalInBasis.
	 * Ensures that the current unit is also represented as a choice.
	 */
	public Vector getCompatibleUnits(){
		Vector source = Units.units, result = new Vector();
		boolean includesCurrent = false;
		for(int i=0;i<source.size();i++){
			Unit testUnit = (Unit)source.get(i);
			if(equalsInBasis(testUnit)) {
				result.add(testUnit);
				if(equals(testUnit)) includesCurrent = true;
			}
		}
		if(!includesCurrent) result.add(this);
		return result;
	}

	public double getConversionTo(Unit u){
		return  multiplier / u.multiplier ;
	}

	public Object clone() {
		try{return super.clone();}
		catch(Exception e){e.printStackTrace();return null;}
	}
}
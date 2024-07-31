package phic.common;
import java.util.*;
/**
 * Methods to format numbers as strings in a particular unit, and the
 * constants used to enum the units.
 * Note: this system may be replaced in the future with the modifiable.Units
 * classes.
 */
public class UnitConstants{
	/**
	 * Unit constant values, just static members.
	 */
	public static final int PER_MIN=15,LITRES=4,LITRES_PER_MIN=12,METRES_HG=6,
			PRU=16,OSMOLES=11,PERCENT=17,NUMBER=0,MOLAR=14,DEGREES=8,GRAMS=3,
			GRAMS_PER_ML=20,JOULES_PER_MIN=18,LITRES_PER_ML=19,GRAMS_PER_LITRE=21,
			CAL_PER_DAY=23,MOLES=22,UNITS_PER_LITRE=24,UNITS=25,
                        SQUARE_METRES=26, JOULES=27 , LITRES_PER_MHG=28;

	/** The strings to represent each of the units */
	public static final String[] ustring=new String[]{"","1","2","g","L","5",
			"mHg","7","°C","9","10","Osm","L/min","13","M","/min","PRU","%","J/min",
			"L/L","g/mL","g/L","mol","cal/d","U/L","U","sq m", "J", "L/mHg"
	};

	/** The prefix strings for powers of 1000 */
	public static final String[] uprefix=new String[]{"p","n","µ","m","","k","M"};
        public static int significantFigures = 4;

	/**
	 * Method returning a string representing the value of this variable.
	 * The desired unit is displayed with a magnitude character, e.g. 'm'
	 * or 'k', if appropriate.
         *
         * 20/9/3 The number is now returned to 4 significant figures. This is
         * to help formatting neatly and avoid disturbing changes in the display.
         * The string length will usually be 4 digits and a decmal point
	 *
	 * Percentages are now transformed from fractional units, i.e.
	 * displayed number is 100 * v.
         *
	 * @param v the value to display
	 * @param units the unit code, e.g. PER_MINUTE
	 * @param addUnitSuffix if this is false, then the unit itself is not appended
         * @param fixed if this is true, then the value is given with a fixed
         *        number of significant figures (i.e. 4). Otherwise, it is
         *        returned with a precision of 3 decimal places.
         * @return the representation of the value as a string.
	 */
	public static final String formatValue(double v,int units,
			boolean addUnitSuffix, boolean fixed){
		if(Double.isNaN(v)){
			return "[Error]";
		}
		String prefix="";
		boolean neg=(v<0);
		v=Math.abs(v);
		if(units==PERCENT){
			v*=100;
		}
		if(canPrefix(units)&&v!=0){
			for(int i=0;i<uprefix.length;i++){
				double j=Math.pow(10,(i-4)*3);
				if(v<j*1000||i==uprefix.length-1){
					prefix=uprefix[i];
					v/=j;
					break;
				}
			}
		}
                String s=null;
                if(fixed){//fixed length (4 s.f.)
                    if(v>=100)v=Math.round(v*10)/10.;
                    else if(v>=10)v=Math.round(100*v)/100.;
                    else v=Math.rint(1000*v)/1000.; // 3 sig fig

                    s=Double.toString(v);

                    if(s.length()<5 && s.indexOf('.')>0){
                        s+='0'; while(s.length()<5) s+='0';
                    }
                }else{ //variable length, up to 3 d.p.
                    v=Math.round(v*1000)/1000.; // 3 decimal places
                    s=Double.toString(v);
                    if(s.endsWith(".0")){
                            s=s.substring(0,s.length()-2);
                    }
                }
		if(addUnitSuffix){
			return(neg?"-":"")+s+" "+prefix+ustring[units]; // unit
		} else{
			return(neg?"-":"")+s; // value
		}
	}

        /**
         * Gets a integer power of 1000 that is immediately below the variable's
         * value. For example, dividing the value by the multiplier will result
         * in a value between 1 and 1000.
         *
         * @param value the value for which to find a unit multiplier
         * @return a 'unit multiplier' - i.e. a power of 1000 that is below the
         * value of the variable.
         */
        public static final double getUnitMultiplier(double value, int unit){
          if(canPrefix(unit)){
            for(int i=0;i<uprefix.length;i++){
                    double j=Math.pow(10,(i-4)*3);
                    if(value<j*1000||i==uprefix.length-1) return j;
            } return 1;
          } else if(unit==PERCENT){
            return .01;
          }
          return 1;
        }


	/**
	 * Format the value, as in
	 * {@link #formatValue(double a, int u, boolean b) formatValue(double value,
	 * int unit, boolean addUnitSuffix)}
	 * and includes the unit suffix.
	 */
	public static String formatValue(double v,int units, boolean fixed){
		return formatValue(v,units,true,fixed);
	}

	/**
	 * Can we use milli, kilo etc. prefixes on this unit?
	 * Currently no prefixes for DEGREES, PERCENT, PER_MIN, NUMBER.
	 */
	public static boolean canPrefix(int u){
		if(u==DEGREES||u==PERCENT||u==PER_MIN||u==NUMBER
                  ||u==SQUARE_METRES){
			return false;
		}
		return true;
	}

	/** Conversion constants */
	public static final double Pa_per_mHg=133322.36841,mH2O_per_mHg=13.595098,
			g_per_lb=453.59237,J_per_cal=4.1868,L_per_gal=3.78541178,
			pints_per_L=2.1133764;

        /** A table of unit change values. */
        private static IniReader overrides = new IniReader("UnitOverrides.txt");

        /**
         * If this is false, then do not use unit overrides; each variable will
         * be displayed in its internal units.
         */
        private static boolean useUnitOverrides = false;
        public static void setUseUnitOverrides(boolean b){ useUnitOverrides=b; phic.gui.Variables.recheckOverridden();}
        public static boolean getUseUnitOverrides(){ return useUnitOverrides;}
        /**
         * Returns true if the file UnitOverrides contains an entry to
         * override this variable's unit
         */
        public static boolean canVariableBeOverridden(String variableName){
            String s[] = overrides.getSectionHeaders();
            for(int i=0;i<s.length;i++) if(s[i].equalsIgnoreCase(variableName)) return true;
            return false;
        }
        /**
         * Return the unit string for the specified variable
         * @param variableName the name of the variable
         * @return the unit, as a string, as it would be displayed (e.g. "Pa")
         */
        public static String getOverriddenUnit(String variableName){
            Map m = overrides.getSectionMap(variableName);
            return m.get("Unit").toString();
        }
        /**
         * return the unit conversion
         * This is the multiplier to convert from the standard unit for the given variable
         * (as specified in Variables.txt) to the overridden unit (as given above).
         */
        public static double getOverridenConversion(String variableName){
            Map m = overrides.getSectionMap(variableName);
            return ((Double)m.get("Conversion")).doubleValue();
        }
        public static double roundSigFigs(double value, int sf){
          int ord = (int)(Math.log(value)/Math.log(10));
          double msk = Math.pow(10, ord + sf);
          return ((int)(value * msk))/msk;
        }
    }

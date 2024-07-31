package phic.common;
/**
 * Gas contains two variables, CO2 and O2, which could represent concentration, volume
 * or percentage composition of the gases.
 * Can be cloned to perform calculations.
 */
public class Gas implements HasContent,Cloneable{
	/** The oxygen content of the gas */
	public VDouble O2=new VDouble();

	/** The CO2 content of the gas */
	public VDouble CO2=new VDouble();

	public Gas(){}

	public Gas(double O2,double CO2){
		this.O2.set(O2);
		this.CO2.set(CO2);
	}

	/**
         * Basic function returning a new gas with a multiple of the concentraion
	 * of this gas.
         * @param x a double by which to multiply the concentrations of gases.
         * @return a new Gas object with twice the concentrations of thic Gas.
         */
	public Gas multiply(double x){
		return new Gas(O2.get()*x,CO2.get()*x);
	}

	/** Set the value of this gas to the same as another gas. */
	public void set(Gas g){
		O2.set(g.O2.get());
		CO2.set(g.CO2.get());
	}
        /** Reset all concentrations to zero */
        public void empty(){
          O2.set(0);
          CO2.set(0);
        }

        public static class WithCO extends Gas{
          public VDouble CO = new VDouble();
          public void empty(){super.empty();CO.set(0);}
        }
}

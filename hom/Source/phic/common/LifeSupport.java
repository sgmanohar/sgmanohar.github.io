package phic.common;
import phic.Body;
import phic.gui.*;

/**
 * The life support machine is a class for maintaining variables at their
 * initial values. This is useful when some organs are disabled or inactivated,
 * and during debugging.
 * Simply, it fudges the values of a set of variables, to their default values.
 */
public class LifeSupport implements java.io.Serializable{
	/**
	 * Creates a life support machine. The call must be followed by a call
	 * to {@link #setBody(Body) setBody(Body)} to determine which body this
	 * object is provide life support for.
	 */
	public LifeSupport(){
	}

	/**
	 * Set which body the machine is to provide life support for.
	 * This is to be called by the environment on initialisation.
	 */
	public void setBody(Body body){
          this.body=body;
		isFudged=new boolean[Variables.variable.length];
		fudgeValue=new double[Variables.variable.length];
		for(int i=0;i<fudgeValue.length;i++){
			fudgeValue[i]=Double.NaN;
		}
	}

	/**
	 * The list of flags indicating which variables are fudged.
	 * @serial This is the only serialised field of this class.
	 */
	protected boolean[] isFudged;

	/**
	 * The list of the fixed values of fudged variables.
	 * If the value is NaN, then the initial value is used.
	 */
	protected double[] fudgeValue;

	/**
	 * The tick method calls the initialise() method on all the variables
	 * which are set to be fudged. tick() should be called either once every
	 * cycle, or (if asynchronous mode enabled) as often as the most frequently
	 * ticking organ.
	 *
	 * The method is called by the CVS as needed.
	 */
	public void tick(){
		for(int i=0;i<isFudged.length;i++){
			if(isFudged[i]){
				if(Double.isNaN(fudgeValue[i])){
					Variables.variable[i].initialise();
				} else{
					Variables.variable[i].node.doubleSetVal(fudgeValue[i]);
				}
			}
		}
	}

	/** Sets whether a variable is fudged or not */
	public void setFudgeVariable(VisibleVariable variable,boolean fudge){
		for(int i=0;i<isFudged.length;i++){
			if(Variables.variable[i]==variable){
				isFudged[i]=fudge;
			}
		}
	}

	/** Reads whether a variable is fudged or not */
	public boolean isFudgeVariable(VisibleVariable v){
		for(int i=0;i<isFudged.length;i++){
			if(Variables.variable[i]==v){
				return isFudged[i];
			}
		}
		throw new IllegalArgumentException("No such variable, "+v.toString());
	}

	/**
	 * Sets whether a variable is fudged or not. i is the index of the
	 * variable to set
	 */
	public void setFudgeVariable(int i,boolean fudge){
		isFudged[i]=fudge;
	}

	/**
	 * Reads whether a variable is fudged or not. i is the index of the
	 * variable to read.
	 */
	public boolean isFudgeVariable(int i){
		return isFudged[i];
	}

	/**
	 * Sets the value of a variable which is to be fudged. If the specified
	 * value is NaN, then the initial value of the variable is returned.
	 */
	public void setFudgeValue(VisibleVariable v,double value){
		for(int i=0;i<Variables.variable.length;i++){
			if(v==Variables.variable[i]){
				fudgeValue[i]=value;
			}
		}
	}

	/**
	 * Return the value at which the fudged variable will be fixed.
	 */
	public double getFudgeValue(VisibleVariable v){
		for(int i=0;i<Variables.variable.length;i++){
			if(v==Variables.variable[i]){
				if(Double.isNaN(fudgeValue[i])){
					return Variables.variable[i].node.getVDouble().initialValue;
				} else{
					return fudgeValue[i];
				}
			}
		}
		return Double.NaN;
	}
        Body body;
        public void reset(){
          setBody(body);
        }
}

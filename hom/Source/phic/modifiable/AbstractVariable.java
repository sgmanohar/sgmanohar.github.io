package phic.modifiable;
import evaluator.Expression;
import phic.modifiable.unit.Unit;

/**
 * Abstract modifiable variable.
 * Includes abstract getters and setters from phic.common.Variable
 */
public abstract class AbstractVariable implements phic.common.Variable{
	public double initialValue;

	public double maximum;

	public double minimum;

	private Unit unit;

	/** The formula text */
	private String formulaText;

	/** The formula expression */
	private Expression formula;

	public String getFormulaText(){
		return formulaText;
	}

	/**
	 * Sets and attempts to compile the formula given. If the compile
	 * fails, the formula is not changed.
	 */
	public void setFormulaText(String s) throws IllegalArgumentException{
		formula=new Expression(s);
		//assert formula.unit == unit
		formulaText=s;
	}

	public String description;

	public AbstractVariable[] getInfluencedBy(){
		return new AbstractVariable[0];
	}

	public AbstractVariable[] getInfluences(){
		return new AbstractVariable[0];
	}
}
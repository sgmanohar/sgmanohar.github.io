package phic.drug;
/**
 * Utility class:
 * Pharmacokinetics module, provides dose-response curves.
 */
public class Binding{
	/**
	 * Simple agonist activation of receptors by a drug
			 * The result is the fractional binding of the drug at the given concentration
	 */
	public static double agonist(double drugConcentration,double IC50){
		return drugConcentration/(drugConcentration+IC50);
	}

	/**
	 * Agonist with competitive antagonist present.
			 * The result is the fractional binding of the agonist, when in the presence of
	 * another agent that competetively binds (and thus inhibits).
	 */
	public static double competitive(double agonistConcentration,
			double antagonistConcentration,double agonistIC50,double antagonistIC50){
		return 0;
	}
}
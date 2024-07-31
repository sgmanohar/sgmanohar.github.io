/**
 * A drug has a set of properties, each a double value, accessed by a key.
 */
package phic.drug;
public interface Drug{
	/**
	 * Keys for drug properties
	 * The default value of all unspecified properties is zero.
	 */
	public static final String
			/** recommended single dose in grams */
			SINGLE_DOSE="SINGLE_DOSE",
			/** Metabolic parameters */
			/**
			 * Renal reabsorption: range [ -1 , +1 ]
			 * When positive, this is the fraction of total urinary drug that is
			 * reabsorbed. When negative, this is the fraction of total renal blood
			 * flow which is cleared of the drug.
			 */
			RENAL_REABSORPTION="RENAL_REABSORPTION",
                        /**
                         * Hepatic metabolism is the fraction of the amount of
                         * drug passing through the liver that is metabolised.
                         * First pass metabolism is carried out on the contents
                         * of the portal vein, then the blood is filtered
                         * depending of the hepatic blood flow.
                         */
                        HEPATIC_METABOLISM="HEPATIC_METABOLISM",
                        /**
                         * This is the ratio at equilibrium of concentration in
                         * fat to concentration in blood. If this is 1.0, then
                         * the fat concentration approaches the blood
                         * concentration. If it is greater, then the fat
                         * will absorb more drug from the blood, but this is
                         * of course limited by the fat blood flow.
                         */
                        LIPID_SOLUBILITY="LIPID_SOLUBILITY",
			/**
			 * This adds to the osmolarity of the container with the drug in. This can
			 * be accessed as a 'receptor activity' to get the total osmotic effect
			 * in a container.
			 */
			OSMOTIC_EFFECT="OSMOTIC_EFFECT",
			/**
			 * Receptor activities: values should be activity at a concentration
			 * of 1 unit, as specified by the units of the drug. This is given by
			 * the Unit = ... line in the drug definition. By default, it is the activity
			 * at a concentration of 1 gram per litre.
			 *
			 *  The values may represent binding affinity to receptors.
			 *
			 * The pairs are bindings between the constants used in the PHIC program
			 * and the strings specified in the drug definition; i.e.
			 * the string in quotes must be exactly as typed in the drug's property
			 * definition in Pharmacy.txt
			 */
			/**
			 * Alpha activity implemented in PerfusedOrgan; 1 unit of activity
			 * raises arterial resistance by 1 PRU
			 */
			ALPHA_ADRENOCEPTOR="ALPHA_ADRENOCEPTOR",
			/**
			 * Beta activity implemented in Heart; 1 unit of activity raises
			 * stroke volume by 1 L/min
			 */
			BETA_ADRENOCEPTOR="BETA_ADRENOCEPTOR",
                        /**
                         * The opiate receptor, when stimulated, inhibits
                         * respiratory drive, decreases pain (analgesic effect),
                         * decreases gut motility, and increases the level of
                         * nausea and sedation.
                         */
                        MU_OPIATE_RECEPTOR="MU_OPIATE_RECEPTOR",

                        /**
                         * The Gamma-amino butyrate receptor, which has its effect
                         * directly on sedation level.
                         */
                        GABA_RECEPTOR="GABA_RECEPTOR",

			/**
			 * Angiotensin receptor blockers and agonists act on the kidney.
			 */
			ANGIOTENSIN_II_RECEPTOR="ANGIOTENSIN_II_RECEPTOR",
                        /**
                         * Constrict or relax venous smooth muscle
                         */
                        VENOUS_SMOOTH_MUSCLE="VENOUS_SMOOTH_MUSCLE",
			ANGIOTENSIN_CONVERTING_ENZYME="ANGIOTENSIN_CONVERTING_ENZYME",
			ALDOSTERONE_RECEPTOR="ALDOSTERONE_RECEPTOR",
			LOOP_REABSORPTION="LOOP_REABSORPTION",
                        ADH_RECEPTOR="ADH_RECEPTOR",
			INSULIN_EFFECT="INSULIN_EFFECT",
                        SEDATIVE_EFFECT="SEDATIVE_EFFECT",
                        /**
                         * This is used to regulate the hypothalamus's
                         * temperature control.
                         */
                        INFLAMMATORY_ACTIVITY="INFLAMMATORY_ACTIVITY";

	/**
	 * This retrieves the value of a given property.
	 * @param key The key of the property requested, from the static list above.
			 * @return The value of this property. If no property is stored, the drug shoul
	 * return the default value.
	 */
	double getProperty(Object key);

	/**
	 * Set the unit of the drug. The unit should be a constant defined in
	 * UnitConstants.java. The units are usually set up by the pharmacy, as
	 * read in from the Pharmacy file, where they are defined by the line
	 * Unit=...
	 * The unit is a concentration unit, i.e. either Molar or g/L.
	 * The unit is used to display the concentration of a drug in a drug
	 * container, by the DrugConcentrationGraphChooser gui.
	 */
	void setUnit(int unit);
}

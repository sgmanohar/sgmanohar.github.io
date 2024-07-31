package phic.common;
/**
 * Basic extension of Organ, which has an arterial resistance, which is used to
 * calculate a blood flow rate through the organ.
 * Currently no arterial-venous access to the blood implemented; organs wishing
 * to alter blood constituents must use the central Blood from Body.blood.
 *
 * @todo Implement arterial and venous blood access.
 */
public abstract class PerfusedOrgan extends Organ{
	/**
	 * The resistance of the artery supply to this organ. This value may
	 * be regulated in the organ's tick() method.
	 * Meausred in PRU; PRU is L/min/mmHg.
	 */
	public VDouble resistance=new VDouble();

	/**
	 * Blood flow of the arterial blood supply to this organ. This value
	 * is calcultated automatically from the arterial pressure (CVS.AP)
	 * using the formula:
	 *  Arterial Pressure = flow * resistance.
	 * Measured in litres per minute.
	 * Currently, this variable cannot be set.
	 *
	 * 10/10/02 Added alpha adrenoceptor action which increases resistance.
	 * 13/1/03  Removed alpha: this may unfortunately be called twice during the same cycle...
	 *          Big problem here! flow.get() is called by the gui to display the variables
	 *          moved to CVS.VCT.
	 */
	public VDoubleReadOnly flow=new VDoubleReadOnly(){
		public double get(){
			double alpha=body.blood.getDrugBinding("ALPHA_ADRENOCEPTOR");
			double f=body.CVS.AP.get()/resistance.get()/body.blood.Visc.get();
                        return f;
		}
	};
}

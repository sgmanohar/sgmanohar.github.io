package phic.gui.exam;
import javax.swing.JPanel;

/**
 * Base class for all examination types.
 * Contains an interface for creating the panel
 */
public interface Examination{
        /** Return the JPanel which contains the displayable components */
        JPanel createPanel();

        /** Calls the code to initialise this panel's data from the current body */
	void initialise(phic.Body body);

	medicine.Entity[] getPathologies();

	medicine.Entity[] getSigns();

        /** The name of the panel, as it will appear in the list of examinations */
	String getName();

        /**
         *  Return the interval at which this panel wants to be updated,
         *  in realtime seconds. Use a value of less than zero if you do
         * not want updates.
         */
        double getUpdateFrequencySeconds();
}

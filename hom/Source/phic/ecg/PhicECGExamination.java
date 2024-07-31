package phic.ecg;
import javax.swing.JPanel;
import medicine.Entity;
import phic.Body;
import phic.gui.exam.Examination;

/**
 * Examination for ECG:
 * a simple implementation of Examination that returns a Trace.
 */
public class PhicECGExamination implements Examination{
	public String getName(){
		return "12-lead ECG";
	}

	public String toString(){
		return getName();
	}

	public Heart heart;

	public void initialise(Body b){
		heart=new PhicHeart(b);
		trace.heart=heart;
		trace.repaint();
	}

	Trace trace;

	public JPanel createPanel(){
		return trace=new Trace();
	}

	public Entity[] getSigns(){
		return null;
	}

	public Entity[] getPathologies(){
		return null;
	}
        public double getUpdateFrequencySeconds(){ return 0.4;}
}

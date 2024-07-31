package phic.gui.exam;
import javax.swing.JPanel;
import medicine.Entity;
import phic.Body;

/**
 * Examine the peripheral blood film of the patient
 */
public class BloodFilmExam implements Examination{
	public BloodFilmExam(){
	}

	BloodFilmPanel panel;

	public JPanel createPanel(){
		return panel=new BloodFilmPanel();
	}

	public void initialise(Body body){
          panel.opacity =  body.blood.MCH / 30E-12;
          panel.cellSize = body.blood.MCV / 88E-15;
          panel.opacityVariability = body.blood.anisocytosis;
          panel.cellSizeVariability = body.blood.anisocytosis;
		panel.cellFrequency[panel.ERYTHROCYTES]=body.blood.Hct.get()*5000/0.45;
		panel.tryCreateFilm();
	}

	public Entity[] getPathologies(){		return null;	}

	public Entity[] getSigns(){		return null;	}

	public String getName(){		return "Blood film";	}

	public String toString(){		return getName();	}
        public double getUpdateFrequencySeconds(){ return 5;}

}

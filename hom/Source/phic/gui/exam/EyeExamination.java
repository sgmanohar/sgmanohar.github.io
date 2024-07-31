package phic.gui.exam;
import javax.swing.*;
import java.awt.*;
import medicine.Entity;
import phic.*;
import phic.drug.Drug;

/**
 * Examination of the eye
 */
public class EyeExamination extends JPanel implements Examination {
	public String getName(){
		return "Eye examination";
	}

	public String toString(){
		return getName();
	}

	public Entity[] getPathologies(){
		return new Entity[0];
	}

	public Entity[] getSigns(){
		return new Entity[0];
	}

	EyeExaminationPanel panel=null;

	public JPanel createPanel(){
          if(panel==null){
            panel=new EyeExaminationPanel();
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
            add(new JLabel("Click to use pen torch"), BorderLayout.SOUTH);

          }
          return this;
        }

        /**
         * Currently modelled:
         * Morphine, sympathetics, death, unconsciousness and temperature
         *
         * Not yet modelled:
         * intracranial pressure,  motor palsies, blindness
         */
        public void initialise(Body body){
          int feeling = body.brain.getFeeling();
		if(feeling==Brain.DEAD){
			panel.eyes[0].parasympatheticPalsy=panel.eyes[1].parasympatheticPalsy=1;
			panel.lookSpeed=0;
		} else if(feeling == Brain.UNCONSCIOUS){
                  panel.lookSpeed=0;
		}else{
			panel.lookSpeed=0.4;
		}
                double mu = body.blood.getDrugBinding(Drug.MU_OPIATE_RECEPTOR)*10;
                double sy = Math.max(0,body.brain.Symp.getError())*5;
                panel.pupilSizeModification=Math.max(0,Math.min(5,sy-mu));

                SkinColour sc=new SkinColour();
                sc.initialise(body);
                panel.skinColour = sc.getColour();
                panel.eyes[0].pupilSize=panel.eyes[1].pupilSize=10-panel.pupilSizeModification;
                //sluggish pupils in cold
                panel.accommodationRate=Math.max(0, 0.2
                  *Math.max(0,Math.min(1, 1+0.13*(body.Temp.get()-33.0)))
                  - mu/10);
		//@todo implement intracranial pressure etc.
	}
        public double getUpdateFrequencySeconds(){ return 6; }
}

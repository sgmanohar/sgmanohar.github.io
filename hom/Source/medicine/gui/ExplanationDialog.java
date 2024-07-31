package medicine.gui;

import javax.swing.JDialog;

import medicine.*;

/**
 * A dialog box to display how two entities may be causally connected
 */

public class ExplanationDialog extends JDialog {

	public ExplanationDialog() {
	}

	String[] explanationsContinue = {
		", which may lead to ",
		", that may give rise to ",
		", leading to ",
		" and subsequent ",
		" and consequently "
	}, explanationsNewSentence = {
		". This might precipitate ",
		". This has been known to cause ",
		". It is possible for this to produce "
	}, explanationsStart = {
		" may cause "
	};

	String chooseOneOf(String[] choices){
		return choices[(int)(Math.random() * choices.length)];
	}
	String createExplanation(Entity[] sequence){
		String explanation = sequence[0].toString();
		boolean continuingSentence = false;
		for(int i=1;i<sequence.length;i++){
			if(i==1) {
				explanation += chooseOneOf(explanationsStart);
				continuingSentence = true;
			} else {
				if(continuingSentence) {
					explanation += chooseOneOf(explanationsContinue);
					//terminate the sentence?
					continuingSentence = Math.random() < 0.5;
				} else {
					explanation += chooseOneOf(explanationsNewSentence);
					continuingSentence = true;
				}
			}
			explanation += sequence[i];
		}
		explanation += ".";
		return explanation;
	}
}
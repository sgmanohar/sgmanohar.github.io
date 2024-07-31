/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package phic.gui;
import javax.swing.JComboBox;

public class VariableListBox extends JComboBox{
	public VariableListBox(){
		for(int i=0;i<Variables.variable.length;i++){
			addItem(Variables.variable[i]);
		}
	}
}
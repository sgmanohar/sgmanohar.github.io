
/**
 * All exhibits that can be tested by the class CudosTes have this interface
 */
package com.cudos.common;

import javax.swing.JTextPane;
import java.awt.Color;

public interface TestableExhibit {
	public JTextPane getTextPane();
	public int getTestItemCount();
	public int getTestControlCount();
	public void selectTestItem(int control,int index,Color colour);
	public void clearTestControl(int control);

	//added 21/1/03
	public CudosApplet getApplet();
	public String getTestName();
}
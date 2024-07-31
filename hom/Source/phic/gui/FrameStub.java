package phic.gui;

import java.awt.event.*;

/**
		 * Functions allowing the startup code to access a frame, without having to load
 * the class.
 */
public interface FrameStub{
	void show();

	void hide();

	void markEvent(Object event);

	void doSetup(String setupFile,String setupSection);

	void message(String message);

	javax.swing.JFrame getJFrame();

	/** Receive drip-finished events from Environment */

	void finishDrip(phic.IntravenousInfusion ivi);

  public void unconsciousForAges();

  public void addCloseListener(ActionListener reshowListener);
}

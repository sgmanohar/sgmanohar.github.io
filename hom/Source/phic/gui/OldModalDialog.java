package phic.gui;
import javax.swing.*;

import phic.*;
/**
 * Extension of JDialog used as a base class for phic modal dialogs
 * which must stop the clock when shown.
 */

public class OldModalDialog extends JDialog{
	/** Sets dialog as modal. */
	public OldModalDialog(){
		//set modal
		setModal(true);

	}

        /** This preserves the currentn clock state  */
	boolean restartClock;

        /** Whether or not to stop the clock while this dialog shows. */
        public boolean stopClock=true;

        /** Get the parent frame */
        public JFrame getParentFrame(){ return PhicApplication.frame.getJFrame(); }


	/* Stops current body clock if needed, and centres on parent frame */
	public void show(){
		//centre on parent frame
		JFrame parent=PhicApplication.frame.getJFrame();
		setLocation(parent.getX()+(parent.getWidth()-getWidth())/2,
				parent.getY()+(parent.getHeight()-getHeight())/2);
		//stop clock if necessary
                if(isModal()){
                  Body body = Current.body;
                  restartClock = body.clock.running && stopClock;
                  if (restartClock) {
                    body.setRunning(false);
                  }
                }
                super.show(); //this is the modal bit
	}
        public void hide(){
          if(isModal()){
            if (restartClock) {
              Current.body.setRunning(true);
            }
          }
          super.hide();
	}
        public void setPreferredSize(java.awt.Dimension d){ setSize(d); }
}

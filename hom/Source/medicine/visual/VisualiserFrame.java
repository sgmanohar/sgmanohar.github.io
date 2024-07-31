
/**
 * Visualiser frame
 */
package medicine.visual;

import javax.swing.JFrame;
import java.awt.*;

public class VisualiserFrame extends JFrame {
  public VisualiserPane visualiserPane1 = new VisualiserPane();

  public VisualiserFrame() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	setSize(400,400);
  }


/**
	todo: on close, inform graphic pane to close any adjustment dialog
*/

  private void jbInit() throws Exception {
    this.getContentPane().add(visualiserPane1, BorderLayout.CENTER);
  }
}
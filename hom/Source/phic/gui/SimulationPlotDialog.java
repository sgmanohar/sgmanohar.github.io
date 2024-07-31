package phic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog showing the a simulation plot
 */

public class SimulationPlotDialog extends ModalDialog {
  JPanel jPanel1 = new JPanel();
  SimulationPlotPanel simulationPlotPanel1 = new SimulationPlotPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  public SimulationPlotDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
    setTitle("Simulation plot");
  }
  private void jbInit() throws Exception {
    jButton1.setAction(simulationPlotPanel1.runAction);
    jButton2.setText("Close");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jButton1, null);
    jPanel1.add(jButton2, null);
    this.getContentPane().add(simulationPlotPanel1, BorderLayout.CENTER);
  }

  void jButton2_actionPerformed(ActionEvent e) {
    simulationPlotPanel1.halt();
    hide();
  }

}

package phic.gui;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sanjay Manohar</p>
 * <p>Company: CUDOS</p>
 * @author Sanjay Manohar
 * @version 1.0
 */

public class CurvesPanel extends JPanel{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  GraphPaper graphPaper1 = new GraphPaper();
  JSpinner jSpinner1 = new JSpinner();
  BorderLayout borderLayout3 = new BorderLayout();
  JSpinner jSpinner2 = new JSpinner();
  JLabel jLabel1 = new JLabel();
  public CurvesPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jPanel3.setLayout(borderLayout3);
    jLabel1.setText("jLabel1");
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel3.add(jSpinner1,  BorderLayout.WEST);
    jPanel3.add(jSpinner2, BorderLayout.EAST);
    jPanel3.add(jLabel1, BorderLayout.CENTER);
    jPanel1.add(jPanel4,  BorderLayout.WEST);
    jPanel1.add(graphPaper1, BorderLayout.CENTER);
    this.add(jPanel2,  BorderLayout.EAST);
  }

}
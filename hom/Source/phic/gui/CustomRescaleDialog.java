package phic.gui;

import phic.gui.HorzScrollGraph.DisplayVariable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Custom rescale a graph
 */

public class CustomRescaleDialog extends ModalDialog {
  public CustomRescaleDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    getRootPane().setDefaultButton(okbutton);
    pack();
  }
  DisplayVariable dv;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton okbutton = new JButton();
  JButton cancelbutton = new JButton();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JLabel jLabel3 = new JLabel();
  JLabel currvalue = new JLabel();
  JLabel currunits = new JLabel();


  public void setVariable(DisplayVariable dv){
    this.dv=dv;
    maxinput.setVariable(dv.vv);
    mininput.setVariable(dv.vv);
    maxinput.setValue(dv.maximum,false);
    mininput.setValue(dv.minimum,false);
    currunits.setText(maxinput.getUnitShown());
    currvalue.setText(dv.vv.formatValue(dv.vv.node.doubleGetVal(), false, false));
    setTitle(dv.vv.node.getFriendlyName()+" scale");
    pack();
  }
  ValueInput maxinput = new ValueInput();
  ValueInput mininput = new ValueInput();


  private void jbInit() throws Exception {
    okbutton.setText("OK");
    okbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateAndExit();
      }
    });
    cancelbutton.setText("Cancel");
    cancelbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hide();
      }
    });
    jPanel3.setLayout(borderLayout1);
    jLabel1.setText("Max");
    jPanel4.setLayout(borderLayout2);
    jLabel2.setText("Min");
    jPanel2.setLayout(borderLayout3);
    jLabel3.setText("The current value of the variable is");
    currvalue.setText("0");
    currunits.setText("u");
    this.getContentPane().add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(okbutton, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel1.add(cancelbutton, null);
    jPanel3.add(jLabel1, BorderLayout.WEST);
    jPanel4.add(mininput,  BorderLayout.CENTER);
    jPanel3.add(maxinput,  BorderLayout.CENTER);
    jPanel4.add(jLabel2, BorderLayout.WEST);
    jPanel2.add(jPanel5,  BorderLayout.CENTER);
    jPanel6.add(jLabel3, null);
    jPanel5.add(jPanel4, null);
    jPanel5.add(jPanel3, null);
    jPanel2.add(jPanel6,  BorderLayout.SOUTH);
    jPanel6.add(currvalue, null);
    jPanel6.add(currunits, null);
  }


  /**
   *  Transfer edited values into the DisplayVariable.
   * If there is an error in the entered values, do not close the dialog.
   */
  public void updateAndExit(){
    double max=maxinput.getValue(), min=mininput.getValue();
    if(Double.isNaN(max) || Double.isNaN(min)) return;
    dv.maximum=max;
    dv.minimum=min;
    dv.origin=min;
    dv.rescalePanel();
    hide();
  }

}



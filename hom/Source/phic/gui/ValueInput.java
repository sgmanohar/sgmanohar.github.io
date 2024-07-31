package phic.gui;

import java.awt.*;
import javax.swing.*;

import phic.common.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sanjay Manohar</p>
 * <p>Company: CUDOS</p>
 * @author Sanjay Manohar
 * @version 1.0
 */

public class ValueInput extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel1 = new JLabel();
  public ValueInput() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jLabel1.setText("u");
    jTextField1.setText("");
    jTextField1.setColumns(7);
    this.add(jTextField1, BorderLayout.CENTER);
    this.add(jLabel1,  BorderLayout.EAST);
  }
  /**
   * Sets the variable that is to be edited,
   * and also the value displayed is adjusted to the current value of the variable.
   * In addition, the unit is altered to match the current value.
   */
  public void setVariable(VisibleVariable v){
    vv=v;
    setValue(v.node.getVDouble().get(),true);
  }
  /**
   * Sets the value that is to be edited. If alterunit is true, then the unit
   * is altered to match the range of value that is being edited.
   */
  public void setValue(double varvalue, boolean alterunit){
    currval=varvalue;
    String s = vv.formatValue(currval, true, false);
    int spc=s.indexOf(' ');
    if(alterunit){
      unit = s.substring(spc + 1);
      unitmult = UnitConstants.getUnitMultiplier(currval, vv.units);
      jLabel1.setText(unit);
    }
    jTextField1.setText(s.substring(0,spc));
  }
  double currval;
  VisibleVariable vv;
  String unit;
  double unitmult;
  /**
   * Return the value that is being edited.
   * if the value is not valid, it returns Double.NaN
   */
  public double getValue(){
    double v;
    try{
      v = Double.parseDouble(jTextField1.getText());
    }catch(NumberFormatException x){
      x.printStackTrace();
      JOptionPane.showMessageDialog(this, "Illegal value",
                 "You must enter a valid number for "+vv.longName,
                 JOptionPane.ERROR_MESSAGE);
      return Double.NaN;
    }
    return v * unitmult;
  }
  public String getUnitShown(){
    return unit;
  }
  public double getUnitMultiplierUsed(){
    return unitmult;
  }
}

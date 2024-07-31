package phic.gui.graphics;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import phic.common.Gas;
import phic.common.VDouble;
import phic.gui.ThinNodeView;

public class GasLabel extends JPanel {
  JTextField o2txt = new JTextField();
  JTextField co2txt = new JTextField();
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel po2label = new JLabel();
  JLabel pco2label = new JLabel();
  GridLayout gridLayout2 = new GridLayout();
  public GasLabel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setPreferredSize(new Dimension(115,44));
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),BorderFactory.createEmptyBorder(1,1,1,1));
    o2txt.setText("0");
    this.setLayout(borderLayout1);
    co2txt.setText("0");
    this.setBorder(border1);
    jPanel1.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(2);
    po2label.setText("pO2");
    pco2label.setText("pCO2");
    jPanel2.setLayout(gridLayout2);
    gridLayout2.setColumns(1);
    gridLayout2.setRows(2);
    this.add(jPanel1, BorderLayout.WEST);
    jPanel1.add(po2label, null);
    jPanel1.add(pco2label, null);
    this.add(jPanel2,  BorderLayout.CENTER);
    jPanel2.add(o2txt, null);
    jPanel2.add(co2txt, null);
  }
  int nValues = 2;
  public void addValue(String label, VDouble variable){
    // use this to add lactate / pH as needed
  }
  public void setGas(Gas g){
    gas = g;
    pO2=g.O2; pCO2 = g.CO2;
    updateValues();
  }
  Gas gas;
  public Gas getGas(){ return gas;}
  VDouble pO2, pCO2;
  public void updateValues(){
    double po2=pO2.get(), pco2 = pCO2.get();
    o2txt.setText(pO2.formatValue(po2,true, true));
    co2txt.setText(pCO2.formatValue(pco2,true,true));
    po2label.setIcon(ThinNodeView.getIcon(po2,pO2));
    pco2label.setIcon(ThinNodeView.getIcon(pco2,pCO2));
  }
}

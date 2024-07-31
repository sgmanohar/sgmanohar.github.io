package phic.gui;

import phic.modifiable.Controller;
import phic.Body;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;

public class ControllerEditor extends JPanel {
  public ControllerEditor() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    varCombo.setEditable(true);
  }
  Controller[] controller;
  private Body body;
  JComboBox typeCombo = new JComboBox(Controller.Type.getTypes());
  TableCellEditor typeEditor = new DefaultCellEditor(typeCombo);
  JComboBox varCombo = new VariableSelectionCombo();
  TableCellEditor variableEditor = new DefaultCellEditor(varCombo);
  TableCellRenderer barRenderer = new BarRenderer();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTable1 = new JTable();
  public void setBody(Body b){
    body=b;
    controller = b.getControllerList().getControllers();
    model.fireTableDataChanged();
    jTable1.createDefaultColumnsFromModel();
    jTable1.getColumnModel().getColumn(1).setCellEditor(variableEditor);
    jTable1.getColumnModel().getColumn(2).setCellEditor(variableEditor);
    jTable1.getColumnModel().getColumn(3).setCellEditor(typeEditor);
    jTable1.getColumnModel().getColumn(7).setCellRenderer(barRenderer);
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.add(jScrollPane1, BorderLayout.CENTER);
    jTable1.setModel(model);
    jScrollPane1.getViewport().add(jTable1, null);
  }
  AbstractTableModel model = new AbstractTableModel(){
    public int getColumnCount(){ return 8; }
    public int getRowCount(){ if(controller==null) return 0; else return controller.length; }
    public Object getValueAt(int r, int c){
      if(controller==null) return null;
      switch(c){
          case 0: return controller[r].getDescription();
          case 1: return controller[r].getControlledVariable();
          case 2:
            if(controller[r].getControlling()!=null) return controller[r].getControllingVariable();
            return controller[r].getDrugProperty();
          case 3: return controller[r].getType();
          case 4: return new Double(controller[r].getGain());
          case 5: return new Double(controller[r].getRateFractionPerMinute());
          case 6: return new Double(controller[r].getConstant());
          default: return null;
      }
    }
    public String getColumnName(int c){ return columns[c]; }
    public boolean isCellEditable(int r, int c){ return c<7; }
    final String[] columns={"Description","Target","Controller","Control type",
        "Gain", "Rate", "Constant", "Error"};
    public void setValueAt(Object o, int r, int c){
      if(controller==null)return;
      switch(c){
        case 0: controller[r].setDescription(o.toString()); break;
        case 1: if(o instanceof VisibleVariable){
            controller[r].setControlledVariable((VisibleVariable)o);
          }break;
        case 2: if(o instanceof VisibleVariable){
            controller[r].setControllingVariable((VisibleVariable)o);
          }else if(o!=null) {
            controller[r].setDrugProperty(o.toString());
          }
        case 3: if(o instanceof Controller.Type) controller[r].setType((Controller.Type)o);
          break;
        case 4: if(o instanceof Number) controller[r].setGain(((Number)o).doubleValue());
          else if(o!=null) try{
            controller[r].setGain(Double.parseDouble(o.toString()));
          }catch(NumberFormatException e){ return; } //note, if an exception is thrown here, we cannot exit the editor normally.
          break;
        case 5: if(o instanceof Number) controller[r].setRateFractionPerMinute(((Number)o).doubleValue());
          else if(o!=null) try{
            controller[r].setRateFractionPerMinute(Double.parseDouble(o.toString()));
          }catch(NumberFormatException e){ return; }
          break;
        case 6: if(o instanceof Number) controller[r].setConstant(((Number)o).doubleValue());
          else  if(o!=null) try{
            controller[r].setConstant(Double.parseDouble(o.toString()));
          }catch(NumberFormatException e){ return; }
          break;
      }
    }
  };
  class BarRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      Object o = table.getModel().getValueAt(row, 2);
      if(o instanceof VisibleVariable){
        VisibleVariable vv = (VisibleVariable)o;
        HorizontalBar b = new HorizontalBar();
        b.drawText=false; b.setForeground(Color.cyan);
        b.setVariable(vv);
        return b;
      }
      return null;
    }
  }
}

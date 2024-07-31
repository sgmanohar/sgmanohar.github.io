package phic.gui;

import javax.swing.JComboBox;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class VariableSelectionCombo extends JComboBox implements ActionListener {
  public VariableSelectionCombo() {
    addActionListener(this);
    setModel(varmodel);
    setEditable(true);
    if(getEditor().getEditorComponent() instanceof JTextField){
      f = (JTextField) getEditor().getEditorComponent();
      f.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {     }
        public void insertUpdate(DocumentEvent e) { if(!updating) editChange();     }
        public void removeUpdate(DocumentEvent e) { if(!updating) editChange();      }
      });
    }
  }
  public void editChange(){
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        String s = f.getText();
        Object o = getSelectedItem();
        if (previousSelection != s && (o == null || o instanceof String || (
            o instanceof VisibleVariable &&
            ! ( (VisibleVariable) o).longName.equals(s)
            ))) { //change list if typed key, not if selected var
          updating = true;
          updateList(s);
          previousSelection = s;
          updating = false;
        }
      }
    });
  }

  JTextField f;
  DefaultComboBoxModel varmodel=new DefaultComboBoxModel();

  Object previousSelection = null;
  public void actionPerformed(ActionEvent e) {
    Object o=getSelectedItem();
    if(o!=previousSelection){
      if(o instanceof String){
        String s=o.toString();
        updateList(s);
      }
      previousSelection = o;
    }else{
      okAction();
    }
  }

  public Runnable acceptListener; //quick hack for listener!

  boolean updating;
  void updateList(Object o){
      String s;
      if(o==null)s=""; else s=o.toString();
      hidePopup();

      varmodel.removeAllElements();
      for (int i = 0; i < Variables.variable.length; i++) {
        VisibleVariable v = Variables.variable[i];
        String slc = s.toLowerCase();
        if (v.shortName.toLowerCase().startsWith(slc)) {
          varmodel.addElement(v);          continue;        }
        if (v.longName.toLowerCase().startsWith(slc)) {
          varmodel.addElement(v);          continue;        }
        if (v.canonicalName.toLowerCase().startsWith(slc)) {
          varmodel.addElement(v);          continue;        }
      }


      if (isShowing()) showPopup();
      //if (varmodel.getSize() == 1) setSelectedItem(varmodel.getElementAt(0));
      //else
      f.setText(s);
      f.setCaretPosition(s.length());

  }




  VisibleVariable selectedVariable;
  NodeView.Type selectedDisplay;
  boolean selectedScrollGraph=false;
  void okAction(){
    Object o=getSelectedItem();
    if(acceptListener!=null)acceptListener.run();
  }
}

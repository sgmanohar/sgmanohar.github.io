package phic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Caret;

public class QuickAdd extends ModalDialog {
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JComboBox varname = new JComboBox();
  JComboBox displaytype = new JComboBox();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  public QuickAdd() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(200,200);
    getRootPane().setDefaultButton(jButton1);
  }
  public void addNotify(){
    namefield = (JTextField)varname.getEditor().getEditorComponent();
    namefield.getDocument().addDocumentListener( new DocumentListener(){
      public void changedUpdate(DocumentEvent e) {     }
      public void insertUpdate(DocumentEvent e) {  if(!mod)doup(); }  public void removeUpdate(DocumentEvent e) {  if(!mod)doup(); }
      void doup(){
         SwingUtilities.invokeLater(new Runnable(){public void run(){
           String s=namefield.getText(); Object o=varname.getSelectedItem();
           if(lastSelection!=s && (o==null || o instanceof String || (
                 o instanceof VisibleVariable && !((VisibleVariable)o).longName.equals(s)
                 ))) { //change list if typed key, not if selected var
             mod=true;updateList(s); lastSelection=s;mod=false;
           }
         }
      });};
      boolean mod=false;
    });
    super.addNotify();
  }
  JTextField namefield;
  Object lastSelection = null;
  private void jbInit() throws Exception {
    this.setTitle("Quick add variable");
    jButton1.setText("OK");
    jButton1.addActionListener(new QuickAdd_jButton1_actionAdapter(this));
    jButton2.setText("Cancel");
    jButton2.addActionListener(new QuickAdd_jButton2_actionAdapter(this));
    varname.setEditable(true);
    varname.setModel(varmodel);
//    varname.addActionListener(new QuickAdd_varname_actionAdapter(this));

    jPanel1.setLayout(borderLayout1);
    displaytype.setModel(typemodel);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(varname,  BorderLayout.NORTH);
    jPanel1.add(jPanel3,  BorderLayout.SOUTH);
    jPanel3.add(currvaluetxt, null);
    jPanel3.add(displaytype, null);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton1, null);
  }

  Object previousSelection = null;
  void varname_actionPerformed(ActionEvent e) {
    Object o=varname.getSelectedItem();
    if(o!=previousSelection){
      if(o instanceof String){
        String s=o.toString();
        updateList(s);
        updateDisplayType();
      }
      previousSelection = o;
    }else{
      okAction();
    }
  }
  DefaultComboBoxModel varmodel=new DefaultComboBoxModel();
  DefaultComboBoxModel typemodel=new DefaultComboBoxModel();
  void updateList(Object s0){
    varname.hidePopup();
    varmodel.removeAllElements();
    String s;
    int icaret = namefield.getCaretPosition();
    if(s0 == null) s=""; else s=s0.toString();
    for(int i=0;i<Variables.variable.length;i++){
      VisibleVariable v=Variables.variable[i];
      String slc= s.toLowerCase();
      if(v.shortName.toLowerCase().replace('$','2').startsWith(slc)) {varmodel.addElement(v); continue;}
      if(v.longName.toLowerCase().startsWith(slc)) {varmodel.addElement(v); continue;}
      if(v.canonicalName.toLowerCase().startsWith(slc)) {varmodel.addElement(v);continue;}
    }
    varname.showPopup();
    if(varmodel.getSize()==1) varname.setSelectedItem(varmodel.getElementAt(0));
    else { varname.setSelectedItem(s); namefield.setCaretPosition(namefield.getDocument().getLength()); }
    updateValue();
  }
  void updateDisplayType(){
    typemodel.removeAllElements();
    Object o = varname.getSelectedItem();
    typemodel.addElement("Scroll graph");
    if(o instanceof VisibleVariable){
      VisibleVariable v = (VisibleVariable) o;
      NodeView.Type[] t = NodeView.Type.forNode(v.node);
      for (int i = 0; i<t.length; i++){
        typemodel.addElement(t[i]);
      }
    }
    if(typemodel.getSize()>0) displaytype.setSelectedIndex(0);
  }

  VisibleVariable selectedVariable;
  NodeView.Type selectedDisplay;
  boolean selectedScrollGraph=false;
  JLabel currvaluetxt = new JLabel();
  void okAction(){
    Object o=varname.getSelectedItem();
    Object p=displaytype.getSelectedItem();
    if(o instanceof VisibleVariable){
      selectedVariable = (VisibleVariable)o;
      if(p instanceof NodeView.Type) selectedDisplay  = (NodeView.Type)  p;
      else selectedScrollGraph = true;
      hide();
    }
  }
  void cancelAction(){
    selectedVariable=null;
    hide();
  }

  void jButton2_actionPerformed(ActionEvent e) {
    cancelAction();
  }

  void jButton1_actionPerformed(ActionEvent e) {
    okAction();
  }

  void varname_itemStateChanged(ItemEvent e) {
    updateDisplayType();
    updateValue();
  }
  void updateValue(){
    Object o = varname.getSelectedItem();
    if(o instanceof VisibleVariable){
      VisibleVariable v = (VisibleVariable) o;
      currvaluetxt.setText(v.formatValue(v.node.doubleGetVal(),true,false));
    }else currvaluetxt.setText("");
  }

}

class QuickAdd_varname_actionAdapter implements java.awt.event.ActionListener {
  QuickAdd adaptee;

  QuickAdd_varname_actionAdapter(QuickAdd adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.varname_actionPerformed(e);
  }
}

class QuickAdd_jButton2_actionAdapter implements java.awt.event.ActionListener {
  QuickAdd adaptee;

  QuickAdd_jButton2_actionAdapter(QuickAdd adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class QuickAdd_jButton1_actionAdapter implements java.awt.event.ActionListener {
  QuickAdd adaptee;

  QuickAdd_jButton1_actionAdapter(QuickAdd adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class QuickAdd_varname_itemAdapter implements java.awt.event.ItemListener {
  QuickAdd adaptee;

  QuickAdd_varname_itemAdapter(QuickAdd adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.varname_itemStateChanged(e);
  }
}

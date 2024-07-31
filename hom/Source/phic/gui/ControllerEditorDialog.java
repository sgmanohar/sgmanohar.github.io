package phic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import phic.Body;
import javax.swing.table.TableColumnModel;
import phic.Current;

public class ControllerEditorDialog extends ModalDialog {
  JPanel jPanel1 = new JPanel();
  ControllerEditor controllerEditior1 = new ControllerEditor();
  JPanel jPanel2 = new JPanel();
  JButton OKbutton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  ControllerEditor editor = new ControllerEditor();
  JButton helpButton = new JButton();
  JButton resetbutton = new JButton();
  public ControllerEditorDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(760,400);
    setTitle("Edit controllers");
    getRootPane().setDefaultButton(OKbutton);
  }
  void setFrame(SimplePhicFrame f){
    resetbutton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            editor.model.fireTableDataChanged();
          }});
      }
    });
    resetbutton.setAction(f.frameActions.resetControllers);
  }
  private void jbInit() throws Exception {
    OKbutton.setText("OK");
    OKbutton.addActionListener(new ControllerEditorDialog_OKbutton_actionAdapter(this));
    jPanel1.setLayout(borderLayout1);
    helpButton.setText("Help");
    helpButton.addActionListener(new ControllerEditorDialog_helpButton_actionAdapter(this));
    this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(resetbutton, null);
    jPanel2.add(helpButton, null);
    jPanel2.add(OKbutton, null);
    jPanel1.add(editor);
  }

  void OKbutton_actionPerformed(ActionEvent e) {
    hide();
  }

  public void setBody(Body b){
    editor.setBody(b);
    TableColumnModel cm = editor.jTable1.getColumnModel();
    cm.getColumn(0).setPreferredWidth(200);
    cm.getColumn(4).setPreferredWidth(30);
    cm.getColumn(5).setPreferredWidth(25);
    cm.getColumn(6).setPreferredWidth(25);
    cm.getColumn(7).setPreferredWidth(20);
  }

  void helpButton_actionPerformed(ActionEvent e) {
    HTMLMessagePane.showDialog("help/Controllers.html","Controllers help");
  }
}

class ControllerEditorDialog_OKbutton_actionAdapter implements java.awt.event.ActionListener {
  ControllerEditorDialog adaptee;

  ControllerEditorDialog_OKbutton_actionAdapter(ControllerEditorDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.OKbutton_actionPerformed(e);
  }
}

class ControllerEditorDialog_helpButton_actionAdapter implements java.awt.event.ActionListener {
  ControllerEditorDialog adaptee;

  ControllerEditorDialog_helpButton_actionAdapter(ControllerEditorDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.helpButton_actionPerformed(e);
  }
}

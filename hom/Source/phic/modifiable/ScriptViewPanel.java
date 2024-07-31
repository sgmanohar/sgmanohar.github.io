package phic.modifiable;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import evaluator.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sanjay Manohar</p>
 * <p>Company: CUDOS</p>
 * @author Sanjay Manohar
 * @version 1.0
 */

public class ScriptViewPanel extends JPanel {
  Script script;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  public JTextField nametxt = new JTextField();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JCheckBox continuouscheck = new JCheckBox();
  JTextField intervaltxt = new JTextField();
  JLabel jLabel3 = new JLabel();
  Component component1;
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();
  Border border1;
  Border border2;
  Border border3;
  JPanel jPanel6 = new JPanel();
  JButton compilebutton = new JButton();
  JLabel compiletxt = new JLabel();
  Border border4;
  BorderLayout borderLayout6 = new BorderLayout();
  JTextArea desctxt = new JTextArea();
  JTextArea bodytxt = new JTextArea();
  public ScriptViewPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setScript(null);
  }
  private void jbInit() throws Exception {
    component1 = Box.createHorizontalStrut(8);
    border1 = BorderFactory.createEmptyBorder(4,4,4,4);
    border2 = BorderFactory.createEmptyBorder(4,4,4,4);
    border3 = BorderFactory.createEmptyBorder(4,4,4,4);
    border4 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    this.setLayout(borderLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Script Name");
    nametxt.setToolTipText("Enter the name for the script");
    nametxt.setText("Script1");
    nametxt.setColumns(15);
    nametxt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nametxt_focusLost(e);
      }
    });
    jPanel1.setLayout(borderLayout2);
    jLabel2.setText("Description");
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setPreferredSize(new Dimension(130, 100));
    jPanel2.setLayout(borderLayout3);
    continuouscheck.setToolTipText("Whether the script runs as a continuous, time-dependent process");
    continuouscheck.setText("Continuous");
    intervaltxt.setToolTipText("If the script runs as a continuous process, this specifies how often " +
    "(in seconds) the script is executed");
  intervaltxt.setColumns(8);
    intervaltxt.setText("0.100");
    intervaltxt.setSelectionStart(5);
    jLabel3.setText("Interval");
    jPanel4.setLayout(borderLayout4);
    jPanel3.setBorder(border1);
    jPanel3.setMaximumSize(new Dimension(32767, 32767));
    jPanel3.setLayout(borderLayout5);
    borderLayout5.setHgap(4);
    borderLayout5.setVgap(4);
    jPanel4.setBorder(border2);
    borderLayout4.setHgap(4);
    borderLayout4.setVgap(4);
    jPanel2.setBorder(border3);
    compilebutton.setToolTipText("Attempt to compile the script body. If errors occur, they are shown " +
    "to the right");
    compilebutton.setText("Compile");
    compilebutton.addActionListener(new ScriptViewPanel_compilebutton_actionAdapter(this));
    compiletxt.setBorder(border4);
    compiletxt.setPreferredSize(new Dimension(150, 25));
    compiletxt.setToolTipText("Messages from the script compiler");
    compiletxt.setText("");
    jPanel6.setLayout(borderLayout6);
    desctxt.setFont(new java.awt.Font("Dialog", 0, 10));
    desctxt.setToolTipText("Type a description of the function here");
    desctxt.setText("");
    desctxt.setLineWrap(true);
    desctxt.setWrapStyleWord(true);
    this.setToolTipText("");
    bodytxt.setToolTipText("Type the body for the script here");
    bodytxt.setText("");
    bodytxt.setLineWrap(true);
    bodytxt.setWrapStyleWord(true);
    this.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel5, BorderLayout.NORTH);
    jPanel5.add(continuouscheck, null);
    jPanel5.add(component1, null);
    jPanel5.add(jLabel3, null);
    jPanel5.add(intervaltxt, null);
    jPanel2.add(jScrollPane2, BorderLayout.CENTER);
    jScrollPane2.getViewport().add(bodytxt, null);
    jPanel2.add(jPanel6,  BorderLayout.SOUTH);
    jPanel6.add(compilebutton, BorderLayout.WEST);
    jPanel6.add(compiletxt, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jPanel3,  BorderLayout.NORTH);
    jPanel3.add(jLabel1, BorderLayout.WEST);
    jPanel3.add(nametxt, BorderLayout.CENTER);
    jPanel1.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jLabel2, BorderLayout.WEST);
    jPanel4.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(desctxt, null);
  }
  public Script getScript() {
    return script;
  }
  public void setScript(Script script) {
    this.script = script;
    if(script!=null){
			nametxt.setText(script.name);
			desctxt.setText(script.description);
			continuouscheck.setSelected(script.isContinuous());
			intervaltxt.setText(String.valueOf(script.getExecutionInterval()));
			bodytxt.setText(script.getText());
      nametxt.setEnabled(true);
      desctxt.setEnabled(true);
      intervaltxt.setEnabled(true);
      bodytxt.setEnabled(true);
		}else{ //blank script
      nametxt.setText("");
      desctxt.setText("");
      intervaltxt.setText("");
      bodytxt.setText("");
      nametxt.setEnabled(false);
      desctxt.setEnabled(false);
      intervaltxt.setEnabled(false);
      bodytxt.setEnabled(false);
    }
  }
    public void updateScriptFromInput(){
           script.name=nametxt.getText();
           script.description=desctxt.getText().replace('\n',' '); //remove carriage returns from description.
           script.setContinuous(continuouscheck.isSelected());
           script.setExecutionInterval(Double.parseDouble(intervaltxt.getText()));
    }

  void compilebutton_actionPerformed(ActionEvent e) {
    if(script==null){compiletxt.setText("No script selected.");return;}
		updateScriptFromInput();
    boolean error =false;
		try{
			script.setText(bodytxt.getText());
		} catch(ParseException x){
      compiletxt.setText(x.toString());
      compiletxt.setToolTipText(x.toString());
      error=true;
		}
    if(!error) {
      compiletxt.setText("Successful");
      compiletxt.setToolTipText("The last compile completed with no errors");
    }
  }

  void nametxt_focusLost(FocusEvent e) {
    updateScriptFromInput();
  }
}

class ScriptViewPanel_compilebutton_actionAdapter implements java.awt.event.ActionListener {
  ScriptViewPanel adaptee;

  ScriptViewPanel_compilebutton_actionAdapter(ScriptViewPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.compilebutton_actionPerformed(e);
  }
}
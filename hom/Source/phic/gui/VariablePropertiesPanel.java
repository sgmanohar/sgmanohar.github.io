/**
 * A panel displaying information about the given variable.
 * It is composed of 3 tabs, for the names, values, and descriptions of the variable.
 * The description panel now interprets the data in VariableInfo.txt 'intelligently'
 * and creates hyperlinks to other variables.
 */
package phic.gui;
import phic.Resource;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import phic.common.*;
import java.awt.event.*;
import phic.common.*;
import phic.modifiable.*;
import phic.*;

public class VariablePropertiesPanel
    extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();

  JTabbedPane jTabbedPane1 = new JTabbedPane();

  JPanel jPanel1 = new JPanel();

  JPanel jPanel2 = new JPanel();

  Box box4;

  JLabel jLabel4 = new JLabel();

  JTextField initialvaluetxt = new JTextField();

  Box box5;

  JLabel jLabel5 = new JLabel();

  JTextField currentvaluetxt = new JTextField();

  JPanel jPanel3 = new JPanel();

  Border border1;

  TitledBorder titledBorder1;

  Box box6;

  JTextField lowerrangetxt = new JTextField();

  JTextField higherrangetxt = new JTextField();

  JLabel jLabel6 = new JLabel();

  JPanel jPanel4 = new JPanel();

  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  ReferenceValuesPanel referencevaluespanel;

  public VariablePropertiesPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    infotext.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
    infotext.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          String s = e.getDescription();
          VisibleVariable v = Variables.forName(s);
          if (v != null) {
            setVariable(v);
            if(variableListener!=null) variableListener.actionPerformed(new ActionEvent(
                this,0,v.longName));
          }
        }
      }
    });
  }

  ActionListener variableListener= null;
  public void addVariableListener(ActionListener al){
    variableListener=AWTEventMulticaster.add(al,variableListener);
  }
  public void remvoveVariableListener(ActionListener al){
    variableListener=AWTEventMulticaster.remove(al,variableListener);
  }

  private void jbInit() throws Exception {
    box4 = Box.createHorizontalBox();
    box5 = Box.createHorizontalBox();
    border1 = BorderFactory.createEtchedBorder();
    titledBorder1 = new TitledBorder(border1, "Normal range");
    box6 = Box.createHorizontalBox();
    box7 = Box.createVerticalBox();
    box2 = Box.createHorizontalBox();
    box1 = Box.createHorizontalBox();
    box3 = Box.createHorizontalBox();
    setLayout(borderLayout1);
//		fullnametxt.setText("jTextField1");
//		canonicalnametxt.setText("jTextField2");
    jPanel1.setLayout(borderLayout4);
//		shortnametxt.setText("jTextField1");
    jLabel4.setPreferredSize(new Dimension(73, 17));
    jLabel4.setText("Initial value");
    initialvaluetxt.setPreferredSize(new Dimension(90, 21));
    initialvaluetxt.setEditable(false);
//		initialvaluetxt.setText("jTextField1");
    jLabel5.setText("Current value");
    currentvaluetxt.setEnabled(true);
    currentvaluetxt.setAlignmentX((float) 0.5);
    currentvaluetxt.setPreferredSize(new Dimension(90, 21));
    currentvaluetxt.setEditable(false);
//		currentvaluetxt.setText("jTextField1");
    jPanel3.setBorder(titledBorder1);
    lowerrangetxt.setPreferredSize(new Dimension(80, 21));
    lowerrangetxt.setEditable(false);
//		lowerrangetxt.setText("jTextField1");
    higherrangetxt.setPreferredSize(new Dimension(80, 21));
    higherrangetxt.setEditable(false);
//		higherrangetxt.setText("jTextField2");
    jLabel6.setText("--");
    jPanel4.setLayout(borderLayout2);
//		infotext.setText("jEditorPane1");
    infotext.setEditable(false);
    jPanel5.setLayout(borderLayout3);
    canonicalnametxt.setEditable(false);
    jLabel2.setPreferredSize(new Dimension(91, 17));
    jLabel2.setText("Canonical name");
    fullnametxt.setEditable(false);
    jLabel1.setPreferredSize(new Dimension(91, 17));
    jLabel1.setText("Full name");
    jLabel3.setPreferredSize(new Dimension(91, 17));
    jLabel3.setText("Short name");
    shortnametxt.setEditable(false);
    unittxt.setText(" ");
    add(jTabbedPane1, BorderLayout.CENTER);
    jPanel1.add(box7, BorderLayout.NORTH);
    box2.add(jLabel2, null);
    box2.add(canonicalnametxt, null);
    box7.add(box1, null);
    box1.add(jLabel1, null);
    box1.add(fullnametxt, null);
    box7.add(box3, null);
    box3.add(jLabel3, null);
    box3.add(shortnametxt, null);
    box7.add(box2, null);
    jPanel2.add(box4, null);
    box4.add(jLabel4, null);
    box4.add(initialvaluetxt, null);
    jPanel2.add(box5, null);
    box5.add(jLabel5, null);
    box5.add(currentvaluetxt, null);
    jPanel2.add(unittxt, null);
    jPanel2.add(jPanel3, null);
    jPanel3.add(box6, null);
    box6.add(lowerrangetxt, null);
    box6.add(jLabel6, null);
    box6.add(higherrangetxt, null);
    jTabbedPane1.add(jPanel4, "Info");
    jTabbedPane1.add(jPanel2, "Values");
    jPanel4.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(infotext, null);
    jTabbedPane1.add(jPanel5, "Reference");
    jTabbedPane1.add(jPanel1, "Name");
  }

  /** The input file, VariableInfo.txt */
  IniReader info = new IniReader("VariableInfo.txt");

  VisibleVariable v = null;

  JScrollPane jScrollPane1 = new JScrollPane();

  JEditorPane infotext = new JEditorPane();
  Box box7;
  JTextField canonicalnametxt = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField fullnametxt = new JTextField();
  Box box2;
  JLabel jLabel1 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField shortnametxt = new JTextField();
  Box box1;
  Box box3;
  BorderLayout borderLayout4 = new BorderLayout();
  /**
   * Setup the data for this panel from the file VariableInfo.txt, looking
   * up the given variable.
   */

  public void setVariable(VisibleVariable v) {
    this.v = v;
    //names
    fullnametxt.setText(v.longName);
    canonicalnametxt.setText(v.canonicalName);
    shortnametxt.setText(v.shortName);
    //values
    VDouble vd = v.node.getVDouble();
    initialvaluetxt.setText(v.formatValue(vd.initialValue, true, true));
    lowerrangetxt.setText(v.formatValue(vd.minimum, true, true));
    higherrangetxt.setText(v.formatValue(vd.maximum, true, true));
    prevValText=v.formatValue(v.node.doubleGetVal(), true, true);
    int p=prevValText.indexOf(' ');
    unit=prevValText.substring(p+1);
    unittxt.setText(unit);
    if(p>0)currentvaluetxt.setText(prevValText.substring(0,p));
    //info
    StringBuffer s = new StringBuffer();
    //convert to conventional name
    String d = v.canonicalName.replace('/', '.');
    if (d.startsWith(".")) {
      d = d.substring(1);
      //retrive info string
    }
    String[] m = info.getSectionStrings(d);
    for (int i = 0; i < m.length; i++) {
      StringTokenizer st = new StringTokenizer(m[i]);
      while (st.hasMoreTokens()) {
        String word = st.nextToken();
        if (word.startsWith("*")) {
          word = word.substring(1);
        }
        try {
          VisibleVariable var = Variables.forName(word);
          if (var == v) {
            throw new IllegalArgumentException("same var");
          }
          word = "<A HREF=\"" +  word + "\">" + Resource.loader.identifierToText(var.longName) + "</A>";
          word += getHighLowString(var);
        }
        catch (IllegalArgumentException x) {}
        s.append( word + " " );
      }
      //s += "</P><P>";
      s.append( '\n' );
    }
    if(addControllers){
      Vector src = Current.body.getControllerList().forControlledVariable(v),
          targ = Current.body.getControllerList().forControllingVariable(v);
      if(src.size()>0){
        s.append("<HR> <H3>Controlled by</H3> ");
        for (int j = 0; j < src.size(); j++) {
          Controller c=(Controller)src.get(j);
          VisibleVariable cv = c.getControllingVariable();
          if(cv!=null) {
            if(j!=0) s.append(", ");
            s.append(getHTMLLink(cv));
          }
        }
      }
      if(targ.size()>0){
        s.append("<HR> <H3>Controls</H3>");
        for(int j=0;j<targ.size();j++){
          Controller c=(Controller)targ.get(j);
          VisibleVariable cv= c.getControlledVariable();
          if(cv!=null) {
            if(j!=0) s.append(", ");
            s.append(getHTMLLink(cv));
          }
        }
      }
    }
    infotext.setText("<HTML>" + s + "</HTML>");
    infotext.setCaretPosition(1);

    if (referencevaluespanel != null) {
      jPanel5.remove(referencevaluespanel);
    }
    referencevaluespanel = new ReferenceValuesPanel(v);
    jPanel5.add(referencevaluespanel, BorderLayout.CENTER);
    jPanel5.setBorder(new EmptyBorder(5,5,5,5));
    jTabbedPane1.setEnabledAt(2, referencevaluespanel.foundValues);
  }
  /** Convert a variable into a HTML string that links to the var */
  public String getHTMLLink(VisibleVariable v){
    return "<A HREF=\"" +  v.longName + "\">"
        + Resource.loader.identifierToText(v.longName) + "</A>"
        + getHighLowString(v);
  }
  public String getHighLowString(VisibleVariable v){
    double val = v.node.doubleGetVal();
    if(val>v.maximum) return "<I>(High)</I>";
    if(val<v.minimum) return "<I>(Low)</I>";
    return "";
  }

  public VisibleVariable getVariable() {
    return v;
  }
  String unit="",  prevValText = "";
  boolean addControllers=true;

  /** to parse the string as a quantity in the correct units */
  public void transferBack() {
    String s = currentvaluetxt.getText();
    if(!s.equals(prevValText)){
      double d=Double.parseDouble(s);
      v.node.doubleSetVal(d);
    }
  }
  JLabel unittxt = new JLabel();
}

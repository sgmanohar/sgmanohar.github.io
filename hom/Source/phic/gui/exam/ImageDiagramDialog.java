package phic.gui.exam;
import phic.gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import phic.*;
import phic.common.IniReader;
import javax.swing.event.*;

/**
 * a dialog to show ImageDiagrams.
 * The list on the left is taken from the 'Charts' property in ImagrDiagrams.txt,
 * And each item in the list creates a diagram according to the section of the
 * file with the given name.
 */

public class ImageDiagramDialog extends ModalDialog {
  public static final String
      DIAGRAM_FILE="ImageDiagrams.txt",
      SECTION_NAME="Pictures";
  JPanel mainpanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList jList1 = new JList();
  JPanel jPanel3 = new JPanel();
  JButton okbutt = new JButton();
  DefaultListModel lmodel = new DefaultListModel();
  IniReader ini = new IniReader(DIAGRAM_FILE);
  public ImageDiagramDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    String[] sections = Resource.loader.getStringList(DIAGRAM_FILE, SECTION_NAME);
    for(int j=0;j<sections.length;j++)
      lmodel.addElement(sections[j]);
    setSize(640,640);
    setTitle("Diagrams");
  }
  private void jbInit() throws Exception {
    mainpanel.setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    okbutt.setText("OK");
    okbutt.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){hide();}
    });
    jList1.setModel(lmodel);
    jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        jList1_valueChanged(e);
      }
    });
    mainpanel.setBackground(Color.white);
    this.getContentPane().add(mainpanel, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,  BorderLayout.WEST);
    jPanel2.add(jScrollPane1,  BorderLayout.CENTER);
    this.getContentPane().add(jPanel3,  BorderLayout.SOUTH);
    jPanel3.add(okbutt, null);
    jScrollPane1.getViewport().add(jList1, null);
  }
  ImageDiagram currentDiagram = null;
  String selval=null;
  void jList1_valueChanged(ListSelectionEvent e) {
    String s = jList1.getSelectedValue().toString();
    if(selval!=null && selval.equals(s)) return;
    ImageDiagram d;
    if(s!=null){
      d = new ImageDiagram(ini, s);
      d.refreshValues();
    }else{ d=null; }
    if(currentDiagram!=null){
      mainpanel.remove(currentDiagram);
    }
    selval=s;
    currentDiagram = d;
    if(d!=null){
      mainpanel.add(d);
    }
    mainpanel.doLayout();
    mainpanel.repaint();
  }
}

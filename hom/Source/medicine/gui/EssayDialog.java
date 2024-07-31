package medicine.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import medicine.*;

public class EssayDialog extends JDialog{
  JPanel jPanel1 = new JPanel();
  JEditorPane jEditorPane1 = new JEditorPane();
  BorderLayout borderLayout1 = new BorderLayout();
  Border border1;

  public EssayDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(300,300);
  }

  private Essay essay;

  public void setEntity(Entity e){
    essay=new Essay(e);
    jEditorPane1.setText(essay.getText());
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    this.setTitle("Essay");
    jEditorPane1.setEditable(false);
    jEditorPane1.setText("jEditorPane1");
    jPanel1.setLayout(borderLayout1);
    jPanel1.setBorder(border1);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jEditorPane1, BorderLayout.CENTER);
  }

}

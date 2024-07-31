package phic.gui;

import javax.swing.*;
import phic.common.*;
import phic.*;
import java.awt.*;
/**
 * Class to edit a set of variables in a panel.
 * Consists of a tabbedPane read from an ini file
 */

public class EditableScreensPanel extends JTabbedPane {
  IniReader ir;
  JPanel panel[];
  public EditableScreensPanel(String resource) {
    ir = new IniReader(resource);
    String[] sh = ir.getSectionHeaders();
    panel =  new JPanel[sh.length];
    for(int i=0;i<panel.length;i++){
      panel[i] = new JPanel();
      String[] var = ir.getSectionStrings(sh[i]);
      panel[i].setLayout(new BorderLayout());
      JPanel content = new JPanel();
      panel[i].add(content, BorderLayout.NORTH);
      content.setLayout(new GridLayout(var.length,0));
      for(int j=0;j<var.length;j++){
        try{ //add the default view for the node
          Node node=Node.findNodeByName(var[j]);
          NodeView nv = new NodeView(node, NodeView.Type.forNode(node)[0], null);
          content.add(nv);
        }catch(IllegalArgumentException e){
          System.out.println("Could not find node '"+var[j]+"' in section '"+sh[i]+"' of '"+resource+"'.");
          e.printStackTrace();
        }
      } //then add the tab
      add(panel[i], sh[i]);
    }
  }

}

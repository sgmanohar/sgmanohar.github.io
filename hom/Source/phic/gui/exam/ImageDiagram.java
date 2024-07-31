package phic.gui.exam;

import javax.swing.*;
import phic.common.IniReader;
import java.util.Map;
import java.awt.*;
import phic.*;
import java.util.Set;
import java.util.Iterator;
import phic.gui.*;
import java.util.Vector;
import medicine.Entity;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Display an image and with series of variable values superimposed
 * over it, as directed by a resource file.
 */

public class ImageDiagram extends JPanel implements Examination {
  Map map;
  Image image;
  static final String IMAGE_KEY = "Image";
  public ImageDiagram(IniReader iniFile, String sectionName) {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    // read the parameters for this image diagram
    map=iniFile.getSectionMap(sectionName);
    image = Resource.loader.getImageResource(  map.get(IMAGE_KEY).toString() );
    Set keys = map.keySet();
    for(Iterator i=keys.iterator();i.hasNext();){
      String s = (String)i.next();
      if(s.equals(IMAGE_KEY)) continue;
      try{
        VisibleVariable vv=null;
        try{
          vv = Variables.forName(s);
          nodes.add(vv);
        }catch(IllegalArgumentException x){
          Node n = Node.findNodeByName(s);
          nodes.add(n);
        }
        String params[] = map.get(s).toString().split(",");
        int x=Integer.parseInt(params[0].trim()), y=Integer.parseInt(params[1].trim());
        JLabel label = new JLabel();
        add(label);
        label.setLocation(x,y);
        label.setSize(label.getPreferredSize());
        if(vv!=null) {
          label.setToolTipText(Resource.identifierToText(vv.longName) +
                               " ("+vv.formatValue(vv.minimum,true,false)+
                               " - "+vv.formatValue(vv.maximum,true,false) +")");
        }
        jlabels.add(label);
        label.addMouseListener(labelMouseListener);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error at line '"+s+"' in image diagram "+sectionName);
      }
    }
  }
  /** Set the text of all the labels to the current values of the corresponding
   * variables */
  public void refreshValues(){
    for(int i=0;i<nodes.size();i++){
      Object o = nodes.get(i);
      Icon icon = null;
      String s="ERROR";
      if(o instanceof VisibleVariable){
        VisibleVariable vv=(VisibleVariable)o;
        double val=vv.node.doubleGetVal();
        s=vv.formatValue(val,true, true);
        icon=ThinNodeView.getIcon(val, vv.node.getVDouble());
      }else if(o instanceof Node){
        s=((Node)o).stringGetVal();
      }
      JLabel label = (JLabel)jlabels.get(i);
      label.setText(s);
      label.setIcon(icon);
      label.setSize(label.getPreferredSize());
    }
  }

  /** Returns a Node or VisibleVariable corresponding to the given JLabel */
  public Object getVariableForLabel(JLabel l){
    for(int i=0;i<jlabels.size();i++) if(l==jlabels.get(i)) return nodes.get(i);
    throw new IllegalArgumentException("Object "+l+" is not a label in this diagram");
  }

  public void paint(Graphics g){
    if(image!=null)g.drawImage(image, 0,0,this);
    super.paint(g);
  }

  /** Handle clicks from the labels */
  MouseListener labelMouseListener = new MouseAdapter(){
    public void mouseClicked(MouseEvent e){
      if(e.getSource() instanceof JLabel){
        Object o=getVariableForLabel((JLabel)e.getSource());
        if(o instanceof VisibleVariable){
          VariablePropertiesDialog vpd=new VariablePropertiesDialog();
          vpd.setVariable((VisibleVariable)o);
          vpd.show();
        }
      }
    }
  };

  /** The labels containing text representing the values of each variable */
  Vector jlabels = new Vector();

  /** The list of nodes or VisibleVariables pointing to the current values
   * of each variable.
   */
  Vector nodes = new Vector();


  private void jbInit() throws Exception {
    this.setLayout(null);
    this.setOpaque(false);
  }

  public JPanel createPanel() {
    return this;
  }

  public Entity[] getPathologies() {
    return null;
  }

  public Entity[] getSigns() {
    return null;
  }

  public double getUpdateFrequencySeconds() {
    return 60;
  }

  public void initialise(Body body) {
    this.refreshValues();
  }

}

package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Member;
import javax.swing.*;
import javax.swing.tree.*;
import phic.*;
import phic.common.ClassVisualiser;
import java.util.Map;
import java.util.HashMap;

/**
 * A panel with a JTree of the body and environment structures.
 * The data is obtained from the class visuliser objects of each node.
 */
public class BodyTree
    extends JPanel {
  DefaultMutableTreeNode root = new DefaultMutableTreeNode("HOM");
  /**
   * The graphical tree object. Nodes are added to it via the 'root' object.
   * Nodes are subclasses of javax.swing.tree.DefaultMutableTreeNode.
   */

  public JTree tree = new JTree();

  ActionListener al = null;
  /** Add an action listener, which will be called when users click on a node. */

  public void addActionListener(ActionListener a) {
    al = AWTEventMulticaster.add(al, a);
  }

  /** Remove an action listener previously installed with addActionListener(). */
  public void removeActionListener(ActionListener a) {
    al = AWTEventMulticaster.remove(al, a);
  }

  JScrollPane scrollpane = new JScrollPane();
  MyRenderer renderer = new MyRenderer();

  public BodyTree() {
    setLayout(new BorderLayout());
    setupNodes();
    renderer.setupIcons();
    scrollpane.getViewport().setView(tree);
    scrollpane.getVerticalScrollBar().setUnitIncrement(10);
    add(scrollpane);
    tree.setCellRenderer(renderer);
    tree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
          if (e.getClickCount() == 2) {
            Object o = selPath.getLastPathComponent();
            if (o instanceof Node) {
              Node n = (Node) o;
              String name = n.canonicalName();
              if (al != null) {
                al.actionPerformed(new ActionEvent(tree,
                    ActionEvent.ACTION_PERFORMED, name));
              }
            }
          }

        }
      }
    });
  }

  public Node getNodeForRow(int row) {
    Object o = tree.getPathForRow(row).getLastPathComponent();
    if (o instanceof Node) {
      return (Node) o;
    }
    else {
      return null;
    }
  }

  /** This creates a fresh tree from the current body and environment */
  void setupNodes() {
    root.removeAllChildren();
    setupNodes(Current.environment, "Environment");
    setupNodes(Current.body, "Body");
    tree.setModel(new DefaultTreeModel(root));
  }

  /** This adds the specified object to the tree, with the specified label. */
  void setupNodes(Object cn, String nn) {
    ClassVisualiser c;
    Node n;
    try {
      c = new ClassVisualiser(cn);
      c.name = nn;
      n = new LimitedNode(c, (DefaultMutableTreeNode) root);
      root.add(n);
      c.createTree(n);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void reload() {
    ( (DefaultTreeModel) tree.getModel()).reload();
  }
}

/** This paints nodes on the tree, with icons to represent the type */
class MyRenderer
    extends DefaultTreeCellRenderer {
  final String[] ifile = new String[] {
      "Method.gif", "Double.gif", "Tick.gif",
      "Cross.gif", "Organ.gif", "Container.gif"};

  ImageIcon[] icon = new ImageIcon[ifile.length];

  public MyRenderer() {
    for (int i = 0; i < ifile.length; i++) {
      icon[i] = new ImageIcon(Resource.loader.getImageResource(ifile[i]));
    }
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                boolean sel, boolean expanded,
                                                boolean leaf, int row,
                                                boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                                       hasFocus);
    if (value instanceof Node) {
      Node node = (Node) value;
      int o = node.getType();
      switch (o) {
        case Node.SIMPLE_METHOD:
          setIcon(icon[0]);
          break;
        case Node.DOUBLE:
          setIcon(icon[1]);
          break;
        case Node.BOOLEAN:
          boolean b = node.booleanGetVal();
          setIcon(b ? icon[2] : icon[3]);
          break;
        case Node.CONTAINER:
          setIcon(icon[5]);
          break;
        case Node.ORGAN:
          setIcon(icon[4]);
          break;
        default: // nil
      }
      if(o==Node.CONTAINER || o==Node.CURVE || o==Node.ORGAN || o==Node.GAS){
        Object t = ( (ClassVisualiser) node.member).object;
        Icon tmp;
        // look for exceptions to the rules:
        if((tmp=(Icon)classToIcon.get(t.getClass()))!=null)    setIcon(tmp);
      }
      VisibleVariable v = null;
      //user conventional names for tree?
//			if((v=Variables.forNode(node))!=null){
//				setText(v.shortName);}else
      if (node.getUserObject() instanceof Member) {
        setText( ( (Member) node.getUserObject()).getName());
      }
    }
    return this;
  }
  Map classToIcon = new HashMap();
  Object[][] classIcons = new Object[][]{
      {Body.class, "SmallMan16.gif"},
  };
  public void setupIcons(){
    for(int i=0;i<classIcons.length;i++)
      classToIcon.put(classIcons[i][0],
                      Resource.loader.getIconResource(classIcons[i][1].toString()));
  }
}

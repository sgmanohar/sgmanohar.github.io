package phic.gui;

import javax.swing.JComponent;

/**
 * The NodeView is a class that can either be subclassed from NewNodeView
 * or OldNodeView.
 * Old is fast + reliable, New is slow & buggy but looks better
 */

public class NodeView extends OldNodeView implements INodeView {
  public NodeView(Node n, Type t, CreateGraphTarget cgt) {
      super(n,t,cgt);
  }

  public NodeView(Node n, int v, CreateGraphTarget cgt) {
    super(n,v, cgt);
  }

  public static NodeView createNodeView(Node node, Type t, CreateGraphTarget cgt){

    return new NodeView(node, t, cgt);
  }
  public JComponent getComponent(){ return this; }
}

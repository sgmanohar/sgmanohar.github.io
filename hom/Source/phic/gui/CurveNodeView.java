package phic.gui;

import java.awt.Dimension;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sanjay Manohar</p>
 * <p>Company: CUDOS</p>
 * @author Sanjay Manohar
 * @version 1.0
 */

public class CurveNodeView extends NodeView {
  public CurveNodeView(Node node, Type type, CreateGraphTarget cgt) {
    super(node,type,cgt);
    add(panel);
    panel.setCurveNode(node);
    setPreferredSize(new Dimension(100,100));
  }
  public void addNotify(){
    super.addNotify();
    HorizontalBar.addBar(panel);
  }
  public void removeNotify(){
    super.removeNotify();
    HorizontalBar.removeBar(panel);
  }
  public CurveViewPanel panel = new CurveViewPanel();
}

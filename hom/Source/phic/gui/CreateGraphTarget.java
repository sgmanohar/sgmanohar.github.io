package phic.gui;

import phic.common.VDouble;

/**
 * Describes a component that can have graphs. These methods
 * allow graphs to be added and removed, and sets the NodeView that matches
 * individual graphs.
 *
 * Thus, allows NodeViews to interface with the component that produces the
 * graphs, adding or removing them as necessary.
 * Currently implemented by HorzScrollGraph,
 * and methods used by ThinNodeView.
 */
public interface CreateGraphTarget {
  /** Add a variable to the graph component, given a Visible Variable */
  public void addNewVariable(VisibleVariable v);
  /**
   * Add a variable to the graph component, given a VDouble (
   * useful for DrugConcentrations)
   */
  public void addNewVariable(VDouble v, String name);
  /** Add a variable to the graph component given a Node */
  public void addNewVariable(Node v);
  /** Remove a graph from the graphing component */
  public void remove(VisibleVariable v);
  /** Remove a graph from the graphing component */
  public void remove(VDouble v);
  /**
   * Determine the colour of the requested node. This method is used by a NodeView
   * who wants to match an interface item to the colour of the graph it represents.
   *
   * @param node Node the node that represents the variable whose colour is needed
   * @return Color the Colour in which the graph of the given variable is being
   * drawn.
   */
  public java.awt.Color getColor(Node node);
  /**
   * Sets the ThinNodeView item which shows the value of the graphed variable.
   * This value is used by the graph component to pass back information such as
   * selection, mouse-over, and also whe the graph is removed from the display
   * by the user.
   *
   * @param vv VisibleVariable the variable whose corresponding ThinNodeView to set.
   * @param t ThinNodeView
   */
  public void setThinNodeView(VisibleVariable vv, ThinNodeView t);
  public ThinNodeView getThinNodeView(VisibleVariable vv);
}

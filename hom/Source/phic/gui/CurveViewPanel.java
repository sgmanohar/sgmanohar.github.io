package phic.gui;

import javax.swing.*;
import phic.common.Curve;
import java.awt.Graphics;
import java.util.Vector;
import java.awt.Color;
import phic.common.VDouble;
import phic.Resource;
import phic.common.UnitConstants;
import phic.common.Quantity;
import phic.modifiable.Range;
import phic.common.Ticker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import phic.common.*;

/**
 * Plot a variable node's value against an ordinate calculated
 * from a 'Curve' node
 */

public class CurveViewPanel extends GraphPaper implements Ticker{
  public CurveViewPanel() {
    setLayout(null);
    setForeground(new Color(0,128,0));
  }
  public void setDescription(String s){
    setToolTipText(s);
  }
  /**
   * The node that points to the Curve object. A curve object contains a function
   * to convert an x-value into a y-value.
   */
  Node curveNode;

  /**
   * A vector of Nodes representing points to be plotted. Note that these
   * nodes must be numerical doubles.
   * The double value represents the x- or y-value on the curve.
   */
  Vector pointNodes = new Vector();
  /** A vector of Colors representing the color of each point to be plotted */
  Vector pointColors = new Vector();
  /**
   * A vector of Booleans representing whether the node's value is to be
   * plotted on the Y axis.
   */
  Vector pointAxes = new Vector();
  Vector pointNames = new Vector();

  public void addPointNode(final Node n, Color c, boolean axis){
    pointNodes.add(n);
    pointColors.add(c);
    pointAxes.add(new Boolean(axis));
    //create label as component
    JLabel l = new JLabel();
    add(l);
    l.setSize(5,5);
    l.setBackground(c);
    l.setOpaque(true);
    l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    l.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
        try{
          VisibleVariable vv=Variables.forNode(n);
          VariablePropertiesDialog d=new VariablePropertiesDialog();
          d.setVariable( vv ) ; d.show();
        }catch(Exception x){}
    }});
    pointComponents.add(l);
    String name = Resource.identifierToText(n.canonicalName());
    try{name = Resource.identifierToText(Variables.forNode(n).longName); }catch(Exception x){}
    pointNames.add(name);
    //scale to point
    Range xr= new Range(minx,maxx), yr=new Range(miny,maxy);
    if(pointNodes.size()==1){ xr.set(0,0); yr.set(0,0); }
    if(n instanceof VDoubleNode){
      VDouble vd = ((VDoubleNode)n).vDouble;
      if (axis) {
        yr.include(vd.minimum,vd.maximum);
        setYRange(yr.minimum,yr.maximum);
        for(double z=vd.minimum;z<=vd.maximum;z+=(vd.maximum-vd.minimum)/100)
          xr.include(curve.getInverse(z));
        setXRange(xr.minimum, xr.maximum);
      }else{
        xr.include(vd.minimum,vd.maximum);
        setXRange(xr.minimum,xr.maximum);
        for(double z=vd.minimum;z<=vd.maximum;z+=(vd.maximum-vd.minimum)/100)
          yr.include(curve.getValue(z));
        if(curve instanceof GasConc.DissocCurve) yr.maximum = 0.120; //special case
        setYRange(yr.minimum,yr.maximum);
      }
    }
  }

  Curve curve;
  public void setCurveNode(Node n){
    curveNode=n;
    curve = (Curve)curveNode.objectGetVal();
  }
  int[] curvePoints  = new int[getWidth()];

  public void tick(double t){
    if(curvePoints.length!=getWidth()) curvePoints  = new int[getWidth()];
    for(int i=0;i<getWidth();i++){
      curvePoints[i] = yS(curve.getValue(xG(i)));
//System.out.println(xG(i)+"\t:"+curve.getValue(xG(i)));
    }
    for(int i=0;i<pointNodes.size();i++){
      JLabel l = (JLabel)pointComponents.get(i);
      Node n = (Node)pointNodes.get(i);
      boolean isY = ((Boolean)pointAxes.get(i)).booleanValue();
      double val = n.doubleGetVal(), x,y;
      if(isY){
        y=val; x=curve.getInverse(val);
      }else{
        x=val; y=curve.getValue(val);
      }
      l.setLocation(xS(x)-2, yS(y)-2);
      String name=(String)pointNames.get(i);
      if(n instanceof VDoubleNode)
        l.setToolTipText(name +" = "+((VDoubleNode)n).vDouble.formatValue(true,false));
      else l.setToolTipText(name +" = "+Quantity.toString(val));
    }
    repaint();
  }
  Vector pointComponents= new Vector();

  Color curveColor = Color.yellow;

  public void paint(Graphics g){
    super.paint(g);
    g.setColor(curveColor);
    for(int i=1;i<curvePoints.length;i++){
      g.drawLine(i-1,curvePoints[i-1],i,curvePoints[i]);
    }

    for(int i=0;i<pointNodes.size();i++){
      Node n = (Node)pointNodes.get(i);
      Color c = (Color)pointColors.get(i);
      boolean isY = ((Boolean)pointAxes.get(i)).booleanValue();
      g.setColor(c);
      double val = n.doubleGetVal(), x,y;
      int o;
      if(isY){
//        y=val; x=curve.getInverse(val);
        g.drawLine(0, o=yS(val), xS(curve.getInverse(val)), o);
      }else{
//        x=val; y=curve.getValue(val);
        g.drawLine(o=xS(val),getHeight(),o,yS(curve.getValue(val)));
      }
//      g.drawRect(xS(x)-2,yS(y)-2, 4,4);
    }

   super.paintComponents(g);
  }
}

package phic.gui;

import java.lang.reflect.*;
import phic.common.VDouble;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This extension of Node allows the properties of a VDouble to be easily
 * accessed, without having to use (VDouble)objectGetVal with a risky cast.
 * All VisibleVariables contain a reference to a VDoubleNode
 */
public class VDoubleNode extends LimitedNode {
  public VDoubleNode(Member nonstaticMember,Object object,
                     DefaultMutableTreeNode parent) {
    super(nonstaticMember, object, parent);
    if(member instanceof Field){
      Field f = (Field)member;
      if(VDouble.class.isAssignableFrom(f.getType())){
        try{
          vDouble = (VDouble) f.get(object);
        }catch(IllegalAccessException x){x.printStackTrace();}
      }
    }
    if(vDouble == null) throw new IllegalArgumentException(object+"'s "+nonstaticMember
        +" does not represent a VDouble");
  }
  protected VDouble vDouble;
  /**
   * Return the VDouble represented by this Node.
   */
  public VDouble getVDouble(){ return vDouble; }
}

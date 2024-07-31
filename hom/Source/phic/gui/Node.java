package phic.gui;
import java.lang.reflect.*;
import java.util.*;
import phic.common.*;
import phic.*;
import javax.swing.tree.*;

import phic.common.*;
import java.io.IOException;

/**
 * Node is the interface between the Phic classes and the hierarchical skeleton
		 * that contains them. Each node on the tree is effectively a pointer to a member
 * of an object of one of the phic.* classes. The node is then able to communicate
		 * get() and set() operations to Phic from the user and from the interface, using
 * reflection. Each of the many access operations may only be used on certain kinds
 * of node.
 *
 * 24/10/3 made abstract; now use LimitedNode for all instances of nodes, to allow
 * the ability to hide or show specific nodes in the tree.
 */
public abstract class Node extends DefaultMutableTreeNode{
	/** The member within the object to which the node refers. */
	Member member;
	/** The object in the phic tree which contains the member referred to by this node. */

	Object object;
	/** Constructor for static fields -- Now redundant? */

	public Node(Member staticMember){
		super(staticMember,true);
		member=staticMember;
		object=null;
		allNodes.add(this);
	}

	/** Constructor for nonstatic  members. The object must be be of the class
	 * that matches the member. If not, an exception is thrown later, when
	 * the node is accessed. */
	public Node(Member nonstaticMember,Object object){
		super(nonstaticMember,true);
		member=nonstaticMember;
		this.object=object;

		allNodes.add(this);
	}

	/** A list to which all created nodes are added */
	protected static Vector allNodes=new Vector();

	//interface
	// The node type tags
	/** Non-visual nodes */
	public static final int NON_VISUAL=0;

	/** Simple methods are methods with no parameters and void return type.
	 * They can be invoked by the framework, and contain no value */
	public static final int SIMPLE_METHOD=1;

	/** Double nodes are of several types. The first is simple double primitives
	 * that are public class members, eg. public double pH;
	 * Secondly, nodes may be Objects that implement Variable, whose get()
	 * and set() methods are called. eg. public PDouble pH=new PDouble();
	 * Thirdly, the node may be just a method whose return type is double, and
	 * which takes no parameters, eg. public double pH();
	 * Double nodes are accessed with doubleSetVal() and doubleGetVal().
	 * @see phic.common.Variable
	 */
	public static final int DOUBLE=2;

	/** Boolean nodes represent primitive boolean values in the phic framework.
	 * eg. public boolean active=true; */
	public static final int BOOLEAN=3;

	/** Container type nodes are special types of node. The member actually
	 *  is an instance of ClassVisualiser for that particular container,
	 *  and thus the tree is allowed to introspect a further level. */
	public static final int CONTAINER=4;
        /**
         * Organ nodes are also special, with instances of ClassVisualiser for the
         * item, allowing introspection.
         */
        public static final int ORGAN=5;
        /**
         * Curve - denotes a Curve object, which also may have content variables
         * that describe the shape of the curve
         */
        public static final int CURVE=6;
        /**
         * Gas - denotes an object of type Gas, which contains the concentrations
         * of O2, CO2 etc in the gas.
         */
        public static final int GAS=7;

	/** returns one of NON_VISUAL, SIMPLE_METHOD, DOUBLE, BOOLEAN, CONTAINER */
	public int getType(){
		if(member instanceof ClassVisualiser){
                  Object t = ((ClassVisualiser)member).object;
                  if(t instanceof phic.common.Container)  return CONTAINER;
                  if(t instanceof phic.common.Organ)      return ORGAN;
                  if(t instanceof phic.common.Curve)      return CURVE;
                  if(t instanceof phic.common.Gas)        return GAS;
		}
		if(member instanceof Method){
			Method m=(Method)member;
			if(m.getReturnType()==Void.TYPE){
				return SIMPLE_METHOD;
			}
			if(m.getReturnType()==Double.TYPE){
				return DOUBLE;
			}
		} else if(member instanceof Field){
			Field f=(Field)member;
			if(f.getType()==Double.TYPE){
				return DOUBLE;
			}
			if(ClassVisualiser.is(f.getType(),Variable.class)){
				return DOUBLE;
			}
			if(f.getType()==Boolean.TYPE){
				return BOOLEAN;
			}
		}
		return NON_VISUAL;
	}
        /** Converts the value into a readable string */
        public String stringGetVal(){
          int t= getType();
          if(t==SIMPLE_METHOD) return canonicalName();
          if(t==BOOLEAN) return Boolean.toString( booleanGetVal() );
          if(t==CONTAINER) {
            return  ((Container)objectGetVal()).toString();
          }
          if(t==DOUBLE){
            if (member instanceof Field) {
              Field f = (Field) member;
              if (f.getType() == Double.TYPE)return Quantity.toString(doubleGetVal());
              try{
                if (ClassVisualiser.is(f.getType(), VDouble.class))return ( (VDouble)
                    f.get(object)).formatValue(true, false);
              }catch(IllegalAccessException e){e.printStackTrace();}
              if(ClassVisualiser.is(f.getType(),Variable.class))return Quantity.toString(doubleGetVal());
            }else if(member instanceof Method){
              return Quantity.toString(doubleGetVal());
            }
          }
          return "Unknown value";
        }

	//accessing values of variables
	/** Gets the value of the node as an object. Valid, for example, on
	 * Container nodes. */
	public Object objectGetVal(){
		try{
			if(member instanceof Field){
				return((Field)member).get(object);
			} else if(member instanceof Method){
				return((Method)member).invoke(object,null);
			} else if(member instanceof ClassVisualiser){
				return((ClassVisualiser)member).object;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
  public void objectSetVal(Object value){
    try{
      if(member instanceof Field){
        ((Field)member).set(object,value);
      } else {
        System.out.println("Cannot set node "+canonicalName()+" to "+value.toString());
      }
    } catch(Exception e){
      e.printStackTrace();
    }
	}

	public boolean booleanGetVal(){
		try{
			return((Boolean)((Field)member).get(object)).booleanValue();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public void booleanSetVal(boolean b){
		try{
			((Field)member).set(object,new Boolean(b));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

        /**
         * This must be called from within a CriticalPeriod to ensure the value
         * is not mid-calculation.
         */
        public double doubleGetVal(){
		try{
			if(member instanceof Field){
				Field f=(Field)member;
				if(f.getType()==Double.TYPE){
					return f.getDouble(object);
				}
				if(ClassVisualiser.is(f.getType(),Variable.class)){
					return((Variable)f.get(object)).get();
				}
			} else if(member instanceof Method){
				return((Double)((Method)member).invoke(object,
						new Object[0])).doubleValue();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return Double.NaN;
	}

	public void doubleSetVal(double d){
		try{
			if(member instanceof Field){
				Field f=(Field)member;
				if(f.getType()==Double.TYPE){
					f.setDouble(object,d);
				}
				if(ClassVisualiser.is(f.getType(),Variable.class)){
					((Variable)f.get(object)).set(d);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/** Valid on nodes which are methods with no parameters. */
	public void methodInvoke(){
		try{
			((Method)member).invoke(object,new Object[0]);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/** Is the variable corresponding to this node settable? */
	public boolean isSettable(){
		if(member instanceof Field){
			Field f=(Field)member;
			if(f.getType()==Double.TYPE){
				return true;
			}
			if(f.getType()==Boolean.TYPE){
				return true;
			}
			if(VDoubleReadOnly.class.isAssignableFrom(f.getType())){
				return false;
			}
			if(Variable.class.isAssignableFrom(f.getType())){
				return true;
			}
		}
		return false;
	}

	/** This sets the 'initial value' of a node. Valid for nodes that
	 * can have initial value, i.e. phic.VDouble.
	 * If the node object is not an instance of VDouble, the call is ignored.
	 * @see phic.common.VDouble */
	public void vdoubleSetRanges(double initialValue,double minimum,
			double maximum){
		Object o=objectGetVal();
		if(VDouble.class.isInstance(o)){
			((VDouble)o).setRanges(initialValue,minimum,maximum);
		}
	}

	public String canonicalName(){
		TreeNode[] n=getPath();
		String s="";
		Node a=null;
		for(int i=0;i<n.length;i++){
			if(n[i] instanceof Node){
				a=(Node)n[i];
				if(a.member!=null){
					s+='/'+a.member.getName();
				}
			}
		}
		return s;
	}
        public String canonicalNameReplaced(){
          String cp=canonicalName();
          cp=cp.replace('/','.');
          if(cp.startsWith(".")) cp=cp.substring(1);
          if(cp.startsWith("B")) cp="b"+cp.substring(1);
          if(cp.startsWith("E")) cp="e"+cp.substring(1);
          return cp;
        }

	/**
         * This finds a node by its name. It handles node names with a delimiter
         * of '/' or '.', and searches within body and environment.
	 * @todo Currently the allNodes static is not altered when saving and
	 * loading files
         * @throws IllegalArgumentException if cannod find the node.
	 */
	public static Node findNodeByName(String pathname){
          pathname = pathname.replace('/','.');
          if(pathname.startsWith("."))pathname=pathname.substring(1);
          for(int i=0;i<allNodes.size();i++){
            Node n=(Node)allNodes.get(i);
            String name=n.canonicalName().replace('/','.');
            if(name.startsWith(".")){
              name=name.substring(1);
            }
            if(name.equals(pathname)){
              return n;
            }
            if(name.startsWith("Body")&&name.length()>5){
              name=name.substring(5);
              if(name.equals(pathname)){
                return n;
              }
              name="body."+name;
              if(name.equals(pathname)){
                return n;
              }
            } else if(name.startsWith("Environment")&&name.length()>12){
              name=name.substring(12);
              if(name.equals(pathname)){
                return n;
              }
              name="environment."+name;
              if(name.equals(pathname)){
                return n;
              }
            }
          }
          throw new IllegalArgumentException("No such node, "+pathname);
        }

        /**
         * This gets the name of the actual ultimate leaf that is read. E.g., for
         * body.blood.arterial.PO2, it returns "get".
         */

        public String getLeafName(){
          return member.getName();
        }

        /** Load in the alias names for nodes */
        static final Properties aliases = new Properties();
        {try{aliases.load(Resource.loader.getResource("NodeAlias.txt"));}catch(Exception e){e.printStackTrace();}}

  /**
   * getFriendlyName gets a name that is either derived from the nodes path, or
   * if it is a visible variable, from the long name.
   *
   * @return String - the friendly name of the node.
   */
  public String getFriendlyName() {
    try{
      VisibleVariable v = Variables.forName(canonicalName());
      return Resource.identifierToText( v.longName );
    }catch(IllegalArgumentException e){
      String r = aliases.getProperty(canonicalName());
      if(r!=null && r.length()>0) return r;
      return Resource.identifierToText( getLeafName() );
    }
  }
}

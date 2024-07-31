package phic.common;
import java.lang.reflect.*;
import java.util.*;
import phic.Current;
import phic.gui.*;
import javax.swing.tree.*;
/**
 * ClassVisualiser
 * Allows the simple methods and fields of a class to be accessed
 * via vectors, and creating a view of them.
 * contains get/set methods to view or alter the fields
 * creates the tree nodes for the variables
 *
 * it is up to the class user to ensure static/instance-bound classes
 * are called appropriately: you should not call the static-type
 * constructor for an object with non-static methods or fields.
 *
 * This class, along with the Node class, is the main point of contact between
 * the interface and the simulation engine.
 */
public class ClassVisualiser implements Member{
	Class c;

	Vector simpleMethods=new Vector();

	Vector doubleFields=new Vector();

	Vector booleanFields=new Vector();

	Vector containers=new Vector();

	public Object object;

	/** constructor for instance wrapper */
	public ClassVisualiser(Object o) throws Exception{
		object=o;
		initialise(o.getClass());
	}

	/** constructor for static wrapper */
	public ClassVisualiser(String className) throws Exception{
		object=null;
		try{
			initialise(Class.forName(className));
		} catch(ClassNotFoundException e){
			Current.body.error("Cannot find class "+className);
		}
	}

	public ClassVisualiser(Class cls) throws Exception{
		object=null;
		initialise(cls);
	}

	/** initaliser called by all public constructors */
	private void initialise(Class cls) throws Exception{
		c=cls;
		Method[] m=c.getMethods();
		for(int i=0;i<m.length;i++){
			if(m[i].getParameterTypes().length==0
					&&Modifier.isPublic(m[i].getModifiers())
					&&!isDefaultMethod(m[i].getName())){
				if(m[i].getReturnType().equals(Void.TYPE)){
					simpleMethods.add(m[i]);
				} else if(m[i].getReturnType().equals(Double.TYPE)){
					doubleFields.add(m[i]);
				}
			}
		}
		Field[] f=c.getFields();
		for(int i=0;i<f.length;i++){
			Class d=f[i].getType();
			if(d==Double.TYPE){
				doubleFields.add(f[i]);
			} else if(d==Boolean.TYPE){
				booleanFields.add(f[i]);
			} else if(is(d,Variable.class)){
				doubleFields.add(f[i]);
			} else if(is(d,HasContent.class) /*|| is(d, Vector.class)*/){
				ClassVisualiser nv=new ClassVisualiser(f[i].get(object));
				nv.name=f[i].getName();
				containers.add(nv);
			}
		}
	}

	// class identity
	public static boolean is(Class a,Class b){
		Class c=a;
		while(c!=Object.class&&c!=null){
			if(c==b){
				return true;
			}
			if(isinterface(c,b)){
				return true;
			}
			c=c.getSuperclass();
		}
		return false;
	}

	static boolean isinterface(Class a,Class b){
		Class[] c=a.getInterfaces();
		for(int i=0;i<c.length;i++){
			if(c[i]==b){
				return true;
			}
		}
		return false;
	}

	public boolean isDefaultMethod(String m){
		return(m.equals("notify")||m.equals("notifyAll")||m.equals("wait")
				||m.equals("run"));
	}

	//node creation
	public void createTree(Node n){
		createNodes(n,simpleMethods);
		createNodes(n,doubleFields);
		createNodes(n,booleanFields);
		createNodes(n,containers);
		//added 25-11-02
		if(object instanceof Vector){
			createNodes(n,(Vector)object);
		}
	}

	void createNodes(Node n,Vector v){ //add each element found to the tree
		for(Enumeration e=v.elements();e.hasMoreElements();){
			Object o=e.nextElement();
			if(o instanceof Member){
				//LimitedNode next=new LimitedNode((Member)o,object, (DefaultMutableTreeNode)n); //create the graphical node from data
        LimitedNode next = LimitedNode.createOrFind((Member)o,object, (DefaultMutableTreeNode)n);
				n.add(next);
				if(o instanceof ClassVisualiser){
					((ClassVisualiser)o).createTree(next);
				}
			}
		}
	}

	//interface
	public String name="Visualiser";

	public Class getDeclaringClass(){
		return ClassVisualiser.class;
	}

	public int getModifiers(){
		return 0;
	}

	public String getName(){
		return name;
	}

	@Override
	public boolean isSynthetic() {
		return false;
	}
}

/**
 * Properties Panel
 * Display an object's fields as properties in a tree
 * Can recurse objects
 *
 */
package sanjay;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
import javax.swing.Timer;

public class PropertiesPanel extends JPanel {
	JSplitPane jSplitPane1 = new JSplitPane();
	BorderLayout borderLayout1 = new BorderLayout();
	JTree tree = new JTree();
	BorderLayout borderLayout2=new BorderLayout();
	JPanel mainpanel=new JPanel();
	JPanel tablecontainer=new JPanel();
	BorderLayout borderLayout3=new BorderLayout();
	BorderLayout borderLayout4=new BorderLayout();
	JPanel tablepanel=new JPanel(){public void paint(Graphics g){
		if(needsRefresh){refreshValues();needsRefresh=false;repaint();} super.paint(g);
	}};
	BoxLayout boxLayout1=new BoxLayout(tablepanel,BoxLayout.Y_AXIS);



	boolean needsRefresh=false;

	/** Regular updates ? */

	protected boolean regularUpdate=false;
	public void setRegularUpdate(boolean u){
		regularUpdate=u;
		if(u==false) timer.stop(); else timer.start();
	}
	public boolean getRegularUpdate(){return regularUpdate;}
	protected Timer timer=new Timer(400,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			for(int i=0;i<tablepanel.getComponentCount();i++){
				Component c=tablepanel.getComponent(i);
				if(c instanceof sanjay.EditorCell) ((sanjay.EditorCell)c).updateDisplay();
			}
		}
	});

	public PropertiesPanel() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static JComponent createPanel(Object root){
		PropertiesPanel p=new PropertiesPanel();
		p.setRootObject(root, root.toString());
		return p;
	}
	public static JFrame createFrame(Object root){
		JFrame f=new JFrame();
		JScrollPane s=new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		f.getContentPane().add(s);
		f.setSize(250,200);
		s.setViewportView(createPanel(root));
		f.show();
		return f;
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		tree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent e) {
				treeChanged();
			}
			public void treeCollapsed(TreeExpansionEvent e) {
				treeChanged();
			}
		});
		this.add(mainpanel);
		tablecontainer.setLayout(borderLayout4);
		mainpanel.setLayout(borderLayout2);
		mainpanel.add(jSplitPane1, BorderLayout.CENTER);
		tablepanel.setLayout(boxLayout1);
		tablecontainer.add(tablepanel, BorderLayout.NORTH);
		jSplitPane1.add(tree, JSplitPane.LEFT);
		jSplitPane1.add(tablecontainer, JSplitPane.RIGHT);
		jSplitPane1.setDividerLocation(150);


		tree.setRowHeight(16);
	}

	public void setRootObject(Object o, String name){
		TVector vectors=new TVector();
		vectors.createFromObject(o, name);
		DefaultMutableTreeNode root = new JTree.DynamicUtilTreeNode(vectors,vectors);
		TreeModel treeModel=new DefaultTreeModel(root);
		tree.setModel(treeModel);
		refreshValues();
	}



	public void refreshValues(){
		tablepanel.removeAll();
		for(int i=0;i<tree.getRowCount();i++){
			Object o=getTreeObject(i);
			JComponent c=null;
			if(o instanceof TLeaf){
				sanjay.EditorCell ec=new sanjay.EditorCell();
				ec.setLeaf((TLeaf)o);
				ec.addActionListener(delegator);
				c=ec;
			}else{
				c=new JLabel(" ");
			}
			c.setPreferredSize(new Dimension(c.getPreferredSize().width,
					16 /*tree.getRowHeight()*/ /*c.getPreferredSize().height*/ ));
			tablepanel.add(c);
		}
		tablecontainer.validate();tablepanel.repaint();
	}


	Object getTreeObject(int row){
		TreePath p=tree.getPathForRow(row);
		if(p==null)return null;
		Object o=p.getLastPathComponent();
		if(o instanceof JTree.DynamicUtilTreeNode)
			o=((JTree.DynamicUtilTreeNode)o).getUserObject();
		return o;
	}

	private PropertyChangeListener pcl=null;
	public void setPropertyChangeListener(PropertyChangeListener p){pcl=p;}


	int excludeModifiers= Modifier.STATIC | Modifier.FINAL;
	boolean noRecursion=false;
	static Vector seenObjects=new Vector();

	/**
	 * This class is a list of TLeaf items (the accessible variables) and nested
	 * TVectors that contain more TLeaf items.
	 */
	class TVector extends Vector{
		String name;
		Object object;
		public String toString(){
			return name;
		}
		/**
		 * Recursively fill this list from an object
		 */
		public void createFromObject(Object o, String name){
			seenObjects.removeAllElements();
			recursiveCreateFromObject(o,name);
		}

		/**
		 * From the given object, retreves any accessible members by reflection,
		 * and if an object property is not of a default type, reflects the
		 * contents of this object too by a reentrant call.
		 */
		protected void recursiveCreateFromObject(Object o, String name){
			this.name=name;
			this.object=o;
			if(seenObjects.contains(o) || o==null) return;
			seenObjects.add(o);

			Field[] fields=o.getClass().getFields();
			for(int i=0;i<fields.length;i++){
				if((fields[i].getModifiers() & excludeModifiers) >0)continue;
				Class type=fields[i].getType();
				if(type.isPrimitive() || type.isAssignableFrom(java.lang.String.class)
					|| sanjay.EditorCell.classHasEditor(type) || noRecursion){
					//create a leaf node
					TLeaf item=new TLeaf();
					item.parent=o;
					item.member=fields[i];
					item.pcl=pcl;
					this.add(item);
				}else{
					TVector item=new TVector();
					//cascade recursive constructing of new TVector
					try{
						Object newParent=fields[i].get(o);
						if(newParent!=null)
							item.recursiveCreateFromObject(newParent, fields[i].getName());
						this.add(item);
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}
	}

	void treeChanged() {
		needsRefresh=true;
	}



	//Action listener
	/** Delegate all actions performed on this listener to the added listeners */
	class Delegator implements ActionListener{
		ActionListener al;
		public void addActionListener(ActionListener a){
			al=AWTEventMulticaster.add(al,a);
		}
		public void removeActionListener(ActionListener a){
			al=AWTEventMulticaster.remove(a,al);
		}
		public void actionPerformed(ActionEvent e){
			if(al!=null) al.actionPerformed(e);
		}
	}
	Delegator delegator = new Delegator();
	public void addActionListener(ActionListener al){
		delegator.addActionListener(al);
	}
	public void removeActionListener(ActionListener al){
		delegator.removeActionListener(al);
	}
}







	class TLeaf{
		Object parent;
		Member member;
		String name;
		PropertyChangeListener pcl;
		public String toString(){
			return member.getName();
		}
		Object get(){
			if(parent!=null){
				if(member instanceof Field){
					try{
						return ((Field)member).get(parent);
					}catch(Exception e){e.printStackTrace();}
				}else{
					return null;
				}
			}
			return null;
		}
		void set(Object o){
			if(parent!=null){
				if(member instanceof Field){
					try{
						((Field)member).set(parent,o);
					}catch(Exception e){e.printStackTrace();}
				}else{
				}
			}
			//fire property change
			if(pcl!=null)pcl.propertyChange(new PropertyChangeEvent(this, name, null, o));
		}
		void setNumeric(double d){
			if(member instanceof Field){
				Class objclass=((Field)member).getType();
				if(objclass.isAssignableFrom(Double.TYPE))
					set(new Double(d));
				else if(objclass.isAssignableFrom(Float.TYPE))
					set(new Float(d));
				else if(objclass.isAssignableFrom(Integer.TYPE))
					set(new Integer((int)d));
				else if(objclass.isAssignableFrom(Long.TYPE))
					set(new Long((long)d));
			}else{
			}
		}
	}

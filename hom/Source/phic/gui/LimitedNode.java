package phic.gui;
import phic.common.*;
import javax.swing.tree.*;
import java.lang.reflect.*;
import java.util.*;
import java.lang.reflect.Member;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This extension of Node allows only a limited set of nodes to be displayed
 * in the tree.
 */
public class LimitedNode extends Node{

	public LimitedNode(Member staticMember,DefaultMutableTreeNode parent){
		super(staticMember);
		realParent=parent;
		checkVisibility();
	}

	/**
	 *  Constructor for nonstatic  members. The object must be be of the class
	 * that matches the member. If not, an exception is thrown later, when
	 * the node is accessed.
	 */
	public LimitedNode(Member nonstaticMember,Object object,
		DefaultMutableTreeNode parent){
		super(nonstaticMember,object);
		realParent=parent;
		checkVisibility();
	}

	/**
	 * Keep a record of which node is the actual parent of this node. This is
	 * needed when a node is removed from the tree (i.e. is made invisible) and
	 * then needs to be reinstated.
		 * It must be an instance of DefaultMutableTreeNode to allow adding and removing.
	 */
	public DefaultMutableTreeNode realParent;

	static IniReader list=new IniReader("VisibleNodes.txt");

	static boolean fullVisibility=true;

	static Vector visibleNodes;

	static{		setFullVisibility(); }

	protected void checkVisibility(){
	}

	/**
	 *
	 * @param section
	 */
	public static void setVisibleNodesDelayed(final String section){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setVisibleNodes(section);}
		});
	}

	/**
	 * Given a section header, find the list of variables that can be displayed,
	 * and add only those to the tree. All other nodes will be hidden.
	 * Note to callers: this method does not update the actual display. To do that, the
	 * JTree's tree-changed event should be fired.
	 *
	 * @param section the section header from the file VisibleNotes.txt
	 * that specifies the variables that are displayed in the tree.
	 */
	public static void setVisibleNodes(String section){
          visibleNodesSection = section;
          setupVisibleNodes();
	}

        /**
         * Restrict the visible nodes to those given by the currently
         * selected section header. This should be called if the nodes are loaded
         * from scratch, and need to be re-restricted.
         */
        public static void setupVisibleNodes(){
          if(visibleNodesSection==null) {setFullVisibility(); return;}
		System.out.println("Setting visibility to '"+visibleNodesSection+"'...");
		fullVisibility=false;
		visibleNodes=new Vector();
		String[] varnames=list.getSectionStrings(visibleNodesSection);
		for(int i=0;i<Node.allNodes.size();i++){ //Go through each node in the tree
			LimitedNode n=(LimitedNode)Node.allNodes.get(i);
			boolean found=false;
			for(int j=0;j<varnames.length;j++) //Compare it with each item in the list
				if(n.nameEquals(varnames[j]))
					found=true;
			if(found){ //If it matches one,
				n.ensureConnected(); //flag this node and all ancestors as needed
			} else{ //else
				//remove the node from the tree if it has not been flagged as needed
				if(!visibleNodes.contains(n))
					n.removeFromParent();
			}
		}
		System.out.println("Visibility set to '"+visibleNodesSection+"'.");
	}



	/**
	 * Check the variable's name against the possible ways of writing this node's
	 * name.
	 * @param s the string to test
	 * @return true if the node has the name given by the string s
	 */
	public boolean nameEquals(String s){
    String cname =canonicalName();
		if(cname.equalsIgnoreCase(s))
			return true;
		VisibleVariable vv=Variables.forNode(this);
		if(vv!=null){
			if(vv.canonicalName.equalsIgnoreCase(s)||vv.longName.equalsIgnoreCase(s)||
				vv.shortName.equals(s))
				return true;
		}
		//try{    if(findNodeByName(s)==this) return true; }
		//catch(IllegalArgumentException e){ }
    if(canonicalName().indexOf('/')>=0){
      String n = cname.replace('/','.');
      if(n.charAt(0)=='.')n=n.substring(1);
      if(n.equalsIgnoreCase(s))			return true;
    }
		return false;
	}

	/**
	 * Make all the nodes in the tree visible
	 */
	public static void setFullVisibility(){
		fullVisibility=true;
                if(visibleNodes==null)visibleNodes=new Vector();
		for(int i=0;i<Node.allNodes.size();i++){ //Go through each node in the tree
			LimitedNode n=(LimitedNode)Node.allNodes.get(i);
			n.ensureConnected(); //and ensure it is displayed
		}
	}

	/**
		 * If a node is set to be displayed, this method ensures that all its ancestor
	 * nodes are also displayed (i.e. eventually connected to the root node)
	 * It flags the node and its ancestors as needed by adding the node to the
	 * vector 'visibleNodes'.
	 */
	protected void ensureConnected(){
		DefaultMutableTreeNode testNode=this;
		visibleNodes.add(this);
		while(testNode!=null&&!testNode.isRoot()){
			if(testNode.getParent()==null)
				realParent.add(this);
			visibleNodes.add(realParent);
			if(testNode instanceof LimitedNode)
				testNode=((LimitedNode)testNode).realParent;
			else
				testNode=null;
		}
	}

	public boolean isVisible(){
          if(visibleNodes==null)return true;
		return visibleNodes.contains(this);
	}

	/**
	 * Returns a node representing the given object. If one already exists, it
	 * returns the existing Node (and sets the parent if necessary). Otherwise,
	 * a new node is created to represent the given object.
		 * This method is called from the ClassVisualiser, to produce nodes representing
	 * every item in the hierarchy.
	 * @param o
	 * @param object
	 * @param n
	 * @return
	 */
	public static LimitedNode createOrFind(Member member,Object object,
		DefaultMutableTreeNode parent){
		for(int i=0;i<Node.allNodes.size();i++){
			LimitedNode node=(LimitedNode)Node.allNodes.get(i);
			if((node.member.getName().equalsIgnoreCase(member.getName()))&&
				(node.object==object)){
				node.realParent=parent;
				return node;
			}
		}
		return new LimitedNode(member,object,parent);
	}



        /** The section header in "VisibleNodes.txt" that indicates which nodes are visible */
        public static String visibleNodesSection=null;

}

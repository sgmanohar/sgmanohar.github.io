package medicine;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Repeating entity crawler: A class to scan each node in the database,
 * starting from a single entity. Unlike the EntityCrawler, each node may be
 * traversed more than once, and the path taken by the crawler is in the
 * directions specified.
 *
 * It is possible to specify a maximum number of times an entity may be traversed.
 */

public class RepeatingEntityCrawler extends Thread{

	public RepeatingEntityCrawler() {
	}



	Entity start;
	ActionListener actionlistener;

	public RepeatingEntityCrawler(Entity startNode, ActionListener al) {
		start=startNode;
		actionlistener=al;
	}


	Vector searched=new Vector();
	Vector traversedCount=new Vector();
	int serial=0;
	boolean searching=false;
	int branchMask=Entity.CAUSE | Entity.EFFECT | Entity.CHILD | Entity.PARENT;
	public int getBranchMask(){return branchMask;}
	public void setBranchMask(int b){branchMask=b;}

	/**
		Initialise crawler loop
	*/
	public void run(){
		searching=true;
		searchTree(start, 0);
		searching=false;
		actionlistener.actionPerformed(new ActionEvent(this, -1,null));
	}

	/**
	 * Keeps track of how many branches away from the start entity we are.
	 */
	int currentDistance = 0;
	int MAX_DISTANCE = Integer.MAX_VALUE;
	int maxTraverseCount = 1;

	/**
	 * recursive tree crawling loop
	 * follows all branches from a root
	 */

	public void searchTree(Entity node, int directionFrom){
		//  synchronise in case we want to use multithreaded search
		synchronized(searched){
			//mark the node as searched / increment traversed count
			int i = findNode(node, searched);
			int count = ((Integer)traversedCount.get(i)).intValue();
			traversedCount.set(i,new Integer( count ++ ));
			if(count > maxTraverseCount) return;

			//inform client of entity
			actionlistener.actionPerformed(
				new ActionEvent(node, serial++, node.name, currentDistance)
			);
		}

		if(currentDistance > MAX_DISTANCE) return;
		currentDistance ++;

		//four-way branch:
		if(directionFrom != Entity.CAUSE && (branchMask & Entity.CAUSE) > 0)
			trySearchAll(node.causes, Entity.EFFECT);
		if(directionFrom != Entity.EFFECT && (branchMask & Entity.EFFECT) > 0)
			trySearchAll(node.effects, Entity.CAUSE);
		if(directionFrom != Entity.CHILD && (branchMask & Entity.CHILD) > 0)
			trySearchAll(node.children, Entity.PARENT);
		if(directionFrom != Entity.PARENT && (branchMask & Entity.PARENT) > 0)
			trySearchAll(node.parents, Entity.CHILD);

		currentDistance --;
	}
	/**
	 * to traverse a list, check if each element has been searched, and if not,
	 * call searchTree again
	 */
	public void trySearchAll(Vector v, int directionFrom){
		Object o;
		for(int i=0;i<v.size();i++){
			o=v.get(i);
			searchTree((Entity)o, directionFrom);
		}
	}

	/**
	 * Return the index of the node in the vector.
	 *
	 * If this is the first time the node has been traversed, add a new
	 * element to the vector, and also add the count (zero) to traversedCount.
	 */
	private int findNode(Entity node, Vector vector){
		int i = vector.indexOf(node);
		if(i<0){
			vector.add(node);
			traversedCount.add(new Integer(0));
			i = vector.indexOf(node);
		}
		return i;
	}
	public int getMaxTraverseCount() {
		return maxTraverseCount;
	}
	public void setMaxTraverseCount(int maxTraverseCount) {
		this.maxTraverseCount = maxTraverseCount;
	}
}
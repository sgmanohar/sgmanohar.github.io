
/**
 * Entity crawler: A class to scan each node in the database, starting from a
 * single entity.
 */
package medicine;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class EntityCrawler extends Thread{

	Entity start;
	ActionListener actionlistener;

	public EntityCrawler(Entity startNode, ActionListener al) {
		start=startNode;
		actionlistener=al;
	}


	Set searchedset=new HashSet();
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
		searchTree(start);
		searching=false;
		actionlistener.actionPerformed(new ActionEvent(this, -1,null));
	}

	/**
	 * Keeps track of how many branches away from the start entity we are.
	 */
	int currentDistance = 0;
	int MAX_DISTANCE = Integer.MAX_VALUE;

	/**
	 recursive tree crawling loop
	 follows all branches from a root
	*/

	public void searchTree(Entity node){
		//  synchronise in case we want to use multithreaded search
		synchronized(searchedset){
			//mark the node as searched
			searchedset.add(node);
			//inform client of entity
			actionlistener.actionPerformed(
				new ActionEvent(node, serial++, node.name, currentDistance)
			);
		}

		if(currentDistance > MAX_DISTANCE) return;
		currentDistance ++;

		//four-way branch:
		if((branchMask & Entity.CAUSE) > 0) trySearchAll(node.causes);
		if((branchMask & Entity.EFFECT) > 0) trySearchAll(node.effects);
		if((branchMask & Entity.CHILD) > 0) trySearchAll(node.children);
		if((branchMask & Entity.PARENT) > 0) trySearchAll(node.parents);

		currentDistance --;
	}
	/**
	 to traverse a list, check if each element has been searched, and if not,
	 call searchTree again
	*/
	public void trySearchAll(Vector v){
		Object o;
		for(int i=0;i<v.size();i++){
			if(!searchedset.contains(o=v.get(i)))
				searchTree((Entity)o);
		}
	}

}

/**
 * Searches for entities which contain, or are exactly equal to, a specified
 * string. The results are returned to an actionlistener, item by item. The
 * event source object is the Entity.
 *
 * When the search is completed, an dummy action event is generated, with
 * source = this EntitySearcher, id=-1. If the stop() method is called, the search
 * terminates early, and this dummy terminator item is not returned.
 */
package medicine;

import java.awt.event.*;
import java.util.*;

public class EntitySearcher implements Runnable{

	ActionListener al;
	Entity startNode;
	Thread thread=new Thread(this,"Searcher_"+toString());
	boolean searching=false;
	String searchstring;

	public boolean caseSensitive=false;
	public boolean exactMatch=false;


	public EntitySearcher(String searchstring, Entity startNode, ActionListener al) {
		this.al=al;
		this.startNode=startNode;
		this.searchstring=searchstring;
	}
	public void run(){
		searching=true;
		try{
			searchTree(startNode);
			al.actionPerformed(new ActionEvent(this, -1,null));
		}catch(StopSearch ss){}
		searching=false;
	}
	public void start(){thread.start();}
	public synchronized void stop(){running=false;}

	Set searchedset=new HashSet();
	Vector found=new Vector();
	int serial=0;
	boolean running=true;

	void assertRunning()throws StopSearch{if(!running)throw new StopSearch();}


	/**
	 * recursive tree search loop
	 * searches all branches from a root
	 */
	public void searchTree(Entity node) throws StopSearch{
		assertRunning();
		//select the appropriate way to check this node
		boolean found;
		if(caseSensitive){
			if(exactMatch) found=node.equals(searchstring);
			else found=node.contains(searchstring);
		}else{
			if(exactMatch) found=node.equalsIgnoreCase(searchstring);
			else found=node.containsIgnoreCase(searchstring);
		}


		//  synchronise in case we want to use multithreaded search
		synchronized(searchedset){
			assertRunning();
		  //mark the node as searched
		  searchedset.add(node);

		  //inform the user if it has been found
		  if(found)foundEntity(node);
		}

		//four-way branch:
			assertRunning();
		trySearchAll(node.causes);
			assertRunning();
		trySearchAll(node.effects);
			assertRunning();
		trySearchAll(node.children);
			assertRunning();
		trySearchAll(node.parents);
	}
	/**
	 * to search a list, check if each element has been searched, and if not,
	 * call searchTree again
	 */
	public void trySearchAll(Vector v) throws StopSearch{
		Object o;
		for(int i=0;i<v.size();i++){
			if(!searchedset.contains(o=v.get(i)))
				searchTree((Entity)o);
		}
	}

	/**
	 * Add the found item to the list and notify the listener
	 */
	public void foundEntity(Entity e){
		found.add(e);
		al.actionPerformed(new ActionEvent(e, serial++, e.name));
	}

}

/** Internal exception thrown in the search thread when stop() method is called */
class StopSearch extends Exception{};

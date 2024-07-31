
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine;

import java.io.*;
import java.util.*;

public class Entity implements Serializable{

	static int serial=0;
	public static final int PARENT=1, CHILD=2, CAUSE=4, EFFECT=8;
	static int relationList[]= new int[]{CAUSE, EFFECT, PARENT, CHILD};


	public Entity(Entity from, int connection) {
		children=new Vector();
		parents=new Vector();
		causes=new Vector();
		effects=new Vector();

		if(from!=null){
			connect(from, connection);
		}

		synonyms=new Vector();
		name="Entity"+serial++;
		description="";
  	}

	/**
		eg. listOf(PARENT) returns parent.
	*/
	public Vector listOf(int relation){
		switch(relation){
			case PARENT: return parents;
			case CHILD:  return children;
			case CAUSE:  return causes;
			case EFFECT: return effects;
		}
		return null;
	}
	public static int inverseOf(int reciprocalRelation){
		switch(reciprocalRelation){
			case PARENT: return CHILD;
			case CHILD:  return PARENT;
			case CAUSE:  return EFFECT;
			case EFFECT: return CAUSE;
		}
		return 0;
	}
	/**
	 e.g. A.connect(B, PARENT) means
	 A.parents.add(B);  B.children.add(A)
	*/
	public void connect(Entity to, int connectAs){
		Vector mylist=listOf(connectAs);
		if(mylist.indexOf(to)>=0)return;	//already connected!
		listOf(connectAs).add(to);
		to.listOf(inverseOf(connectAs)).add(this);
	}
	public void disconnect(Entity from, int relation){
		//check that this is not the only connection!
		if(numConnections()<2){System.out.println("Cannot delete last connection");return;}

		listOf(relation).remove(from);
		from.listOf(inverseOf(relation)).remove(this);
	}
	public String toString(){return name;}



		//SERIALISED MEMBERS

	public Vector children, parents, causes, effects,
		synonyms;
	public String name;
	public String description;



	/**
	 * Check if equal to name or any of the synonyms
	 */
	public boolean equals(String s){
		if(name.equals(s))return true;
		for(int i=0;i<synonyms.size();i++){
			if(s.equals((String)synonyms.get(i)))return true;
		}
		return false;
	}
	public boolean equalsIgnoreCase(String s){
		if(name.equalsIgnoreCase(s))return true;
		for(int i=0;i<synonyms.size();i++){
			if(s.equalsIgnoreCase((String)synonyms.get(i)))return true;
		}
		return false;
	}
	public boolean contains(String s){
		if(name.indexOf(s)>=0) return true;
		for(int i=0;i<synonyms.size();i++){
			if(((String)synonyms.get(i)).indexOf(s)>=0)return true;
		}
		return false;
	}
	public boolean containsIgnoreCase(String s){
		if(indexOfIgnoreCase(name,s)>=0)return true;
		for(int i=0;i<synonyms.size();i++){
			if(indexOfIgnoreCase( (String)synonyms.get(i), s )>=0) return true;
		}
		return false;
	}

	int indexOfIgnoreCase(String main, String sub){
		for(int k=0;k<=main.length()-sub.length();k++){
			if(main.substring(k, k + sub.length()).equalsIgnoreCase(sub))
				return k;
		}
		return -1;
	}


	/**
	 * Is the object blank -- i.e. does it have connections other than its
	 * original one?
	 */
	public boolean isBlank(){
		return synonyms.isEmpty() && numConnections()<2 && description.equals("") ;
	}



	/**
	 * Replaces any uses of the current entry with the replacement entry,
	 * leaving this entry disconnected & henceforth discardable
	 */
	public void replaceAllWith(Entity replacement){
		for(int i=0;i<relationList.length;i++){
			int rel=relationList[i];
			Vector v=listOf(rel);
			for(int j=0;j<v.size();j++){
				Entity dest=(Entity)v.get(j);
				replacement.connect(dest, rel);
				dest.disconnect(this, inverseOf(rel));
			}
		}
	}

	/**
	 * Count total number of links this object has with other objects
	 */
	int numConnections(){
		return causes.size()+effects.size()+parents.size()+children.size();
	}



}

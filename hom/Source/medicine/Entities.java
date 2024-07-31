
package medicine;

import java.io.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.tree.*;

/**
 * General utilities for use with tht class Entity
 */

public class Entities {

	public Entities() {
	}


	Vector entities;

	ActionListener completionListener;
	boolean isLooking=false;
	Entities thisEntities=this;
	public synchronized void setVector(Entity start, ActionListener onComplete){
		isLooking=true;
		entities=new Vector();
		completionListener=onComplete;
		EntityCrawler ec=new EntityCrawler(start, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand()==null){
					isLooking=false;
					completionListener.actionPerformed(new ActionEvent(thisEntities, -1, null));
				}
				if(e.getSource() instanceof Entity){
					Entity entity=(Entity)e.getSource();
					entities.add(entity);
				}
			}
		});
		ec.start();
	}
	public void setVector(Vector v){entities=v;}
	public Vector getVector(){return entities;}

	public void writeTextForm(OutputStream os){
		if(isLooking)throw new Error("Search not completed");
		PrintStream out=new PrintStream(os);
		synchronized (this){
			for(int i=0;i<entities.size();i++){
				Entity ent=(Entity)entities.get(i);
				writeTextForm(out,ent);
			}
		}
	}

	/**
		format of output:


		Pulmonary fibrosis{
			Synonyms { Lung fibrosis, Fibrosis of lung }
			Children { Apical lung fibrosis, Basal lung fibrosis }
			Description {"Scarring and fibrous change of the
		pulmonary interstitium"}
			Parents { Lung pathology }
		}
		Aortic stenosis{
			...
		}

	*/

	void writeTextForm(PrintStream out, Entity e){
		out.println(e.name+" {");

		if(e.synonyms.size()>0)
			out.println("\tSynonyms {" + getDelimitedNames(e.synonyms,", ") + "}");
		if(e.causes.size()>0)
			out.println("\tCauses {" + getDelimitedNames(e.causes,", ") + "}");
		if(e.effects.size()>0)
			out.println("\tEffects {" + getDelimitedNames(e.effects,", ") + "}");
		if(e.parents.size()>0)
			out.println("\tParents {" + getDelimitedNames(e.parents,", ") + "}");
		if(e.children.size()>0)
			out.println("\tChildren {" + getDelimitedNames(e.children,", ") + "}");
		if(!e.description.equals(""))
			out.println("\tDescription {\"" + e.description.replace('{','(')
                                    .replace('}',')') + "\"}");

		out.println("}");
	}



        public static Entity readTextForm(InputStream is) throws IOException{
          // BufferedReader br=new BufferedReader(fr);
          InputStreamReader fr = null;
          EntityData data=null;
          try {
            fr = new InputStreamReader(is);
            data = new EntityData();
            while (true) {
              readEntity(data, fr);
            }
          }          catch (EOF e) {   /** End of file encountered */    }
            if (fr != null) fr.close();
            if(data==null)return null;
            if(data.size()==0)return null;
            return data.getFirstEntity();
         }

         /** Merge two input streams in text format */
         public static Entity mergeTextFromStreams(InputStream is, InputStream is2) throws IOException{
           InputStreamReader fr = null;
           EntityData data = null;
           try {
             fr = new InputStreamReader(is);
             data = new EntityData();
             while (true) {
               readEntity(data, fr);
             }
           } catch (EOF e) { /** End of file encountered */}
           if (fr != null) {fr.close(); fr=null; }
           if (data == null) data=new EntityData();
           try {
             fr = new InputStreamReader(is2);
             while (true) {
               readEntity(data, fr);
             }
           } catch (EOF e) { /** End of file encountered */}
           if (fr != null) fr.close();
           if (data == null)return null;
           if (data.size()==0)return null;
           return data.getFirstEntity();
       }

        /**
         * readEntity - reads an entity form the stream into the data
         *
         * @param data EntityData
         * @param fr FileReader
         */
        private static void readEntity(EntityData data, Reader r) throws IOException, EOF{
          StringBuffer nameb=new StringBuffer();
          int ch;
          while((ch=r.read())!='{' && ch!=-1) nameb.append((char)ch);
          if(ch==-1)throw new EOF();
          String name=nameb.toString().trim();
          Entity e=data.findEntityExact(name);
          if(e==null){
            e= data.addNewEntity(name);
          }
          try{
            while (true) readSection(e,data,r);
          }catch(EOE ex){}
        }
        /**
         * readSection -reads a list of causes, effects, synonyms etc. from an entity
         *
         * @param e Entity
         * @param data EntityData
         */
        private static void readSection(Entity e, EntityData data, Reader r) throws EOE, IOException{
          StringBuffer nameb=new StringBuffer();
          int ch;
          while((ch=r.read())!='{' && ch!='}' && ch!=-1) nameb.append((char)ch);
          if(ch=='}')throw new EOE();
          if(ch==-1)throw new EOF();
          String name=nameb.toString().trim();
          if(name.equals("Causes"))readListTillCloseBracket(e.causes,data,r,true);
          if(name.equals("Effects"))readListTillCloseBracket(e.effects,data,r,true);
          if(name.equals("Parents"))readListTillCloseBracket(e.parents,data,r,true);
          if(name.equals("Children"))readListTillCloseBracket(e.children,data,r,true);
          if(name.equals("Synonyms"))readListTillCloseBracket(e.synonyms,data,r,false);
          if(name.equals("Description")){
            StringBuffer desc=new StringBuffer();
            while((ch=r.read())!='}' && ch!=-1) desc.append((char)ch);
            if(ch==-1)throw new EOF();
            String d=desc.toString().trim();
            if(d.startsWith("\""))d=d.substring(1,d.length()-1);
            mergeDescriptions(e,d);
          }
        }

        private static void readListTillCloseBracket(Vector v, EntityData data,Reader r, boolean convertToEntity) throws IOException, EOF{
          StringBuffer s=new StringBuffer();
          int ch;
          while((ch=r.read())!='}'){
            if(ch==-1)throw new EOF();
            if(ch==','){
              storeEntity(s, v, data,convertToEntity);
            }else  s.append((char)ch);
          }storeEntity(s,v,data,convertToEntity);
        }
        private static void storeEntity(StringBuffer s,Vector v,EntityData d,boolean convertToEntity){
          String n = s.toString().trim();
          if(convertToEntity){
            Entity e = d.findEntityExact(n);
            if(e==null){
              e=d.addNewEntity(n);
              v.addElement(e);
            }else{
              if(!v.contains(e)) v.addElement(e);
            }
            s.setLength(0);
          }else{
            if(!v.contains(s.toString().intern())) v.addElement(s.toString().trim());
            s.setLength(0);
          }
        }
        static class EOF extends IllegalStateException{}
        static class EOE extends IllegalStateException{}

        private static void mergeDescriptions(Entity e, String d){
          if(e.description.equals(d))return;
          if(e.description.startsWith(d)) return;
          if(d.startsWith(e.description)) {e.description=d;return;}
          else e.description += '\n'+d;
        }



	String getDelimitedNames(Vector v,String delimiter){
		StringBuffer list = new StringBuffer();
		for(int i = 0; i < v.size(); i++){
			Object o = v.get(i);
                        String s=o.toString();
                        s=s.replace('{','(').replace('}',')');
			list.append( o.toString() );
			if(i < v.size()-1) list.append( delimiter );
		}
		return list.toString();
	}

/*
        Entity readTextForm(InputStream p,Entity startFrom) throws IOException{
          StreamTokenizer st=new StreamTokenizer(new InputStreamReader(p));
          st.quoteChar('"');
          st.eolIsSignificant(false);
          String name=readUntil(st,"{");
          getSpecificNamedEntity(name,startFrom);
          return null;//incomplete
        }
*/
        /**
         * read from st until the string c is returnec as a token. this is not
         * included in the resuld.
         */
        String readUntil(StreamTokenizer st,String c) throws IOException{
        StringBuffer s=new StringBuffer();
        int tt;
        do{
          tt=st.nextToken();
          if(tt==st.TT_WORD)
          s.append(st.sval);
        }while(!st.sval.equals(c));
        return s.toString().trim();
      }

	/**
	 *  Lists all causes of an entity
	 *  Use: Vector v=getAllCauses(currentEntity, null);
	*/
	public static Vector getAllCauses(Entity entity, Vector except){
		if(except!=null && except.contains(entity))return null;
		if(except==null)except=new Vector();
		Vector v=new Vector();
		except.add(entity);
		for(int i=0;i<entity.causes.size();i++){
			Entity e=(Entity)entity.causes.get(i);
			if(except==null || !except.contains(e)){
				Vector ve=Entities.getAllCauses(e,v);
				if(ve!=null){
					if(ve.size()>0)v.addAll(ve);
					else ve.add(e);
				}
			}
		}
		return v;
	}
	public static Vector getCauseHierarchy(Entity entity, Vector complete){
		if(complete!=null && complete.contains(entity))return null;
		if(complete==null)complete=new Vector();
		Vector v=new Vector();
		complete.add(entity);
		for(int i=0;i<entity.causes.size();i++){
			Entity e= (Entity)entity.causes.get(i);
			if(complete==null || !complete.contains(e)){
				Vector add=Entities.getCauseHierarchy(e, complete);
				if(add!=null)v.add(add);
			}
		}
		return v;
	}
	public static int numConnections(Entity e){
		return e.causes.size()+e.effects.size()+e.parents.size()+e.children.size();
	}



	/**
	 * This is a class that dynamically creates tree nodes representing relations
	 */

	public static class DynamicRelationNode extends DefaultMutableTreeNode{
		Entity entity;
		int relations;
		public DynamicRelationNode(Entity e, int relations){
			super(e);
			entity=e;
			this.relations = relations;
			setAllowsChildren(true);
		}
		boolean loadedChildren = false;
		public boolean isLeaf(){return !getAllowsChildren();}


		/**
		 * Subclassed to load the children if necessary.
		 */
		public int getChildCount(){
			if( !loadedChildren ) loadChildren();
			return super.getChildCount();
		}
		public TreeNode getChildAt(int index) {
			if(!loadedChildren)	loadChildren();
			return super.getChildAt(index);
		}
		public Enumeration children() {
			if(!loadedChildren)	loadChildren();
			return super.children();
		}
		protected void loadChildren() {
				loadedChildren = true;
				createChildren(this, entity, relations);
		}


		public static void createChildren(DefaultMutableTreeNode n, Entity entity, int relations){
			boolean viaParent = (relations & Entity.PARENT) >0 ;
			boolean viaChild = (relations & Entity.CHILD) >0;
			int relation = -1;
			if((relations & Entity.CAUSE) > 0) relation = Entity.CAUSE;
			else if((relations & Entity.EFFECT) > 0)relation = Entity.EFFECT;
			if(relation == -1) return;

			Vector rels = entity.listOf(relation);
			fillWithVector(n,rels, relations);

			if(viaParent){
				Vector p = entity.listOf(Entity.PARENT);
				for(int i=0;i<p.size();i++){
					fillWithVector(n, ((Entity) p.get(i)).listOf(relation), relations );
				}
			}
			if(viaChild){
				fillWithVector(n, entity.listOf(Entity.CHILD), relations);
			}
			n.setAllowsChildren( n.getChildCount()>0 );
		}
		protected static void fillWithVector(DefaultMutableTreeNode n, Vector v, int r){
			if(v==null || n==null)return;
			for(int i=0;i<v.size();i++)	n.add(new DynamicRelationNode( (Entity)v.get(i), r ));  //recurse
		}
	}

	/**
	 * Recursively find whether the given queryItem is a child of 'parent'.
	 */
	public static boolean isChildOf(Entity queryItem, Entity parent){
		Vector p = queryItem.parents;
		if(p.indexOf(parent) >= 0) return true;
		for(int i=0;i<p.size();i++){
			Entity nquery = (Entity)p.get(i);
			if( isChildOf(nquery, parent) ) return true;
		}
		return false;
	}

	public Hashtable namedEntities = new Hashtable();
	/**
	 * Get an entity by name. This call is slow and blocking, unless the entity
	 * has already been found once.
	 * @param any a node to start searching from, if the entity has not yet been found.
	 */
	public Entity getSpecificNamedEntity(String name){
		Object o = namedEntities.get(name);
		if(o != null) return (Entity)o;
		else{
			Entity entity = findNamedEntityExact(name, (Entity)entities.get(0));
			namedEntities.put(name, entity);
			return entity;
		}
	}
        /**
         * Get an entity by name. This call is slow and blocking.
         * @param any a node to start searching from.
         */
        public static Entity getSpecificNamedEntity(String name, Entity any){
                        Entity entity = findNamedEntityExact(name, any);
                        return entity;
        }

        /**
         * Traverse the parents, using only the first element in the list of
         * parents on each level; and return the topmost entity
         */
        public static Entity getUltimateParents(Entity e){
          if(e.parents.size()==0)return null;
          Entity p=(Entity)e.parents.elementAt(0);
          while(p.parents.size()>0){
            p=(Entity)p.parents.elementAt(0);
          }
          return p;
        }

	/**
	 * This is a slow blocking call to search the tree for an exactly named entity.
	 * Do not run from Event thread!
	 */
	private static Entity findNamedEntityExact(final String name, Entity startNode)
				throws AmbiguityException {
		final Vector results = new Vector();
		EntityCrawler ec = new EntityCrawler(startNode, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getSource() instanceof Entity){
					Entity entity = (Entity)e.getSource();
					if(entity.name.equalsIgnoreCase(name))    results.add(entity);
				}
			}
		}	);
		ec.start();
		try{ ec.join(); }catch(Exception e){e.printStackTrace();}
		if(results.size() < 1) return null;
		if(results.size() > 1) throw new AmbiguityException("There are "+results.size()
			+ "entities named " + name);
		return (Entity)results.get(0);
	}
        /**
         * Find whether the 'putativeRelative' is a 'relation' of 'queryEntity'.
         * If 'traverseParents' is true, and 'relation' is CAUSE or EFFECT, then
         * this will return true if 'putativeRelative' is a 'relation' of any
         * parent of any 'relation' of 'queryEntity'.
         */
        public static boolean isRelativeOf(Entity queryEntity, Entity putativeRelative,
             int relation, boolean traverseParents, boolean traverseChildren,
             int maxRecursionDepth){
          if(maxRecursionDepth<=0)return false;
          if(queryEntity==putativeRelative) return true;
          Vector v=queryEntity.listOf(relation);
          for(int i=0;i<v.size();i++){
            if(v.get(i)==putativeRelative) return true; //is it directly related?
          }
          for(int i=0;i<v.size();i++){
            Entity e=(Entity)v.get(i); //is it related to any of the relations?
            if(isRelativeOf(e, putativeRelative, relation,traverseParents,
                            traverseChildren,maxRecursionDepth-1)) return true;
          }
          if(relation==Entity.CAUSE || relation==Entity.EFFECT) {
            if(traverseParents){ //is it related to any of the parents?
              for (int i = 0; i < queryEntity.parents.size(); i++) {
                Entity t = (Entity) queryEntity.parents.get(i);
                if (isRelativeOf(t, putativeRelative, relation, traverseParents,
                                 traverseChildren,maxRecursionDepth-1))return true;
              }
            }
            if(traverseChildren){ //is it related to any of the children?
              for (int i = 0; i < queryEntity.children.size(); i++) {
                Entity t = (Entity) queryEntity.children.get(i);
                if (isRelativeOf(t, putativeRelative, relation, traverseParents,
                                 traverseChildren,maxRecursionDepth-1))return true;
              }
            }
          }
          return false;
        }



  public static class AmbiguityException extends RuntimeException{
		public AmbiguityException(String s){	super(s);	}
		public AmbiguityException(){ }
	}
}

package evaluator;


import java.lang.reflect.*;
import java.util.StringTokenizer;

	// ObjectPath.class


	//uses java reflection to find an object
	//addresed by object.member

	//We do not check the return type of reflected objects at
	//parse time. If the object is not of the required type
	//a MathError will be generated on evaluation.

	//The tree is only traversed during parse time, and as such,
	//the object which contains the last method/field in the tree
	//is fixed at parse time.

public abstract class ObjectPath{
	Object object;
	Object member;

		//these objects are used as root nodes when
		//finding java objects (optional)
	static Object[] root=new Object[]{
		Math.class
	};
	static String[] packages=new String[]{
		"java.lang"
	};
	static PathHelper[] pathHelper=new PathHelper[0];
	static public void setRoot(Object[] rroot){root=rroot;}
	static public Object[] getRoot(){return root;}
	static public void setPackage(String[] rpackages){packages=rpackages;}
	static public String[] getPackage(){return packages;}
	static public void setPathHelper(PathHelper[] helpers){pathHelper=helpers;}
	static public PathHelper[] getPathHelper(){return pathHelper;}
        static public void appendRoot(Object o){ /** Append the object to the list of roots */
          for(int i=0;i<root.length;i++){ if (root[i].equals(o)) return; }
          Object[] nroot=new Object[root.length+1];
          for(int i=0;i<root.length;i++) nroot[i]=root[i];
          nroot[root.length]=o;
          root=nroot;
        }


		// find a java object from a static context
		// and parse up to the name of the field or method
		// specified in the path string
	void createPath(String path) throws ParseException{

			//first try all root objects
		for(int i=0;i<getRoot().length;i++){
			StringTokenizer st=new StringTokenizer(path,".");
			Object o=getRoot()[i];
				//is it an object?
			if(find(st,o,o.getClass()))break;
			st=new StringTokenizer(path,".");
				//or a class for static members?
			if(o instanceof Class) if(find(st,null,(Class)o))break;
		}
			//then try start on a static member/method
		if(member==null){
			String[] packages=getPackage();
			boolean found=false;
			for(int i=0;i<packages.length;i++){
				try{
					StringTokenizer st=new StringTokenizer(path,".");
					String token=st.nextToken();
					Class cls=Class.forName(packages[i]+'.'+token);
					if(!(found=find(st, null , cls))){continue;}
				}catch(ClassNotFoundException e){continue;}
				catch(NoClassDefFoundError e){continue;}
				break;
			}
			if(!found){	//ask each path helper to find the object
				for(int i=0;i<pathHelper.length;i++){
					member=pathHelper[i];
					object=pathHelper[i].findObjectFromPath(path);
					if(object!=null){found=true;break;}
				}
			}
			if(!found) throw new ParseException("Cannot find object "+path);
		}
	}

		//Pass a string tokenizer whose tokens are member names
		//starting in newobject, OR if newobject is null,
		//cls represents the class of whom st's first token
		//is a static member
	boolean find(StringTokenizer st, Object newobject, Class cls){
		try{
			while(st.hasMoreElements()){
				object=newobject;
				String token=st.nextToken();
				try{	//find any fields
					Field field=cls.getField(token);
					member=field;
					if(st.hasMoreElements()) newobject=field.get(object);
				}catch(Exception e){
					try{	//find any 0- or 1-parameter methods
						Method method;
						try{method=cls.getMethod(token, null);}
						catch(NoSuchMethodException ex){
							try{method=cls.getMethod(token,new Class[]{Double.TYPE});}
							catch(NoSuchMethodException exx){
								method=cls.getMethod(token,new Class[]{Double.TYPE, Double.TYPE});
							}
						}
						member=method;
						if(st.hasMoreElements())
							newobject=method.invoke(object, null);
					}catch(Exception ex){
						throw new ParseException("Can't find "+token+" in "+object);
					}
					if(newobject==null && st.hasMoreElements())
						throw new ParseException("Null object "+token+" in "+object);
				}
				if(newobject!=null)cls=newobject.getClass();
			}
			if(member==null) throw new ParseException("Error finding object "+st);
		}catch(ParseException e){
			object=null;
			return false;
		}
		return true;
	}

	/**
	 * Try to return the value of this object; only valid if it represents
	 * a field or a method that takes zero parameters.
	 */
	public Object get() throws NeedsParametersException, ParseException{
		if(member instanceof Field){
			Field f = (Field)member;
			try{ return f.get(object); }
			catch(IllegalAccessException x){
				throw new ParseException("Field "+object+"."+member+
					" not accessible");
			}
		}else if(member instanceof Method){
			Method m = (Method)member;
			if(m.getParameterTypes().length == 0)
				try{ return m.invoke(object, null); }
				catch(Exception x){
					throw new ParseException("Method "+object+"."+member+
						" cannot be invoked");
				}
			else throw new NeedsParametersException(object+"."+member+
				" needs parameters for invocation");
		}else if(member instanceof PathHelper){
			return object;
		}else return null;
	}
	public static class NeedsParametersException extends Exception{
		protected NeedsParametersException(String s){
			super(s);
		}
	}

	/**
	 * Find the type returned from this kind of object
	 */
	public Class getType(){
		if(member instanceof Field){
			return ((Field)member).getType();
		}else if(member instanceof Method){
			return ((Method)member).getReturnType();
		}else if(member instanceof PathHelper){
			return Double.class;
		}else	return null;
	}
}

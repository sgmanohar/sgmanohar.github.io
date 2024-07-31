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

	Member member;

	//these objects are used as root nodes when
	//finding java objects (optional)
        public Member getMember(){ return member; }
        public Object getObject(){ return object; }

	static Object[] root=new Object[0];

	static String[] packages=new String[]{"java.lang"
	};

	static public void setRoot(Object[] rroot){
		root=rroot;}

	static public Object[] getRoot(){
		return root;}

	static public void setPackage(String[] rpackages){
		packages=rpackages;}

	static public String[] getPackage(){
		return packages;}

	// find a java object from a static context
	// and parse up to the name of the field or method
	// specified in the path string
	void createPath(String path) throws ParseException{

		//first try all root objects
		for(int i=0;i<getRoot().length;i++){
			StringTokenizer st=new StringTokenizer(path,".");
			Object o=getRoot()[i];
			//is it an object?
			if(find(st,o,o.getClass()))
				break;
			st=new StringTokenizer(path,".");
			//or a class for static members?
			if(o instanceof Class)
				if(find(st,null,(Class)o))
					break;
                        st=new StringTokenizer(path,".");
                        //or an item from the VariableLookup table?
                        try{
                          Object t = Variable.namespaceLookup.getVariableValue(st.nextToken());
                          if (t != null) {
                            if (find(st, t, t.getClass()))break;
                          }
                        }catch(Exception e){}
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
					if(!(found=find(st,null,cls))){
						continue;}
				} catch(ClassNotFoundException e){
					continue;} catch(NoClassDefFoundError e){
					continue;}
				break;
			}

			/**
			 * Begin Addition for PHIC variables
			 * Attempt to descend into a Variable 's get method, to return a
			 * double.
			 */
			if(member instanceof Field&&
				(((Field)member).getType().isAssignableFrom(phic.common.Variable.class))){
				try{
					Object newObject=(Variable)((Field)member).get(object);
					member=phic.common.Variable.class.getMethod("get",new Class[0]);
					object=newObject;
                                        found=true;
				} catch(Exception ex){}
			}
			/** End addition for PHIC */
                        if(!found)
                          throw new ParseException("Cannot find object "+path);

		}

	}
        Object dummyItem;

	/**
	 * If this object represents a method, this value indicates the number of
	 * parameters needed.
	 */
	protected int nParams;

	/**
         *  Pass a string tokenizer whose tokens are member names
         * starting in newobject, OR if newobject is null,
         * cls represents the class of whom st's first token
         * is a static member
         */
	boolean find(StringTokenizer st,Object newobject,Class cls){
		try{
			while(st.hasMoreElements()){
				object=newobject;
				String token=st.nextToken();
				try{ //find any fields
					Field field=cls.getField(token);
					member=field;
					if(st.hasMoreElements())
						newobject=field.get(object);
				} catch(Exception e){
					try{ //find any 0- or 1-parameter methods
						Method method;
						method=findMethod(cls,token,nParams);
						/*catch(NoSuchMethodException ex){
							method=cls.getMethod(token,new Class[]{Double.TYPE});
									 } */member=method;
						if(st.hasMoreElements()&&nParams==0)
							newobject=method.invoke(object,null);
					} catch(Exception ex){
						throw new ParseException("Can't find "+token+" in "+object);
					}
					if(newobject==null&&st.hasMoreElements())
						throw new ParseException("Null object "+token+" in "+object);
				}
				if(newobject!=null)
					cls=newobject.getClass();
			}
			if(member==null)
				throw new ParseException("Error finding object "+st);
		} catch(ParseException e){
			object=null;
			return false;
		}
		return true;
	}

	/**
	 * Return the first available method in the given class 'cls',
	 * with the specified name 'token', and the specified number of arguments
	 * 'nParams'.
	 */

	private Method findMethod(Class cls,String token,
		int nParams) throws NoSuchMethodException{
		Method[] m=cls.getMethods();
		for(int i=0;i<m.length;i++)
			if(m[i].getName().equals(token)&&m[i].getParameterTypes().length==nParams)
				return m[i];
		throw new NoSuchMethodException();
	}
}

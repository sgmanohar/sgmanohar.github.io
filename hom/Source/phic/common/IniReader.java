package phic.common;
import java.io.*;
import java.util.*;
import phic.Resource;

import javax.swing.JOptionPane;
/**
 * Class to read initialisation files. The formats are as follows:
 * section names: enclosed in square brackets, on one line.
 * data: A string on a line within a section.
		 * keys: A string containing an = sign; the string before the equals is the name
		 * of the Key, the string after the equals sign is the value. The value may be a
 * string representing a floating point value.
 * White space is ignored, unless within a section name, key, or value.
 *
 * <BR>  Example:
 * <BR> [Default]
 * <BR> Width = 32
 */
public class IniReader{
	/** Private data, representing the file as lines */
	private String[] line=new String[0];

	/** Private data, representing the names of the section headers */
	private String[] headers=new String[0];

	/** Remember the filename that the text was read from */
	private String filename = "Unknown";

	/**
	 * Constructor, which reads in the lines of the file into
	 * array 'line[]'. The constructor calls Resource.getResource() with the
         * specified filename to locate the file, and so the path is relative to
         * the resources folder.
	 */
	public IniReader(String filename){
		this.filename=filename;
		try{
			initialise(Resource.loader.getResource(filename));
		} catch(Exception e){
			e.printStackTrace();
      JOptionPane.showMessageDialog(null,"Unable to read file "+filename);
		}
	}

  /**
   * Constructor, reads in lines of the Input stream into array line[]
    */
   public IniReader(InputStream is)throws IOException{
     try{
       initialise(is);
     } catch(Exception e){
       e.printStackTrace();
       JOptionPane.showMessageDialog(null,"Unable to read this resource: "+is);
     }
   }
   protected void initialise(InputStream is) throws IOException{
     BufferedReader br=new BufferedReader(new InputStreamReader(is));
     Vector v=new Vector();
     String s;
     do{
       s=br.readLine();
       v.add(s);
     } while(s!=null);v.remove(v.size()-1);
     line=(String[])v.toArray(line);
   }

	/**
	 * Return an array of all lines of format [word], without brackets. These are
	 * the section header names in the file.
	 */
	public String[] getSectionHeaders(){
		if(doneHeaders){
			return headers;
		}
		Vector v=new Vector();
		for(int i=0;i<line.length;i++){
			String s=line[i];
			if(s.startsWith("[")&&s.endsWith("]")){
				v.add(s.substring(1,s.length()-1));
			}
		}
		return headers=(String[])v.toArray(headers);
	}

	boolean doneHeaders=false;

	/**
	 * Gets all the strings in a section. Each string is one line of the file.
	 * The section begins on the first blank line below the header, and runs
	 * up to the next header line, with the form [NAME], or until the end of the
	 * file.
	 *
	 * @param sectionHeader The header which appears in square brackets at the
	 * top of the requested section of the file.
	 * @throws ArrayIndexOutOfBoundsException if the requested section is not
	 * found.
	 */
	public String[] getSectionStrings(String sectionHeader){
		int i=0;
		String s;
		Vector v=new Vector();
		//find the section
		//can throw ArrayIndexOutOfBoundsException
		do{
			s=line[i++];
		} while(!s.equals('['+sectionHeader+']')&&i<line.length);if(i>=line.length){
			return new String[0];
		}
		i--;
		while(i<line.length){
			//increment to next line
			i++;
			if(i>=line.length){
				break;
			}
			//get next line
			s=line[i];
			if(s.startsWith("[")){
				break;
			}
			if(s.length()>0&&!s.startsWith("//")){
				v.add(s);
			}
		}
		String[] ss=new String[v.size()];
		return(String[])v.toArray(ss);
	}

	/**
	 * Return a hashmap loaded with the properties in the file.
	 * Turns each KEY=VALUE pair in the given section into a key-value pair in
	 * the hashmap.
	 *
	 * @param sectionHeader the name of the section to convert into a hashmap.
	 * @throws IllegalArgumentException if the section header is not found.
	 */
	public Map getSectionMap(String sectionHeader){
		int i=0;
		String s;
		//find the section
		//can throw ArrayIndexOutOfBoundsException
		try{
			do{
				s=line[i++];
			} while(!s.equals('['+sectionHeader+']'));
		} catch(ArrayIndexOutOfBoundsException e){
			errorCannotFind(sectionHeader);
		}
		if(i>=line.length){
			return new Hashtable();
		}
		//add properties to hashmap
		Hashtable h=new Hashtable();
		i--;
		while(i<line.length){
			//increment to next line
			i++;
			if(i>=line.length){
				break;
			}
			//get next line
			s=line[i];
			if(s.startsWith("[")){
				break;
			}
			//parse left=right
			String left,right;
                        if(s.startsWith("//")) continue;
			int p=s.indexOf('=');
			if(p>0){
				left=s.substring(0,p).trim();
				right=s.substring(p+1).trim();
				if(left.equals("")){
					continue;
				}
			} else{
				left=s.trim();
				if(left.equals("")){
					continue;
				}
				right="";
			}
			//attempt to read 'right' as a number
			try{
				double d=Double.parseDouble(right);
				h.put(left,new Double(d));
			} catch(NumberFormatException e){
				//attempt to read 'right' as a boolean
				if(right.equalsIgnoreCase("true")||right.equalsIgnoreCase("yes")){
					h.put(left,Boolean.TRUE);
				} else if(right.equalsIgnoreCase("false")||right.equalsIgnoreCase("no")){
					h.put(left,Boolean.FALSE);
				} else{
					h.put(left,right);
				}
			}
		}
		return h;
	}

	/**
	 * Gets pairs like a map, but ordered.
	 * The return value is an array of String pairs, the first being the text
	 * before the equals, and the second being the text after it. It is
	 * useful if the Key-Value pairs need to be in order.
	 *
	 * @param sectionHeader the name of the section whose strings are to be
	 * retrieved.
   * @throws IllegalArgumentException if the sectionHeader is not found
	 */
	public String[][] getSectionPairs(String sectionHeader){
		int i=0;
		String s;
		//find the section
		//can throw ArrayIndexOutOfBoundsException
		do{
			s=line[i++];
		} while(!s.equals('['+sectionHeader+']')&&i<line.length);if(i>=line.length){
                  //return new String[0][0]; //exit gracefully?
                  throw new IllegalArgumentException("Section '"+sectionHeader+"' not found in file '"+filename+"'.");
		}
		//add properties to hashmap
		Vector v=new Vector();
		i--;
		while(i<line.length){
			//increment to next line
			i++;
			if(i>=line.length){
				break;
			}
			//get next line
			s=line[i];
			if(s.startsWith("[")){
				break;
			}
			//parse left=right
			String left,right;
                        if(s.startsWith("//")) continue;
			int p=s.indexOf('=');
			if(p>0){
				left=s.substring(0,p).trim();
				right=s.substring(p+1).trim();
				if(left.equals("")){
					continue;
				}
			} else{
				left=s.trim();
				if(left.equals("")){
					continue;
				}
				right="";
			}
			//attempt to read 'right' as a number
			v.add(new String[]{left,right});
		}
		String[][] strings=new String[2][v.size()];
		return(String[][])v.toArray(strings);
	}

        /**
         * Convert an String[] array of lines into a
         * String[][] array of pairs of items, separated by '='.
         * Items are trimmed and blanks are ignored.
         */
        public static String[][] getPairsFromStrings(String[] in){
          Vector out = new Vector();
          String left, right;
          for(int i=0;i<in.length;i++){
            int p=in[i].indexOf('=');
            if(p>0){
              left=in[i].substring(0,p).trim();
              right=in[i].substring(p+1).trim();
              if(left.equals("")) continue; //ignore strings beginning with =
            } else{ //lines without an '=' give only a 'left'.
              left=in[i].trim();
              if(left.equals("")) continue; //ignore blank lines
              right="";
            }
            out.add(new String[]{left,right});
          }
          String[][] strings=new String[2][out.size()];
          return (String[][])out.toArray(strings);
        }


	/**
	 * Throws an illegal argument exception.
	 * Called when a requested item is not found in this file.
	 */
	void errorCannotFind(String object){
		throw new IllegalArgumentException("Item not found in file "+filename+": '"
				+object+"'");
	}

  /** Returns the filename of the file that is represented by this iniReader. */
	public String getFilename(){
		return filename;
  }
}

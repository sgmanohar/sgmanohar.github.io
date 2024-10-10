
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;
import java.net.*;
import java.io.*;
import java.util.*;

public class CudosIndexReader {
	BufferedReader b;
	URL url;
	public CudosIndexReader(URL base,String f) {
		try{
			url=new URL(base,f);
		}catch(Exception e){e.printStackTrace();}
	}
        public CudosIndexReader(URL item){
          url=item;
        }
	public  String[] getStringsInSection(String section){
		boolean found=false;int count=0;
		reopen();
		String curr;
		try{
			while(b.ready()){
				curr=readLine(b);
				if(found){
					if(curr.startsWith("[")) break;
					if(curr.length()>0)count++;
				}
				if(curr.startsWith("["+section+"]")){
					found=true;
					b.mark(1000);
				}
			}
			if(found){
				b.reset();
				String[] txts=new String[count];
				for(int i=0;i<count;i++){
					while((curr=readLine(b)).length()==0);
					txts[i]=curr;
				}
				return txts;
			}else throw new RuntimeException("Index section "+section+" not found");
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
        /** Map of properties for section */
        public Map getMapFromSection(String section){
          String[] s=getStringsInSection(section);
          Map m = new HashMap();
          for(int i=0;i<s.length;i++){
            int q=s[i].indexOf("=");
            if(q>0){
              String l=s[i].substring(0,q).trim(),
                  r=s[i].substring(q+1).trim();
              m.put(l,r);
            } else if(s[i].trim().length()>0) m.put(s[i],null);
          }
          return m;
        }

        public String getProperty(String section, String property){
          return (String)getMapFromSection(section).get(property);
        }

        /** Comma separated list from a property in a section */
        public String[] getStringList(String section, String property){
          String s=(String)getMapFromSection(section).get(property);
          if(s.indexOf(',')>=0) return  s.split("\\s*,\\s*");
          else return new String[]{s};
        }

	public void reopen(){
		try{
			if(b!=null)b.close();
			InputStream is=url.openStream();
			InputStreamReader d=new InputStreamReader(is);
			b=new BufferedReader(d);
		}catch(Exception e){e.printStackTrace();}
	}
	public String[] getSectionNames(){
		String curr="";int count=0;
		try{
			reopen();
			b.mark(50000);
			while(b.ready()){
				curr=readLine(b);
				if(curr.startsWith("[") && !curr.startsWith("[End]") && !curr.startsWith("[Properties]"))count++;
			}
			b.reset();
			String[] sect=new String[count];
			for(int i=0;i<count;i++){
				while(b.ready() && (!(curr=readLine(b)).startsWith("[") || curr.startsWith("[Properties]")));
				sect[i]=curr.substring(1,curr.length()-1).trim();
			}
			return sect;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	public String getProperty(String key){
		String prop[]=getStringsInSection("Properties");
		for(int i=0;i<prop.length;i++){
			if(prop[i].startsWith(key+"="))return prop[i].substring(key.length()+1);
		}
		return "";
	}
	public String readLine(BufferedReader b){
		String s;
		try{
			do{
				s=b.readLine().trim();
				if(s==null)return null;
			}while(b.ready() && (s.startsWith("//") ||s.startsWith(";")));
		return s;
		}catch(Exception e){e.printStackTrace();}
		return "";
	}
}

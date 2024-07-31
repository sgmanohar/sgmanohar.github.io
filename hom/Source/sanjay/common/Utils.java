package sanjay.common;

public class Utils {
  /**
   * Preprocessing of backslash escapes:
   * remove escaped newlines, insert newlines for \n and \r,
   * and allow escaping of quotes.
   */
  public static final String unescape(String s){
    for(int i=0;i<s.length();i++){
      if(s.charAt(i)=='\\'){
        String lt=s.substring(0,i);
        String rt=s.substring(i+2);
        char c2=s.charAt(i+1);
        if(c2=='\n' || c2=='\r') s=lt+rt;
        if(c2=='"' || c2=='\\') { s=lt+c2+rt; i++; }
        if(c2=='n' || c2=='r') {  s=lt+'\n'+rt; i++; }
      }
    }
    return s;
  }

  /**
   * Repeat a string several times
   */
  public static final String strstr(String s, int i) {
    if (i <= 0)return "";
    StringBuffer sb = new StringBuffer(s.length() * i);
    for (int j = 0; j < i; j++) sb.append(s);
    return sb.toString();
  }

}

package com.cudos.common;

import java.util.*;

/**
 * filter utils
 */

public class Filter {
  public static String[]and(String[]s1,String[]s2){
    Vector v=new Vector();
    v.addAll(Arrays.asList(s1)); v.retainAll(Arrays.asList(s2));
    return (String[])v.toArray(new String[v.size()]);
  }
  public static String[]andnot(String[]s1,String[]s2){
    Vector s=new Vector();
    s.addAll(Arrays.asList(s1));s.removeAll(Arrays.asList(s2));
    return (String[])s.toArray(new String[s.size()]);
  }
}

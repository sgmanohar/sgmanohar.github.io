
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.*;


public class SagittalSections extends PictureSequence {

  public SagittalSections() {
  }
  String[]p={"coronal-01","coronal-02","coronal-03","coronal-04","coronal-05"};
  public String[] getImagePaths() {
    //TODO: implement this com.cudos.common.PictureSequence abstract method
	String[]s=new String[17];//p.length];
	for(int i=0;i<17/*p.length*/;i++)//{s[i]="resources/images/"+p[i]+".jpg";}
		s[i]="resources/images/New-"+String.valueOf(i+1)+".gif";
	return s;
  }
  public String[] getTexts(){return null;}// new String[]{"a","b","c","d","e"};}
}
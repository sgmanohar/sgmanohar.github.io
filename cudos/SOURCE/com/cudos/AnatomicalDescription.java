
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


public class AnatomicalDescription extends PicturesAndText {
	public final String resourcepath="resources/icons/";
	public static final String[] names={"Sagittal","Coronal","Transverse","Supine","Prone",
		"Superior","Inferior","Posterior","Anterior","Cranial","Rostral","Caudal",
		"Dorsal","Ventral","Medial","Lateral"};
	public static final String[] images={"vowels.gif","","","","",
		"","","","","","","",
		"","","",""};
	public static final String[] descriptions={
		"Sagittal sections are vertical in a plane parallel to the midline (latin: sagittus = arrow)"
		,"Vertically face-on",	"Horizontal","Palms up","Palms down",
		"","","","","","","",
		"","","",""};
	public String getExhibitName(){return "Anatomical Description";}
  public String[] getItemNames() {
    //TODO: implement this com.cudos.common.PicturesAndText abstract method
	return names;
  }

  public String[] getImagePaths() {
    //TODO: implement this com.cudos.common.PicturesAndText abstract method
	String[] imagepath=new String[images.length];
	for(int i=0;i<images.length;i++){
		if(images[i]!="")imagepath[i]=new String(resourcepath+images[i]);
		else imagepath[i]="";
	}
	return imagepath;
  }

  public String[] getTexts() {
    //TODO: implement this com.cudos.common.PicturesAndText abstract method
	return descriptions;
  }
}
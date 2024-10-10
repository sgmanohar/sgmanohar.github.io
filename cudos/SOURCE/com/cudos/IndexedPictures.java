
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
import java.awt.*;

public class IndexedPictures extends IndexedPictureAndText {

	String data[],name,path,file, comments[];
	String commentFile="resources/index/comments.txt";
	double hilitealpha=0.5;
	Color hilite=Color.red;
	public void postinit(){
		CudosIndexReader ixrd=new CudosIndexReader(getApplet().getResourceURL(file));
		data=ixrd.getStringsInSection(name);
		path=ixrd.getProperty("ImagePath");
		String colname=ixrd.getProperty("HighlightColour");
		if(colname.startsWith("blue"))hilite=Color.blue;
		else if(colname.startsWith("green"))hilite=Color.green;
		String halpha=ixrd.getProperty("HighlightAlpha");
		if(halpha.length()!=0)hilitealpha=Double.parseDouble(halpha);

		CudosIndexReader commix=new CudosIndexReader(getApplet().getResourceURL(commentFile));
		String[] items=getItemNames();
		comments=new String[items.length];
		for(int i=0;i<items.length;i++){
			comments[i]=commix.getProperty(items[i]);
		}
		super.postinit();
	}
	public String getExhibitName(){return name;}
	public String getTestName(){ return name; }

	public IndexedPictures(String[]s) {	//call with name of section in Labels.txt
	name=s[1];file=s[0];
	doHighlight=true;
	imagecontrol.setStretched(false);
	}
	public IndexedPictures(String section){
		this(new String[]{"resources/index/labels.txt", section});
	}

	public String[] getItemNames() {
	String[] t=new String[data.length-2];
	for(int i=0;i<data.length-2;i++){
		t[i]=data[i+2];
	}
	return t;
	}

	public String getImageName() {
	return path+data[0];
	}

	public String getIndexName() {
	return path+data[1];
	}

	public String[] getTexts() {
	return comments;
	}
	public Color getHighlightColour(){
	return hilite;
	}
	public double getHighlightAlpha(){
	return hilitealpha;
	}
}

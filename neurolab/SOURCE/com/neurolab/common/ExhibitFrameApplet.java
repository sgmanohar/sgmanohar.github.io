package com.neurolab.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * Added for mac compatibility to show in frame.
 */

public class ExhibitFrameApplet extends JApplet implements HoldsExhibit{
	HeldExhibit content;
//	Timer timer;
	String initialExhibit;
	JFrame frame;
	public void init(){
		//String plaf=UIManager.getInstalledLookAndFeels()[0].getClassName();
		//LookAndFeel plaf=new com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel();
		//try {UIManager.setLookAndFeel(plaf);}
		//catch (Exception e) {e.printStackTrace();}

		frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		try {
		  frame.setDefaultCloseOperation(3);
		}catch (SecurityException ex) {}



		String exhname=getParameter("Exhibit");
		if(exhname==null){
			exhname="ExhibitChooser";
			ReturnButton.createOperational=true;
		}else{
		  ReturnButton.createOperational=false;
		}
		try{
			Class newclass=Class.forName("com.neurolab."+exhname);
			content=(HeldExhibit)newclass.newInstance();
		}catch (Exception e){
			e.printStackTrace();
		}
		content.setHolder(this);
		frame.getContentPane().add((Component)content);
		content.init();
		initialExhibit=exhname;

		frame.setSize(600,440);
		frame.invalidate();
		frame.validate();
		frame.show();
	}

//	public Container getContentPane(){return this;}	// JApplet->Applet version

	public void setExhibit(String ename){
		//if changing to the initial exhibit, don't create a return button
		ReturnButton.createOperational= !ename.endsWith( initialExhibit );
		HeldExhibit newex;
		try{
			Class nclass=Class.forName(ename);
			newex=(HeldExhibit)nclass.newInstance();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		frame.getContentPane().remove((Component)content);
		content=newex;
		System.gc();	// you never know what...
		content.setHolder(this);
		frame.getContentPane().add((Component)content);
		content.init();

		frame.invalidate();
		frame.validate();
	}

	URL codeBase=null;
	public URL getURL(String filename) {
		URL url = null;
		/*// this version doesnt seem to work from within a JAR!
		if (codeBase == null) {
			codeBase = getCodeBase();
		}
		try {
			url = new URL(codeBase, filename);
		} catch (java.net.MalformedURLException e) {
			System.out.println("Couldn't create image: badly specified URL");
			return null;
		}
		return url;
		*/
		try {
      return Class.forName("Neurolab").getResource(filename);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("Can't load resources - class Neurolab in default package not found.");
    }
	}

	public ExhibitFrameApplet() {
	}

  public Image getImage(String resourceName) {
    return getToolkit().createImage(getURL(resourceName));
  }
}

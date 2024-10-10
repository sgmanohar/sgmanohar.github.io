package com.neurolab.common;

import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**
 * Added for compatibility -  runs without browser
 */

public class ExhibitFrame extends JFrame implements HoldsExhibit{
	HeldExhibit content;
	String initialExhibit;

	static String[] params;
	public static void main(String[] p){
		params = p;
		new ExhibitFrame();
	}
	public ExhibitFrame(){
		//String plaf=UIManager.getInstalledLookAndFeels()[0].getClassName();
		//LookAndFeel plaf=new com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel();
		//try {UIManager.setLookAndFeel(plaf);}
		//catch (Exception e) {e.printStackTrace();}

		setSize(620,440);
		getContentPane().setLayout(new BorderLayout());


		String exhname = null;
		if(params!=null && params.length>0) exhname	= params[0];
		if(exhname==null){
			exhname="ExhibitChooser";
			ReturnButton.createOperational=true;
		}

		try{
			Class newclass=Class.forName("com.neurolab."+exhname);
			content=(HeldExhibit)newclass.newInstance();
		}catch (Exception e){
			e.printStackTrace();
		}
		content.setHolder(this);
		getContentPane().add((Component)content);
		content.init();
		initialExhibit=exhname;
		setTitle("Neurolab");
		setDefaultCloseOperation(3); //system.exit on close
		validateTree();

		show();
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
		getContentPane().remove((Component)content);
		content=newex;
		System.gc();	// you never know what...
		content.setHolder(this);
		getContentPane().add((Component)content);
		content.init();
		validateTree();

	}

	public URL getURL(String filename) {
		URL url = null;
		try {
			url = getClass().getResource('/'+filename);
		} catch (Exception e) {
			System.out.println("Couldn't create image: badly specified URL "+filename);
			return null;
		}
		return url;
	}
	public Image getImage(URL url){
		return Toolkit.getDefaultToolkit().createImage(url);
	}
	public Image getImage(URL url, String s)  {
		try{
			return Toolkit.getDefaultToolkit().createImage(new URL(url,s));
		}catch(Exception e){
			throw new RuntimeException(e.toString());
		}
	}
	public Image getImage(String s){
          if(s.startsWith("resources")) s="/"+s;
          return getImage( getClass().getResource(s));
	}
}

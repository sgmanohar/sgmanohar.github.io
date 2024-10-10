package com.neurolab.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

//import com.oyoaha.swing.plaf.oyoaha.*;

public class ExhibitContainer extends JApplet implements HoldsExhibit{
	HeldExhibit content;
//	Timer timer;
	String initialExhibit;
	public void init(){
		//String plaf=UIManager.getInstalledLookAndFeels()[0].getClassName();
		//LookAndFeel plaf=new com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel();
		//try {UIManager.setLookAndFeel(plaf);}
		//catch (Exception e) {e.printStackTrace();}

		this.getContentPane().setLayout(new BorderLayout());
		String exhname=getParameter("Exhibit");
		if(exhname==null){
			exhname="ExhibitChooser";
			ReturnButton.createOperational = true;
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
/*	public void paint(Graphics g){
		super.paint(g);
		g.setFont(new Font("Arial",32,Font.BOLD));
		g.setColor(Color.white);
		g.drawString("Loading Exhibit...",250,250);
		g.setColor(Color.gray);
		g.drawString("Loading Exhibit...",249,249);
	}*/

	URL codeBase=null;
	public URL getURL(String filename) {
		URL url = null;
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
	}
	public Image getImage(String s){
		if(!s.startsWith("/")) s='/'+s;
		InputStream is = getClass().getResourceAsStream(s);
		return Toolkit.getDefaultToolkit().createImage(
			getClass().getResource(s)
		);
	}
}

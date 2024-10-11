package com.neurolab.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

//import com.oyoaha.swing.plaf.oyoaha.*;

public class ExhibitContainer extends JApplet implements HoldsExhibit {
	HeldExhibit content;
//	Timer timer;
	String initialExhibit;
	public void init(){
	  // order of preference of look-and-feel for Applet:
    String[] preferredOrder = {"Nimbus", "Windows Classic", "Windows"};
		String plaf=UIManager.getCrossPlatformLookAndFeelClassName(); //use crossplatform if none found
		LookAndFeelInfo[] lnfs=UIManager.getInstalledLookAndFeels();
		int selected=-1;
		for(int i=0;i<preferredOrder.length;i++) {
		  for(int j=0;j<lnfs.length;j++)
		    if(lnfs[j].getName().equalsIgnoreCase(preferredOrder[i])) {
		      selected=j;
		      break;
		    }
		  if(selected>=0) break;
		}
		if(selected>=0) plaf=lnfs[selected].getClassName();
		try {UIManager.setLookAndFeel(plaf);}
		catch (Exception e) {e.printStackTrace();}
		
    super.init();

		String exhname=getParameter("Exhibit");
		if(exhname==null){
			exhname="ExhibitChooser_HTML";
			ReturnButton.createOperational = true;
		}
		try{

			Class newclass=Class.forName("com.neurolab."+exhname);
			content=(HeldExhibit)newclass.newInstance();
		}catch (Exception e){
			e.printStackTrace();
		}
		content.setHolder(this);
		getContentPane().add((Component)content, BorderLayout.CENTER);

		content.init();
		initialExhibit=exhname;
		
		
	}
	public void start() {
	  super.start();
	  synchronized(getTreeLock()) {
      validateTree();
    }
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
		content.close();
		getContentPane().remove((Component)content);
		content=newex;
		System.gc();	// you never know what...
		content.setHolder(this);
		getContentPane().add((Component)content);
		getContentPane().revalidate();
		getContentPane().repaint();
		content.init();
		synchronized(getTreeLock()) {
      validateTree();
    }
    
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
	  /* // doesnt work in JAR!
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
		*/
	  try {
	    if(!filename.startsWith("/")) filename='/'+filename;
      //return Class.forName("Neurolab").getResource(filename);
	    return getClass().getResource(filename);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Couldnt find resources - "+filename);
    }
	}
	public Image getImage(String s){
		if(!s.startsWith("/")) s='/'+s;
		InputStream is = getClass().getResourceAsStream(s);
		return Toolkit.getDefaultToolkit().createImage(
			getClass().getResource(s)
		);
	}
}

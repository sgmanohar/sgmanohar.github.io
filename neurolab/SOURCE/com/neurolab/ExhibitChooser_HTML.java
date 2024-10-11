//ExhibitChooser.class by Sanjay Manohar
package com.neurolab;

/*
1.2	thick borders added to buttons
2.1	images loaded via getImage(String)
*/



import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import com.neurolab.common.*;

public class ExhibitChooser_HTML extends NeurolabExhibit implements ActionListener, HyperlinkListener{
  String[] htmlFiles = {
    "pages/part1.html", 
    "pages/part2.html",
    "pages/part3.html", 
    "pages/part4.html"
  };
  String[] sectionNames = {
    "Neural mechanisms", "Sensory systems", "Motor systems", "Higher functions"
  };
  String classprefix = "com.neurolab."; // this is prepended to the Exhibit class name
	private Image[] bitmap;
	private Dimension gridsize;
  JEditorPane[] editor = new JEditorPane[4];
  JTabbedPane tabbedPane;
	public void init(){
		super.init();
    tabbedPane = new JTabbedPane();
    getMainContainer().setLayout(new BorderLayout());
    getMainContainer().add(tabbedPane,BorderLayout.CENTER);
    URL resource=null;
    for(int i=0;i<htmlFiles.length;i++){
      try{
        resource  = getClass().getResource("/resources/"+ htmlFiles[i]);
        editor[i] = new JEditorPane(resource);
      }catch(Exception ex){
        System.out.println("file:"+resource.toString());
        ex.printStackTrace();
      }
      //editor[i].setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
      editor[i].setEditable(false);
      editor[i].addHyperlinkListener(this);
      
      JScrollPane scr = new JScrollPane(editor[i]);
      tabbedPane.add(scr,sectionNames[i]);

      //tabbedPane.add(editor[i],sectionNames[i]);
    }


	}

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()!=""){
      getHolder().setExhibit(classprefix+e.getActionCommand());
		}
	}

  public void hyperlinkUpdate(HyperlinkEvent e) {
    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      String s=e.getURL().toString();  // get text of hyperlink
      s=s.substring(s.lastIndexOf('/') + 1); // get just the last word = class name

      if(s!=null && s!=""){            // this should be the exhibit name i.e. class name
        getHolder().setExhibit(classprefix+s);
      }
    }
  }

  public String getExhibitName(){
		return "";
	}
	boolean DRAW_BEVEL=false; // Set this to false if the Look and Feel does an 
	                          // appropriate 3d button rendering
	
  public void close(){  }
}

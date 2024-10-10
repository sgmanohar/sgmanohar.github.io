
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
//import com.neurolab.common.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.Vector;

public class MainCudosApplet extends JApplet implements CudosApplet {
//	public Container getContentPane(){return this;}	// if using Applet instead of JApplet
	public MainCudosApplet() {
		staticinit();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	/*paintComponentMessage(mainPanel,"Main menu loading...");*/
	}
	public void init(){	// add the exhibit
		String clsname=getParameter("exhibit");
		if(clsname!=null){
			int p=clsname.indexOf(':');
			if(p>0){
				String params=clsname.substring(p+1); clsname=clsname.substring(0,p);
				toExhibit(clsname, params); return;
			}
		}else{
			clsname="com.cudos.ModuleChooser";
		}
		toExhibit(clsname);
	}
	static final String[][] pinfo={
		{"exhibit","string","initial exhibit classname"}
	};
	public String[][] getParameterInfo(){return pinfo;}

	//some useful statics
				public static Border		etched;
	public static Border  	        raisedbevel;
	public static Border            loweredbevel;
	TitleBar titleBar;
	JPanel mainPanel = new JPanel();

	public CudosExhibit content;
	private Vector stack=new Vector();

				public void staticinit(){
						etched = BorderFactory.createEtchedBorder();
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel=BorderFactory.createLoweredBevelBorder();

		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (Exception e) {e.printStackTrace();}

	}
        public void paintText3D(Graphics2D g,String t,int x,int y){
		g.setPaint(SystemColor.controlLtHighlight);
		g.drawString(t,x+1,y+1);
		g.setPaint(SystemColor.controlShadow);
		g.drawString(t,x,y);
	}
	public int getTextWidth(Graphics2D g,String t){
	//	return (int)(t.length()*(110./6.)*(points/30.));
		return (int)(g.getFont().getStringBounds(t,0,t.length(),g.getFontRenderContext()).getWidth() );
	};
	public int getTextHeight(Graphics2D g,String t){
	//	return (int)(t.length()*(110./6.)*(points/30.));
		return (int)(g.getFont().getStringBounds(t,0,t.length(),g.getFontRenderContext()).getHeight() );
	};
	public void paintComponentMessage(Component c,String s){
		Graphics2D g=(Graphics2D)c.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("SansSerif",Font.BOLD,16));
		paintText3D(g,s,(c.getWidth()-getTextWidth(g,s))/2,(c.getHeight()-getTextHeight(g,s))/2);
	}
//	public static void setBG(Component c){
//		c.setBackground(systemGray);
//	}

	//NON STATICS
	public Container getMainContainer(){
		return mainPanel;
	}
		// Functions to alter the content of the main pane

	public CudosExhibit getExhibitContent(){
		return content;
	}
	private void setExhibitContent(CudosExhibit e){
		CudosExhibit newcontent=e;
		if(newcontent!=null){
			if(content!=null){
				mainPanel.remove(content);
			}
			content=newcontent;
			mainPanel.add(content, BorderLayout.CENTER);
			validateTree();
			titleBar.setTitle(content.getExhibitName());
			titleBar.repaint();
			content.postinit();
			mainPanel.repaint();
		}
	}

//	ClassLoader cloader=new CudosClassLoader();	//classloader for exhibits

	private void setExhibitContent(String clsname){
		CudosExhibit newcontent;
		try{
			Class cls= Class.forName(clsname);// cloader.loadClass(clsname);
			newcontent=(CudosExhibit)cls.newInstance();
			setExhibitContent(newcontent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setExhibitContent(String clsname,Object param){
		CudosExhibit newcontent;
		try{
			Class cls=Class.forName(clsname);// Class cls=cloader.loadClass(clsname);
			Constructor cn=cls.getConstructor(new Class[]{param.getClass()});
			newcontent=(CudosExhibit)(cn.newInstance(new Object[]{param}));
			setExhibitContent(newcontent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void toChooser(){
		stack.remove(content.getClass().getName());
		String oldcls=(String)stack.get(stack.size()-1);
		setExhibitContent(oldcls);
	}
	public void toExhibit(String cls){
		setExhibitContent(cls);
		stack.add(cls);
	}

	public void toExhibit(String cls,Object param){
		setExhibitContent(cls,param);
		stack.add(cls);
	}
 /* public static void main(String[] args) {
		CudosApplet cudosApplet1 = new CudosApplet();
	}
*/
		//Image loading
	public Image getImage(String s){
		return getImage(getCodeBase(),s);	// using codeBase not documentBase
	}
	public Image getImage(URL u,String s){		// read image and wait for load
		Image i=super.getImage(u,s);
		mt.addImage(i,++iid);
		ipd.show();
		try{mt.waitForID(iid);}catch(Exception e){e.printStackTrace();}
		ipd.hide();
		return i;
	}
	static int iid=0;
	MediaTracker mt=new MediaTracker(this);
	ProgressDialog ipd=new ProgressDialog("Loading Images","Please wait, images downloading");

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(titleBar= new TitleBar(), BorderLayout.NORTH);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
	}

  public void setTitle(String s) {
    titleBar.setTitle(s);
  }

  public URL getResourceURL(String resource) {
    try{
      return new URL(getCodeBase(),resource);
    }catch(MalformedURLException e){e.printStackTrace();}
    return null;
  }

  public String getTitle() {
    return titleBar.getTitle();
  }

}

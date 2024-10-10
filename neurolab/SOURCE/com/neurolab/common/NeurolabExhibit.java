//NeurolabExhibit.class by Sanjay Manohar
package com.neurolab.common;

/*example of NeurolabExhibit:
	public class Vowels extends NeurolabExhibit {
		public String getExhibitName(){return "Vowels";}
		public void init(){
			super.init();
			getMainContainer().setLayout(new BorderLayout());
			//initialise variables
			createComponents();
		}
		public void createComponents(){
			getMainContainer().add(new ReturnButton(),BorderLayout.SOUTH);
		}
	}
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.net.*;
import java.awt.image.ImageObserver;
import java.util.*;

/*
1.1	ok
1.2	added paintText3D(Graphics2D,String,int,int)
1.3	you must now call createComponents from your own init()
1.4	init() now sets maincontainer background
1.5	added class ReturnButton with exit functionality
1.6	added setBG(Component)
2.0	implements HeldExhibit: can be added to the ExhibitContainer by the ExhibitChooser neurolabexhibit.
2.1	no longer implements JApplet.

3.1	URL handling routed through ExhibitHolder
3.2	URLs all relative to codeBase not documentBase
*/

public class NeurolabExhibit extends JPanel implements HeldExhibit{
	private TitleBar titlebar;
	static public Border etched,raisedbevel,loweredbevel;
	private Container master,titlepanel,lowerpanel;
	protected Container maincontainer;
	public static Color systemGray=Color.lightGray;

	public HoldsExhibit holder;

	//to be defined!
	public String getExhibitName(){return "";}	// should return short exhibit name
	public void createComponents(){};	        // should use getMainContainer().add()
	public void init(){

		etched = BorderFactory.createEtchedBorder();
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel=BorderFactory.createLoweredBevelBorder();

		master=this;//master=getContentPane();
		master.setBackground (systemGray);
		master.setLayout(new BorderLayout());

			// generic titlebar with borders
		titlebar=new TitleBar(getExhibitName());
		master.add(titlepanel=new Panel(),BorderLayout.NORTH);
		titlepanel.setLayout(new BorderLayout());
		String edge[]={BorderLayout.NORTH,BorderLayout.SOUTH,BorderLayout.EAST,BorderLayout.WEST};
		for(int i=0;i<4;i++)titlepanel.add(new Spacer(10,10),edge[i]);	// space around title
		titlepanel.add(titlebar,BorderLayout.CENTER);
			// rest of window
		lowerpanel=new Panel();
		master.add(lowerpanel,BorderLayout.CENTER);
		lowerpanel.setLayout(new BorderLayout());
		for(int i=1;i<4;i++)lowerpanel.add(new Spacer(10,10),edge[i]);	// space around lower part
			//main container
		maincontainer=new Panel();
		lowerpanel.add(maincontainer,BorderLayout.CENTER);
		setBG(maincontainer);
	}
	public void setHolder(HoldsExhibit h){holder=h;}
	public HoldsExhibit getHolder(){return holder;}
	public Container getMainContainer(){return maincontainer;}
	public Image getImage(String s){return getHolder().getImage(s);}
	public URL getURL(String s){return getHolder().getURL(s);}

	public String getAppletInfo() {
		return "Neurolab: "+getExhibitName()+", coded in Java by\nRobin Marlow and Sanjay Manohar\nbased on an original program by Dr. Carpenter";
	}
	public void toExhibitChooser(){
		getHolder().setExhibit("com.neurolab.ExhibitChooser");
	}
	public static void paintText3D(Graphics g,String t,int x,int y){
		g.setColor(Color.white);
		g.drawString(t,x+1,y+1);
		g.setColor(Color.gray);
		g.drawString(t,x,y);
	}

	public static int getTextWidth(Graphics g_,String t){
		try{
//			Graphics2D g = (Graphics2D) g_;
//			return (int)(g.getFont().getStringBounds(t,0,t.length(),g.getFontRenderContext()).getWidth() );
		}catch(Exception e){		}
		return 20 * t.length();     ////?????
	};
	public static int getTextHeight(Graphics g_,String t){
		try{
//			Graphics2D g = (Graphics2D) g_;
//			return (int)(g.getFont().getStringBounds(t,0,t.length(),g.getFontRenderContext()).getHeight() );
		}catch(Exception e){		}
		return g_.getFont().getSize() * 2;   ////////???????
	}

	public static void setBG(Component c){
		c.setBackground(systemGray);
	}
				public static NeurolabExhibit neurolab(Component c){
					Component p=c;
					while((p!=null) && !(p instanceof NeurolabExhibit)){
						p=p.getParent();
					}
					return (NeurolabExhibit)p;
				}

	public void close(){}	// override this to finalize timers etc.; called on closing







	///Compatibility  (17-09-02)








	/**
	 * Compatibility version of RenderingHints.AntiAlias:
	 * Does nothing in JRE version 1.8 or lower.
	 */
	public static void antiAlias(Graphics g){
		try{
			ExtraGraphics.antiAlias(g);
		}catch(Exception e){
			// Do nothing if antialias not supported
			// ClassNotFound error will be generated if no Graphics2D.
		}
	}

	/**
	 * A static variable to say whether Graphics2D is supported or not.
	 */
	public static boolean supportsGraphics2D;
	static{
		try{
			Class c = ExtraGraphics.class;
			supportsGraphics2D = true;
		}catch(Exception e){
			supportsGraphics2D = false;
		}
	}

	/**
	 * Set the stroke thickness, using BasicStroke.
	 */
	public static void setStrokeThickness(Graphics g, float thick){
		try{
			ExtraGraphics.setStrokeThickness(g,thick);
		}catch(Exception e){
			//Do nothing if not supported
		}
	}
	/**
	 * Draw a shape.
	 */
/*
	public void doFillShape(Graphics g, Shape s){
		try{
			ExtraGraphics.doFillShape(g,s);
		}catch(Exception e){
			// do nothing?? or try it?
		}
	}
*/

public static final double ptLineDist(Point p, Point e1, Point e2){
	return Math.abs (		(p.x - e1.x) * (e2.y-e1.y) - (p.y - e1.y) * (e2.x-e1.x)	);
}
public static final double ptLineDist(double px, double py, double e1x,
																			double e1y, double e2x, double e2y){
	return Math.abs (		(px - e1x) * (e2y-e1y) - (py - e1y) * (e2x-e1x)	);
}
public static final double distance(Point a, Point b){
	int tmp;
	return Math.sqrt( (tmp=a.x-b.x)*tmp + (tmp=a.y-b.y)*tmp );
}
public static final double distance(Point a, int bx, int by){
	int tmp;
	return Math.sqrt( (tmp=a.x-bx)*tmp + (tmp=a.y-by)*tmp );
}
public void addAll(Vector to, Vector from){
	for(int i=0;i<from.size();i++)	to.addElement(from.elementAt(i));
}
public void removeAll(Vector from, Vector remove){
	for(int i=0;i<remove.size();i++){ while(from.removeElement(remove.elementAt(i))); }
	}











// TitleBar.class by Robin Marlow

class TitleBar extends JPanel {
	Color systemGray;
	String title;
	public TitleBar(String t){
		title=t;
		setBorder(raisedbevel);
		setBackground(systemGray);
	}
	public void setTitle(String t){title=t;}
	public String getTitle(){return title;}
	public void paintComponent( Graphics g ){
		super.paintComponent(g);
		antiAlias(g);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		if(title!=""){
			paintText3D(g,"Neurolab",10,35);
			paintText3D(g,title,(getWidth()- getTextWidth(g,title)-11),35);
		}else	paintText3D(g,"Neurolab",(getWidth()-getTextWidth(g,"Neurolab"))/2,35);
	}
	public Dimension getPreferredSize(){
		return new Dimension(getWidth(),50);
	}

}



public static int LABEL_POS_LEFT=0,LABEL_POS_BELOW=1;
public	class NamedSliderPanel extends JPanel{
		public JSlider slider;
		public Label label;
		public NamedSliderPanel(String name,int max,int labelpos){
			super();
			setLayout(new BorderLayout());
			add(slider=new JSlider(JSlider.HORIZONTAL,0,max,1){
				public Dimension getPreferredSize(){
					return new Dimension(80,30);
				}
			},(labelpos==LABEL_POS_LEFT)?BorderLayout.EAST:BorderLayout.NORTH);
			add(label=new Label(name),
				(labelpos==LABEL_POS_LEFT)?BorderLayout.WEST:BorderLayout.SOUTH);
			setBG(slider);setBG(label);setBG(this);
		}
	}


}

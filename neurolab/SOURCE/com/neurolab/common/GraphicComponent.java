

package com.neurolab.common;

import javax.swing.JComponent;
import java.awt.*;

public class GraphicComponent extends JComponent implements NeurolabGuiComponent{
	public static final int TYPE_LINE=0;
	public static final int TYPE_BOX=1;
	public static final int TYPE_HORZLINE=2;
	public static final int TYPE_VERTLINE=3;            // statics
	public static final int TYPE_ELLIPSE=4;

	public static final int ARROW_NONE=0;
	public static final int ARROW_FORWARDS=1;
	public static final int ARROW_BACKWARDS=2;

	private int type=TYPE_LINE;
	private int thickness=5;                      //properties
	private boolean direction=false;
	private int arrow=ARROW_NONE;
	private int arrowlen=4;

	public void setType(int t){type=t;}           //getters and setters
	public int getType(){return type;}
	public int getThickness(){return thickness;}
	public void setThickness(int t){thickness=t;}
	public boolean getDirection(){return direction;}
	public void setDirection(boolean d){direction=d;}
	public void setArrow(int a){arrow=a;}
	public int getArrow(){return arrow;}

	public GraphicComponent() {
		NeurolabExhibit.setBG(this);
	}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(getForeground());
		NeurolabExhibit.antiAlias(g);
		NeurolabExhibit.setStrokeThickness(g,thickness);
		switch(type){
			case TYPE_LINE:
				if(direction){
		g.drawLine(0,0,getWidth(),getHeight());
	} else {
		g.drawLine(getWidth(),0,0,getHeight());
	}
				break;
			case TYPE_BOX:
				g.fillRect(0,0,getWidth(),getHeight());
				break;
			case TYPE_HORZLINE:
				g.drawLine(0,getHeight()/2,getWidth(),getHeight()/2);
	if(arrow==ARROW_FORWARDS){
		g.drawLine(getWidth(),getHeight()/2,getWidth()-arrowlen,getHeight()/2-arrowlen);
		g.drawLine(getWidth(),getHeight()/2,getWidth()-arrowlen,getHeight()/2+arrowlen);
	}else if(arrow==ARROW_BACKWARDS){
		g.drawLine(0,getHeight()/2,arrowlen,getHeight()/2-arrowlen);
		g.drawLine(0,getHeight()/2,arrowlen,getHeight()/2+arrowlen);
	}
				break;
			case TYPE_VERTLINE:
				g.drawLine(getWidth()/2,0,getWidth()/2,getHeight());
	if(arrow==ARROW_FORWARDS){
		g.drawLine(getWidth()/2,getHeight(),getWidth()/2-arrowlen,getHeight()-arrowlen);
		g.drawLine(getWidth()/2,getHeight(),getWidth()/2+arrowlen,getHeight()-arrowlen);
	}else if(arrow==ARROW_BACKWARDS){
		g.drawLine(getWidth()/2,0,getWidth()/2-arrowlen,arrowlen);
		g.drawLine(getWidth()/2,0,getWidth()/2+arrowlen,arrowlen);
	}
				break;
			case TYPE_ELLIPSE:
				g.fillOval(0,0,getWidth(),getHeight());
				break;
		}
	}
}
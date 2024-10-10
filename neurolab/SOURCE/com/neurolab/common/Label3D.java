
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab.common;

import javax.swing.JComponent;
import java.awt.*;

public class Label3D extends JComponent implements NeurolabGuiComponent{
	String text="";
	public Label3D(String s){
		text=s;
	}
	public void setText(String s){text=s;}
	public String getText(){return text;}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(getForeground());
		NeurolabExhibit.antiAlias(g);
		g.setFont(getFont());
		NeurolabExhibit.paintText3D(g,text,0,getHeight()-7);
	}
	public Label3D() {
	this("");
	}
}
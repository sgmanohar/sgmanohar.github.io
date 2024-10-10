
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
import javax.swing.*;

public class MultiPercentageBar extends JComponent {
	public static final boolean HORIZONTAL=true,VERTICAL=false;

	protected Color[] colors={Color.blue,Color.red,Color.green,Color.yellow};
	public Color[] getColors(){return colors;}
	public void setColors(Color[] c){colors=c;}

	public boolean horz=true;
	public void setOrientation(boolean b){horz=b;}
	public boolean getOrientation(){return horz;}

	public int values[]=new int[1];
	public void setValues(int[] a){values=a;repaint();}
	public int[] getValues(){return values;}

	public MultiPercentageBar(){
		setBorder(BorderFactory.createLoweredBevelBorder());
	}
	public void paint(Graphics g){
		super.paint(g);
		int pos=2,len=0;
		for(int i=0;i<values.length;i++){
			g.setColor(colors[i%colors.length]);	//wrap around if not enough colours
			pos+=len;	//start from end of last bar
			len=values[i]*(getWidth()-4)/100;
			if(horz)g.fillRect(pos,2,len,getHeight()-4);
			else	g.fillRect(2,getHeight()-pos-len,getWidth()-4,len);
		}
	}
	public Dimension getPreferredSize(){
		return new Dimension(100,30);
	}
}

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReturnButton extends JButton implements NeurolabGuiComponent,ActionListener{
	public static boolean createOperational=false;
	boolean operational=createOperational;
	public void paint(Graphics g){
		super.paint(g);
		if(!operational){
			g.setColor(getBackground());
			g.fillRect(0,0,getWidth(),getHeight());
		}
	}
	public ReturnButton(){
		super("Return");
		NeurolabExhibit.setBG(this);
		addActionListener(this);
	}
	public void actionPerformed(ActionEvent e){
		if(operational){
			NeurolabExhibit.neurolab(this).close();
			NeurolabExhibit.neurolab(this).toExhibitChooser();
		}
	}
}
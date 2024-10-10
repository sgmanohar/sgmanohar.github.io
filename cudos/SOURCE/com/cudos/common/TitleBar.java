
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

import javax.swing.*;
import java.awt.*;

public class TitleBar extends JPanel implements CudosGuiComponent{
	String title="";
	public void setTitle(String t){title=t;}
	public String getTitle(){return title;}
	public void paint( Graphics g_ ){
		super.paint(g_);
		Graphics2D g = (Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(getFont());
                CudosApplet a=CudosExhibit.getApplet(this);
		if(title!=null && title!=""){
  			a.paintText3D(g,"CUDOS",10,35);
			a.paintText3D(g,title,(getWidth()- a.getTextWidth(g,title)-11),35);
		}else	a.paintText3D(g,"CUDOS",(getWidth()-a.getTextWidth(g,"CUDOS"))/2,35);
	}
	public TitleBar(){
		setFont(new Font("SansSerif", Font.BOLD, 30));
		setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createRaisedBevelBorder()
			)
		);
	}
	public Dimension getPreferredSize(){
		return new Dimension(getWidth(),50);
	}
}

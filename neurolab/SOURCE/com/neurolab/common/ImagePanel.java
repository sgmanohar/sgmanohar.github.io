
package com.neurolab.common;

import javax.swing.JPanel;
import java.awt.*;
public class ImagePanel extends JPanel {
	Image image;
	public ImagePanel(Image i){
		image=i;
	}
        public ImagePanel(){this(null);}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
                if(image!=null)
		    g.drawImage(image,0,0,this);
	}
}

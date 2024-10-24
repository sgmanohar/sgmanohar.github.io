
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

import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.*;

public class ImageControl extends JComponent {
	Image image;
	boolean stretch=false;
  	public ImageControl() {
		image=null;
  	}
	public void setStretched(boolean b){stretch=b;}
	public boolean getStretched(){return stretch;}
	public ImageControl(Image i){setImage(i);}
        public void setImage(Image i) {
          image = i;
          if (i != null) {
            int w = image.getWidth(this);
            if (image.getWidth(this) > 0) {
              repaint();
              loaded = true;
            }
            else loaded = false;
          }
        }

        String swait = "Please wait, loading image...";
	int count=0;
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(image!=null && loaded){
			if(stretch){
				g.drawImage(image,0,0,getWidth(),getHeight(),this);
			}else{
				int x=image.getWidth(this),y=image.getHeight(this);
				g.drawImage(image,(getWidth()-x)/2,(getHeight()-y)/2,this);
			}
		}else{
			CudosExhibit.getApplet(this).paintComponentMessage(this,swait);
			g.setColor(Color.blue);
			g.fill3DRect(10,getHeight()/2+50,count++,20,false);
		}
	}
	boolean loaded=false;
	public boolean imageUpdate(Image i,int inf,int x,int y,int w,int h){
		if((i==image)&&(inf & ImageObserver.ALLBITS)>0){
			loaded=true;
			repaint();		//redraw now info is here!
		}
		return super.imageUpdate(i,inf,x,y,w,h);
	}
	public Point screenToImage(Point p,Image i){
		if(stretch)return new Point(p.x*i.getWidth(this)/getWidth(),p.y*i.getHeight(this)/getHeight());
		else return new Point(p.x-(getWidth()-i.getWidth(this))/2,p.y-(getHeight()-i.getHeight(this))/2);
	}
	public Point imageToScreen(Point p,Image i){
		if(stretch)return new Point(p.x*getWidth()/i.getWidth(this),p.y*getWidth()/i.getWidth(this));
		else return new Point(p.x+(getWidth()-i.getWidth(this))/2,p.y+(getHeight()-i.getHeight(this))/2);
	}

}

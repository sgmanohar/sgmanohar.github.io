
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
import java.util.Vector;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ImageStackControl extends JComponent {
	Vector iCoords=new Vector();
	Vector images=new Vector();
	Vector pixels=new Vector();
	Vector loaded=new Vector();
	Vector readytoload=new Vector();
	int[] pix;
	int count=1;

	int renderres=200;
	public float transparency=0.4f;
	int blocksize=2;

	public void paint(Graphics g_){
		Graphics2D g=(Graphics2D)g_;
		if(dest!=null){
			g.drawImage(dest,0,0,this);
		}else{
			Point[] p;int[] pix;
			g.setColor(Color.black);
			for(int i=0;i<loaded.size();i++){
				p=(Point[])iCoords.get(i);
				drawOutline(g,p);
			}
		}
		if(selection>=0){
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(4));
			drawOutline(g,(Point[])iCoords.get(selection));
		}
		g.fill3DRect(0,getHeight()-10,(int)(20*Math.log(count)),10,false);
	}
	public void drawOutline(Graphics2D g,Point[] p){
		g.draw(new Line2D.Double(p[0],p[1]));
		g.draw(new Line2D.Double(p[1],p[3]));
		g.draw(new Line2D.Double(p[3],p[2]));
		g.draw(new Line2D.Double(p[2],p[0]));
	}

	public int selection=-1;
	IndexedImageListener listener;
	public void setIndexedImageListener(IndexedImageListener l){listener=l;}
	public ImageStackControl() {
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int n=images.size();
				selection=n-e.getY()/thick-1;
				if(selection>=n)selection=-1;
				if(listener!=null)listener.indexedImageEvent(selection);
				repaint();
			}
		});
	}
	Image dest;
	public void buildGraphic(){
		if(dest==null)dest=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)dest.getGraphics();
		g.setColor(Color.white);g.fillRect(0,0,getWidth(),getHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,transparency));
		repositionImages();	//check- we cant afford not to
		Point[] p;int[] pix;
		int w,h, rx,ry, pixbase,c=0;
		double lx,ly,pixdx;
		for(int i=0;i<loaded.size();i++){
			p=(Point[])iCoords.get(i);
			if( ((Boolean)loaded.get(i)).booleanValue() ){		//is loaded?
				pix=(int[])pixels.get(i);
				w=((Image)images.get(i)).getWidth(this);
				h=((Image)images.get(i)).getHeight(this);
				for(int m=0;m<renderres;m++){
count++;getGraphics().fill3DRect(0,getHeight()-10,(int)(20*Math.log(count)),10,false);
					rx=p[0].x+m*(p[2].x-p[0].x)/renderres;		//start of line
					ry=p[0].y+m*(p[2].y-p[0].y)/renderres;
					lx=((p[1].x-p[0].x)+m*(p[3].x-p[2].x-p[1].x+p[0].x)/(double)renderres)/renderres;	//delta along line
					ly=((p[1].y-p[0].y)+m*(p[3].y-p[2].y-p[1].y+p[0].y)/(double)renderres)/renderres;	//float the division
					pixbase=w*(int)(m*h/renderres);pixdx=w/(double)renderres;
					for(int n=0;n<renderres;n++){
						c=pix[pixbase+(int)(n*pixdx)];
						if((c&0xff)<0xf0 && (c&0xff00)<0xf000 && (c&0xff0000)<0xf00000){					// not white
							g.setColor(new Color(c));
							g.fillRect(rx+(int)(n*lx),ry+(int)(n*ly),blocksize,blocksize);
						}
					}
				}
			}
		}
		count=1;
	}
	int readyimages=0;
	int thick,renderh;
	public double perspective=1;
	public void repositionImages(){
		int ny=images.size();
		Point[] ps;
		thick=getHeight()*4/5/ny;
		renderh=(int)(perspective*getHeight()/4);
		int recede=(int)(50/perspective);
		for(int i=0;i<ny;i++){
			ps=(Point[])iCoords.get(i);
			int ni=ny-i-1;
			ps[0]=new Point(recede,thick*ni);
			ps[1]=new Point(getWidth()-recede,thick*ni);
			ps[2]=new Point(0,thick*ni+renderh);
			ps[3]=new Point(getWidth(),thick*ni+renderh);
		}
	}

	public void addImage(Image i){
		images.add(i);
		int ny=images.size();
		iCoords.add(new Point[4]);
		loaded.add(new Boolean(false));
		pixels.add(null);
		repositionImages();
		if(i.getWidth(this)>=0){	//load pixels immediately
			readytoload.add(new Boolean(true));
			loadPixels(i);
		}else readytoload.add(new Boolean(false));
	}
	public void loadPixels(Image i){
		loading=true;
		int j=images.indexOf(i);
		int w=i.getWidth(this),h=i.getHeight(this);
		int[] pix=new int[w*h];
		PixelGrabber pg=new PixelGrabber(i,0,0,w,h,pix,0,w);
		try{pg.grabPixels();
		}catch(Exception e){e.printStackTrace();}
		pixels.set(j,pix);
		loaded.set(j,new Boolean(true));
		if(++readyimages>=images.size()){
			buildGraphic();		// do the business!
			repaint();
		}else{
			for(int k=0;k<images.size();k++){
				if(!((Boolean)loaded.get(k)).booleanValue() && ((Boolean)readytoload.get(k)).booleanValue()){
					loadPixels((Image)images.get(k));
				}
			}	//what does this do?
		}
		loading=false;
	}
	boolean loading=false;
	public boolean imageUpdate(Image i,int inf,int x,int y,int w,int h){
		int j=images.indexOf(i);
		if(inf==ImageObserver.ALLBITS){
			if( !((Boolean)loaded.get(j)).booleanValue()){
				readytoload.set(j,new Boolean(true));
				if(!loading)loadPixels(i);
			}
		}
		count++;repaint();
		return super.imageUpdate(i,inf,x,y,w,h);
	}
}
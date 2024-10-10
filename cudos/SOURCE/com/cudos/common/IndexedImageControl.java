
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
import java.awt.event.*;
import java.awt.image.*;
import java.util.Random;
import java.util.*;

public class IndexedImageControl extends ImageControl {

	Image index;
	IndexedImageListener listener=null;
	protected static int[] lookup={ 0xc00000,0x800000,0x008000,0x808000,
					0x000080,0x800080,0x008080,0x00c000,
					0x0000c0,0xff0000,0x00ff00,0xffff00,
					0x0000ff,0xff00ff,0x00ffff,0xff80ff,
0x80ffff,
					0xffff80 };

	ImageRegion[] region=new ImageRegion[lookup.length];
	public boolean imageUpdate(Image i,int inf,int x,int y,int w,int h){
//System.out.println("image info "+inf+":"+y);
		if((i==index)&&((inf&ImageObserver.ALLBITS)>0)){
			indexloaded=true;
			if(region[0]==null)createRegions();
		}
		return super.imageUpdate(i,inf,x,y,w,h);
	}
	boolean regionsCreated=false;
	public void createRegions(){
		int w=index.getWidth(this),h=index.getHeight(this);
		int[] pix=new int[w*h];
		PixelGrabber pg=new PixelGrabber(index,0,0,w,h,pix,0,w);
		try {
		     pg.grabPixels();
		} catch (Exception x) {
			x.printStackTrace();
		}
		for(int i=0;i<lookup.length;i++){
			region[i]=new ImageRegion(pix,w,h,lookup[i]+0xff000000);
		}
		regionsCreated=true;
	}
	public IndexedImageControl() {
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int ix,iy;
				Point s=screenToImage(e.getPoint(),index);
				ix=s.x;iy=(int)s.y;
				int pix[]=new int[3];
				PixelGrabber pg=new PixelGrabber(index,ix,iy,1,1,pix,0,1);
				try {
				     pg.grabPixels();
				} catch (Exception x) {
					x.printStackTrace();
				}
				if(listener!=null)listener.indexedImageEvent(lookUpPix(pix[0]));
			}
		} );
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	Random rand=new Random();

	public void indexHighlight(int i,Color c,double alpha){
		int ix=getImageWidth(index),iy=getImageHeight(index);
		int searchval=lookup[i]+0xff000000;
		Graphics2D g=(Graphics2D)getGraphics();
		g.setColor(c);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.2f));
		if(indexloaded && regionsCreated)region[i].shade(g,c,alpha);
		else{
			if(!indexloaded) System.out.println("Index image not loaded yet.");
			else if(!regionsCreated) System.out.println("Regions not created yet.");
		}
	}
	public int lookUpPix(int p){
		int j=-1;
		for(int i=0;i<lookup.length;i++)
			if(p==lookup[i]+0xff000000){j=i;break;}
		return j;
	}
	public int getImageWidth(Image i){return i.getWidth(this);}
	public int getImageHeight(Image i){return i.getHeight(this);}
	public void setIndexedImageListener(IndexedImageListener l){
		listener=l;
	}
	boolean indexloaded=false;
	public void setIndex(Image i){
		index=i;
		if(index.getHeight(this)>=0){
			indexloaded=true;
			createRegions();
		}
		else indexloaded=false;
	}
	public Image getIndex(){return index;}

	class ImageRegion{
		int[][] bounds;//first index=y coordinate, next is array of x coordinates for line starts/stops;
		int height;
		public ImageRegion(int[]pixels, int width,int h,int colour){
			height=h;
			bounds=new int[height][];
			for(int y=0;y<height;y++){
				boolean inside=false;int currp=0;	//start each line outside region
				Vector b=new Vector();
				for(int x=0;x<width;x++){
					currp=pixels[x+width*y];
					if((!inside && currp==colour)||(inside &&(currp!=colour))){
						inside=!inside;
						b.add(new Integer(x));
					}
				}
				if(inside)b.add(new Integer(width-1));	//end each line outside region
				bounds[y]=new int[b.size()];
				for(int i=0;i<bounds[y].length;i++)
					bounds[y][i]=((Integer)b.get(i)).intValue();
			}
		}
		public void shade(Graphics2D g,Color c,double alpha){
			g.setColor(c);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)alpha));
			int sw=2;
			g.setStroke(new BasicStroke(sw/2));
			Point p1,p2;int b=0;
			for(int y=0;y<height;y++){
				if(b++%sw==0){
				for(int xi=0;xi<bounds[y].length;xi++){
					p1=imageToScreen(new Point(bounds[y][xi++],y),image);
					p2=imageToScreen(new Point(bounds[y][xi],y),image);
					g.drawLine(p1.x,p1.y,p2.x,p2.y);
				}
				}
			}
		}
	}
}
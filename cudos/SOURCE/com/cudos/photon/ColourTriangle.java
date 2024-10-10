
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.photon;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.event.*;

public class ColourTriangle extends JPanel implements Runnable {

	public ColourTriangle() {
	setColourSpace(new MacleodBoyntonSpace());
	addComponentListener(new ComponentAdapter(){public void componentResized(ComponentEvent e){
		init();
	}});
	thread.start();
	}
	public void addNotify(){
		super.addNotify();
		init();
	}
	public Vector receptors;
	public Object notifier;
	int cw=100,ch=100;
	public Raster colourRaster=Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,cw,ch,3,new Point(0,0));
	public ColorSpace CIE=ColorSpace.getInstance(ColorSpace.CS_CIEXYZ),
			RGB=ColorSpace.getInstance(ColorSpace.CS_sRGB),
			YCC=ColorSpace.getInstance(ColorSpace.CS_PYCC);

	public ColorSpace colourSpace;
	public void setColourSpace(ColorSpace c){
		colourSpace=c;
		setModel();
	}
	public ColorSpace getColourSpace(){return colourSpace;}
	public ColorModel colourModel;
	public void setModel(){
		colourModel=new ComponentColorModel(colourSpace, new int[]{8,8,8},
			false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
		fillPixels();
	}
	ColorModel dataColorModel=new DirectColorModel(24, 0xff<<16,0xff<<8,0xff);

	Thread thread=new Thread(this,"triangle updater");
	public void run(){
		while(true){
			if(notifier!=null) synchronized(notifier){try{notifier.wait();}
				catch(InterruptedException e){e.printStackTrace();}
			}else try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
			repaint();
		}
	}


		//initialisation fiunctions
	void init(){
		pixels=new int[getWidth()*getHeight()];
		fillPixels();
	}
	int[] pixels;
	void fillPixels(){
		int w=getWidth(),h=getHeight();
		for(int i=0;i<w;i++)for(int j=0;j<h;j++){
			pixels[i+j*w]=getRGBIntFromLUV(new float[]{
				0.5f, i/(float)w-0.5f, j/(float)h-0.5f });
		}
		imageSource=new MemoryImageSource(w,h,pixels,0,w);
		imageSource.setAnimated(true);
		image=createImage(imageSource);
	}
	Image image;
	MemoryImageSource imageSource;

	int indicatorRadius = 8;
	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		if(image!=null)g.drawImage(image,0,0,this);
		float[] f=getReceived();
		f[1]=Math.min(Math.max(f[1]+0.5f,0),1);
		f[2]=Math.min(Math.max(f[2]+0.5f,0),1);
		int	x=(int)(f[1]*getWidth()),
			y=(int)(f[2]*getHeight());
		g.setColor(Color.white);
		int r=indicatorRadius;
		g.drawLine(0,y,x,y);
		g.drawLine(x,0,x,y);
		g.fillOval(x-r,y-r,2*r,2*r);
		g.setColor(Color.black);
		g.fillOval(x-r+2,y-r+2,2*r-4,2*r-4);
	}


	// Returns vector of total photons, sw/lw catch and lw/mw catch
	public float[] getReceived(){
		float sw,mw,lw,total;
		try{
			sw=(float)((Photoreceptor)receptors.get(0)).activity;
			mw=(float)((Photoreceptor)receptors.get(1)).activity;
			lw=(float)((Photoreceptor)receptors.get(2)).activity;
			total=sw + mw + lw;
		}catch (Exception e){e.printStackTrace();return new float[]{0,0,0};}
		return new float[]{
			total / 3 , sw / (mw + lw) - 0.5f , lw / mw - 0.5f
		};
	}
	public int getRGBReceived(){
		return getRGBIntFromLUV(getReceived());
	}



	//Colour conversions
	public float[] getRGBfromLUV(float[] c){
		return colourSpace.toRGB(c);
	}
	public float[] getLUVfromRGB(float[] c){
		return colourSpace.fromRGB(c);
	}
	public Color getColourFromLUV(float[] c){
		float[] t=getRGBfromLUV(c);
		return new Color( ftr(t[0]),ftr(t[1]),ftr(t[2]) );
	}
	public int getRGBIntFromLUV(float[] c){
		float[] t=getRGBfromLUV(c);
		return 0xff<<24 | ftr(t[0])<<16 | ftr(t[1])<<8 | ftr(t[2]);
	}
	//convert [0,1] to [0,255]
	int ftr(float f){return Math.max(Math.min((int)(f*256),255),0);}
	int fts(float f){return Math.max(Math.min((int)f,255),0);}


}





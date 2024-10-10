
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.waves;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import com.cudos.common.*;

public class RippleTank extends DraggableContainer implements Runnable{
	int sx=5,sy=5;
	int dwidth=80,dheight=50;
	public boolean intank(Point p){
		if(p.getX()>sx*dwidth || p.getY()>sy*dheight)return false; else return true;
	}

	public RippleSource createSource(int x,int y){
		RippleSource rs=new RippleSource(x,y);
		sources.add(rs);
		addDraggable(rs);
                return rs;
	}
	public RippleWall createWall(int x,int y,int w,int h){
		RippleWall rw=new RippleWall(x,y,w,h);
		walls.add(rw);
		addDraggable(rw);
		setupWalls();
                return rw;
	}
        public void removeAll(){
          Vector owalls=(Vector)walls.clone();
          for(int i=0;i<owalls.size();i++) destroyDraggable((DraggableComponent)owalls.get(i));
          Vector osources=(Vector)sources.clone();
          for(int i=0;i<osources.size();i++) destroyDraggable((DraggableComponent)osources.get(i));
          setupWalls();
        }

	public void deleteSelected(){
		if(selected!=null)destroyDraggable(selected);
		setupWalls();
	}
	public void destroyDraggable(DraggableComponent m){
		super.destroyDraggable(m);
		if(m instanceof RippleSource)sources.remove(m);
		else if(m instanceof RippleWall)walls.remove(m);
		else System.out.println("cannot remove object");
		if(selected==m)setSelected(null);
		setupWalls();
	}
	public void setSelected(DraggableComponent s){
		if(selected!=null)selected.setSelected(false);		//tell component it is selected
		selected=s;
		if(s!=null)s.setSelected(true);
		getSelectionRecipient().setSelected(s);//allow container to select item
	}
	SelectionRecipient getSelectionRecipient(){
		Container t=this;
		while(t!=null && !(t instanceof SelectionRecipient))t=t.getParent();
		return (SelectionRecipient)t;
	}

	int command;
	public int getCommand(){return command;}
	public void setCommand(int c){command=c;}
	static final int COMMAND_SELECT=0,COMMAND_CREATESOURCE=1,COMMAND_CREATEWALL=2;

	int mx=0,my=0;
	boolean stimulus=false;
	float[][] px=new float[dwidth][dheight];
	float[][] vx=new float[dwidth][dheight];
	BufferedImage bi;
		public RippleTank() {
			//initialise tank arrays
		bi=new BufferedImage(dwidth,dheight,BufferedImage.TYPE_BYTE_INDEXED,icm());
		for(int i=0;i<dwidth*dheight;i++){
			px[i%dwidth][i/dwidth]=0;
			vx[i%dwidth][i/dwidth]=0;
		}

		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				switch(getCommand()){
					case COMMAND_SELECT:
						setSelected(null);break;
					case COMMAND_CREATESOURCE:
						if(intank(e.getPoint()))createSource(e.getX(),e.getY());
						break;
					case COMMAND_CREATEWALL:
						if(intank(e.getPoint()))createWall(e.getX(),e.getY(),15,100);
						break;
				}
			}
		});

		thread.start();
	}
	IndexColorModel icm(){
		byte[] r=new byte[256],g=new byte[256],b=new byte[256];
		Color c1=color1,c2=color2;
		for(int i=0;i<256;i++){
			r[i]=(byte)(c1.getRed()+(c2.getRed()-c1.getRed())*i/255);
			g[i]=(byte)(c1.getGreen()+(c2.getGreen()-c1.getGreen())*i/255);
			b[i]=(byte)(c1.getBlue()+(c2.getBlue()-c1.getBlue())*i/255);
		}
		return new IndexColorModel(8,256,r,g,b);
	}
	Color color1=Color.black,color2=Color.white;
	public Color getColor1(){return color1;}
	public Color getColor2(){return color2;}
	public void setColor1(Color c){color1=c;setColors();}
	public void setColor2(Color c){color2=c;setColors();}
	public void setColors(){
		BufferedImage bi2=new BufferedImage(dwidth,dheight,BufferedImage.TYPE_BYTE_INDEXED,icm());
		bi.copyData(bi2.getRaster());
		bi=bi2;	//remove old image
	}

	public DraggableComponent selected=null;
	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		g.drawImage(bi,0,0,dwidth*sx,dheight*sy,this);

		for(Enumeration e=walls.elements();e.hasMoreElements();){
			RippleWall rw=(RippleWall)e.nextElement();
			rw.paint(g.create(rw.getX(),rw.getY(),rw.getWidth(),rw.getHeight()));
		}
		for(Enumeration e=sources.elements();e.hasMoreElements();){
			RippleSource rs=(RippleSource)e.nextElement();
			rs.paint(g.create(rs.getX(),rs.getY(),rs.getWidth(),rs.getHeight()));
		}
		if(selected!=null)((Component)selected).repaint();
	}


	byte[][] walltype=new byte[dwidth][dheight];
	public void setupWalls(){	//called by child walls on move
		RippleWall cwall;
		for(int i=1;i<dwidth-1;i++)for(int j=1;j<dheight-1;j++){
			byte wall=0;
			for(Enumeration e=walls.elements();e.hasMoreElements();){
				if((cwall=(RippleWall)e.nextElement()).wallcontains(i*sx,j*sy)){
					if(cwall.getType()!=RippleWall.TYPE_RECTSTEP)wall=32;
					else wall=127;
				}
			}
			walltype[i][j]=wall;
		}
	}

	public void tick(){
//		if(stimulus)px[mx][my]=128+(float)(127*Math.sin(freq/70.*ticks++));
			//produce ripples at sources
		for(Enumeration e=sources.elements();e.hasMoreElements();){
			RippleSource rs=(RippleSource)e.nextElement();
			if(rs.getType()==RippleSource.TYPE_POINT){
				px[rs.getX()/sx][rs.getY()/sy]=rs.getNextValue();
			}else if(rs.getType()==RippleSource.TYPE_LINE){
				float value=rs.getNextValue();
				for(int i=0;i<dheight;i++){
					px[rs.getX()/sx][i]=value;
				}
			}
		}
			//calculate velocity&height
		for(int i=1;i<dwidth-1;i++){
			for(int j=1;j<dheight-1;j++){
				float k=getForce(i,j);
				vx[i][j]+=k*0.5;
			}
		}

			//copy array to screen
		int c;
		DataBuffer db=bi.getRaster().getDataBuffer();
		for(int i=1;i<dwidth-1;i++){
			for(int j=1;j<dheight-1;j++){
				px[i][j]+=vx[i][j];
				c=Math.abs((int)(px[i][j]*(contrast+50)/100.)+128);
				if(c>255)c=255;else if(c<0)c=0;
				db.setElem(i+j*dwidth,c);
			}
		}

		repaint();
	}
	float getForce(int i,int j){
		return getDH(i-1,j,i,j)+getDH(i+1,j,i,j)+getDH(i,j-1,i,j)+getDH(i,j+1,i,j);
	}
	public float getDH(int i,int j,int i0,int j0){
		if(i==0 || i==dwidth-1 || j==0 || j==dheight-1)return -vx[i0][j0];
		if(walltype[i][j]==0)return px[i][j]-px[i0][j0];
		if(walltype[i][j]<127)return -px[i0][j0];
		else return (px[i][j]-px[i0][j0])/2.5f;
	}

	Thread thread=new Thread(this);
	int ticks=0;
	boolean threadpaused=false;
	public void run(){while(true){
			if(!threadpaused)tick();
			try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
	}	}
	public void start(){threadpaused=false;}
	public void pause(){threadpaused=true;}
	public void clear(){
		for(int i=0;i<dwidth;i++){
			for(int j=0;j<dheight;j++){
				vx[i][j]=0;
				px[i][j]=0;
//				imagedata[i+dwidth*j]=(byte)128;
			}
		}
		repaint();
	}
	public Vector sources=new Vector(), walls=new Vector();
	int contrast=50;
	public void setContrast(int c){contrast=c;}
}

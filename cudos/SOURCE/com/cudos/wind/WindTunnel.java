
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.wind;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import com.cudos.common.*;

/*


						 vy[i][j]
			 |        |        |
	-----+--------|--------+-----
			 |        V        |
			 |                 |
			 |                 |
	 vx[i][j] -->    p[i][j]    --> vx[i+1][j]
			 |                 |
			 |                 |
			 |        |        |
	-----+--------|--------+-----
			 |        V        |
						vy[i][j+1]


	p=div v; v=-grad p;


dv/dt = -grad(p/r + X) - (v.del)v
	= -(v.del)v - (grad p)/r + n div grad v + g 		with viscosity
dr/dt = -div(r v) = -(grad r).v - r div v
p = r (v.v/2 + X)

*/

public class WindTunnel extends DraggableContainer implements Runnable{
	int sx=10,sy=15;
	int dwidth=40,dheight=25;
	int W = sx*dwidth, H = sy*dheight;

/*
	double     p[][]=new  double[dwidth+1][dheight+1];	// creates tunnel fields
	double    np[][]=new  double[dwidth+1][dheight+1];

	double    vx[][]=new  double[dwidth+1][dheight+1];
	double    vy[][]=new  double[dwidth+1][dheight+1];
	double   nvx[][]=new  double[dwidth+1][dheight+1];
	double   nvy[][]=new  double[dwidth+1][dheight+1];

	boolean fixp[][]=new boolean[dwidth+1][dheight+1];
*/
	boolean wall[][]=new boolean[dwidth+1][dheight+1];

	final int N = 1000;
	int v0 = 1;
	int[] x=new int[N], y=new int[N],
		a=new int[N];
	boolean[] hit=new boolean[N];

	public Vector walls=new Vector();

	public void restart(){				//initialise tunnel fields
		for(int i=0;i<N;i++){
			boolean h = true; while(h){
				x[i]=(int)(dwidth*Math.random());
				y[i]=(int)(dheight*Math.random());
				a[i]=(int)(2*Math.random()+1);
				h = isHit(i);
			}
		}
		particles.removeAllElements();
		setupWalls();
	}
	public void addWall(WindWall w){
		walls.add(w);
		this.add(w);
		setupWalls();
	}
	public void removeWall(WindWall w){
		walls.remove(w);
		this.remove(w);
		setupWalls();
	}
	public void removeAllWalls(){
		for(int i=0;i<walls.size();i++){
			this.remove((Component)walls.get(i));
		}
		walls.removeAllElements();
		setupWalls();
	}
	public void setupWalls(){
		Point pt;
		for(int i=1;i<dwidth-1;i++)for(int j=1;j<dheight-1;j++){
			pt=new Point(i*sx + sx/2,j*sy + sy/2);
			wall[i][j]=(j==0 || j==dheight-1);
			for(Enumeration en=walls.elements();en.hasMoreElements();){
				WindWall w = (WindWall)en.nextElement();
				Shape s = w.getShape();
				if(s.contains(pt)){
					wall[i][j]=true;
//					p[i][j]=0;
				}
			}
		}
	}
	public void setBlowerOn(boolean b){
/*		for(int i=0;i<dheight;i++){
			p[0][i]=b?drivingPressure:0;
			np[0][i]=0;
			p[dwidth-1][i]=0;
			np[dwidth-1][i]=0;
		}
*/  v0=b?1:0;
	}

	private double drivingPressure = 1;

	Color[] cols=new Color[256];			//colour interface
	Color color1=Color.black,color2=Color.white;
	public Color getColor1(){return cols[0];}
	public Color getColor2(){return cols[255];}
	public void setColor1(Color c){color1=c;setColors(color1,color2);}
	public void setColor2(Color c){color2=c;setColors(color1,color2);}
	public void setColors(Color c1,Color c2){
		for(int i=0;i<256;i++)cols[i]=new Color(c1.getRed()+(c2.getRed()-c1.getRed())*i/255,
			c1.getGreen()+(c2.getGreen()-c1.getGreen())*i/255,c1.getBlue()+(c2.getBlue()-c1.getBlue())*i/255);
	}

	public void run(){
		while(true){
			if(running)tick();
			try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
			Thread.yield();
		}
	}
	boolean running=false;
	public void start(){running=true;}
	public void stop(){running=false;}



	//CALCULATION






	double c=0.5;	//pressure per div v
	double d=0.5;	//vel per unit dp/dx
	private void tick(){
/*		for(int it=0;it<10;it++){
		for(int i=1;i<=dwidth;i++)for(int j=1;j<=dheight;j++){
			if(!wall[i][j]){
				if(!wall[i-1][j])vx[i][j]= -d*(p[i][j]-p[i-1][j]);
				else vx[i][j]=0;
				if(!wall[i][j-1])vy[i][j]= -d*(p[i][j]-p[i][j-1]);
				else vy[i][j]=0;
			}else vx[i][j]=vy[i][j]=0;
		}
		for(int i=1;i<dwidth;i++)for(int j=1;j<dheight;j++){
			p[i][j]+= c*(vx[i][j]-vx[i+1][j] + vy[i][j]-vy[i][j+1]);
		}
		}
		//if(mx!=0 && my!=0)System.out.println("@("+mx+","+my+")="+p[mx][my]+"p,("+vx[mx][my]+","+vy[mx][my]+")v");
	*/
		for(int i=0;i<N;i++){
			if( isHit(i) ){

			}
			int vx, vy;
			switch(a[i]){
				case 0: vy=1;break;
				case 1: vx=1;
					if(x[i]%2==1) vy=1;
					break;
				case 2: vx=1;
					if(x[i]%2==0) vy=-1;
					break;
				case 3: vy=-1;break;
				case 4: vx=-1;
					if(x[i]%2==0) vy=-1;
					break;
				case 5: vx=-1;
					if(x[i]%2==1) vy=1;
			}
/*			if(hwall &&! hit[i]){
				if(vx[i]>0 && wall[inx+1][iny])
				if(vx[i]<dwidth && wall[inx-1][iny])
{ }
			}
			hit[i]=hwall;
*/
		}
		repaint();
	}
	final boolean isHit(int i){
		return wall[(int)x[i]][(int)y[i]];
	}








	WindTunnel me;	 //represents 'this' in inner classes == 'outer'
	int mx=0,my=0;
	public WindTunnel() {
		restart();
		addMouseListener(new MouseAdapter(){			// create particles on click
			public void mousePressed(MouseEvent e){createFrom=e.getPoint();}
			public void mouseReleased(MouseEvent e){createFrom=null;}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){createFrom=e.getPoint();}
			public void mouseMoved(MouseEvent e){
				mx=e.getX()/sx;my=e.getY()/sy;
				if(mx<0||mx>dwidth)mx=0;if(my<0||my>dheight)my=0;
			}
		});
		thread.start();
	}

	Thread thread=new Thread(this);
	Vector particles=new Vector();
	Point createFrom=null;
	Random rand=new Random();
	int particleDispersion = 20;
	public void doCreateParticle(){
		if(createFrom!=null){
			int px=(int)(createFrom.getX()/sx),py=(int)(createFrom.getY()/sy);
			int disp = particleDispersion;
			if(px<dwidth && px>0 && py<dheight && py>0){
				for(int i=0;i<5;i++){
					particles.add(new Particle(me,
						createFrom.x+(rand.nextInt()%disp)-disp/2,
						createFrom.y+(rand.nextInt()%disp)-disp/2) );
				}
			}
		}
	}

	double arrowscale=100;
	int dr=2;
	boolean linesVisible=true;
	double linesProp=0.2;
	public void paint(Graphics g){
		super.paint(g);

//		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(linesVisible){
		for(int i=0;i<dwidth;i++){
			for(int j=0;j<dheight;j++){
//				if(rand.nextDouble()>linesProp)continue;
	//			g.setColor( cols[toIdx(p[i][j]*128+128)] );
//				g.fillOval(i*sx-dr,j*sx-dr,dr*2,dr*2);
	//			g.drawLine(i*sx,j*sy+i%sy,i*sx+(int)(vx[i][j]*sx*arrowscale),j*sy+i%sy+(int)(vy[i][j]*sy*arrowscale));
//				if(wall[i][j])g.fillRect(i*sx,j*sy,sx,sy);
					//else g.fillOval(i*sx-2,j*sy-2,4,4);
			}
		}
		}

		doCreateParticle();
		for(Enumeration en=particles.elements();en.hasMoreElements();){
			Particle pa=(Particle)en.nextElement();
			int px=(int)(pa.x/sx),py=(int)(pa.y/sy);
			boolean die=false;
			if(px<dwidth && px>0 && py<dheight && py>0){
//				pa.vx=vx[px][py]*1000;
//				pa.vy=vy[px][py]*1000;
				if(pa.vx==0 && pa.vy==0)die=true;
				pa.paint(g);
			}else die=true;
			if(die)particles.remove(pa);
		}
	}
	public int toIdx(double d){if(d<0)return 0;else if(d<256)return (int)d;else return 255;}
	public double getDrivingPressure() {
		return drivingPressure;
	}
	public void setDrivingPressure(double drivingPressure) {
		this.drivingPressure = drivingPressure;
		setBlowerOn(true);
	}
}
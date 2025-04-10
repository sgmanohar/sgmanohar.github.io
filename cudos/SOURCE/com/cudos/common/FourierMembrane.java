
package com.cudos.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

public class FourierMembrane extends JPanel implements Runnable,MouseMotionListener{
	public FourierMembrane(){
		setup();
		addMouseMotionListener(this);
		setBackground(Color.black);
		Thread thread=new Thread(this);
		thread.start();
	}

		// Interface
	public void stimulate(double d){for(int i=0;i<N;i++)dx[i]+=d;}
	public void setN(int nn){nn=N;setup();}
	public int getN(){return N;}
	public String text="Basilar Membrane (Fourier Analysis)";
	public double k=1, damp=0.9;
	public int timer=30;

		// Implementation
	private int N=100;
	private double[] x,dx,av;
	private void setup(){
		x=new double[N];
		dx=new double[N];
		av=new double[N];
	}
		//display
	public void paint(Graphics g){
		super.paint(g);
//		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int w=getSize().width, mid=getSize().height/2;
		for(int i=0;i<N;i++){
			g.setColor(new Color(
				tocol(Math.sin(((double)i)/N)),
				tocol(Math.cos(((double)i)/N)),
				tocol(x[i]/mid) ));
			g.drawLine(i*w/N, mid, i*w/N, mid+(int)(x[i]) );
			g.fillOval(i*w/N, mid+(int)(x[i]), 6,6);
			if(i!=0)
			g.drawLine((i-1)*w/N,mid-(int)(av[i-1]*0.05),i*w/N,mid-(int)(av[i]*0.05) );
		}
		g.setColor(Color.red);
		g.setFont(new Font("Arial",Font.PLAIN,12));
		g.drawString(text,5,30);
	}
	private int tocol(double a){a=127+127*a;if(a>255)return 255;else if(a<0)return 0;else return (int)a;}

		//calculate
	void tick(){
		for(int i=0;i<N;i++){
			x[i]+=dx[i];		// dx/dt
			dx[i]-=k*x[i]*i/N;	// dv/dt
			dx[i]*=damp;		// damping
			av[i]=(av[i]+Math.abs(x[i]))*damp;
		}
	}

		//thread
	public void run(){
		while(true){
			tick();
			repaint();
			try{Thread.sleep(timer);}catch(Exception ex)
			{ex.printStackTrace();}
		}
	}

		//MouseMotionListener
	int ox;
	public void mouseDragged(MouseEvent e){}
	public void mouseMoved(MouseEvent e){
		int nx=e.getX();
		stimulate(nx-ox);
		ox=nx;
	}
}
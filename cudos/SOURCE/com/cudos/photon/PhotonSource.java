
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.photon;

import com.cudos.common.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class PhotonSource extends DraggableComponent implements Runnable{

	JToggleButton onoff=new JToggleButton();
	JLabel wlabel=new JLabel();
		//Create components: label for wavelength, on off switch
	public PhotonSource() {
		setLayout(null);
		setOpaque(false);		//needs components to paint on top of image!
		setSize(new Dimension(64,32));
		thread.start();

			//switch
		add(onoff);
		onoff.setSize(new Dimension(8,8));
		onoff.setLocation(4,2);
		onoff.setBackground(Color.red);
onoff.setCursor(new Cursor(Cursor.HAND_CURSOR));
		onoff.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
			if(onoff.isSelected())
				{start();onoff.setBackground(Color.green);}
			else	{stop();onoff.setBackground(Color.red);}
			if(actionlistener!=null)
				actionlistener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"OnOffPressed"));
		}});

			//wavelenght label
		add(wlabel);
		wlabel.setLocation(5,19);
		wlabel.setSize(35,12);
		wlabel.setFont(new Font("Dialog",Font.PLAIN,9));
		wlabel.setBackground(Color.black);
		wlabel.setForeground(Color.green);
		wlabel.setOpaque(true);
		wlabel.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		wlabel.addMouseListener(new MouseAdapter(){public void mousePressed(MouseEvent e){
			my=e.getY();
		}});
		wlabel.addMouseMotionListener(new MouseMotionAdapter(){public void mouseDragged(MouseEvent e){
			int dy=e.getY()-my;
			my=e.getY();
			setWavelength(getWavelength()-dy);
		}});

		setForeground(Color.red);

		//initial wavelength
		setWavelength(600);
	}
	int my;	//previous mouse y coords
	public PhotonSource(int wavelength){
		this();
		setWavelength(wavelength);
	}
	static Image image;
	public void paint(Graphics g){
		if(image!=null){
			g.drawImage(image,0,0,this);
			g.fill3DRect(4,11,(int)(getWidth()*getIntensity()*15),10,true);
		}
		super.paint(g);
	}
	public void addNotify(){
		super.addNotify();
		if(image==null)image=CudosExhibit.getApplet(this).getImage("resources/icons/photoreceptors/photonsource.gif");
	}
	public void dragTo(Point p){
		PhotoreceptorPanel pp=(PhotoreceptorPanel)getParent();
		p.x=Math.min(p.x,pp.maxSourceX-getWidth());
		super.dragTo(p);
	}



	int wavelength;
	int delay=100;
	ActionListener actionlistener=null;

	public int getWavelength(){
		return wavelength;
	}
	public void setWavelength(int wavelength){
		wavelength=Math.max(Math.min(wavelength,800),300);
		this.wavelength=wavelength;
		wlabel.setText(String.valueOf(wavelength)+" nm");
		if(actionlistener!=null)actionlistener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"WavelengthChange"));
	}
	public double getIntensity(){
		return 1./delay;
	}
	public void setIntensity(double i){
		delay=(int)(1/i);
	}
	public void addActionListener(ActionListener al){
		actionlistener=AWTEventMulticaster.add(al,actionlistener);
	}
	public void removeActionListener(ActionListener al){
		actionlistener=AWTEventMulticaster.remove(al,actionlistener);
	}



	Thread thread=new Thread(this);
	public void run(){
		while(true){
			if(running){
				PhotoreceptorPanel p=(PhotoreceptorPanel)getParent();
				synchronized(this){synchronized(p.photons){synchronized(p){synchronized(p.photoreceptors){
					if(p == null) return;	//thread dies
					Photon photon=new Photon();
					photon.setWavelength(wavelength);
					int h=(int)(Math.random()*getHeight());
					photon.setLocation( getX() + getWidth(), getY()+h );
					p.photons.add(photon);
					p.add(photon);
				}}}}
				try{Thread.sleep(delay);}
				catch(Exception e){e.printStackTrace();}
			}else{
				try{synchronized(this){wait();}}
				catch(Exception e){e.printStackTrace();}
			}
		}
	}

	boolean running=false;
	public synchronized void start(){
		running=true;
		notifyAll();
	}
	public void stop(){
		running=false;
	}
	public boolean isRunning(){return running;}

		//Handle mouse events


}
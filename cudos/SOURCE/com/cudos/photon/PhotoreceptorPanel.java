
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
import java.util.*;
import java.awt.event.*;

public class PhotoreceptorPanel extends DraggableContainer implements Runnable, ActionListener{

	public PhotoreceptorPanel() {
		setLayout(null);
		thread.start();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	public Vector	photons=new Vector(),
			photoreceptors=new Vector(),
			photonsources=new Vector();

	int photoreceptorX=200, maxSourceX=80;

	boolean running=true, terminated=false;
	public Object notifier=new Object();
	Thread thread=new Thread(this,"Main panel thread");
	Vector deadPhotons=new Vector();
	public void run(){
		while(!terminated){
		while(running){
			//tick each photon and collision check
			photons.removeAll(deadPhotons);			//remove from list
			synchronized(this){
//				RepaintManager rm=RepaintManager.currentManager(this);
				for(int i=0;i<deadPhotons.size();i++){
					Photon p=(Photon)deadPhotons.get(i);
					Rectangle r=p.getBounds();
					remove(p);
					repaint(r);
//					rm.removeInvalidComponent(p);
				}
			}
			deadPhotons=new Vector();
			synchronized(photons){
				for(int i=0;i<photons.size();i++){
					Photon photon=(Photon)photons.get(i);
					photon.move();
					if(photon.getX()+photon.getWidth()>=photoreceptorX){
						//has it passed the left edge of the receptors?
						deadPhotons.add(photon);
						for(int j=0;j<photoreceptors.size();j++){
							//has it landed on one of the photoreceptors?
							Photoreceptor r=(Photoreceptor)photoreceptors.get(j);
							if(photon.getY()+photon.getHeight()>r.getY() &&
								 photon.getY()<r.getY()+r.getHeight() ){
								r.receivePhoton(photon);
								break;
							}
						}
					}
				}
			}

			//tick each photoreceptor (i.e. alter its activity & repaint it)
			for(int i=0;i<photoreceptors.size();i++){
				Photoreceptor r=(Photoreceptor)photoreceptors.get(i);
				r.tick();
			}
			if(notifier!=null)synchronized(notifier){notifier.notifyAll();}

			//wait a bit
			try{Thread.sleep(100);}catch(Exception  e){e.printStackTrace();}
		}
		//stopped running temporarily?
		try{synchronized(this){wait();}}
		catch(Exception e){e.printStackTrace();}
		}
	}

	//ActionListener chains events to any listeners
	ActionListener al;
	public void actionPerformed(ActionEvent e){
		if(al!=null)al.actionPerformed(e);
	}



	//INTERFACE



		//startin and stoppin

	public synchronized void start(){
		running=true;
		notifyAll();
	}
	public void stop(){
		running=false;
	}
	public synchronized void terminate(){
		terminated=true;
		running=false;
		notifyAll();	//in case already stopped
	}


		//adding and removing
	int recpos=0;
	public void addPhotoreceptor(int type){
		Photoreceptor r=new Photoreceptor(type);
		add(r);
		r.setLocation(photoreceptorX,190+40*recpos++);
		photoreceptors.add(r);
		repaint();
	}
	int srcpos=0;
	public void addPhotonSource(){
		PhotonSource s=new PhotonSource();
		s.addActionListener(this);
		add(s);
		s.setLocation(10,190+50*srcpos++);
		photonsources.add(s);
		repaint();
	}
	public synchronized void removePhotonSource(){
		PhotonSource s=(PhotonSource)photonsources.get(photonsources.size()-1);
		synchronized(s){synchronized(photonsources){
			remove(s);
			photonsources.remove(s);
		}}
		repaint();
	}
	public void addActionListener(ActionListener al){
		this.al=AWTEventMulticaster.add(al,this.al);
	}
	public void removeActionListener(ActionListener al){
		this.al=AWTEventMulticaster.remove(al,this.al);
	}


		//repositioning of photons at the start
	int photonstop(){return getHeight()*3/7;}
	int photonsbottom(){return getHeight()*5/7;}
	public synchronized void add(Photon p){
		p.setLocation(maxSourceX,photonstop()+(int)(Math.random()*(photonsbottom()-photonstop())) );
		super.add(p);
	}
	public synchronized void remove(Photon p){
		super.remove(p);
	}

	public synchronized void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(maxSourceX-5,photonstop(),5,photonsbottom()-photonstop());
	}
	private void jbInit() throws Exception {
	}

}
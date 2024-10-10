
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import com.neurolab.common.*;
import java.awt.event.*;

public class SynapticInteraction extends NeurolabExhibit {
 public String getExhibitName() {
    return "Synaptic Interaction";
  }
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  Oscilloscope osc = new Oscilloscope(1,new ActionListener(){public void actionPerformed(ActionEvent e){}});
  ReturnButton returnButton1 = new ReturnButton();
  Label3D label3D1 = new Label3D();
  JLabel picture = new JLabel(){
	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(cellimage,0,0,new ImageObserver(){
			public boolean imageUpdate(Image i,int f,int x,int y,int w,int h){
				if((f&ImageObserver.ALLBITS)>0){picture.repaint(); return false;}
				return true;
			}
		});
	}
  };
	Timer timer = new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){ //regular calculations
			for(int i=0;i<6;i++)c[i]*=0.8;	//decay synapses
				//calculate conduction in dendrites
			double dx1,dx2,dX;
			dx1= (-85 * c(5) - 70 * crest) / (c(5) + crest + c(1) + c(2));
			x1=tau*x1+(1-tau)*dx1;
			dx2= (-85 * c(4) - 70 * crest) / (c(4) + crest + c(3));
			x2=tau*x2+(1-tau)*dx2;
			dX = (-60 * crest - 85 * c(6) + x1 * cden + x2 * cden) / (crest + c(6) + cden + cden);
			X =tau*X +(1-tau)*dX;
				//set oscilloscope and sound
			osc.setPosY(new int[]{180-(int)((X+70)*10)});
			ap.setRate(Math.max(X+70,0));
		}
	});
	double c(int i){return c[i-1];}
	double X=-70,x1,x2;
	double[] c=new double[6];
	double tau=0.8,crest=0.1,cden=0.1;
	Image cellimage;
	ActionPotentials ap=new ActionPotentials();
	public String getName(){return "Synaptic interaction";}
	PixelGrabber pg;
	byte[] pixel=new byte[1];
	public SynapticInteraction() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		} catch(Exception e) {
			e.printStackTrace();
		}
		cellimage=getImage("resources/SynapseCell.gif");
		osc.setBaseY(new int[]{30});
//		picture.setIcon( new ImageIcon(cellimage) );
		picture.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){	//handle click event
				pg=new PixelGrabber(cellimage,e.getX(),e.getY(),1,1,false);
				try{
					pg.grabPixels(200);
				}catch(Exception x){x.printStackTrace();}
				pixel=(byte[])pg.getPixels();
				switch(pixel[0]){
					case 0: case 1: case 8: break;
					default: c[pixel[0]-2]+=10;
				}
			}
		});
		ap.timer.start();
		timer.start();
	}
	public void close() {
		ap.timer.stop();
		timer.stop();
	}

  private void jbInit() throws Exception {
    osc.setGutter(5);
    osc.setBounds(new Rectangle(313, 13, 254, 150));
    jPanel1.setLayout(null);
    returnButton1.setBounds(new Rectangle(486, 272, 71, 27));
    label3D1.setSize(new Dimension(100, 20));
    label3D1.setFont(new java.awt.Font("Dialog", 0, 18));
    label3D1.setText("Click on a synapse to activate it");
    label3D1.setBounds(new Rectangle(14, 276, 324, 32));
    picture.setBounds(new Rectangle(6, 5, 289, 269));
    jPanel1.add(osc, null);
    jPanel1.add(label3D1, null);
    jPanel1.add(picture, null);
    jPanel1.add(returnButton1, null);
    jPanel1.setBackground(systemGray);
    getMainContainer().setLayout(borderLayout1);
    getMainContainer().add(jPanel1, BorderLayout.CENTER);
  }
}
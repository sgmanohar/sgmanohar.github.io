package com.cudos.photon;

import com.cudos.common.*;

import javax.swing.*;
import java.awt.*;

public class Photoreceptor extends DraggableComponent {

	public static int SHORT_WAVELENGTH=0, MEDIUM_WAVELENGTH=1, LONG_WAVELENGTH=2;

	int type;
	JLabel wlabel=new JLabel();
	public Photoreceptor(int type) {
		setSize(new Dimension(64,32));
		this.type=type;

			//appearance
		setLayout(null);
		setOpaque(false);
		add(wlabel);
		wlabel.setLocation(22,19);
		wlabel.setSize(35,12);
		wlabel.setFont(new Font("Dialog",Font.PLAIN,9));
		wlabel.setBackground(Color.black);
		wlabel.setForeground(Color.green);
		wlabel.setOpaque(true);

			//spectrum
		setup();
		wlabel.setText( getPeakWavelength() + " nm");
	}
	public void dragTo(Point p){
		PhotoreceptorPanel pp=(PhotoreceptorPanel)getParent();
		p.x=pp.photoreceptorX;
		super.dragTo(p);
	}
	static Image image;
	public void paint(Graphics g){
		if(image!=null){
			g.drawImage(image,0,0,this);
			g.setColor(Color.red);
			g.fill3DRect(19,11,(int)(getWidth()*activity*0.5),10,true);
		}
		super.paint(g);
	}
	public void addNotify(){
		super.addNotify();
		if(image==null)image=CudosExhibit.getApplet(this).getImage("resources/icons/photoreceptors/photoreceptor.gif");
	}



	int baseWavelength;		//the wavelength corresponding to absorptionSpectrum[0]

	double[] absorptionSpectrum;	//the probability of absorbing a photon of a given wavelength
	int peakWavelengthRelative;	//the position of the logical peak in the spectrum data

	public double activity;

	protected float overallSensitivity = 0.2f;

	public double getAbsorption(int wavelength){
		int i=wavelength-baseWavelength;
		if(i>0 && i<absorptionSpectrum.length)
			return absorptionSpectrum[i];
		else return 0;
	}
	public int getPeakWavelength(){
		return peakWavelengthRelative+baseWavelength;
	}
	public void setPeakWavelength(int p){
		baseWavelength=p-peakWavelengthRelative;
	}


		//interface with panel

	public void receivePhoton(Photon photon){
		activity += overallSensitivity * getAbsorption(photon.wavelength);
		this.repaint();
	}
	public void tick(){
		activity*=0.96;
		this.repaint();
	}


public void setup(){		//this bit is all made up and should load the actual data for the absorption spectrum
	baseWavelength=300+type*100;
	absorptionSpectrum=new double[300];
	int mid=absorptionSpectrum.length/2;
	peakWavelengthRelative = mid;
	for(int i=0;i<absorptionSpectrum.length;i++)
		absorptionSpectrum[i]= 0.6 * Math.exp(-(i-mid)*(i-mid)/4500.);
}


}
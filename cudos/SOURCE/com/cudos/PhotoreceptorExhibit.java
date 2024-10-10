
/**
(C) Sanjay Manohar 2001
 */
package com.cudos;

import com.cudos.common.*;
import java.awt.*;
import javax.swing.*;
import com.cudos.photon.*;
import java.awt.event.*;
import javax.swing.border.*;

public class PhotoreceptorExhibit extends CudosExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JButton jButton2 = new JButton();
	PhotoreceptorPanel photoreceptorPanel = new PhotoreceptorPanel();
	Border border1;
	JPanel jPanel2 = new JPanel();
	WavelengthGraph wavelengthGraph = new WavelengthGraph();
	BorderLayout borderLayout2 = new BorderLayout();
	ColourTriangle colourTriangle = new ColourTriangle();
	ColourLabel jLabel1 = new ColourLabel();
	JButton jButton1 = new JButton();
	JPanel jPanel3 = new JPanel();
	Border border2;
	TitledBorder titledBorder1;
	JPanel jPanel4 = new JPanel();
	JPanel colourpanel = new JPanel();
	Border border3;
	TitledBorder titledBorder2;
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel5 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	JButton jButton3 = new JButton();
	private JPanel jPanel6 = new JPanel();


	public PhotoreceptorExhibit() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		//connect components together
	wavelengthGraph.receptors=photoreceptorPanel.photoreceptors;
	colourTriangle.receptors=photoreceptorPanel.photoreceptors;
	wavelengthGraph.sources=photoreceptorPanel.photonsources;

	colourTriangle.notifier=photoreceptorPanel.notifier;
	jLabel1.notifier=photoreceptorPanel.notifier;
	jLabel1.colourTriangle=colourTriangle;

		//add receptors
	photoreceptorPanel.addPhotoreceptor(Photoreceptor.SHORT_WAVELENGTH);
	photoreceptorPanel.addPhotoreceptor(Photoreceptor.MEDIUM_WAVELENGTH);
	photoreceptorPanel.addPhotoreceptor(Photoreceptor.LONG_WAVELENGTH);

	}


	private void jbInit() throws Exception {
		border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(3, 3, 3),new Color(4, 4, 4),Color.black,Color.black);
		border2 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border2,"Photon source");
		border3 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder2 = new TitledBorder(border3,"Perceived colour");
		this.getContentPane().setLayout(borderLayout1);
		jButton2.setText("Add");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jPanel1.setPreferredSize(new Dimension(160, 37));
		jPanel1.setLayout(borderLayout4);
		photoreceptorPanel.setBackground(new Color(255, 208, 255));
		photoreceptorPanel.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				photoreceptorPanel_actionPerformed(e);
			}
		});
		photoreceptorPanel.setLayout(null);
		photoreceptorPanel.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				photoreceptorPanel_actionPerformed(e);
			}
		});
		wavelengthGraph.setForeground(new Color(0, 192, 0));
		wavelengthGraph.setPreferredSize(new Dimension(150, 200));
		wavelengthGraph.setDrawLabels(true);
		jPanel2.setBorder(BorderFactory.createLoweredBevelBorder());
		jPanel2.setPreferredSize(new Dimension(304, 154));
		jPanel2.setLayout(borderLayout2);
		colourTriangle.setPreferredSize(new Dimension(100, 100));
		jLabel1.setOpaque(true);
		jLabel1.setPreferredSize(new Dimension(10, 20));
		jLabel1.setBounds(new Rectangle(131, 47, 41, 17));
		jButton1.setText("Remove");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel3.setBorder(titledBorder1);
		colourpanel.setBorder(titledBorder2);
		colourpanel.setLayout(borderLayout3);
		jButton3.setText("Exit");
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton3_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.EAST);
		jPanel1.add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(jButton2, null);
		jPanel3.add(jButton1, null);
		jPanel1.add(jPanel5, BorderLayout.SOUTH);
		jPanel5.add(jButton3, null);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(wavelengthGraph, BorderLayout.CENTER);
		this.getContentPane().add(photoreceptorPanel, BorderLayout.CENTER);
		colourpanel.add(jLabel1, BorderLayout.NORTH);
		this.getContentPane().add(jPanel4, BorderLayout.WEST);
		jPanel4.add(colourpanel, null);
		colourpanel.add(colourTriangle, BorderLayout.CENTER);
		this.getContentPane().add(jPanel6,  BorderLayout.SOUTH);
	}



	void jButton2_actionPerformed(ActionEvent e) {
	photoreceptorPanel.addPhotonSource();
	wavelengthGraph.repaint();
	}

	//this is called when there is a change in the wavelengths
	void photoreceptorPanel_actionPerformed(ActionEvent e) {
	wavelengthGraph.repaint();
	}

	void jButton1_actionPerformed(ActionEvent e) {
	photoreceptorPanel.removePhotonSource();
	wavelengthGraph.repaint();
	}

	void jButton3_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
	}
}

class ColourLabel extends JLabel implements Runnable {
	Thread thread=new Thread(this,"Colour updater");
	public ColourLabel(){
		thread.start();
	}
	Object notifier;
	ColourTriangle colourTriangle;
	public void run(){
		while(true){
			if(notifier!=null)try{
				synchronized(notifier){notifier.wait();}
			}catch(InterruptedException e){e.printStackTrace();}
			else try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
			if(colourTriangle!=null)setBackground(new Color(colourTriangle.getRGBReceived()));
		}
	}
}
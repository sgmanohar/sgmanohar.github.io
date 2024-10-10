
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab;

import java.awt.*;
import javax.swing.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Random;

public class SoundAndHearing extends NeurolabExhibit {
 public String getExhibitName() {
		return "Sound and Hearing";
	}
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel4 = new JPanel0();
	JRadioButton jRadioButton1 = new JRadioButton0();
	GridLayout gridLayout1 = new GridLayout();
	JRadioButton jRadioButton2 = new JRadioButton0();
	JRadioButton jRadioButton3 = new JRadioButton0();
	JRadioButton jRadioButton4 = new JRadioButton0();
	JRadioButton jRadioButton5 = new JRadioButton0();
	JRadioButton jRadioButton6 = new JRadioButton0();
	JRadioButton jRadioButton7 = new JRadioButton0();
	JRadioButton jRadioButton8 = new JRadioButton0();
	JPanel jPanel5 = new JPanel0();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel6 = new JPanel0();
	JPanel jPanel7 = new JPanel0();
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel8 = new JPanel0();
	Border border1;
	JPanel jPanel9 = new JPanel0();
	BorderLayout borderLayout5 = new BorderLayout();
	Label3D label3D1 = new Label3D();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel11 = new JPanel0();
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel12 = new JPanel0();
	JRadioButton jRadioButton9 = new JRadioButton0();
	GridLayout gridLayout3 = new GridLayout();
	JRadioButton jRadioButton10 = new JRadioButton0();
	JRadioButton jRadioButton11 = new JRadioButton0();
	JRadioButton jRadioButton12 = new JRadioButton0();
	Border border2;
	TitledBorder titledBorder1;
	JPanel jPanel13 = new JPanel0();
	BorderLayout borderLayout8 = new BorderLayout();
	Border border3;
	JButton listen = new JButton();
	JPanel jPanel14 = new JPanel0();
	BorderLayout borderLayout9 = new BorderLayout();
	JPanel jPanel15 = new JPanel0();
	Label3D label3D2 = new Label3D();
	ButtonGroup bg2=new ButtonGroup();
	ButtonGroup bg1=new ButtonGroup();
	JPanel space = new JPanel0();
	JPanel place = new JPanel0();


	JPanel waveform = new JPanel(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		g.setColor(Color.green);
		Point o,n=new Point(2,128);
		int d,m;
		for(int i=2;i<getWidth()-2;i++){
			o=n;
			m=(i*2)%480;
			d=wave[m];
			n=new Point(i,d);
			g.drawLine(o.x,o.y/3+10,n.x,n.y/3+10);
		}
	}
	};
	Spectrum spectrum = new Spectrum();
class Spectrum extends JPanel{
	int base=5;
	int selection=1;
	public Spectrum(){
		MouseInputAdapter mia=new MouseInputAdapter(){
			int oy;
			public void mouseClicked(MouseEvent e){
				mousePressed(e);
			}
			public void mousePressed(MouseEvent e){
				selection=e.getX()*25/(getWidth()-10);
				if(selection<1)selection=1;else if(selection>24)selection=24;
				paintImmediately(new Rectangle(0,0,1000,1000));
				redrawSelection();
				oy=e.getY();
			}
			public void mouseDragged(MouseEvent e){
				ampl[selection]-=e.getY()-oy;
				ampl[selection]=(byte)(int)Math.min(ampl[selection],getHeight()-10);
				oy=e.getY();
				doWave();
				waveform.repaint();
				redrawSelection();
			}
		};
		addMouseListener(mia);
		addMouseMotionListener(mia);
		setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
	}
	public void redrawSelection(){
		Graphics g=getGraphics();
		g.setColor(Color.black);
		g.fillRect(selection*(getWidth()-10)/25,2,(getWidth()-10)/25-1,getHeight()-base-2);
		g.fillRect(2,2,20,20);
		g.setColor(Color.yellow);
//		g.fillRect(selection*(getWidth()-10)/25,getHeight()-ampl[selection]-base,(getWidth()-10)/25-1,ampl[selection]);
		g.fillRect((2*selection+1)*(getWidth()-10)/(25*2)-barw/2,getHeight()-ampl[selection]-base,barw,ampl[selection]);
		g.drawString(String.valueOf(selection),2,15);
		phase[ph[selection]].setSelected(true);
	}
	int barw=2;
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.red);
		g.drawLine(5,getHeight()-base,getWidth()-5,getHeight()-base);
		g.setColor(Color.green);
		for(int i=1;i<25;i++){
//			g.fillRect(i*(getWidth()-10)/25-barw,getHeight()-ampl[i]-base,(getWidth()-10)/25-1,ampl[i]);
			g.fillRect((2*i+1)*(getWidth()-10)/(25*2)-barw/2,getHeight()-ampl[i]-base,barw,ampl[i]);
		}
		redrawSelection();
	}
	};

	byte[] ampl=new byte[25],ph=new byte[25];
	byte[][] preamp=new byte[8][25];
	byte[][] preph=new byte[8][25];
	public void setupPresets(){
		Random rand=new Random();
		for(int i=0;i<8;i++)for(int j=0;j<25;j++)preamp[i][j]=preph[i][j]=0;
		preamp[0][1]=60;	//sin
		preamp[1][1]=100;preamp[1][3]=33;preamp[1][5]=20;preamp[1][7]=14;
		preamp[1][9]=11;preamp[1][11]=9;preamp[1][13]=7;preamp[1][15]=6;
		preamp[1][17]=5;preamp[1][19]=5;preamp[1][21]=5;preamp[1][23]=4;	//sq
		preamp[2][1]=108;preph[2][1]=1;preamp[2][3]=12;preph[2][3]=1;		//tri
		preamp[2][5]=4;preph[2][5]=1;preamp[2][7]=2;preph[2][7]=1;
		preamp[3][1]=63;preamp[3][3]=63;	//1+3
		preamp[4][8]=63;preamp[4][9]=63;	//beats
		for(int i=1;i<25;i++){
			preamp[5][i]=5;
			preamp[6][i]=(byte)(30/i);
			preamp[7][i]=(byte)rand.nextInt(20);
		}
	}

	JRadioButton[] phase=new JRadioButton[]{jRadioButton9,jRadioButton10,jRadioButton11,jRadioButton12};
	JRadioButton[] preset=new JRadioButton[]{jRadioButton1,jRadioButton2,jRadioButton3,jRadioButton4,jRadioButton5,jRadioButton6,jRadioButton7,jRadioButton8};
	int[] wave=new int[480];
	final int N=4;
	byte[] buf=new byte[96*N];
	CustomSound sound=new CustomSound(buf);
	boolean waveHasChanged=true;

	public void doWave(){
		for(int i=0;i<480;i++)wave[i] = 128;//128;
		for(int j=1;j<25;j++){
			if(ampl[j]>0){
				int n=ph[j] * 120;
				for(int i=0;i<480;i++)
					wave[i]+=Math.max(Math.min(
						ampl[j]*Math.sin(Math.PI*(j*i+n)/240),
						256),-120)
					;
			}
		}

				//autoscale
		int max=128,min=128;
		for(int i=0;i<480;i++){		//calculate max deflection
			if(wave[i]>max)max=wave[i];
			if(wave[i]<min)min=wave[i];
		}
		max=Math.max(128-min,max-128);
				//scale each point
		if(max>127)for(int i=0;i<480;i++)wave[i]=128+(wave[i]-128)*127/max;



		int j, t;
		for(int i=0;i<96;i++){
			j = i * 5;
			t=(wave[j]-64)/2;
			if(t<0)t=0;else if(t>127)t=127;	//truncate!
			//if(t>127)t-=128;else t=(t-128)&0xff;	// signed two's complement?
			buf[i] = (byte)(t + 128);
			for(int k=1;k<N;k++)buf[i+96*k]=buf[i];

//System.out.print(String.valueOf(t)+",");
		}
System.out.println(buf[0]+","+buf[buf.length-1]);
/*		int q;
		for(int i=1;i<97;i++){
			q=tmp[2*i-1]&0xff;
			if(q>127)q-=256;
			q=(tmp[2*i-2]&0xff)|(0x100*q);
			buf[(i-1)*2]=(byte)(q%256);buf[(i-1)*2+1]=(byte)(q/256);
		}
*/		waveHasChanged=true;
	}
	public SoundAndHearing() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	setupPresets();
	phase[0].setSelected(true);
	listen.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			if(waveHasChanged){	//regenerate wave
				sound.close();
				sound.open(buf);
			}
			sound.start();
		}
		public void mouseReleased(MouseEvent e){
			sound.stop();
		}
	});
	ActionListener phaseal=new ActionListener(){public void actionPerformed(ActionEvent e){
		int j=0;
		for(int i=0;i<phase.length;i++)if(phase[i].isSelected())j=i;
		ph[spectrum.selection]=(byte)j;
		doWave();
		waveform.repaint();
	}};
	for(int i=0;i<phase.length;i++){
		phase[i].addActionListener(phaseal);
		bg1.add(phase[i]);
	}
	ActionListener presetal=new ActionListener(){public void actionPerformed(ActionEvent e){
		int j=0;
		for(int i=0;i<preset.length;i++)if(preset[i].isSelected())j=i;
		for(int i=1;i<25;i++){
			ampl[i]=preamp[j][i];
			ph[i]=preph[j][i];
		}
		doWave();
		spectrum.repaint();
		waveform.repaint();
	}};
	for(int i=0;i<preset.length;i++){
		preset[i].addActionListener(presetal);
		bg2.add(preset[i]);
	}
	preset[0].doClick();
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(4,4,0,4));
		border2 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border2,"Phase");
		border3 = BorderFactory.createEmptyBorder(10,10,10,10);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(3,3,3,3)));
		jPanel4.setLayout(gridLayout1);
		jRadioButton1.setText("Sine wave");
		gridLayout1.setRows(8);
		gridLayout1.setColumns(1);
		jRadioButton2.setText("Square wave");
		jRadioButton3.setText("Triangle wave");
		jRadioButton4.setText("1st + 3rd harmonic");
		jRadioButton5.setText("Beats");
		jRadioButton6.setText("Comb");
		jRadioButton7.setText("Sawtooth");
		jRadioButton8.setText("Complex");
		jPanel5.setLayout(gridLayout2);
		gridLayout2.setRows(2);
		gridLayout2.setColumns(1);
		jPanel6.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
		jPanel6.setLayout(borderLayout4);
		jPanel8.setBorder(border1);
		jPanel8.setLayout(borderLayout5);
		label3D1.setPreferredSize(new Dimension(100, 20));
		label3D1.setFont(new java.awt.Font("Dialog", 1, 14));
		label3D1.setText("Waveform");
		waveform.setBackground(Color.black);
		waveform.setForeground(Color.green);
		waveform.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2)));
		jPanel9.setPreferredSize(new Dimension(110, 25));
		jPanel7.setLayout(borderLayout6);
		jPanel11.setLayout(borderLayout7);
		jRadioButton9.setText("0");
		jRadioButton9.setFont(new java.awt.Font("Dialog", 1, 10));
		jPanel12.setLayout(gridLayout3);
		jRadioButton10.setText("90");
		jRadioButton10.setFont(new java.awt.Font("Dialog", 1, 10));
		jRadioButton11.setText("180");
		jRadioButton11.setFont(new java.awt.Font("Dialog", 1, 10));
		jRadioButton12.setText("270");
		jRadioButton12.setFont(new java.awt.Font("Dialog", 1, 10));
		jPanel12.setBorder(titledBorder1);
		jPanel13.setLayout(borderLayout8);
		jPanel13.setBorder(border3);
		listen.setFont(new java.awt.Font("SansSerif", 1, 16));
		listen.setText("Listen");
		listen.setBackground(systemGray);
		jPanel14.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(10,10,0,10)));
		jPanel14.setLayout(borderLayout9);
		label3D2.setPreferredSize(new Dimension(80, 20));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 14));
		label3D2.setText("Spectrum");
		spectrum.setBackground(Color.black);
		spectrum.setForeground(Color.green);
		spectrum.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2)));
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(returnButton1, null);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jRadioButton1, null);
		jPanel4.add(jRadioButton2, null);
		jPanel4.add(jRadioButton3, null);
		jPanel4.add(jRadioButton4, null);
		jPanel4.add(jRadioButton5, null);
		jPanel4.add(jRadioButton6, null);
		jPanel4.add(jRadioButton7, null);
		jPanel4.add(jRadioButton8, null);
		jPanel1.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(jPanel6, null);
		jPanel6.add(jPanel8, BorderLayout.CENTER);
		jPanel8.add(jPanel9, BorderLayout.SOUTH);
		jPanel9.add(label3D1, null);
		jPanel8.add(waveform, BorderLayout.CENTER);
		jPanel5.add(jPanel7, null);
		jPanel7.add(jPanel11, BorderLayout.EAST);
		jPanel11.add(jPanel12, BorderLayout.SOUTH);
		jPanel12.add(jRadioButton9, null);
		jPanel12.add(jRadioButton10, null);
		jPanel12.add(jRadioButton11, null);
		jPanel12.add(jRadioButton12, null);
		jPanel11.add(jPanel13, BorderLayout.CENTER);
		place.setLayout(new GridBagLayout());
		place.add(space);
		place.add(listen);
		jPanel13.add(place);
		jPanel7.add(jPanel14, BorderLayout.CENTER);
		jPanel14.add(jPanel15, BorderLayout.SOUTH);
		jPanel15.add(label3D2, null);
		jPanel14.add(spectrum, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}
	public void close(){
	sound.close();
	}
}
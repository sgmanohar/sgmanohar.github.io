
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

//import com.neurolab.common.*;
import com.cudos.common.kinetic.*;
import javax.swing.event.*;

public class TransportProcesses extends CudosExhibit implements ChangeListener{
	public String getExhibitName(){return "Transport Processes";}

	static final String[] comments={"Since the membrane is sugar-impermeable, water molecules that are bound to sugar molecules (by hydrogen bonding) are not free to diffuse across the membrane. So there is less diffusion out from the more concentrated solution, giving a net flux of water in.",
					"The membrane is partially permeable to water molecules, and so when the concentrations of molecules either side of the membrane are equal, the rates of movement in either direction across the membrane are equal. This gives a state of dynamic equilibrium.",
					"The membrane molecules act like channels which allow specific types of molecule to pass through freely.",
					"",
					"The transporter proteins use ATP to pump molecules across the membrane. They operate in one direction only, consume energy and are capable of pumping against (i.e. up) a concentration gradient.",
					"3 Na+ ions are expelled, and 2 K+ ions are pumped in, using energy from the breakdown of ATP. The pump is ubiquitous, unidirectional, and is responsible for maintaining the normal electrochemical gradient across the membrane of the cell."};

	JPanel main = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JButton jButton1 = new JButton();
	Border border1;
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border2;
	JPanel jPanel3 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JTextPane textpane = new JTextPane();
	BorderLayout borderLayout4 = new BorderLayout();
	Border border3;
	JPanel graphicpane = new JPanel();
	Border border4;
	KineticPane kineticPane = new KineticPane();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel4 = new JPanel();
	Border border5;
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel5 = new JPanel();
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel6 = new JPanel();
	JSlider temperature = new JSlider();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel7 = new JPanel();
	JRadioButton primary = new JRadioButton();
	JRadioButton secondary = new JRadioButton();
	Border border6;
	TitledBorder titledBorder1;
	JPanel jPanel8 = new JPanel();
	JRadioButton diffusion = new JRadioButton();
	JRadioButton osmosis = new JRadioButton();
	Border border7;
	TitledBorder titledBorder2;
	GridLayout gridLayout1 = new GridLayout();
	FlowLayout flowLayout1 = new FlowLayout();
	FlowLayout flowLayout2 = new FlowLayout();
	JRadioButton facilitated = new JRadioButton();

	public TransportProcesses() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	Timer timer;int memy;int[] nmols=new int[7];
	Random rand=new Random();
	Membrane membrane;
	public void postinit(){
		nmols[Molecule.WATER]=100;nmols[Molecule.SUGAR]=0;nmols[Molecule.SODIUM]=0;
		memy=(kineticPane.getHeight()-Membrane.thickness)/2;
		membrane=kineticPane.createMembraneAcross(memy,0,kineticPane.getWidth());	// last param is getWidth() ??


		Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
		for(int k=0;k<2;k++){
			Molecule m;
			for(int j=0;j<nmols[k];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(k);
			}
		}

		timer=new Timer(250,new ActionListener(){		//slider setting routine
			public void actionPerformed(ActionEvent e){
				setSliders();
			}
		} );
		osmosis.doClick();	//select and trigger transportchange
		timer.start();
	}

	void transportchange(ActionEvent e) {
		if(osmosis.isSelected()){
			removeSpecificMolecules();
			nmols[Molecule.SUGAR]=100;
			Molecule m;
			Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
			for(int j=0;j<nmols[Molecule.SUGAR];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SUGAR);
			}
			textpane.setText(comments[0]);
			setupSliders(Molecule.WATER,Molecule.SUGAR,Molecule.SODIUM);
		}else if(primary.isSelected()){
			removeSpecificMolecules();
			Class pumpclass;
			try{
				pumpclass=Class.forName("com.cudos.common.kinetic.SimplePump");
			}catch(Exception ex){ex.printStackTrace();return;}
			kineticPane.createTransporter(pumpclass,membrane,60,20);		//do not remember these?
			kineticPane.createTransporter(pumpclass,membrane,160,20);		//do not remember these?
			kineticPane.createTransporter(pumpclass,membrane,260,20);
			kineticPane.createTransporter(pumpclass,membrane,360,20);		//do not remember these?
			Molecule m;
			Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
			nmols[Molecule.SUGAR]=100;
			for(int j=0;j<nmols[Molecule.SUGAR];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SUGAR);
			}
			textpane.setText(comments[4]);
			setupSliders(Molecule.WATER,Molecule.SUGAR,Molecule.SODIUM);
		}else if(diffusion.isSelected()){
			removeSpecificMolecules();
			textpane.setText(comments[1]);
			setupSliders(Molecule.WATER,Molecule.SUGAR,Molecule.SODIUM);
		}else if(facilitated.isSelected()){
			removeSpecificMolecules();
			Class diffclass;
			try{
				diffclass=Class.forName("com.cudos.common.kinetic.DiffusionChannel");
			}catch(Exception ex){ex.printStackTrace();return;}
			kineticPane.createTransporter(diffclass,membrane,100,20);
			kineticPane.createTransporter(diffclass,membrane,200,20);
			kineticPane.createTransporter(diffclass,membrane,300,20);

			textpane.setText(comments[2]);
			Molecule m;
			Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
			nmols[Molecule.SUGAR]=100;
			for(int j=0;j<nmols[Molecule.SUGAR];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SUGAR);
			}
			setupSliders(Molecule.WATER,Molecule.SUGAR,Molecule.SODIUM);
		}else if(sodiumpump.isSelected()){
			removeSpecificMolecules();
			Class pumpclass;
			try{
				pumpclass=Class.forName("com.cudos.common.kinetic.SodiumPump");
			}catch(Exception ex){ex.printStackTrace();return;}
			kineticPane.createTransporter(pumpclass,membrane,100,20);
			kineticPane.createTransporter(pumpclass,membrane,200,20);
			kineticPane.createTransporter(pumpclass,membrane,300,20);
			kineticPane.createTransporter(pumpclass,membrane,400,20);
			Molecule m;
			Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
			nmols[Molecule.SODIUM]=50;nmols[Molecule.POTASSIUM]=50;
			for(int j=0;j<nmols[Molecule.SODIUM];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SODIUM);
			}
			for(int j=0;j<nmols[Molecule.POTASSIUM];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.POTASSIUM);
			}
			textpane.setText(comments[5]);
			setupSliders(Molecule.WATER,Molecule.SODIUM,Molecule.POTASSIUM);
		}else if (secondary.isSelected()){
			removeSpecificMolecules();
			Class pumpclass;
			try{
				pumpclass=Class.forName("com.cudos.common.kinetic.SodiumPump");
			}catch(Exception ex){ex.printStackTrace();return;}
			Class transclass;
			try{
				transclass=Class.forName("com.cudos.common.kinetic.Antiporter");
			}catch(Exception ex){ex.printStackTrace();return;}
			kineticPane.createTransporter(pumpclass,membrane,100,20);
			kineticPane.createTransporter(transclass,membrane,200,20);
			kineticPane.createTransporter(transclass,membrane,300,20);
			kineticPane.createTransporter(pumpclass,membrane,400,20);
			Molecule m;
			Rectangle b=new Rectangle(5,5,kineticPane.getWidth()-5,memy-20);
			nmols[Molecule.SODIUM]=50;nmols[Molecule.POTASSIUM]=50;nmols[Molecule.SUGAR]=50;
			for(int j=0;j<nmols[Molecule.SODIUM];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SODIUM);
			}
			for(int j=0;j<nmols[Molecule.POTASSIUM];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.POTASSIUM);
			}
			for(int j=0;j<nmols[Molecule.SUGAR];j++){
				kineticPane.molecules.add(m=new Molecule(rand,b) );
				m.setType(Molecule.SUGAR);
			}
			textpane.setText(comments[5]);
			setupSliders(Molecule.SUGAR,Molecule.SODIUM,Molecule.POTASSIUM);

		}
		kineticPane.repaint();
		textpane.repaint();
	}
	public void removeSpecificMolecules(){
		kineticPane.removeMolecules(Molecule.MEMBRANE_PROTEIN);
		kineticPane.removeMolecules(Molecule.SUGAR);
		kineticPane.removeMolecules(Molecule.SODIUM);
		kineticPane.removeMolecules(Molecule.POTASSIUM);
		nmols[Molecule.SODIUM]=0;nmols[Molecule.POTASSIUM]=0;
		nmols[Molecule.SUGAR]=0;
	}
	int[] slidertype=new int[]{Molecule.WATER,Molecule.SUGAR,Molecule.SODIUM};
	public void setupSliders(int a,int b,int c){
		slidertype=new int[]{a,b,c};
		for(int i=0;i<3;i++){
			sliderlabel[i+1].setText(Molecule.names[slidertype[i]]);
		}
		setSliders();
	}
	public void setSliders(){
		for(int i=1;i<4;i++){
			int type=slidertype[i-1];
			if(nmols[type]>0)
				slider[i].setValue(100*kineticPane.getMoleculesAbove(memy,type)/(nmols[type]));
			else slider[i].setValue(0);
		}
	}
	public void stateChanged(ChangeEvent s){
		JSlider sl=(JSlider)s.getSource();
		int i=3;
		for(int j=0;j<3;j++)if(slider[j+1]==sl){i=j;break;}	//find what molecule type
		if(i==3)throw new RuntimeException("Cannot find slider");
		i=slidertype[i];	//translate into molecule type
		int currm,needm;
		if((currm=kineticPane.getMoleculesAbove(memy,i))!=(needm=(nmols[i])*sl.getValue()/100)){
			Molecule m;
			Graphics2D g=(Graphics2D)kineticPane.getGraphics();
			int midline=memy+Membrane.thickness/2;
			int ht=kineticPane.getHeight();
			Random r=new Random();
			int tries=0;
				// try 20 times to move a molecule of type i from one side to the other
			if(currm>needm){
				while((kineticPane.getMoleculesAbove(memy,i)>needm)&&(++tries<20)){
					m=kineticPane.getAMolecule(r,i);	//move down
					if((m!=null)&&(m.y<midline)){
						int ny=ht-m.y;
						while(ny<memy+membrane.thickness+10)ny+=m.s;
						m.move(g,m.x,ny);	//flip about midline
					}
				}
			}else if(currm<needm){
				while((kineticPane.getMoleculesAbove(memy,i)<needm)&&(++tries<20)){
					m=kineticPane.getAMolecule(r,i);	//move up
					if((m!=null)&&(m.y>midline)){
						int ny=ht-m.y-m.s;
						if(ny<0)ny=0;	//ensure not intersecting membrane
						while(ny>memy)ny-=m.s;
						m.move(g,m.x,ny);
					}
				}
			}
		}
	}


	ButtonGroup bg1=new ButtonGroup();
	JSlider[] slider=new JSlider[4];
	JLabel[] sliderlabel=new JLabel[4];
	JPanel[] sliderpanel=new JPanel[4];
	Border border8;
	JPanel jPanel9 = new JPanel();
	Border border9;
	TitledBorder titledBorder3;
	GridLayout gridLayout2 = new GridLayout();
	BorderLayout borderLayout8 = new BorderLayout();
	BorderLayout borderLayout9 = new BorderLayout();
	BorderLayout borderLayout10 = new BorderLayout();
	JSlider sugarsize = new JSlider();
	JLabel jLabel2 = new JLabel();
	Border border10;
	JRadioButton sodiumpump = new JRadioButton();

	private void jbInit() throws Exception {
		sliderpanel[3] = new JPanel();
		sliderpanel[2] = new JPanel();
		sliderlabel[2] = new JLabel();
		sliderlabel[3] = new JLabel();
		slider[2] = new JSlider();
		slider[3] = new JSlider();
		sliderpanel[1] = new JPanel();
		sliderlabel[1] = new JLabel();
		slider[1] = new JSlider();
	 border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
		border2 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
		border3 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
		border4 = BorderFactory.createEmptyBorder(5,5,5,5);
		border5 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(5,5,5,5));
		border6 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Active transport");
		border7 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder2 = new TitledBorder(border7,"Passive transport");
		border8 = BorderFactory.createLineBorder(Color.lightGray,1);
		border9 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder3 = new TitledBorder(border9,"Ratio");
		border10 = BorderFactory.createLineBorder(Color.lightGray,1);
		jButton1.setText("Exit");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		main.setLayout(borderLayout1);
		jPanel2.setLayout(borderLayout2);
		this.getContentPane().setLayout(borderLayout3);
		textpane.setPreferredSize(new Dimension(74, 100));
		textpane.setBackground(SystemColor.control);
		textpane.setBorder(border3);
		textpane.setText("jTextPane1");
		textpane.setFont(new java.awt.Font("SansSerif", 1, 14));
		jPanel3.setLayout(borderLayout4);
		graphicpane.setBorder(border4);
		graphicpane.setLayout(borderLayout5);
		kineticPane.setBackground(new Color(192, 224, 224));
		kineticPane.setPreferredSize(new Dimension(100, 100));
		kineticPane.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				kineticPane_mouseClicked(e);
			}
		});
		jPanel4.setBorder(border5);
		jPanel4.setLayout(borderLayout6);
		jPanel1.setLayout(borderLayout7);
		jPanel5.setLayout(gridLayout1);
		jPanel5.setPreferredSize(new Dimension(140, 1));
		jPanel6.setLayout(null);
		temperature.setBorder(border8);
		temperature.setBounds(new Rectangle(1, 2, 131, 24));
		temperature.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				temperaturechanged(e);
			}
		});
		jPanel6.setPreferredSize(new Dimension(1, 40));
		jLabel1.setText("Temperature");
		jLabel1.setBounds(new Rectangle(30, 23, 79, 17));
		primary.setText("Primary");
		primary.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		secondary.setToolTipText("");
		secondary.setText("Secondary");
		secondary.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		jPanel7.setBorder(titledBorder1);
		jPanel7.setMinimumSize(new Dimension(130, 63));
		jPanel7.setPreferredSize(new Dimension(130, 63));
		jPanel7.setLayout(flowLayout1);
		diffusion.setText("Diffusion");
		diffusion.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		osmosis.setText("Osmosis");
		osmosis.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		jPanel8.setBorder(titledBorder2);
		jPanel8.setPreferredSize(new Dimension(130, 63));
		jPanel8.setLayout(flowLayout2);
		gridLayout1.setColumns(1);
		gridLayout1.setRows(3);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		flowLayout2.setAlignment(FlowLayout.LEFT);
		facilitated.setOpaque(false);
		facilitated.setText("Facilitated diffusion");
		facilitated.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		jPanel9.setBorder(titledBorder3);
		jPanel9.setLayout(gridLayout2);
		sliderpanel[2].setLayout(borderLayout8);
		sliderpanel[3].setLayout(borderLayout9);
		sliderlabel[2].setText("Sugar");
		sliderlabel[3].setText("Na");
		slider[2].setOrientation(JSlider.VERTICAL);
		slider[3].setOrientation(JSlider.VERTICAL);
		sliderpanel[1].setLayout(borderLayout10);
		sliderlabel[1].setText("Water");
		slider[1].setOrientation(JSlider.VERTICAL);
		sugarsize.setBorder(border10);
		sugarsize.setBounds(new Rectangle(135, 2, 140, 25));
		sugarsize.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				sugarsizechanged(e);
			}
		});
		jLabel2.setText("Sugar size");
		jLabel2.setBounds(new Rectangle(176, 25, 60, 17));
		sodiumpump.setText("Na/K pump");
		sodiumpump.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				transportchange(e);
			}
		});
		this.getContentPane().add(main, BorderLayout.CENTER);
		main.add(jPanel1, BorderLayout.EAST);
		jPanel1.add(jButton1, BorderLayout.SOUTH);
		jPanel1.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(jPanel8, null);
		jPanel8.add(osmosis, null);
		jPanel8.add(diffusion, null);
		jPanel8.add(facilitated, null);
		jPanel5.add(jPanel7, null);
		jPanel7.add(secondary, null);
		jPanel7.add(primary, null);
		jPanel7.add(sodiumpump, null);
		jPanel5.add(jPanel9, null);
		jPanel9.add(sliderpanel[1], null);
		sliderpanel[1].add(sliderlabel[1], BorderLayout.SOUTH);
		sliderpanel[1].add(slider[1], BorderLayout.CENTER);
		jPanel9.add(sliderpanel[2], null);
		sliderpanel[2].add(sliderlabel[2], BorderLayout.SOUTH);
		sliderpanel[2].add(slider[2], BorderLayout.CENTER);
		jPanel9.add(sliderpanel[3], null);
		sliderpanel[3].add(sliderlabel[3], BorderLayout.SOUTH);
		sliderpanel[3].add(slider[3], BorderLayout.CENTER);
		main.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(textpane, BorderLayout.SOUTH);
		jPanel3.add(graphicpane, BorderLayout.CENTER);
		graphicpane.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(kineticPane, BorderLayout.CENTER);
		graphicpane.add(jPanel6, BorderLayout.SOUTH);
		jPanel6.add(temperature, null);
		jPanel6.add(jLabel1, null);
		jPanel6.add(sugarsize, null);
		jPanel6.add(jLabel2, null);

	for(int i=0;i<3;i++){
		slider[i+1].addChangeListener(this);
	}
	bg1.add(facilitated);bg1.add(diffusion);bg1.add(osmosis);
	bg1.add(primary);bg1.add(secondary);bg1.add(sodiumpump);
	 }

	void jButton1_actionPerformed(ActionEvent e) {
	timer.stop();
	getApplet().toChooser();
	}

	void temperaturechanged(ChangeEvent e) {
	kineticPane.timer.setDelay(200-temperature.getValue()*2);
	}

	void sugarsizechanged(ChangeEvent e) {
	kineticPane.setMoleculeSizes(Molecule.SUGAR,sugarsize.getValue()/4);
	}

	void kineticPane_mouseClicked(MouseEvent e) {
	kineticPane.repaint();
	}

}
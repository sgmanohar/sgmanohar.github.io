package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;

public class CompoundAP extends NeurolabExhibit implements ActionListener{
	ButtonGroup cbg1=new ButtonGroup();
	ReturnButton returnButton1 = new ReturnButton();
	JButton Stimulate = new JButton();
	JPanel jPanel1 = new JPanel();
	JCheckBox damage = new JCheckBox();
	JSlider dispersion = new JSlider();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel2 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JRadioButton monophasic = new JRadioButton();
	JRadioButton diphasic = new JRadioButton();
	JPanel jPanel3 = new JPanel();
	JSlider electrode1 = new JSlider();
	JLabel jLabel2 = new JLabel();
	JSlider electrode2 = new JSlider();
	JLabel jLabel3 = new JLabel();
	Oscilloscope osc = new Oscilloscope(1,this);
	JPanel jPanel4 = new JPanel();
	GridBagConstraints gbc = new GridBagConstraints();

	int numaps=18;
	double[] appos,apvel;
	int[] apdam;
	int block;
	Timer timer;

	Color[] electrodecols={Color.red,Color.blue};
	JSlider[] electrode;
	JPanel appane = new JPanel(){
			 public void paint(Graphics g){
					 super.paint(g);
					 setStrokeThickness(g,3);
					 g.setColor(Color.black);
					 for(int i=0;i<numaps;i++){
						 g.drawLine(5,apy(i),getWidth()-5,apy(i));
						 }
					 g.drawLine(15,getHeight(),15,15);
					 for(int i=0;i<2;i++){
						 g.setColor(electrodecols[i]);
						 g.drawLine(electrodex(electrode[i].getValue()),getHeight(),electrodex(electrode[i].getValue()),15);
					 }
					 if(damage.isSelected()){
						 for(int i=0;i<numaps;i++){
							 g.drawLine(electrodex(apdam[i]),apy(i),electrodex(apdam[i])+15,apy(i));
						 }
					 }
					 if(monophasic.isSelected()){
						 block=(electrode1.getValue()+electrode2.getValue())/2-1;
						 g.setColor(Color.darkGray);
						 g.fillRect(electrodex(block),10,10,getHeight()-30);
					 }
			 }
		};
	public int electrodex(int a){
		return 20+(appane.getWidth()-15)*a/100;
		}
	public int apy(int a){
		return 20+a*(appane.getHeight()-40)/numaps;
		}
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel jPanel5 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	Border border1;
	Border border2;
	Border border3;

	public String getExhibitName() {
		return "Compound AP  ";
	}
	public void init(){
		super.init();
		createComponents();
		electrode1.setValue(10);
		electrode1.setValue(20);
		dispersion.setValue(30);
		timer=new Timer(100,this);
		stimulate();
	}
	public CompoundAP(){
	}
	public void createComponents() {
		//TODO: implement this com.neurolab.common.NeurolabExhibit abstract method
		try{
			jbInit();
		}catch(Exception e){
			e.printStackTrace();
		}
		osc.remove(osc.buttons);
		osc.timer.setDelay(100);
		osc.xSpeed=5;
		int[] base={50};
		osc.setBaseY(base);
		Color[] cols={Color.green};
		osc.setColors(cols);
		diphasic.setSelected(true);
		electrode1.setValue(10);
		electrode2.setValue(30);
		dispersion.setValue(30);
		electrode=new JSlider[2];
		electrode[0]=electrode1;
		electrode[1]=electrode2;
	}
	int dead=0;
	double voltage=0;
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
		}else if(e.getActionCommand()=="Damage"){
			apdam=new int[numaps];
			Random r=new Random();
			for(int i=0;i<numaps;i++){
				apdam[i]=5+r.nextInt()%80;
			}
			appane.repaint();
		}else if(e.getActionCommand()=="Monophasic"){
			appane.repaint();
		}else if(e.getActionCommand()=="Diphasic"){
			appane.repaint();
		}else{
				//timer
				Graphics g=appane.getGraphics();
				setStrokeThickness(g,3);
				for(int i=0;i<numaps;i++){
								g.setColor(Color.black);
								drawAP(g,15+(int)appos[i],apy(i));
								g.setColor(Color.orange);
								appos[i]+=apvel[i];
								if(appos[i]!=0){
									if( (damage.isSelected()&&((appos[i]>electrodex(apdam[i])-30)&&appos[i]<electrodex(apdam[i])))
										||(monophasic.isSelected()&&(appos[i]>electrodex(block)-30))
										||(appos[i]>appane.getWidth()) ){
											appos[i]=0;
											apvel[i]=0;
											dead++;
									}else{
										drawAP(g,15+(int)appos[i],apy(i));
										if((appos[i]>electrodex(electrode1.getValue())-30)&&(appos[i]<electrodex(electrode1.getValue())) )
											voltage+=20;
										if((appos[i]>electrodex(electrode2.getValue())-30)&&(appos[i]<electrodex(electrode2.getValue())) )
											voltage-=20;
									}
								}
								voltage*=0.8;
				}
				if((dead>=numaps)&&(osc.timer.isRunning()))timer.stop();
				int[] a=new int[1];
				a[0]=-(int)voltage*6;
				osc.setPosY(a);
		}
	}
	public void drawAP(Graphics g,int x,int y){
		g.drawLine(x,y,x+15,y);
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createLineBorder(Color.lightGray,2);
		border2 = BorderFactory.createLineBorder(Color.lightGray,2);
		border3 = BorderFactory.createLineBorder(Color.lightGray,2);
		getMainContainer().setLayout(null);
		returnButton1.setBounds(new Rectangle(461, 268, 113, 45));
		Stimulate.setText("Stimulate");
		Stimulate.setBackground(systemGray);
		Stimulate.setBounds(new Rectangle(1, 211, 90, 54));
		Stimulate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Stimulate_actionPerformed(e);
			}
		});
		jPanel1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Nerve"));
		jPanel1.setBounds(new Rectangle(458, 162, 119, 103));
		jPanel1.setLayout(null);
		jPanel1.setBackground(systemGray);
		damage.setText("Damage");
		damage.setBackground(systemGray);
		damage.setActionCommand("Damage");
		damage.setBounds(new Rectangle(13, 18, 90, 25));
		damage.addActionListener(this);
		dispersion.setBorder(border3);
		dispersion.setBackground(systemGray);
		dispersion.setBounds(new Rectangle(7, 69, 98, 26));
		jLabel1.setText("Dispersion:");
		jLabel1.setBounds(new Rectangle(12, 47, 82, 20));
		jPanel2.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Recording"));
		jPanel2.setBounds(new Rectangle(459, 0, 119, 79));
		jPanel2.setLayout(gridLayout1);
		jPanel2.setBackground(systemGray);
		monophasic.setText("Monophasic");
		monophasic.setBackground(systemGray);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(1);
		diphasic.setText("Diphasic");
		diphasic.setBackground(systemGray);
		jPanel3.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Electrode position"));
		jPanel3.setBounds(new Rectangle(459, 79, 119, 83));
		jPanel3.setLayout(null);
		jPanel3.setBackground(systemGray);
		electrode1.setBorder(border1);
		electrode1.setBackground(systemGray);
		electrode1.setBounds(new Rectangle(20, 26, 92, 26));
		electrode1.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				electrode_stateChanged(e);
			}
		});
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel2.setForeground(Color.red);
		jLabel2.setText("+");
		jLabel2.setBounds(new Rectangle(9, 27, 13, 23));
		jLabel2.setBackground(systemGray);
		electrode2.setBorder(border2);
		electrode2.setBackground(systemGray);
		electrode2.setBounds(new Rectangle(19, 51, 93, 26));
		electrode2.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				electrode_stateChanged(e);
			}
		});
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel3.setForeground(Color.blue);
		jLabel3.setText("-");
		jLabel3.setBackground(systemGray);
		jLabel3.setBounds(new Rectangle(10, 51, 13, 20));
		jPanel4.setBorder(BorderFactory.createRaisedBevelBorder());
		jPanel4.setBounds(new Rectangle(2, 2, 455, 203));
		jPanel4.setBackground(systemGray);
		jPanel4.setLayout(gridBagLayout1);
		appane.setBorder(BorderFactory.createLoweredBevelBorder());
		jPanel5.setBounds(new Rectangle(98, 210, 357, 105));
		jPanel5.setLayout(borderLayout1);
		jPanel5.setBackground(systemGray);
		getMainContainer().add(jPanel4, null);

		gbc.gridx=0;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=1;
		gbc.weightx=1.0;gbc.weighty=1.0;
		gbc.anchor=GridBagConstraints.CENTER; gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(10, 10, 10, 10);
		gbc.ipadx=336;gbc.ipady=185;
		jPanel4.add(appane, gbc);
		/*new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
						,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 336, 185));	*/
		getMainContainer().add(jPanel5, null);
		jPanel5.add(osc, BorderLayout.CENTER);
		getMainContainer().add(Stimulate,null );
		getMainContainer().add(jPanel2, null);
		jPanel2.add(monophasic, null);
		jPanel2.add(diphasic, null);
		getMainContainer().add(jPanel3, null);
		jPanel3.add(electrode1, null);
		jPanel3.add(jLabel2, null);
		jPanel3.add(electrode2, null);
		jPanel3.add(jLabel3, null);
		getMainContainer().add(jPanel1, null);
		jPanel1.add(damage, null);
		jPanel1.add(dispersion, null);
		jPanel1.add(jLabel1, null);
		getMainContainer().add(returnButton1, null);

		cbg1.add(monophasic);cbg1.add(diphasic);
		monophasic.addActionListener(this);
		diphasic.addActionListener(this);
	}

	public void stimulate(){
		if(osc!=null){
			 osc.clear.doClick();
			 osc.sweep.doClick();
		}
			 appos=new double[numaps];
			 apvel=new double[numaps];
			 Random r=new Random();
			 for(int i=0;i<numaps;i++){
				 apvel[i]=10.+dispersion.getValue()*(r.nextInt()%100-50)/1000.;// 100*50/1000 = 1.0
			 }
			 dead=0;
				appane.repaint();
			timer.start();
	}

	void Stimulate_actionPerformed(ActionEvent e) {
		stimulate();
	}

	void electrode_stateChanged(ChangeEvent e) {
			 appane.repaint();
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
		osc.timer.stop();
	}


}
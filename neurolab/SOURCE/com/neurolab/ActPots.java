// Action Potentials by Sanjay Manohar
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Oscilloscope;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.Spacer;


public class ActPots extends NeurolabExhibit implements ActionListener{
	Button returnbutton;
	Oscilloscope osc;
	JPanel rightpanel,rupper,rlower,stimparams,sizepanel,polpanel,cvpanel;
	JCheckBox tea,ttx,ramp;
	JRadioButton positive,negative,current,voltage;
	JSlider size;
	JButton on;
	JLabel sizelabel;
	ButtonGroup i_or_v, pos_or_neg;
	GridLayout bl1;
	BorderLayout bl2;
	JPanel onPanel;
	JPanel a;

	boolean stimulus_on;

	private Color[] oscColors={Color.green,Color.red,Color.blue,Color.yellow,Color.magenta};
	private int base[]={60,150,151,220,260};
	private String label[]={ " V"," gNa"," gK"," I"," Stim"};

	javax.swing.Timer timer;

	public String getExhibitName(){return "Action Potentials";}
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		getMainContainer().setBackground(systemGray);
		//initialise variables
		createComponents();
		current.setSelected(true);
		positive.setSelected(true);
		size.setValue(75);
		initCalc();
		timer=new javax.swing.Timer(30,this);	// begin calculating
		timer.start();
	}
	public void createComponents(){
		osc=new Oscilloscope(5,this){
			public void drawScreenElements(Graphics g){
				g.setFont(new Font("Ariel",Font.BOLD,14));
				for(int i=0;i<5;i++){
					g.setColor(Color.gray);
					int gut=getGutter();
					g.drawLine(gut,gut+baseY[i],getWidth()-2*gut,gut+baseY[i]);
					if(i==0){
						int yk =(int)(osc.graph.calcY(-2.0f*EK ,baseY[i]));
						int yna=(int)(osc.graph.calcY(-2.0f*ENa,baseY[i]));
						g.drawLine(gut,yk ,getWidth()-2*gut,yk );
						g.drawLine(gut,yna,getWidth()-2*gut,yna);
						g.drawString(" E(K)",gut,yk+12 );
						g.drawString(" E(Na)",gut,yna);
					}
					g.setColor(getColors()[i]);
					g.drawString(label[i],gut,
						gut-5+baseY[i]+((i==2)?20:0) );
				}
			}
			public Dimension getPreferredSize(){
				return new Dimension(400,300);
			}
		};
		osc.setColors(oscColors);
		osc.setBaseY(base);
		osc.timer.setDelay(130);
		osc.xSpeed=2;
		osc.buttons.remove(osc.clear);
		getMainContainer().add(osc,BorderLayout.WEST);

		getMainContainer().add(rightpanel=new JPanel(),BorderLayout.EAST);
		rightpanel.setLayout(new BorderLayout());
		rightpanel.add(rupper=new JPanel(),BorderLayout.NORTH);
		rightpanel.add(rlower=new JPanel(),BorderLayout.SOUTH);
		setBG(rightpanel);setBG(rupper);setBG(rlower);

		rlower.setLayout(new FlowLayout());
		rlower.add(tea=new JCheckBox("TEA"));
		rlower.add(ttx=new JCheckBox("TTX"));
		rlower.add(new ReturnButton());
		setBG(tea);setBG(ttx);

		rupper.setLayout(bl1=new GridLayout(3,1));
		rupper.setBorder(BorderFactory.createTitledBorder(etched, "Stimulus"));

		rupper.add(cvpanel=new JPanel());
		cvpanel.setLayout(new BorderLayout());
		cvpanel.add(current=new JRadioButton("Current"), BorderLayout.NORTH);
		cvpanel.add(voltage=new JRadioButton("Voltage, Clamped"), BorderLayout.CENTER);
		(i_or_v=new ButtonGroup()).add(current);
		i_or_v.add(voltage);
		cvpanel.add(new Spacer(20,20),BorderLayout.SOUTH);
		setBG(cvpanel);setBG(current);setBG(voltage);

		rupper.add(stimparams=new JPanel());
		stimparams.setLayout(bl2=new BorderLayout());
		stimparams.setBorder(etched);
		setBG(stimparams);

		stimparams.add(sizepanel=new JPanel(),BorderLayout.NORTH);
		sizepanel.setLayout(new BorderLayout());
		sizepanel.add(sizelabel=new JLabel("          Size"),BorderLayout.WEST);
		sizepanel.add(size=new JSlider(JSlider.HORIZONTAL,0,100,1){
			public Dimension getPreferredSize(){
				return new Dimension(100,30);
			}
		},BorderLayout.EAST);
		setBG(size);setBG(sizelabel);setBG(sizepanel);

		stimparams.add(polpanel=new JPanel(),BorderLayout.WEST);
		polpanel.setLayout(new BorderLayout());
		polpanel.add(positive=new JRadioButton("Positive"),BorderLayout.NORTH);
		polpanel.add(negative=new JRadioButton("Negative"),BorderLayout.SOUTH);
		(pos_or_neg=new ButtonGroup()).add(positive);
		pos_or_neg.add(negative);
		setBG(polpanel);setBG(positive);setBG(negative);

		stimparams.add(ramp=new JCheckBox("Ramp"),BorderLayout.EAST);
		setBG(ramp);


		rupper.add(onPanel= new JPanel());
		onPanel.setBackground(systemGray);
								onPanel.setLayout(new GridLayout(3,1));
		a = new JPanel();
		a.setBackground(systemGray);

		onPanel.add(a);
		onPanel.add(on=new JButton("ON"));
		/*
		rupper.add(on=new JButton("ON"){
			public Dimension getPreferredSize(){
				return new Dimension(80,80);
			}
		}/*,BorderLayout.SOUTH);
			The code above is now redundant ONButton has been moved and shrunk ods20 */
			on.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				stimulus_on=true;
			}
			public void mouseReleased(MouseEvent e){
				stimulus_on=false;
			}
		} );
		setBG(on);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			restart();
			if(osc!=null)osc.graph.repaint();
		}
		else{ //timer
			tick();
		}
	}



		//CALCULATION


	final private float gK0=0.005f,		gKmax=5;
	final private float gNa0=0.0002f,	gNamax=10;
	float stim_size=0;
	float l10,scale;
	float gNa,gK;		// membrane conductances
	float Erest,ENa,EK,E;	// membrane potentials
	float I;		// membrane current
	double n,m,h;		// channel states [0,1]

	public void initCalc(){
		l10=(float)(1/Math.log(10));
		EK=goldman(0,100);		// calculate Ek
		ENa=goldman(100,0);		// and ENa
		Erest=goldman(gNa0,gK0);	// and Erest
		scale=0.05f;
		restart();
	}
	public void restart(){
		gNa=gNa0;gK=gK0;	// initial conductances
		E=Erest;		// resting membrane
		m=n=0;h=1;		// initial proportions
	}
	public float goldman(float gn,float gk){	// returns goldman field potential, given permeabilities
		float r=(gn*440f+gk*20f)/(gn*50f+gk*400f);
		if(r>0f)return (float)(61f*l10*Math.log(r)); else return 0;
	}
	public void tick(){
			//calculate stimulus
		if(stimulus_on){
			if(ramp.isSelected()){
				stim_size+=size.getValue()*0.0005f*((negative.isSelected())?-1f:1f);
			}else{
				stim_size=size.getValue()*0.03f*((negative.isSelected())?-1f:1f);
			}
		}else{
			stim_size=0;
		}
			//for the current value of E, find the conductance
			//of each ion channel
		calculate_g();
			//then using dV=
		E=getVoltage(stim_size);
		displayValues();
	}

		//Subroutines


	public void displayValues(){
		int newpos[]=new int[5];	// array of oscilloscope values
		newpos[0]=(int)(-2.0f *E);		// voltage
		newpos[1]=(int)(-50f  *gNa);		// gNa
		newpos[2]=(int)(-50f  *gK);		// gNa
		newpos[3]=(int)(-1.0f *I) ;		// current
		newpos[4]=(int)(-50f *stim_size);	// stimulus
		for(int i=0;i<5;i++)if(newpos[i]>500 || newpos[i]<-500) newpos[i]=0;
		osc.setPosY(newpos);
	}

	public void calculate_g(){
		double an,am,ah,bn,bm,bh;
		double vv=-50-(int)E;		// V as defined by Huxley
		if(vv==-10)an=0.1;	else an=0.01*(vv+10)/(Math.exp(vv/10+1.0)-1);
		bn=0.125*Math.exp(vv/80);
		if(vv==-25)am=1;	else am=0.1 *(vv+25)/(Math.exp(vv/10+2.5)-1);
		bm = 4.0  * Math.exp(vv / 18);
		ah = 0.07 * Math.exp(vv / 20);
		bh = 1.0 / (Math.exp(vv / 10 + 3) + 1);
		n += scale * (an * (1.0 - n) - bn * n);
		m += scale * (am * (1.0 - m) - bm * m);
		h += scale * (ah * (1.0 - h) - bh * h);
//System.out.println("n="+n+",m="+m+",h="+h);
		if(tea.isSelected())gK =gK0;	else gK=  (float)(gK0  +gKmax  *  Math.pow(n,4));
		if(ttx.isSelected())gNa=gNa0;	else gNa= (float)(gNa0 +gNamax *h*Math.pow(m,3));
	}

	public int getVoltage(float X){
		int V;
		if(voltage.isSelected())
			V=-60+(int)(X*100);
		else
			//membrane's new voltage is calculated from the
			//conductances of the ion channels
			V=(int)goldman(gNa,gK);
		I=(V-(int)ENa)*gNa+(V-(int)EK)*gK;
		if(!(voltage.isSelected())){
			I+=X/1000;
			//dV=currents/capacitance
			V+=(int)(0.02*X/(gNa+gK));
		}
		return V;
	};





	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
		osc.timer.stop();
	}

}



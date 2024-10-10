// time constants by Sanjay Manohar
package com.neurolab;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

import com.neurolab.circuit.*;
import com.neurolab.fluid.*;
import com.neurolab.common.*;

import com.neurolab.common.Oscilloscope;

public class TimeConstants extends NeurolabExhibit implements ActionListener,ChangeListener{
	private Color[] oscColors={Color.green};
	private int base[]={70};
	private String label[]={"V"};

	javax.swing.Timer timer;
	boolean pressed=false;

	public String getExhibitName(){return "Time Constants";}
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		//initialise variables
		createComponents();
		createCircuit();
		//ods20 - changed inital values from 20,20,90
		s_type.setSelected(0);
		R1.slider.setValue(5);
		R2.slider.setValue(6);
		C.slider.setValue(90);

		timer=new javax.swing.Timer(50,this);	// begin calculating
		timer.start();
	}

	Oscilloscope osc;
	NamedSliderPanel R1,R2,C;
	FluidPanel fluidpanel;
	CircuitPanel circuitpanel;
	RadioPanel s_type;
	JPanel lpanel, rpanel, trpanel,brpanel,tlpanel,sliderpanel;
	JButton close_switch;
	String[] s_typenames={"Voltage, V","Current, I","Resistance, R2"};
	public void createComponents(){
		osc=new Oscilloscope(1,this){
			public void drawScreenElements(Graphics g){
				int gut=getGutter();
				g.setFont(new Font("Ariel",Font.BOLD,12));
				g.setColor(getColors()[0]);
				g.drawString(label[0],gut+5,gut+15);
			}
			public Dimension getPreferredSize(){
				return new Dimension(400,120);
			}
		};
		osc.setColors(oscColors);
		osc.setBaseY(base);
		osc.timer.setDelay(200);
		osc.setGutter(5);
		getMainContainer().add(lpanel=new JPanel(),BorderLayout.WEST);
		getMainContainer().add(rpanel=new JPanel(),BorderLayout.EAST);
		setBG(lpanel);setBG(rpanel);

		rpanel.setLayout(new BorderLayout());
		rpanel.add(trpanel=new JPanel(),BorderLayout.NORTH);
		rpanel.add(brpanel=new JPanel(),BorderLayout.SOUTH);
		setBG(trpanel);setBG(brpanel);

		brpanel.setLayout(new BorderLayout());
		//Close Switch shrunk - ods20
		JPanel a = new JPanel();
		a.setBackground(systemGray);
		brpanel.add(a, BorderLayout.CENTER);
		brpanel.add(close_switch=new JButton("Close switch") /*{
			public Dimension getPreferredSize(){
				return new Dimension(100,80);
			}
		}*/,BorderLayout.NORTH );
		brpanel.add(new ReturnButton(),BorderLayout.SOUTH);
		setBG(close_switch);
		close_switch.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				switch(c_type){
					case 0:
						inputvalve.toggle();
						leftSwitch.toggle();
						break;
					case 1:
						currentvalve.toggle();
						leftSwitch.toggle();
						break;
					case 2:
						rightSwitch.toggle();
						exitvalve.toggle();
						break;
				}
				pressed=true;
			}
			public void mouseReleased(MouseEvent e){
				mousePressed(e);
				pressed=false;
			}
		} );

		trpanel.setLayout(new BorderLayout());
		trpanel.add(s_type=new RadioPanel(s_typenames,typeaction),BorderLayout.NORTH);
		trpanel.add(sliderpanel=new JPanel(),BorderLayout.SOUTH);
		setBG(sliderpanel);

		sliderpanel.setLayout(new GridLayout(3,1));
		sliderpanel.add(R1=new NamedSliderPanel("R1",20,LABEL_POS_LEFT));
		sliderpanel.add(R2=new NamedSliderPanel("R2",20,LABEL_POS_LEFT));
		sliderpanel.add(C=new NamedSliderPanel("C",70,LABEL_POS_LEFT));
		C.slider.addChangeListener(this);

		lpanel.setLayout(new BorderLayout());
		lpanel.add(osc,BorderLayout.SOUTH);
		lpanel.add(tlpanel=new JPanel(),BorderLayout.NORTH);
		setBG(tlpanel);

		tlpanel.setLayout(new BorderLayout());
		tlpanel.add(circuitpanel=new CircuitPanel(),BorderLayout.WEST);
		circuitpanel.setPreferredSize(new Dimension(210,190));
		tlpanel.add(fluidpanel=new FluidPanel(),BorderLayout.EAST);
		setBG(circuitpanel);setBG(fluidpanel);

	}

	Vector leftCell,leftCurrent;// rightCurrent, rightWithSwitch;
	Vector pressuretank,currentpipe;
	CircuitSwitch leftSwitch,rightSwitch;
	//CircuitWire rightWire;//
	FluidReservoir reservoir1,reservoir2;
	FluidPipe outpipe;
	FluidValve exitvalve,inputvalve,currentvalve;
	public void createCircuit(){
		int[][] ilist= {{0,0}  ,{50,0} ,{100,0},{130,0},{160,0},{210,0},{260,0},
				{0,40},			{130,75},		{260,60},
				{0,130},		{130,135},		{260,150},
				{0,200},		{130,200},		{260,200}};
		leftCell=new Vector();leftCurrent=new Vector();//rightCurrent=new Vector();rightWithSwitch=new Vector();
		CircuitResistor r1,r2;
		CircuitCapacitor c1;
		CircuitCurrent i1;
		CircuitCell v1;

		for(int i=0;i<ilist.length;i++)
			circuitpanel.nodes.addElement(new Point((int)(ilist[i][0]*.6)+30,(int)(ilist[i][1]*0.75)+25));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,			0,1));
		circuitpanel.componentlist.addElement(leftSwitch=new CircuitSwitch(circuitpanel,	1,2));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,			2,4));
		circuitpanel.componentlist.addElement(rightSwitch=new CircuitSwitch(circuitpanel,	4,5));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,			5,6));
		rightSwitch.closed=true;

		leftCell.addElement(new CircuitWire(circuitpanel,	0,7));
		leftCell.addElement(r1=new CircuitResistor(circuitpanel,	7,10));
		leftCell.addElement(v1=new CircuitCell(circuitpanel,	13,10));	//EMF
		leftCurrent.addElement(new CircuitWire(circuitpanel,	0,7));
		leftCurrent.addElement(i1=new CircuitCurrent(circuitpanel,7,10));
		leftCurrent.addElement(new CircuitWire(circuitpanel,	10,13));	// current
		addAll(circuitpanel.componentlist, leftCell);

		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,	3,8));
		circuitpanel.componentlist.addElement(c1=new CircuitCapacitor(circuitpanel,8,11));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,	11,14));
		//circuitpanel.componentlist.addElement(rightWire = new CircuitWire(circuitpanel,    4,5));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,	6,9));
		circuitpanel.componentlist.addElement(r2=new CircuitResistor(circuitpanel,9,12));
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,	12,15));
		r1.name="R1";r2.name="R2 ";
		v1.name="V "; i1.name="I ";
		c1.name="C ";
		circuitpanel.componentlist.addElement(new CircuitWire(circuitpanel,	13,15));	//ground rail

	//	rightWithSwitch.add(RightSwitch);
		//rightCurrent.add(rightWire);

		fluidpanel.componentlist.addElement(reservoir1=new FluidReservoir(fluidpanel,
			new Point(60,40),new Point(70+C.slider.getValue(),140) ));
		fluidpanel.componentlist.addElement(outpipe=new FluidPipe(fluidpanel,
			new Point(70+C.slider.getValue(),135),new Point(173,135) ));
		outpipe.drawend=true;
		fluidpanel.componentlist.addElement(exitvalve=new FluidValve(fluidpanel,
			new Point(170,140),new Point(170,150) ));
		outpipe.diameter=10;
		exitvalve.diameter=10;

		pressuretank=new Vector();
		pressuretank.addElement(reservoir2=new FluidReservoir(fluidpanel,
			new Point(-10,40),new Point(40,140) ));
		pressuretank.addElement(inputvalve=new FluidValve(fluidpanel,
			new Point(40,135),new Point(60,135) ));
		inputvalve.diameter=10;
		inputvalve.closed=true;
		reservoir2.level=0.9;
		addAll(fluidpanel.componentlist,pressuretank);

		currentpipe=new Vector();
		currentpipe.addElement(currentvalve=new FluidValve(fluidpanel,
			new Point(70,0),new Point(70,30) ));
		currentvalve.diameter=10;
		currentvalve.closed=true;
	}

	double floor=0.057;
	double x=0.3,y=0.3;
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
		}else{	// timer
			switch(s_type.getSelected()){
				case 0:
					y-=(x-floor)/(R2.slider.getValue()+1);
					if(pressed)y+=(reservoir2.level-x)/(R1.slider.getValue()+1);
					break;
				case 1:
					y-=(x-floor)/(R2.slider.getValue()+1);
					if(pressed)y+=0.1;
					break;
				case 2:
					y+=(reservoir2.level-x)/(R1.slider.getValue()+1);
					if(pressed)y-=(x-floor)/(R2.slider.getValue()+1);
					break;
			}
			x=10*y/(C.slider.getValue()+10);

			int[] t=new int[1];
			t[0]=100-(int)(700*x);
			osc.setPosY(t);
			reservoir1.level=x;
			fluidpanel.repaint();
		}
	}
	int c_type=0,cap=0;;
	public void stateChanged(ChangeEvent e){
		int b;
		if(cap!=(b=C.slider.getValue()) ){
			cap=b;
			reservoir1.setPos(reservoir1.getP1(),new Point(70+C.slider.getValue(),140));
			outpipe.setPos(new Point(70+C.slider.getValue(),135),outpipe.getP2());
		}
		circuitpanel.repaint();
		fluidpanel.repaint();
	}
	ActionListener typeaction = new ActionListener(){public void actionPerformed(ActionEvent e){
		int a;
		if(c_type!=(a=s_type.getSelected() )){	// changed s_type
			c_type=a;
			switch(c_type){
				case 0:
					leftSwitch.closed=exitvalve.closed=false;
					rightSwitch.closed=inputvalve.closed=true;
				case 2:
					removeAll(circuitpanel.componentlist, leftCurrent);
					addAll(circuitpanel.componentlist,leftCell);
					removeAll(fluidpanel.componentlist, currentpipe);
					addAll(fluidpanel.componentlist, pressuretank);
					if(c_type==0)break;
					leftSwitch.closed=exitvalve.closed=true;
					rightSwitch.closed=inputvalve.closed=false;
					break;
					//circuitpanel.componentlist.remove(rightWire);//
					//circuitpanel.componentlist.addElement(rightSwitch);//
				case 1:
					removeAll(circuitpanel.componentlist, leftCell);
					addAll(circuitpanel.componentlist, leftCurrent);
					//circuitpanel.componentlist.remove(rightSwitch);//
					//circuitpanel.componentlist.addElement(rightWire);//
					removeAll(fluidpanel.componentlist, pressuretank);
					addAll(fluidpanel.componentlist, currentpipe);
					leftSwitch.closed=exitvalve.closed=false;
					rightSwitch.closed=true;
					break;
			}
		}
		circuitpanel.repaint();
		fluidpanel.repaint();
	}};
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
		osc.timer.stop();
	}


}

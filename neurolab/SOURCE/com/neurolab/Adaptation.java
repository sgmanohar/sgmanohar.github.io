// Sensory Adaptation by Sanjay Manohar
package com.neurolab;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.neurolab.common.*;

public class Adaptation extends NeurolabExhibit implements ActionListener,ChangeListener, SignalListener{
	private Color[] oscColors={Color.yellow,Color.green};
	private int base[]={40,100};
	private String label[]={"Response","Stimulus"};

	javax.swing.Timer timer;

	public String getExhibitName(){return "Adaptation";}
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		//initialise variables
		createComponents();
		timer=new javax.swing.Timer(100,this);	// begin calculating
		timer.start();
		siggen.timer.start();
	}

	JButton waterfall,plateau;
	JPanel controls,srpanel,spanel,rpanel,buttons;
	Oscilloscope osc;
	NamedSliderPanel completeness,timeconstant, freq;
	RadioPanel s_type;
	String[] s_typenames={"Step","Ramp","Sinusoidal"};
	SignalGenerator siggen=new SignalGenerator();
	public void createComponents(){
		osc=new Oscilloscope(2,this){
			public void drawScreenElements(Graphics g){
				g.setFont(new Font("Ariel",Font.BOLD,12));
				for(int i=0;i<2;i++){
					//g.setColor(Color.gray);
					int gut=getGutter();
					//g.drawLine(gut,gut+baseY[i],getWidth()-2*gut,gut+baseY[i]);
					g.setColor(getColors()[i]);
					g.drawString(label[i],gut+5,
						gut+15+i*(getHeight()-4*gut-30) );
				}
			}
			public Dimension getPreferredSize(){
				return new Dimension(500,200);
			}
		};
		osc.setColors(oscColors);
		osc.setBaseY(base);
		osc.timer.setDelay(50);
		getMainContainer().add(osc,BorderLayout.NORTH);
		getMainContainer().add(controls=new JPanel(),BorderLayout.SOUTH);

		controls.setLayout(new BorderLayout());
		controls.add(srpanel=new JPanel(),BorderLayout.WEST);
		controls.add(buttons=new JPanel(),BorderLayout.EAST);
		setBG(controls);setBG(buttons);setBG(srpanel);

		srpanel.setLayout(new BorderLayout());
		srpanel.add(spanel=new JPanel(),BorderLayout.WEST);
		srpanel.add(rpanel=new JPanel(),BorderLayout.EAST);
		setBG(spanel);setBG(rpanel);

		buttons.setLayout(new GridLayout(3,1));
		buttons.add(waterfall=new JButton("Waterfall illusion"));
		buttons.add(plateau=new JButton("Plateau spiral"));
		buttons.add(new ReturnButton());
		setBG(waterfall);
		setBG(plateau);
		waterfall.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				getHolder().setExhibit("com.neurolab.WaterfallIllusion");
			}
		});
		plateau.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				getHolder().setExhibit("com.neurolab.PlateauSpiral");
			}
		});


		spanel.setLayout(new BorderLayout());
/*		spanel.add(s_type=new RadioPanel(s_typenames,this),BorderLayout.WEST);
		spanel.add(new Spacer(20,20),BorderLayout.CENTER);
		spanel.add(freq=new NamedSliderPanel("Frequency",100,LABEL_POS_BELOW),BorderLayout.EAST);
		spanel.setBorder(BorderFactory.createTitledBorder(etched,"Stimulus"));
*/
siggen.setSignalListener(this);
siggen.optionPanel.setLayout(new FlowLayout());
siggen.optionPanel.remove(siggen.squareramp);
//siggen.optionPanel.setPreferredSize(new Dimension(100,100));
siggen.frequencyPanel.remove(siggen.freqLabel);
siggen.frequencyPanel.add(siggen.freqLabel, BorderLayout.WEST);
siggen.amplitudePanel.remove(siggen.amplLabel);
siggen.amplitudePanel.add(siggen.amplLabel, BorderLayout.WEST);
siggen.sinusoidal.setSelected(true);
spanel.add(siggen);

		rpanel.setLayout(new BorderLayout());
		rpanel.add(completeness=new NamedSliderPanel("Completeness",100,LABEL_POS_LEFT),BorderLayout.NORTH);
		rpanel.add(timeconstant=new NamedSliderPanel("Time-constant",100,LABEL_POS_LEFT),BorderLayout.SOUTH);
		rpanel.setBorder(BorderFactory.createTitledBorder(etched,"Response"));
		timeconstant.slider.setMinimum(3);

//		s_type.setSelected(0);
//		freq.slider.setValue(20);
		completeness.slider.setValue(50);
		timeconstant.slider.setValue(30);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Sweep"){
			if(osc!=null)osc.graph.repaint();
			z=0;
		}
		else{	// timer
			//tick();
		}
	}
	public void stateChanged(ChangeEvent e){
	}
	static int ampl=90;
	int time;
	double z=0;
/*	public void tick(){
		double s=0,r;
		int[] G=new int[2];
		double frequency=((double)freq.slider.getValue())/800.;
		int period=(int)(1/frequency);
		double creep=(timeconstant.slider.getValue()+1)/4.;
		++time;time%=period;
		switch(s_type.getSelected()){
			case 0:
				s=(time>period/2)?ampl:-ampl;	// step
				break;
			case 1:
				s=4*ampl/period*((time>period/2)?period-time:time);	// ramp
				break;
			case 2:
				s=ampl*Math.sin(2*Math.PI*time*frequency);	// sinusoid max 8 ticks per cycle
				break;
		}
		z+=(s-z)/creep;
		r=s-completeness.slider.getValue()*z/100;
		G[0]=(int)(r);G[1]=(int)(s);
		osc.setPosY(G);
	}
*/
	public void signalEvent(double s){
		s*=1.7;
		double creep=(timeconstant.slider.getValue()+3)/8.;
		double complete=completeness.slider.getValue()/100.;
		z+=(s-z)/creep;
		double r=s-complete*z;

		int[] y=new int[]{(int)r,(int)s};
		osc.setPosY(y);
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

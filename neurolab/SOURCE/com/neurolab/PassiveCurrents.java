// Passive Currents by Sanjay Manohar
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;
import com.neurolab.common.ThickPanel;

/*
v1.1	ok

*/

public class PassiveCurrents extends NeurolabExhibit implements ActionListener, ChangeListener {

	private Timer timer;
	private float[] decay;
	private int ndecay=100;
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		createComponents();
		timer=new Timer(100,this);
		ndecay=52;
		decay=new float[ndecay];
	}

	JPanel lpanel,rpanel,blpanel,trpanel,mrpanel;
	NamedSliderPanel spacec,timec;
	JButton start,stop;
	JRadioButton charge,current;
	TitledBorder tborder1;
	ButtonGroup ch_or_cur;
	ThickPanel tlpanel;
	GraphPanel graph;

	public void createComponents(){
		getMainContainer().add(lpanel=new JPanel(),BorderLayout.WEST);
		getMainContainer().add(rpanel=new JPanel(),BorderLayout.EAST);
		lpanel.setLayout(new BorderLayout());
		lpanel.add(blpanel=new JPanel(),BorderLayout.SOUTH);
		lpanel.add(tlpanel=new ThickPanel(10),BorderLayout.NORTH);
		setBG(lpanel);setBG(rpanel);setBG(blpanel);

		tlpanel.inner.add(graph=new GraphPanel());

		blpanel.setLayout(new GridLayout(1,2));	// start and sto
		blpanel.add(start=new JButton("Start")/*,BorderLayout.WEST*/);
		blpanel.add(stop=new JButton("Stop")/*,BorderLayout.EAST*/);
		start.addActionListener(this);
		stop.addActionListener(this);
		setBG(start);setBG(stop);

		rpanel.setLayout(new BorderLayout());
		rpanel.add(new ReturnButton(),BorderLayout.SOUTH);
		rpanel.add(trpanel=new JPanel(),BorderLayout.NORTH);
		rpanel.add(mrpanel=new JPanel(),BorderLayout.CENTER);
		setBG(trpanel);setBG(mrpanel);

		trpanel.setBorder(tborder1=BorderFactory.createTitledBorder(etched,"Apply:"));
		trpanel.setLayout(new BorderLayout());
		trpanel.add(charge=new JRadioButton("Charge"),BorderLayout.NORTH);
		trpanel.add(current=new JRadioButton("Current"),BorderLayout.SOUTH);
		(ch_or_cur=new ButtonGroup()).add(charge);
		ch_or_cur.add(current);
		current.addChangeListener(this);
		Font arial14=new Font("Arial",Font.PLAIN,14);
		current.setFont(arial14);charge.setFont(arial14);
		setBG(charge);setBG(current);

		mrpanel.setLayout(new BorderLayout());
		mrpanel.add(spacec=new NamedSliderPanel("Space-constant",100,LABEL_POS_BELOW),BorderLayout.NORTH);
		JPanel tmp=new JPanel();tmp.setLayout(new BorderLayout());
		mrpanel.add(tmp, BorderLayout.CENTER); 
		tmp.add(timec =new NamedSliderPanel("Time-constant" ,100,LABEL_POS_BELOW),BorderLayout.NORTH);
		setBG(tmp);

		charge.setSelected(true);
		spacec.slider.setValue(100);
		timec.slider.setValue(30);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Start"){
			graph.repaint();
			initDecay();
			timer.start();
		}else if(e.getActionCommand()=="Stop"){
			timer.stop();
		}else{	// timer
			tick();
		}
	}
	public void stateChanged(ChangeEvent e){
		initDecay();
	}
	boolean initialrun=false;
	public void initDecay(){
		for(int i=0;i<ndecay;i++)
			decay[i]=0;
		decay[1]=80;
		initialrun=true;
	}
	float vscale;
	public void tick(){
		float X,Y,
			tau=timec.slider.getValue()/4+5,
			lambda=spacec.slider.getValue()*2/25+2;
		float d[]=new float[ndecay];
		X=0.002f/tau;
		Y=X*(lambda*lambda)*5;
		X=1-2*Y-X;
		for(int j=0;j<200;j++){
			for(int i=1;i<(ndecay-1);i++){	// omit endpoints
				d[i]=X*decay[i]+Y*(decay[i-1]+decay[i+1]);
			}
			d[ndecay-1]=0.8f*d[ndecay-2];
			d[0]=d[2];	// why??
			if(current.isSelected())d[1]=80;
			decay=d;	// replace old array
		}
		if(initialrun){
			vscale=80/decay[1];
			initialrun=false;
		}
		Graphics g=graph.getGraphics();
		int oht,ohp,ht,hp,ct=graph.getWidth()/2;
		g.setColor(Color.green);
		int Y0=graph.getHeight()-15;
		ohp=0;oht=(int)(Y0-2.5*vscale*decay[1]);
		for(int i=1;i<ndecay;i++){
			ht=(int)(Y0-2.5*vscale*decay[i]);
			hp=(i-1)*(ct-20)/ndecay;
			g.drawLine(ct-ohp,oht,ct-hp,ht);
			g.drawLine(ct+ohp,oht,ct+hp,ht);
			oht=ht; ohp=hp;
		}
	}
	public String getExhibitName(){return "Passive Currents";}

	class GraphPanel extends JPanel{
		public int base=15;
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(Color.black);
			g.fillRect(1,1,getWidth()-2,getHeight()-2);
			g.setColor(Color.gray);
			g.drawLine(1,getHeight()-base,getWidth(),getHeight()-base);
			g.setColor(new Color(0,128,0));
			g.drawLine(getWidth()/2,0,getWidth()/2,getHeight()-base);
		}
		public Dimension getPreferredSize(){
			return new Dimension(420,250);
		}
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}
}
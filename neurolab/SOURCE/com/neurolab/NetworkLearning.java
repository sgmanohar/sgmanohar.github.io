
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import com.neurolab.common.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.Vector;

public class NetworkLearning extends NeurolabExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	JButton jButton1 = new JButton();
	JButton jButton2 = new JButton();
	JToggleButton randombutton = new JToggleButton();
	JPanel rightpanel = new JPanel0();
	JPanel leftpanel = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	GridLayout gridLayout2 = new GridLayout();
	FlowLayout flowLayout1 = new FlowLayout();


		//GRAPHICS


	JPanel graphic = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		int w=getWidth()/3, h0=getHeight()/input.length,
			h1=getHeight()/hidden.length, h2=getHeight()/output.length;
		for(int i=0;i<input.length;i++){
			drawcell(g,w*0+w/2,h0*i+h0/2,26*input[i]);
			for(int j=0;j<hidden.length;j++)
				drawconnec(g,w*0+w/2,h0*i+h0/2, w*1+w/2,h1*j+h1/2,wih[i][j]);
		}
		for(int i=0;i<hidden.length;i++){
			drawcell(g,w*1+w/2,h1*i+h1/2,hidden[i]);
			for(int j=0;j<output.length;j++)
				drawconnec(g,w*1+w/2,h1*i+h1/2, w*2+w/2,h2*j+h2/2,who[i][j]);
		}
		for(int i=0;i<output.length;i++)
			drawcell(g,w*2+w/2,h2*i+h2/2,10*output[i]);
	}
	};
	int rad=10,diam=rad*2;
	void drawcell(Graphics g,int x,int y,double colour){
		g.setColor(new Color(lim(colour), 0, lim(-colour)/2));
		g.fillOval(x-rad,y-rad,diam,diam);
		g.setColor(Color.black);
		g.drawOval(x-rad,y-rad,diam,diam);
	}
	void drawconnec(Graphics g,int x1,int y1, int x2, int y2, double colour){
		g.setColor(new Color(lim(800*colour),0,lim(-800*colour)/2));
		g.drawLine(x1+rad,y1,x2-rad,y2);
	}
	int lim(double x){return (int)Math.min(Math.max(x,0),255);}
	double lim(double x,double min,double max){return (x>min)?((x<max)?x:max):min;}


		//VARS


	double[]	input=new double[3],
			hidden=new double[20],
			output=new double[2];

	double[][]	wih=new double[input.length][hidden.length],
			who=new double[hidden.length][output.length];
	JCheckBox[]	cb=new JCheckBox[input.length];
	JTextField[]	tp=new JTextField[output.length];
	JPanel[] opanel=new JPanel[output.length];
	JLabel[] olabel=new JLabel[output.length];



		//SUBS


	public void newinput(){
			//get inputs from checkboxes
		for(int i=0;i<input.length;i++)input[i]=
			cb[i].isSelected()?10:-10;

			//calculate
		calculate();
		learn();
			//redraw all
		graphic.repaint();
		settexts();
	}
	void settexts(){
		for(int i=0;i<output.length;i++){
			int v=((int)(output[i]*1))/1;
			tp[i].setText(String.valueOf(v));
		}
		examplestxt.setText(String.valueOf(examples));
		accutxt.setText(String.valueOf(getCorrectPercent()));
	}
	void forget(){	//randomise weights
		for(int i=0;i<input.length;i++)for(int j=0;j<hidden.length;j++)
			wih[i][j]=Math.random()*.2-.1;	//random from -0.1 to 0.1
		for(int i=0;i<hidden.length;i++)for(int j=0;j<output.length;j++)
			who[i][j]=Math.random()*.2-.1;
		examples=0; nCorrect=0;
		newinput();
	}
	boolean randomising=false, repeating=false;
	Timer timer=new Timer(250,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			randomising=true;
			for(int i=0;i<3;i++)	//randomize the checkboxes
				cb[i].setSelected(Math.random()>0.5);
			randomising=false;
			wasCorrect=false;
			repeating=false;
			while(!wasCorrect){newinput();repeating=true;}
		}
	});


		//CALCULATION

		//sigmoid function: range -1 to 1; 0 gives 0
	double sigm(double x){return 2*(1/(1+Math.exp(x))-0.5);}
	double sgn(double x){return x/Math.abs(x);}
	void calculate(){
			//calculate hidden neurones
		for(int j=0;j<hidden.length;j++){
			double sum=0;
			for(int i=0;i<input.length;i++) sum+=wih[i][j]*input[i];
			hidden[j]=255*sigm(sum);
		}
			//calculate output neurones
		output[0]=output[1]=0;
		for(int i=0;i<hidden.length;i++){
			output[0]+=0.1*who[i][0]*hidden[i];
			output[1]+=0.1*who[i][1]*hidden[i];
		}
	}
	double rate1=0.005,rate2=0.0005; //was 0.001
	int examples=0, nCorrect, nWrong;
	boolean wasCorrect;
	Vector scores=new Vector();
	void learn(){
		boolean e=isEven();
		int even=e?1:-1, odd=e?-1:1;
		double discrep[]=new double[hidden.length];
		for(int i=0;i<hidden.length;i++){
				//Teach second layer
			who[i][0]=lim(who[i][0]+0.5*hidden[i]*rate2*odd ,-5,5);
			who[i][1]=lim(who[i][1]+0.5*hidden[i]*rate2*even,-5,5);
			discrep[i]=even*sgn(output[0])*who[i][0] + odd*sgn(output[1])*who[i][1];
				//find error associated with each hidden neurone
		}
				// Teach first layer
		for(int i=0;i<input.length;i++)for(int j=0;j<hidden.length;j++){
			wih[i][j]=lim(wih[i][j]+input[i]*discrep[j]*rate1,-5,5);
		}
		examples++;
		wasCorrect=even*(output[1]-output[0])>0;
		if(!repeating){ if(wasCorrect)nCorrect++; else nWrong++;
			if(scores.size()>200)scores.removeElementAt(0);
			scores.addElement(new Boolean(wasCorrect));
		}
	}

	boolean isEven(){
		int t=0;
		for(int i=0;i<input.length;i++)if(cb[i].isSelected())t++;
		return t%2==0;
	}
	int getCorrectPercent(){
		int c=0;
		for(int i=0;i<scores.size();i++)
			if(((Boolean)scores.elementAt(i)).booleanValue())c++;
		return 100*c/scores.size();
	}






		//INITIALISE

	public NetworkLearning() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		//create input checkboxes
	ItemListener il=new ItemListener(){
		public void itemStateChanged(ItemEvent e){
			for(int i=0;i<input.length;i++)if(e.getItem()==cb[i]){
				if(!randomising)newinput();
			}
		}
	};
	for(int i=0;i<input.length;i++){
		cb[i]=new JCheckBox();
		cb[i].setBackground(systemGray);
		cb[i].addItemListener(il);
		leftpanel.add(cb[i]);
	}
		//and output text fields
	Border b=BorderFactory.createBevelBorder(1);
	for(int i=0;i<output.length;i++){
		tp[i]=new JTextField("0.0",7);
		tp[i].setBorder(b);
//		tp[i].setBackground(Color.black);
//		tp[i].setForeground(Color.green);
		tp[i].setEditable(false);

		olabel[i]=new JLabel((i==0)?"Odd":"Even");
		olabel[i].setFont(new Font("Dialog",Font.BOLD, 14));

		opanel[i]=new JPanel0();
		opanel[i].setLayout(null);
		opanel[i].add(olabel[i]);
		opanel[i].add(tp[i]);

		rightpanel.add(opanel[i]);
		rightpanel.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){layoutRightPanel();}
		});
	}
	forget();
	}
	void layoutRightPanel(){
		int y=rightpanel.getHeight()/output.length/2;
		for(int i=0;i<output.length;i++){
			Rectangle s=new Rectangle(new Point(0,y), olabel[i].getPreferredSize());
			olabel[i].setBounds(s);
			s=new Rectangle(new Point(40,y), tp[i].getPreferredSize());
			tp[i].setBounds(s);
		}
	}


	private void jbInit() throws Exception {

		leftpanel.setLayout(gridLayout1);
		gridLayout1.setColumns(1);
		gridLayout1.setRows(input.length);
		rightpanel.setLayout(gridLayout2);
		gridLayout2.setColumns(1);
		gridLayout2.setRows(output.length);

		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		randombutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				randombutton_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout2);
		jButton1.setBackground(Color.lightGray);
		jButton1.setText("Return");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jButton2.setBackground(Color.lightGray);
		jButton2.setText("Forget");
		randombutton.setBackground(Color.lightGray);
		randombutton.setMnemonic('0');
		randombutton.setText("Random stimuli");
		jPanel3.setLayout(flowLayout1);
		jButton3.setBackground(Color.lightGray);
		jButton3.setText("Repeat stimulus");
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton3_actionPerformed(e);
			}
		});
		examplestxt.setPreferredSize(new Dimension(35, 21));
		examplestxt.setText("0");
		examplestxt.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("Seen");
		jLabel2.setText("Accu%");
		accutxt.setPreferredSize(new Dimension(28, 21));
		accutxt.setText("0");
		rightpanel.setPreferredSize(new Dimension(100, 0));
		jPanel1.add(graphic, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jLabel2, null);
		jPanel3.add(accutxt, null);
		jPanel3.add(jLabel1, null);
		jPanel3.add(examplestxt, null);
		jPanel3.add(jButton3, null);
		jPanel3.add(randombutton, null);
		jPanel3.add(jButton2, null);
		jPanel3.add(jButton1, null);
		jPanel1.add(rightpanel, BorderLayout.EAST);
		jPanel1.add(leftpanel, BorderLayout.WEST);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	void jButton2_actionPerformed(ActionEvent e) {
	forget();
	}

	void randombutton_actionPerformed(ActionEvent e) {
	if(randombutton.isSelected()){timer.start();
	}else{timer.stop();
	}
	}

	void jButton1_actionPerformed(ActionEvent e) {
	getHolder().setExhibit("com.neurolab.NeuralNetwork");
	}
	JButton jButton3 = new JButton();
	JTextField examplestxt = new JTextField();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JTextField accutxt = new JTextField();

	void jButton3_actionPerformed(ActionEvent e) {
	newinput();
	}
}
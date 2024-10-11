
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import com.neurolab.common.GraphicComponent;
import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.MidiGenerator;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

public class CerebellarLearning extends NeurolabExhibit {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel0();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel2 = new JPanel0();
  ReturnButton returnButton1 = new ReturnButton();
  JButton forget = new JButton();
  JRadioButton execute = new JRadioButton0();
  JRadioButton learn = new JRadioButton0();
  ButtonGroup bg1 = new ButtonGroup();
  JPanel mainpanel = new JPanel0();
  JPanel purarray = new JPanel0();
  JPanel jPanel5 = new JPanel0();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  GraphicComponent graphicComponent1 = new GraphicComponent();
  GraphicComponent graphicComponent2 = new GraphicComponent();
  GraphicComponent parascend = new GraphicComponent();



	static final int[] midiNotes={70,72,74,75,77};
	MidiGenerator midi=new MidiGenerator();

	public String getExhibitName(){return "Cerebellar sequencer";}
  public CerebellarLearning() {
  }
	int npurk=5,npar=9;
	PurkinjeCell[] purk=new PurkinjeCell[npurk];
	ParallelFibre[] parf=new ParallelFibre[npar];

  public void init(){
    super.init();
    try {
      jbInit();
      bg1.add(learn);bg1.add(execute);
      myinit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	double[][] strength=new double [npurk][npar];
	boolean[][] patterns=new boolean [npurk][npar];
	public void myinit(){
    		mainpanel.add(purarray, null);
		purarray.setOpaque(false);
		mainpanel.add(graphicComponent1, null);
		mainpanel.add(graphicComponent2, null);
		mainpanel.add(parascend, null);
		int dy=8;
		for(int i=0;i<npar;i++){
			parf[i]=new ParallelFibre(i);
			mainpanel.add(parf[i].parax, null);
			mainpanel.add(parf[i].parbend, null);
		}
		for(int i=0;i<npurk;i++){
			purk[i]=new PurkinjeCell(i);
			purarray.add(purk[i]);
		}
		purk[npurk-1].remove(purk[npurk-1].outputAxon);
		forget.doClick();
		learn.setSelected(true);
		for(int i=0;i<npurk;i++){purk[i].setCellColor(colInactive);}

		patterns[0][0] = patterns[0][3] = patterns[0][6] = true;
		patterns[1][1] = patterns[1][4] = patterns[1][8] = true;
		patterns[2][2] = patterns[2][5] = patterns[2][7] =
		patterns[3][0] = patterns[3][7] = patterns[3][6] =
		patterns[4][1] = patterns[4][2] = patterns[4][3] = true;

	}



  private void jbInit() throws Exception {
    forget.setBackground(systemGray);
    jPanel1.setLayout(borderLayout2);
    forget.setText("Forget");
    forget.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        forget_actionPerformed(e);
      }
    });
    execute.setText("Execute");
    learn.setText("Learn");
    mainpanel.setBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)));
    mainpanel.setLayout(null);
    purarray.setBounds(new Rectangle(93, 12, 472, 255));
    purarray.setLayout(gridLayout1);
    jPanel5.setBounds(new Rectangle(6, 113, 83, 49));
    jPanel5.setLayout(flowLayout1);
    jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16));
    jLabel1.setText("Response");
    jLabel2.setFont(new java.awt.Font("SansSerif", 1, 16));
    jLabel2.setText("Feedback");
    flowLayout1.setHgap(0);
    flowLayout1.setVgap(0);
    gridLayout1.setColumns(npurk);
    graphicComponent1.setType(2);
    graphicComponent1.setBounds(new Rectangle(39, 242, 55, 13));
    graphicComponent2.setType(3);
    graphicComponent2.setThickness(3);
    graphicComponent2.setArrow(2);
    graphicComponent2.setBounds(new Rectangle(34, 172, 12, 76));
    parascend.setType(3);
    parascend.setThickness(3);
    parascend.setBounds(new Rectangle(32, 40, 16, 72));
    getMainContainer().addKeyListener(new java.awt.event.KeyAdapter() {


      public void keyTyped(KeyEvent e) {
        this_keyTyped(e);
      }
    });
    jPanel1.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(learn, null);
    jPanel2.add(execute, null);
    jPanel2.add(forget, null);
    jPanel2.add(returnButton1, null);
    jPanel1.add(mainpanel, BorderLayout.CENTER);
    mainpanel.add(jPanel5, null);
    jPanel5.add(jLabel1, null);
    jPanel5.add(jLabel2, null);

    getMainContainer().setLayout(borderLayout1);
    getMainContainer().add(jPanel1, BorderLayout.CENTER);
  }
	public void activateParallels(PurkinjeCell src){
		int i=getPurkinjeIndex(src);
		for(int k=0;k<npurk;k++){		//zero all
			purk[k].activity=-0.5;
		}
		for(int j=0;j<npar;j++){
			if(patterns[i][j]){
				parf[j].activate();
				if(!learn.isSelected()){
					for(int k=0;k<npurk;k++){	// activate corresponding purkinje cells
System.out.print("purk "+k+" activated "+purk[k].activity);
						purk[k].activate(strength[k][j]);
System.out.println("->"+purk[k].activity);
					}
				}
			}
		}
	}
	class ParallelFibre implements ActionListener{
		int activity=0;
		Color colInactive=new Color(192,0,192);
		Color colActive=Color.red;
		public ParallelFibre(int i){
			int dy=8;
			parax.setType(2);
			parax.setThickness(3);
			parax.setBounds(new Rectangle(50, 21+i*dy, 507, 18));
			parbend.setThickness(3);
			parbend.setBounds(new Rectangle(39, 29+i*dy, 12, 13));
			parax.setOpaque(false);
			parbend.setOpaque(false);
			setCol(colInactive);
		}

		Timer timer=new Timer(750,this);
		public void actionPerformed(ActionEvent e){
			if(activity>0){
				activity--;
				if(activity==0){
					setCol(colInactive);
					timer.stop();
				}
			}
		}
		public void setCol(Color c){
			parax.setForeground(c);
			parbend.setForeground(c);
			parascend.setForeground(c);
		}
		public void activate(){
			activity++;
			setCol(colActive);
			timer.start();
		}

		GraphicComponent parax=new GraphicComponent();
		GraphicComponent parbend=new GraphicComponent();
	}

	public int getPurkinjeIndex(PurkinjeCell src){
		int j=0;
		for(int i=0;i<npurk;i++){
			if(src==purk[i])j=i;
		}		// find out the index of the source purkinje cell
		return j;
	}

	static final Color colInactive=new Color(0,0,128);
	static final Color colActive=Color.red;

	class PurkinjeCell extends JPanel implements ActionListener{

		GraphicComponent graphicComponent1 = new GraphicComponent();
		GraphicComponent graphicComponent2 = new GraphicComponent();
		GraphicComponent outputAxon = new GraphicComponent();
		GraphicComponent graphicComponent3 = new GraphicComponent();
		GraphicComponent graphicComponent4 = new GraphicComponent();
		GraphicComponent graphicComponent5 = new GraphicComponent();
		GraphicComponent graphicComponent6 = new GraphicComponent();
		GraphicComponent graphicComponent7 = new GraphicComponent();
		GraphicComponent graphicComponent8 = new GraphicComponent();

		GraphicComponent stim1=new GraphicComponent();
		GraphicComponent stim2=new GraphicComponent();
		GraphicComponent stim3=new GraphicComponent();
		JButton stimB=new JButton();
		public PurkinjeCell(int i){
			setLayout(null);
			graphicComponent1.setType(4);
			graphicComponent1.setBounds(new Rectangle(25, 135, 43, 44));
			graphicComponent2.setForeground(Color.blue);
			graphicComponent2.setBounds(new Rectangle(0, 171, 37, 66));
			outputAxon.setForeground(Color.red);
			outputAxon.setType(2);
			outputAxon.setBounds(new Rectangle(0, 228, 95, 18));
			graphicComponent3.setType(3);
			graphicComponent3.setThickness(3);
			graphicComponent3.setBounds(new Rectangle(37, 7, 10, 133));

			graphicComponent4.setDirection(true);
			graphicComponent4.setThickness(3);
			graphicComponent4.setBounds(new Rectangle(31, 111, 12, 14));

			graphicComponent5.setType(3);
			graphicComponent5.setThickness(3);
			graphicComponent5.setBounds(new Rectangle(46, 7, 10, 133));

			graphicComponent6.setThickness(3);
			graphicComponent6.setBounds(new Rectangle(50, 110, 12, 14));

			graphicComponent7.setType(3);
			graphicComponent7.setThickness(3);
			graphicComponent7.setBounds(new Rectangle(54, 7, 12, 106));
			graphicComponent8.setType(3);
			graphicComponent8.setThickness(3);
			graphicComponent8.setBounds(new Rectangle(26, 7, 13, 107));

			stim1.setBounds(new Rectangle(70,180,15,30));
			stim1.setThickness(3);
			stim1.setDirection(true);
			stim2.setBounds(new Rectangle(60,175,10,10));
			stim2.setType(2);
			stim2.setThickness(3);
			stim3.setBounds(new Rectangle(65,170,10,10));
			stim3.setType(3);
			stim3.setThickness(3);
			stimB.setBounds(new Rectangle(48,200,45,20));
			stimB.setText( String.valueOf((char)('C'+i)) );
			stimB.setBackground(systemGray);
			add(stimB);
			add(graphicComponent1);
			add(graphicComponent2);
			add(graphicComponent3);
			add(graphicComponent4);
			add(graphicComponent5);
			add(graphicComponent6);
			add(graphicComponent7);
			add(graphicComponent8);
			add(outputAxon);
			add(stim1);
			add(stim2);
			add(stim3);
			setStimColor(colInactive);
			setOpaque(false);

			stimB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					setStimColor(colActive);
					timer.start();
				}
			});
			timer.setRepeats(false);
			timer2.setRepeats(false);
		}

		public boolean isActive=false;
		public double activity;

		Timer timer=new Timer(500,this);

		public void actionPerformed(ActionEvent e){		// when my action is performed, fire the cell
			setStimColor(colInactive);
			setCellColor(colActive);
			isActive=true;
			int me=getPurkinjeIndex(this);

			midi.playNote(midiNotes[me]);
			if(learn.isSelected()){
				for(int i=0;i<npar;i++){
					strength[me][i] += (parf[i].activity>0) ? (1-strength[me][i])*0.5 : -(strength[me][i]+1)*0.2;
				}
			}
			timer2.start();
		}
		Timer timer2=new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setCellColor(colInactive);
				activateParallels(PurkinjeCell.this);
				isActive=false;
			}
		});
		public void setCellColor(Color c){
			graphicComponent1.setForeground(c);
			graphicComponent2.setForeground(c);
			graphicComponent3.setForeground(c);
			graphicComponent4.setForeground(c);
			graphicComponent5.setForeground(c);
			graphicComponent6.setForeground(c);
			graphicComponent7.setForeground(c);
			graphicComponent8.setForeground(c);
			for(int i=getPurkinjeIndex(this)-1;i>=0;i--){
				purk[i].outputAxon.setForeground(c);	//colour in axon
			}
			CerebellarLearning.this.graphicComponent1.setForeground(c);
			CerebellarLearning.this.graphicComponent2.setForeground(c);
		}
		public void setStimColor(Color c){
			stim1.setForeground(c);
			stim2.setForeground(c);
			stim3.setForeground(c);
		}
		public void activate(double stim){
			activity+=stim;
			if(activity>0.5){
				activity=-0.5;
				timer.start();
			}
		}
	}

  void forget_actionPerformed(ActionEvent e) {
	for(int i=0;i<npurk;i++){
		for(int j=0;j<npar;j++){
			strength[i][j]=0;
		}
	}
  }


  void this_keyTyped(KeyEvent e) {
	char c=e.getKeyChar();
	if(c>='a' && c<='e'){
		purk[(int)(c-'a')].actionPerformed( new ActionEvent(this,ActionEvent.ACTION_PERFORMED, String.valueOf(c)) );
	}

  }
	public void close(){
		midi.close();
		for(int i=0;i<purk.length;i++) {
		  purk[i].timer.stop();
		  purk[i].timer2.stop();
		}
		for(int i=0;i<parf.length;i++) {
		  parf[i].timer.stop();
		}
	}
}
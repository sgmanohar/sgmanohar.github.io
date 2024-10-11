
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import com.neurolab.common.*;
import javax.swing.border.*;

public class NeuralNetwork extends NeurolabExhibit {
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	JButton jButton1 = new JButton();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel buttonpanel = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	JPanel graphic = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		int w=getWidth()/nColumns,h=getHeight()/nRows,cw=h/2,ch=h/2;
		for(int i=0;i<nColumns;i++)for(int j=0;j<nRows;j++){
				//draw neurone bodies
			g.setColor(on[i][j]?Color.red:Color.blue);
			g.fillOval(i*w+w/4, j*h+h/4, cw, ch);
			g.setColor(Color.black);
			g.drawOval(i*w+w/4, j*h+h/4, cw, ch);
			if(i<nColumns-1){	//draw connections
				g.setColor(Color.blue);
				for(int r=0;r<nRows;r++){
					g.drawLine(i*w+w/4+cw, j*h+h/2, (i+1)*w+w/4, r*h+h/2);
				}
			}
		}
	}
	};


		//these are the changeable parameters
	int nRows=7,nColumns=5;
	int[][] connection=new int[][]{
		{2,4,3,1,0,6,5},
		{5,3,2,4,1,0,6},
		{6,0,4,2,3,1,5},
		{1,6,4,2,3,5,0},
		{4,1,5,3,2,0,6}
	};



	boolean[][] on=new boolean[nColumns][nRows];
	JCheckBox[] cb=new JCheckBox[nRows];
	JPanel jPanel2 = new JPanel0();
	GridLayout gridLayout2 = new GridLayout();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel6 = new JLabel();
	JLabel jLabel7 = new JLabel();
	Border border1;

	public NeuralNetwork() {
	}
	public void init(){
		super.init();
		try {jbInit();}
		catch(Exception e) {e.printStackTrace();}
		ItemListener il=new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				for(int i=0;i<nRows;i++)if(e.getItem()==cb[i]){
					startNeurone(i,cb[i].isSelected());
				}
			}
		};
		for(int i=0;i<nRows;i++){
			cb[i]=new JCheckBox();
			cb[i].setBackground(systemGray);
			buttonpanel.add(cb[i]);
			cb[i].addItemListener(il);
		}
	}

//?need vector for active timers to prevent null pointers, finalize, etc?
	Vector timers=new Vector();
	public void startNeurone(int i,boolean f){
		timers.add(new NeuroneTimer(i,f));
	}
	class NeuroneTimer implements ActionListener{
		int currentrow,currentcolumn;
		boolean select;
		Timer timer=new Timer(700,this);
		public NeuroneTimer(int startrow,boolean on){
			currentrow=startrow; select=on;
			timer.start();
		}
		public void actionPerformed(ActionEvent e){
			if(currentcolumn<nColumns-1){
				on[currentcolumn][currentrow]=select;
			}else{	//switch off all in last column
				int count=0;
				for(int i=0;i<nRows;i++){
					on[currentcolumn][i]=false;
					if(on[currentcolumn-1][i])count++;
				}
				if(count>0)on[currentcolumn][count-1]=true;
			}
			currentrow=connection[currentcolumn++][currentrow];
			if(currentcolumn>=nColumns)timer.stop();
			graphic.repaint();
		}
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(0,10,0,10);
		getMainContainer().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		jButton1.setBackground(Color.lightGray);
		jButton1.setText("Learning");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		graphic.setLayout(null);
		buttonpanel.setLayout(gridLayout1);
		gridLayout1.setColumns(1);
		gridLayout1.setRows(nRows);
		jPanel2.setLayout(gridLayout2);
		jLabel1.setBackground(Color.lightGray);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel1.setText("1");
		jLabel2.setBackground(Color.lightGray);
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel2.setText("2");
		gridLayout2.setRows(7);
		jLabel3.setBackground(Color.lightGray);
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel3.setText("3");
		jLabel4.setBackground(Color.lightGray);
		jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel4.setText("4");
		jLabel5.setBackground(Color.lightGray);
		jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel5.setText("5");
		jLabel6.setBackground(Color.lightGray);
		jLabel6.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel6.setText("7");
		jLabel7.setBackground(Color.lightGray);
		jLabel7.setFont(new java.awt.Font("Dialog", 1, 14));
		jLabel7.setText("6");
		jPanel2.setBorder(border1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(graphic, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jButton1, null);
		jPanel3.add(returnButton1, null);
		jPanel1.add(buttonpanel, BorderLayout.WEST);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jLabel1, null);
		jPanel2.add(jLabel2, null);
		jPanel2.add(jLabel3, null);
		jPanel2.add(jLabel4, null);
		jPanel2.add(jLabel5, null);
		jPanel2.add(jLabel7, null);
		jPanel2.add(jLabel6, null);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	getHolder().setExhibit("com.neurolab.NetworkLearning");
	}
	public void close() {
	  for(int i=0;i<timers.size();i++) {
	    Object o=(timers.get(i));
	    if(o instanceof NeuroneTimer) {
	      ((NeuroneTimer)o).timer.stop();
	    }
	  }
	}
}
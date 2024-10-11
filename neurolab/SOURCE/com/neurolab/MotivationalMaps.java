
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import javax.swing.*;
import java.awt.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.event.*;

public class MotivationalMaps extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	JPanel jPanel3 = new JPanel0();
	JPanel jPanel4 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel5 = new JPanel0();
	JPanel jPanel6 = new JPanel0();
	JButton jButton2 = new JButton();
	ReturnButton returnButton1 = new ReturnButton();
	Border border1;
	Border border2;
	TitledBorder titledBorder1;
	Border border3;
	Border border4;
	TitledBorder titledBorder2;
	Border border5;
	JCheckBox nsafety = new JCheckBox();
	JCheckBox nsex = new JCheckBox();
	JCheckBox ndrink = new JCheckBox();
	JCheckBox nfood = new JCheckBox();
	JRadioButton cfood = new JRadioButton0();
	JRadioButton cdrink = new JRadioButton0();
	JRadioButton csex = new JRadioButton0();
	JRadioButton cmenace = new JRadioButton0();
	FlowLayout flowLayout2 = new FlowLayout();
	JLabel jLabel1 = new JLabel();
	Border border6;
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel arena = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		g.setFont(new Font("Dialog",Font.BOLD,18));
		for(Enumeration e=items.elements();e.hasMoreElements();){
			Item i=(Item)e.nextElement();
			g.setColor(Item.col[i.type]);
			g.drawString(Item.str[i.type],i.pos.x-9,i.pos.y+9);
		}
		g.setColor(Color.magenta);
		g.fillOval((int)px-radius,(int)py-radius,radius*2,radius*2);
	}
	};
	Vector items=new Vector();
	double px,py;
	int radius=10;
	ButtonGroup bg = new ButtonGroup();
	boolean threadrunning=false;
	Thread thread=new Thread(new Runnable(){
		public void run(){
		  threadrunning=true;
			while(threadrunning){
				move();try{Thread.sleep(100);}
				catch(Exception e){e.printStackTrace();}
			}
		}
	});
	FlowLayout flowLayout1 = new FlowLayout();
	FlowLayout flowLayout3 = new FlowLayout();

	void move(){
		double dx=0,dy=0,norm=0;
		Vector removal=new Vector();
		synchronized(items){
			for(Enumeration e=items.elements();e.hasMoreElements();){
				Item i=(Item)e.nextElement();
				if(needs(i.type)){
					double u=i.pos.x-px,v=i.pos.y-py,
						w=1/(u*u+v*v+0.2);
					if(w>3E-2){i.strength=0;removal.addElement(i);continue;}
					dx+=w*w*u*i.strength; dy+=w*w*v*i.strength;
					norm+=w*w;
				}
			}
			removeAll(items,removal);
		}
		if(norm>0){
			dx/=norm;dy/=norm;
			px+=0.2*dx+0.1*Math.random();
			py+=0.2*dy+0.1*Math.random();
			int r;
			if(px<0)px=0;else if(px>(r=arena.getBounds().width))px=r;
			if(py<0)py=0;else if(py>(r=arena.getBounds().height))py=r;
		}
		if(norm>0 || removal.size()>0)arena.repaint();
	}

	public String getExhibitName(){return "Motivational Map";}

	public MotivationalMaps() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	bg.add(cfood);bg.add(csex);bg.add(cdrink);bg.add(cmenace);
		//centre it
	px=arena.getBounds().width/2;
	py=arena.getBounds().height/2;
	px=100;py=100;

	thread.start();
		//Mouse click creates item
	arena.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			Item i=new Item(getItemTypeSelected());
			i.pos=e.getPoint();
			items.addElement(i);
			arena.repaint();
		}
	});
	}

	private void jbInit() throws Exception {
	 nsafety.setBackground(systemGray);
	 nsex.setBackground(systemGray);
	 ndrink.setBackground(systemGray);
	 nfood.setBackground(systemGray);
	 jButton2.setBackground(systemGray);
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2));
		border2 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border2,"Current need");
		border3 = BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Current need"),BorderFactory.createEmptyBorder(2,20,2,20));
		border4 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder2 = new TitledBorder(border4,"Create map");
		border5 = BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Create map"),BorderFactory.createEmptyBorder(2,20,2,20));
		border6 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		jPanel1.setLayout(borderLayout2);
		jPanel3.setLayout(borderLayout3);
		jButton2.setText("Clear");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jPanel4.setBorder(border3);
		jPanel4.setPreferredSize(new Dimension(251, 130));
		jPanel4.setLayout(flowLayout1);
		jPanel5.setBorder(border5);
		jPanel5.setLayout(flowLayout2);
		nsafety.setText("Safety");
		nsafety.setForeground(Color.yellow);
		nsafety.setFont(new java.awt.Font("Dialog", 1, 12));
		nsex.setText("Sex");
		nsex.setForeground(Color.blue);
		nsex.setFont(new java.awt.Font("Dialog", 1, 12));
		ndrink.setText("Drink");
		ndrink.setForeground(new Color(0, 128, 0));
		ndrink.setFont(new java.awt.Font("Dialog", 1, 12));
		nfood.setText("Food");
		nfood.setForeground(Color.red);
		nfood.setFont(new java.awt.Font("Dialog", 1, 12));
		cfood.setSelected(true);
		cfood.setText("Food");
		cfood.setForeground(Color.red);
		cfood.setFont(new java.awt.Font("Dialog", 1, 12));
		cdrink.setText("Drink");
		cdrink.setForeground(new Color(0, 128, 0));
		cdrink.setFont(new java.awt.Font("Dialog", 1, 12));
		csex.setText("Sex");
		csex.setForeground(Color.blue);
		csex.setFont(new java.awt.Font("Dialog", 1, 12));
		cmenace.setText("Menace");
		cmenace.setForeground(Color.yellow);
		cmenace.setFont(new java.awt.Font("Dialog", 1, 12));
		jPanel3.setPreferredSize(new Dimension(150, 171));
		flowLayout2.setAlignment(FlowLayout.LEFT);
		flowLayout2.setVgap(0);
		jLabel1.setText("Click to place item");
		jPanel2.setBorder(border6);
		jPanel2.setLayout(borderLayout4);
		arena.setBackground(Color.black);
		jPanel6.setLayout(flowLayout3);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		flowLayout1.setVgap(0);
		jPanel1.add(jPanel3, BorderLayout.EAST);
		jPanel3.add(jPanel4, BorderLayout.NORTH);
		jPanel4.add(nfood, null);
		jPanel4.add(ndrink, null);
		jPanel4.add(nsex, null);
		jPanel4.add(nsafety, null);
		jPanel3.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(cfood, null);
		jPanel5.add(cdrink, null);
		jPanel5.add(csex, null);
		jPanel5.add(cmenace, null);
		jPanel5.add(jLabel1, null);
		jPanel3.add(jPanel6, BorderLayout.SOUTH);
		jPanel6.add(jButton2, null);
		jPanel6.add(returnButton1, null);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(arena, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);

	}

	public boolean needs(int i){
		if(i==Item.FOOD)return nfood.isSelected();
		if(i==Item.DRINK)return ndrink.isSelected();
		if(i==Item.SEX)return nsex.isSelected();
		if(i==Item.THREAT)return nsafety.isSelected();
		return false;
	}
	public int getItemTypeSelected(){
		if(cfood.isSelected())return Item.FOOD;
		if(cdrink.isSelected())return Item.DRINK;
		if(csex.isSelected())return Item.SEX;
		if(cmenace.isSelected())return Item.THREAT;
		return -1;
	}

	void jButton2_actionPerformed(ActionEvent e) {
	items.removeAllElements();
	arena.repaint();
	}
	public void close() {
	  threadrunning=false;
	}
}
	class Item{
		static final int FOOD=0,DRINK=1,SEX=2,THREAT=3;
		static final String[] str=new String[]{"F","D","S","M"};
		static final Color[] col=new Color[]{ Color.red,Color.green,
			Color.blue,Color.yellow };
		public Item(int type){
			this.type=type;
			switch(type){
				case FOOD:case SEX:case DRINK: strength=1;break;
				case THREAT:strength=-1;break;
			}
		}
		Point pos;
		int strength;
		int type;
	}

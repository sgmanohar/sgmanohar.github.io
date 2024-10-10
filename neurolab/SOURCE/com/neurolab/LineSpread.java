
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

import javax.swing.*;
import java.awt.*;
import com.neurolab.common.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;

public class LineSpread extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel0();
	BorderLayout borderLayout3 = new BorderLayout();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel3 = new JPanel0();
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel4 = new JPanel0();
	JSlider linespread = new JSlider();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel5 = new JPanel0();
	Border border1;
	TitledBorder titledBorder1;
	FlowLayout flowLayout1 = new FlowLayout();
	JRadioButton singleline = new JRadioButton0();
	JRadioButton singlebar = new JRadioButton0();
	JRadioButton singleedge = new JRadioButton0();
	JRadioButton twolines = new JRadioButton0();
	JCheckBox s_invert = new JCheckBox();
	JPanel spacingpanel = new JPanel0();
	JLabel jLabel2 = new JLabel();
	JSlider spacing = new JSlider();
	JPanel jPanel7 = new JPanel0();
	Border border2;
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel8 = new JPanel0();
	JPanel jPanel9 = new JPanel0();
	Border border3;
	BorderLayout borderLayout6 = new BorderLayout();
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	Label3D label3D4 = new Label3D();
	Label3D label3D5 = new Label3D();
	ButtonGroup bg1=new ButtonGroup();
	Border border4;
	Border border5;


	final int margin=20,npoints=400;
	JPanel graph = new JPanel(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);

		g.setColor(Color.white);
		setStrokeThickness(g,2);
		int base[]=new int[3];
		for(int i=0;i<3;i++){			// draw baselines
			base[i]=(i+1)*getHeight()/3-10;
			g.drawLine(margin,base[i],getWidth()-margin,base[i]);
		}

		int hstim=getHeight()/3-20,h,b1;
		int L=npoints/2-spacing.getValue(),R=npoints/2+spacing.getValue();
		if(s_invert.isSelected()){		//inverse?
			g.fillRect(margin,0,getWidth()-2*margin-1,base[0]);
			g.setColor(Color.black);
			b1=base[1]-hstim;h=-hstim;
		}else{
			b1=base[1];h=hstim;
		}
		if(singleline.isSelected()){		// draw stimuli
			g.drawLine(getWidth()/2,base[0],getWidth()/2,0);
			g.setColor(Color.yellow);
			g.drawLine(margin,b1,getWidth()-margin,b1);
			g.drawLine(getWidth()/2,b1,getWidth()/2,b1-h);
												spacing.setVisible(false);
												jLabel2.setText("");
		}else if(singlebar.isSelected()){
			g.fillRect((int)convertx(L),0,(int)(convertx(R)-convertx(L)),base[0]);
			g.setColor(Color.yellow);
			g.drawLine(margin,b1,convertx(L),b1);
			g.drawLine(convertx(L),b1,convertx(L),b1-h);
			g.drawLine(convertx(L),b1-h,convertx(R),b1-h);
			g.drawLine(convertx(R),b1-h,convertx(R),b1);
			g.drawLine(convertx(R),b1,getWidth()-margin,b1);
												spacing.setVisible(true);
												jLabel2.setText("spacing");
		}else if(singleedge.isSelected()){
			g.fillRect(getWidth()/2,0,getWidth()/2-margin,base[0]);
			g.setColor(Color.yellow);
			g.drawLine(margin,b1,getWidth()/2,b1);
			g.drawLine(getWidth()/2,b1,getWidth()/2,b1-h);
			g.drawLine(getWidth()/2,b1-h,getWidth()-margin,b1-h);
												spacing.setVisible(false);
												jLabel2.setText("");
		}else if(twolines.isSelected()){
			g.drawLine(convertx(L),base[0],convertx(L),0);
			g.drawLine(convertx(R),base[0],convertx(R),0);
			g.setColor(Color.yellow);
			g.drawLine(margin,b1,getWidth()-margin,b1);
			g.drawLine(convertx(L),b1,convertx(L),b1-h);
			g.drawLine(convertx(R),b1,convertx(R),b1-h);
												jLabel2.setText("spacing");
												spacing.setVisible(true);
		}
							// draw result
		g.setColor(Color.green);
		setStrokeThickness(g,2);
		Point op,np=null;
		int v=0;
		for(int i=0;i<npoints;i++){
			if(singleline.isSelected())	 v=spread(i-npoints/2+15)-spread(i-npoints/2-15);
			else if(singlebar.isSelected())	 v=spread(i-L)-spread(i-R);
			else if(singleedge.isSelected()) v=spread(i-npoints/2);
			else if(twolines.isSelected())	 v=spread(i-L+15)-spread(i-L-15)+spread(i-R+15)-spread(i-R-15);
			if(s_invert.isSelected())	 v=1000-v;
			op=np;np=new Point(convertx(i),base[2]-v/11);
			if(op==null)op=np;	// at start, use first point as prev
			g.drawLine(
					op.x, op.y,
					np.x, np.y );
		}
	}
	};
	public int convertx(int x){return margin+x*(graph.getWidth()-2*margin)/npoints;}
	public int spread(double z){
		z/=4+(linespread.getValue()*3/2);
		if(z<=0)return (int)(500*Math.exp(z));
		else return 1000-(int)(500*Math.exp(-z));
	}

	public String getExhibitName() {
		return "Linespread";
	}

	public LineSpread() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		singleline.setSelected(true);
	}

	private void jbInit() throws Exception {
		spacing.setBackground(systemGray);
		linespread.setBackground(systemGray);
		s_invert.setBackground(systemGray);
		border1 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border1,"Stimulus");
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(5,5,5,5));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(93, 93, 93),new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2));
		border4 = BorderFactory.createLineBorder(Color.lightGray,1);
		border5 = BorderFactory.createLineBorder(Color.lightGray,1);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		jPanel3.setLayout(borderLayout4);
		jPanel4.setPreferredSize(new Dimension(120, 50));
		jPanel4.setLayout(null);
		linespread.setBorder(border5);
		linespread.setBounds(new Rectangle(2, 19, 113, 24));
		linespread.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				stimchanged(e);
			}
		});
		jLabel1.setText("Line-spread width");
		jLabel1.setBounds(new Rectangle(7, 2, 106, 14));
		jPanel5.setBorder(titledBorder1);
		jPanel5.setMaximumSize(new Dimension(125, 32767));
		jPanel5.setPreferredSize(new Dimension(125, 63));
		jPanel5.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		singleline.setText("single line");
		singleline.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchanged(e);
			}
		});
		singlebar.setText("single bar");
		singlebar.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchanged(e);
			}
		});
		singleedge.setText("single edge");
		singleedge.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchanged(e);
			}
		});
		twolines.setText("two lines");
		twolines.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchanged(e);
			}
		});
		s_invert.setText("Black-on-white");
		s_invert.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchanged(e);
			}
		});
		jLabel2.setText("spacing");
		jLabel2.setBounds(new Rectangle(29, 20, 52, 17));
		spacingpanel.setLayout(null);
		spacing.setBorder(border4);
		spacing.setBounds(new Rectangle(2, 0, 98, 24));
		spacing.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				stimchanged(e);
			}
		});
		spacingpanel.setPreferredSize(new Dimension(100, 40));
		jPanel7.setBorder(border2);
		jPanel7.setLayout(borderLayout5);
		jPanel8.setPreferredSize(new Dimension(120, 10));
		jPanel8.setLayout(null);
		jPanel9.setBorder(border3);
		jPanel9.setLayout(borderLayout6);
		graph.setBackground(Color.black);
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D1.setText("Stimulus");
		label3D1.setBounds(new Rectangle(28, 22, 83, 25));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D2.setText("distribution");
		label3D2.setBounds(new Rectangle(4, 123, 111, 22));
		label3D3.setBounds(new Rectangle(30, 99, 83, 25));
		label3D3.setText("Stimulus");
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D4.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D4.setText("Image");
		label3D4.setBounds(new Rectangle(54, 197, 61, 25));
		label3D5.setBounds(new Rectangle(3, 219, 111, 22));
		label3D5.setText("distribution");
		label3D5.setFont(new java.awt.Font("SansSerif", 1, 16));
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(returnButton1, BorderLayout.SOUTH);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel4, BorderLayout.SOUTH);
		jPanel4.add(jLabel1, null);
		jPanel4.add(linespread, null);
		jPanel3.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(singleline, null);
		jPanel5.add(singleedge, null);
		jPanel5.add(singlebar, null);
		jPanel5.add(twolines, null);
		jPanel5.add(s_invert, null);
		jPanel5.add(spacingpanel, null);
		spacingpanel.add(spacing, null);
		spacingpanel.add(jLabel2, null);
		jPanel1.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(jPanel8, BorderLayout.WEST);
		jPanel8.add(label3D2, null);
		jPanel8.add(label3D1, null);
		jPanel8.add(label3D5, null);
		jPanel8.add(label3D3, null);
		jPanel8.add(label3D4, null);
		jPanel7.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(graph, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);

		bg1.add(singleline);bg1.add(singlebar);
		bg1.add(singleedge);bg1.add(twolines);
		linespread.setValue(20);
	}


	void stimchanged(ChangeEvent e) {
	graph.repaint();
	}

	void stimchanged(ActionEvent e) {
	//if(singleline.isSelected() || singleedge.isSelected())spacingpanel.setVisible(false); else spacingpanel.setVisible(true);
	graph.repaint();
	}
}
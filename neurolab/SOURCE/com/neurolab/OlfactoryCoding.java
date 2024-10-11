
/**
 * Title:        Neurolab
 * <p>
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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.neurolab.common.GraphicComponent;
import com.neurolab.common.JPanel0;
import com.neurolab.common.JRadioButton0;
import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

public class OlfactoryCoding extends NeurolabExhibit{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel2 = new JPanel0();
	GridLayout gridLayout1 = new GridLayout();
	JRadioButton jRadioButton1 = new JRadioButton0();
	JRadioButton jRadioButton2 = new JRadioButton0();
	JRadioButton jRadioButton3 = new JRadioButton0();
	JRadioButton jRadioButton4 = new JRadioButton0();
	JRadioButton jRadioButton5 = new JRadioButton0();
	JRadioButton jRadioButton6 = new JRadioButton0();
	JRadioButton jRadioButton7 = new JRadioButton0();
	JRadioButton jRadioButton8 = new JRadioButton0();
	JRadioButton jRadioButton10 = new JRadioButton0();
	JPanel jPanel3 = new JPanel0();
	GridLayout gridLayout2 = new GridLayout();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel6 = new JLabel();
	JLabel jLabel7 = new JLabel();
	Border border1;
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	JPanel jPanel4 = new JPanel0();
	GridLayout gridLayout3 = new GridLayout();
	GraphicComponent graphicComponent1 = new GraphicComponent();
	GraphicComponent graphicComponent2 = new GraphicComponent();
	GraphicComponent graphicComponent5 = new GraphicComponent();
	GraphicComponent graphicComponent6 = new GraphicComponent();
	GraphicComponent graphicComponent7 = new GraphicComponent();
	GraphicComponent graphicComponent8 = new GraphicComponent();
	GraphicComponent graphicComponent9 = new GraphicComponent();
	GraphicComponent graphicComponent10 = new GraphicComponent();
	GraphicComponent graphicComponent11 = new GraphicComponent();
	GraphicComponent graphicComponent12 = new GraphicComponent();
	GraphicComponent graphicComponent13 = new GraphicComponent();
	GraphicComponent graphicComponent14 = new GraphicComponent();
	GraphicComponent graphicComponent15 = new GraphicComponent();
	GraphicComponent graphicComponent16 = new GraphicComponent();
	JPanel network = new JPanel0(){

	public void paint(Graphics g)
	{
		super.paint(g);
		boolean x;
		setStrokeThickness(g,3);
		Random rand=new Random (52);
		for (int i=0; i<receptor.length;i++)
//Draw initial lines
			{
		    g.setColor(receptor[i].getBackground());
		    g.drawLine(0,2+i*(getHeight()-4)/(receptor.length-1),50,2+i*(getHeight()-4)/(receptor.length-1));
			}
//Draw inhibitory and excitatory lines
			g.setColor(new Color(0,0,128)); // dark blue
			g.drawLine(50,0*getHeight()/(receptor.length-1),getWidth(),1*getHeight()/(receptor.length-1));
			g.drawLine(50,0*getHeight()/(receptor.length-1),getWidth(),3*getHeight()/(receptor.length-1));
			g.drawLine(50,2+0*getHeight()/(receptor.length-1),getWidth(),2+0*getHeight()/(receptor.length-1));
			g.drawLine(50,1*getHeight()/(receptor.length-1),getWidth(),5*getHeight()/(receptor.length-1));
			g.drawLine(50,1*getHeight()/(receptor.length-1),getWidth(),0*getHeight()/(receptor.length-1));
			g.drawLine(50,2*getHeight()/(receptor.length-1),getWidth(),2*getHeight()/(receptor.length-1));
			g.drawLine(50,2*getHeight()/(receptor.length-1),getWidth(),4*getHeight()/(receptor.length-1));
			g.drawLine(50,2*getHeight()/(receptor.length-1),getWidth(),2*getHeight()/(receptor.length-1));
			g.drawLine(50,3*getHeight()/(receptor.length-1),getWidth(),3*getHeight()/(receptor.length-1));
			g.drawLine(50,3*getHeight()/(receptor.length-1),getWidth(),1*getHeight()/(receptor.length-1));
			g.drawLine(50,4*getHeight()/(receptor.length-1),getWidth(),5*getHeight()/(receptor.length-1));
			g.drawLine(50,4*getHeight()/(receptor.length-1),getWidth(),5*getHeight()/(receptor.length-1));
			g.drawLine(50,5*getHeight()/(receptor.length-1),getWidth(),6*getHeight()/(receptor.length-1));
			g.drawLine(50,6*getHeight()/(receptor.length-1),getWidth(),5*getHeight()/(receptor.length-1));
			g.drawLine(50,-2+6*getHeight()/(receptor.length-1),getWidth(),-2+6*getHeight()/(receptor.length-1));
			g.setColor(new Color(128,0,0)); // dark red
			//g.drawLine(50,i*getHeight()/(receptor.length-1),getWidth(),i*getHeight()/(receptor.length-1));
			g.drawLine(50,0*getHeight()/(receptor.length-1),getWidth(),0*getHeight()/(receptor.length-1));
			g.drawLine(50,1*getHeight()/(receptor.length-1),getWidth(),1*getHeight()/(receptor.length-1));
			g.drawLine(50,1*getHeight()/(receptor.length-1),getWidth(),2*getHeight()/(receptor.length-1));
			g.drawLine(50,3*getHeight()/(receptor.length-1),getWidth(),4*getHeight()/(receptor.length-1));
			g.drawLine(50,3*getHeight()/(receptor.length-1),getWidth(),5*getHeight()/(receptor.length-1));
			g.drawLine(50,4*getHeight()/(receptor.length-1),getWidth(),6*getHeight()/(receptor.length-1));
			g.drawLine(50,5*getHeight()/(receptor.length-1),getWidth(),3*getHeight()/(receptor.length-1));
			

	}



/*  This method is not "intelligent" lines need to be drawn properly	public void paint(Graphics g_)
	{
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		Random rand=new Random(2);
		g.setStroke(new BasicStroke(3));
		for(int i=0;i<receptor.length;i++)
		{
			g.setColor(receptor[i].getBackground());
			g.drawLine(0,2+i*(getHeight()-4)/(receptor.length-1),50,2+i*(getHeight()-4)/(receptor.length-1));
			for(int j=0;j<bulb.length;j++)
			{
				g.setColor(new Color(rand.nextInt(150),0,rand.nextInt(150)));
				g.drawLine(50,i*getHeight()/(receptor.length-1),getWidth(),j*getHeight()/(receptor.length-1));
			}
		}
	}*/
	};

	GraphicComponent[] bulb=new GraphicComponent[]{graphicComponent1,graphicComponent15,graphicComponent13,graphicComponent11,graphicComponent9,graphicComponent8,graphicComponent6};
	JLabel[] receptor=new JLabel[]{jLabel1,jLabel2,jLabel3,jLabel4,jLabel5,jLabel6,jLabel7};
	JRadioButton[] stimulus=new JRadioButton[]{jRadioButton1,jRadioButton2,jRadioButton3,jRadioButton4,jRadioButton5,jRadioButton6,jRadioButton7,jRadioButton8,jRadioButton10};
	ButtonGroup bg=new ButtonGroup();
	int[] spec=new int[]{0x6b,0x18e,0x1f4,0xf8,0x135,0x169,0x11b};
	int[] result=new int[]{0,2,6,4,10,10,5,1,3};
	public String getExhibitName(){return "Olfactory coding";}
	public OlfactoryCoding() {
	}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	for(int i=0;i<stimulus.length;i++){
		bg.add(stimulus[i]);
		stimulus[i].addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
			int j=0;for(int i=0;i<stimulus.length;i++)if(stimulus[i].isSelected())j=i;
			int f=1<<j;for(int i=0;i<receptor.length;i++){
				if((f&spec[i])!=0)receptor[i].setBackground(Color.red);
				else receptor[i].setBackground(Color.gray);
				if(i==result[j])bulb[i].setForeground(Color.red);
				else bulb[i].setForeground(Color.gray);
			}
			network.repaint();
		}} );
	}
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(0,5,0,0);
		getMainContainer().setLayout(borderLayout1);
		jPanel1.setLayout(null);
		returnButton1.setBounds(new Rectangle(500, 282, 72, 35));
		jPanel2.setBorder(BorderFactory.createCompoundBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Stimulus"),BorderFactory.createEmptyBorder(0,10,0,0)));
		jPanel2.setBounds(new Rectangle(23, 14, 78, 290));
		jPanel2.setLayout(gridLayout1);
		gridLayout1.setRows(9);
		gridLayout1.setColumns(1);
		jRadioButton1.setText("A");
		jRadioButton1.setForeground(Color.blue);
		jRadioButton1.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton2.setText("B");
		jRadioButton2.setForeground(Color.blue);
		jRadioButton2.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton3.setText("C");
		jRadioButton3.setForeground(Color.blue);
		jRadioButton3.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton4.setText("D");
		jRadioButton4.setForeground(Color.blue);
		jRadioButton4.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton5.setText("E");
		jRadioButton5.setForeground(Color.blue);
		jRadioButton5.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton6.setText("F");
		jRadioButton6.setForeground(Color.blue);
		jRadioButton6.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton7.setText("G");
		jRadioButton7.setForeground(Color.blue);
		jRadioButton7.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton8.setText("H");
		jRadioButton8.setForeground(Color.blue);
		jRadioButton8.setFont(new java.awt.Font("SansSerif", 1, 14));
		jRadioButton10.setText("J");
		jRadioButton10.setForeground(Color.blue);
		jRadioButton10.setFont(new java.awt.Font("SansSerif", 1, 14));
		jPanel3.setBounds(new Rectangle(138, 33, 116, 238));
		jPanel3.setLayout(gridLayout2);
		gridLayout2.setRows(7);
		gridLayout2.setColumns(1);
		gridLayout2.setVgap(8);
		jLabel1.setBackground(Color.gray);
		jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel1.setBorder(border1);
		jLabel1.setOpaque(true);
		jLabel1.setText("ABDFG");
		jLabel2.setBackground(Color.gray);
		jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel2.setBorder(border1);
		jLabel2.setOpaque(true);
		jLabel2.setText("CBDHJ");
		jLabel3.setBackground(Color.gray);
		jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel3.setBorder(border1);
		jLabel3.setOpaque(true);
		jLabel3.setText("CEFGHJ");
		jLabel4.setBackground(Color.gray);
		jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel4.setBorder(border1);
		jLabel4.setOpaque(true);
		jLabel4.setText("DEFGH");
		jLabel5.setBackground(Color.gray);
		jLabel5.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel5.setBorder(border1);
		jLabel5.setOpaque(true);
		jLabel5.setText("ACEFJ");
		jLabel6.setBackground(Color.gray);
		jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel6.setBorder(border1);
		jLabel6.setOpaque(true);
		jLabel6.setText("ADFGJ");
		jLabel7.setBackground(Color.gray);
		jLabel7.setFont(new java.awt.Font("SansSerif", 1, 14));
		jLabel7.setBorder(border1);
		jLabel7.setOpaque(true);
		jLabel7.setText("ABDEJ");
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D1.setText("Receptors");
		label3D1.setBounds(new Rectangle(143, 277, 105, 29));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 14));
		label3D2.setText("Olfactory bulb");
		label3D2.setBounds(new Rectangle(367, 273, 124, 31));
		jPanel4.setBounds(new Rectangle(416, 27, 100, 248));
		jPanel4.setLayout(gridLayout3);
		gridLayout3.setRows(7);
		gridLayout3.setColumns(1);
		graphicComponent1.setType(2);
		graphicComponent1.setLayout(null);
		graphicComponent2.setType(4);
		graphicComponent2.setBounds(new Rectangle(0, 7, 25, 23));
		graphicComponent5.setBounds(new Rectangle(0, 7, 25, 23));
		graphicComponent5.setType(4);
		graphicComponent6.setLayout(null);
		graphicComponent6.setType(2);
		graphicComponent7.setBounds(new Rectangle(0, 6, 25, 23));
		graphicComponent7.setType(4);
		graphicComponent8.setLayout(null);
		graphicComponent8.setType(2);
		graphicComponent9.setType(2);
		graphicComponent9.setLayout(null);
		graphicComponent10.setBounds(new Rectangle(0, 6, 25, 23));
		graphicComponent10.setType(4);
		graphicComponent11.setType(2);
		graphicComponent11.setLayout(null);
		graphicComponent12.setBounds(new Rectangle(0, 6, 25, 23));
		graphicComponent12.setType(4);
		graphicComponent13.setType(2);
		graphicComponent13.setLayout(null);
		graphicComponent14.setBounds(new Rectangle(0, 6, 25, 23));
		graphicComponent14.setType(4);
		graphicComponent15.setType(2);
		graphicComponent15.setLayout(null);
		graphicComponent16.setBounds(new Rectangle(0, 6, 25, 23));
		graphicComponent16.setType(4);
		network.setBounds(new Rectangle(254, 41, 162, 220));
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(returnButton1, null);
		jPanel1.add(jPanel2, null);
		jPanel2.add(jRadioButton1, null);
		jPanel2.add(jRadioButton2, null);
		jPanel2.add(jRadioButton3, null);
		jPanel2.add(jRadioButton4, null);
		jPanel2.add(jRadioButton5, null);
		jPanel2.add(jRadioButton6, null);
		jPanel2.add(jRadioButton7, null);
		jPanel2.add(jRadioButton8, null);
		jPanel2.add(jRadioButton10, null);
		jPanel1.add(jPanel3, null);
		jPanel3.add(jLabel1, null);
		jPanel3.add(jLabel2, null);
		jPanel3.add(jLabel3, null);
		jPanel3.add(jLabel4, null);
		jPanel3.add(jLabel5, null);
		jPanel3.add(jLabel6, null);
		jPanel3.add(jLabel7, null);
		jPanel1.add(label3D1, null);
		jPanel1.add(jPanel4, null);
		jPanel4.add(graphicComponent1, null);
		graphicComponent1.add(graphicComponent2, null);
		jPanel4.add(graphicComponent15, null);
		graphicComponent15.add(graphicComponent16, null);
		jPanel4.add(graphicComponent13, null);
		graphicComponent13.add(graphicComponent14, null);
		jPanel4.add(graphicComponent11, null);
		graphicComponent11.add(graphicComponent12, null);
		jPanel4.add(graphicComponent9, null);
		graphicComponent9.add(graphicComponent10, null);
		jPanel4.add(graphicComponent8, null);
		graphicComponent8.add(graphicComponent7, null);
		jPanel4.add(graphicComponent6, null);
		graphicComponent6.add(graphicComponent5, null);
		jPanel1.add(network, null);
		jPanel1.add(label3D2, null);
	}
  public void close(){
  }
}
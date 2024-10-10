
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

import com.neurolab.common.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

public class HorizontalCells extends NeurolabExhibit{
  JPanel mainpanel = new JPanel0();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel0();
  Border border1;
  TitledBorder titledBorder1;
  FlowLayout flowLayout1 = new FlowLayout();
  JRadioButton hyperpol = new JRadioButton0();
  JRadioButton depol = new JRadioButton0();
  ButtonGroup bg=new ButtonGroup();
  Label3D label3D1 = new Label3D();
	public String getExhibitName(){return "Horizontal Cells";}
  public HorizontalCells() {
  }
  public void init(){
    super.init();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    hyperpol.setSelected(true);
    myinit();
  }
	int numcells=7;
	RetinalUnit[] unit=new RetinalUnit[numcells];
	double rec[]=new double[numcells];	//receptor activity
	double bp[]=new double[numcells];	//bipolar activity
	double hor;
	int j;
	int light[]=new int[numcells];	//not needed ?
	Timer timer=new Timer(80,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			hor=0;
			for(int i=0;i<numcells;i++){
				rec[i]=rec[i]*0.7+0.3*light[i];
				hor+=rec[i];
			}
			horizontalactivity.setValue(50+(int)hor);
			for(int i=0;i<numcells;i++){
				bp[i]=2*rec[i]-hor/(1+Math.abs(j-i));
				if(hyperpol.isSelected())bp[i]=-bp[i];
				unit[i].bipolaractivity.setValue(50+(int)bp[i]);
				unit[i].receptoractivity.setValue(50+(int)rec[i]);
			}
		}
	} );
	public void myinit(){
		for(int i=0;i<numcells;i++){
			unit[i]=new RetinalUnit();
			cellgrid.add(unit[i], null);
		}
		timer.start();
	}



  JPanel cellgrid = new JPanel0();
  GridLayout gridLayout1 = new GridLayout();
  JPanel retinalunit = new JPanel0();
  JPanel dummy7 = new JPanel0();
  JPanel dummy5 = new JPanel0();
  JPanel dummy4 = new JPanel0();
  JPanel dummy3 = new JPanel0();
  JPanel dummy2 = new JPanel0();
  JPanel dummy1 = new JPanel0();
  Border border2;
  JPanel horizontalcell = new JPanel0();
  GraphicComponent graphicComponent4 = new GraphicComponent();
 GraphicComponent graphicComponent5 = new GraphicComponent();
  GraphicComponent graphicComponent6 = new GraphicComponent();
  GraphicComponent graphicComponent7 = new GraphicComponent();
  GraphicComponent graphicComponent8 = new GraphicComponent();
  GraphicComponent graphicComponent9 = new GraphicComponent();
  GraphicComponent graphicComponent10 = new GraphicComponent();
  GraphicComponent graphicComponent11 = new GraphicComponent();
  JButton Exit = new ReturnButton();
  GraphicComponent graphicComponent12 = new GraphicComponent();
  PercentageBar horizontalactivity = new PercentageBar();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
    titledBorder1 = new TitledBorder(border1,"Bipolar type");
    border2 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    mainpanel.setLayout(null);
    jPanel2.setBorder(titledBorder1);
    jPanel2.setBounds(new Rectangle(440, 0, 134, 100));
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    hyperpol.setText("Hyperpolarising");
    depol.setText("Depolarising");
    getMainContainer().setLayout(borderLayout1);
    label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
    label3D1.setText("Click on a photoreceptor to activate it");
    label3D1.setBounds(new Rectangle(101, 294, 368, 22));
    cellgrid.setBounds(new Rectangle(20, 15, 385, 268));
    cellgrid.setLayout(gridLayout1);
    gridLayout1.setColumns(numcells);
    gridLayout1.setHgap(5);
    horizontalcell.setForeground(new Color(128, 0, 128));
    horizontalcell.setBounds(new Rectangle(65, 148, 465, 44));
	horizontalcell.setOpaque(false);
    horizontalcell.setLayout(null);
    graphicComponent4.setType(2);
    graphicComponent4.setBounds(new Rectangle(10, 13, 443, 16));
    graphicComponent5.setDirection(true);
    graphicComponent5.setBounds(new Rectangle(224, 1, 14, 21));
    graphicComponent6.setBounds(new Rectangle(60, 0, 14, 21));
    graphicComponent6.setDirection(true);
    graphicComponent7.setDirection(true);
    graphicComponent7.setBounds(new Rectangle(279, 0, 14, 21));
    graphicComponent8.setDirection(true);
    graphicComponent8.setBounds(new Rectangle(115, 0, 14, 21));
    graphicComponent9.setBounds(new Rectangle(0, 0, 14, 21));
    graphicComponent9.setDirection(true);
    graphicComponent10.setBounds(new Rectangle(168, 0, 14, 21));
    graphicComponent10.setDirection(true);
    graphicComponent11.setBounds(new Rectangle(334, 0, 14, 21));
    graphicComponent11.setDirection(true);
    Exit.setActionCommand("exit");
    Exit.setText("Return");
    Exit.setBounds(new Rectangle(503, 284, 72, 29));
    Exit.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        Exit_actionPerformed(e);
      }
    });
    graphicComponent12.setType(1);
    graphicComponent12.setBounds(new Rectangle(414, 21, 21, 20));
    horizontalactivity.setOrientation(PercentageBar.VERTICAL);
    horizontalactivity.setForeground(Color.magenta);
    horizontalactivity.setBounds(new Rectangle(473, 199, 24, 69));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setForeground(Color.cyan);
    jLabel1.setText("Receptors");
    jLabel1.setBounds(new Rectangle(21, 0, 90, 13));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel2.setForeground(SystemColor.activeCaption);
    jLabel2.setText("Bipolars");
    jLabel2.setBounds(new Rectangle(22, 290, 56, 15));
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel3.setForeground(new Color(128, 0, 128));
    jLabel3.setText("Horizontal cell");
    jLabel3.setBounds(new Rectangle(366, 6, 82, 12));
    getMainContainer().add(mainpanel, BorderLayout.CENTER);
    mainpanel.add(horizontalcell, null);
    horizontalcell.add(graphicComponent4, null);
    horizontalcell.add(graphicComponent6, null);
    horizontalcell.add(graphicComponent9, null);
    horizontalcell.add(graphicComponent11, null);
    horizontalcell.add(graphicComponent10, null);
    horizontalcell.add(graphicComponent8, null);
    horizontalcell.add(graphicComponent5, null);
    horizontalcell.add(graphicComponent7, null);
    horizontalcell.add(graphicComponent12, null);
    horizontalcell.add(jLabel3, null);
    mainpanel.add(jPanel2, null);
    jPanel2.add(hyperpol, null);
    jPanel2.add(depol, null);
    mainpanel.add(label3D1, null);
    mainpanel.add(cellgrid, null);
    mainpanel.add(Exit, null);
    mainpanel.add(horizontalactivity, null);
    mainpanel.add(jLabel1, null);
    mainpanel.add(jLabel2, null);
    bg.add(hyperpol);
    bg.add(depol);
  }
	class RetinalUnit extends JPanel0 implements MouseListener{
		PercentageBar receptoractivity = new PercentageBar();
		JButton receptorcell = new JButton();
		PercentageBar bipolaractivity = new PercentageBar();
		GraphicComponent bipolarbody = new GraphicComponent();
		GraphicComponent bipolarcell = new GraphicComponent();
		GraphicComponent bipolarterminal = new GraphicComponent();
		public RetinalUnit(){
		    setLayout(null);
		    add(receptoractivity, null);
		    add(receptorcell, null);
		    add(bipolaractivity, null);
		    add(bipolarcell, null);
		    bipolarcell.add(bipolarterminal, null);
		    bipolarcell.add(bipolarbody, null);
		    retinalunit.setBorder(BorderFactory.createLineBorder(Color.black));
		    receptoractivity.setOrientation(PercentageBar.VERTICAL);
		    receptoractivity.setForeground(Color.cyan);
		    receptoractivity.setBounds(new Rectangle(5, 69, 10, 56));
		    receptorcell.setBackground(Color.cyan);
		    receptorcell.setBounds(new Rectangle(17, 4, 31, 122));
		    bipolaractivity.setBounds(new Rectangle(4, 207, 10, 56));
		    bipolaractivity.setOrientation(PercentageBar.VERTICAL);
		    bipolarbody.setBackground(SystemColor.control);
		    bipolarbody.setForeground(SystemColor.activeCaption);
		    bipolarbody.setType(4);
		    bipolarbody.setBounds(new Rectangle(0, 67, 21, 25));
		    bipolarcell.setForeground(SystemColor.activeCaption);
		    bipolarcell.setType(3);
		    bipolarcell.setBounds(new Rectangle(25, 132, 21, 130));
		    bipolarterminal.setType(1);
		    bipolarterminal.setBounds(new Rectangle(2, 0, 17, 18));

			receptorcell.addMouseListener(this);

		}
		public void mousePressed(MouseEvent e){
			receptorcell.setBackground(Color.white);
			for(int i=0;i<numcells;i++){
				if(this==unit[i])j=i;
			}	//work out which unit was pressed and set j
			light[j]=-30;
		}
		public void mouseReleased(MouseEvent e){
			receptorcell.setBackground(Color.cyan);
			for(int i=0;i<numcells;i++){
				if(this==unit[i])j=i;
			}	//work out which unit was pressed and set j
			light[j]=0;
		}
		public void mouseClicked(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}


  void Exit_actionPerformed(ActionEvent e) {
	timer.stop();
	toExhibitChooser();
  }
}
// Cerebellar Dysmetria by Sanjay Manohar
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

// v1.1		ok

public class CerebellarDysmetria extends NeurolabExhibit implements ActionListener{
	JButton clearbutton;
	JPanel rightpanel, leftpanel, buttonpanel,canvas;
	TextArea textarea;
	Point lastpoint;

	private static String instructions="Click on the centre spot, then - keeping the mouse button down - move as quickly as possible to each of the other spots in turn. You will exhibit various aspects of dysmetria, particularly overshoot, intention tremor and decomposition of movement. The demonstration is even more dramatic if you turn the mouse round so that the buttons are towards you.";

	public String getExhibitName(){return "Cerebellar Dysmetria";}
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		getMainContainer().setBackground(systemGray);
		//initialise variables
		createComponents();
	}
	public void createComponents(){
		getMainContainer().add(rightpanel=new JPanel(),BorderLayout.EAST);
		getMainContainer().add(leftpanel=new JPanel(),BorderLayout.WEST);

		leftpanel.setBorder(raisedbevel);
		leftpanel.setBackground(systemGray);
								leftpanel.add(canvas=new JPanel(){
			public void paint(Graphics g){
				super.paint(g);
				antiAlias(g);
				g.setColor(new Color(0,0,64));
				g.fillRect(1,1,getWidth()-2,getHeight()-2);
				int cx=getWidth()/2,cy=getHeight()/2;
				g.setColor(Color.red);
				g.fillOval(cx-10,cy-10,20,20);
				double theta;
				for(int i=0;i<3;i++){
					theta=((double)i*2)*Math.PI/3+Math.PI/6;
					g.fillOval(cx+(int)(cy*0.8*Math.sin(theta)),
							 cy+(int)(cy*0.8*Math.cos(theta)),8,8);
				}	// three dots
			}
			public Dimension getPreferredSize(){
				return new Dimension(400,305);
			}
		});
		canvas.setBackground(systemGray);
		canvas.setBorder(loweredbevel);
								canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
								canvas.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				lastpoint=e.getPoint();
			}
		} );
		canvas.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				Graphics g=canvas.getGraphics();
				antiAlias(g);
				g.setColor(Color.white);
				g.drawLine(lastpoint.x, lastpoint.y,
							(lastpoint = e.getPoint()).x, lastpoint.y);
			}
		} );

		rightpanel.setLayout(new BorderLayout());
		rightpanel.add(buttonpanel=new JPanel(),BorderLayout.SOUTH);
		rightpanel.add(textarea=new TextArea(instructions,17,20,TextArea.SCROLLBARS_NONE),BorderLayout.NORTH);
		rightpanel.setBackground(systemGray);
		textarea.setEditable(false);
		textarea.setBackground(systemGray);

		buttonpanel.setLayout(new BorderLayout());
		buttonpanel.setBackground(systemGray);
		buttonpanel.add(clearbutton=new JButton("Clear"),BorderLayout.WEST);
		clearbutton.addActionListener(this);
		clearbutton.setBackground(systemGray);
		buttonpanel.add(new ReturnButton(),BorderLayout.EAST);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()=="Return")toExhibitChooser();
		if(e.getActionCommand()=="Clear")canvas.repaint();
	} 
	public void close() {}
}


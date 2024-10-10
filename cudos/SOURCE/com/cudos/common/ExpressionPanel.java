package com.cudos.common;

import javax.swing.*;
import java.awt.*;
import evaluator.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * Allows input of a Polar, Parametric or y(x) expression
 */

public class ExpressionPanel extends JPanel {
	private JPanel jPanel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JRadioButton ybutton = new JRadioButton();
	private JRadioButton parametricButton = new JRadioButton();
	private JRadioButton polarButton = new JRadioButton();
	private JPanel panel = new JPanel();
	private ButtonGroup bg = new ButtonGroup();
	private Box box = Box.createHorizontalBox();
	private JTextField formula1 = new JTextField();
	private JTextField formula2 = new JTextField();
	private BorderLayout borderLayout2 = new BorderLayout();
	private Component component1;
	private JPanel range = new JPanel();
	private JTextField tmax = new JTextField();
	private JLabel jLabel1 = new JLabel();
	private JTextField tmin = new JTextField();
	private GridLayout gridLayout1 = new GridLayout();
	private Border border1;
	public ExpressionPanel() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		bg.add(ybutton); bg.add(parametricButton); bg.add(polarButton);
	}
	private void jbInit() throws Exception {
		component1 = Box.createHorizontalStrut(8);
		border1 = BorderFactory.createEmptyBorder(4,4,4,4);
		this.setLayout(borderLayout1);
		ybutton.setFont(new java.awt.Font("Serif", 3, 14));
		ybutton.setSelected(true);
		ybutton.setText("y(x)");
		ybutton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio(e);
			}
		});
		parametricButton.setFont(new java.awt.Font("Serif", 3, 14));
		parametricButton.setText("x(t), y(t)");
		parametricButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio(e);
			}
		});
		polarButton.setFont(new java.awt.Font("Serif", 3, 14));
		polarButton.setText("r(t)");
		polarButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio(e);
			}
		});
		formula1.setFont(new java.awt.Font("SansSerif", 1, 16));
		formula1.setColumns(18);
		formula1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formula1_actionPerformed(e);
			}
		});
		formula1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formula1_actionPerformed(e);
			}
		});
		formula1.setColumns(18);
		formula1.setFont(new java.awt.Font("SansSerif", 1, 16));
		formula2.setFont(new java.awt.Font("SansSerif", 1, 16));
		formula2.setColumns(18);
		formula2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formula2_actionPerformed(e);
			}
		});
		formula2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formula2_actionPerformed(e);
			}
		});
		formula2.setColumns(18);
		formula2.setFont(new java.awt.Font("SansSerif", 1, 16));
		formula2.setVisible(false);
		panel.setLayout(borderLayout2);
		tmax.setToolTipText("");
		tmax.setText("1");
		tmax.setColumns(4);
		tmax.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update(e);
			}
		});
		jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel1.setText("< t <");
		tmin.setText("-1");
		tmin.setColumns(4);
		tmin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update(e);
			}
		});
		range.setLayout(gridLayout1);
		range.setBorder(border1);
		this.add(jPanel1, BorderLayout.NORTH);
		jPanel1.add(ybutton, null);
		jPanel1.add(parametricButton, null);
		jPanel1.add(polarButton, null);
		this.add(panel, BorderLayout.CENTER);
		panel.add(box,  BorderLayout.SOUTH);
		box.add(formula1, null);
		box.add(component1, null);
		box.add(formula2, null);
		box.add(range, null);
		range.add(tmin, null);
		range.add(jLabel1, null);
		range.add(tmax, null);}

	public Expression getExpression(){
		if(polarButton.isSelected()){
			PolarExpression e = new PolarExpression(formula1.getText());
			e.tMinimum = tmin.getText();
			e.tMaximum = tmax.getText();
			return e;
		}else if(parametricButton.isSelected()){
			ParametricExpression e = new ParametricExpression(formula1.getText(), formula2.getText());
			e.tMinimum = tmin.getText();
			e.tMaximum = tmax.getText();
			return e;
		}else{
			return new Expression(formula1.getText());
		}
	}

       /** Construct an expression from a string */
       public void setExpression(String s){
         String params=null;
         String min="-PI",max="PI";
         if(s.startsWith("r=")){
           int q=s.indexOf(';');
           if(q>=0){
             params=s.substring(q+1);
             s=s.substring(0,q);
             int q2=params.indexOf(';');
             min=params.substring(0,q2);
             max=params.substring(q2+1);
           }
           polarButton.setSelected(true);
           formula1.setText(s.substring(2));
           tmin.setText(min);
           tmax.setText(max);
         }else if(s.startsWith("y=")){
             ybutton.setSelected(true);
             formula1.setText(s.substring(2));
         }else if(s.startsWith("x=")){
           int q=s.indexOf(';');
           if(q>=0){
             String ys=s.substring(q+1); s=s.substring(0,q);
             q=ys.indexOf(';');
             if(q>=0){
               params=ys.substring(q+1); ys=ys.substring(0,q);
               int q2=params.indexOf(';');
               min=params.substring(0,q2);
               max=params.substring(q2+1);
             }
             parametricButton.setSelected(true);
             formula1.setText(s.substring(2));
             formula2.setText(ys.substring(2));
             tmax.setText(max);
             tmin.setText(min);
           }
         }
         updateFormula();
       }

	public void setExpression(Expression e){
		if(e instanceof PolarExpression){
			polarButton.setSelected(true);
			formula1.setText(e.getDefinition());
			formula2.setVisible(false);
			range.setVisible(true);
			tmin.setText(((PolarExpression)e).tMinimum);
			tmax.setText(((PolarExpression)e).tMaximum);
		}else if(e instanceof ParametricExpression){
			parametricButton.setSelected(true);
			ParametricExpression pe= (ParametricExpression)e;
			formula1.setText(pe.getXDefinition());
			formula2.setText(pe.getYDefinition());
			formula2.setVisible(true);
			range.setVisible(true);
			tmin.setText(((ParametricExpression)e).tMinimum);
			tmax.setText(((ParametricExpression)e).tMaximum);
		}else{
			ybutton.setSelected(true);
			formula1.setText(e.getDefinition());
			formula2.setVisible(false);
		}
		invalidate();
	}

	void formula2_actionPerformed(ActionEvent e) {
		updateFormula();
	}

	void formula1_actionPerformed(ActionEvent e) {
		updateFormula();
	}

	void radio(ActionEvent e) {
		if(parametricButton.isSelected())		formula2.setVisible(true);
		else formula2.setVisible(false);
		if(ybutton.isSelected()) range.setVisible(false);
		else range.setVisible(true);
		validateTree();
		if(parametricButton.isSelected() && formula2.getText().equals("")) return;
		updateFormula();
	}


	ActionListener al = null;

	void updateFormula(){
		if(al!=null)	al.actionPerformed(new ActionEvent(this, 0,"Update"));
	}
	public void addActionListener(ActionListener al2){ al=AWTEventMulticaster.add(al,al2);}
	public void removeActionListener(ActionListener al2){ al=AWTEventMulticaster.remove(al2,al);}

	void update(ActionEvent e) {
		updateFormula();
	}

}

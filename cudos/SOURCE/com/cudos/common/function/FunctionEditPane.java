package com.cudos.common.function;

import javax.swing.*;
import java.awt.*;

public class FunctionEditPane extends JPanel{
	Function function;

	JLabel[] pLabel;
	JComponent[] pControl;


		//public getters and setters
	public void setFunction(Function f){
		function=f;
		resetLayout();
	}
	public Function getFunction(){return function;}


	FunctionEditPane(Function f){
		function=f;
		resetLayout();
	}
	FunctionEditPane(){
		resetLayout();
	}
	void resetLayout(){
		Double[] p=function.getParameter();
		String[] n=function.getParameterName();
		removeAll();
		setLayout(new GridLayout(2,p.length));
		for(int i=0;i<p.length;i++){
			add(pLabel[i]  =new JLabel(n[i]));
			add(pControl[i]=new JSlider());
			((JSlider)pControl[i]).setValue((int)p[i].doubleValue());
		}	//add controls for parameters
	}
}

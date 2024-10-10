
package com.neurolab.common;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class RadioPanel extends JPanel implements NeurolabGuiComponent {
	private JRadioButton button[];
	private int n;
	private String[] names;
	private ActionListener listener;
	private ButtonGroup bgroup;
	public RadioPanel(String[] nm,ActionListener cl){
		listener=cl;names=nm;
		n=names.length;
		bgroup=new ButtonGroup();
		button=new JRadioButton[n];
								NeurolabExhibit.setBG(this);
								setLayout(new GridLayout(n,1));
		for(int i=0;i<n;i++){
			add(button[i]=new JRadioButton(names[i]));	// add new button
			NeurolabExhibit.setBG(button[i]);	// set background
			button[i].addActionListener(listener);		// add listener
			bgroup.add(button[i]);				// add to group
		}
	}
	public int getSelected(){
		int sel=-1;
		for(int i=0;i<n;i++){
			if(button[i].isSelected())sel=i;
		}
		return sel;
	}
	public void setSelected(int r){
		if(r>=0)button[r].setSelected(true);
		else button[getSelected()].setSelected(false);
	}
	public RadioPanel() {
	}
}

/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab.common;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public abstract class FullScreenExhibit extends NeurolabExhibit {

  public FullScreenExhibit() {
  }
	public JFrame frame=new JFrame(getExhibitName());
	JButton button=new JButton("Return"), but1=new JButton("Return");
	public void init(){
		super.init();
		getMainContainer().add(button);	//the only component!
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0,0,screenSize.width,screenSize.height);
		frame.show();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(but1,BorderLayout.NORTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				doClose();
			}
		});
		ActionListener al=new bl();
		button.addActionListener(al);
		but1.addActionListener(al);
		frame.validate();
	}
	class bl implements ActionListener{
		public void actionPerformed(ActionEvent e){
			doClose();
		}
	}
	public String parentExhibit;
	public void doClose(){
		frame.hide();
		frame.dispose();
		getHolder().setExhibit(parentExhibit);
			//quits the program
	}

}
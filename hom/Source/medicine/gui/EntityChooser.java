
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;
import medicine.*;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class EntityChooser extends JDialog {
	BorderLayout borderLayout1 = new BorderLayout();
	NavigatorPanel navigator = new NavigatorPanel();
	JPanel jPanel1 = new JPanel();
	JButton jButton1 = new JButton();
	JButton jButton2 = new JButton();

	public EntityChooser() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	setSize(475,345);
	setTitle("Select an entity");
	}

	private void jbInit() throws Exception {
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jButton2.setText("Cancel");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		this.getContentPane().add(navigator, BorderLayout.CENTER);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(jButton1, null);
		jPanel1.add(jButton2, null);
	}


	public Entity entity;
	public boolean OK=false;
	public boolean NEW=false;

	void jButton1_actionPerformed(ActionEvent e) {
		OK=true;
		entity=navigator.entity;
		hide();
	}

	void jButton2_actionPerformed(ActionEvent e) {
		OK=false;
		hide();
	}

}
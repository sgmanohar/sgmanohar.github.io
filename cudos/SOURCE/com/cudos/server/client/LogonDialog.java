package com.cudos.server.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.rmi.*;
/**
 * The client-side logon dialog.
 */
public class LogonDialog extends JDialog{
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private GridLayout gridLayout1 = new GridLayout();
	private JPanel jPanel2 = new JPanel();
	private JTextField usertxt = new JTextField();
	private JPasswordField pwtxt = new JPasswordField(26);
	private JLabel jLabel2 = new JLabel();
	private JLabel jLabel1 = new JLabel();
	private JPanel jPanel4 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JButton jButton1 = new JButton();
	private JButton jButton2 = new JButton();
	private JButton jButton3 = new JButton();
	private JButton jButton4 = new JButton();

	public LogonDialog() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		pack();
	}
	private void jbInit() throws Exception {
		this.setModal(true);
		this.setTitle("Log on");
		gridLayout1.setColumns(2);
		gridLayout1.setHgap(5);
		gridLayout1.setRows(2);
		gridLayout1.setVgap(5);
		jPanel2.setLayout(gridLayout1);
		jLabel2.setText("Password");
		jLabel1.setText("User name");
		jPanel1.setLayout(borderLayout1);
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jButton2.setText("Forgot ID");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jButton3.setText("Cancel");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton3_actionPerformed(e);
			}
		});
		jButton4.setText("New student");
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton4_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
		jPanel1.add(jPanel4,  BorderLayout.SOUTH);
		jPanel4.add(jButton3, null);
		jPanel4.add(jButton2, null);
		jPanel4.add(jButton4, null);
		jPanel4.add(jButton1, null);
		jPanel1.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel2, null);
		jPanel2.add(jLabel1, null);
		jPanel2.add(usertxt, null);
		jPanel2.add(jLabel2, null);
		jPanel2.add(pwtxt, null);
	}

	void jButton3_actionPerformed(ActionEvent e) {
		hide();
	}

	private ClientHandle handle = null;
	public ClientHandle getHandle() { return handle; }

	/** Preserve the server handle */
	Server server;

	void jButton1_actionPerformed(ActionEvent e) {
		try{
			Server s = (Server)Naming.lookup(Server.location);
			server = s;
			ClientHandle h = s.login(usertxt.getText(), String.valueOf(pwtxt.getPassword()));
			if(h != null){
				hide();
				JOptionPane.showMessageDialog(null, "Login successful", "Login", JOptionPane.INFORMATION_MESSAGE);
				handle = h;
			}else{
				JOptionPane.showMessageDialog(null, "Login failed", "Login", JOptionPane.INFORMATION_MESSAGE);
			}
		}catch(Exception x){ x.printStackTrace(); error("Server error: "+x.toString()); }
	}

	void jButton4_actionPerformed(ActionEvent e) {
		StudentForm f = new StudentForm();
		f.show();
		handle = f.getHandle();
		hide();
	}

	void jButton2_actionPerformed(ActionEvent e) {
		StudentForm f = new StudentForm();
		f.show();
		handle = f.getHandle();
		hide();
	}
	void error(String e){
		JOptionPane.showMessageDialog(this,e,"Error",JOptionPane.ERROR_MESSAGE);
	}

	public Server getServer(){ return server; }
}

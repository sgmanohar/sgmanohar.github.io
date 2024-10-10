package com.cudos.server.client;

import java.rmi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
/**
 * Form for new students , or those who have lost their id.
 */

public class StudentForm extends JDialog {
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JButton jButton1 = new JButton();
	private JPanel jPanel4 = new JPanel();
	private GridLayout gridLayout1 = new GridLayout();
	private JLabel jLabel1 = new JLabel();
	private JTextField uid = new JTextField();
	private JLabel jLabel2 = new JLabel();
	private JTextField sch = new JTextField();
	private JLabel jLabel3 = new JLabel();
	private JTextField foren = new JTextField();
	private JLabel jLabel4 = new JLabel();
	private JPanel jPanel5 = new JPanel();
	private GridLayout gridLayout2 = new GridLayout();
	private JTextField dobd = new JTextField(2);
	private JTextField doby = new JTextField(4);
	private JTextField dobm = new JTextField(2);
	private JLabel jLabel5 = new JLabel();
	private JTextField surn = new JTextField();
	private JPanel jPanel6 = new JPanel();
	private JLabel jLabel6 = new JLabel();
	private JLabel jLabel7 = new JLabel();
	private JPasswordField pw1 = new JPasswordField();
	private JLabel jLabel8 = new JLabel();
	private JPasswordField pw2 = new JPasswordField();

	public StudentForm() {
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
		this.setTitle("Student Info");
		jPanel1.setLayout(borderLayout1);
		jButton1.setText("Create account");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel4.setLayout(gridLayout1);
		jLabel1.setText("First name");
		gridLayout1.setColumns(2);
		gridLayout1.setRows(7);
		jLabel2.setText("Surname");
		jLabel3.setText("School");
		jLabel4.setText("Date of birth (d/m/y)");
		jPanel5.setLayout(gridLayout2);
		jLabel5.setText("User ID");
		jLabel6.setText("Please fill out the first four lines.");
		uid.setOpaque(false);
		uid.setEditable(false);
		jLabel7.setText("Password");
		jLabel8.setText("Retype password");
		pw1.setEnabled(false);
		pw1.setOpaque(false);
		pw2.setEnabled(false);
		pw2.setOpaque(false);
		jButton2.setText("Cancel");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2,  BorderLayout.SOUTH);
		jPanel2.add(jButton2, null);
		jPanel2.add(jButton1, null);
		jPanel1.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel4, null);
		jPanel4.add(jLabel2, null);
		jPanel4.add(surn, null);
		jPanel4.add(jLabel1, null);
		jPanel4.add(foren, null);
		jPanel4.add(jLabel4, null);
		jPanel4.add(jPanel5, null);
		jPanel5.add(dobd, null);
		jPanel5.add(dobm, null);
		jPanel5.add(doby, null);
		jPanel4.add(jLabel3, null);
		jPanel4.add(sch, null);
		jPanel4.add(jLabel5, null);
		jPanel4.add(uid, null);
		jPanel4.add(jLabel7, null);
		jPanel4.add(pw1, null);
		jPanel4.add(jLabel8, null);
		jPanel4.add(pw2, null);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
		jPanel6.add(jLabel6, null);
	}

	private ClientHandle handle;
	private JButton jButton2 = new JButton();
	public ClientHandle getHandle(){return handle;}
	void jButton1_actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Create account")){
			//create the new account
			try{
				Server s = (Server)Naming.lookup(Server.location);
				Calendar d = Calendar.getInstance();
				int year = Integer.parseInt(doby.getText());
				if(year<100) year+=1900;
				d.set(year, Integer.parseInt(dobm.getText()),Integer.parseInt(dobd.getText()));
				handle = s.createUser(surn.getText(), foren.getText(), sch.getText(), d);
				if(handle==null){ ClientHandle.error("Server error creating account."); return;}

				//set up the dialog to ask for new password
				uid.setText( s.getUserID(handle) );
				jButton1.setText("Set password");
				pw1.setEnabled(true);
				pw1.setBackground(surn.getBackground());
				pw2.setEnabled(true);
				pw2.setBackground(surn.getBackground());
			}catch(Exception x){x.printStackTrace(); ClientHandle.error("Server error: "+x.toString());}
		}else if(e.getActionCommand().equals("Set password")){
			//accept new password
			try{
				Server s = (Server)Naming.lookup(Server.location);
				String p = String.valueOf(pw1.getPassword());
				if(!p.equals("") && p.equals( String.valueOf(pw2.getPassword()) )){
					s.changePassword(handle, "", p);
				hide();
				}else{ ClientHandle.error("Passwords do not match. Please type your password exactly in both boxes");	}
			}catch(Exception x){x.printStackTrace(); ClientHandle.error("Server error:"+x.toString());}
		}
	}


	void jButton2_actionPerformed(ActionEvent e) {
		hide();
	}
}
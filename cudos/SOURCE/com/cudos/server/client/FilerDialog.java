package com.cudos.server.client;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.rmi.*;

/**
 * Remote filer
 */

public class FilerDialog extends JDialog{
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JButton jButton1 = new JButton();
	private JButton delete = new JButton();
	private Box box1;
	private JList fileList = new JList();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTextField filename = new JTextField();

	protected FilerDialog() {
		init();
	}

	ClientHandle handle = null;
	private DefaultListModel filelistmodel = new DefaultListModel();

	public FilerDialog(ClientHandle handle){
		this.handle = handle;
		init();
		readFiles();
	}
	void init(){
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		setSize(300,300);
	}

	void readFiles(){
		try{
			Server s = (Server) Naming.lookup(Server.location);
			String[] f = s.getFilenames(handle);
			filelistmodel.removeAllElements();
			for(int i=0;i<f.length;i++) filelistmodel.addElement(f[i]);
		}catch(Exception e){e.printStackTrace(); ClientHandle.error(e.toString());}
	}


	private void jbInit() throws Exception {
		box1 = Box.createVerticalBox();
		this.setTitle("User files");
		jPanel1.setLayout(borderLayout1);
		jButton1.setText("OK");
		delete.setText("Delete");
		delete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete_actionPerformed(e);
			}
		});
		fileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				fileList_valueChanged(e);
			}
		});
		fileList.setModel(filelistmodel);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel3,  BorderLayout.SOUTH);
		jPanel3.add(delete, null);
		jPanel3.add(jButton1, null);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(box1, null);
		box1.add(filename, null);
		box1.add(jScrollPane1, null);
		jScrollPane1.add(fileList, null);
	}
	public String getFile(){return filename.getText();}

	void fileList_valueChanged(ListSelectionEvent e) {
		filename.setText(fileList.getSelectedValue().toString());
	}

	void delete_actionPerformed(ActionEvent e) {
		String f = filename.getText();
		try{
			Server s = (Server)Naming.lookup(Server.location);
			s.deleteFile(handle,f);
		}catch(Exception x){x.printStackTrace(); ClientHandle.error(x.toString());}
	}
}
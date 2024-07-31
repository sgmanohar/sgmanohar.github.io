
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package medicine.gui;
import medicine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;


public class CauseListDialog extends JDialog {
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JButton jButton1 = new JButton();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JTree tree = new JTree();

	public CauseListDialog() {
		initComponents();
	}

	Entity entity;
	public void setEntity(Entity entity){
		this.entity=entity;
		initTree();
	}

	void initComponents(){
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		setSize(300,300);
                MainApplication.centreWindow(this);
	}

	private void jbInit() throws Exception {
		this.setTitle("List of causes");
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ok_action(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(tree, null);
		this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jButton1, null);
	}

	void ok_action(ActionEvent e) {
	hide();
	}





	void initTree(){
		TreeNode root=new JTree.DynamicUtilTreeNode(
			"Causes of "+entity.name,
			Entities.getAllCauses(entity,null));

		DefaultTreeModel model=new DefaultTreeModel(root);
		tree.setModel(model);
		for(int i=0;i<tree.getRowCount();i++)tree.expandRow(i);
	}
}

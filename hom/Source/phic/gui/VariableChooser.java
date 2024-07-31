/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package phic.gui;
import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.event.*;

public class VariableChooser extends JPanel{
	/**
	 * This is a blocking call that creates a variable chooser box.
	 */
	public static VisibleVariable selectVariable(VisibleVariable selected){
		VariableChooserDialog vc=new VariableChooserDialog();
		vc.variableChooser1.variableListBox.setSelectedItem(selected); //set up dialog
		vc.show(); //display
		if(vc.okpressed){
			return vc.variable;
		} else{
			return selected;
		}
	}

	VisibleVariable variable;

	void variableChanged(ItemEvent e){
		variable=(VisibleVariable)variableListBox.getSelectedItem();
		if(variable!=null){
			unittxt.setText(VisibleVariable.ustring[variable.units]);
			nodetxt.setText(variable.canonicalName);
			shortnametxt.setText(variable.shortName);
		} else{
			nodetxt.setText("");
			shortnametxt.setText("");
		}
	}

	public VisibleVariable getVariable(){
		return variable;
	}

	public VariableChooser(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	JLabel jLabel1=new JLabel();

	JPanel jPanel1=new JPanel();

	VariableListBox variableListBox=new VariableListBox();

	JPanel jPanel2=new JPanel();

	JLabel jLabel4=new JLabel();

	JLabel unittxt=new JLabel();

	JLabel jLabel6=new JLabel();

	JLabel nodetxt=new JLabel();

	JLabel jLabel8=new JLabel();

	JLabel shortnametxt=new JLabel();

	GridLayout gridLayout1=new GridLayout();

	JPanel jPanel3=new JPanel();

	JPanel jPanel4=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private BodyTree bodyTree1=new BodyTree();

	private void jbInit() throws Exception{
		setLayout(new BorderLayout());
		jLabel1.setText("Variable:");
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jLabel4.setText("Unit:");
		jLabel6.setText("Node:");
		jLabel8.setPreferredSize(new Dimension(80,17));
		jLabel8.setText("Short name:");
		jPanel2.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		gridLayout1.setRows(4);
		variableListBox.addItemListener(new java.awt.event.ItemListener(){
			public void itemStateChanged(ItemEvent e){
				variableChanged(e);
			}
		});
		jPanel4.setLayout(borderLayout1);
		bodyTree1.tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e){
				treeselect();
			}
		});
		add(jPanel3,BorderLayout.SOUTH);
		jPanel3.add(jPanel2,null);
		jPanel2.add(jLabel4,null);
		jPanel2.add(unittxt,null);
		jPanel2.add(jLabel6,null);
		jPanel2.add(nodetxt,null);
		jPanel2.add(jLabel8,null);
		jPanel2.add(shortnametxt,null);
		add(jPanel1,BorderLayout.NORTH);
		jPanel1.add(jLabel1,null);
		jPanel1.add(variableListBox,null);
		add(jPanel4,BorderLayout.CENTER);
		jPanel4.add(bodyTree1,BorderLayout.CENTER);
	}

	void treeselect(){
		Object o=bodyTree1.tree.getSelectionPath().getLastPathComponent();
		if(o instanceof Node){
			Node n=(Node)o;
			if(n!=null){
				variable=Variables.forNode(n);
				variableListBox.setSelectedItem(variable);
			}
		}
	}
}
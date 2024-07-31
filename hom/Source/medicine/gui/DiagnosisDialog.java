package medicine.gui;
import medicine.*;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;

/**
 * Ask for symptoms and run the diagnoser
 */

public class DiagnosisDialog extends JDialog implements ActionListener{
	private JPanel jPanel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private BorderLayout borderLayout2 = new BorderLayout();
	private JPanel jPanel4 = new JPanel();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JList obslist = new JList();
	private BorderLayout borderLayout3 = new BorderLayout();
	private Border border1;
	private JPanel jPanel5 = new JPanel();
	private JButton addButton = new JButton();
	private JButton removeButton = new JButton();
	private JLabel jLabel1 = new JLabel();
	private DefaultListModel obslistmodel = new DefaultListModel();
	private BorderLayout borderLayout4 = new BorderLayout();
	private JPanel jPanel6 = new JPanel();
	private BorderLayout borderLayout5 = new BorderLayout();
	private JPanel jPanel7 = new JPanel();
	private JButton jButton1 = new JButton();
	private Border border2;
	private JScrollPane jScrollPane2 = new JScrollPane();
	private JList diaglist = new JList();
	private JLabel jLabel2 = new JLabel();
	private Border border3;
	private JPanel jPanel8 = new JPanel();
	private JButton diagButton = new JButton();

	Entity entity;
	NavigatorPanel navigator;
	private DefaultListModel diaglistmodel = new DefaultListModel();
	public DiagnosisDialog(NavigatorPanel nav) {
		entity=nav.entity;
		navigator=nav;
		init();
	}
	void init(){
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		setSize(400,300);
                MainApplication.centreWindow(this);
	}
	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(5,5,5,5);
		border2 = BorderFactory.createEmptyBorder(5,5,5,5);
		border3 = BorderFactory.createEmptyBorder(5,5,5,5);
		jPanel1.setLayout(borderLayout1);
		jPanel2.setLayout(borderLayout2);
		jPanel4.setLayout(borderLayout3);
		jPanel4.setBorder(border1);
		addButton.setText("Add");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addButton_actionPerformed(e);
			}
		});
		removeButton.setText("Remove");
		removeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeButton_actionPerformed(e);
			}
		});
		jLabel1.setText("Signs and Symptoms");
		jPanel3.setLayout(borderLayout4);
		jPanel6.setLayout(borderLayout5);
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		this.setTitle("Diagnosis");
		jLabel2.setText("Differential");
		jPanel3.setBorder(border3);
		diagButton.setText("Diagnose");
		diagButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagButton_actionPerformed(e);
			}
		});
		obslist.setModel(obslistmodel);
		diaglist.setModel(diaglistmodel);
		gotoButton.setText("Go to");
		gotoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gotoButton_actionPerformed(e);
			}
		});
		jSlider1.setMinimum(1);
		jSlider1.setBorder(BorderFactory.createEtchedBorder());
		jSlider1.setPreferredSize(new Dimension(100, 22));
		jLabel3.setText("Nearness");
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jSplitPane1, BorderLayout.CENTER);
		jSplitPane1.add(jPanel2, JSplitPane.LEFT);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jScrollPane1, BorderLayout.CENTER);
		jPanel4.add(jPanel5,  BorderLayout.SOUTH);
		jPanel5.add(addButton, null);
		jPanel5.add(removeButton, null);
		jScrollPane1.getViewport().add(obslist, null);
		jSplitPane1.add(jPanel3, JSplitPane.RIGHT);
		jPanel3.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jScrollPane2,  BorderLayout.CENTER);
		jPanel3.add(jLabel2, BorderLayout.NORTH);
		jPanel3.add(jPanel8,  BorderLayout.SOUTH);
		jPanel8.add(diagButton, null);
		jPanel8.add(gotoButton, null);
		jScrollPane2.getViewport().add(diaglist, null);
		jPanel1.add(jPanel7,  BorderLayout.SOUTH);
		jPanel7.add(jButton1, null);
		jPanel7.add(jLabel3, null);
		jPanel7.add(jSlider1, null);
		jPanel4.add(jLabel1, BorderLayout.NORTH);
		jSplitPane1.setDividerLocation(200);
	}

	void addButton_actionPerformed(ActionEvent e) {
		EntityChooser ec=new EntityChooser();
		ec.setModal(true);
		ec.navigator.setEntity(entity);
		ec.show();
		if(ec.OK){
			Entity ne = ec.entity;
			obslistmodel.addElement(ne);
		}
	}

	void removeButton_actionPerformed(ActionEvent e) {
		Object re[] = obslist.getSelectedValues();
		for(int i=0;i<re.length;i++) obslistmodel.removeElement(re[i]);
	}

	Diagnoser diagnoser;
	private JButton gotoButton = new JButton();
	JSlider jSlider1 = new JSlider();
	JLabel jLabel3 = new JLabel();
	void diagButton_actionPerformed(ActionEvent e) {
		doDiagnosis();
	}

	public void actionPerformed(ActionEvent e){
		Object o = e.getSource();
		if( o instanceof Entity ){
		}else{
			diaglist.setCursor(Cursor.getDefaultCursor());
		}
		fillList(diaglistmodel, diagnoser.getDifferential());
	}
	void fillList(DefaultListModel lm, Vector v){
		lm.removeAllElements();
		for(int i=0;i<v.size();i++) lm.addElement(v.get(i));
	}

	void gotoButton_actionPerformed(ActionEvent e) {
		Object o=diaglist.getSelectedValue();
		if(o instanceof Entity){
			navigator.setEntity( (Entity) o );
		}
	}

	void jButton1_actionPerformed(ActionEvent e) {
		hide();
	}


	public JList getObsList(){ return obslist; }
	public JList getDiagList(){ return diaglist; }
	public void doDiagnosis(){
		Vector v = new Vector();
		for(int i=0;i<obslistmodel.size();i++){
			Object o = obslistmodel.getElementAt(i);
			if(o instanceof Entity) v.addElement(o);
		}
		diaglistmodel.removeAllElements();
		diagnoser = new Diagnoser(v,this);
		diagnoser.proximityPower = jSlider1.getValue()/100.;
		diagnoser.start();
		diaglist.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
}

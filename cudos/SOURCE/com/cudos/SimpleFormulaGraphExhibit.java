package com.cudos;

import javax.swing.*;
import java.awt.*;
import com.cudos.common.*;
import java.awt.event.*;
import java.awt.geom.*;
import evaluator.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;
import evaluator.NoSuchVariableException;
import javax.swing.border.*;

/**
 * Simple graphing exhibit
 */

public class SimpleFormulaGraphExhibit extends CudosExhibit {

	public String getExhibitName(){return "Graph plotting";}
	private BorderLayout borderLayout1 = new BorderLayout();


	double coorx, coory;
	public void updateCoordinates(){
		if(graphPanel!=null){
			Point2D p = graphPanel.displayPoint;
			if(p!=null && (p.getX()!=coorx || p.getY()!=coory)){
				coorx=p.getX(); coory=p.getY();
				xlabel.setText( String.valueOf(coorx) );
				ylabel.setText( String.valueOf(coory) );
			}
		}
	}


	public SimpleFormulaGraphExhibit() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		graphPanel.displayPointComponent = coordinates;
		graphPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		expressionPanel.setExpression(new Expression("x"));
		updateFormula(false);
		centreButton.doClick();
	}
	private void jbInit() throws Exception {
		border1 = BorderFactory.createEmptyBorder(0,4,4,4);
		this.getContentPane().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		xlabel.setBorder(BorderFactory.createLoweredBevelBorder());
		xlabel.setPreferredSize(new Dimension(80, 21));
		xlabel.setText("0");
		jLabel5.setText("x=");
		ylabel.setBorder(BorderFactory.createLoweredBevelBorder());
		ylabel.setPreferredSize(new Dimension(80, 21));
		ylabel.setText("0");
		jLabel3.setText("y=");
		centreButton.setText("Centre");
		centreButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				centreButton_actionPerformed(e);
			}
		});
		jPanel4.setLayout(borderLayout3);
		jPanel7.setLayout(borderLayout5);
		statusbar.setText("Ready");
		tablemodel.addTableModelListener(new javax.swing.event.TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				tablemodel_tableChanged(e);
			}
		});
		jPanel10.setLayout(borderLayout8);
		vartable.setModel(tablemodel);
		vartable.setCellEditor(valueSliderEditor1);

		vartable.getColumnModel().getColumn(1).setCellEditor(valueSliderEditor1);

		jPanel6.setLayout(borderLayout6);
		jButton3.setText("Add");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton3_actionPerformed(e);
			}
		});
		jPanel5.setLayout(borderLayout4);
		jButton2.setText("Remove");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jPanel9.setLayout(borderLayout7);
		exprStore.setText("Add");
		exprStore.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exprStore_actionPerformed(e);
			}
		});
		exprRemove.setText("Remove");
		exprRemove.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exprRemove_actionPerformed(e);
			}
		});
		jPanel11.setLayout(borderLayout9);
		exprlist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				exprlist_valueChanged(e);
			}
		});
		exprlist.setModel(expressionlistmodel);
		graphPanel.setForeground(new Color(0, 128, 0));
		graphPanel.setDrawLabels(true);
		jPanel13.setLayout(borderLayout10);
		jPanel13.setBorder(BorderFactory.createLoweredBevelBorder());
		jPanel3.setLayout(borderLayout11);
		jPanel3.setBorder(border1);
		expressionPanel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expressionPanel1_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel4,  BorderLayout.CENTER);
		jPanel4.add(jSplitPane1, BorderLayout.CENTER);
		jSplitPane1.add(jPanel1, JSplitPane.LEFT);
		jPanel1.add(jPanel2, BorderLayout.SOUTH);
		coordinates.add(jLabel5, null);
		coordinates.add(xlabel, null);
		coordinates.add(jLabel3, null);
		coordinates.add(ylabel, null);
		jPanel2.add(centreButton, null);
		jPanel1.add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(expressionPanel, BorderLayout.CENTER);
		jPanel2.add(coordinates, null);
		jPanel1.add(jPanel13,  BorderLayout.CENTER);
		jPanel13.add(graphPanel,  BorderLayout.CENTER);
		jSplitPane1.add(jPanel10, JSplitPane.RIGHT);
		jPanel10.add(jTabbedPane1, BorderLayout.CENTER);
		jTabbedPane1.add(jPanel5,  "Variables");
		jPanel5.add(jScrollPane1, BorderLayout.CENTER);
		jPanel5.add(jPanel6, BorderLayout.NORTH);
		jScrollPane1.getViewport().setView(vartable);
		jPanel8.add(jButton3, null);
		jPanel8.add(jButton2, null);
		jTabbedPane1.add(jPanel9, "Equations");
		jPanel9.add(jPanel11, BorderLayout.CENTER);
		jPanel11.add(jScrollPane2, BorderLayout.CENTER);
		jScrollPane2.getViewport().add(exprlist, null);
		jPanel9.add(jPanel12, BorderLayout.NORTH);
		jPanel12.add(exprStore, null);
		jPanel12.add(exprRemove, null);
		jPanel6.add(jPanel8, BorderLayout.CENTER);
		this.getContentPane().add(jPanel7,  BorderLayout.SOUTH);
		jPanel7.add(statusbar, BorderLayout.CENTER);
		jSplitPane1.setDividerLocation(500);
	}

	void jButton1_actionPerformed(ActionEvent e) {
		updateFormula(true);
	}

	private JPanel jPanel4 = new JPanel();
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JPanel jPanel1 = new JPanel();
	private JLabel xlabel = new JLabel();
	private BorderLayout borderLayout2 = new BorderLayout();
	private JLabel jLabel5 = new JLabel();
	private JPanel coordinates = new JPanel(){
		public void paint(Graphics g){
			updateCoordinates();
			super.paint(g);
		}
	};
	private JLabel ylabel = new JLabel();
	private JLabel jLabel3 = new JLabel();
	private JButton centreButton = new JButton();
	private JPanel jPanel3 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private BorderLayout borderLayout3 = new BorderLayout();
	private VariableTableModel tablemodel = new VariableTableModel(Variable.table);
	private JPanel jPanel7 = new JPanel();
	private BorderLayout borderLayout5 = new BorderLayout();
	private JLabel statusbar = new JLabel();


	Vector variables = new Vector();
	/** Update the graph's formula from the text box */
	void updateFormula(boolean showErrors){
		variables.removeAllElements();
		try{
			graphPanel.setExpression( expressionPanel.getExpression() );
			refreshExpressionList();
		}catch(NoSuchVariableException x){
			setVariableValue(x.variableName, 1.0);
			updateFormula(showErrors);
		}catch(ParseException x){
			if(showErrors)
				JOptionPane.showMessageDialog(this,x.toString(), "Error in formula", JOptionPane.ERROR_MESSAGE);
			statusbar.setText(x.getMessage());
		}
	}

        public void setVariableValue(String varName, double value){
          Variable.set(varName, value);
          variables.add(varName);
          tablemodel.fireTableDataChanged();
        }

	void centreButton_actionPerformed(ActionEvent e) {
		graphPanel.setXRange(-1,1);
		graphPanel.setYRange(-1,1);
		repaint();
	}


	void tablemodel_tableChanged(TableModelEvent e) {
		repaint();
	}

	private int serial=1;
	private JPanel jPanel10 = new JPanel();
	private BorderLayout borderLayout8 = new BorderLayout();
	private JTabbedPane jTabbedPane1 = new JTabbedPane();
	private JTable vartable = new JTable();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private BorderLayout borderLayout6 = new BorderLayout();
	private BorderLayout borderLayout4 = new BorderLayout();
	private JPanel jPanel8 = new JPanel();
	private JPanel jPanel6 = new JPanel();
	private JButton jButton3 = new JButton();
	private JPanel jPanel5 = new JPanel();
	private JButton jButton2 = new JButton();
	private JPanel jPanel9 = new JPanel();
	private BorderLayout borderLayout7 = new BorderLayout();
	private JPanel jPanel11 = new JPanel();
	private JPanel jPanel12 = new JPanel();
	private JButton exprStore = new JButton();
	private JButton exprRemove = new JButton();
	private BorderLayout borderLayout9 = new BorderLayout();
	private JScrollPane jScrollPane2 = new JScrollPane();
	private JList exprlist = new JList();
	private DefaultListModel expressionlistmodel = new DefaultListModel();
	JPanel jPanel13 = new JPanel();
	public FormulaGraphPanel graphPanel = new FormulaGraphPanel();
	BorderLayout borderLayout10 = new BorderLayout();
	public ExpressionPanel expressionPanel = new ExpressionPanel();
	private BorderLayout borderLayout11 = new BorderLayout();
	private Border border1;
	private ValueSliderEditor valueSliderEditor1 = new ValueSliderEditor();
	void jButton3_actionPerformed(ActionEvent e) {
		Variable.set("V"+serial++,1.0);
		tablemodel.fireTableDataChanged();
	}

	void jButton2_actionPerformed(ActionEvent e) {
		String name = (String)tablemodel.getValueAt(vartable.getSelectedRow(), 0);
                if(name==null)return;
		Variable.table.remove(name.intern());
		tablemodel.fireTableDataChanged();
	}

	void exprlist_valueChanged(ListSelectionEvent e) {
		int index = exprlist.getSelectedIndex();
		if(index>=0){
			graphPanel.selectExpression(index);
			expressionPanel.setExpression(graphPanel.getExpression());
		}
	}

	void exprStore_actionPerformed(ActionEvent e) {
          storeExpression();
	}
        public void storeExpression(){
		Expression exp = graphPanel.getExpression();
		graphPanel.expressions.add(exp);
		refreshExpressionList();
	}
	void refreshExpressionList(){
		expressionlistmodel.removeAllElements();
		for(int i=0;i<graphPanel.expressions.size();i++){
			Expression e = (Expression) graphPanel.expressions.get(i);
			expressionlistmodel.add(i, e.toString());
		}
		exprlist.setSelectedIndex( graphPanel.expressions.indexOf(graphPanel.getExpression()) );
	}

	void exprRemove_actionPerformed(ActionEvent e) {
		int i = exprlist.getSelectedIndex();
		if(i>=0){
			expressionlistmodel.remove(i);
			graphPanel.expressions.remove(i);
			if(graphPanel.expressions.size()>0) {
				graphPanel.selectExpression(0);
				expressionPanel.setExpression(graphPanel.getExpression());
				refreshExpressionList();
			}
			graphPanel.repaint();
		}
	}

	void expressionPanel1_actionPerformed(ActionEvent e) {
		updateFormula(true);
	}
}


/** Implements a table to reflect the properties in a hashtable */
class VariableTableModel extends AbstractTableModel{
	VariableTableModel(Hashtable h){ this.h=h; }
	Hashtable h;
	String[] colName = {"Variable","Value"};
	Class[] colClass = {String.class, Double.class};
	public int getRowCount(){
		return h.size();
	}
	public int getColumnCount(){
		return 2;
	}
	public Object getValueAt(int row, int col){
		if(col==0){
			Enumeration e = h.keys(); Object o = null;
			for(int i=0;i<=row;i++) o = e.nextElement();
			return o;
		}else{
			Iterator e = h.values().iterator(); Object o = null;
			for(int i=0;i<=row;i++) o = e.next();
			return o;
		}
	}
	public boolean isCellEditable(int row, int col){
		return col==1;
	}
	public String getColumnName(int col){
		return colName[col];
	}
	public Class getColumnClass(int col){
		return colClass[col];
	}
	public void setValueAt(Object value, int row, int col){
		if(col==1 && value!=null){
			Double v=null;
			try{
				if(value instanceof Double) v=(Double)value;
				else v=Double.valueOf( value.toString() );
			}catch(NumberFormatException x){return;}
			h.put( getValueAt(row,0) , v );
			fireTableCellUpdated(row,col);
		}
	}
}

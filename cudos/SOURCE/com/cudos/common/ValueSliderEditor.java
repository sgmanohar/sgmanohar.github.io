package com.cudos.common;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * Control to edit a double value with a slider and text.
 */

public class ValueSliderEditor extends JPanel implements TableCellEditor {

	public ValueSliderEditor() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	JTable table;
	Object value;
	int row, column;
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.table=table;
		this.value=value;
		this.row=row;
		this.column=column;
		init();
		return this;
	}
	public Object getCellEditorValue() {
		return new Double(getText());
	}
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}
	public boolean stopCellEditing() {
		table.setValueAt(getCellEditorValue(),row,column);
		cel.editingStopped(new ChangeEvent(this));
		return true;
	}
	public void cancelCellEditing() {
		cel.editingCanceled(new ChangeEvent(this));
	}

	double smin, smax;
	void init(){
		if(value instanceof Number){
			double v=((Number)value).doubleValue();
			doupdate=false;
			setSlider(v);
			doupdate=true;
		}
		jTextField1.setText(value.toString());
	}

	private MyCEL cel=new MyCEL();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel jPanel1 = new JPanel();
	private BorderLayout borderLayout2 = new BorderLayout();
	private JSlider jSlider1 = new JSlider();
	private JTextField jTextField1 = new JTextField();
	private class MyCEL implements CellEditorListener{
		Vector list = new Vector();
		public void editingCanceled(ChangeEvent e){
			for(int i=0;i<list.size();i++) ((CellEditorListener)list.get(i)).editingCanceled(e);
		}
		public void editingStopped(ChangeEvent e){
			for(int i=0;i<list.size();i++) ((CellEditorListener)list.get(i)).editingStopped(e);
		}
	}
	public void addCellEditorListener(CellEditorListener l) {
		cel.list.add(l);
	}
	public void removeCellEditorListener(CellEditorListener l) {
		cel.list.remove(l);
	}
	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		jTextField1.setBorder(null);
		jTextField1.setToolTipText("");
		jTextField1.setColumns(8);
		jTextField1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jTextField1_actionPerformed(e);
			}
		});
		jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				jSlider1_stateChanged(e);
			}
		});
		this.add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jSlider1, BorderLayout.CENTER);
		this.add(jTextField1,  BorderLayout.EAST);
	}
	void setSlider(double v){
		smin = v/5; smax = v*5;
		jSlider1.setValue((int)(100*((v-smin)/(smax-smin))));
	}
	double getSlider(){
		return jSlider1.getValue()*(smax-smin)/100+smin;
	}
	double getText(){
		return Double.parseDouble(jTextField1.getText());
	}
	void jTextField1_actionPerformed(ActionEvent e) {
		double v=getText();
		doupdate=false;
		setSlider(v);
		doupdate=true;
	}
	boolean doupdate = true;
	void jSlider1_stateChanged(ChangeEvent e) {
		if(doupdate){
			jTextField1.setText( String.valueOf(getSlider()) );
			table.getModel().setValueAt(getCellEditorValue(), row,column);
		}
	}
}


package unit;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.event.*;

/**
 * Class to list the units that can possibly
 * replace the given unit.
 */

public class UnitList extends AbstractCellEditor implements TableCellEditor{

	Unit unit;

	public UnitList() {
	}
	private Unit toUnit(Object o){
		Unit unit;
		if(o instanceof Unit){
			unit = (Unit)o;
		}else if(o!=null) {
			unit = new Unit(o.toString());
		}else{
			unit = new Unit("");
		}
		return unit;
	}

	JComboBox comboBox;
	public Component getTableCellEditorComponent(JTable table,
		Object value, boolean isSelected, int row, int column){
		unit = toUnit(value);
		Vector choices = unit.getCompatibleUnits();

		comboBox = new JComboBox(choices);
		comboBox.setEditable(true);

		comboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JComboBox cb = (JComboBox)e.getSource();
				Object o = cb.getSelectedItem();
				if(o!=null){
					Unit newsel = toUnit(o);
					Vector choices = newsel.getCompatibleUnits();
					cb.setModel(new DefaultComboBoxModel(choices));
					cb.setSelectedItem(newsel);
					try{ cb.showPopup(); }
					catch(Exception x){}
				}
			}
		});

		if(choices.size()>0)comboBox.setSelectedItem(unit);
		return comboBox;
	}
	public Object getCellEditorValue(){
		return comboBox.getSelectedItem();
	}
}
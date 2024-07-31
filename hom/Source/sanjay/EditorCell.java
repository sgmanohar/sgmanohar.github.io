
/**
 * Title:        Vision java projects<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package sanjay;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import java.text.*;

public class EditorCell extends JPanel {
	JComponent displayComponent;
	JPopupMenu popup = new JPopupMenu();

	// The list of displays for leaves of various types
	static Object[][][] displayTypes=new Object[][][]{
		{		//numeric leaf
			new Class[]{Number.class},
			new Class[]{NumberModify.class,  NumberText.class, NumberSlider.class},
			new String[]{"Editor", "Text","Slider"}
		},{	//string leaf
			new Class[]{String.class},
			new Class[]{TextEdit.class},
			new String[]{"Text"}
		},{ //boolean leaf
			new Class[]{Boolean.class},
			new Class[]{BooleanEdit.class},
			new String[]{"Checkbox"}
		},{
			new Class[]{Font.class},
			new Class[]{FontEdit.class, FontSelect.class},
			new String[]{"Edit font", "Select font"}
		},{
			new Class[]{Color.class},
			new Class[]{ColorEdit.class},
			new String[]{"Chooser"}
		},{
			new Class[]{TagList.class},
			new Class[]{DropDown.class},
			new String[]{"List"}
		},{
			new Class[]{FlagList.class},
			new Class[]{FlagEditor.class},
			new String[]{"Menu"}
		}
	};

	public static boolean classHasEditor(Class testCls){
		for(int i=0;i<displayTypes.length;i++){
			Object[][] type=displayTypes[i];
			Class[] cls=(Class[])type[0];
			for(int j=0;j<cls.length;j++){
				if(cls[j].isAssignableFrom(testCls)){
					return true;
				}
			}
		}
		return false;
	}

	Class[] numericDisplays=new Class[]{
		NumberText.class, NumberSlider.class
	};
	String[] numericNames=new String[]{
		"Text", "Slider"
	};

	Class[] textDisplays=new Class[]{
		TextEdit.class
	};
	String[] textNames=new String[]{
		"Text"
	};

	public EditorCell() {
		setLayout(new BorderLayout());
	}

	TLeaf leaf;
	Class objclass;
	//list of possible displays for current leaf
	Class[] list=null; String[] namelist=null;

	//set leaf, and create simplest display
	public void setLeaf(TLeaf newleaf){
		removeAll();
		leaf=newleaf;
		Object object=leaf.get();
		if(object != null) objclass=object.getClass();
			else objclass = java.lang.Object.class; //??
		for(int i=0;i<displayTypes.length;i++){
			Object[][] type=displayTypes[i];
			Class[] cls=(Class[])type[0];
			for(int j=0;j<cls.length;j++){
				if(cls[j].isAssignableFrom(objclass)){
					list=(Class[])type[1];
					namelist=(String[])type[2];
					createDisplay(list[0]); // use the first listed editor type
				}
			}
		}
	}

	void createDisplay(Class displayClass){
		try{
			displayComponent=(JComponent)displayClass.newInstance();
		}catch(Exception ex){ex.printStackTrace();}
		((Display)displayComponent).setLeaf(leaf);
		displayComponent.addMouseListener(rightclicklistener);
		popup.removeAll();
		for(int i=0;i<list.length;i++){
			JMenuItem menuitem=new JMenuItem(namelist[i]);
			menuitem.addActionListener(menulistener);
			popup.add(menuitem);
		}
		removeAll();
		add(displayComponent, BorderLayout.CENTER);
		repaint();
	}

	public void updateDisplay(){
		if(displayComponent!=null && displayComponent instanceof Display){
			((Display)displayComponent).updateDisplay();
		}
	}


	//show popup menu when right-click
	MouseListener rightclicklistener=new MouseAdapter(){
		public void mouseReleased(MouseEvent e){
			if(e.isPopupTrigger()){
				popup.show(EditorCell.this, e.getX(),e.getY());
			}
		}
	};

	//change display when popup menu selection made
	ActionListener menulistener=new ActionListener(){
		public void actionPerformed(ActionEvent e){
			String s=e.getActionCommand();
			for(int i=0;i<list.length;i++){
				if(namelist[i].equals(s)){
					createDisplay(list[i]);
					break;
				}
			}
		}
	};

	/** Delegate action listener */
	public void addActionListener(ActionListener a){
		((Display)displayComponent).addActionListener(a);
	}
	public void removeActionListener(ActionListener a){
		((Display)displayComponent).removeActionListener(a);
	}

}



	// editors

	interface Display{
		void setLeaf(TLeaf leaf);
		void updateDisplay();
		public void addActionListener(ActionListener a);
		public void removeActionListener(ActionListener a);
	}


	abstract class DisplayPanel extends JPanel implements Display{
		DisplayPanel(){
			setLayout(new BorderLayout());
		}
		ActionListener changeListener;
		void fireChange(){
			if(changeListener!=null)changeListener.actionPerformed(
					new ActionEvent(this, 0, "Change"));
		}
		public void addActionListener(ActionListener a){
			changeListener = AWTEventMulticaster.add(changeListener, a);
		}
		public void removeActionListener(ActionListener a){
			changeListener = AWTEventMulticaster.remove(a, changeListener);
		}

	}






	// Subclasses of editors


	class NumberText extends DisplayPanel{
		TLeaf leaf;
		JTextField tf = new JTextField();
		{add(tf);}
		public void setLeaf(TLeaf l){
			leaf=l;
			updateDisplay();
			tf.addFocusListener(new FocusAdapter(){
				public void focusLost(FocusEvent e){
					updateValue();
				}
			});
			tf.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					updateValue();
				}
			});
		}
		public void updateValue(){
			leaf.setNumeric( Double.parseDouble(tf.getText()) );
			fireChange();
		}
		public void updateDisplay(){
			tf.setText(leaf.get().toString());
		}
	}
	class NumberSlider extends DisplayPanel{
		TLeaf leaf;
		JSlider sl = new JSlider();
		{add(sl);}
		double min=0,max=1;
		public void setLeaf(TLeaf l){
			leaf=l;
			updateDisplay();
			sl.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e){
					updateValue();
				}
			});
		}
		public void updateDisplay(){
			double v=((Number)leaf.get()).doubleValue();
			int dv=(int)((v-min)/(max-min)*100);
			sl.setValue(dv);
		}
		public void updateValue(){
			double v=min+sl.getValue()*(max-min)/100;
			leaf.setNumeric(v);
			fireChange();
		}
	}
	class NumberModify extends DisplayPanel implements AdjustmentListener{
		TLeaf leaf;
		public void setLeaf(TLeaf l){
			leaf = l;
			updateDisplay();
		}
		NumberFormat nf = NumberFormat.getInstance();
		public void updateDisplay(){
			double val = ((Number)leaf.get()).doubleValue();
			label.setText(String.valueOf(val));
		}
		//public void updateValue(){		}
		JLabel label = new JLabel();
		JScrollBar scroll = new JScrollBar(JScrollBar.HORIZONTAL,50,10,0,100);
		NumberModify(){
			setLayout(new BorderLayout());
			add(label, BorderLayout.CENTER);
			add(scroll, BorderLayout.EAST);
			scroll.addAdjustmentListener(this);
			nf.setMaximumFractionDigits(2);
			nf.setMaximumIntegerDigits(2);
		}
		boolean ignoreEvent = false;
		public synchronized void adjustmentValueChanged(AdjustmentEvent e){
			if(!ignoreEvent){
				ignoreEvent=true;
				int delta = scroll.getValue() - 50;
				scroll.setValue(50);
				ignoreEvent=false;
				double prev = ((Number)leaf.get()).doubleValue();
				double val = prev;
				if(val<0) val = Math.abs(val);
				if(val==0)val = 0.1;
				double decade = Math.floor(Math.log(val)/Math.log(10))-1;
				double increment = Math.pow(10, decade);
				double next = prev + increment * delta;
				long round = Math.round(next/increment);
				next = round*increment;  //round
				leaf.setNumeric( next );
				fireChange();
				updateDisplay();
			}
		}
	}

	class TextEdit extends DisplayPanel {
		TLeaf leaf;
		JTextField tf = new JTextField();
		{add(tf);}
		public void setLeaf(TLeaf l){
			leaf=l;
			updateDisplay();
			tf.addFocusListener(new FocusAdapter(){
				public void focusLost(FocusEvent e){
					updateValue();
				}
			});
			tf.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					updateValue();
				}
			});
		}
		public void updateValue(){
			leaf.set(tf.getText());
			fireChange();
		}
		public void updateDisplay(){
			tf.setText(leaf.get().toString());
		}
	}
	class BooleanEdit extends DisplayPanel{
		TLeaf leaf;
		JCheckBox cb = new JCheckBox();
		{add(cb);}
		public void setLeaf(TLeaf l){
			leaf=l;
			updateDisplay();
			cb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					updateValue();
				}
			});
		}
		public void updateDisplay(){
			boolean value=((Boolean)leaf.get()).booleanValue();
			cb.setSelected(value);
		}
		public void updateValue(){
			leaf.set(new Boolean(cb.isSelected()));
			fireChange();
		}
	}

	class FontEdit extends DisplayPanel implements ActionListener{
		TLeaf leaf;
		JLabel label=new JLabel();
		JButton button = new JButton("");
		public void setLeaf(TLeaf l){
			leaf=l;
			setLayout(new BorderLayout());
			add(label, BorderLayout.CENTER);
			add(button, BorderLayout.EAST);
			button.addActionListener(this);
			button.setMargin(new Insets(2,0,2,0));
			updateDisplay();
		}
		public void updateDisplay(){
			Font font=(Font)leaf.get();
			label.setText(font.getName()+" "+font.getSize());
		}
		public void actionPerformed(ActionEvent e){
			JDialog d=new FontSelectionDialog();
			d.setFont((Font)leaf.get());
			d.show();
			leaf.set( d.getFont() );
			updateDisplay();
			fireChange();
		}
	}
	class FontSelect extends DisplayPanel{
		TLeaf leaf;
		PropertyEditor pe;
		public void setLeaf(TLeaf l){
			leaf=l;
			setLayout(new BorderLayout());
			pe=PropertyEditorManager.findEditor(Font.class);
			updateDisplay();
			add((Component)pe);
			this.addFocusListener(fl);
		}
		FocusListener fl=new FocusAdapter(){
			public void focusLost(FocusEvent e){
				updateValue();
			}
		};
		public void updateDisplay(){
			pe.setValue((Font)leaf.get());
		}
		public void updateValue(){
			leaf.set( (Font)pe.getValue() );
			fireChange();
		}
	}
	class ColorEdit extends DisplayPanel implements ActionListener{
		TLeaf leaf;
		JLabel label=new JLabel();
		JButton button = new JButton("");
		public void setLeaf(TLeaf l){
			leaf=l;
			setLayout(new BorderLayout());
			add(label, BorderLayout.CENTER);
			add(button, BorderLayout.EAST);
			button.addActionListener(this);
			button.setMargin(new Insets(2,0,2,0));
			label.setOpaque(true);
			updateDisplay();
		}
		public void updateDisplay(){
			Color colour=(Color)leaf.get();
			label.setText("[" + colour.getRed() + "," + colour.getGreen() + ","
										 + colour.getBlue() + "]");
			label.setBackground(colour);
		}
		public void actionPerformed(ActionEvent e){
			leaf.set(
					JColorChooser.showDialog(this, "Select colour", (Color)leaf.get())
			);
			updateDisplay();
			fireChange();
		}
	}

	class DropDown extends DisplayPanel implements ActionListener{
		TLeaf leaf;
		JComboBox cb=new JComboBox();
		{add(cb);}
		public void setLeaf(TLeaf l){
			leaf=l;
			cb.setModel(new DefaultComboBoxModel(
				((TagList)(leaf.get())).getTags() ));
			updateDisplay();
			cb.addActionListener(this);
		}
		public void updateDisplay(){
			cb.setSelectedItem( leaf.get() );
		}
		public void updateValue(){
			leaf.set( ((TagList)leaf.get()).getTags()[ cb.getSelectedIndex() ] );
			fireChange();
		}
		public void actionPerformed(ActionEvent e){
			updateValue();
		}
	}

	class FlagEditor extends DisplayPanel implements ActionListener{
		Object[] flags;
		TLeaf leaf;
		JPopupMenu menu=new JPopupMenu();
		JLabel jl = new JLabel();
		{add(jl);}
		public void setLeaf(TLeaf l){
			leaf=l;
			updateDisplay();
			jl.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
				menu.show(FlagEditor.this, e.getX(),e.getY());
			}});
		}
		public void actionPerformed(ActionEvent e){
			String s=e.getActionCommand();
			for(int i=0;i<flags.length;i++)
				if(flags[i].toString().equals(s)){
					FlagList fl=(FlagList)leaf.get();
					fl.setFlag(i,!fl.getFlag(i));
				}
			updateDisplay();
			fireChange();
		}
		public void updateDisplay(){
			FlagList fl=(FlagList)leaf.get();
			flags = fl.getFlags();
			jl.setText(fl.toString());
			// setup checkboxes of menu
			menu.removeAll();
			for(int i=0;i<flags.length;i++){
				JCheckBox c=new JCheckBox( flags[i].toString(), fl.getFlag(i) );
				menu.add(c);
				c.addActionListener(this);
			}
		}
	}

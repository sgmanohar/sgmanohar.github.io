
package medicine.gui;
import medicine.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;

/**
 * Allows entities to be selected rapidly, by typing some subphrase of the
 * entity desired, and pressing the enter key. The dialog displays a popup
 * list of the possibilities.
 *
 * @todo Replace the ComboBox with an instance of EntityChooserComboBox
 */

public class QuickAddDialog extends JDialog implements ActionListener{

	Entity entity, startNode;
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel2 = new JPanel();
  JComboBox combo = new JComboBox();
	JTextField textfield;
	public QuickAddDialog(Entity startNode){
		this.startNode=startNode;
		init();
	}
  public QuickAddDialog() {
		init();
	}
	void init(){
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
		setModal(true);
		setSize(230,300);

		Component c=combo.getEditor().getEditorComponent();
		if(c instanceof JTextField){
			textfield=(JTextField)c;
		}
		MainApplication.centreWindow(this);
  }

  private void jbInit() throws Exception {
    jButton1.setText("Cancel");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
	jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("OK");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
	jButton2_actionPerformed(e);
      }
    });
    combo.setPreferredSize(new Dimension(200, 21));
    combo.setEditable(true);
    combo.setModel(model);
    jPanel2.add(combo, null);
    jPanel1.add(jButton1, null);
    jPanel1.add(jButton2, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		combo.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				editChange();
			}
		});
  }

  void jButton1_actionPerformed(ActionEvent e) {
		entity=null;
		exit();
  }

	EntitySearcher es;
	boolean createdNewEntity=false;
  DefaultComboBoxModel model = new DefaultComboBoxModel();
	void editChange(){
		keypressed();
	}
	boolean clearList=false;
	String oldstring;
	void keypressed(){
		String string=combo.getEditor().getItem().toString();
		if(string.equals(oldstring)){
			OK();
			return;
		}

		oldstring=string;
		if(string.length()<3)return;
		synchronized(this){
			if(es!=null) es.stop();
			clearList=true;
			es=new EntitySearcher(string, startNode, this);
			es.start();
		}
	}
	void emptyList(){
		model=new DefaultComboBoxModel();
		combo.setModel(model);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()instanceof Entity){
			Object selection=combo.getEditor().getItem();

			if(clearList){
				emptyList();
				clearList=false;
			}
			Entity f=(Entity)e.getSource();
			combo.hidePopup();
			model.addElement(f);
			combo.setMaximumRowCount(model.getSize());
			combo.showPopup();

			combo.getEditor().setItem(selection);
		}
	}

  void jButton2_actionPerformed(ActionEvent e) {
		OK();
	}

	void OK(){
		Object o=combo.getSelectedItem();
		Entity sel=null;
		if(o instanceof Entity)sel=(Entity)o;
		if(sel==null && o instanceof String){
			String name=(String)o;
			for(int i=0;i<model.getSize();i++){
				Object ob=model.getElementAt(i);
				if(ob instanceof Entity){
					Entity test=(Entity)ob;
					if(test.equalsIgnoreCase(name)) sel=test;
				}
			}
		}
		if(sel==null){
			String str=combo.getEditor().getItem().toString();
			if( JOptionPane.showConfirmDialog(this, "Create new entity '"+str+"' ?",
			 "No entity selected", JOptionPane.YES_NO_OPTION) ==JOptionPane.YES_OPTION){
				entity=new Entity(null,-1);
				entity.name=str;
				createdNewEntity=true;
				exit();
			}else{
				entity=null;
				exit();
			}
		}else{
			entity=sel;
			createdNewEntity=false;
			exit();
		}
  }

	void exit(){
		if(es!=null)es.stop();
		hide();
	}
}

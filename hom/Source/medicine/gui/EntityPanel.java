
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;
import medicine.*;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

public class EntityPanel extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();

  public EntityPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

	Entity entity;
  Border border1;
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JScrollPane jScrollPane1 = new JScrollPane();
  JEditorPane descriptiontxt = new JEditorPane();
  HTMLEditorKit hTMLEditorKit1 = new HTMLEditorKit();
  JScrollPane synonympanel = new JScrollPane();
  JList synonymlist = new JList();
  JTextField namepanel = new JTextField();
  JPopupMenu popupmenu = new JPopupMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem removesynon = new JMenuItem();
	public void setEntity(Entity e){
		entity=e;
		init();
	}
	public Entity getEntity(){return entity;}
	public void init(){
		if(entity!=null){
			namepanel.setText(entity.name);
			synonymlist.setListData(entity.synonyms);
			descriptiontxt.setText(entity.description);
		}else{
			namepanel.setText("");
			synonymlist.setListData(new Vector());
			descriptiontxt.setText("");
		}
	}


  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(248, 240, 255),new Color(121, 117, 151)),BorderFactory.createEmptyBorder(1,1,1,1));
    this.setLayout(borderLayout1);
    this.setBorder(border1);
    //descriptiontxt.setEditorKit(null);
    descriptiontxt.addFocusListener(new java.awt.event.FocusAdapter() {

      public void focusLost(FocusEvent e) {
        descriptionChanged(e);
      }
    });
    namepanel.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        nameChanged();
      }
    });
    jMenuItem1.setText("Add...");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addsynonym(e);
      }
    });
    removesynon.setText("Remove");
    removesynon.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        removesynonym(e);
      }
    });
    synonymlist.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseReleased(MouseEvent e) {
        synonymlist_mouseReleased(e);
      }
    });
    namepanel.addFocusListener(new java.awt.event.FocusAdapter() {

      public void focusLost(FocusEvent e) {
        nameChanged();
      }
    });
    namepanel.setFont(new java.awt.Font("SansSerif", 1, 12));
    namepanel.setToolTipText("Name of entity");
    this.add(jTabbedPane1, BorderLayout.CENTER);
    jTabbedPane1.add(jScrollPane1, "Description");
    jTabbedPane1.add(synonympanel, "Synonyms");
    this.add(namepanel, BorderLayout.NORTH);
    synonympanel.getViewport().add(synonymlist, null);
    jScrollPane1.getViewport().add(descriptiontxt, null);
    popupmenu.add(jMenuItem1);
    popupmenu.add(removesynon);
  }



  void namepanel_inputMethodTextChanged(InputMethodEvent e) {
	if(entity != null) entity.name=namepanel.getText();
  }


  void addsynonym(ActionEvent e) {
	SynonymInputDialog sid=new SynonymInputDialog(entity);
	sid.setModal(true);
	sid.show();
	if(sid.synonym!=null && sid.synonym!=""){
		entity.synonyms.add(sid.synonym);
		redisplay();
	}
  }

  void removesynonym(ActionEvent e) {
	Object o=synonymlist.getSelectedValue();
	entity.synonyms.remove(o);
	redisplay();
  }
	void redisplay(){
		setEntity(entity);
	}


  void synonymlist_mouseReleased(MouseEvent e) {
	if(isEditable && e.getModifiers()==MouseEvent.BUTTON3_MASK){
		popupmenu.show(synonympanel,e.getX(),e.getY());
	}

  }



	/**
	 The description box has been exited
	*/
  void descriptionChanged(FocusEvent e) {
	if(entity!=null) entity.description=descriptiontxt.getText();
  }

 	/**
	 The enter key has been pressed in the name input box, or the box has been exited
	*/
	JComponent thisComp=this;
	int nreplaces;
	String lastTestedName="";
 void nameChanged() {
	if(entity==null)return;
	entity.name=namepanel.getText();
	//prevent repetitive name checking
	if(lastTestedName.equals(entity.name))return;
	lastTestedName=entity.name;

	//try and see if the same item already exists
	EntitySearcher esearch=new EntitySearcher(entity.name, entity, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			//is it just this one?
			if(e.getSource()==entity)return;
			if(e.getSource() instanceof EntitySearcher && e.getActionCommand()==null) return;
				//if not:
			synchronized(thisComp){	//ensure nreplaces is updated on time
			  if((e.getSource() instanceof Entity) && (nreplaces<1)){
			    Entity duplicate=(Entity)e.getSource();
			    String dstr=duplicate.name+" ("+duplicate.description+")";
			    if(entity.isBlank()){	//when there is no information here,
				int res=JOptionPane.showConfirmDialog(thisComp, "An entity called "+
				  dstr+" already exists. Would you like to replace the"
				  +"current entity with it?", "Duplicate entity", JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION){
					nreplaces++;
					entity.replaceAllWith(duplicate);
					setEntity(duplicate);
					if(getParent() instanceof NavigatorPanel)
					  ((NavigatorPanel)getParent()).setEntity(entity);
				}
			    }else{			//there is already information here
				JOptionPane.showMessageDialog(thisComp, "Warning: an entity called "+
				  dstr+" already exists.", "Duplicate entity name", JOptionPane.WARNING_MESSAGE);
			    }
			  }
			}
		}
	});
	nreplaces=0;
	esearch.exactMatch=true;
	esearch.start();
  }

	boolean isEditable;
	public void setEditable(boolean t){
		isEditable=t;
		namepanel.setEditable(t);
		descriptiontxt.setEditable(t);
	}
	public boolean isEditable(){return isEditable;}

}

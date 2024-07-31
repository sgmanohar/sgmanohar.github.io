
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
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;


public class EntityEditorPanel  extends JPanel implements ActionListener{
  public NavigatorPanel navigator = new NavigatorPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel clippanel = new JPanel();
  JLabel cliplabel = new JLabel();


  public EntityEditorPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

	//add handler for Clipboard keys to each list
	JList[] lists=new JList[]{navigator.sublist, navigator.superlist, navigator.causelist, navigator.effectlist};
/*	for(int i=0;i<lists.length;i++){
		lists[i].registerKeyboardAction(this, "Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
		 JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		lists[i].registerKeyboardAction(this, "Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
		 JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}
*/
       registerKeyboardAction(this, "Bookmark", KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK),
	 JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

       TransferableEntity.listener=this;
       TransferableEntityList.listener=this;
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEtchedBorder(new Color(248, 240, 255),new Color(121, 117, 151));
    titledBorder1 = new TitledBorder(border1,"Bookmarks");
    border2 = BorderFactory.createEtchedBorder(new Color(248, 240, 255),new Color(121, 117, 151));
    titledBorder2 = new TitledBorder(border2,"Clipboard");
    this.setLayout(borderLayout1);
    clippanel.setMinimumSize(new Dimension(80, 55));
    cliplabel.setToolTipText("Clipboard");
    cliplabel.setText("Nothing copied");
    bookmarkpanel.setMinimumSize(new Dimension(80, 38));
    this.add(navigator, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(bookmarkpanel, null);
    jPanel1.add(clippanel, null);
    clippanel.add(cliplabel, null);
	cliplabel.setIcon(new ImageIcon("openFile.gif"));
  }


	/**
	 when a menu item is selected, open selector window,
	 then add the selected item to the correct list
	*/
/*
  void menuaction(ActionEvent e) {
	String s=e.getActionCommand();
	if(s.equals("Synonyms")){inputSynonym();return;}

	EntityChooser ec=new EntityChooser();
	ec.navigator.setEntity(navigator.entity);
	ec.setModal(true);
	ec.show();
	if(!ec.OK)return;	//no selection made
	if(ec.NEW){
		//new item selected! open editor?
	}
	Vector v=new Vector();
	if(s.equals("Superclasses"))v=navigator.entity.parents;
	if(s.equals("Subclasses"))v=navigator.entity.children;
	if(s.equals("Causes"))v=navigator.entity.causes;
	if(s.equals("Effects"))v=navigator.entity.effects;
		//v must not be null !
		//add the entity to the correct list in the current entity
	v.add(ec.entity);
		//redisplay it
	navigator.setEntity(navigator.entity);
  }
*/


	public void inputSynonym(){
		SynonymInputDialog sid=new SynonymInputDialog(navigator.entity);
		sid.setModal(true);
		sid.show();
		if(sid.synonym!=""){
			navigator.entity.synonyms.add(sid.synonym);
			navigator.setEntity(navigator.entity);
		}
	}


 JPanel bookmarkpanel = new JPanel();
  Border border1;
  TitledBorder titledBorder1;
  Border border2;
  TitledBorder titledBorder2;
/*
    Entity clipboard;
 	public void setClipboard(Entity e){
		clipboard=e;
		cliplabel.setText(e.toString());
	}
	public Entity getClipboard(){return clipboard;}
*/
       public void actionPerformed(ActionEvent e){
		String s=e.getActionCommand();
                if(s.equals("ClipboardChanged")){
                    cliplabel.setText(e.getSource().toString());
                    return;

                }

		Component c=(Component)e.getSource();
		if(c instanceof JList){
			JList l=(JList)c;
//			if(s.equals("Copy")){
				/**
				 Retrieve selected entity from source list, and store in clipboard
				*/
//				Entity f=(Entity)l.getSelectedValue();
//				if(f!=null)setClipboard(f);
//			}else if(s.equals("Paste") && getClipboard()!=null){
				/**
				 calculate what relationship is represented by the currently
				 selected list, and connect the clipboard item to the current
				 entity's list of this relation.
				*/
//				int rel=navigator.relationFromList(l);
//				navigator.entity.connect(getClipboard(),rel);
//				navigator.redisplay();
//			}
		}
		if(s.equals("Bookmark")){
			bookmarkpanel.add(new NavButton(navigator.entity, navigator));
			validateTree();
		}

	}


}


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

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;



public class AdvancedFindDialog extends FindDialog{
  AdvancedFindControls afc = new AdvancedFindControls();
  JButton searchbutton = new JButton();

  public AdvancedFindDialog(Entity e){
	super(e);
	init();
	afc.currententity.setText(e.name);
  }

  public AdvancedFindDialog() {
    init();
  }
  void init(){
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    searchbutton.setText("Search");
    searchbutton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        searchbutton_actionPerformed(e);
      }
    });
    this.getContentPane().add(afc, BorderLayout.NORTH);
    buttonpanel.add(searchbutton, BorderLayout.SOUTH);
  }
        public void findtext_actionPerformed(ActionEvent e){
		//select  find text field
		afc.textcheck.setSelected(true);
	}



	int relation, nrelations;
	int search;
	static final int NRELATIONS=1, TEXT=2, RELATEDTO=3;
	boolean caseSensitive, exactMatch, superclass;

  void searchbutton_actionPerformed(ActionEvent e) {
		synchronized(found){
			found=new Vector();
			foundlist.setListData(found);
		}
		initialiseSearch();
  }
  void initialiseSearch(){
		if(afc.numcheck.isSelected()){
			search=NRELATIONS;
			try{
				relation=afc.getSelectedRelation();
				if(relation<0)throw new Exception("No relationship selected");
				nrelations=Integer.valueOf( afc.numentitiestext.getText() ).intValue();
			}catch(NumberFormatException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Not a number",
				  "Please type a number", JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Please select a relationship",
				  "No relationship selected", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(afc.textcheck.isSelected()){
			search=TEXT;
			exactMatch=!afc.contains.isSelected();
			caseSensitive=afc.casesensitive.isSelected();
			searchstring=afc.findtext.getText();
		}

		EntityCrawler es=new EntityCrawler(anyNode, new ActionListener(){
			public void actionPerformed(ActionEvent e){test(e);}
		});

		if(afc.relatedcheck.isSelected()){
			search=RELATEDTO;
			int i=afc.getSelectedRelation();
			if(i==-1)i=Entity.CAUSE | Entity.CHILD | Entity.EFFECT | Entity.PARENT;
			if(afc.includesupercheck.isSelected()) i=i | Entity.PARENT;
			es.setBranchMask(i);
		}
		es.start();
  }
  void test(ActionEvent e){
	if(e.getActionCommand()==null){actionPerformed(e);return;}
	Entity r=(Entity)e.getSource();
	boolean found=false;

	/**
	 Test whether entity satisfies criteria
	*/
	if(search==NRELATIONS){
		int n=r.listOf(relation).size();
		if(n==nrelations)found=true;
	}else if (search==TEXT){
		if(caseSensitive){
			if(exactMatch) found=r.equals(searchstring);
			else found=r.contains(searchstring);
		}else{
			if(exactMatch) found=r.equalsIgnoreCase(searchstring);
			else found=r.containsIgnoreCase(searchstring);
		}
	}else if(search==RELATEDTO) found=true;

	if(found)actionPerformed(e);
	return;
  }
}

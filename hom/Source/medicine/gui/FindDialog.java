
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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;


public class FindDialog extends JDialog implements  ActionListener{
  JTextField findtext = new JTextField();
  JPanel toppanel = new JPanel();
  JLabel jLabel1 = new JLabel();
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane leftlistpane = new JScrollPane();
  JList foundlist = new JList();
  JPanel buttonpanel = new JPanel();
  JButton OK = new JButton();
  JButton jButton2 = new JButton();
  EntityPanel entitypanel = new EntityPanel();

  public FindDialog(Entity node) {
	this();
	anyNode=node;
  }

	public FindDialog(){
	    try {
	      jbInit();
	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
		setSize(475,430);
                MainApplication.centreWindow(this);
		setTitle("Find item");
	}

  private void jbInit() throws Exception {
    jLabel1.setText("Find");
    findtext.setPreferredSize(new Dimension(200, 21));
    findtext.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        findtext_actionPerformed(e);
      }
    });
    mainPanel.setLayout(borderLayout1);
    leftlistpane.setPreferredSize(new Dimension(100, 132));
    foundlist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

      public void valueChanged(ListSelectionEvent e) {
        foundlist_valueChanged(e);
      }
    });
    OK.setText("OK");
    OK.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        OKclick(e);
      }
    });
    jButton2.setText("Cancel");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        cancelClick(e);
      }
    });
    foundlist.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        foundlist_mouseClicked(e);
      }
    });
    jSplitPane1.setLeftComponent(leftlistpane);
    jSplitPane1.setRightComponent(entitypanel);
    this.getContentPane().add(toppanel, BorderLayout.NORTH);
    toppanel.add(jLabel1, null);
    toppanel.add(findtext, null);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
//    jPanel2.add(leftlistpane, BorderLayout.WEST);
//    jPanel2.add(entitypanel, BorderLayout.NORTH);
    mainPanel.add(jSplitPane1, BorderLayout.CENTER);
    this.getContentPane().add(buttonpanel, BorderLayout.SOUTH);
    buttonpanel.add(OK, null);
    buttonpanel.add(jButton2, null);
    leftlistpane.getViewport().setView(foundlist);
    jSplitPane1.setDividerLocation(170);
  }

	Vector found=new Vector();
	String searchstring;

  void findtext_actionPerformed(ActionEvent e) {
	synchronized(found){
		searchstring=findtext.getText();
		//found=new Vector();
                found.removeAllElements();
		foundlist.setListData(found);
	}
	EntitySearcher es=new EntitySearcher(searchstring, anyNode, this);
	es.start();
  }
	public void actionPerformed(ActionEvent e){
		if(e.getSource()!=null && e.getActionCommand()!=null){
			found.add(e.getSource());
			redisplay();
		}
	}

	void redisplay(){
		foundlist.setListData(found);
	}

	Entity anyNode;	//root node to begin search




	/**
	 When a found item is selected, display it in the entity panel
	*/
  void foundlist_valueChanged(ListSelectionEvent e) {
	entitypanel.setEntity((Entity)foundlist.getSelectedValue());
  }

	Entity entity;
  JSplitPane jSplitPane1 = new JSplitPane();
  void cancelClick(ActionEvent e) {
	entity=null;
	hide();
  }

  void OKclick(ActionEvent e) {
	entity=(Entity)foundlist.getSelectedValue();
	hide();
  }

  void foundlist_mouseClicked(MouseEvent e) {
	if(e.getClickCount()==2){
          entity=(Entity)foundlist.getSelectedValue();
          entityDoubleClicked();
	}
  }
  public void entityDoubleClicked(){
		hide();

  }

}

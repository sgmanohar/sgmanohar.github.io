
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class NavButton extends JButton implements ActionListener{
	Entity ent; NavigatorPanel nav;
  JPopupMenu popup = new JPopupMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem jMenuItem2 = new JMenuItem();
	public NavButton(Entity e, NavigatorPanel n){
		super(e.toString());
		    try {
		      jbInit();
		    }
		    catch(Exception ex) {
		      ex.printStackTrace();
		    }
		ent=e; nav=n;
		addActionListener(this);
			//listen for right clicks
		addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				if(e.getModifiers()==MouseEvent.BUTTON3_MASK){
					rightclick();
				}
			}
		});
	}


	public void actionPerformed(ActionEvent e){
		nav.setEntity(ent);
	}

	void rightclick(){
		popup.show(this, 0, getHeight());
	}


  private void jbInit() throws Exception {
    jMenuItem1.setText("Delete");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        deleteclick(e);
      }
    });
    jMenuItem2.setFont(new java.awt.Font("Dialog", 1, 12));
    jMenuItem2.setText("Go to");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        go(e);
      }
    });
    popup.add(jMenuItem2);
    popup.addSeparator();
    popup.add(jMenuItem1);
  }

  void deleteclick(ActionEvent e) {
	Container c=getParent();
	c.remove(this);
	c.doLayout();
  }


  void go(ActionEvent e) {
	nav.setEntity(ent);
  }
}

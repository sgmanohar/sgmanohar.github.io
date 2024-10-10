
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import com.cudos.common.*;

public class MechanicsDisplay extends JPanel {

  public MechanicsDisplay() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	MechanicsData data;
	public void setData(MechanicsData d){
		data=d;repaint();
		setSelected(null);
	}
	public MechanicsData getData(){return data;}

	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(4));

		if(data!=null){
			BaseComponent[] c=data.getAllComponents();
			for(int i=0;i<c.length;i++){
				if(c[i].isSelected()) g.setColor(Color.red);
				else g.setColor(Color.black);
				c[i].paint(g);
			}
		}
	}

  private void jbInit() throws Exception {
    this.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });
    this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

      public void mouseDragged(MouseEvent e) {
        this_mouseDragged(e);
      }
    });
    this.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        this_mousePressed(e);
      }

      public void mouseReleased(MouseEvent e) {
        this_mouseReleased(e);
      }
    });
  }


	Point lastMouse;
	boolean tempfixed;
	void this_mousePressed(MouseEvent e) {
		Point p=e.getPoint();
		lastMouse=p;
		Node[] n=data.getNodes();
		for(int i=0;i<n.length;i++){
			if(n[i].contains(p)){
				setSelected(n[i]);
				tempfixed=n[i].fixed;
				n[i].fixed=true;
				return;
			}
		}
		MechanicsComponent[] c=data.getComponents();
		for(int i=0;i<c.length;i++){
			if(c[i].contains(p)){
				setSelected(c[i]);
				return;
			}
		}
		setSelected(null);
	}
	void this_mouseReleased(MouseEvent e) {
		if(getSelected() instanceof Node){
			Node n=(Node)getSelected();
			n.fixed=tempfixed;
		}
	}

	void this_mouseDragged(MouseEvent e) {
		if(getSelected()!=null){
			if(getSelected() instanceof Node){
				Node n=(Node)getSelected();
				n.setY(n.getY()+e.getY()-lastMouse.y);
			}else if(getSelected() instanceof MechanicsComponent){
				MechanicsComponent c=(MechanicsComponent)getSelected();
				c.x+=e.getX()-lastMouse.x;
				c.top.setPos(); c.bottom.setPos();
			}
			repaint();
		}
		lastMouse=e.getPoint();
	}

	BaseComponent selectedComponent;
	public SelectionRecipient selectionListener;
	public void setSelectionRecipient(SelectionRecipient r){selectionListener=r;}
	public SelectionRecipient getSelectionRecipient(){return selectionListener;}
	public void setSelected(BaseComponent c){
		if(selectedComponent!=null) selectedComponent.setSelected(false);
		selectedComponent=c;
		if(c!=null) c.setSelected(true);
		selectionListener.setSelected(c);
		repaint();
	}
	public BaseComponent getSelected(){return selectedComponent;}

  void this_componentResized(ComponentEvent e) {
	data.getRoot().x=getWidth()/2;
	repaint();
  }


}

package com.cudos.common;

import javax.swing.JPanel;
//import java.awt.dnd.*;

public class DraggableContainer extends JPanel  {
	public void destroyDraggable(DraggableComponent d){
		remove(d);
	}
	public void addDraggable(DraggableComponent d){
		add(d);
	}



  public DraggableContainer() {
	setLayout(null);
  }
}
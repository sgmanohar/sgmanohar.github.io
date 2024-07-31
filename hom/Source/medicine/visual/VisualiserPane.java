
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.visual;
import medicine.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class VisualiserPane extends JPanel implements ActionListener{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JRadioButton causation = new JRadioButton();
  JRadioButton jRadioButton2 = new JRadioButton();
  ButtonGroup bg1 = new ButtonGroup();
  GraphicPane graphicPane = new GraphicPane();
  JScrollBar depthscroll = new JScrollBar();
  JLabel depthtext = new JLabel();
  JButton jButton1 = new JButton();
  JButton dirbutton = new JButton();

  int dirbuttonstate=1;
  public VisualiserPane() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static final String[] dirnames={"Causes","Effects","Both","Superclasses","Subtypes","Both"};
  void updatedirbutton(){
    if(causation.isSelected())dirbutton.setText(dirnames[dirbuttonstate-1]);
    else dirbutton.setText(dirnames[dirbuttonstate+2]);
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    causation.setText("Causation");
    jRadioButton2.setText("Hierarchy");
    depthscroll.setVisibleAmount(1);
    depthscroll.setValue(1);
    depthscroll.setOrientation(JScrollBar.HORIZONTAL);
    depthscroll.setMaximum(6);
    depthscroll.setBlockIncrement(1);
    depthscroll.setToolTipText("Depth of tree to be shown");
    depthscroll.setPreferredSize(new Dimension(50, 16));
    depthscroll.addAdjustmentListener(new java.awt.event.AdjustmentListener() {

      public void adjustmentValueChanged(AdjustmentEvent e) {
        depthscroll_adjustmentValueChanged(e);
      }
    });
    depthtext.setFont(new java.awt.Font("Dialog", 1, 12));
    depthtext.setPreferredSize(new Dimension(30, 17));
    depthtext.setText("0");
    jButton1.setToolTipText("");
    jButton1.setText("Adjust...");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    dirbutton.setText("Direction");
    dirbutton.addActionListener(new VisualiserPane_dirbutton_actionAdapter(this));
    this.add(jPanel1, BorderLayout.NORTH);
    bg1.add(causation);
    bg1.add(jRadioButton2);
    causation.addActionListener(this);
    jRadioButton2.addActionListener(this);
    jPanel1.add(causation, null);
    jPanel1.add(jRadioButton2, null);
    jPanel1.add(depthscroll, null);
    jPanel1.add(depthtext, null);
    jPanel1.add(dirbutton, null);
    jPanel1.add(jButton1, null);
    causation.setSelected(true);
    this.add(graphicPane, BorderLayout.CENTER);
  }

	public Entity getEntity(){
		return graphicPane.getEntity();
	}
	public void setEntity(Entity e){
		graphicPane.setEntity(e);
	}

	public void actionPerformed(ActionEvent e){
		changeGraphic();
	}
	void changeGraphic(){
		int depth=depthscroll.getValue();
		if(causation.isSelected()){
			graphicPane.setRange(depth,0);
		}else{
			graphicPane.setRange(0,depth);
		}
	}

  void depthscroll_adjustmentValueChanged(AdjustmentEvent e) {
	depthtext.setText(String.valueOf(depthscroll.getValue()));
	changeGraphic();
  }

  void jButton1_actionPerformed(ActionEvent e) {
	new AdjustDiagramDialog(graphicPane).show();
  }

  void dirbutton_actionPerformed(ActionEvent e) {
    dirbuttonstate=1+(dirbuttonstate%3);
    updatedirbutton();
    graphicPane.setDirection((dirbuttonstate%2)>0,(dirbuttonstate>>1)>0);
  }

}

class VisualiserPane_dirbutton_actionAdapter implements java.awt.event.ActionListener {
  VisualiserPane adaptee;

  VisualiserPane_dirbutton_actionAdapter(VisualiserPane adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dirbutton_actionPerformed(e);
  }
}


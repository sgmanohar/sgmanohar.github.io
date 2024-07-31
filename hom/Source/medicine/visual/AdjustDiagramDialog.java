
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AdjustDiagramDialog extends JDialog {
  JButton jButton1 = new JButton();
  JPanel jPanel1 = new JPanel();
  JButton jButton2 = new JButton();
  JPanel jPanel2 = new JPanel();
  Box box1;
  JLabel jLabel1 = new JLabel();
  JScrollBar conscroll = new JScrollBar();
  Box box2;
  JLabel jLabel2 = new JLabel();
  JScrollBar repscroll = new JScrollBar();
  Component component1;
  Component component2;
  JLabel contxt = new JLabel();
  JLabel reptxt = new JLabel();
  Box box3;
  JLabel jLabel5 = new JLabel();
  JScrollBar sprscroll = new JScrollBar();
  JLabel sprtxt = new JLabel();
  Component component3;
  FlowLayout flowLayout1 = new FlowLayout();

  public AdjustDiagramDialog() {
	init();
  }
  public AdjustDiagramDialog(GraphicPane g){
	pane=g;
	getvalues();
	init();
  }
  void init(){
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	setModal(false);
	setSize(300, 200);
  }

  private void jbInit() throws Exception {
    box1 = Box.createHorizontalBox();
    box2 = Box.createHorizontalBox();
    component1 = Box.createHorizontalStrut(8);
    component2 = Box.createHorizontalStrut(8);
    box3 = Box.createHorizontalBox();
    component3 = Box.createHorizontalStrut(8);
    jButton1.setText("Close");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("Defaults");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jLabel1.setText("Connector length");
    conscroll.setOrientation(JScrollBar.HORIZONTAL);
    conscroll.setMaximum(200);
    conscroll.setPreferredSize(new Dimension(88, 16));
    conscroll.addAdjustmentListener(new java.awt.event.AdjustmentListener() {

      public void adjustmentValueChanged(AdjustmentEvent e) {
        scrollchange(e);
      }
    });
    jLabel2.setText("Repulsion");
    repscroll.setPreferredSize(new Dimension(88, 16));
    repscroll.addAdjustmentListener(new java.awt.event.AdjustmentListener() {

      public void adjustmentValueChanged(AdjustmentEvent e) {
        scrollchange(e);
      }
    });
    repscroll.setOrientation(JScrollBar.HORIZONTAL);
    repscroll.setMaximum(500);
    contxt.setBorder(BorderFactory.createLoweredBevelBorder());
    contxt.setPreferredSize(new Dimension(40, 21));
    contxt.setText("0");
    reptxt.setBorder(BorderFactory.createLoweredBevelBorder());
    reptxt.setPreferredSize(new Dimension(40, 21));
    reptxt.setText("0");
    jLabel5.setText("Spring constant");
    sprscroll.setOrientation(JScrollBar.HORIZONTAL);
    sprscroll.setPreferredSize(new Dimension(88, 16));
    sprscroll.addAdjustmentListener(new java.awt.event.AdjustmentListener() {

      public void adjustmentValueChanged(AdjustmentEvent e) {
        scrollchange(e);
      }
    });
    sprtxt.setBorder(BorderFactory.createLoweredBevelBorder());
    sprtxt.setPreferredSize(new Dimension(40, 21));
    sprtxt.setText("0");
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jButton1, null);
    jPanel1.add(jButton2, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(box1, null);
    box1.add(jLabel1, null);
    box1.add(component1, null);
    box1.add(conscroll, null);
    box1.add(contxt, null);
    jPanel2.add(box2, null);
    box2.add(jLabel2, null);
    box2.add(component2, null);
    box2.add(repscroll, null);
    box2.add(reptxt, null);
    jPanel2.add(box3, null);
    box3.add(jLabel5, null);
    box3.add(component3, null);
    box3.add(sprscroll, null);
    box3.add(sprtxt, null);
  }

  void scrollchange(AdjustmentEvent e) {
	update();
  }

	GraphicPane pane;


	public void update(){
		int c=conscroll.getValue();
		contxt.setText(String.valueOf(c));
		pane.defaultLength=c;

		int r=repscroll.getValue();
		reptxt.setText(String.valueOf(r));
		pane.repulsion=r;

		int s=sprscroll.getValue();
		sprtxt.setText(String.valueOf(s));
		pane.rate=s/100.;
	}
	public void getvalues(){
		conscroll.setValue((int)(pane.defaultLength));
		repscroll.setValue((int)(pane.repulsion));
		sprscroll.setValue((int)(pane.rate*100));
	}
	public void defaults(){
		conscroll.setValue(70);
		repscroll.setValue(100);
		sprscroll.setValue(30);
	}

  void jButton1_actionPerformed(ActionEvent e) {
	hide();
  }

  void jButton2_actionPerformed(ActionEvent e) {
	defaults();
  }
}
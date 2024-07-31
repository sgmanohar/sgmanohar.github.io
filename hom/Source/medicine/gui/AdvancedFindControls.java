
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohquar
 * @version 1.0
 */
package medicine.gui;
import medicine.*;

import javax.swing.*;
import java.awt.*;


public class AdvancedFindControls extends JPanel {
  JCheckBox relatedcheck = new JCheckBox();
  JPanel jPanel4 = new JPanel();
  JLabel currententity = new JLabel();
  JCheckBox includesupercheck = new JCheckBox();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JRadioButton effects = new JRadioButton();
  JRadioButton subclasses = new JRadioButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JRadioButton causes = new JRadioButton();
  JRadioButton superclasses = new JRadioButton();
  JPanel jPanel6 = new JPanel();
  JCheckBox numcheck = new JCheckBox();
  JPanel jPanel2 = new JPanel();
  JTextField numentitiestext = new JTextField();
  JCheckBox textcheck = new JCheckBox();
  JTextField findtext = new JTextField();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  ButtonGroup bg = new ButtonGroup();
  JRadioButton exact = new JRadioButton();
  FlowLayout flowLayout2 = new FlowLayout();
  JRadioButton contains = new JRadioButton();
  ButtonGroup bg2 = new ButtonGroup();
  JRadioButton ignorecase = new JRadioButton();
  JRadioButton casesensitive = new JRadioButton();
  JRadioButton synonyms = new JRadioButton();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  ButtonGroup bg3 = new ButtonGroup();


  public AdvancedFindControls() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	bg2.add(contains); bg2.add(exact);
  }

  private void jbInit() throws Exception {
    relatedcheck.setText("Related to");
    currententity.setBorder(BorderFactory.createLoweredBevelBorder());
    currententity.setText("selected");
    includesupercheck.setText("including superclasses");
    jPanel4.setBorder(BorderFactory.createEtchedBorder());
    this.setLayout(borderLayout1);
    effects.setText("Effects");
    subclasses.setToolTipText("");
    subclasses.setText("Subclasses");
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(1);
    flowLayout1.setVgap(1);
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setPreferredSize(new Dimension(120, 110));
    jPanel1.setLayout(flowLayout1);
    causes.setText("Causes");
    superclasses.setText("Superclasses");
    numcheck.setText("Number of items");
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    numentitiestext.setPreferredSize(new Dimension(40, 21));
    numentitiestext.setText("0");
    textcheck.setText("Text: ");
    findtext.setPreferredSize(new Dimension(120, 21));
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jPanel3.setPreferredSize(new Dimension(200, 95));
    jPanel3.setLayout(flowLayout2);
    jPanel5.setLayout(borderLayout2);
    jPanel6.setPreferredSize(new Dimension(100, 49));
    exact.setText("exact");
    contains.setSelected(true);
    contains.setText("contains");
    ignorecase.setSelected(true);
    ignorecase.setText("ignore");
    casesensitive.setText("sensitive");
    synonyms.setText("Synonyms");
    jPanel7.setLayout(borderLayout3);
    jPanel8.setLayout(borderLayout4);
    jLabel1.setText("Case");
    borderLayout4.setHgap(5);
    borderLayout4.setVgap(5);
    this.setPreferredSize(new Dimension(304, 200));
    this.add(jPanel5, BorderLayout.CENTER);
    jPanel5.add(jPanel1, BorderLayout.WEST);
    jPanel1.add(causes, null);
    jPanel1.add(effects, null);
    jPanel1.add(superclasses, null);
    jPanel1.add(subclasses, null);
    jPanel1.add(synonyms, null);
    jPanel5.add(jPanel6, BorderLayout.CENTER);
    jPanel6.add(jPanel3, null);
    jPanel3.add(textcheck, null);
    jPanel3.add(findtext, null);
    jPanel3.add(jPanel7, null);
    jPanel7.add(contains, BorderLayout.CENTER);
    jPanel7.add(exact, BorderLayout.EAST);
    jPanel3.add(jPanel8, null);
    jPanel8.add(casesensitive, BorderLayout.CENTER);
    jPanel8.add(ignorecase, BorderLayout.EAST);
    jPanel8.add(jLabel1, BorderLayout.WEST);
    jPanel6.add(jPanel2, null);
    jPanel2.add(numcheck, null);
    jPanel2.add(numentitiestext, null);
    this.add(jPanel4, BorderLayout.SOUTH);
    jPanel4.add(relatedcheck, null);
    jPanel4.add(currententity, null);
    jPanel4.add(includesupercheck, null);

	bg.add(subclasses);
	bg.add(superclasses);
	bg.add(causes);
	bg.add(effects);

	bg3.add(casesensitive);
	bg3.add(ignorecase);

  }
	public int getSelectedRelation(){
		if(causes.isSelected())return Entity.CAUSE;
		else if(effects.isSelected())return Entity.EFFECT;
		else if(subclasses.isSelected())return Entity.CHILD;
		else if(superclasses.isSelected())return Entity.PARENT;
		return -1;
	}
}

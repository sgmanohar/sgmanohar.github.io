/**
 * Displays the intravenous infusion graphically. Includes a syringe button
 * and a button to call up the pharmacy dialog box. Has a slider to control the
 * rate of infusion.
 */
package phic.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import phic.*;
import phic.common.UnitConstants;
import phic.drug.DrugContainer;

/**
 * This is a panel that is used to control the state of an intravenous infusion.
 * There is a button to switch the drip on an off, with an indicator of how full
 * the bag is. There is a slider to control the rate of infusion, displayed in
 * mL/min. The content of the bag can be ingested, or drugs may be added from
 * the Pharmacy.
 * @todo Implement the repeating infusion, which duplicates itself when it is
 * empty.
 */
public class InfusionPanel
    extends JPanel {
  Border border1;
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel namelabel = new JLabel();

  private Box box2;
  BoxLayout boxlayout;

  private JPanel jPanel1 = new JPanel();

  private JPanel jPanel2 = new JPanel();

  private JButton pharmacy = new JButton();

  private JButton eatbutton = new JButton();

  private JCheckBox continuous_check = new JCheckBox();

  private FlowLayout flowLayout2 = new FlowLayout();

  JPanel jPanel3 = new JPanel();

  JSlider rateslider = new JSlider();

  Box box1;

  JLabel ratelabel = new JLabel();

  BorderLayout borderLayout1 = new BorderLayout();

  Box fluidbox;
  /**
   * Construct a panel to represent the given infusion, with a given name.
   * @param i The infusion represented by this panel.
   * @param name A description of the contents of this infusion bag
   */

  public InfusionPanel(IntravenousInfusion i, String name) {
    this();
    this.infusion = i;
    namelabel.setText(name);
    rateslider.setValue( (int) (1000 * i.rate));
    syringebutton.setOpaque(false);
  }
  public void removeNotify(){
    timer.stop();
    super.removeNotify();
  }

  /** A timer that updates the button's graphics to represent the fullness of the drip */
  protected Timer timer = new Timer(500, new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      syringebutton.repaint();
    }
  });

  InfusionPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    syringebutton.setIcon(new ImageIcon(Resource.loader.getImageResource(
        "SyringeSmall.gif")));
    pharmacy.setIcon(new ImageIcon(Resource.loader.getImageResource(
        "Pharmacy.jpg")));
    eatbutton.setIcon(new ImageIcon(Resource.loader.getImageResource(
        "Mouth.gif")));
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.control, 2);
    fluidbox = Box.createVerticalBox();
    box2 = Box.createVerticalBox();
    //this.setPreferredSize(new Dimension(360, 65));
    box1 = Box.createVerticalBox();
    boxlayout = new BoxLayout(this,BoxLayout.X_AXIS);
    this.setLayout(boxlayout);
    flowLayout1.setHgap(2);
    flowLayout1.setVgap(2);
    syringebutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        syringebutton_actionPerformed(e);
      }
    });
    syringebutton.setMargin(new Insets(2, 2, 2, 2));
    syringebutton.setToolTipText("Infuse drug");
    syringebutton.setAlignmentX( (float) 0.5);
    syringebutton.setOpaque(false);
    namelabel.setAlignmentX( (float) 0.5);
    namelabel.setText("Fluid");
    pharmacy.setBackground(Color.white);
    pharmacy.setToolTipText("Add pharmaceuticals");
    pharmacy.setMargin(new Insets(2, 2, 2, 2));
    pharmacy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pharmacy_actionPerformed(e);
      }
    });
    eatbutton.setToolTipText("Ingest this fluid");
    eatbutton.setMargin(new Insets(0, 0, 0, 0));
    eatbutton.setText("");
    eatbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        eatbutton_actionPerformed(e);
      }
    });
    continuous_check.setToolTipText("Replace drips as soon as they finish");
    continuous_check.setText("Repeat");
    continuous_check.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        continuous_check_actionPerformed(e);
      }
    });
    jPanel1.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    rateslider.setValue(16);
    rateslider.setMaximum(30);
    rateslider.setMajorTickSpacing(10);
    rateslider.setPaintTicks(true);
    rateslider.setMinorTickSpacing(5);
    rateslider.setPaintLabels(true);
    rateslider.setToolTipText("Rate of infusion /ml per minute");
    rateslider.setPreferredSize(new Dimension(80, 50));
    rateslider.setMinimumSize(new Dimension(36, 50));
    rateslider.setLabelTable(rateslider.createStandardLabels(30));
    rateslider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        rateslider_stateChanged(e);
      }
    });
    ratelabel.setText("19 ml/min");
    jPanel3.setLayout(borderLayout1);
    jPanel3.setPreferredSize(new Dimension(80, 67));
    this.add(fluidbox, null);
    fluidbox.add(syringebutton, null);
    fluidbox.add(namelabel, null);
    jPanel1.add(eatbutton, null);
    jPanel1.add(pharmacy, null);
    jPanel2.add(continuous_check, null);
    box2.add(jPanel1, null);
    box2.add(jPanel2, null);
    this.add(jPanel3, null);
    jPanel3.add(box1, BorderLayout.CENTER);
    box1.add(rateslider, null);
    box1.add(ratelabel, null);
    this.add(box2, null);
  }

  public IntravenousInfusion infusion;

  /** Volume of fluid in litres that is displayed as a full syringe */

  final double maximumFluid = 1.0;

  /** The colour of the fluid-background of the syringe button */
  final Color fluidColour = Color.cyan;

  /** The syringe button which is painted according to what fraction of a litre remains */
  JToggleButton syringebutton = new JToggleButton() {
    public void paint(Graphics g) {
      super.paint(g);
      g.setColor(fluidColour);
      int w = (int) ( (getWidth() - 4) * (infusion.volume.get() / maximumFluid));
      g.fill3DRect(2, getHeight()-6, w, 4, this.isSelected());
    }
  };

  /** Change the rate if the slider has moved, and update the label */

  void rateslider_stateChanged(ChangeEvent e) {
    infusion.rate = rateslider.getValue() * 0.001;
    ratelabel.setText(UnitConstants.formatValue(infusion.rate,
                                                UnitConstants.LITRES_PER_MIN, true, false));
  }

  /** Turn the infusion on or off */
  void syringebutton_actionPerformed(ActionEvent e) {
    if (infusion.isRunning()) {
      infusion.stop();
    }
    else {
      infusion.start();
    }
    if (infusion.isRunning()) {
      timer.start();
    }
    else {
      timer.stop();
    }
  }

  /** Call up the pharmacy box, and if a drug was selected, add it to the infusion */
  void pharmacy_actionPerformed(ActionEvent e) {
    DrugDialog dd = new DrugDialog();
    dd.show();
    DrugContainer drugs = dd.getDrugContainer();
    if (drugs != null) { //was OK pressed?
      infusion.add(drugs);
      JLabel label = new JLabel("+" + dd.getDrugLabel());
      label.setAlignmentX(0.5f); //centre under previous label
      fluidbox.add(label);
      validate();
    }
  }

  /**
   * Drink the infusion! Stops the infusion, calls environment.actions.drink,
   * and then set the infusion's status to empty. The clear-up will be
   * performed by the Environment.intravenousInfusions() method on the
   * next cycle.
   */
  void eatbutton_actionPerformed(ActionEvent e) {
    infusion.stop();
    timer.stop();
    Current.environment.actions.drink(namelabel.getText(), infusion);
    infusion.empty = true;
  }

  void continuous_check_actionPerformed(ActionEvent e) {
    infusion.autoRefill = continuous_check.isSelected();
  }
}

package phic.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.Border;
import phic.Current;
import phic.common.*;

/**
 * Display a fluid balance sheet, whose values are taken from the
 * fluid events in the current body's event log.
 */
public class FluidBalanceDialog
    extends ModalDialog {
  JPanel jPanel1 = new JPanel();

  JPanel jPanel2 = new JPanel();

  JButton jButton1 = new JButton();

  Box box1;

  BorderLayout borderLayout1 = new BorderLayout();

  JPanel jPanel3 = new JPanel();

  JPanel jPanel4 = new JPanel();

  Border border1;

  BorderLayout borderLayout2 = new BorderLayout();

  BorderLayout borderLayout3 = new BorderLayout();

  JLabel jLabel1 = new JLabel();

  JLabel jLabel2 = new JLabel();

  Box box2;

  JTextField intotal = new JTextField();

  JPanel jPanel5 = new JPanel();

  BorderLayout borderLayout4 = new BorderLayout();

  JScrollPane jScrollPane1 = new JScrollPane();

  JTextField outtotal = new JTextField();

  Box box3;

  Box box4;

  Box box5;

  JPanel jPanel6 = new JPanel();

  JPanel jPanel7 = new JPanel();

  JList inlist = new JList();

  JList outlist = new JList();

  BorderLayout borderLayout5 = new BorderLayout();

  BorderLayout borderLayout6 = new BorderLayout();

  private JRadioButton radioday = new JRadioButton();

  private JRadioButton radioweek = new JRadioButton();

  private ButtonGroup buttonGroup1 = new ButtonGroup();

  public FluidBalanceDialog() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    buttonGroup1.add(radioday);
    buttonGroup1.add(radioweek);
    fillOut();
    setPreferredSize(new Dimension(600, 270));
    getRootPane().setDefaultButton(jButton1);
  }

  private void jbInit() throws Exception {
    box1 = Box.createHorizontalBox();
    box2 = Box.createHorizontalBox();
    box3 = Box.createHorizontalBox();
    this.setTitle("Fluid balance");
    border1 = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    jButton1.setToolTipText("");
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel2.setLayout(borderLayout1);
    jPanel2.setBorder(border1);
    jPanel3.setLayout(borderLayout2);
    jPanel4.setLayout(borderLayout3);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel1.setPreferredSize(new Dimension(51, 21));
    jLabel1.setText("Fluid in");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel2.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel2.setText("Fluid out");
    intotal.setFont(new java.awt.Font("SansSerif", 1, 12));
    intotal.setPreferredSize(new Dimension(32, 21));
    intotal.setText("0.0 L");
    intotal.setHorizontalAlignment(SwingConstants.RIGHT);
    jPanel5.setLayout(borderLayout4);
    outtotal.setFont(new java.awt.Font("SansSerif", 1, 12));
    outtotal.setPreferredSize(new Dimension(32, 21));
    outtotal.setText("0.0 L");
    outtotal.setHorizontalAlignment(SwingConstants.RIGHT);
    inlist.setBackground(new Color(175, 175, 255));
    inlist.setBorder(BorderFactory.createLoweredBevelBorder());
    outlist.setBackground(Color.pink);
    outlist.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel6.setLayout(borderLayout5);
    jPanel7.setLayout(borderLayout6);
    radioday.setText("Day");
    radioday.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        radioday_actionPerformed(e);
      }
    });
    radioday.setSelected(true);
    radioweek.setText("Week");
    radioweek.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        radioweek_actionPerformed(e);
      }
    });
    jPanel8.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    jPanel1.add(radioweek, null);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(radioday, null);
    jPanel1.add(jButton1, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(box1, BorderLayout.NORTH);
    box1.add(jPanel3, null);
    jPanel3.add(jLabel1, BorderLayout.NORTH);
    box1.add(jPanel4, null);
    jPanel4.add(jLabel2, BorderLayout.NORTH);
    jPanel2.add(box2, BorderLayout.SOUTH);
    box2.add(intotal, null);
    box2.add(outtotal, null);
    jPanel2.add(jPanel5, BorderLayout.CENTER);
    jPanel5.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jPanel8, null);
    jPanel8.add(jPanel6, null);
    jPanel6.add(inlist, BorderLayout.CENTER);
    jPanel8.add(jPanel7, null);
    jPanel7.add(outlist, BorderLayout.CENTER);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    hide();
  }

  /**
   * This fills out the two lists In and Out, using the data from
   * Current.body.eventLog. It uses data for the last day or week, as specified
   * by historyLength. It also fills in the subtotals underneath the lists.
   *
   * @todo Insensible losses are not logged.
   * @todo Fluid volume of stool and food should exclude solids.
   */
  private void fillOut() {
    EventLog log = Current.body.eventLog;
    Clock clock = Current.body.getClock();
    Vector v = log.getEventsOfType(EventLog.FLUID_EVENT);
    Vector inv = new Vector(), outv = new Vector();
    double totalin = 0, totalout = 0;
    /** get details from EventLog */
    long currentTime = clock.getTime();
    for (int i = 0; i < v.size(); i++) {
      EventLog.Event e = (EventLog.Event) v.get(i);
      //skip events that happened before the desired history
      if (e.time < currentTime - historyLength) {
        continue;
      }
      if (e.type == EventLog.FLUID_EVENT) {
        double amt=Double.NaN;
        try{amt = ( (Double) e.parameter).doubleValue();
        }catch(Exception x){ System.out.println(e.parameter+" is not a double"); x.printStackTrace();}
        if (amt >= 0) { //in
          totalin += amt;
          inv.add(clock.getTimeAsString(e.time,
                                        Clock.DATETIME) + ": " + e.name + " " +
                  UnitConstants.formatValue(amt,
                                            UnitConstants.LITRES,true));
          outv.add(" ");
        }
        else { //out
          totalout += amt;
          outv.add(clock.getTimeAsString(e.time,
                                         Clock.DATETIME) + ": " + e.name + " " +
                   UnitConstants.formatValue(amt,
                                             UnitConstants.LITRES,true ));
          inv.add(" ");
        }
      }
    }
    /** Add insensible losses */
    inlist.setListData(inv);
    outlist.setListData(outv);
    intotal.setText(UnitConstants.formatValue(totalin, UnitConstants.LITRES,true));
    outtotal.setText(UnitConstants.formatValue(totalout, UnitConstants.LITRES,true));
  }

  public static final long DAY = 1000 * 60 * 60 * 24, WEEK = DAY * 7;

  public long historyLength = DAY;
  JPanel jPanel8 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();

  private void radioday_actionPerformed(ActionEvent e) {
    historyLength = DAY;
    fillOut();
  }

  private void radioweek_actionPerformed(ActionEvent e) {
    historyLength = WEEK;
    fillOut();
  }
}

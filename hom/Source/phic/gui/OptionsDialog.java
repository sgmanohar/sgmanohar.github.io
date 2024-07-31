package phic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import phic.Current;
import phic.common.ConsciousLevelOptions;
import javax.swing.event.*;
import phic.common.Organ;
import phic.common.CommonThread;
/**
 * Display a set of options for the interface.
 */
public class OptionsDialog extends ModalDialog {
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JButton okbutton = new JButton();
    JButton cancelbutton = new JButton();
    BorderLayout borderLayout1 = new BorderLayout();
    JTabbedPane jTabbedPane1 = new JTabbedPane();
    JPanel jPanel3 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JCheckBox flashcheck = new JCheckBox();
    JCheckBox outOfRangeCheck = new JCheckBox();
  JCheckBox suppressMessages = new JCheckBox();
  JPanel jPanel4 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JCheckBox eating = new JCheckBox();
  JCheckBox drinking = new JCheckBox();
  JCheckBox warnunc = new JCheckBox();
  JTextField uncWarnTime = new JTextField();
  JLabel jLabel6 = new JLabel();
  JCheckBox jCheckBox6 = new JCheckBox();
  JCheckBox warnarrest = new JCheckBox();
  JCheckBox disabledeath = new JCheckBox();
  JCheckBox jCheckBox9 = new JCheckBox();
  JCheckBox unitOverride = new JCheckBox();
  JCheckBox shortnamescheck = new JCheckBox();
  JCheckBox horizontal = new JCheckBox();
  JCheckBox breathingnorm = new JCheckBox();
  JPanel jPanel5 = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JCheckBox antiAlias = new JCheckBox();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField rewindtimefield = new JTextField(4);
  JCheckBox scrollhistorychk = new JCheckBox();
  JCheckBox extendhistorychk = new JCheckBox();
  JCheckBox allowRewindChk = new JCheckBox();
  JLabel jLabel1 = new JLabel();
  JSlider thickslider = new JSlider();
  JLabel thicktxt = new JLabel();
  JCheckBox overlaygraphcheck = new JCheckBox();
  JLabel jLabel2 = new JLabel();
  JTextField delaytxt = new JTextField();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JTextField maxtimetxt = new JTextField();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JTextField tsmoothtxt = new JTextField();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JTextField graphtimetxt = new JTextField();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JTextField slidertimetxt = new JTextField();
  JLabel jLabel14 = new JLabel();
  JCheckBox mousedrawcheck = new JCheckBox();
  JCheckBox randseedchk = new JCheckBox();
  JTextField randseedtxt = new JTextField();
    public OptionsDialog() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getRootPane().setDefaultButton(okbutton);
        getOptions();
        setPreferredSize(new Dimension(350, 370));
    }

    private void jbInit() throws Exception {
        this.setTitle("Interface Options");
        okbutton.setText("OK");
        okbutton.addActionListener(new
                InterfaceOptionsDialog_okbutton_actionAdapter(this));
        cancelbutton.setText("Cancel");
        cancelbutton.addActionListener(new
                InterfaceOptionsDialog_cancelbutton_actionAdapter(this));
        jPanel2.setLayout(borderLayout1);
        jPanel3.setLayout(gridBagLayout1);
        flashcheck.setAlignmentX((float) 1.0);
        flashcheck.setAlignmentY((float) 0.5);
        flashcheck.setToolTipText(
                "Indicates whether the value of variables in the scrolling graph will " +
                "flash when they go out of range");
        flashcheck.setText("Enable flashing when out of range");
        outOfRangeCheck.setAlignmentX((float) 1.0);
        outOfRangeCheck.setToolTipText(
                "Does the list of out-of-range values at the bottom show only the " +
                "most important variables");
        outOfRangeCheck.setText("Only relevant out-of-range values shown");
    suppressMessages.setToolTipText("When the time compression is high, do not display eating, drinking, " +
    "urine and defaecation messages in the console");
    suppressMessages.setText("Suppress messages at faster clock speeds");
    jPanel4.setLayout(gridBagLayout2);
    eating.setText("Stop eating when unconscious");
    drinking.setText("Stop drinking when unconscious");
    warnunc.setText("Warn if unconscious for more than");
    uncWarnTime.setText("8");
    uncWarnTime.setColumns(5);
    uncWarnTime.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setText("hours");
    jCheckBox6.setText("Assume previous posture when regaining consciousness");
    warnarrest.setToolTipText("");
    warnarrest.setSelected(false);
    warnarrest.setText("Warn before cardiac arrest");
    disabledeath.setText("Disable death");
    jCheckBox9.setToolTipText("If this is selected, then display the variable editor for the selected " +
    "item at the bottom of the tree");
    jCheckBox9.setText("Show editor for selected variable");
    unitOverride.setText("Override internal units");
    shortnamescheck.setText("Short names for out-of-range variables");
    horizontal.setText("Make horizontal when unconscious");
    breathingnorm.setText("Resume normal breathing when unconscious");
    jPanel5.setLayout(gridBagLayout3);
    antiAlias.setText("Anti-alias graph lines");
    jLabel3.setText("seconds");
    jLabel4.setText("Store rewind data every");
    jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    rewindtimefield.setHorizontalAlignment(SwingConstants.TRAILING);
    rewindtimefield.setColumns(5);
    rewindtimefield.setText("3");
    rewindtimefield.setToolTipText("Enter the number of seconds between successive stores for rewinding");
    scrollhistorychk.setText("Store scrolling graph history");
    extendhistorychk.setText("Keep scrolling history after resetting");
    allowRewindChk.setText("Allow Rewind to previous time");
    allowRewindChk.addActionListener(new InterfaceOptionsDialog_allowRewindChk_actionAdapter(this));
    jLabel1.setText("Graph line thickness");
    thicktxt.setText("0");
    thickslider.setMaximum(6);
    thickslider.setMinimumSize(new Dimension(45, 24));
    thickslider.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){
        thicktxt.setText(String.valueOf(thickslider.getValue()));
    } });
    overlaygraphcheck.setText("Variable names and values drawn on top of graph");
    jLabel2.setText("Simulation frame delay");
    delaytxt.setText("0");
    delaytxt.setColumns(5);
    delaytxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel5.setText("ms");
    jLabel7.setText("Maximum simulation time step");
    maxtimetxt.setText("120");
    maxtimetxt.setColumns(5);
    maxtimetxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel8.setText("seconds");
    jLabel9.setText("Time smoothing");
    tsmoothtxt.setRequestFocusEnabled(true);
    tsmoothtxt.setText("0.9");
    tsmoothtxt.setColumns(5);
    tsmoothtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel10.setText("(0 to 1)");
    jLabel11.setText("Graph update interval");
    graphtimetxt.setText("50");
    graphtimetxt.setColumns(5);
    graphtimetxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel12.setText("ms");
    jLabel13.setText("Slider update interval");
    slidertimetxt.setText("200");
    slidertimetxt.setColumns(5);
    slidertimetxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel14.setText("ms");
    mousedrawcheck.setText("Allow mouse drawing on graph");
    randseedchk.setText("Use fixed seed for random numbers");
    randseedtxt.setText("");
    randseedtxt.setColumns(5);
    randseedtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(okbutton, null);
        jPanel1.add(cancelbutton, null);
        this.getContentPane().add(jPanel2, BorderLayout.CENTER);
        jPanel2.add(jTabbedPane1, BorderLayout.CENTER);
        jTabbedPane1.add(jPanel3, "Basic options");
    jPanel3.add(unitOverride,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(shortnamescheck,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(suppressMessages,       new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 9), 0, 0));
    jPanel3.add(jCheckBox9,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(flashcheck,     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
    jPanel3.add(outOfRangeCheck,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(jLabel1,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(thickslider,  new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(thicktxt,  new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(overlaygraphcheck,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(mousedrawcheck,  new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(randseedchk,  new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jTabbedPane1.add(jPanel4,  "Unconscious");
    jPanel4.add(eating,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(drinking,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(warnunc,      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(uncWarnTime,      new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(jLabel6,        new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    jPanel4.add(warnarrest,       new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(jCheckBox6,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(disabledeath,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(horizontal,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel4.add(breathingnorm,   new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jTabbedPane1.add(jPanel5,  "Performance");
    jPanel5.add(jLabel4,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(rewindtimefield,        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel3, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(antiAlias,     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(scrollhistorychk,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(extendhistorychk,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(allowRewindChk,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel2,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(delaytxt,  new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel5,  new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel7,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(maxtimetxt,  new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel8, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel9,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(tsmoothtxt,  new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel10,  new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel11,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(graphtimetxt,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel12,  new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel13,   new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(slidertimetxt,  new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel5.add(jLabel14,  new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(randseedtxt,  new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    void okbutton_actionPerformed(ActionEvent e) {
      boolean nfe =false; //number format exception thrown?
      try{
        setOptions();
      }catch(NumberFormatException x){
        JOptionPane.showMessageDialog(this,
         "A value entered was out of range. Please correct it, or press cancel to keep original value.",
         "Number format error", JOptionPane.ERROR_MESSAGE );
        x.printStackTrace();
        nfe=true;
      }
      if(!nfe) hide();
    }

    private void setOptions() {
        SimplePhicFrame f=(SimplePhicFrame)PhicApplication.frame.getJFrame();
        f.graph.flashIfOutOfRange = flashcheck.isSelected();
        f.rewindDialog.setSaveInterval( (int)(Math.max(1000*Double.parseDouble(rewindtimefield.getText()), 300)) );
        f.showSelectedVariableEditor=jCheckBox9.isSelected();
        LimitChecker.showOnlyVisibleNodes = outOfRangeCheck.isSelected();
        phic.common.UnitConstants.setUseUnitOverrides(unitOverride.isSelected());
        LimitChecker.useShortNames=shortnamescheck.isSelected();
        Current.body.setLogRoutineEventsIfFast(!suppressMessages.isSelected());
        f.graph.mainpane.antiAlias=antiAlias.isSelected();
        f.graph.mainpane.thickness = thickslider.getValue();
        f.graph.mainpane.overlayGraphVariables = overlaygraphcheck.isSelected();
        f.graph.mainpane.allowDrawOnGraph = mousedrawcheck.isSelected();
        Organ.useRandomSeed = randseedchk.isSelected();
        Organ.randomSeed    = Long.parseLong(randseedtxt.getText());
        /** Unconsciousness */
        ConsciousLevelOptions u = Current.body.brain.getUnconscious();
        u.eat=!eating.isSelected();
        u.drink=!drinking.isSelected();
        u.flat=horizontal.isSelected();
        u.normalBreathing=breathingnorm.isSelected();
        u.notify=warnunc.isSelected();
        u.notifyHours=Double.parseDouble( uncWarnTime.getText() );
        f.graph.setEnableHistory(scrollhistorychk.isSelected());
        f.graph.enableWholeSessionHistory = extendhistorychk.isSelected();
        f.setRewindEnabled(allowRewindChk.isSelected());
        u.disableDeath = disabledeath.isSelected();

        CommonThread t=Current.thread;
        t.CYCLE_LENGTH_MILLIS = (int)Double.parseDouble(delaytxt.getText());
        t.MAXIMUM_ELAPSED_SECONDS = (int)Double.parseDouble(maxtimetxt.getText());
        t.TIME_SMOOTHING = Double.parseDouble(tsmoothtxt.getText());
        f.graph.setTimer( Integer.parseInt(graphtimetxt.getText()) );
        HorizontalBar.BAR_UPDATE_INTERVAL = (int)Double.parseDouble(slidertimetxt.getText());
    }

    private void getOptions() {
        SimplePhicFrame f=(SimplePhicFrame)PhicApplication.frame.getJFrame();
        flashcheck.setSelected(f.graph.flashIfOutOfRange);
        rewindtimefield.setText(String.valueOf(f.rewindDialog.getSaveInterval()/1000.));
        jCheckBox9.setSelected(f.showSelectedVariableEditor);
        outOfRangeCheck.setSelected(LimitChecker.showOnlyVisibleNodes);
        unitOverride.setSelected(phic.common.UnitConstants.getUseUnitOverrides());
        shortnamescheck.setSelected(LimitChecker.useShortNames);
        suppressMessages.setSelected(!phic.Current.body.getLogRoutineEventsIfFast());
        antiAlias.setSelected(f.graph.mainpane.antiAlias);
        thickslider.setValue(f.graph.mainpane.thickness);
        thicktxt.setText(Integer.toString(thickslider.getValue()));
        overlaygraphcheck.setSelected(f.graph.mainpane.overlayGraphVariables);
        mousedrawcheck.setSelected(f.graph.mainpane.allowDrawOnGraph);
        randseedchk.setSelected(Organ.useRandomSeed);
        randseedtxt.setText(String.valueOf(Organ.randomSeed));
        /** Unconsciousness */
        ConsciousLevelOptions u = Current.body.brain.getUnconscious();
        eating.setSelected(!u.eat);
        drinking.setSelected(!u.drink);
        horizontal.setSelected(u.flat);
        breathingnorm.setSelected(u.normalBreathing);
        warnunc.setSelected(u.notify);
        uncWarnTime.setText(String.valueOf(u.notifyHours));
        scrollhistorychk.setSelected(f.graph.getEnableHistory());
        extendhistorychk.setSelected(f.graph.enableWholeSessionHistory);
        allowRewindChk.setSelected(f.isRewindEnabled());
        rewindtimefield.setEnabled(f.isRewindEnabled());
        disabledeath.setSelected(u.disableDeath);

        CommonThread t=Current.thread;
        delaytxt.setText(String.valueOf(t.CYCLE_LENGTH_MILLIS));
        maxtimetxt.setText(String.valueOf(t.MAXIMUM_ELAPSED_SECONDS));
        tsmoothtxt.setText(String.valueOf(t.TIME_SMOOTHING));
        graphtimetxt.setText(String.valueOf(f.graph.getTimer()));
        slidertimetxt.setText(String.valueOf(HorizontalBar.BAR_UPDATE_INTERVAL));
    }

    void cancelbutton_actionPerformed(ActionEvent e) {
        hide();
    }

  void allowRewindChk_actionPerformed(ActionEvent e) {
    rewindtimefield.setEnabled(allowRewindChk.isSelected());
  }

  void thickslider_stateChanged(ChangeEvent e) {

  }

}


class InterfaceOptionsDialog_okbutton_actionAdapter implements java.awt.event.
        ActionListener {
    OptionsDialog adaptee;
    InterfaceOptionsDialog_okbutton_actionAdapter(OptionsDialog
                                                  adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.okbutton_actionPerformed(e);
    }
}


class InterfaceOptionsDialog_cancelbutton_actionAdapter implements java.awt.
        event.ActionListener {
    OptionsDialog adaptee;
    InterfaceOptionsDialog_cancelbutton_actionAdapter(OptionsDialog
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.cancelbutton_actionPerformed(e);
    }
}

class InterfaceOptionsDialog_allowRewindChk_actionAdapter implements java.awt.event.ActionListener {
  OptionsDialog adaptee;

  InterfaceOptionsDialog_allowRewindChk_actionAdapter(OptionsDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.allowRewindChk_actionPerformed(e);
  }
}


package phic.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import phic.common.DiseaseCreator;
/*
public class OriginalJBInit extends SimplePhicFrame {

   void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.
                                                 createEtchedBorder(),
                                                 BorderFactory.
                                                 createEmptyBorder(2, 2, 2, 2));
    border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    //border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    this.setDefaultCloseOperation(3);
    this.setJMenuBar(menuBar);
    this.setTitle("Human Physiology");
    filemenu.setText("File");
    savecommand.setText("Save state");
    savecommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        savecommand_actionPerformed(e);
      }
    });
    opencommand.setText("Open state");
    opencommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        opencommand_actionPerformed(e);
      }
    });
    exitcommand.setText("Exit");
    exitcommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    mainFramePanel.setLayout(borderLayout1);
    jPanel4.setLayout(borderLayout2);
    leftHandPanel.setLayout(borderLayout3);
    adddisplay.setMinimumSize(new Dimension(0, 0));
    adddisplay.setToolTipText("Display graph of selected quantity");
    adddisplay.setText("Display");
    adddisplay.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adddisplay_actionPerformed(e);
      }
    });
    rightHandPanel.setLayout(borderLayout4);
    rightSplitter.setResizeWeight(1.0);
    interpreterHelpMenu.setText("Interpreter help");
    diseaseMenu.setText("Diseases");
    partialreset.setToolTipText("Reset variables");
    partialreset.setMargin(new Insets(2, 2, 2, 2));
    partialreset.setText("");
    jPanel8.setLayout(borderLayout8);
    sharedPanel.setLayout(sharedLayout);
    jPanel1.setLayout(borderLayout9);
    spacer.setBorder(BorderFactory.createLoweredBevelBorder());
    spacer.setPreferredSize(new Dimension(4, 0));
    combinedScrollGraph1.setMinimumSize(new Dimension(20, 15));
    combinedScrollGraph1.setPreferredSize(new Dimension(10, 15));
    rightHandPanel.add(rightSplitter);
    rightBottomPanel.setLayout(borderLayout5);
    graphspane.setLayout(borderLayout7);
    leftHandPanel.setPreferredSize(new Dimension(150, 371));
    //start.setPreferredSize(new Dimension(21, 21));
    start.setToolTipText("Stop / Go");
    start.setIcon(picon);
    start.setMargin(new Insets(1, 1, 1, 1));
    start.setText("");
    start.addActionListener(   startAction   );
    statusbar.setToolTipText("Message bar");
    statusbar.setPreferredSize(new Dimension(35, 80));
//		statusbar.setBackground(SystemColor.control);
//		statusbar.setSelectionColor(SystemColor.textHighlightText);
    statusbar.setText("PHIC");
//    statusbar.setEditable(false);
//        statusbar.setSelectedTextColor(Color.black);
    statusbar.setFont(new java.awt.Font("Dialog", 0, 12));
    consolepanel.setAutoscrolls(true);
    consolepanel.setPreferredSize(new Dimension(39, 80));
    consolepanel.setMinimumSize(new Dimension(39, 24));
    graphsmenu.setText("Graphs");
    rescalecommand.setText("Rescale");
    rescalecommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    fluidsmenu.setText("Give");
    jMenuItem9.setText("IV drug bolus");
    jMenuItem9.addActionListener(ivDrugBolusAction);
    jMenuItem11.setText("Oral drug");
    jMenuItem11.addActionListener(oralDrugAction);
    fluidlist.setText("Select");
    timebox.setBorder(border2);
    timebox.setPreferredSize(new Dimension(180, 21));
    timebox.setToolTipText("Current date and time");
    timebox.setText("time");
    //namebox.setBorder(border3);
    //namebox.setPreferredSize(new Dimension(100, 21));
    namebox.setToolTipText("Name of patient");
    namebox.setText("name");
    namebox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        namebox_mouseClicked(e);
      }
    });
    timecomp.setMaximum(timeCompressionValues.length - 1);
    timecomp.setSnapToTicks(true);
    timecomp.setMajorTickSpacing(5);
    timecomp.setPaintTicks(true);
    timecomp.setMinorTickSpacing(1);
    timecomp.setToolTipText("Time compression");
    timecomp.setPreferredSize(new Dimension(80, 24));
    timecomp.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        timecomp_stateChanged(e);
      }
    });
    timecomp.setValue(3);
    timeCompLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        timeCompLabel_mouseReleased(e);
      }
    });
    restartbutton.setToolTipText("Restart");
    restartbutton.setIcon(resicon);
    restartbutton.setMargin(new Insets(1, 1, 1, 1));
    restartbutton.setText("");
    restartbutton.addActionListener(restartAction);
    partialreset.addActionListener(partialResetAction);
    partialreset.setIcon(rewicon);
    mainSplitter.setLeftComponent(leftHandPanel);
    mainSplitter.setOneTouchExpandable(true);
    mainSplitter.setRightComponent(rightHandPanel);
    graphsetupmenu.setText("Setup");
    variablepane.setLayout(borderLayout6);
    bodyTree.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bodyTree_actionPerformed(e);
      }
    });
    limitChecker.setMinimumSize(new Dimension(0, 17));
    toolsmenu.setText("Tools");
    jMenuItem2.setText("Medical reports");
    jMenuItem2.addActionListener(medicalReportsAction);
    orbitsmenu.setText("New Orbit");
    dripspanel.setMinimumSize(new Dimension(0, 0));
    horizontalGraphs.setMinimumSize(new Dimension(0, 0));
    horizontalGraphs.setLayout(gridBagLayout1);
    jMenuItem3.setText("Close all");
    jMenuItem3.addActionListener(closeAllGraphsAction);
    jMenuItem7.setText("Pathology analysis");
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem7_actionPerformed(e);
      }
    });
    jMenuItem10.setText("Fluid balance sheet");
    jMenuItem10.addActionListener(fluidBalanceAction);
    lifesupportmenu.setText("Life support");
    lifesupportmenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lifesupportmenu_actionPerformed(e);
      }
    });
    ecg_menu.setText("ECG");
    ecg_menu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    helpmenu.setText("Help");
    jMenuItem12.setText("Examine patient");
    jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    statuscommand.setPreferredSize(new Dimension(90, 21));
    druglevelsmenu.setText("Drug levels");
    druglevelsmenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        druglevelsmenu_actionPerformed(e);
      }
    });
    ratemenu.setText("Rate");
    jMenuItem4.setText("About");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        about_action(e);
      }
    });
    jMenuItem5.setText("Scenario information");
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jMenuItem6.setText("Physiology help");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jMenuItem8.setText("Restart");
    jMenuItem8.addActionListener(restartAction);
    rewindbutton.setToolTipText("Go back");
    rewindbutton.setActionCommand("Rewind");
    rewindbutton.setMargin(new Insets(1, 1, 1, 1));
    rewindbutton.setIcon(backwardIcon);
    rewindbutton.setText("");
    rewindbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jMenuItem13.setText("Options");
    jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jMenuItem14.setText("Script editor");
    jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    printgrcommand.setText("Print graphs");
    printgrcommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    printsccommand.setText("Print screen");
    printsccommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    printlgcommand.setText("Print log");
    printlgcommand.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });
    jMenuItem15.setText("Monitoring");
    jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        command(e);
      }
    });

    menuBar.add(filemenu);
    menuBar.add(graphsmenu);
    menuBar.add(fluidsmenu);
    menuBar.add(toolsmenu);
    menuBar.add(helpmenu);
    filemenu.add(savecommand);
    filemenu.add(opencommand);
    filemenu.addSeparator();
    filemenu.add(printsccommand);
    filemenu.add(printlgcommand);
    filemenu.add(printgrcommand);
    filemenu.addSeparator();
    filemenu.add(exitcommand);
    this.getContentPane().add(mainFramePanel, BorderLayout.CENTER);
//    jPanel1.add(jPanel2, BorderLayout.WEST);
    leftHandPanel.add(jPanel4, BorderLayout.CENTER);
    jPanel4.add(jPanel5, BorderLayout.SOUTH);
    jPanel5.add(adddisplay, null);
    jPanel4.add(bodyTree, BorderLayout.CENTER);
    jPanel4.add(dripspanel, BorderLayout.NORTH);
    leftHandPanel.add(variablepane, BorderLayout.SOUTH);
//    jPanel1.add(jPanel3, BorderLayout.EAST);

    rightSplitter.add(rightBottomPanel, JSplitPane.BOTTOM);
    rightBottomPanel.add(consolepanel, BorderLayout.CENTER);
    rightBottomPanel.add(limitChecker, BorderLayout.NORTH);
    consolepanel.getViewport().add(statusbar, null);
    rightSplitter.add(graphspane, JSplitPane.TOP);
    graphspane.add(combinedScrollGraph1, BorderLayout.CENTER);
    graphspane.add(horizontalGraphs, BorderLayout.SOUTH);
    graphspane.add(jPanel1,  BorderLayout.EAST);
    jPanel1.add(jPanel8, BorderLayout.CENTER);
    jPanel8.add(sharedPanel, BorderLayout.NORTH);
    jPanel1.add(spacer, BorderLayout.WEST);
    controlBar.add(namebox, null);
    controlBar.add(statuscommand, null);
    controlBar.add(timebox, null);
    controlBar.add(restartbutton, null);
    controlBar.add(partialreset, null);
    controlBar.add(rewindbutton, null);
    controlBar.add(start, null);
    controlBar.add(timecomp, null);
    controlBar.add(timeCompLabel, null);
    mainFramePanel.add(controlBar, BorderLayout.NORTH);
    mainFramePanel.add(mainSplitter, BorderLayout.CENTER);
    graphsmenu.add(ratemenu);
    graphsmenu.add(orbitsmenu);
    graphsmenu.addSeparator();
    graphsmenu.add(graphsetupmenu);
    graphsmenu.add(jMenuItem3);
    graphsmenu.add(rescalecommand);
    graphsmenu.add(frameActions.zoomInAll);
    graphsmenu.add(frameActions.zoomOutAll);
    graphsmenu.add(druglevelsmenu);
    graphsmenu.add(jMenuItem15);
    graphsmenu.add(quickAddAction);
    fluidsmenu.add(fluidlist);
    fluidsmenu.addSeparator();
    fluidsmenu.add(jMenuItem9);
    fluidsmenu.add(jMenuItem11);
    toolsmenu.add(jMenuItem2);
    toolsmenu.add(jMenuItem7);
    toolsmenu.add(jMenuItem10);
    toolsmenu.add(lifesupportmenu);
    toolsmenu.add(jMenuItem12);
    toolsmenu.add(ecg_menu);
    toolsmenu.addSeparator();
    toolsmenu.add(editControllersAction);
    toolsmenu.add(diseaseMenu);
    toolsmenu.add(jMenuItem14);
    toolsmenu.add(jMenuItem13);
    helpmenu.add(jMenuItem5);
    helpmenu.add(jMenuItem6);
    helpmenu.add(interpreterHelpMenu);
    helpmenu.addSeparator();
    helpmenu.add(jMenuItem4);
    mainSplitter.setDividerLocation(200);
    statusbar.requestFocus();
    filemenu.add(jMenuItem8);
    interpreterHelpMenu.addActionListener(helpAction);
    getRootPane().setDefaultButton(start);
    //Create disease menu
    DiseaseCreator d = new DiseaseCreator();
    Action[] da=d.getDiseaseActions();
    for(int j=0;j<da.length;j++){
      diseaseMenu.add(da[j]);
    }
    //find the longest; this is not guaranteed to work as it does not account for proportional spacing
    String longest = "";
    for (int i = 0; i < timeCompressionDescriptions.length; i++) {
      if (timeCompressionDescriptions[i].length() > longest.length()) {
        longest = timeCompressionDescriptions[i];
      }
    }
    timeCompLabel.setText(longest);
    timeCompLabel.setPreferredSize(timeCompLabel.getPreferredSize());
    //rightBottomPanel.add(bottomrightpanel,  BorderLayout.EAST);
  }

}
*/

package phic.gui;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import phic.*;
import phic.common.*;
import phic.gui.graphics.*;


/**
 * Phic Main frame class
 * This class contains the bulk of the code for the standard interface.
 * Contains a menu bar, a control bar, a divider pane with BodyTree
 * panel on left, various configurable
 * displays on the right, and a console below.
 *
 * Extends TextReceiver so that it can receive messages from the engine,
 * which are sent to the console pane.
 */
public class SimplePhicFrame
    extends JFrame
    implements TextReceiver {
  public JFrame getJFrame() {
    return this;
  }

  JPanel mainFramePanel = new JPanel();
  JMenuBar menuBar = new JMenuBar();
  JMenu filemenu = new JMenu();
  JMenuItem savecommand = new JMenuItem();
  JMenuItem opencommand = new JMenuItem();
  JMenuItem exitcommand = new JMenuItem();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel leftHandPanel = new JPanel();
  JPanel rightHandPanel = new JPanel();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JButton adddisplay = new JButton();
  BodyTreeLabelled bodyTree = new BodyTreeLabelled();
  JPanel controlBar = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel rightBottomPanel = new JPanel();
  Border border1;
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel graphspane = new JPanel();
  JPanel variablepane = new JPanel();
  JButton start = new JButton();
  JScrollPane consolepanel = new JScrollPane();
  PhicEvaluator statusbar = new PhicEvaluator();
  JMenu graphsmenu = new JMenu();
  JMenuItem rescalecommand = new JMenuItem();
  JMenu fluidsmenu = new JMenu();
  JMenuItem ivdrugmenu = new JMenuItem();
  JMenu fluidlist = new JMenu();
  Border border2;
  JButton namebox = new JButton();
  //Border border3;
  JMenuItem oraldrugmenu = new JMenuItem();
  BorderLayout borderLayout6 = new BorderLayout();
  LimitChecker limitChecker = new LimitChecker();
  ConsciousStateLabel statuscommand = new ConsciousStateLabel();
  JSlider timecomp = new JSlider();
  JLabel timeCompLabel = new JLabel();
  JButton restartbutton = new JButton();
  JSplitPane mainSplitter = new JSplitPane();
  JMenu graphsetupmenu = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenu toolsmenu = new JMenu();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenu orbitsmenu = new JMenu();
  BorderLayout borderLayout7 = new BorderLayout();
  JMenuItem jMenuItem3 = new JMenuItem();
  JPanel horizontalGraphs = new JPanel();
  JMenuItem jMenuItem7 = new JMenuItem();
  JMenuItem jMenuItem10 = new JMenuItem();
  JMenuItem lifesupportmenu = new JMenuItem();
  JMenuItem ecg_menu = new JMenuItem();
  JMenu helpmenu = new JMenu();
  JMenuItem examinemenu = new JMenuItem();
  JMenuItem druglevelsmenu = new JMenuItem();
  JMenu ratemenu = new JMenu();
  JMenuItem aboutmenu = new JMenuItem();
  public HorzScrollGraph graph = new HorzScrollGraph();
  JPanel dripspanel = new JPanel();
   JMenuItem scenariomenu = new JMenuItem();
   JMenuItem physhelpmenu = new JMenuItem();
   JMenuItem restartmenu = new JMenuItem();
  JButton rewindbutton = new JButton();
  RewindDialog rewindDialog = new RewindDialog();
  JMenuItem jMenuItem13 = new JMenuItem();
  JMenuItem jMenuItem14 = new JMenuItem();
  JMenuItem printgrcommand = new JMenuItem();
  JMenuItem printsccommand = new JMenuItem();
  JMenuItem printlgcommand = new JMenuItem();
  JPanel bottomrightpanel = new JPanel();
  JMenuItem jMenuItem15 = new JMenuItem();
  JMenu diseaseMenu = new JMenu();
  JButton partialreset = new JButton();
  JMenuItem commentsmenu = new JMenuItem();
  LayoutManager boxLayout21 = new BoxLayout(dripspanel, BoxLayout.Y_AXIS);

  JSplitPane rightSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  BoxLayout gridBagLayout1 = new BoxLayout(horizontalGraphs, BoxLayout.Y_AXIS);
  /** Time label - keeps the current body time on the screen as a text label*/
  JLabel timebox = new TimeLabel();
  ECGIcon slowPanel = new ECGIcon();

  /** The class that draws the current date and time. */
  class TimeLabel
      extends JLabel {
    TimeLabel() {
      timer.start();
    }

    /** This timer is initialised to intervals of 1 second. */
    public Timer
        timer = new Timer(1000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setText(Current.body.getClock().getTimeString(Clock.DATETIME));
      }
    });
  }

  /** Executable application to create the frame */
  public static void main(String[] args) {
    SimplePhicFrame f = new SimplePhicFrame();
    f.show();
  }

  //load icons
  /** Icons that appear on buttons */
  ImageIcon picon = new ImageIcon(Resource.loader.getImageResource("Play.gif")),
      sicon = new ImageIcon(Resource.loader.getImageResource("Pause.gif")),
      ricon = new ImageIcon(Resource.loader.getImageResource("SmallCross.gif")),

      fficon = new ImageIcon(Resource.loader.getImageResource("FastForward.gif"));
  Image frameicon = Resource.loader.getImageResource("Humanico16.gif");

  /** String locating help resources */
  public final String generalHelpResource = "help/index.html";

  /** String pointing to help for current scenario */
  public String currentHelpResource = "patient/DefaultScenario.html";

  /** String locating interpreter help resource  */
  public String interpreterHelpResource = "help/Scripting.html";

  /**
   * This is the set of actions for the frame
   */
  PhicFrameActions frameActions;

  /**
   * Construct the default frame, and initialise the buttons and menus etc.
   * from the resource files
   */
  public SimplePhicFrame() {
    //stop clock while initialising
    Current.body.setRunning(false);
    frameActions = new PhicFrameActions(this);
    //load interface
    setupSetupMenu();
    DiseaseCreator d = new DiseaseCreator();
    diseaseMenu = d.getMenu();
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    bodyTree.tree.addTreeSelectionListener(new MyTSL());
    setupFluids(fluidlist);
    updateNameBox();
    Current.body.receiver = this;
    limitChecker.start();
    setIconImage(frameicon);
    //pluggable look and feel menu added 18/10/02
    helpmenu.add(new sanjay.PlafMenu(this.getContentPane()));
    //use popup menu of scrollpane here
    setupRateMenu();
    //make the speed slider reflect the time compression
    updateSpeedSlider();
    //load variables
    Variables.initialise();
    PhicFrameShortcutKeyMapper.mapKeys(this);
    slowPanel.setBody(Current.body);
    addWindowListener(wl);
    /** If no filing system available (e.g. applet), disable those icons */
    try{filer =  PhicFileDialog.createFileDialog();}
    catch(IllegalAccessException e){
      System.out.println("Running without filer - no access.");
      savecommand.setEnabled(false);
      opencommand.setEnabled(false);
    }
  }

  /** Copy rates from the CombinedScrollMenu popup menu */
  void setupRateMenu() {
    Component[] c = graph.popupmenu.getComponents();
    for (int i = 0; i < c.length; i++) {
      if (c[i] instanceof JMenuItem) {
        JMenuItem o = (JMenuItem) c[i];
        JMenuItem n = new JMenuItem(o.getAction());
        n.setBackground(o.getBackground());
        ratemenu.add(n);
      }
    }
  }
  void copyPopupmenuToJMenu(JPopupMenu src, JMenu dst){
    Component[] c = src.getComponents();
    for(int i=0;i<c.length;i++){
      dst.add(c[i]);
    }
  }

  /** Create the menus which read lists from ini files. The menu items'
   * action listener calls graphsetup() with the actionCommand set to
   * the fluid name.
   * @see #graphsetup */
  void setupSetupMenu() {
    ActionListener smal = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graphsetup(e);
      }
    };
    // setupMenu(graphsetupmenu, graphsetups, smal, null);
    try {
      graphsetupmenu = Resource.loader.createMenuFromIni(
          graphSetupsResourceFile, "Views", smal, null);
    }
    catch (IOException ex) {ex.printStackTrace();}
    setupMenu(orbitsmenu, orbitsini, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createOrbit(e.getActionCommand());
      }
    }

    , null);
  }

  /** Read in the section headers from an ini file, and use the sections
   * to fill in a menu.
   * @param menu The menu to which the items will be added.
   * @param ini  The IniReader for the ini file containing the headers
   * @param al   An action listener which will be called when an item is selected from the menu
   * @param icon An optional icon to be displayed in the menu */
  void setupMenu(JMenu menu, IniReader ini, ActionListener al, Icon icon) {
    String[] names = ini.getSectionHeaders();
    for (int i = 0; i < names.length; i++) {
      JMenuItem m = new JMenuItem();
      m.setText(names[i]);
      m.addActionListener(al);
      if (icon != null) {
        m.setIcon(icon);
      }
      menu.add(m);
    }
  }

  /** Set up the fluids menu from the list of available fluids.*/
  void setupFluids(JMenu p) {
    ImageIcon fluidicon = new ImageIcon(Resource.loader.getImageResource(
        "Container.gif"));
    String[] names = Fluids.getNames();
    for (int i = 0; i < names.length; i++) {
      JMenuItem mi = new JMenuItem(names[i], fluidicon);
      mi.addActionListener(fluidselectionlistener);
      p.add(mi);
    }
  }

  void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.
                                                 createEtchedBorder(),
                                                 BorderFactory.
                                                 createEmptyBorder(2, 2, 2, 2));
    border2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    //border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
    exitcommand.setAction(frameActions.exitAction);
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
    //partialreset.setToolTipText("Reset variables");
    partialreset.setAction(frameActions.partialResetAction);
    partialreset.setMargin(new Insets(2, 2, 2, 2));
    partialreset.setText("");
    jPanel8.setLayout(borderLayout8);
    sharedPanel.setLayout(sharedLayout);
    jPanel1.setLayout(borderLayout9);
    spacer.setBorder(BorderFactory.createLoweredBevelBorder());
    spacer.setPreferredSize(new Dimension(4, 0));
    graph.setMinimumSize(new Dimension(20, 15));
    graph.setPreferredSize(new Dimension(10, 15));
    diagramsmenu.setText("Diagrams");
    dripspanel.setLayout(boxLayout21);
    commentsmenu.setAction(frameActions.sendProblemToAuthor);
    rightHandPanel.add(rightSplitter);
    rightBottomPanel.setLayout(borderLayout5);
    graphspane.setLayout(borderLayout7);
    leftHandPanel.setPreferredSize(new Dimension(150, 371));
    //start.setPreferredSize(new Dimension(21, 21));
    start.setAction(frameActions.startAction);
    start.setToolTipText("Stop / Go");
    start.setMargin(new Insets(1, 1, 1, 1));
    start.setText("");
    statusbar.setToolTipText("Scrolling log");
    statusbar.setPreferredSize(new Dimension(35, 80));
    statusbar.setText("HOM\n");
    statusbar.setFont(new java.awt.Font("Dialog", 0, 12));
    consolepanel.setAutoscrolls(true);
    consolepanel.setPreferredSize(new Dimension(39, 80));
    consolepanel.setMinimumSize(new Dimension(39, 24));
    graphsmenu.setText("Graphs");
    rescalecommand.setAction(frameActions.rescaleAction);
    fluidsmenu.setText("Give");
    ivdrugmenu.setAction(frameActions.ivDrugBolusAction);
    oraldrugmenu.setAction(frameActions.oralDrugAction);
    fluidlist.setText("Fluid");
    timebox.setBorder(border2);
    timebox.setPreferredSize(new Dimension(180, 21));
    timebox.setToolTipText("Current date and time");
    timebox.setText("time");
    //namebox.setBorder(border3);
    //namebox.setPreferredSize(new Dimension(100, 21));
    namebox.setAction(frameActions.nameChangeAction);
    namebox.setToolTipText("Name of patient");
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
    restartbutton.setAction(frameActions.restartAction);
    restartbutton.setMargin(new Insets(1, 1, 1, 1));
    restartbutton.setText("");
    mainSplitter.setLeftComponent(leftHandPanel);
    mainSplitter.setOneTouchExpandable(true);
    mainSplitter.setRightComponent(rightHandPanel);
    graphsetupmenu.setText("Views");
    variablepane.setLayout(borderLayout6);
    bodyTree.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bodyTree_actionPerformed(e);
      }
    });
    limitChecker.setMinimumSize(new Dimension(0, 17));
    toolsmenu.setText("Tools");
    jMenuItem2.setText("Medical reports");
    jMenuItem2.addActionListener(frameActions.medicalReportsAction);
    orbitsmenu.setText("New Orbit");
    dripspanel.setMinimumSize(new Dimension(0, 0));
    horizontalGraphs.setMinimumSize(new Dimension(0, 0));
    horizontalGraphs.setLayout(gridBagLayout1);
    jMenuItem3.setText("Close all");
    jMenuItem3.addActionListener(frameActions.closeAllGraphsAction);
    jMenuItem7.setAction(frameActions.pathologyAnalysisAction);
    jMenuItem10.setText("Fluid balance sheet");
    jMenuItem10.addActionListener(frameActions.fluidBalanceAction);
    lifesupportmenu.setAction(frameActions.lifeSupportAction);
    ecg_menu.setAction(frameActions.ecgAction);
    helpmenu.setText("Help");
    examinemenu.setAction(frameActions.examineAction);
    statuscommand.setPreferredSize(new Dimension(90, 21));
    druglevelsmenu.setAction(frameActions.drugLevelsAction);
    ratemenu.setText("Rate");
    aboutmenu.setAction(frameActions.aboutAction);
    scenariomenu.setAction(frameActions.scenarioHelp);
    physhelpmenu.setAction(frameActions.physiologyHelp);
    restartmenu.setAction(frameActions.restartAction);
    rewindbutton.setToolTipText("Go back");
    rewindbutton.setActionCommand("Rewind");
    rewindbutton.setMargin(new Insets(1, 1, 1, 1));
    rewindbutton.setAction(frameActions.rewindAction);
    //rewindbutton.setIcon(backwardIcon);
    rewindbutton.setText("");
    jMenuItem13.setAction(frameActions.optionsAction);
    jMenuItem14.setAction(frameActions.scriptEditorAction);
    printgrcommand.setAction(frameActions.printGraphs);
    printsccommand.setAction(frameActions.printScreen);
    printlgcommand.setAction(frameActions.printLog);
    jMenuItem15.setAction(frameActions.monitoringAction);

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
    rightBottomPanel.add(slowPanel, BorderLayout.EAST);
    slowPanel.setVisible(false);
    slowPanel.setPreferredSize(new Dimension(30,30));
    consolepanel.getViewport().add(statusbar, null);
    rightSplitter.add(graphspane, JSplitPane.TOP);
    graphspane.add(graph, BorderLayout.CENTER);
    graphspane.add(horizontalGraphs, BorderLayout.SOUTH);
    rightHandPanel.add(jPanel1,  BorderLayout.EAST);
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
    graphsmenu.add(frameActions.quickAddAction);
    fluidsmenu.add(fluidlist);
    fluidsmenu.addSeparator();
    fluidsmenu.add(ivdrugmenu);
    fluidsmenu.add(oraldrugmenu);

    toolsmenu.add(jMenuItem2);
    toolsmenu.add(jMenuItem7);
    toolsmenu.add(jMenuItem10);
    toolsmenu.add(lifesupportmenu);
    toolsmenu.add(examinemenu);
    toolsmenu.add(ecg_menu);
    toolsmenu.add(diagramsmenu);
    toolsmenu.add(frameActions.simulationPlot);
    toolsmenu.addSeparator();
    toolsmenu.add(frameActions.editControllersAction);
    toolsmenu.add(diseaseMenu);
    toolsmenu.add(jMenuItem14);
    toolsmenu.add(jMenuItem13);

    helpmenu.add(scenariomenu);
    helpmenu.add(physhelpmenu);
    helpmenu.add(interpreterHelpMenu);
    helpmenu.addSeparator();
    helpmenu.add(commentsmenu);
    helpmenu.add(aboutmenu);
    mainSplitter.setDividerLocation(200);
    statusbar.requestFocus();
    filemenu.add(restartmenu);
    diagramsmenu.addActionListener(frameActions.diagramsAction);
    interpreterHelpMenu.addActionListener(frameActions.new HTMLMessageAction(
        interpreterHelpResource, "Interpreter help"));
    getRootPane().setDefaultButton(start);
    //Create disease menu
    //DiseaseCreator d = new DiseaseCreator();
    /*
    Action[] da=d.getDiseaseActions();
    for(int j=0;j<da.length;j++){
      diseaseMenu.add(da[j]);
    }
    */
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

  /**
   *  Call this method to send a message to the status bar and console.
   *  called by the phic.Body.message() method to direct messages from
   *  to the status bar.
   *  Also intercepts death messages, and calls die() to display a message.
   */
  public void message(final String s) {
    synchronized (statusbar) {
      Document d = statusbar.getDocument();
      try {
        d.insertString(d.getLength(), '\n' + s, null);
        statusbar.setSelectionStart(d.getLength());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      if (s.indexOf(" died") >= 0) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            die(s);
          }
        });
      }
    }
  }
  private static final boolean DEBUG_ERRORS = true;
  /** Send an error message to the status bar/console. Currently delegates
   *  to message().
   *  @see #message */
  public void error(String s) {
    message(s);
    if(DEBUG_ERRORS){
      stopPressed();
      JOptionPane.showMessageDialog(this, s, "Error in calculations",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Whether to show the variable selected in the tree as a separate box
   * under the tree. MyTSL calls creates this and calls setSelectPanel
   * if this variable is true.
   *
   * On slow computers, setting this to true can cause long delays when
   * selecting variables in the tree. This value can be set from the
   * InterfaceOptionsDialog.
   */
  public boolean showSelectedVariableEditor = true;

  /** Class that handles making a selection from the list, and displays
   * the simplest NodeView panel below the tree view  by calling
   * setSelectPanel().  */
  class MyTSL
      implements TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) e.getPath().
          getLastPathComponent();
      if (! (dmtn instanceof Node)) {
        return;
      }
      Node n = (Node) dmtn;
      selectednode = n;
      // 2/7/3 to speed up gui, this is optional
      if (showSelectedVariableEditor) {
        NodeView.Type[] types = NodeView.Type.forNode(n);
        JComponent p = null;
        if (types.length > 0) {
          p = new NodeView(selectednode, types[0], graph);
        }
        setSelectPanel(p);
      }
    }
  }

  /** The component that displays information about the currently
   *  selected node. The object should be a NodeView. */
  Component selectiondisplay = null;
  /** The currently selected node in the tree. */
  Node selectednode = null;
  /** Called when a selection is made to add a component (a NodeView panel)
   * to the bottom of the tree view. */
  public void setSelectPanel(Component c) {
    if (selectiondisplay != null) {
      variablepane.remove(selectiondisplay);
    }
    selectiondisplay = c;
    if (selectiondisplay != null) {
      variablepane.add(selectiondisplay);
    }
    validateTree();
    variablepane.repaint();
  }

  /**
   * This method displays a popup menu reflecting the types of NodeView
   * available for the currentls selected Node. It is called when the add
   * view button is clicked. Possible types are instances of NodeView.Type.
   * The menu items invoke the adddisplayAL action listener.
   */
  void adddisplay_actionPerformed(ActionEvent e) {
    JPopupMenu menu = createDisplayPopup(selectednode);
    JComponent src = (JComponent) e.getSource();
    if (menu == null) {
      return;
    }
    menu.show(src, 0, 0 /*-menu.getHeight()*/);
  }

  /** Create a popup menu with the ways of displaying a given variable */
  JPopupMenu createDisplayPopup(Node n) {
    NodeView.Type[] type = NodeView.Type.forNode(n);
    if (type.length > 0) {
      JPopupMenu displaytypemenu = new JPopupMenu("Display");
      //can it be a scrollgraph?
      if (selectednode.getType()
          == Node.DOUBLE /* && Variables.forNode(selectednode)!=null */) {
        JMenuItem m = displaytypemenu.add("Scroll Graph");
        m.addActionListener(addscrollgraphAL);
      }
      for (int i = 0; i < type.length; i++) {
        JMenuItem m = displaytypemenu.add(type[i].toString());
        m.addActionListener(new adddisplayAL(type[i].type));
      }
      return displaytypemenu;
    }
    else {
      return null;
    }
  }

  /**
   * Called when an add command is selected. Adds the selected view type
   * of the selected Node to the right hand panel.
   */
  class adddisplayAL
      implements ActionListener {
    int i;
    adddisplayAL(int j) {
      i = j;
    }

    public void actionPerformed(ActionEvent e) {
      showVariableInMain(selectednode, i);
    }
  }

  /** Actionlistener to add new scrollgraph */
  Action addscrollgraphAL = new AbstractAction("AddScrollGraph") {
    public void actionPerformed(ActionEvent e) {
      /*
      VisibleVariable vv = Variables.forNode(selectednode);
      if (vv == null) {
        Object o = selectednode.objectGetVal();
        if (o instanceof VDouble) {
          combinedScrollGraph1.addNewVariable( (VDouble) o,
                                              selectednode.canonicalName());
          createThinView(selectednode, true);
        }
        else {
          combinedScrollGraph1.addNewVariable(selectednode);
        }
      }
      else {
        combinedScrollGraph1.addNewVariable(vv);
      }
     */
      createThinView(selectednode,true);
    }
  };


  /**
   *  Create a 'thinview' with the selected node, and select graph if necessary
   */
  NodeView createThinView(Node node,boolean showGraph){
    for(int i=0;i<nodeViews.size();i++){
      NodeView n = (NodeView) nodeViews.get(i);        // check if already shown
      if (n.node.equals(node) && (n.node.getType()==NodeView.THIN_VIEW || n.node.getType()==NodeView.THIN_READ_ONLY)) {
        JCheckBox b=((ThinNodeView)n.content).graphcheckbox;
        if(b.isSelected() != showGraph) b.doClick();
        return n;
      }
    }
    NodeView n = showVariableInMain(node, NodeView.THIN_VIEW);
    if (showGraph)
      ( (ThinNodeView) n.content).graphcheckbox.doClick();
    return n;
  }

  //IVI
  /** The listv of panels currently displayed */
  Vector infusionPanels = new Vector();
  /** When a fluid is selected, add an infusion bag containing 1 litre of fluid. */
  ActionListener fluidselectionlistener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      String fluidName = e.getActionCommand();
      //connect a line to bloodstream
      IntravenousInfusion bag = new IntravenousInfusion(Current.body.
          blood);
      //add the requested fluid to the bag
      bag.add(Fluids.get(fluidName, 1.0));
      //create interface
      InfusionPanel p = new InfusionPanel(bag, fluidName);
      dripspanel.add(p);
      infusionPanels.add(p);
      Current.environment.addInfusion(bag);
      validate();
    }
  };
  /** Called when a drip runs out to remove the interface for the infusion */
  public void finishDrip(IntravenousInfusion n) {
    for (int i = 0; i < infusionPanels.size(); i++) { //find correct panel
      InfusionPanel p = (InfusionPanel) infusionPanels.get(i);
      if (p.infusion == n) {
        dripspanel.remove(p);
        infusionPanels.remove(i--);
      }
    }
    validate();
  }

  //control location of insertion of new control
  /** The containerview object to which views of containers can be added.
   *  This object is displayed in a separate panel. */
  ContainerView containerview;
  /** The panel representing a column of the right-hand panel, shared by
   * many views. */

  /** Add a NodeView to the shared panel. Called by showVariableInMain
   * for NodeViews that return false for needNewFrame(). */
  void addToShared(JComponent c) {
    sharedPanel.add(c);
  }

  /**
   * Create a NodeView object for the Node object specified. Called by the
   * adddisplayAL action listener when the add view popup menu is selected.
   */
  NodeView showVariableInMain(Node n, int type) {
    Cursor oldCursor = getCursor();
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    NodeView nv;
    try {
      nv = new NodeView.Type(type).instantiate(n, graph);
      nodeViews.add(nv);
      if (!nv.needsNewFrame()) { //use the shared column
        addToShared(nv);
      }
      else {
        horizontalGraphs.add(nv); //give a whole column
      }
      validateLater();
    }
    finally {
      setCursor(oldCursor);
    }
    return nv;
  }

  //start and stop
  /** Indicates whether the body clock is started. */
  boolean started = false;

  /** Stops the clock when the stop button is pressed. Also handles the icon */
  protected void stopPressed() {
    Current.body.setRunning(false);
    start.setIcon(picon);
    started = false;
  }

  /** Starts the clock when start button is pressed, also altering the icon. */
  protected void startPressed() {
    Current.body.setRunning(true);
    start.setIcon(sicon);
    started = true;
  }


  /**
   * Miscellaneous global actions are handled here.
   * currently Exit, Medical Reports, ECG, Examination, Rescale,
   * IV drug bolus, and hands
   * are handled here.
   */
  void command(ActionEvent e) {
    String c = e.getActionCommand();



  }







  /**
   * An array of values that represent the ticks on the time compression
   * slider. Each value is is the number of physical milliseconds that
   * correspond to each body-clock second.
   */
  double[] timeCompressionValues = new double[] {
      1000, 1000. / 60,
      1000. / (600),
      1000. / (3600),
      1000. / (3600 * 6),
      1000. / (3600 * 24)
  };

  /**
   * An array of strings that represent the descriptions of the time compressions
   * for the ticks on the slider.
   */
  String[] timeCompressionDescriptions = {
      "Real time", "1 minute per sec", "10 minutes per sec",
      "1 hour per sec",
      "6 hours per sec",
      "1 day per sec"
  };

  /** Called when the time compression slider is moved. Sets the body clock
   *  second in milliseconds. Currently, four settings implemented:
   *  1 [physical] second = [body's] second, minute, hour, day, week. */
  void timecomp_stateChanged(ChangeEvent e) {
    int i = timecomp.getValue();
    double value = timeCompressionValues[i];
    Current.body.getClock().setSecond(value);
    timeCompLabel.setToolTipText("x" + Math.round(1000 / value));
    timeCompLabel.setText(timeCompressionDescriptions[i]);
    displaySlowModeItems(Current.body.getClock().isSlowMode);
  }

  /**
   * Shows interface items that are only visible in 'slow mode' - i.e.
   * real time or slower time compressions. This includes cardiac monitor etc.
   */
  void displaySlowModeItems(boolean show){
    slowPanel.setVisible(show);
  }
  /**
   * Updates the text label that displays the current time-compression rate,
   * based on the current time compression slider setting.
   */
  public void updateTimeCompText(){
    int i = timecomp.getValue();
    double value = timeCompressionValues[i];
    timeCompLabel.setToolTipText("x" + Math.round(1000 / value));
    timeCompLabel.setText(timeCompressionDescriptions[i]);

  }



  /**
   * This resets everything. first all the containers and internal values are
   * cleared, then the initial values are loaded from the Variables.txt file,
   * then the interface is reset.
   */
  void doFullReset() {
    synchronized (Organ.cycleLock) {
      Current.body.resetAllValues();
      statusbar.setText("Restarted\n");
      rewindDialog.reset();
      graph.reset();
    }
    //All drug quantities may be removed from the blood at reset, so remove their displays too.
    graph.removeAnyDrugQuantities();
  }

  /**
   * Reset only the values that are found in the tree of variables,
   * i.e. not the scrolling settings, and not the boolean values.
   */
  void doPartialReset(){
    synchronized (Organ.cycleLock) {
      Current.body.resetBodyValues();
      statusbar.setText("Restarted\n");
      rewindDialog.reset();
      graph.reset();
    }
    //All drug quantities may be removed from the blood at reset, so remove their displays too.
    graph.removeAnyDrugQuantities();
  }
  public static final String graphSetupsResourceFile = "GraphSetups.txt";
  public static final String orbitSetupsResourceFile = "OrbitSetups.txt";
  IniReader graphsetups = new IniReader(graphSetupsResourceFile);
  IniReader orbitsini = new IniReader(orbitSetupsResourceFile);
  /**
   *  graphsetup sets up a set of preprogrammed views on the right hand
   * panel. Called by the action listener of the graphsetups menu. Finds
   * the corresponding entry in GraphSetups.ini and calls showVariableInMain
   * creating new NodeViews for each variable.
   * 26.7.5 stop the clock for this
   * @see #setupSetupMenu
   */
  void graphsetup(ActionEvent e) {
    boolean r=Current.body.getClock().running;
    if(r)Current.body.setRunning(false);
    Cursor previousCursor = getCursor();
    System.out.println("Loading graph setup " + e.getActionCommand());
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      String[][] gs;
      if(e.getSource() instanceof String[][]) gs = (String[][])e.getSource();
      else{
        String s = e.getActionCommand();
        //ArrayIndexOutOfBoundsException may be thrown here
        gs = graphsetups.getSectionPairs(s);
      }
      closeAllGraphs();
      int ngraphs = 0;
      CurveNodeView lastCurve = null;
      String orbitXVarName = null; // store the x Orbit variable until we see the y variable.
      for (int i = 0; i < gs.length; i++) {
        //retrieve the name and type from the map
        String varname = gs[i][0];
        if (gs[i][1].equalsIgnoreCase("orbit")) { //Orbit?
          if (orbitXVarName == null) {
            orbitXVarName = varname; //store x variable
          }
          else { // create the orbit
            createOrbit(Variables.forName(orbitXVarName),
                        Variables.forName(varname));
            orbitXVarName = null;
          }
          continue;
        }
        if(gs[i][1].equalsIgnoreCase("curvepointx") || gs[i][1].equalsIgnoreCase("curvepointy") ){
          if(lastCurve==null) throw new IllegalStateException("error: no curves before "+gs[i][0]+"="+gs[i][1]);
          lastCurve.panel.addPointNode(Variables.forName(varname).node,Color.red,gs[i][1].equalsIgnoreCase("curvepointy"));
          continue;
        }
        if(gs[i][0].equalsIgnoreCase("curvedescription")){
          if(lastCurve==null) throw new IllegalStateException("error: no curves before description");
          lastCurve.panel.setDescription(gs[i][1]);
          continue;
        }
        NodeView.Type type = new NodeView.Type(gs[i][1]);
        try { // is a visible variable?
          VisibleVariable variable = Variables.forName(varname);
          if (gs[i][1].equals("Graph") || gs[i][1].equals("GraphSlider")) {
            //combinedScrollGraph1.addNewVariable(variable);
            NodeView nv=createThinView(variable.node,true);
            ngraphs++;
            if(!gs[i][1].equals("GraphSlider")) nv.t.setReadOnly(true);
          }
          else {
            NodeView n=showVariableInMain(variable.node, type.type);
          }
        }
        catch (IllegalArgumentException ex) { //not a visible variable
          // this will throw an illegal argument exception if the string is not
          // a valid node path. This is not caught.
          if (gs[i][1].equals("Graph")) {
            graph.addNewVariable(Node.findNodeByName(varname));
            ngraphs++;
          }
          else {
            NodeView n = showVariableInMain(Node.findNodeByName(varname), type.type);
            if(n instanceof CurveNodeView) lastCurve = (CurveNodeView)n;
          }
        }
      }
      //?		if(ngraphs>0)graphspane.add(combinedScrollGraph1,BorderLayout.CENTER);
      //validateTree();
      graphspane.repaint();
      if(r)Current.body.setRunning(true);
    }
    finally {
      setCursor(previousCursor);
    }
  }

  /** List of currently showing NodeView objects */
  Vector nodeViews = new Vector();

  /** Removes any scrollgraphs and other items from the shared panel */
  void closeAllGraphs() {
    sharedPanel.removeAll();
    nodeViews.removeAllElements();
    graph.removeAllGraphs();
  }

  /**
   * Create an orbit by name from the Orbits.txt definition file
   */
  void createOrbit(String name) {
    String[] s = orbitsini.getSectionStrings(name);
    VisibleVariable v1 = Variables.forName(s[0]);
    VisibleVariable v2 = Variables.forName(s[1]);
    addToShared(new OrbitPanel(v1, v2, graph));
  }

  /**
   * Create a custom orbit from two given visible variables
   */
  void createOrbit(VisibleVariable x, VisibleVariable y) {
    addToShared(new OrbitPanel(x, y, graph));
  }

  public void markEvent(Object event) {
    graph.mark(event);
  }

  /**
   * Called when a selection is made in the tree. It brings up the variable
   * information dialog box.
   */
  void bodyTree_actionPerformed(ActionEvent e) {
    VisibleVariable v;
    try {
      v = Variables.forName(e.getActionCommand());
    }
    catch (IllegalArgumentException x) {
      System.out.println(e.getActionCommand() +
                         " is not a visible variable");
      return;
    }
    VariablePropertiesDialog d = new VariablePropertiesDialog();
    d.setVariable(v);
    d.show();
  }

  /** This will pause the clock when needed. Called during save/load, other
   *  modal dialog boxes. */
  boolean restartClock = false;
  void pauseBodyClock() {
    Clock clock = Current.body.getClock();
    restartClock = clock.running;
    if (restartClock) {
      Current.body.setRunning(false);
    }
  }

  void restartBodyClock() {
    Body body = Current.body;
    if (restartClock) {
      body.setRunning(true);
    }
  }

  void updateNameBox() {
    namebox.setText(Current.person.name);
  }

  /** Filer for save and load */
  PhicFileDialog filer ;
  /** Script editor scripts */
  Vector scripts = new Vector();
  JMenuItem interpreterHelpMenu = new JMenuItem();


  /** Handle the save command */
  void savecommand_actionPerformed(ActionEvent e) {
    pauseBodyClock();
    Person person = Current.person;
    person.body = Current.body;
    person.environment = Current.environment;
    person.organList = Organ.getList();
    try {
      filer.save(Current.person, this);
    }    catch (Exception x) {
      x.printStackTrace();
      String s = (filer==null)?"":filer.getSelectedFile().toString();
      JOptionPane.showMessageDialog(this,
                                    "An error occured: " + x.toString(),
                                    "Error writing file " + s,
                                    JOptionPane.ERROR_MESSAGE);
    }

    restartBodyClock();
  }

  /** Handle the open command */
  void opencommand_actionPerformed(ActionEvent e) {
    try {
      Person person = filer.open(this);
      if (person != null) {
        setupNewPerson(person);
      }
    }
    catch (Exception x) {
      x.printStackTrace();
      JOptionPane.showMessageDialog(this,
                                    "An error occured: " + x.toString(),
                                    "Error opening file " +
                                    ((filer==null)?"":filer.getSelectedFile().toString()),
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * This method sets up the interface to display the person data given.
   * Called when a new data file is loaded
   */
  public void setupNewPerson(Person person) {
    //stop clock
    person.body.getClock().preventTimeLapse();
    person.body.getClock().stop();
    start.setIcon(picon);
    started = false;
    //restore all working variables
    Current.body = person.body;
    Current.environment = person.environment;
    Current.person = person;
    Organ.setList(person.organList);
    person.body.receiver = this;
    //reload all variables
    Node.allNodes = new Vector();
    Variables.initialiseVariables();
    bodyTree.refreshNodes();
    person.body.setupControllers(); //currently the controllers are transient
    try {
      LimitedNode.setupVisibleNodes();
    }
    catch (Exception e) {
      e.printStackTrace();
    } //errors (should not occur) won't affect rest of setup.
    //update interface
    graph.clock = person.body.getClock();
    updateNameBox();
    updateSpeedSlider();
    //ensure all graphs and displays are showing the new variables.
    replaceAllVariables();
    statusbar.restoreObjectRoots();
  }

  /** Replace all the variables monitored by the gui, if the objects have changed. */
  void replaceAllVariables() {
    graph.replaceVariables();
    for (int i = 0; i < nodeViews.size(); i++) {
       ( (OldNodeView) nodeViews.get(i)).replaceVariables();
    }
  }

  /** Makes the slider reflect the current time compression setting */
  void updateSpeedSlider() {
    double s = Current.body.getClock().getSecond();
    for (int i = 0; i < timeCompressionValues.length; i++) {
      if (s == timeCompressionValues[i]) {
        timecomp.setValue(i);
      }
    }
  }


  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout8 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  JPanel sharedPanel = new JPanel();
  BoxLayout sharedLayout = new BoxLayout(sharedPanel, BoxLayout.Y_AXIS);
  BorderLayout borderLayout9 = new BorderLayout();
  JPanel spacer = new JPanel();
  JMenuItem diagramsmenu = new JMenuItem();

  /** Ask for a new body clock rate */
  void timeCompLabel_mouseReleased(MouseEvent e) {
    Point p = e.getComponent().getLocationOnScreen();
    p.translate(e.getX(), e.getY());
    RatePanel.showSmallDialog(p.x, p.y);
  }




  /**
   * When the patient dies, show message dialog.
   * If there is no control bar, then ask whether to restart.
   */
  private void validateLater() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        validate();
      }
    });
  }

  /**
   * This kills the current patient. It sends the message to the coinsole,
   * and shows a dialog box ( @see phic.gui.DeathBox ) with the message.
   *
   * If DisableDeath is selected in the Brain.unconsciousOptione, then this method
   * returns with no effect.
   *
   * @param message String the message describing the reason of death.
   */
  protected void die(String message) {
    if (Current.body.brain.getFeeling() == Brain.DEAD) {
      //return;
    }
    if(Current.body.brain.getUnconscious().disableDeath){ return; }

    DeathBox d = new DeathBox(message, controlBar.getParent() == null);
    JDialog dialog = d.createDialog(this, Current.person.name + " died");
    dialog.show();
    if (d.getValue() instanceof String
        && ( (String) d.getValue()).equalsIgnoreCase("Restart")) {
      restartbutton.doClick();
    }
  }

  public void setRewindEnabled(boolean b){
    rewindbutton.setEnabled(b);
    rewindDialog.enableStore = b;
  }
  public boolean isRewindEnabled(){
    return rewindDialog.enableStore;
  }
  WindowListener wl = new WindowAdapter(){
    public void windowClosed(WindowEvent e){
      Current.body.setRunning(false);
    }
  };
}

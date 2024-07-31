package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import phic.*;
import phic.common.IniReader;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import phic.modifiable.Script;

/**
 * Class to setup the phic frames from a file.
 */
public class PhicFrameSetup
    extends SimplePhicFrame
    implements FrameStub {
  public PhicFrameSetup() {
  }

  final String DEFAULT_SECTION = "Default";

  /** Globally accessible variable to indicate whether the variable tree is visible */
  public static boolean treeIsShowing = true;

  double timeCompression = Double.NaN;

  /**
   * Setup a Simple Phic Frame using parameters from a resource file.
   * Delegates to doSetup().
   */
  public PhicFrameSetup(String resourceFile, String resourceSection) {
    doSetup(resourceFile, resourceSection);
  }

  /**
   * Setup the frame from a resource file.
   * @param resourceFile the name of the resource file containing the section
   * whose properties are to be read
   * @param resourceSection the section with the properties which are to reflect
   * the new frame.
   * @throws IllegalArgumentException if the specified section is not
   * found within the given file
   */
  public void doSetup(String resourceFile, String resourceSection) {
    if (resourceSection == null) {
      resourceSection = DEFAULT_SECTION;
    }
    IniReader ir = new IniReader(resourceFile);
    Map m = null;
    try {
      m = ir.getSectionMap(resourceSection);
    }
    catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid section name in " +
                                         resourceFile + ": " + resourceSection);
    }
    setTitle(Resource.identifierToText("HOM - Human Physiology Model - "+resourceSection));
    doSetup(m);
  }

  /**
   * Uses the property map specified to setup the window.
   */
  public void doSetup(Map m) {
    //put engine properties
    //put GUI properties one by one
    setSize(new Dimension(650, 490));
    try {
      double width = ( (Double) m.get("FrameWidth")).doubleValue();
      if (width > 0) {
        setSize( (int) width, getSize().height);
      }
    }    catch (NullPointerException e) {}
    try {
      double height = ( (Double) m.get("FrameHeight")).doubleValue();
      if (height > 0) {
        setSize(getSize().width, (int) height);
      }
    }    catch (NullPointerException e) {}
    try{
      boolean fullscreen=((Boolean)m.get("FullScreen")).booleanValue();
      if(fullscreen){setExtendedState(MAXIMIZED_BOTH);}
    }catch(Exception e){}

    // ? resizeable
    try {
      boolean resiz = ( (Boolean) m.get("Resizeable")).booleanValue();
      setResizable(resiz);
    }
    catch (NullPointerException e) {}
    try {
      boolean tree = ( (Boolean) m.get("Tree")).booleanValue();
      if (!tree) {
        treeIsShowing = tree;
        mainFramePanel.remove(mainSplitter);
        mainFramePanel.add(rightHandPanel, BorderLayout.CENTER);
        rightBottomPanel.add(dripspanel, BorderLayout.NORTH);
        OldNodeView.TitleBar.allowClose = false;
      }
    }
    catch (NullPointerException e) {}

    try {
      boolean rew = ( (Boolean) m.get("RewindButton")).booleanValue();
      setRewindEnabled(rew);
      if (!rew) {
        controlBar.remove(rewindbutton);
      }
    }
    catch (NullPointerException e) {}

    //Allow BuildABod
    try {
      boolean build = ( (Boolean) m.get("EditPatient")).booleanValue();
      PersonSetupDialog.allowBuildABod = build;
    }
    catch (NullPointerException e) {}

    try {
      boolean limit = ( (Boolean) m.get("LimitChecker")).booleanValue();
      if (!limit) {
        rightBottomPanel.remove(limitChecker);
        limitChecker.running = false;
      }
    }
    catch (NullPointerException e) {}

    //GUI components
    try {
      boolean fluid = ( (Boolean) m.get("FluidsMenu")).booleanValue();
      if (!fluid) {
        menuBar.remove(fluidsmenu.getComponent());
      }
    }    catch (NullPointerException e) {}


    //Diagnoser
    try {
      boolean diagnose = ( (Boolean) m.get("Diagnose")).booleanValue();
      PathologyAnalysisDialog.diagnose=diagnose;
    }        catch (NullPointerException e) {}
    //Tools
    try {
      boolean tools = ( (Boolean) m.get("ToolsMenu")).booleanValue();
      if (!tools) {
        menuBar.remove(toolsmenu.getComponent());
      }
    }
    catch (NullPointerException e) {}
    try {
      boolean graphs = ( (Boolean) m.get("GraphsMenu")).booleanValue();
      if (!graphs) {
        menuBar.remove(graphsmenu.getComponent());
        helpmenu.add(new AbstractAction("Restore default graphs") {
          public void actionPerformed(ActionEvent e) {
            graphsetup(new ActionEvent(this, 0, defaultGraphSetup));
          }
        });
      }
    }
    catch (NullPointerException e) {}
    checkForOtherMenus(m);
    try {
      boolean console = ( (Boolean) m.get("Console")).booleanValue();
      if (!console) {
        rightBottomPanel.remove(consolepanel);
      }
    }
    catch (NullPointerException e) {}
    try {
      boolean controls = ( (Boolean) m.get("ControlBar")).booleanValue();
      if (!controls) {
        mainFramePanel.remove(controlBar);
      }
    }
    catch (NullPointerException e) {}
    //setup graphs
    defaultGraphSetup = (String) m.get("GraphSetup");
    if (defaultGraphSetup != null) {
      try {
        graphsetup(new ActionEvent(this, 0, defaultGraphSetup));
      }
      catch (Exception e) {
        System.out.println("Invalid graph setup: " + defaultGraphSetup);
        e.printStackTrace();
      }
    }
    try {
      double timeb = ( (Double) m.get("Timebase")).doubleValue();
      if (timeb > 0) {
        graph.setTSpeed(timeb);
      }
    }
    catch (NullPointerException e) {}
    String vn = (String) m.get("VisibleNodes");
    if (vn != null) {
      try {
        LimitedNode.visibleNodesSection = vn;
      }
      catch (IllegalArgumentException e) {
        System.out.println("No such VisibleNodes setup: " + vn);
      }
    }
    //load file
    // the filing system is first tried, followed by the Resource.loader
    patientFile = (String) m.get("PatientFile");
    if (patientFile != null) {
      fileOpen(patientFile);
    }
    //time compression
    try {
      timeCompression = ( (Double) m.get("TimeCompression")).doubleValue();
      if (timeCompression > 0) {
        Current.body.getClock().setSecond(1000. / timeCompression);
        updateSpeedSlider();
        updateTimeCompText();
      }
    }
    catch (NullPointerException e) {}

    //started?
    try {
      boolean start = ( (Boolean) m.get("Started")).booleanValue();
      if (!start) {
        stopPressed();
        startClockOnInitialisation = false;
      }
      else {
        startClockOnInitialisation = true;
      }
    }
    catch (NullPointerException e) {}

    //display startup information dialog?
    boolean startWithInformation=false;
    try{
      startWithInformation = ((Boolean)m.get("StartWithInformation")).booleanValue();
    }catch(NullPointerException e){}

    //startup scenario message
    String scenario = (String) m.get("ScenarioHtml");
    if (scenario != null) {
      currentHelpResource = scenario;
      if(startWithInformation)
        HTMLMessagePane.showDialog(scenario, "Welcome");
    }

    String startupScript = (String)m.get("StartupScript");
    if(startupScript!=null){
      try{
        Current.body.startupScript = new Script("Startup script",
          "Run every time the variables are reset",startupScript.trim(),false);
      }catch(Exception x){x.printStackTrace();}
    }
    try{
      graph.mainpane.allowDrawOnGraph = ((Boolean)m.get("AllowMouseDrawing")).booleanValue();
    }catch(Exception e){};
    try{
      boolean showGraphPanel =  ((Boolean)m.get("GraphPanel")).booleanValue();
      if(!showGraphPanel) rightSplitter.remove(graphspane);
    }catch(Exception e){};


  }

  /**
   * The default patient file to load on startup.
   */
  String patientFile = null;

  /**
   * Opens a patient file, either from disc or from the resource path.
   * @param file the name of the file.
   */
  public void fileOpen(String file) {
    try {
      InputStream is = null;
      try {
        is = new FileInputStream(file);
      }
      catch (Exception e) {
        is = Resource.loader.getResource(file);
      }
      Person p;
      if(filer!=null){
        if (file.endsWith(".patient")) {
          p = filer.openPatientFile(is);
          setupNewPerson(p);
        }
        else {
          p = new Person();
          setupNewPerson(p);
          filer.openScriptFile(is);
        }
      }
      startPressed();
    }
    catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null,
                                    "Could not load " + file + "\n because of " +
                                    e, "Error loading file",
                                    JOptionPane.ERROR_MESSAGE);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null,
                                    "The file " + file +
                                    "\n was created with an old version (" + e +
                                    ")",
                                    "Error loading file",
                                    JOptionPane.ERROR_MESSAGE);
    }
    catch(InvocationTargetException e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(null,
                                    "The file "+file +
                                    "\n contains invalid script commands. ",
                                    "Error loading file",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  public void show() {
    super.show();
    if (LimitedNode.visibleNodesSection != null) {
      LimitedNode.setVisibleNodesDelayed(LimitedNode.visibleNodesSection);
    }
    bodyTree.reload();
    restartbutton.doClick();
    if (startClockOnInitialisation) {
      javax.swing.SwingUtilities.invokeLater(
          new Runnable() {
        public void run() {
          javax.swing.Timer t = new javax.swing.Timer(1000,new ActionListener(){public void actionPerformed(ActionEvent e){
              startPressed();
          }});
          t.setRepeats(false);
          t.start();
        }
      }
      );
    }
  }

  /** This is true if we need to start the clock on initialisation */
  protected boolean startClockOnInitialisation = false;
  /** The initial GraphSetup */
  protected String defaultGraphSetup = null;

  /**
   * Special system to look through the menu hierarchy for other options.
   */
  protected void checkForOtherMenus(Map m) {
    for (Iterator i = m.keySet().iterator(); i.hasNext(); ) {
      String menuItem = (String) i.next();
      Component c = searchMenu(menuBar, menuItem);
      if (c != null) {
        boolean visible = ( (Boolean) m.get(menuItem)).booleanValue();
        if (c instanceof JMenuItem) {
           ( (JMenuItem) c).setVisible(visible);
        }
      }
    }
  }

  protected Component searchMenu(MenuElement e, String s) {
    Component c = e.getComponent();
    if (c instanceof JMenuItem) {
      String str = ( (JMenuItem) c).getText();
      if (str != null && str.equalsIgnoreCase(s)) {
        return c;
      }
    }
    MenuElement[] me = e.getSubElements();
    for (int i = 0; i < me.length; i++) {
      Component f = searchMenu(me[i], s);
      if (f != null) {
        return f;
      }
    }
    return null;
  }

  void doFullReset() {
    super.doFullReset();
    if (patientFile != null) {
      fileOpen(patientFile);
    }
    if (!Double.isNaN(timeCompression) && timeCompression > 0) {
      Current.body.getClock().setSecond(1000. / timeCompression);
      updateSpeedSlider();
    }
  }

  public void unconsciousForAges() {
    int i = JOptionPane.showConfirmDialog(this,
        Current.person.name + " has been unconscious for 8 hours. Would you like to start a drip?",
                                          "Unconsciousness warning",
                                          JOptionPane.YES_NO_OPTION,
                                          JOptionPane.WARNING_MESSAGE);
    if (i == JOptionPane.YES_OPTION) {
      fluidselectionlistener.actionPerformed(new ActionEvent(this, 0, "Saline"));
    }
  }

  public void addCloseListener(final java.awt.event.ActionListener reshowListener) {
    addWindowListener(new WindowAdapter(){public void windowClosed(WindowEvent e){
        reshowListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "Close"));
      }});
  }
}

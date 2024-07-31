package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;

import phic.*;
import phic.drug.*;
import phic.ecg.*;
import phic.gui.exam.*;

/**
 * This file contains the actions that are performed by the PhicFrame object. Each
 * SimplePhicFrame has a PhicFrameActions which is associated with it, which manages
 * commands from the main frame interface (menus, buttons and keyboard shortcuts)
 * and produces the appropriate response.
 *
 * The SimplePhicFrame also calls PhicFrameShortcutKeyMapper.mapKeys() to bind
 * the actions in this file to the keyboard shortcuts.
 */

public class PhicFrameActions {
  SimplePhicFrame f;
  public PhicFrameActions(final SimplePhicFrame f) {
    this.f=f;
    printScreen = new PrintAction("Print screen",f);
    printGraphs = new PrintAction("Print graphs",f.graph);
    printLog = new PrintAction("Print log",f.statusbar);
    scenarioHelp =  new HTMLMessageAction( f.currentHelpResource,  "Scenario information"){
      public void actionPerformed(ActionEvent e){
        r=f.currentHelpResource; //update current resource
        super.actionPerformed(e);
      }
    };
    physiologyHelp = new HTMLMessageAction(f.generalHelpResource,  "Physiology help");

    startAction = new PhicAction("Start", f.picon) {
      public void actionPerformed(ActionEvent e) {
        if (!f.started) {
          f.startPressed();
        }
        else {
          f.stopPressed();
        }
      }
    };

  }
  /** This calls zoomAllGraphs(false) in the combinedScrollGraph */
  public Action zoomInAll = new PhicAction("Zoom in graph scales"){
    public void actionPerformed(ActionEvent e){
      f.graph.zoomAllGraphs(false);
    }
  };
  /** This calls zoomAllGraphs(true) in the combinedScrollGraph */
  public Action zoomOutAll = new PhicAction("Zoom out graph scales"){
    public void actionPerformed(ActionEvent e){
      f.graph.zoomAllGraphs(true);
    }
  };
  /** Action to print the whole screen */
  public Action printScreen;
  /** Action to print the graphs (HorzScrollGraph) */
  public Action printGraphs;
  /** Action to print the log panel / console */
  public Action printLog;

  /** Method that is called by the print action to print the given component */
  private void doPrinting(final Component item) {
    PrinterJob p = PrinterJob.getPrinterJob();
    p.setPrintable(new Printable(){
      public int print(Graphics g, PageFormat f, int page){
        if(page==0){
          if(item instanceof HorzScrollGraph){
            /** @todo switch black to white for the graphs */


          }
          Graphics2D g2=(Graphics2D)g;
          g2.translate(f.getImageableX(), f.getImageableY());
          double fw=f.getImageableWidth(), fh=f.getImageableHeight(), cw=item.getWidth(), ch=item.getHeight();
          if(cw>fw || ch>fh){
            if(fw/fh<cw/ch){ //fit width
              g2.scale(fw/cw,fw/cw);
            }else{ //fit height
              g2.scale(fh/ch, fh/ch);
            }
          }
          item.paintAll(g);
          return Printable.PAGE_EXISTS;
        }else{return Printable.NO_SUCH_PAGE; }
      }
    });
    p.setCopies(1);
    p.printDialog();
    try {
      p.print();
    }
    catch (PrinterException ex) {
      JOptionPane.showMessageDialog(f, ex, "Could not print",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }
  /** The action class that calls the print method for various components */
  class PrintAction extends PhicAction{
    Component p;
    public PrintAction(String action, Component p){ super(action);this.p = p; }
    public PrintAction(String action, Icon i,Component p){ super(action,i);this.p = p; }
    public void actionPerformed(ActionEvent e){     doPrinting(p);   }
  }

  public Action monitoringAction = new PhicAction("Monitoring"){
    public void actionPerformed(ActionEvent e){
      new MonitorsDialog().show();
    }
  };
  public Action ivDrugBolusAction = new PhicAction("IV drug bolus") {
    public void actionPerformed(ActionEvent e) {
      DrugDialog d = new DrugDialog();
      d.setTitle("Choose intravenous drug");
      d.OK.setText("Give");
      d.show();
      DrugContainer drug = d.getDrugContainer();
      if (drug != null) {
        Current.body.message("IV bolus of " + d.getDrugLabel());
        Current.body.blood.add(drug);
        f.markEvent(d.getDrugName());
      }
    }
  };
  public Action oralDrugAction = new PhicAction("Oral drug"){
    public void actionPerformed(ActionEvent e) {
      DrugDialog d = new DrugDialog();
      d.setTitle("Choose oral drug");
      d.OK.setText("Give");
      d.show();
      DrugContainer drug = d.getDrugContainer();
      if (drug != null) {
        Current.body.message("Oral dose of " + d.getDrugLabel());
        Current.body.gitract.stomach.add(drug);
        f.markEvent(d.getDrugName());
      }
    }
  };
  public Action medicalReportsAction = new PhicAction("Medical reports"){
    public void actionPerformed(ActionEvent e){
      ScreensDialog s = new ScreensDialog();
      s.show();
    }
  };
  class HTMLMessageAction extends PhicAction{
    String r,a;
    public HTMLMessageAction(String resource, String action){ super(action);r=resource;a=action;}
    public HTMLMessageAction(String resource, Icon i,String action){ super(action,i);r=resource;a=action;}
    public void actionPerformed(ActionEvent e){
      HTMLMessagePane.showDialog(r, a);
    }
  }
  Action scenarioHelp, physiologyHelp;

  ImageIcon resicon = new ImageIcon(Resource.loader.getImageResource("RewindFully.gif"));
  /**
   * Calls Current.body.reset() to reset all the organs and containers,
   *  followed by Variables.initialise() to restore all the initial values.
   * Also resets the rewind dialog, which contains the history data.
   */
  Action restartAction=new PhicAction("Restart", "Reset whole simulation", resicon){
    public void actionPerformed(ActionEvent e) {
      Cursor previousCursor = f.getCursor();
      f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try {
        boolean wasStarted = f.started;
        if (f.started) {
          f.stopPressed();
        }
        f.doFullReset();
        if (wasStarted) {
          f.startPressed();
        }
      }
      finally {
        f.setCursor(previousCursor);
      }
    }
  };

  ImageIcon rewicon = new ImageIcon(Resource.loader.getImageResource("Rewind.gif"));

  /**
   * Partial restart - resets body values,
   * but retains Environment and
   * Controllers
   */
  Action partialResetAction = new PhicAction("Partial reset", "Reset variable values", rewicon ) {
    public void actionPerformed(ActionEvent e) {
      Cursor previousCursor = f.getCursor();
      f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try {
        boolean wasStarted = f.started;
        if (f.started) {
          f.stopPressed();
        }
        f.doPartialReset();
        if (wasStarted) {
          f.startPressed();
        }
      }
      finally {
        f.setCursor(previousCursor);
      }
    }
  };


  /** Action to open quick-add dialog box, allowing variables to be added to the display quickly */
  Action quickAddAction = new PhicAction("Quick add"){
    public void actionPerformed(ActionEvent e) {
      QuickAdd q = new QuickAdd();
      q.show();
      if (q.selectedVariable != null) {
        if (!q.selectedScrollGraph) f.showVariableInMain(q.selectedVariable.node,
            q.selectedDisplay.type);
        else f.createThinView(q.selectedVariable.node,false);
      }
    }
  };


  /**
   * Called when the start/stop button is clicked. Simply starts the body clock
   * or stops it by calling startPressed or stopPressed.
   */
  Action startAction;

  /** creates a dialog for editing controllers */
  Action editControllersAction = new PhicAction("Edit controllers"){
    public void actionPerformed(ActionEvent e){
      ControllerEditorDialog c = new ControllerEditorDialog();
      c.setBody(Current.body);
      c.setFrame(f);
      c.show();
    }
  };


  Action closeAllGraphsAction = new PhicAction("Close all graphs"){public void actionPerformed(ActionEvent e) {
    f.closeAllGraphs();
  }};

  /** Display the fluid balance sheet */
  public Action fluidBalanceAction = new PhicAction("Fluid balance sheet") {
    public void actionPerformed(ActionEvent e) {
      new FluidBalanceDialog().show();
    }
  };

  /** Exit the application */
  public Action exitAction = new PhicAction("Exit"){
    public void actionPerformed(ActionEvent e){
      f.hide();
      Current.body.setRunning(false);
      System.exit(0);
    }
  };
  public Action ecgAction = new PhicAction("ECG"){
    public void actionPerformed(ActionEvent e) {
      PhicECGDialog d = new PhicECGDialog();
      d.show();
    }
  };

  /** Display the examinations dialog */
  public Action examineAction = new PhicAction("Examine patient") {
    public void actionPerformed(ActionEvent e) {
      ExaminationsDialog d = new ExaminationsDialog(Current.body);
      d.show();
    }
  };

  /** Display the about box as a modal dialog */

  public Action aboutAction = new PhicAction("About") {
    public void actionPerformed(ActionEvent e) {
      new AboutBox().show();
    }
  };

  /** Show the script editor dialog */
  public Action scriptEditorAction = new PhicAction("Script editor") {
    public void actionPerformed(ActionEvent e) {
      ScriptEditor se = new ScriptEditor(f.scripts);
      se.show();
      f.scripts = se.getScripts();
    }
  };

  /** Show the options dialog */
  public Action optionsAction = new PhicAction("Options") {
    public void actionPerformed(ActionEvent e) {
      new OptionsDialog().show();
    }
  };

  ImageIcon backwardIcon = new ImageIcon(Resource.loader.getImageResource("PlayBackwards.gif"));

  /** Show the rewind dialog */
  public Action rewindAction = new PhicAction("Rewind" ,backwardIcon) {
    public void actionPerformed(ActionEvent e) {
      f.rewindDialog.show();
    }
  };


  /** Rescale all the graphs in the current scrollgraphs */
  public Action rescaleAction = new PhicAction("Rescale") {
    public void actionPerformed(ActionEvent e) {
      f.graph.rescaleAll();
    }
  };

  /** Name change dialog */
  public Action nameChangeAction = new PhicAction("Change patient details") {
    public void actionPerformed(ActionEvent e) {
      PersonSetupDialog d=new PersonSetupDialog(Current.person);
      d.show();
      if(d.okPressed) {
        f.namebox.setText(d.person.name);
      }
    }
  };

  /** Display the pathology analysis dialog */
  public Action pathologyAnalysisAction = new PhicAction("Pathology analysis") {
    public void actionPerformed(ActionEvent e) {
      new PathologyAnalysisDialog().show();
    }
  };

  /** Display the life support machine dialog */
  public Action lifeSupportAction = new PhicAction("Clamp variables") {
    public void actionPerformed(ActionEvent e) {
      new LifeSupportDialog(Current.environment.getVariableClamps()).show();
    }
  };

  /** Diplay the drug concentration chooser dialog */
  public Action drugLevelsAction = new PhicAction("Drug levels") {
    public void actionPerformed(ActionEvent e) {
      new DrugConcentrationGraphChooser().show();
    }
  };
  public Action diagramsAction= new PhicAction("Diagrams"){
    public void actionPerformed(ActionEvent e){
      new ImageDiagramDialog().show();
    }
  };

  public Action sendProblemToAuthor = new PhicAction("Send problem to author"){
    public void actionPerformed(ActionEvent e){
      new SendCommentsDialog().show();
    }
  };
  public Action resetControllers = new PhicAction("Reset controllers", "Reset all the controller values to defaults"){
    public void actionPerformed(ActionEvent e){
      Current.body.setupControllers();
    }
  };

  public Action speedUp = new PhicAction("Increase simulation speed", "Makes body-time run faster relative to real-time"){
    public void actionPerformed(ActionEvent e){
      f.timecomp.setValue(f.timecomp.getValue()+1);
    }
  };
  public Action speedDown = new PhicAction("Decrease simulation speed", "Makes body-time run slower relative to real-time"){
    public void actionPerformed(ActionEvent e){
      f.timecomp.setValue(f.timecomp.getValue()-1);
    }
  };
  SimulationPlotDialog spdlg;
  public Action simulationPlot = new PhicAction("Simulation plot", "Run the model several times and plot graph of results"){
    public void actionPerformed(ActionEvent e){
      if(spdlg==null) spdlg=new SimulationPlotDialog();
      spdlg.show();
    }
  };


  /**
   * Base class of all actions
   */
  abstract class PhicAction extends AbstractAction{
    public PhicAction(String name){super(name);}
    public PhicAction(String name, Icon icon){ super(name,icon);}
    public PhicAction(String name, String tooltip){
      super(name);
      putValue(SHORT_DESCRIPTION, tooltip);
    }
    public PhicAction(String name, String tooltip, Icon icon){
      super(name,icon);
      putValue(SHORT_DESCRIPTION, tooltip);
    }
  }

}

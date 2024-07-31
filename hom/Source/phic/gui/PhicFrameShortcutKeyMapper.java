package phic.gui;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;

/**
 * This class contains a static method to map keys in a given SimplePhicFrame
 * to the appropriate actions.
 */
public class PhicFrameShortcutKeyMapper {
  public static void mapKeys(final SimplePhicFrame f){
    ActionMap am = f.mainFramePanel.getActionMap();
    InputMap  im = f.mainFramePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    PhicFrameActions a = f.frameActions;
    // ^P start/stop
    am.put(a.startAction,a.startAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK),a.startAction);
    // R reset
    am.put(a.partialResetAction,a.restartAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,0),a.restartAction);
    // ^R full reset
    am.put(a.restartAction,a.restartAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK),a.restartAction);

    // ^A quick add
    am.put(a.quickAddAction, a.quickAddAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK), a.quickAddAction);
    // ^W close all graphs
    //am.put(a.closeAllGraphsAction,a.closeAllGraphsAction);
    //im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_MASK),a.closeAllGraphsAction);

    // ^E controllers
    am.put(a.editControllersAction,a.editControllersAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK),a.editControllersAction);
    // ^D iv drug
    am.put(a.ivDrugBolusAction,a.ivDrugBolusAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_MASK),a.ivDrugBolusAction);
    // D oral drug
    am.put(a.oralDrugAction,a.oralDrugAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0),a.oralDrugAction);
    // F1 scenario help
    am.put(a.scenarioHelp,a.scenarioHelp);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1,InputEvent.CTRL_MASK),a.scenarioHelp);
    // ^F1 physiology help
    am.put(a.physiologyHelp,a.physiologyHelp);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0),a.physiologyHelp);
    // ^F fluid balance
    am.put(a.fluidBalanceAction,a.fluidBalanceAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK),a.fluidBalanceAction);
    // ^M medical reports
    am.put(a.medicalReportsAction,a.medicalReportsAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M,InputEvent.CTRL_MASK),a.medicalReportsAction);
    //Ctrl-O options
    am.put(a.optionsAction,a.optionsAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK),a.optionsAction);
    // ^K clamped variables
    am.put(a.lifeSupportAction, a.lifeSupportAction);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), a.lifeSupportAction);
    // ^U simulation plot dialog
    am.put(a.simulationPlot, a.simulationPlot);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK), a.simulationPlot);

    // ^[ and ^] speed up and slow down
    am.put(a.speedUp, a.speedUp);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, InputEvent.CTRL_MASK), a.speedUp);
    am.put(a.speedDown, a.speedDown);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, InputEvent.CTRL_MASK), a.speedDown);

    Action toConsole = new AbstractAction(){
      public void actionPerformed(ActionEvent e){
        PhicEvaluator s=f.statusbar;
        if(s.hasFocus()){
          s.transferFocus();
        }else{
          s.requestFocusInWindow();
          int pos = Math.max(0,s.getDocument().getLength() - 1);
          s.setCaretPosition(pos);
          try {
            s.setCaretPosition(pos+1);
            if (!s.getDocument().getText(pos, 1).
                equals("\n")) {
              s.getDocument().insertString(pos+1,"\n",null);
              s.setCaretPosition(pos+2);
            }
          }
          catch (BadLocationException ex) {  ex.printStackTrace();   }
          catch (IllegalArgumentException ex) {  ex.printStackTrace();   }
        }
      }
    };
    // ^S to console
    am.put(toConsole, toConsole);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK),toConsole);
  }
}

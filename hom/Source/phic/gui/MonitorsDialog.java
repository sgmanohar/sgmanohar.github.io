package phic.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import phic.*;
import phic.common.*;
import phic.common.realtime.*;
/**
 * Show monitors for realtime variables
 */

public class MonitorsDialog extends ModalDialog {
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  MultipleMonitorsPanel mpanel = new MultipleMonitorsPanel();
  public MonitorsDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    mpanel.timeDelay=26;
    CardiacMonitors cardiac = new CardiacMonitors(Current.body);
    mpanel.addVariable(Current.body.CVS.AP,"AP");
    mpanel.addVariable(Current.body.blood.SatO2,"SatO2");
    //mpanel.addVariable(cardiac.leadII,"Lead II");
    mpanel.addVariable(Current.body.lungs.lungVolume,"Lung Vol");
    mpanel.addVariable(Current.body.lungs.flow,"Flow");
    mpanel.timeDelay=50;
    setPreferredSize(new Dimension(400,300));
    setTitle("Bedside monitors");
    getRootPane().setDefaultButton(jButton1);
  }
  double lastTimeComp;
  public void show(){
    Clock clock = Current.body.clock;
    lastTimeComp = clock.getSecond();
    clock.setSecond(1000);
    clock.start();
    stopClock=false;
    super.show();
  }
  public void hide(){
    Clock clock=Current.body.clock;
    clock.setSecond(lastTimeComp);
    super.hide();
  }
  private void jbInit() throws Exception {
    jButton1.setVerifyInputWhenFocusTarget(true);
    jButton1.setText("Close");
    jButton1.addActionListener(new MonitorsDialog_jButton1_actionAdapter(this));
    jPanel1.setLayout(borderLayout1);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(mpanel, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(jButton1, null);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    hide();
  }

}

class MonitorsDialog_jButton1_actionAdapter implements java.awt.event.ActionListener {
  MonitorsDialog adaptee;

  MonitorsDialog_jButton1_actionAdapter(MonitorsDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

package phic.common.realtime;

import javax.swing.JPanel;
import phic.*;
import phic.common.realtime.MultipleMonitorsPanel;
import java.awt.BorderLayout;
import phic.common.realtime.CardiacMonitors;
import java.awt.Dimension;

/**
 * Panel to show a MonitorsPanel
 */

public class BedsideMonitorPanel extends JPanel {
  MultipleMonitorsPanel mp = new MultipleMonitorsPanel();
  CardiacMonitors cm;
  public BedsideMonitorPanel() {
    setLayout(new BorderLayout());
    add(mp);
    cm= new CardiacMonitors(Current.person.body);
    //mp.addVariable(cm.leadII,"Lead II");
    mp.addVariable(Current.body.blood.SatO2, "SatO2");
    mp.addVariable(Current.body.lungs.lungVolume, "Vol");
    mp.addVariable(Current.body.lungs.flow,"Flow");
    mp.addVariable(Current.body.CVS.AP, "AP");
    setPreferredSize(new Dimension(100,80));
  }

}
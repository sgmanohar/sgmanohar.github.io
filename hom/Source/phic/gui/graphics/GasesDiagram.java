package phic.gui.graphics;

import javax.swing.*;
import phic.gui.exam.*;
import medicine.Entity;
import phic.Body;
import java.awt.*;
import phic.common.Gas;
import phic.common.UnitConstants;
import phic.*;

public class GasesDiagram extends JPanel implements Examination {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JLabel jLabel2 = new JLabel();
  GasLabel tisgas = new GasLabel();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel3 = new JLabel();
  GasLabel vengas = new GasLabel();
  JPanel jPanel4 = new JPanel();
  JLabel jLabel4 = new JLabel();
  GasLabel artgas = new GasLabel();
  JPanel diagpanel = new JPanel(){
    double h1 = 0.25, h2 = 0.75; //proportion of height
    int aw = 5; //arrow width
    int lw = 5; //line width
    public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2= (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke(lw));
      g2.setColor(getForeground());
      int ph1 = (int)(getHeight()*h1), w = getWidth(), ph2 = (int)(getHeight()*h2);
      g2.drawLine(0, ph1, w-lw, ph1);
      g2.drawLine(w-aw-lw, ph1-aw, w-lw, ph1);
      g2.drawLine(w-aw-lw, ph1+aw, w-lw, ph1);
      g2.drawLine(lw, ph2, w, ph2);
      g2.drawLine(lw, ph2, aw+lw, ph2+aw);
      g2.drawLine(lw, ph2, aw+lw, ph2-aw);
    }
  };
  JPanel jPanel5 = new JPanel();
  JLabel jLabel1 = new JLabel();
  GasLabel alvgas = new GasLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JLabel jLabel5 = new JLabel();
  GasLabel atmgas = new GasLabel();
  public GasesDiagram() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * createPanel
   *
   * @return JPanel
   */
  public JPanel createPanel() {
    return this;
  }

  public Entity[] getPathologies() {
    return null;
  }
  public Entity[] getSigns() {
    return null;
  }

  /**
   * initialise
   *
   * @param body Body
   */
  public void initialise(Body body) {
    atmgas.setGas( Current.environment.airP );
    alvgas.setGas( body.lungs.alvP );
    artgas.setGas( body.blood.arterial.getPartials() );
    vengas.setGas( body.blood.venous.getPartials() );
    Gas tissue = new Gas();  // extemporising measure!
    tissue.O2.set(0.025); tissue.O2.setUnit(UnitConstants.METRES_HG);
    tissue.CO2.set(0.050);tissue.CO2.setUnit(UnitConstants.METRES_HG);
    tisgas.setGas( body.CVS.InterstitialP );
  }


  private void jbInit() throws Exception {
    this.setMaximumSize(new Dimension(32767, 32767));
    this.setLayout(borderLayout1);
    jLabel2.setText("Tissues");
    jLabel3.setText("Venous blood");
    jPanel3.setBackground(new Color(214, 153, 255));
    jPanel3.setPreferredSize(new Dimension(120, 50));
    jLabel4.setText("Arterial blood");
    jPanel4.setBackground(new Color(255, 183, 167));
    jPanel4.setPreferredSize(new Dimension(120, 50));
    jPanel1.setBackground(new Color(201, 235, 255));
    jPanel1.setLayout(borderLayout2);
    jPanel2.setBackground(new Color(224, 133, 106));
    jLabel1.setText("Alveolus");
    jPanel5.setBackground(new Color(194, 203, 255));
    jPanel6.setBackground(new Color(221, 255, 255));
    jLabel5.setText("Atmosphere");
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jPanel5, BorderLayout.CENTER);
    jPanel5.add(jLabel1, null);
    jPanel5.add(alvgas, null);
    jPanel1.add(jPanel6, BorderLayout.NORTH);
    jPanel6.add(jLabel5, null);
    jPanel6.add(atmgas, null);
    this.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jLabel2, null);
    jPanel2.add(tisgas, null);
    this.add(jPanel3, BorderLayout.WEST);
    jPanel3.add(jLabel3, null);
    jPanel3.add(vengas, null);
    this.add(jPanel4, BorderLayout.EAST);
    jPanel4.add(jLabel4, null);
    jPanel4.add(artgas, null);
    this.add(diagpanel, BorderLayout.CENTER);
  }
  public String getName(){
    return "Gases diagram";
  }
  public String toString(){ return getName();}
  public double getUpdateFrequencySeconds(){ return 0.2;}
}

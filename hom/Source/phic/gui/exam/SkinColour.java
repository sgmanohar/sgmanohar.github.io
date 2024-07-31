package phic.gui.exam;

import medicine.Entity;
import phic.Body;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import phic.Current;

/**
 * Create a skin colour.
 * The basic skin pigment colour is modified by the body's parameters.
 * The final colour depends on
 *
 *  skin.flow   - the amount that the blood alters the base pigment
 *  blood.Hb
 *  blood.SatO2 - controls the spectrum of light absorbed by blood
 *
 */
public class SkinColour extends JPanel implements Examination{
  public JLabel jLabel1 = new JLabel();
  public JPanel jPanel1 = new JPanel();
  public SkinColour() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public String toString(){
          return getName();
  }

  /**
   * createPanel
   *
   * @return JPanel
   */
  public JPanel createPanel() {
    return this;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "Skin colour";
  }

  public Entity[] getPathologies() {    return null;  }

  public Entity[] getSigns() {    return null;  }

  /**
   * initialise
   * creates the colour based on the body parameters.
   * @param body Body
   */
  public Color basePigment=new Color(255,225,215);
  Body body = null;
  public void initialise(Body body) {
    basePigment = Current.person.skinBasePigment;
    this.body=body;
    update();
  }
  public void update(){
    double hb = body.blood.Hb.get();
    double sat = body.blood.SatO2.get();
    double co = body.blood.CO.get();
    sat+=co; // carbon monoxide is red
    //absorption of haemoglobin (depends on sats):
    double rabs=255*Math.min(0.15,1-sat)/0.15, gabs=255, babs=255*Math.max(0,sat-0.875)/12.5;
    //vascularity
    double vasc= 0.0026 * Math.max(0,Math.min(1.7,0.100/body.skin.resistance.get()));
    hb=Math.max(0,(hb-60)*1.8);
    int r=basePigment.getRed()  -(int)(rabs*hb*vasc),
        g=basePigment.getGreen()-(int)(gabs*hb*vasc),
        b=basePigment.getBlue() -(int)(babs*hb*vasc);
    r=Math.max(Math.min(r,255),0); g=Math.max(Math.min(g,255),0); b=Math.max(Math.min(b,255),0);
    colour=new Color(r,g,b);
    jPanel1.setBackground(colour);
  }
  Color colour;
  /** Return the colour of the skin, once the examination has been initialised with the body. */
  public Color getColour(){return colour;}
  private void jbInit() throws Exception {
    jLabel1.setText("Skin colour:");
    jPanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    jPanel1.setPreferredSize(new Dimension(50, 50));
    this.add(jLabel1, null);
    this.add(jPanel1, null);
  }
  public double getUpdateFrequencySeconds(){ return 0.2;}

}

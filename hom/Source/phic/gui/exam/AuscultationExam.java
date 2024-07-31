package phic.gui.exam;

import javax.swing.*;
import medicine.Entity;
import phic.Body;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import phic.Resource;

/**
 * Auscultate the chest
 */

public class AuscultationExam extends JPanel implements Examination  {
  public AuscultationExam() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    image=Resource.loader.getImageResource("diagram/chest.jpg");
    jLabel1.setOpaque(true);
  }
  Image image;
  public JPanel createPanel() {
    return this;
  }
  Body body;
  public void initialise(Body body) {
    this.body=body;
    auscultation.setBody(body);
  }
  public Entity[] getPathologies() {
    return null;
  }
  public Entity[] getSigns() {
    return null;
  }
  public String getName() {
    return "Auscultation";
  }
  public double getUpdateFrequencySeconds() {
    return 6;
  }
  Auscultation auscultation = Auscultation.createAuscultation();
  JLabel jLabel1 = new JLabel(){
    public void paint(Graphics g){
      super.paint(g);
      g.drawImage(image, 0,0, getWidth(), getHeight(), AuscultationExam.this);
    }
  };
  BorderLayout borderLayout1 = new BorderLayout();
  private void jbInit() throws Exception {
    jLabel1.addMouseMotionListener(ml);
    jLabel1.addMouseListener(ml);
    this.setLayout(borderLayout1);
    this.add(jLabel1, BorderLayout.CENTER);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  class MyMouseListener      extends MouseAdapter      implements MouseMotionListener {
    public void mousePressed(MouseEvent e) {
      if(!body.clock.running) JOptionPane.showMessageDialog(null,"Please start the simulation in real-time mode.", "Simulation not running", JOptionPane.INFORMATION_MESSAGE);
      Point2D p = map(e.getPoint(), (Component)e.getSource());
      auscultation.setCoordinates(p.getX(),p.getY());
      auscultation.startSound();
    }

    public void mouseDragged(MouseEvent e) {
      Point2D p = map(e.getPoint(), (Component)e.getSource());
       auscultation.setCoordinates(p.getX(),p.getY());
    }

    public void mouseReleased(MouseEvent e) {
      auscultation.stopSound();
    }

    public void mouseMoved(MouseEvent e) {   }
  }
  public String toString(){ return "Auscultation of chest";}
  /** translate into coordinates 0-1 */
  public Point2D map(Point p, Component c){
    return new Point2D.Double(p.getX()/c.getWidth() - 0.5, p.getY()/c.getHeight() - 0.5);
  }

  MyMouseListener ml=new MyMouseListener();

}

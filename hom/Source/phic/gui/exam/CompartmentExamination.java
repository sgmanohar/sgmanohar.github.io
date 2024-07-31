package phic.gui.exam;

import javax.swing.JPanel;
import medicine.Entity;
import phic.Body;
import phic.gui.graphics.CompartmentDiagram;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sanjay Manohar</p>
 * <p>Company: CUDOS</p>
 * @author Sanjay Manohar
 * @version 1.0
 */

public class CompartmentExamination implements Examination {
  public CompartmentExamination() {
  }
  CompartmentDiagram panel;
  Body body;
  public JPanel createPanel() {
    if(panel==null) panel= new CompartmentDiagram();
    if(body!=null) panel.setBody(body);
    return panel;
  }
  public void initialise(Body body) {
    this.body=body;
    if(panel!=null)panel.setBody(body);
  }
  public Entity[] getPathologies() {
    return null;
  }
  public Entity[] getSigns() {
    return null;
  }
  public String getName() {
    return "Compartment diagram";
  }
  public String toString(){ return getName();}
  public double getUpdateFrequencySeconds() {
    return 0.2;
  }

}

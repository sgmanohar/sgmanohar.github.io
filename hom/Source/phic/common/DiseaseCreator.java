package phic.common;

import phic.modifiable.Script;
import java.util.*;
import phic.*;
import evaluator.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.ActionListener;
import phic.gui.PhicFrameSetup;

/**
 * A disease creator can create a disease in the current body.
 * The description of the diseases is read from the resource Diseases.txt.
 * Each section of Diseases.txt defines a disease that can be invoked.
 * The first line of the section is a description. Thereafter follows
 * a script, which is executed when the disease is invoked.
 */
public class DiseaseCreator {
  /** Create a disease-creator object */
  public DiseaseCreator() {
  }
  /**
   * Returns an array of strings which are the names of the diseases defined
   * in Diseases.txt.
   */
  public String[] getDiseaseList(){
    IniReader r=new IniReader("Diseases.txt");
    return r.getSectionHeaders();
  }
  public JMenu getMenu(){
    final Action[] diseaseActions = getDiseaseActions();
    // delegate command to disease actions
    ActionListener myal = new ActionListener(){public void actionPerformed(ActionEvent e){
        for(int i=0;i<diseaseActions.length;i++){
          if(diseaseActions[i].getValue(Action.NAME).equals(e.getActionCommand()))
            diseaseActions[i].actionPerformed(e);
        }
      }
    };
    // create a menu
    try{
      JMenu m = Resource.loader.createMenuFromIni("Diseases.txt", "Diseases",
                                                  myal, null);
      return m;
    }catch(IOException e){e.printStackTrace();}
    return null;
  }

  public Action[] getDiseaseActions(){
    String[] s= getDiseaseList();
    Action[] a = new Action[s.length];
    for(int i=0;i<a.length;i++){
      Disease d=new Disease(s[i]);
      a[i]=d.getAction();
    }
    return a;
  }

  /**
   * Represents the data for one disease.
   */
  public static class Disease {
    /** The script object that represents how to make the disease apparent */
    Script script;
    /** Name of the disease, as specified in Diseases.txt */
    String name;
    /** Description of the disease, on the first line of each section in Diseases.txt */
    String description="";
    /** Return the name of the disease */
    public String toString(){ return name; }
    /** The text of the script */
    String scriptText;

    /**
     * Construct a disease object, given the name. The data for the disease will
     * be read from the resource Diseases.txt using an IniReader.
     * The first line is read as the description, and the following lines are
     * treated as the script. Note that each line in the script should be
     * terminated with a semicolon.
     * Ignore lines beginning with Icon and Description
     */

    public Disease(String name){
      IniReader r=new IniReader("Diseases.txt");
      String[] s=r.getSectionStrings(name);
      Map map = r.getSectionMap(name);
      try{
        description = map.get("Description").toString().trim();
      }catch(Exception e){System.out.println("Disease "+name+" has no description");}
      script=new Script();
      StringBuffer sb=new StringBuffer();
      for(int i=0;i<s.length;i++) {
        if(!s[i].startsWith("Icon") && !s[i].startsWith("Description"))
        sb.append(s[i]);
      }
      scriptText=sb.toString();
      recompile();
      this.name=name;
    }
    /** Recompiles the script from scriptText */
    void recompile(){
      try{
        script.setText(scriptText);
      }catch(ParseException e){
        e.printStackTrace();
      }
    }
    /**
     * Invoke the disease. This method executes the script, and thereby makes
     * the disease apparent in the current patient.
     */
    public void invoke(){
      try{
        script.executeOnce();
        String m=showMessage?": "+description:"";
        if(PhicFrameSetup.treeIsShowing) m=": "+script.getText();
        Current.body.message("Disease "+name+m);
      }catch(MathException e){ e.printStackTrace(); }
      catch(StackException e){ e.printStackTrace(); }
    }
    public Action getAction(){
      return new DiseaseAction();
    }
    public boolean showMessage = false;
    protected class DiseaseAction extends AbstractAction{
      DiseaseAction(){
        super(name);
      }
      public void actionPerformed(ActionEvent e){
        recompile();
        if(script.getStatements().length < 1) JOptionPane.showMessageDialog(null,
            "Disease "+name+" not yet implemented", "Disease not implemented", JOptionPane.WARNING_MESSAGE);
        invoke();
      }
    };
  }
}

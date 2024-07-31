package phic.gui;

import javax.swing.*;
import java.awt.event.*;
import phic.*;
import phic.common.ConsciousLevelOptions;


/**
 * This menu controls items in the ConsciousLevelOptions.
 * It allows the user to select whether the patient can eat, drink, sleep and
 * wake, start exercising spontaneously.
 */

public class PermissionMenu extends JPopupMenu  {
  JRadioButtonMenuItem restrictall = new JRadioButtonMenuItem();
  JRadioButtonMenuItem allowall = new JRadioButtonMenuItem();
  JCheckBoxMenuItem eatdrink = new JCheckBoxMenuItem();
  JCheckBoxMenuItem sleep = new JCheckBoxMenuItem();
  JCheckBoxMenuItem exercise = new JCheckBoxMenuItem();
  public PermissionMenu(Person person) {
    this.person = person;
    uo=person.body.brain.getUnconscious();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public void addNotify(){
    updateTicks();
    updateRestrictAll();
    super.addNotify();
  }

  public void setPerson(Person p){
    person=p;
    uo=person.body.brain.getUnconscious();
    updateTicks();
    updateRestrictAll();
  }
  Person person;
  ConsciousLevelOptions uo;

  private void jbInit() throws Exception {
    restrictall.setAction(restrictAllAction);
    restrictall.setToolTipText("Prevents all spontaneous activity");
    restrictall.setVerifyInputWhenFocusTarget(true);
    allowall.setSelected(true);
    allowall.setToolTipText("Allow all spontaneous activity");
    allowall.setAction(allowAllAction);
    sleep.setToolTipText("Allow spontaneous falling asleep at night and waking in the morning");
    sleep.setAction(sleepAction);
    sleep.setState(false);
    exercise.setToolTipText("Allow spontaneous exercising while awake");
    exercise.setAction(exerciseAction);
    eatdrink.setAction(eatDrinkAction);
    eatdrink.setToolTipText("Allow spontaneous eating and drinking");
    this.add(restrictall);
    this.add(allowall);
    this.addSeparator();
    this.add(eatdrink);
    this.add(sleep);
    this.add(exercise);
  }

  /** This allows all spontaneous activity */
  public Action allowAllAction = new AbstractAction("Allow all"){
    public void actionPerformed(ActionEvent e){
      person.environment.NBM = false;
      person.environment.starve = false;
      uo.mobile = true;
      uo.allowSleep = true;
      updateTicks();
    }
  };
  /** This prevents any spontaneous activity */
  public Action restrictAllAction = new AbstractAction("Restrict all"){
    public void actionPerformed(ActionEvent e) {
      person.environment.NBM = true;
      uo.mobile = false;
      uo.allowSleep = false;
      person.body.brain.wakeUp();
      updateTicks();
    }
  };
  void updateTicks(){
    eatdrink.setSelected(!person.environment.NBM && !person.environment.starve);
    exercise.setSelected(uo.mobile);
    sleep.setSelected(uo.allowSleep);
  }
  /** This allows the patient to eat and drink spontaneously */
  public Action eatDrinkAction = new AbstractAction("Auto eat and drink"){
    public void actionPerformed(ActionEvent e){
      if(eatdrink.isSelected()){
        person.environment.NBM = false;
        person.environment.starve = false;
      }else{
        person.environment.NBM = true;
      }
      updateRestrictAll();
    }
  };
  /** This allows the patient to start and stop exercising automatically */
  public Action exerciseAction = new AbstractAction("Auto exercise"){
    public void actionPerformed(ActionEvent e) {
      uo.mobile = exercise.isSelected();
      updateRestrictAll();
    }
  };
  /** This allows the patient to fall asleep spontaneously */
  public Action sleepAction = new AbstractAction("Fall asleep and wake"){
    public void actionPerformed(ActionEvent e) {
      uo.allowSleep = sleep.isSelected();
      if(!uo.allowSleep) person.body.brain.wakeUp();
      updateRestrictAll();
    }
  };

  /**
   * Restrict all is selected only if all items are restricted. Allow all is
   * only selected if all items are allowed.
   */
  void updateRestrictAll(){
    if(!person.environment.NBM  && !person.environment.starve &&
       uo.allowSleep && uo.mobile){
      allowall.setSelected(true);
      restrictall.setSelected(false);
    }
    else if(person.environment.NBM && !uo.allowSleep && !uo.mobile){
      allowall.setSelected(false);
      restrictall.setSelected(true);
    }
    else {
      allowall.setSelected(false);
      restrictall.setSelected(false);
    }
  }
}

package phic.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import phic.*;

/**
 * The label that shows what the state of the brain is, with a coloured background.
 */
public class ConsciousStateLabel
    extends JButton {
  Border border4 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
  /** Create a label to display the conscious state */

  public ConsciousStateLabel() {
    //setBorder(border4);
    setPreferredSize(new Dimension(110, 21));
    setToolTipText("State of patient");
    setText("Conscious");
    setOpaque(true);
    timer.start();
    addActionListener(buttonAL);
    setMargin(new Insets(5,5,5,5));
  }

  /** The last feeling that was displayed */

  private int oldFeeling = -1;
  PermissionMenu menu;

  /**
   * This displays the menu when the button is pressed
   */
  ActionListener buttonAL = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if(menu == null) menu = new PermissionMenu(Current.person);
      else if(menu.isShowing()) {
        menu.setVisible(false);
        return;
      }
      menu.setLocation(0,0);
      menu.show(ConsciousStateLabel.this, 0, ConsciousStateLabel.this.getHeight());
    }
  };

  /** Track changes to the person, and relay them to the PermissionMenu */
  private Body oldperson;
  /**
   * When the timer ticks, check the brain's state using
   * @link Brain#getFeeling() Brain.getFeeling(), and update the label
   * with appropriate colours.
   */
  ActionListener timerAL = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if (menu!=null && oldperson != Current.body) {
        oldperson = Current.body;
        menu.setPerson(Current.person);
      }
      int f = Current.body.brain.getFeeling();
      if (f == oldFeeling) {
        return;
      }
      switch (f) {
        case Brain.DEAD:
          setText("Dead");
          setBackground(Color.red);
          break;
        case Brain.UNCONSCIOUS:
          setText("Unconscious");
          setBackground(Color.orange);
          break;
        case Brain.ILL:
          setText("Unwell");
          setBackground(Color.yellow);
          break;
        case Brain.WELL:
          if (!Current.body.brain.isAsleep()){
            String s = "Well";
            if(Current.body.brain.getUnconscious().mobile){
              if (Current.environment.Exer.get() > 0) s = "Exercising";
              if (Current.environment.Uprt.get() < 0.90) s = "Resting";
            }
            setText(s);
          } else setText("Asleep");
          setBackground(SystemColor.control);
          break;
      }
    }
  };

  /** A timer which updates the label every 1.5 seconds */
  Timer timer = new Timer(1500, timerAL);

  public static String stateStrings[] = new String[] {
      "Well", "Unwell", "Unconscious", "Dead"
  };
}

package phic.gui;
import phic.*;
import phic.common.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Allows the user to 'rewind' to previously stored times.
 * The times at which the state is stored are controlled by this class.
 */

public class RewindDialog extends ModalDialog implements ActionListener{
    public RewindDialog() {
        try {
          jbInit();
        }
        catch(Exception e) {
          e.printStackTrace();
        }
        setSize(200,200);
        timer.start();
        getRootPane().setDefaultButton(gotobutton);
    }

    /**
     * On show, fill the list with the times.
     */
    public void show(){
        model.removeAllElements();
        String[] s = history.getTimes();
        for(int i=0;i<s.length;i++)     model.addElement(s[i]);
        restorelist.setModel(model);
        super.show();
    }

    /**
     * Called by the frame when the clock is reset
     */
    public void reset(){
        history.removeAll();
    }

    /**
     * The interval at which the state is stored, in real-time milliseconds
     * Default: every 5 seconds.
     */
    private int saveInterval = 5000;



    /**
     * When this is true, storing of the system's state at regular intervals is enabled
     */
    public boolean enableStore=true;

    /** The data for stored states */
    DataHistory history = new DataHistory();

    /**
     * Do not attempt to store more than this number of system states.
     * Default = 120, so lasts for 10 minutes if stored every 5 seconds.
     */
    public static final int MAX_STORE_ITEMS = 60;

    /** The timer object that calls the action listener when a state needs storing */
    Timer timer = new Timer(saveInterval, this);

    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel2 = new JPanel();
    JPanel jPanel3 = new JPanel();
    Border border1;
    BorderLayout borderLayout2 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JList restorelist = new JList();
    JButton gotobutton = new JButton();
    JButton cancelbutton = new JButton();
  DefaultListModel model = new DefaultListModel();

    /** Called by the timer to store the current state */
    public void actionPerformed(ActionEvent e){
        if(Current.body.getClock().running
          && enableStore
          && history.getDataSize() <= MAX_STORE_ITEMS)
        history.storeSnapshot( Current.person );
    }
    private void jbInit() throws Exception {
      border1 = BorderFactory.createEmptyBorder(4,4,4,4);
      jPanel1.setLayout(borderLayout1);
      jPanel3.setBorder(border1);
      jPanel3.setLayout(borderLayout2);
      gotobutton.setText("Go to");
      gotobutton.addActionListener(new RewindDialog_gotobutton_actionAdapter(this));
      cancelbutton.setText("Cancel");
      cancelbutton.addActionListener(new RewindDialog_cancelbutton_actionAdapter(this));
      this.setResizable(true);
    this.setTitle("Rewind to time");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(gotobutton, null);
      jPanel2.add(cancelbutton, null);
      jPanel1.add(jPanel3, BorderLayout.CENTER);
      jPanel3.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(restorelist, null);
    }

    /** Go back in time to the selected time */
    void gotobutton_actionPerformed(ActionEvent e) {
        int i = restorelist.getSelectedIndex();
        history.restore(i, (SimplePhicFrame)getParentFrame() );
        history.removeAfter(i);
        hide();
    }

    void cancelbutton_actionPerformed(ActionEvent e) {
        hide();
    }
  public int getSaveInterval() {
    return saveInterval;
  }
  public void setSaveInterval(int saveInterval) {
    this.saveInterval = saveInterval;
    timer.setDelay(saveInterval);
  }


}

class RewindDialog_gotobutton_actionAdapter implements java.awt.event.ActionListener {
  RewindDialog adaptee;

  RewindDialog_gotobutton_actionAdapter(RewindDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.gotobutton_actionPerformed(e);
  }
}

class RewindDialog_cancelbutton_actionAdapter implements java.awt.event.ActionListener {
  RewindDialog adaptee;

  RewindDialog_cancelbutton_actionAdapter(RewindDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelbutton_actionPerformed(e);
  }
}

package phic.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import phic.*;

/**
 * Extension of JDialog used as a base class for phic modal dialogs
 * which must stop the clock when shown.
 */
public class NewModalDialog extends JInternalFrame{
  final JPanel glass=new JPanel();

  /** Sets dialog as modal. */
  public NewModalDialog(){
    //set modal
    //setModal(true); // from old JDialog.
    // Attach mouse listeners to block all events

    glass.setOpaque(false);
    MouseInputAdapter adapter=new MouseInputAdapter(){};
    glass.addMouseListener(adapter);
    glass.addMouseMotionListener(adapter);
    setClosable(true);
    setIconifiable(false);
    setMaximizable(true);
    setResizable(true);
    setFrameIcon(new ImageIcon(Resource.loader.getImageResource("SmallMan16.gif")));
  }

  public boolean isModal(){
    return modal;}

  public void setModal(boolean m){
    modal=m;}

  private boolean modal=true;
  boolean restartClock;

  /** Whether or not to stop the clock while this dialog shows. */
  public boolean stopClock=true;


  /** Get the parent frame */
  public JFrame getParentFrame(){    return PhicApplication.frame.getJFrame();}


  public void setVisible(boolean b){
    if(b) visible();
    else invisible();
  }
  public void show(){ setVisible(true);super.show();}

  /* Stops current body clock if needed, and centres on parent frame */
  private void visible(){
    //centre on parent frame
    JFrame parent=PhicApplication.frame.getJFrame();
    setLocation(parent.getX()+(parent.getWidth()-getWidth())/2,
      parent.getY()+(parent.getHeight()-getHeight())/2);

    //stop clock if necessary
    Body body=Current.body;
    restartClock=body.clock.running && stopClock;
    if(restartClock){
      body.setRunning(false);
    }


    // Change glass pane to our panel
    // Show glass pane, then our modal dialog
    glass.add(this);
    JRootPane rootPane =PhicApplication.frame.getJFrame().getRootPane();
    rootPane.setGlassPane(glass);
    glass.setVisible(true);
    super.show();

    //this is the modal bit. Stopped when stopModal() is called from hide()
    if(modal) startModal();

    //restore it
    if(glass!=null)glass.setVisible(false);
    glass.remove(this);

    if(restartClock){
      body.setRunning(true);
    }
  }

  private void invisible(){
    stopModal();
  }

  /** called to stop the current thread */
  private synchronized void startModal(){
    try{
      if(SwingUtilities.isEventDispatchThread()){
        EventQueue theQueue=getToolkit().getSystemEventQueue();
        while(isVisible()){
          AWTEvent event=theQueue.getNextEvent();
          Object source=event.getSource();
          if(event instanceof ActiveEvent){
            ((ActiveEvent)event).dispatch();
          } else if(source instanceof Component){
            ((Component)source).dispatchEvent(event);
          } else if(source instanceof MenuComponent){
            ((MenuComponent)source).dispatchEvent(event);
          } else{
            System.err.println("Unable to dispatch: "+event);
          }
        }
      } else{
        while(isVisible()){
          wait();
        }
      }
    } catch(InterruptedException ignored){
    }
  }

  private synchronized void stopModal(){
    notifyAll();
  }
}

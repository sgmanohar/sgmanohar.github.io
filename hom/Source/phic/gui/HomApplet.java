package phic.gui;

import java.applet.*;
import java.awt.*;
import java.lang.reflect.*;

/**
 * Web interface for PHIC. Displays an applet that loads HOMLauncher,
 * and calls its main() method. Currently no parameters are supported.
 *
 * This class requires only AWT and Applet classes.
 */

public class HomApplet extends Applet {
  String homclass = "phic.gui.HomLauncher";
  public String getAppletInfo(){ return
        "HOM Human Physiology Simulator, (c) Dr. Sanjay Manohar sgmanohar@hotmail.com";
  }
  String websiteInfo = "http://www.manohar.btinternet.com/hom/index.html";


  TextArea textArea1 = new TextArea();
  BorderLayout borderLayout1 = new BorderLayout();
  public HomApplet() throws HeadlessException {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  Object HOM;
  public void start(){
    Class cls;
    try{ //LOAD CLASS
      cls = Class.forName(homclass);
    }catch(ClassNotFoundException e){
      message(e+e.getMessage()+"\n"+classNotFound);
      e.printStackTrace();
      return;
    }
    try{ // CREATE INSTANCE
      HOM = cls.newInstance();
    }catch(InstantiationException e){
      message(e+e.getMessage()+"\n"+versionError);
      e.printStackTrace();
      return;
    }catch(IllegalAccessException e){
      message(e+e.getMessage()+"\n"+privilegeMessage);
      e.printStackTrace();
      return;
    }
    /* Parameter version - not working yet
    String[] params = new String[]{""};
    try { // INVOKE MAIN METHOD
      cls.getMethod("main", new Class[] {params.getClass()}).invoke(HOM,params);
    }
    catch (SecurityException ex) {
      message(ex+ex.getMessage()+"\n"+privilegeMessage);
      ex.printStackTrace();
      return;
    }
    catch (NoSuchMethodException ex) {
      message(ex+ex.getMessage()+"\n"+versionError);
      ex.printStackTrace();
      return;
    }
    catch (InvocationTargetException ex) {
      message(ex+ex.getMessage()+"\nCaused by "+ex.getCause().getMessage()+homException);
      ex.printStackTrace();
      return;
    }
    catch (IllegalArgumentException ex) {
      message(ex+ex.getMessage()+"\n"+versionError);
      ex.printStackTrace();
      return;
    }
    catch (IllegalAccessException ex) {
      message(ex+ex.getMessage()+"\n"+privilegeMessage);
      ex.printStackTrace();
      return;
    }
    */
    message(success);
  }
  public void stop(){
    message(stoppedMessage);
  }

  void message(String s){
    textArea1.setText(s);
    currentMessage=s;
  }
  String currentMessage = null;


  String classNotFound =
        "This may mean that the applet tag in your HTML file\n"
      + "does not give the correct CODEBASE or ARCHIVE path. It should\n"
      + "point to a 'Hom.jar' file or a 'classes' folder.\n"
      + "Specifically, a file is missing within this file/folder,\n"
      + "called " + homclass + "\n"
      + "Sometimes this error can occur if Java is not correctly\n"
      + "installed on your system. Ensure you have Java 1.2 or\n"
      + "later installed on your system. The software has been\n"
      + "tested with Jave versions up to 1.5."
      ;


  String success = "HOM is now starting in a separate Java window.\n"
      +"If you close this browser window, the separate\n"
      +"HOM window will also close.";

  String homException = "The HOM physiology model has a problem. This is\n"
      +"likely to be a programming error, or could be caused\n"
      +"by extraordinary circumstances while running the program\n"
      +"for example lack of computer resources, or overloading with messages.";

  String versionError = "This version of HOM is internally inconsistent.\n"
      +"This is likely a programming error, i.e. my fault.\n"+
      "Please contact the author at sgmanohar@hotmail.com,\n"
      +"and see the apology on the website.\n"+websiteInfo;

  String privilegeMessage = "You do not have the appropriate access privileges to"
        +"\nrun HOM. If this is an institutional machine, contact"
        +"\nyour system administrator. If this is a personal machine"
        +"\nYou will need to set up your 'java.policy' file. This"
        +"\ncan be found in your JRE/lib/security/ folder, or in"
        +"\nyour user home directory as '.java.policy'."
        +"\nCheck the website for further information: "
        +"\n"+websiteInfo;

  String initialText =
        "HOM Physiology Simulator\n"
      + "(c) Dr. Sanjay Manohar sgmanohar@hotmail.com\n"
      + "The java program will start in a new window.\n"
      + "Please see the website for further information.\n"
      + websiteInfo;

  String stoppedMessage=
      "The applet has been stopped by the browser.";

  private void jbInit() throws Exception {
    textArea1.setEditable(false);
    textArea1.setText(initialText);
    this.setLayout(borderLayout1);
    this.add(textArea1, BorderLayout.CENTER);
  }
}

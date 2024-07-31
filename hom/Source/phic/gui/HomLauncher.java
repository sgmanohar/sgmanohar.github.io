package phic.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import phic.Resource;
import phic.common.IniReader;
import javax.swing.SwingUtilities;
import javax.swing.border.*;

public class HomLauncher extends JFrame implements ActionListener{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton exitb = new JButton();
  public HomLauncher() throws HeadlessException {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setupButtons();
    pack();
    Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((d.width-getWidth())/2, (d.height-getHeight())/2);
    show();
  }
  /**
   *  If command line parameters exist, pass them to PhicApplication straight away
   * and start HOM. otherwise, present a list of options.
   */
  public static void main(String[] s){
    if(s.length>0){
      phic.gui.PhicApplication.main(s);
    }else
      new HomLauncher();
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5));
    this.getContentPane().setLayout(borderLayout1);
    exitb.setText("Exit");
    exitb.addActionListener(this);
    this.setTitle("HOM Setup");
    jPanel2.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    cardpanel.setLayout(cardLayout1);
    loadinglabel.setBackground(SystemColor.control);
    loadinglabel.setFont(new java.awt.Font("Arial", 1, 18));
    loadinglabel.setText("Loading HOM...");
    loadinglabel.setLineWrap(true);
    loadinglabel.setWrapStyleWord(true);
    loadingpanel.setLayout(borderLayout2);
    loadingpanel.setBorder(border1);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(exitb, null);
    this.getContentPane().add(cardpanel,  BorderLayout.CENTER);
    cardpanel.add(jPanel2,   "SelectorPanel");
    loadingpanel.add(loadinglabel,  BorderLayout.CENTER);
    cardpanel.add(loadingpanel,  "LoadingPanel");
  }

  void command(ActionEvent e) {
    String s=e.getActionCommand();
    if(s.equals("Exit")) {
      hide();
      dispose();
      try{System.exit(0);}catch(SecurityException ex){}
    }
    launch(e.getActionCommand());
    /*
        if(s.equals("Student Cardiovascular system")) launch("StudentCVS");
    if(s.equals("Student Temperature")) launch("StudentTemperature");
    if(s.equals("Student Diabetes")) launch("StudentDiabetes");
    if(s.equals("Teacher")) launch("Teacher");
        */
  }

  /**
   * Launch the PhicApplication main routine with the setup specified by the string s.
   * Ensure that this HomLauncher frame is hidden when the PhicFrame appears
   * Create a listener to re-show the HomLauncher when the PhicFrame is closed.
   */
  public void launch(String s){
    setCursor(Cursor.WAIT_CURSOR);
    cardLayout1.show(cardpanel, "LoadingPanel");
    repaint();
    loadinglabel.setText("Loading HOM: "+Resource.identifierToText(s));
    phic.gui.PhicApplication.main(new String[]{"setup",s});
    phic.gui.PhicApplication.frame.getJFrame().addWindowListener(phicFrameListener);
    if(PhicApplication.frame.getJFrame().isShowing()) {restoring();}
    phic.gui.PhicApplication.frame.addCloseListener(reshowListener);
  }

  /** if this is true, then when the main frame closes, the choice menu will be reshown */
  public static boolean reshowMenuOnClose = true;
  ActionListener reshowListener = new ActionListener(){public void actionPerformed(ActionEvent e){
      show();
    }};
  /** When the phic frame appears, hide this HomLauncher frame */
  WindowListener phicFrameListener = new WindowAdapter(){
    public void windowOpened(WindowEvent e){ restoring();  }
  };
  void restoring(){
    hide();
    setCursor(Cursor.DEFAULT_CURSOR);
    cardLayout1.show(cardpanel, "SelectorPanel");
  }

  String framesetupfile = "FrameSetup.txt";
  GridLayout gridLayout1 = new GridLayout();
  JPanel cardpanel = new JPanel();
  CardLayout cardLayout1 = new CardLayout();
  JPanel loadingpanel = new JPanel();
  JTextArea loadinglabel = new JTextArea();
  BorderLayout borderLayout2 = new BorderLayout();
  Border border1;

  public void setupButtons(){
    String[] items = Resource.loader.getStringList(framesetupfile,"HOMLauncher");
    IniReader r = new IniReader(framesetupfile);
    gridLayout1.setRows(items.length);
    for(int i=0;i<items.length;i++){
      JButton b = new JButton(Resource.identifierToText( items[i] ));
      b.setActionCommand(items[i] );
      b.addActionListener(this);
      try{
        String description = r.getSectionMap(items[i]).get("Description").toString();
        b.setToolTipText(description);
      }catch(NullPointerException e){System.out.println(items[i]+" in "+framesetupfile+" has no description");}
      jPanel2.add(b);
      if(i==0)getRootPane().setDefaultButton(b);
    }
  }
  public void actionPerformed(ActionEvent e){ command(e); }
}

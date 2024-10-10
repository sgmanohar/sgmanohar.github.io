/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.cudos.common.systems.*;
import javax.swing.border.*;
import java.util.*;

import java.io.*;
import javax.swing.Timer;
import java.net.URL;

public class SignalProcessingExhibit
    extends CudosExhibit
    implements SelectionRecipient {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JButton jButton1 = new JButton();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JPanel selectedproperties = new JPanel();
  JScrollPane componentscroll = new JScrollPane();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel sdpanel = new JPanel();
  JPanel jPanel7 = new JPanel();
  SystemsDiagram sd = new SystemsDiagram();
  BorderLayout borderLayout6 = new BorderLayout();
  Border border1;
  Border border2;
  BorderLayout borderLayout7 = new BorderLayout();
  JButton menubutton = new JButton();
  JPanel jPanel8 = new JPanel();
  JButton createcomponent = new JButton();
  JButton deletecomponent = new JButton();
  GraphPanel graphpanel = new GraphPanel();
  JPanel jPanel9 = new JPanel();
  JButton jButton2 = new JButton();
  JButton stop = new JButton();
  JPopupMenu mainpopup = new JPopupMenu();
  JMenu jMenu1 = new JMenu();
  JMenuItem menuopen = new JMenuItem();
  JMenuItem menunew = new JMenuItem();
  JMenuItem menusave = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JMenuItem menuconvert = new JMenuItem();

  JList complist = new JList(componentNames);
  private final static String[] componentTypes = {
      "Source", "FunctionSource","Plus",
      "Output", "Input", "Gain",

      "Integrator", "Differentiator", "SimpleTransferFunction",
      "FormulaTransferFunction",
      "Gate", "Subtract",  "Multiplier",
      "Divider", "Delay", "LowPassFilter", "HighPassFilter"
  };
  private final static String[] componentNames = {
      "Source", "Generic source", "Signal add",
      "Output", "Input", "Gain",

      "Integrator", "Differentiator", "Simple transfer function",
      "Generic transfer function",
      "Binary gate", "Comparator",  "Multiplier",
      "Divide", "Delay", "Low pass filter", "High pass filter"
  };

  Timer timer = new Timer(sd.timer.getDelay(), new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      sd.tick();
      graphpanel.tick();
    }
  });

  public String getExhibitName() {
    return "Signal Processing";
  }

  public SignalProcessingExhibit() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    componentscroll.getViewport().setView(complist);
    sd.setSelectionRecipient(this);
  }

  public void postinit() {
    sd.setApplet(getApplet());
    sd.postinit();
    menubutton.setIcon(new ImageIcon(getApplet().getImage(
        "resources/icons/arrow_up.gif")));
  }

  //selection recipient
  SystemsComponent selection;
  BorderLayout borderLayout8 = new BorderLayout();
  JMenuItem resetphases = new JMenuItem();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem editcomponent = new JMenuItem();
  public void setSelected(Object o) {
    if (selection != null) {
      selectedproperties.remove(selection.getPane());
    }
    editcomponent.setEnabled(false);
    if (o != null) {
      selection = (SystemsComponent) o;
      selectedproperties.add(selection.getPane());
      if (o instanceof SystemsCustomComponent) {
        editcomponent.setEnabled(true);
      }
    }
    else {
      selection = null;
    }
    validateTree();
    selectedproperties.repaint();
    soundmenu.setEnabled(selection instanceof com.cudos.common.systems.Output);
  }

  public Object getSelected() {
    return selection;
  }

  //Component creation
  public SystemsComponent createComponent(String classname) {
    SystemsComponent c = null;
    try {
      c = (SystemsComponent) Class.forName("com.cudos.common.systems." +
                                           classname).newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if (c != null) {
      sd.addComponent(c);
    }
    graphpanel.setComponents(sd.getSystemComponents());
    return c;
  }

  //Diagram handling
  void replaceDiagram(SystemsDiagram d) {
    //decouple old diagram
    sdpanel.remove(sd);
    sd.setSelectionRecipient(null);
    sd = d;
    //couple new diagram
    sd.setSelectionRecipient(this);
    sdpanel.add(sd);
    validateTree();
//		sd.setCParents(sdpanel);
    sd.postinit(); //functions that need calling after it is in the container!
//		sd.reloadAllImages();
    graphpanel.setComponents(sd.getSystemComponents());
    sd.setSelection(null); //remove selections
    sd.repaint();
    System.gc(); // if old diagram is discarded
  }

  /// JBuilder UI
  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.
                                                 createBevelBorder(BevelBorder.
        LOWERED, Color.white, Color.white, new Color(134, 134, 134),
        new Color(93, 93, 93)), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    border2 = BorderFactory.createEtchedBorder(Color.white,
                                               new Color(134, 134, 134));
    border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white,
                                              Color.white,
                                              new Color(134, 134, 134),
                                              new Color(93, 93, 93));
    this.getContentPane().setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    jButton1.setText("Exit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel3.setLayout(borderLayout3);
    jPanel5.setLayout(borderLayout4);
    jPanel1.setLayout(borderLayout5);
    sdpanel.setLayout(borderLayout6);
    sdpanel.setBorder(border1);
    createcomponent.setPreferredSize(new Dimension(80, 27));
    createcomponent.setText("Create");
    createcomponent.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        createcomponent_actionPerformed(e);
      }
    });
    deletecomponent.setText("Delete");
    deletecomponent.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        deletecomponent_actionPerformed(e);
      }
    });
    graphpanel.setPreferredSize(new Dimension(250, 70));
    jButton2.setText("Start");
    jButton2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        start(e);
      }
    });
    jPanel9.setPreferredSize(new Dimension(69, 100));
    stop.setText("Stop");
    stop.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        stop(e);
      }
    });
    jPanel7.setLayout(borderLayout7);
    componentscroll.setHorizontalScrollBarPolicy(JScrollPane.
                                                 HORIZONTAL_SCROLLBAR_NEVER);
    componentscroll.setVerticalScrollBarPolicy(JScrollPane.
                                               VERTICAL_SCROLLBAR_ALWAYS);
    menubutton.setPreferredSize(new Dimension(59, 27));
    menubutton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menubutton_actionPerformed(e);
      }
    });
    jMenu1.setMnemonic('F');
    jMenu1.setText("File");
    menuopen.setMnemonic('O');
    menuopen.setText("Open...");
    menuopen.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuopen_actionPerformed(e);
      }
    });
    menunew.setMnemonic('N');
    menunew.setText("New");
    menunew.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menunew_actionPerformed(e);
      }
    });
    menusave.setMnemonic('S');
    menusave.setText("Save...");
    menusave.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menusave_actionPerformed(e);
      }
    });
    jMenu2.setMnemonic('E');
    jMenu2.setText("Edit");
    menuconvert.setMnemonic('I');
    menuconvert.setText("Import component...");
    menuconvert.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuconvert_actionPerformed(e);
      }
    });
    selectedproperties.setLayout(borderLayout8);
    resetphases.setMnemonic('R');
    resetphases.setText("Reset phases");
    resetphases.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        resetphases_actionPerformed(e);
      }
    });
    jMenuItem1.setMnemonic('X');
    jMenuItem1.setText("Exit");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    editcomponent.setEnabled(false);
    editcomponent.setMnemonic('E');
    editcomponent.setText("Edit component");
    editcomponent.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editcomponent_actionPerformed(e);
      }
    });
    infobar.setText("Ready");
    jPanel6.setLayout(borderLayout9);
    jPanel6.setBorder(border3);
    returntoparent.setEnabled(false);
    returntoparent.setMnemonic('B');
    returntoparent.setText("Back");
    returntoparent.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        returntoparent_actionPerformed(e);
      }
    });
    sd.setFont(new java.awt.Font("SansSerif", 0, 10));
    soundmenu.setText("Sound");
    soundmenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        soundmenu_actionPerformed(e);
      }
    });
    jMenu3.setText("Help");
    helpcomponent.setText("Component...");
    helpcomponent.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        helpcomponent_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(sdpanel, BorderLayout.CENTER);
    sdpanel.add(sd, BorderLayout.CENTER);
    jPanel1.add(jPanel7, BorderLayout.SOUTH);
    jPanel7.add(jPanel9, BorderLayout.WEST);
    jPanel9.add(menubutton, null);
    jPanel9.add(jButton2, null);
    jPanel9.add(stop, null);
    jPanel7.add(graphpanel, BorderLayout.CENTER);
    jPanel7.add(jPanel6, BorderLayout.SOUTH);
    jPanel6.add(infobar, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.EAST);
    jPanel2.add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(jPanel5, BorderLayout.CENTER);
    jPanel5.add(componentscroll, BorderLayout.CENTER);
    jPanel5.add(jPanel8, BorderLayout.SOUTH);
    jPanel8.add(createcomponent, null);
    jPanel8.add(deletecomponent, null);
    jPanel3.add(selectedproperties, BorderLayout.SOUTH);
    jPanel2.add(jPanel4, BorderLayout.SOUTH);
    jPanel4.add(jButton1, null);
    mainpopup.add(jMenu1);
    mainpopup.add(jMenu2);
    mainpopup.add(jMenu3);
    jMenu1.add(menunew);
    jMenu1.add(menuopen);
    jMenu1.add(menusave);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem1);
    jMenu2.add(soundmenu);
    jMenu2.add(resetphases);
    jMenu2.addSeparator();
    jMenu2.add(menuconvert);
    jMenu2.add(editcomponent);
    jMenu2.add(returntoparent);
    jMenu3.add(helpcomponent);
    complist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  // handlers
  void jButton1_actionPerformed(ActionEvent e) {
    timer.stop();
    getApplet().toChooser();
  }

  void start(ActionEvent e) {
    start();
  }

  public void start() {
    timer.start();
  }

  void createcomponent_actionPerformed(ActionEvent e) {
    doCreate();
  }

  public SystemsComponent doCreate() {
    return createComponent(componentTypes[complist.getSelectedIndex()]);
  }

  void deletecomponent_actionPerformed(ActionEvent e) {
    if (selection != null) {
      removeComponent(selection);
    }
  }

  public void removeComponent(SystemsComponent sc) {
    sd.removeComponent( (SystemsComponent) selection);
    graphpanel.setComponents(sd.getSystemComponents());
  }

  void stop(ActionEvent e) {
    stop();
  }

  public void stop() {
    timer.stop();
  }

  void menunew_actionPerformed(ActionEvent e) {
    filenew();
  }

  void menuconvert_actionPerformed(ActionEvent e) {
    importcomponent();
  }

  //filing system
  boolean filenew() { //ask to clear diagram then do it
    int ok = JOptionPane.showConfirmDialog(this,
        "This will erase the current diagram",
                                           "Confirm clear diagram",
                                           JOptionPane.OK_CANCEL_OPTION,
                                           JOptionPane.WARNING_MESSAGE);
    if (ok == JOptionPane.OK_OPTION) {
      sd.clearDiagram();
    }
    graphpanel.setComponents(sd.getSystemComponents());
    return ok == JOptionPane.OK_OPTION;
  }

  public void importcomponent() {
    try {
      InputStream file = openFileDialog("Import component");
      if (file != null) {
        SystemsDiagram nsd = loadFromFile(file);
        if (nsd != null) {
          SystemsCustomComponent sc = (SystemsCustomComponent)
              createComponent("com.cudos.common.systems.SystemsCustomComponent");
          if (sc != null) { //successfully created
            if (!sc.setDiagram(nsd)) { //could not use this diagram
              removeComponent(sc);
            }
            else { //correctly initialised
              sc.setType(lastFileTransactionName);
              sc.postload();
            }
          }
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(
          this, e.toString(), "Could not import item",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  void menuopen_actionPerformed(ActionEvent e) {
    if (filenew()) {
      try {
        InputStream file = openFileDialog("Open Systems Diagram");
        if (file != null) {
          SystemsDiagram nsd = loadFromFile(file);
          replaceDiagram(nsd);
        }
      }
      catch (IOException x) {
        x.printStackTrace();
        JOptionPane.showMessageDialog(
            this, x.toString(), "Could not open item",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void menusave_actionPerformed(ActionEvent e) {
    try {
      OutputStream file = saveFileDialog("Save Systems Diagram");
      saveToFile(sd, file);
    }
    catch (IOException x) {
      x.printStackTrace();
      JOptionPane.showMessageDialog(
          this, x.toString(), "Could not save diagram",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public void saveToFile(SystemsDiagram s, OutputStream fos) throws IOException {
    try {
      //FileOutputStream fos=new FileOutputStream(file);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(s.getSystemComponents());
      oos.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public SystemsDiagram loadFromFile(InputStream fis) {
    SystemsDiagram nsd = new SystemsDiagram(); ;
    nsd.setApplet(getApplet());
    try {
      //FileInputStream fis=new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(fis);
      nsd.setSystemComponents( (Vector) ois.readObject());
      ois.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    nsd.initialiseComponents();
    return nsd;
  }

  void menubutton_actionPerformed(ActionEvent e) {
    Point tl = menubutton.getLocationOnScreen();
    Point t2 = this.getLocationOnScreen();
    tl.x -= t2.x;
    tl.y -= t2.y;
    tl.y -= mainpopup.getHeight();
    mainpopup.show(this, tl.x, tl.y);
  }

  static final String defaultFolder =
      "E:/Java/WebRoot/CUDOS/CLASSES/resources/SystemsComponents";
  OutputStream saveFileDialog(String title) throws IOException {
    JFileChooser chooser = new JFileChooser(
        new File(defaultFolder));
    chooser.setDialogTitle(title);
    int returnVal = chooser.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      lastFileTransactionName = chooser.getSelectedFile().getName();
      return new FileOutputStream(chooser.getSelectedFile());
    }
    else {
      return null;
    }
  }

  String lastFileTransactionName = "File";
  InputStream openFileDialog(String title) throws IOException {
    JFileChooser chooser = new JFileChooser(
        new File(defaultFolder));
    chooser.setDialogTitle(title);
    int returnVal = chooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      lastFileTransactionName = chooser.getSelectedFile().getName();
      return new FileInputStream(chooser.getSelectedFile());
    }
    else {
      return null;
    }

  }

  void resetphases_actionPerformed(ActionEvent e) {
    sd.resetAllPhases();
  }

  Vector editstack = new Vector();
  JPanel jPanel6 = new JPanel();
  JLabel infobar = new JLabel();
  BorderLayout borderLayout9 = new BorderLayout();
  Border border3;
  JMenuItem returntoparent = new JMenuItem();
  JMenuItem soundmenu = new JMenuItem();
  JMenu jMenu3 = new JMenu();
  JMenuItem helpcomponent = new JMenuItem();
  void editcomponent_actionPerformed(ActionEvent e) {
    SystemsCustomComponent scc = (SystemsCustomComponent) selection;
    editstack.add(sd);
    scc.getDiagram().setDiagramName(scc.getName());
    replaceDiagram(scc.getDiagram());
    infobar.setText("Editing " + getEditPath(editstack));
    returntoparent.setEnabled(true);
  }

  void returntoparent_actionPerformed(ActionEvent e) {
    SystemsDiagram scd = (SystemsDiagram) editstack.get(editstack.size() - 1);
    editstack.remove(scd);
    replaceDiagram(scd);
    if (editstack.isEmpty()) {
      returntoparent.setEnabled(false);
      infobar.setText("Ready");
    }
    else {
      infobar.setText("Editing " + getEditPath(editstack));
    }
  }

  String getEditPath(Vector v) {
    String s = "";
    for (int i = 0; i < v.size(); i++) {
      s += "/" + ( (SystemsDiagram) v.get(i)).getDiagramName();
    }
    return s + "/" + sd.getDiagramName();
  }

  void soundmenu_actionPerformed(ActionEvent e) {
    if (selection == null)return;
    final boolean t = timer.isRunning();
    if (t) stop();
    Thread thread=new  Thread(new Runnable(){ public void run(){
        byte[] p = new byte[22000];
        int o=0,oo=0;
        for (int i = 0; i < p.length; i++) {
          o=(short)(0x1800*selection.getOutput());
          p[i++] = (byte)(o %256);
          p[i]   = (byte)(o /256);
          sd.tick();
          oo=o;
        }
        CustomSound s = new CustomSound(p);
        s.playOnce();
        try{  Thread.sleep(1000); }
        catch(Exception e){e.printStackTrace();}
        s.close();
        if(t) start();
      }
    }); thread.start();
  }

  void helpcomponent_actionPerformed(ActionEvent e) {
    String typename;
    if(selection==null)typename="SystemsHelp";else typename=selection.getType();
      URL resource = getApplet().getResourceURL(
                             "resources/text/" + typename + ".html");
      HTMLMessagePane.showDialog(resource,
                                 "Help on component " + typename,this);
  }

}

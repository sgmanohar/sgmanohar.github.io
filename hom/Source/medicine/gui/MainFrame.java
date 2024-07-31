/**
 * Title:  \t  ^t<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;

import java.io.*;
import java.util.zip.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import medicine.*;
import medicine.visual.*;

public class MainFrame
    extends JFrame
    implements ActionListener {
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem savemenuitem = new JMenuItem();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenuItem jMenuItem5 = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JMenuItem jMenuItem6 = new JMenuItem();
  public EntityEditorPanel editor = new EntityEditorPanel();
  JMenu jMenu3 = new JMenu();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenuItem jMenuItem7 = new JMenuItem();
  JMenuItem jMenuItem8 = new JMenuItem();
  JMenuItem jMenuItem9 = new JMenuItem();
  JMenuItem jMenuItem10 = new JMenuItem();
  JMenuItem jMenuItem11 = new JMenuItem();
  JMenu jMenu4 = new JMenu();
  JMenuItem jMenuItem4 = new JMenuItem();
  JMenuItem jMenuItem12 = new JMenuItem();
  JMenuItem jMenuItem13 = new JMenuItem();
  JMenuItem jMenuItem14 = new JMenuItem();
  private JMenuItem jMenuItem15 = new JMenuItem();
  private JMenu jMenu5 = new JMenu();
  private JMenuItem causetree_menu = new JMenuItem();
  private JMenuItem effect_tree_menu = new JMenuItem();
  private JMenuItem diagnoseMenu = new JMenuItem();

  String defaultDirectory = "c:\\";
  String appTitle = "Medical Browser";

  public JFileChooser filer;
  boolean filingEnabled = true;
  public MainFrame(boolean enableFiling) {
    filingEnabled = enableFiling;
    initialise();
  }

  public MainFrame() {
    initialise();
  }

  protected void initialise() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    setSize(500, 400);
    editor.navigator.setEntity(rootEntity);

    //setup file dialogs
    if (filingEnabled) {
      filer = new JFileChooser(defaultDirectory);
      filer.addChoosableFileFilter(textFilter);
      filer.addChoosableFileFilter(databaseFilter);
      filer.addChoosableFileFilter(zipFilter);
      filer.setCurrentDirectory(new File(defaultDirectory));
    }

    //setup keyboard shorcuts
    editor.registerKeyboardAction(this, "Save",
                                  KeyStroke.getKeyStroke(KeyEvent.VK_S,
        KeyEvent.CTRL_MASK),
                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    editor.registerKeyboardAction(this, "Open", KeyStroke.getKeyStroke(
        KeyEvent.VK_O, KeyEvent.CTRL_MASK),
                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    editor.registerKeyboardAction(this, "New",
                                  KeyStroke.getKeyStroke(KeyEvent.VK_N,
        KeyEvent.CTRL_MASK),
                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    editor.registerKeyboardAction(this, "AFind",
                                  KeyStroke.getKeyStroke(KeyEvent.VK_F,
        KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK),
                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

  }

  private void jbInit() throws Exception {
    jMenu1.setText("File");
    jMenuItem1.setText("Open...");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        fileopen(e);
      }
    });
    savemenuitem.setEnabled(false);
    savemenuitem.setText("Save");
    savemenuitem.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        filesave(e, false);
      }
    });
    jMenuItem3.setText("Save as...");
    if (!filingEnabled) {
      jMenuItem3.setEnabled(false);
    }
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        fileSaveas(e);
      }
    });
    jMenuItem5.setText("Exit");
    jMenu2.setText("Edit");
    jMenuItem6.setText("New");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        filenew(e);
      }
    });
    jMenuItem5.addActionListener(this);
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setJMenuBar(jMenuBar1);
    this.setTitle(appTitle);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    jMenu3.setText("Help");
    jMenuItem2.setText("Return to first node");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        returntoFirstNode(e);
      }
    });
    jMenuItem7.setText("About...");
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jMenuItem7_actionPerformed(e);
      }
    });
    jMenuItem8.setText("Copy");
    jMenuItem8.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editcommand(e);
      }
    });
    jMenuItem9.setText("Paste");
    jMenuItem9.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editcommand(e);
      }
    });
    jMenuItem10.setText("Delete");
    jMenuItem10.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editcommand(e);
      }
    });
    jMenuItem11.setText("Bookmark");
    jMenuItem11.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editcommand(e);
      }
    });
    jMenu4.setText("Tools");
    jMenuItem4.setText("Visualiser");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        toolsvisualiser(e);
      }
    });
    jMenuItem12.setText("Statistics");
    jMenuItem12.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        toolsstats(e);
      }
    });
    jMenuItem13.setText("Find...");
    jMenuItem13.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editfind(e);
      }
    });
    jMenuItem14.setText("Advanced find...");
    jMenuItem14.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        editadvancedfind(e);
      }
    });
    jMenuItem15.setEnabled(false);
    jMenuItem15.setText("Cause list");
    jMenuItem15.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        causelist(e);
      }
    });
    jMenu5.setText("Lists");
    causetree_menu.setText("Cause tree");
    causetree_menu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        causetree_menu_actionPerformed(e);
      }
    });
    effect_tree_menu.setText("Effect tree");
    effect_tree_menu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        effect_tree_menu_actionPerformed(e);
      }
    });
    diagnoseMenu.setText("Diagnose");
    diagnoseMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        diagnoseMenu_actionPerformed(e);
      }
    });
    essaymenu.setText("Essay...");
    essaymenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        essaymenu_actionPerformed(e);
      }
    });
    jMenuItem16.setActionCommand("Dictionary...");
    jMenuItem16.setText("Dictionary...");
    jMenuItem16.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dictionary_actionPerformed(e);
      }
    });
    jMenuItem17.setText("Merge files...");
    jMenuItem17.addActionListener(this);
    filterfilndmenu.setText("Filter find...");
    filterfilndmenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        filterFind(e);
      }
    });
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenuBar1.add(jMenu4);
    jMenuBar1.add(jMenu3);
    jMenu1.add(jMenuItem6);
    jMenu1.add(jMenuItem1);
    jMenu1.add(savemenuitem);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem17);
    jMenu1.add(jMenuItem3);
    jMenu1.add(jMenuItem5);
    this.getContentPane().add(editor, BorderLayout.CENTER);
    jMenu3.add(jMenuItem2);
    jMenu3.addSeparator();
    jMenu3.add(jMenuItem7);
    jMenu2.add(jMenuItem8);
    jMenu2.add(jMenuItem9);
    jMenu2.add(jMenuItem10);
    jMenu2.addSeparator();
    jMenu2.add(jMenuItem11);
    jMenu2.add(jMenuItem13);
    //jMenu2.add(jMenuItem14); //Advanced find - superseded by filterfindmenu
    jMenu2.add(filterfilndmenu);
    jMenu2.add(jMenuItem16);
    jMenu4.add(jMenuItem4);
    jMenu4.add(jMenuItem12);
    jMenu4.add(jMenu5);
    jMenu4.add(diagnoseMenu);
    jMenu4.add(essaymenu);
    jMenu5.add(jMenuItem15);
    jMenu5.add(causetree_menu);
    jMenu5.add(effect_tree_menu);
  }

  /**
   *Start with a blank document
   */
  Entity rootEntity = new Entity(null, 0);
  String currentFilename = null;

  /**
   * Command handlers
   */

  public void actionPerformed(ActionEvent e) {
    String s = e.getActionCommand();
    if (s.equals("Open")) {
      fileopen(e);
    }
    if (s.equals("Save")) {
      filesave(e, false);
    }
    if (s.equals("New")) {
      filenew(e);
    }
    if (s.equals("AFind")) {
      editadvancedfind(e);
    }
    if (s.equals("Exit")) {
      shutdown();
    }
    if (s.equals("Merge files...")) {
      mergeFiles();
    }
  }

  /**
   * Filing operations
   */

  void filenew(ActionEvent e) {
    if (discardcurrent()) {
      //root element
      rootEntity = new Entity(null, 0);
      editor.navigator.setEntity(rootEntity);
      setFilename(null);
    }
  }

  boolean filesave(ActionEvent e, boolean wait) {
    if (currentFilename != null && currentFilename != "") {
      return writeCurrentFile(null, wait);
    } else {
      return fileSaveas(e);
    }
  }

  /**
   * save as
   */
  Entities saveEntities;
  boolean fileSaveas(ActionEvent e) {
    int res = filer.showSaveDialog(this);
    if (res == JFileChooser.APPROVE_OPTION) {
      String fname = filer.getSelectedFile().getAbsolutePath();
      currentFileFormat = filer.getFileFilter();
      if (currentFileFormat == textFilter && !fname.endsWith(".txt")) {
        fname += ".txt";
      }
      if (currentFileFormat == zipFilter && !fname.endsWith(".zip")) {
        fname += ".zip";
      }
      return writeCurrentFile(fname, false);
    } else {
      return false;
    }
  }

  /**
   * Delegates to various save formats; catches the errors.
   * If optionalFileName is null, use filer.getSelectedFile()
   * If wait is false, the save is asynchronous.
   */
  private boolean writeCurrentFile(String optionalFileName, boolean wait) {
    File file = filer.getSelectedFile();
    if (optionalFileName != null) {
      filer.setSelectedFile(file = new File(optionalFileName));
    }
    boolean success = true;
    try {
      OutputStream os = new FileOutputStream(file);
      if (currentFileFormat == databaseFilter) {
        //save as database
        success = writeDatabaseStream(os);
      } else {
        //save as text file
        saveEntities = new Entities();
        SaveListener sl = new SaveListener(os);
        synchronized (sl) {
          sl.finished = false;
          saveEntities.setVector(editor.navigator.entity, sl);
          if (wait) {
            try {
              while (!sl.finished) {
                sl.wait();
              }
            } catch (InterruptedException ex) {}
          }
        }
      }
      if (success) {
        setFilename(file.getAbsolutePath());
      }
      return success;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(thisFrame, ex.getMessage(), "Save error",
                                    JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
    return false;
  }

  JFrame thisFrame = this;

  /**
   * Save asynchronously to a given OutputStream.
   * Does not set the filename.
   */
  class SaveListener
      implements ActionListener {
    OutputStream os;
    boolean finished = false;
    SaveListener(OutputStream s) {
      os = s;
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == saveEntities) {
        doSave();
      } else {
        finished = true; //ILLEGAL! should be synchronized
      }
    }

    private synchronized void doSave() throws HeadlessException {
      try {
        if (currentFileFormat == zipFilter) {
          ZipOutputStream zos = new ZipOutputStream(os);
          zos.putNextEntry(new ZipEntry("Medical.txt"));
          saveEntities.writeTextForm(zos);
          zos.closeEntry();
          zos.close();
        } else {
          saveEntities.writeTextForm(os);
        }
        os.close();
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(thisFrame, ex.getMessage(), "Save error",
                                      JOptionPane.ERROR_MESSAGE);
      }
      finally {
        finished = true;
        notify();
      }
    }
  };

  /** Delegate to various file formats. Catches errors. */
  void fileopen(ActionEvent e) {
    int res = filer.showOpenDialog(this);
    try {
      if (res == JFileChooser.APPROVE_OPTION) {
        InputStream stream = new FileInputStream(filer.getSelectedFile());
        String fname = filer.getSelectedFile().getAbsolutePath();
        if (filer.getFileFilter() == databaseFilter) {
          readDatabaseFrom(stream, fname);
        } else if (filer.getFileFilter() == zipFilter) { //read zip file
          readTextFromZip(stream, fname);
        } else { //read text file
          readTextFromStream(stream, fname);
        }
      }
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(thisFrame, ex.getMessage(), "Open error",
                                    JOptionPane.ERROR_MESSAGE);
    } catch (ClassNotFoundException ex) {
      JOptionPane.showMessageDialog(thisFrame, ex.getMessage(),
                                    "Incorrect file format",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  public void readTextFromZip(InputStream file, String name) throws IOException {
    ZipInputStream s = new ZipInputStream(file);
    s.getNextEntry();
    readTextFromStream(s, name);
  }

  public void readTextFromStream(InputStream s, String name) throws IOException {
    Cursor oldcursor = getCursor();
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Entity newEntity = Entities.readTextForm(s);
      if (newEntity == null) {
        throw new IOException("Stream " + name + " contains no data");
      }
      editor.navigator.setEntity(newEntity);
      setFilename(name);
    }
    finally {
      setCursor(oldcursor);
    }
  }

  public boolean writeDatabaseStream(OutputStream fos) throws IOException {
    try {
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(rootEntity);
      fos.close();
      return true;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Save error",
                                    JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
      return false;
    }
  }

  public void readDatabaseFrom(InputStream is, String name) throws
      ClassNotFoundException, IOException {
    Cursor oldcursor = getCursor();
    try {
      ObjectInputStream ois = new ObjectInputStream(is);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      rootEntity = (Entity) ois.readObject();
      is.close();
      editor.navigator.setEntity(rootEntity);
      setFilename(name);
    }
    finally {
      setCursor(oldcursor);
    }
  }

  /** Merge current file (text format only) with another file */
  void mergeFiles() {
    File currFile = filer.getSelectedFile();
    FileFilter currFilter = filer.getFileFilter();
    if (currentFileFormat == databaseFilter) {
      requireTextFilterMessage();
      return;
    }
    try {
      InputStream s1 = new FileInputStream(filer.getSelectedFile());
      if (currentFileFormat == textFilter) {
        ZipInputStream s = new ZipInputStream(s1);
        s.getNextEntry();
        s1 = s;
      }
      int res = filer.showOpenDialog(this);
      if (res == JFileChooser.APPROVE_OPTION) {
        InputStream s2 = new FileInputStream(filer.getSelectedFile());
        if (filer.getFileFilter() == databaseFilter) {
          requireTextFilterMessage();
          return;
        } else if (filer.getFileFilter() == zipFilter) { //read zip file
          ZipInputStream s = new ZipInputStream(s2);
          s.getNextEntry();
          s2 = s;
          Entities.mergeTextFromStreams(s1, s2);
        } else { //read text file
          Entities.mergeTextFromStreams(s1, s2);
        }
      }
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(thisFrame, ex.getMessage(), "Open error",
                                    JOptionPane.ERROR_MESSAGE);
    }
    finally {
      filer.setSelectedFile(currFile);
      filer.setFileFilter(currFilter);
    }
  }

  private void requireTextFilterMessage() throws HeadlessException {
    JOptionPane.showMessageDialog(thisFrame,
                                  "You must select a text file for merging.",
                                  "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Display a message asking whether or not to discard the current data. Return true if OK.
   */
  boolean discardcurrent() {
    int r = JOptionPane.showConfirmDialog(this,
                                          "This will clear the current data.",
                                          "Discard current document",
                                          JOptionPane.OK_CANCEL_OPTION,
                                          JOptionPane.WARNING_MESSAGE);
    if (r == JOptionPane.OK_OPTION) {
      return true;
    } else {
      return false;
    }
  }

  void setFilename(String s) {
    currentFilename = s;
    if (s != null && s != "") {
      if (filingEnabled) {
        savemenuitem.setEnabled(true);
      }
      setTitle(appTitle + " - " + s);
      filer.setSelectedFile(new File(s));
    } else {
      savemenuitem.setEnabled(false);
      setTitle(appTitle + " - [None]");
    }
  }

  FileFilter databaseFilter = new FileFilter() {
    public boolean accept(File file) {
      if (file.getName().endsWith(".med")) {
        return true;
      }
      return false;
    }

    public String getDescription() {
      return "Medical database (*.med)";
    }
  };
  FileFilter zipFilter = new FileFilter() {
    public boolean accept(File file) {
      return file.getName().endsWith(".zip");
    }

    public String getDescription() {
      return "Zipped text database (*.zip)";
    }
  };
  FileFilter textFilter = new FileFilter() {
    public boolean accept(File file) {
      if (file.getName().endsWith(".txt")) {
        return true;
      }
      return false;
    }

    public String getDescription() {
      return "Text file database (*.txt)";
    }
  };
  FileFilter currentFileFormat = zipFilter;

  void returntoFirstNode(ActionEvent e) {
    editor.navigator.setEntity(rootEntity);
  }

  void jMenuItem7_actionPerformed(ActionEvent e) {
    new AboutBox().show();
  }

  /**
   Pass on commands such as Copy, Paste, Delete, Bookmark to the editor pane
   stating the current selected list as the source component
   */
  void editcommand(ActionEvent e) {
    JList l = editor.navigator.getFocusedList();
    if (l != null) {
      ActionEvent ae = new ActionEvent(l, e.getID(), e.getActionCommand(),
                                       e.getModifiers());
      editor.actionPerformed(e);
    }
  }

  void toolsvisualiser(ActionEvent e) {
    VisualiserFrame v = new VisualiserFrame();
    v.visualiserPane1.setEntity(editor.navigator.entity);
    v.show();
  }

  void toolsstats(ActionEvent e) {
    new StatisticsDialog(editor.navigator.entity).show();
  }

  void editfind(ActionEvent e) {
    editor.navigator.find();
  }

  void editadvancedfind(ActionEvent e) {
    FindDialog fd = new AdvancedFindDialog(editor.navigator.entity);
    fd.setModal(true);
    fd.show();
    if (fd.entity != null) {
      editor.navigator.setEntity(fd.entity);
    }
  }

  void causelist(ActionEvent e) {
    CauseListDialog cld = new CauseListDialog();
    cld.setEntity(editor.navigator.entity);
    cld.show();
  }

  void effect_tree_menu_actionPerformed(ActionEvent e) {
    CauseTreeDialog d = new CauseTreeDialog();
    d.relations = Entity.EFFECT | Entity.PARENT | Entity.CHILD;
    d.setEntity(this.editor.navigator.entity);
    d.show();
  }

  void causetree_menu_actionPerformed(ActionEvent e) {
    CauseTreeDialog d = new CauseTreeDialog();
    d.relations = Entity.CAUSE | Entity.PARENT | Entity.CHILD;
    d.setEntity(this.editor.navigator.entity);
    d.show();
  }

  void diagnoseMenu_actionPerformed(ActionEvent e) {
    DiagnosisDialog dd = new DiagnosisDialog(editor.navigator);
    dd.show();
  }

  boolean dirty = true;
  JMenuItem essaymenu = new JMenuItem();
  JMenuItem jMenuItem16 = new JMenuItem();
  JMenuItem jMenuItem17 = new JMenuItem();
  JMenuItem filterfilndmenu = new JMenuItem();

  void this_windowClosing(WindowEvent e) {
    shutdown();
  }

  void shutdown() {
    if (allowCloseDocument()) {
      hide();
      dispose();
      //System.exit(0);
    }
  }

  boolean allowCloseDocument() {
    if (!dirty) {
      return true;
    }
    int r = JOptionPane.showConfirmDialog(this, "Save these data?", "Closing",
                                          JOptionPane.YES_NO_CANCEL_OPTION);
    if (r == JOptionPane.YES_OPTION) {
      return filesave(new ActionEvent(this, 0, "Closing"), true);
    } else if (r == JOptionPane.NO_OPTION) {
      return true;
    } else {
      return false; //cancel
    }
  }

  void essaymenu_actionPerformed(ActionEvent e) {
    EssayDialog ed = new EssayDialog();
    ed.setEntity(editor.navigator.entity);
    ed.show();
  }

  void dictionary_actionPerformed(ActionEvent e) {
    DictionaryDialog dd = new DictionaryDialog(editor.navigator.entity);
    dd.show();
    if (dd.returnValue != null) {
      editor.navigator.setEntity(dd.returnValue);
    }
  }

  void filterFind(ActionEvent e){
    FindFilterDialog d=new FindFilterDialog(editor.navigator.entity);
    //d.setModal(true);
    d.setVisible(true);
    if(d.entity!=null) editor.navigator.setEntity(d.entity);
  }
}

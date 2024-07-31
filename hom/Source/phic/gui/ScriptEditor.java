package phic.gui;
import phic.*;
import javax.swing.*;
import java.awt.*;
import phic.modifiable.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import phic.common.*;
import evaluator.*;

/**
 * Class showing a dialog to edit scripts
 */
public class ScriptEditor extends ModalDialog implements ListSelectionListener{

  /** File to read on loading dialog - in the resource path */
  public static final String defaultScriptResource = "scripts/PhicScripts.txt";
  /** Location of other script text files - ?could be user defined, e.g. in the user folder */
  public static final String defaultScriptResourceFolder = "scripts";


  JPanel jPanel1=new JPanel();

  JPanel jPanel2=new JPanel();

  JButton closebutton=new JButton();

  JButton jButton1=new JButton();

  BorderLayout borderLayout1=new BorderLayout();

  JScrollPane jScrollPane1=new JScrollPane();

  JList scriptlist=new JList();

  JPanel jPanel3=new JPanel();

  DefaultListModel scriptlistmodel=new DefaultListModel();

  BorderLayout borderLayout2=new BorderLayout();

  JButton newbutton=new JButton();

  ScriptViewPanel scriptViewPanel1=new ScriptViewPanel();
  /** The scripts being displayed */

  private Vector scripts=new Vector();
  /** Return the scripts that are being displayed. */
  public Vector getScripts(){
    return scripts;
  }
  private JButton jButton2=new JButton();

  JMenuBar jMenuBar1=new JMenuBar();

  JMenu jMenu1=new JMenu();

  JMenuItem jMenuItem1=new JMenuItem();

  JMenuItem jMenuItem2=new JMenuItem();

  JMenuItem jMenuItem3=new JMenuItem();

  JMenuItem jMenuItem4=new JMenuItem();

  JMenu jMenu2=new JMenu();

  JMenuItem jMenuItem5=new JMenuItem();

  JMenu jMenu3=new JMenu();

  JMenuItem scripthelp=new JMenuItem();

  JMenuItem jMenuItem7=new JMenuItem();

  public ScriptEditor(){
    init();
  }
  public ScriptEditor(Vector scripts){
    this.scripts=scripts;
    init();
    updateList();
  }
  private void init(){
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    setSize(400,400);
    if(scripts.size()==0)
      try{
        readScriptsFromStream(Resource.loader.getResource(defaultScriptResource));
      }catch(IOException e){ e.printStackTrace(); }
  }

  private void jbInit() throws Exception{
    closebutton.setText("Close");
    closebutton.addActionListener(close);
    jButton1.setText("Run script once");
    jButton1.addActionListener(runScriptOnce);
    jPanel1.setLayout(borderLayout1);
    jScrollPane1.setPreferredSize(new Dimension(150,131));
    scriptlist.setModel(scriptlistmodel);
    scriptlist.addListSelectionListener(this);
    jPanel3.setLayout(borderLayout2);
    newbutton.setText("New script");
    newbutton.addActionListener(newScript);
    scriptViewPanel1.nametxt.addFocusListener(new FocusAdapter(){
      public void focusLost(FocusEvent e){
        updateList();}
    });
    jButton2.setText("Delete script");
    jButton2.addActionListener(deleteScript);
    jMenu1.setText("File");
    jMenuItem1.addActionListener(newScript);
    jMenuItem1.setText("New Script");
    jMenuItem2.addActionListener(saveScripts);
    jMenuItem2.setText("Save Scripts");
    jMenuItem3.addActionListener(openScripts);
    jMenuItem3.setText("Open Scripts");
    jMenuItem4.setAction(close);
    jMenuItem4.setText("Close Script Editor");
    jMenu2.setText("Edit");
    jMenuItem5.addActionListener(deleteScript);
    jMenuItem5.setText("Delete Selected Script");
    jMenu3.setText("Help");
    scripthelp.setText("Script Language Help");
    scripthelp.addActionListener(scriptHelpAction);
    jMenuItem7.setText("Delete All Scripts");
    this.setJMenuBar(jMenuBar1);
    this.setTitle("Script Editor");
    jPanel2.add(newbutton,null);
    jPanel2.add(jButton2,null);
    jPanel2.add(jButton1,null);
    this.getContentPane().add(jPanel1,BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
    jPanel2.add(closebutton,null);
    jPanel1.add(jScrollPane1,BorderLayout.WEST);
    jPanel1.add(jPanel3,BorderLayout.CENTER);
    jPanel3.add(scriptViewPanel1,BorderLayout.CENTER);
    jScrollPane1.getViewport().add(scriptlist,null);
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenuBar1.add(jMenu3);
    jMenu1.add(jMenuItem1);
    jMenu1.add(jMenuItem2);
    jMenu1.add(jMenuItem3);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem4);
    jMenu2.add(jMenuItem5);
    jMenu2.add(jMenuItem7);
    jMenu3.add(scripthelp);
  }

  /** update the JList showing the scripts with the scripts */
  protected void updateList(){
    Object sel = scriptlist.getSelectedValue();
    scriptlistmodel.removeAllElements();
    for(int i=0;i<scripts.size();i++)
      scriptlistmodel.addElement(scripts.get(i));
    scriptlist.setSelectedValue(sel,true);
  }

  /** Create a new blank script */
  protected Action newScript=new AbstractAction("New Script"){
    public void actionPerformed(ActionEvent e){
      Script s=new Script();
      scripts.add(s);
      updateList();
      scriptlist.setSelectedValue(s,true);
    }};

  /** Selecting a script on the left updates the right */
  public void valueChanged(ListSelectionEvent e){
    scriptViewPanel1.setScript((Script)scriptlist.getSelectedValue());
  }

  /**
   * Run a script once. If an run-time error occurs, display a message-box
   * with the error.
   * This method will set the elapsed time to a putative 1 second.
   */
  protected Action runScriptOnce=new AbstractAction("Run Script Once"){
    public void actionPerformed(ActionEvent e){
      try{
        evaluator.Variable.set("elapsedTime", new Double(1));
        ((Script)scriptlist.getSelectedValue()).executeOnce();
      } catch(Exception x){
        JOptionPane.showMessageDialog(ScriptEditor.this,
          "Error during script execution: "+x.toString(),"Script error",
          JOptionPane.ERROR_MESSAGE);
        x.printStackTrace();
      }
    }
  };

  /** Closes the dialog */
  protected Action close=new AbstractAction("Close"){
    public void actionPerformed(ActionEvent e){
      hide();
    }};

  /**
   * Deletes the selected scripts from the list.
   */
  protected Action deleteScript=new AbstractAction("Delete Script"){
    public void actionPerformed(ActionEvent e){
      Object[] o=scriptlist.getSelectedValues();
      for(int i=0;i<o.length;i++){
        scripts.remove(o[i]);
        //ensure the script does not go on executing! It may still be attached to Clock.ticker.
        ((Script)o[i]).setContinuous(false);
      }
      updateList();
    }};

  protected Action scriptHelpAction = new AbstractAction("Script help"){
    public void actionPerformed(ActionEvent e){
      HTMLMessagePane.showDialog("help/Scripting.html","Script help");
    }
  };

  /**
   * Put a script to file. This shows a dialog box to save the file to, and
   * writes all the scripts in this editor to the file, in ini format.
   * Each section represents one script.
   * @todo ensure description does not contain returns.
   */
  protected Action saveScripts=new AbstractAction("Save Scripts"){
    public void actionPerformed(ActionEvent e){
      JFileChooser fc=new JFileChooser(Resource.loader.
        getResourceURL(defaultScriptResourceFolder).getFile());
      int state=fc.showSaveDialog(ScriptEditor.this);
      if(state==JFileChooser.APPROVE_OPTION){
        try{
          File f=fc.getSelectedFile();
          PrintStream w=new PrintStream(new FileOutputStream(f),true);
          w.println("// Script file "+f.getAbsolutePath());
          w.println("// Last saved "+new Date());
          for(int i=0;i<scripts.size();i++){
            Script script=(Script)scripts.get(i);
            w.println('['+script.name+']');
            w.println("Description"+"="+script.description);
            w.println("Body"+"="+script.getText());
            w.println("Continuous"+"="+script.isContinuous());
            w.println("ExecutionInterval"+"="+script.getExecutionInterval());
            w.println();
          }
        } catch(IOException ex){
          JOptionPane.showMessageDialog(ScriptEditor.this,
            "Error writing file: "+e.toString(),"Could not save scripts",
            JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
      updateList();
    }};

  /**
     * Extract a script from a file. The format is that of an Ini file, with section
   * headers indicating the name of the script. The entries are:
   *  Body= body of function
   *  Description= description
   *  Continuous= true or false
     *  ExecutionInterval= double value indicating seconds between execution times
   * Note that the function attempts to compile all the scripts in the
   */

  protected Action openScripts=new AbstractAction("Open Scripts"){
    public void actionPerformed(ActionEvent e){
      JFileChooser fc=new JFileChooser(Resource.loader.getResourceURL("scripts").
        getFile());
      int state=fc.showOpenDialog(ScriptEditor.this);
      if (state == JFileChooser.APPROVE_OPTION) {
        try {
          readScriptsFromStream(new FileInputStream(fc.getSelectedFile()));
        }catch (IOException ex) {
          JOptionPane.showMessageDialog(ScriptEditor.this,"Error reading file '" +
                                        fc.getSelectedFile().getAbsolutePath() +
                                        "': " +ex.getMessage());
          ex.printStackTrace();
        }
      }
      updateList();
    }};

    public void readScriptsFromStream(InputStream fc)  throws IOException{
        IniReader ir=new IniReader(fc);
        String[] names=ir.getSectionHeaders();
        for(int i=0;i<names.length;i++){
          Map m=ir.getSectionMap(names[i]);
          Script script=new Script();
          script.name=names[i];
          script.description=m.get("Description").toString();
          script.setContinuous(Boolean.valueOf(m.get(("Continuous")).toString()).
            booleanValue());
          script.setExecutionInterval(((Double)m.get("ExecutionInterval")).
            doubleValue());
          try{
            script.setText(m.get("Body").toString());
            scripts.add(script);
          } catch(ParseException pe){
            JOptionPane.showMessageDialog(ScriptEditor.this,
              "Error compiling script "+i+" '"+names[i]+"': "+pe.getMessage());
          }
        }
    }


}




/**
 * Shows a dialog with current problems.
 */
package phic.gui;

import phic.*;
import phic.common.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import medicine.*;
import medicine.gui.*;

public class PathologyAnalysisDialog extends ModalDialog {
    String predicate =
            " is suffering from the following pathological problems:";
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JButton jButton2 = new JButton();
    BorderLayout borderLayout1 = new BorderLayout();
    Border border1;
    JPanel jPanel3 = new JPanel();
    Box box1;
    JLabel jLabel1 = new JLabel();
    JLabel varname = new JLabel();
    FlowLayout flowLayout1 = new FlowLayout();
    Box box2;
    JLabel jLabel3 = new JLabel();
    JLabel varval = new JLabel();
    JButton pathologybutton = new JButton();
    public PathologyAnalysisDialog() {
        setModal(true);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(480, 300));
        initialiseList();
        createProblems();
        if(!diagnose){
          diagnosebutton.setVisible(false);
          pathologybutton.setVisible(false);
        }
        getRootPane().setDefaultButton(jButton2);
        createMainFrame();

    }
    public static boolean diagnose=true;

    private void jbInit() throws Exception {
        border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        box1 = Box.createVerticalBox();
        box2 = Box.createVerticalBox();
        border2 = BorderFactory.createCompoundBorder(BorderFactory.
                createBevelBorder(
                BevelBorder.LOWERED),
                  BorderFactory.createEmptyBorder(3, 3, 3, 3));
        this.setTitle("Pathology Analysis");
        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
              jButton2_actionPerformed(e);
      jPanel2.setBorder(border1);
      jLabel1.setText("Variable:");
      varname.setFont(new java.awt.Font("Dialog", 1, 12));
      varname.setToolTipText("The name of the selected variable");
      jPanel3.setBorder(BorderFactory.createEtchedBorder());
      jPanel3.setPreferredSize(new Dimension(140, 44));
        jPanel3.setLayout(flowLayout1);
            }
        });
        jPanel2.setLayout(borderLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        jLabel3.setToolTipText("");
        jLabel3.setText("Value:");
        varval.setFont(new java.awt.Font("Dialog", 1, 12));
        varval.setToolTipText("The value of the selected variable");
        pathologybutton.setToolTipText(
                "Display the pathology of the selected variable");
        pathologybutton.setText("Pathology");
        pathologybutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pathologybutton_actionPerformed(e);
            }
        });
        problemlist.addListSelectionListener(new javax.swing.event.
                                             ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                problemlist_valueChanged(e);
            }
        });
        jPanel4.setLayout(borderLayout2);
        topcomment.setLineWrap(true);
        topcomment.setPreferredSize(new Dimension(252, 50));
        topcomment.setWrapStyleWord(true);
        topcomment.setBackground(SystemColor.control);
        topcomment.setBorder(border2);
        topcomment.setEditable(false);
        topcomment.setFont(new java.awt.Font("SansSerif", 0, 14));
        diagnosebutton.setToolTipText(
                "Try and come up with a diagnosis that fits the current problems");
        diagnosebutton.setText("Diagnose");
        diagnosebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagnosebutton_actionPerformed(e);
            }
        });
        jScrollPane1.setToolTipText("List of current problems");
        jLabel1.setText("Variable:");
    jPanel3.setLayout(borderLayout3);
    jPanel3.setPreferredSize(new Dimension(150, 53));
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(diagnosebutton, null);
        jPanel1.add(jButton2, null);
        this.getContentPane().add(jPanel2, BorderLayout.CENTER);
        jPanel2.add(jPanel3, BorderLayout.EAST);
        jPanel3.add(box1, BorderLayout.NORTH);
        box1.add(jLabel1, null);
        box1.add(varname, null);
        jPanel3.add(box2,  BorderLayout.CENTER);
        box2.add(jLabel3, null);
        box2.add(varval, null);
        jPanel3.add(pathologybutton,  BorderLayout.SOUTH);
        jPanel2.add(bottompanel, BorderLayout.SOUTH);
        jPanel2.add(jPanel4, BorderLayout.CENTER);
        jPanel4.add(jScrollPane1, BorderLayout.CENTER);
        jPanel4.add(topcomment, BorderLayout.NORTH);
        jScrollPane1.getViewport().setView(problemlist);
    }

    void jButton2_actionPerformed(ActionEvent e) {
      mf.hide();
      mf.dispose();
        hide();
    }

    /**
     * This will invoke the pathology database to find the entity
     * represented by the selection.
     */
    void pathologybutton_actionPerformed(ActionEvent e) {
        if(problemlist.getSelectedValue()==null) return;
        // Load Medicine, Search for variable
        createMainFrame();
        String searchString = problemlist.getSelectedValue().toString();
        Entity startNode = mf.editor.navigator.getEntity();
        EntitySearcher es = new EntitySearcher(searchString, startNode,
                                               new SearchListener(mf.editor,
                searchString));
        es.start();
        mf.show();
    }

    /** Ensure that a medicine.MainFrame pathology window is created */
    static void createMainFrame() {
        if (mf == null) {
            mf = new MainFrame();
           // mf.show();
            mf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //don't end Phic!

            Cursor previousCursor = mf.getCursor();
            mf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                mf.readTextFromZip(Resource.loader.getResource("Pathology.zip"),
                            "Pathology.zip");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Could not load pathology data file");
            } catch (Error e){
              e.printStackTrace();
              JOptionPane.showMessageDialog(null,
                      "For this feature, the program needs to be run using the -ss500k option");
            }finally{
              mf.setCursor(previousCursor);
            }
        }
    }

    class SearchListener implements ActionListener {
        EntityEditorPanel editor;
        String name;
        SearchListener(EntityEditorPanel e, String n) {
            editor = e;
            name = n;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource()instanceof Entity) {
                Entity entity = (Entity) e.getSource();
                if (entity.equals(name)) {
                    foundEntity(editor, entity);
                }
            } else {
                foundAllEntities();
            }
        }
    }


    public boolean readingPathologies=false;
    /**
     * Returns a vector of Entities representing the current pathologies.
     * This is a blocking call
     */
    public Vector readPathologies(){
      readingPathologies=true;
      diagnosisSigns= new Vector();
      //search for each
      Entity startNode = mf.editor.navigator.getEntity();
      synchronized(readPathNotify){
        for(Enumeration i=currentProblems.elements();i.hasMoreElements();){
          String searchString=i.nextElement().toString();
          EntitySearcher es=new EntitySearcher(searchString,startNode,
            new SearchListener(mf.editor,searchString));
          searchCount++;
          es.start();
        }
        try{
          if(searchCount>0){
            readPathNotify.wait(1200);
            if(searchCount>0){ throw new IllegalStateException(
                       "Could not find the pathology entities"
             ); }
          }
        } catch(InterruptedException ex){}
        finally{readingPathologies=false;}
      }
      return diagnosisSigns;
    }
    private Object readPathNotify = new Object();
    public final Entity getDefaultEntity(){ createMainFrame(); return mf.editor.navigator.getEntity(); }

    /** Called after search when pathology... or diagnose... is pressed */
    void foundEntity(EntityEditorPanel editor, Entity e) {
        if (!diagnosing && !readingPathologies) {
            editor.navigator.setEntity(e);
        } else {
            diagnosisSigns.add(e);
        }
    }

    boolean diagnosing = false;
    Vector diagnosisSigns = new Vector();
    static MainFrame mf;
    public static MainFrame getMainFrame(){
      createMainFrame();
      return mf;
    }
    int searchCount = 0;
    /**
     * This is called when the diagnose... button is pressed, and
     * begins a search for the entities corresponding to the current
     * pathology. At the end of the search, the method displayDiagnosis
     * is called, which shows the result.
     */
    void startDiagnosis() {
        diagnosisSigns.removeAllElements();
        diagnosing = true;
        createMainFrame();
        //search for each
        Entity startNode = mf.editor.navigator.getEntity();
        for (Enumeration i = currentProblems.elements(); i.hasMoreElements(); ) {
            String searchString = i.nextElement().toString();
            EntitySearcher es = new EntitySearcher(searchString, startNode,
                    new SearchListener(mf.editor, searchString));
            searchCount++;
            es.start();
        }
    }

    /**
     * Called at end of search, either when Pathology... or Diagnose... is
     * pressed. If diagnose was pressed, the method calls displayDiagnosis,
     * to invoke the pathology database's diagnosis facility.
     */
    void foundAllEntities() {
        if (diagnosing) {
            searchCount--;
            if (searchCount == 0) {
                diagnosing = false;
                displayDiagnosis();
            }
        }else if(readingPathologies){
          searchCount--;
          if(searchCount==0){
            synchronized(readPathNotify){
              readPathNotify.notifyAll();
            }
          }
        }
    }

    /**
     * This method calls the pathology database's
     * Diagnosis facility, and displays the results.
     */
    void displayDiagnosis() {
        DiagnosisDialog dd = new DiagnosisDialog(mf.editor.navigator);
        dd.setModal(true);
        DefaultListModel lm = (DefaultListModel) dd.getObsList().getModel();
        for (Iterator i = diagnosisSigns.iterator(); i.hasNext(); ) {
            lm.addElement(i.next());
        }
        dd.doDiagnosis();
        dd.show();
    }

    IniReader pathlistreader = new IniReader("Pathology.txt");
    Map pathlist;
    Hashtable conditions = new Hashtable();
    public void initialiseList() {
        //create a reverse map to find condition names from variables
        pathlist = pathlistreader.getSectionMap("Pathology");
        for (Iterator i = pathlist.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            conditions.put(pathlist.get(key), key);
        }
    }
    /** Function to convert a pathology description to an entity */
    public Entity getEntityFromPathology(String pathology){
      try{
        return Entities.getSpecificNamedEntity(conditions.get(pathology.intern()).
          toString(),getDefaultEntity());
      }catch(Exception e){return null;}
    }
    /** Function to convert an entity to a pathology description */
    public String getPathologyFromEntity(Entity entity){
      try{
        return pathlist.get(entity.toString().intern()).toString();
      }catch(Exception e){return null;}
    }

    Vector currentProblems = new Vector();
    public Vector getCurrentProblems(){ return currentProblems; }
    JPanel bottompanel = new JPanel();
    JPanel jPanel4 = new JPanel();
    JList problemlist = new JList();
    JScrollPane jScrollPane1 = new JScrollPane();
    BorderLayout borderLayout2 = new BorderLayout();
    JTextArea topcomment = new JTextArea();
    Border border2;
    JButton diagnosebutton = new JButton();
  BorderLayout borderLayout3 = new BorderLayout();

    /** Creates the list of problems, based on the Variable list. */
    public void createProblems() {
        currentProblems = new Vector();
        for (int i = 0; i < Variables.variable.length; i++) {
            VisibleVariable v = Variables.variable[i];
            Node node = v.node;
            if (node.getType() == Node.DOUBLE) {
                double value = node.doubleGetVal();
                if (value > v.maximum) {
                    error(true, v);
                }
                if (value < v.minimum) {
                    error(false, v);
                }
            }
        }
        problemlist.setListData(currentProblems);
        topcomment.setText(Current.person.name + predicate);
    }

    void error(boolean hilo, VisibleVariable v) {
        String probname = (hilo ? "High" : "Low") + " " + v.canonicalName;
        Object o = conditions.get(probname);
        if (o != null) {
            String path = o.toString();
            currentProblems.add(path);
        }
    }

    void problemlist_valueChanged(ListSelectionEvent e) {
        String path = problemlist.getSelectedValue().toString();
        String problem = pathlist.get(path).toString();
        boolean hilo = problem.startsWith("High");
        String variableName;
        if (hilo) {
            variableName = problem.substring(5);
        } else {
            variableName = problem.substring(4);
        }
        varname.setText(variableName);
        VisibleVariable v = Variables.forName(variableName);
        double value = v.node.doubleGetVal();
        varval.setText(v.formatValue(value, true, false));
        NodeView nodeView = new NodeView(v.node, NodeView.VALUE_RANGE, null);
        bottompanel.removeAll();
        bottompanel.add(nodeView);
    }

    void diagnosebutton_actionPerformed(ActionEvent e) {
        startDiagnosis();
    }
}

package com.cudos;

import com.cudos.common.CudosExhibit;
import javax.swing.*;
import java.awt.*;
import com.cudos.common.CudosIndexReader;
import javax.swing.event.*;
import com.cudos.genetic.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Mendelian genetics exhibit
 */

public class GeneticExhibit extends CudosExhibit {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList jList1 = new JList();
  DefaultListModel lm = new DefaultListModel();
  public GeneticExhibit() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  URL RESOURCE_FILE;
  public void addNotify(){
    super.addNotify();
    RESOURCE_FILE=getApplet().getResourceURL("resources/Mendelian.txt");
    ir=new CudosIndexReader(RESOURCE_FILE);
    readIndex();
  }
  CudosIndexReader ir;
  GeneticDisplay geneticDisplay1 = new GeneticDisplay();
  JButton jButton1 = new JButton();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea description = new JTextArea();
  JSplitPane jSplitPane1 = new JSplitPane();
  JSplitPane jSplitPane2 = new JSplitPane();
  public void readIndex(){
    String[] g=ir.getSectionNames();
    for(int i=0;i<g.length;i++)lm.addElement(g[i]);
    jList1.setModel(lm);
  }
  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jList1.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        jList1_valueChanged(e);
      }
    });
    jButton1.setText("Return");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    description.setEditable(false);
    description.setText("");
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    //jScrollPane1.setPreferredSize(new Dimension(150, 130));
    jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    jSplitPane1.setResizeWeight(0.75);
    jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setResizeWeight(0.3);
    jPanel1.add(jButton1, BorderLayout.SOUTH);
    jPanel1.add(jSplitPane2,  BorderLayout.CENTER);
    jSplitPane2.add(jScrollPane1, JSplitPane.TOP);
    jSplitPane2.add(jScrollPane2, JSplitPane.BOTTOM);
    jScrollPane2.getViewport().add(description, null);
    jScrollPane1.getViewport().add(jList1, null);
    this.getContentPane().add(jSplitPane1,  BorderLayout.CENTER);
    jSplitPane1.add(geneticDisplay1, JSplitPane.TOP);
    jSplitPane1.add(jPanel1, JSplitPane.BOTTOM);
  }

  void jList1_valueChanged(ListSelectionEvent e) {
    if(e.getValueIsAdjusting()) return;
    String s = jList1.getSelectedValue().toString();
    readSection(s);
  }
  public void readSection(String section){
/*
        String[] p=ir.getStringsInSection(section);
    String descr="";
    for(int i=0;i<p.length;i++){
      if(p[i].startsWith("Info")){
        descr=p[i].substring(p[i].indexOf('=')+1).trim();
      }
    }
    description.setText(descr);
        }
*/
    description.setText(GenomeFromFile.getGenomeTypeDescription(RESOURCE_FILE,section));
    geneticDisplay1.setupType(RESOURCE_FILE, section);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    getApplet().toChooser();
  }

}

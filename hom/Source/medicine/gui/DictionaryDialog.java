package medicine.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.AbstractButton;
import medicine.*;
import java.util.*;
import java.awt.event.*;

public class DictionaryDialog extends JDialog {
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel toppanel = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList jList1 = new JList();
  BorderLayout borderLayout1 = new BorderLayout();
  Border border1;
  AbstractButton[] buttons=new AbstractButton[27];
  ButtonGroup bg= new ButtonGroup();

  Entity root;
  public DictionaryDialog(Entity root) throws HeadlessException {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.root=root;
    for(int i=0;i<buttons.length;i++){
      buttons[i]=new JToggleButton(String.valueOf((char)('A'+i)));
      bg.add(buttons[i]);
      toppanel.add(buttons[i]);
      buttons[i].addActionListener(bgal);
      buttons[i].setMargin(new Insets(2,2,2,2));
    }
    setSize(250,400);
    MainApplication.centreWindow(this);
    buttons[0].setSelected(false);
  }
  ActionListener bgal = new ActionListener(){
    public void actionPerformed(ActionEvent e){
      currentLetter=e.getActionCommand();
      Entities e2=new Entities();
      e2.setVector(root, updateAction);
    }
  };
  class Item { Entity entity; String name;
    Item(Entity e, String s){entity=e;name=s;}
    public String toString(){return name;}
  };
  ActionListener updateAction = new ActionListener(){
    public void actionPerformed(ActionEvent e){
      if(e.getSource() instanceof Entities){
        Entities e2=(Entities)e.getSource();
        items=new Vector();
        for(int i=0;i<e2.getVector().size();i++){
          Entity e3 = (Entity) e2.getVector().elementAt(i);
          if(e3.name.startsWith(currentLetter)){
            items.add(new Item(e3, e3.name));
          }else{
            for(int j=0;j<e3.synonyms.size();j++){
              String s=e3.synonyms.elementAt(j).toString();
              if(s.startsWith(currentLetter)){
                items.add(new Item(e3,s)); // break; // this break disallows synonyms in the same list
              }
            }
          }
        }
        updateListItems();
      }
    }
  };
  String currentLetter;
  Vector items;
  void updateListItems(){
    Object[] o=items.toArray();
    Arrays.sort(o,comparator);
    jList1.setListData(o);
  }
  Comparator comparator = new Comparator(){
    public int compare(Object o, Object o2){
      return o.toString().compareTo(o2.toString());
    }
  };

  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(109, 109, 110),new Color(156, 156, 158)),BorderFactory.createEmptyBorder(4,4,4,4));
    jButton1.setText("Go");
    jButton2.setText("Cancel");
    jButton2.addActionListener(bl);
    jButton1.addActionListener(bl);
    jPanel1.setLayout(borderLayout1);
    jPanel1.setBorder(border1);
    toppanel.setLayout(gridLayout1);
    gridLayout1.setColumns(9);
    gridLayout1.setRows(3);
    jList1.addMouseListener(new DictionaryDialog_jList1_mouseAdapter(this));
    this.setModal(true);
    this.setTitle("Dictionary");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton1, null);
    this.getContentPane().add(toppanel, BorderLayout.NORTH);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jList1, null);
  }
  public Entity returnValue;
  ActionListener bl=new ActionListener(){
    public void actionPerformed(ActionEvent e){
      if(e.getActionCommand().equals("Cancel")){
        returnValue = null;
        hide();
      }else{
        returnValue = ((Item)jList1.getSelectedValue()).entity;
        hide();
      }
    }
  };
  GridLayout gridLayout1 = new GridLayout();

  void jList1_mouseClicked(MouseEvent e) {
    if(e.getClickCount()>1){
      returnValue = ((Item)jList1.getSelectedValue()).entity;
      hide();
    }
  }
}

class DictionaryDialog_jList1_mouseAdapter extends java.awt.event.MouseAdapter {
  DictionaryDialog adaptee;

  DictionaryDialog_jList1_mouseAdapter(DictionaryDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jList1_mouseClicked(e);
  }
}

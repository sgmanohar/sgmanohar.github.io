package medicine.gui;

import javax.swing.*;
import medicine.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EntityChooserComboBox extends JComboBox implements ActionListener{
  public EntityChooserComboBox() {
    setEditable(true);
    setModel(model);
    getEditor().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editChange();
      }
    });
  }

  protected Entity startNode = null;
  protected EntitySearcher es;
  protected DefaultComboBoxModel model = new DefaultComboBoxModel();
  protected boolean clearList = false;
  protected String oldstring;

  public void setEntity(Entity e){
    startNode=e;
  }

  void editChange() {
    keypressed();
  }


  void keypressed() {
    String string = getEditor().getItem().toString();
    if (string.equals(oldstring)) {
      //Entity has been chosen now
      return;
    }

    oldstring = string;
    if (string.length() < 3) {
      return;
    }
    if(startNode==null)throw new IllegalStateException("Initial node not set");
    synchronized (this) {
      if (es != null) {
        es.stop();
      }
      clearList = true;
      es = new EntitySearcher(string, startNode, this);
      es.start();
    }
  }

  void emptyList() {
    model = new DefaultComboBoxModel();
    setModel(model);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof Entity) {
      Object selection = getEditor().getItem();

      if (clearList) {
        emptyList();
        clearList = false;
      }
      Entity f = (Entity) e.getSource();
      hidePopup();
      model.addElement(f);
      setMaximumRowCount(model.getSize());
      showPopup();

      getEditor().setItem(selection);
    }
  }
  protected Entity entity;
  protected boolean allowCreateNewEntity=true;
  protected boolean createdNewEntity = false;

  public boolean allowCreateNewEntity(){return allowCreateNewEntity;}
  public boolean hasCreatedNewEntity(){return createdNewEntity;}
  public Entity getSelectedValue(){
        Object o=getSelectedItem();
        Entity sel=null;
        if(o instanceof Entity)sel=(Entity)o;
        if(sel==null && o instanceof String){
                String name=(String)o;
                for(int i=0;i<model.getSize();i++){
                        Object ob=model.getElementAt(i);
                        if(ob instanceof Entity){
                                Entity test=(Entity)ob;
                                if(test.equalsIgnoreCase(name)) sel=test;
                        }
                }
        }
        if(sel==null){
                String str=getEditor().getItem().toString();
                if( allowCreateNewEntity &&
                    JOptionPane.showConfirmDialog(this, "Create new entity '"+str+"' ?",
                    "No entity selected", JOptionPane.YES_NO_OPTION) ==JOptionPane.YES_OPTION){
                        entity=new Entity(null,-1);
                        entity.name=str;
                        createdNewEntity=true;
                        return entity;
                }else{
                        entity=null;
                        return entity;
                }
        }else{
                entity=sel;
                createdNewEntity=false;
                return entity;
        }
}


}

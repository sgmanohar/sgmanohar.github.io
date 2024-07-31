package medicine.gui;

import java.util.Vector;
import medicine.Entities;
import medicine.Entity;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import medicine.MainApplication;
import java.text.ParseException;

/**
 * Find items using a set of filters
 */
public class FindFilterDialog extends FindDialog {
  public FindFilterDialog(Entity entity) throws HeadlessException {
    this.entity=entity;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(475,500);
    MainApplication.centreWindow(this);
    setTitle("Find item");
  }

  Entities allEntities=new Entities();

  public void executeFilters(){
    allEntities.setVector(entity,doFind);
  }
  ActionListener doFind=new ActionListener(){
    public void actionPerformed(ActionEvent e){
      processFindAction();
    }
  };
  public void processFindAction(){
      Vector items = (Vector)allEntities.getVector().clone();
      Vector prevItems=items;
      for(int i=0;i<filterListModel.size();i++){
        FindFilter filter = (FindFilter)filterListModel.get(i);
        try{
          if (filter.isOrFilter()) {
            items = mergeLists(items, filter.getFilteredItems(prevItems));
          } else {
            items = filter.getFilteredItems(items);
            //prevItems = (Vector)items.clone();
          }
        }catch(ParseException e){
          e.printStackTrace();
          JOptionPane.showMessageDialog(this,e.toString(),
            "Could not parse filter expression",JOptionPane.ERROR_MESSAGE);
        }
      }
      showSearchResults (items);
  }


  void showSearchResults(Vector v){
    found=v;
    foundlist.setListData(found);
  }

  /**
   * Add the items of source to dest, ensuring there is no overlap (using equals()).
   * Returns dest.
   */
  Vector mergeLists(Vector dest, Vector source){
    for(int i=0;i<source.size();i++){
      Object o = source.get(i);
      if(!dest.contains(o)) dest.add(o);
    }
    return dest;
  }



  // INTERFACE

  JPanel jPanel1 = new JPanel();
  JScrollPane scrollPane = new JScrollPane();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JButton addFilterButton = new JButton();
  JButton removeFilterButton = new JButton();
  JButton searchButton = new JButton();

  private void jbInit() throws Exception {
    jPanel1.setLayout(borderLayout1);
    addFilterButton.setText("Add");
    addFilterButton.addActionListener(new FindFilterDialog_addFilterButton_actionAdapter(this));
    removeFilterButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
        removeFilter();
    }});
    removeFilterButton.setText("Remove Filter");
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    splitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitter.setTopComponent(jPanel1);
    this.getContentPane().remove(this.toppanel);
    splitter.setBottomComponent(this.mainPanel);
    filterListPanel.setLayout(filterListGridLayout);
    filterListGridLayout.setColumns(1);
    searchButton.setText("Search");
    searchButton.addActionListener(new FindFilterDialog_searchButton_actionAdapter(this));
    jPanel1.add(scrollPane, BorderLayout.CENTER);
    jPanel1.add(jPanel3, BorderLayout.NORTH);
    jPanel3.add(addFilterButton, null);
    jPanel3.add(removeFilterButton, null);
    scrollPane.getViewport().add(filterListPanel);
    this.getContentPane().add(splitter, BorderLayout.CENTER);
    this.buttonpanel.add(searchButton, BorderLayout.NORTH);


    splitter.setDividerLocation(250);
  }

  Vector filterListModel = new Vector();
  JSplitPane splitter = new JSplitPane();

  MyList filterListPanel = new MyList();
  GridLayout filterListGridLayout = new GridLayout();
  int filterSelection = -1;

  /** Insert a new filter before the currently selected item in the list */
  void addFilterButton_actionPerformed(ActionEvent e) {
    FindFilter f=new FindFilter(entity);
    f.addFocusListener(focusListener);
    int i=filterSelection;
    if(i>=0) {
      filterListModel.insertElementAt(f,i);
      filterListPanel.add(f,i);
    }    else {
      filterListModel.addElement(f);
      filterListPanel.add(f);
    }
    filterListGridLayout.setRows(filterListModel.size());
    scrollPane.validate();
  }
  class MyList extends JPanel implements Scrollable {
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
    public boolean getScrollableTracksViewportWidth() {
      return true;
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect,  int orientation, int direction) {
      return 50;
    }
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 10;
    }
    GridLayout grid=new GridLayout(1,1);
    MyList(){
      setLayout(grid);
    }
    int count=0;
    public void addListItem(JComponent c){
      grid.setRows(++count);
      add(c);
      validate();
    }
    public void removeListItem(JComponent c){
      remove(c);
      grid.setRows(--count);
      validate();
    }
  };
  void removeFilter(){
    if(filterSelection<0){
      filterSelection=filterListModel.size()-1;
    }
    FindFilter ff=(FindFilter)filterListModel.get(filterSelection);
    filterListModel.remove(filterSelection);
    filterListPanel.removeListItem(ff);
    setSelection(-1);
    scrollPane.validate();
  }
  public void setSelection(int i){
    if(filterSelection>=0 && filterSelection<filterListModel.size()) ((FindFilter)filterListModel.get(filterSelection)).setSelected(false);
    filterSelection=i;
    if(filterSelection>=0) ((FindFilter)filterListModel.get(filterSelection)).setSelected(true);
  }
  FocusListener focusListener = new FocusAdapter(){
    public void focusGained(FocusEvent e){
      if(e.getSource() instanceof FindFilter){
        setSelection(filterListModel.indexOf(e.getSource()));
      }
    }
    public void focusLost(FocusEvent e){
      int i=filterListModel.indexOf(e.getSource());
      if(i>=0) setSelection(-1);
    }
  };

  void searchAction(ActionEvent e) {
    executeFilters();
  }
  public void entityDoubleClicked(){
    MainApplication.frame.editor.navigator.setEntity(entity);
  }
}

class FindFilterDialog_addFilterButton_actionAdapter implements java.awt.event.ActionListener {
  FindFilterDialog adaptee;

  FindFilterDialog_addFilterButton_actionAdapter(FindFilterDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.addFilterButton_actionPerformed(e);
  }
}

class FindFilterDialog_searchButton_actionAdapter implements java.awt.event.ActionListener {
  FindFilterDialog adaptee;

  FindFilterDialog_searchButton_actionAdapter(FindFilterDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.searchAction(e);
  }
}

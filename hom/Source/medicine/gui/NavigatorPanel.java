/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;

import medicine.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import medicine.TransferableEntity;
import java.util.List;
import java.io.*;

public class NavigatorPanel
    extends JPanel
    implements ActionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel superpanel = new JPanel();
  JLabel jLabel1 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList superlist = new SList();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane jScrollPane2 = new JScrollPane();
  JPanel subpanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JList sublist = new SList();
  JLabel jLabel2 = new JLabel();
  EntityPanel entitypanel = new EntityPanel();
  JPanel causepanel = new JPanel();
  JLabel jLabel3 = new JLabel();
  JScrollPane jScrollPane3 = new JScrollPane();
  JList causelist = new SList();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel effectspanel = new JPanel();
  JLabel jLabel4 = new JLabel();
  JScrollPane jScrollPane4 = new JScrollPane();
  JList effectlist = new SList();
  BorderLayout borderLayout5 = new BorderLayout();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem9 = new JMenuItem();
  JMenuItem jMenuItem8 = new JMenuItem();
  JPopupMenu listEditPopup = new JPopupMenu();
  JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem jMenuItem10 = new JMenuItem();

  public NavigatorPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    JList[] lists = new JList[] {
        superlist, sublist, causelist, effectlist};
    for (int i = 0; i < lists.length; i++) {
      //add handlers for double-clicks
      lists[i].addMouseListener(dcl);
      //add handlers for right-clicks
      lists[i].addMouseListener(mcl);
      //add handler for drag/drop
      lists[i].addMouseMotionListener(mml);
      //add handler for delete key
      lists[i].registerKeyboardAction(this, "Delete",
                                      KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
          0)
                                      ,
                                      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      lists[i].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
          KeyEvent.CTRL_MASK), copyaction);
      lists[i].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
          KeyEvent.CTRL_MASK), pasteaction);
      lists[i].getActionMap().put(copyaction, copyaction);
      lists[i].getActionMap().put(pasteaction, pasteaction);
//                lists[i].registerKeyboardAction(this, "Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
//                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//                lists[i].registerKeyboardAction(this, "Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
//                                  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    //add handler for resizing window
    addComponentListener(new ComponentAdapter() {
      public synchronized void componentResized(ComponentEvent e) {
        setPanelSizes();
      }
    });

    //add handler for Ctrl+F
    registerKeyboardAction(this, "Find",
                           KeyStroke.getKeyStroke(KeyEvent.VK_F,
                                                  KeyEvent.CTRL_MASK),
                           JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  Entity entity;
  public void setEntity(Entity e) {
    entity = e;
    init();
  }

  public Entity getEntity() {
    return entity;
  }

  public void init() {
    if (entity != null) {
      superlist.setListData(entity.parents);
      sublist.setListData(entity.children);
      causelist.setListData(entity.causes);
      effectlist.setListData(entity.effects);
    }
    else {
      Vector nullv = new Vector();
      superlist.setListData(nullv);
      sublist.setListData(nullv);
      causelist.setListData(nullv);
      effectlist.setListData(nullv);
    }
    entitypanel.setEntity(entity);
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setText("Superclasses");
    superpanel.setLayout(borderLayout2);
    subpanel.setLayout(borderLayout3);
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel2.setText("Subtypes");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel3.setText("Causes");
    causepanel.setLayout(borderLayout4);
    jScrollPane3.setPreferredSize(new Dimension(100, 150));
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel4.setText("Effects");
    effectspanel.setLayout(borderLayout5);
    jScrollPane4.setPreferredSize(new Dimension(100, 150));
    jMenu1.setText("Add");
    jMenuItem9.setText("Delete");
    jMenuItem9.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuDelete(e);
      }
    });
    jMenuItem8.setText("New item");
    jMenuItem8.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        addNewItem(e);
      }
    });
    jMenuItem6.setFont(new java.awt.Font("Dialog", 1, 12));
    jMenuItem6.setText("Go to");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuGoto(e);
      }
    });
    jMenuItem10.setText("Existing item");
    jMenuItem10.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuAddExist(e);
      }
    });
    causelist.setBackground(new Color(216, 225, 255));
    causelist.setToolTipText("List of causes");
    causelist.setSelectionBackground(Color.blue);
    effectlist.setBackground(new Color(255, 215, 225));
    effectlist.setToolTipText("List of consequences");
    effectlist.setSelectionBackground(Color.red);
    superlist.setToolTipText("List of superclasses");
    sublist.setToolTipText("List of subclasses");
    superpanel.setPreferredSize(new Dimension(337, 60));
    subpanel.setPreferredSize(new Dimension(311, 60));
    entitypanel.setEditable(true);
    jMenuItem1.setText("Quick add...");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        menuQuickAdd(e);
      }
    });
    this.add(superpanel, BorderLayout.NORTH);
    superpanel.add(jLabel1, BorderLayout.WEST);
    superpanel.add(jScrollPane1, BorderLayout.CENTER);
    this.add(subpanel, BorderLayout.SOUTH);
    subpanel.add(jLabel2, BorderLayout.WEST);
    subpanel.add(jScrollPane2, BorderLayout.CENTER);
    this.add(entitypanel, BorderLayout.CENTER);
    this.add(causepanel, BorderLayout.WEST);
    causepanel.add(jLabel3, BorderLayout.NORTH);
    causepanel.add(jScrollPane3, BorderLayout.CENTER);
    this.add(effectspanel, BorderLayout.EAST);
    effectspanel.add(jLabel4, BorderLayout.NORTH);
    effectspanel.add(jScrollPane4, BorderLayout.CENTER);
    jScrollPane4.getViewport().add(effectlist, null);
    jScrollPane3.getViewport().add(causelist, null);
    jScrollPane2.getViewport().add(sublist, null);
    jScrollPane1.getViewport().add(superlist, null);
    jMenu1.add(jMenuItem1);
    jMenu1.add(jMenuItem8);
    jMenu1.add(jMenuItem10);
    listEditPopup.add(jMenuItem6);
    listEditPopup.addSeparator();
    listEditPopup.add(jMenu1);
    listEditPopup.add(jMenuItem9);
  }

  /**
   is moving to new entity allowed?
   */
  public boolean canMove = true;
  boolean canEdit = true;
  public void setEditable(boolean t) {
    canEdit = t;
    entitypanel.setEditable(t);
  }

  public boolean isEditable() {
    return canEdit;
  }

  /**
   Double click listener
   */
  MouseListener dcl = new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
      if (canMove && e.getClickCount() == 2) {
        Component c = (Component) e.getSource();
        if (c == sublist || c == superlist ||
            c == causelist || c == effectlist) {
          JList list = (JList) c;
          Entity ent = (Entity) list.getSelectedValue();
          if (ent != null) {
            setEntity(ent);
          }
        }
      }
    }
  };

  /**
   Run the find dialog, and if OK, make its entity the current entity
   */
  void find() {
    FindDialog fd = new FindDialog(entity);
    fd.setModal(true);
    fd.show();
    if (fd.entity != null) {
      setEntity(fd.entity);
    }
  }

  public void actionPerformed(ActionEvent e) {
    String s = e.getActionCommand();
    if (canMove && s.equals("Find")) {
      find();
    }
    else if (canEdit && s.equals("Delete")) {
      if (e.getSource() instanceof JList) {
        JList list = (JList) e.getSource();
        deleteSelectedItem(list);
      }
    }

  }

  Action copyaction = new AbstractAction("Copy") {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JList) {
        JList l = (JList) e.getSource();
        /**
         * Retrieve selected entity from source list, and store in clipboard
         */
        Object[] o=l.getSelectedValues();
        if(o.length==1){
          Entity f = (Entity) l.getSelectedValue();
          if (f != null) {
            setClipboard(f);
          }
        }else if(o.length>1){
          setClipboard(Arrays.asList(o));
        }
      }
    }

  };
  Action pasteaction = new AbstractAction("Paste") {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JList) {
        JList l = (JList) e.getSource();
        /**
                  calculate what relationship is represented by the currently
                  selected list, and connect the clipboard item to the current
                  entity's list of this relation.
         */
        int rel = relationFromList(l);
        Object o=getClipboard();
        if(o instanceof Entity){
          entity.connect( (Entity) o, rel);
        }else if(o instanceof List){
          for(Iterator i=((List)o).iterator();i.hasNext();){
            entity.connect((Entity)i.next(), rel);
          }
        }
        redisplay();
      }
    }

  };




/** TRUE WINDOWS CLIPBOARD ROUTINES
    public void setClipboard(Entity e) {
      TransferableEntity te = new TransferableEntity(e);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(te, te);
    }
    public void setClipboard(List l){
      TransferableEntityList tl=new TransferableEntityList(l);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(tl,tl);
    }
  DataFlavor textflavor=DataFlavor.getTextPlainUnicodeFlavor();
  public Object getClipboard() {
    Transferable t = Toolkit.getDefaultToolkit().getDefaultToolkit().
        getSystemClipboard().getContents(null);
    try {
      if(t.isDataFlavorSupported(TransferableEntity.entityFlavor)){
        return (Entity) t.getTransferData(
            TransferableEntity.entityFlavor);
      }else if(t.isDataFlavorSupported(TransferableEntityList.listFlavor)){
        return t.getTransferData(
            TransferableEntityList.listFlavor);
      } else if(t.isDataFlavorSupported(textflavor)){
        String s=t.getTransferData(textflavor).toString();
        return Entities.getSpecificNamedEntity(s,entity);
      }else return null;
    }catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
                                    "Cannot paste items of type " +
                                    t.getTransferDataFlavors()[0].
                                    getHumanPresentableName(), e.toString(),
                                    JOptionPane.ERROR_MESSAGE);
      return null;
    }

  }
*/
/** FAKE CLIPBOARD ROUTINES*/
  Object tmp=null;
  public void setClipboard(Object o){ tmp=o; TransferableEntity.listener.actionPerformed(new ActionEvent(o,0,"ClipboardChanged")); }
  public Object getClipboard(){return tmp; }

  /**
   Right click listener
   */
  JList popupSource = null;
  MouseListener mcl = new MouseAdapter() {
    public void mouseReleased(MouseEvent e) {
      if (canEdit && e.getModifiers() == MouseEvent.BUTTON3_MASK) {
        listEditPopup.show( (Component) e.getSource(), e.getX(), e.getY());
        popupSource = (JList) e.getSource();
      }
      if (moving != null) {
        if (currentdroplist != null) {
          Vector from = entity.listOf(relationFromList(currentdraglist));
          Vector to = entity.listOf(relationFromList(currentdroplist));
          synchronized (moving) {
            int ifrom = from.indexOf(moving);
            //from.remove(moving);
            int dest = currentdroplist.getCaretIndex();
            if (to == from && ifrom < dest) {
              dest--;
              //to.insertElementAt(moving, dest);
            }
            if (from != to) {
              if (Entities.numConnections(entity) > 1) {
                entity.disconnect(moving, relationFromList(currentdraglist));
              }
              else {
                from.remove(moving);
                moving.listOf(Entity.inverseOf(relationFromList(currentdraglist))).
                    remove(entity);
              }
              entity.connect(moving, relationFromList(currentdroplist));
            }
            to.remove(moving);
            to.insertElementAt(moving, dest);
            moving = null;
          }
          currentdroplist.setCaretIndex( -1);
          redisplay();
        }
        else {
          moving = null;
        }
      }
    }
  };

  /**
   Drag listener
   */
  Entity moving;
  SList currentdraglist, currentdroplist;
  MouseMotionListener mml = new MouseMotionAdapter() {
    public void mouseDragged(MouseEvent e) {
      if (moving == null) {
        currentdraglist = (SList) e.getSource();
      }
      moving = (Entity) currentdraglist.getSelectedValue();
      if (moving != null) {
        Point oo = ( (Component) (e.getSource())).getLocationOnScreen(),
            no = getLocationOnScreen();
        int x = e.getX() + oo.x - no.x, y = e.getY() + oo.y - no.y;
        Component c = findComponentAt(x, y);
        if (c instanceof SList) {
          SList list = (SList) c;
          Point mo = list.getLocationOnScreen();
          int xx = e.getX() + oo.x - mo.x, yy = e.getY() + oo.y - mo.y;
          int ix = list.locationToIndex(new Point(xx, yy));
          if (ix < 0) {
            ix = list.getModel().getSize();
          }
          if (list.getCaretIndex() != ix || list != currentdroplist) {
            if (currentdroplist != null) {
              currentdroplist.setCaretIndex( -1);
            }
            list.setCaretIndex(ix);
            currentdroplist = list;
          }
        }
        else if (currentdroplist != null) {
          currentdroplist.setCaretIndex( -1);
          currentdroplist = null;
        }
      }
    }
  };
  JMenuItem jMenuItem1 = new JMenuItem();

  /**
   When window is resized, reset the sizes of the peripheral panels
   to 1/4 of the height or width.
   */
  public void setPanelSizes() {
    int w = getWidth(), h = getHeight();
    causepanel.setPreferredSize(new Dimension(w / 4, 100));
    effectspanel.setPreferredSize(new Dimension(w / 4, 100));
    subpanel.setPreferredSize(new Dimension(100, h / 4));
    superpanel.setPreferredSize(new Dimension(100, h / 4));
    invalidate();
    validateTree();
    repaint();
  }

  /**
   find what was right-clicked on, and move it to the centre panel.
   */
  void menuGoto(ActionEvent e) {
    Entity ent = (Entity) popupSource.getSelectedValue();
    if (ent != null) {
      setEntity(ent);
    }
  }

  /**
   create a new item in the appropriate relationship to the current
   entity. Then select it.
   */
  void addNewItem(ActionEvent e) {
    int conn = Entity.inverseOf(relationFromList(popupSource));

    Entity ne = new Entity(entity, conn);
    setEntity(ne);
    JTextField t = entitypanel.namepanel;
    t.requestFocus();
    t.setSelectionStart(0);
    t.setSelectionEnd(t.getText().length());
  }

  int relationFromList(JList list) {
    if (list == superlist) {
      return Entity.PARENT;
    }
    if (list == sublist) {
      return Entity.CHILD;
    }
    if (list == causelist) {
      return Entity.CAUSE;
    }
    if (list == effectlist) {
      return Entity.EFFECT;
    }
    return -1;
  }

  void menuDelete(ActionEvent e) {
    deleteSelectedItem(popupSource);
  }

  void deleteSelectedItem(JList list) {
    Object[] o=list.getSelectedValues();
    boolean changed=false;
    for(int i=0;i<o.length;i++){
      Entity ent = (Entity) o[i];
      if (ent != null) {
        entity.disconnect(ent, relationFromList(list));
        changed=true;
      }
    }
    if(changed)redisplay();
  }

  void menuQuickAdd(ActionEvent e) {
    QuickAddDialog q = new QuickAddDialog(entity);
    q.show();
    if (q.entity != null) {
      entity.connect(q.entity, relationFromList(popupSource));
      if (q.createdNewEntity) {
        setEntity(q.entity);
      }
    }
    redisplay();
  }

  void menuAddExist(ActionEvent e) {
    EntityChooser ec = new EntityChooser();
    ec.setModal(true);
    ec.navigator.setEntity(entity);
    ec.show();
    if (ec.entity != null) {
      entity.connect(ec.entity, relationFromList(popupSource));
    }
    redisplay();
  }

  public void redisplay() {
    setEntity(entity);
  }

  public JList getFocusedList() {
    if (superlist.hasFocus()) {
      return superlist;
    }
    if (sublist.hasFocus()) {
      return sublist;
    }
    if (causelist.hasFocus()) {
      return causelist;
    }
    if (effectlist.hasFocus()) {
      return effectlist;
    }
    return null;
  }

}

class SList
    extends JList {
  public String getToolTipText(MouseEvent e) {
    int ix = locationToIndex(e.getPoint());
    if (ix >= 0 && ix < getModel().getSize()) {
      Object o = getModel().getElementAt(ix);
      return o.toString();
    }
    else {
      return super.getToolTipText(e);
    }
  }

  int caret = -1;
  static int dw = 0;
  Color caretColor = getSelectionBackground();
  public void setSelectionBackground(Color c) {
    super.setSelectionBackground(c);
    if (c != null) {
      caretColor = new Color(c.getRed() / 2, c.getGreen() / 2, c.getBlue() / 2);
    }
    else {
      caretColor = Color.gray;
    }
  }

  /**
   set caret position and if not in view, then scroll one step in that
   direction
   */
  public void setCaretIndex(int ci) {
    caret = ci;
    if (caret >= 0) {
      ensureIndexIsVisible(caret);
    }
    repaint();
  }

  public int getCaretIndex() {
    return caret;
  }

  /**
   Call default paint routine and then draw caret as horizontal line
   */
  public void paint(Graphics g) {
    super.paint(g);
    if (caret < 0) {
      return;
    }

    Point p = indexToLocation(caret);
    if (p == null) {
      p = new Point();
    }
    if (caret == getModel().getSize()) {
      if (caret == 0) {
        p.y = 1;
      }
      else {
        p = indexToLocation(caret - 1);
        int h = (int)this.getCellRenderer().getListCellRendererComponent(this,
            this.getModel().getElementAt(caret - 1), caret - 1, false, false).
            getPreferredSize().getHeight();
        p.y += h;
      }
    }
    g.setColor(caretColor);
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(3));
    g.drawLine(dw, p.y, getWidth() - dw, p.y);
  }

  class SListUI
      extends BasicListUI {
    protected MouseInputListener createMouseInputListener() {
      return new MyMouseInputHandler();
    }

    public class MyMouseInputHandler
        extends MouseInputHandler {
      public void mouseDragged(MouseEvent e) {
        // do some extra work when the mouse moves
        //super.mouseDragged(e);
      }
    }
  }

  public SList() {
    setUI(new SListUI());
  }
}

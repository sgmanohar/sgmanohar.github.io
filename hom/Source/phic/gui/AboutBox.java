package phic.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.HTMLEditorKit;
import phic.Resource;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Enumeration;
import phic.Current;
import phic.common.Organ;

/**
 * Display a about-box dialog. This class extends ModalDialog, so that the
 * calculations are paused while the dialog is shown.
 * It displays a file "resources/About.html" in the right hand panel,
 * and the image "resources/HOMLogo.gif" in the left panel.
 * @todo stop it auto-scrolling to the bottom of the text-box!
 */
public class AboutBox extends ModalDialog{
  private JPanel jPanel2=new JPanel();
  private JButton jButton1=new JButton();
  private Border border1;
  ImageIcon im=new ImageIcon(Resource.loader.getImageResource("HOMLogo.gif"));
  private Border border2;
  private HTMLEditorKit hTMLEditorKit1=new HTMLEditorKit();
  private Border border3;
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JLabel pic = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  Border border4;
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel4 = new JPanel();
  Border border5;
  JPanel jPanel6 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabel1 = new JLabel();
  JTextField maxmemtxt = new JTextField();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  JButton jButton2 = new JButton();
  DefaultTableModel defaultTableModel1 = new DefaultTableModel();
  JLabel jLabel2 = new JLabel();
  JTextField totmemtxt = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField freememtxt = new JTextField();
  JTable jTable1 = new JTable();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel9 = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  JScrollPane jScrollPane2 = new JScrollPane();
  BorderLayout borderLayout6 = new BorderLayout();
  Border border6;
  Border border7;
  JLabel jLabel4 = new JLabel();
  JTextField varstxt = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField vvarstext = new JTextField();
  JEditorPane jEditorPane1 = new JEditorPane(){
    public void paint(Graphics g){
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      super.paint(g);
  }};
  Border border8;
  JLabel jLabel6 = new JLabel();
  JTextField ncontrtxt = new JTextField();
  JLabel jLabel7 = new JLabel();
  JTextField norgantxt = new JTextField();

  public AboutBox(){
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    pic.setIcon(im);
    try{
      hTMLEditorKit1.read(Resource.loader.getResource("About.html"),
        jEditorPane1.getDocument(),0);
    } catch(Exception e){
      e.printStackTrace();
    }
    setPreferredSize(new Dimension(500,340));

    //make sure top left is visible.
    jEditorPane1.scrollRectToVisible(new Rectangle(0,0,1,1));
    jEditorPane1.setCaretPosition(1);
    updateStats();
  }

  private void jbInit() throws Exception{
    border1=BorderFactory.createEmptyBorder(5,5,5,5);
    border2=BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    border3=BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    border4 = BorderFactory.createEmptyBorder(5,5,5,5);
    border5 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    border6 = BorderFactory.createEmptyBorder(5,5,5,5);
    border7 = BorderFactory.createEmptyBorder(5,5,5,5);
    border8 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),BorderFactory.createEmptyBorder(2,2,2,2));
    this.setResizable(false);
    this.setTitle("About this program");
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener(){
      public void actionPerformed(ActionEvent e){
        jButton1_actionPerformed(e);
      }
    });
    jEditorPane1.setBackground(UIManager.getColor("control"));
    jEditorPane1.setBorder(null);
    jEditorPane1.setEditable(false);
    jEditorPane1.setEditorKit(hTMLEditorKit1);
    jPanel1.setBorder(border1);
    jPanel1.setLayout(borderLayout1);
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.
      HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setBorder(border3);
    jPanel3.setBorder(border4);
    jPanel3.setLayout(borderLayout2);
    jPanel4.setBorder(border5);
    jPanel4.setLayout(borderLayout3);
    jLabel1.setText("Maximum memory");
    maxmemtxt.setText("0");
    maxmemtxt.setColumns(14);
    maxmemtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jPanel5.setLayout(gridLayout1);
    jButton2.setSelected(false);
    jButton2.setText("Collect garbage");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jLabel2.setRequestFocusEnabled(true);
    jLabel2.setText("Allocated memory");
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(0);
    gridLayout1.setRows(7);
    totmemtxt.setToolTipText("");
    totmemtxt.setSelectionStart(0);
    totmemtxt.setText("0");
    totmemtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel3.setText("Free memory");
    freememtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    freememtxt.setText("0");
    freememtxt.setSelectionStart(0);
    freememtxt.setToolTipText("");
    jTable1.setModel(defaultTableModel1);
    jPanel6.setLayout(borderLayout4);
    jPanel8.setLayout(borderLayout5);
    jPanel9.setLayout(borderLayout6);
    jPanel9.setBorder(border6);
    jPanel6.setBorder(border7);
    jLabel4.setText("Number of Node Variables");
    varstxt.setText("0");
    varstxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel5.setText("Number of Visible Variables");
    vvarstext.setText("0");
    vvarstext.setHorizontalAlignment(SwingConstants.TRAILING);
    pic.setBorder(border8);
    jLabel6.setText("Number of controllers");
    ncontrtxt.setText("0");
    ncontrtxt.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel7.setText("Number of organs");
    norgantxt.setText("0");
    norgantxt.setHorizontalAlignment(SwingConstants.TRAILING);
    borderLayout1.setHgap(5);
    this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
    jPanel2.add(jButton1,null);
    this.getContentPane().add(jTabbedPane1,  BorderLayout.CENTER);
    jTabbedPane1.add(jPanel1,   "About Human Physiology");
    jPanel1.add(pic, BorderLayout.WEST);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    jTabbedPane1.add(jPanel3,   "Memory");
    jPanel3.add(jPanel4,  BorderLayout.CENTER);
    jScrollPane1.getViewport().setView(jEditorPane1);
    jPanel4.add(jPanel6, BorderLayout.CENTER);
    jPanel6.add(jPanel5,  BorderLayout.NORTH);
    jPanel5.add(jLabel1, null);
    jPanel5.add(maxmemtxt, null);
    jPanel5.add(jLabel2, null);
    jPanel5.add(totmemtxt, null);
    jPanel5.add(jLabel3, null);
    jPanel4.add(jPanel7,  BorderLayout.SOUTH);
    jPanel7.add(jButton2, null);
    jTabbedPane1.add(jPanel8,  "System properties");
    jPanel5.add(freememtxt, null);
    jPanel5.add(jLabel4, null);
    jPanel5.add(varstxt, null);
    jPanel5.add(jLabel5, null);
    jPanel5.add(vvarstext, null);
    jPanel5.add(jLabel6, null);
    jPanel5.add(ncontrtxt, null);
    jPanel5.add(jLabel7, null);
    jPanel5.add(norgantxt, null);
    jPanel8.add(jPanel9, BorderLayout.CENTER);
    jPanel9.add(jScrollPane2,  BorderLayout.CENTER);
    jScrollPane2.getViewport().setView(jTable1);
  }

  void jButton1_actionPerformed(ActionEvent e){
    hide();
  }
  public void updateStats(){
    defaultTableModel1.setColumnIdentifiers(new String[]{"Property","Name"});
    while(defaultTableModel1.getRowCount()>0) defaultTableModel1.removeRow(0);
    Properties p=System.getProperties();
    for(Enumeration i=p.propertyNames(); i.hasMoreElements();){
      Object o = i.nextElement();
      defaultTableModel1.addRow(new Object[]{o, p.get(o)});
    }
//    maxmemtxt.setText(Runtime.getRuntime().maxMemory()+" b");
    totmemtxt.setText(Runtime.getRuntime().totalMemory()+" b");
    freememtxt.setText(Runtime.getRuntime().freeMemory()+" b");
    varstxt.setText(String.valueOf(Node.allNodes.size()));
    vvarstext.setText(String.valueOf(Variables.variable.length));
    ncontrtxt.setText(String.valueOf(Current.body.getControllerList().getControllers().length ));
    norgantxt.setText(String.valueOf(Organ.getList().size()));
  }


  void jButton2_actionPerformed(ActionEvent e) {
    System.gc();
    updateStats();
  }
}

package phic.gui.exam;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import phic.Body;
import phic.gui.ModalDialog;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import phic.common.Organ;
import java.awt.event.WindowEvent;
import phic.gui.HorizontalBar;
import phic.common.Ticker;
import phic.gui.HTMLMessagePane;

/**
 * Dialog box to choose the examinations to perform
 */
public class ExaminationsDialog extends ModalDialog implements Ticker{
  /**
   * This is the list of examination classes.
   * Each one must be an instance of Examination, and the createPanel()
   * method will be called as appropriate and the panel will be displayed
   * on the right of the dialog.
   */
  Class[] examinations=new Class[]{
    Observations.class, SkinColour.class, phic.ecg.PhicECGExamination.class,
    EyeExamination.class,ReflexExamination.class,
    BloodFilmExam.class, AuscultationExam.class,
    phic.gui.graphics.GasesDiagram.class,
    CompartmentExamination.class
  };




  private DefaultListModel examlistmodel=new DefaultListModel();
  private JPanel jPanel1=new JPanel();
  private BorderLayout borderLayout1=new BorderLayout();
  private JPanel jPanel2=new JPanel();
  private JPanel jPanel3=new JPanel();
  private JButton okbutton=new JButton();
  private BorderLayout borderLayout2=new BorderLayout();
  private JPanel jPanel4=new JPanel();
  private JPanel jPanel5=new JPanel();
  private BorderLayout borderLayout3=new BorderLayout();
  private JPanel jPanel6=new JPanel();
  private JScrollPane jScrollPane1=new JScrollPane();
  private JList examlist=new JList();
  private BorderLayout borderLayout4=new BorderLayout();
  private JPanel jPanel7=new JPanel();
  private JLabel jLabel1=new JLabel();
  private JPanel jPanel8=new JPanel();
  private BorderLayout borderLayout5=new BorderLayout();
  private JPanel exampanel=new JPanel();
  private Border border1;
  private BorderLayout borderLayout6=new BorderLayout();
  public ExaminationsDialog(Body b){
    body=b;
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    setPreferredSize(new Dimension(500,350));
    initialiseList();
    setTitle("Examine "+phic.Current.person.name);
    if(!MAKE_MODAL){
      setModal(false);
      addWindowListener(showListener);
    }
  }

  WindowListener showListener = new WindowAdapter(){
    public void windowOpened(WindowEvent e){
      HorizontalBar.addBar(ExaminationsDialog.this);
    }
    public void windowClosing(WindowEvent e){
      HorizontalBar.removeBar(ExaminationsDialog.this);
    }
  };
  double serialTime = 0;
  public void tick(double time){
    serialTime+=time;
    if(currentExamination!=null){
      double ufs = currentExamination.getUpdateFrequencySeconds();
      if(ufs>0 && serialTime>ufs){
        currentExamination.initialise(body);
        serialTime=0;
      }
    }
  }

  public static boolean MAKE_MODAL = false;

  private void jbInit() throws Exception{
    border1=BorderFactory.createEmptyBorder(5,5,5,5);
    jPanel1.setLayout(borderLayout1);
    okbutton.setText("OK");
    okbutton.addActionListener(new java.awt.event.ActionListener(){
      public void actionPerformed(ActionEvent e){
        okbutton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(borderLayout2);
    jPanel4.setLayout(borderLayout3);
    jScrollPane1.setPreferredSize(new Dimension(159,131));
    jPanel6.setLayout(borderLayout4);
    jLabel1.setText("Select examination");
    jPanel5.setLayout(borderLayout5);
    jPanel5.setBorder(border1);
    exampanel.setBorder(BorderFactory.createLoweredBevelBorder());
    exampanel.setLayout(borderLayout6);
    examlist.addListSelectionListener(new javax.swing.event.
      ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
        examlist_valueChanged(e);
      }
    });
    examlist.setModel(examlistmodel);
    helpbutton.setAction(helpAction);
    this.getContentPane().add(jPanel1,BorderLayout.CENTER);
    jPanel1.add(jPanel2,BorderLayout.CENTER);
    jPanel2.add(jPanel4,BorderLayout.WEST);
    jPanel4.add(jPanel6,BorderLayout.CENTER);
    jPanel6.add(jScrollPane1,BorderLayout.CENTER);
    jPanel4.add(jPanel7,BorderLayout.NORTH);
    jPanel7.add(jLabel1,null);
    jScrollPane1.getViewport().add(examlist,null);
    jPanel2.add(jPanel5,BorderLayout.CENTER);
    jPanel5.add(jPanel8,BorderLayout.SOUTH);
    jPanel5.add(exampanel,BorderLayout.CENTER);
    jPanel1.add(jPanel3,BorderLayout.SOUTH);
    jPanel3.add(okbutton,null);
    jPanel3.add(helpbutton, null);
  }

  void okbutton_actionPerformed(ActionEvent e){
    hide();
  }


  JPanel currentExamDisplay=null;
  Body body;
  void setExamination(Examination e){
    if(currentExamDisplay!=null){
      exampanel.remove(currentExamDisplay);
    }
    currentExamDisplay=e.createPanel();
    if(body!=null){
      e.initialise(body);
    }
    exampanel.add(currentExamDisplay);
    exampanel.invalidate();
    exampanel.validate();
    exampanel.repaint();
    currentExamination=e;
  }

  void initialiseList(){
    for(int i=0;i<examinations.length;i++){
      try{
        Examination newExam=(Examination)examinations[i].newInstance();
        examlistmodel.addElement(newExam);
      } catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  int lastSelection = -1;
  Examination currentExamination;
  JButton helpbutton = new JButton();
  void examlist_valueChanged(ListSelectionEvent e){
    int selection = examlist.getSelectedIndex();
    if(selection!=lastSelection){
      lastSelection=selection;
      setExamination((Examination)examlist.getSelectedValue());
    }
  }

  Action helpAction = new AbstractAction("Help"){
    public void actionPerformed(ActionEvent e){
      HTMLMessagePane.showDialog("help/Examination.html", "Examination help");
    }
  };
}

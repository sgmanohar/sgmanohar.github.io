package phic.gui.exam;
import phic.Body;
import medicine.*;
import medicine.gui.*;
import java.awt.*;
import javax.swing.*;
import phic.gui.*;
import java.util.Vector;
import javax.swing.border.*;
import javax.swing.event.*;
import phic.Brain;

/**
 * Class to display current observations on the patient.
 */
public class Observations extends JPanel implements Examination{
  BorderLayout borderLayout1=new BorderLayout();
  BorderLayout borderLayout4=new BorderLayout();
  DefaultListModel signmodel=new DefaultListModel();
  DefaultListModel symptommodel=new DefaultListModel();
  JPanel jPanel1=new JPanel();
  GridLayout gridLayout1=new GridLayout();
  JPanel jPanel2=new JPanel();
  JPanel jPanel3=new JPanel();
  JList jList1=new JList();
  JScrollPane jScrollPane1=new JScrollPane();
  JList jList2=new JList();
  JScrollPane jScrollPane2=new JScrollPane();
  BorderLayout borderLayout2=new BorderLayout();
  BorderLayout borderLayout3=new BorderLayout();
  JLabel jLabel1=new JLabel();
  JLabel jLabel2=new JLabel();
  Border border1;
  Border border2;
  JScrollPane jScrollPane3=new JScrollPane();
  JTextArea description=new JTextArea();
  JPanel jPanel4=new JPanel();
  JTextArea observation=new JTextArea();

  public Observations(){
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    p=createPathologyDialog();
    p.getMainFrame().hide();
  }

  public JPanel createPanel(){
    return this;}

  /** Permanent entities representing the ancestors of all signs and symptoms */
  Entity SIGN,SYMPTOM;
  /**
   * A list of the descriptions of each of the signs and symptoms in the list boxes,
   * arranged in index order, as they appear.
   */
  Vector signDetail,symptomDetail;
  //Dialog loaded synchronously
  PathologyAnalysisDialog p;

  /** find all pathologies that directly cause, or are also, signs.*/
  public void initialise(Body body){
    createObs(body);
    if(body.brain.getFeeling()==Brain.DEAD)
      return;
    //analyse using the medicine program -
    Vector v=p.readPathologies();
    signDetail=new Vector();symptomDetail=new Vector();
    signmodel.removeAllElements();
    symptommodel.removeAllElements();
    try{
      SIGN=Entities.getSpecificNamedEntity("Sign",p.getDefaultEntity());
      SYMPTOM=Entities.getSpecificNamedEntity("Symptom",p.getDefaultEntity());
      for(int i=0;i<v.size();i++){
        Entity e=(Entity)v.get(i);
        if(!tryAdd(p,e,null)){
          for(int j=0;j<e.effects.size();j++){
            Entity e2=(Entity)e.effects.get(j);
            tryAdd(p,e2,e);
          }
        }
      }
    }catch(NullPointerException e){
      // thrown if the MainFrame has not been able to load its data.
      // such an error should have already given a message earlier.
    }
  }

  /**
   * Adds the entity to the lists - either list of signs or symptoms.
   * Returns false if the entity is neither a sign nor a symptom.
   * if efrom is not null, it being consequent on another entity, efrom.
   */
  boolean tryAdd(PathologyAnalysisDialog p,Entity e,Entity efrom){
    if(Entities.isChildOf(e,SIGN)){
      signmodel.addElement(e);
      Object desc=p.getPathologyFromEntity(e);
      if(desc!=null){
        signDetail.add(desc);
      } else if(efrom!=null){
        signDetail.add("Consequence of "+efrom.toString());
      } else
        signDetail.add("");
      return true;
    } else if(Entities.isChildOf(e,SYMPTOM)){
      symptommodel.addElement(e);
      Object desc=p.getPathologyFromEntity(e);
      if(desc!=null){
        symptomDetail.add(desc);
      } else if(efrom!=null){
        symptomDetail.add("Consequence of "+efrom.toString());
      } else
        symptomDetail.add("");
      return true;
    } else
      return false;
  }


  public Entity[] getPathologies(){ return null;  }

  public Entity[] getSigns(){ return null; }

  public String getName(){
    return "Observation";
  }

  private void jbInit() throws Exception{
    border1=BorderFactory.createEmptyBorder(5,5,5,5);
    border2=BorderFactory.createEmptyBorder(5,5,5,5);
    this.setLayout(borderLayout1);
    jPanel1.setLayout(gridLayout1);
    jList1.setModel(signmodel);
    jList1.addListSelectionListener(new
      Observations_jList1_listSelectionAdapter(this));
    jList2.setModel(symptommodel);
    jList2.addListSelectionListener(new
      Observations_jList2_listSelectionAdapter(this));
    jPanel3.setLayout(borderLayout2);
    jPanel2.setLayout(borderLayout3);
    jLabel1.setVerifyInputWhenFocusTarget(true);
    jLabel1.setText("Signs");
    jLabel2.setText("Symptoms");
    jPanel3.setBorder(border1);
    jPanel2.setBorder(border2);
    description.setEditable(false);
    description.setText("");
    description.setLineWrap(true);
    description.setRows(3);
    description.setTabSize(8);
    description.setWrapStyleWord(true);
    jPanel4.setLayout(borderLayout4);
    observation.setEditable(false);
    observation.setText("");
    observation.setLineWrap(true);
    observation.setRows(3);
    observation.setWrapStyleWord(true);
    this.add(jPanel1,BorderLayout.CENTER);
    jPanel1.add(jPanel3,null);
    jPanel3.add(jScrollPane1,BorderLayout.CENTER);
    jPanel3.add(jLabel1,BorderLayout.NORTH);
    jScrollPane1.getViewport().add(jList1,null);
    jPanel1.add(jPanel2,null);
    jPanel2.add(jScrollPane2,BorderLayout.CENTER);
    jPanel2.add(jLabel2,BorderLayout.NORTH);
    this.add(jScrollPane3,BorderLayout.SOUTH);
    this.add(jPanel4,BorderLayout.NORTH);
    jPanel4.add(observation,BorderLayout.CENTER);
    jScrollPane3.getViewport().add(description,null);
    jScrollPane2.getViewport().add(jList2,null);
  }
  /** When a selection is made, display its description in the bottom panel */
  void jList1_valueChanged(ListSelectionEvent e){
    int i=jList1.getSelectedIndex();
    if(i>=0){
      Object s=signDetail.get(i);if(s!=null)
        description.setText(s.toString());
    } else
      description.setText("");
  }

  void jList2_valueChanged(ListSelectionEvent e){
    int i=jList2.getSelectedIndex();
    if(i>=0){
      Object s=symptomDetail.get(i);if(s!=null)
        description.setText(s.toString());
    } else
      description.setText("");
  }

  /** Creates the observation text at the top */
  private void createObs(Body   body){
    String name=phic.Current.person.name;
    int feeling=body.brain.getFeeling();
    //describe the conscious state
    String text=name+" is "+ConsciousStateLabel.stateStrings[feeling];
    //describe the posture
    if(feeling!=Brain.DEAD)
      text+=" and "+postureString(phic.Current.environment.Uprt.get());
    //describe desires
    if(feeling<Brain.UNCONSCIOUS){
      text+=", with a pain score of "+(int)(body.brain.pain.get()*100)+"%. ";
      if(body.brain.hunger.get()>0.75&&body.brain.thirst.get()<0.75)
        text+=name+" is hungry. ";
      else if(body.brain.thirst.get()>0.75&&body.brain.hunger.get()<0.75)
        text+=name+" is thirsty. ";
      else if(body.brain.thirst.get()>0.75&&body.brain.hunger.get()>0.75)
        text+=name+" is hungry and thirsty. ";
    }
    //describe the skin
    double sktmp=body.skin.Temp.get(), swr=body.skin.SwR.get();
    String skt = null, sks=null;
    if(sktmp>28) skt= "hot"; else if(sktmp<20) skt="cold";
    if(swr<0.001) sks="dry"; else if (swr>0.004) sks="sweaty";
    if(sks!=null && skt!=null) text+="The skin is "+skt+" and "+sks+". ";
    else if (sks!=null) text+="The skin is "+sks+". ";
    else if (skt!=null) text+="The skin is "+skt+". ";

    observation.setText(text);
  }

  /**
   *  Returns a posture string given the uprightness
   * @todo put into a lookup table
   */public static String postureString(double uprt){
    int p;
    if(uprt>0.90)
      p=0;
    else if(uprt>0.70)
      p=1;
    else if(uprt>0.55)
      p=2;
    else if(uprt>0.45)
      p=3;
    else if(uprt>0.2)
      p=4;
    else
      p=5;
    return postureString[p];
  }

  /** Descriptions of posture */
  public static String[] postureString={"standing up","sitting down",
    "semi-recumbent","lying flat","lying with head tilted down",
    "standing upside down"
  };
  public String toString(){    return getName();}
  public double getUpdateFrequencySeconds(){ return 2;}

  /**
   * Create the pathology dialog in a new thread, and return when it is
   *   completed
   */
  protected PathologyAnalysisDialog createPathologyDialog() {
    class R implements Runnable{
      PathologyAnalysisDialog result;
      public synchronized void run(){
          result = new PathologyAnalysisDialog();
          notify();
      };
    }
    R r= new R();
    try{
      synchronized (r) {
        new Thread(r).start();
        r.wait();
      }
    }catch(InterruptedException x){x.printStackTrace();}
    return r.result;
  }
}


class Observations_jList1_listSelectionAdapter implements javax.swing.event.
  ListSelectionListener{
  Observations adaptee;
  Observations_jList1_listSelectionAdapter(Observations adaptee){
    this.adaptee=adaptee;
  }

  public void valueChanged(ListSelectionEvent e){
    adaptee.jList1_valueChanged(e);
  }
}


class Observations_jList2_listSelectionAdapter implements javax.swing.event.
  ListSelectionListener{
  Observations adaptee;
  Observations_jList2_listSelectionAdapter(Observations adaptee){
    this.adaptee=adaptee;
  }

  public void valueChanged(ListSelectionEvent e){
    adaptee.jList2_valueChanged(e);
  }
}

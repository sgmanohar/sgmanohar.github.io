package phic.gui;
import phic.Person;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import phic.modifiable.Range;
import phic.gui.exam.SkinColour;
import phic.Current;

/**
 * Shows small dialog to change name of person.
 * Now includes description, age, sex, BMI and height.
 * Can invoke a separate editor BuildABod to fiddle with values.
 */
public class PersonSetupDialog extends ModalDialog{
  JPanel jPanel1=new JPanel();
  Box box1;
  JPanel jPanel2=new JPanel();
  JButton jButton1=new JButton();
  JButton jButton2=new JButton();
  Person person;
  JButton buildabodb=new JButton();
  JPanel jPanel3=new JPanel();
  GridLayout gridLayout1=new GridLayout();
  JPanel jPanel4=new JPanel();
  JPanel jPanel5=new JPanel();
  JPanel jPanel6=new JPanel();
  JLabel jLabel3=new JLabel();
  JTextField agetxt=new JTextField();
  JRadioButton ismale=new JRadioButton();
  JRadioButton isfemale=new JRadioButton();
  GridLayout gridLayout2=new GridLayout();
  JLabel jLabel4=new JLabel();
  JTextField httxt=new JTextField();
  JLabel jLabel5=new JLabel();
  JLabel jLabel6=new JLabel();
  JPanel jPanel7=new JPanel();
  JLabel jLabel1=new JLabel();
  JComboBox namefield=new JComboBox();
  FlowLayout flowLayout1=new FlowLayout();
  ButtonGroup buttonGroup1=new ButtonGroup();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea descfield = new JTextArea();
  Border border1;
  BorderLayout borderLayout3 = new BorderLayout();
  Border border2;
  JPanel jPanel9 = new JPanel();
  JLabel jLabel7 = new JLabel();
  JTextField bmitxt = new JTextField();
  JLabel jLabel8 = new JLabel();
  DefaultComboBoxModel nameModel = new DefaultComboBoxModel();
  /**
   * setup the dialog from the given person
   */
  public PersonSetupDialog(Person person){
    this();
    this.person=person;
    namefield.setSelectedItem(person.name);
    setupDialogFieldsFromPerson(person);
    people = Person.getResourceNames();
    for(int i=0;i<people.length;i++){nameModel.addElement(people[i]);}
    getRootPane().setDefaultButton(jButton1);
  }

  /**
   * Makes the controls in the dialog reflect the parameters of the given person
   */
  void setupDialogFieldsFromPerson(Person person){
    descfield.setText(person.description);
    agetxt.setText(String.valueOf(person.age));
    httxt.setText(String.valueOf(person.height));
    bmitxt.setText(String.valueOf(person.BMI));
    if(person.sex == Person.MALE) ismale.setSelected(true); else isfemale.setSelected(true);
    skinColorLabel.basePigment= person.skinBasePigment;
    for(int i=0;i<skinColors.length;i++) if(skinColorLabel.basePigment.equals(skinColors[i])) colorIndex=i;
  }

  PersonSetupDialog(){
    try{
      jbInit();
    } catch(Exception e){
      e.printStackTrace();
    }
    pack();
    edited=false;
    buildabodb.setEnabled(allowBuildABod);

  }
  public static boolean allowBuildABod=true;
  private void jbInit() throws Exception{
    box1=Box.createHorizontalBox();
    border1 = BorderFactory.createEmptyBorder(5,5,5,5);
    border2 = BorderFactory.createEmptyBorder(5,5,5,5);
    border3 = BorderFactory.createEtchedBorder();
    this.setTitle("Edit patient details");
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener(){
      public void actionPerformed(ActionEvent e){
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("Cancel");
    jButton2.addActionListener(new java.awt.event.ActionListener(){
      public void actionPerformed(ActionEvent e){
        jButton2_actionPerformed(e);
      }
    });
    buildabodb.setToolTipText("Edit the person\'s properties");
    buildabodb.setText("Edit...");
    buildabodb.addActionListener(new java.awt.event.ActionListener(){
      public void actionPerformed(ActionEvent e){
        buildabodb_actionPerformed(e);
      }
    });
    jPanel3.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setHgap(0);
    gridLayout1.setRows(5);
    jLabel3.setText("Age:");
    agetxt.setCaretPosition(0);
    agetxt.setText("");
    agetxt.setColumns(4);
    jPanel6.setBorder(BorderFactory.createEtchedBorder());
    jPanel6.setLayout(gridLayout2);
    ismale.setSelected(false);
    ismale.setText("Male");
    isfemale.setSelected(false);
    isfemale.setText("Female");
    gridLayout2.setColumns(1);
    gridLayout2.setRows(2);
    jLabel4.setText("Height:");
    httxt.setText("");
    httxt.setColumns(4);
    jLabel5.setText("m");
    jLabel6.setText("yrs");
    jLabel1.setText("Name:");
    namefield.setFont(new java.awt.Font("SansSerif",1,12));
    namefield.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        namefield_itemStateChanged(e);
      }
    });
    jPanel4.setLayout(flowLayout1);
    jPanel1.setLayout(borderLayout1);
    jPanel8.setLayout(borderLayout2);
    jLabel2.setText("Description");
    descfield.setFont(new java.awt.Font("SansSerif",0,12));
    descfield.setLineWrap(true);
    descfield.setWrapStyleWord(true);
    jPanel8.setBorder(border1);
    jPanel7.setLayout(borderLayout3);
    jPanel7.setBorder(border2);
    jLabel7.setVerifyInputWhenFocusTarget(true);
    jLabel7.setText("BMI:");
    bmitxt.setSelectedTextColor(Color.white);
    bmitxt.setText("");
    bmitxt.setColumns(4);
    jLabel8.setText("kg/sq m");
    jLabel9.setText("Skin:");
    jButton3.setMargin(new Insets(2, 2, 2, 2));
    jButton3.setText("Change");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        skinChangeAction(e);
      }
    });
    this.getContentPane().add(jPanel1,BorderLayout.CENTER);
    jPanel1.add(box1, BorderLayout.EAST);
    jPanel1.add(jPanel7, BorderLayout.NORTH);
    jPanel7.add(jLabel1,  BorderLayout.WEST);
    jPanel7.add(namefield,  BorderLayout.CENTER);
    jPanel1.add(jPanel8,  BorderLayout.CENTER);
    jPanel8.add(jLabel2,  BorderLayout.NORTH);
    jPanel8.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(descfield, null);
    this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
    jPanel2.add(jButton1,null);
    jPanel2.add(jButton2,null);
    jPanel2.add(buildabodb,null);
    this.getContentPane().add(jPanel3,BorderLayout.EAST);
    jPanel3.add(jPanel4,null);
    jPanel4.add(jLabel3,null);
    jPanel4.add(agetxt,null);
    jPanel4.add(jLabel6,null);
    jPanel3.add(jPanel9, null);
    jPanel9.add(jLabel7, null);
    jPanel9.add(bmitxt, null);
    jPanel9.add(jLabel8, null);
    jPanel3.add(jPanel6,null);
    jPanel6.add(ismale,null);
    jPanel6.add(isfemale,null);
    jPanel3.add(jPanel5,null);
    jPanel5.add(jLabel4,null);
    jPanel5.add(httxt,null);
    jPanel5.add(jLabel5,null);
    jPanel3.add(jPanel10, null);
    jPanel10.add(jLabel9, null);
    jPanel10.add(skinColorLabel.jPanel1, null);
    //skinColorLabel.jPanel1.setBorder(border3);
    //skinColorLabel.jPanel1.setOpaque(true);
    skinColorLabel.jPanel1.setPreferredSize(new Dimension(20, 15));
    jPanel10.add(jButton3, null);
    buttonGroup1.add(ismale);
    buttonGroup1.add(isfemale);
    namefield.setEditable(true);
    namefield.setModel(nameModel);
    skinColorLabel.initialise(Current.body);
  }

  /** OK pressed */
  void jButton1_actionPerformed(ActionEvent e){
    if(person.name!=namefield.getSelectedItem().toString()){
      person.name = namefield.getSelectedItem().toString();
      try{ person.setupParametersFromResource(person.name); }
      catch(Exception x){x.printStackTrace();}
    }
    person.description=descfield.getText();
    /** If the custom editor has been invoked, do not re-estimate the parameters */
    if(!edited) {
      if(!updatePatientParameters())
        return;
    }
    okPressed=true;
    hide();
  }
  public boolean okPressed = false;

  /** Cancel pressed */
  void jButton2_actionPerformed(ActionEvent e){
    hide();
  }

  /** Permitted ranges of values */
  Range ageRange = new Range(15,90),
        heightRange = new Range(1,2.5),
        BMIrange = new Range(10,40);

  /**
   * Read the dialog fields into the current Person, and call
   * Person.calculateEstimates to process these values, and generate body
   * variables.
   * @return false if the values inputted are illegal - i.e. keep the box open.
   */
  boolean updatePatientParameters(){
    try{
      person.age=Double.parseDouble(agetxt.getText());
      person.height=Double.parseDouble(httxt.getText());
      person.sex=ismale.isSelected()?Person.MALE:person.FEMALE;
      person.BMI=Double.parseDouble(bmitxt.getText());
      if(!ageRange.contains(person.age))  throw new IllegalArgumentException("Age must be in the range "+ageRange+" yrs");
      if(!heightRange.contains(person.height))  throw new IllegalArgumentException("Height must be in the range "+heightRange+" metres");
      if(!BMIrange.contains(person.BMI))  throw new IllegalArgumentException("BMI must be in the range "+BMIrange);

      person.calculateEstimates();
      person.skinBasePigment = skinColors[colorIndex];
      return true;
    } catch(NumberFormatException e){
      JOptionPane.showMessageDialog(this,"Please enter a value for age and height.");
      e.printStackTrace();
      return false;
    } catch(IllegalArgumentException e){
      JOptionPane.showMessageDialog(this,e.getMessage(),"Value out of range.",
        JOptionPane.WARNING_MESSAGE);
      return false;
    }
  }
  /** Has the custom editor been used while this dialog has been open? */
  boolean edited;
  JPanel jPanel10 = new JPanel();
  JLabel jLabel9 = new JLabel();
  SkinColour skinColorLabel = new SkinColour();
  JButton jButton3 = new JButton();

  /** invoke the custom editor */
  void buildabodb_actionPerformed(ActionEvent e){
    if( !updatePatientParameters() ) return;
    BuildABod b =new BuildABod();
    b.show();
    if(b.OKpressed)edited=true;
  }

  void skinChangeAction(ActionEvent e) {
    colorIndex = (colorIndex+1)%skinColors.length;
    skinColorLabel.basePigment = skinColors[colorIndex];
    skinColorLabel.update();
  }
  int colorIndex=0;
  Color[] skinColors = {new Color(255,255,235), new Color(235,245,140),
      new Color(160,150,90), new Color(90,70,60)
  };
  Border border3;
  /**
   * A list of the recognised names of people, as loaded from the
   * Person.getResourceNames() method.
   */
  String[] people;
  /**
   * If the new selection of the person is actually an item from the list, then
   * alter the dialog fields accordingly. Otherwise do nothing: the person is
   * typing in the name field.
   */
  void namefield_itemStateChanged(ItemEvent e) {
    String s=namefield.getSelectedItem().toString();
    if(people!=null)for(int i=0;i<people.length;i++){
      if(people[i].equals(s)){
        Person p = Person.newPersonDetailsFromResource(s);
        setupDialogFieldsFromPerson(p);
      }
    }
  }
}

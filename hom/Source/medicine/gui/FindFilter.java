package medicine.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;

import medicine.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.text.ParseException;

public class FindFilter extends JPanel {
  public FindFilter(Entity any) {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    for(int i=0;i<standardStrings.length;i++)
      standardsModel.addElement(Entities.getSpecificNamedEntity(standardStrings[i],any));
    entity=any;
    currentEntity.setText( entity.name);
    entityChooserComboBox1.setEntity(entity);
  }
  Entity entity;

  protected static final String[] standardStrings = {
      "Disease","Pathology","Investigation","Sign",
      "Symptom","Substance","Treatment","Lifestyle"
  };

  public void filterEnitities(Vector list){
  }

  /**
   * filterItems filters the given vector of entities
   * according to the chosen parameters of this filter.
   * The supplied list is NOT changed.
   *
   * @param items Vector the list to be filtered
   * @return Vector a shorter list, a subset of the given items.
   */
  public Vector getFilteredItems(Vector items) throws ParseException {

    if(regexCheck.isSelected()) try{
      regex = Pattern.compile(searchText.getText());
    }catch(PatternSyntaxException e){throw new ParseException(searchText.getText(),0);}
    if(ignoreCaseCheck.isSelected()) matchLC=searchText.getText();
    Vector result=new Vector();
    for(int i=0;i<items.size();i++){
      Entity e=(Entity)items.get(i);
      if(satisfiedBy(e))result.add(e);
    }
    return result;
  }

  protected Pattern regex=null;
  protected String matchLC = null;
  /**
   * Implements the filter returning true if the supplied entity satisfies the
   * filter.
   */
  private boolean satisfiedBy(Entity e) {
    boolean satisfies=false;
    if(tabbedPane.getSelectedComponent()==textPane){
      //Text filter
      String s=searchText.getText();
      if(textMatch(s,e.name)) return true;
      if(synonymCheck.isSelected()){
        for(int i=0;i<e.synonyms.size();i++){
          if(textMatch(s,(String)e.synonyms.get(i))) satisfies= true;
        }
      }
      if(descriptionCheck.isSelected()){
        if(textMatch(s,e.description)) satisfies= true;
      }
      if(matchingRulesCheck.isSelected()){
        // TODO ADD US MATCHING RULES
      }
    }else{
      //'Is a' filter
      int rel=Entity.inverseOf(getSelectedRelation());
      Entity e2 = getSelectedEntity();
      int recursion=recursionNumberModel.getNumber().intValue();
      satisfies= Entities.isRelativeOf(e, e2, rel,includeSuperCheck.isSelected(),
                                       false ,recursion);
    }

    return includeRadio.isSelected() ? satisfies : !satisfies;
  }

  int getSelectedRelation(){
    if     (parentRadio.isSelected())return Entity.PARENT;
    else if(childRadio.isSelected()) return Entity.CHILD;
    else if(causeRadio.isSelected()) return Entity.CAUSE;
    else if(effectRadio.isSelected())return Entity.EFFECT;
    else throw new IllegalStateException("No relation selected");
  }

  Entity getSelectedEntity(){
    if(currentEntityRadio.isSelected()) return this.entity;
    if(selectEntityRadio.isSelected()) return entityChooserComboBox1.getSelectedValue();
    if(standarEntityRadio.isSelected()) return (Entity)standardsCombo.getSelectedItem();
    throw new IllegalStateException("No selected entity");
  }

  /** Match a string to the pattern */
  boolean textMatch(String pattern, String str){
    if(regexCheck.isSelected()){
      return regex.matcher(str).matches();  //REGEX
    }else{
      if(ignoreCaseCheck.isSelected()){
        if(exactCheck.isSelected()) return pattern.equalsIgnoreCase(str); //exact ignore case
        else return str.toLowerCase().indexOf(matchLC)>=0; //contains, ignore case
      }else{
        if(exactCheck.isSelected()) return pattern.equals(str); //exact, case sensitive
        else return str.indexOf(pattern)>=0; //contains, case sensitive
      }
    }
  }

  /**
   * Is this filter an Or-filter - i.e. this filter should act on a list
   * containing items that were not included by the previous filter.
   */
  public boolean isOrFilter() {
    return orCheckbox.isSelected();
  }
  boolean isSelected=false;

  /**
   * Color this item if it is selected
   */
  public void setSelected(boolean b) {
    isSelected=b;
    label.setBackground(b? Color.red : Color.lightGray);
  }

  //INTERFACE
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane tabbedPane = new JTabbedPane();
  JPanel jPanel1 = new JPanel();
  JRadioButton includeRadio = new JRadioButton();
  JRadioButton excludeRadio = new JRadioButton();
  JCheckBox orCheckbox = new JCheckBox();
  ButtonGroup bg1 = new ButtonGroup();
  JPanel textPane = new JPanel();
  JPanel isaPane = new JPanel();
  JPanel jPanel2 = new JPanel();
  JCheckBox synonymCheck = new JCheckBox();
  JCheckBox descriptionCheck = new JCheckBox();
  JCheckBox ignoreCaseCheck = new JCheckBox();
  JCheckBox matchingRulesCheck = new JCheckBox();
  JCheckBox exactCheck = new JCheckBox();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JRadioButton causeRadio = new JRadioButton();
  JRadioButton effectRadio = new JRadioButton();
  JRadioButton parentRadio = new JRadioButton();
  JRadioButton childRadio = new JRadioButton();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  GridLayout gridLayout3 = new GridLayout();
  JPanel jPanel6 = new JPanel();
  JRadioButton standarEntityRadio = new JRadioButton();
  FlowLayout flowLayout2 = new FlowLayout();
  JComboBox standardsCombo = new JComboBox();
  JRadioButton currentEntityRadio = new JRadioButton();
  JLabel currentEntity = new JLabel();
  JPanel jPanel8 = new JPanel();
  JRadioButton selectEntityRadio = new JRadioButton();
  FlowLayout flowLayout4 = new FlowLayout();
  EntityChooserComboBox entityChooserComboBox1 = new EntityChooserComboBox();
  JPanel jPanel9 = new JPanel();
  FlowLayout flowLayout5 = new FlowLayout();
  JCheckBox includeSuperCheck = new JCheckBox();
  JLabel label = new JLabel();
  JCheckBox regexCheck = new JCheckBox();
  ButtonGroup bg2 = new ButtonGroup();
  ButtonGroup bg3 = new ButtonGroup();
  DefaultComboBoxModel standardsModel=new DefaultComboBoxModel();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout6 = new FlowLayout();
  Border border1;
  Border border2;
  Border border3;
  Border border4;
  JSpinner recursionSpin = new JSpinner();
  JLabel jLabel2 = new JLabel();
  SpinnerNumberModel recursionNumberModel=new SpinnerNumberModel(4, 1, 12,1);
  BorderLayout borderLayout4 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextPane searchText = new JTextPane();

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder();
    border2 = BorderFactory.createEmptyBorder();
    border3 = BorderFactory.createEmptyBorder();
    border4 = BorderFactory.createEmptyBorder();
    this.setMaximumSize(new Dimension(32767, 32767));
    this.setLayout(borderLayout1);
    includeRadio.setPreferredSize(new Dimension(91, 23));
    includeRadio.setSelected(true);
    includeRadio.setText("Include");
    excludeRadio.setText("Exclude");
    orCheckbox.setText("Or");
    textPane.setLayout(borderLayout4);
    synonymCheck.setSelected(true);
    synonymCheck.setText("Include synonyms");
    descriptionCheck.setSelected(true);
    descriptionCheck.setText("Include description");
    ignoreCaseCheck.setSelected(true);
    ignoreCaseCheck.setText("Ignore case");
    matchingRulesCheck.setSelected(true);
    matchingRulesCheck.setText("US matching rules");
    exactCheck.setText("Exact text");
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    isaPane.setLayout(borderLayout3);
    causeRadio.setText("Cause");
    effectRadio.setSelected(false);
    effectRadio.setText("Effect");
    parentRadio.setText("Superclass");
    childRadio.setSelected(true);
    childRadio.setText("Subtype");
    jPanel4.setLayout(borderLayout2);
    jLabel1.setText("  of   ");
    jPanel5.setLayout(gridLayout3);
    gridLayout3.setColumns(1);
    gridLayout3.setRows(3);
    standarEntityRadio.setSelected(true);
    standarEntityRadio.setText("");
    jPanel6.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    currentEntity.setText("None");
    selectEntityRadio.setText("Select:");
    jPanel8.setLayout(flowLayout4);
    flowLayout4.setAlignment(FlowLayout.LEFT);
    jPanel9.setLayout(flowLayout5);
    flowLayout5.setAlignment(FlowLayout.LEFT);
    includeSuperCheck.setText("Search superclasses");
    jPanel1.setOpaque(false);
    label.setOpaque(true);
    label.setText("Filter");
    regexCheck.setText("Regular expression");
    regexCheck.addActionListener(new FindFilter_regexCheck_actionAdapter(this));
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jPanel3.setPreferredSize(new Dimension(150, 33));
    jPanel3.setLayout(flowLayout6);
    flowLayout6.setAlignment(FlowLayout.LEFT);
    jPanel6.setBorder(border1);
    jPanel8.setBorder(border1);
    jPanel9.setBorder(border1);
    entityChooserComboBox1.setMaximumRowCount(20);
    jLabel2.setText("recursions");
    recursionSpin.setModel(recursionNumberModel);
    recursionSpin.setEnabled(true);
    recursionSpin.setPreferredSize(new Dimension(40, 18));
    searchText.setFont(new java.awt.Font("DialogInput", 0, 11));
    searchText.setVerifyInputWhenFocusTarget(true);
    searchText.setText("");
    currentEntityRadio.setText("");
    jPanel5.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.add(descriptionCheck, null);
    this.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.add(textPane, "Text");
    textPane.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(synonymCheck, null);
    jPanel2.add(regexCheck, null);
    jPanel2.add(ignoreCaseCheck, null);
    jPanel2.add(matchingRulesCheck, null);
    jPanel2.add(exactCheck, null);
    textPane.add(jScrollPane1, BorderLayout.NORTH);
    jScrollPane1.getViewport().add(searchText, null);
    tabbedPane.add(isaPane, "Is a");
    isaPane.add(jPanel3,  BorderLayout.WEST);
    jPanel3.add(childRadio, null);
    jPanel3.add(parentRadio, null);
    jPanel3.add(causeRadio, null);
    jPanel3.add(effectRadio, null);
    isaPane.add(jPanel4, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(label, null);
    jPanel1.add(orCheckbox, null);
    jPanel1.add(includeRadio, null);
    jPanel1.add(excludeRadio, null);
    bg1.add(includeRadio);
    bg1.add(excludeRadio);
    bg2.add(causeRadio);
    bg2.add(effectRadio);
    bg2.add(childRadio);
    bg2.add(parentRadio);
    bg3.add(standarEntityRadio);
    bg3.add(currentEntityRadio);
    bg3.add(selectEntityRadio);
    jPanel4.add(jPanel5,  BorderLayout.CENTER);
    jPanel5.add(jPanel6, null);
    jPanel6.add(standarEntityRadio, null);
    jPanel4.add(jLabel1,  BorderLayout.WEST);
    jPanel6.add(standardsCombo, null);
    jPanel6.add(currentEntityRadio, null);
    jPanel6.add(currentEntity, null);
    jPanel5.add(jPanel8, null);
    jPanel8.add(selectEntityRadio, null);
    jPanel8.add(entityChooserComboBox1, null);
    jPanel5.add(jPanel9, null);
    jPanel9.add(includeSuperCheck, null);
    jPanel9.add(recursionSpin, null);
    jPanel9.add(jLabel2, null);
    standardsCombo.setModel(standardsModel);
  }


  void regexCheck_actionPerformed(ActionEvent e) {
    boolean b=!regexCheck.isSelected();
    matchingRulesCheck.setEnabled(b);
    exactCheck.setEnabled(b);
    ignoreCaseCheck.setEnabled(b);
  }


}

class FindFilter_regexCheck_actionAdapter implements java.awt.event.ActionListener {
  FindFilter adaptee;

  FindFilter_regexCheck_actionAdapter(FindFilter adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.regexCheck_actionPerformed(e);
  }
}

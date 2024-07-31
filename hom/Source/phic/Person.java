package phic;
import java.util.*;

import java.awt.*;
import phic.common.IniReader;
import phic.modifiable.Script;
import java.util.List;

/**
 * The data for a single patient: serialised to give the patient file.
 * The data includes the patients name and description, the data itself
 * (divided into the Body and Environment), and the organList (which although
 * may be derived from Body, is convenient to represent here, as it will need
 * to replace the static field in Organ (because of the use of a static Organ
 * thread).
 *
 */
public class Person implements java.io.Serializable{
  /** Create the default person: Jason, healthy 20-year-old male. */
  public Person() {}

  /** Patient's name */
  public String name = "Jason";

  /** The actual body and all its data */
  public Body body;

  /** The current environment */
  public Environment environment;

  /** Description of patient, setup and state */
  public String description = "Healthy 20-year-old male";

  /** The organ list, the static member of Organ.class, that
   * is used to schedule the body */
  public Vector organList;

  public final static boolean MALE = true, FEMALE = false;
  /**
   * The sex, which is either Person.MALE or Person.FEMALE.
   */
  public boolean sex = MALE;

  /** Height in metres */
  public double height = 1.70;

  /** Age in years */
  public double age = 20;

  /** Initial Body mass index: */
  public double BMI = 25.0;

  /** Base colour for skin */
  public Color skinBasePigment = new Color(255, 255, 235);

  /**
   * Estimates values for:
   *
   * 1) Total body surface area
   * 2) Initial BMR
   * 3) Fat mass
   * @todo include renal with cockcroft-gault estimation
   * @todo estimate cardiac function - ?using CI
   *
   * based on the age, sex, height and BMI of a patient.
   */
  public void calculateEstimates() {
    body.skin.area.set(estimateSurfaceArea());
    body.BMR.initialValue = estimateBasalEnergyExpenditure() * 1000;
    adjustFatToBMI();
    body.lungs.FRC.set(estimateFRC());
    body.lungs.airwayResistance.set( estimateAirwayResistance() );
    body.lungs.TidV.initialValue = estimateTidalVolume();
    setupBloodVolume();
    body.kidney.overallRenalFunction.set( estimateRenalFunctionFraction() );
  }

  /**
   * Guess tidal volume from height
   */
  double estimateTidalVolume() {
    return 0.510 * (height/1.7) * (height/1.7);
  }

  /**
   * Adjusts the body.Fat to give the correct BMI
   */
  void adjustFatToBMI() {
    double expectedMass = 1000 * BMI * height * height;
    body.Fat.mass.set(Math.max(0,
                               body.Fat.mass.get() + expectedMass -
                               body.Mass.get()));
  }

  /**
   * Estimates the body's basal energy expenditure (BEE) using the
   * Harris-Benedict formula.
   * In kilocalories per day.
   * Found to be proportional to body.Mass, height and negatively to age.
   */
  double estimateBasalEnergyExpenditure() {
    double kcalperday = sex == Person.MALE ?
        66.47 + 13.75 * body.Mass.get() / 1000 + 500 * height - 6.76 * age :
        655.1 + 9.56 * body.Mass.get() / 1000 + 185 * height - 4.68 * age;
    return kcalperday;
  }

  /**
   * Estimates total body surface area using the Anderson et al. (1995)
   * formula.
   * Found to be independent of age, but log-log proportional to height
   * and body.Mass
   */
  double estimateSurfaceArea() {
    return Math.exp( -3.73 + 0.417 * Math.log(height * 100) +
                    0.517 * Math.log(body.Mass.get() / 1000));
  }

  /**
   * Guess the functional residual capacity of the lung. Depends on age
   * and sex
   */
  double estimateFRC() {
    //this goes from 0 to 1 as age goes from 25 to 75
    double reduction = Math.min(1, Math.max(0, (age - 25) / 50));
    if (age < 16) reduction = 1 - age / 16;
    return (sex == MALE ? 3.0 : 2.5)
        * (1 - reduction * 0.75);
  }
  /**
   * Airway resistance decreases with decreasing height, and increases with obesity.
   * It is measured in mHg / L/min
   */
  double estimateAirwayResistance(){
    return 0.003 * (height/1.7) * (height/1.7)
        * Math.max(1,(BMI/25));
  }

  /**
   * Guess the blood volume depending on body mass and sex
   */
  double estimateBloodVolume() {
    return 5 * (height*height*BMI/72.25)
        * (sex==FEMALE?0.94:1);
  }

  /**
   * Uses the MDRD study formula for creatinine clearance
   * with a creatinine of 60 giving a GFR of 120 ml/min
   */
  double estimateRenalFunctionFraction(){
    return Math.pow(age, -0.203) * (sex==MALE?1 : 0.742) * 32788 * Math.pow(60,-1.1) / 120;
  }

  public static final String PERSON_FILE= "patient/Patients.txt";

  /**
   * Alters this persons demographic details to match the text file.
   */
  public void setupDetailsFromResource(String personName){
    IniReader ir=new IniReader(PERSON_FILE);
    Map m=ir.getSectionMap(personName);
    this.name=personName;
    String desc=(String)m.get("Description");
    if(desc!=null)this.description=desc;
    try{
      this.age = ((Double) m.get("Age")).doubleValue();
    }catch(Exception e){}
    String sex= (String)m.get("Sex");
    if(sex!=null)this.sex=(sex.startsWith("M")||sex.startsWith("m"))?MALE:FEMALE;
    try{
      this.BMI = ((Double) m.get("BMI")).doubleValue();
    }catch(Exception e){}
    try{
      this.height = ( (Double) m.get("Height")).doubleValue();
    }catch(Exception e){}
  }

  /**
   *
   */
  void setupBloodVolume(){
    body.blood.Hct.initialValue = sex==MALE? 0.45 : 0.415;
    body.blood.volume.initialValue = estimateBloodVolume();
    body.blood.RCM.initialValue = body.blood.Hct.initialValue * body.blood.volume.initialValue;
  }

  /**
   * Alters this person to conform with the demographics, description and bodily
   * parameters specified in the text file. The text in the given section is
   * executed as a script.
   */
  public void setupParametersFromResource(String personName){
    calculateEstimates();
    IniReader ir=new IniReader(PERSON_FILE);
    String[] s=ir.getSectionStrings(personName);
    Vector l=new Vector(Arrays.asList(s));
    for(int i=0;i<l.size();i++){ // remove troublesome items from scripting
      String t=l.get(i).toString().trim();
      if(t.startsWith("Description")) {l.remove(i);i--;}
      if(t.startsWith("Sex")) {l.remove(i);i--;}
    }
    s=(String[])l.toArray(s);
    try{
      Script script=new Script(s);
      script.executeOnce();
      body.startupScript=script;
    }catch(Exception e){
      System.out.println("Error in resource "+personName);
      e.printStackTrace();
    }
  }
  /**
   * Returns skeletal information about a person - i.e.
   * the name, description, age, sex and BMI, in a new Person object
   * which does not contain a reference to Body or Environment
   */
  public static Person newPersonDetailsFromResource(String personName){
    Person p=new Person();
    p.setupDetailsFromResource(personName);
    return p;
  }
  /**
   * Returns comprehensive information about a person - i.e.
   * the name, description , age, sex, BMI and in addition, executes the script
   * in the text file on new Body and Environment objects.
   * @deprecated this is currently dangerous as creating Body objects (which
   * contain new Organ objects) can interfere with the running of the Organ
   * thread.
   */
  public static Person newPersonFromResource(String personName){
    Person p=new Person();
    p.body=new Body();
    p.environment=new Environment();
    p.setupDetailsFromResource(personName);
    p.setupParametersFromResource(personName);
    return p;
  }


  public static String[] getResourceNames(){
    IniReader ir=new IniReader(PERSON_FILE);
    return ir.getSectionHeaders();
  }
}

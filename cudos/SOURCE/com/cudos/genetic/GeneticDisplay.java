package com.cudos.genetic;

import java.awt.*;
import javax.swing.*;
import java.util.Vector;
import java.awt.event.*;
import java.net.URL;
import com.cudos.common.CudosIndexReader;
import java.util.Arrays;
import java.text.NumberFormat;
import javax.swing.border.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class GeneticDisplay extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel toppanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel motherpanel = new JPanel();
  JPanel fatherpanel = new JPanel();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel childrenprobs = new JPanel();
  GridLayout childrenprobsgrid = new GridLayout();
  JPanel middlepanel = new JPanel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  BorderLayout borderLayout5 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  BorderLayout borderLayout6 = new BorderLayout();
  JPanel motherindividualpanel = new JPanel();
  JPanel fatherindividualpanel = new JPanel();
  JButton mothergametes = new JButton();
  JButton fathergametes = new JButton();
  BorderLayout borderLayout7 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JButton jButton4 = new JButton();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout8 = new BorderLayout();
  JPanel jPanel8 = new JPanel();
  JRadioButton byphenotype = new JRadioButton();
  JRadioButton bygenotype = new JRadioButton();
  ButtonGroup buttonGroup1 = new ButtonGroup();
  JPanel betweenparents = new JPanel();
  JRadioButton bycombination = new JRadioButton();
  BorderLayout borderLayout9 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  Border border1;
  BorderLayout borderLayout10 = new BorderLayout();
  JPanel jPanel9 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JRadioButton ratioradio = new JRadioButton();
  JRadioButton percentradio = new JRadioButton();
  ButtonGroup buttonGroup2 = new ButtonGroup();

  JPanel childstempanel = new JPanel(){
    public void paint(Graphics g){
      super.paint(g);
      g.setColor(getForeground());
      g.drawLine(getWidth()/2,0,getWidth()/2,getHeight());
    }
  };


  /** Diagram of marriage between mother and father */
  JPanel marriagepanel = new JPanel(){
    public void paint(Graphics g){
      super.paint(g);
      int h=getHeight(), w=getWidth();
      g.drawLine(0,h/2,w,h/2);
      g.drawLine(w/2,h/2,w/2,h);
    }
  };

  /** diagram of descendence of children from marriage */
  JPanel childrenlines = new JPanel(){
    public void paint(Graphics g){
      int n=children.size();
      if(n==0)return;
      if(!childrenAreClean) return;
      int  w=getWidth(), h=getHeight(),wc=getWidth()/n;
      //g.drawLine(w/2,0,w/2,h/2); //stem
      //g.drawLine(wc/2,h/2,wc*(n*2-1)/2,h/2); //horizontal beam
      g.drawLine(wc/2,0,wc*(n*2-1)/2,0); //horizontal beam
      for(int i=0;i<n;i++){
        int x=(2*i+1)*wc/2;
        g.drawLine(x,0,x,h);
      } //leaves
    }
  };
  /**
   * This is set to true when children are created, but when the parents
   * or children are changed, this is set to false.
   */
  boolean childrenAreClean = false;
  /** probabilities of each child */
  double[] probabilities= null;

  JPanel childrenpanel = new JPanel();
  GridLayout childrengridlayout = new GridLayout();
  public GeneticDisplay() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /** Vector of Individuals */
  Vector children = new Vector();
  Individual mother = new Individual(), father = new Individual();

  Class genomeClass, phenoClass;
  public void setupType(String[] params){
    dirtyChildren();
    for(int i=0;i<params.length;i++){
      int e=params[i].indexOf('=');
      String lt=params[i].substring(0, e).trim();
      String rt=params[i].substring(e+1).trim();
      try{
        if (lt.equalsIgnoreCase("Genome")) {
          genomeClass = Class.forName("com.cudos.genetic." + rt);
        }
        if (lt.equalsIgnoreCase("Phenotype")) {
          phenoClass = Class.forName("com.cudos.genetic." + rt);
        }
      }catch(ClassNotFoundException x){ x.printStackTrace(); }
    }
    mother.name="Mother "+Individual.serial++;
    mother.create(genomeClass, phenoClass);
    mother.genome.addActionListener(genechangelisten);
    father.name="Father "+Individual.serial++;
    father.create(genomeClass,phenoClass);
    father.genome.addActionListener(genechangelisten);
    updateImages();
  }
  CudosIndexReader ir;
  public void setupType(URL resource, String section){
    ir=new CudosIndexReader(resource);
    removeAllChildren();
    try{
      Individual par[]= { GenomeFromFile.getIndividual(ir, section),
                          GenomeFromFile.getIndividual(ir, section) };
      for(int i=0;i<2;i++){
        par[i].changeSex(i);
        setParent(i, par[i]);
      }
    }catch(WrongSex x){x.printStackTrace();}
  }
  public void setParent(int p,Individual i) throws WrongSex{
    //ensure is not a child
    for(int j=0;j<children.size();j++) if(children.get(j) == i) children.remove(i);
    //ensure the right sex
    if(i.genome.isSexLinked()){
      if(i.genome.hasYChromosome() ^ p!=0) throw new WrongSex();
    }
    if(p==0) mother = i; else father=i;
    i.name = i.name + ((p==0)? " (Mother)" : " (Father)");
    i.genome.addActionListener(genechangelisten);
    i.genome.setCanChangeSex(false); // fix the sex
    i.genopanel.setVisible(true);
    dirtyChildren();
    updateImages();
  }
  public void updateImages(){
    motherindividualpanel.removeAll();
    motherindividualpanel.add(mother.getComponent(), BorderLayout.CENTER);
    fatherindividualpanel.removeAll();
    fatherindividualpanel.add(father.getComponent(), BorderLayout.CENTER);
    childrenpanel.removeAll();
    childrenprobs.removeAll();
    childrengridlayout.setColumns(children.size());
    childrenprobsgrid.setColumns(children.size());
    for(int i=0;i<children.size();i++){
      childrenpanel.add(((Individual)children.get(i)).getComponent());
      int nm=(int)(totalPossibleChildren * probabilities[i]);
      String s;
      if(showPercent) s=NumberFormat.getNumberInstance().format(
          (float) 0.1*(Math.round(1000*probabilities[i])) ) + "%";
      else s=Integer.toString(nm);
      JLabel childLabel = new JLabel(s);
      childLabel.setFont(new Font("Sans serif", Font.BOLD,16));
      childLabel.setHorizontalAlignment(childLabel.CENTER);
      childrenprobs.add(childLabel);
    }
    this.validateTree();
    repaint();
  }
  boolean showPercent=true;

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder();
    this.setLayout(borderLayout1);
    toppanel.setLayout(borderLayout2);
    middlepanel.setLayout(borderLayout3);
    jButton1.setText("Breed");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    childrenpanel.setLayout(childrengridlayout);
    jPanel2.setLayout(borderLayout4);
    childrenprobs.setLayout(childrenprobsgrid);
    childrenlines.setPreferredSize(new Dimension(50, 25));
    motherpanel.setLayout(borderLayout5);
    jLabel1.setText("Mother");
    jLabel2.setText("Father");
    fatherpanel.setLayout(borderLayout6);
    mothergametes.setText("Gametes");
    mothergametes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showgametes(e);
      }
    });
    fathergametes.setText("Gametes");
    fathergametes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showgametes(e);
      }
    });
    betweenparents.setLayout(borderLayout7);
    jButton4.setText("Punnett cross");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showcross(e);
      }
    });
    jPanel7.setLayout(borderLayout8);
    byphenotype.setText("By phenotype");
    byphenotype.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changechildview(e);
      }
    });
    bygenotype.setSelected(true);
    bygenotype.setText("By genotype");
    bygenotype.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changechildview(e);
      }
    });
    bycombination.setText("By combination");
    bycombination.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changechildview(e);
      }
    });
    childrengridlayout.setHgap(8);
    jPanel6.setLayout(borderLayout9);
    jScrollPane1.setBorder(border1);
    jPanel5.setLayout(borderLayout10);
    jPanel9.setLayout(flowLayout1);
    flowLayout1.setHgap(5);
    flowLayout1.setVgap(0);
    childstempanel.setPreferredSize(new Dimension(25, 25));
    jButton2.setText("Full cross");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showcross(e);
      }
    });
    ratioradio.setText("Ratios");
    ratioradio.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changechildview(e);
      }
    });
    percentradio.setSelected(true);
    percentradio.setText("Percentages");
    percentradio.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changechildview(e);
      }
    });
    this.add(toppanel, BorderLayout.NORTH);
    toppanel.add(motherpanel, BorderLayout.WEST);
    motherpanel.add(jLabel1,  BorderLayout.NORTH);
    motherpanel.add(motherindividualpanel, BorderLayout.CENTER);
    motherpanel.add(mothergametes,  BorderLayout.SOUTH);
    toppanel.add(fatherpanel, BorderLayout.EAST);
    fatherpanel.add(jLabel2,  BorderLayout.NORTH);
    fatherpanel.add(fatherindividualpanel, BorderLayout.CENTER);
    fatherpanel.add(fathergametes,  BorderLayout.SOUTH);
    toppanel.add(betweenparents,  BorderLayout.CENTER);
    betweenparents.add(jPanel6, BorderLayout.SOUTH);
    jPanel6.add(jPanel3,  BorderLayout.NORTH);
    jPanel3.add(jButton4, null);
    jPanel3.add(jButton2, null);
    jPanel6.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jButton1, null);
    betweenparents.add(marriagepanel,  BorderLayout.CENTER);
    this.add(jPanel5,  BorderLayout.CENTER);
    jPanel5.add(jScrollPane1, BorderLayout.CENTER);
    jPanel5.add(childstempanel, BorderLayout.NORTH);
    jScrollPane1.getViewport().add(jPanel9, null);
    jPanel9.add(middlepanel, null);
    middlepanel.add(childrenpanel, BorderLayout.SOUTH);
    middlepanel.add(jPanel2,  BorderLayout.CENTER);
    jPanel2.add(childrenlines,  BorderLayout.CENTER);
    jPanel2.add(childrenprobs,  BorderLayout.SOUTH);
    this.add(jPanel7,  BorderLayout.SOUTH);
    jPanel7.add(jPanel8,  BorderLayout.WEST);
    jPanel8.add(bygenotype, null);
    jPanel8.add(byphenotype, null);
    jPanel8.add(bycombination, null);
    jPanel7.add(jPanel4,  BorderLayout.EAST);
    jPanel4.add(percentradio, null);
    jPanel4.add(ratioradio, null);
    buttonGroup1.add(bygenotype);
    buttonGroup1.add(byphenotype);
    buttonGroup1.add(bycombination);
    bycombination.setVisible(false);
    buttonGroup2.add(percentradio);
    buttonGroup2.add(ratioradio);
  }
  void jButton1_actionPerformed(ActionEvent e) {
    doBreed();
  }

  public void removeAllChildren(){
    children.removeAllElements();
    dirtyChildren();
  }

  /** Breed action */
  public void doBreed(){
    children.removeAllElements();
    try {
      children.addAll(createBreed());
      childrenAreClean = true;
      for (int i = 0; i < children.size(); i++) {
        ( (Individual) children.get(i)).genome.addActionListener(
            genechangelisten);
      }
      updateImages();
    } catch(IllegalStateException x){
      error("You have selected incompatible parents! "+
            "Please reselect which example you want to try.");
    }
  }
  ActionListener genechangelisten=new ActionListener(){
    public void actionPerformed(ActionEvent e){
      dirtyChildren();
    }
  };
  void dirtyChildren(){
    childrenAreClean=false;
    childrenprobs.removeAll();
    validateTree();
    repaint();
  }

  boolean collate=true;
  boolean collatePhenos = false;
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jButton2 = new JButton();


  int totalPossibleChildren = 1;
  /** Create a set of individuals that are offspring of parents */
  public Vector createBreed(){

    Genome.ChildSet cs = mother.genome.getPossibleHaplotypes() .combine( father.genome.getPossibleHaplotypes());
    Vector childGenos = new Vector(Arrays.asList(cs.genomes));
    double[] probs = cs.probs;
    totalPossibleChildren=probs.length;
    /**
     * collate genomes
     * i.e. delete all 'identical' genomes, and sum their probabilities into a
     * single genome.
     */
    boolean deleted[] = new boolean[probs.length];
    Vector gdeleted = new Vector();
    int ndeleted = 0;
    if(collate){
      for(int i=0;i<probs.length;i++){
        if(deleted[i]) continue;
        Phenotype p;
        if(collatePhenos && ((Genome)childGenos.get(i)) .isLethal()){
          probs[i]=0;
          deleted[i]=true;
          gdeleted.add(childGenos.get(i));
          ndeleted++;
          continue;
        }
        for(int j=i+1;j<probs.length;j++){
          if(deleted[j]) continue;
          if(
               ((Genome)childGenos.get(i)) .isGeneticallyIdenticalTo( (Genome)childGenos.get(j))
            || ( collatePhenos &&
              ((Genome)childGenos.get(i)) .isPhenotypicallyIdenticalTo( (Genome)childGenos.get(j))
             ) ){
            probs[i]+=probs[j];
            probs[j]=0;
            deleted[j]=true;
            gdeleted.add(childGenos.get(j));
            ndeleted ++;
          }
        }
      }
    }
    double tprob=0; // normalise probabilities
    probabilities=new double[probs.length - ndeleted];
    for(int i=0;i<probs.length;i++) tprob+=probs[i];
    //exclude deleted probabilities
    for(int i=0, j=0;i<probs.length;i++) if(!deleted[i]) probabilities[j++]=probs[i]/tprob;
    //exclude deleted genomes
    childGenos.removeAll(gdeleted);
    /** create individuals */
    Vector children = new Vector();
    for(int i=0;i<childGenos.size();i++){
      Individual c = new Individual();
      if(mother.genome instanceof GenomeFromFile) {
        c = ((GenomeFromFile)mother.genome).createNewBlankIndividual();
        c.genome.setFrom((Genome)childGenos.get(i));
      }else{ try{
          Phenotype p = (Phenotype) phenoClass.newInstance();
          c.create( (Genome) childGenos.get(i), p);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      c.name="Child "+Individual.serial++;
      if(collatePhenos) c.genopanel.setVisible(false);
      children.add(c);
    }
    return children;
  }
  /** return n'th bit of i, either 0 or 1 */
  public int bit(int n,int i){ return (i>>n)&1; }

  void changechildview(ActionEvent e) {
    if(bygenotype.isSelected()){
      collate=true; collatePhenos=false;
    }else if(byphenotype.isSelected()){
      collate=true; collatePhenos=true;
    }else if(bycombination.isSelected()){
      collate=false; collatePhenos=false;
    }
    if(percentradio.isSelected()) showPercent=true;
    else if(ratioradio.isSelected()) showPercent=false;
    doBreed();
  }

  void showgametes(ActionEvent e) {
    Individual from=null;
    JComponent src = (JComponent) e.getSource();
    if(src.equals(mothergametes)){
      from=mother;
    }else if(src.equals(fathergametes)){
      from=father;
    }
    final JPopupMenu p = new JPopupMenu();
    GametesPanel gp = new GametesPanel(from);
    p.add(gp);
    gp.addComponentListener(new ComponentAdapter(){
      public void componentResized(ComponentEvent e){
        p.pack();
      }
    });
    p.pack();
    p.show(src, 0, src.getHeight());
  }

  void showcross(ActionEvent e) {
    JComponent src = (JComponent) e.getSource();
    boolean showPhenotypes = src.equals(jButton4);
    final JPopupMenu p = new JPopupMenu();
    try{
      PunnettPanel pp = new PunnettPanel(mother, father, showPhenotypes);
      p.add(pp);
      pp.addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          p.pack();
        }
      });
      p.pack();
      p.show(src, 0, src.getHeight());
    }catch(IllegalStateException x){ error("You have selected incompatible parents! "+
            "Please reselect which example you want to try."); }
  }

  void error(String e){
    JOptionPane.showMessageDialog(this, e, "An error has occurred",
                                  JOptionPane.ERROR_MESSAGE);
  }



}

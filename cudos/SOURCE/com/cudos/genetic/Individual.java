package com.cudos.genetic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Item representing individual organism
 */

public class Individual extends JPanel{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel top = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton namelabel = new JButton();
  JPanel main = new JPanel();
  JPanel genopanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel phenopanel = new JPanel();
  JPanel bottom = new JPanel();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  GridLayout genegrid = new GridLayout();
  JPanel linkagedisp = new JPanel(){
    public void paint(Graphics g){
      super.paint(g);
      g.setColor(getForeground());
      ((Graphics2D)g).setStroke(new BasicStroke(3));
      int N=genome.getNLoci();
      for(int i=1;i<N;i++){
        double p=genome.linkage(i);
        if(p>0){
          int h=getHeight()/N,
              y1=(int)(h*(i-0.4)),
              y2=(int)(h*(i+0.4)),
              x2=getWidth(),
              x1=x2-10;
          g.drawLine(x1,y1,x2,y1);
          g.drawLine(x1,y1,x1,y2);
          g.drawLine(x1,y2,x2,y2); //draw bracket for linkage of genes
          g.drawString(Integer.toString((int)(p*100))+"%",
                       0,h*i);
        }
      }
    }
  };
  /** Change sex. i=0 for female, 1 for male */
  public void changeSex(int s){genome.changeSex(s);}

  public Individual() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  JComponent getComponent(){
    return this;
  }

  /** Creates a new individual given the class of gene and phenotype */
  public void create(Class genomeClass, Class phenoclass){
    try{
      Phenotype p = (Phenotype) phenoclass.newInstance();
      Genome g = (Genome) genomeClass.newInstance();
      create(g,p);
    }catch(Exception e){e.printStackTrace();}
  }
  public void create(Genome g, Phenotype p){
    genome = g;
    phenotype = p;
    genome.setupGeneListeners(); //genome listens to each gene
    phenotype.setGenome(genome); //phenotype listens to genome
    boolean li=false;
    for(int i=1;i<genome.getNLoci();i++) if(genome.linkage(i)>0) li=true;
    if(li) linkagedisp.setPreferredSize(new Dimension(35,0));
    setImages();
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    top.setLayout(borderLayout2);
    namelabel.setText("Individual 0");
    namelabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        namelabel_actionPerformed(e);
      }
    });
    main.setLayout(borderLayout3);
    genopanel.setLayout(genegrid);
    this.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel1.setLayout(borderLayout4);
    linkagedisp.setForeground(new Color(0, 0, 128));
    linkagedisp.setPreferredSize(new Dimension(1, 1));
    linkagedisp.setToolTipText("The genes exhibit linkage as shown, and do not segregate independently.");
    bottom.setLayout(borderLayout5);
    this.add(top, BorderLayout.NORTH);
    top.add(namelabel,  BorderLayout.CENTER);
    this.add(main, BorderLayout.CENTER);
    main.add(phenopanel, BorderLayout.CENTER);
    main.add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(genopanel, BorderLayout.CENTER);
    jPanel1.add(linkagedisp, BorderLayout.WEST);
    this.add(bottom,  BorderLayout.SOUTH);
  }

  public static int serial=0;
  public String name = "Individual"+serial++;
  /** Each locus contains two Gene objects */
  public Genome genome;
  public Phenotype phenotype;

  public void setImages(){
    namelabel.setText(name);
    genopanel.removeAll();
    genegrid.setColumns(2);
    int N = genome.getNLoci();
    genegrid.setRows(N);
    for(int i=0;i<N;i++){
      Gene[] genes = genome.getGenesAtLocus(i);
      genopanel.add(genes[0].getComponent());
      genopanel.add(genes[1].getComponent());
    }
    phenopanel.removeAll();
    phenopanel.add(phenotype.getComponent());
  }

  JPopupMenu changeMenu = new JPopupMenu("Change:");
  void namelabel_actionPerformed(ActionEvent e) {
    changeMenu.removeAll();
    changeMenu.add(testhomoAction);
    changeMenu.add(heteroAction);
    changeMenu.add(randomiseAction);
    GeneticDisplay p=findDisplayParent(this);
    if(p==null || (this!=p.mother && this!=p.father)){
      changeMenu.add(useAsMother);
      changeMenu.add(useAsFather);
    }
    Component s=(Component)e.getSource();
    changeMenu.show(s,0,s.getHeight());
  }
  Action randomiseAction = new AbstractAction("Set to random genotype"){
    public void actionPerformed(ActionEvent e){
      for(int i=0;i<genome.getNLoci();i++){
        Gene[] genes = genome.getGenesAtLocus(i);
        String[] a=genome.getPossibleAlleles(i);
        genes[0].setAllele(a[(int)(Math.random()*a.length)]);
        genes[1].setAllele(a[(int)(Math.random()*a.length)]);
      }
    }
  };
  Action testhomoAction = new AbstractAction("Set to test homozygote"){
    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < genome.getNLoci(); i++) {
        Gene[] genes = genome.getGenesAtLocus(i);
        String[] a = genes[0].getPossibleAlleles();
        genes[0].setAllele(a[0]);
        genes[1].setAllele(a[0]);
      }
    }
  };
  Action heteroAction = new AbstractAction("Set to heterozygote"){
    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < genome.getNLoci(); i++) {
        Gene[] genes = genome.getGenesAtLocus(i);
        String[] a = genes[0].getPossibleAlleles();
        genes[0].setAllele(a[Math.min(a.length-1,1)]);
        genes[1].setAllele(a[0]);
      }
    }
  };
  Action useAsMother = new AbstractAction("Use as mother"){
    public void actionPerformed(ActionEvent e){
      GeneticDisplay p=findDisplayParent( Individual.this );
      try{
        changeSex(0);
        if (p != null) p.setParent(0, Individual.this);
      }catch(WrongSex x){x.printStackTrace();}
    }
  };
  Action useAsFather = new AbstractAction("Use as father"){
    public void actionPerformed(ActionEvent e){
      GeneticDisplay p=findDisplayParent( Individual.this );
      try{
        changeSex(1);
        if (p != null) p.setParent(1, Individual.this);
      }catch(WrongSex x){x.printStackTrace();}
    }
  };
  BorderLayout borderLayout5 = new BorderLayout();
  static GeneticDisplay findDisplayParent(Component c){
    while(c!=null && !(c instanceof GeneticDisplay)) c=c.getParent();
    return (GeneticDisplay)c;
  }
}

package com.cudos.genetic;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
/**
 * Genome is series of genes
 */

public abstract class Genome  {
  protected Vector loci = new Vector();
  protected Genome() {
  }
  public int getNLoci(){ return loci.size(); }

  /**
   * @return Object[] a list of all possible alleles. Each element should be a
   * String[] containing the list of possible alleles at the given locus
   */
  public abstract Object[] getAllAlleles();

  /**
   * A list of which alleles are on the Y chromosome.
   * If this is null, then the genome is not sex-linked.
   */
  public abstract String[] getYChromosomeAlleles();

  public boolean isSexLinked(){ return getYChromosomeAlleles()!=null; }
  /**
   * Return true if the given allele is on a Y chromosome.
   * Throw an exception if the genome is not sex-linked.
   */
  public boolean isAlleleOnYChromosome(String allele) {
    if (isSexLinked()) {
      String[] yca=getYChromosomeAlleles();
      for (int i = 0; i < yca.length; i++) {
        if (yca[i].equals(allele))return true;
      }
      return false;
    }
    throw new IllegalStateException(
        "Can't check for Y-chromosome; this isn't a" +
        " sex linked trait.");
  }
  /**
   *  Return true if the current set of alleles contains a Y chromosome.
   * If the genome is not sex-linked, an exception is thrown.
   */

  public boolean hasYChromosome() {
    for (int i = 0; i < loci.size(); i++) {
      Gene[] g = getGenesAtLocus(i);
      if (isAlleleOnYChromosome(g[0].getAllele()) ||
          isAlleleOnYChromosome(g[1].getAllele()))return true;
    }
    return false;
  }
  /** Make genome sex mutable / immutable */
  public void setCanChangeSex(boolean b){
    for(int i=0;i<getNLoci();i++)for(int j=0;j<2;j++)getGenesAtLocus(i)[j].setCanChangeSex(b);
  }

  /** Change the sex to 1: female, 2: male */
  public void changeSex(int s){
    if(!isSexLinked()) return;
    boolean y=hasYChromosome();
    if(y && s==0){ // change male to female
      for(int i=0;i>getNLoci();i++){
        if(!isLocusSexLinked(i)) continue;
        Gene[] g=getGenesAtLocus(i);
        for(int q=0;q<2;q++)
          if(isAlleleOnYChromosome(g[q].getAllele())) g[q].setAllele(getFirstNonYAlleleForLocus(i));
      }
    }else if((!y) && s==1){ // change female to male
      for(int i=0;i<getNLoci();i++){
        if (!isLocusSexLinked(i))continue;
        Gene[] g = getGenesAtLocus(i);
        g[1].setAllele(getFirstYAlleleForLocus(i));
      }
    }
  }

  /** Returns true if any possible allele at the given locus is on the Y chromosome */
  public boolean isLocusSexLinked(int loc){
    String[] poss= (String[])getAllAlleles()[loc];
    for(int i=0;i<poss.length;i++) if(isAlleleOnYChromosome( poss[i] )) return true;
    return false;
  }
  public String getFirstYAlleleForLocus(int loc){
    String[] poss= (String[])getAllAlleles()[loc];
    for(int i=0;i<poss.length;i++) if(isAlleleOnYChromosome( poss[i] )) return poss[i];
    throw new IllegalArgumentException("Cannot find any Y-chromosome alleles at locus "+loc);
  }
  public String getFirstNonYAlleleForLocus(int loc){
    String[] poss= (String[])getAllAlleles()[loc];
    for(int i=0;i<poss.length;i++) if(!isAlleleOnYChromosome( poss[i] )) return poss[i];
    throw new IllegalArgumentException("Cannot find any Non-Y-chromosome alleles at locus "+loc);
  }


  public int getLocusForAllele(String a){
    Object[] aa=getAllAlleles();
    for(int i=0;i<aa.length;i++){
      String[] pa = (String[]) aa[i];
      for (int j = 0; j < pa.length; j++)
        if (pa[j].equals(a))return i;
    }
    throw new IllegalArgumentException("No such allele "+a+" in this genome");
  }
  public Gene[] getGenesAtLocus(int locus){
    return (Gene[])loci.get(locus);
  }

  private void setGenesAtLocus(int locus, Gene[] genes){
    String[] poss=(String[])getAllAlleles()[locus];
    int nfound=0;
    for(int i=0;i<poss.length;i++)
      for(int j=0;j<2;j++)
        if (genes[j].getAllele().equals(poss[i])) nfound++;
    if(nfound<2) throw new IllegalArgumentException("Genes "+genes+" are of wrong type for locus "+locus);
    loci.set(locus, genes);
  }
  public String[] getPossibleAlleles(int locus){
    return (String[])getAllAlleles()[locus];
  }
  public abstract double linkage(int locus);
  public Genome duplicate(){
    try {
      Genome g;
      if(this instanceof GenomeFromFile) g=((GenomeFromFile)this).createNewBlankGenome();
      else g = (Genome) getClass().newInstance();
      for (int i = 0; i < loci.size(); i++) {
        Gene[] g2 = g.getGenesAtLocus(i);
        g2[0].setAllele(getGenesAtLocus(i)[0].getAllele());
        g2[1].setAllele(getGenesAtLocus(i)[1].getAllele()); ;
      }
      return g;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public void setFrom(Genome g){
    if(g.loci.size()!=loci.size()) throw new IllegalArgumentException("Genome not compatible");
    for(int i=0;i<loci.size();i++)
      for(int j=0;j<2;j++)
        getGenesAtLocus(i)[j].setAllele( g.getGenesAtLocus(i)[j].getAllele() );
  }
  public void setupGeneListeners(){
    for(int i=0;i<loci.size();i++){
      Gene[] g=getGenesAtLocus(i);
      g[0].addActionListener(genechanged);
      g[1].addActionListener(genechanged);
    }
  }
  ActionListener mylistener = null;
  public void addActionListener(ActionListener al){ mylistener=AWTEventMulticaster.add(al,mylistener); }
  public void removeActionListener(ActionListener al){ mylistener=AWTEventMulticaster.remove(al, mylistener);}
  ActionListener genechanged = new ActionListener(){ public void actionPerformed(ActionEvent e){
      if(mylistener!=null)mylistener.actionPerformed(e);
    }
  };

  /**
   * create a list of possible haplotypes. Each haplotype is a vector of Gene
   * objects.
   */
  public HaplotypeSet getPossibleHaplotypes(){
    HaplotypeSet hs = new HaplotypeSet(this);
    return hs;
  }

  /** Set of all possible gamete haplotypes for an individual parent */
  public static class HaplotypeSet {
    /** Each vector is a haplotype genome */
    public Vector[] haplos;
    public double[] probs;
    Genome genome;
    public void setGenome(Genome g){
      genome=g;
      int n = 1<<genome.loci.size();
      haplos = new Vector[n];
      probs = new double[n];
      for (int i = 0; i < n; i++) {
        haplos[i] = new Vector();
        double prob = 1;
        int laststrand = 0;
        Genome temp = genome.duplicate(); // create new set of genes
        for (int j = 0; j < genome.loci.size(); j++) {
          int strand = bit(j, i);
          haplos[i].add(temp.getGenesAtLocus(j)[strand]);
          if (j > 0 && strand != laststrand) prob *= (1 - genome.linkage(j));
          laststrand = strand;
        }
        probs[i] = prob;
      }
      collate();
    }
    public HaplotypeSet(Genome g){
      setGenome(g);
    }
    /** return n'th bit of i, either 0 or 1 */
    public int bit(int n,int i){ return (i>>n)&1; }

    /**
     * Create a set of possible children from two haplotype sets.
     * All possibilities are constructed, and are not collated.
     */
    public ChildSet combine(HaplotypeSet o){
      int N = haplos.length * o.haplos.length;
      Genome[] poss = new Genome[N];
      double[] prob = new double[N];
      int n=0;
      for(int i=0;i<haplos.length;i++)
        for(int j=0;j<o.haplos.length;j++){
          Genome g=genome.duplicate();
          for(int k=0;k<g.loci.size();k++){
            Gene[] gk = g.getGenesAtLocus(k);
            gk[0].setAllele( ((Gene)haplos[i].get(k)).getAllele() );
            gk[1].setAllele( ((Gene)o.haplos[j].get(k)).getAllele() );
          }
          poss[n]=g;
          prob[n]=probs[i]*o.probs[j];
          n++;
        }
      ChildSet cs=new ChildSet();
      cs.probs=prob;
      cs.genomes=poss;
      return cs;
    }
    /** discard haplotypes with identical alleles */
    public void collate(){
      Vector hap = new Vector();
      hap.addAll(Arrays.asList(haplos));

      boolean deleted[] = new boolean[probs.length];
      Vector gdeleted = new Vector();
      int ndeleted = 0;
      for(int i=0;i<probs.length;i++){
        if(deleted[i]) continue;
        for(int j=i+1;j<probs.length;j++){
          if(deleted[j]) continue;
          if(areHaplotypesIdentical((Vector)hap.get(i), (Vector)hap.get(j))){
            probs[i]+=probs[j];
            probs[j]=0;
            deleted[j]=true;
            gdeleted.add(hap.get(j));
            ndeleted ++;
          }
        }
      }
      double tprob=0; // normalise probabilities
      double[] probabilities=new double[probs.length - ndeleted];
      for(int i=0;i<probs.length;i++) if(!deleted[i]) tprob+=probs[i];
      //exclude deleted probabilities
      for(int i=0, j=0;i<probs.length;i++) if(!deleted[i]) probabilities[j++]=probs[i]/tprob;
      //exclude deleted genomes
      hap.removeAll(gdeleted);
      haplos = (Vector[])hap.toArray(new Vector[hap.size()]);
      probs = probabilities;
    }
    /** return true if the two haplotype genomes contain exactly the same alleles */
    public static boolean areHaplotypesIdentical(Vector h1, Vector h2){
      if(h1.size()!=h2.size())return false;
      for(int i=0;i<h1.size();i++){
        if( ! ((Gene)h1.get(i)).getAllele() .equals(  ((Gene)h2.get(i)).getAllele()) ) return false;
      }
      return true;
    }
  }

  public static class ChildSet{
    public Genome[] genomes;
    public double[] probs;
  }

  public void setMutable(boolean m){
    for(int i=0;i<loci.size();i++) for(int j=0;j<2;j++)
      getGenesAtLocus(i)[j].setMutable(m);
  }

  /** return if the two genomes are identical */
  public boolean isGeneticallyIdenticalTo(Genome g1) {
    if (g1.getNLoci() != getNLoci())return false;
    for (int i = 0; i < loci.size(); i++) { //for each locus:
      Gene[] gi1 = g1.getGenesAtLocus(i),
          gi2 = getGenesAtLocus(i);
      //identical on both strands?
      if (gi1[0].getAllele().equals(gi2[0].getAllele()) &&
          gi1[1].getAllele().equals(gi2[1].getAllele()))continue;
      //strands swapped but same alleles?
      if (gi1[0].getAllele().equals(gi2[1].getAllele()) &&
          gi1[1].getAllele().equals(gi2[0].getAllele()))continue;
      return false;
    }
    return true;
  }

  public boolean isPhenotypicallyIdenticalTo(Genome g1){
    if(isGeneticallyIdenticalTo(g1)) return true;
    return false;
  }
  public boolean isLethal(){return false;}
}

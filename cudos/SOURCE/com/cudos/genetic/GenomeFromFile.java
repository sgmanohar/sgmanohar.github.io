package com.cudos.genetic;
import com.cudos.common.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import com.cudos.genetic.GeneImpl.TextGene;

/**
 * Use a text file and images to define the genes in a genome
 */

public class GenomeFromFile extends Genome {
  public static String[] getGenomeTypes(URL resourceURL){
    return new CudosIndexReader(resourceURL).getSectionNames();
  }
  public static String getGenomeTypeDescription(URL resourceURL, String type){
    return new CudosIndexReader(resourceURL).getProperty(type, "Info");
  }
  private static GenomeFromFile getGenome(CudosIndexReader ir, String section){
    Map m=ir.getMapFromSection(section);
    return new GenomeFromFile(ir, section);
  }


  /** Create a new individual */
  public static Individual getIndividual(CudosIndexReader ir, String section){
    Individual i=new Individual();
    GenomeFromFile g=getGenome(ir, section);
    i.create(g, g.new PhenotypeFromFile());
    return i;
  }
  /** Create a new individual with the characteristics of the current genome */
  public Individual createNewBlankIndividual(){
    return getIndividual(ir, section);
  }
  /** Create a new individual with the characteristics of the current genome */
  public GenomeFromFile createNewBlankGenome(){
    return getGenome(ir, section);
  }

  protected GenomeFromFile(CudosIndexReader ir, String section) {
    this.ir=ir;
    this.section=section;
    info=ir.getProperty(section, "Info");
    geneNames = ir.getStringList(section, "Genes");
    allAlleles = new Object[geneNames.length];
    for(int i=0;i<geneNames.length;i++){
      String[] alleles = ir.getStringList(section, geneNames[i]);
      allAlleles[i] = alleles;
    }
    try{
      String[] link = ir.getStringList(section, "Linkage");
      linkage=new double[link.length];
      for(int i=0;i<link.length;i++){
        linkage[i]=Double.parseDouble(link[i]);
      }
    }catch(Exception e){ linkage = new double[geneNames.length];
      //System.out.println("Error in linkages for section "+section);
    }

    for(int i=0;i<allAlleles.length;i++){
      loci.add( new GeneFromFile[]{ new GeneFromFile(i), new GeneFromFile(i) } );
    }
    try{
      YChromosomeAlleles = ir.getStringList(section, "Y Chromosome");
    }catch(Exception e){}
  }
  CudosIndexReader ir;
  String section;

  String[] getAlleleStrings(String allele){
    try{
      return ir.getStringList(section, allele);
    }catch(Exception x){ throw new RuntimeException("Allele "+allele+" not defined in file."); }
  }

  /**
   *  Look up the property string line for the current genotype,
   * which is stored in a property whose name is the sequence of all the alleles
   * in the genome separated by spaces.
   */
  String[] getPhenotypeStringsForCurrentAlleles(){
    String prop="";
    for(int i=0;i<loci.size();i++){
      Gene[] g = getGenesAtLocus(i);
      int q = g[0].getAlleleIndex()<g[1].getAlleleIndex() ? 0:1;
      prop+=g[q].getAllele()+" "+g[(q+1)%2].getAllele()+" ";
    }
    String s=ir.getProperty(section,prop.trim());
    if(s!=null && s.length()>0) return s.split("\\s*,\\s*");
    throw new RuntimeException("The index file section "+section+" does not contain an entry "+
                               "for the genotype "+prop);
  }

  public String info;
  String geneNames[];
  Object[] allAlleles;
  double[] linkage;
  public Object[] getAllAlleles() {
    return allAlleles;
  }
  public double linkage(int locus) {
    return linkage[locus];
  }
  /** Create a blank gene, using the second possible allele (usually the wild type) */
  class GeneFromFile extends TextGene{
    JLabel pic = new JLabel();
    String[] descriptions;
    String[] imageNames;
    public int getAlleleIndex(){
      String[] aa = (String[])getAllAlleles()[locus];
      String allele = getAllele();
      for (int i = 0; i < aa.length; i++)
        if (aa[i].equals(allele))return i;
      throw new IllegalStateException("This gene has an invalid allele " +
                                      allele);
    }

    protected GeneFromFile(int locus){
      this.locus=locus;
      String[] pa = getPossibleAlleles();
      descriptions = new String[pa.length];
      imageNames = new String[pa.length];
      for(int i=0;i<pa.length;i++){
        String[] s = getAlleleStrings(pa[i]);
        if(s.length>1){
          imageNames[i]=s[0];
          descriptions[i] = s[1];
        }else descriptions[i] = s[0];
      }
      add(pic, BorderLayout.WEST);
      pic.addMouseListener(ml);

      setAllele(pa[1]);
    }
    public void setAllele(String allele){
      super.setAllele(allele);
      int i=getAlleleIndex();
      if(imageNames[i] !=null && imageNames[i].length()>0 )
        try{ pic.setIcon(new ImageIcon(CudosExhibit.getApplet(this).getImage( imageNames[i] ))); }
      catch(Exception e){ }
      pic.setToolTipText(descriptions[i]);
      label.setToolTipText(descriptions[i]);
    }
    public void addNotify(){
      super.addNotify();
      String t=imageNames[getAlleleIndex()];
      if(t!=null && t.length()>0)
        pic.setIcon(new ImageIcon(CudosExhibit.getApplet(this).getImage( t )));
    }
    int locus;
    boolean canChangeSex = true;
    public void setCanChangeSex(boolean b){canChangeSex=b;}
    public String[] getPossibleAlleles() {
      if(isSexLinked() && isLocusSexLinked(locus) && !canChangeSex){
        return isAlleleOnYChromosome(getAllele()) ?
            Filter.and((String[])allAlleles[locus], getYChromosomeAlleles())
            : Filter.andnot((String[])allAlleles[locus], getYChromosomeAlleles());
      }
      else return (String[])allAlleles[locus];
    }
  }
  class PhenotypeFromFile extends PhenoImpl{

    public static final String LETHAL_IMAGE = "resources/images/lethal.jpg";

    JLabel pic = new JLabel();
    protected PhenotypeFromFile(){
      this.add(pic);
    }
    String imageName;
    public void updateGenes(){
      lethal=false;
      String s[]=((GenomeFromFile)genome).getPhenotypeStringsForCurrentAlleles();
      if(s.length>1){
        imageName=s[0];
        description = s[1];
      }else {
        imageName=null;
        description = s[0];
        if(description.equalsIgnoreCase("Lethal")){
          lethal=true;
          imageName=LETHAL_IMAGE;
        }
      }
      try{
        image = CudosExhibit.getApplet(this).getImage(imageName);
        pic.setIcon(new ImageIcon(image));
      }catch(Exception e){}
      pic.setToolTipText(description);
    }
    boolean lethal;
    public boolean isLethal(){return lethal;}
    public void addNotify(){
      super.addNotify();
      if(imageName!=null){
        image = CudosExhibit.getApplet(this).getImage(imageName);
        pic.setIcon(new ImageIcon(image));
      }
    }
    Image image;
    String description;
    public int compareTo(Object o){
      int i=((PhenotypeFromFile)o).image.hashCode() - image.hashCode();
      if(i==0) i=((PhenotypeFromFile)o).description.compareTo(description);
      return i;
    }
  }

  String[] YChromosomeAlleles;
  public String[] getYChromosomeAlleles(){ return YChromosomeAlleles; }

  /** Compare phenotypes by checking the pheno image names */
  public boolean isPhenotypicallyIdenticalTo(Genome g1){
    if(isGeneticallyIdenticalTo(g1)) return true;
    PhenotypeFromFile f=new PhenotypeFromFile();
    f.setGenome(this); String i0 = f.imageName;
    f.setGenome(g1);  return i0==f.imageName || (i0!=null && i0.equals(f.imageName));
  }

  public boolean isLethal(){
    PhenotypeFromFile f=new PhenotypeFromFile();
    f.setGenome(this);
    return f.isLethal();
  }

}

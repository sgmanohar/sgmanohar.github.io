package com.cudos.genetic;
import com.cudos.common.*;

import java.awt.*;

/**
 * Sickle cell genetics
 */

public class SickleCell {

  public static class SickleGenome extends Genome{
    public String[] getYChromosomeAlleles(){return null;}
    public Object[] getAllAlleles() {
      return new String[][]{ alleles };
    }
    public SickleGenome(){
      loci.add(new SickleGene[]{ new SickleGene(), new SickleGene()  });
    }
    public double linkage(int locus) {
      return 0.0;
    }

  }
  public static final String W = "HbW", S = "HbS";
  public static final String[] alleles = {S, W};

  public static class SickleGene extends GeneImpl.TextGene{
    SickleGene(){ setAllele(W); }
    public String[] getPossibleAlleles(){
      return alleles;
    }
  }


  public static class SicklePhenotype extends PhenoImpl{


    public void updateGenes() {
      Gene[] g=genome.getGenesAtLocus(0);
      int s=g[0].getAlleleIndex() + g[1].getAlleleIndex();
      sickler =  s== 0 ;
      setToolTipText(sickler?"Sickler": (s==1)?"Carrier":"Normal");
      repaint();
    }
    boolean sickler = false;

    Image s0, s1;
    public void paint(Graphics g){
      super.paint(g);
      if(sickler){
        if(s1==null) s1=CudosExhibit.getApplet(this).getImage("resources/images/rbc_sickle.jpg");
        g.drawImage(s1,0,0,this);
      }  else {
        if(s0==null) s0=CudosExhibit.getApplet(this).getImage("resources/images/rbc_norm.jpg");
        g.drawImage(s0,0,0,this);
      }
    }
    public boolean isLethal(){return false;}

    public int compareTo(Object o) {
      boolean os=((SicklePhenotype)o).sickler;
      if(os==sickler)return 0;
      if(os==sickler)return 1; else return -1;
    }
  }

}

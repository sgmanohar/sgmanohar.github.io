package com.cudos.genetic;

import javax.swing.*;
import java.awt.*;

/**
 * Hair Colour
 */

public class HairColour {
  public HairColour() {
  }

  public static final String
      A0 = "A0", A1 = "A1", A2="A2",
      B0 = "B0", B1 = "B1", B2="B2",
      typeA[]        = {A0,A1,A2},
      typeB[]        = {B0,B1,B2},
      allAlleles[][] = {typeA , typeB};

  public static class HairGenome extends Genome{
    public String[] getYChromosomeAlleles(){return null;}
    public Object[] getAllAlleles() {
      return allAlleles;
    }
    public HairGenome(){
      loci.add(new HairGene[]{new HairGene(0), new HairGene(0)});
      loci.add(new HairGene[]{new HairGene(1), new HairGene(1)});
    }
    public double linkage(int locus) {      return 0.0;    }

  }
  public static class HairGene extends GeneImpl.TextGene{
    int type = 0;
    JLabel colour = new JLabel("");
    HairGene(){
      remove(label);
      colour.setPreferredSize(new Dimension(10,10));
      setLayout(new BorderLayout());
      add(label, BorderLayout.CENTER);
      add(colour,BorderLayout.WEST);
      colour.setOpaque(true);
    }
    public void setAllele(String allele){
      super.setAllele(allele);
      int a =0, b=0;
      if(allele.equals(A1)) a=1; else if(allele.equals(A2)) a=2;
      if(allele.equals(B1)) b=1; else if(allele.equals(B2)) b=2;
      colour.setBackground(calculateColor(a,b));
    }
    HairGene(int type){
      this();
      this.type=type;
      if(type==0) setAllele(A1);
      else setAllele(B1);
    }
    public String[] getPossibleAlleles(){
      if(type==0)return typeA; else return typeB;
    }

  }
  public static class HairPhenotype extends PhenoImpl{

    int pigA, pigB;
    public void updateGenes() {
      pigA=0; pigB=0;
      Gene[] g=genome.getGenesAtLocus(0);
      for(int i=0;i<2;i++){
        pigA += g[i].getAlleleIndex();
      }
      g=genome.getGenesAtLocus(1);
      for(int i=0;i<2;i++){
        pigB += g[i].getAlleleIndex();
      }

      setToolTipText("Pigment A = "+pigA+", Pigment B = "+pigB);
      repaint();
    }

    Image s0, s1;
    public void paint(Graphics g){
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      super.paint(g);
      g.setColor(calculateColor(pigA, pigB));
      g.fillOval(0,0,getWidth(),getHeight());
    }
    public boolean isLethal(){return false;}
    public int compareTo(Object o) {
      int oc=calculateColor(((HairPhenotype)o).pigA, ((HairPhenotype)o).pigB).getRGB(),
           c=calculateColor(pigA, pigB).getRGB();
      return oc-c;
    }
  }
 static public Color calculateColor(int a, int b){
   int hr=255,hg=255,hb=220,
       ar=60, ag=80, ab=90,
       br=0,  bg=10, bb=20;
   hr -= ar*a + br*b;
   hg -= ag*a + bg*b;
   hb -= ab*a + bb*b;
   return new Color(Math.max(0,Math.min(255,hr)), Math.max(0,Math.min(255,hg)),
                    Math.max(0,Math.min(255,hb)));
 }


}

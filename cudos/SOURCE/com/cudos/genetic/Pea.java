package com.cudos.genetic;

import com.cudos.genetic.GeneImpl.TextGene;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Pea genes
 */

public class Pea {


  public static class PeaGenome extends Genome{
    public String[] getYChromosomeAlleles(){return null;}
    public double linkage(int locus){ return 0; }
    public Object[] getAllAlleles(){
      return allAlleles;
    }
    PeaGenome(){
      loci.add( new PeaGene[]{ new PeaGene(colors[1]), new PeaGene(colors[1]) } );
      loci.add( new PeaGene[]{ new PeaGene(shapes[1]), new PeaGene(shapes[1]) } );
    }
  }


  static GeneralPath wrinkshape;
  static{
    int N=100, np=10, r1=10, r2=15;
    double q=0.03;
    wrinkshape = new GeneralPath(GeneralPath.WIND_NON_ZERO, N);
    wrinkshape.moveTo(r2, 0);
    for(int i=0;i<N;i++){
      int r=((2*i/np)%2==0) ? r1:r2, ro=(r==r1)?r2:r1;
      double t=i*Math.PI*2/N;
      double cs=Math.cos(t), sn=Math.sin(t);
      wrinkshape.curveTo((float)(r*cs-q*sn), (float)(r*sn-q*cs),
                         (float)(ro*cs), (float)(ro*sn),
                         (float)(r*cs), (float)(r*sn));
    }
    wrinkshape.closePath();
    wrinkshape.transform(AffineTransform.getTranslateInstance(r2,r2));
  }

  static Color[] colors = {Color.green, Color.yellow};
  static Shape[] shapes = {wrinkshape, new Ellipse2D.Float(0,0,30,30)};

  static Object[][] allValues = {colors, shapes};

  static String[] colAlleles   = {"y","Y"}, //yellow
                  shapeAlleles = {"r","R"}, //rounded
                  allAlleles[] = {  colAlleles, shapeAlleles };

  public static class PeaGene extends TextGene{

    Object value;


    public PeaGene(){
      remove(label);
      setLayout(new BorderLayout());
      add(label, BorderLayout.CENTER);
      diagram = new Diagram();
      diagram.setPreferredSize(new Dimension(30,30));
      add(diagram, BorderLayout.WEST);
    }
    public PeaGene(Object o){
      this();
      value = o;
      for(int i=0;i<allValues.length;i++)
        for(int j=0;j<allValues[i].length;j++){
          if(allValues[i][j].equals(o)){
            setAllele(allAlleles[i][j].toString());
          }
        }
    }
    public void setAllele(String s){
      super.setAllele(s);
      for(int i=0;i<allAlleles.length;i++)
        for(int j=0;j<allAlleles[i].length;j++)
          if(allAlleles[i][j].equals(s))
            value=allValues[i][j];
    }

    Diagram diagram;
    class Diagram extends JPanel{
      public void paint(Graphics g){
        super.paint(g);
        if (value instanceof Color) {
          g.setColor( (Color) value);
          g.fill3DRect(0, 0, getWidth(), getHeight(), true);
        }
        else if (value instanceof Shape) {
          ( (Graphics2D) g).fill( (Shape) value);
        }
      }
    }
    public String[] getPossibleAlleles(){
      if(value instanceof Color)return  colAlleles;
      if(value instanceof Shape)return shapeAlleles;
      throw new RuntimeException("No such gene type");
    }

  }

  public static class PeaPhenotype extends PhenoImpl{
    AffineTransform scale = AffineTransform.getScaleInstance(3,3);
    public void updateGenes(){
      Gene[] colg=genome.getGenesAtLocus(0), shg=genome.getGenesAtLocus(1);
      setForeground( (colg[0].getAlleleIndex()+colg[1].getAlleleIndex()) == 0 ? colors[0] : colors[1] );
      s = (shg[0].getAlleleIndex() + shg[1].getAlleleIndex() ==0) ? shapes[0] : shapes[1];
      setToolTipText((getForeground()==colors[0]?"Green ":"Yellow ")
                      +(s==shapes[0]?"wrinkled seed":"round seed")) ;
      s=scale.createTransformedShape(s);
    }
    Shape s = shapes[0];
    public void paint(Graphics g){
      super.paint(g);
      ((Graphics2D)g).fill(s);
    }
    public boolean isLethal(){return false;}

    public int compareTo(Object o){ //lexicographically (shape,color)
      int q=((PeaPhenotype)o).s.hashCode()-s.hashCode();
      if(q==0)q=((PeaPhenotype)o).getForeground().getRGB()-getForeground().getRGB();
      return q;
    }
  }

}

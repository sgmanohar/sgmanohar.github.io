package com.cudos.genetic;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;

/**
 * Gene
 */

public interface Gene extends Cloneable{
  public JComponent getComponent();
  public void setAllele(String allele);
  public String getAllele();
  /** create a blank genotype of genes for an organism */
//  public Vector createFullGenotype();
  /** Return all possible alleles, with the recessive test first */
  public String[] getPossibleAlleles();
  public void addActionListener(ActionListener a);
  public void removeActionListener(ActionListener a);
//  public double linkage(int locus);

  public int getAlleleIndex();
  public void setMutable(boolean b);
  public void setCanChangeSex(boolean b);
}


abstract class GeneImpl extends JPanel implements Gene{
  public GeneImpl() {
    this.addMouseListener(ml);
  }
  /** Returns a component that draws the current allele */
  public JComponent getComponent(){
    return this;
  }
  private String allele="W";
  public void setAllele(String allele){
    this.allele=allele;
    //inform phenotype
    if(phenolisten!=null)phenolisten.actionPerformed(new ActionEvent(this,0,"Allele changed"));
  }
  ActionListener phenolisten = null;
  public void addActionListener(ActionListener a){
    phenolisten=AWTEventMulticaster.add(a,phenolisten);
  }
  public void removeActionListener(ActionListener a){
    phenolisten=AWTEventMulticaster.remove(a, phenolisten);
  }

  public String getAllele(){return allele;}
  public int getAlleleIndex(){
    String[] aa=getPossibleAlleles();
    for(int i=0;i<aa.length;i++)
      if(aa[i].equals(allele)) return i;
    throw new IllegalStateException("This gene has an invalid allele "+allele);
  }

  boolean mutable = true;
  public void setMutable(boolean m){mutable=m;}
  MouseListener ml = new MouseAdapter(){
   public void mouseClicked(MouseEvent e){
     if(!mutable)return;
     String[] a = getPossibleAlleles();
     int x=-1;
     for(int i=0;i<a.length;i++){
       if(a[i].equals(allele)) x=i;
     }
     x=(x+1)%a.length;
     setAllele(a[x]);
   }
   public void mouseReleased(MouseEvent e){
     if(!mutable)return;
     if(e.isPopupTrigger()){
       popup(e);
     }
   }
  };
  JPopupMenu alleleMenu = new JPopupMenu("Alleles:");
  public void popup(MouseEvent e){
    alleleMenu.removeAll();
    String[] a = getPossibleAlleles();
    for(int i=0;i<a.length;i++)
      alleleMenu.add(new AlleleAction(a[i]));
    alleleMenu.show(this,e.getX(), e.getY());
  }
  class AlleleAction extends AbstractAction{
    String newallele;
    AlleleAction(String a){super(a);newallele=a;}
    public void actionPerformed(ActionEvent e){
      setAllele(newallele);
    }
  }

  public static abstract class TextGene extends GeneImpl{
    public JLabel label = new JLabel(){
      public void paint(Graphics g){
        ( (Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                           RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);
      }
    };
    public TextGene(){
      setLayout(new BorderLayout());
      add(label, BorderLayout.CENTER);
      label.setOpaque(false);
      label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      label.addMouseListener(ml);
      label.setHorizontalTextPosition(label.CENTER);
      label.setFont(new Font("Serif", Font.BOLD, 14));
    }
    public void setAllele(String s){
      super.setAllele(s);
      label.setText(s);
      try{
        label.setToolTipText("Allele = '" + getAllele() + "' (type " +
                             getAlleleIndex() + " of " +
                             getPossibleAlleles().length + ")");
      }catch(Exception e){};
    }
  }

  public void setCanChangeSex(boolean b){};
}

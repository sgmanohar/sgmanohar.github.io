package com.cudos.genetic;

import javax.swing.JComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Phenotype
 */

public interface Phenotype extends Comparable{
  public JComponent getComponent();
  public void setGenome(Genome v);
  public Genome getGenome();
  public boolean isLethal();
}

abstract class  PhenoImpl extends JPanel implements Phenotype{
  public JComponent getComponent() {
    return this;
  }
  public PhenoImpl(){
    setPreferredSize(new Dimension(100,100));
    setOpaque(true);
  }
  abstract void updateGenes();

  Genome genome;
  public void setGenome(Genome g) {
    genome=g;
    g.addActionListener(alleleChanged);
    updateGenes();
  }

  public ActionListener alleleChanged= new ActionListener(){
    public void actionPerformed(ActionEvent e){
      updateGenes();
    }
  };

  public Genome getGenome() {return genome;  }
}



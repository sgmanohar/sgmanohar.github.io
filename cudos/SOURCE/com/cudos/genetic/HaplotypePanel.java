package com.cudos.genetic;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.border.*;

/**
 * Haplotype panel
 */

public  class HaplotypePanel extends JPanel{
  GridLayout grid=new GridLayout();
  public HaplotypePanel(Vector haplotype){
    setLayout(grid);
    grid.setColumns(1);
    grid.setRows(haplotype.size());
    for(int i=0;i<haplotype.size();i++){
      Gene g=(Gene)haplotype.get(i);
      g.setMutable(false);
      add(g.getComponent());
    }
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
  }
}

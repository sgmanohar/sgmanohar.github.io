package com.cudos.genetic;

import javax.swing.*;
import java.awt.*;

/**
 * Draw the possible gametes
 */

public class GametesPanel extends JPanel {
  JLabel[] probs;
  HaplotypePanel[] happanels;
  JPanel[] panels;
  FlowLayout flowlayout = new FlowLayout();
  public GametesPanel(Individual p) {
    haploset = p.genome.getPossibleHaplotypes();
    int N=haploset.haplos.length;
    panels=new JPanel[N];
    happanels=new HaplotypePanel[N];
    probs=new JLabel[N];
    flowlayout.setAlignment(FlowLayout.CENTER);
    flowlayout.setHgap(10);
    flowlayout.setVgap(10);
    setLayout(flowlayout);
    for(int i=0;i<N;i++){
      probs[i] = new JLabel(Integer.toString(
          (int)Math.round(haploset.probs[i]*100))+"%");
      happanels[i]=new HaplotypePanel(haploset.haplos[i]);
      panels[i] = new JPanel();
      panels[i].setLayout(new BorderLayout());
      panels[i].add(probs[i], BorderLayout.SOUTH);
      panels[i].add(happanels[i], BorderLayout.CENTER);
      add(panels[i]);
    }
  }

  Genome.HaplotypeSet haploset;

}

package com.cudos.genetic;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.cudos.common.CudosExhibit;

/**
 * Punnett square
 */

public class PunnettPanel extends JPanel {
  static final String CROSS_CORNER="resources/images/crosscorner.gif";
  GridLayout gridLayout1 = new GridLayout();
  JLabel topleft = new JLabel();
  BorderLayout borderlayout= new BorderLayout();
  JPanel main = new JPanel() {
    public void paint(Graphics g) {
      super.paint(g);
      if (hs1 == null || hs2 == null)return;
      g.setColor(getForeground());
      ( (Graphics2D) g).setStroke(new BasicStroke(3));
      int h = getHeight() / (hs1.haplos.length + 1) - 2,
          v = getWidth() / (hs2.haplos.length + 1) - 2;
      g.drawLine(0, h, getWidth(), h);
      g.drawLine(v, 0, v, getHeight());
    }
  };
  public void addNotify(){
    super.addNotify();
    topleft.setIcon(new ImageIcon(CudosExhibit.getApplet(this).getImage(CROSS_CORNER)));
  }

  Genome.HaplotypeSet hs1, hs2;
  Genome.ChildSet children;
  JLabel jLabel1 = new JLabel();
  Border border1;
  Border border2;
  public PunnettPanel(Individual p1, Individual p2, boolean showPhenotype) {
    this();
    this.showPhenotype=showPhenotype;
    hs1 = p1.genome.getPossibleHaplotypes();
    hs2 = p2.genome.getPossibleHaplotypes();
    children = hs1.combine(hs2);
    gridLayout1.setRows(hs1.haplos.length+1);
    gridLayout1.setColumns(hs2.haplos.length+1);
    main.add(topleft);
    for(int i=0;i<hs2.haplos.length;i++){
      HaplotypePanel htp=new HaplotypePanel(hs2.haplos[i]);
      main.add(htp);
    }
    for(int i=0;i<hs1.haplos.length;i++){
      HaplotypePanel htp=new HaplotypePanel(hs1.haplos[i]);
      main.add(htp);
      for(int j=0;j<hs2.haplos.length;j++){
        GenomeFromFile g = (GenomeFromFile)children.genomes[i*hs2.haplos.length+j];
        Individual child=g.createNewBlankIndividual();
        child.genome.setFrom(g);
        child.genome.setMutable(false);
        if(showPhenotype)child.phenopanel.setVisible(false);
        child.namelabel.setVisible(false);
        child.linkagedisp.setVisible(false);
        main.add(child);
      }
    }
  }
  public boolean showPhenotype = false;
  public PunnettPanel(Individual i1, Individual i2){    this(i1,i2, false);  }


  public PunnettPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    gridLayout1.setHgap(15);
    gridLayout1.setVgap(15);
    topleft.setToolTipText("Punnett square");
    topleft.setHorizontalAlignment(SwingConstants.CENTER);
    main.setLayout(gridLayout1);
    this.setLayout(borderlayout);
    jLabel1.setText("Maternal gametes vertically, paternal gametes horizontally");
    this.setBorder(border1);
    main.setBorder(border2);
    main.setBackground(getBackground().darker());
    this.add(main);
    this.add(jLabel1,  BorderLayout.SOUTH);
  }

}

package phic.gui.graphics;

import javax.swing.JPanel;
import phic.*;
import phic.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent;
import phic.gui.*;
import javax.swing.JLabel;

/**
 * A diagram of the compartment model
 */

public class CompartmentDiagram extends JPanel {

  String compartmentName[] = {"Plasma", "ECF", "ICF"};
  String itemNames[]       = {"Vol", "Na", "K", "Cl", "Bic", "gluc", "prot", "urea"};
  Color[] colours = {Color.black, Color.red.darker(), Color.cyan.darker(), Color.green.darker(),
      Color.gray, Color.pink.darker(), Color.yellow, Color.orange.darker()};
  VDouble[][] vd;
  VisibleVariable[][] var;
  Component[][] comp;
  Body body;

  public void setBody(Body b){
    if(body!=b){
      vd = new VDouble[][] {
          {
          b.blood.PV, b.blood.PNa, b.blood.PK, b.blood.PCl, b.blood.PBic, b.blood.glucose, b.blood.PPr,
          b.blood.PUN}
          , {
          b.ecf.volume, b.ecf.Na, b.ecf.K, b.ecf.Cl, b.ecf.bicarb,b.ecf.glucose, b.ecf.prot,
          b.ecf.urea}
          , {
          b.icf.volume, b.icf.Na, b.icf.K, b.icf.Cl, b.icf.bicarb, b.icf.glucose, b.icf.prot,
          b.icf.urea}
      };
      rowmids=new int[vd.length];
      rowbots=new int[vd.length];
      var = new VisibleVariable[vd.length][vd[0].length];
      if (comp == null) comp = new Component[vd.length][vd[0].length];
      for (int i = 0; i < vd.length; i++)for (int j = 0; j < vd[i].length; j++) {
        var[i][j] = Variables.forVDouble(vd[i][j]);
        if (comp[i][j] == null){
          comp[i][j] = new Item(j, var[i][j]);
          add(comp[i][j]);
        }
      }
      body=b;
    }
    update();
  }
  class Item extends JLabel{
    VisibleVariable var;
    int colour;
    public Item(int colour,VisibleVariable v){
      this.var=v;
      this.colour=colour;
      setBackground(colours[colour]);
      //setToolTipText(v.longName);
      setOpaque(true);
    }
    public String getToolTipText(){
      return var.longName+" = "+var.formatValue(var.node.getVDouble().get(), true, false);
    }
    public void paint(Graphics g){
      g.setColor(getBackground());
      g.fill3DRect(0,0,getWidth(), getHeight(), true);
      if(getWidth()>20 && getHeight()>10){
        g.setColor(getForeground());
        g.drawString(var.shortName, 2,getHeight()-2);
      }
    }
  }



  public CompartmentDiagram() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void paint(Graphics g){
    super.paintComponent(g);
    g.setColor(Color.black);
    g.drawLine(0,axy,getWidth(),axy);
    g.drawLine(axx,0,axx,getHeight());
    g.setFont(getFont());
    for(int i=0;i<rowmids.length;i++){
      g.drawString(compartmentName[i], 2, rowmids[i]+5);
      double v=vd[i][0].get();
      g.drawString(UnitConstants.formatValue(v, UnitConstants.LITRES, i==rowmids.length-1, true) ,
                   10, Math.max(rowbots[i], rowmids[i]+20));
    }
    int N=3;
    for(int i=0;i<N;i++){
      double v=i*0.300/N;
      int x=axx+i*(getWidth()-axx)/N;
      g.drawString(UnitConstants.formatValue(v, UnitConstants.OSMOLES, i==N-1, true),
                   x+2, axy-4);
      g.drawLine(x,axy,x,axy-4);
    }
    super.paintComponents(g);
  }

  /** volume scale for height */
  double heightScale = 1;
  /** osm scale for width */
  double widthScale = 1;
  /** Maximum volume representable, in L */
  double totalVolume=60;
  /** Maximum osmolarity representable in Osm */
  double maxOsm = 0.350;
  /** axes positions */
  int axx=50, axy=15;
  int[] rowmids, rowbots;

  /** Layout the items */
  public void update() {
    int cumHt = axy+2;
    for (int i = 0; i < vd.length; i++){
      int h=Math.max(1,(int)(vd[i][0].get() * heightScale));
      rowmids[i]=cumHt+h/2;
      rowbots[i]=cumHt+h;
      int cumWd = axx+2;
      for (int j = 1; j < vd[i].length; j++) {
        int w = Math.max(1,(int)(vd[i][j].get() * widthScale));
        comp[i][j].setBounds( cumWd, cumHt, w, h );
        cumWd+=w;
      }
      cumHt+=h+1;
    }
    repaint();
  }


  private void jbInit() throws Exception {
    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        resized();
      }
    });
    setLayout(null);
  }
  /** adjust scale when component is resized */
  public void resized(){
    heightScale = (getHeight()-axy)/totalVolume;
    widthScale  = (getWidth()-axx) /maxOsm;
  }

}

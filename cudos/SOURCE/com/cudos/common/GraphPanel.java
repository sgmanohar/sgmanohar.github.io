
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.border.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
  JPanel jPanel1 = new JPanel();
  JPanel graphpane = new JPanel(){
		public void paint(Graphics g_){
			super.paint(g_);
			Graphics2D g=(Graphics2D)g_;
			double dx=((double)getWidth())/dwidth;
			for(int i=0;i<values.length;i++){
				g.setColor(colors[i%colors.length]);
				int zh=(int)(getHeight()*(1-zero[i]*scale[i]));
				g.drawLine(0,zh,getWidth(),zh);
				for(int x=1;x<posX;x++){
					if((values[i][x-1]!=Double.NaN) && (values[i][x]!=Double.NaN))
					g.drawLine((int)(dx*(x-1)),(int)(getHeight()*values[i][x-1]),(int)(dx*x),(int)(getHeight()*values[i][x]));
				}
			}
		}
	};
	public static final Color[] colors={Color.red,Color.yellow,Color.green,
		Color.cyan,Color.white,Color.magenta,Color.orange,Color.pink,Color.gray,Color.blue};
	public double[][] values;
	public double[] scale;
	public double[] zero;
	int posX;
	public void tick(){
		if(values.length>0){
			for(int i=0;i<components.size();i++){
				GraphableComponent gc=(GraphableComponent)components.get(i);
				values[i][posX]=(1-(gc.getGraphableValue()+zero[i])*scale[i]);
			}
			posX++;if(posX>=values[0].length)reset();
		}
		graphpane.repaint();
	}
	Vector components=new Vector();
	public void setComponents(Vector c){
		components.removeAllElements();
		for(Enumeration e=c.elements();e.hasMoreElements();){
			Object cc=e.nextElement();
			if(cc instanceof GraphableComponent){
				components.add(cc);
				((GraphableComponent)cc).setColour(colors[components.indexOf(cc)]);
			}
		}
		reset();
	}
	public void reset(){
		posX=0;
		zero=new double[components.size()];
		scale=new double[components.size()];
		values=new double[components.size()][dwidth];
		for(int i=0;i<components.size();i++){
			GraphableComponent gc=(GraphableComponent)components.get(i);
			zero[i]=-gc.getGraphableMin();
			scale[i]=1/(gc.getGraphableMax()+zero[i]);
		}
		graphpane.repaint();
	}
  Border border1;
  BorderLayout borderLayout1 = new BorderLayout();

  public GraphPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	reset();
  }

	final int dwidth=180;
  BorderLayout borderLayout2 = new BorderLayout();
  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
    graphpane.setBackground(Color.black);
//    graphpane.setPreferredSize(new Dimension(dwidth, 95));
    graphpane.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        graphpane_mouseClicked(e);
      }
    });
    jPanel1.setBorder(border1);
    jPanel1.setLayout(borderLayout1);
    this.setLayout(borderLayout2);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(graphpane, BorderLayout.CENTER);
  }

  void graphpane_mouseClicked(MouseEvent e) {
	reset();
  }
}
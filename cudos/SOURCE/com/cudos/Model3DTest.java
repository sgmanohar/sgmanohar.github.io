
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.event.*;

public class Model3DTest extends CudosExhibit {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton exit = new JButton();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JTextField jTextField1 = new JTextField();
  FlowLayout flowLayout1 = new FlowLayout();
  JTextField jTextField2 = new JTextField();
  JTextField jTextField3 = new JTextField();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JPanel graph = new JPanel(){
	public void paint(Graphics g_){
		super.paint(g_);
		Graphics2D g=(Graphics2D)g_;
		if(regionsCreated){
			int npl=region.length;
			for(int i=0;i<region.length;i++){
				for(int j=0;j<region[i].length;j++){
					int[][] b=region[i][j].bounds;
					for(int k=0;k<b.length;k++){
						for(int m=0;m<b[k].length;m++){
							g.drawLine(b[k][m++]+ax,k/npl+i*10,b[k][m],k/npl+i*10+ay);
						}
					}
				}
			}
		}
	}
};

  public Model3DTest() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

	int ax=100,ay=100;
	String prefix="resources/BrainSections/";
	String iname[]=new String[]{"transverse-01ix.gif","transverse-02ix.gif","transverse-03ix.gif","transverse-04ix.gif"};
	public void postinit(){
		image=new Image[iname.length];
		for(int i=0;i<iname.length;i++){
			image[i]=getApplet().getImage(iname[i]);
		}
		graph.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		MouseInputAdapter mymouse=new MouseInputAdapter(){
			int ox,oy;
			public void mousePressed(MouseEvent e){
				ox=e.getX();
				oy=e.getY();
			}
			public void mouseDragged(MouseEvent e){
				ax+=e.getX()-ox;ox=e.getX();
				ay+=e.getY()-oy;oy=e.getY();
				graph.repaint();
			}
		};
		graph.addMouseListener(mymouse);
		graph.addMouseMotionListener(mymouse);
		if(image[0].getWidth(this)>=0)createRegions();
	}

	ImageRegion[][] region=new ImageRegion[iname.length][lookup.length];
	protected static int[] lookup={ 0xc00000,0x800000,0x008000,0x808000,
					0x000080,0x800080,0x008080,0x00c000,
					0x0000c0,0xff0000,0x00ff00,0xffff00,
					0x0000ff,0xff00ff,0x00ffff,0xff80ff,
					0x80ffff,0xffff80 };
	int indexLoaded=0;
	public boolean imageUpdate(Image i,int inf,int x,int y,int w,int h){
System.out.println(inf);
		if((inf&ImageObserver.ALLBITS)>0){
			indexLoaded++;
System.out.println(indexLoaded);
			if(region[0]==null && (indexLoaded>=iname.length-1))createRegions();
		}
		return super.imageUpdate(i,inf,x,y,w,h);
	}
	Image[] image;
	boolean regionsCreated=false;
	public void createRegions(){
		for(int j=0;j<image.length;j++){
			int w=image[j].getWidth(this),h=image[j].getHeight(this);
			int[] pix=new int[w*h];
			PixelGrabber pg=new PixelGrabber(image[j],0,0,w,h,pix,0,w);
			try {
			     pg.grabPixels();
			} catch (Exception x) {
				x.printStackTrace();
			}
			for(int i=0;i<lookup.length;i++){
				region[j][i]=new ImageRegion(pix,w,h,lookup[i]+0xff000000);
			}
		}
		regionsCreated=true;
		System.gc();	/// theres a lot of wasted memory?
	}

	class ImageRegion{
		int[][] bounds;//first index=y coordinate, next is array of x coordinates for line starts/stops;
		int height;
		public ImageRegion(int[]pixels, int width,int h,int colour){
			height=h;
			bounds=new int[height][];
			for(int y=0;y<height;y++){
				boolean inside=false;int currp=0;	//start each line outside region
				Vector b=new Vector();
				for(int x=0;x<width;x++){
					currp=pixels[x+width*y];
					if((!inside && currp==colour)||(inside &&(currp!=colour))){
						inside=!inside;
						b.add(new Integer(x));
					}
				}
				if(inside)b.add(new Integer(width-1));	//end each line outside region
				bounds[y]=new int[b.size()];
				for(int i=0;i<bounds[y].length;i++)
					bounds[y][i]=((Integer)b.get(i)).intValue();
			}
		}
	}



  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    exit.setText("Exit");
    exit.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        exit_actionPerformed(e);
      }
    });
    this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(5,5,5,5)));
    jPanel2.setLayout(borderLayout2);
    jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 235),Color.white,new Color(135, 133, 115),new Color(94, 93, 80)),BorderFactory.createEmptyBorder(5,5,5,5)));
    jPanel3.setLayout(borderLayout3);
    jTextField1.setPreferredSize(new Dimension(70, 25));
    jTextField1.setText("0");
    jTextField1.setHorizontalAlignment(SwingConstants.RIGHT);
    jPanel5.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jTextField2.setPreferredSize(new Dimension(70, 25));
    jTextField2.setText("0");
    jTextField2.setHorizontalAlignment(SwingConstants.RIGHT);
    jPanel5.setPreferredSize(new Dimension(90, 31));
    jTextField3.setPreferredSize(new Dimension(70, 25));
    jTextField3.setText("0");
    jTextField3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setText("x");
    jLabel2.setText("y");
    jLabel3.setText("z");
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(exit, null);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(graph, BorderLayout.CENTER);
    this.getContentPane().add(jPanel5, BorderLayout.EAST);
    jPanel5.add(jLabel1, null);
    jPanel5.add(jTextField1, null);
    jPanel5.add(jLabel2, null);
    jPanel5.add(jTextField2, null);
    jPanel5.add(jLabel3, null);
    jPanel5.add(jTextField3, null);
  }

  void exit_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }
}
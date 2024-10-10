
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import com.neurolab.common.*;
import java.awt.event.*;
import java.beans.*;

public class PosturalEquilibrium extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel0();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel0();
	JCheckBox neck = new JCheckBox();
	JCheckBox vestibular = new JCheckBox();
	JCheckBox support = new JCheckBox();
	Border border1;
	TitledBorder titledBorder1;
	FlowLayout flowLayout1 = new FlowLayout();
	JPanel jPanel4 = new JPanel0();
	ReturnButton returnButton1 = new ReturnButton();
	JPanel jPanel5 = new JPanel0();
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	Label3D label3D3 = new Label3D();
	AngleControl slope = new AngleControl();
	AngleControl head = new AngleControl();
	JPanel jPanel6 = new JPanel0();
	Border border2;
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel7 = new JPanel0();
	Border border3;
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel graph = new JPanel0(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
		try{
			//Graphics2D g2=(Graphics2D)g;
			//g2.translate(20,50);
			//g2.scale(1.3,1.3);

			//Rectangle2D.Double sky = new Rectangle2D.Double(-20,-50,300,400);
			//GradientPaint gp = new GradientPaint(140, -50, Color.blue, 140, 100, Color.cyan);
			//g2.setPaint(gp);
			//g2.fill(sky);
			g.setColor(Color.cyan); g.fillRect(-20,-50,300,400);

			Polygon grass = new Polygon(new int[]	  {-20, 280, 280, -20},
                                                    new int[]		{  j,   i, 400, 400}, 4 );
			g.setColor(Color.green);
			g.fillPolygon(grass);
			//g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			setStrokeThickness(g,4);
		}catch(Exception e){
			//?paint without graphics2D
		}

		g.setColor(Color.black);
		g.drawPolyline(new int[]{lx,nx,px,qx},new int[]{ly,ny,py,qy},4);
		//                ' Back leg = q p n l
		g.drawPolyline(new int[]{gx,fx,ex,bx,lx,mx},new int[]{gy,fy,ey,by,ly,my},6);
//                 ' Front leg = g f e b
//                         ' Back = b l
//                         ' Tail = l m
		g.drawPolyline(new int[]{dx,bx,ax,cx},new int[]{dy,by,ay,cy},4);
//                 ' Head
//

		g.drawOval(rx, ry, 2,2);//                     ' Eye

		g.setColor(Color.red);//                        ' Gravity
		g.drawLine (hx, hy,hx, hy + 45);
		g.drawLine (hx,hy+45,hx + 7, hy + 37);
		g.drawLine (hx, hy + 45,hx - 7, hy + 37);

	}
	};


	public Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			calculate();
			graph.repaint();
		}
	} );
	public String getExhibitName(){return "Posture";}
	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		oldbody = 5;
		oldslope = 0;
		i = 170;
		j = 170;
		oldhead = 5;

		slope_change();
		timer.start();
	}

	public PosturalEquilibrium() {
	}


	public int limu(int a, int b){return (a>b)?b:a;}
	public int liml(int a, int b){return (a>b)?a:b;}
int body, oldbody ,oldslope, oldhead;
double ctheta ,stheta ;
int 	x0, y0,                                                   //' body positon
				ax, ay, bx, by, cx, cy, dx, dy, ex, ey, fx, fy, gx, gy,   //' anatomy
	hx, hy, lx, ly, mx, my, nx, ny, px, py, qx, qy, rx, ry;
double k1,k2; //,xx;
int xx;
int beta1,beta2,nnx,nny ,eex,eey;
final double scaler = 0.15;

	public void calculate(){
		int Head=head.getValue();
		int Slope=-slope.getValue();
		double cam,sam,cal,sal;
		if( support.isSelected()) body = body - oldbody * 4/10;
		if( neck.isSelected()) body = body -  4* Head/10;
		if( vestibular.isSelected() ) body = body - 4 * (oldbody - Head)/10;
		body = limu(body, Slope + 38);
		body = liml(body, Slope - 38);

		if( (body == oldbody ) && (Head == oldhead) && (Slope == oldslope))return;
		oldbody = body;
		oldhead = Head;
		oldslope = Slope;
		cam = scaler * Math.cos(Math.PI/180 * (body - Head));
		sam = scaler * Math.sin(Math.PI/180 * (body - Head));
		cal = scaler * Math.cos(Math.PI/180 * body);
		sal = scaler * Math.sin(Math.PI/180 * body);

								x0 = 140 ;                                 //' overall origin
								y0 = 120 ;

		bx = x0 - (int)(300 * sal + 400 * cal);
		by = y0 - (int)(300 * cal - 400 * sal);    //' shoulder

		ax = bx - (int)(300 * cam);
		ay = by + (int)(300 * sam);
		cx = bx - (int)(100 * sam);
		cy = by - (int)(100 * cam);
		dx = bx - (int)(200 * sam);
		dy = by - (int)(200 * cam);                 //' head + ear
		rx = bx - (int)(60 * cam + 60 * sam);
		ry = by + (int)(60 * sam - 60 * cam);       //' eye

		lx = bx + (int)(800 * cal);                 //' Bottom
		ly = by - (int)(800 * sal);

		gx = x0 - (int)(600 * ctheta);  //              ' Front foot
		gy = y0 + (int)(600 * stheta);
		fx = x0 - (int)(500 * ctheta);
		fy = y0 + (int)(500 * stheta);

		qx = x0 + (int)(400 * ctheta );  //             ' Back foot
		qy = y0 - (int)(400 * stheta);
		px = x0 +(int)( 500 * ctheta);
		py = y0 - (int)(500 * stheta);


		nnx = (lx + px) / 2;          //       ' Back knee
		nny = (ly + py) / 2;
		xx = 2025 - ((lx - nnx) *(lx-nnx) + (ly - nny) *( ly-nny) );
		xx = liml(xx, 0);
		k1 = Math.sqrt(xx);
		nx = nnx - (int)((nnx - x0) * k1 / 75);
		ny = nny + (int)((y0 - nny) * k1 / 75);


		eex = (bx + fx) / 2  ;//               ' Front knee
		eey = (by + fy) / 2;
		xx = 2025 - ((bx - eex) *(bx-eex) + (by - eey)*(by-eey));
		xx = liml(xx, 0);
		k2 = Math.sqrt(xx);
		ex = eex - (int)((eex - x0) * k2 / 75);
		ey = eey + (int)((y0 - eey) * k2 / 75);

		mx = lx + (int)(300 * scaler * Math.cos(Math.PI/180 * (body + 45)) ) ;//   ' Tail
		my = ly - (int)(300 * scaler * Math.sin(Math.PI/180 * (body + 45)) );

		hx = (bx + lx)/2;//               ' Centre of gravity
		hy = (by + ly)/2;
	}

	private void jbInit() throws Exception {
		neck.setBackground(systemGray);
		vestibular.setBackground(systemGray);
		support.setBackground(systemGray);
		border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),"Reflexes");
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(15,15,15,15));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		neck.setText("Neck");
		jPanel2.setPreferredSize(new Dimension(150, 35));
		vestibular.setText("Vestibular");
		jPanel3.setBorder(titledBorder1);
		jPanel3.setPreferredSize(new Dimension(170, 120));
		jPanel3.setLayout(flowLayout1);
		support.setToolTipText("");
		support.setText("Positive supporting");
		flowLayout1.setAlignment(FlowLayout.LEFT);
		jPanel5.setLayout(null);
		label3D1.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D1.setText("Slope");
		label3D1.setBounds(new Rectangle(12, 24, 56, 22));
		label3D2.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D2.setText("Head");
		label3D2.setBounds(new Rectangle(16, 83, 49, 20));
		label3D3.setFont(new java.awt.Font("SansSerif", 1, 16));
		label3D3.setText("tilt");
		label3D3.setBounds(new Rectangle(34, 100, 33, 21));
		slope.setForeground(SystemColor.menuText);
		slope.setFont(new java.awt.Font("SansSerif", 1, 12));
		slope.setPrefix("");
		slope.setZero(90);
		slope.setMaximum(30);
		slope.setMinimum(-30);
		slope.setBounds(new Rectangle(77, 5, 40, 62));
		slope.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {
				slope_mouseDragged(e);
			}
		});
		head.setForeground(SystemColor.menuText);
		head.setFont(new java.awt.Font("Serif", 1, 12));
		head.setPrefix("");
		head.setZero(90);
		head.setMaximum(45);
		head.setMinimum(-45);
		head.setBounds(new Rectangle(77, 75, 40, 60));
		jPanel6.setBorder(border2);
		jPanel6.setLayout(borderLayout4);
		jPanel7.setBorder(border3);
		jPanel7.setLayout(borderLayout5);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(vestibular, null);
		jPanel3.add(neck, null);
		jPanel3.add(support, null);
		jPanel2.add(jPanel4, BorderLayout.SOUTH);
		jPanel4.add(returnButton1, null);
		jPanel2.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(label3D1, null);
		jPanel5.add(head, null);
		jPanel5.add(slope, null);
		jPanel5.add(label3D2, null);
		jPanel5.add(label3D3, null);
		jPanel1.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(graph, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	int i,j;
	void slope_change() {
	int Slope=-slope.getValue();
	body = body + Slope - oldslope;

	stheta = scaler * Math.sin(Math.PI/180 * Slope);
	ctheta = scaler * Math.cos(Math.PI/180 * Slope);

	i = 120 - (int)(140 * stheta/ctheta);
	j = 120 + (int)(160 * stheta/ctheta);
}

	void slope_mouseDragged(MouseEvent e) {
	slope_change();
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		timer.stop();
	}

}

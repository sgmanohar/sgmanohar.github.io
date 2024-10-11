package com.neurolab;
//Receptive fields by Sanjay Manohar

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.neurolab.common.ActionPotentials;
import com.neurolab.common.AngleControl;
import com.neurolab.common.ExtraGraphics;
import com.neurolab.common.JPanel0;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.PercentageBar;
import com.neurolab.common.RadioPanel;
import com.neurolab.common.ReturnButton;



public class ReceptiveFields extends NeurolabExhibit implements ActionListener {
	public String getExhibitName(){return "Receptive Fields";}
	ActionPotentials ap;
	javax.swing.Timer timer;
	int cx,cy,ntexdots=7;;
	int[] du,dv;
	public void init(){
		super.init();
		getMainContainer().setLayout(new BorderLayout());
		createComponents();
		ap=new ActionPotentials();
		timer=new javax.swing.Timer(100,this);
		ap.setRate(0);
		cx=250/2;
		cy=250/2;
		du=new int[ntexdots];
		dv=new int[ntexdots];

		timer.start();
	}

	JPanel left,center,right,cbpanel,rtpanel,rmpanel,tlpanel,rightf,rbpanel,
		textpanel,rightt, anglepanel;
	PercentageBar blbar;
	RFScreen rfscreen;
	JCheckBox s_invert,r_invert,r_transient,r_reveal;
	NamedSliderPanel s_size;
	AngleControl s_angle;
	RadioPanel s_typepanel,r_typepanel;
	final String[] s_typelist={"Extended","Circle","Annulus","Edge","Line","Textured"};
	final String[] r_typelist={"Simple large-field","Simple centre-surround",
		"Complex dot-detector","Directional large-field","Simple linear",
		"Complex linear","End-stopped linear"
	};


	public void createComponents(){
		getMainContainer().add(left=new JPanel(),BorderLayout.WEST);
		getMainContainer().add(rightf=new JPanel(),BorderLayout.CENTER);
		((BorderLayout)getMainContainer().getLayout()).setHgap(10);
		setBG(left);setBG(rightf);

		rightf.setLayout(new BorderLayout());
		rightf.add(rightt=new JPanel(),BorderLayout.NORTH);
		rightf.add(rbpanel=new JPanel(),BorderLayout.SOUTH);
		//rbpanel.setPreferredSize(new Dimension(100,30));
		setBG(rightt);setBG(rbpanel);

		rightt.setLayout(new GridLayout(1,2));
		rightt.add(center=new JPanel());
		rightt.add(right=new JPanel());
		setBG(right);setBG(center);

		rbpanel.setLayout(new BorderLayout());
		rbpanel.add(new ReturnButton(),BorderLayout.EAST);
		rbpanel.add(textpanel=new JPanel(){
			public void paint(Graphics g){
				super.paint(g);
				g.setFont(new Font("Arial",Font.BOLD,16));
				paintText3D(g,"Click/Drag in black area",0,getHeight()-8);
			}
		}, BorderLayout.WEST);
		textpanel.setPreferredSize(new Dimension(200,30));
		setBG(textpanel);

		BorderLayout b=new BorderLayout();
		b.setVgap(10);
		left.setLayout(b);
		left.add(tlpanel=new JPanel(),BorderLayout.CENTER);
		left.add(blbar=new PercentageBar(),BorderLayout.SOUTH);
		setBG(tlpanel);

		tlpanel.setBorder(loweredbevel);
		tlpanel.add(rfscreen=new RFScreen(),BorderLayout.NORTH);
		rfscreen.setPreferredSize(new Dimension(250,250));

		center.setBorder(BorderFactory.createTitledBorder(etched,"Stimulus Type"));
		center.setLayout(new BorderLayout());
		center.add(s_typepanel=new RadioPanel(s_typelist,typeaction),BorderLayout.NORTH);
		center.add(cbpanel=new JPanel(),BorderLayout.SOUTH);
		center.add(s_invert=new JCheckBox("Invert light and dark"),BorderLayout.CENTER);
		s_invert.addActionListener(typeaction);
		setBG(cbpanel);setBG(s_invert);

		cbpanel.setLayout(new BorderLayout());
		cbpanel.add(s_size=new NamedSliderPanel("Size",200,LABEL_POS_LEFT),BorderLayout.NORTH);
		cbpanel.add(anglepanel=new JPanel0(),BorderLayout.SOUTH);

		anglepanel.setPreferredSize(new Dimension(40,60));
		anglepanel.add(s_angle=new AngleControl(),BorderLayout.CENTER);
		s_angle.setPreferredSize(new Dimension(40,60));
		s_angle.setPrefix("Angle       ");


		right.setBorder(BorderFactory.createTitledBorder(etched,"Field type"));
		right.setLayout(new BorderLayout());
		right.add(r_typepanel=new RadioPanel(r_typelist,typeaction),BorderLayout.NORTH);
		right.add(rmpanel=new JPanel(),BorderLayout.CENTER);
		right.add(r_reveal=new JCheckBox("Reveal"),BorderLayout.SOUTH);
		r_reveal.addActionListener(typeaction);
		setBG(rmpanel);setBG(r_reveal);

		rmpanel.setLayout(new BorderLayout());
		rmpanel.add(r_invert=new JCheckBox("Invert +/-"),BorderLayout.NORTH);
		rmpanel.add(r_transient=new JCheckBox("Transient"),BorderLayout.SOUTH);
		setBG(r_invert);setBG(r_transient);

		r_typepanel.setSelected(0);
		s_typepanel.setSelected(1);
		r_reveal.setSelected(true);
		s_size.slider.setValue(50);
		s_angle.setValue(40);
	}
	ActionListener typeaction = new ActionListener(){public void actionPerformed(ActionEvent e){
		if((e.getSource()==s_invert)||e.getSource()==r_invert){
			showActivity(calculate());
		}else if(e.getSource()==r_reveal){
		}else{		// stimulus or field changed
			int stim,field;
			stim=s_typepanel.getSelected();
			field=r_typepanel.getSelected();
			switch(field){
				case 3:case 5:case 6:
					r_transient.setSelected(false);
					r_transient.setVisible(false);
					break;
				default:r_transient.setVisible(true);
			}
			switch(stim){
				case 1:case 2:case 4:
					s_size.setVisible(true);
					break;
				default:s_size.setVisible(false);
			}
			switch (stim){
				case 3:case 4:
					anglepanel.setVisible(true);
					synchronized(getTreeLock()) {
			      validateTree();
			    }
			    break;
				default:anglepanel.setVisible(false);
			}
		}
		rfscreen.repaint();
	}};



	double oldactivity, activity, oldf;
	double smoothrate=0.4;
	public void showActivity(double activit){
		if(false)System.out.println("Activity="+String.valueOf(activity));
		activity=smoothrate*activity+(1-smoothrate)*activit;
		if(Math.abs(activity)<1e-4) activity=0;
		double f=activity;
		boolean si = s_invert.isSelected(), ri = r_invert.isSelected(); 
		if(r_transient.isSelected())f=0.5 + 4*(activity-oldactivity) + (0.05*activity-1)*0.45;
    if(si) f = -f; 
    if(ri) f = -f;
    if(r_typepanel.getSelected()==0 && (ri ^ si)) f+=1;  
		oldactivity=activity;
		blbar.p=(int)(100*f);
		oldf=f;
		blbar.repaint();	// percentage bar
		ap.setRate(f*20);	// 0-20 Hz clicks
	}
	double l_act,d_act;
	public void actionPerformed(ActionEvent e){	// is timer event
		showActivity(calculate());
	}



	float annulusf=0.6f;
	int outerrf=120,innerrf=32;
	int u,v,ou,ov;

	public double calculate(){
		float act=0;
		ou=u;ov=v;u=rfscreen.ox-cx;v=rfscreen.oy-cy;	// move to new point
		int field=r_typepanel.getSelected();
		int size=s_size.slider.getValue();
		double angle=s_angle.getValue()*Math.PI/180;
		if(rfscreen.pressing){	// stimulus is on
			switch(s_typepanel.getSelected()){
				case 0:	//extended
					if(field==0)return 1;
					break;
				case 1:	//circle
					return circleinfield(size,field,Math.sqrt(u*u+v*v));
				case 2:	//annulus
					if(field==2)return 0;
					double d=Math.sqrt(u*u+v*v);
					return circleinfield(size,field,d)-circleinfield(annulusf*size,field,d);
				case 3:	//edge
					double proj=Math.abs(u*Math.cos(angle)
									+v*Math.sin(angle));	//?line coordinate 1
				switch(field){
					case 0:	//large
						return 0.5+limit((75-proj)/outerrf,-0.5,0.5);
					case 1:	//concentric
						return limit((75-proj)/innerrf,-0.5,0.5)-limit((75-proj)/outerrf,-0.5,0.5);
					case 4:	//simple linear
						if((angle>80*Math.PI/180)&&(angle<100*Math.PI/180))
							return -limit((Math.abs(v)-75+25)/50,0,1)
										+limit((Math.abs(v)-75+8 )/16,0,1);
						break;
					}
					break;
				case 4:	//line
				//	double proj=Math.abs(u*Math.cos(angle)
				//	    +v*Math.sin(angle));	//?line coordinate 1
				switch(field){
					case 0:	//large
						return 0.003*intrusion(outerrf/2,size,angle);
					case 1:	//concentric
						return 0.01*intrusion(innerrf/2,size,angle)-0.003*intrusion(outerrf/2,size,angle);
					case 2:	//complex
						if(size<18)return 0.01*intrusion(50,size,angle);
						break;
					case 4:	//simple
						return lintrusion(8,size,angle)-lintrusion(25,size,angle);
					case 5:	//complex linear
						if( ((angle> 80*Math.PI/180)&&(angle<100*Math.PI/180)) ||
								((angle>260*Math.PI/180)&&(angle<280*Math.PI/180)) )
							if(Math.abs(v)<70)
								return 0.04 * (v - ov) * limit((40 - Math.abs(u) + size) / 80, 0, 1);
						break;
					case 6:	//end-stopped linear
						if( ((angle> 80*Math.PI/180)&&(angle<100*Math.PI/180)) ||
								((angle>260*Math.PI/180)&&(angle<280*Math.PI/180)) )
							if((Math.abs(v)<70)&&(u-size>-80)&&(u+size<80))
								return 0.001*size*(v-ov);
					}
					break;
				case 5:	//texture
				switch(field){
					case 0:	//large
						for(int i=0;i<ntexdots;i++)
							if(((u+du[i])*(u+du[i])+(v+dv[i])*(v+dv[i]))
								<(outerrf/2)*(outerrf/2))
								act+=0.04;
						break;
					case 1:	//concentric
						double dist;
						for(int i=0;i<ntexdots;i++){
							dist=(u+du[i])*(u+du[i])+(v+dv[i])*(v+dv[i]);
							if(dist<(innerrf/2)*(innerrf/2))
								act+=0.25;
							else if(dist<(outerrf/2)*(outerrf/2))
								act-=0.08;
						}
						break;
					case 2:	//complex spot
						for(int i=0;i<ntexdots;i++)
							if((u+du[i])*(u+du[i])+(v+dv[i])*(v+dv[i])<55*55)
								return 1;
						break;
					case 3:	//directional large
						return 0.05*(u-ou);
					case 4:	//simple linear
						for(int i=0;i<ntexdots;i++)
							if(((v+dv[i])>-8)&&((v+dv[i])<8))
								act+=0.25;
							else if(((v+dv[i])>-25)&&((v+dv[i])<25))
								act-=0.08;
						break;
				}
					break;
			}
		}
		return act;
	}
	public double limit(double v,double l,double h){
		if(v<l)return l;if(v>h)return h;
		return v;
	}
	public double acos(double f){return Math.acos(f);}
	public double limu(double a,double b){
		return (a<b)?a:b;
	}
	public double overlap(double d1,double d2,double dist){
		return 0.01*limu(d1,d2)*limit((d1+d2-2*dist)/(d1+d2-Math.abs(d1-d2)),0,1);
	}
	public double segment(double zz,double r){	// zz=distance to chord
		if(Math.abs(zz)>r){
			if(zz<0)return 0;	// no overlap
			else return Math.PI*r*r;//full overlap
		}else{
			double triangle=zz*Math.sqrt(r*r-zz*zz);
			if(zz<0)return r*r*acos(-zz/r)
					+triangle;
			else return r*r*(Math.PI-acos(zz/r))
					+triangle;
		}
	}
	public double intrusion(double rad,double size,double angle){
		double dx,dy,dz,ww,l1,l2,x1,y1;
		dy=size*Math.cos(-angle);dx=size*Math.sin(-angle);
	//	if(dx<0){dx*=-1;dy*=-1;}
		x1=u-dx/2;y1=v-dy/2;
		dz=dx*x1+dy*y1;
		ww=dz*dz-size*size*(x1*x1+y1*y1-rad*rad);
		if(ww<=0)return 0;
		l1=(-dz-Math.sqrt(ww))/(size);
		l2=(-dz+Math.sqrt(ww))/(size);
		return limit(l2,0,size)-limit(l1,0,size);
	}
	public double lintrusion(double a,double size,double angle){
		double cos=Math.cos(angle);
		if(Math.abs(cos)>0.05)
			return limit((Math.abs(cos*size/2)+1-Math.abs(v))/(2*a),0,1);
		else
			if(Math.abs(v)<a)return 8d*size/(100+a);
		return 0;//What????
	}
	public double circleinfield(double size,int f,double d){
		switch(f){
			case 0:	//large
				return overlap(120,size,d);
			case 1:	//concentric
				return overlap(32, size,d) - 0.27 * overlap(120, size,d);
			case 2:	//complex spot
				if((size<22)&&(d<50))return 1;
				break;
			case 4:	//simple linear
				return 0.003*(segment(8+v ,size/2)-segment(v-8,size/2))
					 -0.001005*(segment(v+25,size/2)-segment(v-25,size/2));
		}
		return 0;
	}
	public class RFScreen extends JPanel implements MouseMotionListener, MouseListener{
		public int ox,oy; 
		public RFScreen(){ 
			super();
			setBorder(loweredbevel);
			addMouseMotionListener(this);
			addMouseListener(this);
		}
		public void paint(Graphics g){
			super.paint(g);
			fillMe(g,getBackColor());
//Antialiased sgm 31/8/01
			antiAlias(g);
			g.setColor(Color.green);
			drawRF(g);
		}
		public void fillMe(Graphics g,Color c){
			g.setColor(c);
			g.fillRect(2,2,getWidth()-4,getHeight()-4);
		}
		public void drawRF(Graphics g){
			if(r_reveal.isSelected()){
				int cx=getWidth()/2,cy=getHeight()/2;
				g.setColor(Color.green);
				switch(r_typepanel.getSelected()){
					case 1:
						g.drawOval(cx-15,cy-15,32,32);		// small circle
					case 0: case 2:
						g.drawOval(cx-60,cy-60,120,120);	// large circle
						break;
					case 3:
						g.drawOval(cx-60,cy-60,120,120);
						g.drawLine(cx-40,cy, cx+40, cy);	// arrow right
						g.drawLine(cx+40,cy,cx+30,cy-10);
						g.drawLine(cx+40,cy,cx+30,cy+10);
						break;
					case 4:
					int l_ht[]={-25,-8,8,25};
						for(int i=0;i<4;i++)g.drawLine(2,cy+l_ht[i],getWidth()-2,cy+l_ht[i]);
						g.drawLine(2,cy+l_ht[0],2,cy+l_ht[3]);
						g.drawLine(getWidth()-2,cy-l_ht[0],getWidth()-2,cy+l_ht[3]);
						break;
					case 5: case 6:
						g.drawRect(cx-40,cy-60,80,120);
						g.drawLine(cx,cy-40,cx,cy+40);		// arrow down
						g.drawLine(cx,cy+40,cx-10,cy+30);
						g.drawLine(cx,cy+40,cx+10,cy+30);
						break;
				}
			}
		}

		public Color getForeColor(){
			return (s_invert.isSelected())?Color.black:Color.white;
		}
		public Color getBackColor(){
			return (s_invert.isSelected())?Color.white:Color.black;
		}
		public void mouseEntered(MouseEvent e){}
		public void mouseMoved(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}

		Shape stim_shape;
		boolean pressing=false;
		public void mousePressed(MouseEvent e){
			int x=e.getX(),y=e.getY();
			int size=s_size.slider.getValue();
			switch(s_typepanel.getSelected()){
				case 0:	// extended= square 3*screensize
					stim_shape=new GeneralPath(new Rectangle(-getWidth(),-getHeight()
								,getWidth()*3,getHeight()*3) );
					break;
				case 1:	// circle diam size
					stim_shape=new GeneralPath(new Ellipse2D.Float(x-size/2,y-size/2,size,size));
					break;
				case 2:	// annulus, radius inner=0.6*outer
				  GeneralPath p;
					stim_shape=p=new GeneralPath(new Ellipse2D.Float(x-size/2,y-size/2,size,size));
					p.setWindingRule(GeneralPath.WIND_EVEN_ODD);
					p.append(new Ellipse2D.Float(
						x-annulusf*size/2, y-annulusf*size/2, annulusf*size, annulusf*size), true);
					break;
				case 3:	// edge = rectangle 700 x 140
					stim_shape=AffineTransform.getRotateInstance(
						(s_angle.getValue()+90)*Math.PI/180,x,y)
						.createTransformedShape(
						new Rectangle(x-350,y-70,700,140)
						);
					break;
				case 4:	// line width 4
					stim_shape=AffineTransform.getRotateInstance(
						(s_angle.getValue()+90)*Math.PI/180,x,y)
						.createTransformedShape(
						new Rectangle(x-size/2,y-2,size,4)
						);
					break;
				case 5:	// 7 * random 4x4 squares
					Shape square=new Rectangle(x,y,4,4);
					GeneralPath gp;
					gp=new GeneralPath(square);
					Random r=new Random(0);
					for(int i=0;i<ntexdots;i++){
						du[i]=r.nextInt()%50;
						dv[i]=r.nextInt()%50;
						gp.append(AffineTransform.getTranslateInstance(du[i],dv[i])
							.createTransformedShape(square), true);
					}
					stim_shape=gp;
			}
			Graphics g=getGraphics();
			ox=x;oy=y;
			g.setColor(getForeColor());
			g.clipRect(2,2,getWidth()-4,getHeight()-4);	// color and clip
//Antialiased sgm 16/12/01
			antiAlias(g);
			doFillShape(g,stim_shape);	// draw stime
			drawRF(g);		// redraw RF
			pressing=true;
			showActivity(calculate());
		}
		public void mouseDragged(MouseEvent e){
				Graphics g=getGraphics();
				g.clipRect(2,2,getWidth()-4,getHeight()-4);
				g.setColor(getBackColor());
				doFillShape(g,stim_shape);	// erase stim
				doFillShape(g,stim_shape.getBounds()); //@todo what is this?

				int dx=e.getX()-ox,dy=e.getY()-oy;
//Antialiased sgm 16/12/01
				antiAlias(g);

				stim_shape=AffineTransform.getTranslateInstance(dx,dy).createTransformedShape(stim_shape);
				//stim_shape.transform(AffineTransform.getTranslateInstance(dx,dy));
				g.setColor(getForeColor());
				doFillShape(g,stim_shape);	// redraw stim
				drawRF(g);		// redraw RF
				ox=e.getX();oy=e.getY();
				showActivity(calculate());
		}
		public void mouseReleased(MouseEvent e){
			pressing=false;
			Graphics g=getGraphics();
			g.setColor(getBackColor());
			g.clipRect(2,2,getWidth()-4,getHeight()-4);
//Antialiased sgm 16/12/01
			antiAlias(g);
			doFillShape(g,stim_shape);	// erase stim
			doFillShape(g,stim_shape.getBounds());


			drawRF(g);		// redraw RF
			showActivity(calculate());
		}
	}
	public void finalize() throws Throwable{
		close();
		super.finalize();
	}
	public void close(){
		if(timer!=null)timer.stop();
		if(ap!=null && ap.timer!=null) ap.timer.stop();
	}

/*  public ReceptiveFields() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	AngleControl angleControl1 = new AngleControl();

	private void jbInit() throws Exception {
		this.setLayout(null);
		angleControl1.setBounds(new Rectangle(198, 61, 45, 82));
		this.add(angleControl1, null);
	}
*/

	void doFillShape(Graphics g, Shape p){
          ExtraGraphics.doFillShape(g, p);
	}
	void doFillShape(Graphics g, Rectangle r){
          ExtraGraphics.doFillShape(g, r);
	}
}

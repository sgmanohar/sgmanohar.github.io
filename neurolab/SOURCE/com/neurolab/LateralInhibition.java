

package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

public class LateralInhibition extends NeurolabExhibit implements ImageObserver{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JButton strip = new JButton();
	JButton cornsweet = new JButton();
	ReturnButton returnButton1 = new ReturnButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel jPanel4 = new JPanel();
	JLabel jLabel1 = new JLabel();
	FlowLayout flowLayout1 = new FlowLayout();
	JSlider completeness = new JSlider();
	Box box1;
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel8 = new JPanel();
	JPanel jPanel9 = new JPanel();
	JPanel jPanel10 = new JPanel();
	Border border1;
	Border border2;
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JButton jButton3 = new JButton();
	JButton jButton4 = new JButton();
	JButton jButton5 = new JButton();
	JButton jButton6 = new JButton();
	JButton jButton7 = new JButton();
	JButton jButton8 = new JButton();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JPanel jPanel11 = new JPanel();
	JPanel jPanel7 = new JPanel();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel12 = new JPanel();
	Border border3;
	TitledBorder titledBorder1;
	JRadioButton singleline = new JRadioButton();
	JRadioButton edge = new JRadioButton();
	JRadioButton pairoflines = new JRadioButton();
	GridLayout gridLayout1 = new GridLayout();
	ButtonGroup bg = new ButtonGroup();
	Border border4;
	JPanel jPanel13 = new JPanel();
	BorderLayout borderLayout7 = new BorderLayout();
	Border border5;
	JPanel jPanel14 = new JPanel();
	BorderLayout borderLayout8 = new BorderLayout();
	Border border6;
	BorderLayout borderLayout9 = new BorderLayout();
	BorderLayout borderLayout10 = new BorderLayout();
	int baseh=40;
	JPanel linegraphic = new JPanel(){
	public void paint(Graphics g){
		super.paint(g);
		antiAlias(g);
			//baseline
		int base=getHeight()-baseh;
		g.setColor(Color.gray);
		g.drawLine(0,base,getWidth(),base);
			//stimulus
		g.setColor(Color.blue);
		int mid=getWidth()/2;
		if(singleline.isSelected() || edge.isSelected()){
			g.drawLine(mid,base,mid,0);
		}
		if(edge.isSelected()){
			g.drawLine(0,base,mid,base);
			g.drawLine(mid,0,getWidth(),0);
		}else if(pairoflines.isSelected()){
			g.drawLine(mid-linespacing*NPIXEL,base,mid-linespacing*NPIXEL,0);
			g.drawLine(mid+linespacing*NPIXEL,base,mid+linespacing*NPIXEL,0);
		}
			//result
		if(linedata==null)return;
		g.setColor(Color.green);
		synchronized(linedata){
			for(int i=1;i<linedata.length;i++){
				g.drawLine((i-1)*NPIXEL,base-(int)(base*linedata[i-1]),i*NPIXEL,base-(int)(base*linedata[i]));
			}
		}
	}
	};
	final int NPIXEL=4;
	int linespacing=4;
	JPanel imagegraphic = new JPanel(){
	public void paint(Graphics g){
		super.paint(g);
		if(image!=null)g.drawImage(image,0,0,this);
	}
	};
	double[] linedata;
	public String getExhibitName(){return "Lateral Inhibition";}
	String[] imagename=new String[]{"resources/lat_inh.jpg","resources/testimage1.jpg"};
	BufferedImage image;
	Image defaultimage;

	public LateralInhibition() {
	}
	Point oldmouse;
	double sgn(double a){return a/Math.abs(a);}

	public void init(){
		super.init();
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	linegraphic.addComponentListener(new ComponentAdapter(){
		public void componentResized(ComponentEvent e){
			linedata=new double[linegraphic.getWidth()/NPIXEL];
			setupLinedata();
		}
	});
	imagegraphic.addComponentListener(new ComponentAdapter(){
		public void componentResized(ComponentEvent e){
			createimage();
		}
	});
	linegraphic.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			oldmouse=e.getPoint();
		}
	});
	linegraphic.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(linedata!=null){
				double m=(e.getY()-oldmouse.y)/(e.getX()-oldmouse.x);
				int dir=(int)sgn(e.getX()-oldmouse.x);
				for(int i=0;i!=e.getX()-oldmouse.x; i+=dir ){
					if(oldmouse.x+i<0 || oldmouse.x+i>linedata.length-1)break;
					double h=(oldmouse.y+m*i);
					linedata[oldmouse.x+i]=1-h/(linegraphic.getHeight()-baseh);
				}
			}
			linegraphic.repaint();
			oldmouse=e.getPoint();
		}
	});
	imagegraphic.addMouseListener(new MouseAdapter(){
		public void mouseClicked(MouseEvent e){
		  if(e.getClickCount()>1) {
  			imageindex = ++imageindex % imagename.length;
  			loadimage();
		  }
		}
	});
	loadimage();
	initkernel();
	}
	int imageindex=0;
	void loadimage(){
			//ask to get width and height of image, returned through imageupdate
		defaultimage=getImage(imagename[imageindex]);
		int x=defaultimage.getWidth(this);
		int y=defaultimage.getHeight(this);
		if(x>0 && y>0)createimage();
	}
		//imageobserver implementation --waiting for data to arrive
	public boolean imageUpdate(Image i, int f, int x, int y, int w, int h){
		if((f&ImageObserver.ALLBITS)==0)return true;
		createimage();
		return false;
	}

	void createimage(){
			//create new blank image
		if(imagegraphic.getWidth()>0 && imagegraphic.getHeight()>0){
			image=new BufferedImage(imagegraphic.getWidth(),imagegraphic.getHeight(),BufferedImage.TYPE_INT_RGB);
				//draw defaultimage onto image scaled to fit
			image.getGraphics().drawImage(defaultimage,0,0,image.getWidth(this),image.getHeight(this),this);
			imagegraphic.repaint();
		}
	}
	public void setupLinedata(){	//setup the different stimuli in linedata
		boolean isedge=edge.isSelected(),is1=singleline.isSelected(),is2=pairoflines.isSelected();
		int w=linedata.length;
		double p=0;
		for(int i=0;i<w;i++){
			if(isedge && i>w/2)p=0.8;
			else if(is1 && i==w/2)p=1;
			else if(is2 && (i==w/2-linespacing || i==w/2+linespacing)) p=1;
			else p=0;
			linedata[i]=p;
		}
	}
	private void jbInit() throws Exception {
	jButton6.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				linecommand(e);
			}
		});
		jButton7.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				linecommand(e);
			}
		});
		jButton8.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				linecommand(e);
			}
		});
		jButton5.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				imagecommand(e);
			}
		});
		jButton4.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				imagecommand(e);
			}
		});
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				imagecommand(e);
			}
		});
		strip.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				strip_actionPerformed(e);
			}
		});
		cornsweet.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cornsweet_actionPerformed(e);
			}
		});
		singleline.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchange(e);
			}
		});
		edge.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchange(e);
			}
		});
		pairoflines.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stimchange(e);
			}
		});
		linegraphic.setBackground(Color.black);
		linegraphic.setForeground(Color.green);
		jPanel4.setBackground(Color.lightGray);
		completeness.setBackground(Color.lightGray);
		strip.setBackground(Color.lightGray);
		cornsweet.setBackground(Color.lightGray);
		jPanel3.setBackground(Color.lightGray);
		jPanel8.setBackground(Color.lightGray);
		jButton5.setBackground(Color.lightGray);
		jButton4.setBackground(Color.lightGray);
		jButton3.setBackground(Color.lightGray);
		jButton6.setBackground(Color.lightGray);
		jButton7.setBackground(Color.lightGray);
		jButton8.setBackground(Color.lightGray);
		jPanel11.setBackground(Color.lightGray);
		jPanel12.setBackground(Color.lightGray);
		jPanel9.setBackground(Color.lightGray);
		singleline.setBackground(Color.lightGray);
		edge.setBackground(Color.lightGray);
		pairoflines.setBackground(Color.lightGray);
		jPanel5.setBackground(Color.lightGray);
		jPanel6.setBackground(Color.lightGray);
		jPanel10.setBackground(Color.lightGray);
		jPanel13.setBackground(Color.lightGray);
		imagegraphic.setBackground(Color.lightGray);
		bg.add(singleline);bg.add(edge);bg.add(pairoflines);
		box1 = Box.createHorizontalBox();
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2));
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2));
		border3 = BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(border3,"Stimulus");
		border4 = BorderFactory.createEmptyBorder(4,4,4,4);
		border5 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		border6 = BorderFactory.createEmptyBorder(4,4,4,4);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		strip.setText("Strip illusion");
		cornsweet.setText("Cornsweet illusion");
		jPanel3.setPreferredSize(new Dimension(150, 103));
		jPanel3.setLayout(gridBagLayout1);
		jPanel4.setLayout(flowLayout1);
		jLabel1.setText("Completeness");
		jPanel4.setPreferredSize(new Dimension(100, 34));
		completeness.setValue(80);
		completeness.setPreferredSize(new Dimension(120, 24));
		jPanel5.setBorder(border1);
		jPanel5.setLayout(borderLayout4);
		jPanel6.setBorder(border2);
		jPanel6.setLayout(borderLayout5);
		jPanel8.setLayout(gridBagLayout2);
		jButton3.setText("Restore");
		jButton4.setText("Lateral inhibition");
		jButton5.setText("Blur");
		jButton6.setText("Blur");
		jButton7.setText("Lateral inhibition");
		jButton8.setText("Restore");
		jPanel11.setLayout(gridBagLayout3);
		jPanel7.setLayout(borderLayout6);
		jPanel12.setBorder(titledBorder1);
		jPanel12.setPreferredSize(new Dimension(40, 63));
		jPanel12.setLayout(gridLayout1);
		singleline.setSelected(true);
		singleline.setText("Single line");
		edge.setText("Edge");
		pairoflines.setText("Pair of lines");
		gridLayout1.setColumns(1);
		gridLayout1.setRows(3);
		jPanel10.setBorder(border4);
		jPanel10.setLayout(borderLayout7);
		jPanel13.setBorder(border5);
		jPanel13.setLayout(borderLayout10);
		jPanel14.setBorder(border5);
		jPanel14.setLayout(borderLayout9);
		jPanel9.setLayout(borderLayout8);
		jPanel9.setBorder(border6);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(cornsweet, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
		jPanel3.add(returnButton1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
		jPanel3.add(strip, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 4, 0), 0, 0));
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jLabel1, null);
		jPanel4.add(completeness, null);
		jPanel1.add(box1, BorderLayout.CENTER);
		box1.add(jPanel5, null);
		jPanel5.add(jPanel9, BorderLayout.CENTER);
		jPanel9.add(jPanel14, BorderLayout.CENTER);
		jPanel14.add(linegraphic, BorderLayout.CENTER);
		jPanel5.add(jPanel7, BorderLayout.SOUTH);
		jPanel7.add(jPanel11, BorderLayout.EAST);
		jPanel11.add(jButton7, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel11.add(jButton6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel11.add(jButton8, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel7.add(jPanel12, BorderLayout.CENTER);
		((BorderLayout)jPanel7.getLayout()).setHgap(10);
		setBG(jPanel7);
		jPanel12.add(singleline, null);
		jPanel12.add(edge, null);
		jPanel12.add(pairoflines, null);
		box1.add(jPanel6, null);
		jPanel6.add(jPanel8, BorderLayout.SOUTH);
		jPanel8.add(jButton4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel8.add(jButton5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel8.add(jButton3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
						,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		jPanel6.add(jPanel10, BorderLayout.CENTER);
		jPanel10.add(jPanel13, BorderLayout.CENTER);
		jPanel13.add(imagegraphic, BorderLayout.CENTER);
		getMainContainer().setLayout(borderLayout1);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
	}

	double[] kernelB=new double[]{1,2,4,6,7,6,4,2,1};
	double[] kernelL=new double[]{-1,-2,-4,-6,-7,41,-7,-6,-4,-2,-1};
	int RK=4;
	double PV=7;
	double gauss(double a){return Math.exp(-a*a);}
	float[] kdataL=new float[]{
		-1, -1, -1,
		-1,  9, -1,
		-1, -1, -1
	};	//lateral inh
	float[] kdataB=new float[]{
		.1f,  .1f,  .1f,
		.1f,  .2f,  .1f,
		.1f,  .1f,  .1f
	};	//blur
	float[] kdataI=new float[]{0,0,0,0,1,0,0,0,0};
		//identity
	Kernel ikL=new Kernel(3,3,kdataL);
	Kernel ikB=new Kernel(3,3,kdataB);
	void createopL(){
		float[] d=new float[9];
		double c=completeness.getValue()/100.;
		for(int i=0;i<9;i++)d[i]=kdataL[i]*(float)c+(1-(float)c)*kdataI[i];
		ikL=new Kernel(3,3,d);
		opL=new ConvolveOp(ikL);
	}
	ConvolveOp opB=new ConvolveOp(ikB), opL=new ConvolveOp(ikL);
	void initkernel(){
		kernelB=new double[1+RK*2];
		kernelL=new double[1+RK*2];
		double sum=0;
		for(int i=1;i<=RK;i++){
			double d=i*2/(double)RK;	//from 0 to 3 across filter
			kernelB[RK+i]=PV*gauss(d);
			kernelB[RK-i]=PV*gauss(d);
			kernelL[RK+i]=-PV*gauss(d);
			kernelL[RK-i]=-PV*gauss(d);
			sum+=2*PV*gauss(d);
		}
		kernelB[RK]=PV;
		kernelL[RK]=sum;
	}
	public double[] convolve(double[] source, double[] kernel, double scale, double completeness){
		double[] ndata=new double[linedata.length];
		int kcentre=kernel.length/2;
		double t=0;for(int i=0;i<kernel.length;i++)t+=kernel[i];
		//t/=2;		//area ratio of each column
		t+=scale;
		for(int i=0;i<linedata.length;i++){
			ndata[i]=0;
			for(int j=0;j<kernel.length;j++){
				int r=Math.min(Math.max(i+j-kcentre,0),linedata.length-1);
				ndata[i]+=kernel[j]*linedata[r];
			}
			if(completeness<1)ndata[i]=ndata[i]*completeness + t*linedata[i]*(1-completeness);
			ndata[i]/=t;
		}
		return ndata;
	}
	void linecommand(ActionEvent e) {
	String s=e.getActionCommand();
	switch(commandInt(s)){
		case BLUR:
			linedata=convolve(linedata,kernelB,0,0.8*completeness.getValue()/100.);
			break;
		case LATIN:
			linedata=convolve(linedata,kernelL,20,completeness.getValue()/100.);
			break;
		case RESTORE:
			setupLinedata();
			break;
	}
	linegraphic.repaint();
	}
	static final int BLUR=0,LATIN=1,RESTORE=2;
	public int commandInt(String s){
	if(s.equals("Blur"))return BLUR;
	else if(s.equals("Lateral inhibition"))return LATIN;
	else if(s.equals("Restore"))return RESTORE;
	return -1;
	}

	void imagecommand(ActionEvent e) {
	switch(commandInt(e.getActionCommand())){
		case BLUR:
				//create new image
			image=opB.filter(image,opB.createCompatibleDestImage(
				image,image.getColorModel() ));
				//perform filtering
			imagegraphic.repaint();
			break;
		case LATIN:
			createopL();
			image=opL.filter(image,opB.createCompatibleDestImage(
				image,image.getColorModel() ));
			imagegraphic.repaint();
			break;
		case RESTORE:
			image.getGraphics().drawImage(defaultimage,0,0,image.getWidth(this),image.getHeight(this),this);
				//repaint defaultimage on image
			break;
	}
	imagegraphic.repaint();
	}

	void strip_actionPerformed(ActionEvent e) {
	getHolder().setExhibit("com.neurolab.StripIllusion");
	}

	void cornsweet_actionPerformed(ActionEvent e) {
	getHolder().setExhibit("com.neurolab.CornsweetIllusion");
	}

	void stimchange(ActionEvent e) {
	setupLinedata();
	linegraphic.repaint();
	}

  public void close(){}

}
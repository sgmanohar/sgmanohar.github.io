/**
 * Iron filings
 * rough and ready, not yet commented or tidied up!
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.Timer;

public class IronFilingsExhibit extends CudosExhibit {
	public String getExhibitName(){return "Iron Filings";}
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JButton jButton1 = new JButton();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	JToggleButton testbutton = new JToggleButton();
	private Cursor cursor1 = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	JPanel display = new JPanel(){
		public void paint(Graphics g_){
			super.paint(g_);
			Graphics2D g=(Graphics2D)g_;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				//draw filings
			for(int i=0;i<filings.size();i++){
				Filing p=(Filing)filings.get(i);
				double len=(p.magn==0)?0:length*p.magn*50;
				len=Math.min(len,10);
				g.drawLine(p.x,p.y,
					p.x + (int)(len*Math.cos(p.angle)),
					p.y + (int)(len*Math.sin(p.angle)) );
			}
				//draw poles
			for(int i=0;i<charges.size();i++){
				Charge c=(Charge)charges.get(i);
				if(c.visible){
					g.setColor(c.q>0?Color.red:Color.blue);
					g.fillOval(c.x-10,c.y-10,20,20);
					if(c==dragging){
						g.setColor(Color.yellow);
						g.setStroke(new BasicStroke(3));
						g.drawOval(c.x-10,c.y-10,20,20);
					}
				}
			}
		}
	};

	int length=15;
	int droprate=2;
	boolean polesvisible=true;
	Vector charges=new Vector();
	Vector filings=new Vector();
	Timer timer=new Timer(80,new ActionListener(){
		public void actionPerformed(ActionEvent e){
//			calc();
			if(addingfilings){
				for(int z=0;z<droprate;z++){
					Filing f=new Filing();
					f.setLocation(	mouse.x+(int)(50*Math.random()-25),
							mouse.y+(int)(50*Math.random()-25) );
					filings.add(f);
				}
				calc();
			}
		}
	});

	public void calc(){
		double top,bottom,rcube;
		Charge p;
		Filing t;
		for(int i=0;i<filings.size();i++){
				//current point
			t=(Filing)filings.get(i);
			top=bottom=0;
			for(int k=0;k<charges.size();k++){
				p=(Charge)charges.get(k);	//charge location
				rcube=Math.pow(t.distance(p),3);
				top+=p.q*(p.getY()-t.getY())/rcube;
				bottom+=p.q*(p.getX()-t.getX())/rcube;
			}
			t.angle=Math.atan2(top,bottom);	//angle 0..2*PI
			t.magn=Math.sqrt(Math.sqrt(top*top+bottom*bottom));
		}
		display.repaint();
	}

	Point2D dragging;
	Border border1;
	JPanel jPanel3 = new JPanel();
	JButton jButton3 = new JButton();
	JButton jButton2 = new JButton();
	JButton deletepolebutt = new JButton();

	boolean addingfilings=false;
	Point mouse=null;
	JButton jButton5 = new JButton();
	JPanel jPanel4 = new JPanel();
	Border border2;
	TitledBorder titledBorder1;
	JTextPane instructions = new JTextPane();
	JCheckBox polesvisiblecb = new JCheckBox();

	public IronFilingsExhibit() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	timer.start();
	display.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			for(int i=0;i<charges.size();i++){
				Charge p=(Charge)charges.get(i);
				if( p.distance(e.getPoint())<20 ){
					if(testing && !p.visible){
						p.visible=true;
						if(istestdone())endatest();
					}
					dragging=p;
					deletepolebutt.setEnabled(true);
					repaint();
					return;
				}
			}
			dragging=null;
			deletepolebutt.setEnabled(false);
			repaint();
			mouse=e.getPoint();
			addingfilings=true;
		}
		public void mouseReleased(MouseEvent e){
//			dragging=null;
			addingfilings=false;
		}
	});
	display.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(dragging!=null){
				dragging.setLocation(e.getPoint());
				calc();
			}else{
				mouse=e.getPoint();
			}
		}
	});
	}
	class Charge extends Point{
		double q;	//charge
		boolean visible=true;
	}
	class Filing extends Point{
		double angle;
		double magn;
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93)),BorderFactory.createEmptyBorder(2,2,2,2));
		border2 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
		titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134)),"Create pole");
		this.getContentPane().setLayout(borderLayout1);
		jButton1.setText("Exit");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout2);
		jPanel1.setBorder(border1);
		jPanel1.setLayout(borderLayout3);
		jButton3.setBackground(Color.blue);
		jButton3.setFont(new java.awt.Font("Dialog", 1, 16));
		jButton3.setText("-");
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jButton2.setBackground(Color.red);
		jButton2.setFont(new java.awt.Font("Dialog", 1, 16));
		jButton2.setText("+");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		deletepolebutt.setEnabled(false);
		deletepolebutt.setActionCommand("Delete");
		deletepolebutt.setText("Delete pole");
		deletepolebutt.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jPanel3.setPreferredSize(new Dimension(121, 37));
		display.setBackground(new Color(222, 222, 202));
		display.setCursor(cursor1);
		jButton5.setActionCommand("Clear");
		jButton5.setText("Clear filings");
		jButton5.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				command(e);
			}
		});
		jPanel4.setBorder(titledBorder1);
		instructions.setText(instructionstring);
		instructions.setFont(new java.awt.Font("Dialog", 0, 12));
		polesvisiblecb.setSelected(true);
		polesvisiblecb.setText("Poles visible");
		polesvisiblecb.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				polesvisibilitychange(e);
			}
		});
		testbutton.setBackground(Color.yellow);
		testbutton.setText("Test");
		testbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				testbutton_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(display, BorderLayout.CENTER);
		jPanel1.add(instructions, BorderLayout.SOUTH);
		this.getContentPane().add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jButton1, BorderLayout.SOUTH);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jPanel4, null);
		jPanel4.add(jButton2, null);
		jPanel4.add(jButton3, null);
		jPanel3.add(deletepolebutt, null);
		jPanel3.add(jButton5, null);
		jPanel3.add(polesvisiblecb, null);
		jPanel3.add(testbutton, null);
	}


        public Charge createPole(int charge, int x, int y){
          Charge c = new Charge();
          c.q=charge;
          c.setLocation(x,y);
          if(!polesvisiblecb.isSelected())c.visible=false;
          charges.add(c);
          calc();
          return c;
        }
	void command(ActionEvent e) {
	String s=e.getActionCommand();
	if(s.equals("+") || s.equals("-")){
		Charge c=new Charge();
		c.setLocation(100+(int)(100*Math.random()),100+(int)(100*Math.random()));
		c.q=(s.equals("+"))?1:-1;
		if(!polesvisiblecb.isSelected())c.visible=false;
		charges.add(c);
		calc();
	}
	if(s.equals("Delete") && dragging!=null){
		charges.remove(dragging);
		deletepolebutt.setEnabled(false);
		calc();
	}
	if(s.equals("Clear")){
		filings.removeAllElements();
		calc();
	}
	}

	void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
	}

	void polesvisibilitychange(ChangeEvent e) {
	if(polesvisiblecb.isSelected())visiblepoles();else invisiblepoles();
	display.repaint();
	}
	boolean testing=false;
	String teststring="Drop iron filings to find all the hidden poles",
		instructionstring="Hold down the mouse to drop iron filings";

	public void setupatest(){
		testing=true;
		instructions.setText(teststring);
		polesvisiblecb.setEnabled(false);
		filings.removeAllElements();
		charges.removeAllElements();
		int nmag=1+(int)(4*Math.random());
		for(int i=0;i<nmag;i++){
			Charge c=new Charge();
			if(Math.random()>0.5)c.q=1;else c.q=-1;
			c.x=(int)(display.getWidth()*Math.random());
			c.y=(int)(display.getHeight()*Math.random());
			charges.add(c);
		}
		invisiblepoles();
	}
	public void invisiblepoles(){
		for(int i=0;i<charges.size();i++) ((Charge)(charges.get(i))).visible=false;
		display.repaint();
	}
	public void visiblepoles(){
		for(int i=0;i<charges.size();i++) ((Charge)(charges.get(i))).visible=true;
		display.repaint();
	}
	public boolean istestdone(){
		for(int i=0;i<charges.size();i++)
			if(!((Charge)charges.get(i)).visible)return false;
		return true;
	}
	public void endatest(){
		testing=false;
		int found=0;
		for(int i=0;i<charges.size();i++)
			if(((Charge)charges.get(i)).visible)found++;
		instructions.setText("You found "+found+" of "+charges.size()+" poles!");
		visiblepoles();
		testbutton.setSelected(false);
		polesvisiblecb.setEnabled(true);
		display.repaint();
	}

	void testbutton_actionPerformed(ActionEvent e) {
	if(testbutton.isSelected())setupatest();
	else endatest();
	}
}

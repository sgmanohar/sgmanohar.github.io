
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos;

import com.cudos.common.*;
import java.awt.*;
import javax.swing.*;
import com.cudos.circuit.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.awt.image.ImageObserver;

public class CircuitExhibit extends CudosExhibit {

	static final String[] componentTypes      = {"Wire","Cell","Resistor","Capacitor",
		"Ammeter","Voltmeter","Lamp","Switch","ACSource","Diode"};


	public String getExhibitName(){return "Circuitboard";}
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton jButton1 = new JButton();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JButton gobutton = new JButton();
  Circuitboard cb = new Circuitboard();
  JPanel rightpane = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList complist = new JList(componentTypes);
  BorderLayout borderLayout5 = new BorderLayout();
  JToggleButton createcomp = new JToggleButton();
  JPanel selectionpane = new JPanel();

	CircuitComponent dragging=null;
	boolean dragall;	// dragging component or ends?
	boolean dragstart;	// which end is dragged?
	int dragpos;	// x offset of node dragging from c1
	ComponentRenderer crender=new ComponentRenderer();

  public CircuitExhibit() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	cb.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			if(createcomp.isSelected()){		//creating new component
				Point start;
				if(cb.isOpen(start=cb.getNearestNode(e.getPoint())) ){
					CircuitComponent newc=
						addCircuitComponent(complist.getSelectedValue().toString(),start.y,start.x,start.x);
					if(newc!=null){
						selectComponent(newc);
						dragging=newc;
						dragstart=false;	// drag the end of the component
						dragall=false;
						createcomp.setSelected(false);
					}
				}
			}else{
				Point node=cb.getExactNode(e.getPoint());
				CircuitComponent d;
				if(node!=null && (d=cb.getComponentAtNode(node))!=null){
					selectComponent(d);
					dragging=d;				//begin dragging component end
					dragstart=(d.c1==node.x);
					dragall=false;
				}else{
					if((d=cb.getComponent(e.getPoint()))!=null){
						selectComponent(d);
						dragging=d;
						dragall=true;
						dragpos=cb.getNearestNode(e.getPoint()).x-d.c1;
					}else selectComponent(null);
				}
			}
		}
		public void mouseReleased(MouseEvent e){
			if(dragging!=null){
				if(dragging.c1==dragging.c2)removeCircuitComponent(dragging);
			}
			dragging=null;
		}
	});
	cb.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent e){
			if(dragging!=null){
				Point end=cb.getNearestNode(e.getPoint());
				if(end!=null){
					if(dragall){
						if(cb.isLineClear(end.x-dragpos,end.x-dragpos+/*Math.abs*/(dragging.c2-dragging.c1),end.y,dragging)){
							int w=dragging.c2-dragging.c1;
							dragging.c1=end.x-dragpos;
							dragging.c2=dragging.c1+w;
							dragging.ch=end.y;
							dragging.onMove();
						}
					}else{
						if(dragstart){					//drag c1
							if(cb.isLineClear(dragging.c1,end.x,dragging.ch,dragging)){
								dragging.c1=end.x;
								dragging.onMove();
							}
						}else{						//drag c2
							if(cb.isLineClear(end.x,dragging.c2,dragging.ch,dragging)){
								dragging.c2=end.x;
								dragging.onMove();
							}
						}
					}
				}
			}
		}
	});
	gobutton.doClick();
  }




	public void removeCircuitComponent(CircuitComponent comp){
		if(comp.selected)selectComponent(null);
		cb.removeCircuitComponent(comp);
		graphcomp.reset();
	}
        public void removeAllCircuitComponents(){
          selectComponent(null);
          cb.removeAllCircuitComponents();
          graphcomp.reset();
        }
	public CircuitComponent addCircuitComponent(String classname,int h,int a,int b){
		CircuitComponent newc=null;
		try{
			Class cl=Class.forName("com.cudos.circuit."+classname);
			newc=(CircuitComponent)cl.getConstructor(new Class[]{Class.forName("com.cudos.circuit.Circuitboard")}).newInstance(new Object[]{cb});
		}catch(Exception ex){ex.printStackTrace();}
		if(newc!=null){
			newc.ch=h;
			newc.c1=a;
			newc.c2=b;
			newc.onMove();
			cb.addCircuitComponent(newc);
		}

		graphcomp.reset();
		return newc;
	}

	Image[] cims=new Image[componentTypes.length];
	public void setupimages(){
		for(int i=0;i<cims.length;i++)
			cims[i]=getApplet().getImage("resources/icons/"+componentTypes[i]+".gif");
	}
	class ComponentRenderer extends JLabel implements ListCellRenderer {
		Image i;
		Point p1,p2;
		public Component getListCellRendererComponent(
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus)    // the list and the cell have the focus
		{
			setText(componentTypes[index]);
			setIcon(null);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			i=cims[index];
			setPreferredSize(new Dimension(getApplet().getTextWidth((Graphics2D)list.getGraphics(),getText())+60,60));
			return this;
		}
		public void paint(Graphics g_){
			Graphics2D g=(Graphics2D)g_;
			super.paint(g_);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(getForeground());
			g.setStroke(new BasicStroke(3));
			p1=new Point(getWidth()/2,getHeight()/2);
			p2=new Point(getWidth(),getHeight()/2);
			int mid=(p1.x+p2.x)/2,dw=i.getWidth(this)/2,dh=i.getHeight(this)/2;
			g.drawLine(p1.x,p1.y,mid-dw,p1.y);
			g.drawLine(mid+dw,p1.y,p2.x,p1.y);
			g.drawImage(i,mid-dw,p1.y-dh,this);
		}
		public boolean imageUpdate(Image img,int info,int x,int y,int w,int h){
			if((info & ImageObserver.ALLBITS)!=0){
				complist.repaint();
				return false;
			}else return true;
		}
	}

	CircuitComponent selectedcomponent=null;
  JPanel raillabels = new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(Color.black);
			for(int i=0;i<cb.cw;i++){
				double v=cb.rail[i].voltage;
				if(!Double.isNaN(v))g.drawString(String.valueOf(Math.round(v*100)/100.),i*cb.sx,30);
			}
		}
	};



	public void selectComponent(CircuitComponent cc){
		if(selectedcomponent!=null){
			selectionpane.remove(selectedcomponent);
			selectedcomponent.selected=false;
		}
		selectedcomponent=cc;
		if(cc!=null){
			selectionpane.add(cc);
			cc.selected=true;
		}
		cb.repaint();
		this.validateTree();
		rightpane.repaint();
	}
	GraphComponent graphcomp=new GraphComponent(cb);

	public void postinit(){
		cb.init();
		CircuitComponent cell0=addCircuitComponent("Cell",0,0,cb.cw-1);
		setupimages();
		complist.repaint();
	}


	Timer timer=new Timer(100,new ActionListener(){
		public void actionPerformed(ActionEvent e){tick();}
	});
  JButton graphselect = new JButton();
  JButton millistep = new JButton();
	public void tick(){
		cb.startCalculation();
		raillabels.repaint();
		graphcomp.tick();
	}

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    jButton1.setText("Return");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel1.setLayout(borderLayout3);
    gobutton.setText("Go");
    gobutton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        gobutton_actionPerformed(e);
      }
    });
    rightpane.setLayout(borderLayout4);
    rightpane.setPreferredSize(new Dimension(215, 200));
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setPreferredSize(new Dimension(70, 200));
    complist.setSelectionBackground(Color.yellow);
    complist.setSelectionForeground(Color.black);
    complist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jPanel5.setLayout(borderLayout5);
    createcomp.setText("Add");
    cb.setPreferredSize(new Dimension(10, 30));
    raillabels.setPreferredSize(new Dimension(10, 30));
    graphselect.setText("Graph");
    graphselect.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        graphselect_actionPerformed(e);
      }
    });
    millistep.setText("Step");
    millistep.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        millistep_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel4, BorderLayout.SOUTH);
    jPanel4.add(gobutton, null);
    jPanel4.add(millistep, null);
    jPanel1.add(raillabels, BorderLayout.NORTH);
    jPanel1.add(cb, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.EAST);
    jPanel2.add(jPanel3, BorderLayout.SOUTH);
    jPanel3.add(graphselect, null);
    jPanel3.add(jButton1, null);
    jPanel2.add(rightpane, BorderLayout.CENTER);
    rightpane.add(jPanel5, BorderLayout.CENTER);
    jPanel5.add(jScrollPane1, BorderLayout.CENTER);
    jPanel5.add(createcomp, BorderLayout.SOUTH);
    rightpane.add(selectionpane, BorderLayout.SOUTH);
    jScrollPane1.getViewport().setView(complist);
    complist.setCellRenderer(crender);
  }

  void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
  }

  void gobutton_actionPerformed(ActionEvent e) {
	if(gobutton.getText().equals("Go")){
		timer.start();
		gobutton.setText("Stop");
		millistep.setEnabled(false);
	}else{
		timer.stop();
		gobutton.setText("Go");
		millistep.setEnabled(true);
	}
  }

  void graphselect_actionPerformed(ActionEvent e) {
	selectComponent(graphcomp);
  }

  void millistep_actionPerformed(ActionEvent e) {
	tick();
  }
}

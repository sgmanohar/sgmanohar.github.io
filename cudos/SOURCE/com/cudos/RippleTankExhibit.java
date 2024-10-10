
/**
 * Exhibit containing a ripple tank
 */
package com.cudos;

import com.cudos.common.CudosExhibit;
import java.awt.*;
import javax.swing.*;
import com.cudos.common.waves.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.cudos.common.*;

public class RippleTankExhibit extends CudosExhibit implements SelectionRecipient{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	public RippleTank tank = new RippleTank();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	Border border1;
	JPanel jPanel3 = new JPanel();
	JButton jButton3 = new JButton();
	JButton jButton2 = new JButton();
	JButton jButton1 = new JButton();
	BorderLayout borderLayout3 = new BorderLayout();
	Border border2;
	GridLayout gridLayout1 = new GridLayout();
	JPanel selectionPanel = new JPanel();
	Border border3;
	Border border4;

	public RippleTank getTank(){return tank;}

	boolean mousedown;
	int lastx,lasty;
	public String getExhibitName(){
		return "Ripple Tank";
	}
	public RippleTankExhibit() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}







	cgrp.add(select);cgrp.add(news);cgrp.add(neww);

	tank.setLayout(null);
	tank.createSource(50,70);
	tank.createWall(100,130,15,120);
//	rippleTank1.walls.add(new RippleWall(100,0,15,100));

		//mouse handlers
/*
	rippleTank1.addMouseListener(new MouseAdapter(){
		public void mousePressed(MouseEvent m){
			DraggableComponent hit=null;
			int htype;
			for(Enumeration e=rippleTank1.sources.elements();e.hasMoreElements();){
				RippleSource rs=(RippleSource)e.nextElement();
				if(rs.getX()/3==m.getX()/3 && rs.getY()/3==m.getY()/3){	//3 pixel hit range?
					htype=SOURCE;
					hit=rs;
				}
			}
			if(hit==null){
				for( Enumeration e=rippleTank1.walls.elements();e.hasMoreElements();){
					RippleWall wl=(RippleWall)e.nextElement();
					if(wl.contains(m.getX(),m.getY())){
						htype=WALL;
						hit=wl;
					}
				}
			}

			if(hit!=null)((Component)hit).repaint();
			else(rippleTank1.repaint());
			mousedown=true;
			lastx=m.getX();lasty=m.getY();
		}
		public void mouseReleased(MouseEvent m){mousedown=false;}
	});
	rippleTank1.addMouseMotionListener(new MouseMotionAdapter(){
		public void mouseDragged(MouseEvent m){
			if(rippleTank1.selected!=null){
				int dx=-(lastx-(lastx=m.getX()));
				int dy=-(lasty-(lasty=m.getY()));
				((DragableComponent)(rippleTank1.selected)).moveto(dx,dy);
			}
		}
	});
*/
	}

	final static int NOT=0,SOURCE=1,WALL=2;
	Object selected;
	JPanel selectionPanelContent;
	public void setSelected(Object o){
		selected=o;
		if(selectionPanelContent!=null){
			selectionPanel.remove(selectionPanelContent);
			selectionPanel.repaint();
		}
		if(o instanceof RippleSource){
			selectionPanel.add(selectionPanelContent=new SourcePanel());
			((SourcePanel)selectionPanelContent).setSource((RippleSource)o);
			selectLabel.setText(((DraggableComponent)o).getName());
		}else if(o instanceof RippleWall){
			selectionPanel.add(selectionPanelContent=new WallPanel());
			((WallPanel)selectionPanelContent).setWall((RippleWall)o);
			selectLabel.setText(((DraggableComponent)o).getName());
		}else if(o==null) selectLabel.setText("No selection");
	}
	public Object getSelected(){return selected;}






	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(5,5,5,5));
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(5,5,5,5));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 235),new Color(135, 133, 115)),BorderFactory.createEmptyBorder(5,5,5,5));
		border4 = BorderFactory.createLineBorder(SystemColor.control,1);
		border5 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(134, 134, 134),new Color(93, 93, 93));
		border6 = BorderFactory.createLineBorder(SystemColor.control,1);
		this.getContentPane().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setBorder(border1);
		jPanel2.setMinimumSize(new Dimension(150, 144));
		jPanel2.setPreferredSize(new Dimension(160, 37));
		jPanel2.setLayout(borderLayout3);
		jPanel3.setLayout(gridLayout1);
		jButton3.setText("Clear");
		jButton3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton3_actionPerformed(e);
			}
		});
		jButton2.setEnabled(false);
		jButton2.setText("Advance");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jButton1.setPreferredSize(new Dimension(90, 27));
		jButton1.setText("Pause");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jPanel3.setBorder(border2);
		jPanel3.setPreferredSize(new Dimension(120, 100));
		gridLayout1.setColumns(1);
		gridLayout1.setRows(3);
		selectionPanel.setBorder(border3);
		selectionPanel.setMinimumSize(new Dimension(120, 35));
		selectionPanel.setPreferredSize(new Dimension(120, 35));
		selectionPanel.setLayout(borderLayout4);
		selectLabel.setFont(new java.awt.Font("Dialog", 1, 12));
		selectLabel.setBorder(border5);
		selectLabel.setPreferredSize(new Dimension(100, 21));
		selectLabel.setText("No Selection");
		tank.setColor2(new Color(192, 255, 255));
		jPanel1.setMinimumSize(new Dimension(100, 45));
		jPanel1.setPreferredSize(new Dimension(100, 45));
		neww.setText("New wall");
		neww.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				neww_actionPerformed(e);
			}
		});
		news.setText("New source");
		news.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				news_actionPerformed(e);
			}
		});
		select.setSelected(true);
		select.setText("Select");
		select.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				select_actionPerformed(e);
			}
		});
		jPanel4.setLayout(borderLayout5);
		jLabel1.setText("Contrast");
		contrast.setMaximum(500);
		contrast.setPreferredSize(new Dimension(150, 24));
		contrast.setBorder(border6);
		contrast.addChangeListener(new javax.swing.event.ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				contrast_stateChanged(e);
			}
		});
		jButton4.setText("Exit");
		jButton4.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton4_actionPerformed(e);
			}
		});
		delbutton.setText("Delete");
		delbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				delbutton_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(tank, BorderLayout.CENTER);
		jPanel1.add(jPanel4, BorderLayout.SOUTH);
		jPanel4.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(select, null);
		jPanel5.add(news, null);
		jPanel5.add(neww, null);
		jPanel5.add(delbutton, null);
		jPanel4.add(jPanel6, BorderLayout.NORTH);
		jPanel6.add(jLabel1, null);
		jPanel6.add(contrast, null);
		this.getContentPane().add(jPanel2, BorderLayout.EAST);
		jPanel2.add(selectionPanel, BorderLayout.CENTER);
		selectionPanel.add(selectLabel, BorderLayout.NORTH);
		jPanel2.add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(jButton3, null);
		jPanel3.add(jButton2, null);
		jPanel3.add(jButton1, null);
		jPanel2.add(jButton4, BorderLayout.SOUTH);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	if(jButton1.getText()=="Pause"){
		tank.pause();
		jButton1.setText("Restart");
		jButton2.setEnabled(true);
	}else{
		tank.start();
		jButton1.setText("Pause");
		jButton2.setEnabled(false);
	}
	}

	void jButton3_actionPerformed(ActionEvent e) {
	tank.clear();
	}

	void jButton2_actionPerformed(ActionEvent e) {
	tank.tick();
	}

	JLabel selectLabel = new JLabel();
	Border border5;
	ButtonGroup cgrp = new ButtonGroup();
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JToggleButton neww = new JToggleButton();
	JToggleButton news = new JToggleButton();
	JToggleButton select = new JToggleButton();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel6 = new JPanel();
	JSlider contrast = new JSlider();
	JLabel jLabel1 = new JLabel();
	Border border6;
	JButton jButton4 = new JButton();
	JButton delbutton = new JButton();

	void select_actionPerformed(ActionEvent e) {
	tank.setCommand(0);
	}

	void news_actionPerformed(ActionEvent e) {
	tank.setCommand(1);
	}

	void neww_actionPerformed(ActionEvent e) {
	tank.setCommand(2);
	}

	void contrast_stateChanged(ChangeEvent e) {
	tank.setContrast(contrast.getValue());
	}

	void jButton4_actionPerformed(ActionEvent e) {
	tank.pause();
	getApplet().toChooser();
	}

	void delbutton_actionPerformed(ActionEvent e) {
	tank.deleteSelected();
	}

 }
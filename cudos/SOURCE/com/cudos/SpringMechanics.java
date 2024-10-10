
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos;

import java.awt.*;
import javax.swing.*;
import com.cudos.common.*;
import java.awt.event.*;
import com.cudos.mechanics.*;
import java.io.*;
import javax.swing.Timer;
import com.cudos.mechanics.Spring;

public class SpringMechanics extends CudosExhibit implements SelectionRecipient{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPopupMenu addpopup = new JPopupMenu();
	JMenuItem adddashpot = new JMenuItem();
	JMenuItem addspring = new JMenuItem();
	Oscilloscope oscilloscope = new Oscilloscope();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel propertiesPanel = new JPanel();
	MechanicsDisplay display = new MechanicsDisplay();
	BorderLayout borderLayout3 = new BorderLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	JPanel jPanel4 = new JPanel();
	JToggleButton addbutton = new JToggleButton();
	JButton removebutton = new JButton();
	JToggleButton joinbutton = new JToggleButton();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel5 = new JPanel();
	JToggleButton playbutton = new JToggleButton();
	JToggleButton pausebutton = new JToggleButton();
	JButton stopbutton = new JButton();
	JPanel jPanel6 = new JPanel();
	JButton filebutton = new JButton();
	JButton jButton2 = new JButton();
	JPopupMenu fileMenu = new JPopupMenu();
	JMenuItem jMenuItem1 = new JMenuItem();
	JMenuItem jMenuItem2 = new JMenuItem();
	JMenuItem jMenuItem3 = new JMenuItem();
	JMenuItem jMenuItem4 = new JMenuItem();

	public String getExhibitName(){return "Mechanics";}
	public SpringMechanics() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(borderLayout1);
		adddashpot.setText("Dashpot");
		adddashpot.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		addspring.setText("Spring");
		addspring.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jPanel1.setLayout(borderLayout2);
		oscilloscope.setPreferredSize(new Dimension(150, 150));
		oscilloscope.setGutter(3);
		jPanel2.setLayout(borderLayout3);
		display.setToolTipText("");
		propertiesPanel.setLayout(borderLayout4);
		addbutton.setText("Add");
		addbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		removebutton.setText("Remove");
		removebutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		joinbutton.setText("Join");
		joinbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jPanel4.setBorder(BorderFactory.createEtchedBorder());
		jPanel3.setLayout(borderLayout5);
		playbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		pausebutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		stopbutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		playbutton.setActionCommand("Play");
		pausebutton.setActionCommand("Pause");
		stopbutton.setActionCommand("Stop");
		propertiesPanel.setPreferredSize(new Dimension(200, 20));
		filebutton.setText("File");
		filebutton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jButton2.setText("Options");
		jMenuItem1.setActionCommand("Open");
		jMenuItem1.setText("Open");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jMenuItem2.setText("Save as");
		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jMenuItem3.setText("New");
		jMenuItem3.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		jMenuItem4.setText("Exit");
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleAction(e);
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.EAST);
		jPanel1.add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(addbutton, null);
		jPanel4.add(removebutton, null);
		jPanel4.add(joinbutton, null);
		jPanel3.add(jPanel5, BorderLayout.SOUTH);
		jPanel5.add(playbutton, null);
		jPanel5.add(pausebutton, null);
		jPanel5.add(stopbutton, null);
		jPanel1.add(oscilloscope, BorderLayout.SOUTH);
		jPanel1.add(propertiesPanel, BorderLayout.CENTER);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(display, BorderLayout.CENTER);
		jPanel2.add(jPanel6, BorderLayout.NORTH);
		jPanel6.add(filebutton, null);
		jPanel6.add(jButton2, null);
		addpopup.add(addspring);
		addpopup.add(adddashpot);
		fileMenu.add(jMenuItem1);
		fileMenu.add(jMenuItem2);
		fileMenu.add(jMenuItem3);
		fileMenu.add(jMenuItem4);
	}
	public void addNotify(){super.addNotify(); init();}


	public static final int SPRING=1, DASHPOT=2;




/*
	Governs action behaviour
*/

	void handleAction(ActionEvent e) {
		String s=e.getActionCommand();
		if(s.equals("Remove")){
			if(getSelected()!=null){
				display.getData().remove(display.getSelected());
				repaint();
			}
		}else if(s.equals("Add")){
			joinbutton.setSelected(false);
			adding=false;
			if(addbutton.isSelected()){
				addpopup.show(addbutton, 0, addbutton.getHeight());
			}
		}else if(s.equals("Spring")){
			adding=true;
			addCommand=SPRING;
			if(getSelected() instanceof Node) setSelected(getSelected());
		}else if(s.equals("Dashpot")){
			adding=true;
			addCommand=DASHPOT;
			if(getSelected() instanceof Node) setSelected(getSelected());
		}else if(s.equals("Join")){
			addbutton.setSelected(false);
			if(joinbutton.isSelected()){
				joiningfrom=true; joiningto=false;
				if(getSelected() instanceof Node) setSelected(getSelected());
			}else{
				joiningfrom=false;joiningto=false;
			}
		}else if(s.equals("Play")){
			if(!playbutton.isSelected())timer.stop();
			else{
				if(pausebutton.isSelected()){
					tick();
					playbutton.setSelected(false);
				}else{
					timer.start();
					pausebutton.setSelected(false);
				}
			}
		}else if(s.equals("Pause")){
			if(pausebutton.isSelected()){
				if(playbutton.isSelected()){
					timer.stop();
					playbutton.setSelected(false);
				}else pausebutton.setSelected(false);
			}else{
				playbutton.setSelected(true);
				timer.start();
			}
		}else if(s.equals("Stop")){
			pausebutton.setSelected(false);
			playbutton.setSelected(false);
			timer.stop();
		}else if(s.equals("Save as")) saveas();
		else if(s.equals("Open")) open();
		else if(s.equals("New")) newfile();
		else if(s.equals("File")){
			fileMenu.show(filebutton, 0,filebutton.getHeight());
		}else if(s.equals("Exit")){
			timer.stop();
			getApplet().toChooser();
		}
	}
	boolean adding,joiningfrom, joiningto;
	boolean paused;
	int addCommand;
	Node joinFrom;

		//SELECTION RECIPIENT
	BaseComponent selected;
	public void setSelected(Object o){
		propertiesPanel.removeAll();
		selected=(BaseComponent)o;
		if(selected!=null){
			propertiesPanel.add( selected.getPanel() );
		}
		validateTree(); propertiesPanel.repaint();

		if(adding && o instanceof Node){
			Node top=(Node)o;
			MechanicsComponent a;
			if(addCommand==SPRING){
				a=new Spring();
			}else if(addCommand==DASHPOT){
				a=new Dashpot();
			}else return;

			a.setTop(top);
			Node bottom=new Node();
			bottom.x=a.x=top.x;
			bottom.y=top.y+a.getNaturalLength();
			a.setBottom(bottom);

			display.getData().add(bottom);
			display.getData().add(a);

			adding=false;
			addbutton.setSelected(false);
			display.repaint();
			return;
		}else if(joiningfrom && o instanceof Node){
			joinFrom=(Node)o;
			joiningfrom=false; joiningto=true;
			return;
		}else if(joiningto && o instanceof Node){
			Node joinTo=(Node)o;
			display.getData().joinNodes(joinFrom, joinTo);
			joiningto=false;joiningfrom=false;
			joinbutton.setSelected(false);

			setSelected(joinFrom);
			display.repaint();
			return;
		}
	}
	public Object getSelected(){return selected;}
	void init(){
		playbutton.setIcon(new ImageIcon(getApplet().getImage("resources/icons/Play.gif")));
		pausebutton.setIcon(new ImageIcon(getApplet().getImage("resources/icons/Pause.gif")));
		stopbutton.setIcon(new ImageIcon(getApplet().getImage("resources/icons/Stop.gif")));

//		filechooser.addChoosableFileFilter(mechfilefilter);

		display.setSelectionRecipient(this);
		MechanicsData d=new MechanicsData();
		display.setData(new MechanicsData());
		d.getRoot().x=display.getWidth()/2;
	}


	Timer timer=new Timer(100, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			tick();
		}
	});

	void tick(){
		MechanicsData data=display.getData();
		data.tick();
		Node[] n=data.getNodes();
		int[] npos=new int[n.length];
		for(int i=0;i<n.length;i++){
			npos[i]=(int)(n[i].y*3);
		}
		if(oscilloscope.graph.getNTraces()!=npos.length) oscilloscope.setNTraces(npos.length);
		oscilloscope.setPosY(npos);
		display.repaint();
	}




/*
	Filing system operations
*/

//	JFileChooser filechooser=new JFileChooser("c:/");
	String currentFile="";
	void open(){
/*		if(!askClear())return;
		filechooser.showOpenDialog(this);
		File f=filechooser.getSelectedFile();
		if(f==null)return;
		String fname=f.getAbsolutePath();
		try{
			display.setData( (MechanicsData)new ObjectInputStream(new FileInputStream(fname)).readObject() );
			setCurrentFile(fname);
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e, "Open error", JOptionPane.ERROR_MESSAGE);
		}
*/	}
	void saveas(){
/*		filechooser.showSaveDialog(this);
		File f=filechooser.getSelectedFile();
		if(f==null)return;
		String fname=f.getAbsolutePath();
		save(fname);
*/	}
	void save(){
		if(getCurrentFile()=="") saveas();
		else save(getCurrentFile());
	}
	void save(String filename){
		try{
			new ObjectOutputStream(new FileOutputStream(filename)).writeObject(display.getData());
			setCurrentFile(filename);
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e, "Save error", JOptionPane.ERROR_MESSAGE);
		}
	}
	void newfile(){
		if(!askClear())return;
		display.getData().clearAll();
		display.repaint();
	}
	boolean askClear(){
		return JOptionPane.showConfirmDialog(this, "This will erase the current setup", "Clear current setup",
		 JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION;
	}

	String getCurrentFile(){return currentFile;}
	void setCurrentFile(String s){
		currentFile=s;
		//set title bar if in frame
	}
	javax.swing.filechooser.FileFilter mechfilefilter=new javax.swing.filechooser.FileFilter(){
		public boolean accept(File f){
			return f.getName().endsWith(".mec");
		}
		public String getDescription(){
			return "Mechanics file (*.mec)";
		}
	};

}
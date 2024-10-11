
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.neurolab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;

import com.neurolab.common.Label3D;
import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.ReturnButton;

public class AnatomicalPathways extends NeurolabExhibit {
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JButton showall = new JButton();
	ReturnButton returnButton1 = new ReturnButton();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel4 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	Label3D label3D1 = new Label3D();
	Label3D label3D2 = new Label3D();
	JList destlist = new JList();
	JPanel jPanel7 = new JPanel();
	Label3D label3D3 = new Label3D();
	JList tractlist = new JList();
	JTextArea jTextPane1 = new JTextArea();
	Border border1;
	Border border2;
	Border border3;
	public String getExhibitName(){return "Neural Pathways";}
	public AnatomicalPathways() {
	}
	public void init(){
		super.init();
		InputStream is=null;
		try {
			jbInit();
			is=getURL("resources/pathways.txt").openStream();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(is!=null)try{readText(is);}catch(IOException e){throw new RuntimeException("cannot load textfile");}
		showall.doClick();
	MouseListener dcl=new MouseAdapter(){
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount()==2){
				if(e.getSource()==sourcelist){
					sourcedcl();
				}else if(e.getSource()==destlist){
					destdcl();
				}
			}
		}
	};
	sourcelist.addMouseListener(dcl);
	destlist.addMouseListener(dcl);
	}
	Vector nuclei;
	Vector pathways;
	DefaultListModel sourcemodel = new DefaultListModel();
	DefaultListModel destmodel = new DefaultListModel();
	DefaultListModel tractmodel = new DefaultListModel();
	JScrollPane jScrollPane1 = new JScrollPane();
	JList sourcelist = new JList();
	JScrollPane jScrollPane2 = new JScrollPane();
	JScrollPane jScrollPane3 = new JScrollPane();
	class Pathway{
		int[][] conn;
		String name;
	}
	void readText(InputStream is) throws IOException{
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String s="";
		nuclei=new Vector();
		while(s!=null && !s.startsWith("*")){
			s=br.readLine();
			nuclei.addElement(s);
		}
		pathways=new Vector();
		s="";
		while(s!=null && !s.startsWith("*")){
			s="";
			String name;
			while(s.length()==0)s=br.readLine();
			if(s.startsWith("*"))break;
			Pathway p=new Pathway();
			p.name=name=s;

			s="";
			while(s.length()==0)s=br.readLine();

			StringTokenizer st=new StringTokenizer(s," ,;\t",false);
/*			int n=st.countTokens()/2;
			p.conn=new int[n][2];
*/
p.conn=new int[1][2];
			int c=0;
			while(st.hasMoreElements()){
				p.conn[c][0]=Integer.parseInt(st.nextToken());
				p.conn[c++][1]=Integer.parseInt(st.nextToken());
c--;
pathways.addElement(p);
p=new Pathway();
p.name=name;
p.conn=new int[1][2];
			}
//			pathways.add(p);
		}
	}

	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(162, 147, 159),new Color(113, 102, 111)),BorderFactory.createEmptyBorder(2,2,2,2));
		border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(162, 147, 159),new Color(113, 102, 111)),BorderFactory.createEmptyBorder(2,2,2,2));
		border3 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.white,Color.white,new Color(162, 147, 159),new Color(113, 102, 111)),BorderFactory.createEmptyBorder(2,2,2,2));
		getMainContainer().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		showall.setBackground(Color.lightGray);
		showall.setText("Show all");
		showall.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showall_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout3);
		jPanel4.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		label3D1.setPreferredSize(new Dimension(90, 30));
		label3D1.setFont(new java.awt.Font("Dialog", 1, 16));
		label3D1.setText("Source");
		label3D2.setText("Destination");
		label3D2.setFont(new java.awt.Font("Dialog", 1, 16));
		label3D2.setPreferredSize(new Dimension(120, 30));
		destlist.setModel(destmodel);
		destlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		destlist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				destinationchange(e);
			}
		});
		jPanel7.setBackground(Color.lightGray);
		jPanel7.setPreferredSize(new Dimension(10, 120));
		label3D3.setText("Tract");
		label3D3.setFont(new java.awt.Font("Dialog", 1, 16));
		label3D3.setPreferredSize(new Dimension(70, 30));
		tractlist.setModel(tractmodel);
		tractlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tractlist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				tractchange(e);
			}
		});
		jTextPane1.setPreferredSize(new Dimension(150, 100));
		jTextPane1.setOpaque(false);
		jTextPane1.setEditable(false);
		jTextPane1.setText("Double-click on a destination to go one stage on, on a source to " +
		"go one stage back");
		jTextPane1.setLineWrap(true); jTextPane1.setWrapStyleWord(true);
		jTextPane1.setFont(new java.awt.Font("Dialog", 0, 13));
		jTextPane1.setBackground(systemGray);
		sourcelist.setModel(sourcemodel);
		sourcelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourcelist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				sourcechange(e);
			}
		});
		jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
		jScrollPane1.setPreferredSize(new Dimension(260, 120));
		jScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane2.setBorder(BorderFactory.createLoweredBevelBorder());
		jScrollPane2.setPreferredSize(new Dimension(260, 120));
		jScrollPane3.setBorder(BorderFactory.createLoweredBevelBorder());
		jScrollPane3.setPreferredSize(new Dimension(300, 90));
		jPanel3.setBackground(Color.lightGray);
		jPanel6.setBackground(Color.lightGray);
		jPanel5.setBackground(Color.lightGray);
		getMainContainer().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jPanel5, null);
		jPanel5.add(label3D1, null);
		jPanel5.add(jScrollPane1, null);
		jScrollPane1.getViewport().add(sourcelist, null);
		jPanel4.add(jPanel6, null);
		jPanel6.add(label3D2, null);
		jPanel6.add(jScrollPane2, null);
		jScrollPane2.getViewport().add(destlist, null);
		jPanel2.add(jPanel7, BorderLayout.SOUTH);
		jPanel7.add(label3D3, null);
		jPanel7.add(jScrollPane3, null);
		jScrollPane3.getViewport().add(tractlist, null);
		jPanel7.add(jTextPane1, null);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(showall, null);
		jPanel3.add(returnButton1, null);
	}
	Vector find(int which, String name){
		Vector f=new Vector();
		if(pathways==null) return f;
		for(int p=0;p<pathways.size();p++){
			Pathway a=(Pathway)pathways.elementAt(p);
			if(which<2){
				for(int i=0;i<a.conn.length;i++){
					int c=a.conn[i][which];
					if(name.equals(nuclei.elementAt(c)))f.addElement(a);
				}
			}else{
				if(a.name.equals(name))f.addElement(a);
			}
		}
		return f;
	}
	void showsource(Vector v){
		Vector w=displayableStrings(0,v);
		sourcemodel.removeAllElements();
		for(int i=0;i<w.size();i++)sourcemodel.addElement(w.elementAt(i));
	}

		//takes vector of pathways, returns vector of strings
	Vector displayableStrings(int which,Vector v){
		Vector w=new Vector();
		if(v==null) return w;
		for(int i=0;i<v.size();i++){
			Pathway p=(Pathway)v.elementAt(i);
			for(int j=0;j<p.conn.length;j++){
				Object s;
				if(which<2)s=nuclei.elementAt(p.conn[j][which]);
				else s=p.name;
				if(w.indexOf(s)<0)w.addElement(s);
					//eliminate redundancies
			}
		}
		//Collections.sort(w);	//sort alphabetically
		return w;
	}
	void showdest(Vector v){
		Vector w=displayableStrings(1,v);
		destmodel.removeAllElements();
		for(int i=0;i<w.size();i++)destmodel.addElement(w.elementAt(i));
	}
	void showtract(Vector v){
		Vector w=displayableStrings(2,v);
		tractmodel.removeAllElements();
		for(int i=0;i<w.size();i++)tractmodel.addElement(w.elementAt(i));
	}

	void sourcedcl(){
		String s=(String)sourcelist.getSelectedValue();
		destmodel.removeAllElements();
		destmodel.addElement(s);
		Vector v=find(1,s);
		showsource(v);showtract(v);
	}
	void destdcl(){
		String s=(String)destlist.getSelectedValue();
		sourcemodel.removeAllElements();
		sourcemodel.addElement(s);
		Vector v=find(0,s);
		showdest(v);showtract(v);
	}

	void sourcechange(ListSelectionEvent e) {
	String s=(String)sourcelist.getSelectedValue();
	if(s!=null){
		Vector v=find(0,s);
		showdest(v);showtract(v);
	}
	}

	void destinationchange(ListSelectionEvent e) {
	String s=(String)destlist.getSelectedValue();
	if(s!=null){
		Vector v=find(1,s);
		showsource(v);showtract(v);
	}
	}

	void tractchange(ListSelectionEvent e) {
	String s=(String)tractlist.getSelectedValue();
	if(s!=null){
		Vector v=find(2,s);
		showsource(v);showdest(v);
	}
	}

	void showall_actionPerformed(ActionEvent e) {
	showsource(pathways);showdest(pathways);showtract(pathways);
	}

	void sourcelist_valueChanged(ListSelectionEvent e) {

	}
  public void close(){
    
  }
}
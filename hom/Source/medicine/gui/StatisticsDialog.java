
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      <p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package medicine.gui;
import medicine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class StatisticsDialog extends JDialog implements ActionListener{
	JPanel jPanel1 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JLabel jLabel1 = new JLabel();
	JLabel nentities = new JLabel();
	JLabel jLabel3 = new JLabel();
	JLabel norphans = new JLabel();
	JLabel jLabel2 = new JLabel();
	JLabel nmeanlinks = new JLabel();


	public StatisticsDialog(Entity e){
	this();
	EntityCrawler c=new EntityCrawler(e, this);
	c.start();
	}
	public StatisticsDialog() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
                setSize(350,200);
                MainApplication.centreWindow(this);
	}

	int n=0, o=0, c=0, fe=0, pe=0, sy=0;
	public void actionPerformed(ActionEvent e){
		if(e.getSource() instanceof EntityCrawler && e.getActionCommand()==null){
			//completed
			return;
		}
		Entity r=(Entity)e.getSource();
		n++;
		c += r.causes.size() + r.effects.size() + r.children.size() + r.parents.size();
		if(r.parents.size()==0)o++;
		if(r.effects.size()==0)fe++;
		if(r.causes.size()==0)pe++;
		sy+=1+r.synonyms.size();
		updatestats();
	}

	void updatestats(){
		nentities.setText(String.valueOf(n));
		norphans.setText(String.valueOf(o));
		nmeanlinks.setText(String.valueOf(((double)c)/n));
		nineffective.setText(String.valueOf(fe));
		nprimarycause.setText(String.valueOf(pe));
		nnames.setText(String.valueOf(((double)sy)/n));
	}

	private void jbInit() throws Exception {
		this.setTitle("Statistics");
		jPanel1.setLayout(gridLayout1);
		jLabel1.setText("Number of entities");
		nentities.setText("jLabel2");
		jLabel3.setText("Number of orphan entities");
		norphans.setText("jLabel4");
		gridLayout1.setColumns(2);
		gridLayout1.setRows(6);
		jLabel2.setText("Mean links per entity");
		nmeanlinks.setText("jLabel4");
		nineffective.setText("jLabel4");
		jLabel5.setText("Entities without effect");
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		lsbel.setText("Primary causes");
		nprimarycause.setText("jLabel4");
		jLabel4.setText("Names per entity");
		nnames.setText("jLabel6");
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jLabel1, null);
		jPanel1.add(nentities, null);
		jPanel1.add(jLabel3, null);
		jPanel1.add(norphans, null);
		jPanel1.add(jLabel2, null);
		jPanel1.add(nmeanlinks, null);
		jPanel1.add(jLabel5, null);
		jPanel1.add(nineffective, null);
		jPanel1.add(lsbel, null);
		this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jButton1, null);
		jPanel1.add(nprimarycause, null);
		jPanel1.add(jLabel4, null);
		jPanel1.add(nnames, null);
	}
	JLabel nineffective = new JLabel();
	JLabel jLabel5 = new JLabel();
	JPanel jPanel2 = new JPanel();
	JButton jButton1 = new JButton();
	private JLabel lsbel = new JLabel();
	private JLabel nprimarycause = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JLabel nnames = new JLabel();

	void jButton1_actionPerformed(ActionEvent e) {
	hide();
	}
}

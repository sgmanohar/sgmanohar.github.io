
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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.cudos.common.*;

public class ModuleChooser extends CudosExhibit {
	FlowLayout flowLayout1 = new FlowLayout();
	JButton Module1 = new JButton();
	JButton jButton2 = new JButton();
	JButton jButton3 = new JButton();
	JButton jButton4 = new JButton();
	public String classbase="com.cudos.";
	JButton jButton1 = new JButton();
	JButton jButton5 = new JButton();
        Button3D button3D4 = new Button3D();
        public ModuleChooser() {
          try {
            jbInit();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }

        ActionListener moduleselectedlistener = new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            moduleselected(e);
          }
        };
        Button3D button3D5 = new Button3D();
        JButton blood1 = new JButton();
	JButton jButton6 = new JButton();
	JButton ripplebutton = new JButton();
	JButton jButton7 = new JButton();
	JButton jButton8 = new JButton();
	JButton jButton9 = new JButton();
	JButton jButton10 = new JButton();
	JButton jButton11 = new JButton();
	JButton jButton12 = new JButton();
	JButton jButton13 = new JButton();
	JButton jButton14 = new JButton();
	JButton jButton15 = new JButton();
	private JButton jButton16 = new JButton();
  JButton jButton17 = new JButton();

	private void jbInit() throws Exception {
		Module1.setActionCommand("TransportProcesses");
		Module1.setText("Membrane transport processes");
		Module1.addActionListener(moduleselectedlistener);
		this.getContentPane().setLayout(flowLayout1);
		jButton2.setActionCommand("AnatomicalDescription");
		jButton2.setText("Anatomical description terminology");
		jButton2.addActionListener(moduleselectedlistener);
		jButton3.setActionCommand("SagittalSections");
		jButton3.setText("Sagittal sections");
		jButton3.addActionListener(moduleselectedlistener);
		jButton4.setActionCommand("CirculationCompartment");
		jButton4.setText("Circulation Compartment");
		jButton4.addActionListener(moduleselectedlistener);
		jButton1.setActionCommand("SpinalCordSections");
		jButton1.setText("Spinal cord sections");
		jButton1.addActionListener(moduleselectedlistener);
		jButton5.setActionCommand("BrownianMotion");
		jButton5.setText("Brownian motion");
		jButton5.addActionListener(moduleselectedlistener);
		button3D4.setFont(new java.awt.Font("Dialog", 1, 14));
		button3D4.setForeground(SystemColor.control);
		button3D4.setActionCommand("ModuleBrainSections");
		button3D4.setText("Brain Sections");
		button3D4.addActionListener(moduleselectedlistener);
		button3D5.setText("Cell biology");
		button3D5.setActionCommand("ModuleCellBiology");
		button3D5.setForeground(SystemColor.control);
		button3D5.setFont(new java.awt.Font("Dialog", 1, 14));
		button3D5.addActionListener(moduleselectedlistener);
		blood1.setActionCommand("BloodTest");
		blood1.setText("Blood Test");
		blood1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton6.setText("Model3DTest");
		jButton6.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		ripplebutton.setActionCommand("RippleTankExhibit");
		ripplebutton.setText("Ripple simulator");
		ripplebutton.addActionListener(new ModuleChooser_ripplebutton_actionAdapter(this));
		jButton7.setActionCommand("WindTunnelExhibit");
		jButton7.setText("Wind Tunnel");
		jButton7.addActionListener(new ModuleChooser_jButton7_actionAdapter(this));
		jButton8.setActionCommand("CircuitExhibit");
		jButton8.setText("Circuit board");
		jButton8.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton9.setActionCommand("SignalProcessingExhibit");
		jButton9.setText("Signal Processing");
		jButton9.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton10.setActionCommand("SimpleMoleculesExhibit");
		jButton10.setText("Molecules");
		jButton10.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton11.setActionCommand("FourierMembraneExhibit");
		jButton11.setText("Basilar Membrane");
		jButton11.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton12.setText("LiposomeFormation");
		jButton12.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton13.setText("IronFilingsExhibit");
		jButton13.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton14.setActionCommand("PhotoreceptorExhibit");
		jButton14.setText("Photoreceptors");
		jButton14.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton15.setActionCommand("SpringMechanics");
		jButton15.setText("Mechanics");
		jButton15.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moduleselected(e);
			}
		});
		jButton16.setActionCommand("SimpleFormulaGraphExhibit");
		jButton16.setText("Formula graph");
		jButton16.addActionListener(new ModuleChooser_jButton16_actionAdapter(this));
		jButton17.setText("GeneticExhibit");
    jButton17.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moduleselected(e);
      }
    });
    this.getContentPane().add(Module1, null);
		this.getContentPane().add(jButton2, null);
		this.getContentPane().add(jButton3, null);
		this.getContentPane().add(jButton4, null);
		this.getContentPane().add(jButton1, null);
		this.getContentPane().add(jButton5, null);
		this.getContentPane().add(button3D4, null);
		this.getContentPane().add(button3D5, null);
		this.getContentPane().add(blood1, null);
		this.getContentPane().add(jButton6, null);
		this.getContentPane().add(ripplebutton, null);
		this.getContentPane().add(jButton7, null);
		this.getContentPane().add(jButton8, null);
		this.getContentPane().add(jButton9, null);
		this.getContentPane().add(jButton10, null);
		this.getContentPane().add(jButton11, null);
		this.getContentPane().add(jButton12, null);
		this.getContentPane().add(jButton13, null);
		this.getContentPane().add(jButton14, null);
		this.getContentPane().add(jButton15, null);
		this.getContentPane().add(jButton16, null);
    this.getContentPane().add(jButton17, null);
	}

	void moduleselected(ActionEvent e) {
          getApplet().toExhibit(classbase+e.getActionCommand());
	}


/*  void runindexedtext(ActionEvent e) {
	getApplet().toExhibit(classbase+"IndexedPictures",e.getActionCommand());
	}
*/

}

class ModuleChooser_ripplebutton_actionAdapter implements java.awt.event.ActionListener {
	ModuleChooser adaptee;

	ModuleChooser_ripplebutton_actionAdapter(ModuleChooser adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.moduleselected(e);
	}
}

class ModuleChooser_jButton7_actionAdapter implements java.awt.event.ActionListener {
	ModuleChooser adaptee;

	ModuleChooser_jButton7_actionAdapter(ModuleChooser adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.moduleselected(e);
	}
}

class ModuleChooser_jButton16_actionAdapter implements java.awt.event.ActionListener {
	private ModuleChooser adaptee;

	ModuleChooser_jButton16_actionAdapter(ModuleChooser adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.moduleselected(e);
	}
}

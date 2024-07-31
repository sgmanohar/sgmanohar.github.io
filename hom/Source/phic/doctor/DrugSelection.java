package phic.doctor;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import phic.*;
import phic.common.IniReader;
import phic.drug.NoSuchDrugException;

/**
 * A frame with a list of common drugs
 */
public class DrugSelection extends IFrame{
	private JPanel jPanel1=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private JPanel jPanel2=new JPanel();

	private JPanel jPanel3=new JPanel();

	private JButton injectbutton=new JButton();

	private BorderLayout borderLayout2=new BorderLayout();

	private JPanel jPanel4=new JPanel();

	private JComboBox catlist=new JComboBox();

	private DefaultComboBoxModel catlistmodel=new DefaultComboBoxModel();

	Image syringe=Resource.loader.getImageResource("doctor/SyringeCursor3.gif");

	ImageIcon syringeIcon=new ImageIcon(syringe);

	public DrugSelection(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		injectbutton.setIcon(syringeIcon);
		//infusebutton.setIcon(dripIcon);
		initialise();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFrameIcon(new ImageIcon(Resource.loader.getImageResource(
				"Pharmacy.jpg")));
	}

	private void jbInit() throws Exception{
		jPanel1.setLayout(borderLayout1);
		injectbutton.setToolTipText("Inject the drug intravenously");
		injectbutton.setText("Inject");
		injectbutton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				injectbutton_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout2);
		catlist.setToolTipText("Select the category of drugs to display");
		catlist.setModel(catlistmodel);
		catlist.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				catlist_actionPerformed(e);
			}
		});
		this.setTitle("Drugs");
		this.setToolTipText("List of drugs for oral or intravenous use");
		infusebutton.setToolTipText("Adds the drug to the current infusion");
		infusebutton.setText("Infuse");
		infusebutton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				infusebutton_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.CENTER);
		jPanel2.add(selectionFrame1,BorderLayout.CENTER);
		jPanel1.add(jPanel3,BorderLayout.SOUTH);
		jPanel3.add(injectbutton,null);
		jPanel3.add(infusebutton,null);
		jPanel1.add(jPanel4,BorderLayout.NORTH);
		jPanel4.add(catlist,null);
	}

	String file="doctor/WardDrugs.txt";

	String categoryListName="Categories";

	IniReader ir=new IniReader(file);

	private SelectionFrame selectionFrame1=new SelectionFrame();

	private JButton infusebutton=new JButton();

	/**
	 * Initialise the list of categories
	 */
	public void initialise(){
		catlistmodel=new DefaultComboBoxModel(ir.getSectionStrings(categoryListName));
		catlist.setModel(catlistmodel);
		catlist.setSelectedIndex(0);
		setPreferredSize(new Dimension(230,200));
	}

	/**
	 * Called when a category is selected.
	 * Creates the icons from the strings in the ini file.
	 */
	void catlist_actionPerformed(ActionEvent e){
		String druglist=catlist.getSelectedItem().toString();
		selectionFrame1.setupFromSection(ir,druglist);
		selectionFrame1.validate();
		selectionFrame1.repaint();
	}

	void injectbutton_actionPerformed(ActionEvent e){
//		this.getDesktopPane().setCursor(syringeCursor);
		try{
			Current.person.body.blood.add(selectionFrame1.getSelectedSubstance());
			Current.person.body.message("Injected "+selectionFrame1.getSelectedName());
		} catch(NoSuchDrugException x){
			JOptionPane.showInternalMessageDialog(this,x,"Could not inject drug",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	void infusebutton_actionPerformed(ActionEvent e){
		try{
			IntravenousInfusion ivi=frame.patientFrame.getCurrentInfusion();
			if(ivi==null){
				throw new Exception(
						"Please start an infusion first, from the Fluids menu.");
			}
			ivi.add(selectionFrame1.getSelectedSubstance());
			InfusionPanel p=frame.patientFrame.infusionPanel;
			String added=selectionFrame1.getSelectedName();
			p.setup(ivi,p.contentLabel.getText()+"+"+added,p.image);
			Current.person.body.message(added+"Added to infusion");
		} catch(Exception x){
			JOptionPane.showInternalMessageDialog(this,x,"Could not inject drug",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
package phic.doctor;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import phic.*;
import phic.common.IniReader;
import phic.drug.NoSuchDrugException;

/**
 * Select a fluid from the list
 */
public class FluidsSelection extends IFrame{
	private JPanel jPanel1=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private SelectionFrame selectionFrame1=new SelectionFrame(true);

	public FluidsSelection(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setupFromSection("Fluids");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFrameIcon(new ImageIcon(Resource.loader.getImageResource(
				"Container.gif")));
	}

	IniReader ir=new IniReader("doctor/Selections.txt");

	private JPanel jPanel2=new JPanel();

	private JButton infusebutton=new JButton();

	public void setupFromSection(String section){
		selectionFrame1.setupFromSection(ir,section);
		setPreferredSize(new Dimension(260,230));
		setMinimumSize(new Dimension(80,100));
	}

	private void jbInit() throws Exception{
		jPanel1.setLayout(borderLayout1);
		infusebutton.setToolTipText(
				"Begin infusing the selected fluid intravenously");
		infusebutton.setText("Start fluid");
		infusebutton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				infusebutton_actionPerformed(e);
			}
		});
		this.setTitle("Fluids");
		this.setToolTipText("The list of fluids available for intravenous infusion");
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(selectionFrame1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(infusebutton,null);
	}

	void infusebutton_actionPerformed(ActionEvent e){
		try{
			IntravenousInfusion ivi=new IntravenousInfusion(Current.person.body.blood);
			ivi.add(selectionFrame1.getSelectedSubstance());
			frame.patientFrame.setCurrentInfusion(ivi,
					selectionFrame1.getSelectedImage(),selectionFrame1.getSelectedName());
		} catch(NoSuchDrugException x){
			JOptionPane.showInternalMessageDialog(this,x,"Cannot infuse fluid",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
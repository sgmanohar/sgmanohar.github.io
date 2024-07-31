package phic.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import phic.Current;
import phic.common.Quantity;
import phic.drug.*;

/**
 * Added 4/1/03
 * Allows browsing of drugs in DrugContainers, and adding them to graphs.
 */
public class DrugConcentrationGraphChooser extends ModalDialog{
	private JPanel jPanel1=new JPanel();

	private JPanel jPanel2=new JPanel();

	private JButton closeButton=new JButton();

	private JButton showButton=new JButton();

	private BorderLayout borderLayout1=new BorderLayout();

	private JPanel jPanel3=new JPanel();

	private JPanel jPanel4=new JPanel();

	private JScrollPane jScrollPane1=new JScrollPane();

	private JList drugList=new JList();

	private JPanel jPanel5=new JPanel();

	private JTextField concentrationText=new JTextField();

	private DefaultListModel druglistmodel=new DefaultListModel();

	/**
	 * The containers whose drugs can be displayed
	 */
	DrugContainer[] containers=new DrugContainer[]{
                        Current.body.blood,
			(DrugContainer)Current.body.ecf,
                        (DrugContainer)Current.body.Fat.getDrugContainer(),
                        (DrugContainer)Current.body.bladder,
			(DrugContainer)Current.environment.stool,
			(DrugContainer)Current.body.kidney.urine,
			(DrugContainer)Current.body.gitract.stomach,
			(DrugContainer)Current.body.gitract.colon,
	};
	/**
	 * The names of the containers, each element corresponding to
	 * one in 'containers'.
	 */

	 String[] containerNames=new String[]{"Blood","ECF", "Fat", "Bladder","Stool","Urine",
			"Stomach","Colon"
	};

	private JComboBox containerList=new JComboBox(containerNames);

	private BorderLayout borderLayout2=new BorderLayout();

	private Border border1;

	private JLabel jLabel1=new JLabel();

	private BorderLayout borderLayout3=new BorderLayout();

	private Border border2;

	private Border border3;

	private JScrollPane jScrollPane2=new JScrollPane();

	private JTextArea drugdescription=new JTextArea();

	public DrugConcentrationGraphChooser(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(400,200));
		containerList.setSelectedIndex(0);
                getRootPane().setDefaultButton(closeButton);
	}

	DrugContainer currentContainer(){
		return containers[containerList.getSelectedIndex()];
	}

	private void jbInit() throws Exception{
		border1=BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(
				BevelBorder.LOWERED),BorderFactory.createEmptyBorder(6,6,6,6));
		border2=BorderFactory.createEmptyBorder(6,6,6,6);
		border3=BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		this.setTitle("Drug concentration viewer");
		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeButton_actionPerformed(e);
			}
		});
		showButton.setToolTipText("Shows the drug in the graph window");
		showButton.setText("Show");
		showButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				showButton_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		jPanel4.setLayout(borderLayout2);
		concentrationText.setPreferredSize(new Dimension(190,21));
		concentrationText.setToolTipText("Concentration of the drug");
		concentrationText.setText("0");
		concentrationText.setHorizontalAlignment(SwingConstants.RIGHT);
		drugList.setToolTipText(
				"Shows a list of drugs in the currently selected container");
		drugList.setModel(druglistmodel);
		drugList.addListSelectionListener(new javax.swing.event.
				ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				drugList_valueChanged(e);
			}
		});
		containerList.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				containerList_actionPerformed(e);
			}
		});
		jPanel4.setBorder(border1);
		jLabel1.setText("Container");
		containerList.setToolTipText(
				"Select the container from which to choose a drug");
		jPanel5.setLayout(borderLayout3);
		jPanel5.setBorder(border2);
		drugdescription.setBorder(border3);
		drugdescription.setToolTipText("Drug description");
		drugdescription.setEditable(false);
		drugdescription.setText("No drug selected");
		drugdescription.setLineWrap(true);
		drugdescription.setWrapStyleWord(true);
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel3,BorderLayout.NORTH);
		jPanel3.add(jLabel1,null);
		jPanel3.add(containerList,null);
		jPanel1.add(jPanel4,BorderLayout.CENTER);
		jPanel4.add(jScrollPane1,BorderLayout.CENTER);
		jPanel1.add(jPanel5,BorderLayout.EAST);
		jPanel5.add(concentrationText,BorderLayout.NORTH);
		jPanel5.add(jScrollPane2,BorderLayout.CENTER);
		jScrollPane2.getViewport().add(drugdescription,null);
		jScrollPane1.getViewport().add(drugList,null);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(showButton,null);
		jPanel2.add(closeButton,null);
	}

	void containerList_actionPerformed(ActionEvent e){
		DrugContainer c=currentContainer();
		druglistmodel.removeAllElements();
		for(int i=0;i<c.drugqs.size();i++){
			druglistmodel.addElement(c.drugqs.get(i));
		}
	}

	void closeButton_actionPerformed(ActionEvent e){
		hide();
	}

	void drugList_valueChanged(ListSelectionEvent e){
		Object o=drugList.getSelectedValue();
		if(o!=null){
			try{
				DrugQuantity q=(DrugQuantity)o;
				concentrationText.setText(q.toString());
				drugdescription.setText(Pharmacy.getDrugDescription(q.getName()));
			} catch(NoSuchDrugException x){
				drugdescription.setText(x.toString());
			}
		} else{
			concentrationText.setText("");
			drugdescription.setText("No drug selected");
		}
	}

	void showButton_actionPerformed(ActionEvent e){
		Object o=drugList.getSelectedValue();
		if(o==null){
			return;
		}
		if(PhicApplication.frame instanceof SimplePhicFrame){
			((SimplePhicFrame)PhicApplication.frame).graph.
					addNewVariable((Quantity)o,
					containerList.getSelectedItem().toString()+" "
					+((DrugQuantity)o).getName());
		}
	}
}

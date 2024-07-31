/**
 * Class to display a dialog box from which the user can choose a drug and the
 * Quantity to administer.
 */
package phic.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import phic.common.UnitConstants;
import phic.drug.*;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Iterator;

public class DrugDialog extends ModalDialog{
	JPanel jPanel1=new JPanel();

	JPanel jPanel2=new JPanel();

	JButton cancel=new JButton();

	public JButton OK=new JButton();

	BorderLayout borderLayout1=new BorderLayout();

	JScrollPane jScrollPane1=new JScrollPane();

	JList druglist=new JList();

	Box box1;

	JLabel drugname=new JLabel();

	JSlider amountslider=new JSlider();

	JLabel amounttext=new JLabel();

	JScrollPane jScrollPane2=new JScrollPane();

	Border border1;

	JTextArea descriptiontext=new JTextArea();

	Border border2;
        JTabbedPane jTabbedPane1 = new JTabbedPane();
        JScrollPane jScrollPane3 = new JScrollPane();
        JTable drugpropstable = new JTable();

	public DrugDialog(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(350,300));
		druglist.setListData(Pharmacy.getDrugList());
                getRootPane().setDefaultButton(OK);
	}

	private void jbInit() throws Exception{
		box1=Box.createVerticalBox();
		border1=BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2,2,2,2));
		border2=BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(4,4,4,4));
		this.setTitle("Choose drug from pharmacy");
		cancel.setText("Cancel");
		cancel.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel_actionPerformed(e);
			}
		});
		OK.setText("OK");
		OK.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				OK_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		drugname.setFont(new java.awt.Font("Dialog",1,12));
		drugname.setAlignmentY((float)0.0);
		drugname.setToolTipText("Name of drug");
		drugname.setText("Drug");
		amounttext.setAlignmentY((float)0.0);
		amounttext.setToolTipText("Amount of drug");
		amounttext.setText("0 mg");
		jScrollPane1.setBorder(border2);
		jScrollPane1.setPreferredSize(new Dimension(160,132));
		amountslider.addChangeListener(new javax.swing.event.ChangeListener(){
			public void stateChanged(ChangeEvent e){
				amountslider_stateChanged(e);
			}
		});
		druglist.addListSelectionListener(new javax.swing.event.
				ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				druglist_valueChanged(e);
			}
		});
		jScrollPane2.setBorder(border1);
		amountslider.setValue(0);
		druglist.setToolTipText("List of drugs");
		descriptiontext.setText("Description");
		descriptiontext.setLineWrap(true);
		descriptiontext.setWrapStyleWord(true);
		descriptiontext.setBackground(SystemColor.control);
		descriptiontext.setEditable(false);
		descriptiontext.setFont(new java.awt.Font("SansSerif",0,12));
                this.getContentPane().add(jPanel1, BorderLayout.CENTER);
                jPanel1.add(jScrollPane1, BorderLayout.WEST);
                jPanel1.add(box1, BorderLayout.CENTER);
                box1.add(jTabbedPane1, null);
                jTabbedPane1.add(jScrollPane2, "Description");
                jTabbedPane1.add(jScrollPane3, "Properties");
                jScrollPane3.getViewport().add(drugpropstable, null);
                jScrollPane2.getViewport().add(descriptiontext, null);
                box1.add(drugname, null);
                box1.add(amountslider, null);
		box1.add(amounttext,null);
		jScrollPane1.getViewport().add(druglist,null);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(OK,null);
		jPanel2.add(cancel,null);
                drugpropstable.setModel(tablemodel);
 	}
        DefaultTableModel tablemodel = new DefaultTableModel(
          new String[]{"Property", "Value"}, 0
        ){public boolean isCellEditable(int i, int j){ return false ; }};

	/**
	 * If a drug is selected, display its description, and set the amount
	 * slider to the default value and range for that drug.
	 */
	void druglist_valueChanged(ListSelectionEvent e){
		Object o=druglist.getSelectedValue();
		if(o!=null){
			drug=(String)druglist.getSelectedValue();
			drugname.setText(drug);
			try{
				descriptiontext.setText(Pharmacy.getDrugDescription(drug));
				middleAmount=Pharmacy.getSingleDrugDose(drug);
                                Map m=Pharmacy.getDrugMap(drug);  // add drug properties to table
                                tablemodel.setRowCount(0);
                                for(Iterator i=m.keySet().iterator();i.hasNext();){
                                  Object k=i.next();
                                  tablemodel.addRow(new Object[]{k,m.get(k)});
                                }
			} catch(Exception ex){
				ex.printStackTrace();
			}
			amountslider.setValue(0);
			amountslider.setValue(sliderMid);
		}
	}

        /** Not implemented */
        Action createNewDrug = new AbstractAction("Create new drug"){
          public void actionPerformed(ActionEvent e){

          }
        };

	/** Name of drug */
	protected String drug;

	/** Normal dose of drug */
	protected double middleAmount;

	/** Selected dose of drug */
	protected double quantity;

	/** The range to display on the slider */
	protected int sliderMid=50,minExponent=-2;

	/**
	 * Calculate how much drug is selected (@link #quantity quantity)
	 * each time the slider changes. Update the label accordingly.
	 */
	protected void amountslider_stateChanged(ChangeEvent e){
		//calculate the amount from the slider's position
		int v=amountslider.getValue();
		double exponent=(v-sliderMid)*(-minExponent)/(double)sliderMid;
		quantity=middleAmount*Math.pow(10,exponent);
		try{
			amounttext.setText(UnitConstants.formatValue(quantity,
					Pharmacy.getDrugUnit(drug), false));
		} catch(NoSuchDrugException x){}
	}

	/**
	 * Has OK been pressed? This is to inform the caller whether to add the
	 * selected drug or not.
	 */
	private boolean OKpressed=false;

	private void OK_actionPerformed(ActionEvent e){
		OKpressed=true;
		hide();
	}

	private void cancel_actionPerformed(ActionEvent e){
		hide();
	}

	/** retain a record of the last drug that was obtained */
	private DrugContainer lastDispensedDrug=null;

	//Public functions
	/** Request selected drug from pharmacy */
	public DrugContainer getDrugContainer(){
		if(OKpressed){
			try{
				OKpressed=false;
				return lastDispensedDrug=Pharmacy.dispenseAmpoule(drug,quantity);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

        /**
         * Returns a name for the drug.
         * @return the name of the drug
         */
        public String getDrugName(){
          return drug;
        }
	/** Get a label for the drug. This includes the quantity of drug. */
	public String getDrugLabel(){
		if(lastDispensedDrug==null){
			return drug+" "+amounttext.getText();
		} else{
			DrugQuantity q=((DrugQuantity)lastDispensedDrug.drugqs.get(0));
			return q.getName()+" "+q.formatValue(q.get(),true,false);
		}
	}
}

package phic.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreeNode;
import phic.common.*;
import java.awt.event.*;

/**
 * The graphical interface for the LifeSupport class.
 */
public class LifeSupportDialog extends ModalDialog{
	private JPanel jPanel1=new JPanel();

	private JPanel jPanel2=new JPanel();

	private JButton jButton1=new JButton();

	private JPanel jPanel3=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private BorderLayout borderLayout2=new BorderLayout();

	private JPanel jPanel4=new JPanel();

	private JScrollPane jScrollPane2=new JScrollPane();

	private JList varlist=new JList();

	private BorderLayout borderLayout3=new BorderLayout();

	private JPanel jPanel5=new JPanel();

	private JButton addvar=new JButton();

	private JButton removevar=new JButton();

	private JPanel jPanel6=new JPanel();

	private JLabel jLabel1=new JLabel();

	protected LifeSupportDialog(){
		init();
	}

	public LifeSupportDialog(LifeSupport ls){
		lifeSupport=ls;
		init();
		updateList();
                getRootPane().setDefaultButton(jButton1);
	}

	protected void init(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(550,250));
	}

	private void jbInit() throws Exception{
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		jPanel3.setLayout(borderLayout2);
		jPanel4.setLayout(borderLayout3);
		jScrollPane2.setPreferredSize(new Dimension(200,100));
		addvar.setText("Add >>");
		addvar.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				addvar_actionPerformed(e);
			}
		});
		removevar.setText("<< Remove");
		removevar.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				removevar_actionPerformed(e);
			}
		});
		this.setTitle("Clamp variable values");
		jLabel1.setText(
				"Select which variables will be artificially held constant");
		selectAllButton.setText("Select all");
		selectAllButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectAllButton_actionPerformed(e);
			}
		});
		jPanel7.setLayout(borderLayout4);
		jLabel2.setText("Value");
		valuetxt.setColumns(8);
    valuetxt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        valuetxt_focusLost(e);
      }
    });
		valuetxt.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				valuetxt_actionPerformed(e);
			}
		});
		varlist.addListSelectionListener(new javax.swing.event.
				ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				varlist_valueChanged(e);
			}
		});
//		bodyTree1.setPreferredSize(new Dimension(200,363));
    jPanel9.setLayout(borderLayout5);
    variableSelectionCombo1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        variableSelectionCombo1_actionPerformed(e);
      }
    });


    jPanel10.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(3);
    this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel3,BorderLayout.CENTER);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(jButton1,null);
		this.getContentPane().add(jPanel6,BorderLayout.NORTH);
		jPanel6.add(jLabel1,null);
		jPanel3.add(jPanel4,BorderLayout.EAST);
		jPanel4.add(jScrollPane2,BorderLayout.CENTER);
		jPanel4.add(jPanel7,BorderLayout.SOUTH);
		jPanel7.add(jPanel8,BorderLayout.CENTER);
		jPanel8.add(valuetxt,null);
		jPanel8.add(unittxt,null);
		jPanel7.add(jLabel2,BorderLayout.WEST);
		jPanel3.add(jPanel5,BorderLayout.CENTER);
    jPanel5.add(jPanel10, null);
    jPanel10.add(selectAllButton, null);
    jPanel10.add(removevar, null);
    jPanel10.add(addvar, null);
    jPanel3.add(jPanel9,  BorderLayout.WEST);
//    jPanel9.add(bodyTree1, BorderLayout.CENTER);
    jPanel9.add(variableSelectionCombo1, BorderLayout.NORTH);
    variableSelectionCombo1.setPreferredSize(new Dimension(160,variableSelectionCombo1.getPreferredSize().height));
		jScrollPane2.getViewport().add(varlist,null);
	}

	void jButton1_actionPerformed(ActionEvent e){
		hide();
	}

	protected LifeSupport lifeSupport;


	private JButton selectAllButton=new JButton();

	private JPanel jPanel7=new JPanel();

	private JPanel jPanel8=new JPanel();

	private BorderLayout borderLayout4=new BorderLayout();

	JTextField valuetxt=new JTextField();

	private JLabel jLabel2=new JLabel();

	private JLabel unittxt=new JLabel();

	protected void updateList(){
		DefaultListModel lm=new DefaultListModel();
		for(int i=0;i<Variables.variable.length;i++){
			if(lifeSupport.isFudgeVariable(i)){
				lm.addElement(Variables.variable[i]);
			}
		}
		varlist.setModel(lm);
	}

	void addvar_actionPerformed(ActionEvent e){
          Object o =
null; /*                bodyTree1.tree.getSelectionPath().getLastPathComponent() */;
          if (o instanceof Node) {
            Node n = (Node) o;
            addNode(n);
            return;

          }else{
            o = variableSelectionCombo1.getSelectedItem();
            if( o instanceof VisibleVariable ) {
              addNode(((VisibleVariable)o).node);
              return;
            }
          }
          if(o==null)o="Null";
          JOptionPane.showMessageDialog(null,
                "Cannot apply clamp to "+o.toString(),"Invalid variable",
                                        JOptionPane.ERROR_MESSAGE);
	}

	protected void addNode(Node n){
		VisibleVariable v=Variables.forNode(n);
		if(v!=null&&n.isSettable()){
			lifeSupport.setFudgeVariable(v,true);
			updateList();
			return;
		}
		for(int i=0;i<n.getChildCount();i++){
			TreeNode nn=n.getChildAt(i);
			if(nn instanceof Node){
				addNode((Node)nn);
			}
		}
	}

	void removevar_actionPerformed(ActionEvent e){
		Object[] o=varlist.getSelectedValues();
		for(int i=0;i<o.length;i++){
			if(o[i] instanceof VisibleVariable){
				VisibleVariable v=(VisibleVariable)o[i];
				lifeSupport.setFudgeVariable(v,false);
				updateList();
			} else{
				throw new RuntimeException("Unexpected non-variable in variable list: "
						+o);
			}
		}
	}

	void selectAllButton_actionPerformed(ActionEvent e){
		varlist.setSelectionInterval(0,varlist.getModel().getSize()-1);
	}

        /** keep track of what power of ten the current unit being edited is using */
        double unit;
  JPanel jPanel9 = new JPanel();
//  BodyTree bodyTree1 = new BodyTree();
  BorderLayout borderLayout5 = new BorderLayout();
  VariableSelectionCombo variableSelectionCombo1 = new VariableSelectionCombo();
  VisibleVariable currentVar=null;
  JPanel jPanel10 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();

  /** Select a variable in the right pane (if it is being fudged) */
  public void setSelection(VisibleVariable v){
    if(lifeSupport.isFudgeVariable(v))
      varlist.setSelectedValue(v,true);
  }
	/** Called when a life supported variable is selected, and displays the fixed value. */
	void varlist_valueChanged(ListSelectionEvent e){
		currentVar = (VisibleVariable)varlist.getSelectedValue();
                VisibleVariable v = currentVar;
		if(v!=null){
                        double val = lifeSupport.getFudgeValue(v);
                        String s = v.formatValue(val,true,false);
                        int i;
			valuetxt.setText(s.substring(0,i=s.indexOf(' ')));
			unittxt.setText(s.substring(i+1));
			valuetxt.setEnabled(true);
                        unit = UnitConstants.getUnitMultiplier(val, v.units);
                        valuetxt.grabFocus();
		} else{
			unittxt.setText("");
			valuetxt.setText("");
			valuetxt.setEnabled(false);
		}
	}

	/**
	 * Called if the fixed value is edited. If box is empty, then use the initial
	 * value of the variable.
	 */
	void valuetxt_actionPerformed(ActionEvent e){
          updateValue();
	}
        /**
         * Called if the fixed value box loses its focus: update the
         * varible's fixed value from the text box.
         */
        void valuetxt_focusLost(FocusEvent e) {
          updateValue();
        }

        /**
         * Read the value in the text box and use it to fix the variable's
         * value, by calling setFudgeValue on the body's LifeSupport object.
         */
        void updateValue(){
		VisibleVariable v=currentVar;
		if(v!=null){
			String s=valuetxt.getText();
			if(s==""){
				lifeSupport.setFudgeValue(v,v.node.getVDouble().initialValue);
				valuetxt.setText(v.formatValue(lifeSupport.getFudgeValue(v),false,true));
			}
			try{
				double value=Double.parseDouble(s);
				lifeSupport.setFudgeValue(v,value*unit);
			} catch(NumberFormatException x){
				x.printStackTrace();
				JOptionPane.showMessageDialog(this,"Not a valid number: "+s);
			}
		}
	}
        /**
         * Called when a selection is made in the variable combo box.
         */
        void variableSelectionCombo1_actionPerformed(ActionEvent e) {
          Object o = variableSelectionCombo1.selectedVariable;
          if(o instanceof VisibleVariable){
            VisibleVariable vv=(VisibleVariable)o;
            addNode(vv.node);
          }
        }
}

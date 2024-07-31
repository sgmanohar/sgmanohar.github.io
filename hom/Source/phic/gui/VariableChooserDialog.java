package phic.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Dialog box containing a VariableChooser to choose a variable either from
 * the tree or from a list.
 */
public class VariableChooserDialog extends ModalDialog{
	private JPanel jPanel1=new JPanel();

	private JPanel jPanel2=new JPanel();

	private JButton OK=new JButton();

	private JButton jButton1=new JButton();

	public VariableChooserDialog(){
		setSize(300,300);
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setTitle("Choose a variable");
	}

	private void jbInit() throws Exception{
		OK.setText("OK");
		OK.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				OK_actionPerformed(e);
			}
		});
		jButton1.setText("Cancel");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		jPanel1.add(variableChooser1,BorderLayout.CENTER);
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(OK,null);
		jPanel2.add(jButton1,null);
	}

	boolean okpressed=false;

	void OK_actionPerformed(ActionEvent e){
		okpressed=true;
		variable=variableChooser1.variable;
		hide();
	}

	void jButton1_actionPerformed(ActionEvent e){
		variable=null;
		hide();
	}

	VisibleVariable variable;

	private BorderLayout borderLayout1=new BorderLayout();

	VariableChooser variableChooser1=new VariableChooser();

	public VisibleVariable getVariable(){
		return variable;
	}
}
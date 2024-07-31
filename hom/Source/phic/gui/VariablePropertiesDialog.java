package phic.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.Dimension;
import phic.Resource;
import java.awt.event.ActionListener;

/**
 * Display information on the given variable, in a dialog box.
 * Uses an instance of VariablePropertiesPanel.
 */
public class VariablePropertiesDialog extends ModalDialog{
	BorderLayout borderLayout1=new BorderLayout();

	VariablePropertiesPanel panel=new VariablePropertiesPanel();

	JPanel jPanel1=new JPanel();

	JButton jButton1=new JButton();

	JButton jButton2=new JButton();

	public VariablePropertiesDialog(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(300,250));
                getRootPane().setDefaultButton(jButton2);
                panel.addVariableListener(varlisten);
	}
        ActionListener varlisten = new ActionListener(){
          public void actionPerformed(ActionEvent e){
            String s=e.getActionCommand();
            setTitle(Resource.identifierToText( s ));
          }
        };

	/** Set the visible variable to display in this dialog box. */
	public void setVariable(VisibleVariable v){
		setTitle(Resource.identifierToText( v.longName ));
		panel.setVariable(v);
	}

	private void jbInit() throws Exception{
		this.getContentPane().setLayout(borderLayout1);
		jButton1.setText("Cancel");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		jButton2.setText("OK");
		jButton2.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton2_actionPerformed(e);
			}
		});
		this.getContentPane().add(panel,BorderLayout.CENTER);
		this.getContentPane().add(jPanel1,BorderLayout.SOUTH);
		jPanel1.add(jButton1,null);
		jPanel1.add(jButton2,null);
	}

	void jButton2_actionPerformed(ActionEvent e){
		//put inputs back from pane to variable
		panel.transferBack();
		hide();
	}

	void jButton1_actionPerformed(ActionEvent e){
		hide();
	}
}

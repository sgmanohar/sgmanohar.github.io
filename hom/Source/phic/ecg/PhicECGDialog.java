package phic.ecg;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import phic.Current;
import phic.gui.ModalDialog;
import java.awt.Dimension;

/**
 * Dialog to draw ECG for Phic
 */
public class PhicECGDialog extends ModalDialog{
	private JPanel jPanel1=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private JPanel jPanel2=new JPanel();

	private JPanel jPanel3=new JPanel();

	private JButton okButton=new JButton();

	private BorderLayout borderLayout2=new BorderLayout();

	private Trace trace1=new Trace();

	public Heart heart=new PhicHeart(Current.body);

	public PhicECGDialog(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(600,430));
		trace1.heart=heart;
	}

	private void jbInit() throws Exception{
		this.setTitle("ECG");
		jPanel1.setLayout(borderLayout1);
		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				okButton_actionPerformed(e);
			}
		});
		jPanel3.setLayout(borderLayout2);
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(okButton,null);
		jPanel1.add(jPanel3,BorderLayout.CENTER);
		jPanel3.add(trace1,BorderLayout.CENTER);
	}

	void okButton_actionPerformed(ActionEvent e){
		hide();
	}
}
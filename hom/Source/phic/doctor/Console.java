package phic.doctor;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import phic.gui.PhicEvaluator;

/**
 * Console window for the engine's output
 */
public class Console extends IFrame implements phic.common.TextReceiver{
	private JPanel jPanel1=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private JScrollPane jScrollPane1=new JScrollPane();

	private PhicEvaluator phicEvaluator1=new PhicEvaluator();

	JPanel jPanel2=new JPanel();

	JTextField jTextField1=new JTextField();

	BorderLayout borderLayout2=new BorderLayout();

	JButton jButton1=new JButton();

	public Console(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setSize(240,200);
	}

	public void message(String s){
		try{
			int p=phicEvaluator1.getDocument().getEndPosition().getOffset();
			p--;
			if(p<0){
				p=0;
			}
			phicEvaluator1.getDocument().insertString(p,s+'\n',null);
			phicEvaluator1.setCaretPosition(p+1);
		} catch(BadLocationException e){
			e.printStackTrace();
		}
		if(s.indexOf("Dead.")>=0){ //moved to body
			frame.alert("Your patient has died.","Died");
		}
	}

	public void error(String s){
		message(s);
	}

	private void jbInit() throws Exception{
		jPanel1.setLayout(borderLayout1);
		phicEvaluator1.setToolTipText("Raw output");
		phicEvaluator1.setText("PHIC\n");
		this.setTitle("Console");
		this.setPreferredSize(new Dimension(220,184));
		jTextField1.setToolTipText("Type commands here");
		jTextField1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jTextField1_actionPerformed(e);
			}
		});
		jPanel2.setLayout(borderLayout2);
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jScrollPane1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(jTextField1,BorderLayout.CENTER);
		jPanel2.add(jButton1,BorderLayout.EAST);
		jScrollPane1.getViewport().add(phicEvaluator1,null);
	}

	void jButton1_actionPerformed(ActionEvent e){
		doInput();
	}

	void jTextField1_actionPerformed(ActionEvent e){
		doInput();
	}

	public void doInput(){
		String s=jTextField1.getText();
		jTextField1.setText("");
	}
}

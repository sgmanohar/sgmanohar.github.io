package phic.ecg;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import sanjay.PropertiesPanel;

/**
 * Test frame for ECG
 */
public class TestApp extends JFrame{
	private JPanel jPanel1=new JPanel();

	private JPanel jPanel2=new JPanel();

	private JButton jButton1=new JButton();

	private BorderLayout borderLayout1=new BorderLayout();

	private JSplitPane jSplitPane1=new JSplitPane();

	private Trace trace1=new Trace();

	private JScrollPane jScrollPane1=new JScrollPane();

	private PropertiesPanel properties=new PropertiesPanel();

	private SimpleHeart heart=new SimpleHeart();

	public TestApp(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setSize(700,400);
		trace1.heart=heart;
		properties.setRootObject(heart,"Heart");
		properties.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				trace1.repaint();
			}
		});
	}

	private void jbInit() throws Exception{
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		jPanel1.setLayout(borderLayout1);
		this.setDefaultCloseOperation(3);//exit on close
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jSplitPane1,BorderLayout.CENTER);
		jSplitPane1.add(trace1,JSplitPane.RIGHT);
		jSplitPane1.add(jScrollPane1,JSplitPane.LEFT);
		jScrollPane1.getViewport().add(properties,null);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(jButton1,null);
	}

	public static void main(String[] s){
		TestApp a=new TestApp();
		a.show();
	}

	void jButton1_actionPerformed(ActionEvent e){
		trace1.executeTrace();
	}
}
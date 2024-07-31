package phic.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.*;
import phic.Current;
import phic.common.IniReader;

/**
 * Modal dialog reading list of screens from the file, and displaying the
		 * appropriate lists of values. The values displayed are in the file Screens.txt.
 */
public class ScreensDialog extends ModalDialog{
	JPanel jPanel1=new JPanel();

	JPanel jPanel2=new JPanel();

	JPanel jPanel3=new JPanel();

	JButton jButton1=new JButton();

	JLabel nametxt=new JLabel();

	JLabel descriptxt=new JLabel();

	Border border1;

	BorderLayout borderLayout1=new BorderLayout();

	Border border2;

	String filename="Screens.txt";

	public ScreensDialog(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		myinit();
                getRootPane().setDefaultButton(jButton1);
	}

	private void jbInit() throws Exception{
		border1=BorderFactory.createEmptyBorder(5,5,5,5);
		border2=BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		jButton1.setText("OK");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});
		nametxt.setFont(new java.awt.Font("Dialog",1,12));
		nametxt.setText(Current.person.name);
		descriptxt.setText(Current.person.description);
		jPanel1.setLayout(borderLayout1);
		this.setTitle("Medical records");
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(tabbedpane,BorderLayout.CENTER);
		this.getContentPane().add(jPanel2,BorderLayout.SOUTH);
		jPanel2.add(jButton1,null);
		this.getContentPane().add(jPanel3,BorderLayout.NORTH);
		jPanel3.add(nametxt,null);
		jPanel3.add(descriptxt,null);
	}

	JPanel[] panels;

	JTabbedPane tabbedpane=new JTabbedPane();

	private final boolean scrollable=true;

	void myinit(){
		setSize(new Dimension(460,480));
		IniReader ir=new IniReader(filename);
		String[] head=ir.getSectionHeaders();
		JPanel[] panel=new JPanel[head.length];
		for(int i=0;i<head.length;i++){
			if(scrollable){
				JScrollPane sp=new JScrollPane();
				sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				sp.setBorder(null);
				panel[i]=new JPanel();
				panel[i].setLayout(new BoxLayout(panel[i],BoxLayout.Y_AXIS));
				sp.getViewport().setView(panel[i]);
				tabbedpane.add(head[i],sp);
			} else{
				panel[i]=new JPanel();
				tabbedpane.add(head[i],panel[i]);
			}
			String[] item=ir.getSectionStrings(head[i]);
			for(int j=0;j<item.length;j++){
				String name=item[j].trim();
				if(!name.equals("")){
					VisibleVariable v=Variables.forName(name);
					panel[i].add(new ValueRangeLabel(v));
				}
			}
		}
	}
}

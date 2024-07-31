package phic.gui.exam;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Dimension;

/**
 * Test the eye examination panel
 */
public class EyeTestDialog extends JDialog{
	private JPanel jPanel1=new JPanel();

	private BorderLayout borderLayout1=new BorderLayout();

	private JPanel jPanel2=new JPanel();

	private JPanel jPanel3=new JPanel();

	private JButton jButton1=new JButton();

	private JPanel jPanel4=new JPanel();

	private BorderLayout borderLayout2=new BorderLayout();

	private JPanel jPanel5=new JPanel();

	private Box box1;

	private JPanel epanel0=new JPanel();

	private JPanel epanel1=new JPanel();

	private JPanel jPanel8=new JPanel();

	private BorderLayout borderLayout3=new BorderLayout();

	private EyeExaminationPanel exam=new EyeExaminationPanel();

	private BorderLayout borderLayout4=new BorderLayout();

	public static void main(String[] s){
		new EyeTestDialog().show();
	}

	public EyeTestDialog(){
		setSize(new Dimension(800,300));
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		epanel0.add(new EyeControlPanel(exam.left));
		epanel1.add(new EyeControlPanel(exam.right));
	}

	private void jbInit() throws Exception{
		box1=Box.createHorizontalBox();
		jPanel1.setLayout(borderLayout1);
		jButton1.setText("OK");
		jPanel2.setLayout(borderLayout2);
		epanel0.setBorder(BorderFactory.createEtchedBorder());
		epanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel5.setLayout(borderLayout3);
		jPanel4.setLayout(borderLayout4);
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.CENTER);
		jPanel2.add(jPanel4,BorderLayout.CENTER);
		jPanel4.add(exam,BorderLayout.CENTER);
		jPanel2.add(jPanel5,BorderLayout.EAST);
		jPanel5.add(jPanel8,BorderLayout.SOUTH);
		jPanel5.add(box1,BorderLayout.CENTER);
		box1.add(epanel0,null);
		box1.add(epanel1,null);
		jPanel1.add(jPanel3,BorderLayout.SOUTH);
		jPanel3.add(jButton1,null);
	}

	class EyeControlPanel extends JPanel implements ActionListener{
		Eye eye;
		//actions

		protected void select(int i,boolean b){
			switch(i){
			case 0:
				eye.opticPalsy=b?1:0;
				break;
			case 1:
				eye.oculomotorPalsy=b;
				break;
			case 2:
				eye.trochlearPalsy=b;
				break;
			case 3:
				eye.abducentPalsy=b;
				break;
			case 4:
				eye.sympatheticPalsy=b?1:0;
				break;
			case 5:
				eye.parasympatheticPalsy=b?1:0;
			break;
			}
		}

		//names
		String[] cbn={"Optic palsy","Oculomotor palsy","Trochlear palsy",
				"Abducent palsy","Sympathetic palsy","Parasympathetic palsy"
		};

		public EyeControlPanel(Eye e){
			eye=e;
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			for(int i=0;i<cbn.length;i++){
				cb[i]=new JCheckBox(cbn[i]);
				add(cb[i]);
				cb[i].addActionListener(this);
			}
		}

		public void actionPerformed(ActionEvent e){
			for(int i=0;i<cbn.length;i++){
				if(cbn[i].equals(e.getActionCommand())){
					select(i,cb[i].isSelected());
				}
			}
		}

		JCheckBox[] cb=new JCheckBox[cbn.length];
	}
}

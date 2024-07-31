package phic.gui.exam;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import medicine.Entity;
import phic.Body;

/**
 * This examines the patients reflexes
 */
public class ReflexExamination extends JPanel implements Examination{
	private BorderLayout borderLayout1=new BorderLayout();

	private Box box1;

	private JPanel jPanel1=new JPanel();

	private JPanel jPanel2=new JPanel();

	private Border border1;

	private Border border2;

	private JPanel rightreflexes=new JPanel();

	private BorderLayout borderLayout2=new BorderLayout();

	private BorderLayout borderLayout3=new BorderLayout();

	private JPanel leftreflexes=new JPanel();

	private JLabel jLabel1=new JLabel();

	private JLabel jLabel2=new JLabel();

	public JPanel createPanel(){
		return this;
	}

	public void initialise(Body body){
		// set this to reflect spasticity etc.
                int[] l = {2,2,2,2,2,2}, r=l;
                if(body.brain.getFeeling()==body.brain.DEAD){ //brain dead
                  l = r = new int[]{0,0,0,0,0,0};
                }else if(body.icf.volume.get()>42){ // cerebral oedema
                  l = r = new int[]{3,3,3,3,3,3,3};
                }
                double temp=body.Temp.get();
                double thyr=body.blood.Thyr.get();
                if(temp<26 || thyr<1.2E-11){
                  l = r = new int[]{1,1,1,1,1,1}; //hypothermic hyporeflexia
                }else if(temp<32 || thyr<1E-11){
                  l = r = new int[]{0,0,0,0,0,0}; //hypothermic areflexia
                }
                left.setState(l);
                right.setState(r);
	}

	public Entity[] getPathologies(){
		return null;
	}

	public Entity[] getSigns(){
		return null;
	}

	public String getName(){
		return "Reflexes";
	}

	public String toString(){
		return getName();
	}

	public ReflexExamination(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		leftreflexes.add(left=new SideDisplay());
		rightreflexes.add(right=new SideDisplay());
	}

	SideDisplay left,right;

	private void jbInit() throws Exception{
		box1=Box.createHorizontalBox();
		border1=BorderFactory.createEmptyBorder(5,5,5,5);
		border2=BorderFactory.createEmptyBorder(5,5,5,5);
		this.setLayout(borderLayout1);
		jPanel1.setBorder(border1);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setBorder(border2);
		jPanel2.setLayout(borderLayout3);
		rightreflexes.setBorder(BorderFactory.createLoweredBevelBorder());
		leftreflexes.setBorder(BorderFactory.createLoweredBevelBorder());
		jLabel1.setText("Right");
		jLabel2.setText("Left");
		this.add(box1,BorderLayout.CENTER);
		box1.add(jPanel1,null);
		jPanel1.add(rightreflexes,BorderLayout.CENTER);
		jPanel1.add(jLabel1,BorderLayout.NORTH);
		box1.add(jPanel2,null);
		jPanel2.add(leftreflexes,BorderLayout.CENTER);
		jPanel2.add(jLabel2,BorderLayout.NORTH);
	}

	static final int R_ABSENT=0,R_DECREASED=1,R_NORMAL=2,R_BRISK=3;

	static final String[] ds={"Absent","Diminished","Normal","Brisk"
	};

	static final String[] ankleds={"Absent","Downgoing","Downgoing","Upgoing"
	};

	class SideDisplay extends JPanel{
		final String[] cs={"Triceps","Biceps","Wrist","Knee","Ankle","Plantar"
		};

		/** Reflex state */
		int[] state=new int[6];

		JLabel[] label=new JLabel[cs.length];

		JLabel[] val=new JLabel[cs.length];

		public SideDisplay(){
			setLayout(new GridLayout(6,2));
			setState(new int[]{2,2,2,2,2,2});
		}

		void setState(int[] state){
			this.state=state;
			removeAll();
			for(int i=0;i<cs.length;i++){
				String s=ds[state[i]];
				if(i==5){
					s=ankleds[state[i]];
				}
				label[i]=new JLabel(cs[i]+": ");
				val[i]=new JLabel(s);
				add(label[i]);
				add(val[i]);
			}
		}
	}
        public double getUpdateFrequencySeconds(){ return 1;}

}

package phic.doctor;
import java.awt.*;
import javax.swing.*;
import phic.*;

/**
		 * The patient frame, which has a picture of the patient, and interacts with the
 * current phic person's body and environment.
 * This is a persistent frame.
 */
public class PatientFrame extends IFrame{
	private IntravenousInfusion currentInfusion;

	protected InfusionPanel infusionPanel=new InfusionPanel();

	private JPanel mainPanel=new JPanel(){
		/** Draw the image centred in the frame */
		public void paint(Graphics g){
			int w=image.getWidth(this),h=image.getHeight(this);
			g.setColor(getBackground());
			g.fillRect(0,0,getWidth(),getHeight());
			if(image!=null){
				g.drawImage(image,(getWidth()-w)/2,(getHeight()-h)/2,this);
			}
			super.paint(g);
		}
	};

	protected Image image=Resource.loader.getImageResource(
			"doctor/patient/Patient1.gif");

	protected Color imageBackground=new Color(140,132,123);

	public PatientFrame(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		mainPanel.setBackground(imageBackground);
		setSize(400,500);
	}

	public IntravenousInfusion getCurrentInfusion(){
		return currentInfusion;
	}

	/**
	 * Change the current infusion
	 * @todo alter the picture according to what is being infused!
	 */
	public void setCurrentInfusion(IntravenousInfusion infusion,Image image,
			String name){
		if(currentInfusion!=infusion){
			if(currentInfusion!=null){
				currentInfusion.empty();
				currentInfusion.empty=true;
				currentInfusion.autoRefill=false;
				infusionPanel.setVisible(false);
			}
			//the environment will now automatically stop and remove this infusion
			if(infusion!=null){
				Current.environment.addInfusion(infusion);
				infusion.start();
				infusionPanel.setVisible(true);
			}
			currentInfusion=infusion;
		}
		infusionPanel.setup(infusion,name,image);
		updateDisplay();
	}

	public void updateDisplay(){
		if(infusionPanel.isVisible()){
			infusionPanel.updateFullness();
		}
	}

	private void jbInit() throws Exception{
		mainPanel.setBackground(UIManager.getColor("window"));
		mainPanel.setOpaque(false);
		mainPanel.setLayout(null);
		infusionPanel.setVisible(false);
		infusionPanel.setBounds(new Rectangle(4,4,198,165));
		this.setTitle("Patient");
		this.getContentPane().add(mainPanel,BorderLayout.CENTER);
		mainPanel.add(infusionPanel);
	}
}
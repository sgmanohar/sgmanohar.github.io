package phic.doctor;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import phic.IntravenousInfusion;

/**
 * Infusion panel for the doctor interface. This appears as part of the Patient
 * window, and is semi-transparent.
 * It shows the picture of the fluid, and sliders to represent the rate of infusion
 * and the volume left in the bag.
 */
public class InfusionPanel extends JPanel{
	private JPopupMenu jPopupMenu1=new JPopupMenu();

	private JMenuItem jMenuItem1=new JMenuItem();

	private BorderLayout borderLayout1=new BorderLayout();

	private JPanel centrepanel=new JPanel();

	private JPanel sliderpanel=new JPanel();

	private BorderLayout borderLayout2=new BorderLayout();

	private JSlider rateSlider=new JSlider();

	private JPanel jPanel1=new JPanel();

	private JLabel jLabel1=new JLabel();

	private JSlider volumeSlider=new JSlider();

	private BorderLayout borderLayout3=new BorderLayout();

	private JLabel jLabel2=new JLabel();

	private BorderLayout borderLayout4=new BorderLayout();

	public JLabel contentLabel=new JLabel();

	private Border border1;

	/** Draw the background as transparent */
	public void paint(Graphics g){
		//Graphics2D g2=(Graphics2D)g;
		Color bg=getBackground();
		g.setColor(new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),128));
		g.fillRect(0,0,getWidth(),getHeight());
		super.paint(g);
	}

	/** The panel that draws the picture of the fluid */
	JPanel picturepanel=new JPanel(){
		public void paint(Graphics g){
			super.paint(g);
			if(image!=null){
				g.drawImage(image,0,0,this);
			}
		}
	};

	public InfusionPanel(){
		init();
	}

	/** The image to display in drip's panel */
	Image image=null;
	/** The intravenous infusion that this panel represents */

	IntravenousInfusion infusion;

	private JLabel jLabel3=new JLabel();

	private JLabel jLabel4=new JLabel();

	/** Setup the panel, given the infusion, its name, and its image. */
	public void setup(IntravenousInfusion ivi,String infusionName,Image image){
		infusion=ivi;
		this.image=image;
		contentLabel.setText(infusionName);
		contentLabel.setToolTipText(infusionName);
		// in millilitres
		volumeSlider.setMaximum((int)(ivi.defaultStartVolume*1000));
		rateSlider.setValue((int)(1/(60*ivi.rate)));
		updateFullness();
	}

	void init(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setOpaque(false);
	}

	/** Updates the volume slider with the volume of the drip */
	void updateFullness(){
		if(infusion!=null){
			double v=infusion.volume.get();
			volumeSlider.setValue((int)(v*1000));
		}
	}

	private void jbInit() throws Exception{
		border1=BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(
				BevelBorder.LOWERED),BorderFactory.createEmptyBorder(2,2,2,2));
		jMenuItem1.setText("Stop infusion");
		this.setLayout(borderLayout1);
		sliderpanel.setLayout(borderLayout2);
		rateSlider.setOrientation(JSlider.VERTICAL);
		rateSlider.setMajorTickSpacing(6);
		rateSlider.setMaximum(18);
		rateSlider.setMinorTickSpacing(2);
		rateSlider.setPaintLabels(true);
		rateSlider.setPaintTicks(true);
		rateSlider.setOpaque(false);
		rateSlider.addChangeListener(new javax.swing.event.ChangeListener(){
			public void stateChanged(ChangeEvent e){
				rateSlider_stateChanged(e);
			}
		});
		jLabel1.setText("Rate");
		volumeSlider.setOrientation(JSlider.VERTICAL);
		volumeSlider.setMajorTickSpacing(200);
		volumeSlider.setMaximum(1000);
		volumeSlider.setMinorTickSpacing(100);
		volumeSlider.setPaintLabels(true);
		volumeSlider.setPaintTicks(true);
		volumeSlider.setEnabled(false);
		volumeSlider.setOpaque(false);
		jPanel1.setLayout(borderLayout3);
		jLabel2.setText("Volume");
		centrepanel.setLayout(borderLayout4);
		contentLabel.setBackground(SystemColor.window);
		contentLabel.setBorder(border1);
		contentLabel.setText("Infusion1");
		picturepanel.setBackground(SystemColor.window);
		picturepanel.setOpaque(false);
		this.setOpaque(false);
		sliderpanel.setOpaque(false);
		centrepanel.setOpaque(false);
		jPanel1.setOpaque(false);
		jLabel3.setText("Hourly");
		jLabel4.setText("mL");
		jPopupMenu1.add(jMenuItem1);
		this.add(sliderpanel,BorderLayout.EAST);
		sliderpanel.add(rateSlider,BorderLayout.CENTER);
		this.add(centrepanel,BorderLayout.CENTER);
		this.add(jPanel1,BorderLayout.WEST);
		jPanel1.add(volumeSlider,BorderLayout.CENTER);
		jPanel1.add(jLabel2,BorderLayout.NORTH);
		jPanel1.add(jLabel4,BorderLayout.SOUTH);
		sliderpanel.add(jLabel1,BorderLayout.NORTH);
		sliderpanel.add(jLabel3,BorderLayout.SOUTH);
		centrepanel.add(contentLabel,BorderLayout.NORTH);
		centrepanel.add(picturepanel,BorderLayout.CENTER);
	}

	/**
	 * Called when the rate slider changes its value; the method reflects
	 * this change in the infusion object. The rate slider value is interpreted
	 * in millilitres per minute.
	 */
	void rateSlider_stateChanged(ChangeEvent e){
		//convery 'hourly' value into litres per minute
		//stat runs over 10 minutes.
		double rate=rateSlider.getValue();
		if(rate==0){
			rate=1.0/6;
		}
		infusion.rate=1.0/(rate*60);
	}
}
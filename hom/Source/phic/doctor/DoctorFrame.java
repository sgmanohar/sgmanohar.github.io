package phic.doctor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import phic.*;

/**
 * The main frame for the doctor's GUI. The frame contains a desktop pane
 * that contains the internal frames.
 */
public class DoctorFrame extends JFrame implements phic.gui.FrameStub{
	/** Executable part - creates a DoctorFrame. */
	public static void main(String[] s){
		DoctorFrame df=new DoctorFrame();
	}

	private JDesktopPane desktop=new JDesktopPane();

	protected int width=800,height=600;

	private JMenuBar jMenuBar1=new JMenuBar();

	private JMenu jMenu1=new JMenu();

	private JMenuItem jMenuItem1=new JMenuItem();

	private JMenuItem jMenuItem2=new JMenuItem();

	private JMenuItem jMenuItem3=new JMenuItem();

	public JMenu windowmenu=new JMenu();

	private TimeControl timecontrol=new TimeControl();

	public PatientFrame patientFrame=new PatientFrame();

	/**
	 * This is the object that continuously updates various displays on the
	 * screen. Currently updates
	 *   Timer
	 *   PatientFrame
	 */
	Timer updater=new Timer(300,new ActionListener(){
		public void actionPerformed(ActionEvent e){
			timecontrol.updateDisplay();
			patientFrame.updateDisplay();
		}
	});

	public DoctorFrame(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		//centre frame on screen
		this.setSize(width,height);
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screen.width-width)/2,(screen.height-height)/2);
		show();
		//Add windows
		desktop.add(console);
		desktop.add(timecontrol);
		desktop.add(patientFrame);
		this.setContentPane(desktop);
		timecontrol.show();
		updater.start();
		//PLAF
		helpmenu.add(new sanjay.PlafMenu(desktop));
		//start phic
		initialisePhic();
		person.body.receiver=console;
		person.body.setRunning(true);
		//this requires existence of the person.
		createObservationsWindow();
	}

	Console console=new Console();

	Person person;

	private JMenu helpmenu=new JMenu();

	private JMenu jMenu2=new JMenu();

	private JMenuItem bloodtestsmenu=new JMenuItem();

	private JMenu jMenu3=new JMenu();

	private JMenuItem fluidsmenu=new JMenuItem();

	private JMenuItem drugsmenu=new JMenuItem();

	private JMenuItem observationsmenu=new JMenuItem();

	private void jbInit() throws Exception{
		this.setJMenuBar(jMenuBar1);
		this.setTitle("Doctor window");
		this.addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(WindowEvent e){
				this_windowClosing(e);
			}
		});
		jMenu1.setText("File");
		jMenuItem1.setText("Open");
		jMenuItem2.setText("Save");
		jMenuItem3.setText("Exit");
		windowmenu.setText("Window");
		helpmenu.setText("Help");
		jMenu2.setText("Investigation");
		bloodtestsmenu.setText("Blood tests");
		bloodtestsmenu.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				bloodtestsmenu_actionPerformed(e);
			}
		});
		jMenu3.setText("Intervention");
		fluidsmenu.setText("Fluids");
		fluidsmenu.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				fluidsmenu_actionPerformed(e);
			}
		});
		drugsmenu.setText("Drugs");
		drugsmenu.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				drugsmenu_actionPerformed(e);
			}
		});
		observationsmenu.setText("Observations");
		observationsmenu.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				observationsmenu_actionPerformed(e);
			}
		});
		jMenuBar1.add(jMenu1);
		jMenuBar1.add(jMenu2);
		jMenuBar1.add(jMenu3);
		jMenuBar1.add(windowmenu);
		jMenuBar1.add(helpmenu);
		jMenu1.add(jMenuItem1);
		jMenu1.add(jMenuItem2);
		jMenu1.addSeparator();
		jMenu1.add(jMenuItem3);
		jMenu2.add(bloodtestsmenu);
		jMenu2.add(observationsmenu);
		jMenu3.add(fluidsmenu);
		jMenu3.add(drugsmenu);
	}

	/**
	 * Creates the phic data (Body and Environment) using the Variables.
	 * Then, set up the current Person data.
	 */
	void initialisePhic(){
		phic.gui.Variables.initialise();
		person=phic.Current.person;
		person.body=Current.body;
		person.environment=Current.environment;
	}

	/**
	 * Calls System.exit(0) to terminate the VM when the main window is closed.
	 */
	void this_windowClosing(WindowEvent e){
		//query to close file?
		System.exit(0);
	}

	/**
	 * Create a set of blood tests in a new window
	 */
	void bloodtestsmenu_actionPerformed(ActionEvent e){
		BloodTestResults b=new BloodTestResults(person);
		desktop.add(b);
		b.show();
	}

	/**
	 * Create a window to select a fluid from
	 */
	void fluidsmenu_actionPerformed(ActionEvent e){
		FluidsSelection f=new FluidsSelection();
		desktop.add(f);
		f.show();
	}

	/**
	 * Create a window to select a drug from
	 */
	void drugsmenu_actionPerformed(ActionEvent e){
		DrugSelection d=new DrugSelection();
		desktop.add(d);
		d.show();
	}

	/**
	 * Observation chart is persistent once started. Only one instance allowed.
	 */
	ObservationsChart obsChart;
	/**
			 * Create an observation chart if none exist. Otherwise, show the existing one.
	 */

	 void observationsmenu_actionPerformed(ActionEvent e){
		if(obsChart==null){
			createObservationsWindow();
		}
		obsChart.show();
	}

	/** Creates the observations window and adds to desktop. Person must exist at this point. */
	protected void createObservationsWindow(){
		obsChart=new ObservationsChart(person);
		desktop.add(obsChart);
	}

	/**
	 * This method is called to display an alert dialog box to the user
	 * for messages, such as the death of the patient.
	 * The window is opened by another thread, so this call is non-blocking.
	 * Thus it may be called directly by the engine.
	 */
	public void alert(Object message,Object title){
		synchronized(alerter){
			alerter.message=message;
			alerter.title=title;
			alerter.notify();
		}
	}

	/** True when the alerter thread is running */
	boolean running=true;

	/**
	 * The alerter thread automatically starts when created. It simply waits
	 * until it is notified, and then displays a MessageDialog with the
	 * specified message. It then terminates. Used to create a non-blocking call
	 * to display a message.
	 */
	class Alerter implements Runnable{
		Object message;

		Object title;

		Thread thread=new Thread(this);

		{
			thread.start();
		}

		public synchronized void run(){
			try{
				while(running){
					wait();
					JOptionPane.showInternalMessageDialog(desktop,message,title.toString(),
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}


	Alerter alerter=new Alerter();
  public void addCloseListener(final ActionListener reshowListener) {
    addWindowListener(new WindowAdapter(){public void windowClosed( WindowEvent e){
        reshowListener.actionPerformed(new ActionEvent(e.getSource(),e.getID(),"Close"));
      }});
  }

  public void doSetup(String setupFile, String setupSection) {
  }

  public void finishDrip(IntravenousInfusion ivi) {
  }

  public JFrame getJFrame() {
    return this;
  }

  public void markEvent(Object event) {
  }

  public void message(String message) {
    this.console.message(message);
  }

  public void unconsciousForAges() {
  }
}

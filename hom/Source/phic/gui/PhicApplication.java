package phic.gui;
import javax.swing.UIManager;

/**
 * Creates the phic window: this is an executable class.
 * The main method creates a window based on command line parameters.
 *
 * There are two main modes - normal (Physiology mode) and doctor mode. In physiology
 * mode, complex window is available for monitoring all variables in many conditions
 * with variable time compression.
 * The parameter 'setup [setupName]' will set up the window according to the specified
 * section  of the file FrameSetup.txt
 *
 * Doctor mode: (parameter 'doctor') a window (DoctorFrame) is created allowing
		 * realistic monitoring of only those variables that manifest clinically, running
 * in real time.
 */
public class PhicApplication{
	public static FrameStub frame=null;

	private static final String physiologyFrameClass="phic.gui.PhicFrameSetup",
			doctorFrameClass="phic.doctor.DoctorFrame";

	public static void main(String[] args){
		try{
			UIManager.installLookAndFeel("Skin look and feel",
					"com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}
		if(!listContains(args,"doctor")){
			try{
				frame=(PhicFrameSetup)Class.forName(physiologyFrameClass).newInstance();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		processArguments(args);
		if(frame!=null){
			frame.show();
		}
	}

	/** Simply returns true if the list of items contains item. */
	private static boolean listContains(String[] list,Object item){
		for(int i=0;i<list.length;i++){
			if(list[i].equalsIgnoreCase((String)item)){
				return true;
			}
		}
		return false;
	}

	/**
	 * This processed command-line argument strings.
	 * If something other than a valid argument is given, or a parameter is
	 * missing, the help text is output to the console.
	 * @todo Simple mode shows the drips panel in an awkward place. The best
	 * solution may be to alter the bottomRightPanel in SimplePhicFrame to
	 * be displayed as a vertical splitter pane.
	 */
	private static void processArguments(String[] args){
		try{
			for(int i=0;i<args.length;i++){
				if(args[i].equalsIgnoreCase("open")){
				//open file
				/*
					try{
						 Person p=frame.filer.openPatientFile(new FileInputStream(args[++i]));
					 frame.setupNewPerson(p);
					 frame.startPressed();
					}catch(Exception e){
					 e.printStackTrace();
					 JOptionPane.showMessageDialog(null,"Could not load "+args[i]+
					 "\n because of "+e,"Error loading file",JOptionPane.ERROR_MESSAGE);
					}
				 */
				} else if(args[i].equalsIgnoreCase("simple")){
				//remove complicated parts of the window
				/*
					frame.mainFramePanel.remove(frame.mainSplitter);
					 frame.mainFramePanel.add(frame.rightHandPanel, BorderLayout.CENTER);
					frame.rightBottomPanel.remove(frame.limitChecker);
					frame.limitChecker.running=false;
					frame.rightBottomPanel.add(frame.dripspanel, BorderLayout.NORTH);
				 */
				} else if(args[i].equalsIgnoreCase("graph")){
				//load the specified graph setup
				/*
					frame.graphsetup(new ActionEvent(frame, 0, args[++i]));
				 */
				} else if(args[i].equalsIgnoreCase("doctor")){
					if(frame!=null){
						frame.hide();
					}
					try{ // runs DoctorFrame.main() with dynamic class loading
						Object df=Class.forName(doctorFrameClass).newInstance();
						//df.getClass().getMethod("main", new Class[]{String[].class})
						//	.invoke(df, new Object[]{args});
					} catch(Exception e){
						e.printStackTrace();
					}
					//revert to no time compression for doctor mode.
					phic.Current.body.getClock().setSecond(1000.);
				} else if(args[i].equalsIgnoreCase("blank")){
				//remove most components from the window
				/*
					frame.mainFramePanel.remove(frame.mainSplitter);
					 frame.mainFramePanel.add(frame.rightHandPanel, BorderLayout.CENTER);
					frame.rightHandPanel.remove(frame.rightBottomPanel);
					frame.limitChecker.running=false;
					frame.rightHandPanel.add(frame.dripspanel, BorderLayout.SOUTH);
					frame.setMenuBar(null);
					frame.mainFramePanel.remove(frame.controlBar);
					JMenuBar b = frame.getJMenuBar();
					b.remove(frame.graphsmenu.getComponent());
					frame.getJMenuBar().remove(frame.toolsmenu.getComponent());
				 */
				} else if(args[i].equalsIgnoreCase("setup")){
					frame.doSetup("FrameSetup.txt",args[++i]);
				} else{
					System.out.println(helpString);
				}
			}
		} catch(ArrayIndexOutOfBoundsException x){
			System.out.println(helpString);
		}
		if(args.length==0){
			try{
				frame.doSetup("FrameSetup.txt","Default");
			} catch(IllegalArgumentException e){
				frame.message(
						"Cannot find resources/FrameSetup.txt file with entry [Default]");
        e.printStackTrace();
				System.out.println("");
			}
		}
	}

	private static String helpString="PHIC Command Line Help\n"+
//		"graph [SetupName]  Displays the specified graph setup,\n"+
//		"                   as specified in GraphSetups.txt\n"+
//		"open [filename]    Opens the specified patient data file\n"+
//		"simple             Opens a simplified version of the window\n"+
			"doctor             Runs in Doctor mode\n"+
//		"blank              Opens a blank window\n"+
			"setup [section]    Setup up the frame using the properties from the\n"
			+"                   given section of the resource file 'FrameSetup.txt'\n"
			+"See the programming documentation for advanced help.\n";

	/**
	 * This is called by the body to mark events in the graph window.
	 */
	public static void markEvent(Object o){
		if(frame!=null){
			frame.markEvent(o);
		}
	}
}
package phic.doctor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import phic.*;
import phic.common.*;

/**
 * Control timing
 */
public class TimeControl extends IFrame{
	Object[] ffwdNames={"1 minute","1 hour","1 day","1 week"};

	double[] ffwdTimes={60,60*60,60*60*24,60*60&24*7};

	ImageIcon iplay=new ImageIcon(Resource.loader.getImageResource("Play.gif")),
			iffwd=new ImageIcon(Resource.loader.getImageResource("FastForward.gif"));

	private JPanel jPanel1=new JPanel();

	private JToggleButton jButton1=new JToggleButton();

	private JPanel jPanel2=new JPanel();

	private JToggleButton ffwdbutton=new JToggleButton();

	private TitledBorder titledBorder1;

	private JComboBox ffwdtime=new JComboBox();

	private DefaultComboBoxModel ffwdlistmodel=new DefaultComboBoxModel(ffwdNames);

	JProgressBar ffwdProgress=new JProgressBar();

	public TimeControl(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		//setSize(300,110);
		pack();
	}

	private void jbInit() throws Exception{
		titledBorder1=new TitledBorder(BorderFactory.createEtchedBorder(),"Fast forward");
		jButton1.setText("");
		jButton1.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				jButton1_actionPerformed(e);
			}
		});
		jButton1.setToolTipText("Start or stop the clock");
		jButton1.setIcon(iplay);
		jButton1.setSelected(true);
		ffwdbutton.setIcon(iffwd);
		this.setTitle("Time");
		ffwdbutton.setToolTipText("Fast forward the clock");
		ffwdbutton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(ActionEvent e){
				ffwdbutton_actionPerformed(e);
			}
		});
		jPanel2.setBorder(titledBorder1);
		ffwdtime.setToolTipText("Amount of time to fast forward by");
		ffwdtime.setModel(ffwdlistmodel);
		ffwdProgress.setPreferredSize(new Dimension(60,14));
		ffwdProgress.setToolTipText("Fast forward progress");
		timelabel.setToolTipText("Current time");
		timelabel.setText("01 Januarymmm 1980 01:01:01");
		clockPanel1.setPreferredSize(new Dimension(40,40));
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(timelabel,null);
		jPanel1.add(jButton1,null);
		jPanel1.add(jPanel2,null);
		jPanel2.add(clockPanel1,null);
		jPanel2.add(ffwdbutton,null);
		jPanel2.add(ffwdtime,null);
		jPanel2.add(ffwdProgress,null);
	}

	/** return the current body clock */
	Clock getClock(){
		if(frame!=null){
			if(frame.person!=null){
				return frame.person.body.getClock();
			}
		}
		return null;
	}

	void jButton1_actionPerformed(ActionEvent e){
		if(jButton1.isSelected()){
			getClock().start();
		} else{
			getClock().stop();
		}
	}

	double previousSecond=1000;

	long startTime,maxWait;

	double fastMillisPerSecond=1;

	void ffwdbutton_actionPerformed(ActionEvent e){
		if(ffwdbutton.isSelected()){
			Clock c=getClock();
			maxWait=(long)(ffwdTimes[ffwdtime.getSelectedIndex()]*1000);
			startTime=c.getTime();
			previousSecond=c.getSecond();
			c.setSecond(fastMillisPerSecond);
			timer.start();
		} else{
			stopFastForwarding();
		}
	}

	/**
	 * @todo replace this with a Clock.requestNotify
	 */
	Timer timer=new Timer(50,new ActionListener(){
		Clock c;

		public void actionPerformed(ActionEvent e){
			if(c==null){
				c=getClock();
			}
			long t=c.getTime()-startTime;
			ffwdProgress.setValue((int)(100*t/maxWait));
			updateDisplay();
			if(t>maxWait){
				stopFastForwarding();
			}
		}
	});

	JLabel timelabel=new JLabel();

	private ClockPanel clockPanel1=new ClockPanel();

	void stopFastForwarding(){
		getClock().setSecond(previousSecond);
		timer.stop();
		ffwdProgress.setValue(0);
		ffwdbutton.setSelected(false);
	}

	public void updateDisplay(){
		Clock c=getClock();
		if(c!=null){
			timelabel.setText(c.getTimeString(Clock.DATETIME));
			clockPanel1.updateClock(c.getTime());
		}
	}
}
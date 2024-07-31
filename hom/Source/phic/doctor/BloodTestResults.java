package phic.doctor;
import phic.common.*;
import phic.gui.ScreensPanel;
import phic.*;

/**
 * Blood test results
 */
public class BloodTestResults extends IFrame{
	Person person;

	public BloodTestResults(Person p){
		person=p;
		initialiseTest();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
	}

	ScreensPanel panel;

	void initialiseTest(){
		Clock c=person.body.getClock();
		String time=c.getTimeString(Clock.DATETIME);
		setTitle("Bloods taken on "+time);
		panel=new ScreensPanel("BloodTests.txt",null);
		this.getContentPane().add(panel);
	}
}
package com.cudos.common;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import com.cudos.server.client.*;

/**
 * Generic testing functions
 */



// testing protocol for any class that implements TestableExhibit

public	class CudosTest {
		static final String instructions="Select what corresponds to the highlighted item:";
		static final String correctmessage="Correct!";
		static final String incorrectmessage="Incorrect.";
		static final String tryagainmessage="Try again";
		static final String showcorrectmessage="This is the correct answer";
		public static final String correctsound="resources/sounds/CorrectSound.wav";
		public static final String incorrectsound="resources/sounds/IncorrectSound.wav";
		final int delayCorrect=3000;
		final int delayIncorrect=1000;

		TestableExhibit e;
		/** Which of the controls is the one to be tested on */
		int testControl;
		/** The correct answer to the current quenstion */
		int item;
		/** The running total */
		public int nCorrect,nTotal;

		/** The maximum number of incorrect responses allowed before the answer
		 * is considered wrong. By default = 3. */
		int MAX_WRONG_PER_QUESTION = 3;

		/** How many times the current question has been answered wrongly */
		int errors;

		Color col1=Color.red, col2=Color.green;
		CudosApplet applet;

		/** The randomized order of the questions in the test. Set in the constructor */
		int[] order;

		/**
		 * Start a test
		 */
		public CudosTest(TestableExhibit t){
			e=t;
			testControl=1;
			nCorrect=0;
			nTotal=0;
			timerRestart.setDelay(1);
			timerRestart.restart(); // calls newQuestion() to start a question
			applet = t.getApplet();
			order = randomizeOrder( e.getTestItemCount() );
		}

		/** Show the instructions in the text pane */
		public void instruct(){
			e.getTextPane().setText(instructions);
		}

		/** Called each time a new question is asked */
		public void newQuestion(){
			if(nTotal == e.getTestItemCount()) {endTest();return;}
			instruct();
			item = order[nTotal++];
			for(int c=0;c<e.getTestItemCount();c++){
				e.clearTestControl(c);
			}
			answer=false;
			e.selectTestItem(testControl,item,col2);
			errors=0;
			lastsel=-1;
		}

		int lastsel;	// what was last chosen? (to eradicate repeat event reception)
		boolean answer;	// are we currently showing the answer? (to eradicate 'correct' fired when displaying answer)

		/** See if the control contains the correct answer */
		public void judge(int control,int sel){
			if(answer){answer=false; return;}
			if(sel==lastsel)return;				//repeat events ignored
			lastsel=sel;
			if(timerRestart.isRunning())return;				// user clicks too soon...
			if(timerIncorrect.isRunning()){
				firedIncorrect();
			}
			if(control!=testControl){
				if(sel==item){						//--RIGHT
					e.getTextPane().setText(correctmessage);
					nCorrect++;
					for(int c=0;c<e.getTestControlCount();c++){
						e.selectTestItem(c,sel,col1);		//switch to red
					}
					applet.play(applet.getResourceURL(correctsound));
					timerRestart.setDelay(delayCorrect);
					timerRestart.start();				// wait
				}else{							//--WRONG
					e.getTextPane().setText(incorrectmessage);
					e.selectTestItem(control,sel,col1);
					e.selectTestItem(testControl,sel,col1);		//show what they clicked on
					applet.play(applet.getResourceURL(incorrectsound));
					timerIncorrect.setDelay(delayIncorrect);
					timerIncorrect.start();				// wait before restart
				}
			}else{								//--has pressed item on wrong control
				instruct();
				if(sel!=item)e.selectTestItem(testControl,item,col2);
			}
		}
		Timer timerRestart=new Timer(delayCorrect,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				timerRestart.stop();
				newQuestion();						//start new test
			}
		});
		Timer timerIncorrect=new Timer(delayIncorrect,new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				firedIncorrect();
			}
		});

		/** Called when an erroneous selection is made */
		public void firedIncorrect(){
			timerIncorrect.stop();
			for(int c=0;c<e.getTestItemCount();c++){		//deselect any incorrect selections
				e.clearTestControl(c);
			}
			e.getTextPane().setText(tryagainmessage);
			e.selectTestItem(testControl,item,col2);			//reshow the test item
			lastsel=-1;
			if(++errors >= MAX_WRONG_PER_QUESTION){
				answer=true;
				for(int c=0;c<e.getTestControlCount();c++){	//if 3 errors, reveal all.
					e.selectTestItem(c,item,col1);
				}
				e.getTextPane().setText(showcorrectmessage);
				timerRestart.start();
			}
		}



	/**
	 * Added 21/1/03
	 * Called when all the items have been asked. Sends the test result to server.
	 */
	void endTest(){
		int r=JOptionPane.showConfirmDialog(null,"Well done, you have scored "+nCorrect+
			" out of "+nTotal+".\nWould you like to log in and store your result?",
			"Test complete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(r==JOptionPane.YES_OPTION){ //Store results to server.
			LogonDialog d = new LogonDialog();
			d.show();
			ClientHandle h = d.getHandle();
			if(h!=null){
				d.getServer().addTestResult(h, new TestResult( e.getTestName(),
					nCorrect, nTotal, new Date(System.currentTimeMillis()) ));
				d.getServer().logout(h);
				JOptionPane.showMessageDialog(null, "Your test result has been stored.",
					"Result stored",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(null, "Your test result has not been stored.",
				"Result not stored",JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/** This can be accessed by other exhibits */

	public static final void endTest(String testName, int nCorrect, int nTotal){
		int r=JOptionPane.showConfirmDialog(null,"Well done, you have scored "+nCorrect+
			" out of "+nTotal+".\nWould you like to log in and store your result?",
			"Test complete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(r==JOptionPane.YES_OPTION){ //Store results to server.
			LogonDialog d = new LogonDialog();
			d.show();
			ClientHandle h = d.getHandle();
			if(h!=null){
				d.getServer().addTestResult(h, new TestResult( testName,
					nCorrect, nTotal, new Date(System.currentTimeMillis()) ));
				d.getServer().logout(h);
				JOptionPane.showMessageDialog(null, "Your test result has been stored.",
					"Result stored",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(null, "Your test result has not been stored.",
				"Result not stored",JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public static int[] randomizeOrder(int n){
		Vector v = new Vector();
		for(int i=0;i<n;i++) v.add( new Integer(i) );
		Collections.shuffle( v );
		int[] a = new int[n];
		for(int i=0;i<n;i++) a[i] = ((Integer)v.get(i)).intValue();
		return a;
	}

	public static void showCorrectMessage(String information, Component c, boolean message){
		CudosApplet applet = CudosExhibit.getApplet(c);
		applet.play(applet.getResourceURL(correctsound));
		if(message)
		JOptionPane.showMessageDialog(c,correctmessage+"\n"+information, "Correct answer",
			JOptionPane.INFORMATION_MESSAGE);
	}
	public static void showIncorrectMessage(String information, Component c, boolean message){
		CudosApplet applet = CudosExhibit.getApplet(c);
		applet.play(applet.getResourceURL(incorrectsound));
		if(message)
		JOptionPane.showMessageDialog(c,incorrectmessage+"\n"+information, "Wrong answer",
			JOptionPane.INFORMATION_MESSAGE);
	}
}

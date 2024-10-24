
package com.cudos.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * An abstract class for a picture with a set of labels for different parts, and
 * descriptions for each part. Implements testing using com.cudos.common.CudosText
 */
public abstract class IndexedPictureAndText extends CudosExhibit implements TestableExhibit{
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel();
	JButton jButton1 = new JButton();
	JButton jButton2 = new JButton();
	JPanel jPanel4 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	Border border1;
	TitledBorder titledBorder1;
	JPanel jPanel5 = new JPanel();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel imagepane = new JPanel();
	protected IndexedImageControl imagecontrol = new IndexedImageControl();
	BorderLayout borderLayout6 = new BorderLayout();
	Border border2;
	Border border3;

	public IndexedPictureAndText() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public abstract String getImageName();
	public abstract String getIndexName();
	public abstract String[] getItemNames();
	public abstract String[] getTexts();
	public Color getHighlightColour(){return Color.red;}
	public double getHighlightAlpha(){return 0.5;}

	String[] text;String[] itemname;
	Image image,index;
	boolean testing=false;
	public void postinit(){
		imagecontrol.setImage(getApplet().getImage(getImageName()));
		imagecontrol.setIndex(getApplet().getImage(getIndexName()));
		itemname=getItemNames();
		listbox.setListData(itemname);
		text=getTexts();

		imagecontrol.setIndexedImageListener(new IndexedImageListener(){
			public void indexedImageEvent(int i){
				if(i<itemname.length && i>=0){
					if(testing){
						test.judge(0,i);
					}else{
						listbox.setSelectedIndex(i);	// should automatically trigger textpane change
					}
				}
				else listbox.getSelectionModel().clearSelection();
			}
		} );
//		imagecontrol.setStretched(false);
		lasthilite=-1;
	}


	private void jbInit() throws Exception {
		border1 = BorderFactory.createEtchedBorder();
		titledBorder1 = new TitledBorder(border1,"Regions");
		border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		this.getContentPane().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		jPanel2.setLayout(borderLayout3);
		jButton1.setText("Exit");
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});
		jButton2.setText("Test");
		jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});
		jPanel4.setLayout(borderLayout4);
		jPanel5.setLayout(borderLayout5);
		imagepane.setLayout(borderLayout6);
		listbox.setBackground(SystemColor.control);
		listbox.setSelectionBackground(Color.red);
		listbox.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				listbox_valueChanged(e);
			}
		});
		jScrollPane1.setBorder(titledBorder1);
		jScrollPane1.setPreferredSize(new Dimension(140, 132));
		textpane.setPreferredSize(new Dimension(76, 80));
		textpane.setBackground(SystemColor.control);
//    textpane.setBorder(border3);
		textpane.setText("");
		textpane.setFont(new java.awt.Font("SansSerif", 1, 14));
		jScrollPane2.setPreferredSize(new Dimension(80, 80));
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.WEST);
		jPanel2.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jButton2, null);
		jPanel3.add(jButton1, null);
		jPanel2.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(listbox, null);
		jPanel1.add(jPanel5, BorderLayout.CENTER);
		jPanel5.add(imagepane, BorderLayout.CENTER);
		imagepane.add(imagecontrol, BorderLayout.CENTER);
		jPanel5.add(jScrollPane2, BorderLayout.SOUTH);
		jScrollPane2.getViewport().add(textpane, null);
	}

	void jButton1_actionPerformed(ActionEvent e) {
	getApplet().toChooser();
	}

	Color oldlistbg;
	void jButton2_actionPerformed(ActionEvent e) {
	if(testing){
		testing=false;
		textpane.setText("Score :"+test.nCorrect+"/"+test.nTotal);
		jButton2.setText("Test");
		listbox.setSelectionBackground(oldlistbg);
	}else{
		testing=true;
		jButton2.setText("Stop test");
		test=new CudosTest(this);
		oldlistbg=listbox.getSelectionBackground();
	}
	}

	public boolean doHighlight=true;
	int lasthilite=-1;
	void listbox_valueChanged(ListSelectionEvent e) {
	int i=listbox.getSelectedIndex();
	if(!testing){
		if(i!=lasthilite){
			if(doHighlight){
				if(lasthilite>=0)imagecontrol.paintImmediately(imagecontrol.getBounds());
				lasthilite=i;
				if(i>=0)imagecontrol.indexHighlight(i,getHighlightColour(),getHighlightAlpha());
			}
			if(i>=0 && i<text.length){
				textpane.setText(text[i]);
				textpane.repaint();
			}else textpane.setText("");
		}
	}else{
		if(i>=0)test.judge(1,i);
	}
	}
	CudosTest test;
	JScrollPane jScrollPane1 = new JScrollPane();
	JList listbox = new JList();
	JScrollPane jScrollPane2 = new JScrollPane();
	JTextPane textpane = new JTextPane();




		// testable interface methods
	public JTextPane getTextPane(){return textpane;}
	public void selectTestItem(int control,int item,Color col){
		switch(control){
			case 0:
//				imagecontrol.paintImmediately(imagecontrol.getBounds());
				imagecontrol.indexHighlight(item,col,0.5f);
				break;
			case 1:
				listbox.setSelectionBackground(col);
				listbox.setSelectedIndex(item);
				break;
			default: throw new RuntimeException("Control number out of bounds");
		}
	}
	public void clearTestControl(int control){
		switch(control){
			case 0:
				imagecontrol.paintImmediately(imagecontrol.getBounds());
				break;
			case 1:
				listbox.clearSelection();
				break;
		}
	}
	public int getTestItemCount(){return itemname.length;}
	public int getTestControlCount(){return 2;}


/*

// testing protocol for any class that implements TestableExhibit
// moved to CudosTest.java

	class CudosTest {
		final String instructions="Select what corresponds to the highlighted item:";
		final String correctmessage="Correct!";
		final String incorrectmessage="Incorrect.";
		final String tryagainmessage="Try again";
		final String showcorrectmessage="This is the correct answer";
		final String correctsound="resources/sounds/CorrectSound.wav";
		final String incorrectsound="resources/sounds/IncorrectSound.wav";
		final int delayCorrect=3000;
		final int delayIncorrect=1000;

		TestableExhibit e;
		int testControl;
		int item;
		public int nCorrect,nTotal;
		Random rand=new Random();
		int errors;
		Color col1=Color.red, col2=Color.green;

		public CudosTest(TestableExhibit t){
			e=t;
			testControl=1;
			nCorrect=0;
			nTotal=0;
			timerRestart.setDelay(1);
			timerRestart.restart();
		}
		public void instruct(){
			e.getTextPane().setText(instructions);
		}
		public void newtest(){
			instruct();
			item=rand.nextInt(e.getTestItemCount());
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
					nCorrect++;nTotal++;
					for(int c=0;c<e.getTestControlCount();c++){
						e.selectTestItem(c,sel,col1);		//switch to red
					}
					getApplet().play(getApplet().getCodeBase(),correctsound);
					timerRestart.setDelay(delayCorrect);
					timerRestart.start();				// wait
				}else{							//--WRONG
					e.getTextPane().setText(incorrectmessage);
					e.selectTestItem(control,sel,col1);
					e.selectTestItem(testControl,sel,col1);		//show what they clicked on
					getApplet().play(getApplet().getCodeBase(),incorrectsound);
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
				newtest();						//start new test
			}
		});
		Timer timerIncorrect=new Timer(delayIncorrect,new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				firedIncorrect();
			}
		});
		public void firedIncorrect(){
			timerIncorrect.stop();
			for(int c=0;c<e.getTestItemCount();c++){		//deselect any incorrect selections
				e.clearTestControl(c);
			}
			e.getTextPane().setText(tryagainmessage);
			e.selectTestItem(testControl,item,col2);			//reshow the test item
			lastsel=-1;
			if(++errors>=3){
				answer=true;
				for(int c=0;c<e.getTestControlCount();c++){	//if 3 errors, reveal all.
					e.selectTestItem(c,item,col1);
				}
				e.getTextPane().setText(showcorrectmessage);
				timerRestart.start();
			}
		}

	}
*/

}
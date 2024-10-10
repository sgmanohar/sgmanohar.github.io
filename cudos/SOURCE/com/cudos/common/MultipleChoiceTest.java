package com.cudos.common;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;

/**
 * Test in multiple choice format, read from file.
 */

public class MultipleChoiceTest extends CudosExhibit {


	boolean passEnabled=true;
	boolean feedbackEnabed=true;
	boolean randomiseAnswers=true;
	boolean messageDialogIfCorrect=false;
	boolean messageDialogIfIncorrect=true;

	public MultipleChoiceTest() {
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		for(int i=0;i<answerButtons.length;i++){
			JButton b = new JButton();
			answerButtons[i]=b;
			b.setFont(bigFont);
			b.setActionCommand(String.valueOf(i));
			b.addActionListener(buttonAction);
			answerspanel.add(b, null);
		}
		setPassEnabled(false);
	}
	public void postinit(){
		super.postinit();
		String qfile = getApplet().getParameter("questions");
		if(qfile!=null) setupFromFile( qfile );
	}

	public void setupFromFile(String filename){
		//name = name of file excluding extension
		String name = new File(filename).getName();
		name = name.substring(0,name.lastIndexOf("."));
		try{
			setupFrom(getApplet().getResourceURL(filename).openStream(),name);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * The file is read, using the first line of a block as the question, and the
	 * remaining lines as the choices. There must be a single blank line between
	 * blocks. The correct answer begins with an asterisk (*).
	 * The answer may be an image filename.
	 */
	public void setupFrom(InputStream is, String name) throws IOException{
		this.name=name;
		//read variable questions and answers, and correctAnswer
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		Vector qs = new Vector();
		Vector as = new Vector();
		Vector ca = new Vector();
		int NA=0; //number of answers per questions
		int na=0; //number of answers so far in current question
		boolean newQ = true; //starting a new q?
		while(line != null){
			line=line.trim();
			if(!line.equals("")){ //text => question or answer
				if (newQ) { //beginning of q => question
					qs.add(line);
					newQ=false;
					na=0;
				}else{ //after beginning of q => answer
					if(line.startsWith("*")){ //the correct answer
						line = line.substring(1);
						ca.add(new Integer( na ));
					}
					as.add(line);
					na++;
				}
			}else{ //blank line => end of question
				if(qs.size()==1) NA=na;
				else if(na!=NA)
					throw new IllegalArgumentException("Wrong number of answers to question "+qs.size());
				if(qs.size()!=ca.size())
					throw new IllegalArgumentException("Wrong number of correct answers (asterisks) at question "+qs.size());
				newQ = true;
			}
			line = br.readLine();
		}
		questions = new String[qs.size()];
		answers = new String[qs.size()][NA];
		correctAnswer = new int[qs.size()];
		for(int i=0;i<qs.size();i++){
			questions[i] = (String)qs.get(i);
			for(int j=0;j<NA;j++)
				answers[i][j] = (String)as.get(i*NA+j);
			correctAnswer[i] = ((Integer)ca.get(i)).intValue();
		}

		initialiseTest();
		nextQuestion();
	}



	// dataset
	String name="";
	String[] questions;
	String[][] answers;
	int[] correctAnswer;



	int correct, currentQuestion, order[];

	/** When a button is pressed */
	ActionListener buttonAction = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			int b = Integer.parseInt(	e.getActionCommand() );
			//check is it right or wrong?
			if (b != correctAnswer[order[currentQuestion]]) incorrectAnswerPressed();
			else correctAnswerPressed();
		}
	};


	/** Prepare for a test to begin */
	protected void initialiseTest(){
		correct=0; currentQuestion=0;
		order=CudosTest.randomizeOrder(questions.length);
		scoretext.setText("0");
	}

	/** Called when the student passes on a question */
	void passbutton_actionPerformed(ActionEvent e) {
		incorrectAnswerPressed();
	}

	protected void correctAnswerPressed(){
		correct++;
		if (feedbackEnabed){
			CudosTest.showCorrectMessage("",this,messageDialogIfCorrect);
			scoretext.setText(String.valueOf(correct));
		}
		currentQuestion++;
		if(currentQuestion>=questions.length) endTest();
		else nextQuestion();
	}
	protected void incorrectAnswerPressed(){
		if (feedbackEnabed){
			String extraText = getCorrectButton().getText();
			if(extraText!=null) extraText = "The correct answer is "+extraText;
			CudosTest.showIncorrectMessage(extraText,this, messageDialogIfIncorrect);
		}
		currentQuestion++;
		if(currentQuestion>=questions.length) endTest();
		else nextQuestion();
	}

	protected void endTest(){
		CudosTest.endTest(name,correct,questions.length);
		initialiseTest();
		nextQuestion();
	}

	/** Set up the next question */
	protected void nextQuestion(){
		questiontext.setText(questions[order[currentQuestion]]);
		questionnumber.setText("Question "+currentQuestion);
		totaltext.setText(String.valueOf(currentQuestion));
		int[] ansOrder=null;
		if(randomiseAnswers) ansOrder = CudosTest.randomizeOrder(answerButtons.length);
		for(int k=0;k<answerButtons.length;k++){
			int i = randomiseAnswers ? ansOrder[k] : k;
			String ans = answers[order[currentQuestion]][k];
			if(ans.endsWith(".jpg") || ans.endsWith(".gif")){
				answerButtons[i].setIcon(new ImageIcon(getApplet().getImage(ans)));
				answerButtons[i].setText("");
			}else	{
				answerButtons[i].setText( ans );
				answerButtons[i].setIcon(null);
			}
			answerButtons[i].setActionCommand(String.valueOf(k));
		}
	}



	/// GUI

	JButton[] answerButtons = new JButton[4];
	Font bigFont = new Font("Dialog", 1, 16);


	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel4 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JTextArea questiontext = new JTextArea();
	JPanel jPanel5 = new JPanel();
	JButton exitbutton = new JButton();
	JButton passbutton = new JButton();
	JPanel jPanel6 = new JPanel();
	JLabel questionnumber = new JLabel();
	JLabel scoretext = new JLabel();
	JLabel jLabel3 = new JLabel();
	JLabel totaltext = new JLabel();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel answerspanel = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	Border border2;
	public String getExhibitName(){ return name; }
	private void jbInit() throws Exception {
		border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),BorderFactory.createEmptyBorder(5,5,5,5));
		border2 = BorderFactory.createEmptyBorder(10,10,10,10);
		this.getContentPane().setLayout(borderLayout1);
		jPanel1.setLayout(borderLayout2);
		jPanel1.setBorder(border1);
		jPanel3.setLayout(borderLayout3);
		jPanel4.setLayout(borderLayout4);
		exitbutton.setText("Exit");
		exitbutton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitbutton_actionPerformed(e);
			}
		});
		passbutton.setText("Pass on this question");
		passbutton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passbutton_actionPerformed(e);
			}
		});
		questionnumber.setFont(new java.awt.Font("Dialog", 1, 16));
		questionnumber.setText("Question 1");
		scoretext.setFont(bigFont);
		scoretext.setText("0");
		jLabel3.setFont(bigFont);
		jLabel3.setText("/");
		totaltext.setFont(bigFont);
		totaltext.setText("0");
		jPanel2.setLayout(borderLayout5);
		answerspanel.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		gridLayout1.setHgap(10);
		gridLayout1.setRows(2);
		gridLayout1.setVgap(10);
		answerspanel.setBorder(border2);
		questiontext.setFont(new java.awt.Font("Serif", 0, 24));
		questiontext.setEditable(false);
		questiontext.setText("What is the");
		questiontext.setLineWrap(true);
		questiontext.setRows(3);
		questiontext.setWrapStyleWord(true);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(jPanel5,  BorderLayout.EAST);
		jPanel5.add(passbutton, null);
		jPanel5.add(exitbutton, null);
		jPanel1.add(jPanel6,  BorderLayout.CENTER);
		jPanel6.add(scoretext, null);
		jPanel6.add(jLabel3, null);
		jPanel6.add(totaltext, null);
		jPanel1.add(questionnumber,  BorderLayout.WEST);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		this.getContentPane().add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(jPanel4,  BorderLayout.CENTER);
		jPanel4.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(questiontext, null);
		jPanel2.add(answerspanel, BorderLayout.CENTER);
	}
	public boolean isPassEnabled() {
		return passEnabled;
	}
	/** Hide the pass button if pass disabled */
	public void setPassEnabled(boolean passEnabled) {
		this.passEnabled = passEnabled;
		passbutton.setVisible(passEnabled);
	}
	public boolean isFeedbackEnabed() {
		return feedbackEnabed;
	}
	/** Hide the score if feedback disabled */
	public void setFeedbackEnabed(boolean feedbackEnabed) {
		this.feedbackEnabed = feedbackEnabed;
		scoretext.setVisible(feedbackEnabed);
	}

	void exitbutton_actionPerformed(ActionEvent e) {
		getApplet().toChooser();
	}
	JButton getCorrectButton(){
		int cmd = correctAnswer[order[currentQuestion]];
		for(int i=0;i<answerButtons.length;i++){
			if(answerButtons[i].getActionCommand().equals(String.valueOf(cmd))) return answerButtons[i];
		}
		return null;
	}
}

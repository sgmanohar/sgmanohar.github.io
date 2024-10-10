package com.neurolab;

/**
 * This ought to work with Robins Apps.
 */


import javax.swing.*;
import java.awt.image.*;
import com.neurolab.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;


public abstract class RobinExhibit extends NeurolabExhibit{
	public abstract int getNumComponents();
	protected abstract Dimension getImageDimension();

	protected int questionumber;
	protected boolean testmode;
	protected int no_components = getNumComponents();
        protected int[] question = new int[no_components];
	protected String[] textStrings;
	protected String[] brainStrings;
	private Dimension dimension = getImageDimension();

	protected Image picture_base;
	public Image picture[] = new Image[no_components+1]; //one image for each object + a blank for quiz mode
	public boolean image_exists[] = new boolean[no_components+1];  //one image for each object + a blank for quiz mode

	public int correct;

	public boolean noarea = false;
	public boolean ignore = false;
	public boolean nocombo = false;



	public void init()
	{
		super.init();
		index =  no_components; //set as end (17th == 16 including number 0)
		testmode = false;
		nocombo = false;
		noarea = false;

		// make flexible look & feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }

		createComponents();
	}




// Define components

	public JPanel picturebox;
	public PicturePanel picgraphic;
	public TextPanel textbox;
	public JComboBox whichbit;

	private GridBagLayout mainLayout,chkbxLayout;
	private GridBagConstraints mainGbConstraints,chkbxGbConstraints;

	public JRadioButton buttonchoice[];
	private ButtonGroup buttonset;
	private JButton returnbutton;
	private JCheckBox testme;

	private Container maincontainer;

//Create components

	public void createComponents(){

// make the Handler
		 EventHandler handler = new EventHandler();

// setup borders
		Border etched,raisedbevel;
		etched = BorderFactory.createEtchedBorder();
		raisedbevel = BorderFactory.createRaisedBevelBorder();

// make components

		// Make picture displaypanel
		picturebox = new JPanel();
		picturebox.setBackground(systemGray);
		picturebox.setBorder(etched);

		chkbxLayout = new GridBagLayout();
		picturebox.setLayout(chkbxLayout);
		chkbxGbConstraints = new GridBagConstraints();
		chkbxGbConstraints.fill = GridBagConstraints.BOTH;
		chkbxGbConstraints.weightx = 6;
		chkbxGbConstraints.weighty = 4;


		//make & add the panel for the actual graphic
		picgraphic = new PicturePanel();
		picgraphic.setBackground(systemGray);
		chkbxGbConstraints.fill = GridBagConstraints.BOTH;
		chkbxGbConstraints.weightx = 80;
		chkbxGbConstraints.weighty = 100;
		//addchkboxComponent( picgraphic, 1, 1, 1, 1);
			addchkboxComponent( picgraphic, 0, 0, 1, 1);

		// Make ComboBox
		whichbit = new JComboBox(brainStrings);
		//whichbit.setBackground(systemGray); looks better as white
		whichbit.setMaximumRowCount(5);
		whichbit.setLightWeightPopupEnabled(false); // the key thing!!!!
		whichbit.addItemListener( handler );

		// Make textbox
		textbox = new TextPanel();
		textbox.setBorder(etched);
		textbox.setBackground(systemGray);
		textbox.n =  no_components; //set as end (17th == 16 including number 0) thus initially in 'choose an area'

		// Make testme
		testme = new JCheckBox ("Test Me");
		testme.addItemListener( handler );
		testme.setBackground(systemGray);

		//make exit button
					returnbutton = new JButton("Return");
		returnbutton.setBackground(systemGray);
		returnbutton.addActionListener( handler );


// add components to maincontainer

		//setup container
		maincontainer = getMainContainer();
		mainLayout = new GridBagLayout();
		maincontainer.setLayout (mainLayout);
		mainGbConstraints = new GridBagConstraints();

		//add the combobox
		mainGbConstraints.fill=GridBagConstraints.NONE;
		addMainComponent( whichbit, 2, 2, 5, 1, 0, 20);

		//add the textbox
		mainGbConstraints.fill=GridBagConstraints.BOTH;
		//textbox.setSize(100,200);
		addMainComponent( textbox, 4, 3, 3, 1, 0, 20);

		//add the picturebox
		mainGbConstraints.fill=GridBagConstraints.BOTH;
		//addMainComponent( picturebox, 1, 1, 1, 4, 70, 100);
		addMainComponent( picturebox, 2, 0, 2, 4, 80, 80);

		//add the returnbutton
		mainGbConstraints.fill=GridBagConstraints.NONE;
		addMainComponent( returnbutton, 6, 5, 1, 1, 0, 0);

		//add the testme
		mainGbConstraints.fill=GridBagConstraints.NONE;
		addMainComponent( testme, 6, 3, 1, 1, 0, 0);

		//add spacers
			//top above picture
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer(10,30), 0, 0, 1, 1, 1, 0);

			//between right side of picture & text box
			//controlling height of textbox
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer (30,50), 4, 2, 1, 1, 0, 80);

			// between combo & textbox
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer (40,30), 3, 4, 1, 1, 0, 0);

			// right of return button
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer (10,0), 6, 6, 1, 1, 0, 0);

			// between text & buttons
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer (40,50), 5, 4, 1, 1, 0, 0);

			// far right level of text box adding a bigger margin
		mainGbConstraints.fill = GridBagConstraints.BOTH;
		addMainComponent( new Spacer (40,30), 7, 4, 1, 1, 0, 0);

	} // end createcomponents()


	public String getAppletInfo()
	{
			return "Spinal Tracts coded in Java by Robin Marlow \n based on an original program by Dr. Carpenter.";
	}


//******************************************
// addMainComponent for gridbag stuff
//******************************************
		 private void addMainComponent( Component c,
				int row, int column, int width, int height,int weightx, int weighty )
		 {
				// set gridx and gridy
				mainGbConstraints.gridx = column;
				mainGbConstraints.gridy = row;

				// set gridwidth and gridheight
				mainGbConstraints.gridwidth = width;
				mainGbConstraints.gridheight = height;

				mainGbConstraints.weightx = weightx;
				mainGbConstraints.weighty = weighty;

				// set constraints
				mainLayout.setConstraints( c, mainGbConstraints );
				maincontainer.add( c );      // add component
		 }

//******************************************
// addchkboxComponent for gridbag stuff
//******************************************

		 private void addchkboxComponent( Component c,
				int row, int column, int width, int height )
		 {
				// set gridx and gridy
				chkbxGbConstraints.gridx = column;
				chkbxGbConstraints.gridy = row;

				// set gridwidth and gridheight
				chkbxGbConstraints.gridwidth = width;
				chkbxGbConstraints.gridheight = height;

				// set constraints
				chkbxLayout.setConstraints( c, chkbxGbConstraints );
				picturebox.add( c );      // add component
		 }


//******************
//Picturepanel
//******************

	public class PicturePanel extends JPanel{
		public PicturePanel() {
			addMouseListener(new MouseClickHandler());
		}
		boolean painting=false;
		public void paint(Graphics g){
			super.paint(g);
			int i = index;

			if (image_exists[i] == true) {
				g.drawImage(picture[i],0,0,dimension.width, dimension.height,this);
			} else {
				picture[i] = createImage(new FilteredImageSource(picture_base.getSource(),
						new RedBlueSwapFilter(i)));
				image_exists[i] = true;
				try{Thread.sleep(100);}catch(Exception ex){ex.printStackTrace();}
				g.drawImage(picture[i],0,0,dimension.width, dimension.height,this);

			}
			painting=false;
		}
	}//end PicturePanel class



//*******************
// Eventhandler
//*******************

	private class EventHandler implements ActionListener, ItemListener{

		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand()=="Return"){
				toExhibitChooser();
			}
		} // end actionlistener

		public void itemStateChanged(ItemEvent e) //eventhandler for the checkbox & combobox
		{
			if ( whichbit.getItemAt(0) == "Choose" && ignore != true)	{
				ignore = true;
				whichbit.removeItemAt(0);
				//whichbit.insertItemAt("Areas 9-11,46: prefrontal cortex",0);
				ignore = false;
			}

			if ( e.getSource() == testme){
				if (e.getStateChange() == ItemEvent.SELECTED){
					testmode = true;
					QuestionHandler();
				} else if (e.getStateChange() == ItemEvent.DESELECTED){
					ignore = true;
					testmode = false;
					noarea = false;
					nocombo = false;
					whichbit.setSelectedIndex(0);
					whichbit.setEnabled(true);
					if (whichbit.getItemAt(0) == brainStrings[1])	{
						whichbit.insertItemAt("Choose",0);
						whichbit.setSelectedIndex(0);
					}
					ignore = false;
					textbox.n = no_components;
					index = no_components;

					picturebox.repaint();
					textbox.update();
				}
			}

			//not testing:
			if ( e.getSource() == whichbit &&
					!ignore && !testmode && e.getStateChange() == ItemEvent.SELECTED){
				//this fires once per click
				//& when program changes the box & then get repainted

				textbox.n = whichbit.getSelectedIndex();
				index = whichbit.getSelectedIndex();

				textbox.update();
				picturebox.repaint();
			}

			//if testing:
			if ( e.getSource() == whichbit &&
					!ignore && testmode && !nocombo && e.getStateChange() == ItemEvent.SELECTED){
					//this fires once per click
					//& when program changes the box & then get repainted

				if (whichbit.getSelectedIndex() == question[questionumber]){
					whichbit.hidePopup();
					nextQuestion(true);
				} else {
					ignore = true;
					whichbit.hidePopup();
					whichbit.setSelectedIndex(question[questionumber]);
					ignore = false;
					nextQuestion(false);
				}
				setupQuestion();
			}
		} //ends itemstatelistener
	} // end handler class




//*************************************
// inner class to handle mouse events
//*************************************

	private class MouseClickHandler extends MouseAdapter {

		private boolean onceover = false;
		public void mouseReleased(MouseEvent e){
			doEvent(e);
		}

		public void mouseClicked(MouseEvent e)	{
			//doEvent(e);
		}
		private void doEvent(MouseEvent e){
			if (noarea == false){
				if ( whichbit.getItemAt(0) == "Choose"){
					ignore = true;
					whichbit.removeItemAt(0);
					ignore = false;
					onceover = true;
				}else onceover = false;

				int[] pixels = new int[1];

				PixelGrabber pg = new PixelGrabber(picture_base, e.getX(), e.getY(), 1, 1, pixels, 0, 2);

				try {
					pg.grabPixels();
				} catch (InterruptedException d) {System.out.println("interrupted waiting for pixels!");	    return;	}

				for (int n=0;n<no_components;n++){
					if (pixels[0] == (0xffff0000 - (n * 0x10000)) ){

						if (whichbit.getSelectedIndex()!=n || onceover == true ){ //if not clicking on area that has already been selected
							if (testmode == true){
								index = (question[questionumber] - no_components);
								picturebox.repaint();
								nextQuestion(false);
							} else {
								//if (textbox.n==no_components) {textbox.n=0; textbox.update();}
								//index=n;
								//picturebox.repaint();
								whichbit.setSelectedIndex(n); //make it selected!
							}

							break;
						} else if (whichbit.getSelectedIndex()==n && testmode==true) {
							//System.out.println("clicking on somewhere already highlighted!");
							index = (question[questionumber] - no_components);
							picturebox.repaint();
							nextQuestion(true);
						}

						break;
					}
				}//ends for loop
			} //ends noarea
		}

	} //end mouseclickhandler



//**************************
// Question Handler
//**************************

	private void QuestionHandler(){
		questionumber = 0;
		correct = 0;

		int n,m;
		double temp;
		boolean unique;

		for (n=0;n<(no_components*2);n++)
		{
			do{
				unique = true;
				temp = (no_components*2 * Math.random());
				question[n] = (int)temp;
				for (m=0;m<n;m++) //check below n
			 if (question[m] == question[n]) unique=false;
		 for (m=(n+1);m<(no_components*2);m++) //check above n (not really needed as haven't yet defined them!!)
			if (question[m] == question[n]) unique=false;
			}while (!unique);
		}

		setupQuestion();
	} // ends Questionhandler



//*****************
// Next question
//*****************

	private void nextQuestion(boolean answer){

//check if question correct - if so display correct!
// if wrong say wrong & show correct answer....
// then inc. question number & paralyse the correct input methods
// unless question no > 32 then display summary & uncheck box

// reset display


		if (answer == true) {
			JOptionPane.showMessageDialog(null,"Correct!","You got it...",JOptionPane.INFORMATION_MESSAGE );
			correct++;
		}

		else {
			JOptionPane.showMessageDialog(null,"Wrong! - The correct answer is shown","You got it...",JOptionPane.INFORMATION_MESSAGE);
		}


		if (questionumber < ((no_components*2) - 1)){
			questionumber++;
			setupQuestion();
		}

		else 	{

			ignore = true;

			testme.doClick();
			testmode = false;

			ignore = false;


			JOptionPane.showMessageDialog(null,correct + " out of " + (no_components*2),"You scored",JOptionPane.INFORMATION_MESSAGE);
		}

	}  // ends NextQuestion


//*****************
// Set up question
//*****************

	private void setupQuestion(){

		if (!testmode) testmode = false;

		else if (question[questionumber] > (no_components - 1))
		{
			ignore = true;
			noarea = false;
			nocombo = true;
			index = no_components;  // de-highlights all the picture

			whichbit.setEnabled(false);	// prevents responses from combo box
			whichbit.setSelectedIndex(question[questionumber]-no_components); // sets question up in combobox

			picturebox.repaint();
			textbox.update();

			ignore = false;
			// need to put up correct text in textbox
			// can't click on correct area cos already 'highlighted' i.e. selected in combobox
		}

		else {

			ignore = true;
			noarea = true;			//prevents clicking in the area window
			nocombo= false;			//allows event handling by the combo box
			whichbit.setEnabled(true);	//allows selection in the combo box

			// need to set combobox to 'choose'
			whichbit.setSelectedIndex(0);

			if (whichbit.getItemAt(0) == brainStrings[1])
			{
				whichbit.insertItemAt("Choose",0);
				whichbit.setSelectedIndex(0);
			}

			index=question[questionumber];  // sets up correct highlighting of area

			picturebox.repaint();
			textbox.update();

			ignore = false;
			//need to put correct text in textbox

		}
	}// ends setup question















	protected class TextPanel extends JTextPane{
		public TextPanel(){
			setEditable(false);
			setPreferredSize(getPreferredSize());
			setMargin(new Insets(3,3,3,3));
			setFont(new Font("Dialog", Font.BOLD, 12));
		}
		public int n; // question number - I think?

		public void init (){
			n = 0;
		};


		public void update(){
			String as = null;
			if(testmode == true){
				if (question[questionumber] <= no_components-1)
					as = "Select the name of the highlighted region";
				else if (question[questionumber] >= no_components)
					as = "Select the area of brain named";
				else as = "something wrong with the text matching!";
			}else{
				as = textStrings[n];
			}
			setText(as);
		}
	}

	/** The currently selected index */
	protected int index;

	class RedBlueSwapFilter extends RGBImageFilter {
		int tindex;
		public RedBlueSwapFilter(int i) 	{
			tindex = i;
			canFilterIndexColorModel = true;
		}
		public int filterRGB(int x, int y, int rgb)		{
			for (int n=0;n<18;n++){   //colour these in grey
				if (rgb == (0xffff0000 - (n * 0x10000)) )		{
					if (n == tindex) return 0xFF1D52FF;
					else return 0x848484;
				}
			}
			return ((255 << 24) | ((rgb & 0xff0000) << 0) | ((rgb & 0xff00) << 0 ) | (rgb & 0xff) );
		}
	}

}

// Vowels.java
package com.neurolab;

// By Robin Marlow
// 4/07/00 created radio buttons in a gridbag panel
// 5/07/00
// vowels 1.2	replaced EE, AH etc images with an array
// vowels 1.3	works with NeurolabExhibit
// vowels v1.4	sound added
// needs return button, robin?
// doesn't work, robin?
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.neurolab.common.*;

public class Vowels extends NeurolabExhibit {

	 private String names[] = { "Larynx alone","EE (beat) ","I (bit)","AY (bait)","E (bet)",
						"A (bat)","ER (Bert)","U (but) ","AH (Bart)",
															"O (bot)","AW (bought)","OH (boat)","OO (boot)" };
	 private String mnames[] =
			{ "Larynx.gif", "EE.gif", "I.gif", "AY.gif",
	"E.gif", "A.gif", "ER.gif", "U.gif", "AH.gif",
	"O.gif", "AW.gif", "OH.gif", "OO.gif" };

	// frequencies of the formants 2 per vowel..
	public static float formant [][] = {{0,0},{300, 2600},{400, 2400},{500, 2200},{600, 2000},{700, 1800},
			{600, 1600},{800, 1400},{800, 1200},{700, 1100},{600, 1000},{500, 900},{400, 800}};

	 public JPanel freqPanel,typePanel,buttonPanel;

	 public GraphPanel graphArea;
	 public PicturePanel mouthArea;
	 public Spacer spacer1, spacer2, spacer3, spacer4, spacer5,spacer6,spacer7;

	 TitledBorder titBord1, titBord2;

	private JRadioButton choices[], freq, spect, high, med, low;
	private ButtonGroup vowelgroup,typegroup,freqgroup;

	private GridBagLayout chkbxLayout,mainLayout;
	private GridBagConstraints chkbxGbConstraints,mainGbConstraints;

	private int currentvowel=0,currentfreq=1;
	public Image throatimage[];
		private byte[] buffer;
		CustomSound sound;			// current sound object
		public JButton listen;
		private int oldvowel=13;			// current vowel index
		private int oldfreq=0;


	 public String getExhibitName(){
		 return "Vowels";
	 }
	public void init(){
		super.init();
		throatimage=new Image[13];
		for (int i=0;i<13;i++){
			throatimage[i]=getImage("resources/vowels/"+mnames[i]);
		}
		buffer=new byte[192];
		sound=new CustomSound(buffer);
		createComponents();
	}
	public void createComponents(){
	//Make the Graph panel
	graphArea = new GraphPanel();
	graphArea.setBackground(systemGray);
	graphArea.setBorder(loweredbevel);

	//Make the Vowel choice radiobuttons
	choices = new JRadioButton[ names.length ];
	buttonPanel = new JPanel();
	buttonPanel.setBackground (systemGray);
	buttonPanel.setBorder(etched);

	chkbxLayout = new GridBagLayout();
	buttonPanel.setLayout(chkbxLayout);
	chkbxGbConstraints = new GridBagConstraints();
	chkbxGbConstraints.fill = GridBagConstraints.HORIZONTAL;
	ButtonHandler handler = new ButtonHandler();

	// make the others initially off
	// group them so only one on at once
	vowelgroup = new ButtonGroup();

	for ( int i = 0; i < choices.length; i++ ) {
					choices[ i ] = new JRadioButton( names[ i ], i==0 );	// is off unless i==0
		choices[ i ].setBackground(systemGray);
		addchkboxComponent( choices[ i ],
			(i<7)?i:(i%7)+1, (i-1)/6, (i==0)?2:1,1);	// add to checkbox
		choices[ i ].addItemListener( handler );		// add listener
		vowelgroup.add(choices[i]);				// add to radio group
	}




// create waveform display type buttons

	freq = new JRadioButton ("Waveform", false);
	spect = new JRadioButton ("Spectrum", true);

	freq.setBackground(systemGray);
	spect.setBackground(systemGray);

	typegroup = new ButtonGroup();
	typegroup.add(freq);
	typegroup.add(spect);

	typePanel = new JPanel();
	typePanel.setLayout(new GridLayout(2,1));
	typePanel.add(spect, BorderLayout.WEST );
	typePanel.add(freq, BorderLayout.WEST );
	typePanel.setBackground(systemGray);

	titBord1 = BorderFactory.createTitledBorder(etched, "Showing");
	typePanel.setBorder(titBord1);

	freq.addItemListener( handler );
	spect.addItemListener( handler );

// create frequency buttons
			high = new JRadioButton ("High", false);
			high.setBackground(systemGray);
			med = new JRadioButton ("Medium", false);
			med.setBackground(systemGray);
			low = new JRadioButton ("Low", true);
			low.setBackground(systemGray);

			freqgroup = new ButtonGroup();
			freqgroup.add(high);
			freqgroup.add(med);
			freqgroup.add(low);

			freqPanel = new JPanel();
			GridLayout glfreq;
			freqPanel.setLayout(glfreq=new GridLayout(3,1));
			glfreq.setHgap(10);
			freqPanel.add(high, BorderLayout.WEST );
			freqPanel.add(med, BorderLayout.WEST);
			freqPanel.add(low, BorderLayout.WEST);
			freqPanel.setBackground (systemGray);
			titBord2 = BorderFactory.createTitledBorder(etched, "Frequency");
			freqPanel.setBorder(titBord2);

			high.addItemListener( handler );
			med.addItemListener( handler );
			low.addItemListener( handler );


// Make spacers
/*	spacer1 = new Spacer(50,50);
	spacer2 = new Spacer(50,50);
	spacer3 = new Spacer(50,50);
	spacer4 = new Spacer(50,50);
	spacer5 = new Spacer(50,50);
	spacer6 = new Spacer(50,50);
	spacer7 = new Spacer(50,50);
*/


//
// add all components to main container
//

			//setup container
	getMainContainer().setBackground(systemGray);
			mainLayout = new GridBagLayout();
			maincontainer.setLayout(mainLayout);
			mainGbConstraints = new GridBagConstraints();
			mainGbConstraints.insets=new Insets(10,10,10,10);

			//add the buttonpanel
			mainGbConstraints.fill=GridBagConstraints.BOTH;
			mainGbConstraints.weightx = 40;
			mainGbConstraints.weighty = 40;
			addMainComponent( buttonPanel, 3, 7, 3, 3 );

			//add the mouthpanel
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			mouthArea = new PicturePanel();
			mouthArea.setBackground(systemGray);
			mainGbConstraints.weightx = 20;
			mainGbConstraints.weighty = 15;
			addMainComponent( mouthArea, 6, 7, 2, 2 );

		 mainGbConstraints.fill = GridBagConstraints.BOTH;
			mainGbConstraints.weightx = 20;
			mainGbConstraints.weighty = 10;
			addMainComponent(typePanel, 5, 4, 2, 3);

			mainGbConstraints.fill = GridBagConstraints.BOTH;//was horz
			mainGbConstraints.weightx = 20;
			mainGbConstraints.weighty = 10;
			addMainComponent(freqPanel, 5, 2, 2, 3);

			mainGbConstraints.fill = GridBagConstraints.BOTH;
			mainGbConstraints.weightx = 40;
			mainGbConstraints.weighty = 40;
			addMainComponent( graphArea, 3, 1, 5, 1);





// this is the listen button
		 mainGbConstraints.fill = GridBagConstraints.BOTH;
			mainGbConstraints.weightx = 20;
			mainGbConstraints.weighty = 10;
			listen=new Button3D("Listen");
			addMainComponent(listen, 6, 9, 1, 1);
			listen.setBackground(systemGray);
		listen.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if((oldvowel!=getVowel())||(oldfreq!=getFreq())){	//if the wave needs reloading?
					oldvowel=getVowel();
					oldfreq=getFreq();
					sound.close();
					WaveMaker();
					sound.open(buffer);
				}
				sound.start();
			};
			public void mouseReleased(MouseEvent e){
				sound.stop();
			}
		} );
	addMainComponent(new ReturnButton(),7,9,1,1);
	}


	class Button3D extends JButton{
		private final int bi=2;
		public Button3D(String t){
			super(t);
			setFont(new Font("Dialog",Font.PLAIN,20));
		}
		public void paint(Graphics g){
			super.paint(g);
			g.setColor(systemGray);
			g.fillRect(bi,bi,getWidth()-2*bi,getHeight()-2*bi);
			paintText3D(g,getText(),
				(getWidth()-getTextWidth(g,getText()))/2,
				getHeight()/2);
//			System.out.print("Repaint");
		}
	}

// addchkboxComponent is programmer defined
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
			buttonPanel.add( c );      // add component
	 }

// addMainComponent is programmer defined
	 private void addMainComponent( Component c,
			int row, int column, int width, int height )
	 {
			// set gridx and gridy
			mainGbConstraints.gridx = column;
			mainGbConstraints.gridy = row;

			// set gridwidth and gridheight
			mainGbConstraints.gridwidth = width;
			mainGbConstraints.gridheight = height;

			// set constraints
			mainLayout.setConstraints( c, mainGbConstraints );
			getMainContainer().add( c );      // add component
	 }


	 // creates wave in a byte array
	public void WaveMaker(){
		float fbuffer[]=new float[192];
		int waven = 192 / getFreq();
		if(getVowel()==0){	//larynx only
			for(int i=0;i<192;i++)buffer[i]=(byte)128;
			for(int i=0;i<getFreq();i++)buffer[i*waven]=buffer[i*waven+1]=1;;
		} else {
			double x=formant[getVowel()][0]*Math.PI/11000;
			double y=formant[getVowel()][1]*Math.PI/11000; //?11050?
			for(int i=0;i<192;i++)fbuffer[i]=0;
			for(int j=0;j<getFreq();j++){
				for(int i = 0 ;i< 192;i++)
				{
					fbuffer[(i+j*waven)%192]+=(byte)(
					 (Math.sin(x*(float)i)+Math.sin(y*(float)i))*Math.exp(-((float)i)/30));
				}// /80
			}
			for(int i=0;i<192;i++)buffer[i]=(byte)(128+32*fbuffer[i]);
		};
	}
	public int getVowel(){return currentvowel;}
	public int getFreq(){return currentfreq;}
	boolean larynx(){return getVowel()==0;}




	 private class ButtonHandler implements ItemListener {
			public void itemStateChanged( ItemEvent e )
			{
				 for ( int i = 0; i < choices.length; i++ )
						if ( e.getSource() == choices[ i ] )
							 {
							 currentvowel=i;
				 mouthArea.vowel = i;
				 mouthArea.repaint();
				 graphArea.vowel = i;
							 break;
				 }

						else if ( e.getSource() == freq )
		graphArea.typeswitch = 1;
						else if ( e.getSource() == spect )
		graphArea.typeswitch = 0;

			else if ( e.getSource() == high ){
					currentfreq=3;
		graphArea.step = 3;
			}
			else if ( e.getSource() == med ){
					currentfreq=2;
		graphArea.step = 2;
			}
			else if ( e.getSource() == low ){
		currentfreq=1;
		graphArea.step = 1;
			}
	graphArea.repaint();

			}
	 }



	//**********************************
	//   CLASS drawing the mouth picture
	//**********************************


class PicturePanel extends JPanel {
	public int vowel = 13;
	public void paint(Graphics g){
		super.paint(g);
		if(vowel==13){
			for(int i=0;i<13;i++)
				g.drawImage(throatimage[i],50,30,1,1,this);
			vowel=0;
		}
		g.drawImage(throatimage[vowel],0,0,73,57,this);
	}
}




	//********************************
	//   CLASS drawing the graphs area
	//********************************

class GraphPanel extends JPanel {
	 // private int width = 100, height = 100;


	 public int vowel;
	 private Rectangle rect;
	 public int step = 0;
	 public int typeswitch;


	 public GraphPanel( )
	 {
	 step = 1;
	 }
	public void paint( Graphics g){
		super.paint(g);
		int ax,ay,bx,cx,incr,loop;
		ax = 10;
		ay = 125;  // a(x,y) start point
		bx = 210; //  b x endpoint
		incr = 5;		// gap between
		loop = 1;		// loop number

		float z1,z2,y,x;
		y=0;
		float const1,scalingfactor,constbaseline;

		// precalculated exponential decay
		// -i/80 too shallow
		// -i/60 much better
		double expdec[]= {0.98,0.97,0.95,0.94,0.92,0.90,0.89,0.88,0.86,0.85,0.83,0.82,0.81,0.79,0.78,0.77,0.75,0.74,0.73,0.72,0.70,0.69,0.68,0.67,0.66,0.65,0.64,0.63,0.62,0.61,0.60,0.59,0.58,0.57,0.56,0.55,0.54,0.53,0.52,0.51,0.50,0.50,0.49,0.48,0.47,0.46,0.46,0.45,0.44,0.43,0.43,0.42,0.41,0.41,0.40,0.39,0.39,0.38,0.37,0.37,0.36,0.36,0.35,0.34,0.34,0.33,0.33,0.32,0.32,0.31,0.31,0.30,0.30,0.29,0.29,0.28,0.28,0.27,0.27,0.26,0.26,0.25,0.25,0.25,0.24,0.24,0.23,0.23,0.23,0.22,0.22,0.22,0.21,0.21,0.21,0.20,0.20,0.20,0.19,0.19,0.19,0.18,0.18,0.18,0.17,0.17,0.17,0.17,0.16,0.16,0.16,0.15,0.15,0.15,0.15,0.14,0.14,0.14,0.14,0.14,0.13,0.13,0.13,0.13,0.12,0.12,0.12,0.12,0.12,0.11,0.11,0.11,0.11,0.11,0.11,0.10,0.10,0.10,0.10,0.10,0.10,0.09,0.09,0.09,0.09,0.09,0.09,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.07,0.07,0.07,0.07,0.07,0.07,0.07,0.07,0.07,0.06,0.06,0.06,0.06,0.06,0.06,0.06,0.06,0.06,0.06,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.05,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04,0.04};

		int shift = 10;
		constbaseline = 120; //was 120

		g.setColor(Color.black);
		rect = new Rectangle(shift,shift,getWidth() - 2*shift,getHeight()-2*shift-20 );
									//was 270
		//was fill shape RoundRectangle2D_20_20 ( 19-9-02 )
		g.fillRect(rect.x, rect.y, rect.width, rect.height);

		if(typeswitch==0){	// Spectrum
			g.setFont(new Font("Ariel", Font.PLAIN, 20));
			paintText3D(g,"Spectrum",100,getHeight()-9);
			for(float i=1;i<3000;i = i + (75 * step)){
				x = 2*shift+(i / 3000) * (getWidth()-4*shift);
				const1 = 200;
				scalingfactor = constbaseline - 50; //was 30
				if (vowel == 0) y = constbaseline - 80 ;
				else{
					z1 = ((formant[vowel][0] - i) / const1); //was 200
					z2 = ((formant[vowel][1] - i) / const1);
					y = (scalingfactor * ((1/(1 + z1*z1))+(1/(1 + z2*z2)))); //(int)
				}
				g.setColor(Color.green);
				g.drawLine((int)x,(int)constbaseline,(int)x,(int)(constbaseline-y));
			}
			// draw red baseline
			g.setColor(Color.red);
			g.drawLine(shift,(int)constbaseline,getWidth()-shift,(int)constbaseline);
		}else{		//Waveform
			antiAlias(g);
			g.setFont(new Font("Ariel", Font.PLAIN, 20));
			paintText3D(g,"Waveform",100,getHeight()-9);
			int waven=200/step; 		// the length of wave repetition envelope
			int w[]= new int [200]; // array storing the 192 points plotted
			double X,Y;
			int j;
			int baseline = 60;

			for ( int i = 0; i < 200; i++)
				w[i] = baseline;             // fill array with y=0
			if (vowel==0) w[1] =w[2]= 30;else {
				X = formant[vowel][0] * (Math.PI / 7000); //was 11000
				Y = formant[vowel][1] * (Math.PI / 7000);
				for ( int i = 1; i < 200; i++)
					w[i] = (int)( baseline + expdec[i]* 30 *( Math.sin(X * i) + Math.sin(Y * i)));
			}
			if (step != 1){
				int i = 0;
				j = waven;
				do{
					w[j]=w[j] + w[i] - baseline;
								i++;j++;j%=200;
				}while (j < 199);
			}

			cx = 10;
			int hoffset=0,voffset=10;
			j=0;
			g.setColor(Color.yellow);
			for ( int i = 0; i < 130; i++){		// draw the line that we've calculated!
				g.drawLine ( hoffset+j+shift,voffset+w[i]+shift,hoffset+2+j+shift,voffset+w[i+1]+shift);
				j+=2;
			}
		}
	}
}

}

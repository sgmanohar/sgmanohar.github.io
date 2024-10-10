// Colour mixing applet for neurolab=0A=
// By Robin=0A=

package com.neurolab;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.neurolab.common.*;

public class ColourMixing extends NeurolabExhibit {

private JPanel buttonbox,randombox;
private JPanel squares,innersquare,fillsquare,fillsquare2,fillsquare3,sliderbarpanel,colourdisplaypanel;

private JButton button;
private JButton blackout,whiteout;
private JCheckBox randomButton;

private Triangle triangle;
private Section section;
public buttonPanel buttonpanel;
private Complementary complement;

private SliderControls fore,back;

private GridBagLayout mainLayout,squaresLayout, cdpLayout;
private GridBagConstraints mainGbConstraints,squaresGbConstraints,cdpGbConstraints;
private Container maincontainer;
private Timer timer;

Color systemGray;

public int gap;
public boolean equiluminant = false;

public double length = 200;
public double angle = Math.PI/3; //60 deg
public int height = (int) (length * Math.sin(angle));
public int radius = (int)( 0.2f * height);
public Point circ_cent = new Point((int)(length -
(Math.cos(angle/2)*height*0.4f)),(int)(0.8f*height));

public Point west,east,north,left,right;
public InputHandler handler = new InputHandler();

Image colourtriangle;

public String getExhibitName(){
		 return "Colours";
		}

public void init()
{
	super.init();

	 // make flexible look & feel
	 try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			 } catch (Exception e) { }

systemGray = new Color (191,191,191);

Border etched,raisedbevel;
etched = BorderFactory.createEtchedBorder();

createComponents();

} // end init


public void createComponents()
{
//***********************

// Create components

//***********************

// creates the coloured squares panel
	squares = new JPanel();
	squares.setBorder(loweredbevel);

// creates the colour triangle
	triangle = new Triangle();
	triangle.setBorder(loweredbevel);
	triangle.addMouseListener(handler);
	addMouseListener(handler);

//creates the triangle section
	section = new Section();

//creates the complement section
	complement = new Complementary();

	buttonpanel = new buttonPanel();

//create panel for colour displays to go in
	colourdisplaypanel = new JPanel();
	//colourdisplaypanel.setBorder(loweredbevel);

//create panel for slider bars to go in
	sliderbarpanel = new JPanel();

//create foreground bars
fore = new SliderControls(this,255,24,255,true);

//create background bars
back = new SliderControls(this,255,255,255,false);


//creates time event

timer = new Timer(2000, new ActionListener() {
	int counter = 0;
				public void actionPerformed(ActionEvent evt) {
	counter++;
				if (counter>9) {
					//System.out.println("Bah!");
		counter=0;
								fore.randomizeSliders();
		}

	else 	{
		//System.out.println("Boo!");
		back.randomizeSliders();
		}
	}
			});




//******************************
// add components to main panel
//******************************

	//setup container

			maincontainer = getMainContainer();
			getMainContainer().setBackground (systemGray);
			mainLayout = new GridBagLayout();
			maincontainer.setLayout (mainLayout);
			mainGbConstraints = new GridBagConstraints();

			// add the triangle display
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			addMainComponent( triangle, 0, 0, 1, 4, 70, 100 );
	triangle.setBackground(Color.black);  //ods20
			calculate(); 		//calculate the colour triangle

			//add the slidebarpanel
			mainGbConstraints.fill = GridBagConstraints.VERTICAL;
			addMainComponent( sliderbarpanel, 0, 1, 1, 4, 10, 0 );

	GridLayout sliderlayout = new GridLayout();
			sliderbarpanel.setLayout(sliderlayout);
	sliderbarpanel.setBackground (systemGray);

			sliderlayout.setColumns(1);
			sliderlayout.setHgap(5);
			sliderlayout.setRows(2);
			sliderlayout.setVgap(5);
	sliderbarpanel.add(fore);
	sliderbarpanel.add(back);

/* now added inside the sliderbarpanel
					//add the foreground sliders
					mainGbConstraints.fill = GridBagConstraints.VERTICAL;
					addMainComponent( fore, 0, 1, 1, 2, 10, 0 );

					//add the background sliders
					mainGbConstraints.fill = GridBagConstraints.VERTICAL;
					addMainComponent( back, 2, 1, 1, 2, 10, 0  );
*/

			//add the colour display panel
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			addMainComponent( colourdisplaypanel, 0, 2, 1, 3, 5, 10 );

			cdpLayout = (new GridBagLayout());
			colourdisplaypanel.setLayout(cdpLayout);
			cdpGbConstraints = new GridBagConstraints();
			cdpGbConstraints.fill = GridBagConstraints.BOTH;
			cdpGbConstraints.weightx = 100;
			cdpGbConstraints.weighty = 20;
			cdpGbConstraints.gridwidth = 1;
			cdpGbConstraints.gridheight = 1;
			cdpGbConstraints.gridx = 0;

			cdpGbConstraints.gridy = 1;
			cdpLayout.setConstraints( section, cdpGbConstraints );
			colourdisplaypanel.add( section );      // add component

			cdpGbConstraints.gridy = 2;
			cdpLayout.setConstraints( complement, cdpGbConstraints );
			colourdisplaypanel.add( complement );      // add component


/*  now added inside the colourdisplay panel
			//add the section
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			addMainComponent( section, 1, 2, 1, 1, 5, 10 );

			//add the complement box
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			addMainComponent( complement, 2, 2, 1, 1, 5, 10 );

			//add the coloured squares display
			squares = new JPanel();
			mainGbConstraints.fill = GridBagConstraints.BOTH;
			addMainComponent( squares, 0, 2, 1, 1, 5, 20 );
*/

			squares = new JPanel();
			cdpGbConstraints.gridy = 0;
			cdpGbConstraints.weighty = 60;
			cdpLayout.setConstraints( squares, cdpGbConstraints );
			colourdisplaypanel.add( squares );      // add component

			squaresLayout = (new GridBagLayout());
			squares.setLayout(squaresLayout);
			squaresGbConstraints = new GridBagConstraints();

			innersquare = new JPanel();
			squaresGbConstraints.fill = GridBagConstraints.BOTH;
			squaresGbConstraints.weightx = 33;
			squaresGbConstraints.weighty = 33;
			squaresGbConstraints.gridx = 1;
			squaresGbConstraints.gridy = 1;
			squaresGbConstraints.gridwidth = 1;
			squaresGbConstraints.gridheight = 1;

			// set constraints
			squaresLayout.setConstraints( innersquare, squaresGbConstraints );

			squares.add( innersquare );      // add component

			fillsquare = new JPanel();
			squaresGbConstraints.gridx = 1;
			squaresGbConstraints.gridy = 2;
			fillsquare.setOpaque(false);
			squaresGbConstraints.fill = GridBagConstraints.BOTH;
			squaresGbConstraints.weightx = 33;
			squaresGbConstraints.weighty = 33;
			squaresGbConstraints.gridx = 0;
			squaresGbConstraints.gridy = 1;
			squaresGbConstraints.gridwidth = 1;
			squaresGbConstraints.gridheight = 1;
			squaresLayout.setConstraints( fillsquare, squaresGbConstraints );
			squares.add( fillsquare );      // add component

			fillsquare2 = new JPanel();
			squaresGbConstraints.gridx = 2;
			squaresGbConstraints.gridy = 0;
			fillsquare2.setOpaque(false);
			squaresLayout.setConstraints( fillsquare2, squaresGbConstraints );

			squares.add( fillsquare2);      // add component

			fillsquare3 = new JPanel();
			squaresGbConstraints.gridx = 0;
			squaresGbConstraints.gridy = 2;
			fillsquare3.setOpaque(false);
			squaresLayout.setConstraints( fillsquare3, squaresGbConstraints );

			squares.add( fillsquare3);      // add component

			int forecolours[] = new int [3];
			int backcolours[] = new int [3];

			fore.getValues(forecolours);
			back.getValues(backcolours);

			innersquare.setBackground(new Color
(forecolours[0],forecolours[1],forecolours[2]));
			squares.setBackground(new Color
(backcolours[0],backcolours[1],backcolours[2]));


			//add the buttonbox
			mainGbConstraints.fill = GridBagConstraints.VERTICAL;
			mainGbConstraints.fill = GridBagConstraints.EAST;
			addMainComponent( buttonpanel, 3, 2, 1, 1, 5, 0 );

} // ends createcomponents


public void start()
{
}

public void paint (Graphics g) //gets called on by repaint or by window covering etc etc
{
	fore.repaint();
	back.repaint();
	buttonpanel.repaint();
	triangle.repaint();
	section.repaint();
	complement.repaint();

		//this acts as squares repaint
	int forecolours[] = new int [3];
	int backcolours[] = new int [3];

	fore.getValues(forecolours);
	back.getValues(backcolours);

	innersquare.setBackground(new Color (forecolours[0],forecolours[1],forecolours[2]));
	squares.setBackground(new Color (backcolours[0],backcolours[1],backcolours[2]));
}


//**************************
// defines addMainComponent
//**************************

	 private void addMainComponent( Component c,

			int row, int column, int width, int height, int weightx, int weighty )

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



//**********************
// The input listener
//**********************


private class InputHandler implements ItemListener,ChangeListener,ActionListener,MouseListener {

			public void itemStateChanged( ItemEvent e )
	{
		Object source = e.getItemSelectable();
		if (source == buttonpanel.randomButton) {
			timer.start();}

		if (e.getStateChange() == ItemEvent.DESELECTED && source == buttonpanel.randomButton){
			timer.stop();}

		if (e.getStateChange() == ItemEvent.SELECTED && source == buttonpanel.constantButton) {
			equiluminant = true;
			calculate();
			repaint();
			}

		if (e.getStateChange() == ItemEvent.DESELECTED && source == buttonpanel.constantButton){
			equiluminant = false;
			calculate();
			repaint();
			}

	}// ends itemstate listener

	public void stateChanged (ChangeEvent e) {	// sliderbars
		repaint();
	} // ends state listener

	public void actionPerformed(ActionEvent e){	// return button
		if(e.getActionCommand()=="Return")toExhibitChooser(); }//ends action listener

	public void mouseClicked(MouseEvent e){
		 if (e.getSource() == triangle){
				//System.out.println(e.getX());
			//System.out.println(e.getY());
		if ( (e.getY() > 76) && (e.getY() < 249) &&
				 (e.getX() > (120 - (e.getY()-76)*0.58f)) &&
				 (e.getX() < (120 + (e.getY()-76)*0.58f)) ) //if within triangle
			{
			//System.out.println("in!");

//******************
// need to determine the site of click & set values accordingly
//******************


		Point red_corner = new Point(20,249);
		Point green_corner = new Point(219,249);
		Point blue_corner = new Point(119,76);

		int red_dist = (int)ptLineDist( green_corner,
			 blue_corner, e.getPoint() );
		int green_dist = (int)ptLineDist( red_corner,
			blue_corner, e.getPoint() );
		int blue_dist = (int)ptLineDist( red_corner,
			 green_corner, e.getPoint() );

		//System.out.println("R " + red_dist);
		//System.out.println("G " + green_dist);
		//System.out.println("B " + blue_dist);

		double scaling = 255.0f / (Math.max(red_dist,(Math.max(green_dist,blue_dist) ) ) );

		red_dist = (int)(red_dist * scaling);
		green_dist = (int)(green_dist * scaling);
		blue_dist = (int)(blue_dist * scaling);

			if (e.isMetaDown()) back.setValues(red_dist,green_dist,blue_dist);
			else fore.setValues(red_dist,green_dist,blue_dist);
			repaint();
			}

	//System.out.println("X" + e.getX() );
	//System.out.println("Y" + e.getY() );

		 }

	}

	public void mousePressed(MouseEvent e){

	if (e.getSource() == buttonpanel.blackout){
		innersquare.setBackground(new Color (0,0,0));
		squares.setBackground(new Color (0,0,0));
		}

	else if (e.getSource() == buttonpanel.whiteout){
		innersquare.setBackground(new Color (255,255,255));
		squares.setBackground(new Color (255,255,255));
		}

	}// ends mouse press listener

	public void mouseReleased(MouseEvent e){

	int forecolours[] = new int [3];
	int backcolours[] = new int [3];

	fore.getValues(forecolours);
	back.getValues(backcolours);

	innersquare.setBackground(new Color (forecolours[0],forecolours[1],forecolours[2]));
	squares.setBackground(new Color (backcolours[0],backcolours[1],backcolours[2]));

	}// ends mouse release listener

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}


} //ends Inputlistener


//***************************************
// Pre calculates the colour triangle picture
//***************************************

public void calculate()

{


double length = 200;
double angle;

angle = Math.PI/3; //60 deg

int height = (int) (length * Math.sin(angle));
int index = 0;
int pix[] = new int[(int)(length * height)];
double ratio = (255 / length);
double scaling;
int redcomp,greencomp,bluecomp;

for(int y=0;y<height;y++)
{
	int xinc = (int)(((length/2)/height)*y);
	for(int x=0;x<length;x++)
	{
		if (x >= (length/2 - xinc) && (x <= (length/2 + xinc))) 	//inside triangle
			{
//			int z = height - y;

			redcomp =   (int) (ratio * ptLineDist( (length/2), 0, length, height,x,y ) );
			greencomp = (int) (ratio * ptLineDist( (length/2), 0, 0, height, x,y ) );
			bluecomp =  (int) (ratio * ptLineDist( 0, height, length, height, x,y ) );

			if (equiluminant == false) scaling = (255.0f / Math.max(redcomp,(Math.max(greencomp,bluecomp))));
			else scaling = 1;

			redcomp = (int)(redcomp*scaling);
			greencomp = (int)(greencomp*scaling);
			bluecomp = (int)(bluecomp*scaling);

			if (distance(circ_cent, x,y) > radius && x > circ_cent.x && y > (circ_cent.y-10))
				{
				redcomp = 191;
				greencomp = 191;
				bluecomp= 191;
				}

			pix[index++] = ((255 << 24) | (redcomp << 16) | (greencomp << 8) | bluecomp);
			}

		else pix[index++] = 0;

	}

}

colourtriangle = createImage(new MemoryImageSource((int)length, (int)height, pix, 0, (int)length));

}


//*********************************************
//class for drawing the triangle graphic
//*********************************************

class Triangle extends JPanel {

//int length = 200;
double f_r,f_g,f_b,f_max;
double b_r,b_g,b_b,b_max;
int forecolours[] = new int [3];
int backcolours[] = new int [3];


	public void paintComponent ( Graphics g)
	{

	super.paintComponent (g);
	Graphics g2 = g;

	int gap = (int)(getWidth()-length)/2;
/*
	System.out.println(gap);
	System.out.println(colourtriangle.getHeight(this));
	System.out.println(colourtriangle.getWidth(this));
*/
	Point west = new Point(gap,4*gap + colourtriangle.getHeight(this));
	Point east = new Point(gap + colourtriangle.getWidth(this),4*gap + colourtriangle.getHeight(this));
	Point north = new Point((gap+(int)length/2),4*gap);

	g.drawImage(colourtriangle, gap, 4*gap, this);
/*
	GeneralPath triangle_area = new GeneralPath();
	triangle_area.moveTo((gap+length/2),4*gap);
	triangle_area.lineTo(gap + colourtriangle.getWidth(this),4*gap + =
colourtriangle.getHeight(this));
	triangle_area.lineTo(gap,4*gap + colourtriangle.getHeight(this));
	//triangle_area.lineTo((gap+length/2),gap);
	triangle_area.closePath();
*/
	g2.setColor(Color.black);
	g2.drawLine(north.x,north.y,east.x,east.y);
	g2.drawLine(east.x,east.y,west.x,west.y);
	g2.drawLine(west.x,west.y,north.x,north.y);

	fore.getValues(forecolours);
	back.getValues(backcolours);


f_r= forecolours[0]/255.0f;
f_g= forecolours[1]/255.0f;
f_b= forecolours[2]/255.0f;
f_max=(f_r+f_g+f_b);
f_r = f_r/f_max;
f_g = f_g/f_max;
f_b = f_b/f_max;


Point centre = new Point((west.x+(colourtriangle.getWidth(this)/2)),
						 (west.y-(colourtriangle.getHeight(this)/2)));

Point foreg = new Point((int)(centre.x + ( (1-f_r) *
		(colourtriangle.getWidth(this)/2))       - ( (1-f_g) *
		(colourtriangle.getWidth(this)/2))), (int)
			(centre.y  - ( ( f_b ) * (colourtriangle.getHeight(this)/2))
				+ ( (1-f_b) * (colourtriangle.getHeight(this)/2))));
	//calculate background (x,y)
b_r= backcolours[0]/255.0f;
b_g= backcolours[1]/255.0f;
b_b= backcolours[2]/255.0f;
b_max=(b_r+b_g+b_b);
b_r = b_r/b_max;
b_g = b_g/b_max;
b_b = b_b/b_max;


Point backg = new Point((int)(centre.x + ( (1-b_r) *
			(colourtriangle.getWidth(this)/2))- ( (1-b_g) *
			(colourtriangle.getWidth(this)/2))), (int)
				(centre.y  - ( ( b_b ) * (colourtriangle.getHeight(this)/2))
						+ ( (1-b_b) * (colourtriangle.getHeight(this)/2))));

Point new_circ_cent = new Point(circ_cent.x+gap,circ_cent.x+4*gap);

if((foreg.x > 0) && (backg.x > 0))	//draw line fore(x,y) to back(x,y)
	{
	//g2.draw(new Line(foreg,backg));
	//g2.draw(new Line(foreg,new_circ_cent));

	Point right = new Point(foreg.x,foreg.y);
	Point left = new Point(backg.x,backg.y);

if (foreg.x < backg.x)
	{
	left.setLocation(foreg);
	right.setLocation(backg);
	}

else 	{
	right.setLocation(foreg);
	left.setLocation(backg);
	}
        double p=right.x-left.x;
        if(p==0)p=0.001;
	double grad = ((right.y-left.y)/(p));

if (right.x > new_circ_cent.x && distance(new_circ_cent, right) > radius)
	do{
	int tempx = right.x-1;
	int tempy = right.y-(int)grad;
	right.setLocation(tempx,tempy);
	}while (right.x > new_circ_cent.x && distance(new_circ_cent,right) > radius);

		if ( (left.y >= 75) && (left.y <= 270) &&
				 (left.x >= (120 - (left.y-75)*0.58f)) &&
				 (left.x <= (120 + (left.y-75)*0.58f)) &&
				 (right.y >= 75) && (right.y <= 270) &&
				 (right.x >= (120 - (right.y-75)*0.58f)) &&
				 (right.x <= (120 + (right.y-75)*0.58f)))
				g2.drawLine((int)left.x,(int)left.y, (int)right.x,(int)right.y);

}


// add the text to the corners
// B
	g2.setFont(new Font("Ariel", Font.BOLD, 30));
	g2.setColor(Color.blue);
	g2.drawString("B",(int)(north.x-10),(int)(north.y-5));
// G
	g2.setColor(Color.green);
	g2.drawString("G",(int)(east.x-10),(int)(east.y+30));
// R
	g2.setColor(Color.red);
	g2.drawString("R",(int)(west.x-10),(int)(east.y+30));
	} //ends paint

} //ends triangle


//*********************************************
//class for drawing the section graphic
//*********************************************

class Section extends JPanel {

	public void paintComponent ( Graphics g)
	{
	super.paintComponent (g);
	Graphics g2 = g;

	setBackground (systemGray);

	double scaling;
	int forecolours[] = new int [3];
	int backcolours[] = new int [3];
	fore.getValues(forecolours);
	back.getValues(backcolours);

float r_step = (backcolours[0]-forecolours[0])/100.0f;
float g_step = (backcolours[1]-forecolours[1])/100.0f;
float b_step = (backcolours[2]-forecolours[2])/100.0f;

int xstart = ((getWidth()-100)/2);
int ystart = ((getHeight()-20)/2);

for(int x=0;x<100;x++)
{
	int newred = (int)(forecolours[0] + ( x * r_step));
	int newgreen = (int)(forecolours[1] + ( x * g_step));
	int newblue = (int)(forecolours[2] + ( x * b_step));

	if (equiluminant == false) scaling = (255.0f / Math.max(newred,(Math.max(newgreen,newblue))));
	else scaling = 1;

	newred = (int)(newred*scaling);
	newgreen = (int)(newgreen*scaling);
	newblue = (int)(newblue*scaling);

	g2.setColor(new Color(newred,newgreen,newblue));

	g2.drawLine(xstart+x,ystart,xstart+x,ystart+20);
}

	g2.setFont(new Font("Ariel", Font.PLAIN, 12));
	g2.setColor(Color.black);
	g2.drawString("Fore",20,(getHeight()/2)+5);
	g2.drawString("Back",(getWidth()-40),(getHeight()/2)+5);

	}

} //ends section

//*********************************************
//class for drawing the complementary graphic
//*********************************************

class Complementary extends JPanel {

	public void paintComponent ( Graphics g)
	{
	super.paintComponent (g);
	Graphics g2 = g;

	setBackground (systemGray);

	int forecolours[] = new int [3];
	fore.getValues(forecolours);

	g2.setColor(new Color((255-forecolours[0]),(255-forecolours[1]),(255-forecolours[2])));
	g2.fillRoundRect(30,0,30,30,5,5);

	g2.setFont(new Font("Ariel", Font.PLAIN, 12));
	g2.setColor(Color.black);
	g2.drawString("Complementary",70,12);
	g2.drawString("colour",75,28);

	}

} //ends complementary

//*********************************************
//class for drawing the buttonpanel
//*********************************************

class buttonPanel extends JPanel {

Button button;
Button blackout,whiteout;
JCheckBox randomButton,constantButton;
GridBagLayout buttonsLayout;
GridBagConstraints buttonsGbConstraints;
Spacer spacer1, spacer2, spacer3;


public buttonPanel()
{

setBackground (systemGray);

InputHandler handler = new InputHandler();

//create exit button
				button=new Button();
				button.setBackground(systemGray);
	button.setLabel("Return");
				button.addActionListener(handler);

//create auto-change checkbox
				randomButton = new JCheckBox("Auto change");
	randomButton.setSelected(false);
	randomButton.addItemListener(handler);
	randomButton.setBackground (systemGray);

//create equiluminance checkbox
				constantButton = new JCheckBox("Equiluminant");
	constantButton.setSelected(false);
	constantButton.addItemListener(handler);
	constantButton.setBackground (systemGray);

// creates the buttons & puts them in the buttonbox

	blackout = new Button();
	blackout.setLabel("Blackout");
	//blackout.setToolTipText("Press to blackout the colour box");
	blackout.setBackground(systemGray);

	whiteout = new Button ();
	whiteout.setLabel("Whiteout");
	//whiteout.setToolTipText("Press to whiteout the colour box");
	whiteout.setBackground(systemGray);

	blackout.addMouseListener(handler);
	whiteout.addMouseListener(handler);

	setBorder(etched);

	buttonsLayout = (new GridBagLayout());
	setLayout(buttonsLayout);
	buttonsGbConstraints = new GridBagConstraints();

	buttonsGbConstraints.fill = GridBagConstraints.BOTH;
	buttonsGbConstraints.weightx = 0;
	buttonsGbConstraints.weighty = 24;
				buttonsGbConstraints.gridwidth = 1;
	buttonsGbConstraints.gridheight = 1;

	buttonsGbConstraints.gridx = 1;
	buttonsGbConstraints.gridy = 1;
	buttonsLayout.setConstraints( randomButton, buttonsGbConstraints );
				add( randomButton );      // add randomise checkbox

	buttonsGbConstraints.gridx = 2;
	buttonsGbConstraints.gridy = 1;
	buttonsLayout.setConstraints( blackout, buttonsGbConstraints );
				add( blackout );      // add blackout button

	buttonsGbConstraints.gridx = 1;
	buttonsGbConstraints.gridy = 3;
	buttonsLayout.setConstraints( constantButton, buttonsGbConstraints );
				//add( constantButton );      // add equiluminance checkbox

	buttonsGbConstraints.gridx = 2;
	buttonsGbConstraints.gridy = 3;
	buttonsLayout.setConstraints( whiteout, buttonsGbConstraints );
				add( whiteout );      // add whiteout button

	buttonsGbConstraints.gridx = 1;
	buttonsGbConstraints.gridy = 5;
				buttonsGbConstraints.gridwidth = 2;
	buttonsLayout.setConstraints( button, buttonsGbConstraints );
				add( button );      // add return button

				buttonsGbConstraints.gridwidth = 1;
				spacer1 = new Spacer(5,5);
				spacer1.setBackground(Color.red);
	buttonsGbConstraints.weightx = 2;
	buttonsGbConstraints.weighty = 2;
				buttonsGbConstraints.gridx = 0;
	buttonsGbConstraints.gridy = 0;
	buttonsLayout.setConstraints( spacer1, buttonsGbConstraints );
			add( spacer1 );      // add spacer1

				spacer2 = new Spacer(5,5);
				spacer2.setBackground(Color.red);
	buttonsGbConstraints.weightx = 2;
	buttonsGbConstraints.weighty = 2;
				buttonsGbConstraints.gridx = 3;
	buttonsGbConstraints.gridy = 3;
	buttonsLayout.setConstraints( spacer2, buttonsGbConstraints );
			add( spacer2 );      // add blackout button
/*
				spacer3 = new Spacer(5,5);
				spacer3.setBackground(Color.red);
	buttonsGbConstraints.weightx = 2;
	buttonsGbConstraints.weighty = 2;
				buttonsGbConstraints.gridx = 0;
	buttonsGbConstraints.gridy = 6;
	buttonsLayout.setConstraints( spacer3, buttonsGbConstraints );
			//add( spacer3 );      // add blackout button

	setLayout(new GridLayout(4,1,5,5));
	add(blackout,BorderLayout.EAST );
	add(randomButton,BorderLayout.EAST );
	add(whiteout,BorderLayout.EAST );
	add(button,BorderLayout.EAST);
*/

				setBackground (systemGray);
}
} //ends buttonpanel

//*********************************************
//class for drawing the sliderbars
//*********************************************

class SliderControls extends JPanel {

InputHandler handler = new InputHandler();
ColourMixing applet;
JSlider red,green,blue;
JPanel rlabel,glabel,blabel;

GridBagLayout sliderLayout;
GridBagConstraints sliderGbConstraints;

public SliderControls(ColourMixing app, int redstart, int greenstart,
					int bluestart, boolean foreground)
{

ballColour rlabel,blabel,glabel;

	applet = app;

	// create red bar
	red = new JSlider(SwingConstants.VERTICAL,0,255,redstart);
	red.setMajorTickSpacing(50);  // set spacing of ticks
	red.setPaintTicks( true );    // make sure draw ticks
	red.addChangeListener(handler);
	red.setBackground (systemGray);

	// create green bar
	green = new JSlider(SwingConstants.VERTICAL,0,255,greenstart);
	green.setMajorTickSpacing(50);  // set spacing of ticks
	green.setPaintTicks( true );    // make sure draw ticks
	green.addChangeListener(handler);
	green.setBackground (systemGray);

	// create blue bar
	blue = new JSlider(SwingConstants.VERTICAL,0,255,bluestart);
	blue.setMajorTickSpacing(50);  // set spacing of ticks
	blue.setPaintTicks( true );    // make sure draw ticks
	blue.addChangeListener(handler);
	blue.setBackground (systemGray);

	sliderLayout = (new GridBagLayout());
	setLayout(sliderLayout);
	sliderGbConstraints = new GridBagConstraints();

	sliderGbConstraints.fill = GridBagConstraints.BOTH;
	sliderGbConstraints.weightx = 33;
	sliderGbConstraints.weighty = 90;
	sliderGbConstraints.gridx = 0;
	sliderGbConstraints.gridy = 0;
	sliderGbConstraints.gridwidth = 1;
	sliderGbConstraints.gridheight = 1;
	sliderLayout.setConstraints( red, sliderGbConstraints );
				add( red );      // add red bar

	sliderGbConstraints.gridx = 1;
	sliderLayout.setConstraints( green, sliderGbConstraints );
				add( green );      // add green bar

	sliderGbConstraints.gridx = 2;
	sliderLayout.setConstraints( blue, sliderGbConstraints );
				add( blue );      // add blue bar

rlabel = new ballColour();
rlabel.setForeground(Color.red);
rlabel.setBackground (systemGray);


glabel = new ballColour();
glabel.setForeground(Color.green);
glabel.setBackground (systemGray);


blabel = new ballColour();
blabel.setForeground(Color.blue);
blabel.setBackground (systemGray);

	sliderGbConstraints.weightx = 33;
	sliderGbConstraints.weighty = 10;
	sliderGbConstraints.gridx = 0;
	sliderGbConstraints.gridy = 1;
	sliderGbConstraints.gridwidth = 1;
	sliderGbConstraints.gridheight = 1;
	sliderLayout.setConstraints( rlabel, sliderGbConstraints );
				add( rlabel );      // add panel for the colours

	sliderGbConstraints.gridx = 1;
	sliderLayout.setConstraints( glabel, sliderGbConstraints );
				add( glabel );      // add panel for the colours

	sliderGbConstraints.gridx = 2;
	sliderLayout.setConstraints( blabel, sliderGbConstraints );
				add( blabel );      // add panel for the colours


TitledBorder title1;

if (foreground) title1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
, "Foreground");
else title1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
, "Background");

title1.setTitleJustification(TitledBorder.CENTER);
setBackground (systemGray);
setBorder(title1);

} // ends slidercontrols public


		public int getValues(int vals[]) {
	vals[0] = red.getValue();
	vals[1] = green.getValue();
	vals[2] = blue.getValue();
	return 0;
		}

		public int setValues(int r,int g,int b) {
	red.setValue(r);
	green.setValue(g);
	blue.setValue(b);
	return 0;
		}

		public int getRed() {
	return red.getValue();
		}

		public int getGreen() {
	return green.getValue();
		}

		public int getBlue() {
	return blue.getValue();
		}

		public void randomizeSliders() {
			red.setValue((int)(255.0f*Math.random()));
	blue.setValue((int)(255.0f*Math.random()));
	green.setValue((int)(255.0f*Math.random()));
		}

//}//ends slidercontrols



//*************************************
//class for drawing the coloured balls
//*************************************

public class ballColour extends JPanel {

	int diameter;

	public void paintComponent ( Graphics g)
	{
		super.paintComponent (g);


		diameter = Math.min(getWidth(),getHeight());
		g.fillOval(0,0,diameter,diameter);

	}//ends graphics

} //ends ballColour

}//ends slidercontrols


}//ends colour mixing

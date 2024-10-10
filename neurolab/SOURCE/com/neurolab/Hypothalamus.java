// Neurolab Hypothalamus
// Robin Marlow
// 14/07/00

/*
Yet Todo
cache the images during loading rather than after first use
remove arbitary numbers i.e 16/15 & 32/31 to make easier to expand
make sure all the greys are constant - ods20 done
remove tell-tales
maybe make feedback system more elegant? & give more enthusiastic responses!!
possibly make it more colourful?
remove all the old crud from the code.

quirks
strange key handling during quiz mode b.cos as move cursor with keys, it selects each time.... thus 'supplying' the answer.

points that need manually adjusting for change of objects
	the switch function in Textpanel
	the colours areas are changed to in RedBlueSwapFilter
	the 32.0 in questionhandler (as it needs to be .0 & not an int)
	the text in the score box in questionhandler
	where it looks for the picture_base & size of it when it draws

	& last & not least LAYOUT!
*/

package com.neurolab;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*; // for manips
//import java.awt.MediaTracker.*;
import java.text.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.io.*;

import com.neurolab.common.*;

//public class Hypothalamus extends NeurolabExhibit implements ActionListener{

public class Hypothalamus extends RobinExhibit{

// setup some variables
	public int getNumComponents(){return 16;}
	public Dimension getImageDimension(){return new Dimension(215,255); }

// Define components
	 public String getExhibitName(){return "Hypothalamus";}


 //  Color systemGray;

	 {
		 brainStrings = new String[] {"Choose","Paraventricular nucleus",
			 "Preoptic nucleus","Anterior nucleus","Suprachiasmatic nucleus",
			 "Supraoptic nucleus","Dorsal nucleus","Dorsomedial nucleus",
			 "Ventromedial nucleus","Arcuate nucleus","Posterior nucleus",
			 "Mammillary body","Optic chiasm","Lateral nucleus","Median eminence",
			 "Adenohypophysis (anterior lobe)","Neurohypophysis (posterior lobe)"};

	 textStrings = new String[]{
			"Main origin of oxytocin-releasing cells in neurohypophysis, controlling uterine contraction and milk ejection.",
				"Includes periventricular and medial regions.  Concerned in control of release of gonadotrophins.",
			"  ",
			"A site of termination of fibres from the retina providing information about general light level, and thus time of day and season.",
			"Main origin of ADH-releasing terminals in neurohypophysis, controlling osmolarity: lesions produce excessive drinking.",
			"  ",
			"Many dopamine-containing cells.",
			"Classic 'satiety centre': lesions provoke over-eating and obesity, and also aggressive behaviour.  Cells probably act as glucose monitors.",
			"Many neurones containing dopamine.",
			"The posterior region is mostly associated with the control of sympathetic responses, and the conservation of body heat.",
			"Important input as output connections with much of the limbic system.",
			"Site of partial decussation of retinal ganglion cell axons.",
			"Classical 'feeding centre': lesions produce appetite loss, and reduced drinking.",
			"Site of release of releasing hormones into the hypophysial portal system by terminal of hypothalmic neurones.",
			"Under the control of releasing hormones carried from the hypothalamus through the portal system.  Hormones released include ACTH, growth hormone, TSH, gonadotrophins & prolactin.",
			"Here, terminals of fibres from the supraoptic and paraventricular regions release ADH and oxytocin.",
			"Click on an area to find out about it! ",
			};
	 }

public void init()
	 {

	 //call the exhibit init routine
	 super.init();

	 // load in the picture

	 picture_base = getImage("resources/imagemaps/hypothalamus.gif");

	 //ImageFilter colorfilter = new RedBlueSwapFilter();


	 }// end init

} // end hypothalamus.class


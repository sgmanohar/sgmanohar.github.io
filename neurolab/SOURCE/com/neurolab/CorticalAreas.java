// Neurolab Cortical areas
// Robin Marlow
// 14/07/00

/*
Yet Todo
query replace the image with a tracing of page 1145 of Grays anatomy

cache the images during loading rather than after first use
make sure all the greys are constant - ods20 done
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

public class CorticalAreas extends RobinExhibit{

// setup some variables

// Define components
	public String getExhibitName(){return "Cortical areas";}
	public int getNumComponents(){return 18;}
	public Dimension getImageDimension(){return new Dimension(321,226);}

	{
		brainStrings = new String[] {"Choose", "Areas 9-11,46: prefrontal cortex","Area 8: frontal eye fields","Area 6: premotor cortex","Area 4: primary motor cortex","Area 3: primary somatosensory cortex","Area 1: primary somatosensory cortex","Area 2: primary somatosensory cortex","Area 5: anterior parietal cortex","Area 7: posterior parietal cortex","Area 17: primary visual cortex","Area 18: parastriate, visual areas V2-V4","Area 19: med. temporal, visual area V5","Areas 39,40: inf. parietal; part of Wernicke","Area 21: inferotemporal cortex","Area 42: secondary auditory cortex","Area 41: primary auditory cortex","Area 22: sup. temporal: part of Wernicke","Areas 44,45: Broca's area (approximately)"};
		textStrings = new String[] {
			"1",
			"2",
			"3",
			"4",
			"5",
			"6",
			"7",
			"8",
			"9",
			"10",
			"11",
			"12",
			"13",
			"14",
			"15",
			"16",
			"17",
			"18",
			"Click on an area to find out about it! ",
			"shouldn't see this!!"
		};
	}

	public void init()
	{

		//call the exhibit init routine
		super.init();

		// load in the picture

		picture_base = getImage("resources/imagemaps/corticalareas.gif");


	}// end init
}
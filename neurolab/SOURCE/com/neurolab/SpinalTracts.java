// Neurolab Spinal Tracts
// Robin Marlow
// 14/07/00

/*
Yet Todo

get dora to redraw the image - (360x231)
consider making it swappable between asc & descending & then showing the bilateral tracts as bilateral??

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

import java.awt.Dimension;

public class SpinalTracts extends RobinExhibit{

// setup some variables
	public int getNumComponents(){return 15;}
	public String getExhibitName(){return "Spinal Tracts";}
	public Dimension getImageDimension(){return new Dimension(360,231);}
			/** From RobinExhibit */
			{
				question = new int[no_components * 2];
				textStrings = new String[]{
					 "Ascending uncrossed fibres",
						 "Ascending uncrossed fibres",
					 "Ascending uncrossed fibres",
					 "Ascending crossed fibres",
					 "Ascending crossed fibres",
					 "Ascending crossed fibres",
					 "Ascending crossed fibres",
					 "Ascending crossed fibres",
					 "Descending bilateral fibres",
					 "Descending crossed fibres",
					 "Descending uncrossed fibres",
					 "Descending uncrossed fibres",
					 "Descending uncrossed fibres",
					 "Descending crossed fibres",
					 "Descending crossed fibres",
					 "Click on an area to find out about it! ",
					 "shouldn't see this!!"
					 };
				brainStrings = new String[]{
					"Choose", "Gracile fasciculus","Cuneate fasciculus",
					"Post. sp-cerebellar","Lateral sp-thalamic","Anterior sp-cerebellar",
					"Spino-olivary","Spino-tectal","Anterior sp-thalamic",
					"Anterior cortico-sp","Tecto-spinal","Medullary reticulo-sp",
					"Vestibulo-spinal","Pontine reticulo-sp","Rubro-spinal",
					"Lateral cortico-sp"};
			}






	 public void init()
	 {
		 //call the exhibit init routine
		 super.init();

		 // load in the picture
		 picture_base = getImage("resources/imagemaps/spinaltracts.gif");
		 
		 // set info text
		 questionTextSelectName = "Select the name of the highlighted tract";
	   questionTextSelectArea = "Select the named spinal tract";
	 }// end init

}



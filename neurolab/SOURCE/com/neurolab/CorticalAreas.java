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

import java.awt.Dimension;

public class CorticalAreas extends RobinExhibit{
  
  // setup some variables
  
  // Define components
  public String getExhibitName(){
    return "Cortical areas";
  }
  public int getNumComponents(){
    return 18;
  }
  public Dimension getImageDimension(){
    return new Dimension(321, 226);
  }
  
  {
    brainStrings = new String[]{"Choose", 
        "<HTML> Areas 9-11,46: <BR>prefrontal cortex",
      "Area 8: frontal eye fields", 
      "Area 6: premotor cortex",
      "Area 4: primary motor cortex", 
      "<HTML>Area 3: primary somatosensory<BR> cortex",
      "<HTML>Area 1: primary  <BR>somatosensory cortex",
      "<HTML>Area 2: secondary <BR>somatosensory cortex",
      "Area 5: anterior parietal cortex",
      "<HTML>Area 7: posterior parietal <BR>cortex",
      "Area 17: primary visual cortex",
      "<HTML>Area 18: parastriate <BR> visual areas V2-V4",
      "<HTML>Area 19: medial <BR> temporal visual area V5",
      "<HTML>Areas 39,40: inferior <BR> parietal part of Wernicke",
      "Area 21: inferotemporal cortex", 
      "<HTML>Area 42: secondary auditory<BR> cortex",
      "<HTML>Area 41: primary auditory <BR>cortex",
      "<HTML>Area 22: superior <BR>temporal part of Wernicke",
      "<HTML>Areas 44,45: <BR>Broca's area (approximately)"};
    
    textStrings = new String[]{
      "Prefrontal cortex is the region of the brain that evaluates options, makes decisions, and acts as an 'executive'",
      "The frontal eye fields control saccadic eye movements",
      "Premotor cortex is involved in developing plans for movement", 
      "Primary motor cortex generates movement commands that are sent to the spinal cord",
      "Primary somatosensory cortex receives inputs from the thalamus regarding tactile sensation", 
      "Primary somatosensory cortex receives inputs from the thalamus regarding tactile sensation",
      "Secondary somatosensory cortex processes information from the sense of touch",
      "Anterior parietal cortex processes higher level touch and is responsible for the sense of space",
      "Posterior parietal cortex integrates visual and tactile information to give a 3-dimensional representation of the world",
      "Primary visual cortex receives visual information from the lateral geniculate nucleus, and extracts lines, depth and movement",
      "Parastriate visual areas perform higher level visual processing, including colour, texture and movement processing",
      "Visual areas MT are involved in higher level motion perception, including 3D motion",
      "Inferior parietal regions are involved in visual and linguistic processing, including word recognition", 
      "Inferiotemporal cortex may be responsible for object recognition and face recognition", 
      "Secondary auditory cortex is involved in higher-level acoustic perception and recognition", 
      "Primary auditory cortex receives auditory information from the medial geniculate nucleus and analyses basic acoustic properties",
      "Superior temporal gyrus is involved in speech recognition and language.", 
      "Brocas area is necessary for generation of language, e.g. speech production", 
      "18",
      "Click on an area to find out about it! ", "shouldn't see this!!"};
  }
  
  public void init(){
    
    // call the exhibit init routine
    super.init();
    
    // load in the picture
    
    picture_base = getImage("resources/imagemaps/corticalareas.gif");
    

  }// end init
}
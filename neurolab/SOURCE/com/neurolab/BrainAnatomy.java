// Neurolab Brain anatomy
// Robin Marlow
// 14/07/00

/*
 Yet Todo
 Not sure all the coloured in areas are correct! Two images don't agree!!
 cache the images during loading rather than after first use
 make sure all the greys are constant - done ods20
 maybe make feedback system more elegant? & give more enthusiastic responses!!
 possibly make it more colourful?
 remove all the old crud from the code.

 quirks
 insensitivity problem... due to course images?
 strange key handling during quiz mode b.cos as move cursor with keys, it selects each time.... thus 'supplying' the answer.
 irregular problem selecting association fibres without repaint of picture

 points that need manually adjusting for change of objects
 the switch function in Textpanel
 the colours areas are changed to in RedBlueSwapFilter
 the 32.0 in questionhandler (as it needs to be .0 & not an int)
 the text in the score box in questionhandler
 where it looks for the picture_base & size of it when it draws
 & last & not least LAYOUT!


 colour maps

 r  corresponding label
 255 Medulla				Association Fibres
 254 Pons				Lateral Ventrical
 253 Pituitary				Corpus Striatum
 252 Hypothalamus			Optic Tract
 251 Cerebellum				Infundibulum
 250 Colliculi				Hypothalamus
 249 Corpus callosum			Third Ventricle
 248 Cerebral cortex			Thalamus
 247 Thalamus				Fornix
 246 Pineal				Caudate Nucleus
 245 Fornix [black]			Amygdala
 244 Aqueduct [black]			Corpus Callosum  [black]
 243 Fourth ventrical [black]		Internal Capsule [black]
 242 Anterior commissure [black]

 */

package com.neurolab;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class BrainAnatomy extends RobinExhibit{
  
  // setup some variables
  
  // Define components
  public String getExhibitName(){
    return "Brain anatomy";
  }
  public int getNumComponents(){
    return transverse==0?14: // sagittal
      13; //coronal
  }
  public Dimension getImageDimension(){
    return new Dimension(321, 226);
  }
  

  JButton swapsection;
  public void init(){
    
    // call the exhibit init routine
    super.init();
    
    // load in the picture
    
    picture_base = getImage("resources/imagemaps/TRANSVERSE.GIF");
    swapsection = new JButton("Change View");
    swapsection.addActionListener(handler);
    mainGbConstraints.fill = GridBagConstraints.NONE;
    addMainComponent(swapsection, 5, 3, 1, 1, 0, 0);

  }// end init
  
  int transverse = 1;
  ActionListener handler=new ActionListener() {public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand() == "Change View" && !testmode){
      ignore = true;
      // System.out.println("change to transverse section");
      // swapsection.setText("Sagital Section");
      if(transverse == 0) { 
        transverse = 1;
        brainStrings = coronalStrings;
        textStrings  = coronalDescriptions;
        picture_base = getImage("resources/imagemaps/TRANSVERSE.GIF");
      }else {
        transverse = 0;
        brainStrings = sagittalStrings;
        textStrings  = sagittalDescriptions;        
        picture_base = getImage("resources/imagemaps/SAGITTAL.GIF");
      }

      whichbit.removeAllItems();
      for(int i = 0; i < brainStrings.length; i++)
        whichbit.addItem(brainStrings[i]);
      no_components = getNumComponents();
      index = no_components;
      textbox.n = index;
      
      if(whichbit.getItemAt(0) == brainStrings[1]){
        whichbit.insertItemAt("Choose", 0);
        whichbit.setSelectedIndex(0);
      }
      
      image_exists = new boolean[no_components+1];
      picture = new Image[no_components+1]; 
      question = new int[no_components];
                                   
      ignore = false;
      picturebox.repaint();
      textbox.update();
    }
  }  };

  String[] sagittalStrings =       {"Choose", "Medulla", "Pons", "Pituitary", "Hypothalamus", "Cerebellum",
      "Colliculi", "Corpus callosum", "Cerebral cortex", "Thalamus", "Pineal",
      "Fornix", "Aqueduct", "Fourth ventricle", "Anterior commissure"}; // sagittal N=14
  
  String[] sagittalDescriptions = {"The medulla is the lowest part of the brainstem", 
      "The pons is the middle part of the brain stem, where the cerebellum attaches", 
      "The anterior pituitary secretes the hormones prolactin, TSH, FSH, LH, ACTH and GH. Posterior pituitary secretes ADH and oxcytocin.",
      "The hypothalamus is the control centre for hormones, visceral function and motivation", 
      "The cerebellum coordinates movements", 
      "The superior colliculi are responsible for visual orienting; The inferior colliculi form part of the auditory pathway.",
      "The corpus callosum is the main white matter tract connecting the left and right cerebral hemispheres.",
      "the cerebral cortex is a convoluted sheet of grey matter that forms the two hemispheres of the cerebrum.",
      "The thalamus is the primary input nucleus serving the cerebral cortex.", 
      "The pineal gland secretes melatonin.", 
      "The fornix is a limbic pathway between the hippocampus and forebrain nuclei.", 
      "The aqueduct of Sylvius (cerebral aqueduct) carries CSF down out of lateral and third ventricles.", 
      "The fourth ventricle is in the lower pons, just anterior to the cerebellum.",
      "The anterior commisure is a white matter tract that connects the left and right temporal lobes and olfactory tracts.",
      "Click on an area to find out about it! ", "shouldn't see this!!"
      };
  
  String[] coronalStrings =       {"Choose", "Association Fibres", "Lateral Ventricle", "Corpus Striatum",
      "Optic Tract", "Infundibulum", "Hypothalamus", "Third Ventricle",
      "Thalamus", "Fornix", "Caudate Nucleus", "Amygdala", "Corpus Callosum",
      "Internal Capsule"}                    ; // transverse N=13
 
  String[] coronalDescriptions = {"Association fibres in the subcortical white matter connect different regions of the cerebral cortex.",
      "The lateral ventricles are the main location where cerebrospinal fluid is synthesised.",
      "The corpus striatum is a region of the basal ganglia involved in control of behaviour.", 
      "The optic tract conveys information from the eyes to the visual regions of the thalamus.",
      "The infundibulum is the stalk of the pituitary gland.", 
      "The hypothalamus is the control centre for hormones, visceral function and motivation.",
      "The third ventricle is a narrow CSF space between the two halves of the thalamus.", 
      "The thalamus is the major input nucleus of the cerebral cortex", 
      "The fornix is a limbic pathway between the hippocampus and forebrain nuclei.", 
      "The caudate nucleus is the superior segment of the basal ganglia, running in the wall of the lateral ventricle.", 
      "The amygdala is a nucleus deep in the anterior temporal lobe that is responsive to fearful stimuli.", 
      "The corpus callosum is the main white matter tract connecting the left and right cerebral hemispheres.", 
      "The internal capsule is the white matter pathway descending from the cerebral cortex to the brainstem and spinal cord.",
      "Click on an area to find out about it! ", "shouldn't see this!!"
    };
  
  
  {
    brainStrings     = coronalStrings;
    textStrings      = coronalDescriptions;
  }

} // end hypothalamus.class


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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import com.neurolab.common.NeurolabExhibit;
import com.neurolab.common.Spacer;

public class BrainAnatomyOld extends NeurolabExhibit{
  
  // setup some variables
  
  // Define components
  public String getExhibitName(){
    return "Brain anatomy";
  }
  
  public int                 transverse       = 0;                        // pseudo-boolean
                                                                          // 0 = sagital/ plane,
                                                                          // 1 = transverse plane
                                                                           
  public int                 no_components[]  = {14, 13};
  public int                 no_whiteparts[]  = {10, 11};
  public double              float_number[]   = {28.0, 26.0};
  
  public JPanel              picturebox;
  public PicturePanel        picgraphic;
  public TextPanel           textbox;
  public JComboBox           whichbit;
  
  public Spacer              spacer1, spacer2, spacer3, spacer4;
  private GridBagLayout      mainLayout, chkbxLayout;
  private GridBagConstraints mainGbConstraints, chkbxGbConstraints;
  
  private JButton            returnbutton, swapsection;
  private JCheckBox          testme;
  
  private Container          maincontainer;
  
  Image                      picture_base[]   = new Image[2];
  Image                      picture[][]      = new Image[2][];
  boolean                    image_exists[][] = new boolean[2][];
  public int                 question[][]     = new int[2][];
  
  public int                 index;
  public int                 questionumber;
  
  public int                 correct;
  public boolean             noarea           = false;
  public boolean             ignore           = false;
  public boolean             testmode, nocombo = false;
  
  Color                      systemGray;
  
  String[][]                 brainStrings     = {
      {"Choose", "Medulla", "Pons", "Pituitary", "Hypothalamus", "Cerebellum",
      "Colliculi", "Corpus callosum", "Cerebral cortex", "Thalamus", "Pineal",
      "Fornix", "Aqueduct", "Fourth ventricle", "Anterior commissure"},
      {"Choose", "Association Fibres", "Lateral Ventricle", "Corpus Striatum",
      "Optic Tract", "Infundibulum", "Hypothalamus", "Third Ventricle",
      "Thalamus", "Fornix", "Caudate Nucleus", "Amygdala", "Corpus Callosum",
      "Internal Capsule"}                     };
  
  String[][]                 textStrings      = {
      { "The medulla is the lowest part of the brainstem", 
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
        "Click on an area to find out about it! ", "shouldn't see this!!"},
        
      {"Association fibres in the subcortical white matter connect different regions of the cerebral cortex.",
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
      "Click on an area to find out about it! ", "shouldn't see this!!"}};
  
  public void init(){
    

    for(int i = 0; i < picture.length; i++) // create the 2nd dimension of the
                                            // matrices
    {
      picture[i] = new Image[no_components[i] + 1];
      image_exists[i] = new boolean[no_components[i] + 1];
      question[i] = new int[no_components[i] * 2];
    }
    
    // call the exhibit init routine
    super.init();
    
    // load in the base pictures
    picture_base[0] = getImage("resources/imagemaps/SAGITTAL.GIF");
    picture_base[1] = getImage("resources/imagemaps/TRANSVERSE.GIF");
    
    index = no_components[transverse]; // set as end (17th == 16 including
                                        // number 0)
    testmode = false;
    nocombo = false;
    noarea = false;
    
    // make flexible look & feel

    // ods20 - this line of code was mucking up the colors, not improving them!
    // systemGray =SystemColor.control;// new Color (191,191,191);
    
    createComponents();
    
  }// end init
  

  public void update(Graphics g){
    paint(g);
  }
  
  /*
   * public void paint (Graphics g) { picturebox.repaint(); textbox.repaint(); } //
   * end paint
   */

  public void createComponents(){
    
    // make the Handler
    EventHandler handler = new EventHandler();
    
    // setup borders
    Border etched, raisedbevel;
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
    
    // addchkboxComponent( new Spacer(10,10), 0, 0, 1, 1); //at top left
    // addchkboxComponent( new Spacer(10,10), 2, 2, 1, 1); // bottom right
    
    // make & add the panel for the actual graphic
    picgraphic = new PicturePanel();
    picgraphic.setBackground(systemGray);
    chkbxGbConstraints.fill = GridBagConstraints.BOTH;
    chkbxGbConstraints.weightx = 80;
    chkbxGbConstraints.weighty = 100;
    // addchkboxComponent( picgraphic, 1, 1, 1, 1);
    addchkboxComponent(picgraphic, 0, 0, 1, 1);
    
    // Make ComboBox
    whichbit = new JComboBox(brainStrings[0]);
    // whichbit.setBackground(systemGray); looks better as white
    whichbit.setMaximumRowCount(5);
    whichbit.setLightWeightPopupEnabled(false); // the key thing!!!!
    whichbit.addItemListener(handler);
    
    // Make textbox
    textbox = new TextPanel();
    textbox.setBorder(etched);
    textbox.setBackground(systemGray);
    textbox.setPreferredSize(new Dimension(150,150));
    textbox.n = no_components[transverse]; // set as end (17th == 16 including
                                            // number 0) thus initially in
                                            // 'choose an area'
    
    // Make testme
    testme = new JCheckBox("Test Me");
    testme.addItemListener(handler);
    testme.setBackground(systemGray);
    
    // make exit button
    returnbutton = new JButton("Return");
    returnbutton.setBackground(systemGray);
    returnbutton.addActionListener(handler);
    
    // make swapsection button
    swapsection = new JButton("Change View");
    swapsection.setBackground(systemGray);
    swapsection.addActionListener(handler);
    

    // add components to maincontainer
    
    // setup container
    maincontainer = getMainContainer();
    // getContentPane().setBackground (systemGray);
    mainLayout = new GridBagLayout();
    maincontainer.setLayout(mainLayout);
    mainGbConstraints = new GridBagConstraints();
    
    // add the combobox
    mainGbConstraints.fill = GridBagConstraints.NONE;
    addMainComponent(whichbit, 2, 2, 5, 1, 0, 20);
    
    // add the textbox
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    // textbox.setSize(100,200);
    addMainComponent(textbox, 4, 3, 3, 1, 0, 20);
    
    // add the picturebox
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    // addMainComponent( picturebox, 1, 1, 1, 4, 70, 100);
    addMainComponent(picturebox, 2, 0, 2, 4, 80, 80);
    
    // add the returnbutton
    mainGbConstraints.fill = GridBagConstraints.NONE;
    addMainComponent(returnbutton, 7, 5, 1, 1, 0, 0);
    
    // add the testme
    mainGbConstraints.fill = GridBagConstraints.NONE;
    addMainComponent(testme, 6, 3, 1, 1, 0, 0);
    
    // add the swapsection button
    mainGbConstraints.fill = GridBagConstraints.NONE;
    addMainComponent(swapsection, 7, 3, 2, 1, 0, 0);
    
    // add spacers
    // top above picture
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    addMainComponent(new Spacer(10, 30), 0, 0, 1, 1, 1, 0);
    
    // between right side of picture & text box
    // controlling height of textbox
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    addMainComponent(new Spacer(30, 50), 4, 2, 1, 1, 0, 80);
    
    // between combo & textbox
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    addMainComponent(new Spacer(40, 30), 3, 4, 1, 1, 0, 0);
    
    // right of return button
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    addMainComponent(new Spacer(10, 0), 6, 6, 1, 1, 0, 0);
    
    // between text & buttons
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    addMainComponent(new Spacer(40, 50), 5, 4, 1, 1, 0, 0);
    
    // far right level of text box adding a bigger margin
    mainGbConstraints.fill = GridBagConstraints.BOTH;
    // addMainComponent( new Spacer (40,30), 7, 4, 1, 1, 0, 0);
    
  } // end createcomponents()
  

  public String getAppletInfo(){
    return "Brain Anatomy coded in Java by Robin Marlow \n based on an original program by Dr. Carpenter";
  }
  

  // ******************************************
  // addMainComponent for gridbag stuff
  // ******************************************
  private void addMainComponent(Component c, int row, int column, int width,
      int height, int weightx, int weighty){
    // set gridx and gridy
    mainGbConstraints.gridx = column;
    mainGbConstraints.gridy = row;
    
    // set gridwidth and gridheight
    mainGbConstraints.gridwidth = width;
    mainGbConstraints.gridheight = height;
    
    mainGbConstraints.weightx = weightx;
    mainGbConstraints.weighty = weighty;
    
    // set constraints
    mainLayout.setConstraints(c, mainGbConstraints);
    maincontainer.add(c); // add component
  }
  
  // ******************************************
  // addchkboxComponent for gridbag stuff
  // ******************************************
  
  private void addchkboxComponent(Component c, int row, int column, int width,
      int height){
    // set gridx and gridy
    chkbxGbConstraints.gridx = column;
    chkbxGbConstraints.gridy = row;
    
    // set gridwidth and gridheight
    chkbxGbConstraints.gridwidth = width;
    chkbxGbConstraints.gridheight = height;
    
    // set constraints
    chkbxLayout.setConstraints(c, chkbxGbConstraints);
    picturebox.add(c); // add component
  }
  

  // ******************
  // Picturepanel
  // ******************
  
  public class PicturePanel extends JPanel{
    
    public PicturePanel(){
      addMouseListener(new MouseClickHandler());
      addMouseMotionListener(new MouseMotionHandler());
    }
    
    public void paint(Graphics g){
      super.paint(g);
      
      if(image_exists[transverse][index] == true) g.drawImage(
          picture[transverse][index], 0, 0, 321, 226, this);
      
      else{
        picture[transverse][index] = createImage(new FilteredImageSource(
            picture_base[transverse].getSource(), new RedBlueSwapFilter()));
        image_exists[transverse][index] = true;
        // fudge fix:
        try{
          Thread.sleep(100);
        }catch(Exception e){}
        g.drawImage(picture[transverse][index], 0, 0, 321, 226, this);
      }
      

    }
    
  }// end PicturePanel class
  


  // ******************
  // TextPanel
  // ******************
  class TextPanel extends JTextPane{
    
    public int n;
    
    public void init(){
      n = 0;
    };
    

    public void update(){
      
      // unless! we want to ask a question & then we want to ask a
      // question instead!
      
      String as;
      setForeground(Color.black);
      
      if(testmode == true){
        if(question[transverse][questionumber] <= (no_components[transverse] - 1)){
          as = "Select the name of the highlighted region";
        }else if(question[transverse][questionumber] >= (no_components[transverse])){
          as = "Select the area of brain named";
        }else as = "something wrong with the text matching!";
      }else as = textStrings[transverse][n];
      setText(as);
    }
    
  }// end TextPanel class
  

  // *******************
  // Eventhandler
  // *******************
  
  private class EventHandler implements ActionListener, ItemListener{
    
    public void actionPerformed(ActionEvent e){
      
      if(e.getActionCommand() == "Return"){
        toExhibitChooser();
      }
      
      if(e.getActionCommand() == "Change View" && !testmode){
        ignore = true;
        // System.out.println("change to transverse section");
        // swapsection.setText("Sagital Section");
        if(transverse == 0) transverse = 1;
        else transverse = 0;
        // transverse = 1;
        whichbit.removeAllItems();
        for(int i = 0; i < brainStrings[transverse].length; i++)
          whichbit.addItem(brainStrings[transverse][i]);
        index = no_components[transverse];
        textbox.n = index;
        
        if(whichbit.getItemAt(0) == brainStrings[transverse][1]){
          whichbit.insertItemAt("Choose", 0);
          whichbit.setSelectedIndex(0);
        }
        
        ignore = false;
        picturebox.repaint();
        textbox.update();
      }
      /*
       * if(e.getActionCommand()=="Sagital Section") { ignore = true;
       * System.out.println("change to sagital section");
       * swapsection.setText("Transverse Section"); transverse = 0;
       * whichbit.removeAllItems(); for (int i=0; i<brainStrings[transverse].length;i++)
       * whichbit.addItem(brainStrings[transverse][i]); index =
       * no_components[transverse]; textbox.n = index;
       * 
       * if (whichbit.getItemAt(0) == brainStrings[transverse][1]) {
       * whichbit.insertItemAt("Choose",0); whichbit.setSelectedIndex(0); }
       * ignore = false; }
       */
    } // end actionlistener
    
    public void itemStateChanged(ItemEvent e) // eventhandler for the checkbox &
                                              // combobox
    {
      
      if(whichbit.getItemAt(0) == "Choose" && ignore != true){
        ignore = true;
        whichbit.removeItemAt(0);
        // whichbit.insertItemAt("brainStrings[transverse][1]",0);
        ignore = false;
        
      }
      

      if(e.getSource() == testme){
        
        if(e.getStateChange() == ItemEvent.SELECTED){
          testmode = true;
          QuestionHandler();
        }
        
        if(e.getStateChange() == ItemEvent.DESELECTED){
          ignore = true;
          testmode = false;
          noarea = false;
          nocombo = false;
          whichbit.setSelectedIndex(0);
          whichbit.setEnabled(true);
          
          if(whichbit.getItemAt(0) == brainStrings[transverse][1]){
            whichbit.insertItemAt("Choose", 0);
            whichbit.setSelectedIndex(0);
          }
          

          ignore = false;
          textbox.n = no_components[transverse];
          index = no_components[transverse];
          
          picturebox.repaint();
          textbox.update();
        }
        

      }
      

      if(e.getSource() == whichbit && !ignore && !testmode
          && e.getStateChange() == ItemEvent.SELECTED){
        // this fires once per click
        // & when program chnages the box & then get repainted
        // if (e.getStateChange() == ItemEvent.SELECTED){
        
        textbox.n = whichbit.getSelectedIndex();
        index = whichbit.getSelectedIndex();
        
        picturebox.repaint();
        textbox.update();
        
        // repaint();
        // }
      }
      

      if(e.getSource() == whichbit && !ignore && testmode && !nocombo
          && e.getStateChange() == ItemEvent.SELECTED){
        // this fires once per click
        // & when program changes the box & then get repainted
        // if (e.getStateChange() == ItemEvent.SELECTED){
        // buttonchoice[whichbit.getSelectedIndex()].setSelected(true);
        // textbox.n = whichbit.getSelectedIndex();
        // index = whichbit.getSelectedIndex();
        
        if(whichbit.getSelectedIndex() == question[transverse][questionumber]){
          whichbit.hidePopup();
          nextQuestion(true);
          
        }

        else{
          ignore = true;
          whichbit.hidePopup();
          whichbit.setSelectedIndex(question[transverse][questionumber]);
          ignore = false;
          nextQuestion(false);
        }
        
        setupQuestion();
        
      }
      
    } // ends itemstatelistener
    
  } // end handler class
  


  // ***********************************
  // CLASS defining the colour filter
  // ***********************************
  

  class RedBlueSwapFilter extends RGBImageFilter{
    
    public RedBlueSwapFilter(){
      canFilterIndexColorModel = true;
    }
    
    public int filterRGB(int x, int y, int rgb){
      // return ((255 << 24) | ((rgb & 0xff0000) << 16) | ((rgb & 0xff00) << 8 )
      // | (rgb & 0xff) );
      // return (( rgb & 0 << 16 | rgb & 0 << 8 ) | ((255) ));
      // return ((255 << 24) | (0 << 16) | (0 << 8 ) | 0 );
      
      for(int n = 0; n < no_whiteparts[transverse]; n++) // colour these in
                                                          // white
      {
        if(rgb == (0xffff0000 - (n * 0x10000))){
          if(n == index) return 0xFF1D52FF; // colour it blue
          else return 0xffffffff; // or white
        }
        
      }
      

      for(int n = no_whiteparts[transverse]; n < no_components[transverse]; n++) // colour
                                                                                  // these
                                                                                  // in
                                                                                  // black
      {
        if(rgb == (0xffff0000 - (n * 0x10000))){
          if(n == index) return 0xFF1D52FF;
          else return 0xff000000;
        }
        
      }
      
      return ((255 << 24) | ((rgb & 0xff0000) << 0) | ((rgb & 0xff00) << 0) | (rgb & 0xff));
      // }
    }
    
  }
  
  // *************************************
  // inner class to handle mouse events
  // *************************************
  
  private class MouseClickHandler extends MouseAdapter{
    
    private boolean onceover  = false;
    boolean         mousedown = false;
    public void mousePressed(MouseEvent e){
      mousedown = true;
    }
    public void mouseReleased(MouseEvent e){
      if(mousedown){
        mouseClicked(e);
        mousedown = false;
      }
    }
    public void mouseClicked(MouseEvent e){
      
      if(noarea == false){
        
        // System.out.println(" Mouse-Click event!");
        
        if(whichbit.getItemAt(0) == "Choose"){
          ignore = true;
          whichbit.removeItemAt(0);
          ignore = false;
          onceover = true;
        }else onceover = false;
        int pixel;
        try {
          pixel = getPixelValueAt(e.getPoint());
        }catch(InterruptedException x) {return;}
        
        for(int n = 0; n < no_components[transverse]; n++){
          if(pixel == (0xffff0000 - (n * 0x10000))){
            
            if(whichbit.getSelectedIndex() != n || onceover == true) 
            {// if not clicking on area that has already been selected
              if(testmode == true){
                index = (question[transverse][questionumber] - no_components[transverse]);
                picturebox.repaint();
                nextQuestion(false);
              }// end of if testmode = true
              
              else if(testmode != true){
                
                if(textbox.n == no_components[transverse]){
                  textbox.n = 0;
                  textbox.repaint();
                }
                index = n;
                whichbit.setSelectedIndex(n); // make it selected!
                picturebox.repaint();
              }
              
              break;
            }

            else if(whichbit.getSelectedIndex() == n && testmode == true){
              System.out.println("clicking on somewhere already highlighted!");
              index = (question[transverse][questionumber] - no_components[transverse]);
              picturebox.repaint();
              // whichbit.setSelectedIndex(n);
              nextQuestion(true);
              // repaint();
              // break;
            }
            
            break;
          }
          

        }// ends for loop
        
      } // ends noarea
      
    }
    
  } // end mouseclickhandler
  class MouseMotionHandler extends MouseMotionAdapter{
    Cursor normal=Cursor.getDefaultCursor(), hand=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public void mouseMoved(MouseEvent e) {
      try{
        int px=getPixelValueAt(e.getPoint());
        if(px!=0 && px!=-1) {
          setCursor(hand);
        }else setCursor(normal);
        
      }  catch(InterruptedException x)  {}
    }
  }


  // **************************
  // Question Handler
  // **************************
  
  private void QuestionHandler(){
    
    questionumber = 0;
    correct = 0;
    
    int n, m;
    double temp;
    boolean unique;
    
    for(n = 0; n < (no_components[transverse] * 2); n++){
      do{
        unique = true;
        temp = (float_number[transverse] * Math.random());
        question[transverse][n] = (int)temp;
        for(m = 0; m < n; m++)
          // check below n
          if(question[transverse][m] == question[transverse][n]) unique = false;
        for(m = (n + 1); m < (no_components[transverse] * 2); m++)
          // check above n (not really needed as haven't yet defined them!!)
          if(question[transverse][m] == question[transverse][n]) unique = false;
      }while(!unique);
    }
    
    // for (n=0;n<32;n=n+4) System.out.println(question[transverse][n] + "," + question[transverse][n+1]+ "," + question[n+2]+ "," + question[n+3]+",");
    

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
    

    if(answer == true){
      JOptionPane.showMessageDialog(null, "Correct!", "You got it...",
          JOptionPane.INFORMATION_MESSAGE);
      correct++;
    }

    else{
      JOptionPane.showMessageDialog(null,
          "Wrong! - The correct answer is shown", "You got it...",
          JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*
     if (answer == true) System.out.println("CORRECT!");
     else System.out.println("WRONG!");
     */

    if(questionumber < ((no_components[transverse] * 2) - 1)){
      questionumber++;
      System.out.println("question number = " + questionumber + " "
          + question[transverse][questionumber]);
      setupQuestion();
    }

    else{
      
      ignore = true;
      testme.doClick();
      testmode = false;
      ignore = false;
      
      JOptionPane.showMessageDialog(null, correct + " out of "
          + (no_components[transverse] * 2), "You scored",
          JOptionPane.INFORMATION_MESSAGE);
      
    }
    
  } // ends NextQuestion
  

  //*****************
  // Set up question
  //*****************
  
  private void setupQuestion(){
    

    if(!testmode) testmode = false;
    
    else if(question[transverse][questionumber] > (no_components[transverse] - 1)){
      ignore = true;
      noarea = false;
      nocombo = true;
      index = no_components[transverse]; // de-highlights all the picture
      
      whichbit.setEnabled(false); // prevents responses from combo box
      whichbit.setSelectedIndex(question[transverse][questionumber]
          - no_components[transverse]); // sets question up in combobox
      
      picturebox.repaint();
      textbox.update();
      
      ignore = false;
      // need to put up correct text in textbox
      
      // can't click on correct area cos already 'highlighted' i.e. selected in combobox
    }

    else{
      
      ignore = true;
      noarea = true; //prevents clicking in the area window
      nocombo = false; //allows event handling by the combo box
      whichbit.setEnabled(true); //allows selection in the combo box
      
      // need to set combobox to 'choose'
      whichbit.setSelectedIndex(0);
      
      if(whichbit.getItemAt(0) == brainStrings[transverse][1]){
        //whichbit.removeItemAt(0);
        whichbit.insertItemAt("Choose", 0);
        whichbit.setSelectedIndex(0);
      }
      
      index = question[transverse][questionumber]; // sets up correct highlighting of area
      
      picturebox.repaint();
      textbox.update();
      
      ignore = false;
      //need to put correct text in textbox
      
    }
  }// ends setup question
  
  public void close(){
  }


  protected int getPixelValueAt(Point e) throws InterruptedException{
    int[] pixels = new int[1];
    PixelGrabber pg = new PixelGrabber(picture_base[transverse], e.x, e.y, 1, 1, pixels, 0, 2);
    pg.grabPixels();
    return pixels[0];
  }
} // end hypothalamus.class


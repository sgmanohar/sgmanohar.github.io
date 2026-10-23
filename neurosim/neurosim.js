/* eslint-disable no-unused-vars */
/*  global $ , THREE, Hammer, bootbox, jQuery, webkitSpeechRecognition, NeuroMan, vis, svgPanZoom */

/****************************************************************
 *  neurosim.js
 *
 *  Neurosim contains three modules:
 * 
 *    NeuroSim: main application module that manages regions, lesions, 
 *      connections, and json data files.
 *
 *    NeuroImage: module that wraps the neurosim-image divs. It loads
 *      and displays svg images, and shows regions and lesions.
 * 
 *    NeuroExam: module that wraps the neuroexam divs. It shows 
 *      the neurological examination findings.
 * 
 ****************************************************************/



/********
 * Main neurosim module
 * contains the connection data.
 * There should be only one instance per page.
 * 
 * Current Dependencies:  
 *   jQuery
 *   jQueryUI
 *   jQuery.svg   - all svg manipulation
 *   svg-pan-zoom 
 *   Bootstrap    - button bar and drop down menus
 *   bootbox      - about this region / slice etc. in modal dialogs
 *   font-awesome - button bar icons, loading spinner
 *   hammer.js    - for touch-screen interface
 * 
 *   neurosim.css - hiding / showing / colouring regions and UI elements
 *   three_animate.js        - my script to draw body, which in turn depends on
 *   three and three/controls/TrackballControl
 *   neurosim-background-calculations -  computation thread
 *
 * Current input files: 
 *   SliceMetadata.json    - contains modality list, regions in slice, and fiducials
 *   Neurology.json        - list of connections, pathways, and hierarchy of containment
 *   Visibility.json       - level of visibility for each region
 *   cases.json            - information on patients and diseases 
 *   case_snippets.html    - HTML snippets to build into examination panels
 * 
 * Examination and UI SVGs:
 *   neurosim-dermatomes, neurosim-face-examination, neurosim-motor-all, neurosim-motor-diagram
 *   neurosim-motor-lower-limb, neurosim-motor-upper-limb, neurosim-reflexes, neurosim-visual-fields
 *   modality-icons, neurosim-eye-selector, axis-icons
 * 
 */

if(NeuroSim===undefined){ // check if not already loaded
  
  
var NeuroSim = function(){
  var my={}; 
  
  /** 
   * location of server for posting and getting scores (json) .
   * If empty, do not send scores.
   */
  my.score_server = ""; // "http://smanohar.com/neurosim/basic/test.php";
  
  /** location of server for posting feedback */
  my.feedback_server = "http://homphysiology.org/cgi-bin/NeuroSimComments.py";
  /**
   * Location for sending inputted text for logging - so we can improve the answers!
   */
  my.logging_server = "http://www.homphysiology.org/cgi-bin/NeuroSimComments.py?command=inputlog&text=";

  
  /** 
   * Scoring method. if true, then button-clicks add to the score 
   * irrespective of whether they are correct 
   */
  my.SCORE_ALLCLICKS = false;
  /**
   * Should any string be added to all history responses?
   */
  my.APPEND_TO_ANSWER = ""; // previously  ", doctor."
  /**
   * False means a patient will answer consistently the same to a given 
   * question (although it may be random each time a case is run).
   * If true, the same question may elicit different answers.
   */
  my.USE_RANDOM_SEED = false;

  /**
   * Include the descriptions of items in an autocomplete list?
   */
  my.AUTOCOMPLETE_INCLUDE_DESCRIPTION = false;

  my.lesions    = {}; // keep track of what is lesioned
  my.html       = {}; // store created html tags and prototypes
  my.html.exams = []; // keep a list of exams displayed
  my.scores     = {}; // index of scores for all cases completed
  my.casename   = "none";
  
  /**
   *  The pager (my.pager) is an interface for setting up the series of elements
   * that can be stepped through, in a case. The pager object knows how to:
   * setup: set up a page, given a <UL> of pages, 
   * inserted: what to do when a page <LI> is inserted into the list to update
   *           the interface, and
   * goto: how to go to a particular page. 
   * 
   * A carousel pager using slick, and and an accordion pager, are provided.
   */
  my.PAGERS = {
    CAROUSEL: { // carousel using slick plugin for jquery
      slickparams : {
          infinite:false,
          dots:true,
          waitForAnimate:false
          //, adaptiveHeight:true  
      },
      setup: function(){
        my.root.find(".exam-section").slick( my.pager.slickparams ); 
      },
      inserted: function(item){
        $(item).closest(".exam-section").slick('unslick');
        setTimeout(function(){
          $(item).closest(".exam-section").slick( my.pager.slickparams ) ;
          my.pager.goto(item);
        },0);
      },
      goto: function(item){
        $(item).closest(".exam-section").slick('slickGoTo',$(item).index() );
      }
    },
    ACCORDION: {  // jQuery accordion
      setup: function(){
        my.root.find(".exam-section").accordion({
              //collapsible: true,
              active:      false,
              icons:       {      header:       "ui-icon-circle-arrow-e",
                                  activeHeader: "ui-icon-circle-arrow-s"},
              heightStyle: "content", // 'short' accordion
              collapsible:true        // for 'long' accordion
              //, heightStyle:"fill"   // for 'long' accordion
            });
        },
      inserted: function(item){
        $(item).closest(".exam-section").accordion("refresh");
        setTimeout(function(){my.pager.goto(item);},0);
      },
      goto: function(item){
        $(item).closest(".exam-section").accordion("option", "active",$(item).index());
      }
    },
    "FLAT":{
      setup: function(){
        my.root.find(".exam-section").addClass("flat");
      },
      inserted: function(item){
        my.pager.goto(item);
      },
      goto: function(item) {
        $('html, body').animate({
          scrollTop: $(item).offset().top
        }, 500);
      }
    }
  };
  
  my.pager = my.PAGERS.FLAT;

  /******************* INITIALISATION ***********************
   * Load order:
   *  1. Begin loading json, html resources  in initialise()
   *  2. Create DOM elements in the mean time
   *  3. When resources loaded, add elements that require resources (initaliseGui), 
   *  4. Trigger any "load" event listeners registered (e.g SVG loading for NeuroImages)
   *  5. When all NeuroImage objects are loaded, start webworker, send updateModel() event
   *  6. Trigger any "imagesLoaded" event listeners (e.g. examination hooks
   *     like visual-fields which need to add event handlers to specific image-regions)  
   */

  /***********************************************************
   * NeuroSim.initialise()
   *  Read the NeuroSim resources from json files.
   *  In parallel, set up the menu and lists in the DOM.
   *  Then when all resources are asynchronously loaded, 
   *  call initialiseGUI.
   * 
   *  Main entry point - called from the document.ready() as soon
   *  as the page has loaded.
   *  Optionally supply the document root to work from 
   *  (useful if we're in an iframe)
   *************************************************************/
  my.initialise = function(root){
    if(root!==undefined){
      my.root=$(root);
    } else {
      my.root = $("body"); // was previously $("*");
    }
    $.when(
      my.load_resource_neurology_json(),
      my.load_resource_slice_metadata_json(),
      my.load_resource_neurosim_history_json(),
      my.load_resource_visibility_json(),
      my.load_resource_case_snippets_html(),
      my.load_resource_cases_json()
    ).done(
      my.initialiseGUI
    );
    // the following commands are called asynchronously, perhaps before
    // the resources have started to load.
    /** initialise the model */
    my.clearLesions();
    
    /** 
     * set up accordions for exam sections 
     */
    my.pager.setup();

    /** Start with no quiz */
    my.current_quiz = undefined; 
    /** Read scores from server */
    if(my.score_server){
      $.getJSON(my.score_server,function(response){
        var obj = response.data;
        if(Array.isArray(obj)){ // did the server return an array?
          // something went wrong, we're meant to receive an object
          if(obj.length===1){ // is the array a single item?
            my.scores = Object.assign(my.scores, obj[0]);
          }else{ // either array was empty, or it has multiple items
            console.warn("server gave no valid scores");
          }
        }else{ // server returned an object already
          my.scores = Object.assign(my.scores, obj); // just update our scores
        }
        my.update_score_text();
      });
    }
  }; // end of NeuroSim.initialise()
  
  /**
   * For a picklist button (could be a <button> or an <input type=checkbox>),
   * find the parent <LI> and get its data-action property.
   * create a handler that will add the required content when the button is clicked.
   * if there is an "data-action", check for "snippet-" to add a snippet,
   * otherwise dispatch to functions declared in case_button_actions.
   * ensure the "stophere" class is cleared, and expose new sections as required. 
   */
  my.set_picklist_action = function(button){
    let act     = button.parent().attr("data-action"); // get any action associated
    let content = button.parent()[0].innerHTML; // whole <button> html
    var txt     = $(button).html(); // just button contents
    var reg_txt = my.unfriendlyName(txt); // convert text to a region name if possible
    let reg = "";   // test whether this corresponds to a brain region.
    if(reg_txt in my.entity_data){ // is the item an entity?
      reg = reg_txt;
    }else if(reg_txt+"-left" in my.entity_data){ // what if we append '-left'
      reg = reg_txt+"-left";  // : is it an entity now?
    }
    if(reg.length>0){ // if we found an entity with this name,
      // create an icon to select the item.
      var region_sel = $("<span class='neurosim-select-action' data-action='select-'"+reg+"><i class='fa fa-crosshairs '></i></span>");
      button.append(region_sel);
      button.prop("title","Click to select "+my.friendlyText(reg)+" in the images");
    }

    /** 
     * Create a function to 'move on' once the picklist button is pressed
     * if the button's parent pick-list has a 'stophere' class.
     * update score if we are scoring every click.
     */
    var try_moveon  = function(button){
      var picklist = button.closest(".neurosim-pick-list");
      if(picklist.hasClass("stophere")){  // if the list is a "stophere",
        picklist.removeClass("stophere"); // then remove the stop, 
        if(my.SCORE_ALLCLICKS){
          my.scores[my.casename] += 1; // score for making any progress at all?
          my.save_scores();
        }
      }
      /* if we have this class, then go through every examination panel
       * and reveal the lesions */
      if(picklist.hasClass("neurosim-reveal-all-lesions")){
        $(".neurosim-exam").addClass("image-show-lesions");
        NeuroSim.all_displays_refresh_lesion();
      }
      // this function hides elements up to "stophere".
      my.update_exam_section(button.closest(".exam-section"));      
    };
    /** 
     * Create a function to execute the data-action attribute of the 
     * button. If it is a "snippet", then either replace the button with the
     * snippet if the parent list has "replace", or add a new accordion section at 
     * the end, if not.
     * If it is not a snippet, then look up the actiion function in the
     * my.case_button_actions dictionary.
     */
    var try_action = function(button,evt){
      if(act!==undefined && act!==null){ // is there an action?
        if(act.startsWith("snippet")){ // does it start with "snippet"?
          // if so, add the snippet 
          // (snippets are elements in 'case-snippets.html' that have a class starting with 'snippet'.)
          if(button.closest("ul").hasClass("replace")){ // Replace button?
            my.replace_with_snippet(evt.target, act,false);
          }else{  // New accordion section?
            // get the parent <UL>, and find the first <LI> ancestor.
            // this should be the Accordion element (or carousel element etc)
            // then add a new accordion item containing the snippet, after this LI.
            var addendum =  my.case_snippets.find("."+act+" li").clone(); // <LI>
            // this is the (accordion) section after which the new item will be added
            var section = button.closest(".neurosim-pick-list").closest("li");
            section.after(  addendum  );
            my.initialise_gui_element(addendum); // create images/exams
            my.update_exam_section( section.parent() ); // ensure future remains hidden, if nonlinear case
            my.startModelWhenReady(); // this sets the model up
            // now open the selected new LI
            my.pager.inserted( addendum );
          }
        }else{ // generic action - look it up in case_button_actions
          // then bind click to the action
            my.case_button_actions[act](evt); 
        }
      }else{ // action undefined or null
        console.log("pick list has no data-action: "+content);
      }
    }
    /** Create a function to select the correspondingn model region based 
     *  on the text in the button */
    var try_select = function(b){
      // try to select the item?
      if(reg.length>0){
        NeuroSim.setSelection(reg);
      }
    };
    /** 
     * when the button is clicked, do all three: move on, execute the data-action, and 
     * select a region if possible.
     */
    button.click(function(e){
      try_moveon(button);
      try_action(button,e);
      try_select(button);
    });
  };

  /** 
   * This is called when an element (button) wants to be replaced by
   * a snippet from the case_snippets.
   * Replace the html, convert to a NeuroExam or NeuroImage if appropriate, 
   * and stop further click events.
   * Classes on the old element are preserved if required
   * Returns the new element
   */
  my.replace_with_snippet = function(el,sn,preservecls){
    var dest = $(el).parent();  
    var cls = el.getAttribute("class"); // preserve the classes to allow configuring the snippet if necessary
    $(el).replaceWith(my.case_snippets.find("."+sn).clone());
    var e; // the new element
    if(sn.startsWith("snippet-exam")){ // is it an exam snippet?
      // find the element within the newly created segment, that is called
      // "neurosim-exam-...", matching the snippet.
      e=$(dest).find("."+sn.replace("snippet","neurosim"))[0];
      var x=NeuroExam(e); // create the appropriate NeuroExam
      if( e!==undefined && preservecls ){ // if a new element was successfully created
        $(e).addClass(cls); // add back the old classes        
      }
    }
    $(el).unbind("click"); // stop the button etc responding from further events.
    return e; // returh the new element
  };
  /** 
   * when a new accordion item is added, or buttons are clicked, find the first 'stophere'
   * and hide everything after it.
   * This often used to reveal new items, by removing a 'stophere'.
   * 
   * When new images are being shown, ensure they are appropriately sized.
   */
  my.update_exam_section = function(section){
    // work out which elements are hidden 
    var hid=$(section).find(">li").filter(":hidden");
    $(section).find(">li").show();                     // unhide all items
    var firststop = $(section).find(".stophere")[0];
     // re-hide everything after the stophere
    $(firststop).closest("li").nextAll().hide(); 
    // find the elements that have been newly shown
    var hid2=hid.filter(":not(:hidden)");
    // now scroll to the bottom, if new elements were shown
    if(hid2.length>0){ 
      //    $('body').animate({scrollTop: $(section).find(">li:last-child").offset().top + 'px'},500);
      //$('body').animate({scrollTop:$(section).prop("scrollHeight")}, 250);
          /** Scroll to bottom of page */
      $("html, body").animate({ scrollTop: $(document).height() }, "slow");

    }
    /** Next we have to ensure any images are appropriately scaled */
    
    // look in each NeuroImage, to see if it has been shown
    for(let i=0;i<NeuroSim.image_slices.length;i++){
      let slc = NeuroSim.image_slices[i];
      hid2.each(function(j,e){ // for each shown section,
        if( $(e).find(".image-svg")[0] === slc.image_svg[0] ){ // does it contain the image-svg?
          if( slc.panzoom !== undefined ){ // does it support pan/zoom?
            setTimeout(function(){
              slc.panzoom.resize();
              slc.panzoom.center(); // if so, size it appropriately
              slc.panzoom.fit();
            },250);
          }
        }
      });
    }
  };
  
  /**
   * List of actions to be performed when buttons pressed. 
   * These are checked when buttons in a neurosim-pick-list are clicked, and 
   * they have a data-action attribute that doesn't start with "snippet-".
   * The element generating the event is replaced with the examination requested
   */
  my.case_button_actions = {
    "exam-visualfields":function(e){
      var dest = $(e.target).parent();
      $(e.target).replaceWith(my.case_snippets.find(".snippet-exam-visualfields").clone());
      var x = NeuroExam($(dest).find(".neurosim-exam-visualfields")[0]);
      $(e.target).unbind("click");
    },
    "quiz-correct":function(e){
      /** Called when a correct answer button is clicked */
      if($(e.target).hasClass("btn") && !$(e.target).hasClass("btn-correct")){
        // display the correct answer
        $(e.target).addClass("btn-correct");
        var raw = my.getButtonText(e.target);
        my.get_stem_description(raw,function(desc){ // get the description of the stem (as a node)
          $(e.target).append(desc); // and append it inside the button...
        });
        my.current_quiz.answer_correct(); // call quiz logic to do the scoring
      }
    },
    "quiz-wrong":function(e){
      /** called when a wrong answer button is clicked */
      if($(e.target).hasClass("btn") && !$(e.target).hasClass("btn-wrong")){
        // display the correct answer
        $(e.target).addClass("btn-wrong");
        var raw = my.getButtonText(e.target);
        my.get_stem_description(raw, function(desc){
          $(e.target).append(desc);
        });
      }
      my.current_quiz.answer_wrong(); // call quiz logic to do the scoring
    }
  };
  /**
   * Convert free text into something resembling a region name
   * from the model.  Eliminates L/R, and puts hyphens between terms.
   * This is used for convering MCQ stems into a the region name, for
   * showing descriptions and showing images
   */
  my.unfriendlyName = function(txt){
    // lower case, remove 'left'/'right'
    var s=txt.toLowerCase().replace(/(left|right)/g,"").trim();
    // remove anything after "and" or "with" 
    if( / and /.test(s)){       s=s.split( / and /); s=s[0];     }
    if(/ with /.test(s)){       s=s.split(/ with /); s=s[0];     }
    // remove anything after / within brackets, or after commas
    s=s.split(/[(),<>]/)[0]; 
    // replace remaning spaces with hyphens
    s=s.trim().replace(/\s+/g,"-");
    // remove non-word characters
    s=s.replace(/[^\w]/g,"").trim();
    return s;
  };
  /** 
   * takes some free text from an MCQ stem, and sees if we have a matching description
   * in case-snippets that matches. We check the 'data-stem' attribute of the snippets
   * to see if it matches the unfriendlyName() of this text. 
   * If no description available, go to Wikipedia.
   * The callback is called asynchronously with the contents as a jQuery node.
   */
  my.get_stem_description = function(txt, callback){
    let txt2 = my.unfriendlyName(txt);
    if(txt2.length===0){     // if the input text is empty
      callback($());        // return an empty jquery object
      return;
    }
    // get any description text from the snippets.
    var desc = my.case_snippets.find(".snippet-stem-description[data-stem=\""+txt2+"\"]").html();
    if(desc===undefined){ 
      desc=""; 
      console.log("no snippets-stem found for "+txt2);
    }

    // create a wikipedia icon to click for more info
    var wik=$("<a><i class='fa fa-info-circle'></i></a>"); // make a 'W' icon
    wik.click(function(e){                                // when W is clicked, 
      NeuroSim.generic_infobox(my.simplify_free_text(txt));
/*      my.getWikipediaSummaryFreeText(txt,function(wtxt){ // look up on Wikipedia
        if(wtxt!==undefined){
                 // and insert the text right here.
          $(e.target).append($("<div class='btn-description'>"+wtxt+"</div>"));
        }else{
          $(e.target).hide(); // if no wikipedia text found, hide the icon.
        }
      });
*/
      //$(e.delegateTarget).off("click"); // stop further clicks
    });
    var x=$("<div><div class='btn-description'>"+desc+"</div></div>");
    x.append(wik);
    callback(x);
  };
  
  /**
   * get the text within a button element. This varies according to whether 
   * it is a standard <btn> or is an <input> checkbox (used for multiple
   * selection buttons). 
   */
  my.getButtonText = function(b){
    if($(b).prop("tagName").toLowerCase()==="input"){ // is it an <input>?
      var l = $(b).next(); // if so get the following element 
      if(l.prop("tagName").toLowerCase()==="label"){ // is it a <label>?
        return l[0].innerHTML.trim(); // if so get the label text
      }else{
        console.warn("can't find button text for "+b);
        return b.innerHTML;
      }
    }else{
      return b.innerHTML.trim();
    }
  };

  /** 
   * Resource 1: Neurology.json - contains all the anatomical regions 
   * and their connections
   * This contains the list of entities and their connections.
   */
  my.load_resource_neurology_json = function(){
    return $.getJSON('Neurology.json',function(data){
      my.entity_data = data;
      /* Create an array of region names for autocompletion */
      my.region_names = [];
      for(var p in my.entity_data){
        my.region_names.push({
          label:my.friendlyText(p),
          value:my.friendlyText(p),
          entity:p
        });
        /** create entries for each synonym too */
        if("s" in my.entity_data[p]){
          for(var syn in my.entity_data[p].s){
            my.region_names.push({
              label:my.friendlyText(syn),
              value:my.friendlyText(syn),
              entity:p
            });
          }
        }
      }

    }).error(function(){
      console.log('error loading entity data from Neurology.json');
    }); // end Neurology.json
  };
  
  /**
   * Resource 2: 
   * load the slice metadata, which contains 'modalities', svgs',
   * 'credits'. SVGs contains a list of modalities, each of which has 
   * a list of slice types (eg planes), and each of those has a numbered 
   * set of slices, with the file names in.
   */
  my.load_resource_slice_metadata_json = function(){
    return $.getJSON('SliceMetadata.json',function(data){
      my.slice_metadata = data; // store all metadata
      my.all_slice_lookup = {};  // for each slice, by name, record its modality, type and number
      /** 
       * Go through each slice, and store its modality, type and slice number,
       * keyed by the filename, so the current slice can easily be located
       */
      for(var m in my.slice_metadata.svgs){ // for each modality
        var modality = my.slice_metadata.svgs[m]; 
        for(var t in modality){ // for each type (plane)
          var type = modality[t];
          for(var s=0;s<type.length; s++){ // for each slice index
            var slice = type[s];
            my.all_slice_lookup[slice.file] = { m:m, t:t, s:s, p:slice.pos };
          } // next slice
        } // next plane
      } // next modality
    }).error(function(){
      console.log('error loading slice data from SliceMetadata.json');
    }); // end load SliceMetadata
  };
  
  /**
   * Resource 3: Load visibility and hierarchy
   */
  my.load_resource_visibility_json = function(){
    return $.getJSON("visibility.json", function(data){
      my.visibility=data;
    }).error(function(e){
      console.log("error loading visibility.json");
    });
  };
  
  /**
   * Resource 4: 
   * Read the history metadata, which contains info on what patients might say 
   * when asked certain questions, depending on the state of the model, 
   * and info on how to display results tables.
   */
  my.load_resource_neurosim_history_json = function(){
    return $.getJSON("neurosim-history.json",function(data){
      my.history_metadata = data;
    }).error(function(e){
      console.log("error loading neurosim-history.json");
      console.log(e);
    });
  };
  /**
   * Resource 5:
   * Read the case snippets html file, and parse into a DOM variable.
   */
  my.load_resource_case_snippets_html = function(){
    return $.get('case-snippets.html',function(data){
      my.case_snippets = $(data);
    }).error(function(e){
      console.log("error loading case snippets html");
    });
  };
  /**
   * Resource 6: cases.json
   * this contains information about how to create lesions for a given 
   * disease process, and how to modify the history.
   * After the resource is read, check the <BODY data-cases=""> property
   * and implement the disease specified.
   */
  my.load_resource_cases_json = function(){
    return $.getJSON("cases.json", function(data){
      my.cases = data;
      my.disease = {}; my.disease.matchers = []; 

      var case_select = $(my.root).attr("data-case");
      if(case_select!==undefined){
        my.implement_lesions(case_select);
      }
    }).error(function(){
      console.log("error loading cases.json");
    });
  };
    

  /** recursively implement the disease.
   * This takes a text parameter, usually from the "data-case" attribute 
   * supplied in the HTML <H1> tag.
   * 
   *  first look for the disease name in 'diseases', then in 'syndromes'.
   *  if it has 'lesion', then implement the lesions.
   *  if it has 'effects', then select effects and search for the lesions in 'syndromes'
   *  if not a disease or syndrome, then treat as "region:lesion;" pairs
   * 
   *  call this with the condition name, to be looked up in the cases.json file.
   * This may be specified in the "body" tag of the document.
   */
  my.implement_lesions = function(e){  
    var x = my.cases.diseases[e]; // is it a disease?
    // keep a list of regexp matchers for testing diagnosis answers
    if(x===undefined){ // if it's not a disease,
      x=my.cases.syndromes[e];    // is it a syndrome?
    } // either way, deal with the problem similarly
    if(x===undefined){ // if it's not a disease or a syndrome:
      // see if we can parse a lesion command, in the form
      // "medulla-left:0.5; median-nerve-left:0.8"
      var lesion_commands = e.split(/;/);
      for(var i=0;i<lesion_commands.length;i++){ // for each lesion
        if(lesion_commands[i].length===0){ continue; } // ignore blanks
        lesion_commands[i] = lesion_commands[i].trim();
        var lr = lesion_commands[i].split(/:/);  // left and right-hand sides of assignment
        if(lr[0]===undefined || lr[1]===undefined){ // if it is not a region:lesion pair, 
          if(lesion_commands.length>1){ // and there was at least one semicolon,
            my.implement_lesions(lesion_commands[i]); // recurse in case it's a disease or syndrome.
          }else{
            console.log("invalid lesion command, "+lesion_commands[i]);
          }
          continue;
        }
        var  lv = parseFloat(lr[1].trim()); // specified lesion value
        lr[0]=lr[0].trim();
        if (lr[0].length===0){continue;}
        if(lr[0] in my.entity_data && !isNaN(lv) & lv!==undefined){
           my.lesions[lr[0]] = lv;
        }else{
          console.log("invalid lesion command, "+lr[0]+":"+lr[1]);
          continue;
        }
      }
      return; // it wasn't a disease or a syndrome, so just return now
    }
    // it was a disease or a syndrome. So let's see if there is a "lesion" or "effects".
    if(x.lesion!==undefined){  // it has a lesion?
      var l=[];
      // implement 'affect' == 'all','any', or probability of lesion
      if(x.affect=="any"){
        var lesion_idx = Math.floor(Math.random()*x.lesion.length);
        l = [x.lesion[lesion_idx]]; // select a random lesion
        // if affect is "all", or was not defined, then
      }else if(x.affect=="all" || x.affect===undefined){ // make all lesions 
        l = x.lesion;
      }else {         // select each lesion with random probability, specified by affect
        for(i=0;i<x.lesion.length;i++){
          if(Math.random()<x.affect){  
            l.push(x.lesion[i]); // add the lesion 
          }
        }
      }
      for(i=0;i<l.length;i++){ // for each lesion
        if(!(x.extent instanceof Array)){ // is extent numeric 
          var ext = x.extent; 
          // if the extent is defined, 
          if (ext===undefined){ ext=1; }
          // just use 'extent' as the lesion amount.
          // otherwise treat the amount as 1.
          my.lesions[ l[i] ] =  ext ;
        }else{ // extent is an array
          // choose a value randomly from extent[0] to extent[1].
          my.lesions[ l[i] ] =  x.extent[0]+Math.random()*(x.extent[1]-x.extent[0]) ;
        }
      } // end for each lesion
    } // end if has lesion
    if("matchers" in x){ // add diagnosis matchers for the lesion
      // this is used when the user has to input a diagnosis, to see if they are right.
      my.disease.matchers = my.disease.matchers.concat(x.matchers);
    }
    if(x.effects !== undefined){ // it has effects?
      // x.effects should be an array now.
      var ef=[]; // effects mean look up the name again in 'cases.json'
      if(x.affect=="any"){ // choose a single effect frmo the list
        ef=[x.effects[Math.floor(Math.random()*x.effects.length)]]; // select random effect
      }else{ // affect is "all" (default)
        ef = ef.concat(x.effects); // add on the list of effects to the list.
      }
      for(i=0;i<ef.length;i++){ // for each selected effect
        // call implement_lesions recursively with the new effect name.
        my.implement_lesions(ef[i]); 
      }
    } // end if has effects

    // add case-specific section to history if it doesn't exist
    if(!("case_specific" in my.history_metadata.history)){
      my.history_metadata.case_specific = {};
    }
    if(x.history !== undefined){ // it has a specific history?
      var hx = [];
      // merge in the new questions from the provided case-specific history
      my.history_metadata.case_specific = object_assign_deep(1, my.history_metadata.case_specific,  x.history );
    }
    if(x.observations){
      my.history_metadata.observations =  object_assign_deep(1, my.history_metadata.observations,  x.observations );
    }

    if(x.collateral){ // does this problem include collateral info?
      if("collateral" in my.history_metadata){ // combine with existing
        my.history_metadata.collateral = Object.assign( my.history_metadata.collateral, x.collateral );
      }else{ // or add fresh
        my.history_metadata.collateral = x.collateral; 
      }
      // reveal collateral button
      $(".neurosim-history-ask-collateral").show();
    }else{
      $(".neurosim-history-ask-collateral").hide();
    }


  }; // end function implement_lesions
  
  /**
   * Clear lesions and start fresh case
   */
  my.new_case = function(casename){ 
    my.lesions = {};                // clear lesions
    if(!(casename in my.scores)){
      my.scores[casename] = 0;        // create a score for this case.
    }
    my.casename = casename;  
  };
  /**
   * Read values from my.scores, and enable some tags to display the score text, 
   * and some nodes to indicate whether the case is complete.
   * We set any tags with class 'case-score-value'
   * and with a 'data-casename' attribute matching a given 
   * case in my.scores, to the appropriate values.
   * then, tags with class 'case-iscomplete' and matching 'data-casename' attributes
   * are given the new class 'neuosim-case-complete'.
   */
  my.update_score_text = function(){
    for(var i in my.scores){
      $(".case-score-value[data-casename="+i+"]").html(my.scores[i]);
      if(my.scores[i]>0){
        $(".case-iscomplete[data-casename="+i+"]").addClass("neurosim-case-complete");
      }
    }
  };
  /**
   * Write the variable NeuroSim.scores to the server.
   */
  my.save_scores = function(){
    var data=JSON.stringify(my.scores);
    if(my.score_server){
      $.ajax({
        type:        "POST",
        url:         my.score_server,
        contentType: "application/json",
        dataType:    "json",
        data:        data,
        error:       function(xhr,opt,er){
          console.log("unable to send scores to server: "+er);
        }
      });
      console.log("sending data: "+data);
    }else{
      console.log("no server, scores not saved.");
    }
    my.update_score_text();
  };
  
    
  /** indicates whether all the metadata has loaded yet. */
  my.isLoaded = false;
  /** Do other initialisation methods want to know when NeuroSim has initialised? */
  my.loadListeners = [];
  /** Indicates whether all the NeuroImage items have loaded their SVGs */
  my.allImagesLoaded = false;
  
  /** 
   * Register a function that would like to be called once neurosim app has initialised. 
   * This is called for exasmple by each NeuroImage, to set its regions.
   */
  my.registerLoadListener = function(fn){
    if(!my.isLoaded){
      my.loadListeners.push(fn);
    }else{
      fn();
    }
  };
  /**
   * Keep list of NeuroExam objects that have been initialised 
   */
  my.exams=[];

  
  /** 
   * For a particular root element, after the DOM has been created.
   * 
   * This creates the NeuroImage objects, 
   * NeuroExam examination objects,
   * Tables, and History blocks
   */
  my.initialise_gui_element = function(root){
    // get the case name from the <h1> tag's data-casename attribute, 
    // located somewhere within the current case's root element.
    var name = $(root).find("h1").attr("data-casename");
    if(name!==undefined){ my.new_case( name ); }
    var casedata = $(root).find("h1").attr('data-case');  // this attribute should be a reference to something in 'cases.json'
    if(casedata!==undefined)  { // if it is specified, look up the case and add lesions
      my.implement_lesions(casedata);
    }
    /**
     * Replace snippets with their corresponding elements from my.case_snippets
     * This requires 'case-snippets.html' to be loeded, but must occur before
     * image-slices and exams are initialised, because it might insert these.
     */
    root.find(".neurosim-snippet").each(function(){ // for each .neurosim-snippet
      var sn=$(this).attr("data-action"); 
      if(sn!==undefined && sn!==null){ // check if it has a "data-action" attribute
        var e=my.replace_with_snippet(this,sn,true); // and replace the element with the snippet
      }else{
        console.log("no data-action attribute for snippet "+sn);
      }
    });
    // for each element with class "image-slice",
    root.find(".image-slice").each(function(){  
      // create a NeuroImage object, using the element's id, and initialise it.
      var image = NeuroImage(this);
    });
    /* Create Examinations */
    root.find(".neurosim-exam-snellen, .neurosim-exam-visualfields, .neurosim-exam-face" +
      ", .neurosim-exam-motor, .neurosim-exam-reflexes, .neurosim-exam-dermatomes" +
      ", .neurosim-exam-imageswapper, .neurosim-exam-observations")
      .each(function(){
        my.exams.push(NeuroExam(this));
    });

    /** create the selection menu */
    root.find(".neurosim-selection-menu").menu(); // then create the menu

    
    /**
     *  Create the 3D man, if a viewport3d is present.
     */
    my.root.find(".viewport3d").each(function(){
      var m = new NeuroMan(this);
      my.three_d = m;
      m.initialise();
    });

    
    /**
     * Create any tables 
     * Looks for .neurosim-table, and adds metadata from neurosim-history.json
     * table section. Example of a typical table is:
     * <ul class=neurosim-table neurosim-highlight-abnormal data-table=Blood>
     *  <li data-table=WCC> 5 </li>
     * </ul>
     */ 
    root.find(".neurosim-table").each(function(){ // for each table
      var table_info = my.history_metadata.tables[ this.getAttribute("data-table") ];
      if(table_info===undefined){ console.log("bad table "+$(this).html()); }
      $(this).attr("title",table_info.title);
      $(this).find(">li").each(function(){ // for each row
        var row_type = this.getAttribute("data-table");
        var row_info = table_info.rows[ row_type ];
        if(row_info===undefined){ console.log("bad row type "+row_type); return; }
        var content=$(this).html(), newcontent="";
        // add row header, and value, to make a table row
        newcontent+="<span class='table-header'>"+row_info[0]+"</span><span>"+content+"</span>";
        newcontent+="<span>"+row_info[1]+"</span>"; // add units and normal range
        var low=parseFloat(row_info[2]), high=parseFloat(row_info[3]), val=parseFloat(content);
        if(row_info[2].length>0 || row_info[3].length>0){
          newcontent+="<span>("+row_info[2]+" &mdash; "+row_info[3]+")<span>";
        }else{ newcontent+="<span>&nbsp;</span>"; }
        $(this).html(newcontent);
        $(this).attr("title",row_info[0]);
        // if range and value are numeric, colour abnormal results
        if( (!isNaN(low) && !isNaN(val) && val<low) ||
            (!isNaN(high) && !isNaN(val) && val>high) ){
          $(this).addClass("neurosim-abnormal"); 
        }
      });
    });
    
    
    /**
     *  Set up pick-lists for user to select options from a list. Picklists can have classes
     * 'multiple' : allow multiple selection
     * 'stophere' : later accordion items are hidden until something is selected in the list. 
     * 'replace'  : when the button is clicked, the button is replaced by a snippet. 
     *              Otherwise create a new section to hold the new snippet. 
     * 'neurosim-reveal-all-lesions' : when clicked, the class "image-show-lesions" is
     *              added to  every examination 
     * They may also have a property "data-action='snippet-...'" if you want the 
     * button to add a case snippet. The snippet replaces the button or is created as
     * a new section, depending on whether the 'replace' class is present.
     */
    
    /*
    root.find(".neurosim-pick-list.multiple  li").each(function(){
      var txt = this.innerHTML;
      // turn each list item's text into a input checkbox. 
      var id  = "chk"+Math.floor(Math.random()*10000);
      var h = "<input type='checkbox' name='"+id+"' id='"+id+"'>";
      // add the text as the label
      h  += "<label for='"+id+"'>"+ txt +"</label>";
      this.innerHTML = h;
      // and turn it into a radio group
      $(this).find("input").checkboxradio({icon:false});
      my.set_picklist_action($(this).find("input"));
    });
    root.find(".neurosim-pick-list.multiple").controlgroup();
    */
    root.find(".neurosim-pick-list li").each(function(){
      var txt = this.innerHTML;
      // use a standard button
      var content = this.innerHTML;
      var toggle = "";
      if($(this).parent().hasClass("multiple")){
         toggle = " data-toggle='button' ";
      }
      this.innerHTML = "<button class='btn btn-primary'"+toggle+">"+txt+"</button>";
      my.set_picklist_action($(this).find("button"));
    });
    root.find(".neurosim-pick-list:not(.multiple)").addClass("btn-group-vertical");
    
    /**
     * Set up pick-text for user to type in an option freely.
     */
    root.find(".neurosim-pick-text").append("<form class='form-inline'>"+
        "<input type='text' placeholder='Diagnosis'>"+
        "<button type=submit class='btn btn-primary'> <i class='fa fa-question-circle'></i> </button>"
    );
    root.find(".neurosim-pick-text form, .neurosim-advance").submit(function(e){
      $(e.target).closest(".neurosim-pick-text").removeClass("stophere");
      my.update_exam_section($(e.target).closest(".exam-section"));
      return false;
    });
    
    /** 
     * Interactive history as a free text entry.
     */
    root.find(".neurosim-history-free").each(function(){
      // Create input form, with textbox. Styled by bootstrap.
      var form=my.case_snippets.find(".snippet-history-listen").clone();
      // Wrap in list item. Use "let" so the handler knows to put results in this list
      let form_li = form.wrap("<li class='neurosim-history-input'></li>").parents();
      form.submit(function(e){ // create submit handler
        e.preventDefault();
        var questiontxt = form.find(".form-control").val();
        if(questiontxt.length===0){ // submitted blank question?
          return; // discard
        }
        // get the input text, and call answer_text_question. This gives a list of lists
        // of the form [ ans, amount, connection ]
        var ans=my.history_answer_text_question(questiontxt);
        my.history_post_answer(questiontxt, ans); // get the answer text from a list of answers.
        form.find(".form-control").val(""); // clear the input box
        if(ans[0] && ans[0][0] && ans[0][0].match(/I didn't understand/)){ // if the input wasn't understood
          form.find(".neurosim-history-hints").removeClass("hidden"); // then show the hints button
        }
      });
      form.find(".neurosim-history-listen").click(function(e){
        // start speech recognition if available
        my.listen(form.find(".form-control")[0]);
      });
      // what to do when the collateral history button is clicked?
      form.find(".neurosim-history-ask-collateral").click(function(e){
        var questiontxt = form.find(".form-control").val(); // get inputted question 
        if(questiontxt.length===0){ // discard if empty
          return;
        }
        // get answer using collateral==true
        var ans = my.history_answer_text_question(questiontxt, true);
        my.history_post_answer(questiontxt, ans, true); // get the answer text from a list of answers.
        form.find(".form-control").val("");
        e.preventDefault();
        return false;
      });
      $(this).append(form_li); // place the textbox form in the list as a list-item.
    });
     
    /**
     * Create an interactive history, with the questions listed, using neurosim-history.json.
     * If the list  <ul class="neurosim-history-selector> lacks the "neurosim-history-all"
     * class, then the "data-history" attribute is read and only question types that
     * match elements in this will be shown.
     * if "data-history" is "case-specific", then list all the case-specific history items.
     */
    root.find(".neurosim-history-selector").each(function(){
      var datahistory = this.getAttribute("data-history"); // get the data history attribute
      if(datahistory===undefined){ // if there is no specified category, 
         datahistory=[];  // ignore it
      }else if(datahistory=="case-specific"){ // get all the case-specific history items
        //TODO include the collaterals here?
        datahistory = Object.keys(my.history_metadata.case_specific);
      }else{ // convert it to a list of question names to include.
        datahistory = datahistory.split(/,/).map(Function.prototype.call, String.prototype.trim); 
      }
      var Hx = my.history_metadata.history; // this history metadata object 
      // create a different function for each element
      // this function adds a question's answer as a list item in this element.
      // the parameter is a question item, an object with 'question', 'keywords' and 'answer'
      // properties. They are obtained from history_metadata.history.
      var history_element = $(this); // keep track of this history gui element
      var add_answer_item = function(q,question_name){ 
        // this let means make a different list element each time we call this
        // so that qitem can be used by the click handler
        let qitem = $("<li> <div class='neurosim-history-question'>"+ q.question +"</div> </li>");
        let iitem = question_name; // keep track of the question's key, for the click handler
        history_element.append(qitem); 
        qitem.click(function(){
          // the key and question element are stored in the handler as a closure.
          my.history_selector_answer_question(iitem,qitem);
        });
      };
      // First implement case_specific answers  - which override the ones below
      Hx = my.history_metadata.case_specific;
      var found_qlist = [];
      if($(this).hasClass("neurosim-history-all")){ // show all questions
        for(i in Hx){ // for each question in the case-specific history 
          add_answer_item( Hx[i], i ); // add this question's answer to the element
          found_qlist.push(i) ; // don't add any generics to the list
        }
        Hx = my.history_metadata.history; // then add everything else from generic history
        for(i in Hx){ 
          if( !found_qlist.includes(i) ){
            add_answer_item( Hx[i], i ); // add this question's answer to the element
          }
        }
      }else{ // show specified questions
        for(i in datahistory){
          Hx = my.history_metadata.case_specific;
          if( Hx[datahistory[i]] ){ // check case-specific history
            add_answer_item( Hx[datahistory[i]], datahistory[i] ); 
          }else{
            Hx = my.history_metadata.history; // check generic history
            if( Hx[ datahistory[i] ] ){
              add_answer_item( Hx[datahistory[i]] , datahistory[i]);
            }
          }
        }
      }

      // OLD:
      // for(i in Hx){ // for each question in the case-specific history 
      //   if( $(this).hasClass("neurosim-history-all") ||
      //       datahistory.includes(i) ){ // does it match this gui element?
      //     add_answer_item( Hx[i] ); // add this question's answer to the element
      //     found_qlist.push(i) ; // don't add any generics to the list
      //   }
      // }
      // Hx = my.history_metadata.history;
      // for(var i in Hx){ // for each generic history question 
      //   if( $(this).hasClass("neurosim-history-all") || 
      //        datahistory.includes(i) ){  // does it match this gui element?
      //     if( !found_qlist.includes(i) ){ // assuming it was not in the case-specific list, 
      //       add_answer_item( Hx[i] ); // add this question's answer to the element
      //     }
      //   } 
      // } // try next question
    });

    
    /**
     *  Diagnosis box as free text entry
     */
    root.find(".neurosim-diagnosis-box").each(function(){ 
      // create Diagnosis box, containing a list of diagnoses, and an input text box
      var snip = my.case_snippets.find(".snippet-diagnosis-box").clone();
      // handle Submit button
      snip.find("form").submit(function(e){
        e.preventDefault();
        var numCorrect, numWrong;
        // check to see if current diagnoses are OK
        snip.find("li").each(function(e){ // for each entered diagnosis
          var found = false;   // see if it matches any regexp
          for(var i=0;i<my.disease.matchers.length;i++){ // go through the matchers
            var regex = new RegExp(my.disease.matchers[i],"i"); // ignore case of the answer
            if(  this.innerHTML.match(regex) ){
              $(this).addClass("neurosim-diagnosis-correct");
              found = true; // label answer "neurosim-diagnosis-correct" if matches
            }
          }
          if(!found){ // otherwise label it "neurosim-diagnosis-wrong"
            $(this).addClass("neurosim-diagnosis-wrong");
          }
          if( found ){ numCorrect+=1; }
          else       { numWrong  +=1; }
        });
      });
      snip.find(".neurosim-diagnosis-add").click(function(e){
        // read inputted diagnosis text 
        var diagnosis = snip.find(".form-control").val();
        // ignore empty diagnoses
        if(diagnosis.length===0){ return; }
        // add new diagnoses just before the text box
        e.delegateTarget.closest("li").before($("<li>"+diagnosis+"</li>")[0]);                 
      });
      
      this.append(snip[0]);
    });

    /**
     *  Set up autocompletion boxes using NeuroSim.region_names, which is loaded
     * from Neurology.json
     */
    my.root.find(".neurosim-region-completion").each(function(i,elm){
      var lod = my.getLodFromClasses($(this));
      $(elm).autocomplete({
        /** determine the set of items to display in the autocomplete */
        source: my.region_names.filter(function(x){
          // use items from region_names. filter them according to the LoD
          // specified in the class.
          if(lod===undefined){
            return true; // no lod? use all items
          }else{
            var vis = NeuroSim.visibility.by_region[x.entity];
            if(vis===undefined){ return false;} // if no visibility rating, don't show it
            if(vis<=lod ){ return true; }
            else return false; // lod present - use only if visibility is below LoD.
          }
        }) ,
        change: function(event,ui){
          if(ui!==null && ui.item!==null){
            my.setSelection(ui.item.entity, this);
          }
        },
        select: function(event,ui){
          if(ui!==null && ui.item!==null){
            my.setSelection(ui.item.entity, this);
          }
        },
        /** create the autocomplete list popup.
         *  include a short description of each region?
         */
        create: function(){
          $(this).data("ui-autocomplete")._renderItem = function(ul,item){
            var descr = my.describe(my.entity_data[item.entity]);
            var nitem = $("<li>").attr("data-value", item.entity)
            .append("<div class=neurosim-entity-list-head>"+item.label+"</div>");
            if(my.AUTOCOMPLETE_INCLUDE_DESCRIPTION){
              nitem.append("<div class=neurosim-description>"+descr+"</span>");
            }
            nitem.appendTo(ul);
            return nitem;
          };
        },
        open: function () {
          // since it might appear in a bootbox, we better bring it forward.
          $(this).autocomplete('widget').css('z-index', 1500);
        }
      }); // end call to autocomplete()
      $(elm).on("autocompletechange", function(event,ui) {
        // when the item is selected, call 'setSelection' to propagate any 
        // change to the rest of NeuroSim.
        if(ui!==null && ui.item!==null){
          my.setSelection(ui.item.entity, this);
        }
      }); // end on 
    }); // end each autocomplete item
    /**
     * create autocompletion for history questions
     */
    my.all_questions_list = [];
    for(var i in my.history_metadata.case_specific){
      var str =  my.history_metadata.case_specific[i].question;
      if(str){ 
        my.all_questions_list.push( str );
      }
    }
    for(i in my.history_metadata.history){
      str =   my.history_metadata.history[i].question;
      if(str){
        my.all_questions_list.push( str );
      }
    }
    my.root.find(".neurosim-question-completion").each(function(i,elm){
      $(elm).autocomplete({
        /** determine the set of items to display in the autocomplete */
        source: my.all_questions_list,
        open: function () {
          // since it might appear in a bootbox, we better bring it forward.
          $(this).autocomplete('widget').css('z-index', 1500);
        }
      }); // end call to autocomplete()
    });
    /**
     * Create lesion sliders - which call set_lesion_from_slider when the
     * values is changed. Note that this is also changed by setSelection.
     */
    my.root.find( ".neurosim-lesion-slider" ).slider({
      orientation: "horizontal",
      range: "min",
      max: 255,
      value: 0,
      stop: my.set_lesion_from_slider
      // no need for change handler - this is called when we change our own slider internally
      //change: my.set_lesion_from_slider
    });

    /**
     * Look for quizzes and set up the selection.
     * A quiz is defined by an element with class "neurosim-quiz-question", and an
     * element with class "neurosim-quiz-answer". One of these can be a "image-slice"
     * and the other can be a list.
     */
    var quizq=root.find(".neurosim-quiz-question");
    if(my.current_quiz === undefined && quizq.length>0){ // are there any quiz questions in this DOM?
      my.addImagesLoadedListener(function(){
        // if so, create a listener to start the quiz once the images'
        // regions have been calculated (needed so that we can select a question)
        /******************************
         *  QUIZ OBJECT:
         *  contains data structure about currently running quiz, and functions to 
         *  update this structure when questions are swapped in or answers are selected.
         */
        my.current_quiz = {
          "question_elem":root.find(".neurosim-quiz-question"),
          "answer_elem":  root.find(".neurosim-quiz-answer"),
          "new_question": function(){
            var slc;
            if(my.current_quiz.question_elem.hasClass("image-slice")){
              // if the question is a slice, this is a naming puzzle
              slc = my.slice_for_element(my.current_quiz.question_elem); //get the slice
              my.current_quiz.type = my.current_quiz.TYPE_NAMING;
            }else if(my.current_quiz.answer_elem.hasClass("image-slice")){
              // if the answer is a slice, this is a clicking puzzle
              slc = my.slice_for_element(my.current_quiz.answer_elem); // get slice
              my.current_quiz.type = my.current_quiz.TYPE_CLICKING;
            }
            ///////////////////
            // Create a question
            var questionlist = my.current_quiz.question_elem.find("ul li");
            var allitems; // list of items that can be used as questions
            var i; // iterator for subsequent for loops (keeps lint happy)
            /** First look for a <ul><li> within the question. If it exists,
             * it contains a list of regions that will be tested.
             */ 
            if(questionlist.length>0){
              allitems=[]; // read the list of question items
              for(i=0;i<questionlist.length;i++){
                // add to 'allitems'
                allitems.push( $(questionlist[i]).html() );
              }
            }else{   // there was no list -- just get the regions visible on the current slice
              allitems = slc.get_regions(); // list of regions on the question slicee
            }
              
            // randomly choose one region from this slice
            var chosenitem = undefined; 
            var count = 20; // but don't try for more than 20 iterations
            while( (chosenitem===undefined || chosenitem in my.current_quiz.completed) &&
                    count++>0 ){ // find an item that hasn't been completed yet
              chosenitem = allitems[Math.floor(Math.random()*allitems.length)];
            }
            my.current_quiz.correct_item = chosenitem.id;
            slc.do_change_selection(chosenitem.id);
            // now set up the answer control
            if(my.current_quiz.type == my.current_quiz.TYPE_NAMING){
              // Naming puzzle: create a menu of possible names 
              my.current_quiz.answer_elem.empty();
              // select integer from 0 to NUM_OPTIONS indicating the index of the
              // correct answer in the list of options
              var correct_index = Math.floor(Math.random()*my.current_quiz.NUM_OPTIONS);
              for(i=0;i<my.current_quiz.NUM_OPTIONS;i++){ // add the options (1 right, the rest wrong answers (foils)) 
                if(i==correct_index){  // add the correct answer here? 
                  // append list item with the friendly name of the highlighted item
                  my.current_quiz.answer_elem.append("<li data-action='quiz-correct'>"+
                      my.friendlyText(chosenitem.id)+"</li>");                
                }else{ // add a wrong answer
                  var foil = undefined; // choose a random region name, from the whole list of items
                  while(foil === undefined && foil !== chosenitem.id){ // ensuring it is not the correct answer
                   foil = my.region_names[ Math.floor(Math.random()*my.region_names.length) ];
                  }
                  // and add the list item here.
                  my.current_quiz.answer_elem.append("<li data-action='quiz-wrong'>"+
                                                     foil.label+"</li>");
                }
              } // next option
            }else if(my.current_quiz.type==my.current_quiz.TYPE_CLICKING){
              // Clicking puzzle: attach the appropriate click handler to each image region
              for(i=0;i<allitems.length;i++){ // for each image-region
                if(allitems[i] == chosenitem){
                  // remove previous handers, then add "correct" handler
                  $(allitems[i]).off().click(my.case_button_actions["quiz-correct"]);
                }else{
                  $(allitems[i]).off().click(my.case_button_actions["quiz-wrong"]);
                }
              }
            }
            my.current_quiz.error_count = 0; // reset the error count for this question 
            my.current_quiz.question_index++; // increment the question index
            // the newly created buttons are in a neurosim-pick-list, and 
            // need to be initialised as GUI
            my.initialise_gui_element($(my.current_quiz.answer_elem));
          },
          "completed": [],
          /** 
           * This function is called by case_button_actions when 
           * a button with attribute  data-action='quiz-correct'  is clicked  
           */
          "answer_correct":function(){ // if a correct answer is selected
            my.current_quiz.score++; // increment score
            my.current_quiz.completed.push(my.current_quiz.correct_item);
            setTimeout(my.current_quiz.new_question, 2000); // move to new question after 2 seconds
          },
          /** 
           * This is called by caase_button_actions when
           * a button with   data-action='quiz-wrong'  is pressed.
           */
          "answer_wrong":function(){ // when a wrong answer is selected
            my.current_quiz.error_count++; // increase the error count for this question
            if(my.current_quiz.error_count > my.current_quiz.MAX_ERRORS){
              // if we have made too many mistakes, move to next question after 2 seconds
              setTimeout(my.current_quiz.new_question,2000);
            }
          },
          "score":0,
          "error_count":0,
          "question_index":0,
          "MAX_ERRORS":100,
          "NUM_OPTIONS":5,
          "type":undefined,
          "TYPE_NAMING":0,
          "TYPE_CLICKING":1
        }; // end Quiz object
        // Now start the quiz.
        my.current_quiz.new_question();
      }); // end images_loaded listener
    }else{ // no quiz element: just count a score
      my.current_quiz = {
        "answer_correct":function(){
          my.scores[my.casename] += 1;
          my.save_scores();
          my.update_score_text();
        },
        "answer_wrong": function(){
          my.scores[my.casename] += 0;
          my.save_scores();
          my.update_score_text();
        }
      };
    } // end if quiz undefined
    
    /** Call update on each accordion. This will deal with any 'stophere' elements */
    root.find(".exam-section").each(function(){
      my.update_exam_section(this);
    });

    
    /** Make resizeable components resizeable */
    root.find(".neurosim-resizeable").resizable();
    
    /** 
     * remove detached images and examinations. 
     * this is needed when dynamically creating cases, so that
     * image data from old cases are not maintained.
     */
    my.remove_detached_images();

    /**
     * add help icons to history, MMSE, MSE
     */
    root.find(".neurosim-history").each(function(){
      create_help_icon("history",$(this).prev());
    });
    root.find(".snippet-observations").each(function(){
      create_help_icon("observations",$(this).prev());
    });
    root.find(".neurosim-exam-mmse").each(function(){
      create_help_icon("mmse",$(this).prev());
    });
    
  }; //end initialise_gui_element()
  
  
  /**
   *
   *  Initialisation called after the json files have been read in.
   *
   *  First call initialise_gui_element on the root element.
   *  Then call anly load-listeners,
   *   i.e. any callbacks that are waiting for the app to load
   *   (i.e. those registered withi registerLoadListener).
   *  and finally call startModelWhenReady, which will wait till all images loaded then 
   *   perform lesion calculations.
   */
  my.initialiseGUI = function(){
    my.initialise_gui_element(my.root);
    /** 
     * now call the load listeners - the resources have loaded! 
     * This includes NeuroImage objects which need the resources to
     * initialise their SVGs.
     */
    my.isLoaded = true;
    for(var i=0;i<my.loadListeners.length; i++){
      my.loadListeners[i]();
    }
    my.loadListeners = [];
    /**
     * Request update the pathology from the model 
     */
    setTimeout(my.startModelWhenReady,0);
  };

  
  /** 
   * The final step in initialisation is when all the NeuroImage 
   * SVGs have been loaded and processed. At this stage, we can
   * start the background thread and call the lesion calculation
   * to set the examination findings
   */
  my.startModelWhenReady = function(){
    var allImagesLoaded=true; // are all the image_slices loaded?
    for(var i=0;i<my.image_slices.length;i++){
      if(!my.image_slices[i].isLoaded){ allImagesLoaded=false; }
    }
    if(allImagesLoaded){
      /** refresh the findings in each examination */
      my.all_displays_refresh_lesion();
      /** Start the background calculation thread */
      my.bgcalc.postMessage({action:"start"});
      my.allImagesLoaded=true;
      /** Call any registered 'imagesLoaded' listeners */
      for(i=0;i<my.imagesLoadedListener.length;i++){
        my.imagesLoadedListener[i]();
      }
      my.imagesLoadedListener = [];
    }else{ // if not yet loaded, check again in 500 ms
      setTimeout(my.startModelWhenReady,500);
    }
  };
  /** 
   * Images loaded listeners will be called after any NeuroImage 
   * svgs have finished processing, i.e. when isLoaded==true for each one.
   */
  my.imagesLoadedListener = [];
  /** Register a function to be called when all images are loaded */
  my.addImagesLoadedListener = function(f){ 
    if(my.allImagesLoaded){ f(); }
    else{  my.imagesLoadedListener.push(f); }
  };
  
  /** 
   * Purge things from NeuroSim.image_slices and Neurosim.exams, 
   * if their nodes have been removed from the DOM.
   */
  my.remove_detached_images = function(){
    my.image_slices = $.grep(my.image_slices, function(e){ // for each image_slice
      return $.contains(document, e.node); // keep it only if  its node is still in the DOM
    });
    my.exams = $.grep(my.exams,function(e){ // similary for each exam
      return $.contains(document, e.node);
    });
  };
    
  /************** END OF INITIALISATION *******************/
  
  
  /********************************************************/
  
  /** 
   * function to send feedback from the user to the server.
   * called by the submit of the feedback form. 
   * Needs a div ".feedback-result" and the form input  ".feedback-comment".
   * sends comment to my.feedback_server.
   */
  my.submit_feedback = function(){
    // parts of the document for providing feedback:
    var result_div = $(".feedback-result"); // box to tell user what's happening
    var input = $('.feedback-comment'); // the input field with comments
    result_div.html("Sending..."); // inform we're sending
    $.get( my.feedback_server + "?command=comment&value=" + input.val()
           ,"", function(result){ // send fb to server
      $(".feedback-comment").val("Sent"); // completed!
      result_div.append($(result));  // server should say "thanks for comment"
      setTimeout(function(){ // close box automatically after 2 sec
        var bb=result_div.closest(".bootbox"); // find dialog box
        bb.modal('hide');   
        result_div.empty();   // clear input box
      }, 2000)
    });
  };
  
  
  
  /********************************************************
   * History-taking functions
   */
  my.history_post_answer= function(questiontext, ans, COLLAT){
    var destination_li = $(".neurosim-history-input");
    // extract useful text, from just the 0th element of each answer
    var txt=[];
    for(var i=0;i<ans.length;i++){
      var ans_text = my.preprocess_speech_text(ans[i][0]);
      txt.push(ans_text);
    }
    ans=txt.join(". "); // combine text into a single answer.
    // then make a list item with the answer
    var list_item = $("<li class='neurosim-history-qa'></li>"); // the list item
    var question_elem = $("<div class='neurosim-history-question'>"+questiontext+"<div>");
    list_item.append(question_elem); // add the question text to the item
    // put it at the end of the list of questions asked, just before the question input box
    destination_li.before(list_item); 

    // create element for the answer
    var ans_item = $("<div class='neurosim-history-answer'>"+ans+"</div>"); 
    if(COLLAT){
      ans_item.addClass("neurosim-history-answer-collateral");
    }
    list_item.append(ans_item); // and add it to the list item, after the question
    my.speak(ans);
  };
  /**
   * Called when a history question is asked - generate a response.
   * q is the question name, from the history_metadata. 
   *   we call history_answer_question to get the anser.
   * o is the list item where the question/answer is displayed.
   */
  my.history_selector_answer_question = function(q,o){
    // calculate the answer
    var ans = my.history_answer_text_question(q);

    //var ans = my.history_answer_question(q);//2020
    for(var i=0;i<ans.length;i++){ // for each response
      var ans_text = my.preprocess_speech_text( ans[i][0] );
      var answer = $("<div class='neurosim-history-answer'>&ldquo;"+ans_text+"&rdquo;</div>");
      $(o).append(answer);
      my.speak(ans_text);
    }
  };
  /**
   * Called when free text is entered, and a question should be answered
   * txt is the question text to process. 
   * Returns the response as an array of arrays.
   */
  my.history_answer_text_question = function(txt, COLLAT){
    var ans=[];
    my.log_event_to_server(txt);
    // subfunction to append the answer to a given question to ans:
    // qu is the question 
    var add_to_answer = function(qu,as){ 
      var matches = false;
      if(qu === txt){ // check if it exactly matches the name of the question?
        matches = true; 
      }else{ // if not, then go through the keywords
        var kw = as.keywords;
        if(kw===undefined){
          console.log("ill-formed keyword in item"); console.log(qu); console.log(as);
          return;
        }
        for(var j=0;j<as.keywords.length;j++){ // for each keywords in this question
          if(new RegExp(as.keywords[j]).test(txt)){ matches=true; }
        }
      }
      if(matches){ // if the input text matches any of the keywords in this question
        // retrieve an appropriate answer element for this particular match
        var ans_elem = my.history_answer_question(qu,as);
        // use any regex substitutions if $ in answer
        // TODO this erroneously assumes ans_elem is a string, whereas in fact
        // it is an array of arrays.
        if(ans_elem.indexOf("$")>=0){
          ans_elem = ans_elem.replace(".*"+as.keywords[j]+".*", ans_elem);
        }
        ans=ans.concat(ans_elem); //  add the answer in the list
      }
    };
    // first search the case-specific answers.
    if(!COLLAT){ // if asking the patient: 
      for(var q in my.history_metadata.case_specific){
        var hq = my.history_metadata.case_specific[q];
        add_to_answer(q,hq); // try each one
      }
      if(ans.length==0){ // if no case-specific answers, try generics.
        for(var q in my.history_metadata.history){ // for each question
          var hq = my.history_metadata.history[q];
          add_to_answer(q,hq);
        }
      }
    } else { // COLLAT = true: check for collateral history?
      for(var q in my.history_metadata.collateral){
        var hq = my.history_metadata.collateral[q];
        add_to_answer(q,hq); // try each one
      }
    }
    if(ans.length===0){ // no answers? ask to repeat
      ans=[["I didn't understand. Could you ask a different way?",[],[]]];
    }
    return ans;
  };
  
  /**
   * This calculates the best answer to a history question.
   * q is the question name. as is the answers object.
   * the return value is a list of responses, 
   * [
   *    [ text, connection_strength, connection ], 
   *    [ ... ]
   * ]
   */
  my.history_answer_question = function(q,as){
    if(as.answer===undefined){
      my.error('old code send the array, not the structure!');
    }
    if(as.answer[0] instanceof Array){ // WAS AS.ANSWER instanceof  (2020)
      return my.history_answer_question_generic(as.answer);
    }else{
      return my.history_answer_question_connections(as.answer,q);
    }
  };
  /**
   *  The answers depend on the connections in the model.
   *  as is a list of objects, each of which is a kind of model-test.
   *  Calculate the connections for each object, and see if there is a 
   *  specific response. Otherwise use the neutral_answer property from histor[q].
   */
  my.history_answer_question_connections = function(as,q){
    var answers = []; // blank list for results
    var worst_resp=""; // keep track of which response was worst
    for(var i in as){ // for each connection test (set of responses)
      var a = as[i]; // one answer item
      // find the worst response
      var tmp = my.find_worst_item_by_connection(a);
      var worst = tmp[0];
      worst_resp = tmp[1];

      if(worst_resp.length===0){ // if no problem noted, choose a neutral answer.
        var neut = my.history_metadata.history[q].neutral_answer;
        if(neut===undefined || neut.length===0){ // no specific neutral responses provided?
          // select a random generic neutral response 
          worst_resp = my.history_metadata.neutral_answers[Math.floor(Math.random()*my.history_metadata.neutral_answers.length)];
        }else{ // use the specific neutral responses for this question
          if(neut instanceof Array){ // are there several options (ie array)?
            neut=neut[Math.floor(Math.random()*neut.length)];  // choose one at random
          }// otherwise assume it was just a single string.
          worst_resp = neut;
        }
      } // now we should have either a real answer or a neutral response
      // check if anything should be appended to every answer, like, ", doctor.".
      if(my.APPEND_TO_ANSWER){
        worst_resp+=my.APPEND_TO_ANSWER; 
      }
      if(worst<1){// was there actually any problem?
        answers.push([ worst_resp, worst, as[i].connection] );
      } // if not, don't store any response for this connection test
    } // next connection test (for this question)
    if(answers.length===0){// no pathology at all?
      answers.push([worst_resp, 1, []]); // return the default answer, ie the last one found
    }
    return answers;
  };

  /***
   * Takes a connection-problem structure, as defined in neurosim-history.json
   * of the form
   * a == { 
   *   "connection": [  ["region-from","region-to"] ] ,
   *   "0.3": "response 1",
   *   "0.2": "response 2"
   * }
   * The function tests the connection, and 
   * returns the worst value and the worst response in the list.
   * Called from history_answer_question_connections, and from the generic image examination panel
   */
  my.find_worst_item_by_connection = function(a){
    var c = a.connection; // the connections associated with this set of responses
    if(c===undefined){ 
      console.log("bad history answer"); console.log(a); 
      // TODO what to return  here?;
    }
    if(!(c[0] instanceof Array)){ c=[c]; }
    // c is now a list of pairs of entities, i.e a list of connections
    var l = 0; // start of with no lesion
    for(var j=0;j<c.length;j++){ // for each connection
      l=my.lesionAnd(l, my.testConnection(c[j][0], c[j][1])); // add up the lesion amount
    } // l is the lesion for this connection test.
    l=1-l; // l is now the connection strength - so 1 is good and 0 is bad.
    // go through each possible answer, and see if our lesion is at least that bad,
    // and find the worst possible response.
    var worst=1;         // start with no problem
    var worst_resp="";       // and a blank response
    for(j in a){     // for each numeric entry in this answer set,
      var jf = parseFloat(j);
      if(!isNaN(jf) &&   // if the answer key is numeric,
          l<jf    &&      // if the connection strength is worse than this threshold,
          jf<worst){      // and this threshold is worse than the worst threshold crossed so far
        worst=jf;        // then make this the worst one so far
        worst_resp=a[j]; // and record the response
      }
    }
    return [worst, worst_resp];
  };

  /**
   * Answer a question by creating a generic response.
   * 'as' is an array of arrays, each of which is one possible response form.
   * Each response form is a list of strings or lists, which are concatenated 
   * to make a response.
   */
  my.history_answer_question_generic = function(as){
    // combine the question hash and the patient index to get a 
    // pseudorandom number.
    // the seed is the same for Every time a question is asked -- so
    // that the patient answers consistently.
    let seed = (""+as).hashCode() + my.history_patient_random_seed;
    // if this variable is set (eg for the inconsistently random-answering cases)
    // then the answer to a question will vary
    if(my.USE_RANDOM_SEED){
      seed = Math.random(); // truly random every time question asked
    }
    // choose one item from as
    var str = my.choose_random_string_from_array(as,seed);
    if(my.APPEND_TO_ANSWER){
      str += my.APPEND_TO_ANSWER;
    }
    return [ [str,0,[],[]] ]; // format it as an answer with no pathology associated.
  };
  
  /**
   * This allows us to specify a text response as a nested array.
   * The first 'level' of the array is a set of alternatives. One is randomly chosen.
   * The second level is a series of items to be concatenated.
   * The third level is random alternatives.  Subsequent levels alternate.
   * e.g.: to choose one of three possible sentences, use:
   *  [ "I feel great.", ["I am feeling ", ["fine", "good"],"."] ]
   */
  my.choose_random_string_from_array = function(as,seed){
    // if it's just a string, return that alone.
    if(typeof as === 'string'){
      return as;
    }
    // if it is an array, choose a random element.
    // note that the seed is only incremented locally, so that recursive calls receive a new seed, 
    // but repeated calls give the same seed.
    var k = Math.floor(as.length*my.seeded_rand(++seed));
    var elem = as[k];
    // if the element is a string, then just return that string
    if(typeof elem === 'string'){
      return elem;
    }else{ // but if it is an array, then concatenate them all
      var str=""; // start with a blank response
      for(var i=0;i<elem.length;i++){ // for each array element
        // send each one recursively to this function, in case it's 
        // another array (from which we need to pick randomly)
        var next = my.choose_random_string_from_array(elem[i],seed);
        // if the next item is a word without punctuation at the start, 
        if(i>0 && ( /\w/.test(next[0]) ) ){
          str += " "; // then append a space first.
        }
        str += next ;
      }
      return str;
    }
  };


  my.history_patient_random_seed = Math.random();
  /** 
   * This gets a list of answers to all possible questions
   * defined in history_metadata
   */
  my.history_all_answers = function(){
    var ans=[];
    for(var q in my.history_metadata.history){ // for each possible question in  history.js
      var as = my.history_metadata.history[q].answer; // list of generic answers
      var a=my.history_answer_question(q,as); // ask it and get the answers
      for(var i=0;i<a.length;i++){ // go through each of the answers
        if(ans[1]<1){ // is there any real pathology?
          ans=ans.concat( ans ); // if so, add the complaint to the list
        }
      }
    }
    return ans;
  };
  /**
   * This calls history_all_answers and fills a list up with the answers
   * The list is emptied first.
   */
  my.history_all_fill_list = function(li){
    var ans = my.history_all_answers(); // fetch current answers
    li.empty(); // remove any existing items in the list
    for(var i=0;i<ans.length;i++){ // for each answer
      var ai = $("<li>"+ans[i][0]+"</li>"); // create a list item
      li.appendChild(ai); // and append it
    }
  };
  /**
   * Speech interface! Call the browser API to speak the string s.
   */
  my.speak = function(s){
    s = my.preprocess_speech(s);
    s = my.process_speech_lesions(s);
    try{ // Method 1: HTML5 speech synthesis
      var ss=new SpeechSynthesisUtterance(s);
      window.speechSynthesis.speak(ss);
    }catch(exc){ // Method 2: google translate
      var au=new Audio();
      au.src = 'http://translate.google.com/translate_tts?ie=utf-8&tl=en&q='+s;
      au.play();
    }
  };
  /**
   * Call the browser API to listen for speech and translate to a question
   */
  my.listen = function(textfield){
    var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;
    var r = new SpeechRecognition();
    r.continuous=false;
    r.interimResults=true;
    r.lang="en-GB";
    r.onresult=function(e){
      var txt;
      for(var i=e.resultIndex; i<e.results.length; i++){
        if(e.results[i].isFinal){ // for a final transcript, 
          txt=e.results[i][0].transcript; // set the text box to the text
          $(textfield).val( txt );
          var ans = my.history_answer_text_question(txt); // and answer the question
          my.history_post_answer(txt, ans);
        }else{
          txt=e.results[i][0].transcript; // for interim results
          $(textfield).val($(textfield).val()+txt); // build in the text box
        }
      }
    };
    r.onend=function(e){ }; // not sure this ever happens in continuous mode
    r.start();
  };

  /**
   * Called by speak() before passing test to the speech routine and before
   * calling process_speech_lesions(). 
   * Square brackets causes nothing to be spoken (but it is still displayed).
   * Note that nesting brackets is not permitted.
   */
  my.preprocess_speech = function(s){
    // find square brackets. 
    while (s.indexOf("[")>=0){
      var i=s.indexOf("["), j=s.indexOf("]");
      if (j<i){
        console.log("invalid brackets in speech:"+s);
      }
      s=s.substring(0,i) + s.substring(j+1); // delete bracketed text
    }
    return s;
  }

  /**
   * Called by history_selector_answer_questions when showing
   * a text answer on screen. The changes made to the string s here
   * are reflected in both the screen text, and the spoken output.
   * Curly braces {} cause the content to be evaluated in js, and the result spoken.
   * Note that nesting is not permitted.
   */
  my.preprocess_speech_text = function(s){
    // find braces
    while (s.indexOf("`")>=0){
      var i=s.indexOf("`"), 
          j=s.substring(i+1).indexOf("`")+ (i+1);
      var mid = s.substring(i+1,j); // get brace contents.
      s = s.substring(0,i) + eval(mid) + s.substring(j+1);
    }
    return s;
  }
  /**
   * Called by speak() before passing text to the speech routine.
   * We modify the text depending on lesions.
   * currently implements cerebellar, oromotor and dysphasia.
   */
  my.process_speech_lesions = function(s){
    if(s.length===0){ return s; }
    // test various aspects of speech function 
    var cbm = my.testConnection("premotor-cortex-left","cerebellum"),
        lip = my.lesionAnd( // lip movement
          my.testConnection("motor-cortex-left", "lower-facial-muscles-right"),
          my.testConnection("motor-cortex-right","lower-facial-muscles-left")),
        tongue = my.lesionAnd( // tongue movement
          my.testConnection("motor-cortex-left", "tongue-right"),
          my.testConnection("motor-cortex-right","tongue-left")),
        pal = my.lesionAnd( // palatal motor control
          my.testConnection("glossopharyngeal-nucleus-left",  "palatal-muscle-left"),  
          my.testConnection("glossopharyngeal-nucleus-right", "palatal-muscle-right")),  
        consc = my.getConsciousLevel(), // conscious level
        phas  = my.getComprehensionLevel() // dysphasia
    ;
    // slurred speech for cerebellar or motor problems
    if(cbm>0.5){
      s.replace(/st/,"sh sh"); 
      s.replace(/sp/,"f");
      s.replace(/ts/,"sh");
    }
    if(lip>0.5){
      s.replace(/b/,"w");
      s.replace(/v/,"w");
      s.replace(/p/,"h");
      s.replace(/f/,"h");
    }
    if(tongue>0.5){
      s.replace(/t/,"ss");
      s.replace(/l/,"w");
    }
    if(pal>0.5){
      s.replace(/k/,"h");
      s.replace(/c[^ei]/,"h");
    }
    // do not speak if drowsy
    if(consc < 0.4 || phas < 0.6){
      s="uh";
    }
    return s;
  };
  
  /**
   * test conscious level of the patient.
   * 1 means conscious, 0 means unconscious.
   */
  my.getConsciousLevel = function(){
    return 1-my.lesionAnd( 
      my.testConnection( "medullary-reticular-formation-left",  "thalamus-left"),
      my.testConnection( "medullary-reticular-formation-right", "thalamus-right")
    );
  };
  /**
   * Test comprehension:
   */
  my.getComprehensionLevel = function(){
    return 1-my.testConnection("superior-temporal-gyrus-left","inferior-frontal-gyrus-left");
  };

  /**
   * Keep track of what is asked - so we can implement better answers!
   */
  my.log_event_to_server = function(text){
    if (!my.logging_server){ // if server parameter is empty, don't log
      return;
    }
    text = text.replace(/[^\w ?!.,]/,""); // allow only spaces and punctuation
    text = encodeURIComponent(text); // turn into a URL
    text =  my.logging_server+text; // full url
    $.ajax( {
      "async": true,
      "crossDomain": true,
      "url": text,
      "method": "GET",
      "dataType":"text",
      "headers": {
          "Access-Control-Allow-Origin":"*"
      }
    }).done(function(){
      console.log("sent "+text+" to "+my.LOGGING_SERVER_URL);
    });
  };











  
  /********************************************************************
   *                      Image slice functions                       *
   *          Manipulate positions of slices and MNI geometry         *
   *                                                                  *
   ********************************************************************/
  
  
  // keep list of all images created
  my.image_slices  = [];  
  /**
   * This is called by an image-slice component when it begins showing a new slice.
   * We must work out if we want to show something in another nav slice, and
   * if so, what.
   */
  my.sliceChanged = function(slice){
    // for each of the currently registered "nav" slices
    for(var i=0;i<my.image_slices.length;i++){
      var navslc = my.image_slices[i];
      // ensure it's a different slice than the one that has been switched to
      if(slice == navslc){ continue; }
      // now try navigation types. Does it have a DRAW_LINE attribute?
      if( navslc.navType & ImageNav.DRAWLINE) {
          // get the fiducial data for the newly selected slice, and 
          // this nav image
          var nav_c = navslc.coords,
              loc_c = slice.coords;   // contains fid and f0exey
          if(nav_c.fid === undefined){
            console.log("can't create navlines for "+navslc.svgFile);
            return;
          }
          var w     = nav_c.fid[6][2], // assume left and bottom are 0,0
              h     = nav_c.fid[6][3]; // width and height
          if(nav_c.f0exey!==undefined && loc_c.f0exey!==undefined){
            // do we know the locations of the two slices in space?
            // if so calculate the intersection line on the nav plane
            var ixn = my.getPlaneIntersection(loc_c.f0exey,nav_c.f0exey, w,h);
            // now find all the coordinates that the line intersects the 
            // nav image's edges
            var p=[];
            if(ixn.bottomx > 0 & ixn.bottomx < w) { p.push([ixn.bottomx,0]); }
            if(ixn.lefty   > 0 & ixn.lefty   < h) { p.push([0,ixn.lefty  ]); }
            if(ixn.topx    > 0 & ixn.topx    < w) { p.push([ixn.topx,h   ]); }
            if(ixn.righty  > 0 & ixn.righty  < h) { p.push([w,ixn.righty ]); }
            if(p.length!=2 || true){ 
              // DEBUG MNI COORDS?
               console.log("Intersections "+JSON.stringify(ixn, null, 2)); 
            }
            navslc.setNavLine(p);
          }else{
            navslc.setNavLine([]); // clear navlines if no fiducials
          }
      }
    }
  };
  /** find the NeuroImage slice corresponding to a DOM element */
  my.slice_for_element = function(e){
    if(e instanceof jQuery){ e=e[0]; } // un-jquery if needed
    for(var i=0;i<my.image_slices.length;i++){
      if(my.image_slices[i].node == e){
        return my.image_slices[i];
      }
    }
    throw "no NeuroImage slice corresponding to object "+e;
  };
  /**
   * This is called when a lesion slider is moved.
   * get slider position and set the model's lesion value.
   * then ask each slice to refresh its lesion display 
   */
  my.set_lesion_from_slider = function(){
    // we are called from the slider's callback, so it should be 'this'
    var l = $(this).slider("value") / 255.0;
    my.root.find(".neurosim-lesion-slider").css("background-color",
        "rgb(255,"+ 256-l   +",255)");
    my.lesions[my.selection] = l;
    my.all_displays_refresh_lesion(my.selection);
  };
  /**
   * For each slice, call the refresh_lesion() method
   * for each Exam, call updateExam();
   * Note that this is a blocking method. May need to change this
   * so that it yields and can be interrupted by any further GUI changes.
   */
  my.all_displays_refresh_lesion =  function(region){
    // first notify any images
    for(var i=0;i<my.image_slices.length; i++){
      if(region!==undefined){
        my.image_slices[i].refresh_lesion(region);
      }
    }
    // notify each of the examination panels
    for(i=0;i<my.exams.length;i++){
      my.exams[i].updateExam();
    }
    // and notify the background calculation thread (provided that it has started)
    if(my.allImagesLoaded){
      my.bgcalc.postMessage({"action":"modelChanged", "data":my.bgcalc.getConnectionData() });
    }
  };
  
  /**
   * Calculate the intersection of two planes.
   * p1 is a 3-column matrix, with column vectors f0, ex, ey for the plane.
   * w and h are the width and height of the second plane.
   * We find the points on plane 2, in plane-2-coordinates (i.e. in terms
   * of the unit vectors p2.ex,p2.ey), at which the plane p1 intersects it.
   * There are four coordinates for the intersection points:
   * the locations where ex coefficient is 0, where ey coefficient is 0,
   * and where ex coefficient is w, and ey coefficient is h.
   * These four coefficients are returned.
   */
  my.getPlaneIntersection = function(p1,p2,w,h){
    // first create a matrix with columne  [ p2,ex, p2.ey, p1.ex ]
    var m1 = new THREE.Matrix4().set( p2[1][0],p2[1][1],p2[1][2], 0,
                                      p2[2][0],p2[2][1],p2[2][2], 0,
                                      p1[1][0],p1[1][1],p1[1][2], 0,
                                      0,0,0,1),
    // then create a matrix with columne [ p2.ex, p2.ey, p1.ey ]
        m2 = new THREE.Matrix4().set( p2[1][0],p2[1][1],p2[1][2], 0,
                                      p2[2][0],p2[2][1],p2[2][2], 0,
                                      p1[2][0],p1[2][1],p1[2][2], 0,
                                      0,0,0,1);
    try{
      m1.getInverse(m1); // and invert it
    }catch(err){
      console.log(err);
    }
    // vector from origin of plane 2 to origin of plane 1
    var df0 = new THREE.Vector3().set( p1[0][0]-p2[0][0],
                                 p1[0][1]-p2[0][1],
                                 p1[0][2]-p2[0][2] );
    // now solve the linear equations by applying the inverse matrices
    // to the f0 vectors. This gives a vector containing s, t, and l.
    // s and t are the coordinates of two points on p2, where the continuations
    // of p1's x and y axes meet the plane p2. 
    // l is the corresponding number of units on the p1 axes that must be 
    // traversed to get there.
    var stl1 = df0.clone().applyMatrix4(m1),
        stl2 = df0.applyMatrix4(m2);
    // now calculate where the line between the two points (s1,t1)
    // and (s2,t2) meets the x and y axis of p2.
    var inter = {
      bottomx:   stl1.x - stl1.y * (stl2.x-stl1.x) /
                                   (stl2.y-stl1.y),
        lefty:  (stl1.y - stl1.x * (stl2.y-stl1.y) /
                                   (stl2.x-stl1.x) ),
         topx:   stl1.x + (h-stl1.y) * (stl2.x-stl1.x) /
                                       (stl2.y-stl1.y),
        righty: (stl1.y + (w-stl1.x) * (stl2.y-stl1.y) /
                                       (stl2.x-stl1.x))
    };
    return inter;
  };
  
  /**
   *  For a given html gui element, find its list of classes, and find the 
   *  first class that contains "lod-". The part of the classname after "lod-" 
   *  is treated as a number indicating the level of detail.
   *  This number is returned, or undefined if not.
   */
  my.getLodFromClasses = function(e){
    var cn = $(e)[0].className; // does the element supplied have class names?
    if(cn===undefined){ console.log("cannot find lod for "+e); return; }
    var classList = cn.split(/\s+/); // get class names
    for(var i=0;i<classList.length;i++){ // check each class on this image
      // is there a class containing lod-?
      var lodpoint = classList[i].indexOf("lod-"); 
      if(lodpoint>=0){  // if found it, 
        // get the appended integer
        var newlod = parseInt(classList[i].substring(lodpoint+4));
        if(newlod>=0 && newlod<20){ // check it's a valid level of detail (positive integer)
          return newlod;         // return LoD
        }else{                     // not a number, or out of range?
          console.log("bad lod found on "+e+": "+classList[i]);
        }
      }
    } // next class name
    return undefined; // no lod- was found in the classes
  };
  
  
  
  /****************************************************
   *                 Utility functions                *
   *              LESIONS AND CONNECTIONS             *  
   *                                                  *
   ****************************************************/
    
    
  /** 
   * remove any lesions
   */
  my.clearLesions = function(){
    my.lesions={}; // empty the set of lesions 
  };
  
  /**********
   * Decide how to draw a region named r, in terms of its "lesionedness"
   * A value of 0 means no lesion.
   * It checks for parent and child lesions too.
   */
  my.drawLesioned = function(r){
    // is r, or its parents, lesioned?
    var l = my.getParentLesionsRecursive(r);
    // are any children lesioned?
    var c = my.getAverageChildLesionRecursive(r);
    // combine lesion of item, parent average, and children average.
    l = my.lesionAnd( l, c ); 
    return l;
  };
  /** 
   * when testing when two regions are connected, 
   * does a particular region (that functions as a node) 
   * count as lesioned, and by how much?
   * (depends on the region's lesion value, and its parents'
   * and children's lesion values)
   */
  my.getLesionForConnectionEnd = function(r){
    //return my.drawLesioned(r);
    return my.getParentLesionsRecursive(r); // don't include child lesions for this
  };
  /** 
   * When testing if regions are connected, does a region
   * (that functions as a "via") count as lesioned?
   * TODO: 
   * This should potentially "and" any child lesions...
   */
  my.getLesionForConnectionVia = function(r){
    var l = my.getParentLesionsRecursive(r);
    var c = my.getProductChildLesionRecursive(r);
    return my.lesionAnd(l,c);
  };
  
  /*************
   * Calculate the average lesion of direct children.
   * This doesn't include the lesion of the item iteself.
   */
  my.getAverageChildLesionRecursive = function(r){
    if(my.entity_data[r]===undefined){
      return 0; // if we don't have any region called "r", then treat it as "unlesioned".
    }
    var tot=0; // keep track of total amount of lesion for contents
    var n  =0;
    if("c" in my.entity_data[r]){ // look at contents
      var c=my.entity_data[r].c; // c is the list of contents (list of strings)
      for(var i in c){  // for each content
        var x=0;
        if(c[i] in my.lesions){ // is it lesioned directly?
          x=my.lesions[c[i]]; // add on any lesion
        }
        // are any of its children lesioned? if so, combine it with the 
        // direct lesions. 
        x = my.lesionAnd(x, my.getAverageChildLesionRecursive(c[i])); 
        tot+=x;
        n++;
      }
    }
    if(n>0){ return tot/n; }
    else   { return 0;     }
  };
  /**
   * Get lesion of children, and treat them multiplicatively rather than 
   * taking the average. This is important when using 'via', since if any
   * child is lesioned, the whole pathway is lesioned. 
   * This is called by getLesionForConnectionVia.
   */
  my.getProductChildLesionRecursive = function(r){
    if(my.entity_data[r]===undefined){ return 0; }
    // if no region r exists, treat it as unlesioned
    var tot=0;
    if("c" in my.entity_data[r]){ // get contents
      var c=my.entity_data[r].c;
      for(var i in c){ // for each content,
        var x=0;
        if(c[i] in my.lesions){ // lesioned?
          x=my.lesions[c[i]];
        }
        // test its children too
        x = my.lesionAnd(x,my.getProductChildLesionRecursive(c[i]));
        tot = my.lesionAnd(tot,x);
      }
    }
    return tot;
  };
        
  
  
  /***********
   * Combine two lesions by an 'and', so that they lesion cumulatively.
   * What does this do?
   * For example, if the item's lesion is 0.2, and there are 2 contents 
   * of which 1 is lesioned 0.4, then the average content lesion is 0.2, and
   * that's combined with the other 0.2, to give 1-0.8*0.8 = 0.36
   * in other words, there's 80% of 80% remaining intact!
   */
  my.lesionAnd = function(x,y){ return 1-(1-x)*(1-y); };
  
  /****************
   * Test the connection between two regions.
   * If either of the regions is lesioned, or any of the 
   * 'via' pathways are lesioned, then the total amount of connection
   * will be less than 1. 
   */
  my.testConnection = function(from, to){
    var x=my.testConnectionRecursive(from,to,1,0);
    if(my.entity_data[to]!==undefined){
      for(var i in my.entity_data[to]){
        // TODO        
      }
    }
    if(x==1){
      console.log("Lesioned connection from "+from+" to "+to); 
    }
    return x;
  };
  /**
   * Recursively test whether any of the inputs to "to" are
   * equal to from, and if not, call this function on those 
   * inputs. If they are, then calculate the amount of lesion
   * in each region, including "via" nodes, and 
   * "and" them together.
   * Note that a result of 1 means there is NO connection.
   * A value of 0 means there is a full, complete connection.
   * updown indicates if we are traversing the parents 1 or children -1.
   * we shouldn't do both in the same run, because the brain is at the top of the tree!
   */
  my.testConnectionRecursive = function(from,to,val, updown){
//console.log(to);
    var e=my.entity_data[to];
    if(e===undefined){ 
      console.log("no such region "+to+" from "+from); return 1;
    }
    var foundLesion = []; // list of lesion amounts for any connections found
    var l;  // keep track of amount of lesion found, so far.
    if ("i" in e){ // does the "to" region have inputs?
      for(var input_i in e.i){  // if so, for each input,
        var input_n = e.i[input_i];
        if( my.isVerticalRelative(input_n, from) ){ // we found the route!
          // propagate backwards
          l = my.getLesionForConnectionEnd(input_n);
          //l = my.getLesionForConnectionEnd(from);
        }else{
          // are we part of the route?
          l = my.testConnectionRecursive(from,input_n,val, updown);
        }
        if(l<1){ // if we are in the route, 
          if("v" in e){ // are there 'via' nodes on the way?
            for(var via_i in e.v){  // for each "via" node
              var via_n = e.v[via_i];
              // include any lesions along the way.
              l = my.lesionAnd(l, my.getLesionForConnectionVia(via_n));
            }
          }
        }
        if(l<1) { foundLesion.push(l); }
      }
    }
    // keep track of if we're finding the path using a vertical relative.
    // if this happens, we don't need to add on the lesion of the actual
    // endpoint, later.
    var through_vert_relative = false;
    if(updown != -1){  // if we're not going downwards
      var p=NeuroSim.getDirectParents(to);
      if(p.length>0){ // any parents?
        for(var parent_i in p){
          var parent_n  = p[parent_i];
          ///if( my.isVerticalRelative(parent_n, from)){
          if( my.isParentRecursive(from,parent_n) ){
            l = my.getLesionForConnectionEnd(parent_n);
          }else{
            l = my.testConnectionRecursive(from, parent_n, val, 1); 
          } // no need to go through 'via', if it's parented. 
          //  (I think?? // TODO check that) 
          if(l<1){ 
            foundLesion.push(l);  
            through_vert_relative= true;
          }
        }
      }
    }
    // after having checked the parents and children, have we found any 
    // possible non-lesioned routes?
    if(updown != 1){ // if we're not going upwards
      if("c" in e){ // any children?
        for(var child_i in e.c){
          var child_n = e.c[child_i];
          ///if( my.isVerticalRelative( child_n, from) ){
          if( my.isParentRecursive( child_n, from ) ){
            l = my.getLesionForConnectionEnd(child_n);
          }else{
            l = my.testConnectionRecursive(from, child_n, val, -1);
          }
          if(l<1){ 
            foundLesion.push(l); 
            through_vert_relative=true;
          }
        }
      }
    }
    if(foundLesion.length>0){ 
      // return the "best" route ie with least lesion
      var l_min = Math.min.apply(null,foundLesion); 
      // and include the lesion of the endpoint (if we're not using
      // a vertical relative)
      if(!through_vert_relative){
        l_min = my.lesionAnd( l_min, my.getLesionForConnectionEnd(to) );
      }
 // console.log(from+"->"+to+"="+l_min); // DEBUG
      return l_min; 
    }else{ return 1; } // no good routes found
  }; // end testConnectionRecursive()
  
  
  /**
   * return true if a and be are related by parenthood/childhood.
   * if a==b, a is a parent of b, or b is a parent of a, return true;
   * otherwise false.
   */
  my.isVerticalRelative = function(a,b){
    if(a==b){ return true; }
    if(my.isParentRecursive(a,b)){return true;}
    if(my.isParentRecursive(b,a)){return true;}
    return false;
  };
  /**
   * Is "a" a parent of "b"? 
   * Inputs are strings. This is a helper function for isVerticalRelative.
   */
  my.isParentRecursive = function(a,b){
    if(a==b){ return true; } // if it's the same item, then yes they're related.
    var ae=my.entity_data[a]; // if not, look at the children of a
    if(ae===undefined){return false;}
    if("c" in ae){
      for(var c in ae.c){ // for each child of a
        var x = my.isParentRecursive(ae.c[c],b); // is this child a parent of b?
        if(x){ return true; } // if not, carry on the search.
      }
    }
  };
  
  
  /**
   * Find out where a "via" region gets inputs from and
   * where it sends them to 
   */
  my.getFromTo = function(o){
    var from=[];
    var to=[];
    for(var i_name in my.entity_data){
      var i_entity = my.entity_data[i_name];
      if("v" in i_entity){
        if( i_entity.v.indexOf(o) > -1 ){
          from.push( i_entity.i[0] );
          to.push( i_name );
        }
      }
      if("i" in i_entity){
        if( i_entity.i.indexOf(o) > -1 ){
          to.push( i_name );
          from.push(o);
        }
      }
    }
    return [from,to];
  };
  
  /** 
   * Get list of any direct/immediate parents of a region
   */
  my.getDirectParents = function(r){
    var par = []; // list of direct parents
    for(var i_name in my.entity_data){ // for each entity
      var i_entity = my.entity_data[i_name];
      if("c" in i_entity){ // does it have contents?
        if( i_entity.c.indexOf(r) > -1 ){ // is r in the contents?
          par.push( i_name ); // if so, add that entity to the list of parents
        }
      }
    }
    return par;
  };
  
  /** 
   * Get the lesions from parents, recursively.
   * Includes the lesion of r itself.
   */
  my.getParentLesionsRecursive = function(r){
    // is this item lesioned directly?
    var base;
    if( r in my.lesions ){
      base = my.lesions[r];
    }else{
      base = 0; 
    }
    var tot=0;
    var num=0;
    var p=my.getDirectParents(r);
    for(var i in p){ // are there direct parents?
      // include the lesion of the parents in the total
      tot += my.getParentLesionsRecursive(p[i]);
      num++; // for averaging across the various parents at this level
    }
    if(tot===0){return base;}
    else{
      if(false){
        return my.lesionAnd(base, tot/num); // combine lesions with parents lesions.
      }else{
        return my.lesionAnd(base,tot); // add up parent lesions??
      }
    }
  };
  
      
  /**
   * Given a lesion amount, form 0 to 1, give a css colour 
   * to fill the lesioned area with.
   * the result is in {property:value} format, so it can be sent
   * to a jquery.css() command.
   */
  my.getLesionColourCss = function(l){
    // lesioned areas are green (0,255,0), 
    // whereas intact areas are white (255,255,255).
    return {"fill":"rgb(0,255,0)",
            "fill-opacity":l}; 
  };
  
  
  
  /***********************************************************
   ******                   SELECTION                   ******
   ***********************************************************/
  
  /**
   * Keep track of the selected region name
   */
  my.selection = null;
  
  /**
   * Set the currently selected region.
   * This function is called by UI elements that select something, and
   * it propagates the events to interested parties.
   * 1) It searches for NeuroImages that have NavType.GOTO_SELECTION
   * 2) updates any DOM nodes with class neurosim-selection-input and 
   *    neurosim-selection-name to show appropriate text
   * 3) fills any DOM elements with classes "neurosim-selection-inputlist", 
   *    containslist, outputlist, and description, with appropriate info 
   *    about the new selection
   */
  my.setSelection = function(region_name, event_source){
    /** 
     * remove any displayed information about previous selection */
    my.root.find(".neurosim-selection-description").html("");
    my.root.find(".neurosim-selection-inputlist").empty();
    my.root.find(".neurosim-selection-containslist").empty();
    my.selection = region_name;
    /* See if we have the region in the data */
    if(my.entity_data.hasOwnProperty(region_name)){
      var data = my.entity_data[region_name];
      var fromto = my.getFromTo(region_name);
      /* update any selection name labels */
      my.root.find(".neurosim-selection-name").html(my.friendlyText(region_name));
      /* update any input boxes (as long as they didn't generate the event) */
      my.root.find(".neurosim-selection-input").each(function(){
        if(this!=event_source){  $(this).val(my.friendlyText(region_name));  }
      });
      /** Is there a description? */
      if("d" in data){
        /* update any description text boxes */
        $(".neurosim-selection-description").html(data.d);
      }

      /* If there are any inputs, set the info on them */
      if("i" in data){
        my.createList(my.root.find(".neurosim-selection-inputlist"), data.i);
      }else{
        my.createList(my.root.find(".neurosim-selection-inputlist"), fromto[0]);
      }
      if("c" in data){
        my.createList(my.root.find(".neurosim-selection-containslist"), data.c);
      }
      // TODO -- Contents list (what does a region contain?)
      if(fromto.length>0){
        my.createList(my.root.find(".neurosim-selection-outputlist"), fromto[1]);
      }
      /**
       * Now try and propagate the selection to any image controls currently 
       * displayed, and that have "NAV" 
       */
      for(var i=0; i<my.image_slices.length;i++){ // for each slice
        var slc = my.image_slices[i];
        if(slc.node != event_source     // if the event did not originate from this slice,
          && $(slc.node).is(":visible")){  // and the slice is visible,
          if( slc.navType & ImageNav.GOTO_SELECTION){ // does it want to move to the selection?
            slc.do_goto_region(region_name); // make the slice go to selection
            // The slice will automatically select the newly selected region, if needed.
          }
          if($(slc.node).hasClass("image-show-selection")){
            slc.do_change_selection(region_name);
          }
        }
      }
      /* update the lesion sliders */
      var les=my.lesions[my.selection];
      if(les===undefined){les=0;}
      try{
        my.root.find(".neurosim-lesion-slider").slider("value",255*les);
      }catch(err){console.log("couldn't set slider:"+err );}
    }else{ // entity not found in data
      console.log("Cannot find entity "+region_name);
    }
  }; // end setSelection
  /**
   * A function to create a series of <LI> within the target <UL>,
   * for each element of an array containing entity names.
   * It creates an <LI> with a click handler for each.
   * The click calls NeuroSim.setSelection, with the menu UL as the source
   */
  my.createList = function(target,list){
    target.empty();
    if(list!==undefined){
      var select_click_handler = function(evt){
        my.setSelection(evt.data, target);
        // is this item inside a 'bootbox' i.e. in a dialog?
        var bb=$(target).closest(".bootbox");
        if(bb.length>0 && evt.data!==undefined){ // and do we have a new item?
          bb.modal('hide');
          NeuroSim.generic_infobox(evt.data);
        }
      };
      for(var i in list){
        var newitem = $("<li class='btn btn-primary' data-value="+list[i]+">"+my.friendlyText(list[i])+"</li>");
        newitem.on("click",null, list[i], select_click_handler);
        newitem.appendTo(target);
      }
    }
  };

  /*************************************************************
   *
   * General Utility functions
   * for manipulating entities, text and graphics 
   *
   ************************************************************/
  
  /**
   * get summary text from wikpedia into a string, 
   * sent to the callback function.
   */
  my.getWikipediaSummary = function(s,callback){
    s=s.replace( / /g, "_" );
    if(s.endsWith("left")) {s=s.substring(0,s.length-5);}
    if(s.endsWith("right")) {s=s.substring(0,s.length-6);}

    var q="https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="+encodeURI(s)+"&redirects=1";
    $.ajax({
      type:"POST",
      url:q,
      crossDomain: true,
      dataType: "jsonp",
      xhrFields: { withCredentials: true },

      success: function(d){
        if ("query" in d){
          var p=d.query.pages,
              t=p[Object.keys(p)[0]].extract;
          callback(t);          
        }else{
          console.log("wikipedia returned "+d+", for query '"+s+"'");
        }
      }
    });
  };
  /** 
   * get summary from wikipedia, after trimming the text to something sensible
   */
  my.getWikipediaSummaryFreeText = function(s,callback){
    my.getWikipediaSummary( my.simplify_free_text(s), callback);
  };
  
  /**
   * Take some free text, e.g. an MCQ stem, and try and pare it down to 
   * something that might be indexed.
   */
  my.simplify_free_text = function(s){
    s=s.toLowerCase().trim();
    if(s.startsWith("left")){  s=s.substring(5);}
    if(s.startsWith("right")){ s=s.substring(6);}
    // remove anything after "and" or "with" 
    if( / and /.test(s)){       s=s.split( / and /); s=s[0];     }
    if(/ with /.test(s)){       s=s.split(/ with /); s=s[0];     }
    s=s.split(/[(<>)]/)[0]; // remove anything after / within brackets.
    return s;
  };
  
  
  /** convert a variable name into a human-readable string */
  my.friendlyText = function(s) {     // make labels "friendly"
    s=s.split("-").join(" ");   // replace hyphens with spaces
    s=s.split("_").join(" "); // replace underscores with spaces
    return s[0].toUpperCase() + s.slice(1); // capitalise first letter
  };
  
  /**
   * like string.join(), but runs "friendlyText" on each item 
   * before calling join 
   */
  my.joinFriendly = function(l,d){
    var l2=[];
    for(var li in l){
      l2.push(my.friendlyText(l[li]));
    }
    if(d!==undefined){return l2.join();}
    else{ return l2.join(d);}
  };
  
  /** 
   * decribe an entity - short description. Either its synonyms, or its
   * contents, inputs, or outputs.
   * (currently used by autocompletion) 
   */
  my.describe = function(o){
    var d="";
    if("s" in o){
      d+="("+my.joinFriendly(o.s)+")";
    }else if("c" in o){
      d+="="+my.joinFriendly(o.c,"+");
    }else if("i" in o){
      d+="input:"+my.joinFriendly(o.i);
    }else{
      var fromto = my.getFromTo(o);
      if(fromto[0].length>0){
        d+="from:"+my.joinFriendly(fromto[0]);
      }
      if(fromto[1].length>0){
        d+="to:"+my.joinFriendly(fromto[1]);
      }
    }
    return d;
  };
  
  /**
   * Show a box with info on the string x.
   * if 'x' is the name of a region, then show the 
   * region inputs/outputs, and its wikipedia summary.
   * Otherwise see if it has a wikipedia summary,
   * and show appropriate search links.
   * This is called from menu action About_this_region.
   */   
  my.generic_infobox = function(x){
    if(x===undefined || x===null || x.length===0){
      return; // we were given a null item to describe
    }
    // get a 'friendly' name suitable for the title of the box
    var f = NeuroSim.friendlyText(x);
    // web-encoded names: underscores or plusses as separators?
    var uscore = f.replace(/ /g, "_");
    if(x in NeuroSim.entity_data){ // if it's a region,
      // make the name L/R-neutral
      if(f.endsWith("left")) {f=f.substring(0,f.length-5);}
      if(f.endsWith("right")) {f=f.substring(0,f.length-6);}
    }
    var x_uri = encodeURI(f); // get format that is suitable for search URLs
    // create a button bar for getting further info about items
    var buttons = "<div class='btn-group'>";
    buttons += "<a class='btn btn-primary'  href='http://en.wikipedia.org/wiki/Special:Search/"+encodeURI(uscore)+"' target='_blank'>Wikipedia</a>";
    buttons += "<a class='btn btn-primary' href='http://www.google.com/search?q="+x_uri+"' target='_blank'>Google</a>";
    buttons += "<a class='btn btn-primary' href='https://www.ncbi.nlm.nih.gov/pubmed/?term="+x_uri+"&sortby=relevance' target='_blank'>PubMed</a>";
    buttons += "</div>";
    var bb      = bootbox.alert(f);  // the bootbox element. create with dummy text.
    var bb_body = $(bb).find(".bootbox-body");
    bb_body.html(""); // remove the dummy text

    // look in our entity_data. Test if  x is a region?
    if(x in NeuroSim.entity_data && NeuroSim.entity_data[x]!==null){
      var data   = NeuroSim.entity_data[x]; // get the entity data
      var fromto = NeuroSim.getFromTo(x); // and its inputs / outputs
      // title  - make an input-form snippet
      var title = NeuroSim.case_snippets.find(".snippet-select-typing").clone();
      bb_body.append( title );
      title.find("input").val(f).addClass("neurosim-input-title");
      
      /*
      text = "<h3 class='image-selection-name'>"+f+"</h3>"; // title of box
      */
      var text = "";
      text += buttons; // add info buttons
      text += "<div>"; // now list the inputs, contents and outputs.
      text += "<div class='hide-empty'>Input:<ul class='neurosim-selection-inputlist hide-empty'></ul></div>";
      text += "<div class='hide-empty'>Contains:<ul class='neurosim-selection-containslist hide-empty'></ul></div>";
      text += "<div class='hide-empty'>Output:<ul class='neurosim-selection-outputlist hide-empty'></ul></div>";
      var parents = NeuroSim.getDirectParents(x);
      if(parents.length>0){
        text+="<div class='hide-empty'>Part of:<ul class='neurosim-selection-parentlist hide-empty'></ul></div>";
      }
      text += "</div><div class='wikipedia-summary hide-empty'></div>";
      if(x in NeuroSim.visibility.by_region){
        text += "Visibility: " + NeuroSim.visibility.by_region[x];
      }
      bb_body.append($(text));
      
      // now create the box as a dialog, using BootBox

      // add the typing box and the lesion slider directly as html elements
      // taken from the snippets file.

      $(bb).find(".bootbox-body").append( NeuroSim.case_snippets.find(".snippet-lesion-slider").clone() );

      // create graph of connections?
      // note this has to be done after the box has been created and added.
      var cg = $("<div class='neurosim-connection-graph'></div>");
      bb.find(".bootbox-body").append(cg);
      my.createConnectionsGraph(x, cg, 3, 
        my.GRAPHDIRS.CHILD | my.GRAPHDIRS.FROM | my.GRAPHDIRS.TO | my.GRAPHDIRS.PARENT, 6);
      
      
      // fill in the wikipedia asynchronously
      NeuroSim.getWikipediaSummaryFreeText(f,function(y){
        $(bb).find(".wikipedia-summary").append($("<p/><p>"+y+"</p>"));
      });
      // fill in the connection lists 
      if(data!==undefined && "i" in data){// any direct inputs?
        NeuroSim.createList($(bb).find(".neurosim-selection-inputlist"), data.i);
      }else{ // otherwise look for indirect inputs
        NeuroSim.createList($(bb).find(".neurosim-selection-inputlist"), fromto[0]);
      }
      if(data!==undefined && "c" in data){ // any contents?
        NeuroSim.createList($(bb).find(".neurosim-selection-containslist"), data.c);
      }
      /*   @TODO -- Contents list (what does a region contain?) */
      if(fromto.length>0){ // any outputs?
        NeuroSim.createList($(bb).find(".neurosim-selection-outputlist"), fromto[1]);
      }
      if(parents.length>0){
        NeuroSim.createList($(bb).find(".neurosim-selection-parentlist"), parents);
      }
    }else{ // not a region
      text = "<h3 class='image-selection-name'>"+f+"</h3>";
      text += buttons;
      text += "<div class='wikipedia-summary'></div>";
      bb_body.append($(text));
      NeuroSim.getWikipediaSummaryFreeText(f,function(y){
        if(y!==undefined){
          $(bb).find(".wikipedia-summary").append($("<p/><p>"+y+"</p>"));
        }else{
          // not found in wikipedia!
          $(bb).find(".wikipedia-summary").append($("<p/><p>No info</p>"));
        }
      });
    }
    /** Create any active components inside the dialog too */
    NeuroSim.initialise_gui_element( bb );
  };

  my.GRAPHDIRS = { CHILD:1, PARENT:2, FROM:4, TO:8 };
  /**
   * Use vis.js to create a graph of connections?
   * x: root node entity. e: jquery element to create it within
   * depth: max recursion depth for nodes of graph
   * dirs: directions in which to move from root node entity
   * (a bitwise combination of GRAPHDIRS)
   * lod: level of detail at which to hide entities (show only visibility<=lod)
   */
  my.createConnectionsGraph = function(x,e, depth, dirs, lod){
    var d=my.entity_data[x];
    // create a network
    var data = {
      nodes: [],
      edges: []
    };
    if(dirs & my.GRAPHDIRS.CHILD){
      my.create_node_recursive(x,function(x){return my.entity_data[x].c;},data,   depth, {edge:{arrows:"to"},lod:lod});
    }
    if(dirs & my.GRAPHDIRS.PARENT){
      my.create_node_recursive(x,function(x){return my.getDirectParents(x);},data,depth, {edge:{arrows:"from"},lod:lod});
    }
    if(dirs & my.GRAPHDIRS.FROM){
      my.create_node_recursive(x,function(x){return my.entity_data[x].i;},data, depth, {edge:{arrows:"from"},lod:lod});
    }
    if(dirs & my.GRAPHDIRS.TO){
      my.create_node_recursive(x,function(x){return my.getFromTo(x)[1];}, data, depth, {edge:{arrows:"to"},lod:lod});
    }
    /* Options for the vis.Network */
    var options = {    
      width:"100%", height:"100%",
      edges: {
        smooth: {
          type: 'continuous'
        }
      },
      nodes:{ shape: 'box' },
      layout: {
        hierarchical:{
          enabled:true ,
          direction:"RL",
        }
      },
      physics:{
        hierarchicalRepulsion:{
          nodeDistance:35,
          springLength:30
        }
      }
    };
    try{
      var network = new vis.Network(e[0], data, options);
      network.on('select', function (params) {
        $(".image-selection-name").html(params.nodes);
        my.createConnectionsGraph(params.nodes[0],e,depth,dirs);
      });

      setTimeout(function() {
        network.fit();
      }, 2000);
    }catch(err){
      console.log(err);
    }
    return e;
  };// end createConnectionGraph
  
  /**
   * x is the name of an entity,
   * f(x) is a function that gets the list of names of another entities
   * related to a specified entity.
   * This function calls f on x, to get a list of related entities, then
   * iterates through them, adding each one to the graph
   */
  my.create_node_recursive = function(x,f,data, depth, opts){
    depth-=1; // go down one more recursion depth
    if(depth<0){ return; } // if depth exceeded, don't go any further.
    // check the visibility of this node
    if(  !(x in NeuroSim.visibility.by_region) 
       ||  NeuroSim.visibility.by_region[x] > opts.lod )
    
    // first create a node for x
    // ensure it's not already added?
    var already_there=false;
    for(var j=0;j<data.nodes.length;j++){
      if(data.nodes[j].id === x){ already_there=true; }
    }
    if(!already_there){
      data.nodes.push({
        id:x, label:my.friendlyText(x),
        level: depth,
        color: 'rgba(192,' + Math.max(0,Math.min(255, 255-depth * 50)) + ',255,1)'
      });
    }
    // now create a list of related nodes, according to f
    var list=f(x);
    if(list===undefined || !Array.isArray(list) || list.length===0){
      return;
    }
    for(var i=0;i<list.length;i++){ // iterate through list
      var xi=list[i];
      if(xi!== undefined && xi in my.entity_data){ // check if it's an entity 
        my.create_node_recursive(xi,f,data, depth, opts); // if so, create a new node (recursively)
        var edge = {from:x, to:xi}; // make an edge to it 
        Object.assign(edge, opts.edge); // add options for edges
        data.edges.push(edge); // and create the edge.
      }
    } // next list item
  }; // end create_node_recursive
  
  
  
  /**
   * Reflect the left-right to the opposite side, given a string representing
   * the name of a region. Eg: pterygoid-left --> pterygoid-right.
   * Note this only reflects the final side in the name (relevant for retina!)
   */
  my.flipLR = function(s){
    if(/-left$/.test(s)){ // left becomes right
      return s.substring(0,s.length-4)+"right";
    }else if(/-right$/.test(s)){ // right becomes left
      return s.substrung(0,s.length-5)+"left";
    }else return s; // non-lateralised - no change
  };
  /** Get a random number from a seed */
  my.seeded_rand = function(seed){
    var x=Math.sin(seed)*10000;
    return x-Math.floor(x);
  };
  /** Create a hash code from a string */
  String.prototype.hashCode = function() {
    var hash = 0, i, chr, len;
    if (this.length === 0) return hash;
    for (i = 0, len = this.length; i < len; i++) {
      chr   = this.charCodeAt(i);
      hash  = ((hash << 5) - hash) + chr;
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  };
  

  /**
   * try and interpolate two paths, specified using svg path strings
   * ( i.e. the "d" attribute ). Paths a and b must have the same number
   * of points.
   * It linearly interpolates each point between the two paths, using 
   * the fraction specified (range 0 to 1 where 0 indicates path a
   * and 1 indicates path b).
   * Node select allows just some of the nodes to be modified - 
   * [start_index_inclusive, end_index_exclusive]  
   */
  my.interpolatePath = function(a,b,frac,nodesel){
    a=a.split(/[, ]/);       // obtain the elements of the path string.
    b=b.split(/[, ]/);
    if(a.length!=b.length){  // ensure the paths have same number of points
      console.log("paths cannot be interpolated: '"+a+"' and '"+b); return "";
    }
    var d=[];                    // empty path for storing result
    for(var i=0;i<a.length; i++){ // work our way through the path
      var af = parseFloat(a[i]),  // see if we can get numbers for this point
          bf = parseFloat(b[i]);
      if(isNaN(af)){         // for letters in the path
        if(!isNaN(bf)){      // ensure it's a letter in both paths
          console.log("paths inequivalent "+a+","+b); return "";
        }
        d.push(a[i]);        // preserve the letter and continue
        continue;
      }else{                 // for numbers in the path
        if(nodesel!==undefined && (i<nodesel[0] || i>=nodesel[1]) ){
          d.push(a[i]); 
        }else{
          var v = frac*bf + (1-frac)*af; // linear interpolation
          d.push( v.toString() );   // store result as string
        }
      }
    }
    return d.join(" ");      // convert back to a "d" attribute
  };
        
        
  /********************************************************************
   *                  Background calculation thread                   *
   *         initialise and pass data to- and from- the thread        *
   ********************************************************************/
  
  /**
   * This creates a worker thread that does the gui updates as required.
   * It can receive messages via postmessage, e.g. when the GUI changes,
   * and responds by sending callback messages when the GUI needs updating.
   */
  try{
    my.bgcalc = new Worker("neurosim-background-calculations.js");
  }catch(excp){
    my.bgcalc = {};
    console.warn("Unable to create background thread - check permissions?");
  }
  // keep a dictionary of registered callback functions - that can be called by the 
  // background calculation function
  my.bgcalc.callbacks = {};
  my.bgcalc.onmessage = function(e){
    // this bit means that if ethe worker sends an event with e.callback as a string,
    // that string is looked up in 'callbacks', and the corresponding function is called
    // with the event e.
    if(e.data.callback in my.bgcalc.callbacks){
      my.bgcalc.callbacks[e.data.callback](e.data);
    }
  };
  my.bgcalc.callbacks.setConnectionRequests = function(data){
    /**
     * this message is sent by the calculation thread after it has started, 
     * so that we know which connections need to be tested when 
     * the model changes. (The background calcuation thread does not have
     * direct access to the lesion or model).
     * We will pass these connections strengths back to the calculation 
     * thread every time the model changes, in all_displays_refresh_lesion().
     * That method calls bgcalc.getConnectionData().
     */
    my.bgcalc.requestedConnections = data.connections;
    // now send the current state of the model back to the bg thread
    my.bgcalc.postMessage({"action":"modelChanged", "data":my.bgcalc.getConnectionData() });
  };
  /**
   * dict containing current connection data values,
   * which will be serialised and passed to the bgcalc threead 
   */
  my.bgcalc.connectionData={}; 
  my.bgcalc.getConnectionData = function(){
    /** 
     * This function is called by all_displays_refresh_lesion(), when the 
     * lesion model changes. This method tests the connections previously requested  
     * by setConnectionRequests, and resturns the values required.
     */
    for( var i in my.bgcalc.requestedConnections ){ // for each field requested,
      // regions is either [from, to], or an array of [[from,to],[from,to],...]
      var regions = my.bgcalc.requestedConnections[i];  
      if(!(regions[0] instanceof Array)){  // if just one pair requested,
        //test the connection and store the result
        my.bgcalc.connectionData[i] = NeuroSim.testConnection(regions[0],regions[1]);
      }else{                               // there is an array of connections to test 
        var d=[];                          // blank array for result
        for(var j=0;j<regions.length;j++){ // for each item in the array
          d.push( NeuroSim.testConnection( regions[j][0], regions[j][1] ) );
        } // test each connection and fill new array with the connection values
        my.bgcalc.connectionData[i] = d;   // store the array in the required field
      }
    }
    return my.bgcalc.connectionData;
  };
  
  
  

  /************************************************************
   *             END OF NEUROSIM MODULE                       *
   ************************************************************/
  
  return my;
}();









/****************************************
 *
 *    NeuroImage
 * 
 *    Module to create an image.
 *    There may be many of these created.
 *
 * Recognised classes:
 *       image-slice              : required to create the svg from data-slice attribute
 *       image-allow-hover        : mouse causes regions to light up (see css .image-selected-region)
 *       image-allow-panzoom      : add zoom control, drag to pan, wheel to zoom
 *       image-show-selection     : selected region lights up
 *       image-show-image         : without this, hides the background svg image
 *       image-show-regions       : all regions visible and overalaid 
 *       image-show-lesions       : lesioned regions shown in colour
 *       image-show-nonfunctional : without this, regions that are not in the model are hidden
 *       image-show-tooltips      : mouse movement shows name of region in bubble
 *       image-show-3dcursor      : vert/horz lines at mouse cursor, and MNI values in corner
 *       neurosim-resizeable      : add drag handles at bottom/right 
 *       image-nav-goto-selection : when selection changes, try find a slice in current series.
 *       image-nav-draw-line      : when a slice is changed in another image, show the line for that slice here
 *       image-nav-draw-alllines  : when a series is changed in another image, show all lines for that series here
 *       neurosim-quiz-question / answer : this image functions as part of a quiz
 *       image-lod-5              : set the initial level of detail for elements in this slice
 *       image-forbid-selection   : cannot click to select (useful for quizzes)
 *       image-left-is-left       : use neurological convention rather than radiological for L/R
 8       image-fullscreen         : prevent using normal SVG size, so the SVG can be fullscreen
 *
 ****************************************/

var NeuroImage = function(node) {
  var my={};
  my.node = node;
  

  /* SVG file name property */
  my.svgFile = '';
  my.lastZoom = 1;//undefined;
  my.lastPan  = {x:0,y:0};//undefined;
  my.navType = ImageNav.NONE; // how do we navigate?
  my.lod = 10; // initial level of detail
  my.BOUNDED_PAN = false; // should we prevent panning outside the edge of the image?
  /**
   * isLoaded - becomes true when the SVG is loaded and regions are processed. 
   * used to determine when all exams loaded, so we can start the model updates
   */
  my.isLoaded = false; // 
  
  /**
   * When initialising the page, update the SVG and create the menus 
   */
  my.initialise = function(){
    $(function() {  
      // create modality menu using the modalities in slice_metadata.json
      $(my.node).find(".image-menu .image-menu-modality").empty();
      var modalities = Object.keys(NeuroSim.slice_metadata.modalities);
      for (var i=0; i<modalities.length; i++){
        $(".image-menu .image-menu-modality", my.node).append("<li>"+NeuroSim.friendlyText(modalities[i])+"</li>");  
      }
      // create level of detail menu using the levels of detail in hierarchy.json
      var lodmenu  = $(my.node).find(".image-menu .image-menu-lod");
      lodmenu.empty();
      for(i=0;i<NeuroSim.visibility.levelnames.length;i++){ // add each level of detail by name
        lodmenu.append("<li data-action='lod"+i+"'>" + 
                       NeuroSim.friendlyText(NeuroSim.visibility.levelnames[i]) +
                       "</li>");
      }
      
      
      // generate menu 
      // first wrap the contents of each menu item in its own div (needed for new jquery menus)
      $(my.node).find(".image-menu" ).menu(); // then generate the menu      
      // next, add menu handlers
      $(my.node).find(".image-menu" ).on("menuselect",function(evt,ui){
        // for the action, see if there's an attribute called "data-action"
        var action = $(ui.item.context).attr("data-action");
        // if not, just use the text in the UI menu 
        if(action === undefined){
          action=ui.item.context.textContent.trim();
        }
        my.process_menu_action(action);
      });
      
      // generate buttons
      $(my.node).find(".image-buttons .btn").click(function(e){
        // get the command from the data-action attribute of the button element.
        my.process_menu_action( $(this).attr("data-action") );
      });
      
      // Create the planes icons
      $(my.node).find(".icon-plane").load("axis-icons.svg",function(){
        $("path",this).attr("style",""); // clear all styles
        $("g", this).hover(function(){
          $(this).toggleClass("icon-plane-active");
        }).click(function(){ // when clicked, try to switch planes?
          var id = $(this)[0].id;
          if(id.length>4){
            var plane = id.substring(id.length-3);
            my.do_set_type(plane);
          }
        });
      });
      
      // Create the modality icons
      $(my.node).find(".icon-modality").load("modality-icons.svg",function(){
        $("g rect",this).attr("style",""); // clear all styles
        //$("g",this).hover(function(){
      //    $("rect",this).toggleClass("icon-modality-active");
        //});
        $("g",this).click(function(){ // when clicked, try to switch modality?
          var id = $(this)[0].id; // the top-level groups have ID's that are modality names
          var modality = id;
          my.do_set_modality(modality);
        });
      });
      
      
      // check if we have any navigation classes - draw-line, goto-selection
      my.navType = ImageNav.NONE;
      if($(my.node).hasClass("image-nav-draw-line")){
        my.navType |= ImageNav.DRAW_LINE;
      }
      if($(my.node).hasClass("image-nav-goto-selection")){
        my.navType |= ImageNav.GOTO_SELECTION;
      }
      
      // get the file name attribute for this image
      var slicefile = $(my.node).attr("data-slice");
      if(slicefile){
        my.setSvgFile( slicefile );
      }else{
        if($(my.node).hasClass("image-slice")){
           console.log(".image-slice tag does not have a valid data-slice attribute");
        }
      }
    });
    
    /** Let NeuroSim know we've been created, so that it can send us messages if things change */
    if(NeuroSim.image_slices.indexOf(my)<0){ // have we already been registered?
      NeuroSim.image_slices.push(my); // if not, add myself
    }
    
    /** now jump to lesions, if required */
    if($(my.node).hasClass("image-gotolesion")){
      // ensure it has 'showselection', and Nav goto selection, otherwise the jump wont work
      $(my.node).addClass("image-showselection");
      my.navType = ImageNav.GOTO_SELECTION;
      // get the first item in the list of lesions. If there are multiple lesions,
      // this will be a random one.
      var les = Object.keys(NeuroSim.lesions)[0];
      if(les!==undefined){ // if we found one,
        NeuroSim.setSelection(les); // call the NeuroSim setSelection to broadcast it.
      }else{
        console.warn("no lesion to show");
      }
    }
    
  }; // end initialise()
  /**
   * Register a function to be called once the SVG has been loaded and processed.
   * These are triggered
   */
  my.addImageLoadedListener = function(f){
    my.loadListeners.push(f);
  };
  /** 
   * List of listeners registered to be called after SVG loaded
   */
  my.loadListeners = [];
  
  my.vector3ToText = function(v){
    var rnd = function(x){return Math.floor(x*1)/1;};
    return "["+rnd(v.x)+","+rnd(v.y)+","+rnd(v.z)+"]";
  };
  
  /* set the property SVG filename, and update the image */
  my.setSvgFile = function(file){
    if(file===undefined || file===null || file.length===0){
      console.log("null slice selected");
      return;
    }
    my.svgFile = file;
    if(NeuroSim.isLoaded){
      my.updateSvg();
    }else{
      NeuroSim.registerLoadListener(my.updateSvg);
    }
    NeuroSim.root.find(".neurosim-selection-slice").val(file);
  };
  my.wait = {"load_count":0, 
             "code":"<a class='wait-spinner'><i class='fa fa-refresh fa-3x'></i></a>"};
  
  /**
   * When we call 'fit' when the image element has been resized 
   % e.g. from very  small at the start, then the zoom parameter
   * is not valid for use on future images. So, this tells us not to 
   * store it in my.lastZoom.
   * At the moment, it starts true, because we may not be correctly
   * be sized at the start. May also be set to true when an item is made
   * visible.
   */
  my.ignoreCurrentZoom=true;
  
  /*******
   *
   * updateSvg: Load the SVG, style it appropriately, and add event handlers to it 
   * Usually called from setSvg().
   * 
   */
  my.updateSvg = function(){
    // get the last zoom and pan position
    if("panzoom" in my ){ // ( if we've already got a panzoom )
      if(!my.ignoreCurrentZoom){
        my.lastZoom = my.panzoom.getZoom();
        my.lastPan  = my.panzoom.getPan();
      }else{
        // next time, the zoom value should be reliable.
        my.ignoreCurrentZoom=false;
      }
    }
    // then try and load in the SVG file into the "svg" node
    my.image_svg = $(my.node).find('> .image-svg');
    if(my.image_svg.length===0){
      console.warn("no image-svg directly within this image node");
    }
    // construct the "waiting" spinner
    if(my.wait.spinner===undefined){ 
      my.wait.spinner = $(my.wait.code);
    }
    $(my.node).append(my.wait.spinner); // add the waiting spinner to the NeuroImage div
    my.wait.load_count ++; // inform that another image was requested
    
    // Perform substitutions if required:
    // current modality = mts.m
    var mts = NeuroSim.all_slice_lookup[ my.svgFile ];      
    var sliceinfo;
    my.actualSvgFile = my.svgFile;
    if(mts!==undefined){ 
      sliceinfo =  NeuroSim.slice_metadata.svgs[mts.m][mts.t][mts.s];
      if("actualfile" in sliceinfo ){ // was an "actual slice" provided?
        my.actualSvgFile = sliceinfo.actualfile;
      }
    }
    // hide all tooltips currently shown (they might get left onscreen if 
    // an element is removed while its tooltip is showing)
    my.image_svg.find(".image-region").tooltip("hide");
    my.image_svg.find(".image-region").tooltip("disable");
    
    
    // Now do the loading
    my.image_svg.load(my.actualSvgFile, function(response, status, xhr){
      // Process loaded image svg file
      my.wait.load_count --; 
      if(my.wait.load_count>0)return; // cancel loading files if another file has been requested since then.
      /**  @TODO this system always shows the last svg to load - not necessarily the 
       *   last one that was requested. */
      
      my.wait.spinner.remove();
      if (status=="error"){
        this.html("Unable to load "+my.svgFile);
        my.isLoaded=true; // allow the application to start despite failure to load
        return;
      }
      /** Now initialise the new SVG. Should this be done immediately? 
       *  or after a delay? */
      //my.image_svg.find("path").attr("style","");
      //window.setTimeout(function(){
      my.processNewSvg(my.image_svg, sliceinfo);
      my.isLoaded = true;
      // call each of the load listeners
      for(var i in my.loadListeners){
        my.loadListeners[i]();
      }
      //},0);
    });// end of load(svg)
  }; // end of updateSvg()
  /**
   * Called after a new SVG has loaded (and had time to be placed in the window).
   * 1. Setup pan zoom environment
   * 2. Substitute names of regions when using linked SVGs (eg left/right; c2->c3)
   * 3. Ensure image defs unique
   * 4. call recomputePaths()
   * 5. call calculateCoordinates()
   * 6. set selected region from model
   * 7. inform NeuroSim a new slice has been loaded (eg for notifying navigation images)
   * 8. add 3D cursor.
   * This function is called by updateSvg before any external loadListeners.
   */
  my.processNewSvg = function(image_svg, sliceinfo){
    
    /** For the pan zoom, it needs the actual svg item. So find it */
    var svgelement;
    svgelement=$(image_svg).children()[0];
    /** Should the mouse wheel zoom or change slice? */
    var wheel_zooms = $(my.node).hasClass("image-wheelzooms");
    // do we need to set up pan/zoom?
    if($(my.node).hasClass("image-allow-panzoom")){
      my.panzoom = svgPanZoom(svgelement, {
          zoomEnabled: true,
          mouseWheelZoomEnabled:wheel_zooms,
          center:      true,
          fit:         true,
          minZoom:     0.1,
          maxZoom:     20,
          beforePan:   my.beforePan,
          controlIconsEnabled: true,
          preventMouseEventsDefault: false,
          customEventsHandler:{ // panzoom events: on init, destroy, couple to hammer
            haltEventListeners:['touchstart','touchend','touchmove','touchleave','touchcancel'],
            init:function(options){
              var instance = options.instance, // instance of svgPanZoom
                  panX=0, panY=0, scale=0;
              this.hammer = Hammer(options.svgElement,{
                inputClass:Hammer.SUPPORT_POINTER_EVENTS ? Hammer.PointerEventInput : Hammer.TouchInput });
              this.hammer.get('pinch').set({enable:true});
              this.hammer.on('doubletap',function(ev){ instance.zoomIn(); });
              // handle pan
              this.hammer.on('panstart panmove',function(ev){ 
                if(ev.type === 'panstart'){   panX=0;panY=0;  }
                instance.panBy({x:ev.deltaX-panX, y:ev.deltaY-panY});
                panX=ev.deltaX; panY=ev.deltaY; 
              });
              // handle pinch
              this.hammer.on('pinchstart pinchmove',function(ev){
                if(ev.type === 'pinchstart'){
                  scale=instance.getZoom();
                  instance.zoom(scale*ev.scale);
                }
                instance.zoom(scale*ev.scale);
              });
              // panning should not move the page
              options.svgElement.addEventListener('touchmove',function(e){ e.preventDefault();});
            }, // end init
            destroy:function(){ this.hammer.destroy(); }
          }
        } ); // end create svgPanZoom()

      if(my.lastZoom !== undefined){ // we already have a panzoom element, so restore the old zoom level
        my.panzoom.resize(); // update svg cahced sizes
        my.panzoom.zoom(my.lastZoom);
        my.panzoom.pan(my.lastPan);
      }
    } // end if allow panzoom
    // NOTE: 
    // after the panzoom has been created, all paths now reside 
    // inside a 'viewport' svg group.
    
    /** If the image-wheelzooms class is present, then the mouse wheel will zoom (default
     * behaviour for svg-panzoom). Otherwise, we want the panzoom */
    if(!wheel_zooms){
      $(svgelement).bind('mousewheel DOMMouseScroll', function(event){
        if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
          my.do_slice_up();   // make the wheel navigate slices up
        } else {
          my.do_slice_down(); // or down.
        }
        return false; // don't scroll page
      });
    }else{  // wheel zooms. handled by svgPanZoom. don't allow the page to scroll
      $(svgelement).bind('mousewheel DOMMouseScroll', function(event){return false;});
    }
    if($(my.node).hasClass("image-fullscreen")){
      $(svgelement).attr("width","100%");
      $(svgelement).attr("height","100%");
    }
    
    
    
    // substitute path names if required
    if(sliceinfo!==undefined && "replace" in sliceinfo){ // is there a "replace" clause in the metadata?
      var regexps=[]; // compile the regular expressions into a list of RegExp objects
      for(var i=0;i<sliceinfo.replace.length;i++){
        regexps.push( new RegExp(sliceinfo.replace[i][0]) );
      }
      $(svgelement).find("path, g").each(function(){ // now for each path or group
        if(this.id.startsWith("viewport")){return;}
        // run the replacement on the ID. Note there is a list of replacements, to be done in order.
        for(var i=0;i<regexps.length;i++){ 
          this.id = this.id.replace(regexps[i], sliceinfo.replace[i][1]); 
        }
      });
    }
    // for axial and coronal images, 
    // now reverse left and right if using radiological convention
    // if main image node has class 'image-left-is-left', then don't change it
    if(!$(my.node).hasClass("image-left-is-left") && //if we're not using left-is-left,
       (my.svgFile.indexOf("axi")>=0 || my.svgFile.indexOf("cor")>=0)){ // and it's coronal / axial,
       $(svgelement).find("path,g").each(function(i,e){ // for each path
         if(this.id.indexOf("-left")>=0){               // if its id contains "-left"
           this.id = this.id.replace(/-left/,"-right"); // replace with "-right"
         }else if(this.id.indexOf("-right")>=0){        // and vice versa
           this.id = this.id.replace(/-right/,"-left");           
         }
       });
    }
    

    // ensure uniqueness of "defs" and "use". If multiple SVGs are displayed, 
    // several svgs may use the same name for their images.
    var uses = $(svgelement).find("use");
    $(svgelement).find("defs image").each(function(){ // for each defined image under defs,
      var old_id    = this.id, 
          unique_id = "img"+Math.floor(Math.random()*10000); // create a random id
      this.id = unique_id; // change the id to the new one,
      for(var i=0;i<uses.length;i++){ // for each "use", 
        // does it have a href attribute that points to this image definition?
        if("baseVal" in uses[i].href && uses[i].href.baseVal == "#"+old_id){
          // if so, update the image use to point to this definition's new name
          uses[i].href.baseVal = "#"+unique_id;
        }else if(typeof uses[i].href === "string" && uses[i] == "#"+old_id){ 
          uses[i].href = unique_id; 
        }
      } // next use of that image
    });


    my.mniCoords=[];      
    my.recomputePaths();
    if($(my.node).hasClass("image-slice")){ // for image-slice elements, 
      my.calculateCoordinates();            // calculate their MNI coordinates using my.mniCoords
    }
    if(my.navType & ImageNav.GOTO_SELECTION){
      my.do_change_selection(NeuroSim.selection);
    }
    // Let NeuroSim know that the slice has changed and the coordinates updated,
    // so that any navigation images that follow us can be updated.
    NeuroSim.sliceChanged(my);
    var root; // keep track of root SVG element.
    /**
     * Set up 3D Cursor if needed
     */
    if($(my.node).hasClass("image-show-3dcursor")){
      my.navType |= ImageNav.THREED_CURSOR;
      // find the root SVG element, for adding lines
      if($(my.node).hasClass("image-allow-panzoom")){
        root = $(my.node).find("svg > g.svg-pan-zoom_viewport");
      }else{
        root = $(my.node).find("svg");
      }
      // has a crosshair already been created in this image? If not,
      if(!("node" in my.coords)){ // create the text for the 3d coords
        my.coords.node = $("<div class='image-nav-coords'></div>");
        $(my.node).append(my.coords.node);
        // now create the crosshair in the SVG
        var width  = root[0].getBBox().width, height=root[0].getBBox().height;
        var xhw = 20; // crosshair width (size of gap)
        my.coords.svgxline = $($.parseXML( // draw 4 lines
          '<g xmlns="http://www.w3.org/2000/svg">'+
          '<path xmlns="http://www.w3.org/2000/svg" class="image-nav-3dcursor" d="m 0,'+(-height)+' 0,'+(height-xhw)+' z"></path>'+
          '<path xmlns="http://www.w3.org/2000/svg" class="image-nav-3dcursor" d="m 0,'+(+xhw)+' 0,'+(height-xhw)+' z"></path>'+
          '<path xmlns="http://www.w3.org/2000/svg" class="image-nav-3dcursor" d="m '+(-width)+',0 '+(width-xhw)+',0 z"></path>'+
          '<path xmlns="http://www.w3.org/2000/svg" class="image-nav-3dcursor" d="m '+(+xhw)+',0 '+(width-xhw)+',0 z"></path></g>'
          ).documentElement);
      } // done creating new bits
      my.coords.svgxline.appendTo(root); // add the crosshair to the new SVG

      // get mouse events relative to zoom viewport, if it exists
      if($(my.node).hasClass("image-allow-panzoom")){ 
        root = $(my.image_svg).find(".svg-pan-zoom_viewport");
      }else{ // otherwise just use the whole svg (unclear if this works)
        root = my.image_svg;
      }
      // 3d cursor
      // create space for processing the SVG point
      my.temp_point = $(my.node).find(".image-svg svg")[0].createSVGPoint();
      root.mousemove(function(evt){ // when the mouse moves
        // transform the mouse into SVG coordinates
        my.temp_point.x=evt.clientX; my.temp_point.y=evt.clientY;
        var svgpt = my.temp_point.matrixTransform(this.getScreenCTM().inverse());
        my.coords.svgxy = [svgpt.x, svgpt.y];
        my.coords.cursor = new THREE.Vector3(1,my.coords.svgxy[0],my.coords.svgxy[1]).applyMatrix4( my.coords.f0exey_matrix );
        my.coords.node.html(my.vector3ToText(my.coords.cursor));
        $(my.coords.svgxline).attr("transform","translate("+svgpt.x+","+svgpt.y+")");
      });
    } // end of image-show-3dcursor
  };// end of processNewSvg
      
  
  /** 
   * Custom pan to limit pan-zoom to edges
   */
  my.beforePan = function(oldPan, newPan){
          var 
              // Computed variables
              sizes = this.getSizes();
          var gutterWidth  = 10,
              gutterHeight = 10;
          if(my.BOUNDED_PAN){
            gutterWidth  = sizes.width;
            gutterHeight = sizes.height;
          }
          var leftLimit = -((sizes.viewBox.x + sizes.viewBox.width) * sizes.realZoom) + gutterWidth,
              rightLimit = sizes.width - gutterWidth - (sizes.viewBox.x * sizes.realZoom),
              topLimit = -((sizes.viewBox.y + sizes.viewBox.height) * sizes.realZoom) + gutterHeight,
              bottomLimit = sizes.height - gutterHeight - (sizes.viewBox.y * sizes.realZoom);
          var customPan = {};
          customPan.x = Math.max(leftLimit, Math.min(rightLimit, newPan.x));
          customPan.y = Math.max(topLimit, Math.min(bottomLimit, newPan.y));
          //if(leftLimit>rightLimit){customPan.x=oldPan.x;}
          //if(topLimit>bottomLimit){customPan.y=oldPan.y;}
          return customPan;
  };
  
  /** 
   * This converts an id string of the form
   * id="mni_12_153_23" into a vector [12,153,23]
   */
  my.getMNI = function(s){
    // get part after 'mni' and segment it by underscores
    var coords = s.substring(4).split("_");
    if(coords.length<3){ // check there are x,y,z values
      console.error("invalid mni coordinate "+s);
      return undefined;
    }
    return coords.map(parseFloat); // covert each string to numbers and return an array of values
  };

  /**
   * Retrive a set of jQuery elements for the regions currently
   * shown on this image slice. Note this only works after 
   * recomputePaths() has been called.
   */
  my.get_regions = function(){
    return $(my.node).find('.image-svg .image-region');
  };

  /** 
   * RECOMPUTE PATHS: 
   * This is called to reset which  paths in an svg are attributed as paths. 
   * It works out what is visible in each slice.
   */
  my.recomputePaths = function(){
    if(NeuroSim.entity_data === undefined){
      console.log("computepaths: Entities not yet loaded...");
      return;
    }

    // find all paths and groups within the main layer
    // previously looked for "svg > path , svg > g > path", 
    // which would include all items in groups too. 
    // now, this depends on whether there is a "panzoom" wrapper viewport group.
    var path_specifier;
    if($(my.node).hasClass("image-allow-panzoom")){
      my.path_specifier = "svg > g > path, svg > g > g";
    }else{
      my.path_specifier = "svg > path, svg > g";
    }
    var regions = my.image_svg.find(my.path_specifier); 
    
    // check for default LoD setting in classes
    var newlod = NeuroSim.getLodFromClasses(my.node);
    if(newlod!==undefined){
      my.lod = newlod;         // set new LoD
    }
    
    // now, decide on style / visibility of each region in the slice
    regions.each(function(i,e){ 
      // does it have an 'id' that matches an entity?
      // or does it contain a hyphen, and we're showing nonfunctional entities too?
      
      if(e.id.startsWith("mni_")){      // look out for mni coords. 
        // for these fiducials, hide the item but record values
        my.mniCoords.push( my.getMNI(e.id) );    
        $(e).hide();
        return;
      }
      
      // no hyphens in name of region: it must be a structural element of the picture!
      // don't change the style for these items.
      // If there's no minus sign, and it's not in entity_data, then it's not an entity.
      if(e.id.indexOf("-")==-1 && NeuroSim.entity_data[e.id]===undefined){ return; }
      
      if(e.id.indexOf("svg-pan-zoom")>-1){ return; } // ignore svg-pan-zoom elements!
      
      var elem_lod = NeuroSim.visibility.by_region[e.id],
          lod_exceeded =  elem_lod > my.lod, // this will be false if the region lod-level is undefined
          is_entity = e.id in NeuroSim.entity_data,
          show_non_functional = $(my.node).hasClass("image-show-nonfunctional");
      if(   ( is_entity && !lod_exceeded) ||
            (!is_entity && show_non_functional) ) { 
        $(e).addClass('image-region');  // if so, make it an image-region 
        $(e).removeClass("image-hide-region");
        e.setAttribute("style","");     // Also, clear any local path styles.
        if(e.tagName=="g"){             // if it is a group, 
          $(e).find("path").attr("style","");  // then also clear the styles of any child paths
        }
      }else{
        // otherwise, if it is a region but we're not showing nonfunction entities:
        $(e).removeClass('image-region'); // make it invisible
        e.setAttribute("style","");     // Also, clear any local path styles.
        if(e.tagName=="g"){             // if it is a group, 
          $(e).find("path").attr("style","");  // then also clear the styles of any child paths
        }
        $(e).attr("title","");
        $(e).attr("data-original-title","");
        var parent;
        // now if the LoD is exceeded, group items into the higher levels of detail.
        // Get name of a region that is a parent of the current region, 
        // but is in the current level of detail, and has the highest
        // possible value.
        if(lod_exceeded && (parent=my.getFirstParentWithLod(e.id)) !== null ) {
          // Are there any visible parents?
          var pnode = $(my.node).find("#"+parent);
          // If the parent is a group, all is fine.
          // But sometimes, the parent is already drawn as a path.
          // if the parent is a path, we can't just put the current element 'inside' - we need 
          // to first create a group.
          // then we put both the parent-path and the current-path inside that group.
          var create_parent_anyway = (pnode.length>0 && pnode.prop("tagName")==="path"); 
          var old_pnode; // in that case, also keep track of the parent element
          if(create_parent_anyway){ 
            // if the parent is a path, remove the parent element from the tree
            old_pnode = pnode;
            pnode.detach(); // but preserve its attributes, as we're going to add it again, inside the new group
          }
          // now create the parent group if necessary
          if(pnode.length===0 || create_parent_anyway){ // does such a parent exist yet?   If not:
            // create a new SVG group, with the name of this parent
            //pnode = $(my.node).find("svg g").append("<g id='#"+parent+"'></g>");
            var xml = $.parseXML('<g xmlns="http://www.w3.org/2000/svg" class="image-region" id="'+ parent +'"></g>');
            pnode = $(xml.documentElement);
            // where to attach the new parent group?  get the first group in the first svg within
            // the main image-svg element.
            var svgroot = $(".image-svg svg > g", my.node)[0];
            if(svgroot===undefined){ // no "g"? just attach a new 'g' to the root, as the container
              // create group xml, convert to jquery
              var new_root_group_xml = $.parseXML('<g xmlns="http://www.w3.org/2000/svg" class="root-for-groups"></g>');
              var new_root_group_node = $(new_root_group_xml.documentElement);
              var main_svg_root = $( $(".image-svg svg",my.node)[0] ); //find the main svg root
              new_root_group_node.appendTo(main_svg_root); // attach the new group to the main svg root
              svgroot = $(".image-svg svg > g", my.node)[0]; // find the newly created root group
            }
            pnode.appendTo($(svgroot)); // attach the parent to the root
            if(create_parent_anyway){ // was there a path with the parent's name already?
              old_pnode.appendTo($(pnode)); // if so, add it to the new group
            }
          }
          // Move the current element into the parent group.
          // It should now highlight and select as a block.
          try{
            $(e).appendTo(pnode);
          }catch(excp){
            console.log("unable to group "+e.id+" inside "+pnode.selector);
            console.log(excp);
          }
          console.log(e.id+" is in "+parent);
        }else{
          // Either LoD is not excceded but the hyphenated item is not an entity, 
          // or the LoD has been exceeded and there are no parents that are 
          // within the LoD.
          // Therefore, completely hide the item.
          $(e).addClass("image-hide-region");
        }
      } // end if nonfunctional
    }); // end for each region
    
    // add mouse-over listener to highlight the areas
    my.image_svg.find('.image-region').mouseenter(function(){
        $('.image-hover-name').html( NeuroSim.friendlyText($(this).attr("id")) );
    });
    // and mouse-click listener to manage selection
    my.image_svg.find('.image-region').click(function(){ // if a region is clicked,
      // check if click-selection is permitted
      if($(my.node).hasClass("image-forbid-selection")){ return; }
      my.do_change_selection(this.id); // change the selection on this slice
      // and notify NeuroSim to propagate this to other UI components that
      // want to follow the selection.
      NeuroSim.setSelection($(this).attr("id"), my.node); 
    });
    my.image_svg.find('.image-region').each(function(){  // for each region, add tooltip text
      if ($(my.node).hasClass("image-show-tooltips")){
        var title = NeuroSim.friendlyText( this.id );
        $(this).attr("title",title);
        $(this).attr("data-original-title",title); // this is for bootstrap tooltips. Not needed for jquery.
      }else{ // if we don't have 'show-tooltips', remove any.
        $(this).attr("title","");
        $(this).attr("data-original-title","");
      }
    }); // end of each region
    my.image_svg.find('.image-region').tooltip({ // set up tooltip position and animation
      position:{my:"left bottom", at:"right top-15"},
      hide:{effect:"",duration:0},   // make tooltip vanish instantly
      track:false  // and ask it to follow mouse?
    });
    // now look for other "title" items (sgm 2020)
    my.image_svg.find(':has(> title)').tooltip({ // set up tooltip position and animation
      position:{my:"left bottom", at:"right top-15"},
      hide:{effect:"",duration:0},   // make tooltip vanish instantly
      track:false  // and ask it to follow mouse?
    });


    // recolour paths if we have that option selected?
    if(my.isRecoloured){ my.recolourRegions(1);}
    // set checkboxes / buttons based on classes of the node
    my.update_ui();
    my.refresh_all_lesions();
  };  // end function recomputePaths()
    
  my.coords={};
  my.coords.f0exey=undefined; 
  /**
   * Calculate the f0, ex and ey coordinates for this image.
   * These are all in mni space, and represent: 
   *   f0 - the bottom left coordinate of the image
   *   ex - the size and direction of an x-pixel on the image, as a MNI vector
   *   ey - the size and direction of a y-pixel.
   */
  my.calculateCoordinates = function(){
    // ensure we have 3 MNI coords
    if(my.mniCoords.length!=3 && false){
      console.log("MNI coords not present");
      return;
    }
    if(NeuroSim.slice_metadata===undefined){
      console.log("tried to initialise image before slice metadata loaded");
    }
    // ensure we have the fiducials from slice metadata
    var fid = NeuroSim.slice_metadata.fiducials[my.svgFile];
    if(fid===undefined || fid.length<6){
      console.log("Image fiducial coords not present for "+my.svgFile);
      my.coords.f0exey = undefined;
      return;
    }
    my.coords.fid=fid;
    // in fact we don't need both of these. This is just to check.
    var m = new THREE.Matrix4();
    // Matrix4.set uses "Row-major" order!
    //    m.set( 1,1,1,0, 
    //           fid[3][0], fid[4][0], fid[5][0], 0,
    //           fid[3][1], fid[4][1], fid[5][1], 0,
    //           0,0,0,1);
    m.set( 1,fid[3][0], fid[3][1], 0,
           1,fid[4][0], fid[4][1], 0,
           1,fid[5][0], fid[5][1], 0, 
           0,  0,        0,        1 );
    
    m.getInverse(m); // set m to the inverse of m
    
    var f0exey_x = new THREE.Vector3( fid[0][0], fid[1][0], fid[2][0] ).applyMatrix4(m),
        f0exey_y = new THREE.Vector3( fid[0][1], fid[1][1], fid[2][1] ).applyMatrix4(m),
        f0exey_z = new THREE.Vector3( fid[0][2], fid[1][2], fid[2][2] ).applyMatrix4(m);
    my.coords.f0exey = [ [ f0exey_x.x, f0exey_y.x, f0exey_z.x ],
                         [ f0exey_x.y, f0exey_y.y, f0exey_z.y ],
                         [ f0exey_x.z, f0exey_y.z, f0exey_z.z ] ];
    my.coords.f0exey_matrix = new THREE.Matrix4();
    var w=my.coords.f0exey;
    // Create a matrix with columns f0, ex, and ey.
    // This is useful for transforming image coords into MNI.
    my.coords.f0exey_matrix.fromArray( [ w[0][0],w[0][1],w[0][2],0,  
                                         w[1][0],w[1][1],w[1][2],0,
                                         w[2][0],w[2][1],w[2][2],0,
                                         0,0,0,1 ]);
  };
  
  
  /**
   * For a region named r, which is not visible at the current LoD,
   * find a parent of the element which has the maximum
   * level of detail equal or lower to the current LoD.
   * -- this gets called recursively to look at higher levels of parent 
   * Returns null if none found.
   */
  my.getFirstParentWithLod = function(r){
    if(NeuroSim.visibility.by_region[r] <= my.lod){ return r; }
    var found = [];
    // go through direct parents and see if any are within LoD
    var parents=NeuroSim.getDirectParents(r);
    for(var i in parents){
      if(NeuroSim.visibility.by_region[parents[i]] <= my.lod) {
        found.push(parents[i]);
      }
    }
    if(found.length>0){ return found[0]; }
    for(i in parents){
      var result = my.getFirstParentWithLod(parents[i]);
      if(result!==undefined && result!==null && result.length>0){
        found.push(result);
      }
    }
    if(found.length>0){ return found[0]; }
    else{ return null; }
  };
  
  
  /**
   * Function to recolour all regions
   */
  my.recolourRegions = function( do_recolor ){
    // for each image-region in the image-svg,
    $(my.node).find(".image-svg").find(".image-region").each(function(i,e){
      if(do_recolor){
        $(e).attr("style","fill-opacity:"+my.RECOLOUR_OPACITY+";fill:"+my.hsv_to_rgb(Math.random(), 0.5, 0.95) );
      }else{ // clear the colouring
        $(e).attr("style","");
      }
    });
  };
  my.RECOLOUR_OPACITY = 0.6;
  my.isRecoloured = false;
  
  
  /**
   * get RGB colour from h s and v
   * borrowed from http://martin.ankerl.com 2009 
   * Converts it to a CSS rgb string
   */
  my.hsv_to_rgb = function(h, s, v){
    var h_i = Math.floor(h*6),
        f = h*6 - h_i,
        p = v * (1 - s),
        q = v * (1 - f*s),
        t = v * (1 - (1 - f) * s), rgbv, integers;
    if(h_i===0){
      rgbv = [v, t, p];
    }else if(h_i==1){
      rgbv = [q, v, p]; 
    }else if(h_i==2){
      rgbv = [p, v, t];
    }else if(h_i==3){
      rgbv = [p, q, v ];
    }else if(h_i==4){
      rgbv = [t, p, v];
    }else if(h_i==5){
      rgbv = [v, p, q];
    }
    integers =  rgbv.map(function(x){
      return Math.floor(x*256);
    });
    return "rgb("+integers.map(String).join(",")+")";
  };
  
  /* menu handler for this slice */
  my.process_menu_action = function(action){
    /** turn spaces to underscores in the action command
     *  (important because the actions may come from the menu text,
     *   yet may refer to modalities etc)
     */
    if(action === undefined){ return; }
    action = action.trim().replace(/ /g , "_");
    /** Does the dataset have a modality called 'action'? */
    if(action in NeuroSim.slice_metadata.svgs){ 
      my.do_set_modality(action);
      return;
    }
    /** Does the current modality have a type called 'action'? */
    var mts = NeuroSim.all_slice_lookup[ my.svgFile ];      // current modality = mts.m
    if(action in NeuroSim.slice_metadata.svgs[mts.m] ){
      my.do_set_type(action);
      return;
    }
    if(action.startsWith("lod")){ // select new level of detail?
      var oldlod = my.lod;
      // remove any classes on the main image node that specify a lod
      // otherwise they will override the new setting.
      var classList = $(my.node)[0].className.split(/\s+/);
      for(var i=0;i<classList.length;i++){ // check each class on this image
        // is there a class beginning with image-lod?
        if(classList[i].startsWith("image-lod-")){ 
          $(my.node).removeClass(classList[i]); // if so, remove it
        }
      }
      // read the new LOD from the end of the command
      my.lod = Number(action.substring(3));
      if(my.lod<=oldlod){ // is it becomming less detailed?
        my.recomputePaths(); // we can move items to subgroups
      }else{ // otherwise we're becoming more detailed.
        my.updateSvg(); // need to reload the SVG from scratch.
      }
      // now update the lod indicators
      my.update_lod_indicators();
      return;
    }
    
    switch (action){
      case "Next":
        my.do_slice_down();
        break;
      case "Previous":
        my.do_slice_up();
        break;
      case "Show_lesions":
        my.toggle_class("image-show-lesions");
        break;
      case "Show_hover":
        my.toggle_class("image-allow-hover");
        break;
      case "Show_nonfunctional":
        my.toggle_class("image-show-nonfunctional");
        my.recomputePaths();
        break;
      case "Show_labels":
        my.toggle_class("image-show-tooltips");
        my.recomputePaths();
        break;
      case "Show_image":
        my.toggle_class("image-show-image");
        break;
      case "In":
        my.panzoom.zoom(1.5);
        break;
      case "Out":
        my.panzoom.zoom(0.75);
        break;
      case "Show_regions":
        my.toggle_class("image-show-regions");
        break;
      case "Show_selection":
        my.toggle_class("image-show-selection");
        my.recomputePaths();
        break;
      case "Show_3d_cursor":
        my.toggle_class("image-show-3dcursor");
        my.setSvgFile(my.svgFile); // needs full re-initialisation to create the line elements
        break;
      case "Recolour":
        my.recolourRegions( my.isRecoloured = !my.isRecoloured );
        break;
      case "About_this_modality":
        // get metadata for this modality
        var mod = NeuroSim.slice_metadata.modalities[mts.m];
        var text = "<h3>" +NeuroSim.friendlyText(mts.m)+"</h3>";
        if("resource" in mod){
          text += "<br>Taken with permission from <i>"+mod.resource+"</i>";
        }        
        if("reference" in mod){
          text += "<br>"+"Reference: "+mod.reference;
        }
        if("credits" in mod){
          text += "<br>"+mod.credits;
        }
        if("link" in mod ){
          text += "<br><a href='"+mod.link+"' target='_blank'>"+mod.link+"</a>";
        }
        bootbox.alert(text); 
        break;
      case "About_this_region":
        if(NeuroSim.selection==null){return;}
        NeuroSim.generic_infobox(NeuroSim.selection);
        break;
      case "About_this_slice":
        // List of regions in this slice
        var reg  = NeuroSim.slice_metadata.regions_by_slice[my.svgFile];
        text = "<h1>Slice regions</h1><ul class='list-group'>";
        var toolt=""; // keep tooltip text
        if(reg!==undefined){ // show regions
          for(var i=0;i<reg.length;i++){ /// for each region in this slice,
            // create a list item for it
            text += "<li class='list-group-item'>"+NeuroSim.friendlyText(reg[i]);
            // check its visibility and add a number
            var vis=NeuroSim.visibility.by_region[reg[i]];
            if(vis!==undefined){
              text += "<span class='tag tag-default tag-pill pull-xs-right'>"+vis+"</span>" ;
            }
            // does it have any parents? note them
            var par = NeuroSim.getDirectParents(reg[i]);
            if(par!==undefined && par.length>0){
              toolt = par.join(" , "); 
              text += "<span class='tag tag-default tag-pill pull-xs-right' title='"+toolt+"'>"+"p"+"</span>" ;
            }
            // does it have any inputs or outputs? note them too.
            var fromto = NeuroSim.getFromTo(reg[i]);
            if(fromto!==undefined && fromto[0].length>0){
              toolt = fromto[0].join(" , ");
              text += "<span class='tag tag-default tag-pill pull-xs-right' title='"+ toolt +"'>"+"i"+"</span>" ;
            }
            if(fromto!==undefined && fromto[1].length>0){
              toolt = fromto[1].join(" , ");
              text += "<span class='tag tag-default tag-pill pull-xs-right' title='"+toolt+"'>"+"o"+"</span>" ;
            }
            // does the region have children? show that.
            if(NeuroSim.entity_data[reg[i]]!==undefined){
              var chil = NeuroSim.entity_data[reg[i]].c;
              if(chil!==undefined){
                toolt = chil.join(" , ");
                text += "<span class='tag tag-default tag-pill pull-xs-right' title='"+toolt+"'>"+"c"+"</span>" ;
              }
            }
            text += "</li>"; // end the list item
          } // next region in this slice
        }else{ text += "no regions defined in Slice Metadata json"; }
        text += "</ul>";
        if(my.coords.f0exey!==undefined){ // Do we have coordinates?
          // Table of coordinates for this image, based on fiducials, obtained in my.coords
          text += "<h1>Slice coordinates</h1>";
          text += "<table class='neurosim-number-table'><tr><th>(mm)</th><th>x</th><th>y</th><th>z</th></tr>";
          var xyztable = function(val){ // quick function to create x,y,z table
            var s=""; // convert to 2 dp
            for(var i=0;i<3;i++){  s += "<td>"+0.01*Math.floor(100*my.coords.f0exey[val][i])+"</td>"; }
            return s;
          };
          text += "<tr><th>Origin</th>" + xyztable(0) + "</tr>";
          text += "<tr><th>X-axis</th>" + xyztable(1) + "</tr>";
          text += "<tr><th>Y-axis</th>" + xyztable(2) + "</tr>";
          text += "</table>";
        } else {
          text += "No MNI fiducials defined for this slice";
        }    // end of coordinates
        var bb=bootbox.alert(text); // show the "About this slice" dialog.
        break;
      default:
        console.log("no such command '"+action+"'");
    }// end switch action
  }; // end of process_menu_action
  
  /** 
   * Called when the Level of Detail changes.
   * Find any elements that are supposed to indicate the lod, and 
   * re-style them with the class "image-lod-selected", depending on the current value of
   * my.lod
   */
  my.update_lod_indicators = function(){
    for(var i=0;i<=9;i++){ // for each level of detail, 0 to 9
      // look for any indicator elements
      var indic = $(my.node).find(".image-lod-indicator[data-action=lod"+i+"]");
      if(i==my.lod){ // depending on our currently selected lod, add or remove the 'selected' class.
        indic.addClass("image-lod-selected");
      }else{
        indic.removeClass("image-lod-selected");
      }
    } // next level of detail
  };
    
    
  /******
   * Slice navigation functions
   ******/
  
  /**
   * returns an object with fields 
   * 'm': modality of slice, 
   * 't': type of slice (eg 'axial')
   * 's': slice number, within the type series
   * 'pos': position of slice 
   * From these numbers, 
   * NeuroSim.slice_metadata.svgs[m][t][s] gives the slice metadata Object
   */
  my.getCurrentSliceLocation = function(){
    // get the modality, type and slice number of the current svg.
    return  NeuroSim.all_slice_lookup[ my.svgFile ]; 
  };
  /**
   * Returns the current slice's metadata, which contains 'pos' and 'file' values.
   */ 
  my.getCurrentSliceMetadata = function(){
    var mts = my.getCurrentSliceLocation();
    //  Look up the list of slice numbers for the current modality, in the metadata
    var slice = NeuroSim.slice_metadata.svgs[mts.m][mts.t][mts.s]; 
    return slice;
  };
  my.do_slice_down = function(){
    my.moveSlice(+1);
  };
  my.do_slice_up   = function(){
    my.moveSlice(-1);
  };
  /**
   * Called to scroll up or down to a new slice within the same 
   * series (modality and axis)
   */
  my.moveSlice = function(increment){
    // get the modality, type and slice number of the current svg
    var mts = NeuroSim.all_slice_lookup[ my.svgFile ]; 
    if(mts===undefined){ // the image is not listed in the slice-metadata - e.g. custom image 
      return;
    }
    // list of slices for the current modality and type. 
    // This is a dictionary keyed by slice "number".
    var slicelist = NeuroSim.slice_metadata.svgs[mts.m][mts.t];
    // what is the current slice number? Look up in the slice metadata
    var i = mts.s; // which index in the slice list are we on?
    var newi = i + increment;
    if (my.CYCLING){ newi=newi%slicelist.length; }
    if (newi<slicelist.length && newi>=0){ // is the increment possible?
      // find the name of the next slice down (i.e. i+1)
      var newslice = slicelist[ newi ];
      if ( newslice===undefined ){
        console.log("Invalid slice found"+mts.m+mts.t+slicelist[newi]);
      }
      my.setSvgFile(newslice.file);
    }
  };
  /**
   * Specify whether going past the end of the slices in this set will go back
   * round to the start
   */
  my.CYCLING = false;
  /** toggle a class and update the UI */
  my.toggle_class = function(cls){
    if( $(my.node).hasClass(cls) ){
      $(my.node).removeClass(cls);
    }else{
      $(my.node).addClass(cls);
    }
    my.update_ui();
  };
  /**
   * This function should take the classes attributed to the node, and
   * update the interface (buttons, menu checks etc) from that 
   */
  my.update_ui = function(){
    var toggle_commands ={
      "image-show-regions"  : "Show regions",
      "image-allow-hover"   : "Show hover",
      "image-show-tooltips" : "Show labels",
      "image-show-image"    : "Show image"
    };
    for(var c in toggle_commands){
      var button  = $(my.node).find(".image-buttons [data-action='" + toggle_commands[c] + "']");
      var eyeicon = $(my.node).find(".image-buttons [data-action='" + toggle_commands[c] + "'] .corner-icon");

      if($(my.node).hasClass(c)){
        button.removeClass("icon-highlight");
        eyeicon.addClass("fa-eye").removeClass("fa-eye-slash");
      }else{
        button.addClass("icon-highlight");
        eyeicon.removeClass("fa-eye").addClass("fa-eye-slash");
      }
    }
    /*  Corner icon code for jQuery UI (removed when using bootstrap)
    if($(my.node).hasClass("image-show-regions")){
      // set checks for show-regions
      $(my.node).find(".image-buttons [data-action='Show regions'] .corner-icon")
        .addClass("fa-eye").removeClass("fa-eye-slash");
    }else{
      // clear checks for show-regions
      $(my.node).find(".image-buttons [data-action='Show regions'] .corner-icon")
        .addClass("fa-eye-slash").removeClass("fa-eye");
    }*/
  };
  
  /**
   * Switch to a new modality.
   * If possible, preserve the axis/plane, and if possible get the current selection to 
   * be visible or select a slice that is in a similar location, to the current slice
   */
  my.do_set_modality = function(modality){
    var mts   = NeuroSim.all_slice_lookup[ my.svgFile ]; // get current modality / type
    var types = NeuroSim.slice_metadata.svgs[modality];
    var newtype; // starts as undefined
    for(var t in types){ // look to see if the current type is also in the new modality
      if(t == mts.t){
        newtype = t;
      }
    }
    var found=false;
    if(newtype !== undefined){ // did we find a corresponding plane?
      // set the slice from the best match in the same plane
      found = my.setSlice_BestInList( types[newtype] );
    }
    if(!found){ // no corresponding plane, or not found it corresponding plane?
      // set the slice from the best match of all planes
      var allslices = [];
      for(t in types){ // add all slices from all types for the new modality 
        for(var s in types[t]){ 
          allslices.push(types[t][s]);  //to a single list
        }
      }
      my.setSlice_BestInList(allslices);
    }
  };
  /**
   * Switch to a new plane in the same modality.
   */
  my.do_set_type = function(type){
    var mts      = NeuroSim.all_slice_lookup[ my.svgFile ]; // get current modality / type / slice
    var typelist = NeuroSim.slice_metadata.svgs[ mts.m ];
    var slices   = typelist[type];
    if( slices === undefined ){
      console.log("invalid plane "+type);
    }else{
      // change to the new plane
      my.setSlice_BestInList(slices);
    }
  };
  /**
   * When changing to a new set of slices, should we find 
   * a slice that contains the current selection?
   */
  my.CHANGE_TO_SELECTED = true;
  
  /**
   * This is used when changing modality, to find an appropriate slice in the new modality.
   * This finds a slice in the slist of slices (from slice_metadata)
   * that is the best one to switch to, and sets the current svg accordingly
   * return true if found a good slice
   */
  my.setSlice_BestInList  = function(slices){
    if(slices.length===0){
      console.log("no slices of this type");
      return false;
    }
    var newslice = -1; // new slice to switch to 
    var i; // for iterator
    // should we try to find a slice containing the selected region?
    if(my.CHANGE_TO_SELECTED && NeuroSim.selection!==undefined){
      var reglist; // region list
      for(i=0; i<slices.length;i++){ // for each of the new possible slices
        // get the list of regions in that slice
        reglist = NeuroSim.slice_metadata.regions_by_slice[ slices[i].file ];
        // does this list contain the currently selected region?
        if( reglist!==null && reglist!== undefined && 
             NeuroSim.selection!==null && NeuroSim.selection!==undefined &&
             reglist.indexOf(NeuroSim.selection) >-1  ){
          newslice=i; // if so, log the slice index
          break; // and exit the for loop
        }
      } // we haven't found a slice with the exact region on it.
      for(i=0;i<slices.length;i++){ // So: go through the slices a second time,
        reglist = NeuroSim.slice_metadata.regions_by_slice[ slices[i].file ];
        if(reglist!==null && reglist!==undefined && NeuroSim.selection!==null && NeuroSim.selection!==undefined){
          for(var j=0;j<reglist.length;j++){ // go through each possible region on each slice
            if(NeuroSim.isVerticalRelative(reglist[j],NeuroSim.selection)){
              newslice=i;  // if the region is a vertical relative (parent/child) of the selection,
              break; // then accept this as the best slice, and change to it.
            }
          }
        }
      }
    } // end if 
    if( newslice== -1 ) {
      /**
       * If we didn't find a slice with the current selection,
       * change to the slice which has the nearest position to the current slice
       */
      // position of current slice
      var mts = NeuroSim.all_slice_lookup[ my.svgFile ]; // get current modality / type / slice
      var old_pos = NeuroSim.slice_metadata.svgs[mts.m][mts.t][mts.s].pos;
      // now find the nearest slice in the new list, i.e. the smallest distance
      var minimum_distance = Number.MAX_VALUE; 
      for(i=0; i<slices.length;i++){ // for each of the possible new  slices
        var new_pos = slices[i].pos;
        var dist = Math.abs(new_pos - old_pos);
        if (dist<minimum_distance){ // are we closer than any other slice so far?
          newslice = i; minimum_distance = dist; // if so, store this slice as the nearest so far.
        }
      }
    }
    // have we found an appropriate slice? 
    if(newslice == -1){
      console.log("couldn't find any appropriate slices");
      newslice = 0; // if not, select the first one of the new modality.
      return false;
    }
    // now switch to it.
    my.setSvgFile(slices[newslice].file);
    return true;
  }; // end setSlice_BestInList
  
  my.navLine = undefined;
  /**
   * Create a nav line (a line on the image) that corresponds to a pair of coordinates
   * that define the start and endpoints of the line.
   * If a nav line already exists, remove it and create a new one.
   */
  my.setNavLine = function(c){
    // if c is empty, then remove the existing navline.
    if(c===undefined || c.length<2){
      if(my.navLine !== undefined){
        my.navLine.remove(); 
        my.navLine = undefined;
      }
      return;
    } // otherwise draw a navline:
    // no nav lines in existence yet?
    if(my.navLine===undefined){
      // create a new path object 
      var xml = $.parseXML('<path xmlns="http://www.w3.org/2000/svg" class="image-nav-line"/>');
      my.navLine = $(xml.documentElement);
      var root;
      if($(my.node).hasClass("image-allow-panzoom")){
        root = $(my.node).find("svg > g.svg-pan-zoom_viewport");
      }else{
        root = $(my.node).find("svg");
      }
      my.navLine.appendTo(root);
      
      // my.navLine = $(my.node).find("svg g").append("<path class='image-nav-line'/>");
    }
    // vector from point 1 to point 2
    var linevec = [ c[1][0]-c[0][0], c[1][1]-c[0][1] ];
    var width=0.01; // proportion of line length to use as thickness of rectangle.
    // set the coordinates of the nav line
    if(false){ // make a rectangle?
      my.navLine.attr("d","m " + c[0][0] + "," + c[0][1] + " " +
                      linevec[0] + "," + linevec[1] + " " +
                      width*linevec[1] + "," + -width*linevec[0] + " " +
                      -linevec[0] + "," + -linevec[1] + " z");
    }else{ // make a simple line
      my.navLine.attr("d","m " + c[0][0] + "," + c[0][1] + " " +
                      linevec[0] + "," + linevec[1] + " z");
    }
  }; // end setNavLine
  
  my.do_goto_region = function(r){
    if(r===undefined){return;}
    // find slices in current series
    var mts    = NeuroSim.all_slice_lookup[ my.svgFile ];
    var slices = NeuroSim.slice_metadata.svgs[mts.m][mts.t]; 
    var foundVerticalRelative;
    // go through all the slices
    for(var i=0;i<slices.length;i++){
      var regs = NeuroSim.slice_metadata.regions_by_slice[slices[i].file];
      // do we have a list of regions in that slice?
      if(regs!==undefined){ 
        if(regs.indexOf(r) >=0 ){ // if so, does the slice contain the desired region?
          my.setSvgFile( slices[i].file ); // if so, change to that slice
          return; // and stop
        }
        // there was no region by that name in that slice
        for(var j=0;j<regs.length;j++){ // for each region in that slice
          if(NeuroSim.isVerticalRelative( regs[j], r )){
            foundVerticalRelative = slices[i].file;
          }
        }
        
      } // next region in that slice
    } // next slice
    // if we get here, there is no slice in the current series that contains the selection.
    // So: now try for parents or children of the desired region.
    if(foundVerticalRelative!==undefined){
      my.setSvgFile(foundVerticalRelative);
    }
    
    // Should we try and change to a different plane here?? Probably not.
  }; // end of do_goto_region
  /**
   * This is called when the selection changes in a different place, and 
   * this slice is being notified in case we want to change our selection.
   * It finds a path with the ID of the selection, or if not present, looks for
   * a "vertical relative" i.e. child or parent that is the selection, and highlights
   * the whole of the corresponding region on this slice.
   * Note that this might actually highlight more than one named path!! 
   */
  my.do_change_selection = function(r){
    var old_sel = $(my.node).find(".image-selectedregion");
    // have we already got this item selected? if so, exit
    if(old_sel.length>0 && old_sel[0].id == r){ return; }
    // otherwise: unselect previously selected regions
    old_sel.removeClass('image-selectedregion');
    // change the newly selected region to 'selected'
    var sel_text = "";
    if(r!==undefined && r!==null){ // (provided it is not blank)
      var sel = $("#"+r,my.node);
      if(sel.length>0){ // do we have the exact item on this slice?
        sel.addClass('image-selectedregion');
        sel_text = NeuroSim.friendlyText(r);
      }else{ // if not, do we have a vertical relative?
        var regs = NeuroSim.slice_metadata.regions_by_slice[my.svgFile];
        if(regs===undefined){
          console.log("Image "+my.svgFile+" has no region metadata"); 
          return;
        }
        for(var i=0; i<regs.length;i++ ){ // for each region in this slice
          if(NeuroSim.isVerticalRelative(r,regs[i])){ // if it is a relative of the selection
            // then select it. 
            $("#"+regs[i], my.node).addClass('image-selected-region');
          }
          sel_text = NeuroSim.friendlyText(r) + " (" +NeuroSim.friendlyText(regs[i]) + ")";
        }
      }
      $(my.node).find('.image-selection-name').html( sel_text );
    }else{
      $(my.node).find('.image-selection-name').html( "" );
    }
  };
  /**
   * when a lesion value changes somewhere in the model, 
   * we should check if we are showing that region, and update the lesion
   * values accordingly
   */
  my.refresh_lesion = function(region){
    if($(my.node).hasClass("image-show-lesions")){
      // find out the lesion level of the region
      var l=NeuroSim.drawLesioned(region);
      // if threre is no recorded lesion, that is 0 lesion
      if(l===null || l===undefined){l=0;}
      // colour any regions with that name, with transparent green overlay
      $("#"+region, my.node).each(function(i,o){
        if(l>0){
          $(this).attr("style","fill:#00ff00;fill-opacity:"+(1-l));
        }else{
          $(this).attr("style","");
        }
      });
    }
  };
  /**
   * on initialisation - check if we need to change the colours to reflect lesions 
   */
  my.refresh_all_lesions = function(){
    if(!$(my.node).hasClass("image-show-lesions")){ return;}
    var regions = my.image_svg.find(my.path_specifier); 
    regions.each(function(i,e){ 
      var l=NeuroSim.drawLesioned(e.id);
      if(l!==null && l!==undefined && l>0){
        $(this).css(NeuroSim.getLesionColourCss(l));
      }
    });
        
  };
  
  my.initialise();
  
  return my;
}; // End of NeuroImage class

  
var ImageNav = function(){
  var my={};
  my.NONE            = 0;
  // instructs the slice to draw a NavLine
  my.DRAW_LINE       = 1; 
  // slice should navigate within the current series to a slice containing the selection
  my.GOTO_SELECTION  = 2;
  // draw a line for each slice in the relevant series. (series must be the one in a given image)
  my.DRAW_ALL_LINES  = 4;
  return my;
}();
/*****
 * End of NeuroImage module
 *****/






/*****************************************************
 * 
 * NeuroExam
 * 
 * Class for an examination. Initialise with a node that has the main element for the 
 * examination. The node should have classes that specify what examination it is.
 *
 *****************************************************/



var NeuroExam = function(node){
  var my  = {};
  my.node = node;
  my.initialise = function(){
    if($(my.node).hasClass("neurosim-exam-snellen")){
      /**************************
       ***
       ***  1. SNELLEN CHART
       ***
       **************************/
      
      my.eye = 0;  // set default eye
      // Add the eye selector icon to the parent element
      var eyeselector = $("<div class='neurosim-eye-selector'></div>"); 
      my.node.appendChild(eyeselector[0]); 
      /** 
       * Create mouse handlers for the eye selector,
       * to toggle the eyepatch from left to right eye 
       */
      eyeselector.on("click",null, null,function(evt){
        if(my.eye === 0 ){
          my.eye = 1; 
          $(my.node).find("#eyepatch-left").attr("visibility","hidden");
          $(my.node).find("#eyepatch-right").attr("visibility","visible");
        }else{
          my.eye = 0; 
          $(my.node).find("#eyepatch-left").attr("visibility","visible");
          $(my.node).find("#eyepatch-right").attr("visibility","hidden");          
        }
        my.updateExam();
      });
      // load the eye selector svg 
      $(eyeselector).load("neurosim-eye-selector.svg",function(response,status,xhr){
        my.eye = 1;  // set to left eye
        $(my.node).find("#eyepatch-left").attr("visibility","hidden"); // hide one eyepatch
      });
      my.svg = $("<div></div>"); // create an element to contain the SVG
      my.node.appendChild(my.svg[0]);   // createShadowRoot().
      my.svg.addClass("image-svg");  // enable the region-selection and lesion-highlight methods
      my.neuro_image=NeuroImage(my.node);
      my.neuro_image.setSvgFile("neurosim-snellen-chart.svg"); // and display the actual snellen chart.
      /** Update the snellen chart's blur and darken to match the lesions in the model */
      my.updateExam = function(){
        var eye_av = 1;
        if(NeuroSim.getConsciousLevel() > 0.5 &&    // conscious
           NeuroSim.getComprehensionLevel() > 0.5){ // and comprehending?
          // use visual connections to determine snellen chart appearance
          var suffix1,suffix2; // suffix for left and right, for determining retina and brain regions
          if(my.eye===0){ suffix1="left"; suffix2="right"; }
          else          { suffix1="right";suffix2="left";  }
          /* test connections from the fovea to V1 */
          var eye_ac = [  NeuroSim.testConnection("retina-"+suffix1+"-fovea", "v1-left" ) ,
                          NeuroSim.testConnection("retina-"+suffix1+"-fovea", "v1-right") ,  
                          NeuroSim.testConnection("retina-"+suffix1+"-fovea", "v1-left" ) ,
                          NeuroSim.testConnection("retina-"+suffix1+"-fovea", "v1-right")
                         ];
          /* Acuity:  Calculate the best vision across relevant retinal foveal locations */
          for(var i=0;i<eye_ac.length;i++){
            eye_av = Math.min( eye_av, eye_ac[i] );
          } 
        }else{ // unconscious or cannot understand!
          // can't report seeing anything at all
          eye_av=1;
        }
        my.setBlur(eye_av*24);                        // blur the image
        my.setDarken(Math.max(0,(eye_av*24-20)/4));   // and darken if necessary
      }; 
      my.setBlur = function(blur){
        // Note: cannot use jQuery setAttr because it does toLower on the attribute
        // and this duplicates the SVG property, which seems to be case-sensitive.
        $(my.node).find("#filter4153 feGaussianBlur")[0].setAttribute("stdDeviation",blur+" "+blur);
      };
      my.setDarken = function(dark){
        // this will dim the view
        // dark should be between 0 and 1.
        $("#black-overlay",my.node).attr("style","fill:#000;fill-opacity:"+dark);
      };
    }else if($(my.node).hasClass("neurosim-exam-visualfields")){
      
      /**********************************
       ***
       ***   2. VISUAL FIELDS
       ***
       **********************************/
      
      my.svg = $("<div></div>");
      my.node.appendChild(my.svg[0]);
      my.svg.addClass("image-svg image-nav-goto-selection");
      my.neuro_image=NeuroImage(my.node);
      my.speech = [[ "nothing","","",""],["Yes","Yup","yeah"]];
      my.updateExam = function(){
        // for each region in the visual field
        if($(my.node).hasClass("image-show-lesions")){
          $(".image-region",my.svg).each(function(i,e){
            // is the connection in tact with V1?
            var l=NeuroSim.testConnection(e.id,"v1");
            // set the fill style of the region.
            $(e).css(NeuroSim.getLesionColourCss(l));
          });
        }
      };  // updateExam
      my.text_information = $("<div></div>"); // text area for comments
      $(my.node).append(my.text_information);

      /** Do we have the "image-click-to-test flag? if so, set up a click listener */ 
      if($(my.node).hasClass("image-click-to-test")){ 
        my.neuro_image.addImageLoadedListener(function(){
          my.svg.find(".image-region").click(function(e){ // listener on each region 
            // This is called whenever the user clicks on a point in the fields.
            // determine whether we can see it?
            var conscious_level     = NeuroSim.getConsciousLevel(),
                comprehension_level = NeuroSim.getComprehensionLevel();
            if(conscious_level     > 0.5 &&       // are they conscious 
               comprehension_level > 0.5 ) {   // and understand the question?
              var l = NeuroSim.testConnection(this.id, "v1");
              // draw a circle, coloured by the degree of lesion
              my.place_dot(this,e,[0,Math.floor(255*l),0], 4);
              //  lesion degree = probability of not seeing it
              var seen = Math.random()>l;
              // now notify or log the click using text
              var log=$(".neurosim-exam-visualfields-click-log");
              if(log.length>0){
                if(seen){
                  log.val( log.val() + "Yes ("+this.id+")\n" );
                }
              }
              var options =  my.speech[0+seen];
              NeuroSim.speak(options[Math.floor(Math.random()*options.length)]);
            }else{ // either unconscious or not comprehending
              my.place_dot(this,e,[0,255,0], 4);     
              if(conscious_level<0.3){
                my.text_information.html("Patient is drowsy.")
              }else if(conscious_level<0.6){
                my.text_information.html("Patient is confused.");
              }else if(comprehension_level<0.5){
                my.text_information.html("Patient doesn't seem to understand you.")
              }
            }
            e.preventDefault(); // don't do 'select all' etc for clicks on regions
          }); // end find.click
        }); // end addlistener
      } // end if 
      // Now load the fields image. This will trigger the loadedListener after
      // image is loaded. 
      my.neuro_image.setSvgFile("neurosim-visual-fields.svg"); 
    }else if($(my.node).hasClass("neurosim-exam-face")){
          
      /****************************
       ***
       ***  3. Face Examination
       ***
       *****************************/
      
      my.svg = $("<div></div>");
      my.node.appendChild(my.svg[0]);
      my.svg.addClass("image-svg");
      my.neuro_image = NeuroImage(my.node);
      my.neuro_image.setSvgFile("neurosim-face-examination.svg");
      // the following are svg elements
      my.eyes = []; // iris disc
      my.pupils = []; // black dots for pupils
      my.facepath = []; // main face path
      my.eyesopen_d = []; // face path with eyes open (to guide animation)
      my.eyesclosed_d = []; // and with eyes closed
      my.lowerlip = [];
      my.lowerlip_open_d = [];
      my.lowerlip_closed_d = [];
      my.pentorch_on = false;
      my.pentorch = []; // coordinates of pentorch
      my.MOUTHY=50; // height (below meridian) below which mouse click opens mouth
      // instructions for moving the eyelids.
      // these are the 'd' coordinates in the svg file, for the 'face-path' element
      // that will allow each lid to move up and down.
      // the n'th to m'th (space-delimited) words must be replaced, inclusive
      my.lid_words = [ [42-1,51-1], [7-1,16-1]];   
      my.lid_up    = [
        [-20.71428,-19.4518,  -39.28571,-20.33597, -18.57143,-0.88417,  -27.14286,24.75683, -27.14286,24.75683 ],
        [ -8.57143,-25.6410,  -27.14286,-24.75683, -18.57143,0.88417,   -39.28572,20.33597, -39.28572,20.33597 ]
      ];
      my.lid_down  = [
        [-18.46428,19.5482, -37.03571,18.66403, -18.57143,-0.88417, -29.39286,-14.24317, -29.39286,-14.24317 ],
        [-11.32143,13.8590, -29.89286,14.74317, -18.57143,0.88417,  -36.53572,-19.16403, -36.53572,-19.16403 ]
      ];
      my.TOOLS = { 
        PENTORCH:1,
        OPHTHALMOSCOPE:2,
        COTTON:3
      };
      my.tool = my.TOOLS.PENTORCH;
      
      /** image to use for fundoscopy */
      my.fundus_image = "";//"fundus-right-normal.jpg";
      
      
      
      my.neuro_image.addImageLoadedListener(function(){ my.readSVG(my.svg); });
      /**
       * this function is called when svg file is read in
       * -- get the relevant image regions into local variables
       * '_d' variables store the original path data for the purpose
       * of subsequent interpolation.
       */
      my.readSVG  = function(svg){ 
        //TODO centre the SVG and increase the viewbox width to capture mouse

        my.pentorch = $(svg).find("#pentorch");
        my.hide_pentorch();
        my.eyes     = $(svg).find("#eye-left,#eye-right") ;       
        my.pupils   = $(svg).find("#pupil-left, #pupil-right");
        my.facepath = $(svg).find("#face-main-path")[0];
        my.eyesclosed_d = $(svg).find("#face-main-path-eyes-closed")[0].getAttribute("d");
        my.eyesopen_d   = my.facepath.getAttribute("d");
        my.lowerlip = $(svg).find("#lower-lip-a")[0];
        my.lowerlip_closed_d = my.lowerlip.getAttribute("d");
        my.lowerlip_open_d   = $(svg).find("#lower-lip-mouth-open")[0].getAttribute("d");
        my.oral_cavity      =  [ $(svg).find("#oral-cavity-left")[0],
                                 $(svg).find("#oral-cavity-right")[0] ];
        my.insidemouth      =  $(svg).find("#face-inside-mouth")[0];
        my.uvula            =  $(svg).find("#face-uvula")[0];
        my.corrugations     = [$(svg).find("#corrugations-left")[0], $(svg).find("#corrugations-right")[0]];
        // get initial values of path coordinates
        my.lowerlip_droop_d =  $(svg).find("#lower-lip-droop")[0].getAttribute("d");
        my.upperlip_droop_d =  $(svg).find("#upper-lip-droop")[0].getAttribute("d");
        my.lowerlip_open_droop_d =  $(svg).find("#lower-lip-open-droop")[0].getAttribute("d");
        //my.oral_cavity_droop_d = [ $(svg).find("#oral-cavity-droop-left")[0].getAttribute("d"), 
        //                           $(svg).find("#oral-cavity-droop-right")[0].getAttribute("d") ];
        my.upperlip         =  $(svg).find("#face-upper-lip")[0];
        my.upperlip_d       = my.upperlip.getAttribute("d");
        my.oral_cavity_d    = {
          closed: {
              nodroop: [my.oral_cavity[0].getAttribute("d"),
                        my.oral_cavity[1].getAttribute("d") ],
              droop:   [$(svg).find("#oral-cavity-droop-left" )[0].getAttribute("d"),
                        $(svg).find("#oral-cavity-droop-right")[0].getAttribute("d") ]
            },
          open: {
              nodroop: [$(svg).find("#oral-cavity-open-left" )[0].getAttribute("d"),
                        $(svg).find("#oral-cavity-open-right")[0].getAttribute("d")
                                 ],
              droop:   [$(svg).find("#oral-cavity-open-droop-left" )[0].getAttribute("d"),
                        $(svg).find("#oral-cavity-open-droop-right")[0].getAttribute("d")
                                 ]
            }
          };        my.tongue    = $(svg).find("#tongue")[0];
        // hide the data-elements that are used to get coordinateas for various states of the mouth
        $(svg).find("#oral-cavity-droop-left, #oral-cavity-droop-right, #oral-cavity-open-left, #oral-cavity-open-right, #oral-cavity-open-droop-left, #oral-cavity-open-droop-right, #lower-lip-mouth-open, #lower-lip-droop, #lower-lip-open-droop, #upper-lip-droop").hide();
        // create links to appropriate fundoscopy images, in the 'defs' section
        // with id #fund-left
        if(my.fundus_image!==undefined){
          $(svg).find("#fundus-right-normal").attr("xlink:href",my.fundus_image);
        }
      };
      my.mouse_move_callback = function(e){
        if(my.eyes.length>0){ // has image been loaded?
          /** This relies upon the eye having no translation on the page.
           *  to achieve this, ungroup, regroup and then rename the eye in inkscape.
           */

          var relX = e.pageX - my.svg.offset().left  - my.svg.width()  * 0.5;
          var relY = e.pageY - my.svg.offset().top   - my.svg.height() * 0.5;
          var evt={
            action:"lookAt",
            lx:relX*0.1, ly:relY*0.1,
            rx:relX*0.1, ry:relY*0.1,
            pageX:e.pageX,  pageY:e.pageY
          };
          NeuroSim.bgcalc.postMessage(evt);
          //eyes[0].setAttribute("transform","matrix(1,0,0,1,"+e.lx+","+e.ly+")");
          //eyes[1].setAttribute("transform","matrix(1,0,0,1,"+e.rx+","+e.ry+")");
          if(relY<my.MOUTHY){
            my.setMouth(0);  // close mouth if mouse not in lower half of face
          }
        }else{  // if not, re-try getting the eyes
          my.readSVG(my.svg);
        }
        if(my.pentorch_on){
          my.moveTorch(e);
        }
      };
      /** When the mouse is moved, over the face, move the eyes: */
      $(my.node).mousemove( function(e){
        my.mouse_move_event_handler(e);
      });
      /** when the finger moves, do the same */
      $(my.node).on('touchmove', function(e){
        my.mouse_move_event_handler(e.originalEvent.touches[0]);
        e.preventDefault();
      });
      
      /**
       * handle mouse and touch movements:
       * send a lookAt event to the background thread
       * send a setMouth event if we move away from the mouth
       * call moveTorch to change the move the spotlight and
       * call updateLight (which posts a 'torch' message
       * to the background thread) if needed.
       */
      my.mouse_move_event_handler = function(e){
        if(my.eyes.length>0){ // has image been loaded?
          /** This relies upon the eye having no translation on the page.
           *  to achieve this, ungroup, regroup and then rename the eye in inkscape.
           */

          var relX = e.pageX - my.svg.offset().left  - my.svg.width()  * 0.5;
          var relY = e.pageY - my.svg.offset().top   - my.svg.height() * 0.5;
          var evt={
            action:"lookAt",
            lx:relX*0.1, ly:relY*0.1,
            rx:relX*0.1, ry:relY*0.1,
            pageX:e.pageX,  pageY:e.pageY
          };
          NeuroSim.bgcalc.postMessage(evt);
          //eyes[0].setAttribute("transform","matrix(1,0,0,1,"+e.lx+","+e.ly+")");
          //eyes[1].setAttribute("transform","matrix(1,0,0,1,"+e.rx+","+e.ry+")");
          if(relY<my.MOUTHY){
            my.setMouth(0);  // close mouth if mouse not in lower half of face
          }
        }else{  // if not, re-try getting the eyes
          my.readSVG(my.svg);
        }
        if(my.pentorch_on){
          my.moveTorch(e);
        }
      };//my.mouse_move_callback);
      
      NeuroSim.bgcalc.callbacks.moveEyes = function(e){
        if(my.eyes.length>0){
          my.eyes[0].setAttribute("transform","matrix(1,0,0,1,"+e.lx+","+e.ly+")");
          my.eyes[1].setAttribute("transform","matrix(1,0,0,1,"+e.rx+","+e.ry+")");        
        }
      };
      NeuroSim.bgcalc.callbacks.moveLids = function(e){
        // message contains "l" and "r", desired lid height
        if(my.eyes.length>0){ // have we initialised the grpahics yet?
          var y = [e.l, e.r],  // required height of lids
              d = my.facepath.getAttribute("d"); // current position of face points
          d = d.split(/[ ,]/);// split the list of points into words

          for(var ei = 0; ei<2;ei++){ // for each eye l/r
            // calculate numerical coordinates required, for the height of the lid
            // by linearly interpolating lid_up to lid_down
            var newlid=[], x; // elementwise, since JS doesn't support vector maths
            for (var i=0;i<my.lid_up[ei].length;i++){ // for each point
              x = (1-y[ei]) * my.lid_up[ei][i] + y[ei] * my.lid_down[ei][i]; 
              newlid[i] = x.toString();
            }
            // replace the path points with the new points
            d = d.slice(0,my.lid_words[ei][0]) .concat( newlid ) .concat( d.slice(my.lid_words[ei][1]+1) );
          }
          d = d.join(" ");
          my.facepath.setAttribute("d",d);
        }
      };

      /**
       * Called by the background thread as it changes the pupil size,
       * so that we can draw it at the right size.
       */
      NeuroSim.bgcalc.callbacks.movePupils = function(e){
        // message contains desired pupil size "p": [left, right] 
        if(my.eyes.length>0){ // have we initialised the graphics yet?
          for(var i=0;i<2;i++){ // for each eye
            my.pupils[i].setAttribute("rx",e.p[i]); // set the x and y size of pupil.
            my.pupils[i].setAttribute("ry",e.p[i]);
          }
        }
      };
      /**
       * called by the background thread to move the mouth 
       */
      NeuroSim.bgcalc.callbacks.moveMouth = function(e){
        if(my.eyes.length>0){
          // scale inside of mouth (black and tongue)
          var yscale = e.m+1; 
//          my.insidemouth.setAttribute("transform","translate(0,304) scale(1,"+yscale+") translate(0,-304)");
          my.tongue.setAttribute("transform","scale(1,"+yscale+")");
          // lower lip
          var d  = NeuroSim.interpolatePath(my.lowerlip_closed_d, my.lowerlip_open_d, e.m),
              // non-droop version with appropriate openness
              dd = NeuroSim.interpolatePath(my.lowerlip_droop_d,  my.lowerlip_open_droop_d, e.m );
              // droop version with appropriate openness
          //d = NeuroSim.interpolatePath(d, my.lowerlip_droop_d, e.droop[1], [1,10]);
          //d = NeuroSim.interpolatePath(d, my.lowerlip_droop_d, e.droop[1], [31, 34]);
          //d = NeuroSim.interpolatePath(d, my.lowerlip_droop_d, e.droop[0], [19,28]);
          d = NeuroSim.interpolatePath(d, dd, e.droop[1], [1,10]);
          d = NeuroSim.interpolatePath(d, dd, e.droop[1], [31, 34]);
          d = NeuroSim.interpolatePath(d, dd, e.droop[0], [19,28]);
          my.lowerlip.setAttribute("d",d);
          // upper lip
          d = NeuroSim.interpolatePath( my.upperlip_d, my.upperlip_droop_d , e.droop[1],[1,10]);
          d = NeuroSim.interpolatePath( d, my.upperlip_droop_d , e.droop[1],[43,46]);
          d = NeuroSim.interpolatePath( d, my.upperlip_droop_d , e.droop[0],[25,34]);
          my.upperlip.setAttribute("d",d);
          // oral cavity (black). two halves, left and right
          for(var i=0;i<2;i++){
            // interpolate between droop and no-droop for open
            d  = NeuroSim.interpolatePath( my.oral_cavity_d.open.nodroop[i],                                                    my.oral_cavity_d.open.droop[i],  e.droop[i] );
            // and for closed
            dd = NeuroSim.interpolatePath( my.oral_cavity_d.closed.nodroop[i],
                                           my.oral_cavity_d.closed.droop[i], e.droop[i]);
            // interpolate between open and closed
            d=NeuroSim.interpolatePath( d, dd, 1-e.m );
            my.oral_cavity[i].setAttribute("d",d);
          }
        }
      };

      $(my.node).mousedown(function(e){
        my.mouse_down_event_handler(e);
      });
      $(my.node).on('touchstart', function(e){
        my.mouse_down_event_handler(e.originalEvent.touches[0]);
        e.preventDefault();
      });
      my.mouse_down_event_handler = function(e){
        /**
         * If the mouse is pressed, turn on the pentorch. 
         * call moveTorch, set the opacity, and call updateLight() to notify the 
         * background thread
         * call setMouth to open the mouth if we are in the lower half of face
         */
        if(!my.pentorch.length){
          my.readSVG(my.svg);
        }
        var relX = e.pageX - my.svg.offset().left  - my.svg.width()  * 0.5;
        var relY = e.pageY - my.svg.offset().top   - my.svg.height() * 0.5;
        // for pentorch and ophthalmoscope tools:
        if(my.pentorch.length && my.tool !== my.TOOLS.COTTON){
          my.pentorch_on = true; // show light
          my.moveTorch(e);       // move light to mouse, and set image
          /** Check if the click is over the mouth */
          // are we in the lower half of the face?
          my.setMouth(relY>my.MOUTHY); // open mouth
        }
        if(my.tool == my.TOOLS.COTTON){
          my.pentorch_on = false;
          my.moveTorch(e); // move the torch anyway, to calculate if we're in the eye
          if(my.eye_light[0]>0 || my.eye_light[1]>0){ // mouse over eye?
            var side = relX<0 ? "right":"left"; // which eye
            var trigem = NeuroSim.testConnection("ophthalmic-touch-receptor-"+side, "corneal-reflex-arc");
            if(trigem<0.2){ // is the corneal arc triggered?
              // attempt to send a blink command to lid muscles
              NeuroSim.bgcalc.postMessage({action:"triggerBlink"});
            }
          }
        }
        
      };
      /** Mouse up events */
      $(my.node).mouseup(function(e){
        my.mouse_up_event_handler(e);
      });
      $(my.node).on('touchend', function(e){
        my.mouse_up_event_handler(e.originalEvent.touches[0]);
        e.preventDefault();
      });
      /** when mouse goes up or touch is released, call hide_pentorch, 
       * and close the mouth */
      my.mouse_up_event_handler = function(e){
        if(my.pentorch.length){
          my.hide_pentorch();
          my.setMouth(0);
        }
      };
      my.hide_pentorch = function(){
          my.pentorch.css({"fill-opacity":"0"});
          my.pentorch_on = false;
          my.torch_update_light();
      };
    
      /**
       * Move the pentorch's location to be at the mouse cursor 
       */
      my.moveTorch = function(e){ // mouse event e
        var m_x   = e.pageX - my.svg.offset().left;
        var m_y   = e.pageY - my.svg.offset().top;
        if(isNaN(m_x) || isNaN(m_y)){
          return;
        } 
        // move the disc of light to the mouse cursor pos
        my.pentorch[0].setAttribute("transform","translate("+m_x+","+m_y+")");
        // calculate how much light in each eye
        my.torch_update_light();
        // display appropriate image
        my.torch_show_image(m_x,m_y);
      }
            /** 
       * amount of Light shining into each eye 
       */
      my.eye_light = [0,0];
      /** 
       * This sends a message to the calculation thread, that the light
       * level has changed. It takes the distance of the pentorch from each pupil
       * to see if it lights the pupil. 
       * As the pupil size changes, the calculation thread will then call the 
       * "movePupils" callback so that we can redraw.
       */
      my.torch_update_light = function(){
        var l = my.pentorch_on ? 1:0;
        if(l>0){ // if there's a light, calculate where it is
          var r = [my.pentorch[0].getBoundingClientRect(),
                   my.pupils[0].getBoundingClientRect(),
                   my.pupils[1].getBoundingClientRect()];
          var c=[];
          for(var i=0;i<3;i++){ // calculate centre for each box
            c[i] = [r[i].left+r[i].width/2, r[i].top+r[i].height/2]; 
          }
          // squared distance from pentorch centre to left and right pupils.
          var sqd = [ Math.pow(c[0][0]-c[1][0],2) + Math.pow(c[0][1]-c[1][1],2),
                      Math.pow(c[0][0]-c[2][0],2) + Math.pow(c[0][1]-c[2][1],2) ];
          // light reaches each eye if it is closer than the pentorch width
          var penw = Math.pow(r[0].width/2,2);
          my.eye_light = [l * (sqd[0]<penw), l * (sqd[1]<penw)];
        }else{
          my.eye_light=[0,0];
        }
        NeuroSim.bgcalc.postMessage({action:"torch", light:my.eye_light});
      };
      /**
       * Depending on the position of the mouse, and the torch state, and the current tool,
       * show an appropriate image in the disc of light.
       * called from moveTorch only. requires x and y coord of mouse to translate image
       */
      my.torch_show_image = function(m_x, m_y){
        if(my.tool == my.TOOLS.PENTORCH){ // just make a transparent yellow disc - spotlight
            my.pentorch.css({"fill-opacity":"0.7",
                             "fill":"#ffffb1"         });      
          }else if(my.tool == my.TOOLS.OPHTHALMOSCOPE){
            if(my.eye_light[0]>0){ // left eye has ophthalmoscope
              var xpat = -680+(m_x-220)*6,
                  ypat = -690+(m_y-210)*6;
              $(my.svg).find("#fundus-right-normal")[0].setAttribute("patternTransform",
                               " translate("+xpat+","+ypat+") scale(-1,1) translate(-400,0)");
              // includes L/R flip
              my.pentorch.css({"fill":"url(#fundus-right-normal)",
                               "fill-opacity":"1.0"
                              }); 
            }else if(my.eye_light[1]>0){ // right eye has ophthalmoscope
              // image centre is roughly [680,690]. 
              // mouse coords of right eye centre is roughly [90,210].
              // angular gain for moving ophthalmoscope is x 6
              var xpat = -680+(m_x-90)*6,
                  ypat = -690+(m_y-210)*6;
              $(my.svg).find("#fundus-right-normal")[0].setAttribute("patternTransform",
                               "translate("+xpat+","+ypat+")");
              // this also removes the L/R flip
              my.pentorch.css({"fill":"url(#fundus-right-normal)",
                               "fill-opacity":"1.0"
                              }); 
            }else{ // not over an eye. Just like pentorch now.
              my.pentorch.css({"fill-opacity":"0.7",
                               "fill":"#ffffb1"       });
            }
        } // not pentorch, not ophthalmomscope.
      };

      /**
       * Called when the mouse is down in the lower half of the face, 
       * to open the mouth, and when it leaves, to close the mouth.
       */
      my.setMouth = function(m){
        NeuroSim.bgcalc.postMessage({action:"moveMouth",m:m});
      };
      my.updateExam = function(){
        if(my.eyes.length>0){
          /** calculate parts of the examination that are static */
          var  xii =    [    NeuroSim.testConnection("hypoglossal-nucleus-left", "tongue-left"),
                             NeuroSim.testConnection("hypoglossal-nucleus-right", "tongue-right") ],
               x   =    [    NeuroSim.testConnection("gag-centre","palatal-muscle-left"),
                             NeuroSim.testConnection("gag-centre","palatal-muscle-right")],
              vii1 =    [    NeuroSim.testConnection("primary-motor-cortex", "frontalis-left"),
                             NeuroSim.testConnection("primary-motor-cortex",  "frontalis-right") ];
          var bb=my.uvula.getBBox(), ctr = [bb.x+bb.width/2, bb.y];
          my.uvula.setAttribute("transform", "translate("+ctr[0]+","+ctr[1]+
                                ") rotate("+(x[0]-x[1])*60+") translate("+-ctr[0]+","+-ctr[1]+")");
          for(var i=0;i<2;i++){ // for left and right
            my.corrugations[i].style.opacity = vii1[i];
          }
        }
        my.torch_update_light();
      };
    }else if($(my.node).hasClass("neurosim-exam-motor")){
      
      /***********************************
       ***   
       ***  4. MOTOR EXAM
       ***
       **********************************/
      
      // main SVG: displays force for each muscle
      my.svg = $("<div></div>");
      my.node.appendChild(my.svg[0]);
      my.svg.addClass("image-svg");
      my.neuro_image = NeuroImage(my.node);
      my.region = $(my.node).attr("data-region");
      if(my.region=="upper-limb"){
        my.neuro_image.setSvgFile("neurosim-motor-upper-limb.svg");
      }else if(my.region=="lower-limb"){
        my.neuro_image.setSvgFile("neurosim-motor-lower-limb.svg");          
      }else{
        my.neuro_image.setSvgFile("neurosim-motor-all.svg");          
      }
      /** text values for the MRC grading. eight levels here. */
      my.MRC=['0','1','2','3','4-','4','4+','5-','5'];
      /** which muscles preferentially affected by  pyramidal weakness? */
      my.PYR=[
        "deltoid",
        "triceps",
        "extensor-carpi-radialis",
        "extensor-carpi-ulnaris",
        "extensor-digitorum",
        "interossei",
        "opponens-pollicis",
        "abductor-pollicis-brevis",
        "psoas",
        "adductor-magnus",
        "hamstrings",
        "tibialis-anterior",
        "tibialis-posterior",
        "extensor-hallucis-longus"
      ];
      // diagram: this shows the muscle movements on picture of body
      my.diagram = {    
        node: $("<div class='motor-diagram image-allow-hover image-show-tooltips image-show-selection'></div"),  // diagram.node - works like an image node
        svg:  $("<div class='image-svg'></div>")   // diagram.svg  - the image-svg within the main image node. 
      };
      my.node.appendChild(my.diagram.node[0]);
      my.diagram.node[0].appendChild(my.diagram.svg[0]);
      my.diagram.neuro_image = NeuroImage(my.diagram.node);
      my.diagram.neuro_image.setSvgFile("neurosim-motor-diagram.svg");
      // once the main svg image loads, add appropriate event handlers for mouseover
      my.neuro_image.addImageLoadedListener(function(){
        // when mouse goes into the motor exam region, show the appropriate diagram arrow
        my.svg.find(".image-region").mouseenter(function(e){
          $(my.diagram.svg).find("#"+e.target.id).css("fill-opacity",1);  
        });
        // and correspondingly, when mouse leaves, hide the diagram arrow
        my.svg.find(".image-region").mouseleave(function(e){
          $(my.diagram.svg).find("#"+e.target.id).css("fill-opacity",0.1);
        });
      });
      /**
       * Calculate whether the region is connected to M1
       */
      my.updateExam = function(){
        // before changing any sizes, check how big the bars are
        // and use this as the maximum for when lesions occur.
        if(!("defaultPowerWidth" in my)){
          my.defaultPowerWidth = $(my.svg).find("#biceps-left, #quadriceps-left")[0].width;
        }
        // for each muscle in the limb
        $(my.svg).find(".image-region").each(function(i,e){
          // is the connection in tact with M1?
          var l  = NeuroSim.testConnection("primary-motor-cortex", e.id);
          var um = NeuroSim.entity_data[e.id].i[0], uml=0; // check for UMN lesion specifically
          if(um!==undefined){ // if we found an UMN, calculate the degree of UMN lesion
            uml=NeuroSim.testConnection("primary-motor-cortex",um);
          }else{ console.warn("no UMN for "+e.id); }
          for(var i=0;i<my.PYR.length;i++){
            if(e.id.indexOf(my.PYR[i]) >= 0){ // is this item one of the pyramidal group?
              l += 0.25 * uml; // if so, amplify any lesion a bit, if there is an UMN lesion.
            }
          }
          l=Math.min(1,Math.max(0,l)); // constrain to range 0-1
          // lesioned areas are green (0,255,0), 
          // whereas intact areas are white (255,255,255).
          var col = Math.floor((1-l)*255);
          // set the fill style of the region.
          $(e).css({"fill": "rgb("+col+",255," +col+")"});
          // give MRC score - index into my.MRC strings
          var mrc_val = 8-((l>0  ) + (l>0.1) + (l>0.2) + (l>0.3) +
                           (l>0.5) + (l>0.6) + (l>0.9) + (l==1) );
          // find the text element within this region
          var mrc_elem = $(e).next();
          if(!mrc_elem.is("text")){ // if it doesn't exist yet, create one
            var d=$(e).attr("d").split(/[ ,]/), x=parseFloat(d[1]), y=parseFloat(d[2]);
            mrc_elem = $(document.createElementNS("http://www.w3.org/2000/svg","text")).attr({
              x: x+17, // location within the region
              y: y+16
//             , "font-size": "10px",
//              "font-weight": "bold",
//              "fill":"rgb(0,0,0)"  // now done in stylesheet.
            }); 
            $(e).after(mrc_elem); // and place it in the svg			  
          }
          mrc_elem.text(my.MRC[mrc_val]); // set the MRC grading text
          mrc_elem.css("fill","rgb(0,"+Math.floor(l*255)+",0)");
          // resize the region
          l=l*0.9;
          var cx=(l)*parseFloat($(e).attr("d").split(/[, ]/)[1]);
          $(e).attr("transform", "matrix("+(1-l)+",0,0,1,"+cx+",0)");
        });
      }; // updateExam
    }else if($(my.node).hasClass("neurosim-exam-reflexes")){
      
      /************************
       ***
       ***  5. REFLEXES
       ***
       ***************************/
      
      my.svg = $("<div></div>");
      my.node.appendChild(my.svg[0]);
      my.svg.addClass("image-svg");
      my.neuro_image=NeuroImage(my.node);
      my.neuro_image.setSvgFile("neurosim-reflexes.svg");
      // TODO - implement these extra reflexes...
      my.cnArcs =   { 
        "corneal"         :[ "ophthalmic-pain-receptor", "orbicularis-oculi" ],
        "vestibuloocular" :[ "vestibular-organ",         "lateral-rectus" ],
        "gag"             :[ "palatal-touch-receptor",   "palatal-muscle" ]
      };
      my.updateExam = function(){
        var cerebel = [ NeuroSim.testConnection("spinocerebellar-tract-left", "ventrolateral-thalamus-right"),
                        NeuroSim.testConnection("spinocerebellar-tract-left", "ventrolateral-thalamus-right") ];
        // for each reflex
        $(my.svg).find(".image-region").each(function(i,e){
          var id      = e.id;
          // is the connection in tact with M1?
          var umn     = 1-NeuroSim.testConnection("primary-motor-cortex", id),
          // is the connection in tact with the first cell body in the pathway?
              anthorn = NeuroSim.entity_data[id].i[0], // this will baulk if there's no input...
              lmn     = 1-NeuroSim.testConnection(anthorn, id),
              reflex  = (1-umn*0.5) * lmn * lmn,
          // was previously (1-0.75*umn)*lmn,
              text    = $(e).find("text");
          if( /^flexor-hallucis-longus/.test( id ) ){ // find Plantar reflex
            var pl, tt; // plantar symbol and tooltip
            if(     lmn<0.15){ pl="&harr;";  tt="mute";    }
            else if(umn<0.98){ pl="&uarr;";  tt="extensor";}
            else             { pl="&darr;";  tt="flexor";  }
            text.html(pl); 
            // text.title(tt); // *** CHANGED TO:
            text.tooltip($("<p>"+tt+"</p>")[0]);
            //text.tooltip(tt);
          }else{ // tendon hammer reflexes
            if( /^pterygoid/.test(id)){   // Find jaw jerk -  test both sides
              id      = NeuroSim.flipLR(id);     // repeat calculation for the opposite side
              umn     = 1-NeuroSim.testConnection("primary-motor-cortex", id);
              anthorn = NeuroSim.entity_data[id].i[0];
              lmn     = 1-NeuroSim.testConnection(anthorn, id);
              var reflex2 = (1-umn*0.5) * lmn * lmn; // contralateral contribution
              reflex  = (reflex+reflex2)/2; // take the mean of the left and right ptergyoids
            }else{ // limb deep tendon reflexes -- reduce in cerebellar syndromes
              var side=/-right$/.test(id); // 0=left, 1=right
              // a cerebellar lesion will reduce the reflex by a notch.
              reflex -= 0.25 * cerebel[side+0]; 
            }
            /* sympathetic tone : 0 = normal, -1 = reduced, >0 = increased */
            var sym = -NeuroSim.testConnection( "sympathetic-nucleus","stellate-ganglion-left" );
            reflex += sym;
            /** how many plusses to draw? */
            var numPlus = Math.floor(4*reflex); //  number of  plusses
            var plusValues = ["-", "+","++","+++","++++"];
            text.html(plusValues[numPlus]);
          }
          var col=Math.floor((1-reflex)*255);
          // set the text color of the plusses
          text.css({"color": "rgb("+col+",255," +col+")"});
        });
      };
    }else if($(my.node).hasClass("neurosim-exam-dermatomes")){
      
      /****************************
       *** 
       ***  6. DERMATOMES
       ***
       *****************************/
      
      my.modselector = $("<div class='neurosim-dermatomes-selector'></div>");
      my.node.appendChild(my.modselector[0]);
      $(my.modselector).load("neurosim-dermatomes-selector.svg",function(response,status,xhr){
        // remove local styles for the selection rectangles
        $(my.modselector).find("svg > g > rect").attr("style","");
        // and add click listeners to the rectangles.
        $(my.modselector).find("svg").children("g").click(function(){
          my.setModality(this.id);
        });
        // remove svg's own size parameters, so it will fit the container.
        $(my.modselector).find("svg").removeAttr("width").removeAttr("height");
      });     
      my.svg = $("<div></div>");
      // append the SVG element. once it is appended,
      $(my.node).append(my.svg[0]).ready(function(){
        // Make it into a NeuroImage
        my.svg.addClass("image-svg");
        my.neuro_image=NeuroImage(my.node);
          // call us back when the SVG regions are processed
        my.neuro_image.addImageLoadedListener(function(){
          /** once dermatome image has been loaded, call setModality, 
           *  to change the region names to receptors */
          my.setModality("pain-receptor");
          // if we are using 'click-to-test', 
          if($(my.node).hasClass("image-click-to-test")){ 
            // then add a listener on each dermatome region 
            my.svg.find(".image-region").click(function(e){ 
              // This is called whenever the user clicks on skin.
              // can we feel it?
              var unconscious = NeuroSim.getConsciousLevel() < 0.4; // are we conscious?
              var l; // degree of lesion
              if(unconscious){
                l=1; // if unconscious, there is no perception
              }else{ // otherwise test strength of connection
                l = my.test_region_current_modality(this.id);
              }
              // draw a circle, coloured by the degree of lesion
              my.place_dot(this,e,[0,Math.floor(255*l),0], 4);
              // what do we say, givenpons the degree of lesion?
              var response = my.responses[Math.floor(l*10)] ;
              // now notify or log the click using text
              var log=$(".neurosim-exam-visualfields-click-log");
              if(log.length>0){
                log.val( log.val() + response + "\n" );
              }
              NeuroSim.speak( response );
            }); // end find.click
          } // end if click-to-test      
        }); // end image-loaded listener
        my.neuro_image.setSvgFile("neurosim-dermatomes.svg");
      });
      my.current_modality = "pain-receptor";
      /** 
       * Called to set the dermatome modality - Will change the names of all the regions 
       * appending the correct receptor name
        // if we havent's called this before, note that the starting names in the svg are like
        // "c7-left", and what we want is "c7-touch-receptor-left" etc. so this will signal to 
        // "setModality" to introduce the modality name to each id.
       */
      my.setModality = function(mod){ 
        my.current_modality = mod;
        // change GUI selection
        $(my.modselector).find("svg > g").removeClass("selected");
        $(my.modselector).find("#"+mod).addClass("selected");
      };
      /**
       * Calculate whether the region is connected to S1, via the appropriate pathway
       */
      my.updateExam = function(){
        // for each dermatome
        if($(my.node).hasClass("image-show-lesions")){
          var unconscious = NeuroSim.getConsciousLevel() < 0.4;
          // are we showing the lesions?
          $(my.svg).find(".image-region").each(function(i,e){
            var cx;
            if(unconscious){
              cx=1; // unconscious - no sense perception.
            }else{  // conscious: check if can perceive
              // is the connection in tact with S1?
              cx  = my.test_region_current_modality(e.id);
            }
            var col = NeuroSim.getLesionColourCss(cx); // what colour should it be?
            // fill the region according to lesion
            if(cx>0){ // cortex connection is lesioned
              //e.css({"color": col});
              $(e).css(col);
            }else{ // no lesion
              $(e).css({"color": "","fill":"","fill-opacity":"","stroke":""}); // reset to stylesheet?
            }
          });
        } // not showing lesions
        $(my.svg).find("circle").remove(); // get rid of any clicks - we'll need to re-examine
      }; // updateExam
      /** These are the possible responses we might make, given different degrees of lesion
                   <0.1   <0.2  <0.3                           <0.4      */
      my.responses=["Yes","Um, Yes","Yes, it's not normal though", "Yes, it feels strange",
      //     <0.5                           <0.6                <0.7
        "It's definitely numb there", "um, just about", "um, that's numb",
      //   <0.8          <0.9  <1: no responses if lesion >0.8
        "um, not sure",   "No",   "No" , "" ];
      /** 
       * for a given region id, convert it to a modality-specific
       * region name, using the currently selected modality.
       */
      my.test_region_current_modality = function(region){
        var modality = my.current_modality;
        if(modality.indexOf("joint")>=0){
          modality="touch-receptor";
        } // at the moment, vibration and light touch test the same pathway.
        // get the ID up to (not including) the final hypohen
        var lastdash = region.lastIndexOf("-"),
            // receptor id:
            rid      = region.substring(0, lastdash ) +
                        "-"+modality+ region.substring(lastdash),
            brain;   // where in the brain to check for a connection?
        if(modality.indexOf("pain-")>=0){ // for pain, thalamus is sufficient.
          brain = "medial-dorsal-thalamus";
        }else{ // for touch and vibration, the perception is cortical
          brain = "primary-sensory-cortex";
        }
        return NeuroSim.testConnection(rid,brain);
      };
      
    } ///// End of dermatomes 
    else if($(my.node).hasClass("neurosim-exam-imageswapper")){
      
      /****************************
       *** 
       ***  7. Generic image swapper
       ***
       *****************************/
      
       /** Create an image that changes according to lesions.
        * The text to generate an answer to a question is used as the filename
        * for the image. 
        */
      my.img  = $("<img class='neurosim-imageswapper-image'></img>");
      my.node.appendChild(my.img[0]);
      my.question_name = $(my.node).attr("data-question");
      if (my.question_name===undefined){
          console.log("error finding question for generic image");
      }
      my.updateExam = function(){
        var src = my.img[0].src; // old image file
        // test the connection and get the new appropriate image
        var response = NeuroSim.history_answer_text_question(my.question_name);
        // this returns an array of answers (one for each connection-test), and 
        // each of these is an array of the response string and lesion values.
        response = response[0][0]; // just take the text component of the response
        if(response !== src){ // has it changed?
          if(!response || response.length==0){ // is it valid?
            console.log("image for "+my.question_name+" returned "+response);
            return;
          }
          if(!response.match(/(.svg|.png|.jpg)/)){
            console.log("bad image swapper response: "+response); 
            response = "resources/blank.png";
          }
          // if so, set the image source file to be the response text
          my.img[0].src = response; 
        }
      };
    } // End of generic image
    else if($(my.node).hasClass("neurosim-exam-observations")){
      /***********************************
       * 
       * Observations examination
       * This section replaces the .neurosim-exam-observations element
       * from the snippets. Then it looks at each "H4" element, gets the name from 
       * its "A" element, and looks this up in history_metadata.observations
       * When there is a match, empty the subsequent "UL" and add elements 
       * for each item in the observations array. Uses choose_random_string_from_array 
       * to select and combine words in the array.
       * 
       **************************************/

      // completely replace the existing node. Keep the old data.
      var snippet = NeuroSim.case_snippets.find(".neurosim-snippet.snippet-observations").clone();
      my.original = $(my.node).replaceWith(snippet); // replace the observation div with the snippet
      my.node = snippet; // the main node becomes this snippet
      // keep track of the old one.
      /** get non-default observations from cases.json */
      my.obsdata = NeuroSim.history_metadata.observations;
      $(my.node).find("h4").each(function(i,e){ // for each H4 tag
        var link = $(e).find("a"); // get the link - use it as a button
        if(link.legth==0){
          console.log("Observation section "+e+"has no link");
        }
        let list_elem = $(e).next("ul"); // get the following list - it can be toggled
        link.click( function(){ // make the link show the list
          list_elem.toggleClass("hidden");
          return false; // stop navigation
        });
        list_elem.addClass("hidden"); // start hidden
        var list_name = link.html().toLowerCase();
        // are there non-default observations for this section?
        if(my.obsdata && my.obsdata[list_name]){ 
          list_elem.empty(); // if so, replace the whole section
          var listdata = my.obsdata[list_name]; // get the case-specific observations
          for(var i in listdata){ // add each one to the section
            var combined_text = NeuroSim.choose_random_string_from_array( 
              listdata[i], NeuroSim.history_patient_random_seed  );
            list_elem.append($("<li>"+combined_text+"</li>"));
          }
        }
      }); 
      //TODO use the neurosim-history
      // question interface with answer_text_question to compute answers.
    }
  /*************************************   
   * End of big 'if'
   **************************************/
    
    /**
     * creation of help text - read in the appropriate snippets and add 
     * them to a 'help icon'.
     */
    my.createHelp = function(){
      var prv=$(my.node).prev();
      if(prv.length>0 && prv.prop("tagName").toLowerCase() === "h3"){ // is the previous item the <h3> heading?
        var examname="", exams = $(my.node)[0].className.split(/\s+/); // get our exam name
        for(var i=0;i<exams.length;i++){
          if(exams[i].indexOf("neurosim-exam-")>=0){
            examname = exams[i].substring(14);
          }
        }

        create_help_icon(examname,prv);
      }
    };
    my.createHelp();
    
    
    ////// End of examination types
    /**
     * Places a dot on an SVG element, given a mouse event coordinate.
     * This is used by dermatomes and visual fields, when image-click-to-test is set.
     * We convert the coordinates using the SVG matrix, and create a "circle" element
     * in the SVG namespace.
     */
    my.place_dot = function(element, event, rgb, radius){
      if(radius===undefined){ radius=4;      }
      if(rgb   ===undefined){ rgb=[0,0,0]; }
      // we need to use svg wizardry to convert the mouse coords to SVG coords
      var pt  = my.svg.find("svg")[0].createSVGPoint(); 
      pt.x    = event.clientX; pt.y=event.clientY; 
      var loc = pt.matrixTransform(element.getScreenCTM().inverse());
      /** When clicked create a dot */
      var dot = $(document.createElementNS("http://www.w3.org/2000/svg","circle")).attr({
        cx: loc.x,
        cy: loc.y,
        r:  radius,
        fill: "rgb("+rgb[0]+","+rgb[1]+","+rgb[2]+")"
      }); 
      element.after(dot[0]); // and place it in the svg
    };
    
    
    
    /** 
     * Ensure that the NeuroSim keeps track of each new NeuroExam,
     * so that it can call the "updateExam" method whenever the selection or lesions change.
     */
    NeuroSim.html.exams.push(my);

  }; // end my.initialise()
  my.initialise(); // call it now!
  
  return my;
};
/**
 * Create a help icon for an examination. Requires the name 
 * (to look up in snippet-help-), and the heading element (usually a H3 tag)
 * to which to add the icon. This is a static function - 
 * @TODO ideally should be a static member of NeuroExam...
 */
var create_help_icon = function(exam_name, h3element){
  if(exam_name.length>0){ // if there is a valid exam name in the node class,
    // look up a help case_snippet.html section with classname "snippet-help-..."
    var helpsnip = NeuroSim.case_snippets.find(".snippet-help-"+exam_name);
    if(helpsnip.length>0){ // if such a help  section exists, 
      // create a help icon
      var hicon =$("<span class='neuroexam-help-icon'><i class='fa fa-question-circle'></i> </span>"); 
      h3element.append(hicon); // add inside <h3>, with class 'neuroexam-help-icon'. 
      // se the tooltip to the help text from the snippet.
      hicon.attr("title",helpsnip.html());
      hicon.attr("data-placement","left");
      hicon.popover({trigger: 'hover click'});//tooltip(); // apply bootstrap tooltips (bigger and nicer?)
    }
  }
}

/**
 *  Slow way to change tags of elements
 */

jQuery.fn.changeTag = function (newTag) {
    var q = this;
    this.each(function (i, el) {
        var h = "<" + el.outerHTML.replace(/(^<\w+|\w+>$)/g, newTag) + ">";
        try {
            el.outerHTML = h;
        } catch (e) { //elem not in dom
            q[i] = jQuery(h)[0];
        }
    });
  return this;
};
   
// Array shuffling prototype (from http://jsbin.com/ozayim/1/edit?html,js,output)
$.shuffle = function(){
    var counter = this.length, temp, index;

    // While there are elements in the array
    while (counter > 0) {
        // Pick a random index
        index = (Math.random() * counter--) | 0;

        // And swap the last element with it
        temp = this[counter];
        this[counter] = this[index];
        this[index] = temp;
    }
};
    
var ordinal_suffix_of = function (i) {
    var j = i % 10,
        k = i % 100;
    if (j == 1 && k != 11) {
        return i + "st";
    }
    if (j == 2 && k != 12) {
        return i + "nd";
    }
    if (j == 3 && k != 13) {
        return i + "rd";
    }
    return i + "th";
  };  
} // end check whether already loaded

/**
 * Static helper: Recursively assign properties of one object
 * to another object, at a particular depth.
 * For example assigning   { A:{B:"b2"} }  
 * to an original object   { A:{B:"b1", C:"c"} }
 * at level 0 produces     { A:{B:"b2"} }
 * at level 1 produces     { A:{B:"b2", C:"c"} }
 * at level 2 leaves the original unchanged becasue there are no
 * fields in the 2nd-level objects (they are strings).
 * Level = integer for depth
 */
var object_assign_deep = function( level, dest,  src ){
  if(level==0){
    try{
      return Object.assign(dest,src);
    }catch(excp){
      return dest;
    }
  }
  var src_keys = Object.keys(src);
  for(var i=0; i<src_keys.length; i++){
    var k = src_keys[i];
    if(!dest[k]){ // if no child object, create one
      dest[k]={};
    }
    dest[k] = object_assign_deep( level-1, dest[k], src[k])
  }
  return dest;
}


$(document).ready( function(){
  /** 
   * See if we're running inside an IFRAME or not.
   * If there's an iFrame and it contains any image slice or exam, 
   * then use that as the root. Otherwise use the main page.
   */
  if($("iframe").length>0 && $("iframe").contents().find(".image-slice, .exam-section").length>0){
    NeuroSim.initialise($("iframe").contents());
  }else{
    NeuroSim.initialise();
  }
});


this.UPDATE_INTERVAL = 100; // milliseconds between calculation updates

this.animate = function(e){
  /**
   * this is called by the timeout function every 100 ms
   *  to calculate the positions of various things.
   */
  if(this.running){
    var elapsed = Date.now() - this.lastFrameTime;
    this.lastFrameTime = Date.now();
    
    //// Do processing 
    if(this.model !== undefined){
      this.moveEyes();
      this.moveLid();
      this.movePupils();
      this.moveMouth();
    }

    ////
    
    this.currentTimeout = setTimeout(this.animate, this.UPDATE_INTERVAL);
  }
};


this.initialiseState = function(){
  // called when starting, to initialise any state-variables
  // for the calculations - for example, current eye position.
  this.eye=[
    // eye position, velocity, lid height, pupil size
    {x:0, y:0, vx:0, vy:0, lid:0, p:4}, 
    {x:0, y:0, vx:0, vy:0, lid:0, p:4}  
  ];
  this.config={
    blinkFreq:0.03 , // blink frequency (per-cycle)
    sacVel:3      ,  // how many frames does a saccade last?
    saccadeRate:0.2,  // how many saccades per frame?
    eyeDriftRand:1,      // number of pixels to drift each frame (only instantiated upon saccades)
    distractRate:0.05, // how many times to be distracted, per frame
    pupilrate:0.2    // proportional pupil change per frame
  };
  this.desired = [
    {x:0,y:0,lid:0,p:3},
    {x:0,y:0,lid:0,p:3} ,
  ];
  this.light = [0.1,0.1];
  this.vergence = 0;
  this.mouth = 0; // mouth 0 = closed, 1 == open
  this.desired_mouth = 0; 
  this.saccade=0; // saccade counter  - how many frames left of a saccade.
  this.blinking=0; // blink counter - how many frames left of a blink
  this.ptosis = 0; // close eyes a little at baseline?
  this.lipdroop = [0,0];
  this.model = undefined; // model is created when NeuroSim posts a "modelChanged" message.
  this.externalTriggerBlink = false; // is a blink being externally triggered?
};

/************************************************************
 * Calculation functions 
 ************************************************************/

/**
 * This is called by the animate() function each tick of the
 * timer. It updates eye position depending on eye velocity, and 
 * calculates changes to the velocity depending on the desired 
 * position. It then posts a "moveEyes" message back to the UI thread.
 */
this.moveEyes = function(){
  // vor going wrong: generate drift when gaze deviates from midline
  // proportional to distance. 
  var x = ( this.eye[0].x + this.eye[0].x ) / 2; // mean eye x position
  // horizontal drift
  var hnys = this.model.vor[1] * Math.abs(x) * (x<0) * 0.1 
           - this.model.vor[0] * Math.abs(x) * (x>0) * 0.1;
  var vnys = this.model.vdrift[0] + this.model.vdrift[1];
  // when vor goes wrong, saccade rate increases. (nystagmus)
  var sacc_rate = this.config.saccadeRate 
    + 0.2 * Math.min(1,Math.abs(hnys)+Math.abs(vnys));
  
  // saccade?
  if(this.saccade==0){  
    // stop eyes at the end of a saccade
    this.eye[0].vx=this.eye[0].vy=this.eye[1].vx=this.eye[1].vy=0;
    // should we start another saccade? 
    var startSaccade = Math.random() < sacc_rate;
  
    if(startSaccade){ // starting a new saccade?
      // start a saccade counter
      this.saccade = this.config.sacVel; // number of frames for saccade
      // now calculate velocity of eye during saccade
      for(var e=0;e<2;e++){ // for each eye
        this.eye[e].vx = (this.desired[e].x - this.eye[e].x)/this.saccade;
        this.eye[e].vy = (this.desired[e].y - this.eye[e].y)/this.saccade;
      }
    }
  }
  if(this.saccade>0){ this.saccade--; } // decrement saccade counter
  
  // drift in the desired direction, over time -- causes microsaccades
  // random component: -0.5 to +0.5 per frame, scaled by factor
  this.desired[0].x += this.config.eyeDriftRand*(Math.random()-0.5);
  this.desired[0].y += this.config.eyeDriftRand*(Math.random()-0.5);
  
  // add drift which gives nystagmus
  for(var e=0;e<2;e++){
    this.eye[e].vx += hnys;
    this.eye[e].vy += vnys*2;
  }
  
  if(Math.random()<this.config.distractRate){
    // create a distraction, occasionally
    this.desired[0].x = 20*(Math.random()-0.5); // random position
    this.desired[0].y = 20*(Math.random()-0.5);
  }
  // for gaze at infinity,
  this.desired[1].x = this.desired[0].x ; // desired eye pos is conjugate.
  this.desired[1].y = this.desired[0].y ; 
  //TODO implement vergence here?
  
  if( Math.abs(this.eye[0].vx)>0 ||      // if any velocity >0
      Math.abs(this.eye[0].vy)>0 ||
      Math.abs(this.eye[1].vx)>0 ||
      Math.abs(this.eye[1].vy)>0  ){
    for(var e=0;e<2;e++){
      this.eye[e].x += this.eye[e].vx; // move the eyes
      this.eye[e].y += this.eye[e].vy;
      this.eye[e].y = Math.max(Math.min(this.eye[e].y, 13),-13); // prevent eyes going too low
    }
    
    // TODO: restrict eyes based on pathology 
    for(var e=0;e<2;e++){ // for each eye
      var eyeflip = 2*(e==0)-1; // -1 for left, +1 for right
      this.eye[e].x *= 1 - ((this.eye[e].x*eyeflip > 0) ? model.lr[e]  :  model.mr[e] );
      this.eye[e].y *= 1 - ((this.eye[e].y < 0) ? model.sr[e]  :  // downgaze - check IO as well
             Math.min(1,this.model.ir[e] + 
               //    - Math.max(0,this.eye[e].x/10.)*(1-model.so[e]) 
                   - Math.min(0,this.eye[e].x/10.)*model.so[e] ) 
            );   
    }
    
    // and send the message back to the UI to move the eyes
    this.postMessage({callback:"moveEyes",
                      lx:this.eye[0].x, ly:this.eye[0].y, rx:this.eye[1].x, ry:this.eye[1].y}); 
  }
};// end move eyes

/**
 *  This updated the position of the eyelids.
 * It accounds for spontaneous blinking, horner's, facial nerve, 
 * and elevation of the lid with upgaze
 */
this.moveLid = function(){
  // should we blink?
  if(this.blinking==0){ // terminate a blink
    this.desired[0].lid = 0;
    this.desired[1].lid = 0; // try to open both  eye lids
    if(this.desired[0].y>-5){
      this.desired[0].lid = 0.08;
      this.desired[1].lid = 0.08;
    }
    if(this.desired[0].y>5){
      this.desired[0].lid = 0.2;
      this.desired[1].lid = 0.2;
    }
    // needs both muller's muscle and levator palpebris superioris
    this.desired[0].lid += (this.model.muller[0] + this.model.levpalp[0])*0.4 + this.ptosis;
    this.desired[1].lid += (this.model.muller[1] + this.model.levpalp[1])*0.4 + this.ptosis;
    
    var doBlink = Math.random()<this.config.blinkFreq; 
    if(doBlink || this.externalTriggerBlink){ // trigger a blink now
      this.externalTriggerBlink = false;
      this.blinking=3;
      this.preblink_eye=[[this.eye[0].x, this.eye[0].y],[this.eye[1].x,this.eye[1].y]];
    }
  }
  if(this.blinking>0){ // initiate a blink
    this.blinking--;
    this.desired[0].lid = 1;
    this.desired[1].lid = 1; // try to close both eyes
    this.eye[0].y-=4.5*this.blinking; // try to raise both eyes (Bell's phenomenon)
    this.eye[1].y-=4.5*this.blinking;
    if(this.blinking==0){ //towards end of blinking
      for(var i=0;i<2;i++){ // restore both eyes
        this.eye[i].x=this.preblink_eye[i][0]; this.eye[i].y=this.preblink_eye[i][1];
      }
    }
    this.postMessage({callback:"moveEyes",
                      lx:this.eye[0].x, ly:this.eye[0].y, rx:this.eye[1].x, ry:this.eye[1].y}); 
  };
  for(var i=0;i<2;i++){ // for L and R
    // prevent full closure with UMN VII palsy
    this.desired[i].lid *= (1-this.model.frontalis[i]);
    // add ptosis if frontalis paralysed
    this.desired[i].lid += 0.24*this.model.frontalis[i];
    // 7/2017 close eyes if drowsy
    this.desired[i].lid += 0.75 * (this.model.consc[0] * this.model.consc[1]);
    // open lid when light on
    if(this.desired[i].lid > 0.5 && this.light[i]>0){
      //this.desired[i].lid = 0;
    }
    // prevent it from going outside the range 0-1
    this.desired[i].lid = Math.max(0,Math.min(1,this.desired[i].lid));
  }
  
  
  // exponential decay-type movement of eye lid to desired position?
  dlid = { l: 0.7*(this.desired[0].lid-this.eye[0].lid) ,
           r: 0.7*(this.desired[1].lid-this.eye[1].lid) 
  };
  
  // TODO: now modify dlid depending on disease... eg myasthenia?
  
  
  
  // finally change the eye lid position if required
  if(Math.abs(dlid.l)>0.01 || Math.abs(dlid.r)>0.01){
    // 1% tolerance on lid position
    this.eye[0].lid += dlid.l;
    this.eye[1].lid += dlid.r;
    this.postMessage({callback:"moveLids",
                      l:this.eye[0].lid, r:this.eye[1].lid});
  }
}; // end move lid
/**
 *  This updates the size of the pupils
 */
this.movePupils = function(){
  // light reaching EWN =  total light input * connection
  var edinger = (1-model.edinger[0])*this.light[0]
              + (1-model.edinger[1])*this.light[1]; 
  if(this.light[0]>0){
    edinger=edinger;
  }
  // pupil size command, depending on light
  var command = Math.max(9-edinger*5) + 2*this.vergence;  
  var r = this.config.pupilrate, dp=[];
  for(var e=0;e<2;e++){ // for each eye
    // calculate target pupil size, based on command,  sympathetics, parasympathetics
    this.desired[e].p = Math.min(12, Math.max(2, 
                        (1-model.iris_symp[e])*(command+9*model.iris_parasymp[e]) ));     
    dp[e]  =  this.desired[e].p - this.eye[e].p;
  }
  if(Math.abs(dp[0])>0.01 || Math.abs(dp[1])>0.01){ 
    // is there a significant change in pupil size?
    this.eye[0].p += r*dp[0] ; // calculate new size
    this.eye[1].p += r*dp[1] ;
    // send new pupil size message back to GUI
    this.postMessage({callback:"movePupils", p:[this.eye[0].p, this.eye[1].p] })
  }
};
/**
 * This is called once every calculation frame, by the animata()
 * function. It updates the mouth position depending on the 
 * desired_mouth position, and posts a message to the GUI
 * if there is a significant change.
 */
this.moveMouth = function(){
  var dm = this.desired_mouth - this.mouth; 
  if(this.model.consc[0]*this.model.consc[1] > 0.5){
    this.desired_mouth = 0;
  }
  
  this.mouth = this.mouth + 0.2*dm;
  var dlipdroop = [];
  for (var i=0;i<2;i++){ // left and right lipdroop
    dlipdroop[i] = this.model.lipdroop[i] - this.lipdroop[i];
    this.lipdroop[i] += 0.5 * dlipdroop[i]; 
  }
  if(Math.abs(dm)>0.01 || Math.abs(dlipdroop[0])>0.01 || Math.abs(dlipdroop[1])>0.01){
    this.postMessage({callback:"moveMouth", m:this.mouth, droop:this.lipdroop});
  }
};



/******************************************************
 * Keyed dict with each connection that needs to be tested 
 *
 */
this.connectionList = {
  "so":             [    ["fourth-nerve-nucleus-left",  "superior-oblique-left"  ],
                         ["fourth-nerve-nucleus-right", "superior-oblique-right" ] ],
//  "mr":             [    ["lateral-gaze-centre-right",   "medial-rectus-left"     ],
//                         ["lateral-gaze-centre-left",  "medial-rectus-right"     ] ],
//  "lr":             [    ["lateral-gaze-centre-left",   "lateral-rectus-left"    ],
//                         ["lateral-gaze-centre-right",  "lateral-rectus-right"   ] ],
  "mr":             [    ["frontal-eye-field-left",    "medial-rectus-left"     ],
                         ["frontal-eye-field-right",   "medial-rectus-right"     ] ],
  "lr":             [    ["frontal-eye-field-right",   "lateral-rectus-left"    ],
                         ["frontal-eye-field-left",    "lateral-rectus-right"   ] ],
  "sr":             [    ["oculomotor-nucleus-left",   "superior-rectus-left"   ],
                         ["oculomotor-nucleus-right",  "superior-rectus-right"  ] ],
  "ir":             [    ["oculomotor-nucleus-left",   "inferior-rectus-left"   ],
                         ["oculomotor-nucleus-right",  "inferior-rectus-right"  ] ],
  "io":             [    ["oculomotor-nucleus-left",   "inferior-oblique-left"  ],
                         ["oculomotor-nucleus-right",  "inferior-oblique-right" ] ],
  "vor":            [    ["vestibular-organ-left",     "lateral-gaze-centre-left" ], 
                         ["vestibular-organ-right",    "lateral-gaze-centre-right"] ],
  "frontalis":      [    ["primary-motor-cortex", "frontalis-left"],
                         ["primary-motor-cortex",  "frontalis-right"] ],
  "edinger":        [    ["retina-left", "edinger-westphal-nucleus"],
                         ["retina-right","edinger-westphal-nucleus"] ],
  "iris_parasymp":  [    ["edinger-westphal-nucleus", "constrictor-pupillae-left" ],
                         ["edinger-westphal-nucleus", "constrictor-pupillae-right"] ],
  "iris_symp":      [    ["sympathetic-nucleus", "dilator-pupillae-left"],
                         ["sympathetic-nucleus", "dilator-pupillae-right"] ],
  "muller":         [    ["sympathetic-nucleus", "superior-tarsal-muscle-left" ],
                         ["sympathetic-nucleus", "superior-tarsal-muscle-right" ] ],
  "levpalp":        [    ["oculomotor-nucleus-left", "levator-palpebrae-superioris-left"],
                         ["oculomotor-nucleus-right", "levator-palpebrae-superioris-right"] ],
  "lipdroop":       [    ["face-motor-strip-right",  "lower-facial-muscles-left"],
                         ["face-motor-strip-left",   "lower-facial-muscles-right"] ],
  "consc":          [    ["medullary-reticular-formation-left",  "thalamus-left" ],
                         ["medullary-reticular-formation-right", "thalamus-right"] ],
  "vdrift":         [    ["cerebellar-flocculonodular-lobe-left", "oculomotor-nucleus-medial-left"],
                         ["cerebellar-flocculonodular-lobe-right", "oculomotor-nucleus-medial-right"] ],
  "hdrift":         [    ["cerebellar-flocculonodular-lobe-left", "lateral-gaze-centre-left"],
                         ["cerebellar-flocculonodular-lobe-right", "lateral-gaze-centre-right"] ]

};


/********************************************
 *  Main Message Processing Entry Point
 */
this.onmessage = function(e) {
  // handle messages from the UI
  var d = e.data;      // get any data sent with the message
  switch(d.action){
    case "start":
      // called when UI elements initially displayed
      if(!this.running){
        this.running = true;
        this.lastFrameTime = Date.now();
        this.postMessage({callback:    "setConnectionRequests", 
                          connections: this.connectionList})
        this.initialiseState();
        this.currentTimeout = setTimeout(this.animate,this.UPDATE_INTERVAL);
      }
      break;
    case "stop":
      // called when UI hidden
      this.running=false;      
      cancelTimeout(this.clearTimeout);  
      break;
    case "lookAt":
      // called when the desired eye position changes.
      // this will set the desiredEye value, so that the animate
      // thread will post the appropriate 'moveEyes' message 
      // at a later point in time. 
      if(!this.running){return;} // only do this if running already.
      this.desired[0].x = d.lx;
      this.desired[0].y = d.ly;
      this.desired[1].x = d.rx;
      this.desired[1].y = d.ry;
      // change the desired 'look at' position
      break;
    case "modelChanged":
      // Called when the underlying neurological model 
      // changes. We can then read the model using NeuroSim.testConnection().
      this.model = d.data;
      break;
    case "torch":
      this.light = d.light;
      break;
    case "moveMouth":
      this.desired_mouth = d.m;
      break;
    case "triggerBlink":
      this.externalTriggerBlink = true;
  }
  // console.log('Posting message back to main script');
};

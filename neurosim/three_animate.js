/*jslint white: true, sloppy: true, vars: true, browser: true*/
/*global  $, THREE */

/** 
 * Module for animating a man with neurological problems
 */

var NeuroMan = function(node){
  var my = {};
  my.node = node;
  // my.modelFile = 'human_1.json';
  my.modelFile  = 'humanmodelrigged_physics_sanjay.json';
  
  var container, scene, renderer, camera, light, cube, mixer, action, 
      controls, geom, mesh;
  var WIDTH, HEIGHT, VIEW_ANGLE, ASPECT, NEAR, FAR;
  
  my.initialise = function(){
    container = my.node;
    // container = document.querySelector('.viewport3d');

    var VIEW_ANGLE = 45,
        ASPECT = 1,
        NEAR = 1,
        FAR = 10000;

    scene = new THREE.Scene();
    var clock  = new THREE.Clock();
    renderer = new THREE.WebGLRenderer({antialias: true});

    renderer.setClearColor(new THREE.Color(0xEEEEEE, 1.0));
    renderer.setSize(500,500, HEIGHT);
    renderer.shadowMapEnabled = true;
    renderer.shadowMapSoft = true;
    renderer.shadowMapType = THREE.PCFShadowMap;
    renderer.shadowMapAutoUpdate = true;

    container.appendChild(renderer.domElement);

    camera = new THREE.PerspectiveCamera(VIEW_ANGLE, ASPECT, NEAR, FAR);
    camera.position.set(0.5, 1.5, 2.5);
    camera.lookAt(scene.position);
    scene.add(camera);

    light = new THREE.DirectionalLight(0xffffff);
    light.position.set(0, 100, 60);
    light.castShadow = true;
    light.shadowCameraLeft = -60;
    light.shadowCameraTop = -60;
    light.shadowCameraRight = 60;
    light.shadowCameraBottom = 60;
    light.shadowCameraNear = 1;
    light.shadowCameraFar = 1000;
    light.shadowBias = -0.0001;
    light.shadowMapWidth = light.shadowMapHeight = 1024;
    light.shadowDarkness = 0.7;
    scene.add(light);

    /** allow pan, rotate, zoom */
    controls = new THREE.TrackballControls( camera, container );
    controls.target.set( 0, 0, 0 );
    controls.rotateSpeed = 5.0;
    controls.zoomSpeed = 1.2;
    controls.panSpeed = 0.8;
    controls.noZoom = false;
    controls.noPan = false;
    controls.staticMoving = true;
    controls.dynamicDampingFactor = 0.3;
    my.controls=controls; // keep them for later use?

    // load the model and create everything
    var loader = new THREE.JSONLoader();
    loader.load(my.modelFile, function (geometry, materials) {
      var material;
      mesh = new THREE.SkinnedMesh(   // create a mesh
        geometry,
        new THREE.MeshFaceMaterial(materials)
      );
      material = mesh.material.materials;   // define materials collection
      geom=geometry;

      // enable skinning
      for (var i = 0; i < materials.length; i++) {
        var mat = materials[i];
        mat.skinning = true;
      }

      mixer = new THREE.AnimationMixer( mesh );
      if(geometry.hasOwnProperty("animations") && geometry.animations.length>0){
        mixer.clipAction( geometry.animations[0],mesh ).play() ;
      }
      scene.add( mesh );
      my.scene  = scene; // public variables
      my.mesh   = mesh; 
      my.camera = camera;
      my.geom   = geom;
      my.bone_list  = mesh.skeleton.bones;
      my.bones  = {};
      my.bone_index = {};
      my.bone_list.forEach(function(e,i){
        my.bones[e.name]=e;
        my.bone_index[e.name] = i;
      });
      my.create_interface();
    });


    /** Create any interface elements, if needed */
    my.create_interface = function(){
      if($(".bonemenu").length>0){
        my.bone_list.forEach(function(b){
          var li = $("<li>" + b.name + "</li>");
          li.appendTo($(".bonemenu"));
          li.click(function(){
            my.current_bone = b;
          });
        });
        $(".axismenu li").each(function(){$(this).click(function(){
          my.current_coord = $(this).html();
        });});    
      }
      for(var pname in my.postures){   // for each specified posture
        if(!my.postures.hasOwnProperty(pname)){continue;}
        let pn=pname;  // allow it to be passed to a function as a closure
        let p=my.postures[pname];
        var li = $("<li>" + pname + "</li>");
        li.appendTo($(".posturemenu"));
        li.mousedown(function(){
          //my.set_posture(pn);
          my.undo_acts.push(my.animate_posture(pn));
        });        
        li.mouseup(my.undo_posture);
      }
    };
    my.undo_acts=[];
    my.undo_posture = function(){
      if(my.undo_acts.length>0) { 
        for(var i=0;i<my.undo_acts.length;i++){
          my.undo_acts[i].play();
        }
        my.undo_acts=[];
      }
    };
    /** Bone calculation function */
    my.current_bone=undefined; 
    my.current_time=0;
    my.current_coord='x';
    my.calculate = function(){
      if(my.current_bone===undefined){ my.current_bone=my.bones["heel.L"]; }
      if(++my.current_time>=10){
        my.current_time=0; 
      }
      if(my.current_time<5){ my.current_bone.rotation[my.current_coord]+=0.1; }
      else                 { my.current_bone.rotation[my.current_coord]-=0.1; }
    };
    
    /**
     * Flag to indicate whether the animation is still running.
     * Set to false to stop the animation.
     */  
    my.running=true;
    
    /** Start the updating function right now */
    ( function update () {


      var delta = clock.getDelta();
      var theta = clock.getElapsedTime();

      if( mixer )    { mixer.update( delta ); }
      if( controls ) { controls.update(); }
      if( my.mesh )  { my.calculate(); }

      renderer.render( scene, camera );
      
      if (my.running){
        requestAnimationFrame( update );
      }
    } )();

    my.postures = {
      "sit":[
        {"bone":"pelvis", "rotation":"x", "value": -3.1 },
        {"bone":"shin.L", "rotation":"x", "value": -1.5},
        {"bone":"shin.R", "rotation":"x", "value": -1.5},
        {"bone":"thigh.L", "rotation":"x", "value": 1.5},
        {"bone":"thigh.R", "rotation":"x", "value": 1.5}
      ],
      "biceps-left-ninety":[
        {"bone":"forearm.L", "rotation":"z", "value":1.37 },
        {"bone":"forearm.L", "position":"x", "value":-0.3 },
        {"bone":"forearm.L", "position":"y", "value":-0.4 },
        {"bone":"forearm.L", "position":"z", "value":-0.35 }
      ],
      "biceps-left":[ // elbow flexion
        {"bone":"forearm.L", "rotation":"z", "value":2 },
        {"bone":"forearm.L", "position":"x", "value":-0.2 },
        {"bone":"forearm.L", "position":"y", "value":-0.55 },
        {"bone":"forearm.L", "position":"z", "value":-0.35 }        
      ],
      "deltoid-left":[  // shoulder abduction
        {"bone":"upper_arm.L", "rotation":"x", "value":0.89 },
        {"bone":"upper_arm.L", "rotation":"y", "value":-0.26 },
        {"bone":"upper_arm.L", "rotation":"z", "value":0.68 }                
      ],
      "pectoralis-major-left":[ // shoulder adduction
        {"bone":"upper_arm.L", "rotation":"x", "value":-0.21}
      ],
      "triceps-left":[
        {"bone":"forearm.L", "rotation":"z", "value":-0.13},
        {"bone":"forearm.L", "position":"x", "value":0.05},
        {"bone":"forearm.L", "position":"z", "value":-0.31}
      ],
      "flexor-carpi-radialis-left":[
        {"bone":"hand.L", "rotation":"z", "value":-2.3 }, 
        {"bone":"hand.L", "position":"x", "value":-0.49},
        {"bone":"hand.L", "position":"y", "value":-0.3},
        {"bone":"hand.L", "position":"z", "value":-0.213},
      ],
      "extensor-carpi-radialis-left":[
        {"bone":"hand.L", "rotation":"z", "value":2.5 }, 
        {"bone":"hand.L", "position":"x", "value":0.4},
        {"bone":"hand.L", "position":"y", "value":-0.1},
        {"bone":"hand.L", "position":"z", "value":-0.283},
      ],
      "flexor-digitorum-left":[
        {"bone":"finger_index.01.L", "rotation":"z", "value":1.74 }, 
        {"bone":"finger_index.01.L", "position":"x", "value":0.51 }, 
        {"bone":"finger_index.01.L", "position":"y", "value":0.9 }, 
        {"bone":"finger_index.01.L", "position":"z", "value":-0.15 }, 
        {"bone":"finger_middle.01.L", "rotation":"z", "value":1.74 }, 
        {"bone":"finger_middle.01.L", "position":"x", "value":0.63 }, 
        {"bone":"finger_middle.01.L", "position":"y", "value":1.05 }, 
        {"bone":"finger_middle.01.L", "position":"z", "value":-0.05 }
      ],
      "psoas-left":[
        {"bone":"thigh.L", "rotation":"x", "value":1.8} 
      ],
      "gluteus-left":[
        {"bone":"thigh.L", "rotation":"x","value":-2.3}
      ]
    };
    my.previous_posture = [];
    /**
     *  restore any previous posture 
     */
    my.restore_previous_posture = function(){
      for(var j=0;j<my.previous_posture.length;j++){
        var field;
        if("rotation" in my.previous_posture[j]){
          field="rotation";
        }else{
          field="position";
        }
        my.bones[my.previous_posture[j].bone][field][ my.previous_posture[j][field] ] = 
            my.previous_posture[j].value;
      }
      my.previous_posture = [];      
    };
    /**
     *  move the bones to a new posture.
     *  p is the name of the posture from "my.postures"
     *  keep track of the previous posture.
     */
    my.set_posture = function(p){
      // select the posture, and apply each of the movements in that posture
      for(var i=0;i<my.postures[p].length;i++){
        var field;
        var movt = my.postures[p][i];
        if("rotation" in movt){
          field="rotation";
        }else{
          field="position";
        }
        var prevpos = {"bone":movt.bone,  
                       "value":my.bones[movt.bone][field][movt[field]]
                      };
        prevpos[field]=movt[field]; // set the axis (x/y/z)
        my.previous_posture.push(prevpos); // store previous posture (so it can be restored later)
        // do new movement
        my.bones[movt.bone][field][ movt[field] ] = movt.value;
      }
    };
    /**
     * Use a three animation clip to move to the specified posture, 
     * and back again, automatically
     */
    my.animate_posture = function(p){
      var kt_forward = [], kt_backward = []; // keyframe tracks
      for(var i=0;i<my.postures[p].length;i++){ // each movt in posture
        var field,  movt = my.postures[p][i];  // get movt
        if("rotation" in movt){
          field="rotation";
        }else{
          field="position";
        }
        var  currval = my.bones[movt.bone][field][movt[field]], // current value of the movt field
             boneid  = movt.bone, //my.bone_index[movt.bone],
             mname   = ".bones["+boneid+"]."+field+"["+movt[field]+"]",
             destval = movt.value;
        
        kt_forward.push( new THREE.NumberKeyframeTrack(mname, [0,1], [currval, destval]));
        kt_backward.push(new THREE.NumberKeyframeTrack(mname, [0,1], [destval, currval]));
      }; // next movt
      var act   = mixer.clipAction( new THREE.AnimationClip(p,1,kt_forward,THREE.InterpolateSmooth) ),
          unact = mixer.clipAction( new THREE.AnimationClip(p,1,kt_backward,THREE.InterpolateSmooth) );
      act.setLoop(THREE.LoopOnce);  act.reset();
      unact.setLoop(THREE.LoopOnce); unact.reset();
      act.play();
      return unact;
    };
  }; // initialise
  return my;
};
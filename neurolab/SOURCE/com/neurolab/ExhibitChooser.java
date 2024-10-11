//ExhibitChooser.class by Sanjay Manohar
package com.neurolab;

/*
1.2	thick borders added to buttons
2.1	images loaded via getImage(String)
*/



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import com.neurolab.common.NeurolabExhibit;

public class ExhibitChooser extends NeurolabExhibit implements ActionListener{
	private String[][] exhibits={
		// name1,	name 2,		classname	// .class, .gif files have this name
		{"Cell ionic",	"equilibria",	"MembranePotentials"},
		{"Action",	"potentials",	"ActPots"},
		{"Compound",	"A.P.",		"CompoundAP"},
		{"Passive",	"currents",	"PassiveCurrents"},
		{"Time",	"constants",	"TimeConstants"},
		{"Conduction",	"velocity",	"ConductionVelocity"},
		{"Sensory",	"adaptation",	"Adaptation"},
		{"Lateral",	"inhibition",	"LateralInhibition"},

		{"Synaptic",	"interaction",	"SynapticInteraction"},
		{"Pacinian",	"corpuscle",	"PacinianCorpuscle"},
		{"Sound &",	"hearing",	"SoundAndHearing"},
		{"Basilar",	"membrane",	"BasilarMembrane"},
		{"Phase",	"locking",	"PhaseLocking"},
		{"Vowels"	,"",		"Vowels"},
		{"Interaural",	"delay",	"BinauralCoincidence"},
		{"Olfactory",	"recognition",	"OlfactoryCoding"},

		{"Visual",	"optics",	"VisualOptics"},
		{"Line",	"Spread",		"LineSpread"},
		{"Retinal",	"receptors",	"RetinalReceptors"},
		{"Blind",	"spot",		"BlindSpot"},
		{"Horizontal",	"cells",	"HorizontalCells"},
		{"Receptive",	"fields",	"ReceptiveFields"},
		{"Colour",	"mixing",	"ColourMixing"},
		{"Line",	"learning",	"LineLearning"},

		{"Postural",	"equilibrium",	"PosturalEquilibrium"},
		{"Stretch",	"reflex",	"StretchReflex"},
		{"Control",	"systems",	"ControlSystems"},
		{"Parametric",	"feedback",	"ParametricFeedback"},
		{"Cerebellar",	"dysmetria",	"CerebellarDysmetria"},
		{"Cerebellar",	"learning",	"CerebellarLearning"},
		{"Neural",	"network",	"NeuralNetwork"},
		{"Eye",		"movements",	"EyeMovements"},

		{"Brain",	"anatomy",	"BrainAnatomy"},
		{"Spinal",	"tracts",	"SpinalTracts"},
		{"Cortical",	"regions",	"CorticalAreas"},
		{"Anatomical",	"pathways",	"AnatomicalPathways"},
		{"Hypo-",	"thalamus",	"Hypothalamus"},
		{"Pavlovian",	"conditioning",	"PavlovianConditioning"},
		{"Motivation",	"model",	"MotivationalMaps"},
	//	{"Quit",	"",		""} Removed Useless Quit button *ods20
	};
	private String iconpath="resources/icons/";
	private String classprefix="com.neurolab.";
	private Image[] bitmap;
	private Dimension gridsize;
	public void init(){
		super.init();
		bitmap=new Image[exhibits.length];
		for(int i=0;i<exhibits.length;i++){
			if(exhibits[i][0]!="")
				bitmap[i]=getImage(iconpath+exhibits[i][2]+".GIF");
		}	// grab images to memory
		gridsize=new Dimension(7,6);
		GridLayout gl=new GridLayout(gridsize.width,gridsize.height);
		gl.setHgap(2);gl.setVgap(2);
		getMainContainer().setLayout(gl);
		createComponents();
		// double-click on background to allow selecting the plaf.
		getMainContainer().addMouseListener(new MouseAdapter() {public void mouseClicked(MouseEvent e) {if(e.getClickCount()==2) NeurolabExhibit.plafset=false;}});
	}
	PictureButton[] button;
	public void createComponents(){
		button=new PictureButton[exhibits.length];
		for(int i=0;i<exhibits.length;i++){
			if(exhibits[i][0]!=""){
				button[i]=new PictureButton(bitmap[i],exhibits[i][0],exhibits[i][1]);
			}else
				button[i]=new PictureButton(null,exhibits[i][0],exhibits[i][1]);
			getMainContainer().add(button[i]);

			button[i].addActionListener(this);
			button[i].setActionCommand(exhibits[i][2]);	//sends classname to listener
		}
	}
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand()!=""){
		getHolder().setExhibit(classprefix+e.getActionCommand());
		}
	}

	public String getExhibitName(){
		return "";
	}
	boolean DRAW_BEVEL=false; // Set this to false if the Look and Feel does an 
	                          // appropriate 3d button rendering
	
	public class PictureButton extends JButton{
		Image image;
		String text1,text2;
		Font font;

		public final int inset=7,bt=2;
		public PictureButton(Image im,String t1,String t2){
			super();
			image=im;
			text1=t1;
			text2=t2;
			setBackground(systemGray);
                        this.setMargin(new Insets(bt,bt,bt,bt));
			font=getFont();
			font=new Font(font.getName(),0,font.getSize()-1);
      this.setFont(font);
 		}
		public void paintComponent(Graphics g){
                  super.paintComponent(g);
//                  antiAlias(g);
      if(DRAW_BEVEL) {            
  			g.setColor(Color.white);	//hilite
  			g.drawLine(bt,bt,getWidth()-bt*2,bt);
  			g.drawLine(bt,bt,bt,getHeight()-bt*2);
  			g.setColor(Color.gray);		//shadow
  			g.drawLine(bt,getHeight()-bt*2,getWidth()-bt*2,getHeight()-bt*2);
  			g.drawLine(getWidth()-bt*2,bt,getWidth()-bt*2,getHeight()-bt*2);
      }

			if(image!=null) g.drawImage(image,inset,inset,getHeight()-2*inset,getHeight()-2*inset,this);
			g.setColor(Color.black);
			g.setFont(font);
			g.drawString(text1,getWidth()/2-inset,15);
			g.drawString(text2,getWidth()/2-inset,30);
 		}
                 public boolean imageUpdate(Image i, int f, int x, int y,
                                            int w, int h){
                   if( (f & ALLBITS) >0 ){repaint();return false;} else return true;
                 }
	}

  public void close(){  }
}

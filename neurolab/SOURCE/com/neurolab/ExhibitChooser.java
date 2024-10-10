//ExhibitChooser.class by Sanjay Manohar
package com.neurolab;

/*
1.2	thick borders added to buttons
2.1	images loaded via getImage(String)
*/



import java.awt.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.net.*;

import com.neurolab.common.NeurolabExhibit;
import java.awt.image.ImageObserver;
//import com.neurolab.common.HeldExhibit;	// needs to start other exhibits!

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
		{"Sound and",	"hearing",	"SoundAndHearing"},
		{"Basilar",	"membrane",	"BasilarMembrane"},
		{"Phase",	"locking",	"PhaseLocking"},
		{"Vowels"	,"",		"Vowels"},
		{"Interaural",	"delay",	"BinauralCoincidence"},
		{"Olfactory",	"recognition",	"OlfactoryCoding"},

		{"Visual",	"optics",	"VisualOptics"},
		{"Linespread",	"",		"LineSpread"},
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
				bitmap[i]=getImage(iconpath+exhibits[i][2]+".gif");
		}	// grab images to memory
		gridsize=new Dimension(7,6);
		GridLayout gl=new GridLayout(gridsize.width,gridsize.height);
		gl.setHgap(2);gl.setVgap(2);
		getMainContainer().setLayout(gl);
		createComponents();
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
			font=new Font("Arial",Font.BOLD,9);
                        this.setFont(font);
 		}
		public void paintComponent(Graphics g){
                  super.paintComponent(g);
                  antiAlias(g);
			g.setColor(Color.white);	//hilite
			g.drawLine(bt,bt,getWidth()-bt*2,bt);
			g.drawLine(bt,bt,bt,getHeight()-bt*2);
			g.setColor(Color.gray);		//shadow
			g.drawLine(bt,getHeight()-bt*2,getWidth()-bt*2,getHeight()-bt*2);
			g.drawLine(getWidth()-bt*2,bt,getWidth()-bt*2,getHeight()-bt*2);

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
}

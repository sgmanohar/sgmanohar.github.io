
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.kinetic;

import com.cudos.common.*;
import java.awt.*;
import java.util.Random;

public class Molecule implements PaintableComponent,Ligand,MoleculeListener{
	public static final int WATER=0;
	public static final int SUGAR=1;
	public static final int SODIUM=2;
	public static final int POTASSIUM=3;
	public static final int MEMBRANE_PROTEIN=4;
	public static final int PROTEIN=5;
	public static final int CELL=6;
	public static final String[] names={	"Water",	"Sugar",	"Sodium",	"Potassium",	"Membrane Protein","Protein", 	"Cell"};
	public static final Color[] cols={	Color.blue,	Color.magenta,	Color.red,	Color.blue,	Color.orange,	Color.gray,	Color.white};
	public static final int[] defsize={	2,		8,		6,		6,		7,		7,		15};
	public static final boolean[] hydrs={	false,		true,		true,		true,		false,		false,		false};
  public Color bg=new Color(192,224,224);
  public int x,y,vx,vy;
  public int s=2;		//size
  private int type=WATER;


	protected Molecule ligand=null;					//interface ligand methods
	public Molecule getLigand(){return ligand;}
	public void setLigand(Molecule l){ligand=l;}
	boolean bound=false;
	public boolean isBound(){return bound;}
	public void setBound(boolean b){
		if(bound && b)throw new RuntimeException("Ligand already bound!");
		bound=b;
	}
	protected boolean waterBinding=false;
	public boolean isWaterBinding(){return waterBinding;}
	public void setWaterBinding(boolean b){waterBinding=b;}

  public Molecule() {
	this(10,10);
  }
	public Molecule(int nx,int ny){
		this(nx,ny,1,1);
	}
	public Molecule(int nx,int ny, int nvx, int nvy){
		x=nx;y=ny;
		vx=nvx;vy=nvy;
	}
	public Molecule(Random r,Rectangle b){
		x=r.nextInt((int)b.getWidth())+(int)b.getX();
		y=r.nextInt((int)b.getHeight())+(int)b.getY();
		vx=r.nextInt(30/s)-(15/s);
		vy=r.nextInt(30/s)-(15/s);
		if(vx==0)vx++;if(vy==0)vy++;	// molecules going straight up/down look silly
	}
	public void setType(int t){
		type=t;
		s=defsize[type];
		waterBinding=hydrs[type];
		if(type==POTASSIUM){
			shape=new Polygon(new int[]{0,s,s/2},new int[]{0,0,s},3);
		}
	}
	public int getType(){return type;}
	public Polygon shape=null;
	public void paint(Graphics2D g,boolean draw){
		if(draw)g.setColor(cols[type]);
		else g.setColor(bg);
		switch(type){
			case WATER:
				g.fillRect(x,y,s,s);
				break;
			case SUGAR:
				g.fillRect(x,y,s,s);
				break;
			case SODIUM:
				g.fillOval(x,y,s,s);
				break;
			case POTASSIUM:
				shape.translate(x,y);
				g.fill(shape);
				shape.translate(-x,-y);	//cumbersome... better way which is still memory efficient on other mols?
				break;
			case PROTEIN:
				g.fillOval(x,y,s,s);
				break;
		}
	};
	public void move(Graphics2D g,int nx,int ny){
		if((bound && ligand.getType()==Molecule.MEMBRANE_PROTEIN))return;
		paint(g,false);
		x=nx;y=ny;
		paint(g,true);
		if(ligand!=null)unbind();
	}
	public void unbind(){
		if(bound &&ligand.getType()==Molecule.MEMBRANE_PROTEIN)return;	//!!
		if(bound && ligand.getLigand()==this){	// remove it from other ligands
			ligand.setLigand(null);
			ligand.setBound(false);
			if(ligand.getMoleculeListener()==this){	//if i am listening,
				ligand.setMoleculeListener(null);	//remove me from the queue
			}
			if(listener!=null)listener=null;	//remove others from my listener queue
			bound=false;ligand=null;
		}
	}

	Random rand=new Random();
	public boolean moleculeEvent(Molecule m){
//		if(m.getType()==Molecule.WATER){
			if(m instanceof MassiveMolecule && this instanceof MassiveMolecule){
				MassiveMolecule mol=(MassiveMolecule)m,Mol=(MassiveMolecule)this;
				mol.x=Mol.x-mol.s;
				mol.y=Mol.y;
			}else{
				m.x=x-m.s;
				m.y=y;
			}
			if(rand.nextInt(200)==0){	//wants to detatch
				m.setMoleculeListener(null);	//I wont to water listen anymore
				m.setBound(false);m.setLigand(null);
				bound=false;ligand=null;
				m.x=x;m.y=y;	//ensure it is not stranded somewhere
			}
//		}
		return true;	// allow me to move the molecule
	}

	MoleculeListener listener=null;
	public MoleculeListener getMoleculeListener(){return listener;}
	public void setMoleculeListener(MoleculeListener m){listener=m;}
	public boolean fireMoleculeEvent(){
		if(listener!=null)return listener.moleculeEvent(this);
		else return false;
	}	//send an event from self...

}
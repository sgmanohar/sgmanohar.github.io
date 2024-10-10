
/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.waves;

import java.awt.*;
import com.cudos.common.*;
import java.awt.event.*;

public class RippleSource extends DraggableComponent /*implements MoveableComponent,DragGestureListener /*,Transferable*/{
	static int sid=0;
	int id;
//	private DragSourceListener parent;
//	public void setParent(DragSourceListener p){parent=p;}
/*	public Object getTransferData(DataFlavor df){
		return this;
	}
	DataFlavor sourceflavor=new DataFlavor(this.getClass(),"Ripple Source");
	public DataFlavor[] getTransferDataFlavors(){
		return new DataFlavor[]{sourceflavor};
	}
	public boolean isDataFlavorSupported(DataFlavor df){
		if(df==sourceflavor)return true;else return false;
	}
	public void dragGestureRecognized(DragGestureEvent dge){
		DragSource.getDefaultDragSource().startDrag(dge,DragSource.DefaultMoveDrop,new StringSelection(name),parent);
	}

*/
//	public int x,y;

	double frequency;	//cycles per tick
	double amplitude;	//0-1
	public void setFrequency(double f){frequency=f;}
	public double getFrequency(){return frequency;}
	public void setAmplitude(double a){amplitude=a;}
	public double getAmplitude(){return amplitude;}
	public RippleSource(int x,int y){
		this(x,y,0.1,0.5);
	}
	RippleSource me=this;
	public RippleSource(int u,int v,double f,double a){
		setLocation(u-2,v-2);
		setSize(10,10);
		frequency=f;amplitude=a;
		id=sid++;	//serial number
		name="Source"+sid;
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				((RippleTank)getParent()).setSelected(me);
			}
		});
	}
	public RippleSource() {
		this(60,60);
	}
	int tick=0;
	public float getNextValue(){
		return (float)(amplitude*127*Math.sin(tick++*frequency*2*Math.PI));
	}
	public void paint(Graphics g){
		g.setColor(new Color(128,0,0));
		g.fillOval(0,0,getWidth()-1,getHeight()-1);
		if(selected){
			g.setColor(Color.yellow);
			g.drawOval(0,0,getWidth()-1,getHeight()-1);
		}
	}
/*	public void moveto(int dx,int dy){
//		x+=dx;y+=dy;
//		setLocation(getX()+dx,getY()+dy);
	}
*/

	public static final int TYPE_POINT=0,TYPE_LINE=1;
	int type;
	public void setType(int t){
		type=t;
	}
	public int getType(){return type;}
}
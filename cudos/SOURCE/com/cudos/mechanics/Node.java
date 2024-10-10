
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.mechanics;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Node extends BaseComponent{

	public Node() {
		createPanel();
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		createPanel();
	}

	transient JPanel panel;
	transient JSlider massSlider;
	transient JLabel massLabel;
	transient JCheckBox fixedcheck;
	void createPanel(){
		panel=new JPanel();
		massLabel=new JLabel("Mass");
		massSlider=new JSlider();
		fixedcheck=new JCheckBox("Fixed");
		massSlider.setMinimum(1);
		massSlider.setMaximum(500);
		massSlider.setPreferredSize(new Dimension(100,30));
		massSlider.setValue((int)(mass*100));
		panel.add(massLabel);
		panel.add(massSlider);
		panel.add(fixedcheck);
		massSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				mass=massSlider.getValue()/100.;
			}
		});
		fixedcheck.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fixed=fixedcheck.isSelected();
			}
		});
	}


	public int x;
	public double y;
	public double mass=1;
	private Vector components=new Vector();

	public double getY(){return y;}
	public void setY(double d){
		for(int i=0;i<components.size();i++){
			MechanicsComponent c=(MechanicsComponent)components.get(i);
			double dl=0;
			if(this==c.top){
				dl=y-d;
			}else{
				dl=d-y;
			}
			if(!c.canResizeBy(dl))return;
		}
		y=d;
	}

	public void addComponent(MechanicsComponent c){
		components.add(c);
		setPos();
	}
	public void removeComponent(MechanicsComponent c){
		components.remove(c);
		setPos();
	}
	public int nComponents(){return components.size();}
	public Vector getComponents(){return components;}
	public void setPos(){
		if(components.size()==0)return;
		if(components.size()==1){
			x=((MechanicsComponent)components.get(0)).x;
		}else{
			int totalx=0;
			for(int i=0;i<components.size();i++) totalx+=((MechanicsComponent)components.get(i)).x;
			x=totalx/components.size();
		}
	}

	Point getPoint(){return new Point(x,(int)y);}
	int radius=5;
	int labelx=25;
	int leftLabelx=-25;
	double forceScale=0.33;

	boolean drawExternalForces=true, drawInternalForces=true;

	public void paint(Graphics g){
		Point c=getPoint();
		g.fillOval(c.x-radius, c.y-radius, radius*2, radius*2);
		if(drawExternalForces && externalForce!=0){
			Point end=new Point(c.x+labelx, (int)(c.y+externalForce*forceScale));
			g.setColor(Color.blue);
			drawForceArrow(g,c.x+labelx, c.y, end.x, end.y);
			String string=String.valueOf( Math.abs(((int)(externalForce*10))/10.) );
			g.drawString( string, c.x+labelx+radius, c.y);
		}
		if(drawInternalForces && internalForce!=0){
			Point end=new Point(c.x+leftLabelx, (int)(c.y+internalForce*forceScale));
			g.setColor(new Color(0,128,0));
			drawForceArrow(g,c.x+leftLabelx, c.y,end.x,end.y);
			String string=String.valueOf( Math.abs(((int)(internalForce*10)/10)) );
			g.drawString(string, c.x+leftLabelx-radius-20, c.y);
		}
	}
	public void drawForceArrow(Graphics g, int x1,int y1,int x2,int y2){
		((Graphics2D)g).setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		int bhat=(y2-y1)>0? 1:-1;
		g.fillPolygon(new int[]{x2, x2-radius/2, x2+radius/2},
						new int[]{y2, y2-radius*bhat, y2-radius*bhat}, 3);
	}
	public boolean contains(Point p){
		return p.distance(getPoint())<radius;
	}
	public JPanel getPanel(){return panel;}

	double force=0 , externalForce, internalForce;
	double v=0;
	boolean fixed=false;

	static double dt=0.1, g=9.8;
	static boolean gravity=true;
	/** called by other components to exert a force on the node*/
	public void pull(double forceDownwards){
		force+=forceDownwards;
	}
	/** called by other components to exert an impulse on the node*/
	public double getImpulsiveStop(Node relativeTo){
		double dv=(v-relativeTo.v);
		return dv*mass/dt;
	}

	/** called by the panel once in the cycle*/
	public void move(){
		externalForce=0;
		if(gravity) externalForce+=mass*g;
		//literally, force from fixation point..
		if(fixed) {
			externalForce+= -force-externalForce -v/dt*mass;
		}
		force+=externalForce;
		double dv=dt*force/mass;
		v+=dv;
		y+=dt*v;
		//consume force
		internalForce=force;
		force=0;
	}

}
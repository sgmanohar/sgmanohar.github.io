
package com.cudos.photon;

import com.cudos.common.DraggableComponent;

import java.awt.*;

public class Photon extends DraggableComponent {
	public Photon() {
		setSize(new Dimension(15,15));
	}
	public void paint(Graphics g){
		g.setColor(Color.yellow);	//will eventually get from spectrum
		int b=(wavelength-400)*255/300;
		b=Math.max(Math.min(b,255),0);
		g.setColor(new Color(b,b,b));
		g.fillOval(0,0,getWidth()-1,getHeight()-1);
		g.setColor(Color.black);
		g.drawOval(0,0,getWidth()-1,getHeight()-1);
	}


	int wavelength;
	int vx=10,vy=0;

	public int getWavelength(){
		return wavelength;
	}
	public void setWavelength(int w){
		wavelength=w;
	}


	public void move(){
		setLocation(getX()+vx,getY()+vy);
	}
}
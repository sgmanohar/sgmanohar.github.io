
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.wind;

import java.awt.*;

public class Particle {
	public double x,y,vx,vy;
	WindTunnel tunnel;
	public Particle(WindTunnel wt) {
		this(wt,10,20);
	}
	public Particle(WindTunnel wt,int px,int py){
		this(wt,px,py,0,0);
	}
	public Particle(WindTunnel wt,int px,int py,int pvx,int pvy){
		tunnel=wt;
		x=px;y=py;
		vx=pvx;vy=pvy;
	}
	public void paint(Graphics g){
		x+=vx;y+=vy;
		g.setColor(Color.magenta);
		g.fillOval((int)x,(int)y,5,5);
	}
}
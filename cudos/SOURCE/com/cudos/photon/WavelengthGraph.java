
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cudos.photon;

import com.cudos.common.*;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;

public class WavelengthGraph extends GraphPaper {


	int minW=300, maxW=800;
	public WavelengthGraph() {
	setXRange(minW, maxW);
	setYRange(0,1);
	}
	public Vector receptors=new Vector(), sources=new Vector();
	Color[] color = {Color.blue, Color.green, Color.red};

	int dw=10;
	public void paint(Graphics g){
		super.paint(g);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		//for each photoreceptor, plot the absorption against the wavelength
		for(int i=0;i<receptors.size();i++){
			g.setColor(color[i%color.length]);
			Photoreceptor r=(Photoreceptor)receptors.get(i);
			int ow=minW; double oa=r.getAbsorption(ow),a;
			for(int w=minW;w<maxW;w+=dw){
				a=r.getAbsorption(w);
				drawLine(g,ow,oa,w,a);
				ow=w;oa=a;
			}
		}
		//for each source, plot the frequency and amplitude
		for(int i=0;i<sources.size();i++){
			PhotonSource s=(PhotonSource)sources.get(i);
			int w=s.getWavelength();
			g.setColor(Spectrum.getColorForWavelength( w ));
			g.fillRect(xS(w)-20, yS(0), 40, yS(s.getIntensity()));
			drawLine(g,w,0,w,s.getIntensity()*10);
			Point rr=toScreen(new Point2D.Double(w,10*s.getIntensity()));
			if(s.isRunning())drawArrow(g,rr.x,getHeight()-rr.y);
		}
	}
	void drawArrow(Graphics g,int x, int s){
		g.fillRect(x-s/9,0,2*s/9,s);
		g.fillPolygon(new int[]{x-s/5,x+s/5,x},new int[]{s,s,s+10},3);
	}
}

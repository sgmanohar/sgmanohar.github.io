
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import java.awt.*;

public class Spectrum {

	public Spectrum() {
	}
	static Object[][] data={
		{new Integer(300), Color.blue},
		{new Integer(400), Color.green},
		{new Integer(500), Color.yellow},
		{new Integer(600), Color.red},
		{new Integer(700), Color.magenta},
	};
	public static Color getColorForWavelength(int wavelength){
		Color previous=Color.black;
		int prevw = 0;
		for(int i=0;i<data.length;i++){
			int w = ((Integer)data[i][0]).intValue();
			Color c = (Color)data[i][1];
			if(wavelength < w){
				float fraction = (wavelength-prevw)/(float)(w - prevw);
				return mix( fraction, previous, c );
			}
			previous = c;
			prevw = w;
		}
		return mix( (wavelength - prevw)/(float)(1000-prevw), previous, Color.black);
	}
	/** f is distance from a towards b */
	static Color mix(float f, Color a, Color b){
		return new Color(
			(int)Math.min(Math.max(0,a.getRed() + f*(b.getRed() - a.getRed())),255),
			(int)Math.min(Math.max(0,a.getGreen() + f*(b.getGreen() - a.getGreen())),255),
			(int)Math.min(Math.max(0,a.getBlue() + f*(b.getBlue() - a.getBlue())),255)
		);
	}
}
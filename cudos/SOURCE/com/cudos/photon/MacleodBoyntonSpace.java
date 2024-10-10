package com.cudos.photon;

import java.awt.color.*;

class MacleodBoyntonSpace extends ColorSpace{
	float rt3=(float)Math.sqrt(3), rt2=(float)Math.sqrt(2);
	float[] whitepoint=new float[]{0.5f,0.5f,0.5f},
		ushort=new float[]{-1/rt3,0.6f/rt3,1/rt3},
		ulong=new float[]{1/rt2,-1/rt2,0};


	public float[] fromCIEXYZ(float[] colorvalue){
	//      Transforms a color value assumed to be in the CS_CIEXYZ conversion color space into this ColorSpace.
		return colorvalue;
	}

	public float[] fromRGB(float[] rgbvalue){
	//	Transforms a color value assumed to be in the default CS_sRGB color space into this ColorSpace.
		return rgbvalue;
	}


	public float[] toCIEXYZ(float[] colorvalue){
	//      Transforms a color value assumed to be in this ColorSpace into the CS_CIEXYZ conversion color space.
		return colorvalue;
	}

	public float[] toRGB(float[] colorvalue){
	//      Transforms a color value assumed to be in this ColorSpace into a value in the default CS_sRGB color space.
		float r[]= new float[3];
		float ampl=0;
		for(int i = 0; i < 3; i++){
			r[i] = (whitepoint[i] +
				colorvalue[1] * ushort[i] + colorvalue[2] * ulong[i]);
			ampl += r[i];
		}

		float scale = 3 * colorvalue[0] / ampl;
		for(int i = 0; i < 3; i++)
			r[i] *= scale;

		return r;
	}


	static final String[] names=new String[]{"Brightness","Shortwave","Longwave"};
	public String getName(int idx){
	//      Returns the name of the component given the component index
		return names[idx];
	}


	public boolean isCS_sRGB(){
	//      Returns true if the ColorSpace is CS_sRGB.
		return false;
	}

	public MacleodBoyntonSpace(){
		super(TYPE_Luv, 3);
	}
}

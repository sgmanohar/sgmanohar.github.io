package com.cudos.common.function;

public class SineFunction implements Function{
	Double	amplitude=new Double(1),
		frequency=new Double(1);

	public Double[] getParameter(){return new Double[]{amplitude,frequency};}
	public String[] getParameterName(){return new String[]{"Amplitude","Frequency"};}
	public double getY(double x){return amplitude.doubleValue()*Math.sin(frequency.doubleValue()*Math.PI*2*x);}
}

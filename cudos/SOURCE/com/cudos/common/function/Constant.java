package com.cudos.common.function;

public class Constant implements Function{
	Double value=new Double(0);
	public Double[] getParameter(){return new Double[]{value};}
	public String[] getParameterName(){return new String[]{"Value"};}
	public double getY(double x){return value.doubleValue();}
}
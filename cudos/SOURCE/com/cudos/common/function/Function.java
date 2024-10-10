package com.cudos.common.function;
interface Function{
	public double getY(double x);
	public Double[] getParameter();		//list of pointers to parameters
	public String[] getParameterName();	//list of names for parameters
}

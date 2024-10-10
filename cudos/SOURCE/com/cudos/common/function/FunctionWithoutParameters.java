package com.cudos.common.function;

public abstract class FunctionWithoutParameters implements Function{
	public Double[] getParameter(){return new Double[0];}
	public String[] getParameterName(){return new String[0];}
}
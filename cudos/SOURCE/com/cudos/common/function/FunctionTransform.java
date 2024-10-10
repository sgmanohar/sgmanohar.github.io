package com.cudos.common.function;

public abstract class FunctionTransform extends ConcatenatedFunction{
	protected Function function;

	public void setInputFunction(Function f){
		function=f;
		super.setElem(new Function[]{function});
	}
	public Function getInputFunction(){return function;}

	public FunctionTransform(){
		super.setElemName(new String[]{"InputFunction"});
	}
	public void setElem(Function[] f){}
	public Function[] getElem(){return null;}
	abstract public double getY(double x);
}

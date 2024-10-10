package com.cudos.common.function;

public class IntegralTransform extends FunctionTransform{
	double delta=0.1;

	public double getY(double x){
		double I=0;
		for(double xi=0;xi<x;x+=delta){
			I+=delta*function.getY(xi);
		}
		return I;
	}
}

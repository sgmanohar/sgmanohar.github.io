package com.cudos.common.function;

public class DifferentialTransform extends FunctionTransform{
	double delta=0.001;
	int maxIter;

	public double getY(double x){
		int iter=0;
		double y=function.getY(x);
		double dy=10,dx=0.1, oD=10,D;
		while( (Math.abs((D=dy/dx)-oD) > delta)
			& ++iter<maxIter ){
			oD=D;
			dx/=2;
			dy=function.getY(x+dx)-y;
		}
		return D;
	}
}

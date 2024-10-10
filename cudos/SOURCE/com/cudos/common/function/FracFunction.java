package com.cudos.common.function;

public class FracFunction extends FunctionWithoutParameters{
	public double getY(double x){
		return x-Math.floor(x);
	}
}
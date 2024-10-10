package com.cudos.common.function;

public class ProductFunction extends ConcatenatedFunction{
	public double getY(double x){
		double product=0;
		for(int i=0;i<elem.length;i++){
			product*=elem[i].getY(x);
		}
                return product;
	}
}

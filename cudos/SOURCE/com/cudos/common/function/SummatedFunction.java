package com.cudos.common.function;

public class SummatedFunction extends ConcatenatedFunction{
	public double getY(double x){
		double sum=0;
		for(int i=0;i<elem.length;i++){
			sum+=elem[i].getY(x);
		}
                return sum;
	}
}

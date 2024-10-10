package com.cudos.common.function;

import com.cudos.common.function.FunctionWithoutParameters;


public class FastFourier extends FunctionWithoutParameters {
	public FastFourier(int nBuckets){
		nB=nBuckets;
		bucket=new double[nBuckets];
	}
	public double[] bucket;
	private int nB;

	public void zero(){
		for(int i=0;i<nB;i++)bucket[i]=0;
	}
	public void tick(double value){
		for(int i=0;i<nB;i++){
			bucket[i]+=value;
			bucket[i]*=(i/nB);
		}
	}
	public double getY(double x){
		return bucket[(int)(x*nB)];
	}
}

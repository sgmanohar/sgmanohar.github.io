package com.cudos.common.function;

public class ConcatenatedFunction implements Function{
	protected Function[] elem;
	protected String[] elemName;
	protected int nParams;


		//getters and setters

	public String[] getElemName(){return elemName;}
	public void setElemName(String[] e){elemName=e;}

	public Function[] getElem(){return elem;}
	public void setElem(Function[] e){
		elem=e;
		nParams=0;
		for(int i=0;i<e.length;i++){
			nParams+=elem[i].getParameter().length;
		}
	}
			//parameter overrides
	public Double[] getParameter(){
		Double[]p;
		p=new Double[nParams];		//final list of parameters
		int j=0;			//index in p
		for(int i=0;i<elem.length;i++){	//index in elem
			Double[] ep=elem[i].getParameter();
			for(int k=0;k<ep.length;k++){	//index in ep
				p[j++]=ep[k];		//add each parameter to p
			}
		}
		return p;
	}

	public String[] getParameterName(){
		String[] n;
		String[] p=new String[nParams];
		int j=0;
		for(int i=0;i<elem.length;i++){
			String[] epn=elem[i].getParameterName();
			String en=elemName[i]+".";
			for(int k=0;k<en.length();k++){
				p[j++]=en+epn[k];
			}		//construct hierarchical parameter name strings
		}
		return p;
	}


		//default concatentation operator

	public double getY(double x){
		double transfer=x;
		for(int i=0;i<elem.length;i++){
			transfer=elem[i].getY(transfer);
		}
		return transfer;
	}
}

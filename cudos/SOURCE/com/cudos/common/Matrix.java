
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

public class Matrix {

	public Matrix() {
	}
	public double[][] a;
	public Matrix(double[][] b){
		a=b;
	}
	public Matrix inverse(){
		if(a.length==0)return null;
		if(!isSquare())return null;
		double d=determinant();
		if(d==0)return null;
		Matrix m;
		if(a.length==1) m=new Matrix(new double[][]{{1}});else
			m=cofactorMatrix().transpose();
		return m.multiply(1/d);
	}
	public Matrix cofactorMatrix(){
		if(!isSquare())return null;
		double[][] b=new double[a.length][a.length];
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a.length;j++){
				b[i][j]=parity(i+j)*cofactor(i,j);
			}
		}
		return new Matrix(b);
	}
	public Matrix multiply(double f){
		double[][] b=new double[a.length][a[0].length];
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[0].length;j++){
				b[i][j]=f*a[i][j];
			}
		}
		return new Matrix(b);
	}
	public Matrix multiply(Matrix m){
		int p=a[0].length;
		if(p!=m.a.length) return null;
		double b[][]=new double[a.length][m.a[0].length];
		for(int i=0;i<a.length;i++){
			for(int j=0;j<m.a[0].length;j++){
				b[i][j]=0;
				for(int k=0;k<p;k++){
					b[i][j]+=a[i][k]*m.a[k][j];
				}
			}
		}
		return new Matrix(b);
	}
	public Matrix transpose(){
		double[][] b=new double[a[0].length][a.length];
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[0].length;j++){
				b[j][i]=a[i][j];
			}
		}
		return new Matrix(b);
	}
	public boolean isSquare(){return a.length==a[0].length;}
	public double cofactor(int i,int j){
		Matrix com=coMatrix(i,j);
		if(com==null)return Double.NaN;else
		return com.determinant();
	}
	public Matrix coMatrix(int i,int j){
		if(!isSquare())return null;
		if(a.length==1)return null;
		double[][] c=new double[a.length-1][a.length-1];
		for(int x=0;x<c.length;x++){
			for(int y=0;y<c.length;y++){
				c[x][y]=a[x+((x>=i)?1:0)][y+((y>=j)?1:0)];
			}
		}
		return new Matrix(c);
	}
	public double determinant(){
		if(!isSquare())return Double.NaN;
		if(a.length<1)return 0;
		if(a.length<2)return a[0][0];
		double total=0;
		for(int i=0;i<a.length;i++){
			total+=parity(i)*a[0][i]*cofactor(0,i);
		}
		return total;
	}
	public int parity(int x){return 1-(x%2)*2;}	//returns +/-

}
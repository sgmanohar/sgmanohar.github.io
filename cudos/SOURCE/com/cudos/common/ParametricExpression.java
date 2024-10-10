package com.cudos.common;

import evaluator.*;

/**
 * represent a polar equation
 */

public class ParametricExpression extends Expression {

	String parameter = "t";
	public double getX() throws StackException, MathException{
		return xExpr.value();
	}
	public double getY() throws StackException, MathException{
		return value();
	}
	private Expression xExpr;
	public ParametricExpression(String x, String y)  throws ParseException{
		super(y);
		xExpr = new Expression(x);
	}
	public String getXDefinition(){return xExpr.getDefinition();}
	public String getYDefinition(){return getDefinition();}
	String tMinimum = "-2", tMaximum = "2";
	public String toString(){ return "x="+getXDefinition()+";y="+getYDefinition(); }
}

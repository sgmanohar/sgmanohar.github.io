package com.cudos.common;

import evaluator.*;

/**
 * An expression representing a polar.
 */

public class PolarExpression extends Expression {
	public double getR() throws StackException, MathException{
		return value();
	}
	public PolarExpression(String radius) throws ParseException{
		super(radius);
	}
	String tMinimum = "-PI", tMaximum = "PI";
	public String toString(){ return "r="+super.toString(); }
}
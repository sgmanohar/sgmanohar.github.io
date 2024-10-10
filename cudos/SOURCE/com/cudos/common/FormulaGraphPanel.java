package com.cudos.common;

import evaluator.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;

/**
 * A graphing component to represent an equation
 */

public class FormulaGraphPanel extends GraphPaper {

	public FormulaGraphPanel() {
		addMouseListener(ml);
		addMouseMotionListener(ml);
	}

	/** The expression to graph */
	private Expression yExpr;

	/** List of graphs currently being displayed */
	public Vector expressions = new Vector();

	/** The colour for the graph line */
	private Color lineColour = Color.yellow;

	public void paint(Graphics g){
		super.paint(g);
		for(int i=0;i<expressions.size();i++){
			Expression expr = (Expression)expressions.get(i);
			if(expr==null)return;
			if(expr==yExpr) g.setColor(lineColour);
			else g.setColor(getLineColour(i));
			((Graphics2D)g).setStroke(new BasicStroke(3));
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if(expr instanceof PolarExpression){
				polarGraph(g, (PolarExpression)expr);
			}else if(expr instanceof ParametricExpression){
				parametricGraph(g, (ParametricExpression)expr);
			}else{
				yGraph(g,expr);
			}
		}
	}

	int POLAR_POINTS=600, PARAMETRIC_POINTS=600;
	int SCREEN_MAX=1600;

	/** Plot r against t */
	void polarGraph(Graphics g, PolarExpression expr){
		double tmin = -Math.PI, tmax = Math.PI;
		try{
			tmin = new Expression(expr.tMinimum).value();
			tmax = new Expression(expr.tMaximum).value();
		}catch(StackException e){ } catch(MathException e){	tmin=-1;tmax=1;	}
		double oxg=Double.NaN, oyg = Double.NaN, delta = (tmax-tmin)/POLAR_POINTS;
		int ox = 0, oy = 0;
		for(double theta=tmin; theta<tmax; theta+=delta){
			try{
				t = theta;	double r = expr.getR();
				double xg = r * Math.cos(t), yg = r * Math.sin(t);
				int x = xS(xg), y = yS(yg);
				if(!Double.isNaN(oxg) && !Double.isNaN(oyg) && !Double.isNaN(xg) && !Double.isNaN(yg)
						&& Math.abs(x)<SCREEN_MAX && Math.abs(y)<SCREEN_MAX && Math.abs(ox)<SCREEN_MAX && Math.abs(oy)<SCREEN_MAX ){
					g.drawLine(ox,oy,x,y);
				}
				ox=x;oy=y;  oxg=xg;oyg=yg;
			}catch(MathException e){ } catch(StackException e){ }
		}
	}

	/** Plot x,y at t */
	void parametricGraph(Graphics g, ParametricExpression expr){
		double tmin = -2, tmax = 2;
		try{
			tmin = new Expression(expr.tMinimum).value();
			tmax = new Expression(expr.tMaximum).value();
		}catch(StackException e){ } catch(MathException e){ tmin=-1;tmax=1; }
		double oxg=Double.NaN, oyg = Double.NaN, delta = (tmax-tmin)/PARAMETRIC_POINTS;
		int ox = 0, oy = 0;
		for(double theta=tmin; theta<tmax; theta+=delta){
			try{
				t = theta;
				double xg = expr.getX(),  yg = expr.getY();
				int x = xS(xg), y = yS(yg);
				if(!Double.isNaN(oxg) && !Double.isNaN(oyg) && !Double.isNaN(xg) && !Double.isNaN(yg)
						&& Math.abs(x)<SCREEN_MAX && Math.abs(y)<SCREEN_MAX && Math.abs(ox)<SCREEN_MAX && Math.abs(oy)<SCREEN_MAX ){
					g.drawLine(ox,oy,x,y);
				}
				ox=x;oy=y;  oxg=xg;oyg=yg;
			}catch(MathException e){ } catch(StackException e){ }
		}
	}

	/** Plot y against x */
	void yGraph(Graphics g, Expression expr){
		int ox=0,oy=0;
		double oyg=Double.NaN;
		for(int xs=0;xs<getWidth();xs++){
			//calculate the point
			double xg = xG(xs);
			//Variable.set("x",xg);
			x=xg;
			int ys = oy;
			try{
				double yg = expr.value();
				ys = yS(yg);
				//draw the line (only if no error)
				if(!Double.isNaN(yg) && !Double.isNaN(oyg) && Math.abs(ys)<SCREEN_MAX && Math.abs(oy)<SCREEN_MAX)
					g.drawLine(ox,oy,xs,ys);
				oyg=yg;
			}catch(MathException e){ }
			 catch(StackException e){ }
			ox=xs; oy=ys;
		}
	}




	/** These variables can be used in formulae */
	public static double x,t;



	public Color getLineColour() {
		return lineColour;
	}
	public void setLineColour(Color lineColour) {
		this.lineColour = lineColour;
	}


	//setup the formula interpreter
	{
		ObjectPath.appendRoot(Math.class);
                ObjectPath.appendRoot(MathExtra.class);
                ObjectPath.appendRoot(this);
	}
	/** Changes the current expression (yExpr) */
	public void setExpression(String e) throws ParseException{
		expressions.remove(yExpr);
		yExpr = new Expression(e);
		expressions.add(yExpr);
		repaint();
	}
	/** Changes the current expression (yExpr) */
	public void setExpression(Expression e){
		expressions.remove(yExpr);
		yExpr = e;
		expressions.add(e);
		repaint();
	}

	/** Returns the current expression */
	public Expression getExpression(){
		return yExpr;
	}
	/** Selects an expression from the expresssions list as the selected exrepssion (yExpr) */
	public void selectExpression(int i){
		yExpr = (Expression)expressions.get(i);
		repaint();
	}

	/** Zoom into / out of a point in graph-coordinates, by scale factor */
	public void zoom(double scale, Point2D p){
		double rx=(maxx-minx)*scale/2, ry=(maxy-miny)*scale/2;
		minx = p.getX() - rx; maxx = p.getX() + rx;
		miny = p.getY() - ry; maxy = p.getY() + ry;
		recalculateTicks();
		repaint();
	}


        public void zoom(double scale, double x, double y){
          zoom(scale, new Point2D.Double(x,y));
        }
	/** translate by a number of pixels */
	public void translate(int x, int y){
		double dx = xG(x)-minx, dy = yG(getHeight()-y)-miny;
		minx += dx; maxx += dx;
		miny -= dy; maxy -= dy;
		repaint();
	}

	/** Zooms in steps towards a point p at scale. */
	class SmoothZoomer {
		double N = 6;
		double fraction, dcx,dcy;
		Point2D direction;
		public SmoothZoomer(double scale, Point2D p){
			fraction = Math.pow(scale, 1 / N);
			dcx = (p.getX() - (maxx+minx)/2) / N;
			dcy = (p.getY() - (maxy+miny)/2) / N;
			//direction=p;
			timer.start();
		}
		int n = 0;
		Timer timer = new Timer(50,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				direction = new Point2D.Double(
					(maxx+minx)/2 + dcx*1,
					(maxy+miny)/2 + dcy*1
				);
				zoom(fraction, direction);
				n++;
				if(n >= N) timer.stop();
			}
		});
	}

	/* for an external component who wishes to display a cursor coordinate*/
	public Point2D displayPoint;
	public Component displayPointComponent;

	/** Mouse functions */
	protected MouseInputListener ml = new MouseInputAdapter(){
		public void mouseClicked(MouseEvent e){
			if(e.getButton()!=e.BUTTON1)
				new SmoothZoomer(3/2., toGraph(e.getPoint()) );
			else
				new SmoothZoomer(2/3., toGraph(e.getPoint()) );
		}
		Point op;
		public void mousePressed(MouseEvent e){
			op=e.getPoint();
		}
		public void mouseDragged(MouseEvent e){
			Point p = e.getPoint();
			int dx = p.x-op.x, dy = p.y-op.y;
			translate(-dx,-dy);
			op=p;
		}
		public void mouseMoved(MouseEvent e){
			displayPoint = toGraph( e.getPoint() );
			if(displayPointComponent!=null) displayPointComponent.repaint();
		}
	};

	Color[] colours={ new Color(96,128,160)/*, Color.blue, Color.white, Color.cyan, Color.magenta, Color.orange*/ };
	public Color getLineColour(int i){		return colours[i%colours.length]; 	}

}

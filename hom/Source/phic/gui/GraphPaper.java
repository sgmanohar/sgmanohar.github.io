/**
		 * This class is a panel that paints itself with a grid of major and minor lines
 * with an external coordinate system in doubles for painting on this grid.
 */
package phic.gui;
import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

public class GraphPaper extends JPanel{
	public GraphPaper(){
		setFont(new Font("Dialog",Font.PLAIN,10));
		setForeground(Color.green);
		setBackground(Color.black);
		minx=miny=0;
		maxx=maxy=100;
		majorx=majory=50;
		minorx=minory=10;
	}

	protected double minx,maxx,majorx,minorx;

	protected double miny,maxy,majory,minory;

	public void paint(Graphics g){
		super.paint(g);
                double dx=maxx-minx,dy=maxy-miny;
                //minor ticks
                g.setColor(getFracColour(0.3));
                drawg(g,minx+offsetx,(int)(dx/minorx),minorx,false,false);
                drawg(g,miny+offsety,(int)(dy/minory),minory,true,false);
                //major ticks
                g.setColor(getFracColour(0.7));
                drawg(g,minx+offsetx,2+(int)(dx/majorx),majorx,false,drawXLabels);
                drawg(g,miny+offsety,1+(int)(dy/majory),majory,true,drawYLabels);
                super.paintChildren(g);
	}
        public void paintComponents(Graphics g){
          super.paintComponent(g);
	}

	public Color getFracColour(double p){
		Color f=getForeground(),b=getBackground();
		double q=1-p;
		return new Color((int)(p*f.getRed()+q*b.getRed()),
				(int)(p*f.getGreen()+q*b.getGreen()),(int)(p*f.getBlue()+q*b.getBlue()));
	}

	//setup the graphic range
	/**
	 * Set the range of the x-axis, in graph coordinates, and adjust the
	 * major and minor ticks to fit --- unless internalResize == true.
	 */
	public void setXRange(double minimum,double maximum){
		minx=minimum;
		maxx=maximum;
		if(!internalResize){
			double b=10*tenths(maxx-minx);
                        double nx;
			do{ b/=10;
                           nx=((maxx-minx)/b);
			}while(nx==0);
			if(getWidth()/nx<50){
				majorx=b;
				minorx=b/2;
			} else{
				majorx=b;
				minorx=b/5;
			}
			//make minx an exact multiple of majorx (rounding down always)
			//minx=majorx*(int)(minx/majorx);
                        offsetx = (minx - majorx*(int)(minx/majorx))/2; //center the central gridline
//System.out.println("x["+minx+","+maxx+"]d"+majorx);
		}
	}

	/**
	 * Set the range of the y-axis, in graph coordinates, and adjust the
	 * major and minor ticks to fit --- unless internalResize == true.
	 */
	public void setYRange(double minimum,double maximum){
		miny=minimum;
		maxy=maximum;
		if(!internalResize){
			double b=tenths(maxy-miny);
			double ny=((maxy-miny)/b);
			if(getHeight()/ny<50){
				majory=b;
				minory=b/2;
			} else{
				majory=b/2;
				minory=b/5;
			}
			//miny must be a multiple of majory
			//miny=majory*(int)(miny/majory);
                        offsety = (miny-majory*(int)(miny/majory))/2;
                        //center the central gridline
//System.out.println("y["+miny+","+maxy+"]d"+majory);
		}
	}
        double offsetx,offsety;
	/**
	 * When true, the setXRange and setYRange commands do not alter the
	 * major and minor tick spacing.
	 */
	public boolean internalResize=false;

	/** Delegate drawLine method using graph coordinates. */
	public void drawLine(Graphics g,double x1,double y1,double x2,double y2){
		g.drawLine(xS(x1),yS(y1),xS(x2),yS(y2));
	}

	/** Convert a graph point into screen coordinates */
	public Point toScreen(Point2D p){
		return new Point(xS(p.getX()),yS(p.getY()));
	}

	/** Convert a screen coordinate point into graph coordinates. */
	public Point2D toGraph(Point p){
		return new Point2D.Double(xG(p.x),yG(p.y));
	}

	/** property drawLabels */
	boolean drawXLabels=false,drawYLabels=false;

	public void setDrawLabels(boolean d){
		drawXLabels=drawYLabels=d;
		repaint();
	}

	public boolean isDrawLabels(){
		return drawXLabels||drawYLabels;
	}

	public void setDrawXLabels(boolean d){
		drawXLabels=d;
		repaint();
	}

	public void setDrawYLabels(boolean d){
		drawYLabels=d;
		repaint();
	}

	public boolean isDrawXLabels(){
		return drawXLabels;
	}

	public boolean isDrawYLabels(){
		return drawYLabels;
	}

	public double getMajorX(){
		return majorx;
	}

	public void setMajorX(double d){
		majorx=d;
		repaint();
	}

	public double getMajorY(){
		return majory;
	}

	public void setMajorY(double d){
		majory=d;
		repaint();
	}

	public void setMinorX(double d){
		minorx=d;
	}

	public double getMinorX(){
		return minorx;
	}

	public double getMinorY(){
		return minory;
	}

	public void setMinorY(double minory){
		this.minory=minory;
	}

	//internal functions
	double tenths(double a){
		int r=(int)Math.round(Math.log(a)/Math.log(10));
		return Math.pow(10,r-1);
	}

	/**
	 * Draw lines of the graph paper.
	 */
	void drawg(Graphics g,double low,int n,double dist,boolean horz,
			boolean labels){
		if(horz){
			for(int i=0;i<n;i++){
				double yg=low+dist*i;
				int y=yS(yg);
				g.drawLine(0,y,getWidth(),y);
				if(labels){
					g.drawString(tostring(yg,majory),0,y);
				}
			}
		} else{
			for(int i=0;i<n;i++){
				double xg=low+dist*i;
				int x=xS(xg);
				g.drawLine(x,0,x,getHeight());
				if(labels){
					g.drawString(tostring(xg,majorx),x,getHeight()-10);
				}
			}
		}
	}

	/**
	 * Convert a double to a string for labelling axes
	 */
	String tostring(double a,double major){
		a=((int)(100*a))/100.;
		String z;
		if(major>10){
			z=String.valueOf((int)a);
		} else{
			z=String.valueOf(a);
		}
		if(z.endsWith(".0")){
			z=z.substring(0,z.length()-2);
		}
		return z;
	}

	/** X-Coordinate transform from graph to screen */ public int xS(double xG){
		return(int)(getWidth()*(xG-minx)/(maxx-minx));
	}

	/** Y-Coordinate transform from graph to screen */
	public int yS(double yG){
		return(int)(getHeight()*(1-(yG-miny)/(maxy-miny)));
	}

	/** X-coordinate transform from screen to graph */
	public double xG(double xS){
		return xS/getWidth()*(maxx-minx)+minx;
	}

	/** Y-coordinate transform from screen to graph */
	public double yG(double yS){
		return(1-yS/getHeight())*(maxy-miny)+miny;
	}
}

package phic.gui;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import phic.common.*;
import phic.modifiable.*;
/**
 * An extension of label that draws a VisibleVariable's value, maximum
 * and minimum on a horizontal bar. The numerical value is also displayed.
 *
 * Includes a static updater thread that starts when the first bar is created.
 * This thread can be used by any gui class to update the values using
 * the Ticker interface.
 */
public class HorizontalBar extends JLabel implements  Ticker{
  /**
   * The delay (ms) between updates of all HorizontalBars.
   * This includes updates of the ThinNodeViews, and of
   * OrbitPanels.
   */
  static int BAR_UPDATE_INTERVAL=200;


	/** Create a horizontal bar view */
	public HorizontalBar(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		setText("");
		//Black background
		setOpaque(true);
		setBackground(Color.black);
		setForeground(new Color(0,0,0x80));
		//fixed width
		int prefHeight= /* getPreferredSize().height */14;
		setPreferredSize(new Dimension(72,prefHeight));
		//rescale on mouse clicks
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				rescale();
			}
		});
	}
        public void addNotify(){
          bars.add(this);
          super.addNotify();
        }
        public void removeNotify(){
          super.removeNotify();
          bars.remove(this);
        }


	/** Starts the thread */
	{
		ensureThreadRunning();
	}

        private static void ensureThreadRunning() {
          if ( !running )
                        {
                                running = true ;
                                thread = new Thread ( new BarThreadRunnable() , "BarUpdater" ) ;
                                thread.start () ;
                        }
        }
        /**
         * Add a ticker. This ticker will be called every time the interface needs
         * to be updated with values.
         * This ensures that the thread is running.
         * To stop the regular calls, use removeBar(Ticker)
         * @param t Ticker a ticker that will be called regularly
         */
        public static void addBar(Ticker t){
          bars.add(t);
          ensureThreadRunning();
        }
        /**
         * removeBar - stops the given ticker from being called regularly
         * to update its values.
         *
         * @param orbitPanel OrbitPanel
         */
        public static void removeBar(Ticker t) {
          bars.remove(t);
        }



	/** Static updater thread for HorizontalBars */
	static Thread thread;

	/** Static list of bars that have been created */
	static Vector bars=new Vector();


	/** Is the static updater thread running? */
	static boolean running=false;

        /** The updater thread waits 300ms between calling tick() on each bar. */
        static class BarThreadRunnable implements Runnable{
          public void run(){
            try {
              while (running) {
                while (bars.size() > 0) {
                  synchronized(Organ.cycleLock){
                    for (int i = 0; i < bars.size(); i++) {
                      Ticker b = (Ticker) bars.get(i);
                      b.tick(BAR_UPDATE_INTERVAL/1000.);
                    }
                  }
                  try {
                    Thread.sleep(BAR_UPDATE_INTERVAL);
                  }
                  catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
                try {
                  Thread.sleep(300);
                }
                catch (InterruptedException x) {
                  x.printStackTrace();
                }
              }
            }finally {
              running = false;
            }
          }
	}

	/** Stop the updater thread for all HorizontalBars */
	public static void stop(){
		running=false;
	}

	/** Current value displayed */
	double val;

	/** The updater for this bar */
	public void tick(double time){
		double newval=getValue();
		if(newval!=val){
			val=newval;
			repaint();
		}
	}

	/** Set/get the variable to display */
	VisibleVariable variable;

	Node node;
	/** Set the variable displayed by this bar */

	public void setVariable(VisibleVariable v){
		variable=v;
		node=v.node;
		rescale();
	}

	/** Set the node displayed by this bar */
	public void setNode(Node n){
		node=n;
	}

	/** Get the variable displayed by this bar */
	public VisibleVariable getVariable(){
		return variable;
	}

	/** Return the node displayed by this bar */
	public Node getNode(){
		return node;
	}

	/** Scale the bar to fit the min, max and current value */
	protected double scaleMin,scaleMax;

	/** Scaling property - will the scale always include zero? */
	public boolean alwaysIncludeZero=true;

			/** Scaling property - will the scale always include the variable's minimum */
	public boolean alwaysIncludeMinimum=true;

			/** Scaling property - will the scale always include the variable's maximum */
	public boolean alwaysIncludeMaximum=true;

  public int zoom = Range.ZOOM_NORMAL;
  /**
   * If this is true, then the normal value is always at the centre of the bar.
   */
  protected boolean SYMMETRICAL = true;

	/** Rescales the values on the bar */
	void rescale(){
          zoom++;
          zoom %= Range.ZOOM_RANGES.length;
          double val = getValue();
          double min = 0, max = 0;
          if (variable != null) {
            if (alwaysIncludeMinimum) {
              min = Math.min(variable.minimum, val);
            }
            if (alwaysIncludeZero) {
              min = Math.min(min, 0);
            }
            if (alwaysIncludeMaximum) {
              max = Math.max(variable.maximum, val);
            }
          }
          else if (node != null) {
            min = max = val;
          }
          //scaleMin=Quantity.getNearestRoundBelow(min);
          //scaleMax=Quantity.getNearestRoundAbove(max);
          if(variable!=null){
            Range r = phic.modifiable.Range.findRange(variable, zoom);
            if (SYMMETRICAL) {
              r = phic.modifiable.Range.findRangeSymmetrical(variable, zoom);
            }
            scaleMin = r.minimum;
            scaleMax = r.maximum;
            repaint();
          }

          String ttt = null;
          if (drawText) {
            if (variable != null) {
              ttt = "Scale " + variable.formatValue(scaleMin, true, false) + " - "
                  + variable.formatValue(scaleMax, true, false);
            }
            else {
              ttt = "Scale " + Quantity.toString(scaleMin) + " - " +
                  Quantity.toString(scaleMax);
            }
          }
          else {
            if (variable != null) {
              ttt = "Value = " + variable.formatValue(val, true, false)
                  + ", Scale " + variable.formatValue(scaleMin, true, false) + " - "
                  + variable.formatValue(scaleMax, true, false);
            }
            else {
              ttt = "Value = " + Quantity.toString(val)
                  +", Scale " + Quantity.toString(scaleMin) + " - " +
                  Quantity.toString(scaleMax);
            }
          }
          setToolTipText(ttt);
        }
	/** Return the value of the variable. */
	private final double getValue(){
          if(node!=null) synchronized(phic.common.Organ.cycleLock){
            return node.doubleGetVal();
          }
          return Double.NaN;
        }

	/**
	 * The colour of the bar is
	 *   red:    too high - above the maximum
	 *   cyan:   OK - within the range minimum -- maximum
	 *   yellow: too low - below the minimum
	 */
	protected Color getColour(){
		double val=getValue();
		if(val<variable.minimum){
			return Color.yellow;
		}
		if(val<=variable.maximum){
			return Color.cyan;
		}
		return Color.red;
	}

	/** Border thickness */
	private int b=2;

	/** Draw the text? */
	public boolean drawText=true;

	/** Draw the bar, min and max lines, and the text */
	public void paint(Graphics g){
		super.paint(g);
		if(variable!=null){
//			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(getForeground());
			double val=getValue();
			int bw=barWidth(val);
			if(bw>0){
//				bw=Math.max(bw,getWidth());
				g.fill3DRect(b,b,bw,getHeight()-2*b,true);
			}
			int minx=barWidth(variable.minimum)+b,maxx=barWidth(variable.maximum)+b,
					initx=barWidth(variable.initial)+b;
			g.setColor(Color.yellow);
			g.drawLine(minx,0,minx,getHeight());
			g.setColor(Color.red);
			g.drawLine(maxx,0,maxx,getHeight());
			g.setColor(Color.orange);
			g.drawLine(initx,0,initx,getHeight());
			if(drawText){
				g.setColor(getColour());
				g.setFont(getFont());
				g.drawString(variable.formatValue(val,true,true),b,getHeight()-b);
			}
		}
	}

	/**
	 * Return the x coordinate on the label that corresponds to the
	 * given value of the variable. Uses scaleMax and scaleMin to calculate
	 * the position.
	 */
	protected int barWidth(double v){
		double f=(v-scaleMin)/(scaleMax-scaleMin);
		f=Math.max(0,Math.min(1,f));
		double w=((getWidth()-2*b)*f);
		if(w>0){
			return(int)w;
		} else{
			return 0;
		}
	}

	private void jbInit() throws Exception{
		this.setBorder(BorderFactory.createLoweredBevelBorder());
	}
}

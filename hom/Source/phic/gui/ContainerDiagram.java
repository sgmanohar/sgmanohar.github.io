package phic.gui;
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import phic.common.UnitConstants;
import phic.modifiable.Range;

/**
 * Draw a container with a pictorial representation of its contents
 */
public class ContainerDiagram extends JPanel{
	/** Create a diagram */
	public ContainerDiagram(){
		setLayout(null);
		currentDiagrams.add(this);
		setPreferredSize(new Dimension(150,150));
	}

	/** The container to display */
	phic.common.Container container;
	/** The margin around the walls of the container, in pixels */

	int margin=8;

	/** Number of ticks to draw up the left hand side of the container */
	int nTicks=5;

	/** The height of the fluid level in pixels */
	int fluidLevel;

	/** Paint the background of the container */
	public void paint(Graphics g){
		//paint background
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
		int containerHeight=(getHeight()-2*margin);
		double v=container.volume.get();
		//get a range that includes zero
		Range range=Range.findRange(v,Range.ZOOM_VARIABLE_RANGE);
		fluidLevel=(int)(containerHeight*v/range.maximum);
		//draw fluid
		g.setColor(Color.cyan);
		g.fillRect(margin,getHeight()-margin-fluidLevel,getWidth()-2*margin,
				fluidLevel);
		//borders
		g.setColor(Color.black);
		g.drawLine(margin,margin,margin,getHeight()-margin);
		g.drawLine(margin,getHeight()-margin,getWidth()-margin,getHeight()-margin);
		g.drawLine(getWidth()-margin,margin,getWidth()-margin,getHeight()-margin);
		//ticks and volume label
		for(int i=0;i<nTicks;i++){
			int z;
			g.drawLine(margin-2,z=margin+i*containerHeight/nTicks,margin,z);
		}
		g.drawString(UnitConstants.formatValue(range.maximum,UnitConstants.LITRES,false),
				0,12);
		paintChildren(g);
	}

	/** The components that represent constituents of the container */
	JLabel[] constituents;
	/** Initialise the display from a given container's values */

	public void setContainer(phic.common.Container c){
		container=c;
		constituents=new JLabel[container.quantityname.length];
		for(int i=0;i<constituents.length;i++){
			constituents[i]=new JLabel();
			constituents[i].setToolTipText(container.quantityname[i]);
			constituents[i].setOpaque(true);
			constituents[i].setBackground(quantityColors[i]);
			add(constituents[i]);
		}
		calculateRectangles();
	}

	/**
	 * The quantity of substance that is represented the height of the
	 * fluid level. Measured in moles.
	 */
	double maxQuantity=1;

	/** This is called regularly to set the heights to reflect the composition */
	public void calculateRectangles(){
		int cumulativeHeight=0;
		double totalMoles=0;
		//solids first
		int solidHeight=(int)(fluidLevel*container.solids.getC());
		constituents[7].setBounds(margin+1,getHeight()-margin-solidHeight,
				getWidth()-2*margin-1,solidHeight);
		cumulativeHeight+=solidHeight;
		//total moles of other stuff
		for(int i=0;i<7;i++){
			double q=container.substance(i).getQ();
			totalMoles+=q;
		}
		Range range=Range.findRange(totalMoles,Range.ZOOM_OUT);
		double scale=(fluidLevel-cumulativeHeight)/range.maximum;
		for(int i=0;i<7;i++){
			double q=container.substance(i).getQ();
			int height=(int)(q*scale);
			height=Math.min(Math.max(height,0),fluidLevel);
			constituents[i].setBounds(margin+1,
					getHeight()-margin-cumulativeHeight-height,getWidth()-2*margin-1,
					height);
			cumulativeHeight+=height; //stack on top of each other
			constituents[i].setToolTipText(container.quantityname[i]+" ("
					+container.substance(i).formatValue(true,false)+")");
		}
	}

	/**
	 * The colours are the default recommended colours for each quantity,
	 * if they are to be graphically represented.
	 * they are in order: bicarb, H, glucose, urea, prot, K, Na, solids
	 */
	public static Color[] quantityColors=new Color[]{Color.green,Color.yellow,
			Color.pink,Color.orange,Color.white,Color.blue,Color.red,Color.darkGray,
      Color.magenta
	};

			/** indicates whether the thread has started observing this containerdiagram */
	boolean started;

	/** A list of the currently active ContainerDiagrams */
	static Vector currentDiagrams=new Vector();

	/** Whether the thread is running */
	static boolean running=true;

	/** Delay between ContainerDiagram updates */
	static int timerDelay=300;

	/** The thread that does the updating */
	static Thread thread=new Thread(new Runnable(){
		public void run(){
			while(running){
				for(int i=0;i<currentDiagrams.size();i++){
					ContainerDiagram d=(ContainerDiagram)currentDiagrams.get(i);
					if(d.started){
						if(d.getParent()==null){
							currentDiagrams.remove(d);
							continue;
						}
						d.calculateRectangles();
						d.repaint();
					} else{
						if(d.getParent()!=null){
							d.started=true;
						}
					}
				}
				try{
					Thread.sleep(timerDelay);
				} catch(Exception e){
					e.printStackTrace();
					running=false;
				}
			}
			System.out.println("Containerview thread died");
		}
	}


	,"ContainerDiagram Updater");

	/** Code that runs the updater as soon as the ContainerDiagram class is loaded */
	static{
		thread.start();
	}
}

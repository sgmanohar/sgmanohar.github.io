package phic.gui.exam;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import phic.Resource;

/**
 * Draw a blood film
 */
public class BloodFilmPanel extends JPanel{
	public BloodFilmPanel(){
		setBackground(new Color(224,255,255));
		//load images
		mt=new MediaTracker(this);
		for(int i=0;i<cellNames.length;i++){
			cellImages[i]=Resource.loader.getImageResource(cellNames[i]+".gif");
			mt.addImage(cellImages[i],0);
		}
		addMouseListener(input);
		addMouseMotionListener(input);
	}

	class Input extends MouseInputAdapter{
		Point point;

		public void mousePressed(MouseEvent e){
			point=e.getPoint();
		}

		public void mouseDragged(MouseEvent e){
			xx+=e.getX()-point.x;
			yy+=e.getY()-point.y;
			xx=Math.max(Math.min(xx,0),getWidth()-fwidth);
			yy=Math.max(Math.min(yy,0),getHeight()-fheight);
			point=e.getPoint();
			repaint();
		}
	}


	int xx,yy;

	Input input=new Input();

	MediaTracker mt;
	/** Code / index for cell types */

	static final int ERYTHROCYTES=0,LYMPHOCYTES=1;

	//@todo Neutrophils, Macrocytes, hypochromic microcytes, Reticulocytes,
	// Platelets, Monocytes
	// Images must exist in the resourcepath called, e.g., "Erythrocytes.jpg"
	/** Cell type names - and image file names */
	String[] cellNames={"Erythrocytes","Lymphocytes"
	};
	/** Images: loaded in constructor */

	Image[] cellImages=new Image[cellNames.length];
	/**
	 * The frequencies of the cells, x 10^9 per L
	 * These values are altered by the examination class, which uses
	 * haematocrit etc. to determine numbers.
	 */

	 double[] cellFrequency=new double[]{5000,10
	};

	int fwidth=1000,fheight=1000;
        double opacity = 0.5f, opacityVariability=0.1f;
        double cellSize = 1, cellSizeVariability=0.1;
	double thickness=2;

	BufferedImage film;

	public void tryCreateFilm(){
		new Thread(new Runnable(){
			public void run(){
				filmLoaded=false;
				repaint();
				try{
					mt.waitForAll();
				} catch(Exception e){
					e.printStackTrace();
				}
				createFilm();
				filmLoaded=true;
				repaint();
			}
		}).start();
	}

        private void createFilm() {
          film = new BufferedImage(fwidth, fheight, BufferedImage.TYPE_INT_ARGB);
          int area = fwidth * fheight;
          Graphics2D g2 = film.createGraphics();
          for (int i = 0; i < cellNames.length; i++) {
            double ch = cellImages[i].getHeight(this), cw = cellImages[i].getWidth(this);
            int number = (int) (thickness * area * cellFrequency[i] / 1000000);
            for (int j = 0; j < number; j++) {
              g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                         Math.max(0,
                  Math.min(1f, (float) (0.4* opacity + (Math.random()-0.5) * opacityVariability)))));
              double sz = cellSize * (1+(Math.random()-0.5)*cellSizeVariability);
              g2.drawImage(cellImages[i], (int) (Math.random() * fwidth),
                           (int) (Math.random() * fheight), (int)(cw*sz), (int)(ch*sz), this);
            }
          }
        }

	boolean filmLoaded=false;

	public void paint(Graphics g){
		super.paint(g);
		if(film!=null){
			g.drawImage(film,xx,yy,this);
		}
		if(!filmLoaded){
			g.drawString("Creating blood film...",20,20);
		}
	}
}

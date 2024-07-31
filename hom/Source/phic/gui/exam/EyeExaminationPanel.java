package phic.gui.exam;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import sanjay.common.RadialPaint;
import phic.Current;

/**
 * Panel to draw eye examination
 */
public class EyeExaminationPanel extends JPanel{
	/// Constants
	public static final int LEFT=0,RIGHT=1;

	public Eye left=new Eye(),right=new Eye();

	public Eye[] eyes=new Eye[]{left,right};

	public int interocularDistance=150;

	public EyeExaminationPanel(){
		initialisePoints();
		addMouseListener(mouseInput);
		addMouseMotionListener(mouseInput);
		jbInit();
	}
        public void addNotify(){
          super.addNotify();
          timer.start();
        }
        public void removeNotify(){
          timer.stop();
          super.removeNotify();
        }

	/** Set up the initial coordinates */
	public  void initialisePoints(){
		eyes[0].centre=new Point((getWidth()-interocularDistance)/2,getHeight()/2);
		eyes[1].centre=new Point((getWidth()+interocularDistance)/2,getHeight()/2);
		for(int i=0;i<2;i++){
			eyes[i].pupilCentre=(Point)eyes[i].centre.clone();
		}
	}

	/** Handle mouse events */
	public  EyeInput mouseInput=new EyeInput();

	public class EyeInput extends MouseInputAdapter{
		public  Point lookingAt=new Point(0,0);

		public void mousePressed(MouseEvent e){
			lightOn=true;
			repaint();
		}

		public void mouseReleased(MouseEvent e){
			lightOn=false;
			repaint();
		}

		public void mouseMoved(MouseEvent e){
			lookingAt.setLocation(e.getX(),e.getY());
			//repaint();
		}

		public void mouseDragged(MouseEvent e){
			lookingAt.setLocation(e.getX(),e.getY());
			//repaint();
		}
	};


	private boolean lightOn=false;

	Point currentDirection=new Point();

        /** Speed at which the eyeballs follow the mouse pointer */
	public double lookSpeed=0.3;


	Timer timer=new Timer(100,new TimerListener());
	/** Controls gradual movement of the eyes and pupil size */

        /** This listener performs the animation tasks of moving eyeballs and pupils */
	class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//calculate pupil size from light incident
                        tick();
                        Point t=mouseInput.lookingAt;
                        movePupilsToLookAt(t);
			double totalLight=0;
                        for (int i = 0; i < 2; i++) {
                          double distLight = mouseInput.lookingAt.distance(eyes[i].pupilCentre);
                          if (lightOn) {
                            totalLight += (distLight < lightRadius) ? 1 - eyes[i].opticPalsy : 0;
                          }
                        }
			pupilSizeCommand=Math.max(9-totalLight*5 + pupilSizeModification,3);
			repaint();
		}
	}
        public void tick(){
        }

        /** Alteration of pupil size, e.g. by morphine */
        public double pupilSizeModification = 0;


        /** Distances in panel, in pixels */
	public int distanceOfTarget=300,radiusOfEyeball=20;

        /**
         * Rate at which pupils accommodate to light
         */
        public double accommodationRate=0.2;

	Point[] lastDeflections=new Point[2];

	{
		lastDeflections[0]=new Point();
		lastDeflections[1]=new Point();
	}
        boolean saccade=false;
        int saccadeTimeout=0;
        double meanError=0;
        double saccErrorThresh=40;
        Point desiredL=new Point(), desiredR=new Point();
        double saccvxl=0, saccvyl=0, saccvxr, saccvyr;
        double maxSaccadeSpeed=8;
        int minimumSaccadeInterval = 5;
        int saccadeFramesLeft, thinkingTimeLeft=-1;
        double microsaccadeProbability = 0.1, microsaccadeMagnitude=3;
        int thinkingDelay=4;

	/** Sets the pupils' coordinates so they are 'looking at' the point */
	public void movePupilsToLookAt(Point p){
		double ratio=radiusOfEyeball/(double)distanceOfTarget;
                double dxl=desiredL.x-lastDeflections[1].x,
                       dxr=desiredR.x-lastDeflections[0].x,
                       dyl=desiredL.y-lastDeflections[1].y,
                       dyr=desiredR.y-lastDeflections[0].y;
                meanError= dxl*dxl+dyl*dyl + dxr*dxr+dyr*dyr;
                if(!saccade){
                  //start saccade?
                 if(saccadeTimeout==0){
                   if (meanError > saccErrorThresh) {
                     double thl = Math.atan2(dyl, dxl), thr = Math.atan2(dyr, dxr);
                     double mgl = dxl*dxl+dyl*dyl, mgr = dxr*dxr+dyr*dyr;
                     double spL = mgl*2/(mgl+mgr), spR = mgr*2/(mgl+mgr);
                     int frames = (int)((Math.sqrt(mgl)+Math.sqrt(mgr))/2/maxSaccadeSpeed);
                     frames=Math.max(frames,1);
                     saccvxl = dxl/frames;
                     saccvyl = dyl/frames;
                     saccvxr = dxr/frames;
                     saccvyr = dyr/frames;
                     saccadeFramesLeft=frames;

                     if(thinkingTimeLeft==-1){
                       thinkingTimeLeft = thinkingDelay;
                     }else if(thinkingTimeLeft==0){
                       saccade = true; thinkingTimeLeft = -1;
                     }else{
                       thinkingTimeLeft--;
                     }
                   } else if(Math.random()<microsaccadeProbability){
                     saccadeFramesLeft=1;
                     saccvxl =  (Math.random()-0.5)*microsaccadeMagnitude;
                     saccvxr = - saccvxl;
                     saccvyl = saccvyr = (Math.random()-0.5)*microsaccadeMagnitude;
                     saccade=true;
                   }
                 }else saccadeTimeout--;
                }else{
                  //terminate saccade?
                  saccadeFramesLeft--;
                  if(saccadeFramesLeft<=0) {
                    saccade = false;
                    saccadeTimeout = minimumSaccadeInterval;
                  }
                }

		for(int i=0;i<2;i++){
			int LR=(i==0)?-1:1; //left or right?
			//deflection: positive = abduction
			double px=(p.x-getWidth()/2)*2+getWidth()/2;
			double py=(p.y-getHeight()/2)*2+getHeight()/2;
			Point deflection=new Point((int)(LR*(px-eyes[i].centre.x
					+eyes[i].strabismusX)*ratio),
					(int)((py-eyes[i].centre.y+eyes[i].strabismusY)*ratio));
			//diseases
			if(eyes[i].abducentPalsy){
				deflection.x=Math.min(deflection.x,0);
			}
			if(eyes[i].oculomotorPalsy){
				deflection.x=Math.max(deflection.x,10);
				deflection.y=Math.max(deflection.y,10);
			}
                        deflection.x *= 1-((deflection.x>0) ? eyes[i].lr : eyes[i].mr);
                        deflection.y *= 1-((deflection.y<0) ? eyes[i].sr : eyes[i].ir);
                        deflection.x=Math.max(-30,Math.min(30,deflection.x));
                        deflection.y=Math.max(-20,Math.min(20,deflection.y));

                        if(i==0){desiredR.x = deflection.x; desiredR.y=deflection.y;}
                        else{desiredL.x=deflection.x; desiredL.y=deflection.y;}

                        if(saccade){
                          //execute saccade
                          lastDeflections[i].x+=(i==0)?saccvxr:saccvxl;
                          lastDeflections[i].y+=(i==0)?saccvyr:saccvyl;

                        }else{
                          //move slowly
                          lastDeflections[i].x += lookSpeed * (deflection.x - lastDeflections[i].x);
                          lastDeflections[i].y += lookSpeed * (deflection.y - lastDeflections[i].y);
                        }
                        eyes[i].pupilCentre.x=eyes[i].centre.x+LR*lastDeflections[i].x;
                        eyes[i].pupilCentre.y=eyes[i].centre.y+lastDeflections[i].y;
                        calcPupilSize(i);
		}
	}
        public void calcPupilSize(int i){
          double targetPupilSize = pupilSizeCommand;
          targetPupilSize = Math.min(12, Math.max(2,
                              (1 - eyes[i].sympatheticPalsy) *
                              (targetPupilSize + 9 * eyes[i].parasympatheticPalsy) ));
          eyes[i].pupilSize += accommodationRate * (targetPupilSize - eyes[i].pupilSize);
        }



        /** The desired size of the pupils, depending on light */
	double pupilSizeCommand=4;

	/** Draw the eyes */
	public int esx=40,iris=18;
        public int esy[] = {20, 20};

        /** Size of light's coverage, in pixels */
	public int lightRadius=50;

        //old sclera: new Color(128,255,255),new Color(64,192,220)
        public Color[] irisColor={ new Color(154,108,108), new Color(92,48,48) };

        /** Paint the eyes in place */
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Shape[] sclera=new Shape[2];
		for(int i=0;i<2;i++){
			//sclera
			g2.setPaint(new RadialPaint(Color.white,Color.lightGray,
					new Point(eyes[i].centre.x-10,eyes[i].centre.y-10),50));
			sclera[i]=new Ellipse2D.Double(eyes[i].centre.x-esx,eyes[i].centre.y-esy[i],
					esx*2,esy[i]*2);
			g2.fill(sclera[i]);
			//g.fillOval(eyes[i].centre.x - esx, eyes[i].centre.y - esy,
			//   esx * 2, esy * 2);
			//iris
			//g.setColor(new Color(64,192,220));
			g2.setPaint(new RadialPaint(irisColor[0], irisColor[1],
					new Point(eyes[i].centre.x-10,eyes[i].centre.y-10),20));
			g.fillOval(eyes[i].pupilCentre.x-iris,eyes[i].pupilCentre.y-iris,iris*2,
					iris*2);
			//pupil
			double ps=eyes[i].pupilSize;
			//g.setColor(Color.black);
			g2.setPaint(new RadialPaint(new Color(128,128,128),Color.black,
					new Point(eyes[i].centre.x-10,eyes[i].centre.y-10),15));
			g2.fill(new Ellipse2D.Double(eyes[i].pupilCentre.x-ps,
					eyes[i].pupilCentre.y-ps,ps*2,ps*2));
			//shine
			g.setColor(new Color(255,255,255,156));
			g.fillOval(eyes[i].centre.x-15,eyes[i].centre.y-15,10,10);
		}
		//skin
		g.setColor(skinColour);
		GeneralPath s=new GeneralPath(new Rectangle2D.Double(0,0,getWidth(),
				getHeight()));
		s.append(sclera[0],false);
		s.append(sclera[1],false);
		s.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		g2.fill(s);
		if(lightOn){
//			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));
			g2.setColor(new Color(255,255,192,108));
			g2.fillOval(mouseInput.lookingAt.x-lightRadius,
					mouseInput.lookingAt.y-lightRadius,lightRadius*2,lightRadius*2);
		}
	}

	private void jbInit(){
		this.addComponentListener(new java.awt.event.ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				this_componentResized(e);
			}
		});
	}

	void this_componentResized(ComponentEvent e){
		initialisePoints();
	}
        /** The skin colour */
        public Color skinColour = new Color(250,230,220);
}

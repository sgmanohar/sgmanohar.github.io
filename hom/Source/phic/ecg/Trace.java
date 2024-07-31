package phic.ecg;
import java.awt.*;

/**
 * Draws the ECG trace on the paper
 */
public class Trace extends Paper{
	Heart heart;

	public static int I=0,II=1,III=2,aVR=3,aVL=4,aVF=5,V1=6,V2=7,V3=8,V4=9,V5=10,V6=11;

	public static Lead[] leads=new Lead[]{new Lead(0,0,"I"),new Lead(Math.PI/3,0,"II"),
			new Lead(Math.PI*2/3,0,"III"),new Lead(Math.PI*7/6,0,"aVR"),
			new Lead(-Math.PI/6,0,"aVL"),new Lead(Math.PI*2,0,"aVF"),
                        new Lead(-0.7,0.2,1,"V1"),new Lead(-0.3,0.2,1,"V2"),
                        new Lead(0,0,1,"V3"),new Lead(0.3,0,1,
			"V4"),new Lead(0.7,0,1,"V5"),new Lead(1,0,1,"V6"),
	};

	public void paint(Graphics g){
		super.paint(g);
		if(heart==null){
			return;
		}
		g.setColor(Color.black);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);((Graphics2D)g).setStroke(new
				BasicStroke(1));
		//get a set of heartbeats as a field
		double[][] field=heart.getBeatsField(traceTime);
		//this counts the current position in the field array
		int fieldIndex=0;
		//the vertical distance in volts between leads
		double leadHeight=traceHeightVolts/4;
		//the duration of a single column of traces
		double duration=traceTime/4;
		double[][] rhythmStripPoints=leads[II].getPoints(field);
		for(int i=0;i<4;i++){ // each column of leads
			//times relative to beginning of trace, for this column.
			double startTime=i*duration,endTime=startTime+duration;
			//calculate column of 4 leads, bottom is rhythm strip.
			Object[] leadcolumn=new Object[4];
			for(int j=0;j<3;j++){
				int L=3*i+j;
				leadcolumn[j]=leads[L].getPoints(field);
				//draw labels for leads
				g.drawString(leads[L].name,xS((i+0.5)*duration),yS((3.8-j)*leadHeight));
				//draw joins
				if(i>0){
					drawLine(g,i*duration,(3.7-j)*leadHeight,i*duration,
							(3.3-j)*leadHeight);
				}
			}
			leadcolumn[3]=rhythmStripPoints;
			//check time offset of this field is before the end of the column
			while(field[fieldIndex][3]<endTime&&fieldIndex<field.length){
				//if so, draw the next point for each lead in this column
				for(int j=0;j<leadcolumn.length;j++){
					double[][] p=(double[][])leadcolumn[j];
					if(fieldIndex>0){
						drawLine(g,p[fieldIndex-1][0],p[fieldIndex-1][1]+(3.5-j)*leadHeight,
								p[fieldIndex][0],p[fieldIndex][1]+(3.5-j)*leadHeight);
					} else{
						drawLine(g,0,(3.5-j)*leadHeight,p[0][0],p[0][1]+(3.5-j)*leadHeight);
					}
				}
				if(field[fieldIndex][3]>stopAfterTime){
					return;
				}
				fieldIndex++; //go to next point
			} //end of column
		} //end of trace
	} //end of paint

	/** Allow progressive drawing of ECG */
	double stopAfterTime=Double.MAX_VALUE;

	double drawingRate=0.1;

	public void executeTrace(){
		thread=new Thread(new Runnable(){
			public void run(){
				while(stopAfterTime<traceTime){
					stopAfterTime+=drawingRate;
					repaint();
					try{
						Thread.sleep(50);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				stopAfterTime=Double.MAX_VALUE;
			}
		});
		stopAfterTime=0;
		thread.start();
	}

	Thread thread;
}

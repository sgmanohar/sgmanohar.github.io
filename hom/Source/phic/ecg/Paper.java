package phic.ecg;
import java.awt.Color;
import java.awt.event.*;
import phic.gui.GraphPaper;

/**
 * ECG Paper
 */
public class Paper extends GraphPaper{
	public Paper(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception{
		this.setBackground(Color.white);
		this.setFont(new java.awt.Font("Dialog",1,12));
		this.setForeground(Color.red);
		this.setMajorX(1/largeSquaresPerSecond);
		this.setMajorY(1/largeSquaresPerVolt);
		this.setMinorX(1/largeSquaresPerSecond/5);
		this.setMinorY(1/largeSquaresPerVolt/5);
		this.addComponentListener(new java.awt.event.ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				this_componentResized(e);
			}
		});
	}

	double largeSquaresPerSecond=5;

	double largeSquaresPerVolt=1;

	double pixelsPerSecond=125;

	double pixelsPerVolt=25;

	/** Trace length in seconds */
	double traceTime=1;

	double traceHeightVolts=4;

	void this_componentResized(ComponentEvent e){
		internalResize=true;
		setXRange(0,traceTime=getWidth()/pixelsPerSecond);
		setYRange(0,traceHeightVolts=getHeight()/pixelsPerVolt);
		internalResize=false;
	}

	public double getPixelsPerSecond(){
		return pixelsPerSecond;
	}

	public void setPixelsPerSecond(double pixelsPerSecond){
		this.pixelsPerSecond=pixelsPerSecond;
	}
}
package phic.doctor;
import java.awt.*;
import javax.swing.JPanel;

/**
 * Draw the clockface
 */
public class ClockPanel extends JPanel{
	public ClockPanel(){
	}

	void updateClock(long time){
		this.time=time;
		repaint();
	}

	long time;

	int ticklen=3;

	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int cx=getWidth()/2,cy=getHeight()/2;
		int r=Math.min(cx,cy)-1;
		g.setColor(Color.white);
		g.fillOval(cx-r,cy-r,2*r,2*r);
		g.setColor(Color.black);
		g.drawOval(cx-r,cy-r,2*r,2*r);
		//ticks
		for(int i=0;i<12;i++){
			double a=i*Math.PI*2/12,s=Math.sin(a),c=Math.cos(a);
			g.drawLine((int)(cx+r*s),(int)(cy-r*c),(int)(cx+(r-ticklen)*s),
					(int)(cy-(r-ticklen)*c));
		}
		//second hand
		double asec=((time/1000)%60)/60.*Math.PI*2;
		double rsec=r*0.8;
		g.drawLine(cx,cy,cx+(int)(rsec*Math.sin(asec)),(int)(cy-rsec*Math.cos(asec)));
		//minute hand
		asec=((time/60000)%60)/60.*Math.PI*2;
		rsec=r*0.9;
		g2.setStroke(new BasicStroke(2));
		g.drawLine(cx,cy,cx+(int)(rsec*Math.sin(asec)),(int)(cy-rsec*Math.cos(asec)));
		//hour hand
		double fhrs=time/3600000.;
		asec=((fhrs/12)-(int)(fhrs/12))*Math.PI*2;
		rsec=r*0.5;
		g2.setStroke(new BasicStroke(3));
		g.drawLine(cx,cy,cx+(int)(rsec*Math.sin(asec)),(int)(cy-rsec*Math.cos(asec)));
	}
}
package com.neurolab.fluid;

import java.awt.*;
import com.neurolab.common.*;

public class FluidPipe extends FluidComponent{
	public int diameter=16;
	public double level=1.;
	public boolean drawstart=false,drawend=false;
	public FluidPipe(FluidPanel p,Point p1,Point p2){super(p,p1,p2);}
	public void paint(Graphics g_){
		try{
			Graphics g=(Graphics)g_;
			NeurolabExhibit.setStrokeThickness(g,stroke);
			g.setColor(linecolor);	//lines
			Point a=perpendicular(p1,diameter/2),b=perpendicular(p2,diameter/2);
			g.drawLine(a.x, a.y, b.x, b.y);
			a=perpendicular(p1,-diameter/2); b=perpendicular(p2,-diameter/2);
			g.drawLine(a.x, a.y, b.x, b.y);
			Point corner=perpendicularLess(p1,diameter/2-inset);
			g.setColor(watercolor);	//water
			g.fillRect(corner.x-(int)(inset*Math.abs(diry)),
                                   corner.y-(int)(inset*Math.abs(dirx)),
                                   (int)((magn+2*inset)*Math.abs(diry)+(diameter-2*inset)*Math.abs(dirx)),
                                   (int)((magn+2*inset)*Math.abs(dirx)+(diameter-2*inset)*Math.abs(diry))
                                   );
			// ,getLineAngle()-Math.PI/2)
			//			 .createTransformedShape(AffineTransform.getTranslateInstance(-inset*diry,inset*dirx)) );
			g.setColor(linecolor);	//ends
			if(drawstart){
				Point q1=new Point(p1.x-(int)(inset*diry),p1.y+(int)(inset*dirx));
				a=perpendicular(q1,diameter/2); b=perpendicular(q1,-diameter/2);
				g.drawLine(a.x,a.y, b.x,b.y);
			}
			if(drawend){
				Point q2=new Point(p2.x+(int)(inset*diry),p2.y-(int)(inset*dirx));
				a=perpendicular(q2,diameter/2); b=perpendicular(q2,-diameter/2);
				g.drawLine(a.x,a.y, b.x,b.y);
				//g.drawLine.Double(perpendicular(q2,diameter/2),perpendicular(q2,-diameter/2)));
			}
		}catch(Exception e){
			//? No graphics2 D
		}
	}
}

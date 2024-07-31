package sanjay.common;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.*;
import java.awt.image.*;
/**
 * radial gradient paint
 */

public class RadialPaint implements Paint, PaintContext {

	Color c1,c2;
	Point centre;
	int fallof;

	BufferedImage im = null;
	ColorModel cm = ColorModel.getRGBdefault();
	public RadialPaint(Color c1, Color c2, Point centre, int fallof) {
		this.c1=c1; this.c2=c2;
		this.centre=centre;
		this.fallof=fallof;
	}
	public void dispose(){
		im = null;
	}

	public ColorModel getColorModel(){
		return cm;
	}
	public Raster getRaster(int x, int y, int w, int h){
		WritableRaster r = im.getRaster();
		Raster rr = r.createChild(x-b.x,y-b.y,w,h,b.x,b.y,null);
		return rr;
	}

	Rectangle b;
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
//		int  x = (int)userBounds.getX(), y = (int)userBounds.getY(),
//		     w = (int)userBounds.getWidth(), h = (int)userBounds.getHeight();
		int x=deviceBounds.x, y=deviceBounds.y, w=deviceBounds.width, h=deviceBounds.height;
		b=deviceBounds;
		im = new BufferedImage( w,h, BufferedImage.TYPE_INT_ARGB);
		WritableRaster r=im.getRaster();
		for(int i=0;i<w;i++) for(int j=0;j<h; j++){
			double f = Math.min(centre.distance(i+x,j+y)/fallof,1);
			r.setPixel(i,j,new int[]{
				(int)(c1.getRed() * (1-f) + c2.getRed() * f),
				(int)(c1.getGreen() * (1-f) + c2.getGreen() * f),
				(int)(c1.getBlue() * (1-f) + c2.getBlue() * f),
				255

			});
		}
		return this;
	}
	public int getTransparency() {
		return OPAQUE;
	}
}
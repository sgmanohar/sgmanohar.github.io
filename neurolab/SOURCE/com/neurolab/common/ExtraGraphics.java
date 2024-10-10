package com.neurolab.common;

import java.awt.*;

//import java.awt.geom.*;

/**
 * Static methods to aid conversion to Java 1.1.8
 */

public class ExtraGraphics {
  static Class c=null, rh, bs;
  {
    try{
      c=Class.forName("java.awt.Graphics2D");
      rh=Class.forName("java.awt.RenderingHints");
      bs=Class.forName("java.awt.BasicStroke");
    }catch(Exception e){}
  }
	public static void antiAlias(Graphics g){
          try{
            c=Class.forName("java.awt.Graphics2D");
            if(c==null)return;
            rh=Class.forName("java.awt.RenderingHints");
          if (c.isInstance(g)) {
              c.getMethod("setRenderingHint",
                          new Class[] {Class.forName("java.awt.RenderingHints$Key"),
                          Object.class})
                  .invoke(g, new Object[] {rh.getField("KEY_ANTIALIASING").get(null),
                          rh.getField("VALUE_ANTIALIAS_ON").get(null)});
            }
          }catch(Exception e){}
          ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        }
	public static void setStrokeThickness(Graphics g, float thick){
          try{
            c=Class.forName("java.awt.Graphics2D");
            bs=Class.forName("java.awt.BasicStroke");
           if(c==null)return;
            if (c.isInstance(g)) {
              c.getMethod("setStroke", new Class[]{Class.forName("java.awt.Stroke")})
                  .invoke(g, new Object[]{
                            bs.getConstructor(new Class[]{Float.TYPE})
                            .newInstance(new Object[]{new Float(thick)})
                          }
                  );
            }
          }catch(Exception e){}
		((Graphics2D)g).setStroke(new BasicStroke(thick));
	}
	public static  void doFillShape(Graphics g, Object shape){
          try {
            c = Class.forName("java.awt.Graphics2D");
            if (c == null)return;
            if (c.isInstance(g)) {
              c.getMethod("fill", new Class[] {Class.forName("java.awt.Shape")})
                  .invoke(g, new Object[] {shape});
            }
          }
            catch (Exception e) {}

       //    ( (Graphics2D) g).fill((Shape)shape);
	}

}

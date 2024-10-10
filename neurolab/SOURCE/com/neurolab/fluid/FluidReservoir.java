package com.neurolab.fluid;

import java.awt.*;
import com.neurolab.common.*;

public class FluidReservoir
    extends FluidComponent {
  public double level = 0.5;
  public FluidReservoir(FluidPanel p, Point p1, Point p2) {
    super(p, p1, p2);
  }

  public void paint(Graphics g) {
    NeurolabExhibit.setStrokeThickness(g, stroke);
    g.setColor(linecolor);
    g.drawLine( (int) p1.x, (int) p1.y, (int) p1.x, (int) p2.y);
    g.drawLine( (int) p1.x, (int) p2.y, (int) p2.x, (int) p2.y);
    g.drawLine( (int) p2.x, (int) p2.y, (int) p2.x, (int) p1.y);
    g.setColor(watercolor);
    if (level > 0) {
      if (level > 1) {
        level = 1;
      }
      double height = p2.y - p1.y - inset;
      g.fillRect( (int) (p1.x + inset),
                 (int) (p1.y + (1 - level) * height),
                 (int) (p2.x - p1.x - 2 * inset),
                 (int) (level * height)+1);
    }
  }
}

package phic.gui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Paint the history stored in each VDouble
 */

public class HorzHistoryGraph extends HorzScrollGraph {
  public HorzHistoryGraph() {
    mainpane=new HistMainPane();
  }
  class HistMainPane extends MainPane{
    public void paint(Graphics g) {
      super.paint(g);
      g.drawImage(upper, scrollPoint - switchPoint + upper.getWidth(), 0, this);
      g.drawImage(lower, scrollPoint - switchPoint, 0, this);
      int n = previousImages.size();
      int cum_t = 0;
      if (enableHistory && scrollPoint > 0)for (int i = n - 1; i >= 0; i--) {
        BufferedImage prev = (BufferedImage) previousImages.get(i);
        cum_t += prev.getWidth();
        g.drawImage(prev, scrollPoint - switchPoint - cum_t, 0, this);
      }
      paintOverlayComponents(g); // draw non-scrolling elements
    }
  }

}

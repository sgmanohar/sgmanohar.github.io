package phic.gui;

import javax.swing.*;
import phic.common.*;
import java.util.*;
import java.awt.*;
import phic.modifiable.Range;
import javax.swing.border.EmptyBorder;

/**
 * Shows reference values of a given variable, as specified in the file
 * ReferenceValues.txt
 */

public class ReferenceValuesPanel extends JPanel {
    IniReader file = new IniReader("ReferenceValues.txt");
    public ReferenceValuesPanel(VisibleVariable vv) {
        setVariable(vv);
    }

    public void setVariable(VisibleVariable vv) {
        readVariable(vv);
        createLabels();
        repaint();
        synchronized (Organ.cycleLock) {
          currentValue = variable.node.doubleGetVal();
        }
    }
    /** The current value of the variable. Set in the initialiser, used by paint() */
    double currentValue;

    Image bigArrow = phic.Resource.loader.getImageResource("LeftArrowRed3D.gif"),
                     smallArrow = phic.Resource.loader.getImageResource(
            "LeftArrowBlue3D.gif");
    Map map;
    VisibleVariable variable;
    final void readVariable(VisibleVariable vv) {
        this.variable = vv;
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        try {
            try {
                map = file.getSectionMap(variable.longName);
            } catch (IllegalArgumentException e) {
              map=file.getSectionMap(variable.canonicalName);
            }
            //calculate the range
            for (Iterator i = map.values().iterator(); i.hasNext(); ) {
                double d = ((Double) i.next()).doubleValue();
                extendRangeTo(d);
            }
            foundValues=true;
        } catch (Exception e) {
            System.out.println("Cannot find reference values for variable '"+vv.canonicalName+"' in 'resources/ReferenceValues.txt'");
            if(!(e instanceof IllegalArgumentException)) e.printStackTrace();
            map = new HashMap();
            foundValues=false;
        }
        extendRangeTo(variable.minimum);
        extendRangeTo(variable.maximum);
        synchronized (Organ.cycleLock) {
            extendRangeTo(variable.node.doubleGetVal());
        }
        min = Range.findRange(min, Range.ZOOM_IN).minimum;
        max = Range.findRange(max, Range.ZOOM_IN).maximum;
        if (max / min > 20) {
            min = 0;
        }
    }
    /**
     * This value is true when values have been found for the given variable
     * from the ReferenceValues.txt file
     */
    public boolean foundValues = false;

    public void doLayout(){
      super.doLayout();
      createLabels();
    }


    JLabel[] labels;
    /**
     * Creates labels for the panel, at the appropriate locations for the
     * values to be displayed.
     */
    void createLabels(){
      removeAll();
      setLayout(null);
      labels=new JLabel[map.size()];
      Vector bottoms = new Vector();
      for (Iterator i = map.keySet().iterator(); i.hasNext(); ) { //reference vals
          Object name = i.next();
          try {
              double val = ((Double) map.get(name)).doubleValue();
              int pos = getPos(val);
              String paintString = variable.formatValue(val,true,true) + ": " +
                                   name.toString();
              JLabel label = new JLabel(paintString);
              add(label);
              Dimension s = label.getPreferredSize();
              if(pos>getHeight()-50){
                bottoms.add(label);
                while(pos>getHeight()-20){
                  pos-=5;
                  for(int j=0;j<bottoms.size();j++){
                    JLabel l=(JLabel)bottoms.get(j);
                    l.setLocation(l.getX(),l.getY()-5);
                  }
                }
              }
              System.out.println(paintString+" - "+pos+" px / "+getHeight());
              Rectangle r = new Rectangle(sx + 47, pos + 5, s.width,s.height);
              double bx = r.getMaxY()-getHeight();
              if(bx>0)r.setLocation(r.x,r.y+(int)(bx+1));
              label.setBounds(r);
          } catch (Exception e) {
              continue;
          } // don't worry about errors during pain
      }
    }

    /** Decreases min or increases max to include the given value in the range
     * @param value the value to include in the range. */
    final void extendRangeTo(double value) {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    /** The Range of values that are to be displayed */
    double max, min;

    /**
             * Convert a value to its position in the display, using min and max to scale
     * the value.
             * @param value the value of the variable to convert into screen coordinates
     * @return the position to draw the value in screen coordinates
     */
    int getPos(double value) {
        return (int) (getHeight() * (max - value) / (max - min));
    }

    /** position of scale (default 10 pixels) */
    int sx = 70;
    /** Draw the display */
    public void paint(Graphics g) {
        super.paint(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                          RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(sx, 0, sx, getHeight());
        int nTicks = 5;
        for (int i = 0; i <= nTicks; i++) { //ticks
            int h = (nTicks-i) * getHeight() / nTicks;
            if (h == getHeight()) {
                h--;
            }
            g.drawLine(sx, h, sx + 10, h);
            if (h < 10) {
                h += 5;
            }
            if (h > getHeight() - 10) {
                h -= 10;
            }
            g.drawString(variable.formatValue(min+i * (max - min) / nTicks, true, true), 3,
                         h + 5);
        }
        int n=0;
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) { //reference vals
            Object name = i.next(); n++;
            try {
                double val = ((Double) map.get(name)).doubleValue();
                int pos = getPos(val);
                g.drawImage(smallArrow, sx + 15,
                            pos - smallArrow.getHeight(this) / 2, this);
                String paintString = variable.formatValue(val,true,true) + ": " +
                                     name.toString();
                g.drawLine(sx+20, pos,
                  labels[n].getX(), (int)labels[n].getBounds().getCenterY());
                //g.drawString(paintString, sx + 47, pos + 5);
            } catch (Exception e) {
                continue;
            } // don't worry about errors during paint
        }

        g.drawImage(bigArrow, sx + 15,
                    getPos(currentValue) - bigArrow.getHeight(this) / 2, this);
        g.setColor(Color.cyan);
        int top=getPos(variable.maximum);
        g.fill3DRect(sx+10, top, 5, getPos(variable.minimum)-top,true);

        super.paintChildren(g);
    }

}

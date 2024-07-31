package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import phic.Current;
import phic.common.Clock;

/**
 * The scroll panel that draws a vertically scrolling trace of the value of
 * a given VisibleVariable. It is actually obsolete and replaced by
 * CombinedScrollGraph.
 */
public class ScrollGraphPane extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();

    JPanel jPanel1 = new JPanel();

    JPanel jPanel2 = new JPanel();

    Border border1;

    Border border2;

    BorderLayout borderLayout2 = new BorderLayout();

    JPanel graphpanel = new GraphPanel();

    JPanel xscalebar = new JPanel() {
        public void paint(Graphics g) {
            super.paint(g);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                              RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.white);
            if (variable != null) {
                g.drawString(variable.formatValue(variable.minimum, false, false),
                             0, 10);
                g.drawString(variable.formatValue(variable.initial, false, false),
                             getscreenx(variable.initial), 10);
                g.drawString(variable.formatValue(variable.maximum, false, false),
                             getWidth() - 30, 10);
            }
            for (int i = 0; i < 10; i++) {
                g.drawLine(i * getWidth() / 10, 15, i * getWidth() / 10, 20);
            }
        }
    };

    public ScrollGraphPane() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer.start();
        /*	varnamebox.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
         setVariable(VariableChooser.selectVariable(variable));
         }});
        addMouseListener
        (new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               ((GraphPanel) graphpanel).plot(e.getX());
           }
       });
       */
//allow dragging of number to change value
        valuetext.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
        valuetext.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouse = e.getPoint();
            }
        });
        valuetext.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int d = e.getY() - mouse.y;
                double val = variable.node.doubleGetVal();
                double du = phic.common.Quantity.getNearestRoundBelow(val);
                if (d != 0) {
                    variable.node.doubleSetVal(val + du * d);
                }
            }
        });
    }

    Point mouse;

    static int pixelrate = 7;

    int timermsec = 100;

    VisibleVariable variable = null;

    public void setVariable(VisibleVariable n) {
        variable = n;
//		varnamebox.setText(n.longName);
    }

    Timer timer = new Timer(timermsec, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (Current.body.getClock().running && variable != null) {
                double val = variable.node.doubleGetVal();
                plot(val);
                valuetext.setText(variable.formatValue(val, true, true));
            }
        }
    });

    Border border3;

    JTextField valuetext = new JTextField();

    static int colourindex = 0;

    static final Color[] colourset = new Color[] {Color.red, Color.yellow,
                                     Color.cyan,
                                     Color.orange, Color.green, Color.pink};

    final Color colour = colourset[colourindex++ % colourset.length];

    BorderLayout borderLayout3 = new BorderLayout();

    Border border4;

    public void plot(double d) {
        ((GraphPanel) graphpanel).plot(getscreenx(d));
    }

    int getscreenx(double d) {
        if (variable != null) {
            return (int) ((d - variable.minimum) * graphpanel.getWidth() /
                          (variable.maximum
                           - variable.minimum));
        } else {
            return 0;
        }
    }

    private void jbInit() throws Exception {
        border1 = BorderFactory.createCompoundBorder(BorderFactory.
                createBevelBorder(
                BevelBorder.LOWERED, Color.white, Color.white,
                new Color(134, 134, 134),
                new Color(93, 93, 93)),
                  BorderFactory.createEmptyBorder(2, 2, 2, 2));
        border2 = BorderFactory.createCompoundBorder(BorderFactory.
                createEtchedBorder(
                Color.white, new Color(134, 134, 134)),
                BorderFactory.createEmptyBorder(1, 1,
                                                1, 1));
        border3 = BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                                                  Color.white,
                                                  Color.white,
                                                  new Color(134, 134, 134),
                                                  new Color(93, 93, 93));
        border4 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        this.setLayout(borderLayout1);
        jPanel2.setLayout(borderLayout2);
        this.setBorder(border2);
        this.setPreferredSize(new Dimension(136, 160));
        graphpanel.setBackground(Color.black);
        graphpanel.setForeground(Color.red);
        xscalebar.setBackground(Color.black);
        xscalebar.setForeground(Color.lightGray);
        xscalebar.setPreferredSize(new Dimension(10, 20));
        valuetext.setBackground(Color.black);
        valuetext.setFont(new java.awt.Font("SansSerif", 1, 12));
        valuetext.setForeground(colour);
        valuetext.setPreferredSize(new Dimension(110, 21));
        valuetext.setCaretColor(Color.orange);
        valuetext.setDisabledTextColor(Color.red);
        valuetext.setEditable(false);
        valuetext.setText("0.0");
        valuetext.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel1.setBorder(border4);
        jPanel1.setPreferredSize(new Dimension(130, 23));
        jPanel1.setLayout(borderLayout3);
        this.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(valuetext, BorderLayout.CENTER);
        this.add(jPanel2, BorderLayout.CENTER);
        jPanel2.add(xscalebar, BorderLayout.NORTH);
        jPanel2.add(graphpanel, BorderLayout.CENTER);
    }

    class GraphPanel extends JPanel {
        public GraphPanel() {
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    for (int i = 0; i < 2; i++) {
                        image[i] = new BufferedImage(getWidth(), getHeight(),
                                BufferedImage.TYPE_BYTE_INDEXED);
                        image[i].getGraphics().clearRect(0, 0, getWidth(),
                                getHeight());
                    }
                }
            });
        }

        public void paint(Graphics g_) {
            super.paint(g_);
            Graphics2D g = (Graphics2D) g_;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            if (image[0] != null) {
                g.drawImage(image[(bank + 1) % 2], 0, -pos, null);
                g.drawImage(image[bank], 0, image[bank].getHeight() - pos, null);
            }
        }

        BufferedImage[] image = new BufferedImage[2];

        int pos = 0, bank = 0;

        int prevp;

//		long gridinterval=1000*60*60;
        long minorinterval = (long) (Current.body.getClock().getSecond() * 60);

        long majorinterval = minorinterval * 60;

        long lastminmod, lastmajmod;

        public void plot(int x) {
            if (image[0] == null) {
                return;
            }
            pos += pixelrate;
            if (pos >= image[0].getHeight()) {
                pos = 0;
                //switch banks
                bank = (bank + 1) % 2;
                //flush new bank
                image[bank].getGraphics().clearRect(0, 0, image[bank].getWidth(),
                        image[bank].getHeight());
            }
            Graphics2D g = (Graphics2D) image[bank].getGraphics();
//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//will this work in 256colours?
            int op;
            if ((op = pos - pixelrate) < 0) {
                op = 0;
                //draw zeroline
            }
            g.setColor(Color.gray);
            if (variable != null) {
                int z = getscreenx(variable.initial);
                g.drawLine(z, op, z, pos);
            }
            //draw ticks & scale
            long t = Current.body.getClock().getTime();
            if ((t % minorinterval) - lastminmod < 0) { //have we crossed a whole-hour boundary?
                g.drawLine(0, pos, 6, pos);
                int pp;
                if (pos - 10 < 0) {
                    pp = 0;
                } else {
                    pp = pos - 10;
                }
                if ((t % majorinterval) - lastmajmod < 0) {
                    g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
                                                BasicStroke.JOIN_BEVEL, 0.1f,
                                                new float[] {2, 1}
                                                , 0));
                    g.drawLine(0, pos, getWidth(), pos);
                    //day=t%1000*60*60*24
                    String ts = Current.body.getClock().getTimeString(Clock.
                            TIME);
                    g.drawString(ts, 0, pp);
                } else {
                    for (int i = 0; i < 10; i++) {
                        g.drawLine(i * getWidth() / 10, pos,
                                   i * getWidth() / 10, pos);
                    }
                }
            }
            lastminmod = t % minorinterval;
            lastmajmod = t % minorinterval;
            //draw graph
            g.setColor(colour);
            g.drawLine(prevp, op, prevp = x, pos);
            repaint();
        }
    }
    /*
     void closebutton_actionPerformed(ActionEvent e) {
     setVisible(false);
     Container p=getParent();
     p.remove(this);
     p.invalidate();
     }
     */
}
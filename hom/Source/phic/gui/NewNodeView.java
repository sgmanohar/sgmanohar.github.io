package phic.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;

import phic.*;
import phic.common.*;
import java.awt.Container;
/**
 * Node views are panels that implement a view of a specific phic variable
 * identified by a node on the object tree.
 * They are created by the PhicFrame class, and added to the frame. The default
 * interface provides a title label and a close button, almost like a MDI window
 * Perhaps that will be a future implementation of this class?
 */
public class NewNodeView extends JDesktopPane {
    static ImageIcon methodicon = new ImageIcon(Resource.loader.
                                                getImageResource(
            "Method.gif"));
    /** Integers representing the kinds of view avaiable */
    static final int DOUBLE_VALUE = 0, DOUBLE_BAR = 1, DOUBLE_SCROLLGRAPH = 2,
            DOUBLE_SLIDER = 3, BOOLEAN_CHECKBOX = 4, METHOD_BUTTON = 5,
            CONTAINER_TABLE = 6,
                              VALUE_RANGE = 7, CONTAINER_DIAGRAM = 8;
    /** The Strings representing kinds of view. The string may be used in
     *  graph setup files to specify node views, and give the names types
     *  available when Type objects are created. */
    public static String[] string = new String[] {"Value", "Bar", "Graph",
                                    "Slider",
                                    "Checkbox", "Button", "Table",
                                    "Value+Range", "Diagram"
    };
    /** Static type object for Nodes of these types. They correspond to the
     * types of Node. @see Node */
    public static Type[] doubleType = new Type[] {new Type(3), new Type(1),
                                      new Type(2),
                                      new Type(0), new Type(7)}
                                      , booleanType = new Type[] {new Type(4)}
            ,
            methodType = new Type[] {new Type(5)}
                         , containerType = new Type[] {new Type(6),
                                           new Type(8)};

    /** Icons representing the different nodeviews. */
    static final String[] iconResources = {
      "Double.gif", "Double.gif", "Double.gif", "Double.gif",
      "Tick.gif",
      "Method.gif",
      "Container.gif", "Double.gif", "Container.gif"
    };

    /** NodeView types are objects that simply represent a kind of view. There
     *  are a fixed number of static type objects. They can be converted to
     *  Strings. The static method forNode is provided. */
    public static class Type {
        public Type(int a) {
            type = a;
        }

        public Type(String name) {
            for (int i = 0; i < string.length; i++) {
                if (string[i].equals(name)) {
                    type = i;
                    return;
                }
            }
            throw new IllegalArgumentException("No such display type " + name);
        }

        /** Returns an array of view types that can be used with the
         *  specified node. */
        public static Type[] forNode(Node node) {
            if (node != null) {
                switch (node.getType()) {
                case Node.DOUBLE:
                    return doubleType;
                case Node.BOOLEAN:
                    return booleanType;
                case Node.SIMPLE_METHOD:
                    return methodType;
                case Node.CONTAINER:
                    return containerType;
                }
            }
            return new Type[0];
        }

        int type;
        public String toString() {
            return string[type];
        }
    }


    Node node;
    VisibleVariable v;


    /** When the node objects have changed, this is called to get the new objects. */
    public void replaceVariables() {
      if (v != null) {
        v = Variables.forName(v.canonicalName);
        node = v.node;
        if(type==DOUBLE_BAR)h.setVariable(v);
      } else {
        node = Node.findNodeByName(node.canonicalName());
        if(type==BOOLEAN_CHECKBOX){
          check.setSelected(node.booleanGetVal());
        }
      }
    }


    JButton closebutton = new JButton();
    NewNodeView thisView = this;
    JPanel panel = new JPanel();
    HorizontalBar h;
    JCheckBox check;
    TitleBar titleBar;
    java.awt.Container content;

    /** create the interface, including title bar and close button. */
    void gui() {
      setLayout(new BorderLayout());
      //content.setLayout(new FlowLayout());
        //panel.setLayout(new BorderLayout());
        //add(panel, BorderLayout.NORTH);
        //close button
        /* //migrated to TitleBar
        closebutton.setToolTipText("Close this graph");
        closebutton.setIcon(new ImageIcon(Resource.loader.getImageResource(
                "SmallCross.gif")));
        closebutton.setMargin(new Insets(0, 0, 0, 0));
        closebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                java.awt.Container p = getParent();
                p.remove(thisView);
                p.invalidate();
            }
        });
        */
        //name label

        String name;
        if (v == null && node != null) {
            name = node.canonicalName();
            if(name.startsWith("/Environment/"))
              name=name.substring(13);
            if(name.startsWith("/Body/"))
              name=name.substring(6);
        } else if (v != null) {
            name = v.longName;
        } else {
            name = "View";
        }
      f = new JInternalFrame(name);
      content = f.getContentPane();
      f.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      f.setResizable(false);
      f.setClosable(true);
      //icon
      if(type>=0 && type<iconResources.length){
        String iconResource=iconResources[type];
        f.setFrameIcon(new ImageIcon(Resource.loader.getImageResource(
          iconResource)));
      }
      //show frame
      add(f,BorderLayout.CENTER);
      setDesktopManager(desktopManager);
      //edit title bar
      if(f.getUI() instanceof BasicInternalFrameUI){
        Object o=((BasicInternalFrameUI)f.getUI()).getNorthPane();
        if(o instanceof JComponent){
          JComponent p = (JComponent)o;
          p.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
              displayNodeDialog();
            }
          });
          Font f=p.getFont();
          if(f!=null)p.setFont(f.deriveFont(Font.PLAIN,10f)); //enforce small un-bold title bar
          String toolTip = v==null? node.canonicalName() : v.longName+" ("+node.canonicalName()+")";
          p.setToolTipText(toolTip);
          /*Component co = p.getComponent(0);
          if(co instanceof JLabel){
            JLabel lab =((JLabel)co);
            lab.setIcon(null); lab.setSize(0,0); lab.setHorizontalTextPosition(0); lab.setIconTextGap(0); lab.setIcon(null);
          }
          co.setVisible(false);
          p.remove(co);*/ // failed attempt to prevent the left hand gap.
        }
      }
      //on close
      f.addInternalFrameListener(new InternalFrameAdapter(){
        public void internalFrameClosed(InternalFrameEvent e){
          final java.awt.Container c = thisView.getParent();
          SwingUtilities.invokeLater(new Runnable(){public void run(){
              c.remove(thisView);
              c.getParent().invalidate();
              c.getParent().validate();
          }});
        }
      });
/*
        JLabel title = new JLabel(name);
        title.setOpaque(true);
        title.setBackground(SystemColor.activeCaption);
        title.setForeground(SystemColor.activeCaptionText);
        title.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                //if(e.getClickCount()==2){ // double click for information
                displayNodeDialog();
                //}
            }
        });
        panel.add(title, BorderLayout.CENTER);
        panel.add(closebutton, BorderLayout.EAST);
        */
       //setBorder( BorderFactory.createEtchedBorder());
       if(v!=null)   titleBar=new TitleBar(v); else if(node!=null) titleBar = new TitleBar(node);
       //content.add(titleBar, BorderLayout.NORTH);
       titleBar.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
           if(e.getActionCommand().equals("Close")){
             setVisible(false);
             java.awt.Container p = getParent();
             p.remove(thisView);
           }
         }
       });
    }

    JInternalFrame f;
    void finishGui(){
      f.pack();
      setPreferredSize(f.getPreferredSize());
    }
    public void addNotify(){
      super.addNotify();
      try{
        f.setSelected(true);
      } catch(PropertyVetoException ex){}
      f.show();
    }




    /**
     * Determines whether the component needs to go in the separate shared
     * column for larger components.
     * Should return true if the desired width of the component is greater
     * than 150.
     */
    public boolean needsNewFrame() {
        if (type == VALUE_RANGE || type == CONTAINER_TABLE) {
            return true;
        } else {
            return false;
        }
    }

    int type;
    public NewNodeView(Node n, Type t) {
        this(n, t.type);
    }
    Ticker ticker;
    public NewNodeView(Node n, int view) {
        node = n;
        type = view;
        if (node != null) {
            v = Variables.forNode(node);
        }
        gui();
        if (view == -1) {
            return;
        }
        switch (view) {
        case DOUBLE_VALUE: // a label whose text is the variable's value. updated regularly
            final JLabel label = new JLabel();
            final Node ntmp = n;
            ticker = new Ticker() {
              double oldVal = Double.NaN;
                public void tick(double time) {
                  double newVal = ntmp.doubleGetVal();
                  if(newVal!=oldVal){ //if changed
                    oldVal=newVal;
                    if(v==null){ //then update text
                      label.setText(Quantity.toString(newVal));
                    } else{
                      label.setText(v.formatValue(newVal,true,true));
                    }
                  }
                  if(getParent()==null)HorizontalBar.removeBar(this);//if removed
                }
            };
            ticker.tick(0);
            content.add(label);
            HorizontalBar.addBar(ticker);
            break;
        case DOUBLE_SCROLLGRAPH: // the (defunct) scroll graphs
            ScrollGraphPane s = new ScrollGraphPane();
            s.setVariable(v);
            content.add(s, BorderLayout.CENTER);
            break;
        case DOUBLE_BAR: // a horizontal bar
             h = new HorizontalBar();
            if (v == null) {
                h.setNode(node);
            } else {
                h.setVariable(v);
            }
            h.setFont(new Font("Dialog", Font.BOLD, 12));
            //setPreferredSize(new Dimension(150,35));
            content.add(h);
            break;
        case BOOLEAN_CHECKBOX:
            if (v == null) {
                check = new JCheckBox(n.canonicalName(),
                                      n.booleanGetVal());
            } else {
                check = new JCheckBox(v.longName, n.booleanGetVal());
            }
            check.addChangeListener(new CheckChangeListener());
            content.add(check);
            break;
        case CONTAINER_TABLE: // Table of values for the container contents
            Object o = n.objectGetVal();
            if (o instanceof phic.common.Container) {
                ContainerView c = new ContainerView((phic.common.
                        Container) o, true);
                content.add(c, BorderLayout.CENTER);
            }
            break;
        case VALUE_RANGE:
            if (v != null) {
                ValueRangeLabel vv = new ValueRangeLabel(v);
                content.add(vv);
            }
            break;
        case METHOD_BUTTON:
            String name = n.canonicalName();
            name = name.substring(name.indexOf('/', 1) + 1);
            JButton b = new JButton(name, methodicon);
            b.addActionListener(new ButtonListener());
            content.add(b);
            break;
        case DOUBLE_SLIDER:
            content.add(new SliderView(node));
            break;
        case CONTAINER_DIAGRAM:
            ContainerDiagram d = new ContainerDiagram();
            d.setContainer((phic.common.Container) node.objectGetVal());
            content.add(d, BorderLayout.CENTER);
            break;
        default:
            throw new RuntimeException("Unknown view type " + view);
        }
        finishGui();
    }

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
          String comment = node.getLeafName();
          PhicApplication.markEvent(comment);
          Current.body.message(comment);
            node.methodInvoke();
        }
    };
    class CheckChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
          boolean on = ((JCheckBox) e.getSource()).isSelected();
          boolean prev = node.booleanGetVal();
          if(prev != on){
            node.booleanSetVal(on);
            String comment = node.getLeafName()+(on?" On":" Off");
            PhicApplication.markEvent(comment);
            Current.body.message(comment);
          }
        }
    }


    class SliderView extends JPanel implements phic.common.Ticker,
            ChangeListener {
        SliderView(Node n) {
            setLayout(new BorderLayout());
            node = n;
            slider = new JSlider(JSlider.HORIZONTAL);
            text = new JLabel("");
            add(slider, BorderLayout.CENTER);
            add(text, BorderLayout.EAST);
            slider.setValue(50);
            slider.addChangeListener(this);
            tick(0);
            slider.setPreferredSize(new Dimension(100,
                                                  slider.getPreferredSize().
                                                  height));
            text.setPreferredSize(new Dimension(60,
                                                text.getPreferredSize().height));
            HorizontalBar.addBar(this);
            startval = n.doubleGetVal();
        }
        /** stop updating when removed from display */
        public void removeNotify(){
          HorizontalBar.removeBar(this);
          HorizontalBar.removeBar(ticker);
          super.removeNotify();
        }

        JLabel text;
        JSlider slider;
                /** Initial value of variable for those that are not VisibleVariables */
        double startval;
        /** Whether this is a user manual change in the slider position, or an automatic one
         * to reflect variable value changing.
         */
        boolean automaticAdjust = false;
        /** Update the slider position with the variable's value */
        double oldVal = Double.NaN;
        public void tick(double t) {
            if (v != null) {
                automaticAdjust = true;
                double value = node.doubleGetVal();
                if(value != oldVal){
									oldVal=value;
									slider.setValue((int)(100*(value-v.minimum)
                    /(v.maximum
                    -v.minimum)));
									text.setText(v.formatValue(value,true,false));
								}
                automaticAdjust=false;
            }
        }

        /** Update the variable's value using the new slider position  */
        public void stateChanged(ChangeEvent e) {
            if (automaticAdjust) {
                return;
            }
            String newString;
            double newVal;
            if (v != null) {
                newVal = v.minimum
                         + (v.maximum - v.minimum) * slider.getValue()
                         / 100.;
                newString = v.formatValue(newVal, true, false);
            } else {
                newVal = startval * (1 + (slider.getValue() - 50) / 50.);
                newString = Quantity.toString(newVal);
            }
            node.doubleSetVal(newVal);
            text.setText(newString);
        }
    }

 //migrated to TitleBar
    public void displayNodeDialog() {
        if (v != null) {
            VariablePropertiesDialog d = new VariablePropertiesDialog();
            d.setVariable(v);
            d.show();
        } else {
            System.out.println(node.canonicalName() +
                               " is not a visible variable");
        }
    }



    /**
     * Class to handle the title bar of a node view, with a close button that
     * generates actionEvents, and on clicking the title, displays a
     * VariablePropertiesDialog for the given variable.
     * The title bar can be created for a node or for a variable.
     */
    public static class TitleBar extends JPanel{

      Node node;
      VisibleVariable variable;
      String title="";
      JLabel label = new JLabel();
      JButton closebutton = new JButton();
      ActionListener al = null;
      /**
       * The action listener must handle "Close" commands, and remove the
       * variable's view from the display.
       */
      public void addActionListener(ActionListener al2){
        al=AWTEventMulticaster.add(al,al2);
      }
      public void removeActionListener(ActionListener al2){
        al=AWTEventMulticaster.remove(al2, al);
      }
      /** Create a title bar for the view of the specified node */
      public TitleBar(Node n){
        String name;
        node=n;
        if(node==null) name="View"; else{
          title=node.canonicalName();
          if(title.startsWith("/Environment/"))
            title=title.substring(13);
          if(title.startsWith("/Body/"))
            title=title.substring(6);
        }
        init();
      }
      /** Create a title bar for a view of the specified variable */
      public TitleBar(VisibleVariable v){
        variable=v; node=v.node; title=v.longName;
        init();
      }
      /** Initialise the title bar */
      private void init(){
        setLayout(new BorderLayout());
        label.setOpaque(true);
        label.setText(title);
        label.setBackground(SystemColor.activeCaption);
        label.setForeground(SystemColor.activeCaptionText);
        label.addMouseListener(new MouseAdapter(){
          public void mouseReleased(MouseEvent e){
            //if(e.getClickCount()==2){ // double click for information
            displayNodeDialog();
            //}
          }
        });
        String toolTip = variable==null? node.canonicalName() : variable.longName+" ("+node.canonicalName()+")";
        label.setToolTipText(toolTip);
        add(label, BorderLayout.CENTER);
        closebutton.setToolTipText("Close this graph");
        closebutton.setIcon(new ImageIcon(Resource.loader.getImageResource(
          "SmallCross.gif")));
        closebutton.setMargin(new Insets(0, 0, 0, 0));
        /**
         * Notify the action listener with command "Close". The action listener
         * should take the action of removing the NodeView etc. from the display.
         * Invalidate() is called on the parent container of the titlebar.
         */
        closebutton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            java.awt.Container p = getParent();
            al.actionPerformed(new ActionEvent(this, 0, "Close"));
            p.invalidate();
          }
        });
        add(closebutton,BorderLayout.EAST);
      }
      /** Get the title text of this titlebar */
      public String getTitle(){ return title; }
      /** Set the title text of the title bar */
      public void setTitle(String s){
        title = s;
        label.setText(s);
      }
      /**
       * Display the dialog for information on this variable. The method
       * is called on clicking (or double clicking) the title bar.
       */
      public void displayNodeDialog() {
         if (variable != null) {
             VariablePropertiesDialog d = new VariablePropertiesDialog();
             d.setVariable(variable);
             d.show();
         } else {
             System.out.println(node.canonicalName() +
                                " is not a visible variable");
         }
     }
   }

   static DesktopManager desktopManager = new DefaultDesktopManager(){
     public void dragFrame(JComponent c, int x, int y){}
   };
}

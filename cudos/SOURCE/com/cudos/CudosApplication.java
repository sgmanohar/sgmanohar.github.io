package com.cudos;

import java.awt.*;
import com.cudos.common.*;
import java.net.URL;
import java.lang.reflect.Constructor;
import javax.swing.*;
import java.util.Vector;

/**
 *  An application class based on a JFrame
 */

public class CudosApplication extends JFrame implements CudosApplet {
  BorderLayout borderLayout1 = new BorderLayout();
  TitleBar titleBar1 = new TitleBar();
  public static void main(String[] s){
    CudosApplication app=new CudosApplication();
    app.params = s;
  }
  String[] params;
  public CudosApplication() throws HeadlessException {
    try {
      jbInit();
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(900,700);
    init();
    show();
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
  private void init(){
    String clsname = getParameter("exhibit");
    if (clsname != null) {
      int p = clsname.indexOf(':');
      if (p > 0) {
        String params = clsname.substring(p + 1);
        clsname = clsname.substring(0, p);
        toExhibit(clsname, params);
        return;
      }
    }
    else {
      clsname = "com.cudos.ModuleChooser";
    }
    toExhibit(clsname);
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    mainpanel.setLayout(borderLayout2);
    this.getContentPane().add(titleBar1, BorderLayout.NORTH);
    this.getContentPane().add(mainpanel, BorderLayout.CENTER);
  }
  public URL getResourceURL(String s){
    return(getClass().getResource("../../"+s));
  }
  public Image getImage(String s){
    Image i=null;
    try{
      i= Toolkit.getDefaultToolkit().createImage(getResourceURL(s));
    }catch(Exception e){error(e.toString()+": error loading image "+s);}
    if(i==null) error("Could not find image "+s);
    return i;
  }
  public String getParameter(String s){
    if(params==null)return null;
    for(int i=0;i<params.length;i++){
      String left = params[i].substring(0,params[i].indexOf('=')).trim();
      if(left.equalsIgnoreCase(s)){
        return params[i].substring(params[i].indexOf('=')+1).trim();
      }
    }
    return null;
  }
  public int getTextHeight(Graphics2D g, String s){
    return (int)g.getFontMetrics().getStringBounds(s,g).getHeight();
  }
  public int getTextWidth(Graphics2D g, String s){
    return (int)g.getFontMetrics().getStringBounds(s,g).getWidth();
  }
  public void paintComponentMessage(Component c,String s){
    Graphics2D g=(Graphics2D)c.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(new Font("SansSerif",Font.BOLD,16));
    paintText3D(g,s,(c.getWidth()-getTextWidth(g,s))/2,(c.getHeight()-getTextHeight(g,s))/2);
  }
  public void paintText3D(Graphics2D g,String t,int x,int y){
    g.setPaint(SystemColor.controlLtHighlight);
    g.drawString(t, x + 1, y + 1);
    g.setPaint(SystemColor.controlShadow);
    g.drawString(t, x, y);
  }

  Vector stack = new Vector();
  public void toChooser(){
          stack.remove(content.getClass().getName());
          String oldcls=(String)stack.get(stack.size()-1);
          setExhibitContent(oldcls);
  }
  public void toExhibit(String cls){
          setExhibitContent(cls);
          stack.add(cls);
  }

  public void toExhibit(String cls,Object param){
          setExhibitContent(cls,param);
          stack.add(cls);
  }

  private CudosExhibit content;
  JPanel mainpanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  private void setExhibitContent(CudosExhibit e) {
    CudosExhibit newcontent = e;
    if (newcontent != null) {
      if (content != null) {
        mainpanel.remove(content);
      }
      content = newcontent;
      mainpanel.add(content, BorderLayout.CENTER);
      validateTree();
      setTitle(content.getExhibitName());
      content.postinit();
      mainpanel.repaint();
    }
  }

  void setExhibitContent(String clsname, Object param){
    CudosExhibit newcontent;
    try {
      Class cls = Class.forName(clsname); // Class cls=cloader.loadClass(clsname);
      if(param!=null){
        Constructor cn = cls.getConstructor(new Class[] {param.getClass()});
        newcontent = (CudosExhibit) (cn.newInstance(new Object[] {param}));
      }else{ newcontent = (CudosExhibit) (cls.newInstance()); }
      setExhibitContent(newcontent);
    }
    catch (Exception e) {
      e.printStackTrace();
      error(e+": could not create exhibit "+clsname+" with param "+param);
    }
  }
  public void setTitle(String s){super.setTitle(s);titleBar1.setTitle(s);titleBar1.repaint();}
  void setExhibitContent(String clsname){ setExhibitContent(clsname, null); }
  public void error(String s){
    JOptionPane.showMessageDialog(this, s, "CUDOS Application Error",
    JOptionPane.ERROR_MESSAGE);
  }

  public void play(URL uRL) {
    try{
      sun.audio.AudioPlayer.player.start(new sun.audio.AudioStream(uRL.
          openStream()));
    }catch(Exception e){e.printStackTrace();}
  }
}

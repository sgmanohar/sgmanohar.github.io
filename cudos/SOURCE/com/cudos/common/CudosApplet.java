package com.cudos.common;

import java.net.*;

import java.awt.*;

/**
 * Extracts functions in the applet
 */

public interface CudosApplet {
  public Image getImage(String resource);
  public int getTextWidth(Graphics2D g, String s);
  public void toChooser();
  public void toExhibit(String exhname, Object params);
  public void toExhibit(String exhname);
  public void paintComponentMessage(Component c, String swait);
  public void setTitle(String s);
  public String getTitle();
  public void paintText3D(Graphics2D g, String text, int x, int y);
  public int getTextHeight(Graphics2D g, String text);
  public URL getResourceURL(String resource);
  public String getParameter(String param);

  public void play(URL uRL);
}

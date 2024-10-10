package com.neurolab.common;

import java.net.URL;
import java.awt.Image;

public interface HoldsExhibit{
	public abstract void setExhibit(String a);
	public abstract Image getImage(String resourceName);
	public abstract URL getURL(String resourceName);
}


/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import javax.swing.JPanel;
import java.awt.*;

public class CudosExhibit extends JPanel {
  public Container getContentPane(){return this;};
  public CudosExhibit() {
  }
  public String getExhibitName(){return "";}
  public CudosApplet getApplet(){
          Component p=this;
          while((p!=null) && !(p instanceof CudosApplet)){
            p=p.getParent();
          }
	  if(p==null)throw new RuntimeException("Exhibit not within applet!");
          return (CudosApplet)p;
        }
  public static CudosApplet getApplet(Component p){
          while((p!=null) && !(p instanceof CudosApplet)){
            p=p.getParent();
          }
	  if(p==null)throw new RuntimeException("Component not within applet!");
          return (CudosApplet)p;
        }

  public void postinit(){};
}
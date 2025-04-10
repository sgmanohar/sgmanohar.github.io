
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

import java.util.Vector;

public class CudosClassLoader extends ClassLoader {

  public CudosClassLoader() {
  }

	ProgressDialog d=new ProgressDialog("Loading program");
	Vector v=new Vector();

	public Class loadClass(String name) throws ClassNotFoundException {
			//turn on loading message
		if(v.isEmpty()) d.show();
		v.add(name);

		Class c=super.loadClass(name);

			//turn off loading message
		v.remove(name);
		if(v.isEmpty())d.hide();

		return c;
	}
/*	protected Class findClass(String name) throws ClassNotFoundException {
		//turn off loading message
		v.remove(name);
		if(v.isEmpty()) d.hide();

		return super.findClass(name);
	}
*/
}
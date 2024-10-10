package com.cudos.server.client;

import java.rmi.*;

/**
 * This is a handle object returned by a server on logging in, which gives the client access
 * to the Student data for the logged in student.
 */

public class ClientHandle {
	public static void error(String e){
		javax.swing.JOptionPane.showMessageDialog(null, e,"Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	public void finalize() throws Throwable{
		try{ ((Server)Naming.lookup(Server.location)).logout(this);}
		catch(Exception e){System.out.println("Could not log out "+this); e.printStackTrace();}
	}
}
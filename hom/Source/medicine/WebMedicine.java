package medicine;

import java.applet.*;
import javax.swing.*;
import medicine.*;
import medicine.gui.*;

import java.io.*;
import java.net.*;
/**
 * Runs Medicine from an applet
 */

public class WebMedicine extends Applet {

	public WebMedicine() {
	}
	public void start(){
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (Exception e) {e.printStackTrace();}
		//frame with saving disabled
		MainFrame f=new MainFrame(false);
		try{
			InputStream data = new URL(getCodeBase(),"resources/medical.med")
																.openStream();
			f.readDatabaseFrom(data, "Web database");
		}catch(Exception e){
			throw new RuntimeException("Error reading from:"+e.toString());
		}
		f.show();
	}
}
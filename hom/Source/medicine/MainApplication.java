
/**
 * This runs the program. A parameter, the filename to load, may be passed to
 * the main() method.
 */
package medicine;
import medicine.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainApplication {

  public MainApplication() {
  }
	public static MainFrame frame;
	public static void main(String[] params){
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (Exception e) {e.printStackTrace();}

		frame=new MainFrame();
		if(params.length>0){
			final String file = params[0];
                        Thread nt=new Thread(new Runnable(){
                          public void run(){
                            try{
                              File f=new File(file);
                              InputStream stream = new FileInputStream(f);
                              if (file.endsWith("txt")) {
                                frame.readTextFromStream(stream, file);
                              }
                              else if (file.endsWith("zip")) {
                                frame.readTextFromZip(stream, file);
                              }
                              else {
                                frame.readDatabaseFrom(stream, file);
                              }
                              frame.filer.setSelectedFile(f);
                            }catch(IOException ex){
                              ex.printStackTrace();
                              JOptionPane.showMessageDialog(null, ex.getMessage(), "Open error: "+file,
                              JOptionPane.ERROR_MESSAGE);
                            }catch(ClassNotFoundException ex){
                              ex.printStackTrace();
                             JOptionPane.showMessageDialog(null, ex.getMessage(), "Incorrect format: "+file,
                             JOptionPane.ERROR_MESSAGE);
                            }
                          }
                        });
			nt.start();
		}
		frame.show();
	}
	public static Point getCentre(){
		if(frame==null)return null;
		return new Point(
			frame.getLocation().x + frame.getSize().width / 2,
			frame.getLocation().y + frame.getSize().height / 2 );
	}
	public static void centreWindow(Window w){
		if(frame==null)return;
		Point p=getCentre();
		p.x = p.x - w.getWidth() / 2;
		p.y = p.y - w.getHeight() / 2;
		w.setLocation(p);
	}
}

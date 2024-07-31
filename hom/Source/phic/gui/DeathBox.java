package phic.gui;
import java.awt.Dimension;
import java.beans.*;
import java.io.*;
import javax.swing.*;
import phic.Resource;

/**
 * A panel that is shown when the patient dies, giving the cause of death
 * and offering help, and optionally, a button to restart the simulation.
 */
public class DeathBox extends JOptionPane{
	protected static final String[] options3={"OK","Help","Restart"},
			options2={"OK","Help"};

	protected String[] optionSet;

	public DeathBox(Object reason,boolean offerRestart){
		super(reason,WARNING_MESSAGE,DEFAULT_OPTION,null,
				(offerRestart?options3:options2),options3[0]);
		addPropertyChangeListener(pcl);
	}

	PropertyChangeListener pcl=new PropertyChangeListener(){
		boolean showing=false;

		public void propertyChange(PropertyChangeEvent e){
			if(!showing&&getValue()==options3[1]){ //Help pressed
				showing=true;
				String helptext="";
				BufferedReader r=new BufferedReader(new InputStreamReader(Resource.
						loader.getResource("help/ModesOfDying.html")));
				String t;
				try{
					while((t=r.readLine())!=null){
						helptext+="\n"+t;
					}
				} catch(IOException x){
					x.printStackTrace();
				}
				JEditorPane editorpane=new JEditorPane("text/html",helptext);
				editorpane.setEditable(false);
				editorpane.setCaretPosition(1);
				JScrollPane sp=new JScrollPane(editorpane);
				sp.setPreferredSize(new Dimension(400,300));
				JOptionPane.showMessageDialog(null,sp,"Help on cause of death",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	};
}
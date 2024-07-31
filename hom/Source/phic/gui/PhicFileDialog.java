
package phic.gui;
import javax.swing.*;
import java.io.*;
import phic.*;
import phic.common.*;
import java.awt.Component;
import sun.awt.shell.ShellFolder;
import phic.modifiable.Script;
import evaluator.MathException;
import java.lang.reflect.*;

/**
 * Loads and saves person files. Uses the serialisation from Person.
 * Initial file to save and load = /classes/resources/patient/*.patient
 */

public class PhicFileDialog extends JFileChooser{

  /** Workaround for Java Bug#4711700 creating JFileChoosers */
  public static PhicFileDialog createFileDialog() throws IllegalAccessException{
    boolean error = false;
    do {
      File[] files;
      try{
        files = (File[]) ShellFolder.get("fileChooserShortcutPanelFolders");
      }catch(Exception e){ throw new IllegalAccessException("No file system access.");}
      error = false;
      for (int i = 0; i < files.length; i++)
        if (files[i] instanceof ShellFolder) {
          if ( ( (ShellFolder) files[i]).getIcon(true) == null) {
            error = true;
            System.out.println("avoided Bug#4711700");
            break;
          }
        }
    }
    while (error);
    return new PhicFileDialog();
  }

	private PhicFileDialog(){
		//super(Current.getResourcePath()+"/patient");
                super(Current.class.getResource("").getPath()+"/resources/patient");
		this.addChoosableFileFilter(new PatientFilter());
                this.addChoosableFileFilter(new ScriptFileFilter());
	}

	/**
	 * A file filter to display files ending with .patient.
	 * @todo Currently not working - causes crash??
	 */
	class PatientFilter extends javax.swing.filechooser.FileFilter{
		public boolean accept(File f){
			return f.getName().endsWith(".patient") || f.getName().indexOf('.')<0;
		}

		public String getDescription(){
			return "HOM patient file (*.patient)";
		}
	}

        class ScriptFileFilter extends javax.swing.filechooser.FileFilter{
          public boolean accept(File f){
            return f.getName().endsWith(".txt") || f.getName().endsWith(".js")
                || f.getName().endsWith(".script");
          }
          public String getDescription(){ return "HOM patient script"; }
        }
        public static void writeVariablesToScript(OutputStream s, Person p){
          PrintWriter w = new PrintWriter(s);
          writeVariablesToScript(w,p);
        }
        /**
         * Writes a script assigning each VisibleVariable of person p to its
         * current value.
         * Running this script will restore the physiological state relatively reliably.
         */
        public static void writeVariablesToScript(PrintWriter w, Person p){
          w.println("// Variable script: "+p.body.getClock().getTimeString(Clock.DATETIME));
          VisibleVariable[] v = Variables.variable;
          for(int i=0;i<v.length;i++){
            w.println( v[i].canonicalName + "\t=\t" +
                     String.valueOf(v[i].node.doubleGetVal()) + ";" );
          }
          w.println("// End of variables");
          w.flush();
          w.close();
        }


	/**
	 * Request to save the person specified as a file, opening a "Save As" box.
	 * If an error occurs, the user is notified.
         */
        public void save(Person p, Component parent) {
          if (showSaveDialog(parent) == APPROVE_OPTION) {
            try {
              OutputStream os = new FileOutputStream(getSelectedFile());

              if (getFileFilter() instanceof PatientFilter) {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(p);
                oos.close();
              }
              else if (getFileFilter() instanceof ScriptFileFilter) {
                writeVariablesToScript(os, p);
              }
            }
            catch (Exception e) {
              e.printStackTrace();
              JOptionPane.showMessageDialog(this, e, "Error saving file",
                                            JOptionPane.ERROR_MESSAGE);
            }
          }
        }

	/**
	 * Request to open a patient file, opening a "Open" box.
	 * Returns the person retrieved from the file.
	 * If an error occurs, the user is notified
	 */
	public Person open(Component parent){
		Person person=null;
		if(showOpenDialog(parent)==APPROVE_OPTION){
                  try{
                    InputStream is = new FileInputStream(getSelectedFile());
                    if (getFileFilter() instanceof PatientFilter) {
                      ObjectInputStream ois = new ObjectInputStream(is);
                      person = (Person) ois.readObject();
                      ois.close();
                    }
                    else {
                      Script.executeScriptFromStream(is, null);
                      is.close();
                    }
                  }
                  catch (InvalidClassException x) {
                    JOptionPane.showMessageDialog(this,"The file " + getSelectedFile() +
                        " is not compatible with this version of the program.",
                         "Incompatible file", JOptionPane.ERROR_MESSAGE);
                  }
                  catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, e, "Error opening file",
                                                  JOptionPane.ERROR_MESSAGE);
                  }

		}
		return person;
	}

	/**
	 * This is the new version of open which works with arbitrary input
	 * streams. It may be moved from this class into another one which
	 * won't cause access exceptions...
	 */
	public Person openPatientFile(InputStream is) throws IOException,
			ClassNotFoundException{
		ObjectInputStream ois=new ObjectInputStream(is);
		Person person=(Person)ois.readObject();
		ois.close();
		return person;
	}

        /**
         * This is the new version of open which works with arbitrary input
         * streams. It may be moved from this class into another one which
         * won't cause access exceptions...
         */
        public void openScriptFile(InputStream is) throws IOException,
            ClassNotFoundException, InvocationTargetException, IOException {
          Script.executeScriptFromStream(is, null);
          is.close();
        }

}

package sanjay;

import javax.swing.*;
import java.io.*;


/**
 * <p>Title: Psychology Toolkit</p>
 * <p>Description: Toolkit to create psychophysical experiments</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sanjay Manohar
 * @version 2.0
 */

public class SimpleFileDialog extends JFileChooser {

	protected SimpleFileDialog() {
	}

	public SimpleFileDialog(JFrame parent) {
		this.parent = parent;
	}

	JFrame parent = null;

	File currentFile = null;
	Object currentData = null;

	public void save(Object o){
		if(currentFile == null){
			saveAs(o);
		}else{
			write(o,currentFile);
		}
	}
	protected boolean write(Object o, File file){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(o);
			return true;
		}catch(IOException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,"Error saving file",
				e.toString(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public void saveAs(Object o){
		showSaveDialog(parent);
		File file = getSelectedFile();
		write(o, file);
	}


	public Object open(){
		showOpenDialog(parent);
		File file = getSelectedFile();
		if(currentFile!=null && !discardCurrent()) return null;
		try{
			ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(file));
			Object o = ois.readObject();
			setFile(file, o);
			return o;
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(parent,"Error opening file",
				ex.toString(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}


	protected void setFile(File file, Object data){
		currentFile = file;
		currentData = data;
		parent.setTitle("[" + currentFile.getName() + "]");
	}

	/** Returns true if the current file can be discarded, or has been saved. */
	public boolean discardCurrent(){
		int saveIt = JOptionPane.showConfirmDialog(parent,
			"Discard the current changes?", "Clear current data",
			JOptionPane.OK_CANCEL_OPTION);
		if(saveIt == JOptionPane.OK_OPTION){
			return write(currentData, currentFile);
		}
		return false;
	}

}
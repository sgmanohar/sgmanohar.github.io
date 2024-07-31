package phic.gui;

import javax.swing.JFileChooser;
import phic.*;
import phic.common.*;

/**
 * Dialog to load and save ini-files
 */

public class IniFileDialog extends JFileChooser {
  public IniFileDialog() {
    super(Resource.loader.getResourceURL("").getFile());
  //this.addChoosableFileFilter(new IniFilter());
  }
  public void save(){

  }

}
package phic;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.io.IOException;
import javax.swing.ImageIcon;
import phic.common.IniReader;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.JMenu;

/**
 * Loads resources for Phic. Use the command Resource.loader.getResource() to
 * obtain an InputStream.
 */
public class Resource{

  /**
   * The version number of this HOM build
   */
  public static final double
      HOM_VERSION = 2;



	/** An instance of Resource (the only one) */
	public static Resource loader=new Resource();

	/**
	 * Resource prefix is the string which locates the directory (package)
	 * containing resources. It is relative to the package containing this class,
	 * which is 'phic'. The default is 'resources/'
	 */
	protected static final String resourcePrefix="resources/";

        /** Return the software version */
        public String getHOMVersion(){
          return Double.toString(HOM_VERSION);
        }

	/**
	 * Return an input stream to retrieve the data from the given resource.
	 * The resources are found, by default, under the 'resources' folder,
	 * directly under the phic folder.
	 * @todo Note: Future classes should use this method to get resources.
	 * The resourcePath variable will be removed.
	 */
	public InputStream getResource(String resourceName){
		return getClass().getResourceAsStream(resourcePrefix+resourceName);
	}

	/**
	 * Return a URL to retrieve the data from the given resource.
	 * See getResource(String)
	 */
	public URL getResourceURL(String resourceName){
		return getClass().getResource(resourcePrefix+resourceName);
	}

	/**
	 * This gets an image resource from the resources folder in phic.
	 * It uses the default Toolkit to create the image.
	 *
	 */
	public Image getImageResource(String resourceName){
		if(resourceName==null){
			throw new NullPointerException("Bad resource name '"+resourceName+"'");
		}
		Image i=Toolkit.getDefaultToolkit().createImage(getClass().getResource(
				resourcePrefix+resourceName));
		if(i==null){
			throw new RuntimeException("Image '"+resourceName+"' could not be loaded");
		}
		return i;
	}

        /**
         * Simply calls getImageResource with the same parameter, and creates
         * an ImageIcon for that image.
         */
        public ImageIcon getIconResource(String resourceName){
          return new ImageIcon(getImageResource(resourceName));
        }

        public Properties getProperties(String resourceName){
          Properties p = new Properties();
          try{
            p.load(getResource(resourceName));
          }catch(IOException e){
            throw new RuntimeException("Could not load resource "+resourceName, e);
          }
          return p;
        }
        /** Return a list of strings from a property-value pair. Returns null no
         * such property exists. */
        public static String[] getStringList(Properties p, String item){
          String s = p.getProperty(item);
          if(s!=null) return s.split("\\s*,\\s*");
          else return null;
        }
        public String[] getStringList(String propertiesResourceName, String item){
          return getStringList(getProperties(propertiesResourceName), item);
        }

	/** Creates an instance of Resource */
	public Resource(){
	}
        public static String identifierToText(String o){
          StringBuffer s = new StringBuffer();
          for(int i=0;i<o.length();i++){
            char c = o.charAt(i);
            if(i==0 && !Character.isUpperCase(o.charAt(i+1))) c=Character.toUpperCase(c);
            if(i > 1 && Character.isUpperCase(c)){
              if (i < o.length() - 1 && Character.isLowerCase(o.charAt(i + 1))) {
                s.append(' ');
              }else if( Character.isLowerCase(o.charAt(i-1)) ){
                s.append(' ');
              }
            }
            s.append(c);
          }
          return s.toString();
        }

        /**
         * Read in the section headers from an ini file, and use the sections
         * to fill in a menu.
         * If menuName is a property key (on the left of a property-value pair),
         * then the value of the property is read as a list of the sub-items on
         * the menu; items in the list must be either a
         * section header (enclosed by square brackets) or a property key
         * (on the left of a property-value pair).
         *
         * Otherwise,
         * a menu is created that contains all the section headers in the ini
         * file.
         *
         * @param resourceName the resource file that is to be read, giving
         * information about the menu.
         * @param al the ActionListener which will receive events from the menu
         * items. The al will receive the data from the section within the ini
         * file represented by the menu item: the actionEvent's 'source' will
         * be set to a String[] representing the result of
         * IniReader.getSectionStrings().
         */
        public JMenu createMenuFromIni(String resourceName, String menuName, ActionListener al, Icon icon) throws IOException{
          Properties p = getProperties(resourceName);
          IniReader ini = new IniReader(getResource(resourceName));
          return recursiveCreateMenu(p,ini,menuName, al, icon);
        }

        /** Properties in the file with this name represent the Icon resource
         * file used for the menu item.
         */
        protected final String ICON_PROPERTY_NAME = "Icon";

        protected JMenu recursiveCreateMenu(Properties p, IniReader ini, String menuName, final ActionListener al, Icon icon){
          String friendlyname = Resource.identifierToText( menuName );
          JMenu menu = new JMenu(friendlyname);
          String[] itemName = getStringList(p, menuName);
          if(itemName==null) itemName = ini.getSectionHeaders();
          for (int i = 0; i < itemName.length; i++) {
            String[] sectionStrings = null;
            try{
              sectionStrings = ini.getSectionStrings(itemName[i]);
              if(sectionStrings.length==0) throw new IllegalArgumentException();
            }catch(IllegalArgumentException e){
              // no section header with given name
              if(p.containsKey(itemName[i])){ // represents a new submenu
                menu.add( recursiveCreateMenu(p,ini,itemName[i], al, icon) );
                continue;
              }else throw new IllegalArgumentException("Malformed menu file: element "+itemName[i]);
            }
            // section header representing menu item (leaf)
            final String[] eventParams = sectionStrings;
            Icon oldicon =icon;
            try{
              ImageIcon ic = getIconResource(ini.getSectionMap(itemName[i]).get(ICON_PROPERTY_NAME).toString());
              if(ic!=null) icon=ic;
            }catch(NullPointerException x){} catch(IllegalArgumentException x){}
            JMenuItem m = new JMenuItem(new AbstractAction(itemName[i],icon){
              public void actionPerformed(ActionEvent e){
                e.setSource(eventParams);
                al.actionPerformed(e);
              }
            });
            icon=oldicon;
            menu.add(m);
          }
          return menu;
        }

}

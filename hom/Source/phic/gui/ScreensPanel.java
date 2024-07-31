package phic.gui;
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import phic.common.IniReader;

/**
 * The screens panel drawing medical records from a file indicating which
 * variables to display.
 * @todo This will soon replace the (almost identical) implementation in
 * ScreensDialog.
 */
public class ScreensPanel extends JPanel{
	JPanel jPanel1=new JPanel();

	Border border1;

	BorderLayout borderLayout1=new BorderLayout();

	Border border2;

	String filename;

	Vector sections;
	/**
	 * If this constructor is used, then setSections must be called with the
	 * appropriate list of sections
	 * A full list of sections available can be found with the getSections()
	 * method.
	 */

	 protected ScreensPanel(String file){
		filename=file;
		ir=new IniReader(filename);
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public ScreensPanel(String file,Vector sections){
		this(file);
		this.sections=sections;
		myinit();
	}

	private void jbInit() throws Exception{
		border1=BorderFactory.createEmptyBorder(5,5,5,5);
		border2=BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		jPanel1.setLayout(borderLayout1);
		this.add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(tabbedpane,BorderLayout.CENTER);
	}

	JPanel[] panels;

	JTabbedPane tabbedpane=new JTabbedPane();

	private final boolean scrollable=true;

	/**
	 * Return the list of strings representing blood tests that are available
	 */
	public String[] getSections(){
		Vector v=new Vector();
		return ir.getSectionHeaders();
	}

	/**
	 * Set the sections to be displayed; the sections are a vector of strings
	 * each of which is the name of a section header in the .ini file
	 */
	public void setSections(Vector v){
		sections=v;
		myinit();
	}

	/**
	 * This is the file from which the possible blood tests (and what they
	 * correspond to in PHIC) are read.
	 */
	protected IniReader ir;

	/**
	 * Create the tabbed panels from the file.
	 */
	protected void myinit(){
		setSize(new Dimension(460,480));
		tabbedpane.removeAll();
		String[] head=ir.getSectionHeaders();
		JPanel[] panel=new JPanel[head.length];
		for(int i=0;i<head.length;i++){
			if(!doDisplay(head[i])){
				continue;
			}
			if(scrollable){
				JScrollPane sp=new JScrollPane();
				sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				sp.setBorder(null);
				panel[i]=new JPanel();
				panel[i].setLayout(new BoxLayout(panel[i],BoxLayout.Y_AXIS));
				sp.getViewport().setView(panel[i]);
				tabbedpane.add(head[i],sp);
			} else{
				panel[i]=new JPanel();
				tabbedpane.add(head[i],panel[i]);
			}
			String[] item=ir.getSectionStrings(head[i]);
			for(int j=0;j<item.length;j++){
				String name=item[j].trim();
				if(!name.equals("")){
					VisibleVariable v=Variables.forName(name);
					panel[i].add(new ValueRangeLabel(v));
				}
			}
		}
	}

	/**
	 * Check whether the given header name is in contained in the vector
	 * sections - this means whether the pane with the given title should
	 * be displayed or not.
	 */
	boolean doDisplay(String s){
		if(sections==null){
			return true;
		}
		for(int i=0;i<sections.size();i++){
			if(sections.get(i).equals(s)){
				return true;
			}
		}
		return false;
	}
}
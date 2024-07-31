package phic.doctor;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import phic.Resource;
import phic.common.IniReader;
import phic.drug.NoSuchDrugException;

/**
 * List of items that can be selected
 */
public class SelectionFrame extends JPanel{
	private BorderLayout borderLayout1=new BorderLayout();

	private JScrollPane jScrollPane1=new JScrollPane();

	private JPanel listpanel=new JPanel();

	private BorderLayout borderLayout2=new BorderLayout();

	private Box listbox;

	ButtonGroup bg=new ButtonGroup();

	String[] images,names;

	public SelectionFrame(){
		init();
	}

	public SelectionFrame(boolean horizontal){
		this.horizontal=horizontal;
		init();
	}

	public SelectionFrame(String[] images,String[] names){
		init();
		setupImages(images,names);
	}

	Item[] items;

	public void setupImages(String[] images,String[] names){
		this.images=images;
		this.names=names;
		items=new Item[names.length];
		bg=new ButtonGroup();
		listbox.removeAll();
		for(int i=0;i<images.length;i++){
			Image m=null;
			try{
				m=Resource.loader.getImageResource(images[i]);
			} catch(Exception e){
				throw new RuntimeException("Could not load image resource "+images[i]
						+": "+e.getMessage());
			}
			items[i]=new Item(m,names[i]);
			items[i].setFont(items[i].getFont().deriveFont(Font.BOLD));
			bg.add(items[i]);
			listbox.add(items[i]);
		}
	}

	void init(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean horizontal=false;

	private void jbInit() throws Exception{
		listbox=horizontal?Box.createHorizontalBox():Box.createVerticalBox();
		this.setLayout(borderLayout1);
		if(horizontal){
			jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.
					VERTICAL_SCROLLBAR_NEVER);
		} else{
			jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.
					HORIZONTAL_SCROLLBAR_NEVER);
		}
		listpanel.setLayout(borderLayout2);
		this.add(jScrollPane1,BorderLayout.CENTER);
		jScrollPane1.getViewport().add(listpanel,null);
		listpanel.add(listbox,BorderLayout.CENTER);
	}

	class Item extends JToggleButton{
		Item(Image m,String s){
			super(new ImageIcon(m));
			text=s;
			image=m;
			setBorder(null);
			setToolTipText(s);
		}

		String text;

		Image image;

		public void paint(Graphics g){
			//super.paint(g);
			g.drawImage(image,0,0,this);
			g.setColor(Color.white);
			g.drawString(text,2,getHeight()-9);
			g.setColor(getForeground());
			g.drawString(text,1,getHeight()-10);
			if(isSelected()){
				g.setColor(Color.red);
				g.draw3DRect(0,0,getWidth()-1,getHeight()-1,true);
				g.draw3DRect(1,1,getWidth()-3,getHeight()-3,false);
			}
		}
	}


	Map[] substanceMap;

	public void setupFromSection(IniReader ir,String section){
		names=ir.getSectionStrings(section);
		String[] imageNames=new String[names.length];
		substanceMap=new Map[names.length];
		for(int i=0;i<names.length;i++){
			substanceMap[i]=ir.getSectionMap(names[i]);
			imageNames[i]=(String)substanceMap[i].get("Image");
			if(imageNames[i]==null){
				throw new NullPointerException("Cannot find image name for "+names[i]);
			}
			//set up fluids?
		}
		setupImages(imageNames,names);
	}

	String[] getNames(){
		return names;
	}

	String getSelectedName(){
		return names[getSelectedIndex()];
	}

	int getSelectedIndex(){
		for(int i=0;i<items.length;i++){
			if(items[i].isSelected()){
				return i;
			}
		}
		throw new IllegalStateException("No selection made");
	}

	Map getSelectedMap(){
		return substanceMap[getSelectedIndex()];
	}

	phic.common.Container getSelectedSubstance() throws NoSuchDrugException{
		Map m=getSelectedMap();
		phic.common.Container c=new phic.common.Container();
		String s=(String)m.get("Fluid");
		if(s!=null){
			c.add(DrugParser.createSubstance(s));
		}
		s=(String)m.get("Drug");
		if(s!=null){
			c.add(DrugParser.createSubstance(s));
		}
		return c;
	}

	Image getSelectedImage(){
		return items[getSelectedIndex()].image;
	}
}
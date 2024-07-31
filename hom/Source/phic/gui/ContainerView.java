package phic.gui;
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import phic.common.*;

/**
		 * A panel containing a table, whose rows represent containers, and whose columns
 * are the contents of containers.
 */
public class ContainerView extends JPanel implements Runnable{
	BorderLayout borderLayout1=new BorderLayout();

	JScrollPane jScrollPane1=new JScrollPane();

	JTable table=new JTable();
	//model for table
	/** The table model to display the values in the container */

	MyTableModel tablemodel=new MyTableModel();
	/**
	 * This defines the columns in the table as constituents, and
	 * rows as different containers in the vector 'containers'
	 */

	 class MyTableModel extends DefaultTableModel{
		public int getRowCount(){
			if(containers!=null){
				return containers.size();
			} else{
				return 0;
			}
		}

		public int getColumnCount(){
			return 8;
		}

		public String getColumnName(int i){
			return cnames[i];
		}

		public Object getValueAt(int row,int column){
			if(containers!=null){
				phic.common.Container c=(phic.common.Container)containers.get(row);
				if(column==0){
					return new Double(c.volume.get());
				}
                                Quantity q = ((Quantity)c.qs.get(tx[column-1]));
                                if(showingQuantities) return q.Q;
				else return q;
			} else{
				return null;
			}
		}

		public Vector containers=new Vector();
	}

        protected boolean showingQuantities = false;


	/**
	 * The column header names for the container table. These are in a different
	 * order to that found in Container.qs. The array tx[] is used to find the
	 * Container 'qs' index of the column.
	 */
	String[] cnames=new String[]{"Volume /l","%Solids","[H]","[HCO3-]","[Na]",
			"[K]","[glucose]","[protein]","[urea]"};
	/** Maps the order of cnames onto order of Container.qs */

	int[] tx=new int[]{7,1,0,6,5,2,4,3};

	/**
	 * These represent the units of the columns. The units are not currently
	 * used since it doesn't look nice to have milli or micro prefixes etc. in
	 * the table format.
	 */
	int[] un=new int[]{UnitConstants.LITRES,UnitConstants.PERCENT,
			UnitConstants.MOLAR,UnitConstants.MOLAR,UnitConstants.MOLAR,
			UnitConstants.MOLAR,UnitConstants.MOLAR,UnitConstants.MOLAR,
			UnitConstants.MOLAR
	};

	/** Adds the container to the table model */
	public void addContainer(phic.common.Container c){
		synchronized(tablemodel){
			tablemodel.containers.add(c);
			tablemodel.fireTableStructureChanged();
			tablemodel.notifyAll();
		}
	}

	/** Create a container view of a given Container. It will not automatically update. */
	public ContainerView(phic.common.Container c){
		tablemodel.containers.add(c);
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/** Creates a container view of a given Container, and allows the caller
	 * to specify whether the view will be updated regularly
	 */
	public ContainerView(phic.common.Container c,boolean update){
		this(c);
		if(update){
			thread.start();
		}
	}

	//updater thread
	/** Updatger thread */
	Thread thread=new Thread(this,"Container view updater");
	/** Interval between updates = 1 second */

	long delay=1000;

	/** The thread calls tablemode.fireTableDataChanged() to update the data */
	public void run(){
		while(true){
			while(tablemodel.containers.size()>0){
				tablemodel.fireTableDataChanged();
				try{
					Thread.sleep(delay);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			try{
				tablemodel.wait();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void jbInit() throws Exception{
		this.setLayout(borderLayout1);
		table.setToolTipText("");
		table.setModel(tablemodel);
		this.setPreferredSize(new Dimension(454,42));
//    table.setPreferredSize(new Dimension(450,40));
//    table.setPreferredScrollableViewportSize(new Dimension(450,40));
		this.add(jScrollPane1,BorderLayout.CENTER);
		jScrollPane1.getViewport().add(table,null);
	}
  public boolean isShowingQuantities() {
    return showingQuantities;
  }
  public void setShowingQuantities(boolean showingQuantities) {
    this.showingQuantities = showingQuantities;
  }
}
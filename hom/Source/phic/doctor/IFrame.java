package phic.doctor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Base class for internal frames for the doctor's GUI.
 * Should implement docking?
 */
public class IFrame extends JInternalFrame{
	private JMenuItem mi;

	protected DoctorFrame frame=null;

	public void addNotify(){
		try{
			frame=findDoctorFrame();
			mi=new JMenuItem(new Act());
			frame.windowmenu.add(mi);
		} catch(Exception e){
			//throw new RuntimeException("Not in a container");
			e.printStackTrace();
		}
		super.addNotify();
	}

	public void removeNotify(){
		if(frame!=null){
			frame.windowmenu.remove(mi);
			frame=null;
		}
		super.removeNotify();
	}

	public IFrame(){
		this.setIconifiable(true);
		this.setMaximizable(true);
		this.setResizable(true);
		this.setClosable(true);
		this.setSize(100,100);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private DoctorFrame findDoctorFrame(){
		Component x=this;
		while(!(x instanceof DoctorFrame)&&(x!=null)){
			x=x.getParent();
		}
		if(x!=null){
			return(DoctorFrame)x;
		}
		return null;
	}

	class Act extends AbstractAction{
		public void actionPerformed(ActionEvent e){
			show();
		}

		public Act(){
			super(getTitle());
		}
	}
}
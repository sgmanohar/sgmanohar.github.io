/**
 * A panel showing a horizontal bar?
 *
 * @todo this class is unused - delete
 */
package phic.gui;
import javax.swing.*;

public class HorizontalBarView extends JPanel{
	HorizontalBar bar=new HorizontalBar();

	public HorizontalBarView(){
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setVariable(VisibleVariable v){
		variable=v;
		bar.setVariable(v);
		label.setText(v.longName);
	}

	VisibleVariable variable;

	JLabel label=new JLabel();

	public VisibleVariable getVariable(){
		return variable;
	}

	private void jbInit() throws Exception{
		this.add(label,null);
		this.add(bar,null);
	}
}
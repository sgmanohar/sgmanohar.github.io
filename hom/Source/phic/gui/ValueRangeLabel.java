/**
 * Displays a VisibleVariable as a label box, containing the name of the variable,
 * its current value, and the normal range.
 * If the value is above or below the normal range, then the error is
 * highlighted.
 */
package phic.gui;
import java.awt.*;
import javax.swing.*;
import phic.Resource;

public class ValueRangeLabel extends JPanel{
	JLabel nametxt=new JLabel();

	JLabel mintxt=new JLabel();

	JLabel maxtxt=new JLabel();

	JTextField valtxt=new JTextField();

	JLabel jLabel1=new JLabel();

	JLabel jLabel2=new JLabel();

	JLabel jLabel3=new JLabel();

	public ValueRangeLabel(VisibleVariable v){
		this.v=v;
		try{
			jbInit();
		} catch(Exception e){
			e.printStackTrace();
		}
		myinit();
	}

	VisibleVariable v;

	private void jbInit() throws Exception{
		nametxt.setFont(new java.awt.Font("Dialog",1,12));
		nametxt.setPreferredSize(new Dimension(140,17));
		nametxt.setHorizontalAlignment(SwingConstants.TRAILING);
		nametxt.setText("Name");
		mintxt.setPreferredSize(new Dimension(75,17));
		mintxt.setToolTipText("Minimum normal value");
		mintxt.setText("jLabel3");
		maxtxt.setPreferredSize(new Dimension(75,17));
		maxtxt.setToolTipText("Maximum normal value");
		maxtxt.setText("jLabel4");
		valtxt.setBackground(Color.black);
		valtxt.setFont(new java.awt.Font("SansSerif",1,13));
		valtxt.setForeground(Color.cyan);
		valtxt.setPreferredSize(new Dimension(98,22));
		valtxt.setEditable(false);
		valtxt.setText("jTextField1");
		jLabel1.setText("-");
		jLabel2.setText("(");
		jLabel3.setText(")");
		this.add(nametxt,null);
		this.add(valtxt,null);
		this.add(jLabel2,null);
		this.add(mintxt,null);
		this.add(jLabel1,null);
		this.add(maxtxt,null);
		this.add(jLabel3,null);
	}

	/**
	 * Set this to true if you would like any abnormal values to be highlighted
	 * in colour.
	 */
	public boolean useColourForErrors=true;

	/**
	 * This is the background colour for abnormal values. The default
	 * is pink.
	 */
	public Color errorColour=Color.pink;

	/**
	 * Set up the variable label with the values and text.
	 */
	void myinit(){
		double value=v.node.doubleGetVal();
		nametxt.setText(Resource.identifierToText( v.longName ));
		valtxt.setText(v.formatValue(value,true,true));
		mintxt.setText(v.formatValue(v.minimum,true,false));
		maxtxt.setText(v.formatValue(v.maximum,true,false));
		nametxt.setToolTipText(v.canonicalName);
		valtxt.setToolTipText("Initial value = "+v.formatValue(v.initial,true,false));
		if(useColourForErrors){
			if(value<v.minimum){
				mintxt.setOpaque(true);
				mintxt.setBackground(errorColour);
			}
			if(value>v.maximum){
				maxtxt.setOpaque(true);
				maxtxt.setBackground(errorColour);
			}
		}
	}
}

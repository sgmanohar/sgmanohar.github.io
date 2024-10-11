package com.neurolab.common;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ThickPanel extends JPanel implements NeurolabGuiComponent{
	public JPanel inner;
				private int thick;
	private static final String[] border={BorderLayout.NORTH,BorderLayout.SOUTH,
				BorderLayout.EAST,BorderLayout.WEST};
				private String text="";
				public void setText(String s){text=s;}
				public String getText(){return text;}
	public ThickPanel(int b){
		super();
		thick=b;
		setLayout(new BorderLayout());
					NeurolabExhibit.setBG(this);;
		add(inner=new JPanel());
		NeurolabExhibit.setBG(inner);
		for(int i=0;i<4;i++)
			add(new Spacer(thick,thick),border[i]);
		setBorder(NeurolabExhibit.raisedbevel);
		inner.setBorder(NeurolabExhibit.loweredbevel);
	}
				public ThickPanel(){this(5);}
				public void paint(Graphics g){
					super.paint(g);
					NeurolabExhibit.antiAlias(g);
					g.setFont(getFont());
					NeurolabExhibit.paintText3D(g,text,(getWidth()-NeurolabExhibit.getTextWidth(g,text))/2,getHeight()-6);
				}
}

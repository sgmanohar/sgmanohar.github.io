
/**
 * Title:        CUDOS<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.circuit;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;
import javax.swing.*;

public class CircuitComponent extends JPanel implements ImageObserver{
	public CircuitComponent(){
		this(null);
	}
  public CircuitComponent(Circuitboard tcb) {
	cb=tcb;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
	Circuitboard cb;
	public Circuitboard getCircuitboard(){return cb;}

	protected Image i=null;
	public int ch,c1,c2;
	public Point p1,p2;
	public boolean selected=false;
	public void paintCircuit(Graphics g_){
		Graphics2D g=(Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int 	mid=(p1.x+p2.x)/2,
			dw=(c1<c2?1:-1)*i.getWidth(this)/2,	// use -dw if component reversed
			dh=i.getHeight(this)/2;
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.black);
//if(!paths.isEmpty())g.setColor(Color.blue);
		g.drawLine(p1.x,p1.y,mid-dw,p1.y);
		g.drawLine(mid+dw,p1.y,p2.x,p1.y);
		g.fillOval(p1.x-4,p1.y-4,8,8);
		g.fillOval(p2.x-4,p2.y-4,8,8);
		if(isReversible()){
			g.drawImage(i,mid-dw,p1.y-dh,mid+dw,p1.y+dh,0,0,i.getWidth(this),i.getHeight(this),this);
		}else{
			g.drawImage(i,mid-Math.abs(dw),p1.y-dh,this);
		}
		if(selected){
			g.setColor(Color.red);
			if(c1<c2)g.drawRect(p1.x,p1.y-dh,p2.x-p1.x,dh*2);
			else g.drawRect(p2.x,p2.y-dh, p1.x-p2.x,dh*2);
		}
	}
	public boolean imageUpdate(Image image,int info,int x,int y,int w,int h){
		if((info&ImageObserver.ALLBITS)>0)return false;
		else return true;
	}
	public void onMove(){
		Point np1,np2;
		np1=new Point(c1*cb.sx,ch*cb.sy);
		np2=new Point(c2*cb.sx,ch*cb.sy);
		if(    (p1==null || p2==null)
		   || !(np1.x==p1.x && np1.y==p1.y && np2.x==p2.x && np2.y==p2.y)){
			p1=np1;p2=np2;
			cb.repaint();
		}
	}
	double current;

		//override these to set component function
		//when creating new components
	public double getResistanceFromEMF(double emf){
			//effective resistance of component, given total emf round one path
		return getResistance();
	}
	public double getResistance(){
			//effective resistance of component
		return 0.001;
	}
	public double getEMF(){
			//component generates its own EMF?
		return 0;
	}
	public void process(){
			//tick-wise component-specific processing
	};
	public boolean passesCurrent(){
			//can current pass through the component
		return true;
	}
	public boolean isReversible(){
			//can the icon be printed backwards if the component is reversed?
		return false;
	}

  private void jbInit() throws Exception {
	cname.setPreferredSize(new Dimension(100, 21));
    cname.setText("Dummy component");
    this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(2,2,2,2)));
    this.setPreferredSize(new Dimension(160, 40));
    this.setLayout(flowLayout1);
    jLabel1.setLabelFor(cname);
    jLabel1.setText("Name");
    flowLayout1.setAlignment(FlowLayout.LEFT);
    this.add(jLabel1, null);
    this.add(cname, null);
  }
  JTextField cname = new JTextField();
  JLabel jLabel1 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();

  public String getName(){ return cname.getText(); }
  public void setName(String s){ cname.setText(s); }

	public static final int B_RESISTOR=1,B_FIXEDVOLTAGE=2,B_EMF=3;
	public int getBehaviour(){return B_RESISTOR;}

	String getUnit(){return "";}
	static final String[] prefix={"p",   "n",  "µ",  "m",  "", "k", "M"};
	static final double[] vunit ={1E-12, 1E-9, 1E-6, 1E-3, 1,  1E3, 1E6};
	public String unitString(double v){
		if(Double.isNaN(v))return "??? "+getUnit();
		boolean neg=v<0; v=Math.abs(v);
		double sval=Double.NaN;
		String pf="tiny";
		int round=10;
		for(int i=vunit.length-1;i>=0;i--){
			if(v>=vunit[i]){
				sval=v/vunit[i];
				pf=prefix[i];
				break;
			}
		}
		if(pf.equals("tiny")){
			if(v!=0){
				sval=v/vunit[0];
				pf=prefix[0];
				round=1000;
			}else pf="";
		}
		return (neg?"-":"")+Math.round(sval*round)*1.0/round+" "+pf+getUnit();
	}

	// for calculation
	Vector paths=new Vector();	//paths that cross this component
	Vector directions=new Vector();	//direction of each path crossing
}

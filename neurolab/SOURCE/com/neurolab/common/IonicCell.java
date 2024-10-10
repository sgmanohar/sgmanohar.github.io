
/**
 * Title:        Neurolab<p>
 * Description:  Converted to Java from an original by Roger Carpenter
 * <p>
 * Copyright:    Copyright (c) Sanjay Manohar, Robin Marlow<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar, Robin Marlow
 * @version 1.0
 */
package com.neurolab.common;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class IonicCell {

double qk=13916, qn=227, qa=8683, qx=5500  ;  //           ' Quantities
public int ki=146, ni=3, ai=88,  xi=57  ;// ' Concentrations inside
public int ko=5, no=145, ao=150, c=300    ;//' Concentrations outside
double fk=82.84, fn=113.62 , fa=-30.78   ;      //         ' G-flux
double pk=86.22, pn=-130.64         ;       //      ' Pump
double gk=6, gn=5, ga=30, pmax=2000;       //     ' Transport constants
public double vol=89.23;
double mv=-11, e=0       ;        //   ' Various
double zpot=1.59              ;         // ' GCFE factor
boolean initflag, pumping=true;
int oldno, oldni, oldko, oldki, oldvol;

double X=5391,Y=3485,z=130.6;
Timer Timer1=new Timer(150,new ActionListener(){
	public void actionPerformed(ActionEvent e){
		Timer1_Timer();
	}
} );

ActionListener listener;
public double[] Text1=new double[13];

public boolean RBC=true;	//to be set by owner

public void Restart(){	//to be called by owner
	setup();
}
public IonicCell(){		//constructor
	initflag = false;
	setup();
	initialise();
	Timer1.start();
}
/*	//what is this?
public void Main(){
	setup();
}
*/
public void PumpOn(boolean Value){
//	if( !Value  ) PumpOn.ForeColor = BLACK;
//	else PumpOn.ForeColor = RED;
	pumping = Value;
}
public void RBC_Click(){
	RBC=true;
	setup();
}
public void Squid_Click(){
	RBC=false;
	setup();
}
public void spinChange(int i){
	qk = ki * vol;
	qn = ni * vol;
	qa = ai * vol;

	updateout();
	updateperm();
}


public void fire(String s){
	if(listener!=null)
		listener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));
}
public void addActionListener(ActionListener l){
	listener=l;
}

public void Burst(){
	fire("Burst");
				Timer1.stop();
 }

public void CellDraw(){

	if ((oldno != no) || (oldko !=ko))
		fire("RedrawCell");

	if ((oldni != no)||( oldki !=ki)||( oldvol !=vol ))
	fire("RedrawCell");
}

		//low passing added
	double lqk=qk,lqn=qn,lqa=qa,lqx=qx;
	double lrate=0.4;
 public void Concs(){
	lqk=(1-lrate)*lqk+lrate*qk;
	lqn=(1-lrate)*lqn+lrate*qn;
	lqa=(1-lrate)*lqa+lrate*qa;
	lqx=(1-lrate)*lqx+lrate*qx;

	ki = (int)(lqk / vol);
	ni = (int)(lqn / vol);
	ai = (int)(lqa / vol);
	xi = (int)(lqx / vol);
}

public void Fluxes(){
	fk = 0.1 * gk * (ko * zpot - ki);
	fn = 0.1 * gn * (no * zpot - ni);
	fa = 0.1 * ga * (ai * zpot - ao);
}


public void GCFE(){
	Y = gn * no + gk * ko + ga * ai;
	X = gn * ni + gk * ki + ga * ao;
	if(Y > 0)zpot = X / Y;
	else zpot = 1000 * X;

	if( zpot != 0 )mv = -25 * Math.log(zpot);	//log10 or loge?
}

public void initialise(){
	if (RBC) {
		xi = 55;
		ki = 140;
		ni = 10;
		ai = 95;
		mv = -60;
	}else{
		xi = 145;
		ki = 140;
		ni = 10;
		ai = 5;
		mv = -70;
	}
	vol = 100;
	pmax = 2000;
	qk = ki * vol;
	qn = ni * vol;
	qa = ai * vol;
	qx = xi * vol;
	updateperm();
	updateout();
	Shower();
				Timer1.start();
}


public void iterate(){
	Pump();
	GCFE();
	Fluxes();
	Quants();
	Volume();
	Concs();
}


public void Pump(){
	if(pumping){
			Y = 42. * ni / (42 * ni + 8 + ki);
			z = 180. * ko / (180 * ko + 18 + no);
//		System.out.println( "pumping "+String.valueOf(42*ni)+"/"+String.valueOf(42*ni+8+ki) );
	}else Y = 0;
	z = Y * Y * Y * z * z * pmax;
	pn = -z;
	pk = 0.66 * z;
}

public void Quants(){
	qk = fk + pk + qk;
	qn = fn + pn + qn;
	qa = qk + qn - qx;

	if( qk <= vol ) qk = vol;
	if (qn <= vol )qn = vol;
	if (qa <= vol )qa = vol;
}

public void setup(){
//	CellPic.Cls
	if( RBC){
		Text1[2] = 6;//        'permeabilities
		Text1[5] = 5;
		Text1[8] = 30;
		Text1[1] = 5; //     ' external concs
		Text1[4] = 145;
		Text1[7] = 150;
	}else{
		Text1[2] = 30;
		Text1[5] = 2;
		Text1[8] = 40;
		Text1[1] = 5;
		Text1[4] = 145;
		Text1[7] = 150;
	}
//	pumping = PumpOn.Value;
	initialise();
	fire("AllTextChange");
//	bursttxt.Visible = False;
}

public void Shower(){
/*
	Picture1(0).FillStyle = 0;
	Picture1(0).Line (0, 0)-(350, 100), GREY, BF;
	Picture1(0).Line (0, 0)-(ki, 100), BLUE, BF
	Picture1(0).Line (ki, 0)-(ki + ni, 100), RED, BF
	Picture1(0).Line (ki + ni, 0)-(ki + ni + ai, 100), BLACK, BF
	Picture1(0).Line (ki + ni + ai, 0)-(ki + ni + ai + xi, 100), DGREEN, BF
*/
	fire("BarChange");
	Text1[11] = ki;
	Text1[10] = ni;
	Text1[9] = ai;
	Text1[12] = mv;
	fire("TextChange");

	if( vol > 130 )Burst();
	else CellDraw();
}


public void Timer1_Timer(){
	if( !initflag ) {
		setup();
		initflag = true;
	}else{
		iterate();
		Shower();
	}
}

public void updateout(){
/*	Picture1(1).FillStyle = 0
	Picture1(1).Line (0, 0)-(350, 100), GREY, BF
*/
	ko = (int)(Text1[1]);
	no = (int)(Text1[4]);
	ao = (int)(Text1[7]);
/*	Picture1(1).Line (0, 0)-(ko, 100), BLUE, BF;
	Picture1(1).Line (ko, 0)-(ko + no, 100), RED, BF;
	Picture1(1).Line (ko + no, 0)-(ko + no + ao, 100), BLACK, BF;
*/	//percentage bar here;
//	fire("TextChange");//no, just reading!

	c = ko + no + ao;
}

public void updateperm(){
	gk = Text1[2];
	gn = Text1[5];
	ga = Text1[8];
//	fire("TextChange");//no, just reading!
}

	//low pass added
public void Volume(){
	vol = 0.5 * (vol + (lqk + lqn + lqa + lqx) / c);
}


public double Val(String s){return Double.valueOf(s).doubleValue();}

}

/**
 * Title:        Cudos<p>
 * Description:  Cambridge University Distributed Opportunity Systems
 * Roger Carpenter<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      Cambridge University<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common.kinetic;

public class CircularWall extends Wall implements SpecialWall {
	public MassiveMolecule M;
	public double r;	//Radius
//	double vx,vy;
	public boolean collisionTest(MassiveMolecule m,double nx,double ny){
		double dx=(m.x+m.s/2)-(M.x+M.s/2),dy=(m.y+m.s/2)-(M.y+M.s/2);
		double ndx=(nx+m.s/2)-(M.x+M.s/2),ndy=(ny+m.s/2)-(M.y+M.s/2);
		if( m!=M &&((dx*dx+dy*dy<M.s*M.s/4+m.s*m.s/4) || (ndx*ndx+ndy*ndy<M.s*M.s/4+m.s*m.s/4)) ){ //molecule has crossed
//System.out.print(m.vx+","+m.vy+"<>"+M.vx+","+M.vy);
			double al,th1,th2,ph1,ph2, mass,Mass=M.Mass;
			double v1,v2,V1,V2;
			al=Math.atan2(dy,dx);	// angle of line of impact
			th1=Math.atan2(m.vy,m.vx)-al;		// angle of molecule v1
			ph1=Math.atan2(M.vy,M.vx)-al;		// angle of my V1
			v1=Math.sqrt(m.vx*m.vx+m.vy*m.vy);	// magnitude moecule v1;
			V1=Math.sqrt(M.vx*M.vx+M.vy*M.vy);
			mass=m.Mass;				// relative of course

			th2=Math.atan2( (v1*Math.sin(th1)*(mass+Mass)),
					((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1)) );
			ph2=Math.atan2( (V1*Math.sin(ph1)*(Mass+mass)),
					(2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1)) );
			V2=Math.sqrt(Math.pow((2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1))/(mass+Mass),2)
					+V1*V1*Math.sin(ph1)*Math.sin(ph1) );
			v2=Math.sqrt(Math.pow(((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1))/(mass+Mass),2)
					+v1*v1*Math.sin(th1)*Math.sin(th1) );
//System.out.print("::"+th2/Math.PI+","+v1+"::");

			m.vx=(v2*Math.cos(th2+al));
			m.vy=(v2*Math.sin(th2+al));
//	if(m.vx==0 && m.vy==0)m.vx-=5;
			M.vx=(V2*Math.cos(ph2+al));
			M.vy=(V2*Math.sin(ph2+al));
//System.out.println(M.vx+","+M.vy+";"+v2+","+V2);
			return true;
		}else return false;
	}
	public boolean collisionTest(Molecule m,int nx,int ny){
//System.out.println("AAAARGH!");
		double dx=(m.x-m.s/2)-(M.x+M.s/2),dy=(m.y-m.s/2)-(M.y+M.s/2);
		double ndx=(nx-m.s/2)-(M.x+M.s/2),ndy=(ny-m.s/2)-(M.y+M.s/2);
		if( m!=M &&((dx*dx+dy*dy<r*r+m.s*m.s/4) || (ndx*ndx+ndy*ndy<r*r+m.s*m.s/4)) ){ //molecule has crossed
			double al,th1,th2,ph1,ph2, mass,Mass=M.Mass;
			double v1,v2,V1,V2;
			al=Math.atan2(dy,dx);	// angle of line of impact
			th1=Math.atan2(m.vy,m.vx)-al;		// angle of molecule v1
			ph1=Math.atan2(M.vy,M.vx)-al;		// angle of my V1
			v1=Math.sqrt(m.vx*m.vx+m.vy*m.vy);	// magnitude moecule v1;
			V1=Math.sqrt(M.vx*M.vx+M.vy*M.vy);
			mass=1;				// relative of course

			th2=Math.atan2( (v1*Math.sin(th1)*(mass+Mass)),
					((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1)) );
			ph2=Math.atan2( (V1*Math.sin(ph1)*(Mass+mass)),
					(2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1)) );
			V2=Math.sqrt(Math.pow((2*mass*v1*Math.cos(th1)+(Mass-mass)*V1*Math.cos(ph1))/(mass+Mass),2)
					+V1*V1*Math.sin(ph1)*Math.sin(ph1) );
			v2=Math.sqrt(Math.pow(((mass-Mass)*v1*Math.cos(th1)+2*Mass*V1*Math.cos(ph1))/(mass+Mass),2)
					+v1*v1*Math.sin(th1)*Math.sin(th1) );
//System.out.print(M.vx+","+M.vy+";"+v1+"-"+V1+":::");

			m.vx=(int)(v2*Math.cos(th2+al));
			m.vy=(int)(v2*Math.sin(th2+al));
//	if(m.vx==0 && m.vy==0)m.vx-=5;
			M.vx=(V2*Math.cos(ph2+al));
			M.vy=(V2*Math.sin(ph2+al));
//System.out.println(M.vx+","+M.vy+";"+v2+","+V2);
			return true;
		}else return false;
	}
  public CircularWall(MassiveMolecule mol,double radius) {
	r=radius;
	M=mol;
//	vx=M.vx;vy=M.vy;
  }


}
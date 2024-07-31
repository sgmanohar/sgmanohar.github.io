package phic.ecg;
/**
 * Basic implementation of heart
 */
public class SimpleHeart implements Heart{
	public SimpleHeart(){
	}

	public double P_DELAY=0.4,P_SIZE=0.1,P_DURATION=0.05,PR_INTERVAL=0.2,
			QRS_SIZE=1.5,QRS_WIDTH=0.05,QT_INTERVAL=0.2,ST_SEGMENT=0,
                        T_SIZE=0.3, T_WIDTH=0.2;
                      /**
                       * Parameters of ECG
                       * @todo check these are correct
                       * Note that:
                       * P_DELAY + PR_INTERVAL + QT_INTERVAL + T_WIDTH = 1.0
                       */

	public Axis T_AXIS=new Axis(4*Math.PI/9,Math.PI/5),
                   ST_AXIS=new Axis(Math.PI/3, 0),
                  QRS_AXIS=new Axis(3*Math.PI/10,Math.PI/5),
                    P_AXIS=new Axis(Math.PI/3, Math.PI/4);

	/**
         * Timecourse of ECG given by size of electric field, and duration of
         * each segment.
         * format { {x1,y1,z1,t1} , {x2,y2,z2,t2} , ... }, where t is a duration
         * from the previous segment.
	 * Positive xyz indicate left, inferior, and anterior respectively.
	 * Time units are fractions of one heartbeat.
	 */
	double[][] field=createField();

	public double[][] createField(){
		//slightly rotated downstroke
		Axis q_axis=new Axis(QRS_AXIS,0.0,0.0);
		return new double[][]{
				// P wave
				{0,0,0,P_DELAY},
                                P_AXIS.vector(P_SIZE, P_DURATION/2) /*{P_SIZE,P_SIZE,P_SIZE,P_DURATION/2}*/,
                                {0,0,0,P_DURATION/2},
				{0,0,0,PR_INTERVAL-P_DURATION},

				// QRS complex
				q_axis.vector(QRS_SIZE,QRS_WIDTH/3),QRS_AXIS.vector(-0.3*QRS_SIZE,
				QRS_WIDTH/3),

				// S-T segment
				ST_AXIS.vector(ST_SEGMENT, QRS_WIDTH/3),
                                ST_AXIS.vector(ST_SEGMENT, QT_INTERVAL-QRS_WIDTH),

				// T wave
				{T_SIZE*T_AXIS.x(),T_SIZE*T_AXIS.y(),T_SIZE*T_AXIS.z(),T_WIDTH/2},
                                {0,0,0,T_WIDTH/2}
		};
	}

	public double rate=72;

	public double getRate(){
		return rate;
	}

	/**
	 * Return the electric field of a sequence of heartbeats.
	 */
	public double[][] getBeatsField(double time){
		field=createField();
		double sBeat=60/rate;
		int n=2+(int)(time/sBeat);
		double[][] b=new double[field.length*n][4];
		double cumulativeTime=0;
		for(int i=0;i<b.length;i++){
			for(int j=0;j<3;j++){
				b[i][j]=field[i%field.length][j];
			}
			b[i][3]=(cumulativeTime+=sBeat*field[i%field.length][3]);
		}
		return b;
	}
}

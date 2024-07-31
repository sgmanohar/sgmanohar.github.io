package phic.ecg;
import phic.Body;
import java.util.Arrays;

/**
		 * A version of SimpleHeart that is capable of reflecting some of the properties
 * of the Body in an ECG.
 *
 * When the heart object is created, the cardiac parameters are set up
 * according to rules defined in this file. When connected to the ecg.Trace
 * via appropriate Leads, an ECG is produced that reflects the heart's
 * condition.
 */
public class PhicHeart extends SimpleHeart{
	Body body;
	/**
	 * Create the heart using a specific body, and call initialiseHeart()
	 * to read in the values from Phic.
	 */

	 public PhicHeart(Body body){
		this.body=body;
                ectopicField = createEctopic();
                chbField =createEctopic();
                chbField[0][3] = 0.7;
		initialiseHeart();
                afField = createAF();
	}
        public double[][] ectopicField, chbField, afField;

	/**
	 * Initialise the heart each time ECG is recalculated.
	 */
	public double[][] createField(){
		initialiseHeart();
		return super.createField();
	}

        /**
         * Creates an ectopic beat
         * P_DELAY + PR_INTERVAL + QT_INTERVAL + T_WIDTH = 1.0
         */
        public double[][] createEctopic(){
          P_DELAY=0.2;
          P_SIZE=0;
          PR_INTERVAL=0.1;
          QRS_SIZE = 2.4;
          QRS_AXIS = new Axis(-Math.PI*0.62, -Math.PI*0.3);
          QRS_WIDTH=0.4;
          QT_INTERVAL=0.5;
          ST_SEGMENT=1.4;
          T_SIZE=0.05;
          T_WIDTH=0.2;
          return super.createField();
        }

        /** Variation in baseline voltage (mV */
        public double afWander = 0.040;
        /** Variation in time of beat, in seconds */
        public double afVar = 0.300;
        public double[][] createAF() {
          P_SIZE = 0;
          double[][] f = super.createField();
          for (int i = 0; i < f[0].length; i++) {
            f[i][0] += (Math.random() * 2 - 1) * afWander;
            f[i][1] += (Math.random() * 2 - 1) * afWander;
            f[i][1] += (Math.random() * 2 - 1) * afWander;
          } // irregular baseline?
          return f;
        }

        double pVE = 0;
        public static final int SINUS=0, CHB=2, AF = 4;
        public int rhythm = SINUS;

	/**
	 * Reads in relevant value from Phic:
	 *
	 *   Heart rate from phic.Heart
	 *   LVF -> tall QRS
	 *   K+: 1 mM -> 0.1 mV increase in T wave height, 0.04 increase QRS width
         * ischaemia -> t-wave inversion
	 */
	public void initialiseHeart(){
          if (body == null) {
            return;
          }
          if (body.brain.getFeeling() == phic.Brain.DEAD) { //flat line
            QRS_SIZE = 0;
            T_SIZE = 0;
            P_SIZE = 0;
            ST_SEGMENT = 0;
            return;
          }
          this.pVE = body.CVS.heart.pVE;
            rate = body.CVS.heart.rate.get();
            rhythm = (body.blood.PK.get()<0.002) ? AF : SINUS;

            P_DELAY=0.2;
            P_SIZE=0.1;
            P_DURATION=0.05;
            PR_INTERVAL=0.2;
            double lvh = body.CVS.heart.LVH.getError(),
                rvstrain = Math.max(0,
                                    Math.min(1, body.CVS.heart.right.sysV.getError() * 20));
            QRS_SIZE = 1.5
                +
                Math.min(Math.max( (body.CVS.heart.right.atrialP.get() - 0.008) * 2000,
                                  0), 1)
                + lvh;
            QRS_AXIS = new Axis(3 * Math.PI / 10
                                - lvh * Math.PI / 2
                                + rvstrain * Math.PI / 2,
                                Math.PI / 5);
            QT_INTERVAL=0.2;
            double plasmaK = body.blood.PK.getError();
            T_SIZE = 0.3 + Math.min(plasmaK * 500, 4);
            P_SIZE = 0.14 - plasmaK * 10;
            QRS_WIDTH = 0.05 + Math.max(0, plasmaK * 42); //PK*32
            double ischaemia = body.CVS.heart.ischaemia();

            //t-wave inversion
            T_AXIS.phi = 4 * Math.PI / 9 - Math.min(ischaemia / 0.008, 1) * Math.PI;
            T_AXIS.theta = Math.PI / 5 - Math.min(ischaemia / 0.008, 1) * Math.PI;
            T_SIZE=0.3;
            ST_SEGMENT = Math.min(ischaemia, 0.010) * -80;
            //ST_AXIS = new Axis(ischaemia * 200*Math.PI, ischaemia*200*Math.PI);

        }

        /**
         * Return the electric field of a sequence of heartbeats.
         */
        public double[][] getBeatsField(double time){
          field = createField();
          double sBeat = 60 / rate, sEctopic = 0.35;
          int n = 2 + (int) (time / Math.min(sBeat,sEctopic));
          double[][] b = new double[field.length * n][4];
          double cumulativeTime = 0;
          int i=0;
          for (int w = 0; w < n; w++) {
            double beatData[][], beatDuration;
            if(Math.random()<pVE){ // Ectopic beat
              beatData = ectopicField;
              beatDuration = sEctopic;
            }else if(rhythm == CHB){
              /** @todo add p-waves */
              beatData = chbField;
              beatDuration = 1.54;
            }else if(rhythm == AF){
              beatData = afField;
              //randomise duration of each beat, within plus or minus 100ms of rate.
              beatDuration=Math.max(0.2, sBeat+(Math.random()*2-1)*afVar);
            }else{
              beatData = field;
              beatDuration=sBeat;
            }
            for(int k=0;k<beatData.length;k++){
              for (int j = 0; j < 3; j++) {
                b[i][j] = beatData[k][j];
              }
              b[i][3] = (cumulativeTime += beatDuration * beatData[k][3]);
              i++;
            }
          }
          return b;
        }

}

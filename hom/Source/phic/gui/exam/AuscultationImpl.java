package phic.gui.exam;

import phic.Body;
import javax.sound.sampled.*;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import phic.common.Ticker;
/**
 * Class to generate sound corresponding to ausculation of the thorax.
 * Uses data from the supplied Body object.
 */

public class AuscultationImpl extends Auscultation  {
  AudioFormat af=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplesPerSecond, 8*bytesPerSample, 1, 1, samplesPerSecond,  false);
  SourceDataLine sdl;
  public AuscultationImpl() {
    try {
      sdl = (SourceDataLine)AudioSystem.getLine(
          new SourceDataLine.Info(SourceDataLine.class, af));
    }
    catch (LineUnavailableException ex) {
      ex.printStackTrace();
      if(body!=null) body.error("Sound unavailable :"+ex.toString());
    }
  }

  public void startSound() {
    try {
      sdl.open();
      sdl.start();
      running=true;
      timer.start();
    }
    catch (LineUnavailableException ex) {
      ex.printStackTrace();
      if(body!=null) body.error("Sound unavailable :"+ex.toString());
    }
  }
  public void stopSound() {
    timer.stop();
    running=false;
    sdl.stop();
    sdl.close();
  }
  protected boolean running = false;
  public void finalize() throws Throwable{
    super.finalize();
    stopSound();
  }

  public static final int samplesPerSecond = 44100;
  public static final int bytesPerSample = 1;

  /**
   * Buffer in which sound is created before being copied into the sound data
   * line's buffer using SourceDataLine.write()
   * Create a reasonably large buffer: 1 second of audio
   */
  byte[] buf = new byte[samplesPerSecond*bytesPerSample];
  /**
   * This is called to update the sound buffer.
   * Sounds are created dynamically according to rules from the body data.
   */
  ActionListener timerAction = new ActionListener(){public void actionPerformed(ActionEvent e){
      tick(0);
  }};
  /** The maximum value that a sample can take. Depends on the number of bits per sample. */
  int maxSample = 1<<(8*bytesPerSample);

  public void tick(double t){
    if(running && !body.clock.running){stopSound();return;}
    if(body.clock.running && !running) startSound();
    if(!running || body==null) return;
    int n = sdl.available();
    if (n <= 0) return;
    double initHPhase = body.CVS.heart.phase; //heart's phase right now
    double initRPhase = body.lungs.phase;     //lung's  phase right now
    double HR = body.CVS.heart.rate.get();
    double RR = body.lungs.RespR.get();
    double pulmOed = Math.min(1,Math.max(0, 2*(body.lungs.Oedema.get()-0.200)));
    double aorticRegurg = body.CVS.heart.aorticRegurg.get() * nearness(0,0,0.2),
        aorticStenosis = body.CVS.heart.aorticStenosis.get() * nearness( 0,-0.3,0.3);
    double CO = body.CVS.heart.CO.get();

    int waitingSamples = (sdl.getBufferSize() - n)/bytesPerSample; //samples already in queue
    n = Math.min(n, buf.length)/bytesPerSample; //number of samples to write
    double twoPiBeat = 2 * Math.PI / (HR/60);
    double twoPiBreath = 2 * Math.PI / (RR/60);
    double lastbreathsample=0;
    double lop=0;//low pass breath sound filter
    /** Relative volume of S1 and S2 */
    double hs1 = 1 * nearness(0.3,0.2, 0.5),
           hs2 = 0.8 * nearness(0,-0.2, 0.5);
    /** Relative volume of lung sound */
    double bs = 0.02 * nearness(0,0, 1);
    for (int i = 0; i < n; i++) {
      /** Calculate phase through cycle for next sample - always positive */
      double hph = (initHPhase +
                    HR/60 * (i + waitingSamples) / (double) samplesPerSecond) %1 ;
      double rph = (initRPhase +
                    RR/60 * (i + waitingSamples) / (double) samplesPerSecond) %1 ;
      double dt = 1/(double)samplesPerSecond;
      /** Where in the cycle S2 is heard */
      double hs2ph = hph-0.5;
      /** Where in the cycle S1 is heard */
      double hs1ph = hph-0.1;
      /** velocity of aortic jet, from 0-1 */
      double aovel = Math.max(0, CO/5.0 *  Math.exp(-200*(hph-0.3)*(hph-0.3)));
      double airvel = breathAmpl(rph);
      double nextSample =
          // 0.020 -5000 100
            hs1*(hs1ph>-0.090?Math.exp(-9000*hs1ph*hs1ph)* Math.sin(150 * twoPiBeat * hs1ph):0)
          + hs2*(hs2ph>0    ?Math.exp( -90 * hs2ph)     * Math.sin(120 * twoPiBeat * hs2ph):0)
          + aorticStenosis *0.3* aovel *
              noiseCentred(0,400+aovel*100,0.6,dt)
          + hs2*aorticRegurg*0.1*(hs2ph>0?Math.exp( -50 * (1-aorticRegurg) * hs2ph) * Math.random():0)
          + bs * airvel *
              noiseCentred(1,1000+400*airvel,0.6,dt)
          + bs*15 * pulmOed * ((Math.random()<pulmOed*0.02)? 1:0) * Math.exp(-150*(rph-0.25)*(rph-0.25))
          ;
      nextSample = Math.max(-1,Math.min(1,nextSample));
      int intSample = (int)(0.4*maxSample*nextSample);
      buf[i*bytesPerSample] = (byte)intSample;
      if(bytesPerSample>1) buf[i*bytesPerSample+1]=(byte)(intSample>>8);
    }
    sdl.write(buf, 0, n*bytesPerSample);
  }
  /** Calculate amount of lung sound at given phase in resp cycle */
  double breathAmpl(double phase){
    //return Math.max(0,Math.min(1,0.1*Math.log(Math.abs(30*body.lungs.flow.get()))));

    return (phase<0.3)? Math.exp(-80 * (phase-0.15)*(phase-0.15)) * 0.8
                      : Math.exp( -10 * (phase-0.65)*(phase-0.65))
                      * Math.exp( -2.0* (phase-0.3) )
                      * Math.exp( -1.0* (phase-0.5) )
    ;

  }
  /** channels for random numbers for the filtered noise function */
  double[] freq=new double[100], time=new double[100];

  /**
   * Gives noise centred around a given frequency. Attaches single cycles
   * of many cosine waves in series.
   */
  public double noiseCentredF(int channel, double centreFreq, double bandWidth, double dt){
    time[channel]+=dt;
    if(freq[channel]==0)freq[channel]=centreFreq;
    if(time[channel]>1/freq[channel] || freq[channel]<20){
      time[channel]=0;
      freq[channel] = centreFreq*(1-bandWidth/2+bandWidth*Math.random());
    } //each whole cycle, choose a new frequency
    return Math.cos(freq[channel]*2*Math.PI*time[channel]);
  }

  void initWheeze(int channels, double centreFreq, double bandWidth){
    for(int i=50;i<channels+50;i++){
      freq[i]=centreFreq + (Math.random()-0.5)*bandWidth;
    }
  }
  public double wheeze(int channels, double centreFreq, double bandWidth, double dt){
    double sum=0;
    for(int i=50;i<channels+50;i++){
      if(freq[i]==0) freq[i]=centreFreq + (Math.random()-0.5)*bandWidth;
      time[i]+=dt;
      sum+=Math.sin(freq[i]*2*Math.PI*time[i]);
    }
    return sum;
  }

  int NR=10;
  public double noiseCentred(int channel, double centreFreq, double bandWidth, double dt){
    double x = 0;
    for(int i=0;i<NR; i++){
      x+=noiseCentredF(NR*channel+i, centreFreq, bandWidth, dt);
    }
    return x;
  }

  public double nearness(double x, double y, double s){
    double t;
    return Math.exp( -(((t=(x-xcoord))*t) + ((t=(y-ycoord))*t)) / (s*s) );
  }

  /** Milliseconds between updates to the buffer */
  public static final int TIMER_DELAY = 250;
  /** The timer that updates the buffer */
  Timer timer = new Timer(TIMER_DELAY, timerAction);
  int sscale = 120;
  double A = 87.6, mu=255;
  /** translate a sample from a double between -1 and +1 into a PCM byte */
  public byte packByte(double value){
    int sgn=value>=0?1:-1;
    int v = Math.abs((int)(100*value));   // scale to range -128 to +128
    v=Math.max(0,Math.min(127,v)); // truncate to range
    // a-law
    //double sample= value<=1/A?(A*v / (1 + Math.log(A))) : (1 + Math.log(A*v)) / (1 + Math.log(A)) ;
    // mu-law
    //double sample = Math.log(1 + mu * v) / Math.log(1 + mu);
    //int isample=(int)Math.max(-127,Math.min(127,sample));
    //return (byte)(sgn*0x80 | (~0x80 & (int)sample));
    return ((byte) (( v +128)));
  }
  byte reverseBits(byte b){
    return (byte)( b/128 + 2*(b/64&1) + 4*(b/32&1) + 8*(b/16&1) +
                   16*(b/8&1) + 32*(b/4&1) + 64*(b/2&1) + 128*(b&1)
                   );
  }
}

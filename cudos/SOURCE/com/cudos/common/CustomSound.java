package com.cudos.common;


import javax.sound.sampled.*;

public class CustomSound extends Object{
        private byte[] pBuffer;
        private int length;
        private Clip clp;
        private Mixer mix;
        private boolean silent=false;
        public CustomSound(byte[] p){
                open(p);
        }
        int loops=1;
        public void open(byte[] p){
                pBuffer=new byte[loops*p.length];
                for(int i=0;i<p.length;i++){
                        byte value=(byte)((int)p[i]-192);
                        for(int j=0;j<loops;j++) pBuffer[j*p.length+i]=value;
                }
                length=pBuffer.length;
                mix=AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
                try {clp=(Clip)mix.getLine(mix.getSourceLineInfo(
                        new Line.Info(Class.forName("javax.sound.sampled.Clip")) )[0]);
                        AudioFormat af=new AudioFormat(22050f,16,1,false,false);
                        clp.open(af,pBuffer,0,length);
                } catch (Exception e) {
                        e.printStackTrace();
                        silent=true;
                }
                FloatControl gain=(FloatControl)clp.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(gain.getMaximum()/8);
        }
        public void close(){
                if(clp!=null){
                        if(isActive())stop();
                        if(clp.isOpen())clp.close();
                }
        }
        public void start(){
                if(!silent)clp.loop(clp.LOOP_CONTINUOUSLY);
        }
        public void playOnce(){
                if(!silent)clp.loop(1);
        }
        public void stop(){
                if(!silent){
                        clp.stop();
                        clp.setFramePosition(0);
                }
        }
        public boolean isActive(){
                if(!silent)return clp.isActive();
                else return false;
        }
        public void finalize() throws Throwable{
                if(!silent)clp.close();
                super.finalize();
        }
}

package phic.common;
import phic.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;
import phic.modifiable.RecordedEvent;
/**
 * Class containing data stored from various points in time in the history of
 * the patient.
 * Note: The Person data must NOT contain an item of this class!
 */

public class DataHistory {
    public DataHistory() {
    }

    Vector data = new Vector();
    Vector events = new Vector();

    public void storeSnapshot(Person p){
        data.add(new Snapshot(p));
    }
    public void storeEvent(String eventScript){
      RecordedEvent e=new RecordedEvent();
      e.setStatement(eventScript);
      e.setTime(Current.body.getClock().getTime());
      events.add( e );
    }

    public int getDataSize(){ return data.size();}

    public String[] getTimes(){
        String[] s = new String[data.size()];
        for(int i=0;i<data.size();i++){
            s[i] = Clock.datetimeformat.format(new java.util.Date(((Snapshot)data.get(i)).getTime()));
        }
        return s;
    }
    /** Restore the history, using the current SimplePhicFrame */
    public void restore(int i, phic.gui.SimplePhicFrame frame){
        ((Snapshot)data.get(i)).restore(frame);
    }
    /** Clear the history */
    public void removeAll(){
        data.removeAllElements();
    }


    /**
     * Removes all items after the given index
     * @param i the index after which to remove the items
     */

   public void removeAfter(int i) {
       while (data.size() > i+1)    data.remove(i+1);
    }




    /**
     * A single record in the data history.
     * Each record is about 35 k, compressed around 10 k
     */


    static class Snapshot{
        byte[] data;
        protected long time;
        /**
         * Create a snapshot, which files the given Person entry
         * in a zip file in memory
         */

                public Snapshot(Person p){
            try{
                time = p.body.getClock().getTime();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);
                zos.putNextEntry(new ZipEntry(toString()));
                ObjectOutputStream oos = new ObjectOutputStream(zos);
                oos.writeObject(p);
                zos.closeEntry();
                zos.close();
                data = baos.toByteArray();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public Person getPerson(){
            try{
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ZipInputStream zis = new ZipInputStream(bais);
                zis.getNextEntry();
                ObjectInputStream ois = new ObjectInputStream(zis);
                Person p = (Person)ois.readObject();
                zis.closeEntry();
                zis.close();
                return p;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        public long getTime(){ return time; }
        public String toString(){
            return Clock.datetimeformat.format(new java.util.Date(time));
        }
        public void restore(phic.gui.SimplePhicFrame f){
            f.setupNewPerson(getPerson());
        }
    }

}


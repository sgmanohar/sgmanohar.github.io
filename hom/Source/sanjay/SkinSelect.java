package sanjay;
import com.l2fprod.gui.*;
import com.l2fprod.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import com.l2fprod.gui.plaf.skin.*;
import java.util.Arrays;

import java.util.List;
import java.net.URL;
/**
 * This performs commands needed to choose a skin.
 */

public class SkinSelect implements Runnable {
  public SkinSelect() {
  }


  public void run(){
    JFileChooser fc= new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.showOpenDialog(null);
    File f=fc.getSelectedFile();
    if(f!=null){
      try{
        String fs = f.toString();
        URL u = f.toURL();
        Skin sk;
        if(fs.endsWith(".zip")){
          sk = SkinLookAndFeel.loadThemePack(u);
        }else{
          sk = SkinLookAndFeel.loadSkin(u);
        }
        SkinLookAndFeel.setSkin(sk);
        SkinLookAndFeel.enable();
      } catch(Exception ex){ex.printStackTrace();}
    }
  }






  JFrame f;
  SkinChooser sc;
  public void run2(){
    sc = new com.l2fprod.gui.SkinChooser();
    f=new JFrame("Choose skin");
    JPanel p=new JPanel();
    JButton b=new JButton("Load...");
    JButton b2=new JButton("OK");
    JPanel p2=new JPanel();
    f.getContentPane().add(p);
    p.setLayout(new BorderLayout());
    p.add(p2,BorderLayout.SOUTH);
    p2.add(b);  p2.add(b2);
    b2.addActionListener(new OKAL());
    p. add(sc, BorderLayout.CENTER) ;
    b.addActionListener(new AL());
    f.pack();
    f.show();
  }

  class AL implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFileChooser fc=new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.showOpenDialog(f);
      File file=fc.getSelectedFile();
      if(file!=null){
        try{
          //Skin skin=SkinLookAndFeel.loadSkin(file.toURL());
          String[] loc=sc.getSkinLocations();
          if(loc==null){
            sc.setSkinLocations(new String[]{file.toString()});
          }else{
            String[] loc2 = new String[loc.length+1];
            System.arraycopy(loc, 0,loc2,0,loc.length);
            loc2[loc.length]=file.toString();
            sc.setSkinLocations(loc2);
          }
        } catch(Exception ex){ex.printStackTrace();}
      }
    }
  }
  class OKAL implements ActionListener{
    public void actionPerformed(ActionEvent e){
      try{
        String s=sc.getSelectedSkins()[0];
        Skin sk = SkinLookAndFeel.loadSkin(s);
        SkinLookAndFeel.setSkin(sk);
      }
      catch(Exception ex){ex.printStackTrace();}
      if(f!=null){
        f.hide();
        f.dispose();
      }
    }
  }
}

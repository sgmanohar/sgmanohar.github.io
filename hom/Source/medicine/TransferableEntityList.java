package medicine;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.awt.event.ActionListener;

public class TransferableEntityList implements Transferable,ClipboardOwner {
  List list;
  public TransferableEntityList(List list) {
    this.list=list;;
    if(listener!=null)listener.actionPerformed(new ActionEvent(listToString(),0,"ClipboardChanged"));
  }
  public static DataFlavor listFlavor = new DataFlavor(List.class,"Entity");
  public static ActionListener listener=null;
  DataFlavor[] flavors=new DataFlavor[]{listFlavor,
      DataFlavor.stringFlavor};
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for(int i=0;i<flavors.length;i++)if(flavor.equals(flavors[i]))return true;
    return false;

  }
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if(flavor.equals(flavors[0]))return list;
   if(flavor.equals(flavors[1])){
     return listToString();
   }
   throw new UnsupportedFlavorException(flavor);
  }
  String listToString(){
    String s="";
    for(Iterator i=list.iterator();i.hasNext();){
      s+=((Entity)i.next()).toString()+";";
    }
    return s;
  }

  /**
   * lostOwnership
   *
   * @param clipboard Clipboard
   * @param contents Transferable
   */
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
  }

}

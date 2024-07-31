package medicine;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TransferableEntity implements Transferable , ClipboardOwner{
  Entity e;
  public TransferableEntity(Entity e) {
    this.e=e;
    if(listener!=null)listener.actionPerformed(new ActionEvent(e,0,"ClipboardChanged"));
  }
  public static DataFlavor entityFlavor = new DataFlavor(Entity.class,"Entity");
  public static ActionListener listener=null;
  DataFlavor[] flavors=new DataFlavor[]{entityFlavor,
      DataFlavor.stringFlavor};
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for(int i=0;i<flavors.length;i++)if(flavor.equals(flavors[i]))return true;
    return false;
  }
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
     if(flavor.equals(flavors[0]))return e;
     if(flavor.equals(flavors[1]))return e.toString();
     throw new UnsupportedFlavorException(flavor);
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

package com.vectorprint.dragdropedit;

import com.vectorprint.client.Kaartje;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Gebruikt door de transferhandler om kaartjes van de ene cel naar de andere te krijgen.
 * @author Eduard Drenth at VectorPrint.nl
 */
public class TransferableKaartje implements Transferable, Serializable {

   public static final DataFlavor KAARTJE_DATA_FLAVOR = new DataFlavor(Kaartje.class, "Planbord kaartje");
   Kaartje k = null;

   public TransferableKaartje(Kaartje k) {
      this.k = k;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{KAARTJE_DATA_FLAVOR};
   }

   public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
      return KAARTJE_DATA_FLAVOR.equals(dataFlavor);
   }

   public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
      if (isDataFlavorSupported(dataFlavor)) {
         return k;
      } else {
         return null;
      }

   }
}

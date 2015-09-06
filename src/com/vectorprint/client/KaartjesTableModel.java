/*
 * KaartjesTableModel.java
 *
 * Created on February 16, 2006, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.vectorprint.client;

import java.rmi.RemoteException;
import javax.swing.table.AbstractTableModel;
import com.vectorprint.server.RemoteKaartjesTableModel;

/**
 * Dit is het lokale datamodel voor de Kaartjes in het planbord, dit model zet aanroepen door naar het remote datamodel.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class KaartjesTableModel extends AbstractTableModel {

   private RemoteKaartjesTableModel rtm;

   /**
    * Creates a new instance of KaartjesTableModel. Dispatches calls to the tablemodel to a remote object
    *
    * @param rtm the remote model
    */
   public KaartjesTableModel(RemoteKaartjesTableModel rtm) {
      this.rtm = rtm;
   }

   public String getColumnName(int kol) {
      try {
         return rtm.getColumnName(kol);
      } catch (RemoteException ex) {
         ex.printStackTrace();
         return "RMIFOUT";
      }
   }

   public boolean isCellEditable(int row, int column) {
      try {
         return rtm.isCellEditable(row, column);
      } catch (RemoteException ex) {
         ex.printStackTrace();
         return false;
      }
   }

   public int getRowCount() {
      try {
         return rtm.getRowCount();
      } catch (RemoteException ex) {
         ex.printStackTrace();
         return 0;
      }
   }

   public int getColumnCount() {
      try {
         return rtm.getColumnCount();
      } catch (RemoteException ex) {
         ex.printStackTrace();
         return 0;
      }
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         return rtm.getValueAt(rowIndex, columnIndex);
      } catch (RemoteException ex) {
         ex.printStackTrace();
         return null;
      }
   }

   public Class getColumnClass(int columnIndex) {
      return Kaartje.class;
   }

   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      try {
         rtm.setValueAt(aValue, rowIndex, columnIndex);
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.client;

import java.rmi.RemoteException;
import com.vectorprint.server.TableChanged;

/**
 * class responsible for notifying the local table model that a certain cell changed in the remote data.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class TableChangedImpl implements TableChanged {

   private transient KaartjesTableModel ktm;

   /**
    *
    * @param ktm the local table model
    */
   public TableChangedImpl(KaartjesTableModel ktm) {
      this.ktm = ktm;
   }

   /**
    * will be called by the server when data changed in a certain cell
    *
    * @param row
    * @param col
    * @throws RemoteException
    */
   public void notifyChanged(int row, int col) throws RemoteException {
      ktm.fireTableCellUpdated(row, col);
   }
}

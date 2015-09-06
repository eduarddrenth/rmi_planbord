/*
 * RemoteKaartjesTableModel.java
 *
 * Created on 28 juli 2006, 22:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.vectorprint.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface voor een remote datamodel.
 * @author Eduard Drenth at VectorPrint.nl
 */
public interface RemoteKaartjesTableModel extends Remote {

   public String getColumnName(int kol) throws RemoteException;

   public boolean isCellEditable(int row, int column) throws RemoteException;

   public int getRowCount() throws RemoteException;

   public int getColumnCount() throws RemoteException;

   public Object getValueAt(int rowIndex, int columnIndex) throws RemoteException;

   public Class getColumnClass(int columnIndex) throws RemoteException;

   public void setValueAt(Object aValue, int rowIndex, int columnIndex) throws RemoteException;

   /**
    * registreer een object dat op de hoogte moet worden gesteld van wijzigingen in een cel
    * @param tc
    * @throws RemoteException 
    */
   public void registerTableChanged(TableChanged tc) throws RemoteException;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * een object dat geinteresseerd is in veranderingen in een cel
 * @author Eduard Drenth at VectorPrint.nl
 */
public interface TableChanged extends Remote {

   public void notifyChanged(int row, int col) throws RemoteException;
}

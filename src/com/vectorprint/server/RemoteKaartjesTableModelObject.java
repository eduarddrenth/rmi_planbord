/*
 * RemoteKaartjesTableModelObject.java
 *
 * Created on 28 juli 2006, 22:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.vectorprint.server;

import com.vectorprint.client.Kaartje;
import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementatie van een remote datamodel
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class RemoteKaartjesTableModelObject extends UnicastRemoteObject implements RemoteKaartjesTableModel {

   private Kaartje[][] kaartjes;
   private List namen;
   private static List props = new LinkedList();

   /**
    * Creates a new instance of KaartjesTableModel
    */
   public RemoteKaartjesTableModelObject(Kaartje[][] kaartjes, List namen) throws RemoteException {
      super();
      this.kaartjes = kaartjes;
      this.namen = namen;
   }

   public String getColumnName(int kol) throws RemoteException {
      return (namen != null && namen.size() > kol) ? (String) namen.get(kol) : "NONAME";
   }

   public boolean isCellEditable(int row, int column) throws RemoteException {
      if (kaartjes!=null) {
         synchronized(kaartjes) {
            return true;
         }
      }
      return true;
   }

   public int getRowCount() throws RemoteException {
      return (kaartjes != null) ? kaartjes.length : 0;
   }

   public int getColumnCount() throws RemoteException {
      return (kaartjes != null && kaartjes[0] != null) ? kaartjes[0].length : 0;
   }

   public Object getValueAt(int rowIndex, int columnIndex) throws RemoteException {

      if (kaartjes != null
          && rowIndex > -1 && columnIndex > -1
          && kaartjes.length > rowIndex
          && kaartjes[rowIndex].length > columnIndex) {
         synchronized (kaartjes) {
            return kaartjes[rowIndex][columnIndex];
         }
      }
      return null;
   }

   public Class getColumnClass(int columnIndex) throws RemoteException {
      return Kaartje.class;
   }

   /**
    * pas de waarde in een cel aan, laat de clients dit weten en persisteer het model
    * @param aValue
    * @param rowIndex
    * @param columnIndex
    * @throws RemoteException 
    */
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) throws RemoteException {
      if (kaartjes != null
          && rowIndex > -1 && columnIndex > -1
          && kaartjes.length > rowIndex
          && kaartjes[rowIndex].length > columnIndex) {
         synchronized (kaartjes) {
            kaartjes[rowIndex][columnIndex] = (Kaartje) aValue;
            for (TableChanged tc : listeners) {
               tc.notifyChanged(rowIndex, columnIndex);
            }
            try {
               store(this);
            } catch (FileNotFoundException ex) {
               Logger.getLogger(RemoteKaartjesTableModelObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
               Logger.getLogger(RemoteKaartjesTableModelObject.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
   }

   /**
    * lees settings in, laat een evt. gepersisteerd model, registreer het model in de rmiregistry
    * @param args
    * @throws FileNotFoundException
    * @throws IOException 
    */
   public static void main(String args[]) throws FileNotFoundException, IOException {

      RemoteKaartjesTableModelObject model = null;

      try {

         if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
         }

         BufferedReader in = new BufferedReader(
             new FileReader("planbord.props"));
         // kolomnamen lezen

         List headers = new LinkedList();
         int rows = 10;

         String line = null;

         while ((line = in.readLine()) != null) {
            if (line.trim().equals("")) {
               continue;
            }
            String[] s = line.split("[\t=: ]+");
            if (s.length == 1) {
               headers.add(s[0]);
            } else {
               props.add(s);
               if ("rows".equals(s[0])) {
                  rows = Integer.parseInt(s[1]);
               }
            }
         }
         in.close();

         model = new RemoteKaartjesTableModelObject(
             new Kaartje[rows][headers.size()],
             headers);
         load(model);
         
         Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);
         registry.rebind("//localhost/RemoteKaartjesTableModel", model);
         
      } catch (FileNotFoundException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }

   private static void store(RemoteKaartjesTableModel model) throws FileNotFoundException, IOException {
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
          System.getProperty("user.home") + File.separator + "PlanBord.txt"), 1024);
      for (int i = 0; i < model.getRowCount(); i++) {
         for (int j = 0; j < model.getColumnCount(); j++) {
            Object val = model.getValueAt(i, j);
            if (val != null) {
               Kaartje k = (Kaartje) val;
               out.write((i + "\t").getBytes());
               out.write((j + "\t").getBytes());
               out.write((k.naaien.getBackground().hashCode() + "\t").getBytes());
               out.write((k.omslagbinnen.getBackground().hashCode() + "\n").getBytes());
               out.write((k.tekst.getText() + "\n").getBytes());
            }
         }
      }
      out.close();
   }

   private static void load(RemoteKaartjesTableModel model) throws FileNotFoundException, IOException {
      BufferedReader in = new BufferedReader(new FileReader(
          System.getProperty("user.home") + File.separator + "PlanBord.txt"), 1024);
      String regel = null;
      String t = "";
      Kaartje k = null;
      while ((regel = in.readLine()) != null) {
         if (regel.matches("^[0-9]+\t[0-9]+\t-?[0-9]+\t-?[0-9]+$")) {
            if (k != null && !"".equals(t)) {
               k.tekst.setText(t);
               t = "";
            }
            String[] split = regel.split("\t");
            int row = Integer.parseInt(split[0]);
            int cel = Integer.parseInt(split[1]);
            Color naaien = new Color(Integer.parseInt(split[2]));
            Color omslag = new Color(Integer.parseInt(split[3]));
            k = new Kaartje();
            k.naaien.setBackground(naaien);
            k.omslagbinnen.setBackground(omslag);
            model.setValueAt(k, row, cel);
         } else {
            if (!"".equals(t)) {
               t = t + "\n";
            }
            t += regel;

         }
      }
      if (k != null && !"".equals(t)) {
         k.tekst.setText(t);
         t = "";
      }
      in.close();
   }
   private List<TableChanged> listeners = new ArrayList<TableChanged>(2);

   /**
    * voeg een refenetie naar een client toe
    * @param tc
    * @throws RemoteException 
    */
   public void registerTableChanged(TableChanged tc) throws RemoteException {
      listeners.add(tc);
   }
}

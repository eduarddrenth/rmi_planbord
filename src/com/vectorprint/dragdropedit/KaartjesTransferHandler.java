/*
 * KaartjesTransferHandler.java
 *
 * Created on 29 juli 2006, 8:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.vectorprint.dragdropedit;

import com.vectorprint.client.Kaartje;
import com.vectorprint.client.PlanBord;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 * Verantwoordelijk voor het uitvoeren van knippen, kopieren en plakken van kaartjes alsmede voor het verwerken van sleepacties.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class KaartjesTransferHandler extends TransferHandler {

   private int rowDraggedKaartje, colDraggedKaartje;
   private JPopupMenu dragImage = new JPopupMenu();
   private MouseEvent dragStart = null;
   private int xDeviation, yDeviation;
   private Color dragColor = new Color(20, 200, 20, 50);

   public KaartjesTransferHandler() {
      dragImage.setBackground(dragColor);
      dragImage.setBorderPainted(false);
   }

   public void moveDragImage(int x, int y) {
      if (dragImage.isVisible()) {
         dragImage.setLocation(x - xDeviation, y - yDeviation);
      }
   }

   @Override
   public boolean importData(JComponent jComponent, Transferable transferable) {
      boolean retValue = true;
      JTable tabel = (JTable) jComponent;
      Invoer invoer = (Invoer) tabel.getDefaultEditor(Kaartje.class);
      Point p = null;
      if (dragImage.isVisible()) {
         p = new Point(invoer.dragEnd.getX(), invoer.dragEnd.getY());
      }
      int row = (!dragImage.isVisible()) ? tabel.getSelectedRow() : tabel.rowAtPoint(p);
      int col = (!dragImage.isVisible()) ? tabel.getSelectedColumn() : tabel.columnAtPoint(p);

      Kaartje tk = null;
      try {
         tk = (Kaartje) transferable.getTransferData(TransferableKaartje.KAARTJE_DATA_FLAVOR);
      } catch (UnsupportedFlavorException ex) {
         ex.printStackTrace();
         retValue = false;
      } catch (IOException ex) {
         ex.printStackTrace();
         retValue = false;
      }

      if (!canImport(jComponent, transferable.getTransferDataFlavors()) || row < 0 || col < 0 || !retValue || tk == null
          || (dragStart != null && !dragImage.isVisible())) {
         retValue = false;
         if (dragImage.isVisible() && tk != null) {
            tabel.setValueAt(tk, rowDraggedKaartje, colDraggedKaartje);
         }
      } else {
         if (tabel.getValueAt(row, col) == null) {
            tabel.setValueAt(tk, row, col);
         } else {
            retValue = false;
         }

         if (!retValue) {
            tabel.setValueAt(tk, rowDraggedKaartje, colDraggedKaartje);
         } else if (!dragImage.isVisible()) {
            ((Invoer) tabel.getDefaultEditor(Kaartje.class)).editAnyway = true;
            tabel.editCellAt(row, col);
         }
      }
      if (dragImage.isVisible()) {
         dragImage.setVisible(false);
         dragImage.remove(0);
         dragStart = null;
      }
      return retValue;
   }

   @Override
   public boolean canImport(JComponent jComponent, DataFlavor[] dataFlavor) {
      boolean retValue = false;

      if (dataFlavor == null || dataFlavor.length < 1) {
      } else {
         for (int i = 0; i < dataFlavor.length; i++) {
            if (TransferableKaartje.KAARTJE_DATA_FLAVOR.equals(dataFlavor[i])) {
               retValue = true;
               break;
            }
         }
      }
      return retValue;
   }

   @Override
   public int getSourceActions(JComponent jComponent) {
      return TransferHandler.COPY_OR_MOVE;
   }

   @Override
   protected Transferable createTransferable(JComponent jComponent) {
      JTable tabel = (JTable) jComponent;
      return new TransferableKaartje((Kaartje) tabel.getValueAt(rowDraggedKaartje, colDraggedKaartje));
   }

   @Override
   public void exportToClipboard(JComponent jComponent, Clipboard clipboard, int i) throws IllegalStateException {
      JTable tabel = (JTable) jComponent;
      rowDraggedKaartje = tabel.getSelectedRow();
      colDraggedKaartje = tabel.getSelectedColumn();
      if (tabel.getValueAt(rowDraggedKaartje, colDraggedKaartje) != null) {
         Transferable tf = createTransferable(jComponent);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(tf, PlanBord.getInstance());
         exportDone(jComponent, tf, i);
      }
   }

   @Override
   protected void exportDone(JComponent jComponent, Transferable transferable, int i) {
      if (i == TransferHandler.MOVE) {
         JTable tabel = (JTable) jComponent;
         tabel.setValueAt(null, rowDraggedKaartje, colDraggedKaartje);
         tabel.repaint();
      }
   }

   @Override
   public void exportAsDrag(JComponent jComponent, InputEvent inputEvent, int i) {
      dragStart = (MouseEvent) inputEvent;
      JTable tabel = (JTable) jComponent;
      rowDraggedKaartje = tabel.getSelectedRow();
      colDraggedKaartje = tabel.getSelectedColumn();
      Kaartje k = (Kaartje) tabel.getValueAt(rowDraggedKaartje, colDraggedKaartje);
      if (k != null) {
         Transferable tf = createTransferable(jComponent);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(tf, PlanBord.getInstance());
         exportDone(jComponent, tf, i);

         /*  we willen exact weten waar in het object in de cel we geklikt hebben
          *  daar is niet een functie voor, dus moeten we rekenen ofzo....
          *
          *
          */
         Point cellLocation = tabel
             .getAccessibleContext() // geeft een AccessibleJTable terug
             .getAccessibleTable()
             .getAccessibleAt(rowDraggedKaartje, colDraggedKaartje) // geeft een AccessibleJTableCell terug
             .getAccessibleContext()
             .getAccessibleComponent()
             .getLocationOnScreen();

         xDeviation = dragStart.getX() - (int) cellLocation.getX();
         yDeviation = dragStart.getY() - (int) cellLocation.getY();

         Kaartje dup = (Kaartje) k.clone();
         dup.setOpaque(false);

         dragImage.add(dup);
         dragImage.pack();
         dragImage.show(tabel, dragStart.getX() - xDeviation, dragStart.getY() - yDeviation);
      }
   }
}

/*
 * Invoer.java
 *
 * Created on February 17, 2006, 4:43 PM
 */
package com.vectorprint.dragdropedit;

import com.vectorprint.client.Kaartje;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

/**
 * verantwoordelijk voor het tonen van een editor voor celinhoud en voor het bijwerken van waarden in de celinhoud op
 * basis van waarden uit de editor. Daarnaast speelt deze klasse ook een rol bij slepen van kaartjes.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class Invoer extends DefaultCellEditor implements MouseMotionListener, MouseListener {

   private Kaartje k = null;
   private JTable tabel = null;
   public boolean editAnyway = false;
   MouseEvent dragStart = null, dragEnd = null;

   @Override
   public boolean isCellEditable(EventObject evt) {
      if (editAnyway) {
         editAnyway = false;
         return true;
      }
      return super.isCellEditable(evt);
   }

   @Override
   public Component getTableCellEditorComponent(JTable table,
       Object value,
       boolean isSelected,
       int row,
       int column) {
      if (value == null) {
         k = new Kaartje();
      } else {
         k = (Kaartje) value;
      }
      EdittableKaartje ek = (EdittableKaartje) editorComponent;
      ek.naaien.setBackground(k.naaien.getBackground());
      ek.omslagbinnen.setBackground(k.omslagbinnen.getBackground());
      ek.tekst.setText(k.tekst.getText());
      tabel.repaint();
      return ek;
   }

   @Override
   public Object getCellEditorValue() {
      if (k != null) {
         EdittableKaartje ek = (EdittableKaartje) editorComponent;
         String t = ek.tekst.getText();
         k.tekst.setText(t);
         k.naaien.setBackground(ek.naaien.getBackground());
         k.omslagbinnen.setBackground(ek.omslagbinnen.getBackground());
         k.tekst.setToolTipText(k.tekst.getText());
      }
      return k;
   }

   public void mouseDragged(MouseEvent mouseEvent) {
      mouseEvent.consume();
      if (dragStart == null) {
         tabel.getTransferHandler().exportAsDrag(tabel, mouseEvent, (mouseEvent.isControlDown()) ? TransferHandler.COPY : TransferHandler.MOVE);
         dragStart = mouseEvent;
      } else {
         ((KaartjesTransferHandler) tabel.getTransferHandler()).moveDragImage(mouseEvent.getX(), mouseEvent.getY());
      }
   }

   public void mouseMoved(MouseEvent mouseEvent) {
   }

   public void mouseClicked(MouseEvent mouseEvent) {
   }

   public void mousePressed(MouseEvent mouseEvent) {
   }

   public void mouseReleased(MouseEvent mouseEvent) {
      mouseEvent.consume();
      if (dragStart != null) {
         dragEnd = mouseEvent;
         if (tabel.getTransferHandler().importData(tabel, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(tabel))) {
            editAnyway = true;
            Point p = new Point(dragEnd.getX(), dragEnd.getY());
            tabel.editCellAt(tabel.rowAtPoint(p), tabel.columnAtPoint(p));
         }
         tabel.repaint();
         dragStart = null;
      }
   }

   public void mouseEntered(MouseEvent mouseEvent) {
   }

   public void mouseExited(MouseEvent mouseEvent) {
   }

   /**
    * Creates new form Invoer
    *
    * @param table
    */
   public Invoer(JTable table, EdittableKaartje ek) {
      super(new JTextField());

      editorComponent = ek;

      setClickCountToStart(2);

      tabel = table;

   }
}

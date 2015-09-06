/*
 * KaartjesCellRenderer.java
 *
 * Created on February 16, 2006, 5:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.vectorprint.client;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Verantwoordelijk voor het renderen van een Kaartje in een cell van het datamodel.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class KaartjesCellRenderer implements TableCellRenderer {

   private String title;
   private Color color = new Color(180, 230, 180);

   /**
    * Creates a new instance of KaartjesCellRenderer
    *
    * @param title
    */
   public KaartjesCellRenderer(String title) {
      this.title = title;
   }

   /**
    *
    * @param table de tabel waarin we de data tonen
    * @param value de waarde die we tonen, dit is een Kaartje
    * @param isSelected is de cel geselecteerd
    * @param hasFocus heeft de cel de focus
    * @param row
    * @param column
    * @return
    */
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Kaartje k = (Kaartje) value;
      String t = "";
      if (k != null) {
         if (isSelected) {
            t = k.tekst.getText();
            k.setBackground(color.darker());
         } else {
            k.setBackground(color);
         }
      }
      if (hasFocus) {
         PlanBord.getInstance().setTitle(title + ", kolom: " + table.getColumnName(column) + ", regel: " + row + " " + t);
      }
      return k;
   }
}

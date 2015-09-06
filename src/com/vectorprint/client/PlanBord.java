/*
 * PlanBord.java
 *
 * Created on February 18, 2006, 10:08 PM
 */
package com.vectorprint.client;

import com.vectorprint.dragdropedit.EdittableKaartje;
import com.vectorprint.dragdropedit.KaartjesTransferHandler;
import com.vectorprint.dragdropedit.Invoer;
import com.vectorprint.dragdropedit.TransferableKaartje;
import com.vectorprint.theme.SkinChoose;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import com.vectorprint.server.RemoteKaartjesTableModel;
import com.vectorprint.server.TableChanged;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * De client GUI voor het planbord, initializeert de tabel met de mogelijkheden voor inline editten van Kaartjes,
 * renderen van Kaartjes, slepen van kaartjes, toetsenbordcombinaties. Initializeert thema's op basis van skinlf.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class PlanBord extends javax.swing.JFrame implements ClipboardOwner {

   private Invoer invoer;
   private static PlanBord me;
   private static List props = new LinkedList();
   private Help help;

   /**
    *
    * @return the one and only instance of the gui in a classloader
    */
   public static PlanBord getInstance() {
      return me;
   }

   /**
    * Creates new PlanBord gui using a tablemodel
    *
    * @param tabelModel
    */
   private PlanBord(KaartjesTableModel tabelModel) {
      initComponents();
      if (me == null) {
         me = this;
         help = new Help();
      }

      jTable1.setDefaultRenderer(Kaartje.class, new KaartjesCellRenderer(getTitle()));

      jTable1.setModel(tabelModel);

      jTable1.getTableHeader().setFont(
          jTable1.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));

      invoer = new Invoer(jTable1, new EdittableKaartje());

      jTable1.setDefaultEditor(Kaartje.class, invoer);

      jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      jTable1.setTransferHandler(new KaartjesTransferHandler());

      jTable1.addMouseMotionListener(invoer);

      jTable1.addMouseListener(invoer);

      jTable1.addMouseMotionListener(new MouseMotionAdapter() {
         public void mouseDragged(MouseEvent mouseEvent) {
            mouseEvent.consume();
            ((KaartjesTransferHandler) jTable1.getTransferHandler()).moveDragImage(mouseEvent.getX(), mouseEvent.getY());
         }
      });

      jTable1.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent evt) {
            switch (evt.getModifiers()) {
               case 0:
                  switch (evt.getKeyCode()) {
                     case 127:
                        evt.consume();
                        if (jTable1.getValueAt(jTable1.getSelectedRow(), jTable1.getSelectedColumn()) != null) {
                           jTable1.getTransferHandler().exportToClipboard(jTable1, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.MOVE);

                        }

                        break;
                     case 10:
                        evt.consume();
                        invoer.editAnyway = true;
                        jTable1.editCellAt(jTable1.getSelectedRow(), jTable1.getSelectedColumn());
                        break;
                  }
                  break;
            }
         }
      });

      Dimension d = new Kaartje().getPreferredSize();

      for (int i = 0; i < jTable1.getColumnCount(); i++) {
         jTable1.getColumnModel().getColumn(i).setWidth((int) d.getWidth());
         jTable1.getColumnModel().getColumn(i).setPreferredWidth((int) d.getWidth());

      }
      for (int i = 0; i < jTable1.getRowCount(); i++) {
         jTable1.setRowHeight(i, (int) d.getHeight());
      }


      String[] themas = {
         "aquathemepack.zip",
         "bbjthemepack.zip",
         "BeOSthemepack.zip",
         "coronaHthemepack.zip",
         "crystal2themepack.zip",
         "gfxOasisthemepack.zip",
         "midnightthemepack.zip",
         "oliveGreenLunaXPthemepack.zip",
         "roueBrownthemepack.zip",
         "silverLunaXPthemepack.zip",
         "skinlfthemepack.zip",
         "tigerthemepack.zip"
      };
      SkinChoose.theme = "skinlfthemepack.zip";
      SkinChoose.addSkins(jMenu1, new Window[]{this, help}, themas);
      SkinChoose.changeSkin(SkinChoose.theme, SkinChoose.class);


   }

   JTable getTable() {
      return jTable1;
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jScrollPane1 = new javax.swing.JScrollPane();
      jTable1 = new javax.swing.JTable();
      jMenuBar1 = new javax.swing.JMenuBar();
      jMenu1 = new javax.swing.JMenu();
      jMenu2 = new javax.swing.JMenu();
      jMenuItem1 = new javax.swing.JMenuItem();
      jMenuItem2 = new javax.swing.JMenuItem();
      jMenu3 = new javax.swing.JMenu();
      jMenuItem3 = new javax.swing.JMenuItem();
      jMenuItem4 = new javax.swing.JMenuItem();
      jMenuItem5 = new javax.swing.JMenuItem();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setTitle("Planbord Krips");

      jTable1.setToolTipText("dubbelklik met de muis om text op een kaartje te wijzigen,\nof de kleur van de 2 balkjes te wijzigen. PIJLTJES = \"navigeren\", DEL = \"kaartje verwijderen\", Ctrl-X = \"cut\", Ctrl-C = \"copy\", Ctrl-V = \"paste\", Enter = \"kaartje invoegen\".");
      jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
      jTable1.setCellSelectionEnabled(true);
      jScrollPane1.setViewportView(jTable1);

      jMenu1.setMnemonic('u');
      jMenu1.setText("uiterlijk");
      jMenuBar1.add(jMenu1);

      jMenu2.setMnemonic('h');
      jMenu2.setText("Help");
      jMenu2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenu2ActionPerformed(evt);
         }
      });

      jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
      jMenuItem1.setText("help");
      jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem1ActionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem1);

      jMenuItem2.setText("about");
      jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem2ActionPerformed(evt);
         }
      });
      jMenu2.add(jMenuItem2);

      jMenuBar1.add(jMenu2);

      jMenu3.setMnemonic('E');
      jMenu3.setText("Edit");

      jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
      jMenuItem3.setText("Kopieren");
      jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Kopieren(evt);
         }
      });
      jMenu3.add(jMenuItem3);

      jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
      jMenuItem4.setText("Knippen");
      jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Knippen(evt);
         }
      });
      jMenu3.add(jMenuItem4);

      jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
      jMenuItem5.setText("Plakken");
      jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            Plakken(evt);
         }
      });
      jMenu3.add(jMenuItem5);

      jMenuBar1.add(jMenu3);

      setJMenuBar(jMenuBar1);

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void Plakken(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Plakken
       jTable1.getTransferHandler().importData(jTable1, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(jTable1));
       jTable1.repaint();

    }//GEN-LAST:event_Plakken

    private void Knippen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Knippen
       jTable1.getTransferHandler().exportToClipboard(jTable1,
           Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.MOVE);
    }//GEN-LAST:event_Knippen

    private void Kopieren(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Kopieren
       jTable1.getTransferHandler().exportToClipboard(jTable1,
           Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
    }//GEN-LAST:event_Kopieren

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
       JOptionPane.showMessageDialog(this, "Versie 2.0, gemaakt bij VectorPrint door Eduard Drenth");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       help.setVisible((help.isVisible()) ? false : true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
    }//GEN-LAST:event_jMenu2ActionPerformed

   /**
    * zoek het remote data model, maak het planbord op basis van dit model, registreer een listener voor wijzigingen bij
    * het remote data model en toon het planbord.
    *
    * @param args
    */
   public static void main(String args[]) {

      // bugfix for having to click twice before dragging starts in JTable etc.
      System.setProperty("sun.swing.enableImprovedDragGesture", "true");
      if (System.getSecurityManager() == null) {
         System.setSecurityManager(new RMISecurityManager());
      }

      try {
         Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);

         RemoteKaartjesTableModel rtm = (RemoteKaartjesTableModel) registry.lookup("//localhost/RemoteKaartjesTableModel");

         final PlanBord bord = new PlanBord(new KaartjesTableModel(rtm));

         TableChanged ltc = new TableChangedImpl((KaartjesTableModel) bord.jTable1.getModel());
         TableChanged stubTc = (TableChanged) UnicastRemoteObject.exportObject(ltc, 0);
         rtm.registerTableChanged(stubTc);

         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               bord.setVisible(true);
            }
         });
      } catch (IOException ex) {
         ex.printStackTrace();
         Frame f = new Frame("fout");
         f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
               System.exit(1);
            }
         });
         f.setVisible(true);
      } catch (NotBoundException ex) {
         ex.printStackTrace();
         Frame f = new Frame("fout");
         f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
               System.exit(1);
            }
         });
         f.setVisible(true);
      }


   }

   public void lostOwnership(Clipboard clipboard, Transferable transferable) {
      Kaartje k = null;
      try {
         k = (Kaartje) transferable.getTransferData(TransferableKaartje.KAARTJE_DATA_FLAVOR);
      } catch (UnsupportedFlavorException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      System.err.println("Kaartje in Clipboard verloren, gegevens:");
      System.err.println(k.tekst.getText());
      System.err.println("naaien: " + k.naaien.getBackground().equals(EdittableKaartje.naaikleur));
      System.err.println("omslagbinnen: " + k.omslagbinnen.getBackground().equals(EdittableKaartje.omslagkleur));
   }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JMenu jMenu1;
   private javax.swing.JMenu jMenu2;
   private javax.swing.JMenu jMenu3;
   private javax.swing.JMenuBar jMenuBar1;
   private javax.swing.JMenuItem jMenuItem1;
   private javax.swing.JMenuItem jMenuItem2;
   private javax.swing.JMenuItem jMenuItem3;
   private javax.swing.JMenuItem jMenuItem4;
   private javax.swing.JMenuItem jMenuItem5;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTable jTable1;
   // End of variables declaration//GEN-END:variables
}

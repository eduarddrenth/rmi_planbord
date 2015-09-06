package com.vectorprint.theme;

import com.l2fprod.gui.plaf.skin.*;

import java.awt.Window;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.io.BufferedInputStream;

import javax.swing.JCheckBoxMenuItem;

import javax.swing.JMenu;

import javax.swing.JMenuItem;

import javax.swing.SwingUtilities;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Skinnen van java GUI's o.b.v. skinlf. De opzet is:
 *
 * <ul>
 *
 * <li>Zet een aantal zip files met skinlf thema's in een jar</li>
 *
 * <li>Genereer een Jmenu waarmee deze skinlf thema's kunnen worden geselecteerd</li>
 *
 * </ul>
 *
 *
 *
 * Voorbeeld code:
 *
 *
 *
 * <pre>
 *
 * String[] themas = { "tigerthemepack.zip", "themepack.zip" };
 *
 * SkinChoose.theme = "tigerthemepack.zip";
 *
 * SkinChoose.addSkins(javax.swing.Jmenu, java.awt.Window, themas);
 *
 * SkinChoose.changeSkin(SkinChoose.theme);
 *
 * </pre>
 *
 *
 *
 * Ander voorbeeld, waarbij meedere Windows worden geskinned en waarbij de zipfiles met de thema's<br>
 *
 * in een jar zitten op hetzelfde niveau als de opgegeven Class:
 *
 *
 *
 * <pre>
 *
 * String[] themas = { "tigerthemepack.zip", "themepack.zip" };
 *
 * SkinChoose.addSkins(javax.swing.Jmenu, java.awt.Window[], themas);
 *
 * SkinChoose.changeSkin("tigerthemepack.zip", Class uit een eigen jar);
 *
 * </pre>
 *
 *
 *
 * @author eduard
 *
 */
public class SkinChoose {

   /**
    * String met het actuele thema
    *
    */
   public static String theme = "default";
   private static JMenu menu = null;
   private static Window[] windowsEffected = null;
   private static Class base = null;

   /**
    *
    *
    * @param menu Het menu waar de thema's in komen
    * @param c Windows die gerepaint gaan worden bij thema wisseling
    * @param skins de skins die in een jar beschikbaar zijn
    */
   public static void addSkins(JMenu menu, Window[] c, String[] skins) {

      SkinChoose.windowsEffected = c;

      SkinChoose.menu = menu;
      for (String skin : skins) {
         addItem(menu, skin);
      }

   }

   /**
    *
    *
    * @param menu Het menu waar de thema's in komen
    * @param c een Window dat gerepaint gaat worden bij thema wisseling
    * @param skins de skins die in een jar beschikbaar zijn
    */
   public static void addSkins(JMenu menu, Window c, String[] skins) {

      Window[] cc = {c};

      addSkins(menu, cc, skins);

   }

   /**
    *
    * @param name the skin to look for
    *
    * @return true when skin was successfully changed.
    *
    */
   public static boolean changeSkin(String name) {

      return changeSkin(name, SkinChoose.class);

   }

   /**
    *
    * @param name the skin to look for
    *
    * @param searchBase The zipfiles with the skins are searched in the jar where this class is found, at the same
    * level.
    *
    * @return true when skin was successfully changed.
    *
    */
   public static boolean changeSkin(String name, Class searchBase) {

      if (windowsEffected == null || name == null) {

         return false;

      }

      if (base == null) {
         base = searchBase;
      }

      boolean retval = false;

      try {

         Skin huid = null;

         Skin prevSkin = null;

         if ("default".equals(name) || "".equals(name)) {

            try {

               prevSkin = SkinLookAndFeel.getSkin();

            } catch (Exception e) {
            }

            SkinLookAndFeel.setSkin(null);

            try {

               huid = SkinLookAndFeel.getSkin();

            } catch (Exception e) {

               System.err.println(e.getMessage());

               SkinLookAndFeel.setSkin(prevSkin);

            }

         } else {

            BufferedInputStream in = new BufferedInputStream(searchBase.getResourceAsStream(name));

            if (in.available() > 0) {

               huid = SkinLookAndFeel.loadThemePack(in);

            }

            in.close();

         }

         if (huid != null) {

            SkinLookAndFeel.setSkin(huid);

            if (!UIManager.getLookAndFeel().getClass().getName().equals("com.l2fprod.gui.plaf.skin.SkinLookAndFeel")) {

               try {

                  UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");

               } catch (Exception ex) {
               }

            }

            repaintAfterChange();

            theme = name;

            retval = true;

         } else {

            System.err.println("skin: " + name + " not found");

         }

      } catch (Exception ex) {

         ex.printStackTrace(System.err);

      }

      return retval;

   }

   public static void defaultJavaLF() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      repaintAfterChange();
   }

   private static void repaintAfterChange() {
      for (Window windowsEffected1 : windowsEffected) {
         SwingUtilities.updateComponentTreeUI(windowsEffected1);
         windowsEffected1.pack();
      }

   }

   private static void addItem(JMenu menu, String text) {

      JCheckBoxMenuItem m = new JCheckBoxMenuItem(text.replaceFirst("/", "").replaceFirst("themepack", "").replaceFirst("\\.zip", ""));

      m.setActionCommand(text);

      m.addActionListener(new ChangeSkinAction());

      m.setSelected(text.equals(theme));

      menu.add(m);

   }

   /**
    *
    * @author eduard
    *
    */
   private static class ChangeSkinAction implements ActionListener {

      /**
       *
       * @param e
       *
       */
      public void actionPerformed(ActionEvent e) {

         boolean changed = (base != null) ? SkinChoose.changeSkin(e.getActionCommand(), base) : SkinChoose.changeSkin(e.getActionCommand());

         if (changed) {

            for (int i = 0; i < SkinChoose.menu.getItemCount(); i++) {

               SkinChoose.menu.getItem(i).setSelected(false);

            }

            ((JMenuItem) e.getSource()).setSelected(true);

         }

      }
   }
}

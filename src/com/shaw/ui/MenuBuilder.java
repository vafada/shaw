package com.shaw.ui;

import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.windows.ExtWindowsLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.shaw.db.HSQLConnection;
import com.shaw.ExcelBuilder;
import com.shaw.ShawException;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class MenuBuilder {
    /**
     * Builds, configures, and answers the menubar. Requests HeaderStyle,
     * look-specific BorderStyles, and Plastic 3D hint from Launcher.
     */
    JMenuBar buildMenuBar(Settings settings) {

        JMenuBar bar = new JMenuBar();
        bar.putClientProperty(Options.HEADER_STYLE_KEY, settings.getMenuBarHeaderStyle());
        bar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, settings.getMenuBarPlasticBorderStyle());
        bar.putClientProperty(ExtWindowsLookAndFeel.BORDER_STYLE_KEY, settings.getMenuBarWindowsBorderStyle());
        bar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, settings.getMenuBar3DHint());

        bar.add(buildFileMenu());
        bar.add(buildHelpMenu());
        return bar;
    }


    /**
     * Builds and answers the file menu.
     */
    private JMenu buildFileMenu() {
        //JMenuItem item;

        JMenu menu = createMenu("File", 'F');

        // Build a submenu that has the noIcons hint set.

        JMenuItem submenu = createMenuItem("Export Inventory", 'x');
        submenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new MyFilter());
                int returnVal = fc.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String filename = null;
                    File file = fc.getSelectedFile();
                    if (file.getName().indexOf(".xls") > -1) {
                        filename = file.getPath();
                    } else {
                        filename = file.getPath() + ".xls";
                    }
                    try {
                        ExcelBuilder.exportInventory(filename);
                        JOptionPane.showMessageDialog(null,
                                "Exported to excel",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (ShawException se) {
                        JOptionPane.showMessageDialog(null,
                                se.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        menu.add(submenu);
        menu.addSeparator();

        if (!isQuitInOSMenu()) {
            JMenuItem exit = createMenuItem("Exit", 'E');
            exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    HSQLConnection.getInstance().close();
                    System.exit(0);
                }
            });
            menu.add(exit);
        }

        return menu;
    }


    /**
     * Builds and answers the help menu.
     */
    private JMenu buildHelpMenu() {

        JMenu menu = createMenu("Help", 'H');

        JMenuItem item;
        item = createMenuItem("About", 'a');
        menu.add(item);

        return menu;
    }

    // Factory Methods ********************************************************

    protected JMenu createMenu(String text, char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    protected JMenuItem createMenuItem(String text) {
        return new JMenuItem(text);
    }

    protected JMenuItem createMenuItem(String text, char mnemonic) {
        return new JMenuItem(text, mnemonic);
    }

    protected JMenuItem createMenuItem(String text, char mnemonic, KeyStroke key) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }

    protected JMenuItem createMenuItem(String text, Icon icon) {
        return new JMenuItem(text, icon);
    }

    protected JMenuItem createMenuItem(String text, Icon icon, char mnemonic) {
        JMenuItem menuItem = new JMenuItem(text, icon);
        menuItem.setMnemonic(mnemonic);
        return menuItem;
    }

    protected JMenuItem createMenuItem(String text, Icon icon, char mnemonic, KeyStroke key) {
        JMenuItem menuItem = createMenuItem(text, icon, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }

    protected JRadioButtonMenuItem createRadioButtonMenuItem(String text, boolean selected) {
        return new JRadioButtonMenuItem(text, selected);
    }

    protected JCheckBoxMenuItem createCheckBoxMenuItem(String text, boolean selected) {
        return new JCheckBoxMenuItem(text, selected);
    }


    // Subclass will override the following methods ***************************

    /**
     * Checks and answers whether the quit action has been moved to an
     * operating system specific menu, e.g. the OS X application menu.
     *
     * @return true if the quit action is in an OS-specific menu
     */
    protected boolean isQuitInOSMenu() {
        return false;
    }


    /**
     * Checks and answers whether the about action has been moved to an
     * operating system specific menu, e.g. the OS X application menu.
     *
     * @return true if the about action is in an OS-specific menu
     */
    protected boolean isAboutInOSMenu() {
        return false;
    }

    class MyFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }

            String filename = file.getName();
            return filename.endsWith(".xls");
        }

        public String getDescription() {
            return "*.xls";
        }
    }
}

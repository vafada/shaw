package com.shaw.ui;

import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.windows.ExtWindowsLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.clearlook.ClearLookManager;

import com.shaw.Utility;
import com.shaw.ui.admin.AdminPanel;
import com.shaw.ui.partsinput.PartsInputPanel;
import com.shaw.ui.sales.SalesPanel;
import com.shaw.ui.report.InventoryPanel;
import com.shaw.db.HSQLConnection;
import com.shaw.db.ShawDbConnectionException;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.File;

public class MainFrame extends JFrame {
    private final String SALES_PANEL = "Sales Panel";
    private final String ORDER_PANEL = "Order Panel";
    private final String INVENTORY_PANEL = "Inventory Panel";
    private final String ADMIN_PANEL = "Admin Panel";

    /**
     * Describes optional settings of the JGoodies Looks
     */
    private final Settings settings;
    /**
     * The card panel
     */
    private JPanel cardPanel;
    /**
     * Main panels
     */
    private SalesPanel salesPanel;
    private PartsInputPanel purchasingPanel;
    private InventoryPanel inventoryPanel;
    private AdminPanel adminPanel;

    /**
     * Constructs a <code>DemoFrame</code>, configures the UI,
     * and builds the content.
     */
    protected MainFrame(Settings settings) {
        this.settings = settings;
        try {
            initDatabase();
        } catch (ShawDbConnectionException dbe) {
            dbe.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    dbe.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        configureUI();
        build();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                HSQLConnection.getInstance().close();
                System.exit(0);
            }
        });
    }

    private void initDatabase() throws ShawDbConnectionException {
        HSQLConnection.getInstance().initialize();
    }

    public static void main(String[] args) {
        MainFrame instance = new MainFrame(createSettings());

        Dimension screenSize = instance.getToolkit().getScreenSize();

        int height = (int) (screenSize.getHeight() * .80);
        int width = (int) (screenSize.getWidth() * .80);

        instance.setSize(width, height);
        instance.locateOnScreen(instance);
        instance.setVisible(true);
    }

    private static Settings createSettings() {
        Settings settings = Settings.createDefault();
        // Configure the settings here.
        return settings;
    }


    /**
     * Configures the user interface; requests Swing settings and
     * jGoodies Looks options from the launcher.
     */
    private void configureUI() {
        Options.setDefaultIconSize(new Dimension(18, 18));

        // Set font options
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, settings.isUseSystemFonts());
        Options.setGlobalFontSizeHints(settings.getFontSizeHints());
        Options.setUseNarrowButtons(settings.isUseNarrowButtons());

        // Global options
        Options.setTabIconsEnabled(settings.isTabIconsEnabled());
        ClearLookManager.setMode(settings.getClearLookMode());
        ClearLookManager.setPolicy(settings.getClearLookPolicyName());
        UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY, settings.isPopupDropShadowEnabled());

        // Swing Settings
        LookAndFeel selectedLaf = settings.getSelectedLookAndFeel();
        if (selectedLaf instanceof PlasticLookAndFeel) {
            PlasticLookAndFeel.setMyCurrentTheme(settings.getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(settings.getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(settings.isPlasticHighContrastFocusEnabled());
        } else if (selectedLaf.getClass() == MetalLookAndFeel.class) {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        }

        // Work around caching in MetalRadioButtonUI
        JRadioButton radio = new JRadioButton();
        radio.getUI().uninstallUI(radio);
        JCheckBox checkBox = new JCheckBox();
        checkBox.getUI().uninstallUI(checkBox);

        try {
            UIManager.setLookAndFeel(selectedLaf);
        } catch (Exception e) {
            System.out.println("Can't change L&F: " + e);
        }

    }

    private void build() {
        getContentPane().setLayout(new BorderLayout());
        setTitle("Shaw Merchandising");

        setJMenuBar(new MenuBuilder().buildMenuBar(settings));
        setIconImage(Utility.readImageIcon("icon.png").getImage());
        getContentPane().add(buildToolBar(), BorderLayout.NORTH);
        getContentPane().add(buildContentPane(), BorderLayout.CENTER);
    }

    /**
     * Builds and answers the content.
     */
    private JComponent buildContentPane() {
        cardPanel = new JPanel(new CardLayout());

        salesPanel = new SalesPanel(this, settings);
        cardPanel.add(salesPanel, SALES_PANEL);
        purchasingPanel = new PartsInputPanel(this);
        cardPanel.add(purchasingPanel, ORDER_PANEL);
        inventoryPanel = new InventoryPanel();
        cardPanel.add(inventoryPanel, INVENTORY_PANEL);
        adminPanel = new AdminPanel();
        cardPanel.add(adminPanel, ADMIN_PANEL);
        return cardPanel;
    }

    // Tool Bar *************************************************************
    /**
     * Builds, configures and returns the toolbar. Requests
     * HeaderStyle, look-specific BorderStyles, and Plastic 3D Hint
     * from Launcher.
     */
    private Component buildToolBar() {
        JPanel toolbarPanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        // Swing
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, settings.getToolBarHeaderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, settings.getToolBarPlasticBorderStyle());        
        toolBar.putClientProperty(ExtWindowsLookAndFeel.BORDER_STYLE_KEY, settings.getToolBarWindowsBorderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, settings.getToolBar3DHint());

        AbstractButton button = Utility.createToolBarButton("sales.gif");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, SALES_PANEL);
            }
        });

        toolBar.add(button);

        button = Utility.createToolBarButton("order.gif");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, ORDER_PANEL);
            }
        });
        toolBar.add(button);
        toolBar.addSeparator();

        button = Utility.createToolBarButton("report.gif");
        toolBar.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inventoryPanel.generateReports();
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, INVENTORY_PANEL);
            }
        });

        toolBar.addSeparator();

        button = Utility.createToolBarButton("admin.gif");
        toolBar.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adminPanel.refreshData();
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                cl.show(cardPanel, ADMIN_PANEL);
            }
        });

        toolbarPanel.add(toolBar, BorderLayout.CENTER);

        toolBar = new JToolBar();
        // Swing
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, settings.getToolBarHeaderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, settings.getToolBarPlasticBorderStyle());
        toolBar.putClientProperty(ExtWindowsLookAndFeel.BORDER_STYLE_KEY, settings.getToolBarWindowsBorderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, settings.getToolBar3DHint());
        toolBar.setFloatable(false);
        JLabel logo = new JLabel(Utility.readImageIcon("logo.png"));
        toolBar.add(logo);
        toolbarPanel.add(toolBar, BorderLayout.EAST);
        //return toolBar;
        return toolbarPanel;
    }

    /**
     * Locates the given component on the screen's center.
     */
    protected void locateOnScreen(Component component) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation((screenSize.width - paneSize.width) / 2,
                (screenSize.height - paneSize.height) / 2);
    }    
}

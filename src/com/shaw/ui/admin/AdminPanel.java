package com.shaw.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private ProductTab productTab;
    public AdminPanel() {
        initComponents();
        buildUI();
    }

    private void initComponents() {
        productTab = new ProductTab();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Product", productTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        productTab.buildData();
    }
}

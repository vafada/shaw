package com.shaw.ui;

import com.shaw.Utility;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private JLabel image;
    private JLabel text;

    private Icon errorIcon;
    private Icon successIcon;

    public InfoPanel() {
        super(new BorderLayout());
        initComponents();
        buildUI();
    }

    public InfoPanel(boolean small) {
        super(new BorderLayout());
        if (small == false) {
            initComponents();
            buildUI();
        } else {
            initSmallComponents();
            buildSmallUI();
        }
    }

    private void initSmallComponents() {
        errorIcon = Utility.readImageIcon("failedsmall.gif");
        successIcon = Utility.readImageIcon("check.png");
        image = new JLabel();
        text = new JLabel("Error");
    }

    private void initComponents() {
        errorIcon = Utility.readImageIcon("failed.gif");
        successIcon = Utility.readImageIcon("check.png");
        image = new JLabel();
        text = new JLabel("Error");
    }

    private void buildSmallUI() {
        add(image, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);

        image.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        Font font = text.getFont();
        text.setFont(new Font(font.getName(), Font.BOLD, 10));
        text.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    }

    private void buildUI() {
        add(image, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);

        image.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = text.getFont();
        text.setFont(new Font(font.getName(), Font.BOLD, 14));
        text.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    }

    public void setErrorText(String text) {
        this.image.setIcon(errorIcon);
        this.text.setText(text);
    }

    public void setSuccessText(String text) {
        this.image.setIcon(successIcon);
        this.text.setText(text);
    }
}

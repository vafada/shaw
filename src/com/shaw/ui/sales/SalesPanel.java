package com.shaw.ui.sales;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.BorderStyle;
import com.jgoodies.plaf.windows.ExtWindowsLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;

import com.shaw.ShawException;
import com.shaw.Utility;
import com.shaw.manager.ProductManager;
import com.shaw.manager.SalesManager;
import com.shaw.ui.InfoPanel;
import com.shaw.ui.SimpleInternalFrame;
import com.shaw.ui.Settings;
import com.shaw.bean.ProductBean;
import com.shaw.bean.SalesPartBean;
import com.shaw.bean.SalesBean;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

public class SalesPanel extends JPanel {
    private static final int NEW = 1;
    private static final int EDIT = 2;

    private JPanel mainPanel;
    private JPanel bottomPanel;

    private JTextField invoiceNumber;
    private JTextField purchaseOrder;
    private JTextField restCertNo;
    private JTextField dateIssued;
    private JTextField placeIssued;
    private JTextField tin;
    private JTextField soldTo;
    private JTextField address;
    private JTextField date;
    private JTextField terms;
    private JTextField remarks;
    private JTextField receivedBy;

    private JTextField quantity;
    private JTextField partNumber;
    private JTextField description;
    private JTextField unitCost;
    private JTextField stock;
    private JTextField unitSale;
    private JTextField totalCost;
    private JTextField totalSale;

    private JButton addUpdate;
    private JButton newButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton dateImage;
    private JButton dateIssuedImage;
    private JButton process;
    private AbstractButton cancelButton;

    private JTable table;
    private SalesTableModel tableModel;

    private InfoPanel infoPanel;

    private int ACTION = NEW;

    private int editRowIndex;
    private int salesId;
    private JFrame parent;
    private Settings settings;

    private SalesManager manager;

    public SalesPanel(JFrame parent, Settings settings) {
        this.settings = settings;
        this.parent = parent;
        this.manager = new SalesManager();
        initComponents();
        buildUI();
    }

    private void initComponents() {
        invoiceNumber = new JTextField(60);
        purchaseOrder = new JTextField(60);
        restCertNo = new JTextField(60);
        dateIssued = new JTextField(60);
        placeIssued = new JTextField(60);
        tin = new JTextField(60);
        soldTo = new JTextField(60);
        address = new JTextField(60);
        date = new JTextField(60);
        terms = new JTextField(60);
        remarks = new JTextField(60);
        receivedBy = new JTextField(60);

        quantity = new JTextField(100);
        partNumber = new JTextField(100);
        description = new JTextField(100);
        unitCost = new JTextField(100);
        unitSale = new JTextField(100);
        totalCost = new JTextField(100);
        totalSale = new JTextField(100);
        stock = new JTextField(100);

        infoPanel = new InfoPanel(true);

        tableModel = new SalesTableModel(totalCost, totalSale);
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());

        //JPanel toolbarPanel = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        // Swing
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, settings.getToolBarHeaderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, BorderStyle.EMPTY);
        toolBar.putClientProperty(ExtWindowsLookAndFeel.BORDER_STYLE_KEY, settings.getToolBarWindowsBorderStyle());
        toolBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, settings.getToolBar3DHint());

        toolBar.add(Box.createGlue());

        AbstractButton button = Utility.createToolBarButton("new1.gif");
        button.setToolTipText("New");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setViewMode(true);
                resetFields();
            }
        });

        toolBar.add(button);

        button = Utility.createToolBarButton("open1.gif");
        button.setToolTipText("Open");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BrowseSalesDialog browseDialog = new BrowseSalesDialog(parent);
                salesId = browseDialog.getSelectedId();
                if (salesId != -1) {
                    try {
                        SalesBean sales = manager.getSalesReport(salesId);
                        displaySalesReport(sales);
                        cancelButton.setEnabled(true);
                    } catch (ShawException se) {
                        se.printStackTrace();
                        showErrorPanelWithoutClear("Error opening sales report");
                    }
                }
                browseDialog = null;
            }
        });

        toolBar.add(button);

        cancelButton = Utility.createToolBarButton("cancel1.gif");
        cancelButton.setToolTipText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(SalesPanel.this, "Cancel Sales?", "Cancel", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
                if (salesId != -1) {
                    try {
                        manager.cancelSales(salesId);
                        setViewMode(true);
                        resetFields();
                    } catch (ShawException se) {
                        se.printStackTrace();
                        showErrorPanelWithoutClear("Error canceling sales");
                    }
                }
            }
        });
        cancelButton.setEnabled(false);
        toolBar.add(cancelButton);

        toolBar.add(Box.createGlue());

        mainPanel.add(toolBar, BorderLayout.NORTH);

        SimpleInternalFrame salesFrame = new SimpleInternalFrame("Sales");

        FormLayout layout = new FormLayout("left:max(40dlu;pref), 5dlu, 100dlu, 10dlu, left:max(40dlu;pref), 5dlu, 100dlu, 10dlu, left:max(40dlu;pref), 5dlu, 100dlu",
                "p, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, fill:100dlu:grow, 5dlu, p, 5dlu, p");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        int y = 2;

        builder.addSeparator("Details", cc.xyw(1, y, 11));

        y += 2;

        builder.addLabel("Date:", cc.xy(1, y));
        JPanel datePanel = new JPanel(new BorderLayout());
        dateImage = new JButton(Utility.readImageIcon("calendar.gif"));
        dateImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(SalesPanel.this.parent, true);
                JCalendar calendar = new JCalendar();
                calendar.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("calendar")) {
                            Calendar calendar = (Calendar) evt.getNewValue();
                            date.setText(Utility.formatDate(calendar.getTime()));
                        }
                    }
                });
                dialog.getContentPane().add(calendar);
                dialog.pack();
                Dimension paneSize = dialog.getSize();
                Dimension screenSize = dialog.getToolkit().getScreenSize();
                dialog.setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
                dialog.setVisible(true);

            }
        });

        datePanel.add(date, BorderLayout.CENTER);
        date.setText(Utility.formatDate(new Date()));
        date.setEditable(false);
        date.setFocusable(false);
        datePanel.add(dateImage, BorderLayout.EAST);

        builder.add(datePanel, cc.xy(3, y));

        builder.addLabel("Terms:", cc.xy(5, y));
        builder.add(terms, cc.xy(7, y));
        builder.addLabel("Rest cert no.:", cc.xy(9, y));
        builder.add(restCertNo, cc.xy(11, y));

        y += 2;

        builder.addLabel("Invoice no.:", cc.xy(1, y));
        builder.add(invoiceNumber, cc.xy(3, y));
        builder.addLabel("Purchase order no.:", cc.xy(5, y));
        builder.add(purchaseOrder, cc.xy(7, y));
        builder.addLabel("Place issued:", cc.xy(9, y));
        builder.add(placeIssued, cc.xy(11, y));

        y += 2;

        builder.addLabel("Sold to:", cc.xy(1, y));
        builder.add(soldTo, cc.xy(3, y));
        builder.addLabel("Received by:", cc.xy(5, y));
        builder.add(receivedBy, cc.xy(7, y));
        builder.addLabel("Remarks:", cc.xy(9, y));
        builder.add(remarks, cc.xy(11, y));


        y += 2;

        builder.addLabel("Address:", cc.xy(1, y));
        builder.add(address, cc.xy(3, y));
        builder.addLabel("TIN:", cc.xy(5, y));
        builder.add(tin, cc.xy(7, y));

        builder.addLabel("Date Issued:", cc.xy(9, y));
        JPanel dateIssuedPanel = new JPanel(new BorderLayout());
        dateIssuedImage = new JButton(Utility.readImageIcon("calendar.gif"));
        dateIssuedImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(SalesPanel.this.parent, true);
                JCalendar calendar = new JCalendar();
                calendar.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("calendar")) {
                            Calendar calendar = (Calendar) evt.getNewValue();
                            dateIssued.setText(Utility.formatDate(calendar.getTime()));
                        }
                    }
                });
                dialog.getContentPane().add(calendar);
                dialog.pack();
                Dimension paneSize = dialog.getSize();
                Dimension screenSize = dialog.getToolkit().getScreenSize();
                dialog.setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
                dialog.setVisible(true);

            }
        });

        dateIssuedPanel.add(dateIssued, BorderLayout.CENTER);
        dateIssued.setText(Utility.formatDate(new Date()));
        dateIssued.setEditable(false);
        dateIssued.setFocusable(false);
        dateIssuedPanel.add(dateIssuedImage, BorderLayout.EAST);


        builder.add(dateIssuedPanel, cc.xy(11, y));

        y += 2;

        builder.addSeparator("Products", cc.xyw(1, y, 11));

        y += 2;

        builder.addLabel("Quantity:", cc.xy(1, y));
        builder.add(quantity, cc.xy(3, y));
        builder.addLabel("Unit cost:", cc.xy(5, y));
        builder.add(unitCost, cc.xy(7, y));
        unitCost.setEditable(false);
        unitCost.setFocusable(false);
        builder.addLabel("Remaining Stock:", cc.xy(9, y));
        builder.add(stock, cc.xy(11, y));
        stock.setEditable(false);
        stock.setFocusable(false);

        y += 2;

        builder.addLabel("Part Number:", cc.xy(1, y));
        builder.add(partNumber, cc.xy(3, y));
        partNumber.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) { //enter was pressed
                    fillFields(partNumber.getText().trim().toUpperCase());
                }
            }
        });

        partNumber.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                fillFields(partNumber.getText().trim().toUpperCase());
            }
        });

        builder.addLabel("Unit sales:", cc.xy(5, y));
        builder.add(unitSale, cc.xy(7, y));

        y += 2;

        builder.addLabel("Description:", cc.xy(1, y));
        builder.add(description, cc.xyw(3, y, 5));
        description.setEditable(false);
        description.setFocusable(false);

        y += 2;

        newButton = new JButton("New");
        newButton.setToolTipText("New (CTRL-N)");
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ACTION = NEW;
                addUpdate.setText("Add");
                clearFieldsAll();
                removeInfoPanel();
            }
        });

        editButton = new JButton("Edit");
        editButton.setToolTipText("Edit (CTRL-E)");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = table.getSelectedRow();
                if (rowIndex != -1) {
                    ACTION = EDIT;

                    addUpdate.setText("Update");

                    SalesPartBean parts = tableModel.getParts(rowIndex);

                    partNumber.setText(parts.getPartNumber());
                    partNumber.setEditable(false);
                    description.setText(parts.getDescription());
                    quantity.setText(String.valueOf(parts.getQuantity()));
                    unitCost.setText(String.valueOf(parts.getUnitCost()));
                    unitSale.setText(String.valueOf(parts.getUnitSales()));

                    editRowIndex = rowIndex;

                    removeInfoPanel();

                    partNumber.requestFocus();
                    quantity.requestFocus();
                }
            }
        });

        addUpdate = new JButton("Add");
        addUpdate.setToolTipText("Add / Update (CTRL-I)");
        addUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateData()) {
                    if (ACTION == NEW) {
                        //check if product already exist in model
                        int index = tableModel.indexOf(partNumber.getText().trim());
                        if (index >= 0) { //product is already in the table
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("Part number ").append(partNumber.getText().trim().toUpperCase()).append(" already exist in the list.\n");
                            buffer.append("Adding this would replace the old data.\n");
                            buffer.append("Replace old data?");

                            int result = JOptionPane.showConfirmDialog(SalesPanel.this, buffer.toString(), "Replace existing", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                try {
                                    tableModel.updateParts(index, generateSalesPartBean());
                                } catch (ShawException se) {
                                    showErrorPanelWithoutClear(se.getMessage());
                                    return;
                                }
                                clearFieldsAll();
                                removeInfoPanel();
                            } else if (result == JOptionPane.NO_OPTION) {
                                return;
                            }
                        } else {
                            try {
                                tableModel.addParts(generateSalesPartBean());
                            } catch (ShawException se) {
                                showErrorPanelWithoutClear(se.getMessage());
                                return;
                            }
                            clearFieldsAll();
                            removeInfoPanel();
                        }
                    } else {
                        ACTION = NEW;
                        try {
                            tableModel.updateParts(editRowIndex, generateSalesPartBean());
                        } catch (ShawException se) {
                            showErrorPanelWithoutClear(se.getMessage());
                            return;
                        }
                        partNumber.setEditable(true);
                        addUpdate.setText("Add");
                        clearFieldsAll();
                        removeInfoPanel();
                    }
                    quantity.requestFocus();
                }
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.setToolTipText("Delete (CTRL-D)");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ACTION == EDIT) {
                    showErrorPanelWithoutClear("Cannot delete while editing.");
                    return;
                }

                int rowIndex = table.getSelectedRow();
                if (rowIndex != -1) {
                    tableModel.removeParts(rowIndex);
                }
            }
        });

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();
        bbuilder.addGriddedButtons(new JButton[]{newButton, editButton, addUpdate, deleteButton});

        builder.add(bbuilder.getPanel(), cc.xyw(1, y, 7));

        y += 2;

        builder.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), cc.xyw(1, y, 11));

        y += 2;

        builder.addLabel("Total cost:", cc.xy(5, y));
        builder.add(totalCost, cc.xy(7, y));
        totalCost.setText("0.0");
        totalCost.setEditable(false);
        totalCost.setHorizontalAlignment(JTextField.RIGHT);
        builder.addLabel("Total sales:", cc.xy(9, y));
        builder.add(totalSale, cc.xy(11, y));
        totalSale.setText("0.0");
        totalSale.setEditable(false);
        totalSale.setHorizontalAlignment(JTextField.RIGHT);

        y += 2;

        bbuilder = new ButtonBarBuilder();
        process = new JButton("Process");
        process.setToolTipText("Process (CTRL-P)");
        bbuilder.addGriddedButtons(new JButton[]{process});
        process.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    SalesBean form = new SalesBean();
                    form.setInvoiceNumber(invoiceNumber.getText().trim());
                    form.setPurchaseOrderNumber(purchaseOrder.getText().trim());
                    form.setRestCertNumber(restCertNo.getText().trim());

                    try {
                        form.setDateIssued(Utility.parseDate(dateIssued.getText().trim()));
                    } catch (ParseException pe) {
                        showErrorPanelWithoutClear("Cannot parse date issued");
                        return;
                    }

                    form.setPlaceIssued(placeIssued.getText().trim());
                    form.setTin(tin.getText().trim());
                    form.setSoldTo(soldTo.getText().trim());
                    try {
                        form.setDate(Utility.parseDate(date.getText().trim()));
                    } catch (ParseException pe) {
                        showErrorPanelWithoutClear("Cannot parse date");
                        return;
                    }
                    form.setAddress(address.getText().trim());
                    form.setTerms(terms.getText().trim());
                    form.setRemarks(remarks.getText().trim());
                    form.setReceivedBy(receivedBy.getText().trim());

                    form.setTotalCost(tableModel.getCost());
                    form.setTotalSales(tableModel.getSales());

                    form.setPartsList(tableModel.getParts());


                    try {
                        manager.addSales(form);
                        resetFields();
                        showSuccessPanel("Successfully processed data.");
                    } catch (ShawException se) {
                        se.printStackTrace();
                        showErrorPanelWithoutClear(se.getMessage());
                    }
                }
            }
        });

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bbuilder.getPanel(), BorderLayout.WEST);

        builder.add(bottomPanel, cc.xyw(1, y, 11));


        salesFrame.setContent(mainPanel);
        mainPanel.add(builder.getPanel(), BorderLayout.CENTER);
        add(salesFrame, BorderLayout.CENTER);

        final String PROCESS = "process";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), PROCESS);
        this.getActionMap().put(PROCESS, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                process.doClick();
                invoiceNumber.requestFocus();
            }
        });
        final String NEW = "new";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), NEW);
        this.getActionMap().put(NEW, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                newButton.doClick();
            }
        });
        final String EDIT = "edit";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), EDIT);
        this.getActionMap().put(EDIT, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                editButton.doClick();
            }
        });
        final String ADD = "add";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK), ADD);
        this.getActionMap().put(ADD, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                addUpdate.doClick();
            }
        });
        final String DELETE = "delete";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK), DELETE);
        this.getActionMap().put(DELETE, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                deleteButton.doClick();
            }
        });


        MyOwnFocusTraversalPolicy newPolicy = new MyOwnFocusTraversalPolicy();
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(newPolicy);

    }

    private void showErrorPanel(String error) {
        infoPanel.setErrorText(error);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.updateUI();

        clearFields();
    }

    private void showSuccessPanel(String text) {
        infoPanel.setSuccessText(text);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.updateUI();

        clearFields();
    }

    private void showErrorPanelWithoutClear(String error) {
        infoPanel.setErrorText(error);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.updateUI();
    }

    private void removeInfoPanel() {
        bottomPanel.remove(infoPanel);
        bottomPanel.updateUI();
    }

    private void clearFieldsAll() {
        partNumber.setText("");
        unitSale.setText("");
        clearFields();
        partNumber.setEditable(true);
    }

    private void clearFields() {
        stock.setText("");
        description.setText("");
        quantity.setText("");
        unitCost.setText("");
    }

    private boolean fillFields(String partNumber) {
        if ("".equalsIgnoreCase(partNumber)) {
            removeInfoPanel();
            return false;
        }

        ProductManager manager = new ProductManager();
        try {
            ProductBean product = manager.getProduct(partNumber);
            if (product != null) {
                description.setText(product.getDescription());
                unitCost.setText(String.valueOf(product.getUnitPrice()));
                stock.setText(String.valueOf(product.getQuantity()));
                removeInfoPanel();
                return true;
            } else {
                showErrorPanel("Invalid part number");
            }
        } catch (ShawException se) {
            showErrorPanel("Error getting product");
        }
        return false;
    }

    private boolean validateForm() {
        int size = tableModel.getParts().size();
        if (size == 0) {
            showErrorPanelWithoutClear("No products");
            return false;
        }
        try {
            boolean unique = manager.isUniqueInvoiceNumber(invoiceNumber.getText().trim());
            if (!unique) {
                showErrorPanelWithoutClear("Duplicate Invoice number");
                return false;
            }
        } catch (ShawException se) {
            showErrorPanelWithoutClear("Error checking for invoice number");
            return false;
        }

        /* if (invoiceNumber.getText().trim().length() == 0) {
             showErrorPanelWithoutClear("No invoice number");
             return false;
         }
 */
        return true;
    }

    private boolean validateData() {
        ProductManager manager = new ProductManager();

        try {
            ProductBean product = manager.getProduct(partNumber.getText().trim());

            if (product == null) {
                showErrorPanelWithoutClear("Invalid part number");
                return false;
            }
            String quantity = this.quantity.getText().trim();
            if (quantity.length() == 0) {
                showErrorPanelWithoutClear("Invalid quantity");
                return false;
            } else {
                try {
                    Integer.parseInt(quantity);
                } catch (NumberFormatException nfe) {
                    showErrorPanelWithoutClear("Invalid quantity");
                    return false;
                }
            }

            String unitSales = this.unitSale.getText().trim();
            if (unitSales.length() == 0) {
                showErrorPanelWithoutClear("Invalid unit sales");
                return false;
            } else {
                try {
                    Double.parseDouble(unitSales);
                } catch (NumberFormatException nfe) {
                    showErrorPanelWithoutClear("Invalid unit sales");
                    return false;
                }
            }
        } catch (ShawException se) {
            showErrorPanel("Error getting product");
        }
        return true;
    }

    private SalesPartBean generateSalesPartBean() throws ShawException {
        SalesPartBean parts = new SalesPartBean();
        parts.setPartNumber(partNumber.getText().trim().toUpperCase());
        int _quantity = Integer.parseInt(quantity.getText().trim());
        int inStock = Integer.parseInt(stock.getText().trim());

        if (_quantity > inStock) {
            throw new ShawException("Quantity exceeds stock");
        }

        parts.setQuantity(_quantity);
        parts.setRemaining(inStock - _quantity);
        parts.setDescription(description.getText().trim());
        parts.setUnitCost(Double.parseDouble(unitCost.getText().trim()));
        parts.setUnitSales(Double.parseDouble(unitSale.getText().trim()));


        return parts;
    }

    private void resetFields() {
        salesId = -1;
        invoiceNumber.setText("");
        purchaseOrder.setText("");
        restCertNo.setText("");
        dateIssued.setText(Utility.formatDate(new Date()));
        placeIssued.setText("");
        tin.setText("");
        soldTo.setText("");
        address.setText("");
        date.setText(Utility.formatDate(new Date()));
        terms.setText("");
        remarks.setText("");
        receivedBy.setText("");


        quantity.setText("");
        partNumber.setText("");
        description.setText("");
        stock.setText("");
        unitCost.setText("");
        unitSale.setText("");
        totalCost.setText("0.0");
        totalSale.setText("0.0");

        ACTION = NEW;
        addUpdate.setText("Add");
        cancelButton.setEnabled(false);
        tableModel.resetData();
    }

    private void displaySalesReport(SalesBean sales) {
        setViewMode(false);

        invoiceNumber.setText(sales.getInvoiceNumber());
        purchaseOrder.setText(sales.getPurchaseOrderNumber());
        restCertNo.setText(sales.getRestCertNumber());
        dateIssued.setText(Utility.formatDate(sales.getDateIssued()));
        placeIssued.setText(sales.getPlaceIssued());
        tin.setText(sales.getTin());
        soldTo.setText(sales.getSoldTo());
        date.setText(Utility.formatDate(sales.getDate()));
        address.setText(sales.getAddress());
        terms.setText(sales.getTerms());
        remarks.setText(sales.getRemarks());
        receivedBy.setText(sales.getReceivedBy());

        partNumber.setText("");
        quantity.setText("");
        unitSale.setText("");

        totalCost.setText(Utility.formatDouble(sales.getTotalCost()));
        totalSale.setText(Utility.formatDouble(sales.getTotalSales()));

        tableModel.setParts(sales.getPartsList());
    }

    private void setViewMode(boolean editable) {
        invoiceNumber.setEditable(editable);
        purchaseOrder.setEditable(editable);
        restCertNo.setEditable(editable);
        placeIssued.setEditable(editable);
        tin.setEditable(editable);
        soldTo.setEditable(editable);
        dateImage.setEnabled(editable);
        dateIssuedImage.setEnabled(editable);
        address.setEditable(editable);
        terms.setEditable(editable);
        remarks.setEditable(editable);
        receivedBy.setEditable(editable);

        partNumber.setEditable(editable);
        quantity.setEditable(editable);
        unitSale.setEditable(editable);

        newButton.setEnabled(editable);
        addUpdate.setEnabled(editable);
        editButton.setEnabled(editable);
        deleteButton.setEnabled(editable);

        process.setEnabled(editable);


    }

    public class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy {

        public Component getComponentAfter(Container focusCycleRoot,
                                           Component aComponent) {
            if (aComponent.equals(invoiceNumber)) {
                return soldTo;
            } else if (aComponent.equals(soldTo)) {
                return address;
            } else if (aComponent.equals(address)) {
                return quantity;
            } else if (aComponent.equals(quantity)) {
                return partNumber;
            } else if (aComponent.equals(partNumber)) {
                return unitSale;
            } else if (aComponent.equals(unitSale)) {
                return terms;
            } else if (aComponent.equals(terms)) {
                return purchaseOrder;
            } else if (aComponent.equals(purchaseOrder)) {
                return receivedBy;
            } else if (aComponent.equals(receivedBy)) {
                return tin;
            } else if (aComponent.equals(tin)) {
                return restCertNo;
            } else if (aComponent.equals(restCertNo)) {
                return placeIssued;
            } else if (aComponent.equals(placeIssued)) {
                return remarks;
            } else if (aComponent.equals(remarks)) {
                return newButton;
            } else if (aComponent.equals(newButton)) {
                return editButton;
            } else if (aComponent.equals(editButton)) {
                return addUpdate;
            } else if (aComponent.equals(addUpdate)) {
                return deleteButton;
            } else if (aComponent.equals(deleteButton)) {
                return process;
            }
            return invoiceNumber;
        }

        public Component getComponentBefore(Container focusCycleRoot,
                                            Component aComponent) {
            if (aComponent.equals(process)) {
                return deleteButton;
            } else if (aComponent.equals(deleteButton)) {
                return addUpdate;
            } else if (aComponent.equals(addUpdate)) {
                return editButton;
            } else if (aComponent.equals(editButton)) {
                return newButton;
            } else if (aComponent.equals(newButton)) {
                return remarks;
            } else if (aComponent.equals(unitSale)) {
                return partNumber;
            } else if (aComponent.equals(partNumber)) {
                return quantity;
            } else if (aComponent.equals(quantity)) {
                return address;
            } else if (aComponent.equals(remarks)) {
                return placeIssued;
            } else if (aComponent.equals(placeIssued)) {
                return restCertNo;
            } else if (aComponent.equals(restCertNo)) {
                return tin;
            } else if (aComponent.equals(tin)) {
                return receivedBy;
            } else if (aComponent.equals(receivedBy)) {
                return purchaseOrder;
            } else if (aComponent.equals(purchaseOrder)) {
                return terms;
            } else if (aComponent.equals(terms)) {
                return unitSale;
            } else if (aComponent.equals(address)) {
                return soldTo;
            } else if (aComponent.equals(soldTo)) {
                return invoiceNumber;
            }
            return invoiceNumber;
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            return invoiceNumber;
        }

        public Component getLastComponent(Container focusCycleRoot) {
            return process;
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            return invoiceNumber;
        }
    }
}

package com.shaw.ui.partsinput;

import com.shaw.ShawException;
import com.shaw.Utility;
import com.shaw.bean.ProductBean;
import com.shaw.bean.PartsBean;
import com.shaw.bean.PartsInputBean;
import com.shaw.manager.ProductManager;
import com.shaw.manager.PartsInputManager;
import com.shaw.ui.SimpleInternalFrame;
import com.shaw.ui.InfoPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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


public class PartsInputPanel extends JPanel {
    private static final int NEW = 1;
    private static final int EDIT = 2;

    private JPanel mainPanel;
    private JPanel bottomPanel;

    private JTextField customerNumber;
    private JTextField orderNumber;
    private JTextField allocationNumber;
    private JTextField paymentTerms;
    private JTextField discount;
    private JTextField surcharge;
    private JTextField date;
    private JTextField invoiceNumber;

    private JTextField partNumber;
    private JTextField description;
    private JTextField quantity;
    private JTextField unitPrice;
    private JTextField extendedAmount;

    private JTable table;
    private PartsInputTableModel tableModel;

    private InfoPanel infoPanel;

    private JButton addUpdate;

    private int ACTION = NEW;

    private int editRowIndex;
    private JFrame parent;

    public PartsInputPanel(JFrame parent) {
        this.parent = parent;
        initComponents();
        buildUI();
    }

    private void initComponents() {
        customerNumber = new JTextField(30);
        orderNumber = new JTextField(30);
        allocationNumber = new JTextField(30);
        paymentTerms = new JTextField(30);
        discount = new JTextField(30);
        surcharge = new JTextField(30);
        date = new JTextField(30);
        invoiceNumber = new JTextField(30);

        partNumber = new JTextField(30);
        description = new JTextField(30);
        quantity = new JTextField(30);
        unitPrice = new JTextField(30);
        extendedAmount = new JTextField(30);


        infoPanel = new InfoPanel(true);

        tableModel = new PartsInputTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        SimpleInternalFrame salesFrame = new SimpleInternalFrame("Parts Input");

        FormLayout layout = new FormLayout("left:max(40dlu;pref), 5dlu, left:max(150dlu;min), 15dlu, left:max(40dlu;pref), 5dlu, left:max(150dlu;min)",
                "p, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, fill:100dlu:grow, 5dlu, p");
        //buttons
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        int y = 2;

        builder.addSeparator("Details", cc.xyw(1, y, 7));

        y += 2;

        builder.addLabel("Customer No.:", cc.xy(1, y));
        builder.add(customerNumber, cc.xy(3, y));
        builder.addLabel("Order No.:", cc.xy(5, y));
        builder.add(orderNumber, cc.xy(7, y));

        y += 2;

        builder.addLabel("Allocation No.:", cc.xy(1, y));
        builder.add(allocationNumber, cc.xy(3, y));
        builder.addLabel("Payment Terms:", cc.xy(5, y));
        builder.add(paymentTerms, cc.xy(7, y));

        y += 2;

        builder.addLabel("Discount:", cc.xy(1, y));
        builder.add(discount, cc.xy(3, y));
        builder.addLabel("Surcharge:", cc.xy(5, y));
        builder.add(surcharge, cc.xy(7, y));

        y += 2;

        builder.addLabel("Date:", cc.xy(1, y));

        JPanel datePanel = new JPanel(new BorderLayout());
        JButton dateImage = new JButton(Utility.readImageIcon("calendar.gif"));
        dateImage.setFocusable(false);
        dateImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(PartsInputPanel.this.parent, true);
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

        builder.addLabel("Invoice No.:", cc.xy(5, y));
        builder.add(invoiceNumber, cc.xy(7, y));

        y += 2;

        builder.addSeparator("Products", cc.xyw(1, y, 7));

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
        builder.addLabel("Quantity:", cc.xy(5, y));
        builder.add(quantity, cc.xy(7, y));
        quantity.setEditable(false);
        quantity.setDocument(new IntegerDocument());

        y += 2;

        builder.addLabel("Description:", cc.xy(1, y));
        builder.add(description, cc.xy(3, y));
        description.setEditable(false);
        description.setFocusable(false);
        builder.addLabel("Unit Price:", cc.xy(5, y));
        builder.add(unitPrice, cc.xy(7, y));
        unitPrice.setEditable(false);
        unitPrice.setDocument(new DoubleDocument());

        y += 2;

        builder.addLabel("Extended Amount:", cc.xy(1, y));
        builder.add(extendedAmount, cc.xy(3, y));
        extendedAmount.setEditable(false);
        extendedAmount.setFocusable(false);

        y += 2;

        final JButton newButton = new JButton("New");
        newButton.setToolTipText("New (CTRL-N)");
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ACTION = NEW;
                addUpdate.setText("Add");
                clearFieldsAll();
                removeInfoPanel();
            }
        });

        final JButton editButton = new JButton("Edit");
        editButton.setToolTipText("Edit (CTRL-E)");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = table.getSelectedRow();
                if (rowIndex != -1) {
                    ACTION = EDIT;

                    addUpdate.setText("Update");

                    PartsBean parts = tableModel.getParts(rowIndex);

                    partNumber.setText(parts.getPartNumber());
                    partNumber.setEditable(false);
                    description.setText(parts.getDescription());
                    quantity.setText(String.valueOf(parts.getQuantity()));
                    quantity.setEditable(true);
                    unitPrice.setText(String.valueOf(parts.getUnitPrice()));
                    unitPrice.setEditable(true);
                    extendedAmount.setText(String.valueOf(parts.getExtendedAmount()));

                    editRowIndex = rowIndex;

                    removeInfoPanel();
                }
            }
        });


        addUpdate = new JButton("Add");
        addUpdate.setToolTipText("Add / Update (CTRL-I)");
        addUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateData()) {
                    try {
                        if (ACTION == NEW) {
                            //check if product already exist in model
                            int index = tableModel.indexOf(partNumber.getText().trim());
                            if (index >= 0) { //product is already in the table
                                StringBuffer buffer = new StringBuffer();
                                buffer.append("Part number ").append(partNumber.getText().trim().toUpperCase()).append(" already exist in the list.\n");
                                buffer.append("Adding this would replace the old data.\n");
                                buffer.append("Replace old data?");

                                int result = JOptionPane.showConfirmDialog(PartsInputPanel.this, buffer.toString(), "Replace existing", JOptionPane.YES_NO_OPTION);
                                if (result == JOptionPane.YES_OPTION) {
                                    tableModel.updateParts(index, generatePartsInputBean());
                                    clearFieldsAll();
                                    removeInfoPanel();
                                } else if (result == JOptionPane.NO_OPTION) {
                                    return;
                                }
                            } else {
                                tableModel.addParts(generatePartsInputBean());
                                clearFieldsAll();
                                removeInfoPanel();
                            }
                        } else {
                            ACTION = NEW;
                            tableModel.updateParts(editRowIndex, generatePartsInputBean());
                            partNumber.setEditable(true);
                            addUpdate.setText("Add");
                            clearFieldsAll();
                            removeInfoPanel();
                        }
                    } catch (ParseException pe) {
                        showErrorPanelWithoutClear("Error parsing amount.");
                    }
                }
            }
        });

        final JButton deleteButton = new JButton("Delete");
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

        builder.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), cc.xyw(1, y, 7));

        y += 2;

        bbuilder = new ButtonBarBuilder();
        final JButton process = new JButton("Process");
        process.setToolTipText("Process (CTRL-P)");
        bbuilder.addGriddedButtons(new JButton[]{process});
        process.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    PartsInputBean form = new PartsInputBean();
                    form.setCustomerNumber(customerNumber.getText().trim());
                    form.setOrderNumber(orderNumber.getText().trim());
                    form.setAllocationNumber(allocationNumber.getText().trim());
                    form.setPaymentTerm(paymentTerms.getText().trim());
                    form.setDiscount(discount.getText().trim());
                    form.setSurcharge(surcharge.getText().trim());
                    try {
                        form.setDate(Utility.parseDate(date.getText().trim()));
                    } catch (ParseException pe) {
                        showErrorPanelWithoutClear("Cannot parse date");
                        return;
                    }
                    form.setInvoiceNumber(invoiceNumber.getText().trim());

                    form.setPartsList(tableModel.getParts());

                    PartsInputManager manager = new PartsInputManager();
                    try {
                        manager.addPartsInput(form);
                        resetFields();
                        showSuccessPanel("Successfully processed data.");
                        //JOptionPane.showMessageDialog(PartsInputPanel.this, "Successfully saved data.");
                    } catch (ShawException se) {
                        se.printStackTrace();
                        showErrorPanelWithoutClear("Error processing data.");
                        /*
                        JOptionPane.showMessageDialog(PartsInputPanel.this,
                                "Error saving data.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                                */
                    }
                }
            }
        });

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bbuilder.getPanel(), BorderLayout.WEST);

        //builder.add(bbuilder.getPanel(), cc.xyw(1, y, 7));
        builder.add(bottomPanel, cc.xyw(1, y, 7));

        salesFrame.setContent(mainPanel);
        mainPanel.add(builder.getPanel(), BorderLayout.CENTER);
        add(salesFrame, BorderLayout.CENTER);

        final String PROCESS = "process";
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), PROCESS);
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


    }

    private void resetFields() {
        ACTION = NEW;
        removeInfoPanel();
        customerNumber.setText("");
        orderNumber.setText("");
        allocationNumber.setText("");
        paymentTerms.setText("");
        discount.setText("");
        surcharge.setText("");
        date.setText(Utility.formatDate(new Date()));
        invoiceNumber.setText("");

        partNumber.setText("");
        partNumber.setEditable(true);
        quantity.setText("");
        quantity.setEditable(false);
        description.setText("");
        unitPrice.setText("");
        extendedAmount.setText("");

        tableModel.resetData();


    }

    private PartsBean generatePartsInputBean() throws ParseException {
        PartsBean parts = new PartsBean();
        parts.setPartNumber(partNumber.getText().trim().toUpperCase());
        parts.setQuantity(Integer.parseInt(quantity.getText().trim()));
        parts.setDescription(description.getText().trim());
        parts.setUnitPrice(Double.parseDouble(unitPrice.getText().trim()));
        parts.setExtendedAmount(Utility.parseDouble(extendedAmount.getText().trim()));

        return parts;
    }

    private boolean validateForm() {
        int size = tableModel.getParts().size();
        if (size == 0) {
            showErrorPanelWithoutClear("No products");
            return false;
        }
        /*if (invoiceNumber.getText().trim().length() == 0) {
            showErrorPanelWithoutClear("No invoice number");
            return false;
        }*/

        return true;
    }

    private boolean validateData() {
        ProductManager manager = new ProductManager();

        try {
            ProductBean product = manager.getProduct(partNumber.getText().trim());

            if (product == null) {
                showErrorPanel("Invalid part number");
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

            String unitPrice = this.unitPrice.getText().trim();
            if (unitPrice.length() == 0) {
                showErrorPanelWithoutClear("Invalid unit price");
                return false;
            } else {
                try {
                    Double.parseDouble(unitPrice);
                } catch (NumberFormatException nfe) {
                    showErrorPanelWithoutClear("Invalid unit price");
                    return false;
                }
            }
        } catch (ShawException se) {
            showErrorPanel("Error getting product");
        }
        return true;
    }

    private boolean fillFields(String partNumber) {
        if ("".equalsIgnoreCase(partNumber)) {
            quantity.setEditable(false);
            removeInfoPanel();
            return false;
        }

        ProductManager manager = new ProductManager();
        try {
            ProductBean product = manager.getProduct(partNumber);
            if (product != null) {
                description.setText(product.getDescription());
                unitPrice.setText(String.valueOf(product.getUnitPrice()));
                quantity.setEditable(true);
                unitPrice.setEditable(true);
                removeInfoPanel();
                return true;
            } else {
                quantity.setEditable(false);
                unitPrice.setEditable(false);
                showErrorPanel("Invalid part number");
            }
        } catch (ShawException se) {
            quantity.setEditable(false);
            unitPrice.setEditable(false);
            showErrorPanel("Error getting product");
        }
        return false;
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
        quantity.setEditable(false);
        unitPrice.setEditable(false);
        clearFields();
        partNumber.setEditable(true);
    }

    private void clearFields() {
        description.setText("");
        unitPrice.setText("");
        quantity.setText("");
        extendedAmount.setText("");
    }

    class IntegerDocument extends PlainDocument {
        public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
            if (string == null) {
                return;
            } else {
                String newValue;
                int length = getLength();
                if (length == 0) {
                    newValue = string;
                } else {
                    String currentContent = getText(0, length);
                    StringBuffer currentBuffer = new StringBuffer(currentContent);
                    currentBuffer.insert(offset, string);
                    newValue = currentBuffer.toString();
                }
                try {
                    int value = Integer.parseInt(newValue);
                    super.insertString(offset, string, attributes);
                    if (unitPrice.getText().trim().length() > 0 && "".equalsIgnoreCase(newValue) == false) {
                        double total = value * Double.parseDouble(unitPrice.getText().trim());
                        extendedAmount.setText(Utility.formatDouble(total));
                    }
                } catch (NumberFormatException exception) {
                    Toolkit.getDefaultToolkit().beep();
                    extendedAmount.setText("");
                }
            }
        }
    }

    class DoubleDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) {
                if (Character.isDigit(source[i])) {
                    result[j++] = source[i];
                } else {
                    if (source[i] == '.' && isMoreThanOneDot() == false) {
                        result[j++] = source[i];
                    } else {
                        // do nothing
                    }
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
            String newValue = unitPrice.getText().trim();
            if (quantity.getText().trim().length() > 0 && "".equalsIgnoreCase(newValue) == false) {
                double total = Double.parseDouble(newValue) * Integer.parseInt(quantity.getText().trim());
                extendedAmount.setText(Utility.formatDouble(total));
            }
        }

        private boolean isMoreThanOneDot() {
            String str = null;
            try {
                str = getText(0, getLength());
            } catch (javax.swing.text.BadLocationException ble) {
                ble.printStackTrace();
            }

            if (str.indexOf('.') == -1) {
                return false;
            } else {
                return true;
            }
        }
    }
}
